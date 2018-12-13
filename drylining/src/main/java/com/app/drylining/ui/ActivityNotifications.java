package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.adapter.NotificationAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Conversation;
import com.app.drylining.model.MSGCountModel;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.retrofit.ApiClient;
import com.app.drylining.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.drylining.ui.DashboardActivity.toolbar_txt_messages_new;

/**
 * Created by Panda on 6/15/2017.
 */

public class ActivityNotifications extends CustomMainActivity implements RequestTaskDelegate {
    private static final String TAG = "ActivityNotifications";
    private String userId, userType;
    private TextView btnRemoveAll;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ApplicationData appData;
    private ArrayList<Conversation> conList;
    private NotificationAdapter adapter;
    private ProgressDialog pdialog;
    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;
    private TextView txtNotifications;
    private Animation animShow, animHide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout = (LinearLayout) findViewById(R.id.adminBar);

        txtNotifications = (TextView) findViewById(R.id.txt_header);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

 /*       btnRemoveAll = (TextView) findViewById(R.id.txt_remove_all);

        btnRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllClicked();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = null;

        if (conList != null)
            conList.clear();

        initialize();
    }

    private void countApiCall() {
        appData = ApplicationData.getSharedInstance();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MSGCountModel> countModelCall = apiService.getCountBit("0", appData.getUserId());
        countModelCall.enqueue(new Callback<MSGCountModel>() {
            @Override
            public void onResponse(Call<MSGCountModel> call, Response<MSGCountModel> response) {
                try {
                    toolbar_txt_messages_new.setText("");
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Call<MSGCountModel> call, Throwable t) {

            }
        });

    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);
        countApiCall();
        conList = new ArrayList<Conversation>();

        sendGetNotificationsRequest();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firstActivityIntent = null;
                String userType = appData.getUserType();
                if (userType.equals("R")) {
                    firstActivityIntent = new Intent(ActivityNotifications.this, DashboardActivity.class);
                } else if (userType.equals("L")) {
                    firstActivityIntent = new Intent(ActivityNotifications.this, SearchActivity.class);
                } else
                    return;

                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityNotifications.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);

        adminPanel = new AdminPanel(this);
    }

    @Override
    public void openAdminPanel() {
        adminLayout.setVisibility(View.VISIBLE);
        adminLayout.startAnimation(animShow);

        new CountDownTimer(AppConstant.ADMIN_BAR_OPEN_TIME, 100) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                adminbar.imgOpenArrow.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void closeAdminPanel() {
        adminLayout.setVisibility(View.GONE);
        adminLayout.startAnimation(animHide);

        adminbar.imgOpenArrow.setVisibility(View.GONE);
        recreate();
    }

    @Override
    public void onBackPressed() {
        if (adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else {
            super.onBackPressed();
            if (appData.getUserType().equals("R"))
                startActivity(new Intent(this, DashboardActivity.class));
            else if (appData.getUserType().equals("L"))
                startActivity(new Intent(this, SearchActivity.class));
            finish();
        }
    }

    private void removeAllClicked() {
        showProgressDialog();

        RequestTask requestTask = new RequestTask(AppConstant.REMOVE_NOTIFICATIONS, AppConstant.HttpRequestType.RemoveNotifications);

        requestTask.delegate = ActivityNotifications.this;

        userId = appData.getUserId();

        String post_content = "userId=" + userId;

        requestTask.execute(AppConstant.REMOVE_NOTIFICATIONS + "?" + post_content);
    }

    private void sendGetNotificationsRequest() {
        showProgressDialog();

        RequestTask requestTask = new RequestTask(AppConstant.GET_NOTIFICATIONS, AppConstant.HttpRequestType.GetNotifications);

        requestTask.delegate = ActivityNotifications.this;

        userId = appData.getUserId();

        userType = appData.getUserType();

        String post_content = "userId=" + userId + "&userType=" + "L";

        requestTask.execute(AppConstant.GET_NOTIFICATIONS + "?" + post_content);
//        Log.e(TAG, "sendGetNotificationsRequest: "+requestTask.execute(AppConstant.GET_NOTIFICATIONS + "?" + post_content));
    }

    private void setRecyclerView() {
        if (adapter == null) {
            adapter = new NotificationAdapter(this, conList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }


    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        JSONObject con_response = null;

        try {
            if (completedRequestType == AppConstant.HttpRequestType.GetNotifications) {
                con_response = new JSONObject(response);

                Log.d("Property Response", response.toString());

                String status = con_response.getString("status");
                AppDebugLog.println("Properties status is " + status);

                JSONArray cons_array = con_response.getJSONArray("messages");
                JSONArray time_array = con_response.getJSONArray("times");
                for (int i = 0; i < cons_array.length(); i++) {
                    JSONObject con = cons_array.getJSONObject(i);
                    int id = con.getInt("id");
                    int sender = con.getInt("sender_id");
                    String senderName = con.getString("sender_name");

                    int receiver = con.getInt("receiver_id");

                    int offer = con.getInt("offer_id");
                    String content = con.getString("content");

                    String message_type = con.getString("message_type");

                    String message_state = con.getString("isRead");
                    String notification_id = con.getString("notification_id");
                    String interest_id = con.getString("interest_id");
                    String confirm_id = con.getString("confirm_id");

                    String newRead = con.getString("read");

                    JSONObject timeObj = time_array.getJSONObject(i);
                    long agoTime = timeObj.getLong("time");

                    Conversation conversation = new Conversation(id, sender, senderName, content, message_type, message_state, calcTime(agoTime), notification_id,newRead,interest_id,confirm_id);
                    conversation.setOfferId(offer);

                    conList.add(conversation);
                }

                String cnt_notifications = String.valueOf(conList.size());
                //txtNotifications.setText("Notifications (" + cnt_notifications + ")");
                appData.setCntMessages(cnt_notifications);

                adminbar.setMessages(cnt_notifications);
                adminbar.setUserName(appData.getUserName());
                adminbar.setUserId(appData.getUserId());
            } else if (completedRequestType == AppConstant.HttpRequestType.RemoveNotifications) {
                conList.clear();

                txtNotifications.setText("Notifications");

                adminbar.setMessages(String.valueOf(0));

                appData.setCntMessages(String.valueOf(0));
            }
            setRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String calcTime(long agoTime) {
        String res;
        int days = (int) (agoTime / (3600 * 24));
        if (days > 0) {
            res = String.valueOf(days) + " days";
            return res;
        }

        int hours = (int) (agoTime / 3600);
        if (hours > 0) {
            res = String.valueOf(hours) + " h ";
            return res;
        }


        int reminder = (int) (agoTime - hours * 3600);
        int mins = reminder / 60;

        res = String.valueOf(mins) + " min";
        return res;
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
        if (!this.isFinishing() && pdialog == null) {
            pdialog = ProgressDialog.show(this, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog() {
        if (pdialog != null && !this.isFinishing()) {
            pdialog.dismiss();
            pdialog = null;
        }
    }
}
