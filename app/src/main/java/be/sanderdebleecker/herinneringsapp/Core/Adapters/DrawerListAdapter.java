package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import be.sanderdebleecker.herinneringsapp.Models.NavItem;
import be.sanderdebleecker.herinneringsapp.R;

public class DrawerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NavItem> navItems;

    public DrawerListAdapter(Context context,ArrayList<NavItem> navItems) {
        this.context = context;
        this.navItems = navItems;
    }
    public int getCount() {
        return navItems.size();
    }
    public Object getItem(int position) {
        return navItems.get(position);
    }
    public long getItemId(int position) {
        return 0;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        NavItem currentItem = navItems.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(currentItem.isParent()) {
                view = inflater.inflate(R.layout.item_drawer, null);
            }else{
                view = inflater.inflate(R.layout.sub_item_drawer, null);
            }
        }
        else {
            view = convertView;
        }
        NavItem item = navItems.get(position);

        TextView txtvTitle = (TextView) view.findViewById(R.id.itemSubTitle);
        ImageView imgvIcon = (ImageView) view.findViewById(R.id.imgvDrawerIcon);

        txtvTitle.setText( item.getTitle() );
        imgvIcon.setImageDrawable(ContextCompat.getDrawable(context,item.getDrawable()));

        return view;
    }


}
