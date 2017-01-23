package be.sanderdebleecker.herinneringsapp.Interfaces;

import be.sanderdebleecker.herinneringsapp.Helpers.Security.ClientSession;

public interface ILoginFListener {
    void toRegister();
    void login(ClientSession loginSession);
}
