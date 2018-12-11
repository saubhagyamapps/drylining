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
import com.app.drylining.data.Tool;
import com.app.drylining.ui.ActivityRemoveTool;
import com.app.drylining.ui.AddedToolDetailActivity;
import com.app.drylining.ui.DashboardActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AddedToolsAdapter extends RecyclerView.Adapter<AddedToolsAdapter.ViewHolder>
{
    private ArrayList<Tool> arrayList;
    public Context mContext;
    private ApplicationData appData;
    private DashboardActivity activity;

    public AddedToolsAdapter(Context context, ArrayList<Tool> arrayList)
    {
        this.arrayList = arrayList;
        this.mContext = context;
        appData = ApplicationData.getSharedInstance();
        this.activity = (DashboardActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_added_tools, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, int position)
    {
        //set dynamic value
        Tool tool = arrayList.get(position);

        holder.txtNotifications.setVisibility(View.VISIBLE);

        if (tool != null)
        {
            holder.mainLayout.setTag(tool);
            holder.btnRemoveOffer.setTag(tool);
            holder.txtOfferName.setText(tool.getName());
            holder.txtRoomType.setText(tool.getCategory());
            holder.txtPrice.setText(tool.getCurrency() + " " + tool.getPrice());

            if(tool.getPosterId().equals(appData.getUserId())){
                holder.btnRemoveOffer.setVisibility(View.VISIBLE);
            } else {
                holder.btnRemoveOffer.setVisibility(View.GONE);
            }

            if(tool.getConversations() > 0)
            {
                holder.txtNotifications.setText(String.valueOf(tool.getConversations()));

                if(tool.getIsUnreadMsg() > 0)
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

            Picasso.get()
                    .load(tool.getImage_path())
                    .resizeDimen(R.dimen.cell_offer_image_size, R.dimen.cell_offer_image_size)
                    .centerCrop()
                    .into(holder.propertyImage);
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
        private TextView txtOfferName, txtRoomType, txtPrice, txtNotifications;
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
            propertyImage = (ImageView) v.findViewById(R.id.ic_offer);
            mainLayout.setOnClickListener(this);

            btnRemoveOffer = (ImageView) v.findViewById(R.id.btn_delete);

            btnRemoveOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Tool tool = (Tool) v.getTag();
                    Intent removeIntent = new Intent(mContext, ActivityRemoveTool.class);
                    removeIntent.putExtra("SELECTED_TOOL_ID", tool.getId());
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
            Tool tool = (Tool) view.getTag();
            int toolId = tool.getId();
            Intent intent = new Intent(mContext, AddedToolDetailActivity.class);
            intent.putExtra("ToolID", toolId);
            AppDebugLog.println("ToolID" + toolId);
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

