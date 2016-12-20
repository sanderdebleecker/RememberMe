package be.sanderdebleecker.herinneringsapp.Models;

import java.util.ArrayList;

public class Album {
    private int id;
    private String name;
    private String author;
    private int authorId;
    private Memory thumbnail;
    private ArrayList<Memory> memories;

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
    public ArrayList<Memory> getMemories() {
        return memories;
    }
    public void setMemories(ArrayList<Memory> memories) {
        this.memories = memories;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Memory getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(Memory thumbnail) {
        this.thumbnail = thumbnail;
    }
    public int getAuthorId() {
        return authorId;
    }
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}
