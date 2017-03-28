package be.sanderdebleecker.herinneringsapp.Interfaces;

public interface IRegisterFListener {
    void cancelRegister();
    void registerSuccess(String user, String id);
}
