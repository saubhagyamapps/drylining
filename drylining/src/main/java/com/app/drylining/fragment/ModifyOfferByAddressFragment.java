package com.app.drylining.fragment;


import android.app.ProgressDialog;
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
import com.app.drylining.custom.ExpandableHeightGridView;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.ActivityModifyOffer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModifyOfferByAddressFragment extends Fragment implements RequestTaskDelegate
{
    private ApplicationData appData;
    private ActivityModifyOffer activity;
    private View view = null;

    private ProgressDialog pdialog;

    private TextView lblError, msgError;
    private AutoCompleteTextView txtCityName;
    private AppCompatEditText txtName, txtStreet, txtUnity, txtDescription;
    private TextView tPrice;

    private ArrayList<String> attachments;
    private LayoutInflater inflater;
    private ExpandableHeightGridView gridView;
    //private GridAdapter gridAdapter;

    private RelativeLayout selectionDialog;
    private TextView btnAlbum, btnCamera,lblImageCount;

    private RelativeLayout spPropContainer, spRoomContainer, spGarageContainer;
    private Spinner spProperty,spRoom,spType;
    private CustomSpinnerAdapter spPropertyAdapter, spRoomAdapter, spTypeAdapter;

    private String postJobType, postWorkType;

    private String postProperty, postCurrentType, postGarage;

    private Button btnUploadImage,btnSave;

    protected static final int REQUEST_CAMERA = 1;
    protected static final int REQUEST_IMAGE = 2;

    private JSONObject selectedOfferObj;
    private String selectedOfferId, selectedOfferLat, selectedOfferLng;
    private HashMap<String,JSONObject> imageMap=new HashMap<>();

    private String removedImage;

    private  CustomAutoCompleteListener listener;

    public ModifyOfferByAddressFragment() {
        // Required empty public constructor
    }

    public void tabChanged() {
        AppDebugLog.println("In tabChanged of AddOfferByAddressFragment : ");
    }

    public void tabUnSelected() {
    }

    @Override
    public void onPause()
    {
        //gridAdapter = null;
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppDebugLog.println("In onCreateView of AddOfferByAddressFragment : ");
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_modify_offer_by_address, container, false);

            lblError = (TextView) view.findViewById(R.id.lbl_error);
            txtName = (AppCompatEditText) view.findViewById(R.id.txt_post_name);
            txtCityName = (AutoCompleteTextView) view.findViewById(R.id.txt_city_name);
            txtStreet = (AppCompatEditText) view.findViewById(R.id.txt_street_name);
            //txtUnity = (AppCompatEditText) view.findViewById(R.id.txt_unit_number);
            txtDescription = (AppCompatEditText) view.findViewById(R.id.txt_description);
            tPrice = (TextView) view.findViewById(R.id.txt_price);
            msgError = (TextView) view.findViewById(R.id.msg_error);
            //btnCamera = (TextView) view.findViewById(R.id.btnCamera);
            //btnAlbum = (TextView) view.findViewById(R.id.btnAlbum);
            //lblImageCount = (TextView) view.findViewById(R.id.lblImgCount);

            spPropContainer = (RelativeLayout) view.findViewById(R.id.sp_PropContainer);
            spRoomContainer = (RelativeLayout) view.findViewById(R.id.sp_RoomTypeContainer);
            spGarageContainer = (RelativeLayout) view.findViewById(R.id.sp_GarageContainer);
            spProperty = (Spinner) view.findViewById(R.id.sppinerProperty);
            spRoom = (Spinner) view.findViewById(R.id.spinnerRoom);
            spType = (Spinner) view.findViewById(R.id.sppinerType);
            //btnUploadImage = (Button) view.findViewById(R.id.btn_upload);
            btnSave = (Button) view.findViewById(R.id.btn_save);
            gridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
            selectionDialog = (RelativeLayout) view.findViewById(R.id.selectionDialog);

            setHasOptionsMenu(true);

            initialize();

            setInitialValues();

        }
        return view;
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = (ActivityModifyOffer) getActivity();

        attachments = new ArrayList<String>();

        listener=new CustomAutoCompleteListener(getContext(),txtCityName,true);
        txtCityName.addTextChangedListener(listener);

        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

        spPropertyAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.property_types));
        spProperty.setAdapter(spPropertyAdapter);
        spProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if (tv != null)  tv.setText("Type of job - "+tv.getText());

                postProperty = getResources().getStringArray(R.array.property_types)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spProperty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int width= txtCityName.getWidth();
                spProperty.setDropDownWidth(width);
                return false;
            }
        });

        spRoomAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.currency_types));
        spRoom.setAdapter(spRoomAdapter);
        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if (tv != null)  tv.setText(tv.getText());

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
        spType.setAdapter(spTypeAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                tv.setText("Type of work - "+tv.getText());

                postGarage = getResources().getStringArray(R.array.property_extra_spaces)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int width= txtCityName.getWidth();
                spType.setDropDownWidth(width);
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields())
                {
                    String postName = txtName.getText().toString();
                    String postCity = txtCityName.getText().toString();
                    String postStreet = txtStreet.getText().toString();

                    String postJobType = "";
                    String postWorkType = "";
                    String postCurrencyType;

                    String postDesc = txtDescription.getText().toString();
                    String postPrice = tPrice.getText().toString();
                    String postUserId = appData.getUserId();

                    String postOfferId= "postOfferId=" + selectedOfferId;

                    postName = "name=" + postName;
                    postPrice = "price=" + postPrice;
                    postCurrencyType = "currency=" + postCurrentType;
                    postCurrencyType = postCurrencyType.substring(0, postCurrencyType.indexOf('('));
                    postCity = "city=" + postCity;
                    postStreet = "street=" + postStreet;
                    postJobType = "jobType=" + postProperty;
                    postWorkType = "workType=" + postGarage;
                    postDesc = "description=" + postDesc;


                    String postLongitude = "postLongitude~" + selectedOfferLng;
                    String postLatitude = "postLatitude~" + selectedOfferLat;

                    postUserId = "postUserId=" +postUserId;
                    String postAddType = "postModifyType=" + "ByAddress";

                    if (appData.getConnectionDetector().isConnectingToInternet()) {
                        showProgressDialog();
                        lblError.setVisibility(View.GONE);
                        msgError.setVisibility(View.GONE);
                        RequestTask requestTask = new RequestTask(AppConstant.UPDATE_PROPERTY, AppConstant.HttpRequestType.UPDATE_PROPERTY);
                        requestTask.delegate = ModifyOfferByAddressFragment.this;
                        requestTask.execute(AppConstant.UPDATE_PROPERTY, postOfferId + "&" + postName + "&" + postPrice + "&" + postCurrencyType + "&" + postCity + "&" + postStreet + "&" + postJobType + "&" + postWorkType + "&" +
                                postDesc + "&" + postAddType + "&" + postUserId);
                        /*requestTask.execute(AppConstant.UPDATE_PROPERTY, postOfferId, postCity, postStreet, postProperty, postRoom,
                                postGarage, postPrice, postLongitude, postLatitude, postUserId, postDesc, postUnit, postAddType);*/
                    } else {
                        Util.showNoConnectionDialog(getActivity());
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

    private void setInitialValues()
    {
        try {
            selectedOfferObj = new JSONObject(activity.getIntent().getStringExtra("propertyInfo"));

            JSONObject offerObj = selectedOfferObj.getJSONObject("info");

            selectedOfferId  = offerObj.getString("id");

            String propName = offerObj.getString("name");
            String proType = offerObj.getString("room_type");
            String propPrice = offerObj.getString("price");
            String propCurrency = offerObj.getString("currency_type");
            String propCity = offerObj.getString("postCity");
            String propStreet=offerObj.getString("streetName");
            String propGarbage = offerObj.getString("job_type");
            String propDesc = offerObj.getString("description");

            selectedOfferLng = offerObj.getString("longitude");
            selectedOfferLat = offerObj.getString("latitude");

            txtName.setText(propName);
            txtCityName.setText(propCity);
            txtStreet.setText(propStreet);
            tPrice.setText(propPrice);

            String[] array=getResources().getStringArray(R.array.property_types);
            spProperty.setSelection(Util.getStringIndx(array, proType));

            array=getResources().getStringArray(R.array.currency_types);
            spRoom.setSelection(Util.getStringIndx(array, propCurrency));

            array=getResources().getStringArray(R.array.property_extra_spaces);
            spType.setSelection(Util.getStringIndx(array, propGarbage));

            txtDescription.setText(propDesc);

        } catch (JSONException e) {
            e.printStackTrace();
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

        if(tPrice.getText().toString().trim().length()==0){
            msgError.setText("Please correct the below fields.");
            tPrice.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject login_response = null;

        try
        {
            login_response = new JSONObject(response);
            String status = login_response.getString("status");
            if (status.equals("success"))
            {
                /*Intent i = new Intent(activity, AddedOffersActivity.class);
                i.putExtra("offerUpdated", 1);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);*/
                activity.finish();
            } else {
                msgError.setText("Something goes wrong.Please try again.");
                lblError.setVisibility(View.VISIBLE);
                msgError.setVisibility(View.VISIBLE);
            }

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
