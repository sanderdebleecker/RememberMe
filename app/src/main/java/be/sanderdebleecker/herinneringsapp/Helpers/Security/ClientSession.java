package be.sanderdebleecker.herinneringsapp.Helpers.Security;

public class ClientSession {
    private String authUsername;
    private String authIdentity;
    private Modes mode;

    public enum Modes {
        OFFLINE,
        ONLINE;
    }
    public ClientSession(String authUsername,String authIdentity) {
        this.authUsername = authUsername;
        this.authIdentity = authIdentity;
        this.mode = Modes.OFFLINE;
    }
    public ClientSession(String authUsername, String authIdentity, Modes mode) {
        this.authUsername = authUsername;
        this.authIdentity = authIdentity;
        this.mode = mode;
    }

    public String getAuthIdentity() {
        return authIdentity;
    }

    public String toString() {
        return authUsername.toString();
    }
}
