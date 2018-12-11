package com.app.drylining.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.adapter.SearchedToolsAdapter;
import com.app.drylining.adapter.ViewPagerAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Tool;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchedToolDetailActivity extends CustomMainActivity implements RequestTaskDelegate
{
    private Toolbar toolbar;
    private ApplicationData appData;
    private ProgressDialog pdialog;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private SearchedToolsAdapter adapter;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView txtOfferName, txtDistance, txtPrice, txtFavorites, txtDescription,
            txtParking, txtLocation, txtPhone, txtName,txtRoomType, txtInterestSent, txtSimilarOffers;
    private CheckBox cbFavourite;
    private LinearLayout interestedLayout;
    private Button btnBack, btnNewSearch, btnConversation, btnInterested, btnInterestedYes, btnInterestedNo;
    private String[] images;
    private ArrayList<String> propertySliderImages = new ArrayList<String>();

    private ScrollView mainScrollView;
    private RelativeLayout mainContainer;

    private double distance;
    private int cnt_conversations;
    private View actNext,actPrev;

    private Tool selectedOffer;
    private int offerId;
    private String userId;
    private boolean isInterested, isFavorited;

    private ArrayList<Tool> similarOfferList;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_tool_detail);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);

        mainContainer = (RelativeLayout) findViewById(R.id.main_container);
        mainContainer.setVisibility(View.GONE);

        //recyclerView = (RecyclerView) findViewById(R.id.another_recyclerView);
        viewPager = (ViewPager) findViewById(R.id.pager);
        txtOfferName = (TextView) findViewById(R.id.offer_name);
        txtDistance = (TextView) findViewById(R.id.txt_distance);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        txtFavorites = (TextView) findViewById(R.id.txt_notification);
        txtDescription = (TextView) findViewById(R.id.txt_desc);
        txtParking = (TextView) findViewById(R.id.txt_parking);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtRoomType = (TextView) findViewById(R.id.txt_offer_type);

        //txtSimilarOffers = (TextView) findViewById(R.id.lbl_similar);

        btnBack = (Button) findViewById(R.id.btn_back);
        btnNewSearch = (Button) findViewById(R.id.btn_new_search);

        //txtInterestSent = (TextView) findViewById(R.id.msg_sent_interest);
        //interestedLayout = (LinearLayout) findViewById(R.id.interestedLayout);
        //btnInterested = (Button) findViewById(R.id.btn_interested);
        //btnInterestedYes = (Button) findViewById(R.id.btn_notify_yes);
        //btnInterestedNo = (Button) findViewById(R.id.btn_notify_no);

        //btnConversation= (Button)findViewById(R.id.btn_send_question);

        actNext=findViewById(R.id.btn_next);
        actPrev=(findViewById(R.id.btn_previous));

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SearchedToolDetailActivity.this.onBackPressed();
            }
        });

        btnNewSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SearchedToolDetailActivity.this, SearchNewToolActivity.class));
                SearchedToolDetailActivity.this.finish();
            }
        });

        /*btnConversation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ConversationClicked();
            }
        });*/


        /*btnInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInterested)
                    interestedLayout.setVisibility(View.VISIBLE);
            }
        });

        btnInterestedYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                RequestTask requestTask = new RequestTask(AppConstant.NEW_INTERESTED, AppConstant.HttpRequestType.SendNewInterested);
                requestTask.delegate = SearchedToolDetailActivity.this;

                String post_content = "&userId=" + userId + "&offerId=" + offerId;

                requestTask.execute(AppConstant.NEW_INTERESTED + "?" + post_content);
            }
        });

        btnInterestedNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interestedLayout.setVisibility(View.GONE);
            }
        });*/

        /*cbFavourite = (CheckBox) findViewById(R.id.cb_favorite);
        cbFavourite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                FavoriteClicked(cb.isChecked());
            }
        });*/

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone((String)txtPhone.getText());

            }
        });
    }

    private void dialContactPhone(final String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
        //startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        initialize();
    }

    @Override
    protected void onPause() {
        super.onPause();

        similarOfferList.clear();
        adapter = null;
    }

    @Override
    public void onBackPressed()
    {
        if(adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else {
            super.onBackPressed();
            //startActivity(new Intent(this, SearchedOffersActivity.class));
            finish();
        }
    }

    /*private void ConversationClicked()
    {
        Intent intent;
        if(cnt_conversations == 0)
            intent= new Intent(SearchedToolDetailActivity.this, ActivityNewQuestion.class);
        else
            intent = new Intent(SearchedToolDetailActivity.this, ActivityConversationsLessee.class);
        intent.putExtra("SELECTED_OFFER", selectedOffer);
        intent.putExtra("IS_FAVORITED", isFavorited);
        startActivity(intent);
        finish();
    }*/

    /*private void FavoriteClicked(boolean isChecked)
    {
        String option ;
        if(isChecked)
            option = "favorite";
        else
            option = "unfavorite";

        showProgressDialog();
        RequestTask requestTask = new RequestTask(AppConstant.FAVORITE, AppConstant.HttpRequestType.FavoriteRequest);
        requestTask.delegate = SearchedToolDetailActivity.this;

        String params = "?propertyId=" + offerId + "&userId=" + userId + "&option=" + option;

        Log.e("Offer Details Params",params);

        requestTask.execute(AppConstant.FAVORITE + params);
    }*/

    private void initialize()
    {
        //txtInterestSent.setVisibility(View.GONE);
        //interestedLayout.setVisibility(View.GONE);
        isInterested = false;
        isFavorited   = false;

        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        Intent intent = getIntent();
        offerId = intent.getIntExtra("OfferID",1);
        intent.getClass();

        userId = appData.getUserId();

        similarOfferList = new ArrayList<Tool>();
        try
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_TOOL_INFO_LESSEE, AppConstant.HttpRequestType.getToolInfo);
            requestTask.delegate = SearchedToolDetailActivity.this;
            String params="propertyId=" + offerId + "&userId=" + userId + "&userType=" + appData.getUserType();

            Log.e("Offer Details Params",params);

            requestTask.execute(AppConstant.GET_TOOL_INFO_LESSEE + params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        //setViewPager();
        //setRecyclerView();
    }

    /*private void setRecyclerView()
    {
        if (adapter == null)
        {
            adapter = new SearchedToolsAdapter(this, similarOfferList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
    }*/

    private void setViewPager()
    {
        if (viewPagerAdapter == null)
        {
            // Pass results to ViewPagerAdapter Class
            viewPagerAdapter = new ViewPagerAdapter(this, images);
            // Binds the Adapter to the ViewPager
            viewPager.setAdapter(viewPagerAdapter);
        }

        updateNavigationButtons();
        viewPagerAdapter.notifyDataSetChanged();
    }

    public void updateNavigationButtons()
    {
        if(viewPager.getAdapter().getCount()<=1){
            actNext.setVisibility(View.INVISIBLE);actPrev.setVisibility(View.INVISIBLE);
        }else {

            actNext.setVisibility(View.VISIBLE);actPrev.setVisibility(View.VISIBLE);
            if(viewPager.getCurrentItem()==0){
                actPrev.setVisibility(View.INVISIBLE);
            }
            if(viewPager.getCurrentItem()==viewPager.getAdapter().getCount()-1)actNext.setVisibility(View.INVISIBLE);
        }
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
                Intent firstActivityIntent = new Intent(SearchedToolDetailActivity.this, SearchActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SearchedToolDetailActivity.this.startActivity(firstActivityIntent);
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

        adminbar.imgOpenArrow.setVisibility(View.GONE);
        recreate();
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public void previousClicked(View view) {
        viewPager.setCurrentItem(getItem(-1), true);
        updateNavigationButtons();
    }

    public void nextClicked(View view) {
        viewPager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
        updateNavigationButtons();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        AppDebugLog.println("Response is "+response);
        cancelProgressDialog();
        if(completedRequestType == AppConstant.HttpRequestType.getToolInfo)
        {
            JSONObject properties_response = null;
            try {
                properties_response = new JSONObject(response);
                String status = properties_response.getString("status");
                AppDebugLog.println("Properties status is " + status);
                JSONObject propertyObj = properties_response.getJSONObject("result");

                JSONArray propImages = propertyObj.getJSONArray("images");
                // images = new String[propImages.length()];
                for (int image = 0; image < propImages.length(); image++) {
                    JSONObject imageObj = propImages.getJSONObject(image);
                    AppDebugLog.println("First Image " + imageObj.getString("image_path"));
                    propertySliderImages.add(imageObj.getString("image_path"));
                }
                Object[] mStringArray = propertySliderImages.toArray();
                images = new String[mStringArray.length];
                for (int i = 0; i < mStringArray.length; i++) {
                    Log.d("string is", (String) mStringArray[i]);
                    images[i] = (String) mStringArray[i];
                }

                JSONObject propInfoObj = propertyObj.getJSONObject("info");
                // JSONObject propInfoObj = propInfo.getJSONObject(0);
                int propId = Integer.parseInt(propInfoObj.getString("id").toString());
                String propName = propInfoObj.getString("name");
                String propToolType = propInfoObj.getString("tool_type");
                long price = Long.parseLong(propInfoObj.getString("price"));
                String currency = propInfoObj.getString("currency_type");
                String currency_sign = "$";
                switch (currency){
                    case "USD": currency_sign = "$";
                        break;
                    case "EUR": currency_sign = "€";
                        break;
                    case "Pound": currency_sign = "£";
                        break;
                }

                String propPrice = currency_sign + " " + propInfoObj.getString("price");
                String propAddress = propInfoObj.getString("postCity") + ", " + propInfoObj.getString("streetName");
                //+ " " + propInfoObj.getString("unitCode");
                //String propGarbage = propInfoObj.getString("job_type");
                String propDesc = propInfoObj.getString("description");
                Double longitude = Double.parseDouble(propInfoObj.getString("longitude"));
                Double latitude = Double.parseDouble(propInfoObj.getString("latitude"));

                //distance = propInfoObj.getDouble("distance");

                JSONObject renterInfoObj = propertyObj.getJSONObject("renter");
                String renterName = renterInfoObj.getString("name");
                String renterPhone = renterInfoObj.getString("phone");

                /*String cityLocation = appData.getPreCityLocation();
                Double currentLatitude = 0.0;
                Double currentLongitude = 0.0;
                if (cityLocation.length() > 0) {
                    currentLatitude = Double.parseDouble(cityLocation.split(",")[0]);
                    currentLongitude = Double.parseDouble(cityLocation.split(",")[1]);
                }

                double distance = SphericalUtil.computeDistanceBetween(new LatLng(latitude, longitude), new LatLng(currentLatitude, currentLongitude));
*/
                txtOfferName.setText(propName);
                txtPrice.setText(propPrice);
                txtRoomType.setText(propToolType);
                txtLocation.setText("Location: " + propAddress);
                txtParking.setText("Tool type: " + propToolType);
                txtDescription.setText(propDesc);
                //txtDistance.setText(formatNumber(distance));
                txtName.setText(renterName);
                txtPhone.setText(renterPhone);

                selectedOffer = new Tool(propId, propName, propToolType, price, 0/*distance*/);

                /*cnt_conversations = properties_response.getInt("conversation_count");
                if (cnt_conversations != 0) {
                    btnConversation.setText("Conversation (" + cnt_conversations + ")");
                }

                int interest = properties_response.getInt("isInterested");*/
                /*if(interest != 0)
                {
                    isInterested = true;
                    btnInterested.setText("You have sent interest");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));
                }

                int isFavorite = properties_response.getInt("isFavorite");
                if(isFavorite != 0)
                {
                    cbFavourite.setChecked(true);
                    isFavorited = true;
                }*/

                /*int count_favorites = properties_response.getInt("count_favorites");
                txtFavorites.setText(String.valueOf(count_favorites));

                int numSimilarOffers = properties_response.getInt("similar_offer_count");
                if(numSimilarOffers > 0)
                {
                    JSONArray similarOffersArray = properties_response.getJSONArray("similar_offers");
                    for (int i = 0; i < similarOffersArray.length(); i++)
                    {
                        JSONObject similarOfferObj = similarOffersArray.getJSONObject(i);
                        int id_1 = Integer.parseInt(similarOfferObj.getString("id").toString());
                        String name_1 = similarOfferObj.getString("name");
                        String room_type_1 = similarOfferObj.getString("room_type");
                        Long price_1 = Long.parseLong(similarOfferObj.getString("price").toString());
                        Double longitude_1 = Double.parseDouble(similarOfferObj.getString("longitude").toString());
                        Double latitude_1 = Double.parseDouble(similarOfferObj.getString("latitude").toString());
                        String image_path_1 = similarOfferObj.getString("image_path");

                        double distance_1 = similarOfferObj.optDouble("distance");

                        Tool similarOffer = new Tool(id_1, name_1, longitude_1, latitude_1, price_1, room_type_1, image_path_1);
                        similarOffer.setDistance(distance_1);

                        similarOfferList.add(similarOffer);
                    }
                }
                else
                {
                    //txtSimilarOffers.setVisibility(View.GONE);
                }

                String cnt_notifications = properties_response.getString("notifications");
                appData.setCntMessages(cnt_notifications);
                adminbar.setMessages(cnt_notifications);*/

                adminbar.setUserName(appData.getUserName());

                //setRecyclerView();

                setViewPager();

                mainContainer.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*else if(completedRequestType == AppConstant.HttpRequestType.SendNewInterested)
        {
            cancelProgressDialog();
            JSONObject newInterestedResultObj = null;
            try {
                newInterestedResultObj = new JSONObject(response);

                String status = newInterestedResultObj.getString("status");
                if(status.equals("success"))
                {
                    interestedLayout.setVisibility(View.GONE);
                    //txtInterestSent.setVisibility(View.VISIBLE);
                    isInterested = true;
                    btnInterested.setText("You have sent interest");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));

                    //mainScrollView.fullScroll(ScrollView.FOCUS_UP);
                    //mainScrollView.scrollTo(0, (int) txtInterestSent.getY());
                }
                else
                    Toast.makeText(SearchedToolDetailActivity.this,
                            newInterestedResultObj.getString("msg"), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else if(completedRequestType == AppConstant.HttpRequestType.FavoriteRequest)
        {
            cancelProgressDialog();
            try {
                JSONObject favoriteResultObj = new JSONObject(response);
                if(favoriteResultObj.getString("status").equals("success"))
                {
                    int curFavorites = Integer.parseInt(txtFavorites.getText().toString());
                    switch (favoriteResultObj.getString("option"))
                    {
                        case "favorite":
                            curFavorites++;
                            txtFavorites.setText(String.valueOf(curFavorites));
                            isFavorited = true;
                            break;
                        case "unfavorite":
                            curFavorites--;
                            txtFavorites.setText(String.valueOf(curFavorites));
                            isFavorited = false;
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    private String formatNumber(double distance)
    {
        String unit = "m";
        if (distance > 1000)
        {
            distance /= 1000;
            unit = "km";
        }
        return String.format("%4.1f %s", distance, unit);
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
        if (!isFinishing() && pdialog == null) {
            pdialog = ProgressDialog.show(this, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog() {
        if (pdialog != null && !isFinishing()) {
            pdialog.dismiss();
            pdialog = null;
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

}
