package com.example.administrator.volleydongnao14.http.download;

import com.example.administrator.volleydongnao14.http.HttpTaskRunnable;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class DownloadRecord extends Copyable<DownloadRecord> {

    public DownloadRecord(String url, String filePath) {
        this.url = url;
        this.filePath = filePath;
    }

    public DownloadRecord( ) {
    }

    private long currentLength;

    private long totalLength;

    private String url ;

    private String filePath;

    private  transient HttpTaskRunnable httpTaskRunnable;
    //下载的状态
    private DownloadStatus status;

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public HttpTaskRunnable getHttpTaskRunnable() {
        return httpTaskRunnable;
    }

    public void setHttpTaskRunnable(HttpTaskRunnable httpTaskRunnable) {
        this.httpTaskRunnable = httpTaskRunnable;
    }
}
