package be.sanderdebleecker.herinneringsapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import be.sanderdebleecker.herinneringsapp.Core.MainApplication;
import be.sanderdebleecker.herinneringsapp.Data.DummyDA;
import be.sanderdebleecker.herinneringsapp.Data.UserDA;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.ClientSession;
import be.sanderdebleecker.herinneringsapp.Interfaces.ILoginFListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IRegisterFListener;
import be.sanderdebleecker.herinneringsapp.Interfaces.IUserFListener;

public class LoginActivity extends AppCompatActivity implements ILoginFListener, IRegisterFListener, IUserFListener {
    //LIFECYCLE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MainApplication app = (MainApplication) getApplicationContext();
        UserDA usersData = new UserDA(this);
        usersData.open();
        int userCount = usersData.count();
        if(userCount==0) {
            DummyDA dummyData = new DummyDA(this);
        }
        if(app.getCurrSession()!=null) {
            usersData.close();
            start();
        } else{
            if(userCount>0) {
                usersData.close();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction trans = fm.beginTransaction();
                trans.add(R.id.activity_login,UsersFragment.newInstance());
                trans.commit();
            }else{
                usersData.close();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction trans = fm.beginTransaction();
                trans.add(R.id.activity_login,LoginFragment.newInstance());
                trans.commit();
            }
        }
    }

    //CONTROLLER
    public void start() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //NAV
    public void loadRegisterF() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.activity_login,RegisterFragment.newInstance()).commit();
    }
    private void loadLoginF() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        trans.replace(R.id.activity_login,LoginFragment.newInstance()).commit();
    }
    //INTERFACES
    @Override
    public void toRegister() {
        loadRegisterF();
    }
    @Override
    public void login(String user, int id) {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(new ClientSession(user,id));
        start();
    }
    @Override
    public void registerSuccess(String user, int id) {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(new ClientSession(user,id));
        start();
    }
    @Override
    public void cancelRegister() {
        loadLoginF();
    }
    @Override
    public void onUserSelect(String user, int id) {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(new ClientSession(user,id));
        start();
    }
    public void onBackToLogin() {
        loadLoginF();
    }
    public void onBackToRegister() {
        loadRegisterF();
    }
}
