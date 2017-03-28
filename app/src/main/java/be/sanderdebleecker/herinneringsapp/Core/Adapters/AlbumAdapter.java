package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.Album;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.R;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
    private float unitSize = 0;
    private int mColumns = 3;
    private List<Album> mAlbums;
    private Context mContext;

    public AlbumAdapter(Context context,List<Album> albums) {
        this.mContext = context;
        this.mAlbums = albums;
        calculateUnitDimensions();
    }
    public AlbumAdapter(Context context,ArrayList<Album> albums,int columns) {
        this.mContext = context;
        this.mAlbums = albums;
        this.mColumns = columns;
        calculateUnitDimensions();
    }
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        unitSize = dpWidth / mColumns;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgvAlbum;
        public LinearLayout container;

        MyViewHolder(View v) {
            super(v);
            imgvAlbum = (ImageView) v.findViewById(R.id.imgvAlbumImage);
            container = (LinearLayout) v.findViewById(R.id.row_album);
        }
    }
    public AlbumAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_album,parent,false);
        return new AlbumAdapter.MyViewHolder(itemView);
    }
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Album a = mAlbums.get(position);
        String name = a.getName();
        String type = a.getThumbnail().getType();
        String path = a.getThumbnail().getPath();
        TableRow.LayoutParams params = new TableRow.LayoutParams((int) unitSize, (int) unitSize); // (width, height)
        holder.container.setLayoutParams(params);
        if(type!=null && path!=null) {
            loadThumbnail(holder.imgvAlbum,type,path);
        }
    }
    private void loadThumbnail(ImageView imgv, String type, String path) {
        StorageHelper.loadCroppedMedia(mContext,imgv,new MediaItem(MediaItem.Type.valueOf(type),path), (int) unitSize);
    }

    public String getUuid(int position) {
        Album a = mAlbums.get(position);
        return a.getUuid();
    }

    public int getItemCount() {
        return mAlbums.size();
    }


}
