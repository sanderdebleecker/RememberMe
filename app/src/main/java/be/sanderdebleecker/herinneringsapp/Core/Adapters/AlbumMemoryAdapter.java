package be.sanderdebleecker.herinneringsapp.Core.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import be.sanderdebleecker.herinneringsapp.Helpers.StorageHelper;
import be.sanderdebleecker.herinneringsapp.Models.MediaItem;
import be.sanderdebleecker.herinneringsapp.Models.SelectableMemory;
import be.sanderdebleecker.herinneringsapp.R;


public class AlbumMemoryAdapter extends RecyclerView.Adapter<AlbumMemoryAdapter.MyViewHolder>  {
    private boolean lockedSelection = false;
    private int[] resources = new int[]{R.drawable.ic_panorama_fish_eye_red_24dp,R.drawable.ic_lens_red_24dp};
    private static final Comparator<SelectableMemory> comperator = new Comparator<SelectableMemory>() {
        @Override
        public int compare(SelectableMemory a, SelectableMemory b) {
            return a.getTitle().compareTo(b.getTitle());
        }
    };
    private SortedList<SelectableMemory> mMems;
    private Context mContext;
    private float unitSize = 0;
    private int mColumns = 3;
    //CTOR
    public AlbumMemoryAdapter(Context context) {
        this.mContext = context;
        init();
    }
    public AlbumMemoryAdapter(Context context,int columns) {
        this.mContext = context;
        this.mColumns = columns;
        init();
    }

    //ADAPTER
    public int getItemCount() {
        return mMems.size();
    }

    public List<Integer> getSelectedMemories() {
        List<Integer> selectedMems = new ArrayList<Integer>();
        for(int i=0;i<mMems.size();i++) {
            if(mMems.get(i).isSelected()) {
                selectedMems.add(mMems.get(i).getId());
            }
        }
        return selectedMems;
    }

    public void setSelected(List<Integer> selectedMems) {
        for(int i=0;i<mMems.size();i++) {
            if(selectedMems.contains(mMems.get(i).getId())) {
                mMems.get(i).setSelected(true);
            }else{

            }
        }
        notifyDataSetChanged();
    }
    public void filterSelected(List<Integer> selectedMems) {
        for(int i=0;i<mMems.size();i++) {
            if (selectedMems.contains(mMems.get(i).getId())) {
                mMems.get(i).setSelected(true);
            }
        }
        for(int j=0;j<mMems.size();j++) {
            if(!mMems.get(j).isSelected()) {
                mMems.remove(mMems.get(j));
            }
        }
        notifyDataSetChanged();
    }
    //VIEWHOLDER
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgvMemory;
        public ImageView imgvToggle;
        public RelativeLayout container;

        public MyViewHolder(View v) {
            super(v);
            imgvMemory = (ImageView) v.findViewById(R.id.album_memory_imgvMemory);
            imgvToggle = (ImageView) v.findViewById(R.id.album_memory_imgvToggle);
            imgvToggle.setTag(R.id.imgvToggle_status,0);
            container = (RelativeLayout) v.findViewById(R.id.row_album_memory);
            initEvents();
        }
        private void initEvents() {
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lockedSelection) return;
                    int selected=0;
                    if(Integer.parseInt(imgvToggle.getTag(R.id.imgvToggle_status).toString())==0) {
                        selected=1;
                    }
                    imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[selected]));
                    imgvToggle.setTag(R.id.imgvToggle_status,selected);
                    selectionChange(getAdapterPosition(),selected==1);
                }
            });

        }
    }
    private void selectionChange(int pos,boolean selected) {
        mMems.get(pos).setSelected(selected);
    }
    public AlbumMemoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_album_memory,parent,false);
        return new MyViewHolder(itemView);
    }
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SelectableMemory m = mMems.get(position);
        //Set toggled
        if(m.isSelected()) {
            holder.imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[1]));
            holder.imgvToggle.setTag(R.id.imgvToggle_status,1);
        }else{
            holder.imgvToggle.setImageDrawable(ContextCompat.getDrawable(mContext,resources[0]));
            holder.imgvToggle.setTag(R.id.imgvToggle_status,0);
        }
        //Arrange height
        TableRow.LayoutParams params = new TableRow.LayoutParams((int) unitSize, (int) unitSize); // (width, height)
        holder.container.setLayoutParams(params);
        //Set Image
        loadThumbnail(holder.imgvMemory,m.getType(),m.getPath());
    }

    //m
    private void init() {
        calculateUnitDimensions();
        loadSortedList();
    }
    private void loadSortedList() {
        mMems = new SortedList<>(SelectableMemory.class, new SortedList.Callback<SelectableMemory>() {
            @Override
            public int compare(SelectableMemory a, SelectableMemory b) {
                return comperator.compare(a,b);
            }
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }
            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }
            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }
            @Override
            public boolean areContentsTheSame(SelectableMemory oldItem, SelectableMemory newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }
            @Override
            public boolean areItemsTheSame(SelectableMemory item1, SelectableMemory item2) {
                return item1.getId() == item2.getId();
            }
        });
    }
    private void loadThumbnail(ImageView imgv, String type, String path) {
        StorageHelper.loadCroppedMedia(mContext,imgv,new MediaItem(MediaItem.Type.valueOf(type),path), (int) unitSize);
    }

    //m load
    public void add(List<SelectableMemory> mems){
        mMems.beginBatchedUpdates();
        for(SelectableMemory m : mems) {
            mMems.add(m);
        }
        mMems.endBatchedUpdates();
    }
    public void add(SelectableMemory mem) {
        mMems.add(mem);
    }
    public void remove(SelectableMemory mem) {
        mMems.remove(mem);
    }
    public void remove(List<SelectableMemory> mems) {
        mMems.beginBatchedUpdates();
        for (SelectableMemory model : mems) {
            mMems.remove(model);
        }
        mMems.endBatchedUpdates();
    }
    public void replaceAll(List<SelectableMemory> models) {
        mMems.beginBatchedUpdates();
        for (int i = mMems.size() - 1; i >= 0; i--) {
            final SelectableMemory model = mMems.get(i);
            if (!models.contains(model)) {
                mMems.remove(model);
            }
        }
        mMems.addAll(models);
        mMems.endBatchedUpdates();
        notifyDataSetChanged();
    }
    //m
    public boolean hasSelected() {
        boolean hasSelected=false;
        for(int i=0;i<mMems.size();i++) {
            if(mMems.get(i).isSelected()) {
                hasSelected=true;
                break;
            }
        }
        return hasSelected;
    }
    public boolean isLockedSelection() {
        return lockedSelection;
    }
    public void setLockedSelection(boolean lockedSelection) {
        this.lockedSelection = lockedSelection;
    }
    //EVENTS

    //MISC
    private void calculateUnitDimensions() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        unitSize = dpWidth / mColumns;
    }
}
