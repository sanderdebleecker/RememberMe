package be.sanderdebleecker.herinneringsapp.Interfaces;

public interface IRegisterFListener {
    void cancelRegister();
    void registerSuccess(String user, int id);
}
