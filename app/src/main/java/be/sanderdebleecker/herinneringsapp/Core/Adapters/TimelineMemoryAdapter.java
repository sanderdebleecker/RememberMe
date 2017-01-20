package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.R;

//TODO DATETIME ZONES WONT BE INCORPERATED
//TODO Heterogeneous  views

public class TimelineMemoryAdapter extends RecyclerView.Adapter<TimelineMemoryAdapter.MyViewHolder>{
    private static final int IMG_WIDTH_PERCENT = 20;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat formatParser = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<Memory> mMemories;
    private Context mContext;
    private String currYear = "";
    private int unitSize;

    public TimelineMemoryAdapter(Context context, ArrayList<Memory> memories) {
        this.mContext = context;
        this.mMemories = memories;
        calculateUnitDimensions();
    }
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline,parent,false);
        return new TimelineMemoryAdapter.MyViewHolder(itemView);
    }
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Memory m = mMemories.get(position);
        String year =  getDate(m.getDate());
        if(!year.equals("") && !year.equals(currYear) ) {
            holder.txtvDate.setText(year);
            currYear=year;
        }else {
            holder.txtvDate.setText("");
        }
        holder.txtvTitle.setText(m.getTitle());
        holder.txtvAuthorLoc.setText(m.getCreator()+" - "+m.getLocation().getName());
        holder.txtvDescr.setText(m.getDescription());
        StorageHelper.loadCroppedMedia(mContext,holder.imgvTimeline,new MediaItem(MediaItem.Type.valueOf(m.getType()),m.getPath()),unitSize);
    }
    private String getDate(String dateString) {
        try {
            Date date = formatParser.parse(dateString);
            return formatter.format(date);
        } catch(Exception e) {
            return "";
        }
    }
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        unitSize = (int) (dpWidth / (100/IMG_WIDTH_PERCENT));
    }
    public int getItemCount() {
        return mMemories.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtvDate,txtvTitle,txtvAuthorLoc,txtvDescr;
        public ImageView imgvTimeline;

        public MyViewHolder(View v) {
            super(v);
            txtvDate = (TextView) v.findViewById(R.id.txtvDateTimeline);
            txtvTitle = (TextView) v.findViewById(R.id.txtvTitleTimeline);
            txtvAuthorLoc = (TextView) v.findViewById(R.id.txtvAuthorLocationTimeline);
            txtvDescr = (TextView) v.findViewById(R.id.txtvDescrTimeline);
            imgvTimeline = (ImageView) v.findViewById(R.id.imgvTimeline);
        }
    }

}
