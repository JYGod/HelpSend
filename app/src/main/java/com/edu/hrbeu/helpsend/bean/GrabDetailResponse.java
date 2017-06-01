package com.edu.hrbeu.helpsend.bean;


import java.util.ArrayList;

public class GrabDetailResponse {
    private String status;
    private GrabOrderDetail message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public GrabOrderDetail getMessage() {
        return message;
    }

    public void setMessage(GrabOrderDetail message) {
        this.message = message;
    }
}
