package com.app.drylining.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Conversation;

import java.util.ArrayList;

/**
 * Created by Panda on 6/4/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>
{
    private ArrayList<Conversation> arrayList;
    public Context mContext;
    private ApplicationData appData;
    private String clientName;

    public ConversationAdapter(Context context, ArrayList<Conversation> conList, String client)
    {
        this.mContext = context;
        this.arrayList= conList;
        this.clientName = client;
        appData = ApplicationData.getSharedInstance();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_conversation, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ConversationAdapter.ViewHolder holder, int position)
    {
        Conversation con = arrayList.get(position);
        if(con != null)
        {
            holder.mainLayout.setTag(con);
            holder.txtContent.setText(con.getContent());

            holder.txtRenterName.setVisibility(View.VISIBLE);
            holder.txtYou.setText("You");

            if(appData.getUserId().equals(String.valueOf(con.getSenderId())))
            {
                holder.label_client.setVisibility(View.GONE);
            }
            else
            {
                holder.txtYou.setText("");
                holder.txtRenterName.setText(clientName);
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        private RelativeLayout mainLayout;
        private LinearLayout label_client;
        private TextView txtRenterName, txtYou;
        private AppCompatTextView txtContent;

        public ViewHolder(View v)
        {
            super(v);
            //find view
            mainLayout = (RelativeLayout) v.findViewById(R.id.cell_layout);
            label_client = (LinearLayout) v.findViewById(R.id.label_client);

            txtRenterName = (TextView) v.findViewById(R.id.txtRenterName);
            txtYou = (TextView) v.findViewById(R.id.txtYou);
            txtContent = (AppCompatTextView) v.findViewById(R.id.txt_content);
        }
    }
}
