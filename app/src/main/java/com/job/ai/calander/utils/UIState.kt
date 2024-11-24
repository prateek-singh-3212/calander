package com.job.ai.calander.utils

sealed class UIState<T> {
    data class Success<T>(val data: T): UIState<T>()
    data class Error<T>(val exception: String): UIState<T>()
    data class Loading<T>(val isLoading: Boolean): UIState<T>()

    companion object {
        fun <T> loading(isLoading: Boolean) = Loading<T>(isLoading)
        fun <T> success(data: T) = Success(data)
        fun <T> error(message: String) = Error<T>(message)
    }
}