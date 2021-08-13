package com.example.MVMcR_MA_QCR.DataClass;

public class ProductionReport {
    int ok;
    int nok;
    int total;
    String model="na";
    public ProductionReport(int ok, int nok, int total, String model) {
        this.ok = ok;
        this.nok = nok;
        this.total = total;
        this.model = model;
    }
    public ProductionReport() {

    }
    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public int getNok() {
        return nok;
    }

    public void setNok(int nok) {
        this.nok = nok;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
