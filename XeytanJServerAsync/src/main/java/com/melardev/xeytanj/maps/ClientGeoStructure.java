package com.melardev.xeytanj.maps;

import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;

public class ClientGeoStructure {

    private Marker marker;
    private MarkerOptions markerOptions;
    private String title;

    private String city;
    private String country;
    private Double lat;
    private Double lon;

    public ClientGeoStructure() {
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ClientGeoStructure(String city, String country, Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.country = country;

    }


    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

}
