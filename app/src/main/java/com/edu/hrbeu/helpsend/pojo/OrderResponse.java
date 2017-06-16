package com.edu.hrbeu.helpsend.pojo;


import com.edu.hrbeu.helpsend.bean.Order;

import java.util.ArrayList;

public class OrderResponse {
    private String status;
    private ArrayList<Order>message;
    private String exp;

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

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
}
