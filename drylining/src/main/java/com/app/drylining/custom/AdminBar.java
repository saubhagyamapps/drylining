package com.app.drylining.custom;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.chat.ui.activity.DialogsActivity;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.model.MSGCountModel;
import com.app.drylining.retrofit.ApiClient;
import com.app.drylining.retrofit.ApiInterface;
import com.app.drylining.ui.ActivityNotifications;
import com.app.drylining.ui.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.drylining.ui.DashboardActivity.toolbar_txt_messages_new;

/**
 * Created by Panda on 6/20/2017.
 */

public class AdminBar implements View.OnClickListener {
    public ImageView imgOpenArrow, imagesChatIcon;
    private String userId;
    private LinearLayout toolbar_msg_layout, toolbar_user_layout;
    private RelativeLayout spLanguageContainer;
    private Spinner spLanguage;
    private CustomSpinnerAdapter spLanguageAdapter;
    private TextView txtMessages, txtUserName;
    private ImageView imgMessageIcon, imgActionIcon;
    private CustomMainActivity mActivity;
    private ApplicationData appData;
    private boolean adminPanelIsOpen;

    public AdminBar(CustomMainActivity activity) {
        this.mActivity = activity;

        toolbar_msg_layout = (LinearLayout) mActivity.findViewById(R.id.toolbar_msg_layout);
        toolbar_user_layout = (LinearLayout) mActivity.findViewById(R.id.toolbar_user_layout);

        txtMessages = (TextView) mActivity.findViewById(R.id.toolbar_txt_messages);
        txtUserName = (TextView) mActivity.findViewById(R.id.toolbar_txt_name);
        imgMessageIcon = (ImageView) mActivity.findViewById(R.id.toolbar_icon_message);
        imgActionIcon = (ImageView) mActivity.findViewById(R.id.toolbar_icon_action);

        spLanguageContainer = (RelativeLayout) mActivity.findViewById(R.id.sp_LanguageContainer);
        spLanguage = (Spinner) mActivity.findViewById(R.id.spinnerLanguage);

        imgOpenArrow = (ImageView) mActivity.findViewById(R.id.toolbar_img_admin_open_arrow);
        imagesChatIcon = (ImageView) mActivity.findViewById(R.id.imagesChatIcon);

        txtMessages.setOnClickListener(this);
        txtUserName.setOnClickListener(this);

        imgMessageIcon.setOnClickListener(this);
        imgActionIcon.setOnClickListener(this);

        adminPanelIsOpen = false;
        openChatTab();

    }

    private void openChatTab() {
        imagesChatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogsActivity.start(mActivity);
            }
        });
    }

    public void setMessages(String msgCount) {
        /*if(msgCount.equals("0"))
            toolbar_msg_layout.setVisibility(View.GONE);
        else*/
        toolbar_msg_layout.setVisibility(View.VISIBLE);
        txtMessages.setText(DashboardActivity.mMsgCount);
    }

    public void setUserName(String name) {
        toolbar_user_layout.setVisibility(View.VISIBLE);
        txtUserName.setText(name);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.toolbar_txt_name || id == R.id.toolbar_icon_action) {
            if (adminPanelIsOpen == false) {
                adminPanelIsOpen = true;
                mActivity.openAdminPanel();
            } else {
                adminPanelIsOpen = false;
                mActivity.closeAdminPanel();
            }
        } else if (id == R.id.toolbar_txt_messages || id == R.id.toolbar_icon_message) {
            countApiCall();
            Intent notificationsIntent = new Intent(mActivity, ActivityNotifications.class);
            mActivity.startActivity(notificationsIntent);
            mActivity.finish();

        }

    }

    private void countApiCall() {
        appData = ApplicationData.getSharedInstance();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MSGCountModel> countModelCall = apiService.getCountBit("0", appData.getUserId());
        countModelCall.enqueue(new Callback<MSGCountModel>() {
            @Override
            public void onResponse(Call<MSGCountModel> call, Response<MSGCountModel> response) {
                toolbar_txt_messages_new.setText("");
            }

            @Override
            public void onFailure(Call<MSGCountModel> call, Throwable t) {

            }
        });

    }
}
