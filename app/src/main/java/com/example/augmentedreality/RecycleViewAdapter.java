package com.example.augmentedreality;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<Integer> imageObject;
    private List<ViewHolder> viewholderlist = new ArrayList<>();
    public static int counter = 0;
    private Context mContext;

    public RecycleViewAdapter(Context context, List<Integer> imageObject)
    {
        mContext = context;
        this.imageObject = imageObject;
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recycle, parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        int iconSrc = imageObject.get(position);
//        holder.setData(iconSrc);
        Glide.with(mContext)
                .asBitmap()
                .load(imageObject.get(position))
                .into(holder.imageView);

        viewholderlist.add(holder);

        holder.imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                setImageAlpha();
//                Log.d(TAG,"onClick: clicked on an image: "+ mImageUrls.get(position));
                //Toast.makeText(mContext,"image clicked:"+mImageUrls.get(position),Toast.LENGTH_SHORT).show();
                holder.imageView.setAlpha(0.5f);
                counter=position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageObject.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.placeHolder);

        }
    }

    private void setImageAlpha()
    {
        for(int i=0;i<viewholderlist.size();i++)
        {
            viewholderlist.get(i).imageView.setAlpha(1f);
        }
    }
}