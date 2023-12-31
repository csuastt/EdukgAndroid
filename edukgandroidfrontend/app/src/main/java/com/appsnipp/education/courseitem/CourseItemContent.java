/***
 * This Activity is used for the helper class of course item.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.courseitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CourseItemContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<CourseItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, CourseItem> ITEM_MAP = new HashMap<String, CourseItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(CourseItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static void reInit() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static CourseItem createDummyItem(int position) {
        return new CourseItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class CourseItem {
        public final String id;
        public final String content;
        public final String details;

        public CourseItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        public String getContent() {
            return content;
        }

        public String getId() {
            return id;
        }

        public String getDetails() {
            return details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}