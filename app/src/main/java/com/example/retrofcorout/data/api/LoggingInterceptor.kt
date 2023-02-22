package com.example.retrofcorout.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

internal class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()
        Log.d(
            "OkHttp", String.format(
                "Sending request %s%n%s body%n%s",
                request.url, request.headers, request.body
            )
        )
        val response = chain.proceed(request)
        val t2 = System.nanoTime()
        Log.d(
            "OkHttp", String.format(
                "Received response for %s in %.1fms%n%s body%n%s",
                response.request.url, (t2 - t1) / 1e6, response.headers, response.body
            )
        )
        return response
    }
}