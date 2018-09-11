package com.example.administrator.volleydongnao14.http.download;

import android.os.Handler;
import android.os.Looper;

import com.example.administrator.volleydongnao14.http.download.interfaces.IDownLitener;
import com.example.administrator.volleydongnao14.http.download.interfaces.IDownloadServiceCallback;
import com.example.administrator.volleydongnao14.http.interfaces.IHttpService;

import org.apache.http.HttpEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/16 0016.
 * 1
 * DownItenInfo
 */

public class DownLoadListener implements IDownLitener{

    private DownloadRecord downloadRecord;

    private File targetFile;
    protected  String url;
    private long breakPoint;
    private IDownloadServiceCallback downloadServiceCallback;

    private IHttpService httpService;
    /**
     * 得到主线程
     */
    private Handler handler=new Handler(Looper.getMainLooper());
    public DownLoadListener(DownloadRecord downloadRecord,
                            IDownloadServiceCallback downloadServiceCallback,
                            IHttpService httpService) {
        this.downloadRecord = downloadRecord;
        this.downloadServiceCallback = downloadServiceCallback;
        this.httpService = httpService;
        this.targetFile =new File(downloadRecord.getFilePath());
        /**
         * 得到已经下载的长度
         */
        this.breakPoint= targetFile.length();
    }

    public void addHttpHeader(Map<String,String> headerMap)
    {


    }
    public DownLoadListener(DownloadRecord downloadRecord) {
        this.downloadRecord = downloadRecord;
    }

    @Override
    public void setHttpServive(IHttpService httpServive) {
        this.httpService=httpServive;
    }

    /**
     * 设置取消接口
     */
    @Override
    public void setCancleCalle() {

    }

    @Override
    public void setPuaseCallble() {

    }

    @Override
    public void onSuccess(HttpEntity httpEntity) {
        InputStream inputStream = null;
        try {
            inputStream = httpEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long startTime = System.currentTimeMillis();
        //用于计算每秒多少k
        long speed = 0L;
        //花费时间
        long useTime = 0L;
        //下载的长度
        long writeLen = 0L;
        //接受的长度
        long receiveLenSlice = 0L;
        boolean bufferLen = false;
        /*得到下载的长度*/
        long dataLength = httpEntity.getContentLength();
        //单位时间下载的字节数
        long calcSpeedLenSlice = 0L;
        /*总数*/
        long totalLength = this.breakPoint + dataLength;
        //更新数量
        this.receviceTotalLength(totalLength);
        //更新状态
        this.downloadStatusChange(DownloadStatus.downloading);
        byte[] buffer = new byte[1024];
        int readTimes = 0;
        long currentTime = System.currentTimeMillis();
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        try {
            if (!makeDir(this.getTargetFile().getParentFile())) {
                downloadServiceCallback.onDownloadError(downloadRecord,1,"创建文件夹失败");
            } else {
                fos = new FileOutputStream(this.getTargetFile(), true);
                bos = new BufferedOutputStream(fos);
                int length = 1;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (this.getHttpService().isCancle()) {
                        downloadServiceCallback.onDownloadError(downloadRecord, 1, "用户取消了");
                        return;
                    }

                    if (this.getHttpService().isPause()) {
                        downloadServiceCallback.onDownloadError(downloadRecord, 2, "用户暂停了");
                        return;
                    }

                    bos.write(buffer, 0, length);
                    writeLen += (long) length;
                    receiveLenSlice += (long) length;
                    calcSpeedLenSlice += (long) length;
                    ++readTimes;
                    if (receiveLenSlice * 10L / totalLength >= 1L || readTimes >= 5000) {
                        currentTime = System.currentTimeMillis();
                        useTime = currentTime - startTime;
                        startTime = currentTime;
                        speed = 1000L * calcSpeedLenSlice / useTime;
                        readTimes = 0;
                        calcSpeedLenSlice = 0L;
                        receiveLenSlice = 0L;
                        this.downloadLengthChange(this.breakPoint + writeLen, totalLength, speed);
                    }
                }
                bos.close();
                inputStream.close();
                if (dataLength != writeLen) {
                    downloadServiceCallback.onDownloadError(downloadRecord, 3, "下载长度不相等");
                } else {
                    this.downloadLengthChange(this.breakPoint + writeLen, totalLength, speed);
                    this.downloadServiceCallback.onDownloadSuccess(downloadRecord.copy());
                }
            }
        } catch (IOException ioException) {
            if (this.getHttpService() != null) {
//                this.getHttpService().abortRequest();
            }
            return;
        } catch (Exception e) {
            if (this.getHttpService() != null) {
//                this.getHttpService().abortRequest();
            }
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }

                if (httpEntity != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 创建文件夹的操作
     * @param parentFile
     * @return
     */
    private boolean makeDir(File parentFile) {
        return parentFile.exists()&&!parentFile.isFile()
                ?parentFile.exists()&&parentFile.isDirectory():
                parentFile.mkdirs();
    }


    private void downloadLengthChange(final long downloadLength, final long totalLength, final long speed) {

        downloadRecord.setCurrentLength(downloadLength);
        if(downloadServiceCallback !=null)
        {
            DownloadRecord downloadRecord= this.downloadRecord.copy();
            synchronized (this.downloadServiceCallback)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallback.onCurrentSizeChanged(DownLoadListener.this.downloadRecord, downloadLength /totalLength,speed);
                    }
                });
            }

        }

    }

    /**
     * 更改下载时的状态
     * @param downloading
     */
    private void downloadStatusChange(DownloadStatus downloading) {
        downloadRecord.setStatus(downloading);
        final DownloadRecord copyDownloadRecord = downloadRecord.copy();
        if(downloadServiceCallback !=null)
        {
            synchronized (this.downloadServiceCallback)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallback.onDownloadStatusChanged(copyDownloadRecord);
                    }
                });
            }
        }
    }

    /**
     * 回调  长度的变化
     * @param totalLength
     */
    private void receviceTotalLength(long totalLength) {
        downloadRecord.setCurrentLength(totalLength);
        final DownloadRecord copyDownloadRecord = downloadRecord.copy();
        if(downloadServiceCallback !=null)
        {
            synchronized (this.downloadServiceCallback)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        downloadServiceCallback.onTotalLengthReceived(copyDownloadRecord);
                    }
                });
            }
        }

    }

    @Override
    public void onFail() {

    }

    public IHttpService getHttpService() {
        return httpService;
    }

    public File getTargetFile() {
        return targetFile;
    }
}
