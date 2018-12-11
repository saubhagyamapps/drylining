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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.adapter.FavoriteToolsAdapter;
import com.app.drylining.adapter.ViewPagerAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Favorite;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddedToolDetailActivity extends CustomMainActivity implements RequestTaskDelegate
{
    private Toolbar toolbar;
    private ApplicationData appData;
    private ProgressDialog pdialog;

    private ArrayList<Favorite> favoriteList;
    private RecyclerView recyclerView;

    private FavoriteToolsAdapter adapter;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private String[] images;
    private ArrayList<String> propertySliderImages = new ArrayList<String>();

    private TextView txtOfferName, txtPrice, btnModifyOffer, btnRemoveOffer, txtReceivedQuestions, txtInterests, txtDescription,
            txtParking, txtLocation, txtPhone, txtName,txtRoomType, txtNumFavorites;
    private TextView readQuestions, numReadQuestions, unreadQuestions, numUnreadQuestions;
    private LinearLayout layoutUnread, layoutRead;

    private RelativeLayout mainContainer;

    private Button btnBack, btnNewOffer;

    private View actNext,actPrev;

    private JSONObject propObj;
    private int selectedOfferId;
    private Offer selectedOffer;

    private int unreadMsgClientId, readMsgClientId;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_tool_detail);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        mainContainer = (RelativeLayout) findViewById(R.id.main_container);
        mainContainer.setVisibility(View.GONE);

        btnBack = (Button) findViewById(R.id.btn_back);
        //btnNewOffer = (Button) findViewById(R.id.btn_new_offer);

        txtOfferName = (TextView) findViewById(R.id.offer_name);
        txtRoomType = (TextView) findViewById(R.id.txt_offer_type);
        //btnModifyOffer = (TextView) findViewById(R.id.btn_modify_offer);
        txtPrice = (TextView) findViewById(R.id.txt_price);
        //txtInterests = (TextView) findViewById(R.id.lbl_interested_people);
        //txtReceivedQuestions = ( TextView ) findViewById(R.id.lbl_received_questions);
        //btnRemoveOffer = (TextView) findViewById(R.id.txt_remove_offer);

        viewPager = (ViewPager) findViewById(R.id.pager);
        txtDescription = (TextView) findViewById(R.id.txt_desc);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtParking = (TextView) findViewById(R.id.txt_parking);

        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtName = (TextView) findViewById(R.id.txt_name);

        actNext = findViewById(R.id.btn_next);
        actPrev = findViewById(R.id.btn_previous);

        /*readQuestions = (TextView)findViewById(R.id.txt_read_questions);
        numReadQuestions = (TextView)findViewById(R.id.txt_read_questions_number);
        unreadQuestions = (TextView)findViewById(R.id.txt_unread_questions);
        numUnreadQuestions = (TextView)findViewById(R.id.txt_unread_questions_number);

        layoutRead = (LinearLayout)findViewById(R.id.layout_read);
        layoutUnread = (LinearLayout)findViewById(R.id.layout_unread);

        //txtNumFavorites = (TextView) findViewById(R.id.txt_favorites);
        recyclerView = (RecyclerView) findViewById(R.id.interests_recyclerView);*/

        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AddedToolDetailActivity.this.onBackPressed();
            }
        });

       /* btnNewOffer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(AddedToolDetailActivity.this, AddNewToolActivity.class));
            }
        });

        btnModifyOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(AddedToolDetailActivity.this, ActivityModifyTool.class);
                intent.putExtra("propertyInfo", propObj.toString());
                intent.putExtra("SELECTED_TOOL_ID", selectedOfferId);
                startActivity(intent);
                AddedToolDetailActivity.this.finish();
            }
        });

        btnRemoveOffer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent removeIntent = new Intent(AddedToolDetailActivity.this, ActivityRemoveTool.class);
                removeIntent.putExtra("SELECTED_TOOL_ID", selectedOfferId);
                startActivity(removeIntent);
            }
        });

        layoutUnread.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AddedToolDetailActivity.this, ActivityConversationsRenter.class);
                intent.putExtra("SELECTED_OFFER", selectedOffer);
                intent.putExtra("CLIENT_ID", unreadMsgClientId);
                startActivity(intent);

                AddedToolDetailActivity.this.finish();
            }
        });

        layoutRead.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(AddedToolDetailActivity.this, ActivityConversationsRenter.class);
                intent.putExtra("SELECTED_OFFER", selectedOffer);
                intent.putExtra("CLIENT_ID", readMsgClientId);
                startActivity(intent);

                AddedToolDetailActivity.this.finish();
            }
        });*/

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialContactPhone((String) txtPhone.getText());
            }
        });

        favoriteList = new ArrayList<Favorite>();
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

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        favoriteList.clear();

        showProgressDialog();
        selectedOfferId = getIntent().getIntExtra("ToolID",1);
        RequestTask requestTask = new RequestTask(AppConstant.GET_TOOL_INFO_RENTER, AppConstant.HttpRequestType.getToolInfo);
        requestTask.delegate = AddedToolDetailActivity.this;
        requestTask.execute(AppConstant.GET_TOOL_INFO_RENTER + selectedOfferId);
    }

    private void setRecyclerView()
    {
        if(adapter == null)
        {
            adapter = new FavoriteToolsAdapter(favoriteList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

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
        }else
            {

            actNext.setVisibility(View.VISIBLE);actPrev.setVisibility(View.VISIBLE);
            if(viewPager.getCurrentItem()==0){
                actPrev.setVisibility(View.INVISIBLE);
            }

            if(viewPager.getCurrentItem()==viewPager.getAdapter().getCount()-1)
                actNext.setVisibility(View.INVISIBLE);
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
                Intent firstActivityIntent = new Intent(AddedToolDetailActivity.this, DashboardActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                AddedToolDetailActivity.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);

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
        //TransitionManager.beginDelayedTransition(adminLayout);
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
        else {
            super.onBackPressed();
            //startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    private int getItem(int i)
    {
        return viewPager.getCurrentItem() + i;
    }

    public void previousClicked(View view)
    {
        viewPager.setCurrentItem(getItem(-1), true);

        updateNavigationButtons();
    }

    public void nextClicked(View view)
    {
        viewPager.setCurrentItem(getItem(+1), true); //getItem(-1) for previous
        updateNavigationButtons();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        AppDebugLog.println("Response is "+response);
        JSONObject properties_response = null;
        try {
            properties_response = new JSONObject(response);
            String status = properties_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            JSONObject propertyObj = properties_response.getJSONObject("result");

            propObj=propertyObj;

            JSONArray propImages = propertyObj.getJSONArray("images");
            // images = new String[propImages.length()];
            for (int image = 0; image < propImages.length(); image++)
            {
                JSONObject imageObj = propImages.getJSONObject(image);
                AppDebugLog.println("First Image "+imageObj.getString("image_path"));
                propertySliderImages.add(imageObj.getString("image_path"));
            }

            Object[] mStringArray = propertySliderImages.toArray();
            images = new String[mStringArray.length];

            for(int i = 0; i < mStringArray.length ; i++)
            {
                Log.d("string is", (String) mStringArray[i]);
                images[i] = (String) mStringArray[i];
            }

            JSONObject propInfoObj = propertyObj.getJSONObject("info");
            String propName = propInfoObj.getString("name");
            String proproomType = propInfoObj.getString("room_type");
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

            String propAddress = propInfoObj.getString("postCity") +", "+ propInfoObj.getString("streetName");
                    //+ " " + propInfoObj.getString("unitCode");
            String propGarbage = propInfoObj.getString("job_type");
            String propDesc = propInfoObj.getString("description");

            JSONObject renterInfoObj = propertyObj.getJSONObject("renter");
            String renterName = renterInfoObj.getString("name");
            String renterPhone= renterInfoObj.getString("phone");

            txtOfferName.setText(propName);
            txtPrice.setText(propPrice);
            txtRoomType.setText(proproomType);
            txtLocation.setText("Location: "+ propAddress);
            txtParking.setText("Tool type: "+ propGarbage);
            txtDescription.setText(propDesc);
            txtName.setText(renterName);
            txtPhone.setText(renterPhone);

            selectedOffer = new Offer(selectedOfferId, propName, proproomType, propInfoObj.getString("price"), 0);

            /*if(properties_response.getInt("countUnread") != 0)
            {
                JSONObject lastUnreadObj = properties_response.getJSONObject("lastUnread");
                unreadQuestions.setText(lastUnreadObj.getString("client_name") + " - " + calcTime(lastUnreadObj.getLong("time")));
                numUnreadQuestions.setText(lastUnreadObj.getString("count"));
                unreadMsgClientId = lastUnreadObj.getInt("client_id");
            }
            else
            {
                unreadQuestions.setVisibility(View.GONE);
                numUnreadQuestions.setVisibility(View.GONE);
            }

            if(properties_response.getInt("countRead") != 0)
            {
                JSONObject lastReadObj = properties_response.getJSONObject("lastRead");
                readQuestions.setText(lastReadObj.getString("client_name") + " - " + calcTime(lastReadObj.getLong("time")));
                numReadQuestions.setText(lastReadObj.getString("count"));
                readMsgClientId = lastReadObj.getInt("client_id");
            }
            else {
                txtReceivedQuestions.setVisibility(View.GONE);

                readQuestions.setVisibility(View.GONE);
                numReadQuestions.setVisibility(View.GONE);
            }

            int cntInterest = properties_response.getInt("countInterest");
            txtInterests.setText("People who are interested " + "(" + cntInterest + ")" );
            if( cntInterest > 0 )
            {
                JSONArray favoriteArray = properties_response.getJSONArray("interests");
                for(int i=0; i<favoriteArray.length(); i++)
                {
                    JSONObject favoriteObj = favoriteArray.getJSONObject(i);
                    int id = favoriteObj.getInt("id");
                    int senderId = favoriteObj.getInt("senter_id");
                    String senderName = favoriteObj.getString("senter_name");
                    int offerId  = favoriteObj.getInt("offer_id");
                    String isConfirmed = favoriteObj.getString("isConfirmed");

                    Favorite favorite = new Favorite(id, senderId, senderName, offerId, isConfirmed);
                    favoriteList.add(favorite);
                }
            }
            else
            {
                txtInterests.setVisibility(View.GONE);
            }*/


            /*int cntFavorites = properties_response.getInt("countFavorites");
            txtNumFavorites.setText(String.valueOf(cntFavorites));*/

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);

            adminbar.setMessages(cnt_notifications);
            adminbar.setUserName(appData.getUserName());
            adminbar.setUserId(appData.getUserId());

            setViewPager();
            //setRecyclerView();

            mainContainer.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String calcTime(long agoTime)
    {
        String res;
        int days = (int)(agoTime/(3600*24));
        if(days > 0)
        {
            res = String.valueOf(days) + " days ago";
            return res;
        }

        int hours = (int)(agoTime / 3600);
        int reminder = (int)(agoTime - hours * 3600);
        int mins = reminder / 60;

        res = String.valueOf(mins) + " min ago";

        if(hours > 0)
            res = String.valueOf(hours) + " h " + res;

        return res;
    }

    @Override
    protected void onPause()
    {
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

    private void showProgressDialog()
    {
        if (!isFinishing() && pdialog == null)
        {
            pdialog = ProgressDialog.show(this, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog()
    {
        if (pdialog != null && !isFinishing())
        {
            pdialog.dismiss();
            pdialog = null;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
//        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId()==R.id.action_logout)
        {
            Intent loginIntent=new Intent(getApplicationContext(),MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
