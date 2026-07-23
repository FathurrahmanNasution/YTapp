import subprocess
import tkinter as tk
from tkinter import messagebox, ttk
from pathlib import Path
import threading
import sys
import os
import re

DOWNLOADS = Path("D:/YTapp")


def get_ytdlp_path():
    if getattr(sys, "frozen", False):
        return os.path.join(sys._MEIPASS, "yt-dlp.exe")
    local = Path.cwd() / "yt-dlp.exe"
    return str(local) if local.exists() else "yt-dlp"


def start_download(cmd, success_msg):
    progress_window = tk.Toplevel()
    progress_window.title("Downloading...")
    progress_window.geometry("400x120")
    progress_window.resizable(False, False)

    label = tk.Label(progress_window, text="Downloading...")
    label.pack(pady=10)

    progress = ttk.Progressbar(progress_window, length=350)
    progress.pack(pady=5)

    percent_label = tk.Label(progress_window, text="0%")
    percent_label.pack(pady=5)

    def run():
        try:
            startupinfo = subprocess.STARTUPINFO()
            startupinfo.dwFlags |= subprocess.STARTF_USESHOWWINDOW
            startupinfo.wShowWindow = subprocess.SW_HIDE
            
            proc = subprocess.Popen(
                cmd,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                text=True,
                bufsize=1,
                startupinfo=startupinfo,
                creationflags=subprocess.CREATE_NO_WINDOW
            )

            for line in proc.stdout:
                match = re.search(r"(\d{1,3}\.\d)%", line)
                if match:
                    percent = float(match.group(1))
                    progress["value"] = percent
                    percent_label.config(text=f"{percent:.1f}%")

            proc.wait()

            progress_window.destroy()

            if proc.returncode == 0:
                messagebox.showinfo("Done", success_msg + f"\nSaved to: {DOWNLOADS}")
            else:
                messagebox.showerror("Error", "Download failed.")
        except Exception as e:
            messagebox.showerror("Error", str(e))
            progress_window.destroy()

    threading.Thread(target=run, daemon=True).start()


def download_mp4_opus():
    url = url_entry.get().strip()
    if not url:
        messagebox.showerror("Error", "URL is empty")
        return
    
    response = messagebox.askyesno(
        "Codec Warning",
        "This will download the best quality MP4 with Opus codec.\n\n"
        "⚠️ WARNING: Audio may NOT work in Windows Media Player!\n"
        "Use VLC or another modern player.\n\n"
        "Continue?"
    )
    
    if not response:
        return

    ytdlp = get_ytdlp_path()
    outtmpl = str(DOWNLOADS / "%(title)s.%(ext)s")

    cmd = [
        ytdlp,
        "-f", "bestvideo+bestaudio/best",
        "--merge-output-format", "mp4",
        "-o", outtmpl,
        url
    ]

    referer = referer_entry.get().strip()
    if referer:
        cmd.extend(["--referer", referer])

    start_download(cmd, "MP4 Downloaded (Best Quality - Opus)!")


def download_mp4_compatible():
    url = url_entry.get().strip()
    if not url:
        messagebox.showerror("Error", "URL is empty")
        return

    ytdlp = get_ytdlp_path()
    outtmpl = str(DOWNLOADS / "%(title)s.%(ext)s")

    cmd = [
        ytdlp,
        "-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best",
        "--merge-output-format", "mp4",
        "-o", outtmpl,
        url
    ]

    referer = referer_entry.get().strip()
    if referer:
        cmd.extend(["--referer", referer])

    start_download(cmd, "MP4 Downloaded (Compatible - AAC)!")


def download_mp3():
    url = url_entry.get().strip()
    if not url:
        messagebox.showerror("Error", "URL is empty")
        return

    ytdlp = get_ytdlp_path()
    outtmpl = str(DOWNLOADS / "%(title)s.%(ext)s")

    cmd = [
        ytdlp,
        "-f", "bestaudio",
        "--extract-audio",
        "--audio-format", "mp3",
        "-o", outtmpl,
        url
    ]

    referer = referer_entry.get().strip()
    if referer:
        cmd.extend(["--referer", referer])

    start_download(cmd, "MP3 Downloaded!")


def open_folder():
    os.startfile(str(DOWNLOADS))


root = tk.Tk()
root.title("YT Downloader")
root.geometry("600x310")
root.resizable(False, False)

tk.Label(root, text="Video URL:").pack(pady=(12, 0))
url_entry = tk.Entry(root, width=85)
url_entry.pack(pady=6, padx=12)

tk.Label(root, text="Referer URL (Optional):").pack(pady=(2, 0))
referer_entry = tk.Entry(root, width=85)
referer_entry.pack(pady=6, padx=12)

# First row of buttons
btn_frame1 = tk.Frame(root)
btn_frame1.pack(pady=3)

tk.Button(
    btn_frame1, 
    text="MP4 Best Quality\n(Opus - VLC only)", 
    command=download_mp4_opus, 
    width=28,
    height=2
).grid(row=0, column=0, padx=4)

tk.Button(
    btn_frame1, 
    text="MP4 Compatible\n(AAC - All players)", 
    command=download_mp4_compatible, 
    width=28,
    height=2
).grid(row=0, column=1, padx=4)

# Second row
btn_frame2 = tk.Frame(root)
btn_frame2.pack(pady=3)

tk.Button(
    btn_frame2, 
    text="MP3 Audio Only", 
    command=download_mp3, 
    width=58,
    height=2
).grid(row=0, column=0, padx=4)

tk.Button(root, text="Open D:\\YTapp", command=open_folder, width=68).pack(pady=8)

root.mainloop()