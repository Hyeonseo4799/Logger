package com.skogkatt.sample.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class FakeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        val (response, code) = when (path) {
            "/success" -> """{"code":200,"message":"hello"}""" to 200
            "/error" -> """{"code":401,"message":"Unauthorized"}""" to 401
            "/exception" -> throw IOException("Network error")
            else -> "" to 404
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("")
            .code(code)
            .body(response.toResponseBody("application/json".toMediaType()))
            .build()
    }
}