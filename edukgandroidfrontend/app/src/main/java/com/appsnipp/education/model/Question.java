/***
 * This question class is the class containing the data structure of the question struct.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.model;

import java.util.ArrayList;

// 问题类，包含主要变量有
// 问题名称
// 问题类别
// 问题属性
// 问题文本
// 问题答案
// 相关知识点
public class Question {

    private String name;
    private ArrayList<String> label;
    private String feature;
    private String text;
    private String answer;
    private ArrayList<Knowledge> knowledgeList;
    private long id;
    private long qId;

    public Question() {
        // Default init with null pointer
        this("",null,"","","",null);
    }
    public Question(String _name, ArrayList<String> _label, String _feature, String _text,
                    String _answer, ArrayList<Knowledge> _knowledgeList) {
        name = _name;
        label = _label;
        feature = _feature;
        text = _text;
        answer = _answer;
        knowledgeList = _knowledgeList;
        id = -1;
        qId = -1;
    }

    public Question(String _name, ArrayList<String> _label, String _feature, String _text,
                    String _answer, ArrayList<Knowledge> _knowledgeList, long _id, long _qid) {
        name = _name;
        label = _label;
        feature = _feature;
        text = _text;
        answer = _answer;
        knowledgeList = _knowledgeList;
        id = _id;
        qId = _qid;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLabel() {
        return label;
    }

    public String getFeature() {
        return feature;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }

    public ArrayList<Knowledge> getKnowledgeList() {
        return knowledgeList;
    }

    // naive version of getContent
    public String getContent() {
        return getName() + getFeature() + getText() + getAnswer() + getLabel().get(0);
    }

    public long getId() {
        return id;
    }

    public long getqId() {
        return qId;
    }
}
