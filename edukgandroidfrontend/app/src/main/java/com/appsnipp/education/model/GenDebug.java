/***
 * This GenDebug class is the debug class used for Debug infomation generation.
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education.model;

import java.util.ArrayList;

public class GenDebug {

    ArrayList<Question> questionBase;
    ArrayList<Knowledge> knowledgeBase;
    public GenDebug(){
        //Init Tag Base
        ArrayList<String> mathematics = new ArrayList<String>();
        mathematics.add("数学");
        ArrayList<String> chinese = new ArrayList<String>();
        chinese.add("语文");
        ArrayList<String> english = new ArrayList<String>();
        english.add("英语");
        //Init knowledge Base: has not set knowledge graph TODO
        ArrayList<Knowledge> knowledgeBase = new ArrayList<Knowledge>();
        ArrayList<String> knowledgeGraph = new ArrayList<String>();
        knowledgeBase.add(new Knowledge("等差数列公式","数学知识点","数学公式", mathematics, knowledgeGraph));
        knowledgeBase.add(new Knowledge("倍角公式","数学知识点","数学公式", mathematics, knowledgeGraph));
        knowledgeBase.add(new Knowledge("李白生平","语文知识点","语文概念", chinese, knowledgeGraph));
        knowledgeBase.add(new Knowledge("杜甫代表作","语文知识点","语文概念", chinese, knowledgeGraph));
        knowledgeBase.add(new Knowledge("has been 用法","英语知识点","英语概念", english, knowledgeGraph));
        knowledgeBase.add(new Knowledge("过去分词变化形式","英语知识点","英语用法", english, knowledgeGraph));

        //Init question Base
        ArrayList<Knowledge> knowledgeMathematics = new ArrayList<Knowledge>();
        ArrayList<Knowledge> knowledgeChinese = new ArrayList<Knowledge>();
        ArrayList<Knowledge> knowledgeEnglish = new ArrayList<Knowledge>();
        for(Knowledge k : knowledgeBase) {
            if(k.getLabel().get(0).equals("数学")) {
                knowledgeMathematics.add(k);
            } else if (k.getLabel().get(0).equals("语文")) {
                knowledgeChinese.add(k);
            } else if (k.getLabel().get(0).equals("英语")) {
                knowledgeEnglish.add(k);
            }
        }
        questionBase = new ArrayList<Question>();
        Question _question1 = new Question("等差数列问题", mathematics, "数学-等差数列", "前n项和公式为：Sn=a1*(   )+[n*(n-1)*d]/2或Sn=[(    )*(a1+an)]/2", "前n项和公式为：Sn=a1*n+[n*(n-1)*d]/2或Sn=[n*(a1+an)]/2", knowledgeMathematics);
        Question _question2 = new Question("倍角公式问题", mathematics, "数学-倍角公式","倍角公式倍角公式倍角公式倍角公式倍角公式","倍角公式倍角公式倍角公式倍角公式",knowledgeMathematics);
        Question _question3 = new Question("李白生平问题", chinese, "语文-人物生平","李白李白李白李白李白李白李白李白","李白李白李白李白李白李白李白李白李白李白李白李白",knowledgeMathematics);
        Question _question4 = new Question("杜甫代表作问题", chinese, "语文-人物代表作","杜甫代表作杜甫代表作杜甫代表作杜甫代表作杜甫代表作","杜甫代表作杜甫代表作杜甫代表作杜甫代表作",knowledgeMathematics);
        Question _question5 = new Question("has been用法问题", english, "英语-时态概念","has been和has gone 的用法 have(has)been”和“have(has)gone”相似点:两句都有去某个地方的意思。不同点:“have(has)been”是________","“have(has)been”是曾经到过某地,现在人已经回到说话的地方。",knowledgeMathematics);
        Question _question6 = new Question("过去分词变化形式问题", english, "英语-词形用法","规则动词的过去式变化如下： 一般情况下,动词词尾加 -ed ,如： worked played wanted acted 以不发音的 -e 结尾动词,动词词尾加(    )","动词词尾加 -d",knowledgeMathematics);
        questionBase.add(_question1);
        questionBase.add(_question2);
        questionBase.add(_question3);
        questionBase.add(_question4);
        questionBase.add(_question5);
        questionBase.add(_question6);
    }

    public ArrayList<Question> getQuestionBase() {
        return questionBase;
    }

    public ArrayList<Knowledge> getKnowledgeBase() {
        return knowledgeBase;
    }
}
