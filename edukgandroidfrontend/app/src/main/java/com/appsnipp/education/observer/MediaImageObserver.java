/***
 * This media Image Observer class is a class containing the media Image loading process file
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.observer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class MediaImageObserver extends MediaFileBaseObserver {
    private static final String TAG = MediaImageObserver.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static volatile MediaImageObserver mInstance = null;
    private static final String[] MEDIA_STORE_IMAGE = {
            MediaStore.Images.ImageColumns.DATA,
            // 时间 这里不能用 Date_ADD 因为是秒级 按时间筛选不准确
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            // 宽
            MediaStore.Images.ImageColumns.WIDTH
    };
    // 截屏关键词 随时补充
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap", "Screenshot","截屏"
    };
    // 按照日期插入的顺序取第一条
    private final static String QUERY_ORDER_SQL = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC LIMIT 1";
    private final Point mPoint;
    public static MediaFileBaseObserver getInstance(Application application) {
        if (mInstance == null) {
            synchronized (MediaFileBaseObserver.class) {
                if (mInstance == null) {
                    mInstance = new MediaImageObserver(application.getApplicationContext());
                }
            }
        }
        return mInstance;
    }
    public MediaImageObserver(Context context) {
        super(context);
        mPoint = null;
        //ScreenUtil.getRealScreenSize(context);
    }
    @Override
    void acquireTargetFile(Uri contentUri) {
        Cursor cursor = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Bundle bundle = new Bundle();
                // 按照文件时间
                bundle.putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, new String[]{MediaStore.Files.FileColumns.DATE_TAKEN});
                // 降序
                bundle.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_DESCENDING);
                // 取第一张
                bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 1);
                cursor = mContentResolver.query(contentUri, MEDIA_STORE_IMAGE, bundle,null);
            } else {
                // 查找
                cursor = mContentResolver.query(contentUri, MEDIA_STORE_IMAGE, null, null, QUERY_ORDER_SQL);
            }
            findImagePathByCursor(cursor);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            } else {
                e.printStackTrace();
            }
        }finally {
            if (cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
    }
    private void findImagePathByCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()){
            Log.d(TAG,"Cannot find newest image file");
            return;
        }
        // 获取 文件索引
        int imageColumnIndexData = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        int imageCreateDateIndexData = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
        int imageWidthColumnIndexData = cursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH);
        String imagePath = cursor.getString(imageColumnIndexData);
        int imageWidth = cursor.getInt(imageWidthColumnIndexData);
        long imageCreateDate = cursor.getLong(imageCreateDateIndexData);
        // 时间判断 判断截屏时间 与 截屏图片实际生成时间的差
        if (imageCreateDate < mStartListenTime) {
            return;
        }
        // 这里只判断width 长截屏无法判断
        if (mPoint != null && mPoint.x != imageWidth){
            return;
        }
        // path 为空
        if (TextUtils.isEmpty(imagePath)){
            return;
        }
        // 判断关键词
        String lowerCasePath = imagePath.toLowerCase();
        // 关键词比对
        for (String keyword : KEYWORDS) {
            if (lowerCasePath.contains(keyword)){
                if (mCaptureCallback != null) {
                    //mCaptureCallback.capture(imagePath);
                }
                break;
            }
        }
    }
}
