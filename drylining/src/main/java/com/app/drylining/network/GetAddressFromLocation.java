package com.app.drylining.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.app.drylining.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Pnada on 4/2/2017.
 */

public class GetAddressFromLocation
{
    private String Address1 = "", Address2 = "", City = "", State = "", Country = "", County = "", PIN = "", StreetName = "";
    private double curLatitude, curLongitude;
    private paser_Json pJson = new paser_Json();

    private PlacesTask placesTask;

    private ProgressDialog pdialog;

    private AutoCompleteTextView city;
    private AppCompatEditText street;
    private AppCompatEditText pin;
    private Context context;

    public GetAddressFromLocation(Context context, double lat, double lon, AutoCompleteTextView city, AppCompatEditText street/*, AppCompatEditText pin*/)
    {
        this.context = context;
        this.curLatitude = lat;
        this.curLongitude = lon;
        this.city = city;
        this.street = street;
        //this.pin = pin;
        getAddress();

        showProgressDialog();
    }

    public void getAddress()
    {
        Address1 = "";
        Address2 = "";
        City = "";
        State = "";
        Country = "";
        County = "";
        PIN = "";

        try {
            String latlng = String.valueOf(curLatitude)+ "," + String.valueOf(curLongitude);

            placesTask = new PlacesTask();
            placesTask.execute(latlng);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {


        Log.e("Location Url",strUrl);

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception ", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void showProgressDialog()
    {
        if (pdialog == null)
        {
            pdialog = ProgressDialog.show(context, context.getResources().getString(R.string.alert_title_loading), context.getResources()
                    .getString(R.string.alert_body_wait));
        }
    }
    private void cancelProgressDialog()
    {
        if (pdialog != null) {
            pdialog.dismiss();
            pdialog = null;
        }

    }

    private class PlacesTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... location)
        {
            // For storing data from web service
            String data = "";

            String latlag = "";
            latlag = location[0];

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlag +"&sensor=false";

           // String url= AppConstant.WEBSERVICE_PATH + "?longitude=" + curLongitude + "&latitude=" + curLatitude;

            try{
                // Fetching the data from web service in background
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            Log.e("Map Result", result);
            cancelProgressDialog();

            try {
                parceResult(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parceResult(String result) throws JSONException {
        /*JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(result);
            String Status = jsonObj.getString("status");

            if(Status.equalsIgnoreCase("ok"))
            {
                city.setText(jsonObj.optString("city"));
                street.setText(jsonObj.optString("street"));
                pin.setText(jsonObj.optString("unit_number"));
            }
        } catch (JSONException e) {
            city.setText("");
            street.setText("");
            pin.setText("");
        }*/


        JSONObject jsonObj = new JSONObject(result);

        String Status = jsonObj.getString("status");
        if (Status.equalsIgnoreCase("OK"))
        {
            JSONArray Results = jsonObj.getJSONArray("results");
            JSONObject zero = Results.getJSONObject(0);
            JSONArray address_components = zero.getJSONArray("address_components");


            HashMap<String, String> sub_locality = new HashMap<>();

            for (int i = 0; i < address_components.length(); i++) {
                JSONObject zero2 = address_components.getJSONObject(i);

                Log.e("Address Component:" + i, zero2.toString());

                String long_name = zero2.getString("long_name");
                JSONArray mtypes = zero2.getJSONArray("types");
                String Type = mtypes.getString(0);

                if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                    if (hasType(mtypes, "street_number") || hasType(mtypes, "premise")) {
                        Address1 = long_name;           //street_number
                    } else if (hasType(mtypes, "route")) {
                        //Address1 = Address1 + long_name;
                        StreetName = long_name;
                    } else if (hasType(mtypes, "sublocality")) {
                        sub_locality.put(mtypes.optString(mtypes.length() - 1), long_name);
                        Address2 = long_name;
                    } else if (hasType(mtypes, "locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        City = long_name;
                    } else if (hasType(mtypes, "administrative_area_level_2")) {
                        County = long_name;
                    } else if (hasType(mtypes, "administrative_area_level_1")) {
                        State = long_name;
                    } else if (hasType(mtypes, "country")) {
                        Country = long_name;
                    } else if (hasType(mtypes, "postal_code")) {
                        PIN = long_name;
                    }
                }
            }

            cancelProgressDialog();
            city.setText(City);
            //street.setText(Address1);
            street.setText(StreetName);
            //pin.setText(PIN);
            pin.setText(Address1);

           /* if(City.length()>0)city.setText(City);
            else*/
            String address = Address1;


            for (int i = 3; i > 0; i--) {
                String key = "sublocality_level_" + i;
                if (sub_locality.get(key) != null) {
                    address += " " + sub_locality.get(key);
                }
            }
        }
    }


    public boolean hasType(JSONArray array,String compare){
        int size=array.length();

        for(int i=0;i<size;i++){
            if(array.optString(i).equalsIgnoreCase(compare))return true;
        }

        return false;
    }

}
