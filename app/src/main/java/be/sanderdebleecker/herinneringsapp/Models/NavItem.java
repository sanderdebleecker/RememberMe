package be.sanderdebleecker.herinneringsapp.Models;

public class NavItem {
    private String title;
    private int drawable;
    private boolean isParent;

    public NavItem(String name) {
        title = name;
        setParent(false);
    }
    public NavItem(String name,boolean parent) {
        this.title = name;
        this.isParent = parent;
    }
    public NavItem(String name,boolean parent,int drawable) {
        this.title = name;
        this.isParent = parent;
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getDrawable() {
        return drawable;
    }
    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
    public boolean isParent() {
        return isParent;
    }
    public void setParent(boolean parent) {
        isParent = parent;
    }
}
