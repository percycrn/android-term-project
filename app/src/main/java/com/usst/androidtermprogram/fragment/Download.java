package com.usst.androidtermprogram.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.usst.androidtermprogram.R;
import com.usst.androidtermprogram.entities.FileInfo;
import com.usst.androidtermprogram.service.DownloadService;
import com.usst.androidtermprogram.service.PlayMusicService;

public class Download extends Fragment {

    // download
    private Button buttonStart;
    private Button buttonPause;
    private final static String url = "http://dlsw.baidu.com/sw-search-sp/soft/e0/13545/GooglePinyinInstaller.1419846448.exe";
    private final static String fileName = "GooglePinyinInstaller.1419846448.exe";
    private FileInfo fileInfo = null;
    private ProgressBar mPbProgress;
    // music
    private ImageButton musicPlay;
    private ImageButton musicPause;
    private ImageButton musicStop;
    public final static int PLAY_MUSIC = 1;
    public final static int PAUSE_MUSIC = 2;
    public final static int STOP_MUSIC = 3;
    // video
    private ImageButton videoPlay;
    private ImageButton videoPause;
    private ImageButton videoStop;
    private VideoView videoView;
    public final static int PLAY_VIDEO = 1;
    public final static int PAUSE_VIDEO = 2;
    public final static int STOP_VIDEO = 3;

    public Download() {
        // Required empty public constructor
    }

    private void initComponents() {
        mPbProgress = getActivity().findViewById(R.id.pbProgress);
        mPbProgress.setMax(100);
        buttonStart = getActivity().findViewById(R.id.btStart);
        buttonPause = getActivity().findViewById(R.id.btStop);
        musicPlay = getActivity().findViewById(R.id.music_play);
        musicPause = getActivity().findViewById(R.id.music_pause);
        musicStop = getActivity().findViewById(R.id.music_stop);
        videoPlay = getActivity().findViewById(R.id.video_play);
        videoPause = getActivity().findViewById(R.id.video_pause);
        videoStop = getActivity().findViewById(R.id.video_stop);
        fileInfo = new FileInfo(0, url, fileName, 0, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 注册断点下载广播接收器
        IntentFilter filterDownload = new IntentFilter();
        filterDownload.addAction(DownloadService.ACTION_UPDATE);
        // 在fragment里面要通过LocalBroadcastManager注册广播
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(downloadReceiver, filterDownload);

        // 注册音乐播放广播接收器
        IntentFilter filterMusic = new IntentFilter();
        filterMusic.addAction("MUSIC_COMPLETE");
        broadcastManager.registerReceiver(musicReceiver, filterMusic);

        initComponents();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("fileInfo", fileInfo);
                getActivity().startService(intent);
                Toast.makeText(getActivity(), "Download starts!", Toast.LENGTH_SHORT).show();
            }
        });
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DownloadService.class);
                intent.setAction(DownloadService.ACTION_STOP);
                intent.putExtra("fileInfo", fileInfo);
                getActivity().startService(intent);
                Toast.makeText(getActivity(), "Download pauses...", Toast.LENGTH_SHORT).show();
            }
        });
        musicPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayMusicService.class);
                intent.putExtra("type", PLAY_MUSIC);
                getActivity().startService(intent);
            }
        });
        musicPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayMusicService.class);
                intent.putExtra("type", PAUSE_MUSIC);
                getActivity().startService(intent);
            }
        });
        musicStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayMusicService.class);
                intent.putExtra("type", STOP_MUSIC);
                getActivity().startService(intent);
            }
        });
        videoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getActivity(), VideoService.class);
                intent.putExtra("type", PLAY_VIDEO);
                getActivity().startService(intent);*/
                if (videoView == null) {
                    videoView = getActivity().findViewById(R.id.video_view);
                    videoView.setMediaController(new MediaController(getContext()));
                    Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.video);
                    videoView.setVideoURI(uri);
                    videoView.start();
                    videoView.requestFocus();
                } else if (!videoView.isPlaying()) {
                    videoView.start();
                }
            }
        });
        videoPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                }
            }
        });
        videoStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView != null) {
                    videoView.seekTo(10);
                }
            }
        });
    }

    // 断点下载广播接收器
    BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                int finished = intent.getIntExtra("finished", 0);
                mPbProgress.setProgress(finished);
            }
        }
    };

    // 音乐播放广播接收器
    BroadcastReceiver musicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getContext(), "音乐播放完毕", Toast.LENGTH_SHORT).show();
        }
    };

}
