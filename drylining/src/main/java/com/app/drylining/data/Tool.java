package com.app.drylining.data;

/**
 * Created by TGYH on 3/3/2018.
 */

public class Tool {
    private int id, conversations, isUnreadMsg;
    private String name;
    private double longitude;
    private double latitude;
    private long price;
    private double distance;
    private String category;
    private String image_path;
    private String currency;
    private String posterId;
    private String posterType;

    public Tool(int id, String name, double longitude,double latitude, long price, String category,String image_path)
    {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.price = price;
        this.distance = 0;
        this.category = category;
        this.image_path = image_path;
    }

    public Tool(int id, String name, String category, long price, double distance)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        this.distance = distance;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterId(){
        return posterId;
    }

    public void setPosterId(String posterId){
        this.posterId = posterId;
    }

    public String getPosterType()
    {
        return posterType;
    }

    public void setPosterType(String posterType)
    {
        this.posterType = posterType;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getCurrency()
    {
        String currency_sign = "$";
        switch (currency){
            case "USD": currency_sign = "$";
                break;
            case "EUR": currency_sign = "€";
                break;
            case "Pound": currency_sign = "£";
                break;
        }
        return currency_sign;
    }

    public void setCurrency(String currency)
    {
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

    public double getDistance()
    {
        return distance;
    }

    public  void setDistance(double distance)
    {
        this.distance = distance;
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
