package com.kiviabrito.allfood.utils

/**
 * Wrapper class to avoid value to be consumed more than once when using LiveData.
 */

class SingleEventWrapper <T>(private val value: T) {

    private var isConsumed = false

    fun get(): T? =
            if (isConsumed) {
                null
            } else {
                isConsumed = true
                value
            }
}