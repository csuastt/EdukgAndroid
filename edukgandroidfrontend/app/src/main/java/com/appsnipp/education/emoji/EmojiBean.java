/***
 * This class is an auxiliary class which is a bean class for emoji.
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.emoji;

/**
 * Describe: 表情的实体类
 */

public class EmojiBean {
    private int id;
    private int unicodeInt;

    public String getEmojiString() {
        return  getEmojiStringByUnicode(unicodeInt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnicodeInt() {
        return getEmojiStringByUnicode(unicodeInt);
    }

    public void setUnicodeInt(int unicodeInt) {
        this.unicodeInt = unicodeInt;
    }

    public static String getEmojiStringByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public String toString() {
        return "EmojiBean{" +
                "id=" + id +
                ", unicodeInt=" + unicodeInt +
                '}';
    }
}
