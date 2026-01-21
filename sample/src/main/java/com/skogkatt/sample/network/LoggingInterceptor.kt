package com.skogkatt.sample.network

import com.skogkatt.logviewer.LogConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.util.UUID

class LoggingInterceptor : Interceptor {
    private val tag = "interceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val endIndex = url.indexOfFirst { it == '?' }
            .takeIf { it > 0 } ?: url.length
        val apiUrl = url.take(endIndex)
        val randomId = UUID.randomUUID().toString().take(8)

        logRequest(request, randomId)

        val startTime = System.currentTimeMillis()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            LogConfig.e(tag, "<< [HTTP Failed] $apiUrl ($e)")
            throw e
        }
        val endTime = System.currentTimeMillis()

        logResponse(response, randomId, endTime - startTime)

        return response
    }

    private fun logRequest(request: Request, randomId: String) {
        LogConfig.i(tag, "$randomId >> [${request.method}] ${request.url}")
        request.body?.let {
            val buffer = Buffer()
            request.body!!.writeTo(buffer)
            val body = buffer.readUtf8()
            LogConfig.d(tag, "Request Body: $body")
        } ?: run {
            LogConfig.d(tag, "Request Body: empty")
        }
    }

    private fun logResponse(response: Response, randomId: String, duration: Long) {
        LogConfig.i(tag, "$randomId << [${response.code}] ${response.request.url} ${response.message} (${duration}ms)")
        val responseBody = response.peekBody(Long.MAX_VALUE)
        val body = responseBody.string()
        body.takeIf { it.isNotEmpty() }?.let {
            LogConfig.d(tag, "Response Body: $body")
        } ?: run {
            LogConfig.d(tag, "Response Body: empty")
        }
    }
}