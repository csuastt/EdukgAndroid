package com.appsnipp.education.util;

/***
 * This is a file which transfer English courses to Chinese
 * @author Shuning Zhang
 * @version 1.0
 */

public class TransChinese2English {
    public TransChinese2English(){}
    public String contain(String course) {
        if(course.contains("数学")) {
            return "maths";
        } else if(course.contains("语文")) {
            return "chinese";
        } else if(course.contains("英语")) {
            return "english";
        } else if(course.contains("物理")) {
            return "physics";
        } else if(course.contains("化学")) {
            return "chemistry";
        } else if(course.contains("生物")) {
            return "biology";
        } else if(course.contains("地理")) {
            return "geography";
        } else if(course.contains("历史")) {
            return "history";
        } else if(course.contains("政治")) {
            return "politics";
        } else {
            return "";
        }
    }
    public String trans(String chinese){
        if(chinese.equals("数学")) {
            return "maths";
        } else if(chinese.equals("语文")) {
            return "chinese";
        } else if(chinese.equals("英语")) {
            return "english";
        } else if(chinese.equals("物理")) {
            return "physics";
        } else if(chinese.equals("化学")) {
            return "chemistry";
        } else if(chinese.equals("生物")) {
            return "biology";
        } else if(chinese.equals("地理")) {
            return "geography";
        } else if(chinese.equals("历史")) {
            return "history";
        } else if(chinese.equals("政治")) {
            return "politics";
        } else {
            return "";
        }
    }

}
