package com.job.ai.calander.utils

import android.util.Log
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.internal.readBomAsCharset
import retrofit2.Response
import kotlin.text.Charsets.UTF_8

sealed class ApiResult<T> {
    data class Loading<T>(val progress: Boolean) : ApiResult<T>()
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure<T>(
        val exception: Exception,
    ) : ApiResult<T>()

    companion object {
        fun <T> loading(progress: Boolean) = Loading<T>(progress)
        fun <T> success(data: T) = Success(data)
        fun <T> failed(e: Exception) = Failure<T>(e)
        fun <T> failed(errorMessage: String) = Failure<T>(Exception(errorMessage))
    }
}

inline fun <reified U> ApiResult(
    dispatcher: CoroutineDispatcher,
    crossinline afterFetchSuccess: (U) -> Unit = {},
    crossinline block: suspend () -> Response<U>,
): Flow<ApiResult<U>> {
    return flow<ApiResult<U>> {
        emit(ApiResult.Loading(true))
        try {
            block().also {
                if (it.isSuccessful && it.body() != null) {
                    val data = it.body()!!
                    afterFetchSuccess(data)
                    emit(ApiResult.success(data))
                } else {
                    emit(ApiResult.failed("API failed with code: ${it.code()}"))
                }
                emit(ApiResult.Loading(false))
            }
        } catch (e: Exception) {
            Log.d("API ERROR", "Result Failure Catch ex: ${e.message}")
            // Add Basic Generic Exception Handling If Needed
            emit(ApiResult.failed(e))
            emit(ApiResult.Loading(false))
        }
    }.flowOn(dispatcher)
}