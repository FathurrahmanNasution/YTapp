@echo off
title Simple YT Downloader

echo ==============================
echo       Simple Downloader
echo ==============================

set /p URL=Paste video URL: 

echo.
echo 1) MP4 - Best Quality (Opus codec - VLC only, NOT WMP)
echo 2) MP4 - Compatible (AAC codec - Works with all players)
echo 3) MP3 - Audio only
set /p CHOICE=Choose format (1/2/3): 

if "%CHOICE%"=="1" goto mp4opus
if "%CHOICE%"=="2" goto mp4compat
if "%CHOICE%"=="3" goto mp3

echo Invalid choice.
pause
exit

:mp4opus
echo Downloading MP4 (Best Quality - Opus codec)...
echo WARNING: This may not play audio in Windows Media Player!
echo Use VLC or another modern player.
echo.
yt-dlp.exe -f "bestvideo+bestaudio/best" ^
 --merge-output-format mp4 ^
 -o "%CD%\%%(title)s.%%(ext)s" "%URL%"
echo.
echo MP4 saved.
pause
exit

:mp4compat
echo Downloading MP4 (Compatible - AAC codec)...
echo This will work with Windows Media Player.
echo.
yt-dlp.exe -f "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best" ^
 --merge-output-format mp4 ^
 -o "%CD%\%%(title)s.%%(ext)s" "%URL%"
echo.
echo MP4 saved.
pause
exit

:mp3
echo Downloading MP3...
yt-dlp.exe -f bestaudio --extract-audio --audio-format mp3 ^
 -o "%CD%\%%(title)s.%%(ext)s" "%URL%"
echo.
echo MP3 saved.
pause
exit