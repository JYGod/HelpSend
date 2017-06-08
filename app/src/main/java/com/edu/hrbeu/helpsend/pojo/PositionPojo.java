package com.edu.hrbeu.helpsend.pojo;


import com.edu.hrbeu.helpsend.bean.GrabOrder;
import com.edu.hrbeu.helpsend.bean.Order;

public class PositionPojo {

    private Order.Location start;
    private Order.Location end;

    public PositionPojo(Order.Location start, Order.Location end) {
        this.start = start;
        this.end = end;
    }


    public Order.Location getStart() {
        return start;
    }

    public void setStart(Order.Location start) {
        this.start = start;
    }

    public Order.Location getEnd() {
        return end;
    }

    public void setEnd(Order.Location end) {
        this.end = end;
    }
}
