package com.edu.hrbeu.helpsend.bean;



public class Order {

    private String orderId;
    private String orderOwnerId;
    private String orderReceiverId;
    private Location startLocation;
    private Location endLocation;
    private String sender;
    private String receiver;
    private String senderTel;
    private String receiverTel;
    private String goodsName;
    private String putOrderTime;
    private String sendTime;//界面上选择的那个时间
    private String receiveTime;
    private String receiveOrderTime;
    private String deliveryTime;
    private String remark;
    private String imagePath;
    private String orderStatus;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderOwnerId() {
        return orderOwnerId;
    }

    public void setOrderOwnerId(String orderOwnerId) {
        this.orderOwnerId = orderOwnerId;
    }


    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSenderTel() {
        return senderTel;
    }

    public void setSenderTel(String senderTel) {
        this.senderTel = senderTel;
    }

    public String getReceiverTel() {
        return receiverTel;
    }

    public void setReceiverTel(String receiverTel) {
        this.receiverTel = receiverTel;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPutOrderTime() {
        return putOrderTime;
    }

    public void setPutOrderTime(String putOrderTime) {
        this.putOrderTime = putOrderTime;
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

    public String getReceiveOrderTime() {
        return receiveOrderTime;
    }

    public void setReceiveOrderTime(String receiveOrderTime) {
        this.receiveOrderTime = receiveOrderTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrderReceiverId() {
        return orderReceiverId;
    }

    public void setOrderReceiverId(String orderReceiverId) {
        this.orderReceiverId = orderReceiverId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }


    public static class  Location{
        private String description;
        private String latitude;
        private String longitude;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
