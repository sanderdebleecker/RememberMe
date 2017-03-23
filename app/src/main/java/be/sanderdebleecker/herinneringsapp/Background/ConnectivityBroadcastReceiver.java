package be.sanderdebleecker.herinneringsapp.Background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import be.sanderdebleecker.herinneringsapp.Core.MainApplication;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
    public enum ConnectivityState {
        NONE,DISABLED,WIFI,DATA
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            MainApplication app = (MainApplication) context.getApplicationContext();
            if(hasWiFi(context)) {
                Intent serviceIntent = new Intent(context, SyncService.class );
                context.startService(serviceIntent);
            }
        }catch(NullPointerException ex) {
            Log.d("ConnectivityBCReceiver","MainApplication was not availabe (nullptr)");
        }
    }
    public boolean hasWiFi(Context context) {
        return getState(context)==ConnectivityState.WIFI;
    }
    public ConnectivityState getState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return ConnectivityState.DISABLED;
        }else{
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
                if(isWiFi) {
                    return ConnectivityState.WIFI;
                }else{
                    return ConnectivityState.DATA;
                }
            }else{
                return ConnectivityState.NONE;
            }
        }
    }


}
