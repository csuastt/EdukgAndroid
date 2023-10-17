/***
 * This Activity is used for chatting between users and the EDUKG intelligent helper.
 * @author Shuning Zhang
 * @version 1.0
 *
 */
package com.appsnipp.education.activities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.adapter.ChatAdapter;
import com.appsnipp.education.bean.AudioMsgBody;
import com.appsnipp.education.bean.FileMsgBody;
import com.appsnipp.education.bean.ImageMsgBody;
import com.appsnipp.education.bean.Message;
import com.appsnipp.education.bean.MsgSendStatus;
import com.appsnipp.education.bean.MsgType;
import com.appsnipp.education.bean.TextMsgBody;
import com.appsnipp.education.bean.VideoMsgBody;
import com.appsnipp.education.util.ChatUiHelper;
import com.appsnipp.education.util.FileUtils;
import com.appsnipp.education.util.LogUtil;
import com.appsnipp.education.util.PictureFileUtil;
import com.appsnipp.education.widget.MediaManager;
import com.appsnipp.education.widget.RecordButton;
import com.appsnipp.education.widget.StateButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

/***
 * This Class implements the activity and ui of the chatting between users and the EDUKG intelligent helper.
 * @author Shuning Zhang
 * @version 1.0
 *
 */
public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.common_toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.common_toolbar_back)
    RelativeLayout toolbarBack;
    @BindView(R.id.icon_toolbar_back)
    ImageView mIconBack; //返回
    @BindView(R.id.llContent)
    LinearLayout mLlContent;
    @BindView(R.id.rv_chat_list)
    RecyclerView mRvChat;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.bottom_layout)
    RelativeLayout mRlBottomLayout;//表情,添加底部布局
    @BindView(R.id.ivAdd)
    ImageView mIvAdd;
    @BindView(R.id.ivEmo)
    ImageView mIvEmo;
    @BindView(R.id.btn_send)
    StateButton mBtnSend;//发送按钮
    @BindView(R.id.ivAudio)
    ImageView mIvAudio;//录音图片
    @BindView(R.id.btnAudio)
    RecordButton mBtnAudio;//录音按钮
    @BindView(R.id.rlEmotion)
    LinearLayout mLlEmotion;//表情布局
    @BindView(R.id.llAdd)
    LinearLayout mLlAdd;//添加布局
    @BindView(R.id.swipe_chat)
    SwipeRefreshLayout mSwipeRefresh;//下拉刷新
    private ChatAdapter mAdapter;
    public static final String 	  mSenderId="right";
    public static final String     mTargetId="left";
    public static final int       REQUEST_CODE_IMAGE=0000;
    public static final int       REQUEST_CODE_VEDIO=1111;
    public static final int       REQUEST_CODE_FILE=2222;
    public static Application mApplication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initContent();
    }

    private ImageView  ivAudio;

    /***
     * This Function is used for the creation of the class ui of Chat Activity.
     * @param
     * @return Nothing
     */
    protected void initContent() {
        ButterKnife.bind(this);
        mAdapter = new ChatAdapter(this, new ArrayList<Message>());
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
        mRvChat.setLayoutManager(mLinearLayout);
        mRvChat.setAdapter(mAdapter);
        mSwipeRefresh.setOnRefreshListener(this);
        initChatUi();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                final boolean isSend = mAdapter.getItem(position).getSenderId().equals(ChatActivity.mSenderId);
                if (ivAudio != null) {
                    if (isSend) {
                        ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                    } else {
                        ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                    }
                    ivAudio = null;
                    MediaManager.reset();
                } else {
                    ivAudio = view.findViewById(R.id.ivAudio);
                    MediaManager.reset();
                    if (isSend) {
                        ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
                    } else {
                        ivAudio.setBackgroundResource(R.drawable.audio_animation_left_list);
                    }
                    AnimationDrawable drawable = (AnimationDrawable) ivAudio.getBackground();
                    drawable.start();
                    MediaManager.playSound(ChatActivity.this, ((AudioMsgBody) mAdapter.getData().get(position).getBody()).getLocalPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (isSend) {
                                ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                            } else {
                                ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                            }

                            MediaManager.release();
                        }
                    });
                }
            }
        });
        toolbarBack.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                //  先是得到Fragment所在的 Activity，然后在得到管理器对象，
                // 获取并开始事务对象，在进行切换，然后在利用addToBackStack()，最终提交事务
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mIconBack.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                //  先是得到Fragment所在的 Activity，然后在得到管理器对象，
                // 获取并开始事务对象，在进行切换，然后在利用addToBackStack()，最终提交事务
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        toolbarTitle.setOnClickListener(new View.OnClickListener() {      //  点击跳转时
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                //  先是得到Fragment所在的 Activity，然后在得到管理器对象，
                // 获取并开始事务对象，在进行切换，然后在利用addToBackStack()，最终提交事务
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    /***
     * This Function is used for refreshing the history message.
     * @param
     * @return Nothing
     */
    @Override
    public void onRefresh() {
        //下拉刷新模拟获取历史消息
        List<Message>  mReceiveMsgList=new ArrayList<Message>();
        //构建文本消息
        Message mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage("收到的消息");
        mMessgaeText.setBody(mTextMsgBody);
        mReceiveMsgList.add(mMessgaeText);
        //构建图片消息
        //构建文件消息
        Message mMessgaeFile=getBaseReceiveMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody=new FileMsgBody();
        mFileMsgBody.setDisplayName("收到的文件");
        mFileMsgBody.setSize(12);
        mMessgaeFile.setBody(mFileMsgBody);
        mReceiveMsgList.add(mMessgaeFile);
        mAdapter.addData(0,mReceiveMsgList);

        mSwipeRefresh.setRefreshing(false);
    }


    /***
     * This Function is used for the initation of the ui layout of this Activity.
     * @param
     * @return Nothing
     */
    private void initChatUi(){
        //mBtnAudio
        final ChatUiHelper mUiHelper= ChatUiHelper.with(this);
        mUiHelper.bindContentLayout(mLlContent)
                .bindttToSendButton(mBtnSend)
                .bindEditText(mEtContent)
                .bindBottomLayout(mRlBottomLayout)
                .bindEmojiLayout(mLlEmotion)
                .bindAddLayout(mLlAdd)
                .bindToAddButton(mIvAdd)
                .bindToEmojiButton(mIvEmo)
                .bindAudioBtn(mBtnAudio)
                .bindAudioIv(mIvAudio)
                .bindEmojiData();
        //底部布局弹出,聊天列表上滑
        mRvChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRvChat.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter.getItemCount() > 0) {
                                mRvChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });
        //点击空白区域关闭键盘
        mRvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                mEtContent.clearFocus();
                mIvEmo.setImageResource(R.mipmap.ic_emoji);
                return false;
            }
        });
        //
        ((RecordButton) mBtnAudio).setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                LogUtil.d("录音结束回调");
                File file = new File(audioPath);
                if (file.exists()) {
                    sendAudioMessage(audioPath,time);
                }
            }
        });

    }

    @OnClick({R.id.btn_send,R.id.rlPhoto,R.id.rlVideo,R.id.rlLocation,R.id.rlFile})
    public void onViewClicked(View view) {
        String sendText = "";
        switch (view.getId()) {
            case R.id.btn_send:
                sendText = mEtContent.getText().toString();
                sendTextMsg(mEtContent.getText().toString());
                mEtContent.setText("");
                break;
            case R.id.rlPhoto:
                PictureFileUtil.openGalleryPic(ChatActivity.this,REQUEST_CODE_IMAGE);
                break;
            case R.id.rlVideo:
                PictureFileUtil.openGalleryAudio(ChatActivity.this,REQUEST_CODE_VEDIO);
                break;
            case R.id.rlFile:
                PictureFileUtil.openFile(ChatActivity.this,REQUEST_CODE_FILE);
                break;
            case R.id.rlLocation:
                break;
        }
        if(sendText.equals("杜甫")) {
            List<Message>  mReceiveMsgList=new ArrayList<Message>();
            //构建文本消息
            Message mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
            TextMsgBody mTextMsgBody=new TextMsgBody();
            mTextMsgBody.setMessage("杜甫也是很有名的大诗人哦，你可以再去搜搜李白或者白居易。");
            mMessgaeText.setBody(mTextMsgBody);
            mReceiveMsgList.add(mMessgaeText);
            mAdapter.addData(mAdapter.getItemCount(), mReceiveMsgList);
        } else if(sendText.equals("233333")) {
            List<Message>  mReceiveMsgList=new ArrayList<Message>();
            //构建文本消息
            Message mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
            TextMsgBody mTextMsgBody=new TextMsgBody();
            mTextMsgBody.setMessage("dbq，你在逗我玩吧~");
            mMessgaeText.setBody(mTextMsgBody);
            mReceiveMsgList.add(mMessgaeText);
            mAdapter.addData(mAdapter.getItemCount(), mReceiveMsgList);
        } else {
            String course;
            for (String i : AppSingleton.courseArray) {
                if (sendText.contains(i)) {
                    course = AppSingleton.courseMap.get(i);
                }
            }
            course = AppSingleton.nowCourse;
            String url = AppSingleton.EDUKG_PREFIX + "/api/typeOpen/open/inputQuestion";
            System.err.println(url);
            SharedPreferences preferences = this.getSharedPreferences("userInfo",
                    this.MODE_PRIVATE);
            String id = preferences.getString("edukg_id", "");
            OkHttpClient client = new OkHttpClient();
            System.err.println(id);
            System.err.println(sendText);
            System.err.println(AppSingleton.nowCourse);
            FormBody body = new FormBody.Builder()
                    .add("inputQuestion", sendText)
                    .add("id", id)
                    .add("course", course)
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            okhttp3.Call call = client.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    System.err.println(response.toString());
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        System.err.println(result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        int code = jsonObject.get("code").getAsInt();
                        if (code == 0) {

                        }
                    }
                }
            });
            url = AppSingleton.EDUKG_PREFIX + "/api/typeOpen/open/linkInstance";
            System.err.println(url);
            System.err.println(course);
            preferences = this.getSharedPreferences("userInfo",
                    this.MODE_PRIVATE);
            id = preferences.getString("edukg_id", "");
            client = new OkHttpClient();
            body = new FormBody.Builder()
                    .add("context", sendText)
                    .add("id", id)
                    .add("course", "chinese")
                    .build();
            request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            call = client.newCall(request);
            String finalSendText = sendText;
            String finalCourse = course;
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    String finalText = "真的对不起，小助手也不知道给你推荐什么好咯...";
                    System.err.println("Failed");
                    List<Message> mReceiveMsgList = new ArrayList<Message>();
                    Message mMessgaeText = getBaseReceiveMessage(MsgType.TEXT);
                    TextMsgBody mTextMsgBody = new TextMsgBody();
                    mTextMsgBody.setMessage(finalText);
                    mMessgaeText.setBody(mTextMsgBody);
                    mReceiveMsgList.add(mMessgaeText);
                    mAdapter.addData(mAdapter.getItemCount(), mReceiveMsgList);
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    System.err.println(response.toString());
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        System.err.println(result);
                        JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                        int code = jsonObject.get("code").getAsInt();
                        if (code == 0) {
                            JsonArray re = jsonObject.get("data").getAsJsonObject().get("results").getAsJsonArray();
                            for (JsonElement e : re) {
                                String r = "chinese";
                                query(r, e.getAsJsonObject().get("entity").getAsString());
                            }
                        }
                    }
                }
            });
        }
    }



    /***
     * This Function is used for the post processing of the
     * callback of the other intent pages.
     * @param requestCode the type of the request
     * @param resultCode the type of the processing result
     * @param data the Intent page
     * @return Nothing
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FILE:
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    LogUtil.d("获取到的文件路径:"+filePath);
                    sendFileMessage(mSenderId, mTargetId, filePath);
                    break;
                case REQUEST_CODE_IMAGE:
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListPic) {
                        LogUtil.d("获取图片路径成功:"+  media.getPath());
                        sendImageMessage(media);
                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    // 视频选择结果回调
                    List<LocalMedia> selectListVideo = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListVideo) {
                        LogUtil.d("获取视频路径成功:"+  media.getPath());
                        sendVedioMessage(media);
                    }
                    break;
            }
        }
    }

    /***
     * This Function is used for the processing and sending of text message.
     * @param hello the content of the text message sent
     * @return Nothing
     */
    //文本消息
    private void sendTextMsg(String hello)  {
        final Message mMessgae=getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage(hello);
        mMessgae.setBody(mTextMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }

    /***
     * This Function is used for the query sending to the backend of EDUKG to get results corresponding to the question.
     * @param course the course selected in String form
     * @param entity the entity mentioned in String form
     * @return Nothing
     */
    public void query(String course, String entity) {
        OkHttpClient client = new OkHttpClient();
        String url = AppSingleton.EDUKG_PREFIX +  "/api/typeOpen/open/infoByInstanceName";
        entity = entity.split(" ")[0];
        url += "?";
        SharedPreferences preferences = getSharedPreferences ("userInfo", MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("edukg_id", "");
        url += "id=";
        url += id;
        url += "&course=";
        if(AppSingleton.courseMap.containsKey(course)) {
            url += AppSingleton.courseMap.get(course);
        } else {
            url += course;
        }
        url += "&name=";
        url += entity;
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String finalText = "小助手也不知道答案呀";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                        List<Message>  mReceiveMsgList=new ArrayList<Message>();
                        Message mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
                        TextMsgBody mTextMsgBody=new TextMsgBody();
                        mTextMsgBody.setMessage(finalText);
                        mMessgaeText.setBody(mTextMsgBody);
                        mReceiveMsgList.add(mMessgaeText);
                        mAdapter.addData(mAdapter.getItemCount(),mReceiveMsgList);
                    }
                });
                System.err.println(e);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if(response.isSuccessful()){
                    String result = response.body().string();
                    System.err.println(result);
                    JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                    int code = jsonObject.get("code").getAsInt();
                    if(code == -1 || code == 9) {

                    } else if(code == 0) {
                        JsonObject answerObject = jsonObject.get("data").getAsJsonObject();
                        JsonArray property = answerObject.get("property").getAsJsonArray();
                        JsonArray content = answerObject.get("content").getAsJsonArray();
                        String text = "";
                        text = answerObject.get("label").getAsString() + "是" + course + "科目的知识点。\n";
                        text += "知识梳理:\n";
                        for(JsonElement t: property) {
                            try {

                                //System.err.println(t.getAsJsonObject().get("object").getAsString().split("#")[0]);
                                if(!t.getAsJsonObject().get("object").getAsString().split("#")[0].contains("www")) {
                                    text +=
                                            answerObject.get("label").getAsString() + "的" +
                                                    t.getAsJsonObject().get("predicateLabel").getAsString()
                                                    + "是" +
                                                    t.getAsJsonObject().get("object").getAsString().split("#")[0] + "。\n";
                                }
                            } catch( Exception e){
                            }
                        }
                        text += "知识关联\n";
                        for(JsonElement t: content) {
                            try {
                                if(!t.getAsJsonObject().get("object_label").getAsString().contains("cs.tsinghua")) {
                                    text += answerObject.get("label").getAsString() + "的" +
                                            t.getAsJsonObject().get("predicate_label").getAsString() + "是" +
                                            t.getAsJsonObject().get("object_label").getAsString() +
                                            "。\n";
                                }
                            } catch (Exception e){
                                try {
                                    if(t.getAsJsonObject().get("predicate_label").getAsString().contains("cs.tsinghua")) {
                                        text += answerObject.get("label").getAsString() + "的" +
                                                t.getAsJsonObject().get("predicate_label").getAsString() + "是" + t.getAsJsonObject().get("subject_label").getAsString()
                                                + "。\n";
                                    }
                                } catch(Exception ee) {

                                }
                            }
                        }
                        String finalText;
                        if(text.length() > 200) {
                            finalText = text.substring(0, 105) + "...";
                        } else {
                            finalText = text;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO
                                List<Message>  mReceiveMsgList=new ArrayList<Message>();
                                Message mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
                                TextMsgBody mTextMsgBody=new TextMsgBody();
                                mTextMsgBody.setMessage(finalText);
                                mMessgaeText.setBody(mTextMsgBody);
                                mReceiveMsgList.add(mMessgaeText);
                                mAdapter.addData(mAdapter.getItemCount(),mReceiveMsgList);
                            }
                        });
                        //处理UI需要切换到UI线程处理
                    }
                }
            }
        });
    }

    /***
     * This Function is used for the post processing of the
     * callback of the other intent pages.
     * @param media the content of the local media object in audio form
     * @return Nothing
     */
    //图片消息
    private void sendImageMessage(final LocalMedia media) {
        final Message mMessgae=getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody=new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getCompressPath());
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }

    /***
     * This Function is used for the post processing of the
     * callback of the other intent pages.
     * @param media the content of the local media object in video form
     * @return Nothing
     */
    //视频消息
    private void sendVedioMessage(final LocalMedia media) {
        final Message mMessgae=getBaseSendMessage(MsgType.VIDEO);
        //生成缩略图路径
        String vedioPath=media.getPath();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(vedioPath);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        String imgname = System.currentTimeMillis() + ".jpg";
        String urlpath = Environment.getExternalStorageDirectory() + "/" + imgname;
        File f = new File(urlpath);
        try {
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        }catch ( Exception e) {
            LogUtil.d("视频缩略图路径获取失败："+e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody=new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }

    /***
     * This Function is used for the post processing of the
     * callback of the other intent pages.
     * @param from the string representing the source of the file message
     * @param to the string representing the destination of the file message
     * @param path the string representing the file path
     * @return Nothing
     */
    //文件消息
    private void sendFileMessage(String from, String to, final String path) {
        final Message mMessgae=getBaseSendMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody=new FileMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDisplayName(FileUtils.getFileName(path));
        mFileMsgBody.setSize(FileUtils.getFileLength(path));
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }

    /***
     * This Function is used for the post processing of the
     * callback of the other intent pages.
     * @param path the content of the path of the audio message sent
     * @param time the length of the audio message
     * @return Nothing
     */
    //语音消息
    private void sendAudioMessage(  final String path,int time) {
        final Message mMessgae=getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody=new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData( mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
        //下拉刷新模拟获取历史消息
        List<Message>  mReceiveMsgList=new ArrayList<Message>();
        //构建文本消息
        Message mMessgaeText=getBaseReceiveMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody=new TextMsgBody();
        mTextMsgBody.setMessage("你是想问李白的代表作吗？你可以去看看：梦游天姥吟留别 -- 海客谈瀛洲，烟涛微茫信难求...");
        mMessgaeText.setBody(mTextMsgBody);
        mReceiveMsgList.add(mMessgaeText);
        mAdapter.addData(mAdapter.getItemCount(), mReceiveMsgList);
    }

    /***
     * This Function is used for the basic processing of the message's processing
     * @param msgType the basic type of the message
     * @return Nothing
     */
    private Message getBaseSendMessage(MsgType msgType){
        Message mMessgae=new Message();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    /***
     * This Function is used for the basic receiving of the message's processing.
     * @param msgType the receiving type of the message
     * @return Nothing
     */
    private Message getBaseReceiveMessage(MsgType msgType){
        Message mMessgae=new Message();
        mMessgae.setUuid(UUID.randomUUID()+"");
        mMessgae.setSenderId(mTargetId);
        mMessgae.setTargetId(mSenderId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }

    /***
     * This Function is used for the receiving and updating of the message.
     * @param mMessgae the message to the sent
     * @return Nothing
     */
    private void updateMsg(final Message mMessgae) {
        mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);
        //模拟2秒后发送成功
        new Handler().postDelayed(new Runnable() {
            public void run() {
                int position=0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i=0;i<mAdapter.getData().size();i++){
                    Message mAdapterMessage=mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())){
                        position=i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 800);

    }



}
