package com.example.augmentedreality.Modules.FaceFilter;


// Adapter of recyclerview

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.augmentedreality.R;

import java.util.ArrayList;

public class RecyclerViewAdapterfilter extends RecyclerView.Adapter<RecyclerViewAdapterfilter.ViewHolder>
{

    private  static final String TAG = "RecyclerViewAdapterFilter";

    private static ArrayList<Integer> mImageUrls = new ArrayList<>();
    private static ArrayList<ViewHolder> viewholderlist = new ArrayList<>();
    private Context mContext;
    private  static int flag=-1;
    private static int counter=0;

    RecyclerViewAdapterfilter(Context context, ArrayList<Integer> imageUrls)
    {
        mImageUrls = imageUrls;
        mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG,"onCreateViewHolder: Called.");
        //Toast.makeText(mContext,"onCreateviewHolder",Toast.LENGTH_SHORT).show();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d(TAG,"onBindViewHolder: Called");
        //Toast.makeText(mContext,"onBindViewHolder : "+position,Toast.LENGTH_SHORT).show();

        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(holder.image);

        viewholderlist.add(holder);

        holder.image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                setImageAlpha();
                Log.d(TAG,"onClick: clicked on an image: "+ mImageUrls.get(position));
                //Toast.makeText(mContext,"image clicked:"+mImageUrls.get(position),Toast.LENGTH_SHORT).show();
                holder.image.setAlpha(0.5f);
                counter=position;

                if(FaceFilter.augmentedFaceNode!=null)
                {
                    FaceFilter.removefromdisplay();
                }
                FaceFilter.placefilter(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
        }
    }

    private void setImageAlpha()
    {
        for(int i=0;i<viewholderlist.size();i++)
        {
            viewholderlist.get(i).image.setAlpha(1f);
        }
    }

}

