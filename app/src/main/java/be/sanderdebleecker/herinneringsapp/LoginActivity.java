package be.sanderdebleecker.herinneringsapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
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
        new Initializer().execute();
    }
    private int load() {
        UserDA usersData = new UserDA(this);
        usersData.open();
        int userCount = usersData.count();
        usersData.close();
        return userCount;
    }
    private void onLoad(int userCount) {
        MainApplication app = (MainApplication) getApplicationContext();
        if(userCount==0) {
            new DummyDA(this);
        }
        if(app.getCurrSession()!=null) {
            start();
        } else{
            if(userCount>0) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction trans = fm.beginTransaction();
                trans.add(R.id.activity_login,UsersFragment.newInstance());
                trans.commit();
            }else{
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction trans = fm.beginTransaction();
                trans.add(R.id.activity_login,LoginFragment.newInstance());
                trans.commit();
            }
        }
    }
    //Controller
    public void start() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    //Fragments
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
    //Interfaces
    @Override
    public void toRegister() {
        loadRegisterF();
    }
    @Override
    public void login(ClientSession loginSession) {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(loginSession);
        start();
    }
    @Override
    public void registerSuccess(String user, String identifier) {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(new ClientSession(user,identifier));
        start();
    }
    @Override
    public void cancelRegister() {
        loadLoginF();
    }
    @Override
    public void onUserSelect(String user, String identifier) {
        MainApplication app = (MainApplication) getApplicationContext();
        app.setCurrSession(new ClientSession(user,identifier));
        start();
    }
    public void onBackToLogin() {
        loadLoginF();
    }
    public void onBackToRegister() {
        loadRegisterF();
    }
    //Tasks
    private class Initializer extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return load();
        }
        @Override
        protected void onPostExecute(Integer result) {
            onLoad(result);
        }
    }

}
