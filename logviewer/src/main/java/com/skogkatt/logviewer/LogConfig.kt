package com.skogkatt.logviewer

import android.util.Log
import com.skogkatt.logviewer.util.currentTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object LogConfig {
    private val _logEntryList = MutableStateFlow<List<LogEntry>>(emptyList())
    val logEntryList = _logEntryList.asStateFlow()

    fun v(tag: String, message: String) = log(LogLevel.VERBOSE, tag, message)
    fun d(tag: String, message: String) = log(LogLevel.DEBUG, tag, message)
    fun i(tag: String, message: String) = log(LogLevel.INFO, tag, message)
    fun w(tag: String, message: String) = log(LogLevel.WARN, tag, message)
    fun e(tag: String, message: String) = log(LogLevel.ERROR, tag, message)

    fun clear() = _logEntryList.update { emptyList() }

    private fun log(level: LogLevel, tag: String, message: String) {
        if (!BuildConfig.DEBUG && level.priority < Log.INFO) return
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message)
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARN -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
        }
        _logEntryList.update { it + LogEntry(tag, message, level, currentTimestamp()) }
    }
}

enum class LogLevel(val priority: Int) {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
}

data class LogEntry(
    val tag: String,
    val message: String,
    val level: LogLevel,
    val timestamp: String,
)

