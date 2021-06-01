package com.example.qctestingapp;

import java.sql.Timestamp;
import java.util.Date;

public class Questions_main {
    int id;
    String question;
    String answer;
    boolean isHighlighted=false;
    String qr_code;
    public static final String OK="OK";
    public static final String NOT_OK="NOT OK";
    static int qNo=1;

    public Questions_main(int id,String question, boolean isHighlighted) {
        this.id=id;
        this.question=question;
        this.isHighlighted=isHighlighted;
      //  this.id=qNo;
        qNo++;
    }

    public Questions_main(int id, String question, String answer, boolean isHighlighted, String qr_code) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.isHighlighted = isHighlighted;
        this.qr_code = qr_code;
    }

    @Override
    public String toString() {
        return "Questions_main{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", isHighlighted=" + isHighlighted +
                ", qr_code='" + qr_code + '\'' +
                '}';
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
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
