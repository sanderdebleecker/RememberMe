package be.sanderdebleecker.herinneringsapp.Models.View;

import be.sanderdebleecker.herinneringsapp.Models.Entity;

public class UserVM extends Entity {
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

}
