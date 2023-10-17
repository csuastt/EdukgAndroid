package com.appsnipp.education.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CourseCardTest {
    private CourseCard myCourseCard;

    @Before
    public void setUp() throws Exception {
        CourseCard myCourseCard = new CourseCard(1, 1, "testCourseTitle","testQuantityCourses");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getId() {
        assert(myCourseCard.getId() == 1);
    }

    @Test
    public void getImageCourse() {
        assert(myCourseCard.getImageCourse() == 1);
    }

    @Test
    public void getCourseTitle() {
        assert(myCourseCard.getCourseTitle() == "testCourseTitle");
    }

    @Test
    public void getQuantityCourses() {
        assert(myCourseCard.getQuantityCourses() == "testQuantityCourses");
    }

    @Test
    public void getUrlCourse() {
    }

    @Test
    public void testEquals() {
    }
}