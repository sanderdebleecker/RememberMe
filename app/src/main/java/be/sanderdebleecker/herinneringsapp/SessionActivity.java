package be.sanderdebleecker.herinneringsapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        loadNewSessionFragment();
    }

    private void loadNewSessionFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        NewSessionFragment fragm = NewSessionFragment.newInstance();
        trans.replace(R.id.activity_session, fragm);
        trans.commit();
    }
}
