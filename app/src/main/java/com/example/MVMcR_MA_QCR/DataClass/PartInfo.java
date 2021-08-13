package com.example.MVMcR_MA_QCR.DataClass;

public class PartInfo {
    int partId;
    String partname;
    String answer;
    String concern;
    byte[] nokImage=new byte[]{1};

    public PartInfo(String partname, String answer) {
        this.partname = partname;
        this.answer = answer;
    }
    public PartInfo(int partId,String partname, String answer) {
        this.partname = partname;
        this.answer = answer;
        this.partId=partId;
    }

    @Override
    public String toString() {
        return "PartInfo{" +
                "partId=" + partId +
                ", partname='" + partname + '\'' +
                ", answer='" + answer + '\'' +
                ", concern='" + concern + '\'' +
                '}';
    }

    public byte[] getNokImage() {
        return nokImage;
    }

    public void setNokImage(byte[] nokImage) {
        this.nokImage = nokImage;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public String getConcern() {
        return concern;
    }

    public void setConcern(String concern) {
        this.concern = concern;
    }

    public String getPartname() {
        return partname;
    }

    public void setPartname(String partname) {
        this.partname = partname;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}