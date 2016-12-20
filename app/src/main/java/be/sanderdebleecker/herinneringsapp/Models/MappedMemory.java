package be.sanderdebleecker.herinneringsapp.Models;

public class MappedMemory {
    private int id;
    private String title;
    public Location location;

    public MappedMemory(int id,String title,double lng,double lat) {
        this.id = id;
        this.title = title;
        this.location = new Location(lng,lat,"");
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
