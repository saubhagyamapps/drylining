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
import com.app.drylining.custom.ItemClickListener;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Offer;
import com.app.drylining.ui.ActivityRemoveOffer;
import com.app.drylining.ui.AddedOfferDetailActivity;
import com.app.drylining.fragment.AddNewOfferFragment;
import com.app.drylining.ui.DashboardActivity;

import java.util.ArrayList;


public class AddedOffersAdapter extends RecyclerView.Adapter<AddedOffersAdapter.ViewHolder>
{
    private ArrayList<Offer> arrayList;
    public Context mContext;
    private ApplicationData appData;
    private DashboardActivity activity;
    private AddNewOfferFragment addNewOfferFragment;

    public AddedOffersAdapter(Context context, ArrayList<Offer> arrayList)
    {
        this.arrayList = arrayList;
        this.mContext = context;
        appData = ApplicationData.getSharedInstance();
        this.activity = (DashboardActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_added_offers, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position)
    {
        //set dynamic value
        Offer offer = arrayList.get(position);

        holder.txtNotifications.setVisibility(View.VISIBLE);

        if (offer != null)
        {
            holder.mainLayout.setTag(offer);
            holder.btnRemoveOffer.setTag(offer);
            holder.txtOfferName.setText(offer.getName());
            holder.txtRoomType.setText(offer.getCategory());
            holder.txtPrice.setText(offer.getCurrency() + " " + offer.getPrice());
            holder.txtInterest.setText(offer.getInterested());

            if(offer.getJobStatus() > 0)
            {
                holder.btnRemoveOffer.setVisibility(View.GONE);
            }

            if(offer.getConversations() > 0)
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
                    holder.txtNotifications.setTextColor(mContext.getResources().getColor(R.color.color_gray_cc));
                }
            }
            else
            {
                holder.txtNotifications.setVisibility(View.INVISIBLE);
                holder.imgMsgIcon.setVisibility(View.INVISIBLE);
            }

            /*Picasso.with(mContext)
                    .load(offer.getImage_path())
                    .resizeDimen(R.dimen.cell_offer_image_size, R.dimen.cell_offer_image_size)
                    .centerCrop()
                    .into(holder.propertyImage);*/
        }
    }

    @Override
    public int getItemCount()
    {
        Log.d("arrraylist is new ", "-------->" + arrayList.size());
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // each data item is just a string in this case
        private ItemClickListener clickListener;
        private RelativeLayout mainLayout;
        private TextView txtOfferName, txtRoomType, txtPrice, txtNotifications, txtInterest;
        private ImageView propertyImage, btnRemoveOffer, imgMsgIcon;


        public ViewHolder(View v)
        {
            super(v);
            //find view
            mainLayout = (RelativeLayout) v.findViewById(R.id.mainLayout);
            txtOfferName = (TextView) v.findViewById(R.id.offer_name);
            txtRoomType = (TextView) v.findViewById(R.id.txt_offer_type);
            txtPrice = (TextView) v.findViewById(R.id.txt_price);
            txtNotifications = (TextView) v.findViewById(R.id.txt_distance);
            imgMsgIcon = (ImageView) v.findViewById(R.id.img_msg_marker) ;
            txtInterest = (TextView)v.findViewById(R.id.txt_interest);
            //propertyImage = (ImageView) v.findViewById(R.id.ic_offer);
            mainLayout.setOnClickListener(this);

            btnRemoveOffer = (ImageView) v.findViewById(R.id.btn_delete);
            btnRemoveOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Offer offer = (Offer) v.getTag();
                    Intent removeIntent = new Intent(mContext, ActivityRemoveOffer.class);
                    removeIntent.putExtra("SELECTED_OFFER_ID", offer.getId());
                    mContext.startActivity(removeIntent);
                }
            });

        }

        public void setClickListener(ItemClickListener itemClickListener)
        {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view)
        {
            Offer offer = (Offer) view.getTag();
            int offerId = offer.getId();
            Intent intent = new Intent(mContext, AddedOfferDetailActivity.class);
            intent.putExtra("OfferID", offerId);
            intent.putExtra("BackActivityName", "MyJobAcivity");
            AppDebugLog.println("OfferID" + offerId);
            mContext.startActivity(intent);
            //activity.finish();
        }

        public boolean onLongClick(View view)
        {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}

