package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import be.sanderdebleecker.herinneringsapp.Data.UserDA;
import be.sanderdebleecker.herinneringsapp.Helpers.Forms.Validator;
import be.sanderdebleecker.herinneringsapp.Helpers.Security.ClientSession;
import be.sanderdebleecker.herinneringsapp.Interfaces.ILoginFListener;

public class LoginFragment extends Fragment {
    private boolean performingLogin=false;
    private ILoginFListener listener;
    private TextView btnNewAccount;
    private Button btnLogin;
    private TextInputEditText etxtUser;
    private TextInputEditText etxtPass;

    //ctor
    public LoginFragment() {
    }
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }
    //lifecycle
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (ILoginFListener) getActivity();
        }catch(ClassCastException e){
            throw new ClassCastException("Activity must implement OnLoginFListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        new Initializer().execute(v);        //Register
        return v;
    }
    private void loadView(View v) {
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnNewAccount = (TextView) v.findViewById(R.id.btnNewAccount);
        etxtUser = (TextInputEditText) v.findViewById(R.id.etxtUser);
        etxtPass = (TextInputEditText) v.findViewById(R.id.etxtPassword);
    }
    private void loadEvents() {
        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.toRegister();
            }
        });
        //Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validated = Validator.validateRequiredFields(etxtUser,etxtPass);
                if(validated) {
                    String user = Validator.getValue(etxtUser);
                    String pass = Validator.getValue(etxtPass);
                    if(!performingLogin) {
                        performingLogin=true;
                        new LoginTask().execute(user,pass);
                    }
                }
            }
        });
    }
    private ClientSession login(String username,String password) {
        UserDA usersData = new UserDA(getContext());
        usersData.open();
        int userIdentity = usersData.getIdentifier(username,password);
        usersData.close();
        return new ClientSession(username,userIdentity);
    }
    private void onLoginResult(ClientSession loginSession) {
        if(loginSession.getAuthIdentity() > -1) {
            Toast.makeText(getActivity(),"Login succesvol",Toast.LENGTH_LONG).show();
            listener.login(loginSession);
        }else{
            Toast.makeText(getActivity(),"Verkeerde combinatie",Toast.LENGTH_LONG).show();
        }
    }
    //Tasks
    public class Initializer extends AsyncTask<View,Void,Void> {
        @Override
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loadEvents();
        }
    }
    public class LoginTask extends AsyncTask<String,Void,ClientSession> {
        @Override
        protected ClientSession doInBackground(String... params) {
            return login(params[0],params[1]);
        }
        @Override
        protected void onPostExecute(ClientSession loginSession) {
            onLoginResult(loginSession);
            performingLogin=false;
        }
    }
}
