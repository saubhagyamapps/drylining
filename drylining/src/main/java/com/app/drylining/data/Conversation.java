package com.app.drylining.data;

/**
 * Created by Panda on 6/3/2017.
 */

public class Conversation
{
    private int senderId, receiverId, offerId, id;
    private String content, senderName, messageType, messageState;

    private String time_ago;    //ex: 5min, 6hours, 3days etc...

    public String getConfirm_id() {
        return confirm_id;
    }

    public void setConfirm_id(String confirm_id) {
        this.confirm_id = confirm_id;
    }

    private String confirm_id;    //ex: 5min, 6hours, 3days etc...

    public String getNewRead() {
        return newRead;
    }

    public void setNewRead(String newRead) {
        this.newRead = newRead;
    }

    private String newRead;    //ex: 5min, 6hours, 3days etc...

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    private String interest_id;    //ex: 5min, 6hours, 3days etc...

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    private String notification_id;
    public Conversation(int sender, int receiver, int offer, String content)
    {
        this.senderId = sender;
        this.receiverId = receiver;
        this.offerId = offer;
        this.content = content;
    }

    public Conversation(int id, int senderId, String senderName, String content, String type, String state, String ago,String notification_id,String newRead,String interest_id,String confirm_id)
    {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.messageType = type;
        this.messageState= state;
        this.time_ago = ago;
        this.notification_id=notification_id;
        this.interest_id=interest_id;
        this.newRead=newRead;
        this.confirm_id=confirm_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId()
    {
        return senderId;
    }

    public int getReceiverId()
    {
        return receiverId;
    }

    public int getOfferId()
    {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getContent()
    {
        return content;
    }

    public void addContent(String addString)
    {
        this.content = this.content + "\n" + addString;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageState() {
        return messageState;
    }

    public String getTimeAgo() {
        return time_ago;
    }
}
