package com.app.drylining.data;

import java.io.Serializable;

public class Favorite implements Serializable
{
    private int id, senderId, offerId;
    private String senderName,isConfirmed, senderPhone;

    public Favorite(int id, int senderId, String senderName, int offerId, String isConfirmed)
    {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.offerId = offerId;
        this.isConfirmed = isConfirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone()
    {
        return this.senderPhone;
    }

    public void setSenderPhone(String senderPhone)
    {
        this.senderPhone = senderPhone;
    }
}
