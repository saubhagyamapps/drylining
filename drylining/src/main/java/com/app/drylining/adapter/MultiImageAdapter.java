package com.app.drylining.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.drylining.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.MyViewHolder> {

    private ArrayList<String> mImagesList;
    private Context mContext;
    private SparseBooleanArray mSparseBooleanArray;
    private int addableNum;

    public MultiImageAdapter(Context context, ArrayList<String> imageList, int num)
    {
        mContext = context;
        mSparseBooleanArray = new SparseBooleanArray();
        mImagesList = new ArrayList<String>();
        this.mImagesList = imageList;
        this.addableNum = num;
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for(int i=0;i<mImagesList.size();i++) {
            if(mSparseBooleanArray.get(i)) {
                mTempArry.add(mImagesList.get(i));
            }
        }

        return mTempArry;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
        }
    };

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_multiphoto_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String imageUrl = mImagesList.get(position);

//        Glide.get(mContext)
//                .load("file://"+imageUrl)
//                .centerCrop()
//                .placeholder(R.drawable.load)
//                .error(R.drawable.load)
//                .into(holder.imageView);
        Picasso.get()
                .load("file://"+imageUrl)
                .placeholder(R.drawable.load)
                .resizeDimen(R.dimen.grid_cell_image_size, R.dimen.grid_cell_image_size)
                .centerCrop()
                .into(holder.imageView);

        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(mSparseBooleanArray.get(position));
        holder.checkBox.setOnCheckedChangeListener(mCheckedChangeListener);

        if(holder.checkBox.isChecked())
            holder.imgTransparent.setVisibility(View.GONE);
        else
            holder.imgTransparent.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        public CheckBox checkBox;
        public ImageView imageView, imgTransparent;

        public MyViewHolder(View view) {
            super(view);

            checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
            imageView = (ImageView) view.findViewById(R.id.imageView1);
            imgTransparent = (ImageView) view.findViewById(R.id.img_transparent);
            imgTransparent.bringToFront();

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked())
                        imgTransparent.setVisibility(View.GONE);
                    else
                        imgTransparent.setVisibility(View.VISIBLE);
                }
            });

            imgTransparent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(getCheckedItems().size() == addableNum)
                    {
                        Toast.makeText(mContext, "You can select up to " + addableNum + " photos more", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    imgTransparent.setVisibility(View.GONE);
                    checkBox.setChecked(true);
                }
            });
        }
    }
}
