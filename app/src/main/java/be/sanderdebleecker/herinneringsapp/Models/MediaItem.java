package be.sanderdebleecker.herinneringsapp.Models;



public class MediaItem {
    public enum Type {
        NONE,
        IMAGE,
        AUDIO,
        VIDIO,
        GALLERY_IMAGE,
        GALLERY_AUDIO,
        GALLERY_VIDEO;
    };

    private Type type;
    private String path;

    public MediaItem() {
        this.type = Type.NONE;
        this.path = "";
    }
    public MediaItem(String type,String path)  {
        this.type = MediaItem.Type.valueOf(type);
        this.path = path;
    }
    public MediaItem(Type type, String path) {
        this.type = type;
        this.path = path;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
