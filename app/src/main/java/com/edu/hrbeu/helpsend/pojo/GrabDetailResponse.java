package com.edu.hrbeu.helpsend.pojo;


import com.edu.hrbeu.helpsend.bean.GrabOrderDetail;


public class GrabDetailResponse {
    private String status;
    private GrabOrderDetail message;
    private String exp;

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

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
}
