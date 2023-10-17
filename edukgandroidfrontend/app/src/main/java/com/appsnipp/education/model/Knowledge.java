/***
 * This knowledge class is the class including the data structure of knowledge information.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.model;

import java.util.ArrayList;

// 知识类，知识类里面包含的信息
// 1.知识点名称
// 2.知识点类型
// 3.知识点属性
// 4.知识点标签
// 5.知识点关系图
public class Knowledge {
    private String name;
    private String belonging;
    private String feature;
    private ArrayList<String> label;
    private ArrayList<String> graph;
    public Knowledge(String _name, String _belonging, String _feature, ArrayList<String> _label, ArrayList<String> _graph) {
        name  = _name;
        belonging = _belonging;
        feature = _feature;
        label = _label;
        graph = _graph;
    }
    public Knowledge() {
        // Default Null settings
        this("","","",null,null);
    }

    public String getName() {
        return name;
    }

    public String getBelonging() {
        return belonging;
    }

    public String getFeature() {
        return feature;
    }

    public ArrayList<String> getLabel() {
        return label;
    }

    public ArrayList<String> getGraph() {
        return graph;
    }

}
