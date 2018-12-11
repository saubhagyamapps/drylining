package com.app.drylining.custom;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.chat.ui.activity.DialogsActivity;
import com.app.drylining.chat.ui.activity.SelectUsersActivity;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.ui.ActivityFavoriteOffers;
import com.app.drylining.ui.ActivityMyAccount;
import com.app.drylining.ui.ActivityNotifySettings;
import com.app.drylining.ui.AddedOffersActivity;
import com.app.drylining.ui.DashboardActivity;
import com.app.drylining.ui.MainActivity;
import com.app.drylining.ui.SearchActivity;
import com.app.drylining.utils.AppInfo;

import java.util.Locale;

public class AdminPanel implements View.OnClickListener
{
    private CustomMainActivity mActivity;
    private ImageView imgFavoriteOffer;
    private TextView txtJobs, txtFavoriteOffer, txtMyAccount, txtNotifySettings, txtSwitchRenter, txtSwitchLessee, txtLogout;
    private TextView txtChats, txtContacts;
    private CheckBox cbRenter, cbLessee;

    private RelativeLayout spLanguageContainer;
    private Spinner spLanguage;
    private CustomSpinnerAdapter spLanguageAdapter;
    private String postLanguage;

    private ApplicationData appData;

    private LinearLayout titleJobsContainer;

    public AdminPanel(CustomMainActivity activity) {
        this.mActivity = activity;

        appData = ApplicationData.getSharedInstance();

        titleJobsContainer = activity.findViewById(R.id.title_jobs_container);
        txtJobs = activity.findViewById(R.id.admin_txt_offer);
        imgFavoriteOffer = (ImageView) activity.findViewById(R.id.admin_img_favorite_offer);
        txtFavoriteOffer = (TextView) activity.findViewById(R.id.admin_txt_favorite_offer);

        txtMyAccount = (TextView) activity.findViewById(R.id.admin_txt_my_account);
        txtChats = activity.findViewById(R.id.admin_txt_my_chats);
        txtContacts = activity.findViewById(R.id.admin_txt_my_contacts);
        txtNotifySettings = (TextView) activity.findViewById(R.id.admin_txt_notify_settings);
        txtLogout = (TextView) activity.findViewById(R.id.admin_txt_logout);

        spLanguageContainer = (RelativeLayout) mActivity.findViewById(R.id.sp_LanguageContainer);
        spLanguage = (Spinner) mActivity.findViewById(R.id.spinnerLanguage);

        cbRenter = (CheckBox) activity.findViewById(R.id.admin_cb_renter);
        cbLessee = (CheckBox) activity.findViewById(R.id.admin_cb_lessee);

        txtJobs.setOnClickListener(this);
        imgFavoriteOffer.setOnClickListener(this);
        txtFavoriteOffer.setOnClickListener(this);
        txtMyAccount.setOnClickListener(this);
        txtChats.setOnClickListener(this);
        txtContacts.setOnClickListener(this);
        txtNotifySettings.setOnClickListener(this);
        cbRenter.setOnClickListener(this);
        cbLessee.setOnClickListener(this);
        txtLogout.setOnClickListener(this);

        cbRenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateCheckTextStatus(R.id.admin_txt_chk_renter, isChecked);
            }
        });

        cbLessee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateCheckTextStatus(R.id.admin_txt_chk_lessee, isChecked);
            }
        });

        String userType = appData.getUserType();
        if (userType.equals("R")) {
            txtFavoriteOffer.setText("My jobs");
            imgFavoriteOffer.setImageResource(R.drawable.ic_my_offers);

            cbRenter.setChecked(true);
            cbLessee.setChecked(false);

            updateCheckTextStatus(R.id.admin_txt_chk_renter, true);
            updateCheckTextStatus(R.id.admin_txt_chk_lessee, false);

            titleJobsContainer.setVisibility(View.GONE);
        } else if (userType.equals("L")) {
            txtFavoriteOffer.setText("My jobs");

            imgFavoriteOffer.setImageResource(R.drawable.ic_favourite);

            cbRenter.setChecked(false);
            cbLessee.setChecked(true);

            updateCheckTextStatus(R.id.admin_txt_chk_renter, false);
            updateCheckTextStatus(R.id.admin_txt_chk_lessee, true);

            titleJobsContainer.setVisibility(View.VISIBLE);
        }

        spLanguageAdapter = new CustomSpinnerAdapter(mActivity, mActivity.getResources().getStringArray(R.array.language_names));
        spLanguage.setAdapter(spLanguageAdapter);
        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if (tv != null) tv.setText(tv.getText());

                postLanguage = mActivity.getResources().getStringArray(R.array.language_names)[position];

                Locale locale = new Locale("en");
                switch (postLanguage) {
                    case "Bulgarian(BG)":
                        locale = new Locale("bg");
                        break;
                    case "Spanish(ES)":
                        locale = new Locale("es");
                        break;
                    case "French(FR)":
                        locale = new Locale("fr");
                        break;
                    case "Italian(IT)":
                        locale = new Locale("it");
                        break;
                    case "Dutch(NL)":
                        locale = new Locale("nl");
                        break;
                    case "Romanian(RO)":
                        locale = new Locale("ro");
                        break;
                }
                Resources resources = mActivity.getResources();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                mActivity.getBaseContext().getResources().updateConfiguration(configuration, mActivity.getBaseContext().getResources().getDisplayMetrics());
                if (!postLanguage.equals(appData.getLanguage())) {
                    appData.setLanguage(postLanguage);
                    appData.setLocale(locale);
                    mActivity.recreate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        String languageName = appData.getLanguage();
        String[] array = mActivity.getResources().getStringArray(R.array.language_names);
        spLanguage.setSelection(Util.getStringIndx(array, languageName));
    }

    private void updateCheckTextStatus(int id,boolean checked)
    {
        ((TextView)mActivity.findViewById(id)).setTextColor(checked? ContextCompat.getColor(mActivity, R.color.color_checked):ContextCompat.getColor(mActivity, R.color.color_gray_66));
    }
    @Override
    public void onClick(View v)
    {
        mActivity.closeAdminPanel();

        switch (v.getId())
        {
            case R.id.admin_cb_renter:
                CheckBox renterCB = (CheckBox) v;
                if (renterCB.isChecked())
                {
                    cbLessee.setChecked(false);
                    goToFirstActivity(true);
                    mActivity.finish();
                }
                else
                {
                    cbLessee.setChecked(true);
                    goToFirstActivity(false);
                    mActivity.finish();
                }
                break;
            case R.id.admin_cb_lessee:
                CheckBox lesseeCB = (CheckBox) v;
                if (lesseeCB.isChecked())
                {
                    cbRenter.setChecked(false);
                    goToFirstActivity(false);
                    mActivity.finish();
                }
                else
                {
                    cbRenter.setChecked(true);
                    goToFirstActivity(true);
                    mActivity.finish();
                }
                break;
            case R.id.admin_txt_logout:
                appData.setIsLoggedIn("N");
                Intent loginIntent=new Intent(mActivity, MainActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(loginIntent);
                mActivity.finish();
                break;
            case R.id.admin_txt_offer:
                AppInfo.getInstance().setJobType(AppInfo.JOBS);
                Intent jobIntent = new Intent(mActivity, SearchActivity.class);
                mActivity.startActivity(jobIntent);
                mActivity.finish();
                break;
            case R.id.admin_txt_favorite_offer:
                AppInfo.getInstance().setJobType(AppInfo.MY_JOB);
                if(appData.getUserType().equals("R"))
                {
                    Intent myOfferIntent = new Intent(mActivity, DashboardActivity.class);
                    mActivity.startActivity(myOfferIntent);
                    mActivity.finish();
                }
                else if(appData.getUserType().equals("L"))
                {
                    Intent mFavoritesIntent = new Intent(mActivity, SearchActivity.class);
                    mActivity.startActivity(mFavoritesIntent);
                    mActivity.finish();
                }
                break;
            case R.id.admin_txt_my_account:
                Intent myAccountIntent = new Intent(mActivity, ActivityMyAccount.class);
                mActivity.startActivity(myAccountIntent);
                mActivity.finish();
                break;
            case R.id.admin_txt_my_chats:
                DialogsActivity.start(mActivity);
                break;
            case R.id.admin_txt_my_contacts:
                SelectUsersActivity.start(mActivity);
                break;
            case R.id.admin_txt_notify_settings:
                Intent settingIntent = new Intent(mActivity, ActivityNotifySettings.class);
                mActivity.startActivity(settingIntent);
                mActivity.finish();
                break;
        }
    }

    private void goToFirstActivity(boolean isRenter)
    {
        Intent firstActivityIntent ;
        if(isRenter)
        {
            appData.setUserType("R");
            firstActivityIntent = new Intent(mActivity, DashboardActivity.class);
        }
        else
        {
            appData.setUserType("L");
            firstActivityIntent = new Intent(mActivity, SearchActivity.class);
        }
        firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.closeAdminPanel();
        mActivity.startActivity(firstActivityIntent);
        mActivity.finish();
    }
}
