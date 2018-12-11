package com.app.drylining.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.adapter.SearchedOffersAdapter;
import com.app.drylining.adapter.ViewPagerAdapter;
import com.app.drylining.chat.ui.activity.DialogsActivity;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchedOfferDetailActivity extends CustomMainActivity implements RequestTaskDelegate
{
    private Toolbar toolbar;
    private ApplicationData appData;
    private ProgressDialog pdialog;
    private RecyclerView recyclerView;
    private ViewPager viewPager;
    private SearchedOffersAdapter adapter;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView txtOfferName, txtDistance, txtPrice, txtFavorites, txtDescription,
            txtParking, txtLocation, txtPhone, txtName,txtRoomType, txtInterestSent, txtSimilarOffers;
    private CheckBox cbFavourite;
    private LinearLayout interestedLayout;
    private Button btnBack, btnNewSearch, btnConversation, btnInterested, btnReviewOffer;
    private String[] images;

    private ScrollView mainScrollView;
    private RelativeLayout mainContainer;

    private double distance;
    private int cnt_conversations;
    private View actNext,actPrev;

    private JSONObject propObj;
    private int selectedOfferId;
    private Offer selectedOffer;
    private int offerId;
    private String userId;
    private boolean isInterested, isFavorited;

    private ArrayList<Offer> similarOfferList;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private LinearLayout employerReviewLayout, contactPersonReviewLayout;
    public RatingBar employer_ratingBar, contact_person_ratingBar;
    private TextView txtEmployerMarks, txtContactPersonMarks, txtEmployerDescription, txtContactPersonDescription;
    ImageView imageViewChat;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_offer_detail);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);
        imageViewChat=findViewById(R.id.imagesChat);
        mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BackStack", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Back", "Recent Job");
        editor.clear();// Storing string
        editor.apply();
        mainContainer = (RelativeLayout) findViewById(R.id.main_container);
        mainContainer.setVisibility(View.GONE);

        btnReviewOffer = (Button) findViewById(R.id.btn_review_offer);
        txtOfferName = (TextView) findViewById(R.id.offer_name);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        txtFavorites = (TextView) findViewById(R.id.txt_notification);
        txtDescription = (TextView) findViewById(R.id.txt_desc);
        txtParking = (TextView) findViewById(R.id.txt_parking);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtRoomType = (TextView) findViewById(R.id.txt_offer_type);

        btnBack = (Button) findViewById(R.id.btn_back);
        btnNewSearch = (Button) findViewById(R.id.btn_new_search);

        btnInterested = (Button) findViewById(R.id.btn_interested);

        employerReviewLayout = (LinearLayout) findViewById(R.id.employer_review);
        txtEmployerMarks = (TextView) findViewById(R.id.txt_employer_marks);
        txtEmployerDescription = (TextView) findViewById(R.id.txt_employer_review_description);
        employer_ratingBar = (RatingBar) findViewById(R.id.employer_ratingBar);

        contactPersonReviewLayout = (LinearLayout) findViewById(R.id.contact_person_review);
        txtContactPersonMarks = (TextView) findViewById(R.id.txt_contact_person_marks);
        txtContactPersonDescription = (TextView) findViewById(R.id.txt_contact_person_description);
        contact_person_ratingBar = (RatingBar) findViewById(R.id.contact_person_ratingBar);

        employer_ratingBar.setFocusable(false);
        contact_person_ratingBar.setFocusable(false);
        employer_ratingBar.setRating(3.5f);
        contact_person_ratingBar.setRating(3.2f);
        imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsActivity.start(SearchedOfferDetailActivity.this);
            }
        });
        employer_ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        contact_person_ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SearchedOfferDetailActivity.this.onBackPressed();
            }
        });

        btnNewSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(SearchedOfferDetailActivity.this, SearchNewOfferActivity.class));
                SearchedOfferDetailActivity.this.finish();
            }
        });

        btnReviewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchedOfferDetailActivity.this, JobReviewActivity.class);
                intent.putExtra("propertyInfo", propObj.toString());
                intent.putExtra("SELECTED_OFFER_ID", offerId);
                startActivity(intent);
                finish();
            }
        });


        btnInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                RequestTask requestTask = new RequestTask(AppConstant.NEW_INTERESTED, AppConstant.HttpRequestType.SendNewInterested);
                requestTask.delegate = SearchedOfferDetailActivity.this;

                String post_content = "&userId=" + userId + "&offerId=" + offerId;

                requestTask.execute(AppConstant.NEW_INTERESTED + "?" + post_content);
            }
        });

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
            startActivity(new Intent(this, DashboardActivity.class));

            finish();
        }
    }

    /*private void ConversationClicked()
    {
        Intent intent;
        if(cnt_conversations == 0)
            intent= new Intent(SearchedOfferDetailActivity.this, ActivityNewQuestion.class);
        else
            intent = new Intent(SearchedOfferDetailActivity.this, ActivityConversationsLessee.class);
        intent.putExtra("SELECTED_OFFER", selectedOffer);
        intent.putExtra("IS_FAVORITED", isFavorited);
        startActivity(intent);
        finish();
    }*/

    private void initialize()
    {
        //txtInterestSent.setVisibility(View.GONE);
        //interestedLayout.setVisibility(View.GONE);
        isInterested = false;
        isFavorited  = false;

        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        Intent intent = getIntent();
        offerId = intent.getIntExtra("OfferID",1);
        intent.getClass();

        userId = appData.getUserId();
      /*  if (appData.getUserType().equals("R")){
            btnInterested.setVisibility(View.GONE);
        }*/

        similarOfferList = new ArrayList<Offer>();
        try
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_PROPERTY_INFO_LESSEE, AppConstant.HttpRequestType.getPropertyInfo);
            requestTask.delegate = SearchedOfferDetailActivity.this;
            String params="propertyId=" + offerId + "&userId=" + userId + "&userType=" + appData.getUserType();

            Log.e("Offer Details Params", params);

            requestTask.execute(AppConstant.GET_PROPERTY_INFO_LESSEE + params);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // setViewPager();
        //setRecyclerView();
    }

    /*private void setRecyclerView()
    {
        if (adapter == null)
        {
            adapter = new SearchedOffersAdapter(this, similarOfferList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
    }*/

    private void setToolbar()
    {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent firstActivityIntent = new Intent(SearchedOfferDetailActivity.this, SearchActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SearchedOfferDetailActivity.this.startActivity(firstActivityIntent);
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
        //updateNavigationButtons();
    }

    public void nextClicked(View view) {
        viewPager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
        //updateNavigationButtons();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        AppDebugLog.println("Response is "+response);
        cancelProgressDialog();
        if(completedRequestType == AppConstant.HttpRequestType.getPropertyInfo)
        {
            JSONObject properties_response = null;
            try {
                properties_response = new JSONObject(response);
                String status = properties_response.getString("status");
                AppDebugLog.println("Properties status is " + status);
                JSONObject propertyObj = properties_response.getJSONObject("result");

                propObj = propertyObj;

                JSONObject propInfoObj = propertyObj.getJSONObject("info");
                // JSONObject propInfoObj = propInfo.getJSONObject(0);

                String mUserId = propInfoObj.getString("user_id");

                /*if(mUserId.equals(appData.getUserId())){
                    Intent intent = new Intent(getApplicationContext(), AddedOfferDetailActivity.class);
                    intent.putExtra("OfferID", offerId);
                    AppDebugLog.println("OfferID" + offerId);
                    startActivity(intent);
                }*/
                int propId = Integer.parseInt(propInfoObj.getString("id").toString());
                String propName = propInfoObj.getString("name");
                String propJobType = propInfoObj.getString("job_type");
                String price = propInfoObj.getString("price");
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
                String propWorkType = propInfoObj.getString("work_type");
                String propDesc = propInfoObj.getString("description");
                String mEmployName = propInfoObj.getString("contact_name");
                String mEmployCon = propInfoObj.getString("contact_mobile");


                int job_status = Integer.parseInt(propInfoObj.getString("status"));
                Double longitude = Double.parseDouble(propInfoObj.getString("longitude"));
                Double latitude = Double.parseDouble(propInfoObj.getString("latitude"));

                //distance = propInfoObj.getDouble("distance");

                JSONObject renterInfoObj = propertyObj.getJSONObject("renter");
                String renterName = renterInfoObj.getString("name");
                String renterPhone = renterInfoObj.getString("phone");

                txtOfferName.setText(propName);
                txtPrice.setText(propPrice);
                txtRoomType.setText(propJobType);
                txtLocation.setText("Location: " + propAddress);
                txtParking.setText("Work type: " + propWorkType);
                txtDescription.setText(propDesc);
                //txtDistance.setText(formatNumber(distance));
                txtName.setText(mEmployName);
                txtPhone.setText(mEmployCon);

                selectedOffer = new Offer(propId, propName, propJobType, price, 0/*distance*/);

                int interest = properties_response.getInt("isInterested");
                if(job_status == 0 && interest != 0)
                {
                    isInterested = true;
                    btnInterested.setText("You have sent interest");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));
                }
                if(job_status == 1){
                    isInterested = true;
                    btnInterested.setEnabled(false);
                    btnInterested.setText("You are progressed on");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));
                }
                if(job_status == 2){
                    isInterested = true;
                    btnInterested.setEnabled(false);
                    btnInterested.setText("You finished out");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));
                    btnReviewOffer.setVisibility(View.VISIBLE);
                }

                if(job_status == 3){
                    isInterested = true;
                    btnInterested.setEnabled(false);
                    btnInterested.setText("You finished out");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));
                    btnReviewOffer.setVisibility(View.GONE);
                    if(properties_response.getInt("count_employer_review") != 0)
                    {
                        employerReviewLayout.setVisibility(View.VISIBLE);
                        JSONObject employerReview = properties_response.getJSONObject("employer_review");
                        float employerMarks = Float.parseFloat(employerReview.getString("marks"));
                        employer_ratingBar.setRating(employerMarks);
                        txtEmployerMarks.setText(employerReview.getString("marks"));
                        txtEmployerDescription.setText(employerReview.getString("description"));
                        //unreadMsgClientId = lastUnreadObj.getInt("client_id");
                    }
                    else
                    {
                        employerReviewLayout.setVisibility(View.GONE);
                    }

                    if(properties_response.getInt("count_contact_person_review") != 0)
                    {
                        contactPersonReviewLayout.setVisibility(View.VISIBLE);
                        JSONObject contactPersonReview = properties_response.getJSONObject("contact_person_review");
                        float contactPersonMarks = Float.parseFloat(contactPersonReview.getString("marks"));
                        contact_person_ratingBar.setRating(contactPersonMarks);
                        txtContactPersonMarks.setText(contactPersonReview.getString("marks"));
                        txtContactPersonDescription.setText(contactPersonReview.getString("description"));
                        //readMsgClientId = lastReadObj.getInt("client_id");
                    }
                    else {
                        contactPersonReviewLayout.setVisibility(View.GONE);
                    }
                } else {
                    employerReviewLayout.setVisibility(View.GONE);
                    contactPersonReviewLayout.setVisibility(View.GONE);
                }

                String cnt_notifications = properties_response.getString("notifications");
                appData.setCntMessages(cnt_notifications);
                adminbar.setMessages(cnt_notifications);

                adminbar.setUserName(appData.getUserName());

                //setRecyclerView();

                //setViewPager();

                mainContainer.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(completedRequestType == AppConstant.HttpRequestType.SendNewInterested)
        {
            cancelProgressDialog();
            JSONObject newInterestedResultObj = null;
            try {
                newInterestedResultObj = new JSONObject(response);

                String status = newInterestedResultObj.getString("status");
                if(status.equals("success"))
                {
                    //interestedLayout.setVisibility(View.GONE);
                    //txtInterestSent.setVisibility(View.VISIBLE);
                    isInterested = true;
                    btnInterested.setText("You have sent interest");
                    btnInterested.setBackground(getResources().getDrawable(R.drawable.bg_success));

                    //mainScrollView.fullScroll(ScrollView.FOCUS_UP);
                    //mainScrollView.scrollTo(0, (int) txtInterestSent.getY());
                }
                else
                    Toast.makeText(SearchedOfferDetailActivity.this,
                            newInterestedResultObj.getString("msg"), Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
