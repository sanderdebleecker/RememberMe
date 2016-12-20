package be.sanderdebleecker.herinneringsapp.Models;

public class Location {
    private double lng;
    private double lat;
    private String name;

    public Location() {

    }
    public Location(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }
    public Location(double lng, double lat, String name) {
        this.lng = lng;
        this.lat = lat;
        this.name = name;
    }

    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
