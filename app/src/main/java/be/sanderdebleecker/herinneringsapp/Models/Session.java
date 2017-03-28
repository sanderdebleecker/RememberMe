package be.sanderdebleecker.herinneringsapp.Models;

import java.util.List;

public class Session extends Entity {
    private String name;
    private List<Album> albums;
    private String date;
    private String notes;
    private int duration;
    private int count;
    private String author;
    private boolean isFinished;

    //CTOR
    public Session() {

    }
    public Session(String name,String date,String authorIdentifier) {
        this.name = name;
        this.date = date;
        this.author = authorIdentifier;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Album> getAlbums() {
        return albums;
    }
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public boolean isFinished() {
        return isFinished;
    }
    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String authorIdentifier) {
        this.author = authorIdentifier;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
