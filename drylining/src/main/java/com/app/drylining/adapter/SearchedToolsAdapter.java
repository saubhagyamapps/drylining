package com.app.drylining.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.custom.ItemClickListener;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Tool;
import com.app.drylining.ui.SearchedToolDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panda on 20-05-2017.
 */

public class SearchedToolsAdapter extends RecyclerView.Adapter<SearchedToolsAdapter.ViewHolder>
{
    private ArrayList<Tool> arrayList;
    public Context mContext;
    private ApplicationData appData;
    private CustomMainActivity mActivity;

    public SearchedToolsAdapter(Context context, ArrayList<Tool> arrayList)
    {
        this.arrayList = arrayList;
        this.mContext = context;
        appData = ApplicationData.getSharedInstance();

        this.mActivity = (CustomMainActivity) context;

        //sortOffersByDistance();
    }

    /*public void sortOffersByDistance()
    {
        Collections.sort(arrayList, new Comparator<Offer>()
        {
            @Override
            public int compare(Offer o1, Offer o2)
            {
                return (o1.getDistance() < o2.getDistance()) ? -1 : (o1.getDistance() > o2.getDistance()) ? 1 : 0;
            }
        });
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_searched_tools, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position)
    {
        //set dynamic value
        Tool tool = arrayList.get(position);

        //holder.txtNotifications.setVisibility(View.VISIBLE);

        if (tool != null)
        {
            holder.mainLayout.setTag(tool);
            holder.txtOfferName.setText(tool.getName());
            holder.txtRoomType.setText(tool.getCategory());
            holder.txtPrice.setText(tool.getCurrency() + " " + tool.getPrice());
            //holder.txtDistance.setText(formatNumber(offer.getDistance()));

            /*if(offer.getConversations() > 0)
            {
                holder.txtNotifications.setText(String.valueOf(offer.getConversations()));
                if(offer.getIsUnreadMsg() > 0)
                {
                    holder.imgMsgIcon.setImageResource(R.drawable.ic_mail_dark);
                    holder.txtNotifications.setTextColor(mContext.getResources().getColor(R.color.black));
                }
                else
                {
                    holder.imgMsgIcon.setImageResource(R.drawable.ic_mail_gray_cc);
                    holder.txtNotifications.setTextColor(mContext.getResources().getColor(R.color.color_gray_99));
                }
            }
            else {
                holder.txtNotifications.setVisibility(View.GONE);
                holder.imgMsgIcon.setVisibility(View.GONE);
            }*/

            Picasso.get()
                    .load(tool.getImage_path())
                    .resizeDimen(R.dimen.cell_offer_image_size, R.dimen.cell_offer_image_size)
                    .centerCrop()
                    .into(holder.propertyImage);
        }
    }

    private String formatNumber(double distance)
    {
        String unit = "m";
        if (distance > 1000)
        {
            distance /= 1000;
            unit = "km";
        }
        return String.format("%4.1f %s", distance, unit);
    }

    @Override
    public int getItemCount()
    {
        Log.d("arrraylist is ", "-------->" + arrayList.size());
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // each data item is just a string in this case
        private ItemClickListener clickListener;
        private RelativeLayout mainLayout;
        private TextView txtOfferName, txtRoomType, txtPrice, txtDistance, txtNotifications;
        private ImageView propertyImage, imgMsgIcon;

        public ViewHolder(View v)
        {
            super(v);
            //find view
            mainLayout = (RelativeLayout)v.findViewById(R.id.mainLayout);
            txtOfferName = (TextView) v.findViewById(R.id.offer_name);
            txtRoomType = (TextView) v.findViewById(R.id.txt_offer_type);
            txtPrice = (TextView) v.findViewById(R.id.txt_price);
            //txtDistance = (TextView) v.findViewById(R.id.txt_distance);
            //txtNotifications = (TextView)v.findViewById(R.id.txt_notification);
            //imgMsgIcon = (ImageView) v.findViewById(R.id.img_msg_marker);
            propertyImage = (ImageView) v.findViewById(R.id.ic_offer);
            mainLayout.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener)
        {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view)
        {
            //selected offer
            Tool offer = (Tool) view.getTag();
            int offerId = offer.getId();
            Intent intent = new Intent(mContext, SearchedToolDetailActivity.class);
            intent.putExtra("OfferID", offerId);
            //intent.putExtra("DISTANCE", offer.getDistance());

            AppDebugLog.println("Search OfferID" + offerId);
            mContext.startActivity(intent);

            //mActivity.finish();
        }

        public boolean onLongClick(View view)
        {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}

