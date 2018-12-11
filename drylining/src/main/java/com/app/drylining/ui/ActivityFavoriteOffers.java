package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.adapter.SearchedOffersAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Panda on 6/26/2017.
 */

public class ActivityFavoriteOffers extends CustomMainActivity implements RequestTaskDelegate
{
    private ApplicationData appData;
    private Toolbar toolbar;
    private ActivityFavoriteOffers mActivity;
    private RecyclerView recyclerView;
    private SearchedOffersAdapter adapter;
    private ArrayList<Offer> offerList;
    private ProgressDialog pdialog;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_favorite_offers);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initialize();
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        mActivity = this;
        offerList = new ArrayList<Offer>();
        sendGetRequest();
    }

    private void setToolbar()
    {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent firstActivityIntent = new Intent(ActivityFavoriteOffers.this, SearchActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityFavoriteOffers.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);

        adminPanel = new AdminPanel(this);
    }

    private void sendGetRequest()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_FAVORITE_OFFERS, AppConstant.HttpRequestType.GetFavorites);
            requestTask.delegate = ActivityFavoriteOffers.this;

            requestTask.execute(AppConstant.GET_FAVORITE_OFFERS + "?" + "userId=" + appData.getUserId());
        }
        else
        {
            Toast.makeText(this, "Please connect to internet...", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressDialog()
    {
        if (!mActivity.isFinishing() && pdialog == null)
        {
            pdialog = ProgressDialog.show(mActivity, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }
    private void cancelProgressDialog()
    {
        if (pdialog != null && !mActivity.isFinishing())
        {
            pdialog.dismiss();
            pdialog = null;
        }

    }

    @Override
    public void openAdminPanel()
    {
        adminLayout.setVisibility(View.VISIBLE);
        adminLayout.startAnimation( animShow );

        new CountDownTimer(AppConstant.ADMIN_BAR_OPEN_TIME, 100) {

            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                adminbar.imgOpenArrow.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void closeAdminPanel()
    {
        adminLayout.setVisibility(View.GONE);
        adminLayout.startAnimation( animHide );

        adminbar.imgOpenArrow.setVisibility(View.GONE);
        recreate();
    }

    @Override
    public void onBackPressed()
    {
        if(adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else
        {
            super.onBackPressed();
            startActivity(new Intent(this, SearchedOffersActivity.class));
            finish();
        }
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject favorites_response = null;

        Log.e("Favorite Response",response);

        try
        {
            favorites_response = new JSONObject(response);
            String status = favorites_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            JSONArray properties_array = favorites_response.getJSONArray("result");
            JSONArray msgNumArray = favorites_response.getJSONArray("msgs");
            for (int i = 0; i < properties_array.length(); i++)
            {
                JSONObject property = properties_array.getJSONObject(i);
                int id = Integer.parseInt(property.getString("id").toString());
                String name = property.getString("name");
                String room_type = property.getString("room_type");
                String price = property.getString("price").toString();
                Double longitude = 1.0;//Double.parseDouble(property.getString("longitude").toString());
                Double latitude = 0.0;//Double.parseDouble(property.getString("latitude").toString());
                String image_path = property.getString("image_path");
                double distance=property.optDouble("distance");

                Offer offer = new Offer(id, name, longitude, latitude, price, room_type, image_path);
                offer.setDistance(distance);
                offer.setConversations(msgNumArray.getInt(i));

                offerList.add(offer);
            }

            String cnt_notifications = favorites_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);

            adminbar.setMessages(cnt_notifications);
            adminbar.setUserName(appData.getUserName());
            adminbar.setUserId(appData.getUserId());

            sortOffersByDistance();

            setRecyclerView();
        } catch (Exception e) {
            Log.e("Search res",e.getMessage());
        }
    }

    public void sortOffersByDistance()
    {
        Collections.sort(offerList, new Comparator<Offer>()
        {
            @Override
            public int compare(Offer o1, Offer o2)
            {
                return (o1.getDistance() < o2.getDistance()) ? -1 : (o1.getDistance() > o2.getDistance()) ? 1 : 0;
            }
        });
    }

    private void setRecyclerView()
    {
        if (adapter == null)
        {
            adapter = new SearchedOffersAdapter(this, offerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void timeOut()
    {

    }

    @Override
    public void codeError(int code)
    {

    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record)
    {

    }
}
