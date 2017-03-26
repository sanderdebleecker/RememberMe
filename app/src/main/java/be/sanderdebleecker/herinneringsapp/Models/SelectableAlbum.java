package be.sanderdebleecker.herinneringsapp.Models;

import java.util.ArrayList;

public class SelectableAlbum extends Album {
    private boolean isSelected;

    public SelectableAlbum() {

    }
    public SelectableAlbum(Album a) {
        this.setUuid(a.getUuid());
        this.setAuthor(a.getAuthor());
        this.setAuthorId(a.getAuthorId());
        this.setName(a.getName());
        ArrayList<Memory> memories = a.getMemories();
        if(memories!=null)
            this.setMemories(memories);
        Memory thumbnail = a.getThumbnail();
        if(thumbnail!=null)
            this.setThumbnail(a.getThumbnail());
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
