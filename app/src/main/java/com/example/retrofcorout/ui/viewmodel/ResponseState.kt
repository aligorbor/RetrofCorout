package com.example.retrofcorout.ui.viewmodel

sealed class ResponseState<out T> {
    data class Success<T>(val data: T?) : ResponseState<T>()
    data class Error(val error: Throwable) : ResponseState<Nothing>()
    object Loading : ResponseState<Nothing>()
}
