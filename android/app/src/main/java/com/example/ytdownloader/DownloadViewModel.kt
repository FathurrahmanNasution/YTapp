package com.example.ytdownloader

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val _downloadStatus = MutableStateFlow("")
    val downloadStatus: StateFlow<String> = _downloadStatus

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var isInitialized = false

    private fun initYoutubeDL() {
        if (!isInitialized) {
            try {
                YoutubeDL.getInstance().init(getApplication())
                try {
                    FFmpeg.getInstance().init(getApplication())
                } catch (e: Exception) {
                    // FFmpeg init non-fatal fallback
                }
                try {
                    com.yausername.aria2c.Aria2c.getInstance().init(getApplication())
                } catch (e: Exception) {}
                
                try {
                    _downloadStatus.value = "Updating yt-dlp engine to latest version..."
                    YoutubeDL.getInstance().updateYoutubeDL(getApplication())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                
                isInitialized = true
            } catch (e: com.yausername.youtubedl_android.YoutubeDLException) {
                _downloadStatus.value = "Init error: ${e.message ?: e.toString()}"
                e.printStackTrace()
            } catch (e: Exception) {
                _downloadStatus.value = "Init error: ${e.message ?: e.toString()}"
                e.printStackTrace()
            }
        }
    }

    fun downloadMp4Opus(url: String, referer: String = "") {
        startRealDownload(url, formatOption = "bestvideo+bestaudio/best", suffix = "(Opus)", referer = referer)
    }

    fun downloadMp4Compat(url: String, referer: String = "") {
        startRealDownload(url, formatOption = "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best", suffix = "(Compat)", referer = referer)
    }

    fun downloadMp3(url: String, referer: String = "") {
        startRealDownload(url, formatOption = "bestaudio", suffix = "(Audio)", referer = referer)
    }

    private fun startRealDownload(
        url: String, 
        formatOption: String, 
        suffix: String,
        referer: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            initYoutubeDL()
            if (!isInitialized) return@launch

            _downloadStatus.value = "Starting download..."
            _progress.value = 0f

            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val request = YoutubeDLRequest(url)
                request.addOption("-f", formatOption)
                request.addOption("-o", "${downloadsDir.absolutePath}/%(title)s $suffix.%(ext)s")
                
                request.addOption("--downloader", "aria2c")
                request.addOption("--downloader-args", "aria2c:-x 16 -k 1M")
                
                if (suffix == "(Audio)") {
                    request.addOption("--extract-audio")
                    request.addOption("--audio-format", "mp3")
                } else {
                    request.addOption("--merge-output-format", "mp4")
                }

                if (referer.isNotBlank()) {
                    request.addOption("--add-header", "Referer:$referer")
                }

                YoutubeDL.getInstance().execute(request) { progressValue, _, line ->
                    if (progressValue == -1f) {
                        _progress.value = 50f
                        _downloadStatus.value = "Downloading via Aria2c (Fast Mode)..."
                    } else {
                        _progress.value = progressValue
                        _downloadStatus.value = "Downloading: ${progressValue.toInt()}%"
                    }
                }

                _progress.value = 100f
                _downloadStatus.value = "Download Finished!\nSaved to: ${downloadsDir.name}"
            } catch (e: Exception) {
                _downloadStatus.value = "Error: ${e.localizedMessage ?: "Download failed"}"
            }
        }
    }
}
