package be.sanderdebleecker.herinneringsapp.Interfaces;

public interface IUserFListener {
    void onUserSelect(String user, int id);
    void onBackToLogin();
    void onBackToRegister();
}
