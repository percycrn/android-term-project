package com.usst.androidtermprogram.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.usst.androidtermprogram.MainActivity;
import com.usst.androidtermprogram.R;
import com.usst.androidtermprogram.fragment.Download;


public class PlayMusicService extends Service {

    private MediaPlayer mediaPlayer;
    private boolean isStop = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //在此方法中服务被创建
    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            //为播放器添加播放完成时的监听器
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Intent intent = new Intent();
                    intent.setAction("MUSIC_COMPLETE");
                    sendBroadcast(intent);
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getIntExtra("type", -1)) {
            case Download.PLAY_MUSIC:
                if (isStop) {
                    //重置mediaPlayer
                    mediaPlayer.reset();
                    //将需要播放的资源与之绑定
                    mediaPlayer = MediaPlayer.create(this, R.raw.music);
                    //开始播放
                    mediaPlayer.start();
                    //是否循环播放
                    mediaPlayer.setLooping(false);
                    isStop = false;
                } else if (!mediaPlayer.isPlaying() && mediaPlayer != null) {
                    mediaPlayer.start();
                }
                break;
            case Download.PAUSE_MUSIC:
                //播放器不为空，并且正在播放
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case Download.STOP_MUSIC:
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    isStop = true;
                }
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
