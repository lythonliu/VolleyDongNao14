package com.example.administrator.volleydongnao14.http.download;

import android.os.Environment;
import android.util.Log;

import com.example.administrator.volleydongnao14.http.HttpTaskRunnable;
import com.example.administrator.volleydongnao14.http.RequestInfo;
import com.example.administrator.volleydongnao14.http.ThreadPoolManager;
import com.example.administrator.volleydongnao14.http.download.interfaces.IDownloadServiceCallback;
import com.example.administrator.volleydongnao14.http.interfaces.IHttpListener;
import com.example.administrator.volleydongnao14.http.interfaces.IHttpService;

import java.io.File;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class FileDownManager implements IDownloadServiceCallback {
    private static final String TAG ="dongnao" ;
    //    private  static
    private byte[] lock=new byte[0];
    /**
     * 下载
     * @param url
     */
    public void download(String url)
    {

        synchronized (lock)
        {
            String[] split=url.split("/");
            String fileName=split[split.length-1];

            File targetFile=new File(Environment.getExternalStorageDirectory(),fileName);
            //实例化DownloadItem
            DownloadRecord downloadRecord =new DownloadRecord(url,targetFile.getAbsolutePath());

            RequestInfo requestInfo =new RequestInfo();
            //设置请求下载的策略
            IHttpService httpService=new FileDownHttpService();
            //得到请求头的参数 map
            Map<String,String> map=httpService.getHttpHeadMap();
            /**
             * 处理结果的策略
             */
            IHttpListener httpListener=new DownLoadListener(downloadRecord,this,httpService);

            requestInfo.setHttpListener(httpListener);
            requestInfo.setHttpService(httpService);

            HttpTaskRunnable httpTaskRunnable =new HttpTaskRunnable(requestInfo);
            try {
                ThreadPoolManager.getInstance().execte(new FutureTask<Object>(httpTaskRunnable,null));
            } catch (InterruptedException e) {

            }

        }



    }
    @Override
    public void onDownloadStatusChanged(DownloadRecord downloadRecord) {

    }

    @Override
    public void onTotalLengthReceived(DownloadRecord downloadRecord) {

    }

    @Override
    public void onCurrentSizeChanged(DownloadRecord downloadRecord, double downloadLength, long speed) {
        Log.i(TAG,"下载速度："+ speed/1000 +"k/s");
        Log.i(TAG,"-----路径  "+ downloadRecord.getFilePath()+"  下载长度  "+ downloadLength +"   速度  "+speed);
    }

    @Override
    public void onDownloadSuccess(DownloadRecord downloadRecord) {
        Log.i(TAG,"下载成功    路劲  "+ downloadRecord.getFilePath()+"  url "+ downloadRecord.getUrl());
    }

    @Override
    public void onDownloadPause(DownloadRecord downloadRecord) {

    }

    @Override
    public void onDownloadError(DownloadRecord downloadRecord, int var2, String var3) {

    }
}
