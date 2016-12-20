package be.sanderdebleecker.herinneringsapp.Models;

public class Memory {
    private int id;
    private String title;
    private String description;
    private String date;
    private Location location;
    private String path;
    private String type;
    private int creator;

    //title,descr,date,mMediaItem.getType().toString(),mMediaItem.getPath(),location.getName(),location.getLng(),location.getLat(),author
    public Memory() {

    }
    public Memory(int id,String title,String desc,String date,String type,String path,String location,Double lng,Double lat,int creator) {
        this.id = id;
        this.title = title;
        this.description = desc;
        this.date = date;
        this.type = type;
        this.path = path;
        this.location = new Location(lng,lat,location);
        this.creator = creator;
    }
    public Memory(String title,String desc,String date,String type,String path,String location,Double lng,Double lat,int creator) {
        this.title = title;
        this.description = desc;
        this.date = date;
        this.type = type;
        this.path = path;
        this.location = new Location(lng,lat,location);
        this.creator = creator;
    }
    public Memory(int id,String title,String desc,String date,String type,String path,int creator) {
        this.id = id;
        this.title = title;
        this.description = desc;
        this.date = date;
        this.type = type;
        this.path = path;
        this.creator = creator;
    }
    public Memory(String title,String desc,String date,String type,String path,int creator) {
        this.title = title;
        this.description = desc;
        this.date = date;
        this.type = type;
        this.path = path;
        this.creator = creator;
    }


    //GETSET
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public int getCreator() {
        return creator;
    }
    public void setCreator(int creator) {
        this.creator = creator;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

}
