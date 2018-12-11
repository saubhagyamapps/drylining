package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.Util;
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

import java.util.regex.Pattern;


public class ActivityMyAccount extends CustomMainActivity implements RequestTaskDelegate
{
    private ActivityMyAccount activity;
    private ApplicationData appData;
    private Toolbar toolbar;
    private ProgressDialog pdialog;

    private EditText txtName, txtEmail, txtPhone, txtPassword, txtRetypePwd;
    private TextView lblError, msgError;
    private Button btnSave;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private ScrollView scrollView;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_account);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        txtName = (EditText) findViewById(R.id.txt_name);
        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtPhone = (EditText) findViewById(R.id.txt_mobile);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        txtRetypePwd = (EditText) findViewById(R.id.txt_retype_password);
        lblError = (TextView) findViewById(R.id.lbl_error);
        msgError = (TextView) findViewById(R.id.msg_error);
        btnSave = (Button) findViewById(R.id.btn_save);

        txtPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0)
                {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    txtPassword.setSelection(s.length());
                }
                else if(s.length() == 0)
                {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtRetypePwd.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0)
                {
                    txtRetypePwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    txtRetypePwd.setSelection(s.length());
                }
                else if(s.length() == 0)
                {
                    txtRetypePwd.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                btnSaveClicked();
            }
        });

        initialize();
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = this;
        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        txtName.setText(appData.getUserName());
        txtEmail.setText(appData.getUserEmail());
        txtPhone.setText(appData.getPhoneNumber());
        txtPassword.setText(appData.getUserPassword());
        txtRetypePwd.setText(appData.getUserPassword());

//        sendGetAccountRequest();
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
                    firstActivityIntent = new Intent(ActivityMyAccount.this, DashboardActivity.class);
                }
                else if(userType.equals("L"))
                {
                    firstActivityIntent = new Intent(ActivityMyAccount.this, SearchActivity.class);
                }

                else
                    return;

                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityMyAccount.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);

        adminPanel = new AdminPanel(this);
    }

    private void sendGetAccountRequest()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_ACCOUNT, AppConstant.HttpRequestType.GetAccount);
            requestTask.delegate = ActivityMyAccount.this;
            String userId = appData.getUserId();
            requestTask.execute(AppConstant.GET_ACCOUNT + "?" + "userId=" + userId);
        }
    }

    private  void sendSaveAccountRequest()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            lblError.setVisibility(View.GONE);
            msgError.setVisibility(View.GONE);
            RequestTask requestTask = new RequestTask(AppConstant.UPDATE_ACCOUNT, AppConstant.HttpRequestType.UpdateAccount);
            requestTask.delegate = ActivityMyAccount.this;
            requestTask.execute(AppConstant.UPDATE_ACCOUNT, getPostContent());
        } else {
            Util.showNoConnectionDialog(activity);
        }
    }


    private void btnSaveClicked()
    {
        if (isValid())
        {
            sendSaveAccountRequest();
        }
        else
        {
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            focusToError();
        }
    }

    private void focusToError()
    {
        scrollView.post(new Runnable()
        {
            @Override
            public void run() {
                scrollView.scrollTo(0, lblError.getTop());
            }
        });
    }
    private boolean isValid()
    {
        boolean isValid = true;
        txtName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtRetypePwd.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtPhone.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));

        AppDebugLog.println("Hurray here");
        String strName = txtName.getText().toString();
        String strEmail = txtEmail.getText().toString();
        String strPass = txtPassword.getText().toString();
        String strRepass = txtRetypePwd.getText().toString();
        String strPhone = txtPhone.getText().toString();

        if (strRepass.length() == 0 || !strPass.equals(strRepass))
        {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtRetypePwd.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        if (strPass.length() == 0)
        {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        if (strPhone.equals("") || !Pattern.matches(AppConstant.PHONE_VALIDATION, strPhone))
        {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtPhone.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        if (strEmail.equals("") || !Pattern.matches(AppConstant.EMAIL_VALIDATION, strEmail))
        {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        if (strName.equals(""))
        {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        return isValid;
    }

    private String getPostContent()
    {
        String strName = txtName.getText().toString();
        String strEmail = txtEmail.getText().toString();
        String strPhone = txtPhone.getText().toString();
        String strPass = txtPassword.getText().toString();

        appData.setUserName(strName);
        appData.setUserEmail(strEmail);
        appData.setPhoneNumber(strPhone);
        appData.setUserPassword(strPass);

        String postContent = "userId=" + appData.getUserId() + "&name=" + strName + "&password=" +
                strPass + "&phone=" + strPhone + "&email=" + strEmail + "&type=" + appData.getUserType() ;
        return postContent;
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
        JSONObject responseObj = null;

        try
        {
            responseObj = new JSONObject(response);
            String status = responseObj.getString("status");

            if(completedRequestType == AppConstant.HttpRequestType.GetAccount)
            {
                JSONObject userInfo = responseObj.getJSONObject("result");
                txtName.setText(userInfo.getString("name"));
                txtEmail.setText(userInfo.getString("email"));
                txtPhone.setText(userInfo.getString("phone"));
                txtPassword.setText(userInfo.getString("password"));
                txtRetypePwd.setText(userInfo.getString("password"));

                String cnt_notifications = responseObj.getString("notifications");
                appData.setCntMessages(cnt_notifications);

                adminbar.setMessages(cnt_notifications);
                adminbar.setUserName(appData.getUserName());
                adminbar.setUserId(appData.getUserId());
            }

            else if(completedRequestType == AppConstant.HttpRequestType.UpdateAccount)
            {
                if(status.equals("success"))
                {
                    lblError.setText("Changes saved");
                    lblError.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_success));
                    lblError.setVisibility(View.VISIBLE);
                }
                else if(status.equals("failed"))
                {
                    msgError.setText(responseObj.getString("msg"));
                    lblError.setText("ERROR!");
                    lblError.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error));
                    lblError.setVisibility(View.VISIBLE);
                    msgError.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (JSONException e)
        {
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
}
