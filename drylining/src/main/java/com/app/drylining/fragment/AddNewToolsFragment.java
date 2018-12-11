package com.app.drylining.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.app.drylining.adapter.AddedToolsAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomAutoCompleteListener;
import com.app.drylining.custom.MessageCntSetListener;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Tool;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.AddNewToolActivity;
import com.app.drylining.ui.DashboardActivity;
import com.app.drylining.ui.SearchNewToolActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class AddNewToolsFragment extends Fragment implements RequestTaskDelegate
{
    private ApplicationData appData;
    private DashboardActivity activity;
    private View view = null;

    private TextView lblError, msgError;

    private RecyclerView recyclerView;
    private AddedToolsAdapter adapter;
    private ArrayList<Tool> toolList;
    private ProgressDialog pdialog;
    private String location;
    private TextView msg_success, txt_none_added_offer;
    private Button btnNewSearch, btnAddNewTool;

    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private CountDownTimer lTimer;

    private boolean backPressedOneTime = false;

    private MessageCntSetListener messageCntSetListener;

    private CustomAutoCompleteListener listener;



    @SuppressLint("ValidFragment")
    public AddNewToolsFragment() {
        // Required empty public constructor
    }

    public void tabChanged() {
        AppDebugLog.println("In tabChanged of AddOfferByAddressFragment : ");
    }

    public void tabUnSelected() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AppDebugLog.println("In onCreateView of AddOfferByAddressFragment : ");
        // Inflate the layout for this fragment
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_add_new_tools, container, false);

            //toolbar = (Toolbar) view.findViewById(R.id.toolBar);
            adminLayout= (LinearLayout) view.findViewById(R.id.adminBar);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            msg_success = (TextView) view.findViewById(R.id.msg_success);
            txt_none_added_offer = (TextView) view.findViewById(R.id.txt_none_offers);

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

            messageCntSetListener = (MessageCntSetListener) this.getActivity();

            /*AppDebugLog.println("stored location :" + ApplicationData.getSharedInstance().getCurrentLocation());
            LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener()
            {
                public void onLocationChanged(Location location)
                {
                    // Called when a new location is found by the network location provider.
                    AppDebugLog.println("Current Longitude:" + location.getLongitude());
                    AppDebugLog.println("Current latitude:" + location.getLatitude());

                    ApplicationData.getSharedInstance().setCurrentLocation(location.getLatitude() + "," + location.getLongitude());

                    if (adapter != null)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider)
                {
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
        msg_success.setVisibility(View.GONE);
        Intent intent = getActivity().getIntent();
        try
        {
            String isp = intent.getStringExtra("offerAdded");
            if(isp.equals("1"))
                msg_success.setVisibility(View.VISIBLE);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            msg_success.setVisibility(View.GONE);
        }

        if(intent.getIntExtra("OFFER_REMOVED", 0) == 1)
        {
            msg_success.setVisibility(View.VISIBLE);
            msg_success.setText("YOUR OFFER HAS BEEN REMOVED");
        }

        if(intent.getIntExtra("offerUpdated", 0) == 1)
        {
            msg_success.setVisibility(View.VISIBLE);
            msg_success.setText("YOUR OFFER HAS BEEN MODIFIED");
        }
        appData = ApplicationData.getSharedInstance();
        activity = (DashboardActivity) getActivity();

        location = AppConstant.NULL_STRING;
        toolList = new ArrayList<Tool>();

        //setToolbar();

        animShow = AnimationUtils.loadAnimation( this.getActivity(), R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this.getActivity(), R.anim.view_hide);
    }

    public void OnNewToolClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            startActivity(new Intent(activity, AddNewToolActivity.class));
            //activity.finish();
        }
        else{
            Util.showNoConnectionDialog(activity);
        }
    }



    @Override
    public void onResume()
    {
        AppDebugLog.println("In resume of LoginFragment");
        super.onResume();
        toolList.clear();

        sendGetProperties();
    }

    private void sendGetProperties()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_RECENT_TOOLS, AppConstant.HttpRequestType.RecentTools);
            requestTask.delegate = AddNewToolsFragment.this;//AddedOffersActivity.this;
            String userId = appData.getUserId();
            requestTask.execute(AppConstant.GET_RECENT_TOOLS + "?" + "senderId=" + appData.getUserId() + "&userType=" + appData.getUserType());
        }
    }

    private void setRecyclerView()
    {
        if (adapter == null)
        {
            AppDebugLog.println("location in setRecyclerView:" + this.location);
            //adapter = new AddedOffersAdapter(this, offerList);
            adapter = new AddedToolsAdapter(this.getActivity(), toolList);
            //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();

        if(toolList.size() == 0)
        {
            txt_none_added_offer.setVisibility(View.VISIBLE);
        }
        else{
            txt_none_added_offer.setVisibility(View.GONE);
        }
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject properties_response = null;
        try
        {
            properties_response = new JSONObject(response);

            Log.e("Property Response",response.toString());

            String status = properties_response.getString("status");
            AppDebugLog.println("Properties status is " + status);
            JSONArray properties_array = properties_response.getJSONArray("result");
            JSONArray msgNumArray = properties_response.getJSONArray("msgs");
            JSONArray isUnReadMsgArray = properties_response.getJSONArray("isUnReadMsgs");

            String cnt_notifications = properties_response.getString("notifications");
            appData.setCntMessages(cnt_notifications);

            messageCntSetListener.messageCntSet(cnt_notifications);
            //adminbar.setMessages(cnt_notifications);

            //adminbar.setUserName(appData.getUserName());
            //adminbar.setUserId(appData.getUserId());

            for (int i = 0; i < properties_array.length(); i++)
            {
                JSONObject property = properties_array.getJSONObject(i);
                int id = Integer.parseInt(property.getString("id").toString());
                String name = property.getString("name");
                String posterId = property.getString("user_id");
                String posterType = property.getString("user_type");
                String tool_type = property.getString("tool_type");
                Long price = Long.parseLong(property.getString("price").toString());
                String currency_type = property.getString("currency_type");
                Double longitude = Double.parseDouble(property.getString("longitude").toString());
                Double latitude = Double.parseDouble(property.getString("latitude").toString());
                String image_path = property.getString("image_path");

                Tool tool = new Tool(id, name, longitude, latitude, price, tool_type, image_path);
                tool.setPosterId(posterId);
                tool.setPosterType(posterType);
                //tool.setConversations(msgNumArray.getInt(i));
                //tool.setIsUnreadMsg(isUnReadMsgArray.getInt(i));
                tool.setCurrency(currency_type);
                toolList.add(tool);
            }
            setRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void timeOut()
    {

    }

    @Override
    public void codeError(int code)
    {

    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record)
    {

    }

    private void showProgressDialog() {
        if (!activity.isFinishing() && pdialog == null) {
            pdialog = ProgressDialog.show(activity, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog() {
        if (pdialog != null && !activity.isFinishing()) {
            pdialog.dismiss();
            pdialog = null;
        }
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
