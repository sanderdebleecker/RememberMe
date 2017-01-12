package be.sanderdebleecker.herinneringsapp.Models;


import java.util.ArrayList;
import java.util.List;

/*
*       stmt.bindString(2, newSession.getDate());
        stmt.bindString(3, newSession.getDuration());
        stmt.bindLong(  4, newSession.getCount());
        stmt.bindString(5, newSession.getSessionIsFinished());
        stmt.bindString(6, newSession.getAuthor());*/

public class Session {
    private int id;
    private String name;
    private List<Album> albums;
    private String date;
    private int duration;
    private int count;
    private int author;
    private boolean sessionIsFinished;

    //CTOR
    public Session() {

    }
    public Session(String name,String date,int author) {
        this.name = name;
        this.date = date;
        this.author = author;
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
    public boolean getSessionIsFinished() {
        return sessionIsFinished;
    }
    public void setSessionIsFinished(boolean sessionIsFinished) {
        this.sessionIsFinished = sessionIsFinished;
    }
    public int getAuthor() {
        return author;
    }
    public void setAuthor(int author) {
        this.author = author;
    }
}
