package com.app.drylining.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.custom.CustomViewPager;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.fragment.LoginFragment;
import com.app.drylining.fragment.SignUpFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.app.drylining.data.ApplicationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends CustomMainActivity implements TabLayout.OnTabSelectedListener{

    private ApplicationData appData;
    private Locale locale;

    private int currentTabIndex;
    private TabLayout tabLayout;

    private AdminBar adminbar;
    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private CountDownTimer lTimer;

    private CustomViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private LoginFragment loginFragment;
    private SignUpFragment signUpFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);

        initialize();
    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("", "Refreshed token: " + token);
        AppDebugLog.println("FCM Token in onTokenRefresh : " + token);
        appData.setGCMTokenId(token);

        Resources resources = getResources();
        if (appData.getLocale() != null){
            locale = appData.getLocale();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        };
        setInitialUI();
    }

    private void setInitialUI() {
        initializeFragments();
        setupViewPager();
    }

    @Override
    public void openAdminPanel()
    {
        adminLayout.setVisibility(View.VISIBLE);
        adminLayout.startAnimation( animShow );

        lTimer = new CountDownTimer(AppConstant.ADMIN_BAR_OPEN_TIME, 100) {

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
            showConfirmExitAlert(this, "", "Are you sure to exit this app?", this);
        }
    }

    private void initializeFragments() {
        loginFragment = new LoginFragment();
        signUpFragment = new SignUpFragment();
    }

    private void setupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(loginFragment, getResources().getString(R.string.tab_title_login));
        viewPagerAdapter.addFragment(signUpFragment, getResources().getString(R.string.tab_title_signup));

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
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

    public void tabChanged(TabLayout.Tab tab) {
        int position = tab.getPosition();
        currentTabIndex = position;

        AppDebugLog.println("In tabChanged : " + currentTabIndex);
        switch (currentTabIndex) {
            case AppConstant.TAB_LOGIN:
                loginFragment.tabChanged();
                break;
            case AppConstant.TAB_SIGNUP:
                signUpFragment.tabChanged();
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
            case AppConstant.TAB_LOGIN:
                loginFragment.tabUnSelected();
                break;
            case AppConstant.TAB_SIGNUP:
                signUpFragment.tabUnSelected();
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

    public static void showConfirmExitAlert(final Context context, String title, String message, final Activity contextToFinish)
    {
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

}
