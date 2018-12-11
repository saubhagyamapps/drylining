package com.app.drylining.adapter;

import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.Favorite;
import com.app.drylining.custom.FavoriteListener;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.AddedOfferDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> implements RequestTaskDelegate
{

    private ProgressDialog pdialog;
    private ArrayList<Favorite> arrayList;
    public AddedOfferDetailActivity mContext;
    private RecyclerView recyclerView;

    private FavoriteListener listener;

    public FavoriteAdapter(ArrayList<Favorite> arrayList, AddedOfferDetailActivity context, FavoriteListener listener)
    {
        this.arrayList = arrayList;
        this.mContext  = context;
        this.listener = listener;
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_favorite, parent, false);
        FavoriteAdapter.ViewHolder vh = new FavoriteAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, int position)
    {
        Favorite favorite = arrayList.get(position);
        if(favorite != null)
        {
        //    holder.btnConfirm.setTag(favorite);
            holder.btnConfirm.setTag(favorite);
            holder.txtName.setText(favorite.getSenderName());
            holder.txtPhone.setText(favorite.getSenderPhone());
            if(favorite.getIsConfirmed().equals("yes"))
            {
                holder.btnConfirm.setText("Confirmed");
                holder.btnConfirm.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                holder.btnConfirm.setTextColor(mContext.getResources().getColor(R.color.gray_txt_color));
            }
            else
                holder.btnConfirm.setText("Confirm");
        }
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtName, txtPhone, btnConfirm;
        private LinearLayout mainLayout;

        public ViewHolder(View v)
        {
            super(v);
            //find view
            mainLayout = (LinearLayout) v.findViewById(R.id.main_layout) ;
            txtName = (TextView) v.findViewById(R.id.txt_person_name);
            txtPhone = (TextView) v.findViewById(R.id.txt_person_phone);
            btnConfirm = (TextView) v.findViewById(R.id.txt_is_confirmed);
            /*recyclerView = (RecyclerView) v.findViewById(R.id.interests_recyclerView);*/

            /*mainLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Favorite tagFavorite = (Favorite)v.getTag();
                    String isConfirmed = tagFavorite.getIsConfirmed();
                    if(isConfirmed.equals("no"))
                    {
                        btnConfirm.setText("Confirmed");
                        btnConfirm.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                        btnConfirm.setTextColor(mContext.getResources().getColor(R.color.gray_txt_color));

                        RequestTask requestTask = new RequestTask(AppConstant.NEW_INTERESTED, AppConstant.HttpRequestType.SendNewInterested);
                        requestTask.delegate = (AddedOfferDetailActivity)mContext;
                        String post_content = "&userId=" + tagFavorite.getSenderId() + "&offerId=" + tagFavorite.getOfferId();
                        requestTask.execute(AppConstant.NEW_INTERESTED + "?" + post_content);
                    }
                }
            });*/

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  btnConfirmClicked((Favorite)v.getTag());
                                                  }
                                          }

            );


        }
    }

    private void btnConfirmClicked(Favorite tagFavorite)
    {
        RequestTask requestTask = new RequestTask(AppConstant.SET_OFFER_CONFIRM, AppConstant.HttpRequestType.SetOfferConfirm);
        requestTask.delegate = FavoriteAdapter.this;
        String post_content = "&userId=" + tagFavorite.getSenderId() + "&offerId=" + tagFavorite.getOfferId();
        requestTask.execute(AppConstant.SET_OFFER_CONFIRM + "?" + post_content);

    }



    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        AppDebugLog.println("Response is "+response);
        JSONObject request_response = null;
        try
        {
            request_response = new JSONObject(response);
            String status = request_response.getString("status");
            if(status.equals("success")) {
                if(listener != null)
                    listener.onClickHidden();
            }




        } catch (JSONException e) {
                e.printStackTrace();
        }
    }

    @Override
    public void timeOut() {

    }

    @Override
    public void codeError(int code) {

    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record) {

    }

    public void cancelProgressDialog()
    {
        if (pdialog != null)
        {
            pdialog.dismiss();
            pdialog = null;
        }
    }
}
