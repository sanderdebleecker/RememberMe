package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.Memory;
import be.sanderdebleecker.herinneringsapp.R;

/*
*  Adapter for putting images into an instagram-like recycleview with specified columns
* */
//TODO : make generic adapter that devides screen into rows so MemoriesA and AlbumsA can inherit from it;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MyViewHolder>{
    private List<Memory> mMemories;
    private Context mContext;
    private int mColumns = 3;
    private float unitSize = 0;

    //CTOR
    public MemoryAdapter(Context context, List<Memory> memories) {
        this.mContext = context;
        this.mMemories = memories;
        calculateUnitDimensions();
    }
    public MemoryAdapter(Context context, List<Memory> memories, int columns) {
        this.mContext = context;
        this.mMemories = memories;
        calculateUnitDimensions();
        mColumns = columns;
    }

    //VIEWHOLDER
    public class MyViewHolder extends RecyclerView.ViewHolder {
        //public TextView txtvMemoryTitle;
        public ImageView imgvMemory;
        public TextView txtvMemoryTitle;
        public FrameLayout container;
        MyViewHolder(View v) {
            super(v);
            //txtvMemoryTitle = (TextView) v.findViewById(R.getResId.txtvMemoryTitle);
            imgvMemory = (ImageView) v.findViewById(R.id.imgvMemoryImage);
            txtvMemoryTitle = (TextView) v.findViewById(R.id.txtvMemoryTitle);
            container = (FrameLayout) v.findViewById(R.id.rowMemory);
        }
    }

    //CUSTOM METHODS
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        unitSize = dpWidth / mColumns;
    }
    private void setFailOver() {
        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {

            }
        });
    }
    public MemoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_memory,parent,false);
        return new MemoryAdapter.MyViewHolder(itemView);
    }

    //LOADER METHODS
    public void onBindViewHolder(MemoryAdapter.MyViewHolder holder, int position) {
        //StorageHelper class might have a design flaw with path ("/" + filename)
        Memory m = mMemories.get(position);
        String title = m.getTitle();
        String type = m.getType();
        String path = m.getPath();
        holder.txtvMemoryTitle.setText(title);
        TableRow.LayoutParams params = new TableRow.LayoutParams((int) unitSize, (int) unitSize); // (width, height)
        holder.container.setLayoutParams(params);
        loadThumbnail(holder.imgvMemory,type,path);
    }
    private void loadThumbnail(ImageView imgv, String type, String path) {
        StorageHelper.loadCroppedMedia(mContext,imgv,new MediaItem(MediaItem.Type.valueOf(type),path), (int) unitSize);
    }

    //INTERMEDIATE METHODS
    public void loadMemories(List<Memory> newMemories) {
        this.mMemories= newMemories;
        this.notifyDataSetChanged();
    }
    public String getIdentifier(int position) {
        Memory m = mMemories.get(position);
        return m.getUuid();
    }
    //DEFAULT METHODS
    public int getItemCount() {
        if(mMemories!=null){
            return mMemories.size();
        }else{
            return 0;
        }
    }}
