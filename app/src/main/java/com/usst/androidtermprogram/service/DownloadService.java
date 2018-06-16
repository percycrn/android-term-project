package com.usst.androidtermprogram.service;

/*
 * Created by percycrn on 2018/3/27.
 */

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.usst.androidtermprogram.entities.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

// service 在后台运行 不会被用户关闭
// service 优先级高 不会被系统回收
public class DownloadService extends Service {

    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    @SuppressLint("StaticFieldLeak")
    private static DownloadTask downloadTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取Activity传来的参数
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test", "Start: " + fileInfo.toString());
            // 启动初始化线程
            //noinspection unchecked
            new MyTask(fileInfo).execute();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.i("test", "Stop: " + fileInfo.toString());
            if (downloadTask != null) {
                downloadTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask {

        private FileInfo mFileInfo;

        MyTask(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            HttpURLConnection connection = null;
            RandomAccessFile raf = null;
            try {
                // 连接网络文件
                URL url = new URL(mFileInfo.getUrl());
                connection = (HttpURLConnection) url.openConnection();
                //connection.setConnectTimeout(30000);
                connection.setRequestMethod("GET"); // 可用post
                int length = -1;
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 获得文件长度
                    length = connection.getContentLength();
                }
                if (length <= 0) {
                    return null;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdir();
                }
                // 在本地创建文件
                File file = new File(dir, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd"); // rwd: 可读可写可删除

                // 设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection!=null){
                        connection.disconnect();
                    }
                    if(raf!=null){
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            downloadTask = new DownloadTask(DownloadService.this, mFileInfo);
            downloadTask.download();
            return null;
        }
    }
}
