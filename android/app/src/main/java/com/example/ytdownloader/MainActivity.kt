package com.example.ytdownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: DownloadViewModel = viewModel()) {
    var urlText by remember { mutableStateOf("") }
    var refererText by remember { mutableStateOf("") }
    val downloadStatus by viewModel.downloadStatus.collectAsState()
    val progress by viewModel.progress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📹 YT Downloader Mobile",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("Paste Video URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = refererText,
            onValueChange = { refererText = it },
            label = { Text("Referer URL (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.downloadMp4Opus(urlText, refererText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = urlText.isNotBlank() && !downloadStatus.contains("Downloading")
        ) {
            Text("MP4 Best Quality (Opus - Modern Players)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.downloadMp4Compat(urlText, refererText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = urlText.isNotBlank() && !downloadStatus.contains("Downloading")
        ) {
            Text("MP4 Compatible (AAC - All Players)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.downloadMp3(urlText, refererText) },
            modifier = Modifier.fillMaxWidth(),
            enabled = urlText.isNotBlank() && !downloadStatus.contains("Downloading")
        ) {
            Text("MP3 Audio Only")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (downloadStatus.isNotEmpty()) {
            Text(text = downloadStatus, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = "${progress.toInt()}%", modifier = Modifier.padding(top = 4.dp))
        }
    }
}
