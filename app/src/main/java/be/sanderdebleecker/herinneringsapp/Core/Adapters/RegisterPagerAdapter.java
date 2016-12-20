package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.RegisterPagerFragment;


//TODO make the adapter more dynamic with the enum RegisterPagerFragment.Page
//TODO restructure dataflow into decent manner

public class RegisterPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<RegisterPagerFragment> mFragments = new ArrayList<RegisterPagerFragment>();

    public class Form{
        public TextInputEditText etxtFirstName,etxtLastName,etxtUsername,etxtQuestion1,etxtQuestion2,etxtAnswer1,etxtAnswer2,etxtPassword,etxtConfirmPassword;
    }

    public RegisterPagerAdapter(FragmentManager fm,ArrayList<RegisterPagerFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
    public Form getForm() {
        Form f = new Form();
        f.etxtUsername = mFragments.get(0).etxtUsername;
        f.etxtPassword = mFragments.get(0).etxtPassword;
        f.etxtQuestion1 = mFragments.get(1).etxtQuestion1;
        f.etxtQuestion2 = mFragments.get(1).etxtQuestion2;
        f.etxtAnswer1 = mFragments.get(1).etxtAnswer1;
        f.etxtAnswer2 = mFragments.get(1).etxtAnswer2;
        f.etxtFirstName = mFragments.get(2).etxtFirstName;
        f.etxtLastName = mFragments.get(2).etxtLastName;
        return f;
    }
    public TextInputEditText getEtxtUsername() {
        return mFragments.get(0).etxtUsername;
    }
    public boolean validate() {
        try{
            boolean accV = mFragments.get(0).validate() ;
            boolean secV = mFragments.get(1).validate() ;
            boolean detV = mFragments.get(2).validate();
            return accV && secV && detV;
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return RegisterPagerFragment.Pages.values().length;
    }
}
