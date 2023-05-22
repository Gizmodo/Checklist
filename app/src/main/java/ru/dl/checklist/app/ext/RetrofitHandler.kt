package ru.dl.checklist.app.ext

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.message
import org.json.JSONObject
import ru.dl.checklist.app.utils.ApiResult
import ru.dl.checklist.app.utils.HTTPConstants
import timber.log.Timber
import java.net.UnknownHostException

object RetrofitHandler {
    fun exceptionHandler(throwable: Throwable): ApiResult.Error {
        Timber.e("exceptionHandler $throwable")
        return when (throwable) {
            is SecurityException -> ApiResult.Error(HTTPConstants.SECURITY_EXCEPTION)
            is UnknownHostException -> ApiResult.Error(HTTPConstants.NO_INTERNET_ERROR_MESSAGE)
            is MalformedJsonException -> ApiResult.Error(HTTPConstants.JSON_MALFORMED)
            is JsonSyntaxException -> ApiResult.Error(HTTPConstants.JSON_EXCEPTION)
            else -> ApiResult.Error(throwable.message.toString())
        }
    }

    fun <T> errorHandler(
        error: ApiResponse.Failure.Error<T>
    ): ApiResult.Error {
        Timber.e("errorHandler ${error.statusCode.code}")
        when (error.statusCode) {
            StatusCode.Unauthorized -> return ApiResult.Error(HTTPConstants.Unauthorized)
            StatusCode.Forbidden -> return ApiResult.Error(HTTPConstants.Forbidden)
            StatusCode.BadGateway -> return ApiResult.Error(HTTPConstants.BadGateway)
            StatusCode.GatewayTimeout -> return ApiResult.Error(HTTPConstants.GatewayTimeout)
            StatusCode.InternalServerError -> return ApiResult.Error(HTTPConstants.SERVER_ERROR)
            StatusCode.BadRequest -> {
                return try {
                    val jObjError = JSONObject(error.errorBody?.string()!!)
                    ApiResult.Error(jObjError.getString("message"))
                } catch (e: Exception) {
                    ApiResult.Error(error.message())
                }
            }

            else -> {
                Timber.wtf("${error.statusCode.code} " + HTTPConstants.GENERIC_ERROR_MESSAGE)
                return ApiResult.Error(HTTPConstants.GENERIC_ERROR_MESSAGE)
            }
        }
    }
}