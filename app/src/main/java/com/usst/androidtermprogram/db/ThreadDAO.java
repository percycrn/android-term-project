package com.usst.androidtermprogram.db;


import com.usst.androidtermprogram.entities.ThreadInfo;

import java.util.List;

/**
 * 数据访问接口
 * Created by percycrn on 2018/3/27.
 */

public interface ThreadDAO {
    void insertThread(ThreadInfo threadInfo);

    void deleteThread(String url, int thread_id);

    /**
     * 更新线程下载进度
     *
     * @param url download destination
     * @param thread_id 线程 id
     */
    void updateThread(String url, int thread_id, int finished);

    /**
     * 查询文件的线程信息
     *
     * @param url 下载地址
     * @return
     */
    List<ThreadInfo> getThreads(String url);

    /**
     * 线程信息是否存在
     *
     * @param url
     * @param thread_id
     * @return
     */
    boolean isExists(String url, int thread_id);
}
