package be.sanderdebleecker.herinneringsapp.Interfaces;


import be.sanderdebleecker.herinneringsapp.Models.Session;

public interface IEndSessionPagerFListener {
    Session getSession();
    int getSessionDuration();
    void viewPreviousPage();
}
