package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Panda on 3/30/2017.
 */

public class SearchedResultActivity extends CustomMainActivity implements RequestTaskDelegate
{
    private ApplicationData appData;
    private Toolbar toolbar;
    private SearchedResultActivity activity;
    private RecyclerView recyclerView;
    private SearchedOffersAdapter adapter;
    private ArrayList<Offer> offerList;
    private ProgressDialog pdialog;
    private TextView txtSearchResult;
    private Button btnNewSearch, btnEditSearch;

    private SearchOfferParameters searchParams;
    private double searchRange = 0;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        txtSearchResult = (TextView) findViewById(R.id.txt_search_result);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        btnNewSearch = (Button) findViewById(R.id.btn_new_search);
        btnNewSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                OnNewSearchClicked();
            }
        });

        btnEditSearch = (Button) findViewById(R.id.btn_edit_search);
        btnEditSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                OnEditSearchClicked();
            }
        });

        searchParams = (SearchOfferParameters)getIntent().getSerializableExtra("searchParam");

        searchRange = getRangeValue(searchParams.getSearchRange());     //get real range value from string

        initialize();
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


    private void OnEditSearchClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            Intent intentEdit = new Intent(this, SearchNewOfferActivity.class);
            intentEdit.putExtra("searchParam", searchParams);
            startActivity(intentEdit);
            finish();
        }
        else
        {
            Util.showNoConnectionDialog(this);
        }
    }

    private void OnNewSearchClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            startActivity(new Intent(this, SearchNewOfferActivity.class));
            finish();
        }
        else{
            Util.showNoConnectionDialog(this);
        }
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        activity = this;
        offerList = new ArrayList<Offer>();
        sendSearchRequest();
    }

    private void sendSearchRequest()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.SEARCH_OFFER, AppConstant.HttpRequestType.searchOfferRequest);
            requestTask.delegate = SearchedResultActivity.this;
            searchParams.setRangeInMeter((long)getRangeValue(searchParams.getSearchRange()));
            String request = searchParams.getPostContent();

            Log.e("Search request",request);

            Log.e("SearchResult Acicivty", "sendSearchRequest: "+ AppConstant.SEARCH_OFFER+"?"+request + "&senderId=" + appData.getUserId());
            requestTask.execute(AppConstant.SEARCH_OFFER+"?"+request + "&senderId=" + appData.getUserId());
        }
        else
        {
            Toast.makeText(this, "Please connect to internet...", Toast.LENGTH_SHORT).show();
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
            txtSearchResult.setText("Searched results " + "(" + offerList.size() + " matches)");
            adapter = new SearchedOffersAdapter(this, offerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
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
                Intent firstActivityIntent = new Intent(SearchedResultActivity.this, SearchActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SearchedResultActivity.this.startActivity(firstActivityIntent);
            }
        });

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

        new CountDownTimer(AppConstant.ADMIN_BAR_OPEN_TIME, 100) {

            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                adminbar.imgOpenArrow.setVisibility(View.VISIBLE);
            }
        }.start();
        recreate();
    }

    @Override
    public void onBackPressed()
    {
        if(adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else
            super.onBackPressed();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        JSONObject properties_response = null;

        Log.e("Search Response",response);

        try
        {
            properties_response = new JSONObject(response);
            String status = properties_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            JSONArray properties_array = properties_response.getJSONArray("result");
            JSONArray msgNumArray = properties_response.getJSONArray("msgs");
            JSONArray isUnReadMsgArray = properties_response.getJSONArray("isUnReadMsgs");

            for (int i = 0; i < properties_array.length(); i++)
            {
                JSONObject property = properties_array.getJSONObject(i);
                int id = Integer.parseInt(property.getString("id").toString());
                String name = property.getString("name");
                String room_type = property.getString("room_type");
                String price = property.getString("price").toString();
                String currency_type = property.getString("currency_type");
                Double longitude = 0.0;//Double.parseDouble(property.getString("longitude").toString());
                Double latitude = 0.0;//Double.parseDouble(property.getString("latitude").toString());
                String image_path = property.getString("image_path");
                double distance=property.optDouble("distance");
                String city = property.getString("postcity");
                String user_id = property.getString("user_id");

                int job_state = Integer.parseInt(property.getString("status"));
                String interested = "";
                if (job_state == 1){
                    interested = "Progress by " + property.getString("worker");
                } else {
                    interested  = "Interested: " + property.getString("countInterest");
                }

                Offer offer = new Offer(id, name, longitude, latitude, price, room_type, image_path,user_id,"");
                offer.setDistance(distance);
                offer.setCurrency(currency_type);
                offer.setPostCity(city);
                offer.setInterested(interested);
                offer.setInterested(user_id);

                offer.setConversations(msgNumArray.getInt(i));
                offer.setIsUnreadMsg(isUnReadMsgArray.getInt(i));

                offerList.add(offer);
            }

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);
            adminbar.setMessages(cnt_notifications);

            adminbar.setUserName(appData.getUserName());

            setRecyclerView();
        } catch (Exception e) {
            Log.e("Search res",e.getMessage());
        }
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

}
