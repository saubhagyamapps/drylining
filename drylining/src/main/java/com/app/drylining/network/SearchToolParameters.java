package com.app.drylining.network;

import java.io.Serializable;

/**
 * Created by Pnada on 4/4/2017.
 */

public class SearchToolParameters implements Serializable
{
    private String searchName, searchCity, searchRange, searchProperty, searchRoom, searchGarage, searchCityLocation;
    private String postContent;
    private int searchMinPrice, searchMaxPrice;

    private String searchStreet;
    private String pinCode;

    private long mRangeInMeter;


    public SearchToolParameters(String name/*String city*/, String property/*, String garage*/, int min, int max/*, String location*/) {
        this.searchName = name;
        /*this.searchCity = city;*/
        /*this.searchRange = range;*/
        this.searchProperty = property;
        /*this.searchRoom = room;*/
        /*this.searchGarage = garage;*/
        /*this.searchMinPrice = Integer.valueOf(min).intValue();
        this.searchMaxPrice = Integer.valueOf(max).intValue();*/
        this.searchMinPrice = min;
        this.searchMaxPrice = max;
        /*this.searchCityLocation = location;*/
    }

    public void setSearchNmae(String name) {
        this.searchCity = name;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchCity(String city) {
        this.searchCity = city;
    }

    public String getSearchCity() {
        return searchCity;
    }

    public void setSearchRange(String range) {
        this.searchRange = range;
    }

    public String getSearchRange() {
        return searchRange;
    }

    public void setSearchProperty(String property) {
        this.searchProperty = property;
    }

    public String getSearchProperty() {
        return searchProperty;
    }

    public void setSearchRoom(String room) {
        this.searchRoom = room;
    }

    public String getSearchRoom() {
        return searchRoom;
    }

    public void setSearchGarage(String garage) {
        this.searchGarage = garage;
    }

    public String getSearchGarage() {
        return searchGarage;
    }

    public void setSearchMinPrice(int minPrice)
    {
        this.searchMinPrice = minPrice;
    }

    public int getSearchMinPrice()
    {
        return searchMinPrice;
    }

    public void setSearchMaxPrice(int maxPrice)
    {
        this.searchMaxPrice = maxPrice;
    }

    public int getSearchMaxPrice()
    {
        return searchMaxPrice;
    }


    public void setRangeInMeter(long range){
        mRangeInMeter=range;
    }


    public void setStreet(String street){
        searchStreet=street;
    }

    public void setPinCode(String code){
        pinCode=code;
    }

    /*public LatLng getSearchCityLocation()
    {
        String [] splitedLocation = searchCityLocation.split(",");
        double lat = Double.valueOf(splitedLocation[0]).doubleValue();
        double lng = Double.valueOf(splitedLocation[1]).doubleValue();
        return new LatLng(lat, lng);
    }*/

    public String getSearchCityLocation()
    {
        return searchCityLocation;
    }

    public String getPostContent() {
        //postContent="cityName=" + searchCity +"&postStreet="+searchStreet+"&postUnit="+pinCode+"&propertyType=" + searchProperty + "&roomType=" + searchRoom + " rooms house" + "&garageType=" + searchGarage+"&max_range="+mRangeInMeter+"&minPrice="+searchMinPrice+"&maxPrice="+searchMaxPrice;
        postContent="name=" + searchName + "&propertyType=" + searchProperty + "&minPrice=" + searchMinPrice + "&maxPrice=" +searchMaxPrice;
//        postContent = "cityName=kolkatta&postStreet=Ushumpur,%20Agarpara&postUnit=1&propertyType=Room&roomType=3%20rooms%20house&garageType=Parking&max_range=100000&minPrice=100&maxPrice=1000";///"cityName=" + searchCity + "&propertyType=" + searchProperty + "&roomType=" + searchRoom + " rooms house" + "&garageType=" + searchGarage;

        return  postContent.replace(" ","%20");
    }
}
