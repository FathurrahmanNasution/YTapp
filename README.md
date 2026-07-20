# YT Downloader 📹🎶

A lightweight YouTube video and audio downloader for Windows, featuring both a Python GUI (Tkinter) and a quick Windows Batch script CLI. Powered by [`yt-dlp`](https://github.com/yt-dlp/yt-dlp) and [`FFmpeg`](https://ffmpeg.org/).

---

## ✨ Features

- 🎬 **MP4 Best Quality (Opus Codec)**: Downloads highest available video & audio quality. *(Best played using VLC or modern media players)*.
- 🎵 **MP4 Compatible (AAC Codec)**: Downloads MP4 video with standard AAC audio for maximum compatibility *(Windows Media Player, legacy devices)*.
- 🎧 **MP3 Audio Only**: Extracts high-quality audio directly to MP3 format.
- 🖥️ **Dual Interface**: Choose between a simple GUI application or a fast command-line batch script.
- 📦 **Executable Build Ready**: Pre-configured PyInstaller `.spec` file to bundle everything into a standalone executable.

---

## 🛠️ Requirements & Setup

When cloning this repository, you need to add the required third-party binaries to the project root directory before running or building.

### 1. Prerequisites
- **Python 3.8+** (installed and added to PATH)
- **Git**

### 2. Download Required Binaries
Place the following files directly in the root directory (`YTapp/`):

1. **`yt-dlp.exe`**: Download the latest release from [yt-dlp Releases](https://github.com/yt-dlp/yt-dlp/releases).
2. **`ffmpeg.exe` & `ffprobe.exe`**: Download an essential build from [gyan.dev FFmpeg Builds](https://www.gyan.dev/ffmpeg/builds/) (or official FFmpeg source) and extract `ffmpeg.exe` and `ffprobe.exe` to the root folder.

---

## 🚀 How to Run

### Option A: Python GUI Application
Run the GUI built with Python's built-in `tkinter`:

```bash
python yd_app.py
```

### Option B: Windows Batch Script
Double-click `yt-download.bat` or run it via Command Prompt:

```cmd
yt-download.bat
```

---

## 📦 Building a Standalone Executable (.exe)

You can package the GUI app along with `yt-dlp.exe`, `ffmpeg.exe`, and `ffprobe.exe` into a single standalone Windows executable using PyInstaller:

1. Install PyInstaller:
   ```bash
   pip install pyinstaller
   ```

2. Build using the provided `.spec` file:
   ```bash
   pyinstaller yd_app.spec
   ```

3. The generated executable will be created in `dist/yd_app.exe`.

---

## 📁 Project Structure

```
YTapp/
├── yd_app.py          # Python Tkinter GUI application source
├── yd_app.spec        # PyInstaller specification for bundling executables
├── yt-download.bat    # Command-line Windows batch downloader
├── .gitignore         # Git ignore rules for binaries, build outputs & media
└── README.md          # Documentation
```

---

## ⚠️ Notes & Troubleshooting

- **Audio not playing in Windows Media Player?**  
  If you downloaded using **MP4 Best Quality**, the audio codec may be **Opus**. Use **VLC Media Player** or download using **MP4 Compatible (AAC)**.
- **Save Location**:  
  By default, downloads are saved to `D:\YTapp`. Ensure this drive/folder exists or modify `DOWNLOADS` in `yd_app.py` if needed.
