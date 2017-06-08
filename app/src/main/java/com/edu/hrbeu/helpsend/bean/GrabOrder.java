package com.edu.hrbeu.helpsend.bean;



public class GrabOrder {
    private String orderId;
    private Location startLocationPojo;
    private Location endLocationPojo;
    private String goodsWeight;
    private String goodsCategory;
    private String sendTime;
    private String receiveTime;
    private String distance;
    private String orderPrice;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Location getStartLocationPojo() {
        return startLocationPojo;
    }

    public void setStartLocationPojo(Location startLocationPojo) {
        this.startLocationPojo = startLocationPojo;
    }

    public Location getEndLocationPojo() {
        return endLocationPojo;
    }

    public void setEndLocationPojo(Location endLocationPojo) {
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
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
