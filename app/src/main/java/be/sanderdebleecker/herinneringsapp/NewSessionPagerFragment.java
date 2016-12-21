package be.sanderdebleecker.herinneringsapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewSessionPagerFragment extends SessionPagerFragment {


    //CTOR
    public NewSessionPagerFragment() {
    }
    public static SessionPagerFragment newInstance() {
        return new SessionPagerFragment();
    }

    //LC
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_session_pager, container, false);
    }

    //M

}
