package com.usst.androidtermprogram.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.usst.androidtermprogram.db.ThreadDAO;
import com.usst.androidtermprogram.db.ThreadDAOImpl;
import com.usst.androidtermprogram.entities.FileInfo;
import com.usst.androidtermprogram.entities.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 下载任务类
 * Created by percycrn on 2018/3/27.
 */

class DownloadTask {
    private Context context;
    private FileInfo fileInfo;
    private ThreadDAO mDAO;
    private int mFinished = 0;
    boolean isPause = false;

    DownloadTask(Context context, FileInfo fileInfo) {
        this.context = context;
        this.fileInfo = fileInfo;
        mDAO = new ThreadDAOImpl(context);
    }

    void download() {
        // 读取数据库的线程信息
        List<ThreadInfo> threadInfoS = mDAO.getThreads(fileInfo.getUrl());
        ThreadInfo threadInfo;
        if (threadInfoS.size() == 0) {
            // 若数据库无线程对象，则新建一个对象
            threadInfo = new ThreadInfo(
                    0, fileInfo.getUrl(), 0, fileInfo.getLength(), 0);
        } else {
            threadInfo = threadInfoS.get(0);
        }
        // 创建子线程进行下载
        new DownloadThread(threadInfo).start();
    }

    /**
     * 下载线程
     */
    class DownloadThread extends Thread {
        private ThreadInfo mThreadInfo;

        DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            // 向数据库插入线程信息
            if (!mDAO.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())) {
                mDAO.insertThread(mThreadInfo);
            }
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(mThreadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("GET");
                // 设置下载位置
                int start = mThreadInfo.getStart() + mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                // 设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start); // 跳过start中字节的位数，从start+1开始写入
                // 开始下载
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    // 读取数据
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 写入文件
                        raf.write(buffer, 0, len);
                        // 把下载进度发送广播给Activity
                        mFinished += len;
                        // 500ms之后再刷新，减少手机负载
                        if (System.currentTimeMillis() - time > 500) {
                            intent.putExtra("finished", mFinished * 100 / fileInfo.getLength());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            //context.sendBroadcast(intent);
                            // mThreadInfo.setFinished(mFinished);
                        }
                        // 在下载暂停时，保存下载进度
                        if (isPause) {
                            mDAO.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);
                            return;
                        }
                    }
                    // 删除线程信息
                    mDAO.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                    conn.disconnect();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            super.run();
        }
    }
}
