package be.sanderdebleecker.herinneringsapp.Models.View;

import be.sanderdebleecker.herinneringsapp.Models.Entity;

public class SessionVM extends Entity {
    int duration;
    String name;
    String author;
    String date;

    //CTOR
    public SessionVM() {

    }
    public SessionVM(int id, String name, String author, int duration, String date) {
        this.name = name;
        this.author = author;
        this.duration = duration;
        this.date = date;
    }

    //getset
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
