package com.edu.hrbeu.helpsend.bean;


public class GrabOrderDetail {
    private String remark;
    private String orderOwnerAvatarPath;
    private String orderOwnerGender;
    private String orderId;
    private Order.Location startLocationPojo;
    private Order.Location endLocationPojo;
    private String goodsWeight;
    private String goodsCategory;
    private String sendTime;
    private String receiveTime;
    private String orderOwnerId;
    private String imagePath;
    private String orderOwnerNickName;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrderOwnerAvatarPath() {
        return orderOwnerAvatarPath;
    }

    public void setOrderOwnerAvatarPath(String orderOwnerAvatarPath) {
        this.orderOwnerAvatarPath = orderOwnerAvatarPath;
    }

    public String getOrderOwnerGender() {
        return orderOwnerGender;
    }

    public void setOrderOwnerGender(String orderOwnerGender) {
        this.orderOwnerGender = orderOwnerGender;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Order.Location getStartLocationPojo() {
        return startLocationPojo;
    }

    public void setStartLocationPojo(Order.Location startLocationPojo) {
        this.startLocationPojo = startLocationPojo;
    }

    public Order.Location getEndLocationPojo() {
        return endLocationPojo;
    }

    public void setEndLocationPojo(Order.Location endLocationPojo) {
        this.endLocationPojo = endLocationPojo;
    }

    public String getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(String goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getOrderOwnerId() {
        return orderOwnerId;
    }

    public void setOrderOwnerId(String orderOwnerId) {
        this.orderOwnerId = orderOwnerId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOrderOwnerNickName() {
        return orderOwnerNickName;
    }

    public void setOrderOwnerNickName(String orderOwnerNickName) {
        this.orderOwnerNickName = orderOwnerNickName;
    }
}
