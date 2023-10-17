package edukg;

import com.csvreader.CsvReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

// 推荐的主要类
public class Recommendation {
    // 存储知识图谱
    static JSONObject[] graph = new JSONObject[9];
    // 代表标签的 tag
    static String labelString = "http://www.w3.org/2000/01/rdf-schema#label";
    // 课程
    static String[] courses = {"chinese", "math", "english", "physics", "chemistry", "biology", "politics", "history", "geo"};
    // 最大距离
    static int steps = 3;
    // 最多推荐实体数
    static int maxEntityNum = 15;
    // 用户 id
    static String id;

    // 初始化，读入 csv 文件
    static void init() {
        for (int i = 0; i < 9; i++) {
            graph[i] = new JSONObject();
            readCSV(courses[i] + ".csv", i);
        }
    }

    // 获取 edukg_id
    static String getID() {
        try {
            // 连接 eudkg 后端并登录获取
            PostMethod postMethod = new PostMethod("http://open.edukg.cn/opedukg/api/typeAuth/user/login");
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            NameValuePair[] data = {
                    new NameValuePair("phone", "18122250317"),
                    new NameValuePair("password", "18122250317abc")
            };
            postMethod.setRequestBody(data);
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(postMethod);
            return JSONObject.fromObject(postMethod.getResponseBodyAsString()).getString("id");
        } catch (Exception e) {
            return "-1";
        }
    }

    // 尝试从 edukg 获取实体
    static boolean getEntity(String entity, int idx) {
        if (id.equals("-1")) {
            return true;
        }
        try {
            String param = "?id=" + id + "&name=" + URLEncoder.encode(entity, StandardCharsets.UTF_8) + "&course=" + courses[idx];
            GetMethod getMethod = new GetMethod("http://open.edukg.cn/opedukg/api/typeOpen/open/infoByInstanceName" + param);
            getMethod.setRequestHeader("Content-Type", "application/json;charset=utf-8");
            HttpClient httpClient = new HttpClient();
            httpClient.executeMethod(getMethod);
            JSONObject res = (JSONObject)JSONObject.fromObject(getMethod.getResponseBodyAsString()).get("data");
            JSONArray property = (JSONArray)res.get("property");
            return property.size() != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 寻找相似实体进行推荐
    static JSONArray find(ArrayList<JSONObject> list) {
        JSONArray ans = new JSONArray();
        id = getID();
        // 根据课程推荐，如果没有则所有课程均搜索一遍
        for (JSONObject listItem : list) {
            String entity = listItem.getString("entity");
            String course = listItem.getString("course");
            int flag = 0;
            for (int i = 0; i < 9; i++) {
                if (course.equals(courses[i])) {
                    bfs(entity, i, ans);
                    flag = 1;
                }
            }
            if (ans.size() >= maxEntityNum) {
                break;
            }
            if (flag == 0) {
                for (int i = 0; i < 9; i++) {
                    bfs(entity, i, ans);
                }
            }
            if (ans.size() >= maxEntityNum) {
                break;
            }
        }
        // 默认推荐“李白”相关内容
        if (ans.size() == 0) {
            bfs("李白", 0, ans);
        }
        return ans;
    }

    // Dijkstra 算法推荐
    static void bfs(String src, int idx, JSONArray ans) {
        HashMap<String, Integer> vis = new HashMap<>();
        ArrayDeque<String> que = new ArrayDeque<>();
        que.push(src);
        vis.put(src, 0);
        while (!que.isEmpty()) {
            String fr = que.pop();
            if (graph[idx].get(fr) != null) {
                for (Object item : ((JSONObject)graph[idx].get(fr)).keySet()) {
                    if (vis.get((String)item) == null && vis.get(fr) + 1 <= steps) {
                        vis.put((String)item, vis.get(fr) + 1);
                        que.push((String)item);
                        if (((JSONObject)graph[idx].get(fr)).get(item).equals(labelString + "+")) {
                            JSONObject obj = new JSONObject();
                            obj.put("entity", item);
                            obj.put("course", courses[idx]);
                            // 只有未超最大数额且可以再 eudkg 后端访问到的实体才可能被推荐
                            if (ans.size() < maxEntityNum && getEntity((String)item, idx)) {
                                ans.add(obj);
                            }
                        }
                    }
                }
            }
        }
    }

    // 读取知识图谱 csv 文件
    public static void readCSV(String filePath, int idx) {
        try {
            CsvReader reader = new CsvReader(filePath, ',', StandardCharsets.UTF_8);
            reader.setSafetySwitch(false);
            reader.readHeaders();
            while (reader.readRecord()) {
                String[] values = reader.getValues();
                if (graph[idx].get(values[0]) == null) {
                    graph[idx].put(values[0], new JSONObject());
                }
                if (graph[idx].get(values[2]) == null) {
                    graph[idx].put(values[2], new JSONObject());
                }
                ((JSONObject)graph[idx].get(values[0])).put(values[2], values[1] + "+");
                ((JSONObject)graph[idx].get(values[2])).put(values[0], values[1] + "-");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
