/*
 * Copyright (c) 2020. rogergcc
 */
/***
 * This CourseCard class is the courseCard dataclass.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.model;

public class CourseCard {

    private int Id;
    private int imageCourse;
    private String courseTitle;
    private String quantityCourses;
    private String urlCourse;

    public CourseCard(int id, int imageCourse, String courseTitle, String quantityCourses) {
        Id = id;
        this.imageCourse = imageCourse;
        this.courseTitle = courseTitle;
        this.quantityCourses = quantityCourses;
    }

    public CourseCard(int imageCourse, String courseTitle, String quantityCourses) {
        this.imageCourse = imageCourse;
        this.courseTitle = courseTitle;
        this.quantityCourses = quantityCourses;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getImageCourse() {
        return imageCourse;
    }

    public void setImageCourse(int imageCourse) {
        this.imageCourse = imageCourse;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getQuantityCourses() {
        return quantityCourses;
    }

    public void setQuantityCourses(String quantityCourses) {
        this.quantityCourses = quantityCourses;
    }

    public String getUrlCourse() {
        return urlCourse;
    }

    public void setUrlCourse(String urlCourse) {
        this.urlCourse = urlCourse;
    }

    @Override()
    public boolean equals(Object other) {
        // This is unavoidable, since equals() must accept an Object and not something more derived
        if (other instanceof CourseCard) {
            CourseCard courseCard = (CourseCard) other;
            return courseCard.getId()==(this.getId());
        }

        return false;
    }
}
