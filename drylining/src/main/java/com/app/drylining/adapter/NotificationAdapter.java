package com.app.drylining.adapter;

import android.app.ProgressDialog;
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
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Conversation;
import com.app.drylining.data.Offer;
import com.app.drylining.model.NotificationReadModel;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.retrofit.ApiClient;
import com.app.drylining.retrofit.ApiInterface;
import com.app.drylining.ui.ActivityConversationsLessee;
import com.app.drylining.ui.ActivityConversationsRenter;
import com.app.drylining.ui.ActivityNotifications;
import com.app.drylining.ui.AddedOfferDetailActivity;
import com.app.drylining.ui.SearchedOfferDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Panda on 6/17/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> implements RequestTaskDelegate {
    private static final String TAG = "NotificationAdapter";
    public ActivityNotifications mContext;
    private ApplicationData appData;
    private ProgressDialog pdialog;
    private ArrayList<Conversation> arrayList;
    private int selectedClientId, selectedOfferId;                //use in click process
    private Offer selectedOffer;
    private String selectedNotificationType, selectedNotificationState;

    public NotificationAdapter(ActivityNotifications context, ArrayList<Conversation> conList) {
        this.mContext = context;
        this.arrayList = conList;

        appData = ApplicationData.getSharedInstance();
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_notification, parent, false);
        NotificationAdapter.ViewHolder vh = new NotificationAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, int position) {
        Conversation con = arrayList.get(position);
        if (con != null) {
           /* if (!appData.getUserId().equals(String.valueOf(con.getSenderId()))) {
*/
                holder.mainLayout.setTag(con);

                holder.txtTimeAgo.setText(con.getTimeAgo());

                holder.txtContent.setText(con.getContent());
                if (con.getNewRead().equals("0")) {
                    holder.mainLayout.setBackgroundResource(R.color.light_gray);
                } else {
                    holder.mainLayout.setBackgroundResource(R.color.app_bg_color);
                }
                String type = con.getMessageType();
                if (type.equals("LtoR") || type.equals("RtoL")) {
                    holder.iconImg.setImageResource(R.drawable.ic_mail);

                    String state = con.getMessageState();
                    if (state.equals("unread")) {
                        holder.mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.primary_color));
                    } else if (state.equals("read")) {
                        holder.mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_gray_99));
                    }
                } else if (type.equals("interest") || type.equals("confirm_interest")) {
                    //holder.iconImg.setImageResource(R.drawable.ic_thumb_white);
                    //holder.mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_gray_4d));
                } else if (type.equals("favorite")) {
                    holder.iconImg.setImageResource(R.drawable.ic_favourite_white);
                    holder.mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_success));
                } else if (type.equals("criteria")) {
                    holder.iconImg.setImageResource(R.drawable.ic_star_white);
                    holder.mainLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_success));
                }

            //}
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        AppDebugLog.println("Response is " + response);
        JSONObject request_response = null;
        try {
            request_response = new JSONObject(response);

            if (completedRequestType == AppConstant.HttpRequestType.UpdateNotification) {
                String status = request_response.getString("status");
                if (status.equals("success"))
                    clickProc(selectedNotificationType);
            } else if (completedRequestType == AppConstant.HttpRequestType.getPropertyInfo) {
                if (selectedNotificationType.equals("LtoR")) {
                    JSONObject propObj = request_response.getJSONObject("result");
                    JSONObject offerObj = propObj.getJSONObject("info");

                    String propName = offerObj.getString("name");
                    String proproomType = offerObj.getString("room_type");
                    String propPrice = offerObj.getString("price");

                    Offer selectedOffer = new Offer(selectedOfferId, propName, proproomType, propPrice, 0);

                    Intent intent = new Intent(mContext, ActivityConversationsRenter.class);
                    intent.putExtra("SELECTED_OFFER", selectedOffer);
                    intent.putExtra("CLIENT_ID", selectedClientId);
                    mContext.startActivity(intent);
                    mContext.finish();
                } else if (selectedNotificationType.equals("RtoL")) {
                    boolean isFavorited = false;
                    JSONObject propObj = request_response.getJSONObject("result");
                    JSONObject offerObj = propObj.getJSONObject("info");

                    int propId = Integer.parseInt(offerObj.getString("id").toString());
                    String propName = offerObj.getString("name");
                    String proproomType = offerObj.getString("room_type");
                    String price = offerObj.getString("price");
                    String propPrice = offerObj.getString("price") + " EUR/month";
                    double distance = offerObj.getDouble("distance");
                    selectedOffer = new Offer(propId, propName, proproomType, price, distance);

                    int isFavorite = request_response.getInt("isFavorite");
                    if (isFavorite != 0)
                        isFavorited = true;

                    Intent intent = new Intent(mContext, ActivityConversationsLessee.class);
                    intent.putExtra("SELECTED_OFFER", selectedOffer);
                    intent.putExtra("IS_FAVORITED", isFavorited);
                    mContext.startActivity(intent);
                    mContext.finish();
                }
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

    private void showProgressDialog() {
        if (pdialog == null) {
            pdialog = ProgressDialog.show(mContext, mContext.getResources().getString(R.string.alert_title_loading),
                    mContext.getResources().getString(R.string.alert_body_wait));
        }
    }

    public void cancelProgressDialog() {
        if (pdialog != null) {
            pdialog.dismiss();
            pdialog = null;
        }
    }

    private void notificationClicked(Conversation tag) {
        Log.e(TAG, "notificationClicked: ");

        selectedClientId = tag.getSenderId();
        selectedOfferId = tag.getOfferId();
        selectedNotificationType = tag.getMessageType();
        selectedNotificationState = tag.getMessageState();

        if (selectedNotificationState.equals("unread")) {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.UPDATE_NOTIFICATIONS, AppConstant.HttpRequestType.UpdateNotification);
            requestTask.delegate = NotificationAdapter.this;
            requestTask.execute(AppConstant.UPDATE_NOTIFICATIONS + tag.getId());
        } else if (selectedNotificationState.equals("read")) {
            clickProc(selectedNotificationType);
        }
        readNotifaction(tag.getNotification_id(), tag.getInterest_id(), tag.getConfirm_id());
    }

    private void readNotifaction(String notification_id, String interest_id, String confirm_id) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        final Call<NotificationReadModel> readModelCall = apiService.getNotificationBit(notification_id, interest_id, confirm_id);
        readModelCall.enqueue(new Callback<NotificationReadModel>() {
            @Override
            public void onResponse(Call<NotificationReadModel> call, Response<NotificationReadModel> response) {
                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<NotificationReadModel> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
            }
        });
    }

    private void clickProc(String messageType) {
        if (appData.getUserId().equals(String.valueOf(selectedClientId))) {
            Intent detailIntent = new Intent(mContext, AddedOfferDetailActivity.class);
            detailIntent.putExtra("OfferID", selectedOfferId);
            detailIntent.putExtra("BackActivityName", "Notifation");

            mContext.startActivity(detailIntent);
        } else {
            if (messageType.equals("LtoR")) {
                showProgressDialog();
                RequestTask requestTask = new RequestTask(AppConstant.GET_PROPERTY_INFO_RENTER, AppConstant.HttpRequestType.getPropertyInfo);
                requestTask.delegate = NotificationAdapter.this;
                requestTask.execute(AppConstant.GET_PROPERTY_INFO_RENTER + selectedOfferId);

            } else if (messageType.equals("RtoL")) {
                showProgressDialog();
                RequestTask requestTask = new RequestTask(AppConstant.GET_PROPERTY_INFO_LESSEE, AppConstant.HttpRequestType.getPropertyInfo);
                requestTask.delegate = NotificationAdapter.this;
                String params = "propertyId=" + selectedOfferId + "&userId=" + appData.getUserId();
                requestTask.execute(AppConstant.GET_PROPERTY_INFO_LESSEE + params);
            } else if (messageType.equals("interest") || messageType.equals("favorite")) {
                Intent detailIntent = new Intent(mContext, SearchedOfferDetailActivity.class);
                detailIntent.putExtra("OfferID", selectedOfferId);
                detailIntent.putExtra("BackActivityName", "Notifation");
                mContext.startActivity(detailIntent);
                //mContext.finish();
            } else if (messageType.equals("confirm_interest") || messageType.equals("criteria")) {
                Intent intent = new Intent(mContext, AddedOfferDetailActivity.class);
                intent.putExtra("OfferID", selectedOfferId);
                intent.putExtra("BackActivityName", "Notifation");
                mContext.startActivity(intent);
                //mContext.finish();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private RelativeLayout mainLayout;
        private TextView txtContent, txtTimeAgo;
        private ImageView iconImg;

        public ViewHolder(View v) {
            super(v);
            //find view
            mainLayout = (RelativeLayout) v.findViewById(R.id.main_layout);

            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationClicked((Conversation) v.getTag());
                }
            });

            txtContent = (TextView) v.findViewById(R.id.txt_message);
            txtTimeAgo = (TextView) v.findViewById(R.id.txt_time_ago);
            iconImg = (ImageView) v.findViewById(R.id.img_icon);
        }
    }
}
