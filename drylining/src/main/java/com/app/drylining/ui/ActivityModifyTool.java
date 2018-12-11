package com.app.drylining.ui;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.app.drylining.R;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.custom.CustomViewPager;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.fragment.ModifyToolByAddressFragment;
import com.app.drylining.fragment.ModifyToolPickFromMapFragment;

import java.util.ArrayList;
import java.util.List;

public class ActivityModifyTool extends CustomMainActivity implements TabLayout.OnTabSelectedListener
{
    private int currentTabIndex;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private CustomViewPager viewPager;
    private ActivityModifyTool.ViewPagerAdapter viewPagerAdapter;

    private ModifyToolByAddressFragment modifyToolByAddressFragment;
    private ModifyToolPickFromMapFragment modifyToolPickFromMapFragment;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private ApplicationData appData;

    private Animation animShow, animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_tool);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        adminLayout= (LinearLayout) findViewById(R.id.adminBar);
        initialize();
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        setToolbar();

        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);

        setInitialUI();
    }

    @Override
    public void onBackPressed()
    {
        if(adminLayout.getVisibility() == View.VISIBLE)
            closeAdminPanel();
        else
        {
            super.onBackPressed();

            Intent returnIntent = new Intent(this, AddedToolDetailActivity.class);
            returnIntent.putExtra("ToolID", getIntent().getIntExtra("SELECTED_TOOL_ID", -2));
            startActivity(returnIntent);

            this.finish();
        }
    }

    private void setInitialUI()
    {
        initializeFragments();
        setupViewPager();
    }

    private void initializeFragments()
    {
        modifyToolByAddressFragment = new ModifyToolByAddressFragment();
        modifyToolPickFromMapFragment = new ModifyToolPickFromMapFragment();
    }

    private void setupViewPager()
    {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(modifyToolByAddressFragment, getString(R.string.tab_title_modify_tool_by_address));
        //viewPagerAdapter.addFragment(modifyToolPickFromMapFragment, getString(R.string.tab_title_add_offer_pick_from_map));

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
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

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
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
                Intent firstActivityIntent = new Intent(ActivityModifyTool.this, DashboardActivity.class);
                firstActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityModifyTool.this.startActivity(firstActivityIntent);
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

    public void tabChanged(TabLayout.Tab tab)
    {
        int position = tab.getPosition();
        currentTabIndex = position;

        AppDebugLog.println("In tabChanged : " + currentTabIndex);
        switch (currentTabIndex)
        {
            case AppConstant.TAB_MODIFY_OFFER_BY_ADDRESS:
                modifyToolByAddressFragment.tabChanged();
                break;
            case AppConstant.TAB_MODIFY_OFFER_PICK_FROM_MAP:
                modifyToolPickFromMapFragment.tabChanged();
                break;

            default:
                break;
        }
        viewPager.setCurrentItem(position, true);
    }

    private void tabUnSelected(TabLayout.Tab tab)
    {
        int position = tab.getPosition();
        currentTabIndex = position;
        AppDebugLog.println("In tabUnSelected : " + currentTabIndex);
        switch (currentTabIndex)
        {
            case AppConstant.TAB_MODIFY_OFFER_BY_ADDRESS:
                modifyToolByAddressFragment.tabUnSelected();
                break;
            case AppConstant.TAB_MODIFY_OFFER_PICK_FROM_MAP:
                modifyToolPickFromMapFragment.tabUnSelected();
                break;
            default:
                break;
        }

        viewPager.setCurrentItem(position, true);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        tabChanged(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab)
    {
        tabUnSelected(tab);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab)
    {

    }
}
