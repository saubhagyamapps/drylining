package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;


public class ActivityNotifySettings extends CustomMainActivity implements View.OnClickListener, RequestTaskDelegate
{
    private ActivityNotifySettings activity;
    private ApplicationData appData;
    private Toolbar toolbar;
    private ProgressDialog pdialog;

    private CheckBox cbOnOff, cbBoarding, cbJointing, cbScreed, cbPlastering, cbDayWork, cbPriceWork;
    private TextView lblSuccess, txtFavorite, txtInterest;
    private Button btnSave;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private String on_of, boarding, jointing, screed, platering, day_work, price_work;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notify_settings);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        cbOnOff = (CheckBox) findViewById(R.id.setting_cb_on_off);
        cbBoarding = (CheckBox) findViewById(R.id.setting_cb_boarding);
        cbJointing = (CheckBox) findViewById(R.id.setting_cb_jointing);
        cbScreed = (CheckBox) findViewById(R.id.setting_cb_screed);
        cbPlastering = (CheckBox) findViewById(R.id.setting_cb_plastering);

        cbDayWork = (CheckBox) findViewById(R.id.setting_cb_day_work);
        cbPriceWork = (CheckBox) findViewById(R.id.setting_cb_price_work);

        lblSuccess = (TextView) findViewById(R.id.lbl_success) ;

        /*txtFavorite = (TextView) findViewById(R.id.setting_txt_new_favorite);
        txtInterest = (TextView) findViewById(R.id.setting_txt_new_interest);*/

        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        cbOnOff.setOnClickListener(this);
        cbBoarding.setOnClickListener(this);
        cbJointing.setOnClickListener(this);
        cbScreed.setOnClickListener(this);
        cbPlastering.setOnClickListener(this);
        cbDayWork.setOnClickListener(this);
        cbPriceWork.setOnClickListener(this);

        initialize();
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = this;
        lblSuccess.setVisibility(View.GONE);

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        cbOnOff.setChecked(true);
        cbBoarding.setChecked(true);
        cbJointing.setChecked(true);
        cbScreed.setChecked(true);
        cbPlastering.setChecked(true);
        cbDayWork.setChecked(true);
        cbPriceWork.setChecked(true);

        /*if(appData.getUserType().equals("R"))
        {
            txtFavorite.setText("A user favorited your offer");
            txtInterest.setText("New interest received");
        }
        else if(appData.getUserType().equals("L"))
        {
            txtFavorite.setText("New offer matches my criteria");
            txtInterest.setText("My interest is confirmed");
        }*/

        sendGetNotifySettingsRequest();
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
                Intent firstActivityIntent = null;
                String userType = appData.getUserType();
                if(userType.equals("R"))
                {
                    //firstActivityIntent = new Intent(ActivityNotifySettings.this, AddedOffersActivity.class);
                    firstActivityIntent = new Intent(ActivityNotifySettings.this, DashboardActivity.class);

                }
                else if(userType.equals("L"))
                {
                    //firstActivityIntent = new Intent(ActivityNotifySettings.this, SearchedOffersActivity.class);
                    firstActivityIntent = new Intent(ActivityNotifySettings.this, SearchActivity.class);
                }

                else
                    return;

                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityNotifySettings.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);
        adminbar.setUserName(appData.getUserName());
        adminbar.setUserId(appData.getUserId());
        adminbar.setMessages(appData.getCntMessages());

        adminPanel = new AdminPanel(this);
    }

    private void sendGetNotifySettingsRequest()
    {
        showProgressDialog();

        RequestTask requestTask = new RequestTask(AppConstant.GET_NOTIFY_SETTINGS, AppConstant.HttpRequestType.GetNotifySettings);

        requestTask.delegate = ActivityNotifySettings.this;

        requestTask.execute(AppConstant.GET_NOTIFY_SETTINGS + appData.getUserId() + "&userType=" + appData.getUserType());
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
            if(appData.getUserType().equals("R"))
                startActivity(new Intent(this, DashboardActivity.class));
            else if(appData.getUserType().equals("L"))
                startActivity(new Intent(this, SearchActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.setting_cb_on_off:
                boolean isChecked = cbOnOff.isChecked();
                cbBoarding.setChecked(isChecked);
                cbJointing.setChecked(isChecked);
                cbScreed.setChecked(isChecked);
                cbPlastering.setChecked(isChecked);
                cbDayWork.setChecked(isChecked);
                cbPriceWork.setChecked(isChecked);
                break;

            case R.id.btn_save:
                showProgressDialog();

                RequestTask requestTask = new RequestTask(AppConstant.SET_NOTIFY_SETTINGS, AppConstant.HttpRequestType.SetNotifySettings);

                requestTask.delegate = ActivityNotifySettings.this;

                requestTask.execute(AppConstant.SET_NOTIFY_SETTINGS + appData.getUserId() + "&" + getSaveSettingsPostContent());
                break;
        }
    }

    private String getSaveSettingsPostContent()
    {
        String postContent = "";

        if(cbBoarding.isChecked())
            boarding = "yes";
        else
            boarding = "no";

        if(cbJointing.isChecked())
        {
            if(appData.getUserType().equals("R"))
                jointing = "yes";
            else if(appData.getUserType().equals("L"))
                jointing = "yes";
        }
        else
        {
            if(appData.getUserType().equals("R"))
                jointing = "no";
            else if(appData.getUserType().equals("L"))
                jointing = "no";
        }

        if(cbScreed.isChecked())
            screed = "yes";
        else
            screed = "no";

        if(cbPlastering.isChecked())
            platering = "yes";
        else
            platering = "no";

        if(cbDayWork.isChecked())
        {
            if(appData.getUserType().equals("R"))
                day_work = "yes";
            else if(appData.getUserType().equals("L"))
                day_work = "yes";
        }
        else
        {
            if(appData.getUserType().equals("R"))
                day_work = "no";
            else if(appData.getUserType().equals("L"))
                day_work = "no";
        }

        if(cbPriceWork.isChecked())
            price_work = "yes";
        else
            price_work = "no";

        postContent = "boarding=" + boarding + "&jointing=" + jointing + "&screed=" + screed + "&plastering=" + platering
                + "&day_work=" + day_work + "&price_work=" + price_work + "&userType=" + appData.getUserType();

        return postContent;
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject con_response = null;

        try
        {
            con_response = new JSONObject(response);

            Log.e("Notify Response", response.toString());

            String status = con_response.getString("status");
            AppDebugLog.println("settings status is " + status);

            if(completedRequestType == AppConstant.HttpRequestType.GetNotifySettings)
            {
                JSONObject settingsObj = con_response.getJSONObject("settings");

                initCheckState(settingsObj);
            }

            else if(completedRequestType == AppConstant.HttpRequestType.SetNotifySettings)
            {
                if(status.equals("success"))
                {
                    lblSuccess.setVisibility(View.VISIBLE);
                }
                else
                {
                    Toast.makeText(this, "   Failed setting...\nPlease try again", Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCheckState(JSONObject settingsObj)
    {
        try
        {
            boarding = settingsObj.getString("boarding");
            jointing = settingsObj.getString("jointing");
            screed = settingsObj.getString("screed");
            platering = settingsObj.getString("plastering");
            day_work = settingsObj.getString("day_work");
            price_work = settingsObj.getString("price_work");
            /*interest_received  = settingsObj.getString("interest_received");
            by_email = settingsObj.getString("by_email");
*/
            if(boarding.equals("yes"))
                cbBoarding.setChecked(true);
            else if(boarding.equals("no"))
                cbBoarding.setChecked(false);

            if(jointing.equals("yes"))
                cbJointing.setChecked(true);
            else if(jointing.equals("no"))
                cbJointing.setChecked(false);

            if(screed.equals("yes"))
                cbScreed.setChecked(true);
            else if(screed.equals("no"))
                cbScreed.setChecked(false);

            if(platering.equals("yes"))
                cbPlastering.setChecked(true);
            else if(jointing.equals("no"))
                cbPlastering.setChecked(false);

            if(appData.getUserType().equals("R"))
            {
                if(day_work.equals("yes"))
                    cbDayWork.setChecked(true);
                else if(day_work.equals("no"))
                    cbDayWork.setChecked(false);

                if(price_work.equals("yes"))
                    cbPriceWork.setChecked(true);
                else if(price_work.equals("no"))
                    cbPriceWork.setChecked(false);
            }
            else if(appData.getUserType().equals("L"))
            {
                if(day_work.equals("yes"))
                    cbDayWork.setChecked(true);
                else if(day_work.equals("no"))
                    cbDayWork.setChecked(false);

                if(price_work.equals("yes"))
                    cbPriceWork.setChecked(true);
                else if(price_work.equals("no"))
                    cbPriceWork.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog()
    {
        if (!this.isFinishing() && pdialog == null)
        {
            pdialog = ProgressDialog.show(this, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }
    private void cancelProgressDialog()
    {
        if (pdialog != null && !this.isFinishing())
        {
            pdialog.dismiss();
            pdialog = null;
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
}
