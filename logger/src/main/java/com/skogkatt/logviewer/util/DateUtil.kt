package com.skogkatt.logviewer.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun currentTimestamp(): String {
    val now = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
    return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
}