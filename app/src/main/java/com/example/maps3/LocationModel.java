package com.example.maps3;

public class LocationModel {
    private double lat;
    private double lng;
    private int icon;
    private String title;
    private String subTitle;

    public LocationModel(double lat, double lng, int icon, String title, String subTitle) {
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
        this.title = title;
        this.subTitle = subTitle;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
