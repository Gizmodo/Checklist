package ru.dl.checklist.data.repository

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.whatif.whatIfNotNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.dl.checklist.app.di.module.IoDispatcher
import ru.dl.checklist.app.ext.whenNotNullNorEmpty
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.app.utils.HTTPConstants
import ru.dl.checklist.data.mapper.DtoToEntityMapper.toEntity
import ru.dl.checklist.data.mapper.EntityToDomainMapper.toDomain
import ru.dl.checklist.data.model.entity.MediaEntity
import ru.dl.checklist.data.source.cache.ChecklistDao
import ru.dl.checklist.data.source.cache.MarkDao
import ru.dl.checklist.data.source.cache.MediaDao
import ru.dl.checklist.data.source.cache.ZoneDao
import ru.dl.checklist.data.source.remote.RemoteApi
import ru.dl.checklist.domain.model.Answer
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.MarkDomainWithCount
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.repository.CheckListRepository
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class CheckListRepositoryImpl @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val zoneDao: ZoneDao,
    private val markDao: MarkDao,
    private val mediaDao: MediaDao,
    private val remoteDataSource: RemoteApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CheckListRepository {

    override fun getChecklists(): Flow<ApiResult<List<ChecklistDomain>>> = flow {
        val myScope = CoroutineScope(Dispatchers.IO)
        val response = remoteDataSource.getChecklist()
        response.suspendOnSuccess {
            var scopeResult: ApiResult<List<ChecklistDomain>> = ApiResult.Loading
            val job = myScope.launch {
                try {
                    data.checklists.whenNotNullNorEmpty { list ->
                        list.forEach { checklist ->
                            checklistDao.getByUUID(checklist.uuid.toString()).whatIfNotNull(
                                whatIf = { },
                                whatIfNot = {
                                    val newChecklistId =
                                        checklistDao.insert(checklist.toEntity())
                                    checklist.zones.whenNotNullNorEmpty { zoneList ->
                                        zoneList.forEach { zone ->
                                            val newZoneId =
                                                zoneDao.insert(zone.toEntity(newChecklistId))
                                            zone.marks.whenNotNullNorEmpty { markList ->
                                                markList.map { it.toEntity(newZoneId) }
                                                    .onEach { markDao.insert(it) }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    val checklist = checklistDao.getAll()
                    val returnList = checklist.map { it.toDomain() }
                    scopeResult = ApiResult.Success(returnList)
                } catch (e: Exception) {
                    Timber.e("An error occurred: " + e.message)
                    scopeResult = ApiResult.Error(e.message.toString())
                }
            }
            job.join()
            emit(scopeResult)
        }.suspendOnError {
            Timber.e("suspendOnError ${statusCode.code}")
            when (this.statusCode) {
                StatusCode.Unauthorized -> emit(ApiResult.Error(HTTPConstants.Unauthorized))
                StatusCode.Forbidden -> emit(ApiResult.Error(HTTPConstants.Forbidden))
                StatusCode.BadGateway -> emit(ApiResult.Error(HTTPConstants.BadGateway))
                StatusCode.GatewayTimeout -> emit(ApiResult.Error(HTTPConstants.GatewayTimeout))
                StatusCode.InternalServerError -> emit(ApiResult.Error(HTTPConstants.SERVER_ERROR))
                StatusCode.BadRequest -> {
                    try {
                        val jObjError = JSONObject(errorBody?.string()!!)
                        emit(ApiResult.Error(jObjError.getString("message")))
                    } catch (e: Exception) {
                        emit(ApiResult.Error(message()))
                    }
                }

                else -> {
                    Timber.wtf("${statusCode.code} " + HTTPConstants.GENERIC_ERROR_MESSAGE)
                    emit(ApiResult.Error(HTTPConstants.GENERIC_ERROR_MESSAGE))
                }
            }
        }.suspendOnException {
            Timber.e(this.exception)
            when (exception) {
                is SecurityException -> emit(ApiResult.Error(HTTPConstants.SECURITY_EXCEPTION))
                is UnknownHostException -> emit(ApiResult.Error(HTTPConstants.NO_INTERNET_ERROR_MESSAGE))
                is MalformedJsonException -> emit(ApiResult.Error(HTTPConstants.JSON_MALFORMED))
                is JsonSyntaxException -> emit(ApiResult.Error(HTTPConstants.JSON_EXCEPTION))
                else -> emit(ApiResult.Error(this.exception.message.toString()))
            }
        }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getZonesByChecklist(uuid: String): Flow<List<ZoneDomain>> {
        val inter = zoneDao.getZoneListByChecklist(uuid)
        val interMap = inter.map { list ->
            list.map { it.toDomain() }
        }
        return interMap.flowOn(dispatcher)
    }

    override fun getMarksByZone(zoneId: Long): Flow<List<MarkDomain>> {
        val inter = markDao.getMarkListByZone(zoneId)
        val interMap = inter.map { list ->
            list.map { it.toDomain() }
        }
        return interMap.flowOn(dispatcher)
    }

    override fun getMarksByZoneWithCount(zoneId: Long): Flow<List<MarkDomainWithCount>> {
        val inter = markDao.getMarkListByZoneWithCount(zoneId)
        return inter.flowOn(dispatcher)
    }

    override suspend fun changeAnswer(markId: Long, answer: Answer) {
        withContext(dispatcher) { markDao.updateMarkAnswer(markId, answer) }
    }

    override suspend fun changeComment(markId: Long, comment: String) {
        withContext(dispatcher) { markDao.updateMarkComment(markId, comment) }
    }

    override suspend fun addPhoto(markId: Long, byteArray: ByteArray) {
        withContext(dispatcher) {
            val mediaEntity = MediaEntity(markId = markId, media = byteArray)
            val res = mediaDao.insert(mediaEntity)
            Timber.i("mediaId: $res")
        }
    }
}