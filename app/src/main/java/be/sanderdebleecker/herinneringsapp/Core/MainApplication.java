package be.sanderdebleecker.herinneringsapp.Core;

import android.support.multidex.MultiDexApplication;

import be.sanderdebleecker.herinneringsapp.Helpers.Security.ClientSession;

//extends global
public class MainApplication extends MultiDexApplication {
    private ClientSession currSession;

    public String getCurrSessionValue() {
        return currSession.toString();
    }
    public ClientSession getCurrSession() {
        return currSession;
    }
    public void setCurrSession(ClientSession currSession) {
        this.currSession = currSession;
    }
}
