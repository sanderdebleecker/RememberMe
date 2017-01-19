package be.sanderdebleecker.herinneringsapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import be.sanderdebleecker.herinneringsapp.Interfaces.INewSessionFListener;

public class SessionActivity extends AppCompatActivity implements INewSessionFListener {

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


    //Interface methods
    @Override
    public void back() {
        onBackPressed();
    }
    @Override
    public void cancel(){
        finish();
    }
    @Override
    public void onSaved() {
        Toast.makeText(this,"Sessie opgeslaan",Toast.LENGTH_SHORT).show();
    }
    //Activity Events overrides
    @Override
    public void onBackPressed() {
        confirmExitDialog().show();
    }


    //Dialog
    private AlertDialog confirmExitDialog() {
        AlertDialog dialog =new AlertDialog.Builder(this)
                .setTitle("Sessie")
                .setMessage("Wil je de sessie verlaten?")
                .setPositiveButton("Blijven", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
                ).setNegativeButton("Verlaten", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        return dialog;
    }
}
