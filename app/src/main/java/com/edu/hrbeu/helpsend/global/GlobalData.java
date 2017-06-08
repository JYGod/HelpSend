package com.edu.hrbeu.helpsend.global;


import com.edu.hrbeu.helpsend.bean.Order;
import com.edu.hrbeu.helpsend.pojo.PositionPojo;

import java.io.File;

public class GlobalData {

    public static final String USER_ID="123";
    public static Order MY_ORDER=new Order();
    public static String MY_LOCATE;
    public static String END_LOCATE;
    public static String LOCATE_DIRECTION;
    public static String SENDER_PHONE;
    public static String RECEIVER_PHONE;
    public static String GOODS_DESCRIPION;
    public static String SEND_TIME;
    public static File ACCESSORY;
    public static final String ORDER_LISTENER="com.edu.hrbeu.helpsend.ORDER_LISTENER";
    public static File USER_AVATAR;
    public static final String QQ_APP_ID="1106089061";
    public static Order.Location mLocation=new Order.Location();

    public static final int TIM_SDK_APP_ID=1400031229;

    public static String ORDER_SELECT="put";

    public static String MY_REAL_NAME;
    public static String MY_IDENTITY_CARD;
    public static File MY_CARD_FRONT;
    public static File MY_CARD_BACK;
    public static File MY_SELFIE;

    public static PositionPojo POSITION_POINTS=new PositionPojo(new Order.Location(),new Order.Location());
}
