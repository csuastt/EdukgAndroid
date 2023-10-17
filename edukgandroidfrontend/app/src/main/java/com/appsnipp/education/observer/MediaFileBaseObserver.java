/***
 * This media file base observer class is a class containing the media file updating and transfering process
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

public abstract class MediaFileBaseObserver implements CaptureFileObserver.CaptureCallback {
    protected Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 获取截屏事件回调
     */
    protected CaptureFileObserver.CaptureCallback mCaptureCallback;
    private final CaptureFileObserver mCaptureInternalFileObserver;
    private final CaptureFileObserver mCaptureExternalFileObserver;
    private final Uri[] mContentUris = {MediaStore.Images.Media.INTERNAL_CONTENT_URI, MediaStore.Images.Media.EXTERNAL_CONTENT_URI};
    protected final ContentResolver mContentResolver;
    protected long mStartListenTime;
    public MediaFileBaseObserver(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        // 内部外部媒体文件的监听
        mCaptureInternalFileObserver = new CaptureFileObserver(mContentUris[0], this, mHandler);
        mCaptureExternalFileObserver = new CaptureFileObserver(mContentUris[1], this, mHandler);
    }
    /**
     * 开始进行捕捉截屏监听
     */
    public void registerCaptureListener(){
        // 记录开始监听的时间 算是一个图片是否是截屏的一个指标
        mStartListenTime = System.currentTimeMillis();
        // 注意 第二个boolean参数 要设置为true 不然有些机型由于多媒体文件层级不同 导致变化监听不到 所以设置后代文件夹发生了文件改变也要进行通知
        mContentResolver.registerContentObserver(mContentUris[0],true, mCaptureInternalFileObserver);
        mContentResolver.registerContentObserver(mContentUris[1],true, mCaptureExternalFileObserver);
    }
    /**
     * 解除绑定
     */
    public void unregisterCaptureListener(){
        mContentResolver.unregisterContentObserver(mCaptureInternalFileObserver);
        mContentResolver.unregisterContentObserver(mCaptureExternalFileObserver);
    }
    /**
     * 设置回调监听
     * @param captureCallback 回调
     */
    public void setCaptureCallbackListener(CaptureFileObserver.CaptureCallback captureCallback){
        mCaptureCallback = captureCallback;
    }
    @Override
    public void onMediaFileChanged(Uri contentUri) {
        acquireTargetFile(contentUri);
    }
    /**
     * 获取目标的文件
     * @param contentUri 内容URI
     */
    abstract void acquireTargetFile(Uri contentUri);
}