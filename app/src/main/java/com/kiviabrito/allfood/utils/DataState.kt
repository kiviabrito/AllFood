package com.kiviabrito.allfood.utils

/**
 * Class to handle success, error and loading.
 */

sealed class DataState<out R> {

    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val errorMsg: String) : DataState<Nothing>()
    object Loading : DataState<Nothing>()

}