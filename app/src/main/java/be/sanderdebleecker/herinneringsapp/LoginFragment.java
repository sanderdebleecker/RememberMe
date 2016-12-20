package be.sanderdebleecker.herinneringsapp;

import android.content.Context;
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
import be.sanderdebleecker.herinneringsapp.Interfaces.ILoginFListener;

public class LoginFragment extends Fragment {
    private ILoginFListener listener;
    private TextView btnNewAccount;
    private Button btnLogin;
    private TextInputEditText etxtUser;
    private TextInputEditText etxtPass;

    public LoginFragment() {
    }
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnNewAccount = (TextView) v.findViewById(R.id.btnNewAccount);
        etxtUser = (TextInputEditText) v.findViewById(R.id.etxtUser);
        etxtPass = (TextInputEditText) v.findViewById(R.id.etxtPassword);

        //Register
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
                    UserDA usersData = new UserDA(getContext());
                    usersData.open();
                    int userIdentity = usersData.exists(user,pass);
                    if(userIdentity > -1) {
                        Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();
                        usersData.close();
                        listener.login(user,userIdentity);
                    }else{
                        Toast.makeText(getActivity(),"Failure",Toast.LENGTH_LONG).show();
                        usersData.close();
                    }
                }
            }
        });
        return v;
    }


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

}
