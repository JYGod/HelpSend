package com.edu.hrbeu.helpsend.bean;


import java.util.ArrayList;

public class OrderResponse {
    private String status;
    private ArrayList<Order>message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Order> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<Order> message) {
        this.message = message;
    }
}
