package com.skogkatt.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skogkatt.sample.network.FakeInterceptor
import com.skogkatt.sample.network.LoggingInterceptor
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class MainViewModel : ViewModel() {
    init {
        action()
    }

    private fun action() {
        viewModelScope.launch {
            runCatching {
                execute("https://fake.local/success")
                execute("https://fake.local/error")
                execute("https://fake.local/exception")
            }
        }
    }

    companion object {
        val client = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .addInterceptor(FakeInterceptor())
            .build()

        private fun execute(url: String) {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute()
        }
    }
}