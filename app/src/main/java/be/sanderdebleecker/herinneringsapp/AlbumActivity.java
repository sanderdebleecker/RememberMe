package be.sanderdebleecker.herinneringsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import be.sanderdebleecker.herinneringsapp.Interfaces.INewAlbumFListener;

import static be.sanderdebleecker.herinneringsapp.MainActivity.EXTRA_OVERVIEW;

public class AlbumActivity extends AppCompatActivity implements INewAlbumFListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        loadFragment();
    }

    private void loadFragment() {
        int id;
        Bundle data = getIntent().getExtras();
        if(data==null) {
            id = -1;
        }else{
            id = data.getInt(MainActivity.EXTRA_ID_ALBUM);
        }
        if(id!=-1) {
            loadAlbumF(id);
        }else{
            loadNewAlbumF();
        }
    }


    private void loadAlbumF(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        AlbumFragment fragm = AlbumFragment.newInstance(id);
        trans.replace(R.id.activity_album, fragm);
        trans.commit();
    }
    private void loadNewAlbumF() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        NewAlbumFragment fragm = NewAlbumFragment.newInstance();
        trans.replace(R.id.activity_album, fragm);
        trans.commit();
    }

    @Override
    public void albumSaved() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_OVERVIEW, MainActivity.Overviews.Albums.toString());
        startActivity(intent);
    }

    @Override
    public void cancel() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_OVERVIEW, MainActivity.Overviews.Albums.toString());
        startActivity(intent);
    }
}

