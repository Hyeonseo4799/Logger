package com.skogkatt.sample

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skogkatt.logviewer.LogConfig
import com.skogkatt.logviewer.LogLevel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val mainViewModel = MainViewModel()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var searchText by remember { mutableStateOf("") }

            val entries by LogConfig.logEntryList.collectAsStateWithLifecycle()
            val filtered by remember {
                derivedStateOf {
                    entries.filter {
                        it.level.name.contains(searchText, ignoreCase = true) ||
                                it.message.contains(searchText, ignoreCase = true) ||
                                it.timestamp.contains(searchText, ignoreCase = true)
                    }
                }
            }

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = LogConfig::clear,
                        content = { Text(text = "clear") }
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row {
                        TextField(
                            modifier = Modifier.weight(1f),
                            value = searchText,
                            onValueChange = { searchText = it }
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filtered) { logEntry ->
                            Text(
                                modifier = Modifier
                                    .clickable { copyToClipboard(logEntry.message) }
                                    .fillMaxWidth()
                                    .background(
                                        color = when (logEntry.level) {
                                            LogLevel.VERBOSE -> Color.LightGray.copy(alpha = 0.5f)
                                            LogLevel.DEBUG -> Color.Gray.copy(alpha = 0.5f)
                                            LogLevel.INFO -> Color.Cyan.copy(alpha = 0.5f)
                                            LogLevel.WARN -> Color.Magenta.copy(alpha = 0.5f)
                                            LogLevel.ERROR -> Color.Red.copy(alpha = 0.5f)
                                        }
                                    )
                                    .padding(10.dp),
                                text = "[${logEntry.timestamp}] -- ${logEntry.message}",
                            )
                        }
                    }
                }
            }
        }
    }

    fun Context.copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}