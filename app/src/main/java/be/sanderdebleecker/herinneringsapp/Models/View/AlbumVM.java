package be.sanderdebleecker.herinneringsapp.Models.View;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Models.Album;

public class AlbumVM {
    private Album album;
    private List<String> selectedMemories;

    public AlbumVM() {

    }

    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
    public List<String> getSelectedMemories() {
        return selectedMemories;
    }
    public void setSelectedMemories(List<String> selectedMemories) {
        this.selectedMemories = selectedMemories;
    }
}
