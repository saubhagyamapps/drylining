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
import com.app.drylining.data.Offer;
import com.app.drylining.ui.AddedOfferDetailActivity;
import com.app.drylining.ui.SearchedOfferDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Panda on 20-05-2017.
 */

public class SearchedOffersAdapter extends RecyclerView.Adapter<SearchedOffersAdapter.ViewHolder> {
    public Context mContext;
    private ArrayList<Offer> arrayList;
    private ApplicationData appData;
    private CustomMainActivity mActivity;

    public SearchedOffersAdapter(Context context, ArrayList<Offer> arrayList) {
        this.arrayList = arrayList;
        this.mContext = context;
        appData = ApplicationData.getSharedInstance();

        this.mActivity = (CustomMainActivity) context;

        //sortOffersByDistance();
    }

    public void sortOffersByDistance() {
        Collections.sort(arrayList, new Comparator<Offer>() {
            @Override
            public int compare(Offer o1, Offer o2) {
                return (o1.getDistance() < o2.getDistance()) ? -1 : (o1.getDistance() > o2.getDistance()) ? 1 : 0;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_searched_offers, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            //set dynamic value
            Offer offer = arrayList.get(position);

            holder.txtNotifications.setVisibility(View.VISIBLE);

            if (offer != null) {
                holder.mainLayout.setTag(offer);
                holder.txtOfferName.setText(offer.getName());
                holder.txtRoomType.setText(offer.getCategory());
                holder.txtPrice.setText(offer.getCurrency() + " " + offer.getPrice());
                holder.txtCity.setText(offer.getPostCity());
                holder.txtInterested.setText(offer.getInterested());

                if (offer.getConversations() > 0) {
                    holder.txtNotifications.setText(String.valueOf(offer.getConversations()));
                    if (offer.getIsUnreadMsg() > 0) {
                        holder.imgMsgIcon.setImageResource(R.drawable.ic_mail_dark);
                        holder.txtNotifications.setTextColor(mContext.getResources().getColor(R.color.black));
                    } else {
                        holder.imgMsgIcon.setImageResource(R.drawable.ic_mail_gray_cc);
                        holder.txtNotifications.setTextColor(mContext.getResources().getColor(R.color.color_gray_99));
                    }
                } else {
                    holder.txtNotifications.setVisibility(View.GONE);
                    holder.imgMsgIcon.setVisibility(View.GONE);
                }

            /*Picasso.with(mContext)
                    .load(offer.getImage_path())
                    .resizeDimen(R.dimen.cell_offer_image_size, R.dimen.cell_offer_image_size)
                    .centerCrop()
                    .into(holder.propertyImage);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }
        return String.format("%4.1f %s", distance, unit);
    }

    @Override
    public int getItemCount() {
        try {
            Log.d("arrraylist is ", "-------->Size My Job Fragment" + arrayList.size());
            return arrayList.size();
        }catch (Exception e){
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private ItemClickListener clickListener;
        private RelativeLayout mainLayout;
        private TextView txtOfferName, txtRoomType, txtPrice, txtCity, txtInterested, txtNotifications;
        private ImageView propertyImage, imgMsgIcon;

        public ViewHolder(View v) {
            super(v);
            //find view
            mainLayout = (RelativeLayout) v.findViewById(R.id.mainLayout);
            txtOfferName = (TextView) v.findViewById(R.id.offer_name);
            txtRoomType = (TextView) v.findViewById(R.id.txt_offer_type);
            txtPrice = (TextView) v.findViewById(R.id.txt_price);
            txtCity = (TextView) v.findViewById(R.id.txt_city);
            txtInterested = (TextView) v.findViewById(R.id.txt_interest);
            txtNotifications = (TextView) v.findViewById(R.id.txt_notification);
            imgMsgIcon = (ImageView) v.findViewById(R.id.img_msg_marker);
            //propertyImage = (ImageView) v.findViewById(R.id.ic_offer);
            mainLayout.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            //selected offer
            Offer offer = (Offer) view.getTag();
            int offerId = offer.getId();

            if (offer.getmUserIdPostedJob().equals(appData.getUserId())) {
                Intent intent = new Intent(mContext, AddedOfferDetailActivity.class);
                intent.putExtra("OfferID", offerId);
                //intent.putExtra("DISTANCE", offer.getDistance());

                AppDebugLog.println("Search OfferID" + offerId);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, SearchedOfferDetailActivity.class);
                intent.putExtra("OfferID", offerId);
                //intent.putExtra("DISTANCE", offer.getDistance());

                AppDebugLog.println("Search OfferID" + offerId);
                mContext.startActivity(intent);
            }


            //mActivity.finish();
        }

        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}

