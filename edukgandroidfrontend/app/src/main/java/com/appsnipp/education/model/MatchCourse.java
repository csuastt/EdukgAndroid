/*
 * Copyright (c) 2020. rogergcc
 */
/***
 * This matchCourse class is the class containing some courseCard definition of the matched course.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.model;


public class MatchCourse {

    private final int id;
    private final String name;
    private final String numberOfCourses;
    private final int imageResource;

    public MatchCourse(int id, String name, String numberOfCourses, int imageResource) {
        this.id = id;
        this.name = name;
        this.numberOfCourses = numberOfCourses;
        this.imageResource = imageResource;
    }

    @Override
    public String toString() {
        return "MatchCourse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numberOfCourses='" + numberOfCourses + '\'' +
                ", imageResource=" + imageResource +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumberOfCourses() {
        return numberOfCourses;
    }

    public int getImageResource() {
        return imageResource;
    }
}
