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
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.adapter.FavoriteAdapter;
import com.app.drylining.adapter.ViewPagerAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.custom.FavoriteListener;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Favorite;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddedOfferDetailActivity extends CustomMainActivity implements RequestTaskDelegate, FavoriteListener {
    private static final String TAG = "AddedOfferDetailActivit";
    public RatingBar employer_ratingBar, contact_person_ratingBar;
    private Toolbar toolbar;
    private ApplicationData appData;
    private ProgressDialog pdialog;
    private ArrayList<Favorite> favoriteList;
    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private String[] images;
    private ArrayList<String> propertySliderImages = new ArrayList<String>();
    private TextView txtOfferName, txtPrice, btnModifyOffer, btnFinishOffer, btnRemoveOffer, txtReceivedQuestions, txtInterests, txtDescription,
            txtParking, txtLocation, txtPhone, txtName, txtRoomType, txtNumFavorites;
    private TextView readQuestions, numReadQuestions, unreadQuestions, numUnreadQuestions;
    private ImageView imgRemove;
    private LinearLayout layoutUnread, layoutRead;
    private RelativeLayout mainContainer;
    private Button btnBack, btnReviewOffer;
    private View actNext, actPrev;
    private JSONObject propObj;
    private int selectedOfferId;
    private String mBackActivityName;
    private Offer selectedOffer;
    private int unreadMsgClientId, readMsgClientId;
    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;
    private Animation animShow, animHide;
    private LinearLayout employerReviewLayout, contactPersonReviewLayout;
    private TextView txtEmployerMarks, txtContactPersonMarks, txtEmployerDescription, txtContactPersonDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_offer_detail);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout = (LinearLayout) findViewById(R.id.adminBar);

        mainContainer = (RelativeLayout) findViewById(R.id.main_container);
        mainContainer.setVisibility(View.GONE);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("BackStack", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Back", "Post Job"); // Storing string
        editor.clear();
        editor.apply(); // commit changes
        btnBack = (Button) findViewById(R.id.btn_back);

        txtOfferName = (TextView) findViewById(R.id.offer_name);
        txtRoomType = (TextView) findViewById(R.id.txt_offer_type);
        btnModifyOffer = (TextView) findViewById(R.id.btn_modify_offer);
        btnFinishOffer = (TextView) findViewById(R.id.btn_finish_offer);
        btnReviewOffer = (Button) findViewById(R.id.btn_review_offer);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        txtInterests = (TextView) findViewById(R.id.lbl_interested_people);
        //txtReceivedQuestions = (TextView) findViewById(R.id.lbl_received_questions);
        imgRemove = (ImageView) findViewById(R.id.img_remove);
        btnRemoveOffer = (TextView) findViewById(R.id.txt_remove_offer);

        //viewPager = (ViewPager) findViewById(R.id.pager);
        txtDescription = (TextView) findViewById(R.id.txt_desc);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtParking = (TextView) findViewById(R.id.txt_parking);

        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtName = (TextView) findViewById(R.id.txt_name);

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

        txtNumFavorites = (TextView) findViewById(R.id.txt_favorites);
        recyclerView = (RecyclerView) findViewById(R.id.interests_recyclerView);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddedOfferDetailActivity.this.onBackPressed();
            }
        });

        btnModifyOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddedOfferDetailActivity.this, ActivityModifyOffer.class);
                intent.putExtra("propertyInfo", propObj.toString());
                intent.putExtra("SELECTED_OFFER_ID", selectedOfferId);
                startActivity(intent);
                AddedOfferDetailActivity.this.finish();
            }
        });

        btnFinishOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishJob();
            }
        });

        btnReviewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddedOfferDetailActivity.this, JobReviewActivity.class);
                intent.putExtra("propertyInfo", propObj.toString());
                intent.putExtra("SELECTED_OFFER_ID", selectedOfferId);
                startActivity(intent);
                finish();
            }
        });

        btnRemoveOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent removeIntent = new Intent(AddedOfferDetailActivity.this, ActivityRemoveOffer.class);
                removeIntent.putExtra("SELECTED_OFFER_ID", selectedOfferId);
                startActivity(removeIntent);
            }
        });

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone((String) txtPhone.getText());
            }
        });


        favoriteList = new ArrayList<Favorite>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);

        favoriteList.clear();

        showProgressDialog();
        selectedOfferId = getIntent().getIntExtra("OfferID", 1);
        mBackActivityName = getIntent().getStringExtra("BackActivityName");
        Log.e(TAG, "initialize:----> " + mBackActivityName);
        RequestTask requestTask = new RequestTask(AppConstant.GET_PROPERTY_INFO_RENTER, AppConstant.HttpRequestType.getPropertyInfo);
        requestTask.delegate = AddedOfferDetailActivity.this;
        String userId = appData.getUserId();
        requestTask.execute(AppConstant.GET_PROPERTY_INFO_RENTER + selectedOfferId + "&senderId=" + userId);
    }

    private void finishJob() {
        showProgressDialog();
        RequestTask requestTask = new RequestTask(AppConstant.SET_FINISH_JOB, AppConstant.HttpRequestType.FinishJobRequest);
        requestTask.delegate = AddedOfferDetailActivity.this;
        requestTask.execute(AppConstant.SET_FINISH_JOB + selectedOfferId);
    }

    private void setRecyclerView() {
        if (adapter == null) {
            adapter = new FavoriteAdapter(favoriteList, this, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }


    private void dialContactPhone(final String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
        //startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firstActivityIntent = new Intent(AddedOfferDetailActivity.this, DashboardActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                AddedOfferDetailActivity.this.startActivity(firstActivityIntent);
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
        //TransitionManager.beginDelayedTransition(adminLayout);
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
            if (mBackActivityName.equals("Notifation")) {
                finish();
            } else if (mBackActivityName.equals("SearchAcivity")) {
                finish();
            } else {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }
        }
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
        cancelProgressDialog();
        AppDebugLog.println("Response is " + response);
        JSONObject properties_response = null;
        try {
            properties_response = new JSONObject(response);
            String status = properties_response.getString("status");
            if (completedRequestType == AppConstant.HttpRequestType.getPropertyInfo) {
                AppDebugLog.println("Properties status is " + status);
                JSONObject propertyObj = properties_response.getJSONObject("result");

                propObj = propertyObj;

                JSONObject propInfoObj = propertyObj.getJSONObject("info");
                String propName = propInfoObj.getString("name");
                String propJobType = propInfoObj.getString("job_type");

                String currency = propInfoObj.getString("currency_type");
                String currency_sign = "$";
                switch (currency) {
                    case "USD":
                        currency_sign = "$";
                        break;
                    case "EUR":
                        currency_sign = "€";
                        break;
                    case "Pound":
                        currency_sign = "£";
                        break;
                }

                String propPrice = currency_sign + " " + propInfoObj.getString("price");
                String propAddress = propInfoObj.getString("postCity") + ", " + propInfoObj.getString("streetName");
                String propWorkType = propInfoObj.getString("work_type");
                int job_status = Integer.parseInt(propInfoObj.getString("job_status").toString());
                String propDesc = propInfoObj.getString("description");
                String employerId = propInfoObj.getString("user_id");
                JSONObject workerInfoObj = propertyObj.getJSONObject("worker");
                String workerName = workerInfoObj.getString("name");
                String workerPhone = workerInfoObj.getString("phone");
                String mEmployName = propInfoObj.getString("contact_name");
                String mEmployCon = propInfoObj.getString("contact_mobile");

                txtOfferName.setText(propName);
                txtPrice.setText(propPrice);
                txtRoomType.setText(propJobType);
                txtLocation.setText("Location: " + propAddress);
                txtParking.setText("Work type: " + propWorkType);
                txtDescription.setText(propDesc);
                txtName.setText(mEmployName);
                txtPhone.setText(mEmployCon);

             /*   if(workerName == null || workerName.equals("")) {
                    txtName.setText(appData.getUserName());
                    txtPhone.setText(appData.getPhoneNumber());
                }*/

                selectedOffer = new Offer(selectedOfferId, propName, propJobType, propInfoObj.getString("price"), 0);

                if (properties_response.getInt("count_employer_review") != 0) {
                    employerReviewLayout.setVisibility(View.VISIBLE);
                    JSONObject employerReview = properties_response.getJSONObject("employer_review");
                    float employerMarks = Float.parseFloat(employerReview.getString("marks"));
                    employer_ratingBar.setRating(employerMarks);
                    txtEmployerMarks.setText(employerReview.getString("marks"));
                    txtEmployerDescription.setText(employerReview.getString("description"));
                    //unreadMsgClientId = lastUnreadObj.getInt("client_id");
                } else {
                    employerReviewLayout.setVisibility(View.GONE);
                }

                if (properties_response.getInt("count_contact_person_review") != 0) {
                    contactPersonReviewLayout.setVisibility(View.VISIBLE);
                    JSONObject contactPersonReview = properties_response.getJSONObject("contact_person_review");
                    float contactPersonMarks = Float.parseFloat(contactPersonReview.getString("marks"));
                    contact_person_ratingBar.setRating(contactPersonMarks);
                    txtContactPersonMarks.setText(contactPersonReview.getString("marks"));
                    txtContactPersonDescription.setText(contactPersonReview.getString("description"));
                } else {
                    contactPersonReviewLayout.setVisibility(View.GONE);
                }

                int cntInterest = properties_response.getInt("countInterest");
                txtInterests.setText("People who are interested " + "(" + cntInterest + ")");
                if (appData.getUserId().equals(employerId)) {
                    if (cntInterest > 0 && job_status == 0) {
                        JSONArray favoriteArray = properties_response.getJSONArray("interests");
                        for (int i = 0; i < favoriteArray.length(); i++) {
                            JSONObject favoriteObj = favoriteArray.getJSONObject(i);
                            int id = favoriteObj.getInt("id");
                            int senderId = favoriteObj.getInt("senter_id");
                            String senderName = favoriteObj.getString("senter_name");
                            int offerId = favoriteObj.getInt("offer_id");
                            String isConfirmed = favoriteObj.getString("isConfirmed");

                            Favorite favorite = new Favorite(id, senderId, senderName, offerId, isConfirmed);
                            favorite.setSenderPhone(favoriteObj.getString("senter_phone"));

                            favoriteList.add(favorite);

                        }
                    }
                }

                if (job_status == 1) {
                    txtInterests.setVisibility(View.GONE);
                    btnModifyOffer.setVisibility(View.GONE);
                    imgRemove.setVisibility(View.GONE);
                    btnRemoveOffer.setVisibility(View.GONE);
                    btnFinishOffer.setVisibility(View.VISIBLE);

                }

                if (job_status == 2) {
                    txtInterests.setVisibility(View.GONE);
                    btnModifyOffer.setVisibility(View.GONE);
                    imgRemove.setVisibility(View.GONE);
                    btnRemoveOffer.setVisibility(View.GONE);
                    btnFinishOffer.setVisibility(View.GONE);
                    btnReviewOffer.setVisibility(View.VISIBLE);
                }

                if (job_status == 3) {
                    txtInterests.setVisibility(View.GONE);
                    btnModifyOffer.setVisibility(View.GONE);
                    imgRemove.setVisibility(View.GONE);
                    btnRemoveOffer.setVisibility(View.GONE);
                    btnFinishOffer.setVisibility(View.GONE);
                    btnReviewOffer.setVisibility(View.GONE);

                }

                if (!employerId.equals(appData.getUserId())) {
                    txtInterests.setVisibility(View.GONE);
                    btnModifyOffer.setVisibility(View.GONE);
                    imgRemove.setVisibility(View.GONE);
                    btnRemoveOffer.setVisibility(View.GONE);
                    btnFinishOffer.setVisibility(View.GONE);
                    btnReviewOffer.setVisibility(View.GONE);
                }

                int cntFavorites = properties_response.getInt("countFavorites");
                txtNumFavorites.setText(String.valueOf(cntFavorites));

                String cnt_notifications = properties_response.getString("notifications");
                appData.setCntMessages(cnt_notifications);

                adminbar.setMessages(cnt_notifications);
                adminbar.setUserName(appData.getUserName());
                adminbar.setUserId(appData.getUserId());

                //setViewPager();
                setRecyclerView();

                mainContainer.setVisibility(View.VISIBLE);
            } else if (completedRequestType == AppConstant.HttpRequestType.FinishJobRequest) {
                AppDebugLog.println("Properties status is " + status);
                if (status.equals("success")) {
                    txtInterests.setVisibility(View.GONE);
                    btnModifyOffer.setVisibility(View.GONE);
                    imgRemove.setVisibility(View.GONE);
                    btnRemoveOffer.setVisibility(View.GONE);
                    btnFinishOffer.setVisibility(View.GONE);
                    btnReviewOffer.setVisibility(View.VISIBLE);
                } else {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String calcTime(long agoTime) {
        String res;
        int days = (int) (agoTime / (3600 * 24));
        if (days > 0) {
            res = String.valueOf(days) + " days ago";
            return res;
        }

        int hours = (int) (agoTime / 3600);
        int reminder = (int) (agoTime - hours * 3600);
        int mins = reminder / 60;

        res = String.valueOf(mins) + " min ago";

        if (hours > 0)
            res = String.valueOf(hours) + " h " + res;

        return res;
    }

    @Override
    protected void onPause() {
        adapter = null;
        viewPagerAdapter = null;
        super.onPause();
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

    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickHidden() {
        txtInterests.setVisibility(View.GONE);
        btnModifyOffer.setVisibility(View.GONE);
        imgRemove.setVisibility(View.GONE);
        btnRemoveOffer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        btnFinishOffer.setVisibility(View.VISIBLE);
    }
}
