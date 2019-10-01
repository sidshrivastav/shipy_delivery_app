package com.pb.ensadelivery.models;

import com.google.gson.annotations.SerializedName;

public class OrderModel {
    @SerializedName("order_id")
    int orderId;
    @SerializedName("product_id")
    int productId;
    @SerializedName("product_title")
    String productTitle;
    @SerializedName("receiver_name")
    String customerName;
    @SerializedName("delivery_address")
    String deliveryAddress;
    @SerializedName("delivery_latitude")
    String deliveryLatitude;
    @SerializedName("delivery_longitude")
    String deliveryLongitute;


    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryLatitude() {
        return deliveryLatitude;
    }

    public void setDeliveryLatitude(String deliveryLatitude) {
        this.deliveryLatitude = deliveryLatitude;
    }

    public String getDeliveryLongitute() {
        return deliveryLongitute;
    }

    public void setDeliveryLongitute(String deliveryLongitute) {
        this.deliveryLongitute = deliveryLongitute;
    }
}