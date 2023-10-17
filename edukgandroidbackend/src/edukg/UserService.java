package edukg;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import jakarta.servlet.http.*;
import net.sf.json.*;
import org.apache.commons.lang.math.NumberUtils;

// 主要后端类，继承自 HttpServlet
public class UserService extends HttpServlet {
    // info 存储用户信息， nameMap 保存用户名到用户 id 的映射
    JSONObject info = new JSONObject(), nameMap = new JSONObject();
    // info 中每一项包含的信息
    final String[] PARAM_SET = {"id", "name", "password", "old_password", "new_password",
            "nickname", "gender", "description", "type", "course", "context", "time", "date", "entity", "correctness"};

    @Override
    public void init() {
        // 推荐类初始化
        Recommendation.init();

        // 读取信息
        try {
            // 打开文件
            info.clear();  nameMap.clear();
            File file_info = new File("info.dat");
            File file_map = new File("map.dat");
            // 如果文件存在，则读取
            if (file_info.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file_info), StandardCharsets.UTF_8));
                info = JSONObject.fromObject(reader.readLine());
                reader.close();
            }
            if (file_map.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file_map), StandardCharsets.UTF_8));
                nameMap = JSONObject.fromObject(reader.readLine());
                reader.close();
            }
            // 否则，新建空的 json 对象
            if (info == null || nameMap == null) {
                info = new JSONObject();
                nameMap = new JSONObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        // 将更改写入文件
        try {
            File file_info = new File("info.dat");
            File file_map = new File("map.dat");
            BufferedWriter writer_info = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_info), StandardCharsets.UTF_8));
            BufferedWriter writer_map = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file_map), StandardCharsets.UTF_8));
            writer_info.write(info.toString());
            writer_info.close();
            writer_map.write(nameMap.toString());
            writer_map.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/json");
        //除 /edukg_id 外，其余路径均不合法
        if (request.getPathInfo() == null || !request.getPathInfo().equals("/edukg_id")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "请求的页面不合法！");
            return;
        }

        // 为避免多线程访问出错，此处需同步
        synchronized (this) {
            String res = Recommendation.getID();
            // 网络连接失败
            if (res.equals("-1")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "网络连接失败！");
            // 网络连接成功，返回 edukg_id
            } else {
                JSONObject obj = new JSONObject();
                obj.put("edukg_id", res);
                response.getWriter().println(obj);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/json");
        // 空路径不合法
        if (request.getPathInfo() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "请求的页面不合法！");
            return;
        }

        // 解析参数
        JSONObject param = new JSONObject();
        for (String paramStr : PARAM_SET) {
            param.put(paramStr, request.getParameter(paramStr));
        }
        if (request.getPathInfo().equals("/del_history") || request.getPathInfo().equals("/del_favorites")) {
            param.put("appendix_id", request.getParameter(request.getPathInfo().substring(5) + "_id"));
        }

        // 为避免多线程访问出错，此处需同步
        synchronized (this) {
            // 根据请求路径选择
            switch (request.getPathInfo()) {
                // 注册
                case "/register":
                    if (param.get("name") == null || param.get("password") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    } else if (nameMap.get(param.get("name")) != null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "用户已注册！");
                        return;
                    }
                    response.getWriter().println(register(param.getString("name"), param.getString("password")));
                    break;
                // 登录
                case "/login":
                    if (param.get("name") == null || param.get("password") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    } else {
                        String id = (String)nameMap.get(param.get("name"));
                        if (id == null || !((JSONObject)info.get(id)).get("password").equals(param.get("password"))) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "用户名或密码错误！");
                            return;
                        }
                    }
                    response.getWriter().println(login(param.getString("name")));
                    break;
                // 用户信息修改
                case "/info_edit":
                    if (param.get("id") == null || info.get(param.get("id")) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    } else if (param.get("name") != null && nameMap.get(param.get("name")) != null
                            && !nameMap.get(param.get("name")).equals(param.get("id"))) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "该用户名已被使用！");
                        return;
                    }
                    response.getWriter().println(infoEdit((String)param.get("id"), (String)param.get("name"),
                            (String)param.get("nickname"), (String)param.get("gender"), (String)param.get("description")));
                    break;
                // 密码修改
                case "/password_edit":
                    if (param.get("id") == null || info.get(param.get("id")) == null ||
                            param.get("old_password") == null || param.get("new_password") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    } else if (!((JSONObject)info.get(param.get("id"))).get("password").equals(param.get("old_password"))) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "旧密码输入错误！");
                        return;
                    }
                    response.getWriter().println(passwordEdit((String)param.get("id"), (String)param.get("new_password")));
                    break;
                // 获取历史或收藏
                case "/history":
                case "/favorites":
                    if (param.get("id") == null || info.get(param.get("id")) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(getAppendix(param.getString("id"), request.getPathInfo().substring(1)));
                    break;
                // 新增历史或收藏
                case "/add_history":
                case "/add_favorites":
                    if (param.get("id") == null || info.get(param.get("id")) == null || param.get("context") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(addAppendix(param.getString("id"), request.getPathInfo().substring(5),
                            (String)param.get("type"), (String)param.get("course"), (String)param.get("context")));
                    break;
                // 删除历史或收藏
                case "/del_history":
                case "/del_favorites":
                    if (param.get("id") == null || info.get(param.get("id")) == null || param.get("appendix_id") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(delAppendix(param.getString("id"), request.getPathInfo().substring(5),
                            param.getString("appendix_id")));
                    break;
                // 获取使用时间
                case "/time":
                    if (param.get("id") == null || info.get(param.get("id")) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(getTime(param.getString("id")));
                    break;
                // 新增使用时间
                case "/add_time":
                    if (param.get("id") == null || info.get(param.get("id")) == null
                            || param.get("time") == null || param.get("date") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    } else if (!NumberUtils.isDigits(param.getString("time"))) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(addTime(param.getString("id"), Integer.parseInt((String)param.get("time")), param.getString("date")));
                    break;
                // 新增问题作答记录
                case "/add_question":
                    if (param.get("id") == null || info.get(param.get("id")) == null
                            || param.get("entity") == null || param.get("correctness") == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    } else if (!param.get("correctness").equals("yes") && !param.get("correctness").equals("no")) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "\"correctness\"字段只能为\"yes\"或\"no\"！");
                        return;
                    }
                    response.getWriter().println(addQuestion((String)param.get("id"), (String)param.get("entity"),
                            (String)param.get("course"), (String)param.get("correctness")));
                    break;
                // 获取知识点评估或课程评估
                case "/score":
                case "/course_score":
                    if (param.get("id") == null || info.get(param.get("id")) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    if (request.getPathInfo().equals("/score")) {
                        response.getWriter().println(getScore(param.getString("id")));
                    } else {
                        response.getWriter().println(getCourseScore(param.getString("id")));
                    }
                    break;
                // 获取学习路径推荐
                case "/recommendation":
                    if (param.get("id") == null || info.get(param.get("id")) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(getRecommendation(param.getString("id")));
                    break;
                // 路径不合法
                default:
                    if (param.get("id") == null || info.get(param.get("id")) == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数错误！");
                        return;
                    }
                    response.getWriter().println(getRecommendation(param.getString("id")));
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "请求的页面不合法！");
                    break;
            }
        }
    }

    // 注册
    private JSONObject register(String name, String password) {
        // 初始化个参数，只有 id 、 name 、 password 有初始值
        String id = String.valueOf(System.currentTimeMillis());
        JSONObject personInfo = new JSONObject();
        personInfo.put("id", id);
        personInfo.put("name", name);
        personInfo.put("password", password);
        personInfo.put("nickname", "");
        personInfo.put("gender", "");
        personInfo.put("description", "");
        personInfo.put("appendix", new JSONObject());
        personInfo.put("statistics", new JSONObject());
        personInfo.put("course", new JSONObject());
        personInfo.put("time", new JSONObject());
        info.put(id, personInfo);
        nameMap.put(name, id);

        JSONObject res = new JSONObject();
        res.put("code", "Operation Succeed.");
        return res;
    }

    // 登录
    private JSONObject login(String name) {
        // 已确认密码正确，再 nameMap 中寻找对应用户 id
        JSONObject res = new JSONObject();
        String id = nameMap.getString(name);
        // 返回 info 中各项的值
        res.put("id", ((JSONObject)info.get(id)).get("id"));
        res.put("name", ((JSONObject)info.get(id)).get("name"));
        res.put("nickname", ((JSONObject)info.get(id)).get("nickname"));
        res.put("gender", ((JSONObject)info.get(id)).get("gender"));
        res.put("description", ((JSONObject)info.get(id)).get("description"));
        return res;
    }

    // 用户信息修改
    private JSONObject infoEdit(String id, String name, String nickname, String gender, String description) {
        // 编辑名字
        if (name != null) {
            nameMap.remove(((JSONObject)info.get(id)).get("name"));
            ((JSONObject)info.get(id)).put("name", name);
            nameMap.put(name, id);
        }
        // 编辑昵称
        if (nickname != null) {
            ((JSONObject)info.get(id)).put("nickname", nickname);
        }
        // 编辑性别
        if (gender != null) {
            ((JSONObject)info.get(id)).put("gender", gender);
        }
        // 编辑个性签名
        if (description != null) {
            ((JSONObject)info.get(id)).put("description", description);
        }
        return login((String)((JSONObject)info.get(id)).get("name"));
    }

    // 密码修改
    private JSONObject passwordEdit(String id, String password) {
        ((JSONObject)info.get(id)).put("password", password);
        JSONObject res = new JSONObject();
        res.put("code", "Operation Succeed.");
        return res;
    }
    
    // 获取历史或收藏
    private JSONArray getAppendix(String id, String appendixType) {
        JSONArray res = new JSONArray();
        JSONObject appendixes = (JSONObject)((JSONObject)info.get(id)).get("appendix");
        for (Object appendix : appendixes.values()) {
            // 根据请求为历史或收藏分别获取
            if (((JSONObject)appendix).get("appendix_type").equals(appendixType)) {
                JSONObject obj = new JSONObject();
                obj.put(appendixType + "_id", ((JSONObject)appendix).get("id"));
                if (appendixType.equals("history")) {
                    obj.put("type", ((JSONObject)appendix).get("type"));
                }
                obj.put("course", ((JSONObject)appendix).get("course"));
                obj.put("context", ((JSONObject)appendix).get("context"));
                res.add(obj);
            }
        }
        return res;
    }

    // 新增历史或收藏
    private JSONObject addAppendix(String id, String appendixType, String type, String course, String context) {
        if (type == null) {
            type = "";
        }
        if (course == null) {
            course = "";
        }
        String appendixID = String.valueOf(System.currentTimeMillis());
        JSONObject appendix = new JSONObject();
        appendix.put("id", appendixID);
        appendix.put("appendix_type", appendixType);
        // 添加历史
        if (appendixType.equals("history")) {
            appendix.put("type", type);
            // 如果为实体访问记录，需要更新对知识点和课程的数据统计
            if (type.equals("entity")) {
                // 更新知识点数据统计
                if (((JSONObject)((JSONObject)info.get(id)).get("statistics")).get(context) == null) {
                    JSONObject obj = new JSONObject();
                    obj.put("entity", context);
                    obj.put("course", course);
                    obj.put("browse", 1);
                    obj.put("correctness", 0);
                    ((JSONObject)((JSONObject)info.get(id)).get("statistics")).put(context, obj);
                    appendix.put("course", course);
                    appendix.put("context", context);
                    ((JSONObject)((JSONObject)info.get(id)).get("appendix")).put(appendixID, appendix);
                } else {
                    Integer browse = (Integer)((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("statistics")).get(context)).get("browse");
                    ((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("statistics")).get(context)).put("browse", browse + 1);
                }
                // 更新课程数据统计
                if (!course.equals("")) {
                    if (((JSONObject)((JSONObject)info.get(id)).get("course")).get(course) == null) {
                        JSONObject obj = new JSONObject();
                        obj.put("course", course);
                        obj.put("browse", 1);
                        obj.put("correctness", 0);
                        ((JSONObject)((JSONObject)info.get(id)).get("course")).put(course, obj);
                    } else {
                        Integer browse = (Integer)((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("course")).get(course)).get("browse");
                        ((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("course")).get(course)).put("browse", browse + 1);
                    }
                }
            } else {
                appendix.put("course", course);
                appendix.put("context", context);
                ((JSONObject)((JSONObject)info.get(id)).get("appendix")).put(appendixID, appendix);
            }
        // 添加收藏
        } else {
            appendix.put("course", course);
            appendix.put("context", context);
            ((JSONObject)((JSONObject)info.get(id)).get("appendix")).put(appendixID, appendix);
        }

        JSONObject res = new JSONObject();
        res.put("code", "Operation Succeed.");
        return res;
    }

    // 删除历史或收藏
    private JSONObject delAppendix(String id, String appendixType, String appendixID) {
        for (Object obj : ((JSONObject)((JSONObject)info.get(id)).get("appendix")).values()) {
            // 根据请求分别删除历史或收藏
            if (((JSONObject)obj).get("id").equals(appendixID)
                    && ((JSONObject)obj).get("appendix_type").equals(appendixType)) {
                ((JSONObject)((JSONObject)info.get(id)).get("appendix")).remove(((JSONObject)obj).get("id"));
                break;
            }
        }

        JSONObject res = new JSONObject();
        res.put("code", "Operation Succeed.");
        return res;
    }

    // 获取使用时长
    private JSONArray getTime(String id) {
        JSONArray res = new JSONArray();
        for (Object date : ((JSONObject)((JSONObject)info.get(id)).get("time")).keySet()) {
            JSONObject obj = new JSONObject();
            obj.put("date", date);
            obj.put("time", ((JSONObject)((JSONObject)info.get(id)).get("time")).get(date));
            res.add(obj);
        }
        return res;
    }

    // 新增使用时长
    private JSONObject addTime(String id, int time, String date) {
        if (((JSONObject)((JSONObject)info.get(id)).get("time")).get(date) == null) {
            ((JSONObject)((JSONObject)info.get(id)).get("time")).put(date, time);
        } else {
            int oldTime = (int)((JSONObject)((JSONObject)info.get(id)).get("time")).get(date);
            ((JSONObject)((JSONObject)info.get(id)).get("time")).put(date, oldTime + time);
        }

        JSONObject res = new JSONObject();
        res.put("code", "Operation Succeed.");
        return res;
    }

    // 新增问题作答记录
    private JSONObject addQuestion(String id, String entity, String course, String correctness) {
        if (course == null) {
            course = "";
        }
        // 更新知识点数据统计
        if (((JSONObject)((JSONObject)info.get(id)).get("statistics")).get(entity) == null) {
            JSONObject obj = new JSONObject();
            obj.put("entity", entity);
            obj.put("course", course);
            obj.put("browse", 0);
            if (correctness.equals("yes")) {
                obj.put("correctness", 1);
            } else {
                obj.put("correctness", -1);
            }
            ((JSONObject)((JSONObject)info.get(id)).get("statistics")).put(entity, obj);
        } else {
            Integer num = (Integer)((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("statistics")).get(entity)).get("correctness");
            num = correctness.equals("yes") ? num + 1 : num - 1;
            ((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("statistics")).get(entity)).put("correctness", num);
        }
        // 更新课程数据统计
        if (!course.equals("")) {
            if (((JSONObject)((JSONObject)info.get(id)).get("course")).get(course) == null) {
                JSONObject obj = new JSONObject();
                obj.put("course", course);
                obj.put("browse", 0);
                if (correctness.equals("yes")) {
                    obj.put("correctness", 1);
                } else {
                    obj.put("correctness", -1);
                }
                ((JSONObject)((JSONObject)info.get(id)).get("course")).put(course, obj);
            } else {
                Integer num = (Integer)((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("course")).get(course)).get("correctness");
                num = correctness.equals("yes") ? num + 1 : num - 1;
                ((JSONObject)((JSONObject)((JSONObject)info.get(id)).get("course")).get(course)).put("correctness", num);
            }
        }

        JSONObject res = new JSONObject();
        res.put("code", "Operation Succeed.");
        return res;
    }

    // 获取知识点分数评估
    private JSONArray getScore(String id) {
        JSONArray res = new JSONArray();
        JSONObject statistics = (JSONObject)((JSONObject)info.get(id)).get("statistics");
        for (Object item : statistics.values()) {
            JSONObject obj = new JSONObject();
            obj.put("entity", ((JSONObject)item).get("entity"));
            obj.put("course", ((JSONObject)item).get("course"));
            int score = scoreCalc((int)((JSONObject)item).get("browse"), (int)((JSONObject)item).get("correctness"));
            obj.put("score", score);
            res.add(obj);
        }
        return res;
    }

    // 获取课程分数评估
    private JSONArray getCourseScore(String id) {
        JSONArray res = new JSONArray();
        JSONObject course = (JSONObject)((JSONObject)info.get(id)).get("course");
        for (Object item : course.values()) {
            JSONObject obj = new JSONObject();
            obj.put("course", ((JSONObject)item).get("course"));
            int score = scoreCalc((int)((JSONObject)item).get("browse"), (int)((JSONObject)item).get("correctness"));
            obj.put("score", score);
            res.add(obj);
        }
        return res;
    }

    // 总分计算
    private int scoreCalc(int browse, int correctness) {
        return correctness * 3 - browse;
    }

    // 获取学习路径推荐
    private JSONArray getRecommendation(String id) {
        JSONArray arr = getScore(id);
        ArrayList<JSONObject> list = new ArrayList<>();
        for (Object item : arr) {
            list.add((JSONObject)item);
        }
        // 将分数进行排序
        list.sort((a, b) -> {
            if ((int)a.get("score") == (int)b.get("score")) {
                return 0;
            } else {
                return (int)a.get("score") < (int)b.get("score") ? 1 : -1;
            }
        });
        return Recommendation.find(list);
    }
}
