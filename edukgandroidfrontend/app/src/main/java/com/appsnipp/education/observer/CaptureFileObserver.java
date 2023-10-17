/***
 * This knowledge class is the file observer capturing class containing the file tansfering process which is not exposed to users
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.observer;

import android.database.ContentObserver;
import android.net.Uri;

import android.os.Handler;

public class CaptureFileObserver extends ContentObserver {
    private final Uri mContentUri;
    private final CaptureCallback mCaptureCallback;

    public CaptureFileObserver(Uri contentUri, CaptureCallback captureCallback, Handler handler) {
        super(handler);
        mCaptureCallback = captureCallback;
        mContentUri = contentUri;
    }
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        // 触发了截屏 注意这里会多次回调
        if (mCaptureCallback != null){
            mCaptureCallback.onMediaFileChanged(mContentUri);
        }
    }
    /**
     * 内容观察者回调事件
     */
    public interface CaptureCallback {

        void onMediaFileChanged(Uri contentUri);
    }
}
