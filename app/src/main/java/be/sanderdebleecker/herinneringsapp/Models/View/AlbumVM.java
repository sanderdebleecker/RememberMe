package be.sanderdebleecker.herinneringsapp.Models.View;

import java.util.List;

import be.sanderdebleecker.herinneringsapp.Models.Album;

public class AlbumVM {
    private Album album;
    private List<Integer> selectedMemories;

    public AlbumVM() {

    }

    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }
    public List<Integer> getSelectedMemories() {
        return selectedMemories;
    }
    public void setSelectedMemories(List<Integer> selectedMemories) {
        this.selectedMemories = selectedMemories;
    }
}
