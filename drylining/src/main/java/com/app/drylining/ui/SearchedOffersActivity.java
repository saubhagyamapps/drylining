package com.app.drylining.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
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
import android.graphics.Color;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.Util;
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
import com.app.drylining.network.SearchOfferParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchedOffersActivity extends CustomMainActivity implements RequestTaskDelegate
{
    private static final String TAG = "SearchedOffersActivity";
    private ApplicationData appData;
    private Toolbar toolbar;
    private SearchedOffersActivity activity;
    private RecyclerView recyclerView;
    private SearchedOffersAdapter adapter;
    private ArrayList<Offer> offerList, filteredList;
    private ArrayList<Offer> offerListTmp;
    private ProgressDialog pdialog;
    private Button btnNewSearch;
    private TextView txtNoRecentSearch;

    private String preSearchCity, preSearchRange, preSearchProperty, preSearchRoom, preSearchGarage, PreSearchCityLocation;
    private int preSearchMinPrice, preSearchMaxPrice;
    private double preSearchRangeValue = 0;

    private SearchOfferParameters preSearchParams;

    private LinearLayout mLinearScroll;

    int offersPerPage = 10;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_offers);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        txtNoRecentSearch = (TextView)findViewById(R.id.txtNoRecentSearch);

        btnNewSearch = (Button) findViewById(R.id.btn_new_search);
        btnNewSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OnNewSearchClicked();
            }
        });

        mLinearScroll = (LinearLayout) findViewById(R.id.linear_scroll);

        AppDebugLog.println("stored location :" + ApplicationData.getSharedInstance().getCurrentLocation());

        // if (ApplicationData.getSharedInstance().getCurrentLocation().equalsIgnoreCase("0,0")) {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                AppDebugLog.println("Current Longitude:" + location.getLongitude());
                AppDebugLog.println("Current latitude:" + location.getLatitude());
                // if (ApplicationData.getSharedInstance().getCurrentLocation().equalsIgnoreCase("0,0")) {
                ApplicationData.getSharedInstance().setCurrentLocation(location.getLatitude() + "," + location.getLongitude());
                // Toast.makeText(getBaseContext(), "Current Longitude:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                // }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
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
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        txtNoRecentSearch.setVisibility(View.GONE);

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        activity = this;
        offerList = new ArrayList<Offer>();
        filteredList = new ArrayList<Offer>();

        sendSearchRequest();
    }

    private SearchOfferParameters getPreSearchParams()
    {
        preSearchCity = appData.getPreCityName();
        preSearchRange = appData.getPreRange();
        preSearchProperty = appData.getPreProperty();
        preSearchRange = appData.getPreRange();
        preSearchRoom = appData.getPreRoom();
        preSearchGarage = appData.getPreGarage();
        preSearchMinPrice = appData.getPreMinPrice();
        preSearchMaxPrice = appData.getPreMaxPrice();
        PreSearchCityLocation = appData.getPreCityLocation();

        preSearchRangeValue = getRangeValue(preSearchRange);

        return new SearchOfferParameters(preSearchCity, preSearchProperty,
                preSearchGarage, preSearchMinPrice, preSearchMaxPrice, PreSearchCityLocation);
    }

    private double getRangeValue(String searchRange)
    {
        int i;
        String[] array = getResources().getStringArray(R.array.search_range);
        for( i = 0 ; i<array.length ; i++)
        {
            if (array[i].equals(searchRange))
                break;
        }
        switch (i)
        {
            case 0:
                return 1000;
            case 1:
                return 5000;
            case 2:
                return 15000;
            case 3:
                return 30000;
            case 4:
                return 50000;
            default:
                return 0;
        }
    }

    private void sendSearchRequest()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_LAST_SEARCH, AppConstant.HttpRequestType.GetLastSearch);
            requestTask.delegate = SearchedOffersActivity.this;

            requestTask.execute(AppConstant.GET_LAST_SEARCH + "?" + "&senderId=" + appData.getUserId());
            Log.e(TAG, "My job Search list Api : "+AppConstant.GET_LAST_SEARCH + "?" + "&senderId=" + appData.getUserId() );
        }
        else
        {

        }
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
        if (pdialog != null && !activity.isFinishing()) {
            pdialog.dismiss();
            pdialog = null;
        }

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

        if(offerList.size() == 0)
            txtNoRecentSearch.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void setToolbar()
    {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        adminbar = new AdminBar(this);

        adminbar.setUserId(appData.getUserId());

        adminPanel = new AdminPanel(this);
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
            showConfirmExitAlert(this, "", "Are you sure to exit this app?", this);
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject properties_response = null;
        try
        {
            properties_response = new JSONObject(response);
            String status = properties_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            int result_count = properties_response.getInt("count");

            if(result_count > 0)
            {
                JSONArray properties_array = properties_response.getJSONArray("result");
                JSONArray msgNumArray = properties_response.getJSONArray("msgs");
                for (int i = 0; i < properties_array.length(); i++) {
                    JSONObject property = properties_array.getJSONObject(i);
                    int id = Integer.parseInt(property.getString("id").toString());
                    String name = property.getString("name");
                    String room_type = property.getString("room_type");
                    String price = property.getString("price").toString();

                    Double longitude = Double.parseDouble(property.getString("longitude").toString());
                    Double latitude = Double.parseDouble(property.getString("latitude").toString());
                    String image_path = property.getString("image_path");

                    double distance = property.optDouble("distance");

                    Offer offer = new Offer(id, name, longitude, latitude, price, room_type, image_path,"","");
                    offer.setDistance(distance);

                    offer.setConversations(msgNumArray.getInt(i));

                    offerList.add(offer);
                }
            }

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);
            adminbar.setMessages(cnt_notifications);

            adminbar.setUserName(appData.getUserName());

            setRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void addPageButtons()
    {
        int size = offerList.size() / offersPerPage;
        int rem = offerList.size() % offersPerPage;
        if(rem != 0)
            size+=1;

        for (int j = 0; j < size; j++)
        {
            final int k;
            k = j;
            final Button btnPage = new Button(SearchedOffersActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 2, 2, 2);
            btnPage.setTextColor(Color.WHITE);
            btnPage.setTextSize(26.0f);
            btnPage.setId(j);
            btnPage.setText(String.valueOf(j + 1));
            mLinearScroll.addView(btnPage, lp);

            btnPage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // TODO Auto-generated method stub
                    /**
                     * add arraylist item into list
                     */
                    addItem(k);
                }
            });
        }
    }

    private void addItem(int i)
    {
        // TODO Auto-generated method stub
        offerListTmp.clear();
        i = i * offersPerPage;

        /**
         * fill temp array list to set on page change
         */
        for (int j = 0; j < offersPerPage; j++)
        {
            offerListTmp.add(j, offerList.get(i));
            i = i + 1;
        }

        // set view
        //setView();
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

    private void OnNewSearchClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            startActivity(new Intent(SearchedOffersActivity.this, SearchNewOfferActivity.class));
        }
        else{
            Util.showNoConnectionDialog(this);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu){

//        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.action_logout){


            Intent loginIntent=new Intent(getApplicationContext(),MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
