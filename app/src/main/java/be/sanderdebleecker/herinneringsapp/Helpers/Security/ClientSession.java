package be.sanderdebleecker.herinneringsapp.Helpers.Security;

public class ClientSession {
    public static final String EXTRA_CLIENT_SESSION = "be.quindo.herinneringsapp.loginactivity.clientsession";
    private String authUsername;
    private int authIdentity;
    private Modes mode;

    public enum Modes {
        OFFLINE,
        ONLINE;
    }
    public ClientSession(String authUsername,int authIdentity) {
        this.authUsername = authUsername;
        this.authIdentity = authIdentity;
        this.mode = Modes.OFFLINE;
    }
    public ClientSession(String authUsername, int authIdentity, Modes mode) {
        this.authUsername = authUsername;
        this.authIdentity = authIdentity;
        this.mode = mode;
    }

    public int getAuthIdentity() {
        return authIdentity;
    }

    public String toString() {
        return authUsername.toString();
    }
}
