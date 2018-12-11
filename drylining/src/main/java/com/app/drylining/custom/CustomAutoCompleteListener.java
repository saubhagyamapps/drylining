package com.app.drylining.custom;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.app.drylining.Util;
import com.app.drylining.network.PlaceJSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pnada on 4/7/2017.
 */

public class CustomAutoCompleteListener implements TextWatcher
{
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    /*AsyncTask parserTask;*/
    Context context;
    private boolean listCityOnly;

    public  CustomAutoCompleteListener(Context context, AutoCompleteTextView autoCompleteTextView)
    {
        this.context = context;
        this.atvPlaces = autoCompleteTextView;
    }


    public  CustomAutoCompleteListener(Context context, AutoCompleteTextView autoCompleteTextView,boolean cityOnly) {
        this.context = context;
        this.atvPlaces = autoCompleteTextView;
        listCityOnly=cityOnly;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        /*if (parserTask!=null) {
            parserTask.cancel(true);
        }*/


    }

    @Override
    public void afterTextChanged(Editable s) {
        placesTask = new PlacesTask();
        placesTask.execute(s.toString());
    }

    private String downloadUrl(String strUrl) throws IOException
    {
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

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + "AIzaSyCGekRuy84a8xehhm2ChR0a5s_z9fum0Go";

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from web service in background
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            if(listCityOnly){
                  CityListTask listTask=new CityListTask();
                  listTask.execute(result);
            }else {
                // Creating ParserTask
                ParserTask parserTask = new ParserTask();
                // Starting Parsing the JSON string returned by Web Service
                parserTask.execute(result);
            }
        }
    }




    private class CityListTask extends AsyncTask<String,Integer,List<String>>{

        @Override
        protected List<String> doInBackground(String... result) {


            List<String> cities=new ArrayList<>();

            try{
                JSONObject jObject = new JSONObject(result[0]);

                JSONArray array=jObject.optJSONArray("predictions");
                for(int i=0;i<array.length();i++){
                    Log.e("array val"+i,array.optJSONObject(i).toString());
                    JSONObject placeObj=array.optJSONObject(i);
                    if(Util.hasJsonArrayContains(placeObj.optJSONArray("types"),"locality")){
                        JSONArray termsArr=placeObj.optJSONArray("terms");
                        int tcount=termsArr.length();
                        for(int j=0;j<tcount;j++){
                            JSONObject tObj=termsArr.optJSONObject(j);
                            if(tObj.optInt("offset")==0){
                                cities.add(tObj.optString("value"));
                                break;
                            }
                        }
                    }

                }

            }catch(Exception e){
                Log.e("Exception",e.toString());
            }

            return cities;
        }

        public void onPostExecute(List<String> listCities){
            ArrayAdapter adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1);
            for(String city:listCities)adapter.add(city);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }

    }



    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>
    {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

               JSONArray array=jObject.optJSONArray("predictions");
               for(int i=0;i<array.length();i++){
                   Log.e("array val"+i,array.optJSONObject(i).toString());
               }

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.e("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(context, result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }
}
