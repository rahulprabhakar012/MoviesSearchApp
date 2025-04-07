package com.example.moviessearch.utils

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException

object ErrorUtils {
    private const val TAG = "ErrorUtils"

    fun handleError( error: Throwable) {
        when (error) {
            is IOException -> Log.e(TAG, "IOException: ${error.message}")
            is HttpException -> Log.e(TAG, "HttpException: ${error.message}")
            is TimeoutException -> Log.e(TAG, "TimeoutException: ${error.message}")
            else -> Log.e(TAG, "Unknown Error: ${error.message}")
        }
    }
}