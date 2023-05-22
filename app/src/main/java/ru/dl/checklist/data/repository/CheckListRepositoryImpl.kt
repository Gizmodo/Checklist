package ru.dl.checklist.data.repository

import com.skydoves.sandwich.getOrElse
import com.skydoves.sandwich.mapSuccess
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.dl.checklist.app.di.module.IoDispatcher
import ru.dl.checklist.app.ext.RetrofitHandler.errorHandler
import ru.dl.checklist.app.ext.RetrofitHandler.exceptionHandler
import ru.dl.checklist.app.ext.whenNotNullNorEmpty
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.data.mapper.DtoToEntityMapper.toEntity
import ru.dl.checklist.data.mapper.EntityToDomainMapper.toDomain
import ru.dl.checklist.data.model.entity.MediaEntity
import ru.dl.checklist.data.model.remote.ReadyChecklist
import ru.dl.checklist.data.source.cache.ChecklistDao
import ru.dl.checklist.data.source.cache.MarkDao
import ru.dl.checklist.data.source.cache.MediaDao
import ru.dl.checklist.data.source.cache.ZoneDao
import ru.dl.checklist.data.source.remote.RemoteApi
import ru.dl.checklist.domain.model.BackendResponseDomain
import ru.dl.checklist.domain.model.ChecklistDomain
import ru.dl.checklist.domain.model.MarkDomain
import ru.dl.checklist.domain.model.MarkDomainWithCount
import ru.dl.checklist.domain.model.ZoneDomain
import ru.dl.checklist.domain.repository.CheckListRepository
import timber.log.Timber
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
                    scopeResult = ApiResult.Success(checklist)
                } catch (e: Exception) {
                    Timber.e("An error occurred: " + e.message)
                    scopeResult = ApiResult.Error(e.message.toString())
                }
            }
            job.join()
            emit(scopeResult)
        }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)

    override fun getZonesByChecklist(uuid: String): Flow<List<ZoneDomain>> {
        val inter = zoneDao.getZoneListByChecklist(uuid)
        return inter.flowOn(dispatcher)
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

    override suspend fun changeAnswer(markId: Long, answer: Float) {
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

    override fun uploadImages(uuid: String) = flow {
        val mediaList = markDao.getMediaListByChecklist(uuid)
        when {
            mediaList.isEmpty() -> {
                emit(ApiResult.Error("Фотографии отсутствуют"))
            }

            else -> {
                val parts = mutableListOf<MultipartBody.Part>()
                mediaList.forEach { media ->
                    val mediaRequestBody =
                        media.media.toRequestBody(
                            "image/jpeg".toMediaTypeOrNull(),
                            0,
                            media.media.size
                        )
                    val mediaPart =
                        MultipartBody.Part.createFormData("media", "image.jpg", mediaRequestBody)
                    parts.add(mediaPart)

                    val uuidRequestBody =
                        media.uuid.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val uuidPart =
                        MultipartBody.Part.createFormData("uuid", uuidRequestBody.toString())
                    parts.add(uuidPart)
                }

                val response = remoteDataSource.uploadImages(parts)
                response.suspendOnSuccess {
                    val res = response.mapSuccess {
                        BackendResponseDomain(
                            this.message ?: "Пустое поле message",
                            this.result ?: false
                        )
                    }.getOrElse(BackendResponseDomain("Ошибка маппинга ответа", false))
                    when {
                        res.result -> {
                            emit(ApiResult.Success(res))
                        }

                        else -> {
                            emit(ApiResult.Error(res.message))
                        }
                    }
                }
                    .suspendOnError { emit(errorHandler(this)) }
                    .suspendOnException { emit(exceptionHandler(exception)) }
            }
        }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)


    override fun uploadMarks(uuid: String) = flow {
        val marks = markDao.getMarkListByChecklist(uuid)
        val response =
            remoteDataSource.uploadMarks(ReadyChecklist(uuid, marks))
        response.suspendOnSuccess {
            val res = response.mapSuccess {
                BackendResponseDomain(
                    this.message ?: "Пустое поле message",
                    this.result ?: false
                )
            }.getOrElse(BackendResponseDomain("Ошибка маппинга ответа", false))
            when {
                res.result -> {
                    emit(ApiResult.Success(res))
                }

                else -> {
                    emit(ApiResult.Error(res.message))
                }
            }
        }
            .suspendOnError { emit(errorHandler(this)) }
            .suspendOnException { emit(exceptionHandler(exception)) }
    }.onStart { emit(ApiResult.Loading) }
        .catch {
            Timber.e(it)
            emit(ApiResult.Error(it.message.orEmpty()))
        }
        .flowOn(dispatcher)
}