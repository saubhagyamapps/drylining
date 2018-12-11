package com.app.drylining.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomAutoCompleteListener;
import com.app.drylining.custom.CustomSpinnerAdapter;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.SearchOfferParameters;
import com.app.drylining.ui.SearchNewOfferActivity;

import com.app.drylining.ui.SearchedResultActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchOfferByAddressFragment extends Fragment implements View.OnClickListener
{
    private ApplicationData appData;
    private SearchNewOfferActivity activity;
    private View view = null;

    private TextView lblError, msgError, txtMinPrice, txtMaxPrice;
    private AutoCompleteTextView txtCityName;

    private RelativeLayout spMaxRangeContainer, spCountryContainer, spPropContainer, spRoomContainer, spGarageContainer;
    private Spinner spCountry, spRange, spProperty, spRoom, spType;
    private CustomSpinnerAdapter spRangeAdapter, spCountryAdapter, spPropertyAdapter, spRoomAdapter, spTypeAdapter;
    private Button btnSearch;

    private CrystalRangeSeekbar priceSeekBar;
    private int minPrice, maxPrice;

    private CustomAutoCompleteListener listener;

    private String searchRange, searchCountry, searchProperty, searchRoom, searchGarage;

    private SearchOfferParameters previousParam;

    public SearchOfferByAddressFragment() {
        // Required empty public constructor
    }

    public void tabChanged()
    {
        AppDebugLog.println("In tabChanged of SearchOfferByAddressFragment : ");
    }

    public void tabUnSelected()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AppDebugLog.println("In onCreateView of SearchOfferByAddressFragment : ");
        // Inflate the layout for this fragment
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_search_offer_by_address, container, false);

            lblError = (TextView) view.findViewById(R.id.lbl_error);
            msgError = (TextView) view.findViewById(R.id.msg_error);
            txtCityName = (AutoCompleteTextView) view.findViewById(R.id.txt_city_name);

            spCountryContainer = (RelativeLayout) view.findViewById(R.id.sp_CountryContainer);
            spPropContainer = (RelativeLayout) view.findViewById(R.id.sp_PropContainer);
            spGarageContainer = (RelativeLayout) view.findViewById(R.id.sp_GarageContainer);

            spCountry = (Spinner) view.findViewById(R.id.spinnerCountry);
            spProperty = (Spinner) view.findViewById(R.id.spinnerProperty);
            spType = (Spinner) view.findViewById(R.id.spinnerParkType);
            btnSearch = (Button) view.findViewById(R.id.btn_address_search);

            /*priceSeekBar = (CrystalRangeSeekbar) view.findViewById(R.id.price_seekbar);
            txtMinPrice = (TextView) view.findViewById(R.id.txt_minPrice);
            txtMaxPrice = (TextView) view.findViewById(R.id.txt_maxPrice);*/

            setHasOptionsMenu(true);

            initialize();
        }
        return view;
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = (SearchNewOfferActivity) getActivity();
        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

        listener = new CustomAutoCompleteListener(getContext(), txtCityName);

        txtCityName.addTextChangedListener(listener);

        spCountryAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.country_names));
        spCountry.setAdapter(spCountryAdapter);
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                TextView tv = (TextView)parent.getSelectedView();
                tv.setText(tv.getText());

                searchCountry = getResources().getStringArray(R.array.country_names)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spPropertyAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.property_types));
        spProperty.setAdapter(spPropertyAdapter);
        spProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                TextView tv = (TextView)parent.getSelectedView();
                if (tv != null)  tv.setText("Type of job - " + tv.getText());

                searchProperty = getResources().getStringArray(R.array.property_types)[position];
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

        spTypeAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.property_extra_spaces));
        spType.setAdapter(spTypeAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                TextView tv = (TextView)parent.getSelectedView();
                if (tv != null)  tv.setText("Type of work - " + tv.getText());

                searchGarage = getResources().getStringArray(R.array.property_extra_spaces)[position];
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

        /*priceSeekBar.setMinValue(100);
        priceSeekBar.setMaxValue(3000);
        priceSeekBar.setSteps(10);

        minPrice = 100;
        maxPrice = 3000;

        priceSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener()
        {
            @Override
            public void valueChanged(Number minValue, Number maxValue)
            {
                minPrice = minValue.intValue();
                maxPrice = maxValue.intValue();

                txtMinPrice.setText(String.valueOf(minPrice));
                txtMaxPrice.setText(String.valueOf(maxPrice));
            }
        });*/

        btnSearch.setOnClickListener(this);

        previousParam = ((SearchNewOfferActivity) getActivity()).getPreviousParams();

        if(previousParam != null)       //if edit or new search?
        {
            initializeParams();
        }
    }

    private void initializeParams()
    {
        int i = 0;

        txtCityName.setText(previousParam.getSearchCity());

        for(i = 0; i < spProperty.getCount(); i ++)
        {
            if(spPropertyAdapter.getItem(i).equals(previousParam.getSearchProperty()))
            {
                spProperty.setSelection(i);
                i = 0;
                break;
            }
        }

        for(i = 0; i < spType.getCount(); i ++)
        {
            if(spTypeAdapter.getItem(i).equals(previousParam.getSearchGarage()))
            {
                spType.setSelection(i);
                i = 0;
                break;
            }
        }
    }

    @Override
    public void onResume()
    {
        AppDebugLog.println("In resume of LoginFragment");
        super.onResume();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_address_search:
                addressSearchClicked();
                break;
        }
    }

    public void addressSearchClicked()
    {
        if (checkFields())
        {
            AppDebugLog.println("coming in yes");
            goToSearch();

            /*if (appData.getConnectionDetector().isConnectingToInternet())
            {
                String address = txtCityName.getText().toString();      //get location of entered city(in fact, no need at this version)
                *//*GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(address,
                        getContext(), new GeocoderHandler());*//*
            }
            else
            {
                Util.showNoConnectionDialog(getActivity());
            }*/
        } else
        {
            AppDebugLog.println("coming in not");
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
        }
    }

    private void goToSearch(/*String locationAddress*/)
    {
        //Log.e("Location Address",locationAddress);
        String city = txtCityName.getText().toString();
        String[] seperated = city.split(",");

        String searchCity = seperated[0];

        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

        ///////i should to notify these values to SearchedResultActivity
        SearchOfferParameters searchParam = new SearchOfferParameters(searchCity, searchProperty, searchGarage/*, minPrice, maxPrice, locationAddress*/);

        searchParam.setStreet("");
        searchParam.setPinCode("");
        searchParam.setSearchCountry(searchCountry);

        /////save as last search
        appData.setPreCityName(searchCity);
        appData.setPreRange(searchRange);
        appData.setPreProperty(searchProperty);
        appData.setPreRoom(searchRoom);
        appData.setPreGarage(searchGarage);
        appData.setPreMinPrice(minPrice);
        appData.setPreMaxPrice(maxPrice);
        //appData.setPreCityLocation(locationAddress);

        appData.setIsSearched(true);

        Intent intentToSearchedActivity = new Intent(getActivity(), SearchedResultActivity.class);
        intentToSearchedActivity.putExtra("searchParam", searchParam);
        getActivity().startActivity(intentToSearchedActivity);
        getActivity().finish();
    }

   /* private class GeocoderHandler extends Handler
    {
        @Override
        public void handleMessage(Message message)
        {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");

                    goToSearch(locationAddress);
                    break;
                case 2:
                    msgError.setText("Please correct the below fields.");
                    txtCityName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
                    break;
                default:
                    locationAddress = null;
            }
        }
    }*/

    private boolean checkFields()
    {
        boolean isValid = true;
        if(txtCityName.getText().toString().trim().equals(""))
        {
            txtCityName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }
        return isValid;
    }
}