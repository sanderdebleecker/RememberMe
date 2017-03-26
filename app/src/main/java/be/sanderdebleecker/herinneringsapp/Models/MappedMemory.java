package be.sanderdebleecker.herinneringsapp.Models;

public class MappedMemory extends Entity {

    private String title;
    public Location location;

    public MappedMemory(String uuid,String title,double lng,double lat) {
        this.setUuid(uuid);
        this.title = title;
        this.location = new Location(lng,lat,"");
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
