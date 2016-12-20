package be.sanderdebleecker.herinneringsapp.Models.View;

public class UserVM {
    private int id;
    private String username;

    public UserVM() {
    }
    public UserVM(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
