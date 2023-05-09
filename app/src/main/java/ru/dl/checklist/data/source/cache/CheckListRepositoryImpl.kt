package ru.dl.checklist.data.source.cache

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import org.json.JSONObject
import ru.dl.checklist.app.di.module.IoDispatcher
import ru.dl.checklist.data.mapper.ChecklistMapper
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.app.utils.HTTPConstants
import ru.dl.checklist.data.source.remote.RemoteApi
import ru.dl.checklist.domain.model.ChecklistDomain
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class CheckListRepositoryImpl @Inject constructor(
    private val localDataSource: ChecklistDAO,
    private val remoteDataSource: RemoteApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : CheckListRepository {
    fun test() {
        val data = localDataSource.selectAll()
    }

    override fun getCheckList(): Flow<ApiResult<ChecklistDomain>> = flow {
        val response = remoteDataSource.getChecklist()
        response.suspendOnSuccess(ChecklistMapper) {
            emit(ApiResult.Success(this))
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