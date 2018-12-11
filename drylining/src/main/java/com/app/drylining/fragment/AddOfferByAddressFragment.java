package com.app.drylining.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomAutoCompleteListener;
import com.app.drylining.custom.CustomSpinnerAdapter;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.AddNewOfferActivity;
import com.app.drylining.ui.DashboardActivity;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddOfferByAddressFragment extends Fragment implements RequestTaskDelegate
{
    private ApplicationData appData;
    private AddNewOfferActivity activity;
    private View view = null;

    private ProgressDialog pdialog;

    private TextView lblError, msgError;
    private AutoCompleteTextView txtCityName;
    private AppCompatEditText txtPostName, txtStreet, txtUnity, txtDescription, txtContactName, txtContactPhoneNumber;
    private TextView tPrice;

    private LayoutInflater inflater;

    private RelativeLayout spCountryContainer, spPropContainer, spRoomContainer, spGarageContainer;
    private Spinner spCountry, spJobType, spRoom, spWorkType;
    private CustomSpinnerAdapter spCountryAdapter, spPropertyAdapter, spRoomAdapter, spTypeAdapter;

    private String postCountry, postJobType, postCurrentType, postWorkType, contactName, contactPhoneNumber;

    private Button btnAddOffer;

    private CustomAutoCompleteListener listener;

    public AddOfferByAddressFragment() {
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
            view = inflater.inflate(R.layout.fragment_add_offer_by_address, container, false);

            lblError = (TextView) view.findViewById(R.id.lbl_error);
            txtPostName = (AppCompatEditText) view.findViewById(R.id.txt_post_name);
            txtCityName = (AutoCompleteTextView) view.findViewById(R.id.txt_city_name);
            txtStreet = (AppCompatEditText) view.findViewById(R.id.txt_street_name);
            txtDescription = (AppCompatEditText) view.findViewById(R.id.txt_description);
            txtContactName = (AppCompatEditText) view.findViewById(R.id.txt_contact_name);
            txtContactPhoneNumber = (AppCompatEditText) view.findViewById(R.id.txt_contact_mobile);
            tPrice = (TextView) view.findViewById(R.id.txt_price);
            msgError = (TextView) view.findViewById(R.id.msg_error);

            spCountryContainer = (RelativeLayout) view.findViewById(R.id.sp_CountryContainer);
            spPropContainer = (RelativeLayout) view.findViewById(R.id.sp_PropContainer);
            spRoomContainer = (RelativeLayout) view.findViewById(R.id.sp_RoomTypeContainer);
            spGarageContainer = (RelativeLayout) view.findViewById(R.id.sp_GarageContainer);

            spCountry = (Spinner) view.findViewById(R.id.spinnerCountry);
            spJobType = (Spinner) view.findViewById(R.id.spinnerProperty);
            spRoom = (Spinner) view.findViewById(R.id.spinnerRoom);
            spWorkType = (Spinner) view.findViewById(R.id.spinnerType);
            btnAddOffer = (Button) view.findViewById(R.id.btn_add_offer);

            setHasOptionsMenu(true);

            initialize();

        }
        return view;
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = (AddNewOfferActivity) getActivity();

        listener=new CustomAutoCompleteListener(getContext(),txtCityName,true);
        txtCityName.addTextChangedListener(listener);

        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

        spCountryAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.country_names));
        spCountry.setAdapter(spCountryAdapter);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if(tv != null) tv.setText(tv.getText());

                postCountry = getResources().getStringArray(R.array.country_names)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spPropertyAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.property_types));
        spJobType.setAdapter(spPropertyAdapter);
        spJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if (tv != null) tv.setText("Type of job - "+tv.getText());

                postJobType = getResources().getStringArray(R.array.property_types)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spJobType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int width= txtCityName.getWidth();
                spJobType.setDropDownWidth(width);
                return false;
            }
        });

        spRoomAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.currency_types));
        spRoom.setAdapter(spRoomAdapter);
        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if (tv != null) tv.setText(tv.getText());

                postCurrentType = getResources().getStringArray(R.array.currency_types)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spRoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int width= txtCityName.getWidth() * 3 / 7;
                spRoom.setDropDownWidth(width);
                return false;
            }
        });

        spTypeAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.property_extra_spaces));
        spWorkType.setAdapter(spTypeAdapter);
        spWorkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if(tv != null) tv.setText("Type of work - "+tv.getText());

                postWorkType = getResources().getStringArray(R.array.property_extra_spaces)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spWorkType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int width= txtCityName.getWidth();
                spWorkType.setDropDownWidth(width);
                return false;
            }
        });

       /* txtContactName.setText(appData.getUserName());
        txtContactPhoneNumber.setText(appData.getPhoneNumber());*/

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(checkFields())
                {
                    if(AppConstant.IS_PAYMENT_ENABLE == false)
                    {
                        sendAddOfferRequest();
                    }
                    else
                    {
                    }
                }
                else
                {
                    lblError.setVisibility(View.VISIBLE);
                    msgError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void sendAddOfferRequest()
    {
        String postCity = txtCityName.getText().toString();
        String postStreet = txtStreet.getText().toString();
        String postName = txtPostName.getText().toString();
        String postContactName = txtContactName.getText().toString();
        String postMobile = txtContactPhoneNumber.getText().toString();
        String postCountry = "";
        String postJobType = "";
        String postWorkType = "";
        String postCurrencyType = "";

        String postDesc = txtDescription.getText().toString();
        String postPrice = tPrice.getText().toString();
        String postUserId = appData.getUserId();

        postName = "name=" + postName;
        postPrice = "price=" + postPrice;
        postCurrencyType = "currency=" + this.postCurrentType;
        postCurrencyType = postCurrencyType.substring(0, postCurrencyType.indexOf('('));
        postCountry = "country=" + this.postCountry;
        postCity = "city=" + postCity;
        postStreet = "street=" + postStreet;
        postJobType = "jobType=" + this.postJobType;
        postWorkType = "workType=" + this.postWorkType;
        postDesc = "description=" + postDesc;
        postContactName = "contactName="  + postContactName;
        postMobile = "contactMobile=" + postMobile;

        //String location = appData.getCurrentLocation();
        Double currentLatitude = 0.0;
        Double currentLongitude = 0.0;
        /*if (location.length() > 0) {
            currentLatitude = Double.parseDouble(location.split(",")[0]);
            currentLongitude = Double.parseDouble(location.split(",")[1]);
        }*/

        String postLongitude = "postLongitude~" + currentLongitude;
        String postLatitude = "postLatitude~" + currentLatitude;

        postUserId = "postUserId=" + postUserId;
        String postAddType = "postAddType=" + "AddByAddress";

        if (appData.getConnectionDetector().isConnectingToInternet()) {
            showProgressDialog();
            lblError.setVisibility(View.GONE);
            msgError.setVisibility(View.GONE);
            RequestTask requestTask = new RequestTask(AppConstant.ADD_PROPERTY, AppConstant.HttpRequestType.addProperty);
            requestTask.delegate = AddOfferByAddressFragment.this;
            requestTask.execute(AppConstant.ADD_PROPERTY, postName + "&" + postPrice + "&" + postCurrencyType + "&" + postCountry + "&" + postCity + "&" + postStreet + "&" + postJobType + "&" + postWorkType + "&" +
                    postDesc + "&" + postAddType + "&" + postUserId + "&" + postContactName + "&" + postMobile);
            //Log.e("request;", postName + "&" + postPrice + "&" + postCity + "&" + postStreet + "&" + postJobType + "&" + postWorkType + "&" + postDesc + "&" + postAddType);
        } else {
            Util.showNoConnectionDialog(getActivity());
        }
    }

    public boolean checkFields()
    {
        boolean isValid = true;
        if(txtCityName.getText().toString().trim().equals(""))
        {
            msgError.setText("Please correct the below fields.");
            txtCityName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }

        if(txtStreet.getText().toString().trim().equals(""))
        {
            msgError.setText("Please correct the below fields.");
            txtStreet.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }

        if(txtDescription.getText().toString().trim().equals(""))
        {
            msgError.setText("Please correct the below fields.");
            txtDescription.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }

        if(tPrice.getText().toString().trim().length()==0)
        {
            msgError.setText("Please correct the below fields.");
            tPrice.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onResume()
    {
        AppDebugLog.println("In resume of LoginFragment");
        super.onResume();
    }



    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject login_response = null;

        try {
            login_response = new JSONObject(response);
            String status = login_response.getString("status");
            if (status.equals("success"))
            {
                Intent i = new Intent(activity, DashboardActivity.class);
                //i.putExtra("offerAdded","1");
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                activity.finish();
            }
            else
            {
                msgError.setText("Something goes wrong.Please try again.");
                lblError.setVisibility(View.VISIBLE);
                msgError.setVisibility(View.VISIBLE);
            }

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
}
