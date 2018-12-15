package com.app.drylining.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.app.drylining.adapter.AddedOffersAdapter;
import com.app.drylining.adapter.SearchedOffersAdapter;
import com.app.drylining.custom.AdminBar;
import com.app.drylining.custom.AdminPanel;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomAutoCompleteListener;
import com.app.drylining.custom.MessageCntSetListener;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.data.Offer;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.AddNewOfferActivity;
import com.app.drylining.ui.DashboardActivity;
import com.app.drylining.ui.SearchNewOfferActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class AddNewOfferFragment extends Fragment implements RequestTaskDelegate {
    private static final String TAG = "AddNewOfferFragment";
    int Flag = 1;

    Button txtMyJob, txtRecentrlyAddJob, btn_new_search;
    private ApplicationData appData;
    private DashboardActivity activity;
    private View view = null;
    private TextView lblError, msgError;
    private RecyclerView recyclerView, recyclerView_new;
    private AddedOffersAdapter adapter;
    private SearchedOffersAdapter adapter1;
    private ArrayList<Offer> offerList, offerList1;
    private ProgressDialog pdialog;
    private String location;
    private TextView msg_success, txt_none_added_offer, txt_none_offers_new;
    private Button btnAddNewOffer;
    private AdminBar adminbar;

    private AdminPanel adminPanel;
    private LinearLayout adminLayout;

    private Animation animShow, animHide;

    private CountDownTimer lTimer;

    private boolean backPressedOneTime = false;

    private CustomAutoCompleteListener listener;

    private MessageCntSetListener messageCntSetListener;

    @SuppressLint("ValidFragment")
    public AddNewOfferFragment() {
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
            view = inflater.inflate(R.layout.fragment_add_new_offer, container, false);
            SharedPreferences pref = getActivity().getSharedPreferences("BackStack", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("Back", "Recent Job");
            editor.clear();// Storing string
            editor.apply();
            //toolbar = (Toolbar) view.findViewById(R.id.toolBar);
            adminLayout = (LinearLayout) view.findViewById(R.id.adminBar);
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            recyclerView_new = (RecyclerView) view.findViewById(R.id.recyclerView_new);
            msg_success = (TextView) view.findViewById(R.id.msg_success);
            txt_none_added_offer = (TextView) view.findViewById(R.id.txt_none_offers);
            txt_none_offers_new = (TextView) view.findViewById(R.id.txt_none_offers_new);
            txtMyJob = (Button) view.findViewById(R.id.txtMyJob);
            txtRecentrlyAddJob = (Button) view.findViewById(R.id.txtRecentrlyAddJob);
            btn_new_search = (Button) view.findViewById(R.id.txtRecentrlyAddJob);

            btnAddNewOffer = (Button) view.findViewById(R.id.btn_add_offer);


            messageCntSetListener = (MessageCntSetListener) this.getActivity();

            initialize();
            btnAddNewOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnAddNewOffer.getText().equals("Post Job")) {
                        OnNewOfferClicked();
                    } else {
                        OnNewSearchClicked();
                    }
                }
            });
        }
        clickRcentlyJob();
        clickMyJob();
        return view;

    }

    private void clickMyJob() {
        txtMyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView_new.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                txtRecentrlyAddJob.setBackgroundColor(getResources().getColor(R.color.white));
                txtMyJob.setBackgroundResource(R.drawable.app_board);
                btnAddNewOffer.setText("Post Job");
                sendSearchRequest();
                offerList.clear();
                Flag = 0;
            }
        });
    }

    private void clickRcentlyJob() {
        txtRecentrlyAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offerList1.clear();
                recyclerView.getRecycledViewPool().clear();
                recyclerView.setVisibility(View.GONE);
                recyclerView_new.setVisibility(View.VISIBLE);
                btnAddNewOffer.setText("New Search");
                txtMyJob.setBackgroundColor(getResources().getColor(R.color.white));
                txtRecentrlyAddJob.setBackgroundResource(R.drawable.app_board);
                sendGetProperties();
                Flag = 1;


            }
        });
    }

    private void OnNewSearchClicked() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            startActivity(new Intent(this.getActivity(), SearchNewOfferActivity.class));
        } else {
            Util.showNoConnectionDialog(this.getActivity());
        }
    }

    private void initialize() {
        msg_success.setVisibility(View.GONE);
        Intent intent = getActivity().getIntent();
        try {
            String isp = intent.getStringExtra("offerAdded");
            if (isp.equals("1"))
                msg_success.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            msg_success.setVisibility(View.GONE);
        }

        if (intent.getIntExtra("OFFER_REMOVED", 0) == 1) {
            msg_success.setVisibility(View.VISIBLE);
            msg_success.setText("YOUR OFFER HAS BEEN REMOVED");
        }

        if (intent.getIntExtra("offerUpdated", 0) == 1) {
            msg_success.setVisibility(View.VISIBLE);
            msg_success.setText("YOUR OFFER HAS BEEN MODIFIED");
        }
        appData = ApplicationData.getSharedInstance();
        activity = (DashboardActivity) getActivity();

        location = AppConstant.NULL_STRING;
        offerList = new ArrayList<Offer>();
        offerList1 = new ArrayList<Offer>();

        //setToolbar();

        animShow = AnimationUtils.loadAnimation(this.getActivity(), R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this.getActivity(), R.anim.view_hide);
    }

    public void OnNewOfferClicked() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            startActivity(new Intent(activity, AddNewOfferActivity.class));
            activity.finish();
        } else {
            Util.showNoConnectionDialog(activity);
        }
    }

    @Override
    public void onResume() {
        AppDebugLog.println("In resume of LoginFragment");
        super.onResume();
        offerList.clear();
        offerList1.clear();

    /*    try {
            SharedPreferences pref = getActivity().getSharedPreferences("BackStack", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            // pref.getString("AddActivity", "");
            Log.e(TAG, "onResume:././././.// " + pref.getString("Back", ""));
            if (pref.getString("Back", "").equals("Recent Job")) {

            } else {
                sendSearchRequest();

            }

        } catch (Exception e) {
            sendGetProperties();
        }*/
        if (Flag == 0)
        {
            sendSearchRequest();
        }
        else
        {
            sendGetProperties();

        }
        //sendGetProperties();
    }

    private void sendGetProperties() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            Flag = 1;
            showProgressDialog();

            RequestTask requestTask = new RequestTask(AppConstant.GET_LAST_SEARCH, AppConstant.HttpRequestType.GetLastSearch);
            requestTask.delegate = AddNewOfferFragment.this;

            requestTask.execute(AppConstant.GET_LAST_SEARCH + "?" + "senderId=" + "58");
        }
    }

    private void sendSearchRequest() {

        Flag = 0;
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            showProgressDialog();
            RequestTask requestTask = new RequestTask(AppConstant.GET_PROPERTIES, AppConstant.HttpRequestType.getProperties);
            requestTask.delegate = AddNewOfferFragment.this;//AddedOffersActivity.this;
            String userId = appData.getUserId();
            requestTask.execute(AppConstant.GET_PROPERTIES + userId);
        }

    }

    private void setRecyclerView() {
        if (adapter == null) {
            AppDebugLog.println("location in setRecyclerView:" + this.location);
            adapter = new AddedOffersAdapter(this.getActivity(), offerList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);

        }
        adapter.notifyDataSetChanged();
        txt_none_offers_new.setVisibility(View.GONE);
        if (offerList.size() == 0) {
            txt_none_added_offer.setVisibility(View.VISIBLE);
        } else {
            txt_none_added_offer.setVisibility(View.GONE);
        }
    }

    private void setRecyclerView_new() {
        try {
            if (adapter1 == null) {
                adapter1 = new SearchedOffersAdapter(this.getActivity(), offerList1);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
                recyclerView_new.setLayoutManager(mLayoutManager);
                recyclerView_new.setItemAnimator(new DefaultItemAnimator());
                recyclerView_new.setAdapter(adapter1);
            }
            adapter1.notifyDataSetChanged();
            txt_none_added_offer.setVisibility(View.GONE);
            if (offerList1.size() == 0) {
                txt_none_offers_new.setVisibility(View.VISIBLE);
            } else {
                txt_none_offers_new.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        JSONObject properties_response = null;
        if (Flag == 0) {
            try {
                properties_response = new JSONObject(response);

                Log.e("Property Response", response.toString());

                String status = properties_response.getString("status");
                AppDebugLog.println("Properties status is " + status);
                if (status.equals("failed")) {
                    txt_none_added_offer.setVisibility(View.VISIBLE);

                }
                JSONArray properties_array = properties_response.getJSONArray("result");
                JSONArray msgNumArray = properties_response.getJSONArray("msgs");
                JSONArray isUnReadMsgArray = properties_response.getJSONArray("isUnReadMsgs");

                String cnt_notifications = properties_response.getString("notifications");
                appData.setCntMessages(cnt_notifications);

                messageCntSetListener.messageCntSet(cnt_notifications);

                for (int i = 0; i < properties_array.length(); i++) {
                    JSONObject property = properties_array.getJSONObject(i);
                    int id = Integer.parseInt(property.getString("id").toString());
                    String name = property.getString("name");
                    String work_type = property.getString("work_type");
                    String price = property.getString("price").toString();
                    String currency_type = property.getString("currency_type");
                    Double longitude = Double.parseDouble(property.getString("longitude").toString());
                    Double latitude = Double.parseDouble(property.getString("latitude").toString());
                    int job_status = Integer.parseInt(property.getString("job_status").toString());
                    String job_statusCon = "";
                    if (job_status == 0) {
                        job_statusCon = "Interested: " + property.getString("countInterest");
                    } else if (job_status == 1) {
                        job_statusCon = "Progress by " + property.getString("worker");
                    }
                    Integer conversations = property.getInt("conversation_count");

                    Offer offer = new Offer(id, name, longitude, latitude, price, work_type, "");
                    offer.setConversations(conversations);
                    offer.setJobStatus(job_status);
                    offer.setInterested(job_statusCon);
                    offer.setCurrency(currency_type);
                    // /offer.setConversations(msgNumArray.getInt(i));
                    offer.setIsUnreadMsg(isUnReadMsgArray.getInt(i));
                    offerList.add(offer);
                }
                setRecyclerView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //  JSONObject properties_response = null;
            try {
                properties_response = new JSONObject(response);
                String status = properties_response.getString("status");
                AppDebugLog.println("Properties status is " + status);
                int result_count = Integer.parseInt(properties_response.getString("count"));

                if (result_count > 0) {
                    JSONArray properties_array = properties_response.getJSONArray("result");
                    JSONArray msgNumArray = properties_response.getJSONArray("msgs");
                    for (int i = 0; i < properties_array.length(); i++) {
                        if (i == 31) {
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
                        String mUserIdPostedJob = property.getString("user_id");

                        double distance = property.optDouble("distance");

                        Offer offer = new Offer(id, name, longitude, latitude, price, job_type, image_path, mUserIdPostedJob);
                        offer.setDistance(distance);
                        offer.setCurrency(currency_type);
                        offer.setInterested(job_statuse);
                        offer.setPostCity(city);
                        offer.setPostCity(mUserIdPostedJob);

                        offer.setConversations(msgNumArray.getInt(i));

                        offerList1.add(offer);
                    }
                }

                String cnt_notifications = properties_response.getString("notifications");
                appData.setCntMessages(cnt_notifications);

                messageCntSetListener.messageCntSet(cnt_notifications);

                setRecyclerView_new();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "backgroundActivityComp: " + e.getMessage());
            }
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
}
