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
import com.app.drylining.adapter.SearchedToolsAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.MessageCntSetListener;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Tool;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.network.SearchOfferParameters;
import com.app.drylining.ui.AddNewToolActivity;
import com.app.drylining.ui.SearchActivity;
import com.app.drylining.ui.SearchNewToolActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchedToolsFragment extends Fragment implements RequestTaskDelegate
{
    private ApplicationData appData;
    private Toolbar toolbar;
    private SearchActivity activity;
    private View view = null;

    private RecyclerView recyclerView;
    private SearchedToolsAdapter adapter;
    private ArrayList<Tool> offerList, filteredList;
    private ArrayList<Tool> offerListTmp;
    private ProgressDialog pdialog;
    private Button btnNewSearch, btnAddNewTool;
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

    @SuppressLint("ValidFragment")
    public SearchedToolsFragment() {
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
            view = inflater.inflate(R.layout.fragment_searched_tools, container, false);
            //toolbar = (Toolbar) findViewById(R.id.toolBar);
            adminLayout = (LinearLayout) view.findViewById(R.id.adminBar);

            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

            txtNoRecentSearch = (TextView) view.findViewById(R.id.txtNoRecentSearch);

            btnAddNewTool = (Button) view.findViewById(R.id.btn_add_tool);
            btnAddNewTool.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    OnNewToolClicked();
                }
            });

            btnNewSearch = (Button) view.findViewById(R.id.btn_new_search);
            btnNewSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnNewSearchClicked();
                }
            });

            mLinearScroll = (LinearLayout) view.findViewById(R.id.linear_scroll);

            messageCntSetListener = (MessageCntSetListener) this.getActivity();
/*

            AppDebugLog.println("stored location :" + ApplicationData.getSharedInstance().getCurrentLocation());

            // if (ApplicationData.getSharedInstance().getCurrentLocation().equalsIgnoreCase("0,0")) {
            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    AppDebugLog.println("Current Longitude:" + location.getLongitude());
                    AppDebugLog.println("Current latitude:" + location.getLatitude());
                    // if (ApplicationData.getSharedInstance().getCurrentLocation().equalsIgnoreCase("0,0")) {
                    ApplicationData.getSharedInstance().setCurrentLocation(location.getLatitude() + "," + location.getLongitude());
                    // Toast.makeText(getBaseContext(), "Current Longitude:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    // }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            };

            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return view;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
*/

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
        offerList = new ArrayList<Tool>();
        filteredList = new ArrayList<Tool>();

        sendSearchRequest();
    }

    public void OnNewToolClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            startActivity(new Intent(activity, AddNewToolActivity.class));
            activity.finish();
        }
        else{
            Util.showNoConnectionDialog(activity);
        }
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
            RequestTask requestTask = new RequestTask(AppConstant.GET_RECENT_TOOLS, AppConstant.HttpRequestType.RecentTools);
            requestTask.delegate = SearchedToolsFragment.this;

            requestTask.execute(AppConstant.GET_RECENT_TOOLS + "?" + "senderId=" + appData.getUserId() + "&userType=" + appData.getUserType());

            //requestTask.execute(AppConstant.GET_RECENT_TOOLS + "?" + "senderId=" + appData.getUserId());
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
            adapter = new SearchedToolsAdapter(this.getActivity(), offerList);
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
            int result_count = properties_response.getInt("count");

            if(result_count > 0)
            {
                JSONArray properties_array = properties_response.getJSONArray("result");
                JSONArray msgNumArray = properties_response.getJSONArray("msgs");
                for (int i = 0; i < properties_array.length(); i++) {
                    JSONObject property = properties_array.getJSONObject(i);
                    int id = Integer.parseInt(property.getString("id").toString());
                    String name = property.getString("name");
                    String tool_type = property.getString("tool_type");
                    Long price = Long.parseLong(property.getString("price").toString());
                    String currency_type = property.getString("currency_type");
                    Double longitude = Double.parseDouble(property.getString("longitude").toString());
                    Double latitude = Double.parseDouble(property.getString("latitude").toString());
                    String image_path = property.getString("image_path");

                    double distance = property.optDouble("distance");

                    Tool offer = new Tool(id, name, longitude, latitude, price, tool_type, image_path);
                    //offer.setDistance(distance);
                    offer.setCurrency(currency_type);

                    offer.setConversations(msgNumArray.getInt(i));

                    offerList.add(offer);
                }
            }

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);
            messageCntSetListener.messageCntSet(cnt_notifications);

            //adminbar.setMessages(cnt_notifications);

            //adminbar.setUserName(appData.getUserName());

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
            startActivity(new Intent(this.getActivity(), SearchNewToolActivity.class));
        }
        else{
            Util.showNoConnectionDialog(this.getActivity());
        }
    }
}
