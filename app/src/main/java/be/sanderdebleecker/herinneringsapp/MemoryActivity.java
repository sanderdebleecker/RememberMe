package be.sanderdebleecker.herinneringsapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import be.sanderdebleecker.herinneringsapp.Interfaces.INewMemoryFListener;

public class MemoryActivity extends AppCompatActivity implements INewMemoryFListener {
    //LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        loadFragment();
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
