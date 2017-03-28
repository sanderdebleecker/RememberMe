package be.sanderdebleecker.herinneringsapp.Interfaces;

public interface IUserFListener {
    void onUserSelect(String user, String identifier);
    void onBackToLogin();
    void onBackToRegister();
}
