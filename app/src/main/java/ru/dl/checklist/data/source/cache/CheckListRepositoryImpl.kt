package ru.dl.checklist.data.source.cache

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.dl.checklist.app.di.module.IoDispatcher
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.app.utils.HTTPConstants
import ru.dl.checklist.data.mapper.ChecklistsMapper
import ru.dl.checklist.data.model.entity.Mapper.toEntity
import ru.dl.checklist.data.source.remote.RemoteApi
import ru.dl.checklist.domain.model.ChecklistsDomain
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class CheckListRepositoryImpl @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val zoneDao: ZoneDao,
    private val markDao: MarkDao,
    private val remoteDataSource: RemoteApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CheckListRepository {
    override fun getCheckList(): Flow<ApiResult<ChecklistsDomain>> = flow {
        val myScope = CoroutineScope(Dispatchers.IO)
        val response = remoteDataSource.getChecklist()
        response.suspendOnSuccess(ChecklistsMapper) {
            val _this = this
            myScope.launch {
                try {
                    withContext(dispatcher) {
                        checklists.forEach { checklist ->
                            val exist = checklistDao.getByUUID(checklist.uuid)
                            exist?.apply { checklistDao.delete(this) }
                            val newChecklistId = checklistDao.insert(checklist.toEntity())
                            checklist.zones.forEach { zone ->
                                val newZoneId = zoneDao.insert(zone.toEntity(newChecklistId))
                                /*  zone.marks.forEach { mark ->
                                      markDao.insert(mark.toEntity(newZoneId))
                                  }*/
                                zone.marks.map { it.toEntity(newZoneId) }
                                    .onEach { markDao.insert(it) }
                            }
                        }
                    }
                    emit(ApiResult.Success(_this))
                } catch (e: Exception) {
                    // Handle the exception here
                    Timber.e("An error occurred: " + e.message)
                    emit(ApiResult.Error(e.message.toString()))
                }
            }
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
    }.onStart {
        emit(ApiResult.Loading)
    }.catch {
        Timber.e(it)
        emit(ApiResult.Error(it.message.orEmpty()))
    }
        .flowOn(dispatcher)


}