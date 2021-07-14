package com.example.qctestingapp;

import java.sql.Timestamp;
import java.util.Date;

public class Questions_main {
    int id;
    String question;
    String answer;
    String highlight="NOHIGHLIGHT";
    String qr_code;
    String remark="no remark";
    public static final String OK="OK";
    public static final String NOT_OK="NOT OK";
    static int qNo=1;

    public Questions_main(int id,String question, String highlight) {
        this.id=id;
        this.question=question;
        this.highlight=highlight;
      //  this.id=qNo;
        qNo++;
    }

    public Questions_main(int id, String question, String answer, String highlight, String qr_code) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.highlight = highlight;
        this.qr_code = qr_code;
    }

    @Override
    public String toString() {
        return "Questions_main{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", isHighlighted=" + highlight +
                ", qr_code='" + qr_code + '\'' +
                '}';
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String isHighlighted() {
        return highlight;
    }

    public void setHighlighted(String highlighted) {
        highlight = highlighted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
