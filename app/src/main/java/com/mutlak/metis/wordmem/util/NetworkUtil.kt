package com.mutlak.metis.wordmem.util

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import retrofit2.HttpException

object NetworkUtil {


    const val HTTP_OK = 200;
    const val HTTP_NOT_FOUND = 404;
    const val HTTP_SERVER_ERROR = 500;

    /**
     * Returns true if the Throwable is an instance of RetrofitError with an
     * http status code equals to the given one.
     */
    fun isHttpStatusCode(throwable: Throwable, statusCode: Int): Boolean {
        return throwable is HttpException && throwable.code() == statusCode
    }

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null
    }

}
