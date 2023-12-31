/***
 * This class is an auxiliary class which is a dao class for emoji.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.emoji;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.appsnipp.education.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 *describe: 表情数据库操作
 *author: Went_Gone
 *create on: 2016/10/27
 */
public class EmojiDao {
    private static final String TAG = "EmojiDao";
    private String path;
    private static EmojiDao dao;
    public static EmojiDao getInstance(){
        if (dao == null){
            synchronized (EmojiDao.class){
                if (dao == null){
                    dao = new EmojiDao();
                }
            }
        }
        return dao;
    }
    private EmojiDao(){
        try {
            path = CopySqliteFileFromRawToDatabases("emoji.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<EmojiBean> getEmojiBean(){
        List<EmojiBean> emojiBeanList = new ArrayList<EmojiBean>();
        //original  SQLiteDatabase.NO_LOCALIZED_COLLATORS
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("emoji", new String[]{"unicodeInt","_id"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            EmojiBean bean = new EmojiBean();
            int unicodeInt = cursor.getInt(0);
            int id = cursor.getInt(1);
            bean.setUnicodeInt(unicodeInt);
            bean.setId(id);
            emojiBeanList.add(bean);
        }
        return emojiBeanList;
    }


    /**
     * 将assets目录下的文件拷贝到database中
     * @return 存储数据库的地址
     */
    public static String CopySqliteFileFromRawToDatabases(String SqliteFileName) throws IOException {
        // 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>
        // 复制的话这里需要换成自己项目的包名
        File dir = new File("data/data/" + "com.appsnipp.education" + "/databases");

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }

        File file= new File(dir, SqliteFileName);
        InputStream inputStream = null;
        OutputStream outputStream =null;

        //通过IO流的方式，将assets目录下的数据库文件l，写入到SD卡中。
        if (!file.exists()) {
            try {
                Log.w("Warn","Entering the first branch");
                file.createNewFile();
                Log.v("Warn","Entering success part");
                //Not right I think
                inputStream = Objects.requireNonNull(App.getInstance().getClass().getClassLoader()).getResourceAsStream("assets/" + SqliteFileName);
                outputStream = new FileOutputStream(file);
                Log.v("Info",SqliteFileName);
                byte[] buffer = new byte[1024];
                int len ;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer,0,len);
                }
            } catch (IOException e) {
                Log.v("Warn","Entering fail part");
                e.printStackTrace();
            }
            finally {
                Log.v("Warn","Output file");
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return file.getPath();
    }
}

