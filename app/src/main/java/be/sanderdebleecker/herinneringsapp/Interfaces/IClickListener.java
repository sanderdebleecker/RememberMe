package be.sanderdebleecker.herinneringsapp.Interfaces;

import android.view.View;

public interface IClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
