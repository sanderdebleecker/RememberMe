package be.sanderdebleecker.herinneringsapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import be.sanderdebleecker.herinneringsapp.Background.ConnectivityBroadcastReceiver;
import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Interfaces.INewMemoryFListener;

public class MemoryActivity extends AppCompatActivity implements INewMemoryFListener {
    private ConnectivityBroadcastReceiver broadcastReceiver;
    //LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        broadcastReceiver = new ConnectivityBroadcastReceiver();
        final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
        registerReceiver(broadcastReceiver, new IntentFilter(CONNECTIVITY_CHANGE));
        MainApplication app = (MainApplication) getApplicationContext();
        if(app.getConnectivityState()== ConnectivityBroadcastReceiver.ConnectivityState.WIFI) {
            Toast.makeText(this,"has Wifi",Toast.LENGTH_SHORT).show();
        }
        loadFragment();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void loadFragment() {
        int id;
        Bundle data = getIntent().getExtras();
        if(data==null) {
            id = -1;
        }else{
            id = data.getInt(MainActivity.EXTRA_ID_MEMORY);
        }
        if(id!=-1) {
            loadMemF(id);
        }else{
            loadNewMemF();
        }
    }
    //PERMISSIONS
    //METHODS
    private void loadNewMemF() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.aMemory_container, NewMemoryFragment.newInstance());
        trans.commit();
    }
    private void loadMemF(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        MemoryFragment fragm = MemoryFragment.newInstance(id);
        trans.replace(R.id.aMemory_container, fragm);
        trans.commit();
    }
    @Override
    public void memorySaved() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void cancel() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
