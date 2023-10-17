package com.appsnipp.education.ui.detail;

/***
 * This is the question detail page which shows detailed questions
 * @author Shuning Zhang
 * @version 1.0
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.appsnipp.education.AppSingleton;
import com.appsnipp.education.R;
import com.appsnipp.education.model.Question;
import com.appsnipp.education.ui.menuhome.HomeFragment;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionDetail extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String qName = "";
    private Question question = new Question();
    private String defaultText = "滑动即可查看答案";

    public QuestionDetail() {
        // Required empty public constructor
    }
    public QuestionDetail(Question _question) {
        question = _question;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuestionDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static QuestionDetail newInstance(String param1, String param2) {
        QuestionDetail fragment = new QuestionDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_question_detail, container, false);
        ImageView toolbarBack = mView.findViewById(R.id.icon_question_toolbar_back);
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_question_detail, new HomeFragment()).commit();
            }
        });
        // DEBUG
        ArrayList<String> _label = new ArrayList<>();
        _label.add("math");
        if(question == null) {
            question = new Question("空问题", _label, "出错了", "问题不存在", "答案不存在",
                    null);
        }
        TextView questionView = mView.findViewById(R.id.text_question);
        TextView answerView = mView.findViewById(R.id.answer_question);
        TextView inputView = mView.findViewById(R.id.choose_question);
        Switch switchView = mView.findViewById(R.id.switch_question);
        RadioGroup radio = mView.findViewById(R.id.choice_4_append_0);
        // 只能判断第一道题
        switchView.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true) {
                    if(question.getText().contains("A") || question.getText().contains("B") ||
                            question.getText().contains("C") || question.getText().contains("D")) {
                        if(radio.getCheckedRadioButtonId() == R.id.choice_A_0) {
                            if(question.getAnswer().equals("A")) {
                                Toast.makeText(getActivity(), "回答正确", Toast.LENGTH_SHORT).show();
                                send_try(true);
                            } else {
                                Toast.makeText(getActivity(), "回答错误", Toast.LENGTH_SHORT).show();
                                send_try(false);
                            }
                        } else if(radio.getCheckedRadioButtonId() == R.id.choice_B_0) {
                            if(question.getAnswer().contains("B")) {
                                Toast.makeText(getActivity(), "回答正确", Toast.LENGTH_SHORT).show();
                                send_try(true);
                            } else {
                                Toast.makeText(getActivity(), "回答错误", Toast.LENGTH_SHORT).show();
                                send_try(false);
                            }
                        } else if(radio.getCheckedRadioButtonId() == R.id.choice_C_0) {
                            if(question.getAnswer().contains("C")) {
                                Toast.makeText(getActivity(), "回答正确", Toast.LENGTH_SHORT).show();
                                send_try(true);
                            } else {
                                Toast.makeText(getActivity(), "回答错误", Toast.LENGTH_SHORT).show();
                                send_try(false);
                            }
                        } else if(radio.getCheckedRadioButtonId() == R.id.choice_D_0) {
                            if(question.getAnswer().contains("D")) {
                                Toast.makeText(getActivity(), "回答正确", Toast.LENGTH_SHORT).show();
                                send_try(true);
                            } else {
                                Toast.makeText(getActivity(), "回答错误", Toast.LENGTH_SHORT).show();
                                send_try(false);
                            }
                        }
                    } else {
                        if(inputView.getText().toString().contains(question.getAnswer())) {
                            Toast.makeText(getActivity(), "回答正确", Toast.LENGTH_SHORT).show();
                            send_try(true);
                        } else if(inputView.getText().equals("")){
                            Toast.makeText(getActivity(), "未作答", Toast.LENGTH_SHORT).show();
                            send_try(false);
                        } else {
                            Toast.makeText(getActivity(), "回答错误", Toast.LENGTH_SHORT).show();
                            send_try(false);
                        }
                    }
                    // 发送请求
                    answerView.setText(question.getAnswer());
                } else {
                    answerView.setText(defaultText);
                }
            }
        });
        if(question.getText().contains("A") || question.getText().contains("B") ||
                question.getText().contains("C") || question.getText().contains("D")) {
            if(question.getText().contains("小题") ){
                String[] split = question.getText().split("小题");
                try {
                    String[] _A = split[1].split("A");
                    questionView.setText(_A[0]);
                    RadioButton A = mView.findViewById(R.id.choice_A_0);
                    RadioButton B = mView.findViewById(R.id.choice_B_0);
                    RadioButton C = mView.findViewById(R.id.choice_C_0);
                    RadioButton D = mView.findViewById(R.id.choice_D_0);
                    String[] _B = _A[1].split("B");
                    A.setText("A: " + _B[0]);
                    String[] _C = _B[1].split("C");
                    B.setText("B: " + _C[0]);
                    String[] _D = _C[1].split("D");
                    C.setText("C: " + _D[0]);
                    D.setText("D: " + _D[1].substring(0, _D[1].length()-1));
                    radio.setVisibility(VISIBLE);
                    A.setVisibility(VISIBLE);
                    B.setVisibility(VISIBLE);
                    C.setVisibility(VISIBLE);
                    D.setVisibility(VISIBLE);
                    inputView.setVisibility(INVISIBLE);
                } catch (Exception e) {
                }
                try {
                    String[] _A = split[2].split("A");
                    RadioButton A = mView.findViewById(R.id.choice_A_1);
                    RadioButton B = mView.findViewById(R.id.choice_B_1);
                    RadioButton C = mView.findViewById(R.id.choice_C_1);
                    RadioButton D = mView.findViewById(R.id.choice_D_1);
                    String[] _B = _A[1].split("B");
                    A.setText("A: " + _B[0]);
                    String[] _C = _B[1].split("C");
                    B.setText("B: " + _C[0]);
                    String[] _D = _C[1].split("D");
                    C.setText("C: " + _D[0]);
                    D.setText("D: " + _D[1].substring(0, _D[1].length()-1));
                    radio.setVisibility(VISIBLE);
                    A.setVisibility(VISIBLE);
                    B.setVisibility(VISIBLE);
                    C.setVisibility(VISIBLE);
                    D.setVisibility(VISIBLE);
                    inputView.setVisibility(INVISIBLE);
                } catch (Exception e) {
                }
                try {
                    String[] _A = split[3].split("A");
                    RadioButton A = mView.findViewById(R.id.choice_A_2);
                    RadioButton B = mView.findViewById(R.id.choice_B_2);
                    RadioButton C = mView.findViewById(R.id.choice_C_2);
                    RadioButton D = mView.findViewById(R.id.choice_D_2);
                    String[] _B = _A[1].split("B");
                    A.setText("A: " + _B[0]);
                    String[] _C = _B[1].split("C");
                    B.setText("B: " + _C[0]);
                    String[] _D = _C[1].split("D");
                    C.setText("C: " + _D[0]);
                    D.setText("D: " + _D[1].substring(0, _D[1].length()-1));
                    radio.setVisibility(VISIBLE);
                    A.setVisibility(VISIBLE);
                    B.setVisibility(VISIBLE);
                    C.setVisibility(VISIBLE);
                    D.setVisibility(VISIBLE);
                    inputView.setVisibility(INVISIBLE);
                } catch (Exception e) {
                }
                try {
                    String[] _A = split[4].split("A");
                    RadioButton A = mView.findViewById(R.id.choice_A_3);
                    RadioButton B = mView.findViewById(R.id.choice_B_3);
                    RadioButton C = mView.findViewById(R.id.choice_C_3);
                    RadioButton D = mView.findViewById(R.id.choice_D_3);
                    String[] _B = _A[1].split("B");
                    A.setText("A: " + _B[0]);
                    String[] _C = _B[1].split("C");
                    B.setText("B: " + _C[0]);
                    String[] _D = _C[1].split("D");
                    C.setText("C: " + _D[0]);
                    D.setText("D: " + _D[1].substring(0, _D[1].length()-1));
                    radio.setVisibility(VISIBLE);
                    A.setVisibility(VISIBLE);
                    B.setVisibility(VISIBLE);
                    C.setVisibility(VISIBLE);
                    D.setVisibility(VISIBLE);
                    inputView.setVisibility(INVISIBLE);
                } catch (Exception e) {
                }
                try {
                    String[] _A = split[5].split("A");
                    RadioButton A = mView.findViewById(R.id.choice_A_4);
                    RadioButton B = mView.findViewById(R.id.choice_B_4);
                    RadioButton C = mView.findViewById(R.id.choice_C_4);
                    RadioButton D = mView.findViewById(R.id.choice_D_4);
                    String[] _B = _A[1].split("B");
                    A.setText("A: " + _B[0]);
                    String[] _C = _B[1].split("C");
                    B.setText("B: " + _C[0]);
                    String[] _D = _C[1].split("D");
                    C.setText("C: " + _D[0]);
                    D.setText("D: " + _D[1].substring(0, _D[1].length()-1));
                    radio.setVisibility(VISIBLE);
                    A.setVisibility(VISIBLE);
                    B.setVisibility(VISIBLE);
                    C.setVisibility(VISIBLE);
                    D.setVisibility(VISIBLE);
                    inputView.setVisibility(INVISIBLE);
                } catch (Exception e) {
                }
            }else {
                String rawText = question.getText();
                String [] _A = rawText.split("A");
                questionView.setText(_A[0]);
                RadioButton A = mView.findViewById(R.id.choice_A_0);
                RadioButton B = mView.findViewById(R.id.choice_B_0);
                RadioButton C = mView.findViewById(R.id.choice_C_0);
                RadioButton D = mView.findViewById(R.id.choice_D_0);
                String [] _B = _A[1].split("B");
                A.setText("A: " + _B[0]);
                String [] _C = _B[1].split("C");
                B.setText("B: " + _C[0]);
                String [] _D = _C[1].split("D");
                C.setText("C: " + _D[0]);
                D.setText("D: " + _D[1]);
                radio.setVisibility(VISIBLE);
                A.setVisibility(VISIBLE);
                B.setVisibility(VISIBLE);
                C.setVisibility(VISIBLE);
                D.setVisibility(VISIBLE);
                inputView.setVisibility(INVISIBLE);
            }
        } else {
            questionView.setText(question.getText());
        }
        answerView.setText(defaultText);

        //[ADD TO BACKEND]
        // post请求把浏览记录发给后端
        try_sending(0);



        return mView;
    }
    public void try_sending(int cnt) {
        String url = AppSingleton.URL_PREFIX +  "/api/add_history";
        System.err.println(url);
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        if(cnt <= 5) {
            OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("type","question")
                    .add("id",id)
                    .add("course",AppSingleton.nowCourse)
                    .add("context", question.getText())
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            okhttp3.Call call = client.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.err.println("Failed");
                    try_sending(cnt + 1);
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    System.err.println(response.toString());
                    if(response.isSuccessful()){
                        String result = response.body().string();
                        System.err.println(result);

                    }
                }
            });
        }
    }
    public void send_try(boolean is_true) {
        String url = AppSingleton.URL_PREFIX + "/api/add_question";
        System.err.println(url);
        SharedPreferences preferences = getActivity().getSharedPreferences ("userInfo",
                getActivity().MODE_PRIVATE);
        // change in order to prevent from Exception
        String id = preferences.getString("id", "");
        System.err.println(id);
        System.err.println(question.getName());
        System.err.println(AppSingleton.nowCourse);
        // build request
        OkHttpClient client = new OkHttpClient();
        String is_correct = "no";
        if(is_true == true) {
            is_correct = "yes";
        }
        FormBody body = new FormBody.Builder()
                .add("id",id)
                .add("entity", question.getName())
                .add("course", AppSingleton.nowCourse)
                .add("correctness", is_correct)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Failed");
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                System.err.println(response.toString());
                if(response.isSuccessful()){
                    System.err.println("succeed");
                }
            }
        });
    }
}