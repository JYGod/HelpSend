package com.edu.hrbeu.helpsend.bean;

import java.util.ArrayList;

public class GrabResponse {
    private String status;
    private ArrayList<GrabOrder> message;

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
}
