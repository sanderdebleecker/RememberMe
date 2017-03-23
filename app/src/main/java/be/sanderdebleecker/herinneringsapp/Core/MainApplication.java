package be.sanderdebleecker.herinneringsapp.Core;

import android.support.multidex.MultiDexApplication;

import be.sanderdebleecker.herinneringsapp.Background.ConnectivityBroadcastReceiver;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.ClientSession;

//extends global
public class MainApplication extends MultiDexApplication {
    private ClientSession currSession;
    private ConnectivityBroadcastReceiver.ConnectivityState connectivityState;

    //Get/set
    public String getCurrSessionValue() {
        return currSession.toString();
    }
    public ClientSession getCurrSession() {
        return currSession;
    }
    public void setCurrSession(ClientSession currSession) {
        this.currSession = currSession;
    }
    public void setConnectivityState(ConnectivityBroadcastReceiver.ConnectivityState connectivityState) {
        this.connectivityState = connectivityState;
    }
    public ConnectivityBroadcastReceiver.ConnectivityState getConnectivityState() {
        return connectivityState;
    }
}
