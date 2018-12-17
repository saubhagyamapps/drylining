package com.app.drylining.data;


import android.util.Log;

import java.io.Serializable;

public class Offer implements Serializable {
    private static final String TAG = "Offer";
    private int id, conversations, isUnreadMsg;
    private String name;
    private double longitude;
    private double latitude;
    private String postCity;
    private String price;
    private double distance;
    private String category;
    private String image_path;
    private String interested;
    private String currency;
    private String user_id;
    private String mUserIdPostedJob;
    private int jobStatus;// 0: create, 1: progress

    public Offer(int id, String name, double longitude, double latitude, String price, String category, String image_path, String mUserIdPostedJob) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.distance = 0;
        this.category = category;
        this.image_path = image_path;
        this.mUserIdPostedJob = mUserIdPostedJob;
    }

    public Offer(int id, String name, double longitude, double latitude, String price, String category, String image_path, String mUserIdPostedJob, String dami) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.distance = 0;
        this.category = category;
        this.image_path = image_path;
        this.mUserIdPostedJob = mUserIdPostedJob;
        this.user_id = user_id;
    }
    public Offer(int id, String name, String category, String price, double distance) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.distance = distance;
        this.price = price;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getmUserIdPostedJob() {
        return mUserIdPostedJob;
    }

    public void setmUserIdPostedJob(String mUserIdPostedJob) {
        this.mUserIdPostedJob = mUserIdPostedJob;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        Log.e(TAG, "getName: " + name);
        return name;
    }

    public void setName(String name) {
        Log.e(TAG, "setName: " + name);
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPostCity() {
        return postCity;
    }

    public void setPostCity(String postCity) {
        this.postCity = postCity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        Log.e(TAG, "setPrice: " + price);
        this.price = price;
    }

    public String getCurrency() {
        String currency_sign = "$";
        switch (currency) {
            case "USD":
                currency_sign = "$";
                break;
            case "EUR":
                currency_sign = "€";
                break;
            case "Pound":
                currency_sign = "£";
                break;
        }
        return currency_sign;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getJobStatus() {
        return this.jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getInterested() {
        return this.interested;
    }

    public void setInterested(String interested) {
        this.interested = interested;
    }

    public int getConversations() {
        return conversations;
    }

    public void setConversations(int conversations) {
        this.conversations = conversations;
    }

    public int getIsUnreadMsg() {
        return isUnreadMsg;
    }

    public void setIsUnreadMsg(int isUnreadMsg) {
        this.isUnreadMsg = isUnreadMsg;
    }
}
