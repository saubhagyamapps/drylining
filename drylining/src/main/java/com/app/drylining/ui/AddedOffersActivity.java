package com.app.drylining.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.adapter.AddedOffersAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


public class AddedOffersActivity extends CustomMainActivity implements RequestTaskDelegate
{
    private ApplicationData appData;
    private Toolbar toolbar;
    private AddedOffersActivity activity;
    private RecyclerView recyclerView;
    private AddedOffersAdapter adapter;
    private ArrayList<Offer> offerList;
    private ProgressDialog pdialog;
    private String location;
    private TextView msg_success, txt_none_added_offer;
    private Button btnAddNewOffer;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private CountDownTimer lTimer;

    private boolean backPressedOneTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_offers);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        msg_success = (TextView) findViewById(R.id.msg_success);
        txt_none_added_offer = (TextView) findViewById(R.id.txt_none_offers);

        btnAddNewOffer = (Button) findViewById(R.id.btn_add_offer);
        btnAddNewOffer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnNewOfferClicked();
            }
        });

        AppDebugLog.println("stored location :" + ApplicationData.getSharedInstance().getCurrentLocation());
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                // Called when a new location is found by the network location provider.
                AppDebugLog.println("Current Longitude:" + location.getLongitude());
                AppDebugLog.println("Current latitude:" + location.getLatitude());

                ApplicationData.getSharedInstance().setCurrentLocation(location.getLatitude() + "," + location.getLongitude());

                if (adapter != null)
                {
                    adapter.notifyDataSetChanged();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider)
            {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        initialize();
    }

    private void initialize()
    {
        msg_success.setVisibility(View.GONE);
        Intent intent = getIntent();
        try
        {
            String isp = intent.getStringExtra("offerAdded");
            if(isp.equals("1"))
                msg_success.setVisibility(View.VISIBLE);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            msg_success.setVisibility(View.GONE);
        }

        if(intent.getIntExtra("OFFER_REMOVED", 0) == 1)
        {
            msg_success.setVisibility(View.VISIBLE);
            msg_success.setText("YOUR OFFER HAS BEEN REMOVED");
        }

        if(intent.getIntExtra("offerUpdated", 0) == 1)
        {
            msg_success.setVisibility(View.VISIBLE);
            msg_success.setText("YOUR OFFER HAS BEEN MODIFIED");
        }

        appData = ApplicationData.getSharedInstance();
        activity = this;
        location = AppConstant.NULL_STRING;
        offerList = new ArrayList<Offer>();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        offerList.clear();

        sendGetProperties();
    }

    private void sendGetProperties()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_PROPERTIES, AppConstant.HttpRequestType.getProperties);
            requestTask.delegate = AddedOffersActivity.this;
            String userId = appData.getUserId();
            requestTask.execute(AppConstant.GET_PROPERTIES + userId);
        }
    }


    private void setRecyclerView()
    {
        if (adapter == null)
        {
            AppDebugLog.println("location in setRecyclerView:" + this.location);
            adapter = new AddedOffersAdapter(this, offerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();

        if(offerList.size() == 0)
        {
            txt_none_added_offer.setVisibility(View.VISIBLE);
        }
        else{
            txt_none_added_offer.setVisibility(View.GONE);
        }
    }

    private void setToolbar()
    {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        adminbar = new AdminBar(this);

        adminPanel = new AdminPanel(this);
    }

    @Override
    public void openAdminPanel()
    {
        adminLayout.setVisibility(View.VISIBLE);
        adminLayout.startAnimation( animShow );

        lTimer = new CountDownTimer(AppConstant.ADMIN_BAR_OPEN_TIME, 100) {

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

    private void showProgressDialog()
    {
        if (!activity.isFinishing() && pdialog == null)
        {
            pdialog = ProgressDialog.show(activity, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog()
    {
        if (pdialog != null && !activity.isFinishing())
        {
            pdialog.dismiss();
            pdialog = null;
        }

    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject properties_response = null;
        try
        {
            properties_response = new JSONObject(response);

            Log.e("Property Response",response.toString());

            String status = properties_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            JSONArray properties_array = properties_response.getJSONArray("result");
            JSONArray msgNumArray = properties_response.getJSONArray("msgs");
            JSONArray isUnReadMsgArray = properties_response.getJSONArray("isUnReadMsgs");

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);
            adminbar.setMessages(cnt_notifications);

            adminbar.setUserName(appData.getUserName());
            adminbar.setUserId(appData.getUserId());

            for (int i = 0; i < properties_array.length(); i++)
            {
                JSONObject property = properties_array.getJSONObject(i);
                int id = Integer.parseInt(property.getString("id").toString());
                String name = property.getString("name");
                String room_type = property.getString("room_type");
                String price = property.getString("price").toString();
                Double longitude = Double.parseDouble(property.getString("longitude").toString());
                Double latitude = Double.parseDouble(property.getString("latitude").toString());
                String image_path = property.getString("image_path");

                Offer offer = new Offer(id, name, longitude, latitude, price, room_type, image_path,"","");
                offer.setConversations(msgNumArray.getInt(i));
                offer.setIsUnreadMsg(isUnReadMsgArray.getInt(i));
                offerList.add(offer);
            }
            setRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        if(adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else
        {
            showConfirmExitAlert(this, "", "Are you sure to exit this app?", this);
        }
    }

    @Override
    protected void onPause()
    {
        adapter = null;
        super.onPause();
    }

    @Override
    public void timeOut()
    {

    }

    @Override
    public void codeError(int code) {

    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record) {

    }

    public void OnNewOfferClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            startActivity(new Intent(this, AddNewOfferActivity.class));
            AddedOffersActivity.this.finish();
        }
        else{
            Util.showNoConnectionDialog(this);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
       // getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.action_logout)
        {
            Intent loginIntent=new Intent(this,MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void showConfirmExitAlert(final Context context, String title, String message, final Activity contextToFinish)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(R.layout.dialog_custom_double_button);
        dialog.setTitle(title);

        TextView textView = (TextView) dialog.findViewById(R.id.dialogTxt);
        textView.setText(message);

        dialog.findViewById(R.id.dialogBtnYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                contextToFinish.finish();
            }
        });

        dialog.findViewById(R.id.dialogBtnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
