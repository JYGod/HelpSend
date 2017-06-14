package com.edu.hrbeu.helpsend;


import android.app.Application;
import android.content.Context;
import com.edu.hrbeu.helpsend.data.OrderFactory;
import com.edu.hrbeu.helpsend.seivice.OrderService;

public class MyApplication extends Application {

    private OrderService orderService;



    private static MyApplication get(Context context){
        return (MyApplication) context.getApplicationContext();
    }

    public static MyApplication create(Context context){
        return MyApplication.get(context);
    }


    public OrderService getOrderService(){
        if (orderService == null){
            orderService = OrderFactory.create();
        }
        return orderService;
    }

    public void setOrderService(OrderService orderService){
        this.orderService=orderService;
    }



}
