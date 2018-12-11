package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.Util;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ActivityNewQuestion extends CustomMainActivity implements RequestTaskDelegate
{
    private Offer selectedOffer;
    private Toolbar toolbar;
    private ApplicationData appData;
    private TextView txtOfferName, txtDistance, txtPrice, txtRoomType, txtMsgError;
    private ImageView imageFavorite;
    private AppCompatEditText question;
    private Button btnBack, btnNewSearch, btnSubmitConversation;

    private ProgressDialog pdialog;

    public String lesseeId;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        txtMsgError = (TextView)findViewById(R.id.msg_error);
        txtOfferName = (TextView)findViewById(R.id.offer_name);
        txtRoomType  = (TextView)findViewById(R.id.txt_offer_type);
        txtPrice = (TextView)findViewById(R.id.txt_price);
        txtDistance = (TextView)findViewById(R.id.txt_distance);
        imageFavorite = (ImageView)findViewById(R.id.img_favourite);

        question = (AppCompatEditText)findViewById(R.id.txt_question);

        btnBack = (Button)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ActivityNewQuestion.this.onBackPressed();
            }
        });

        btnNewSearch = (Button)findViewById(R.id.btn_new_search);
        btnNewSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity( new Intent(ActivityNewQuestion.this, SearchNewOfferActivity.class));
                ActivityNewQuestion.this.finish();
            }
        });

        btnSubmitConversation = (Button)findViewById(R.id.btn_send_new_question) ;
        btnSubmitConversation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isValid())
                {
                    sendConversation();
                }
                else
                {
                    txtMsgError.setVisibility(View.VISIBLE);
                }
            }
        });

        initialize();
    }

    private boolean isValid()
    {
        boolean isValid = true;
        if(question.getText().toString().equals(""))
        {
            isValid = false;
            question.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled));
            question.setBackground(ContextCompat.getDrawable(ActivityNewQuestion.this, R.drawable.bg_error_border));
        }
        return isValid;
    }

    private void sendConversation()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.NEW_CONVERSATION, AppConstant.HttpRequestType.NewConversation);
            requestTask.delegate = ActivityNewQuestion.this;

            lesseeId = appData.getUserId();

            String message = null;
            try {
                message = URLEncoder.encode(question.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this, "Encoding error, please try again", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            String post_content = "senderType=" + "L" + "&offerId=" + selectedOffer.getId() +
                             "&lesseeId=" + lesseeId + "&conversation=" + message;

            requestTask.execute(AppConstant.NEW_CONVERSATION + "?" + post_content);
        }
        else
        {
            Util.showNoConnectionDialog(this);
        }
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        selectedOffer = (Offer)getIntent().getSerializableExtra("SELECTED_OFFER");

        txtOfferName.setText(selectedOffer.getName());
        txtRoomType.setText(selectedOffer.getCategory());
        txtPrice.setText(String.valueOf(selectedOffer.getPrice()) + " EUR/month");
        txtDistance.setText(formatNumber(selectedOffer.getDistance()));
        txtMsgError.setVisibility(View.GONE);

        boolean isFavorite = getIntent().getBooleanExtra("IS_FAVORITED", false);
        if(isFavorite)
            imageFavorite.setImageResource(R.drawable.ic_favourite_dark);
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
                Intent firstActivityIntent = new Intent(ActivityNewQuestion.this, SearchActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityNewQuestion.this.startActivity(firstActivityIntent);
            }
        });

        adminbar = new AdminBar(this);
        adminbar.setUserName(appData.getUserName());
        adminbar.setUserId(appData.getUserId());
        adminbar.setMessages(appData.getCntMessages());

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
        else {
            super.onBackPressed();

            Intent intent = new Intent(ActivityNewQuestion.this, SearchedOfferDetailActivity.class);
            intent.putExtra("OfferID", selectedOffer.getId());
            ActivityNewQuestion.this.startActivity(intent);
            ActivityNewQuestion.this.finish();
        }
    }

    private String formatNumber(double distance)
    {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "m";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }
        return String.format("%4.2f%s", distance, unit);
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject con_response = null;
        try {
            con_response = new JSONObject(response);
            String status = con_response.getString("status");
            String msg = con_response.getString("msg");
            if(status.equals("success"))
            {
                appData.showUserAlert(ActivityNewQuestion.this, "Success",
                        "Your question is successfully sent.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppDebugLog.println("Change tab :");
                    }
                });
                txtMsgError.setVisibility(View.GONE);
            }
            else
            {
                txtMsgError.setVisibility(View.VISIBLE);
                txtMsgError.setText(msg);
            }
        } catch (JSONException e) {
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
    public void percentageDownloadCompleted(int percentage, Object record)
    {

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
}
