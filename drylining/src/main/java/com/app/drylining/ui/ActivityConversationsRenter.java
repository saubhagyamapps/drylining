package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.adapter.ConversationAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Conversation;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Panda on 6/7/2017.
 */

public class ActivityConversationsRenter extends CustomMainActivity implements RequestTaskDelegate
{
    private Offer selectedOffer;
    private Toolbar toolbar;
    private ApplicationData appData;
    private TextView txtOfferName, txtPrice, txtRoomType;
    private Button btnBack, btnInbox;
    private ImageButton btnSubmitAnswer;
    private RecyclerView recyclerView;
    private AppCompatEditText txtAnswer;

    private ProgressDialog pdialog;

    public String userId, clientName;
    public int clientId;
    private ArrayList<Conversation> conList, sortedConList;
    private ConversationAdapter adapter;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_renter);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);

        txtOfferName = (TextView) findViewById(R.id.offer_name);
        txtRoomType = (TextView) findViewById(R.id.txt_offer_type);
        txtPrice = (TextView) findViewById(R.id.txt_price);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        txtAnswer = (AppCompatEditText) findViewById(R.id.txt_answer);

        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ActivityConversationsRenter.this, AddedOfferDetailActivity.class);
                intent.putExtra("OfferID", selectedOffer.getId());
                ActivityConversationsRenter.this.startActivity(intent);
                ActivityConversationsRenter.this.finish();
            }
        });

        btnInbox = (Button) findViewById(R.id.btn_inbox);
        btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnInboxClicked();
            }
        });

        btnSubmitAnswer = (ImageButton) findViewById(R.id.btn_send);
        btnSubmitAnswer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btnSendClicked();
            }
        });

        initialize();
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();

        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        conList = new ArrayList<Conversation>();
        sortedConList = new ArrayList<Conversation>();

        selectedOffer = (Offer)getIntent().getSerializableExtra("SELECTED_OFFER");
        clientId = getIntent().getIntExtra("CLIENT_ID", 0);

        txtOfferName.setText(selectedOffer.getName());
        txtRoomType.setText(selectedOffer.getCategory());
        txtPrice.setText(String.valueOf(selectedOffer.getPrice()) + " EUR");

        sendGetConversationsRequest();
    }

    private void sendGetConversationsRequest()
    {
        showProgressDialog();

        RequestTask requestTask = new RequestTask(AppConstant.GET_CONVERSATION_RENTER, AppConstant.HttpRequestType.GetConversations);

        requestTask.delegate = ActivityConversationsRenter.this;

        userId = appData.getUserId();

        String post_content = "userId=" + userId + "&offerId=" + selectedOffer.getId() + "&clientId=" + clientId;

        requestTask.execute(AppConstant.GET_CONVERSATION_RENTER + "?" + post_content);
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
                Intent firstActivityIntent = new Intent(ActivityConversationsRenter.this, DashboardActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityConversationsRenter.this.startActivity(firstActivityIntent);
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

            Intent intent = new Intent(ActivityConversationsRenter.this, AddedOfferDetailActivity.class);
            intent.putExtra("OfferID", selectedOffer.getId());
            ActivityConversationsRenter.this.startActivity(intent);
            ActivityConversationsRenter.this.finish();
        }
    }

    private void btnSendClicked()
    {
        if (!txtAnswer.getText().toString().equals(""))
        {
            RequestTask requestTask = new RequestTask(AppConstant.NEW_CONVERSATION, AppConstant.HttpRequestType.NewConversation);
            requestTask.delegate = ActivityConversationsRenter.this;

            userId = appData.getUserId();

            String message = null;
            try {
                message = URLEncoder.encode(txtAnswer.getText().toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this, "Encoding error, please try again", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            String post_content = "senderType=" + "R" + "&renterId=" + userId + "&clientId=" + clientId +
                    "&conversation=" + message + "&offerId=" + selectedOffer.getId();

            requestTask.execute(AppConstant.NEW_CONVERSATION + "?" + post_content);

            Conversation newCon = new Conversation(Integer.valueOf(userId), clientId, selectedOffer.getId(), txtAnswer.getText().toString());

            Conversation LastCon = sortedConList.get(sortedConList.size() - 1);
            if(LastCon.getSenderId() == newCon.getSenderId())
                sortedConList.get(sortedConList.size() - 1).addContent(newCon.getContent());
            else
                sortedConList.add(newCon);

            adapter.notifyDataSetChanged();

            closeKeyboard(this, txtAnswer.getWindowToken());

            recyclerView.smoothScrollToPosition(sortedConList.size() - 1);

            txtAnswer.setText("");
        }
    }

    public void closeKeyboard(Context c, IBinder windowToken)
    {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    private void btnInboxClicked()
    {
        Intent intent = new Intent(this, ActivityNotifications.class);
        startActivity(intent);
        ActivityConversationsRenter.this.finish();
    }

    private void setRecyclerView()
    {
        if(adapter == null)
        {
            adapter = new ConversationAdapter(this, sortedConList, clientName);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();

        recyclerView.smoothScrollToPosition(sortedConList.size() - 1);
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject con_response = null;
        try
        {
            con_response = new JSONObject(response);

            Log.e("Property Response",response.toString());

            String status = con_response.getString("status");

            clientName = con_response.getString("client");

            AppDebugLog.println("Properties status is " + status);
            JSONArray cons_array = con_response.getJSONArray("result");
            for (int i = 0; i < cons_array.length(); i++)
            {
                JSONObject con = cons_array.getJSONObject(i);
                int sender = con.getInt("sender_id");
                int receiver = con.getInt("receiver_id");
                int offer  = con.getInt("offer_id");
                String content = con.getString("content");

                Conversation conversation = new Conversation(sender, receiver, offer, content);

                conList.add(conversation);
            }

            String cnt_notifications = con_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);

            adminbar.setMessages(cnt_notifications);
            adminbar.setUserName(appData.getUserName());
            adminbar.setUserId(appData.getUserId());

            sortConList();

            setRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortConList()
    {
        sortedConList.clear();

        sortedConList.add(0, conList.get(0));
        int sortedConListSize ;
        Conversation con;
        for (int i=1; i<conList.size(); i++)
        {
            sortedConListSize = sortedConList.size();
            con = conList.get(i);

            if(con.getSenderId() == sortedConList.get(sortedConListSize-1).getSenderId())
            {
                sortedConList.get(sortedConListSize-1).addContent(con.getContent());
            }
            else
            {
                sortedConList.add(con);
            }
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
    public void cancelProgressDialog()
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
