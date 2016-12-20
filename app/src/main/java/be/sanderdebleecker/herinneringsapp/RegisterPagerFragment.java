package be.sanderdebleecker.herinneringsapp;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.sanderdebleecker.herinneringsapp.Helpers.Forms.Validator;

/*
* Fragment which represents 3 slides thus having 3 layouts, and the necessary
* switches to differentiate functionality
* */

public class RegisterPagerFragment extends Fragment  {
    private Pages mCurrPage;
    public TextInputEditText etxtFirstName,etxtLastName,etxtUsername,etxtQuestion1,etxtQuestion2,etxtAnswer1,etxtAnswer2,etxtPassword,etxtConfirmPassword;


    public enum Pages {
        Account(R.layout.viewpage_register_1,R.id.etxtUsername,R.id.etxtPassword,R.id.etxtConfirmPassword),
        Security(R.layout.viewpage_register_2,R.id.etxtQuestion1,R.id.etxtAnswer1,R.id.etxtQuestion2,R.id.etxtAnswer2),
        Details(R.layout.viewpage_register_3,R.id.etxtFirstName,R.id.etxtLastName);
        private int resource;
        private int fields[];

        Pages(int resource, int... fields) {
            this.resource = resource;
            this.fields = fields;
        }
        public int getResource() {
            return resource;
        }
        public int[] getFields() {
            return fields;
        }
    }
    //CTOR
    public RegisterPagerFragment() {}
    public static RegisterPagerFragment newInstance(Pages page) {
        RegisterPagerFragment frag = new RegisterPagerFragment();
        frag.mCurrPage = page;
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                mCurrPage.getResource(), container, false);
        loadView(rootView);
        return rootView;
    }
    public void loadView(View v) {
        switch (mCurrPage) {
            case Account:
                loadAccountView(v);
                break;
            case Security:
                loadSecurityView(v);
                break;
            case Details:
                loadDetailsView(v);
                break;
            default:
                break;
        }
    }
    public void loadAccountView(View v) {
        etxtUsername = (TextInputEditText) v.findViewById(R.id.etxtUsername);
        etxtPassword = (TextInputEditText) v.findViewById(R.id.etxtPassword);
        etxtConfirmPassword = (TextInputEditText) v.findViewById(R.id.etxtConfirmPassword);
    }
    public void loadSecurityView(View v) {
        etxtQuestion1 = (TextInputEditText) v.findViewById(R.id.etxtQuestion1);
        etxtQuestion2 = (TextInputEditText) v.findViewById(R.id.etxtQuestion2);
        etxtAnswer1 = (TextInputEditText) v.findViewById(R.id.etxtAnswer1);
        etxtAnswer2 = (TextInputEditText) v.findViewById(R.id.etxtAnswer2);
    }
    public void loadDetailsView(View v) {
        etxtFirstName = (TextInputEditText) v.findViewById(R.id.etxtFirstName);
        etxtLastName = (TextInputEditText) v.findViewById(R.id.etxtLastName);
    }


    public boolean validate() {
        switch(mCurrPage) {
            case Account:
                return validateAccount();
            case Security:
                return validateSecurity();
            case Details:
                return validateDetails();
            default:
                return false;
        }
    }
    private boolean validateAccount() {
        boolean f1 = Validator.validateQuestionFields(etxtUsername);
        boolean f2 = Validator.validatePass(etxtPassword);
        boolean f3 = (etxtPassword.getText().toString().equals(etxtConfirmPassword.getText().toString()));
        if(!f3) {
            etxtConfirmPassword.setError("Komt niet overeen!");
        }
        return (f1 && f2 && f3);
    }
    private boolean validateSecurity() {
        return Validator.validateQuestionFields(etxtQuestion1,etxtAnswer1,etxtQuestion2,etxtAnswer2);
    }
    private boolean validateDetails() {
        return Validator.validateNameFields(etxtFirstName, etxtLastName);
    }

    public Pages getmCurrPage() {
        return mCurrPage;
    }
}
