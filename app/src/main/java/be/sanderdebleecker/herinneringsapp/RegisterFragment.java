package be.sanderdebleecker.herinneringsapp;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.Toast;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Core.Adapters.RegisterPagerAdapter;
import be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI.DotsScrollBar;
import be.sanderdebleecker.herinneringsapp.Data.UserDA;
import be.sanderdebleecker.herinneringsapp.Helpers.Forms.Validator;
import be.sanderdebleecker.herinneringsapp.Interfaces.IRegisterFListener;
import be.sanderdebleecker.herinneringsapp.Models.User;

//TODO FORCE user to select from a collection of open question preventing a BRUTE FORCE ANGLE
//TODO implement onbackpressed to slide back to first viewpage, after that use super.onbackpress();

public class RegisterFragment extends Fragment {
    private Button btnCancel,btnRegister;
    private IRegisterFListener listener;
    private ViewPager mPager;
    private LinearLayoutCompat mPagerIndicator;
    private RegisterPagerAdapter mPagerAdapter;

    //Ctor
    public RegisterFragment() {

    }
    public static Fragment newInstance() {
        return new RegisterFragment();
    }
    //Lifecycle
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (IRegisterFListener) getActivity();
        }catch(ClassCastException ex){
            throw new ClassCastException(getActivity().toString()+" must impl OnRegisterFListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }
    public void loadView(View v) {
        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnRegister = (Button) v.findViewById(R.id.btnRegister);
        mPager = (ViewPager) v.findViewById(R.id.register_pager);
        mPagerIndicator = (LinearLayoutCompat) v.findViewById(R.id.register_pager_indicator);
    }
    private void loadViewPagerAdapter() {
        ArrayList<RegisterPagerFragment> fragments = new ArrayList<>();
        fragments.add(RegisterPagerFragment.newInstance(RegisterPagerFragment.Pages.Account));
        fragments.add(RegisterPagerFragment.newInstance(RegisterPagerFragment.Pages.Security));
        fragments.add(RegisterPagerFragment.newInstance(RegisterPagerFragment.Pages.Details));
        mPagerAdapter = new RegisterPagerAdapter(getActivity().getSupportFragmentManager(),fragments);
    }

    private void loadViewPager() {
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateViewPagerIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        updateViewPagerIndicator(0);
    }

    private void updateViewPagerIndicator(int currPage) {
        mPagerIndicator.removeAllViews();
        DotsScrollBar.createDotScrollBar(getContext(), mPagerIndicator, currPage, mPagerAdapter.getCount());
    }
    public boolean isValidated() {
        return mPagerAdapter.validate();
    }
    public int regiserSuccess(UserDA usersData) {
        RegisterPagerAdapter.Form f = mPagerAdapter.getForm();
        User user = new User();
        user.setUsername(Validator.getValue(f.etxtUsername));
        user.setPassword(Validator.getValue(f.etxtPassword));
        user.setFirstName(Validator.getValue(f.etxtFirstName));
        user.setLastName(Validator.getValue(f.etxtLastName));
        user.setQ1(Validator.getValue(f.etxtQuestion1));
        user.setQ2(Validator.getValue(f.etxtQuestion2));
        user.setA1(Validator.getValue(f.etxtAnswer1));
        user.setA2(Validator.getValue(f.etxtAnswer2));
        return usersData.insert(user);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        new Initializer().execute(v);

        return v;
    }
    private void addEvents() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancelRegister();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidated()) {
                    TextInputEditText etxtUsername = mPagerAdapter.getEtxtUsername();
                    String username = Validator.getValue(etxtUsername);
                    UserDA usersData = new UserDA(getContext());
                    usersData.open();
                    if(!usersData.getIdentifier(username)){
                        int newUserId = regiserSuccess(usersData);
                        if(newUserId!=-1) {
                            usersData.close();
                            listener.registerSuccess(Validator.getValue(etxtUsername),newUserId);
                        }else{
                            Toast.makeText(getActivity(),"Gebruiker niet geregistreerd",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        etxtUsername.setError("Gebruiker bestaat al!");
                    }
                    usersData.close();
                }else {
                }
            }
        });
    }
    //Tasks
    private class Initializer extends AsyncTask<View, Void, Void> {
        protected Void doInBackground(View... params) {
            loadView(params[0]);
            loadViewPagerAdapter();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            addEvents();
            loadViewPager();
        }
    }

}
