package com.appsnipp.education.widget;

/***
 * This is the program counter which records the total counting number
 * @author Shuning Zhang
 * @version 1.0
 */

public class Counter {
    private int counter = 0;
    public Counter(){counter = 0;}
    public void returnZero() {counter = 0;}
    public void increase() {counter += 1;}
    public int get() { return counter; }
}
