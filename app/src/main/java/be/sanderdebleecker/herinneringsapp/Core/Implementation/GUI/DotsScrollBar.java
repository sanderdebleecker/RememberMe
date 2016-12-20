package be.sanderdebleecker.herinneringsapp.Core.Implementation.GUI;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import be.sanderdebleecker.herinneringsapp.R;

/**
 * Credit goes to Shruti @ http://stackoverflow.com/questions/17107668/view-pager-with-fragments-and-indicator
 * Changed import to compat
 */

public class DotsScrollBar
{
    LinearLayout main_image_holder;
    public static void createDotScrollBar(Context context, LinearLayoutCompat main_holder, int selectedPage, int count)
    {
        for(int i=0;i<count;i++)
        {
            ImageView dot = null;
            dot= new ImageView(context);
            LinearLayout.LayoutParams vp =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            dot.setLayoutParams(vp);
            if(i==selectedPage)
                try {
                    dot.setImageResource(R.drawable.ic_lens_black_24dp);
                } catch (Exception e) {
                    Log.d("inside DotsScrollBar.jv", "could not locate identifier");
                }
            else
            {
                dot.setImageResource(R.drawable.ic_panorama_fish_eye_black_24dp);
            }
            main_holder.addView(dot);
        }
        main_holder.invalidate();
    }
}