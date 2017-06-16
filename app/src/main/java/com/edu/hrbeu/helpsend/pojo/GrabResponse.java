package com.edu.hrbeu.helpsend.pojo;

import com.edu.hrbeu.helpsend.bean.GrabOrder;

import java.util.ArrayList;

public class GrabResponse {
    private String status;
    private ArrayList<GrabOrder> message;
    private String exp;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GrabOrder> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<GrabOrder> message) {
        this.message = message;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
}
