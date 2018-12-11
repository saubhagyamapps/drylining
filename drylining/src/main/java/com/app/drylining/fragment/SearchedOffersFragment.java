package com.app.drylining.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.adapter.SearchedOffersAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.MessageCntSetListener;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.network.SearchOfferParameters;
import com.app.drylining.ui.SearchActivity;
import com.app.drylining.ui.SearchNewOfferActivity;
import com.app.drylining.utils.AppInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchedOffersFragment extends Fragment implements RequestTaskDelegate
{
    private ApplicationData appData;
    private Toolbar toolbar;
    private SearchActivity activity;
    private View view = null;

    private RecyclerView recyclerView;
    private SearchedOffersAdapter adapter;
    private ArrayList<Offer> offerList, filteredList;
    private ArrayList<Offer> offerListTmp;
    private ProgressDialog pdialog;
    private Button btnNewSearch;
    private TextView txtNoRecentSearch;

    private String preSearchCity, preSearchRange, preSearchProperty, preSearchRoom, preSearchGarage, PreSearchCityLocation;
    private int preSearchMinPrice, preSearchMaxPrice;
    private double preSearchRangeValue = 0;

    private SearchOfferParameters preSearchParams;

    private LinearLayout mLinearScroll;

    int offersPerPage = 10;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private MessageCntSetListener messageCntSetListener;

    TextView jobTitle;

    @SuppressLint("ValidFragment")
    public SearchedOffersFragment() {
        // Required empty public constructor
    }

    public void tabChanged() {
        AppDebugLog.println("In tabChanged of AddOfferByAddressFragment : ");
    }

    public void tabUnSelected() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppDebugLog.println("In onCreateView of AddOfferByAddressFragment : ");
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_searched_offers, container, false);
            //toolbar = (Toolbar) findViewById(R.id.toolBar);
            adminLayout = (LinearLayout) view.findViewById(R.id.adminBar);

            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

            txtNoRecentSearch = (TextView) view.findViewById(R.id.txtNoRecentSearch);

            btnNewSearch = (Button) view.findViewById(R.id.btn_new_search);
            btnNewSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnNewSearchClicked();
                }
            });

            mLinearScroll = (LinearLayout) view.findViewById(R.id.linear_scroll);

            messageCntSetListener = (MessageCntSetListener) this.getActivity();

            jobTitle = view.findViewById(R.id.job_title);
            if(AppInfo.getInstance().jobType() == AppInfo.JOBS) {
                jobTitle.setText(R.string.lbl_jobs);
            }
            else {
                jobTitle.setText(R.string.txt_my_jobs);
            }

            initialize();
        }
        return view;
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();

        //setToolbar();

        txtNoRecentSearch.setVisibility(View.GONE);

        animShow = AnimationUtils.loadAnimation( this.getActivity(), R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this.getActivity(), R.anim.view_hide);

        activity = (SearchActivity) getActivity();
        offerList = new ArrayList<Offer>();
        filteredList = new ArrayList<Offer>();

        sendSearchRequest();
    }

    private SearchOfferParameters getPreSearchParams()
    {
        preSearchCity = appData.getPreCityName();
        preSearchRange = appData.getPreRange();
        preSearchProperty = appData.getPreProperty();
        preSearchRange = appData.getPreRange();
        preSearchRoom = appData.getPreRoom();
        preSearchGarage = appData.getPreGarage();
        preSearchMinPrice = appData.getPreMinPrice();
        preSearchMaxPrice = appData.getPreMaxPrice();
        PreSearchCityLocation = appData.getPreCityLocation();

        preSearchRangeValue = getRangeValue(preSearchRange);

        return new SearchOfferParameters(preSearchCity, preSearchProperty,
                preSearchGarage, preSearchMinPrice, preSearchMaxPrice, PreSearchCityLocation);
    }

    private double getRangeValue(String searchRange)
    {
        int i;
        String[] array = getResources().getStringArray(R.array.search_range);
        for( i = 0 ; i<array.length ; i++)
        {
            if (array[i].equals(searchRange))
                break;
        }
        switch (i)
        {
            case 0:
                return 1000;
            case 1:
                return 5000;
            case 2:
                return 15000;
            case 3:
                return 30000;
            case 4:
                return 50000;
            default:
                return 0;
        }
    }

    private void sendSearchRequest()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();

            if(AppInfo.getInstance().jobType() == AppInfo.JOBS) {
                RequestTask requestTask = new RequestTask(AppConstant.GET_LAST_SEARCH, AppConstant.HttpRequestType.GetLastSearch);
                requestTask.delegate = SearchedOffersFragment.this;

                requestTask.execute(AppConstant.GET_OFFER_LESSEE + appData.getUserId() + "$userType=" + appData.getUserType());
            }
            else {
                RequestTask requestTask = new RequestTask(AppConstant.GET_LAST_SEARCH, AppConstant.HttpRequestType.GetLastSearch);
                requestTask.delegate = SearchedOffersFragment.this;

                requestTask.execute(AppConstant.GET_LAST_SEARCH + "?" + "senderId=" + appData.getUserId());
            }
        }
        else
        {

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
        if (pdialog != null && !activity.isFinishing()) {
            pdialog.dismiss();
            pdialog = null;
        }

    }

    private void setRecyclerView()
    {
        if (adapter == null)
        {
            adapter = new SearchedOffersAdapter(this.getActivity(), offerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }

        if(offerList.size() == 0)
            txtNoRecentSearch.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject properties_response = null;
        try
        {
            properties_response = new JSONObject(response);
            String status = properties_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            int result_count = Integer.parseInt(properties_response.getString("count"));

            if(result_count > 0)
            {
                JSONArray properties_array = properties_response.getJSONArray("result");
                JSONArray msgNumArray = properties_response.getJSONArray("msgs");
                for (int i = 0; i < properties_array.length(); i++) {
                    if(i == 31) {
                        int a = 0;
                    }
                    JSONObject property = properties_array.getJSONObject(i);
                    int id = Integer.parseInt(property.getString("id").toString());
                    String name = property.getString("name");
                    String job_type = property.getString("job_type");
                    String price = property.getString("price").toString();
                    String currency_type = property.getString("currency_type");
                    Double longitude = Double.parseDouble(property.getString("longitude").toString());
                    Double latitude = Double.parseDouble(property.getString("latitude").toString());
                    String image_path = property.getString("image_path");
                    String job_statuse = property.getString("my_state");
                    String city = property.getString("postcity");

                    double distance = property.optDouble("distance");

                    Offer offer = new Offer(id, name, longitude, latitude, price, job_type, image_path);
                    offer.setDistance(distance);
                    offer.setCurrency(currency_type);
                    offer.setInterested(job_statuse);
                    offer.setPostCity(city);

                    offer.setConversations(msgNumArray.getInt(i));

                    offerList.add(offer);
                }
            }

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);

            messageCntSetListener.messageCntSet(cnt_notifications);

            setRecyclerView();
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

    private void OnNewSearchClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            startActivity(new Intent(this.getActivity(), SearchNewOfferActivity.class));
        }
        else{
            Util.showNoConnectionDialog(this.getActivity());
        }
    }
}
