package com.usst.androidtermprogram.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import com.usst.androidtermprogram.MainActivity;
import com.usst.androidtermprogram.fragment.Download;

public class VideoService extends Service {

    private VideoView videoView;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (videoView == null) {
            videoView = new VideoView(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("type", -1)) {
            case Download.PLAY_VIDEO:
                /*videoView.setVideoPath("C:\\Users\\85387\\Desktop\\video.qlv");
                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setLooping(false);
                        mediaPlayer.start();
                        Toast.makeText(VideoService.this, "开始播放", Toast.LENGTH_LONG).show();
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Toast.makeText(VideoService.this, "播放完毕", Toast.LENGTH_SHORT).show();
                    }
                });*/
                break;
            case Download.PAUSE_VIDEO:
                videoView.pause();
                break;
            case Download.STOP_VIDEO:
                videoView.stopPlayback();
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
