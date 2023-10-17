package com.appsnipp.education.util;

/***
 * This is a file which is intented to parse the url and send request
 * @author Shuning Zhang
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;

public class BodyParser {
    Map<String, String> params = new HashMap<String, String>();
    public BodyParser(){}
    public void add(String key, String val){
        if(key.startsWith("\"") || key.endsWith("\"")) key = key.substring(1, key.length() - 1);
        if(val.startsWith("\"") || val.endsWith("\"")) val = val.substring(1, val.length() - 1);
        params.put(key, val);
    }
    void put(String key, String val){
        if(key.startsWith("\"") || key.endsWith("\"")) key = key.substring(1, key.length() - 1);
        if(val.startsWith("\"") || val.endsWith("\"")) val = val.substring(1, val.length() - 1);
        params.put(key, val);
    }
    public String parse(){
        String suffix = "?";
        for(String k: params.keySet()){
            suffix += k;
            suffix += "=";
            suffix += params.get(k);
            suffix += "&";
        }
        suffix = suffix.substring(0, suffix.length() - 1);
        return suffix;
    }
}
