package com.app.drylining.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.custom.CustomViewPager;
import com.app.drylining.custom.MessageCntSetListener;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.fragment.AddNewOfferFragment;
import com.app.drylining.fragment.AddNewToolsFragment;
import com.app.drylining.model.MSGCountModel;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.retrofit.ApiClient;
import com.app.drylining.retrofit.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends CustomMainActivity implements TabLayout.OnTabSelectedListener, MessageCntSetListener, RequestTaskDelegate {
    private static final String TAG = "DashboardActivity";
    public static String mMsgCount;
    public static TextView toolbar_txt_messages_new;
    TextView toolbar_txt_messages;
    private int currentTabIndex;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private CustomViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private AddNewOfferFragment addNewOfferFragment;
    private AddNewToolsFragment addNewToolsFragment;
    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;
    private ApplicationData appData;
    private Animation animShow, animHide;
    private CountDownTimer lTimer;
    private Locale locale;

    public static void showConfirmExitAlert(final Context context, String title, String message, final Activity contextToFinish) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout = (LinearLayout) findViewById(R.id.adminBar);
        toolbar_txt_messages = (TextView) findViewById(R.id.toolbar_txt_messages);
        toolbar_txt_messages_new = (TextView) findViewById(R.id.toolbar_txt_messages_new);
        initialize();

    }

    private void countApiCall() {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MSGCountModel> countModelCall = apiService.getCountBit("1", appData.getUserId());
        countModelCall.enqueue(new Callback<MSGCountModel>() {
            @Override
            public void onResponse(Call<MSGCountModel> call, Response<MSGCountModel> response) {
                mMsgCount = response.body().getCount();
                toolbar_txt_messages_new.setText(response.body().getCount());
            }

            @Override
            public void onFailure(Call<MSGCountModel> call, Throwable t) {

            }
        });

    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();

        setToolbar();
        countApiCall();
        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);

        setInitialUI();
    }

    private void setInitialUI() {
        initializeFragments();
        setupViewPager();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        Drawable launcher = ContextCompat.getDrawable(this, R.mipmap.app_small_toolbar_icon);
        toolbar.setNavigationIcon(launcher);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firstActivityIntent = new Intent(DashboardActivity.this, DashboardActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DashboardActivity.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);
        adminbar.setUserName(appData.getUserName());
        adminbar.setUserId(appData.getUserId());
        adminbar.setMessages(mMsgCount);

        adminPanel = new AdminPanel(this);
    }

    @Override
    public void openAdminPanel() {
        adminLayout.setVisibility(View.VISIBLE);
        adminLayout.startAnimation(animShow);

        lTimer = new CountDownTimer(AppConstant.ADMIN_BAR_OPEN_TIME, 100) {

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
        Resources resources = getResources();
        locale = appData.getLocale();

        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }

    @Override
    public void onBackPressed() {
        if (adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else {
            showConfirmExitAlert(this, "", "Are you sure to exit this app?", this);
        }
    }

    private void initializeFragments() {
        addNewOfferFragment = new AddNewOfferFragment();
        addNewToolsFragment = new AddNewToolsFragment();
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(addNewOfferFragment, getString(R.string.tab_title_add_new_offer));
        viewPagerAdapter.addFragment(addNewToolsFragment, getString(R.string.tab_title_add_new_tool));

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        sendGetNotificationsRequest();
    }

    @Override
    public void messageCntSet(String notifications) {
        adminbar.setMessages(notifications);
    }

    public void tabChanged(TabLayout.Tab tab) {
        int position = tab.getPosition();
        currentTabIndex = position;

        AppDebugLog.println("In tabChanged : " + currentTabIndex);
        switch (currentTabIndex) {
            case AppConstant.TAB_ADD_NEW_OFFER:
                addNewOfferFragment.tabChanged();
                break;
            case AppConstant.TAB_ADD_NEW_TOOL:
                addNewToolsFragment.tabChanged();
                break;

            default:
                break;
        }
        viewPager.setCurrentItem(position, true);
    }

    private void tabUnSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        currentTabIndex = position;
        AppDebugLog.println("In tabUnSelected : " + currentTabIndex);
        switch (currentTabIndex) {
            case AppConstant.TAB_ADD_NEW_OFFER:
                addNewOfferFragment.tabUnSelected();
                break;
            case AppConstant.TAB_ADD_NEW_TOOL:
                addNewToolsFragment.tabUnSelected();
                break;
            default:
                break;
        }

        viewPager.setCurrentItem(position, true);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tabChanged(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tabUnSelected(tab);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        AppDebugLog.println("In onTabReselected : " + tab.getPosition());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //   getMenuInflater().inflate(R.menu.menu_main,menu);
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

    private void sendGetNotificationsRequest() {

        RequestTask requestTask = new RequestTask(AppConstant.GET_NOTIFICATIONS, AppConstant.HttpRequestType.GetNotifications);

        requestTask.delegate = (RequestTaskDelegate) DashboardActivity.this;

        String post_content = "userId=" + appData.getUserId() + "&userType=" + "L";

        requestTask.execute(AppConstant.GET_NOTIFICATIONS + "?" + post_content);
        //Log.e(TAG, "sendGetNotificationsRequest: "+requestTask.execute(AppConstant.GET_NOTIFICATIONS + "?" + post_content));
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {

        JSONObject con_response = null;

        try {
            if (completedRequestType == AppConstant.HttpRequestType.GetNotifications) {
                con_response = new JSONObject(response);

                Log.d("Property Response", response.toString());


                String status = con_response.getString("status");
                AppDebugLog.println("Properties status is " + status);
                Log.e(TAG, "Massege Count " + con_response.getInt("count"));
                // mMsgCount = String.valueOf(con_response.getInt("count"));
                toolbar_txt_messages.setText(String.valueOf(con_response.getInt("count")));
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

                    JSONObject timeObj = time_array.getJSONObject(i);
                 /*   long agoTime = timeObj.getLong("time");

                    Conversation conversation = new Conversation(id, sender, senderName, content, message_type, message_state, calcTime(agoTime));
                    conversation.setOfferId(offer);*/

                }
/*
                String cnt_notifications = String.valueOf(conList.size());
              //  txtNotifications.setText("Notifications (" + cnt_notifications + ")");
                appData.setCntMessages(cnt_notifications);

                adminbar.setMessages(cnt_notifications);
                adminbar.setUserName(appData.getUserName());
                adminbar.setUserId(appData.getUserId());*/
            } else if (completedRequestType == AppConstant.HttpRequestType.RemoveNotifications) {
            }
        } catch (Exception e) {
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
