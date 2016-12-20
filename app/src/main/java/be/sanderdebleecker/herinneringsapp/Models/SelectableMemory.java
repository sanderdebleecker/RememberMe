package be.sanderdebleecker.herinneringsapp.Models;

public class SelectableMemory extends Memory {
    private boolean selected;

    public SelectableMemory() {

    }
    public SelectableMemory(Memory m) {
        this.setLocation(m.getLocation());
        this.setTitle(m.getTitle());
        this.setDescription(m.getDescription());
        this.setDate(m.getDate());
        this.setType(m.getType());
        this.setPath(m.getPath());
        this.setCreator(m.getCreator());
        this.setId(m.getId());
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
