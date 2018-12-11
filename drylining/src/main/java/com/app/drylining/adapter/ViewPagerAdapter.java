package com.app.drylining.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.drylining.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Momin Nasirali on 11-03-2017.
 */

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    String[] images;
    LayoutInflater inflater;

    public ViewPagerAdapter(Context context, String[] images)
    {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        ImageView imgflag;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.cell_view_pager, container,
                false);


        // Locate the ImageView in viewpager_item.xml
        imgflag = (ImageView) itemView.findViewById(R.id.imageView);
        // Capture position and set to the ImageView
        // imgflag.setImageResource(images[position]);

        Picasso.get()
                .load(images[position])
                .resizeDimen(R.dimen.page_viewer_image_size, R.dimen.page_viewer_image_size)
                .centerCrop()
                .into(imgflag);

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
