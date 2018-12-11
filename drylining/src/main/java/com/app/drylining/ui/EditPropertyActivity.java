package com.app.drylining.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomAutoCompleteListener;
import com.app.drylining.custom.ExpandableHeightGridView;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.GetAddressFromLocation;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTask1;
import com.app.drylining.network.RequestTaskDelegate;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditPropertyActivity extends AppCompatActivity implements View.OnClickListener, RequestTaskDelegate {

    private ApplicationData appData;
    private View view = null;

    private ProgressDialog pdialog;

    private TextView lblError, msgError;
    private AutoCompleteTextView txtCityName;
    private AppCompatEditText txtStreet,txtUnity;
    private TextView tPrice;

    private ArrayList<String> attachments;
    private LayoutInflater inflater;
    private ExpandableHeightGridView gridView;

    //private AddOfferByAddressFragment.GridAdapter gridAdapter;

    private RelativeLayout selectionDialog;
    private TextView btnAlbum, btnCamera,lblImageCount;
    private Spinner spProperty,spRoom,spType;
    private Button btnUploadImage,btnUpdateOffer;

    protected static final int REQUEST_CAMERA = 1;
    protected static final int REQUEST_IMAGE = 2;


    private JSONObject mOfferObj;
    private int mOfferId;
    private GridAdapter gridAdapter;

    private HashMap<String,JSONObject> imageMap=new HashMap<>();
    private String imageDeleted;

    private View mMapLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblError = (TextView) findViewById(R.id.lbl_error);
        txtCityName = (AutoCompleteTextView) findViewById(R.id.txt_city_name);
        txtStreet = (AppCompatEditText) findViewById(R.id.txt_street_name);
        //txtUnity = (AppCompatEditText) findViewById(R.id.txt_unit_number);
        tPrice = (TextView) findViewById(R.id.txt_price);
        msgError = (TextView) findViewById(R.id.msg_error);
        btnCamera = (TextView) findViewById(R.id.btnCamera);
        btnAlbum = (TextView) findViewById(R.id.btnAlbum);
        lblImageCount = (TextView) findViewById(R.id.lblImgCount);
        spProperty = (Spinner) findViewById(R.id.sppinerProperty);
        spRoom = (Spinner) findViewById(R.id.sppinerRoom);
        spType = (Spinner) findViewById(R.id.sppinerType);
        btnUploadImage = (Button) findViewById(R.id.btn_upload);
        btnUpdateOffer = (Button) findViewById(R.id.btn_add_offer);
        gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        selectionDialog = (RelativeLayout) findViewById(R.id.selectionDialog);

        ViewUtil.setSpinnerDropDownView(this,spProperty,R.array.property_types);
        ViewUtil.setSpinnerDropDownView(this,spRoom,R.array.currency_types);
        ViewUtil.setSpinnerDropDownView(this,spType,R.array.property_extra_spaces);


        mMapLayout=findViewById(R.id.mapLayout);

        btnUpdateOffer.setText("Update");
        mOfferId=getIntent().getIntExtra("OfferId",1);
        try {
            mOfferObj=new JSONObject(getIntent().getStringExtra("propertyInfo"));
        } catch (JSONException e) {
        }

        //setHasOptionsMenu(true);
        initialize();

        setData(mOfferObj);


    }

    private void setData(JSONObject obj){


        //ArrayList<String> imagesList=new ArrayList<>();
        String[] images=null;
        try {
            JSONArray propImages = obj.getJSONArray("images");
            // images = new String[propImages.length()];
            for (int image = 0; image < propImages.length(); image++) {
                JSONObject imageObj = propImages.getJSONObject(image);
                Log.e("ICAb",imageObj.toString());
                imageMap.put(imageObj.getString("image_path"),imageObj);
                attachments.add(imageObj.getString("image_path"));
            }
            /*Object[] mStringArray = imagesList.toArray();
            images = new String[mStringArray.length];
            for (int i = 0; i < mStringArray.length; i++) {
                Log.d("string is", (String) mStringArray[i]);
                images[i] = (String) mStringArray[i];
            }*/

            JSONObject propInfoObj = obj.getJSONObject("info");
            Log.e("Property Info",propInfoObj.toString());
            // JSONObject propInfoObj = propInfo.getJSONObject(0);
            String propName = propInfoObj.getString("name");
            String roomType = propInfoObj.getString("room_type").split(" ")[0];
            String proType = propInfoObj.getString("propertyTtype");
            String propPrice = propInfoObj.getString("price");
            String propCity = propInfoObj.getString("postCity") ;
            String propStreet=propInfoObj.getString("streetName");
            String propGarbage = propInfoObj.getString("garageType");
            String unitCode=propInfoObj.getString("unitCode");
            //String propDesc = propInfoObj.getString("description");
            Double longitude = Double.parseDouble(propInfoObj.getString("longitude"));
            Double latitude = Double.parseDouble(propInfoObj.getString("latitude"));


            txtCityName.setText(propCity);
            txtStreet.setText(propStreet);
            //txtUnity.setText(unitCode);
            tPrice.setText(propPrice);

            String[] array=getResources().getStringArray(R.array.property_types);
            spProperty.setSelection(Util.getStringIndx(array,proType));

            array=getResources().getStringArray(R.array.currency_types);
            spRoom.setSelection(Util.getStringIndx(array,roomType));

            array=getResources().getStringArray(R.array.property_extra_spaces);
            spType.setSelection(Util.getStringIndx(array,propGarbage));


            Log.e("property Info",propInfoObj.toString());

        }catch (Exception e){
        }

        setGridAdapter();
        updateUploadButton();
    }

    private  CustomAutoCompleteListener listener;

    private void initialize() {
        appData = ApplicationData.getSharedInstance();
        attachments = new ArrayList<String>();
        /*String names[] = {"Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, names);
        txtCityName.setAdapter(arrayAdapter);*/


        listener=new CustomAutoCompleteListener(this,txtCityName,true);
        txtCityName.addTextChangedListener(listener);

        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);
        selectionDialog.setVisibility(View.GONE);
        btnAlbum.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);

        findViewById(R.id.btn_pick_from_map).setOnClickListener(this);

        spProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                tv.setText("Type of property - "+tv.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                tv.setText("No. of rooms - "+tv.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                tv.setText("Garage / parking - "+tv.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnUpdateOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields())
                {
                    String postCity = txtCityName.getText().toString();
                    String postStreet = txtStreet.getText().toString();
                    String postUnit = "";//txtUnity.getText().toString();
                    String postProperty = spProperty.getSelectedItem().toString();
                    String postRoom = spRoom.getSelectedItem().toString();
                    String postGarage = spType.getSelectedItem().toString();
                    String postPrice = tPrice.getText().toString();
                    String postUserId = appData.getUserId();


                   // propertyId=1&action_type=edit&postCity=kolkotta&postDesc=test test&postPrice=600&postGarage=Parking place&postProperty=Room&postRoom=5&postStreet=&postUnit="

                    if(postCity.length()==0)postCity=" ";
                    if(postStreet.length()==0)postStreet=" ";
                    if(postUnit.length()==0)postUnit=" ";

                    String postId="propertyId~"+mOfferId;
                    String postAction="action_type~edit";

                    postCity = "postCity~" +postCity;
                    postStreet = "postStreet~" +postStreet;
                    postProperty = "postProperty~" +postProperty;
                    postRoom = "postRoom~" +postRoom;
                    postGarage = "postGarage~" +postGarage;
                    postPrice = "postPrice~" +postPrice;
                    postUnit="postUnit~"+postUnit;

                    /*String location = appData.getCurrentLocation();
                    Double currentLatitude = 0.0;
                    Double currentLongitude = 0.0;
                    if (location.length() > 0) {
                        currentLatitude = Double.parseDouble(location.split(",")[0]);
                        currentLongitude = Double.parseDouble(location.split(",")[1]);
                    }

                    String postLongitude = "postLongitude~" +currentLongitude;
                    String postLatitude = "postLatitude~" +currentLatitude;*/
                    String postDesc = "postDesc~Meadows-a part of the prestigious sports city located at sector 150, Noida, has everything going for it—from brilliantly planned spaces, luxurious features, inspired roman architecture to world-class amenities and a golf course club. It is spread across 7 acres of verdant expanse and offers luxurious sky villas with personal pools, 2BHK+ study units , 3 and 4 BHK apartments varying in sizes from 1425 sq. ft. to 3400 sq. ft and 28 signature villas.\\n\\nA Large and luxurious State-of-the-art Clubhouse has been planned with Squash, Billiards, Gymnasium, Swimming Pool, Spa and a large banquet hall to fulfill your family’s entertainment needs. For the outdoors, a jogging trail, a cycling track, multipurpose courts, yoga pavilion and children’s play area have been specially provided.";
                   // postUserId = "postUserId~" +postUserId;

                    ArrayList<String> postAttachments = new ArrayList<>();
                    for (int i = 0; i < attachments.size(); i++) {
                        String attachment = AppConstant.NULL_STRING;
                        Log.e("Total attachments are", "" + attachments.size());
                        if (!attachments.get(i).equals("")&&!attachments.get(i).startsWith("http")) {
                            attachment = "propertyImage~" + attachments.get(i);
                            postAttachments.add(attachment);
                        }
                    }

                    // String "~postStreet=" + postStreet + "~postUnit=" + postUnit + "~postProperty=" + postProperty + "~postRoom=" + postRoom + "~postGarage=" + postGarage + "~postPrice=" + postPrice + "~postLongitude=" + strLongitude + "~postLatitude=" + strLatitude + "~postUserId=" + 10;

                    if (appData.getConnectionDetector().isConnectingToInternet()) {
                        showProgressDialog();
                        lblError.setVisibility(View.GONE);
                        msgError.setVisibility(View.GONE);
                        RequestTask1 requestTask = new RequestTask1(AppConstant.UPDATE_PROPERTY, AppConstant.HttpRequestType.UPDATE_PROPERTY,EditPropertyActivity.this,postAttachments,true);
                        requestTask.delegate = EditPropertyActivity.this;
                        requestTask.execute(AppConstant.UPDATE_PROPERTY,postId,postAction,postCity,postStreet,postProperty,postRoom,postGarage,postPrice,postDesc,postUnit);
                    } else {
                        Toast.makeText(EditPropertyActivity.this, "Please connect network", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    lblError.setVisibility(View.VISIBLE);
                    msgError.setVisibility(View.VISIBLE);
                }
            }
        });
      //  setGridAdapter();

    //    initializeMap();
    }

    public boolean checkFields() {

        boolean isValid = true;
        if(txtCityName.getText().toString().trim().equals(""))
        {
            msgError.setText("Please correct the below fields.");
            txtCityName.setBackground(ContextCompat.getDrawable(EditPropertyActivity.this, R.drawable.bg_error_border));
            isValid = false;
        }

        if(attachments.size()==0) {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.dark_red));
            isValid = false;
        }

        if(tPrice.getText().toString().trim().length()==0){
            msgError.setText("Please correct the below fields.");
            // tPrice.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            tPrice.setError("Enter valid Price....");
            isValid = false;
        }
        return isValid;

    }


    public void onBackPressed(){
        if(selectionDialog.getVisibility()==View.VISIBLE){
            selectionDialog.setVisibility(View.INVISIBLE);
        }else if(mMapLayout.getVisibility()==View.VISIBLE){
            mMapLayout.setVisibility(View.INVISIBLE);
        }else super.onBackPressed();
    }

    public void onResume(){
        super.onResume();
    }

    public void onStop(){
        super.onStop();
    }

    public void initializeMap() {

        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {



        switch (view.getId()){

            case R.id.btnAlbum:
                selectionDialog.setVisibility(View.GONE);
                selectFromGallery();
                break;
            case R.id.btnCamera:
                selectionDialog.setVisibility(View.GONE);
                captureImage();
                break;
            case R.id.btn_upload:
                selectionDialog.setVisibility(View.VISIBLE);
                break;

            case R.id.ic_delete:

                String attachment = (String) view.getTag();
                if(attachment.startsWith("http")){
                    showProgressDialog();
                    imageDeleted=attachment;
                    JSONObject obj=imageMap.get(attachment);
                    RequestTask reqTask=new RequestTask(AppConstant.EDIT_IMAGE,AppConstant.HttpRequestType.DELETE_IMAGE);
                    reqTask.delegate = this;
                    String params = "action_type=delete&id="+obj.optInt("id")+"&property_id="+obj.optInt("property_id");
                    reqTask.execute(AppConstant.EDIT_IMAGE+params);

                }else{

                    attachments.remove(attachment);
                    gridAdapter.notifyDataSetChanged();

                }

                updateUploadButton();
                break;

            case R.id.btn_pick_from_map:
                mMapLayout.setVisibility(View.VISIBLE);
                initializeMap();
                break;

        }

    }

    private void setGridAdapter() {
        if (gridAdapter == null) {
            gridAdapter =new GridAdapter(this);
            gridView.setAdapter(gridAdapter);
            gridView.setExpanded(true);
            // gridView.setOnItemClickListener(activity);
        }
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {

        cancelProgressDialog();


        Log.e("Property Edit Response",response);

        if(completedRequestType== AppConstant.HttpRequestType.DELETE_IMAGE){

            try {
                JSONObject respObj = new JSONObject(response);
                String status = respObj.getString("status");


                if (status.equals("success")) {
                        if(imageDeleted!=null&&attachments.contains(imageDeleted)){
                            attachments.remove(imageDeleted);
                            gridAdapter.notifyDataSetChanged();
                            updateUploadButton();
                        }
                }
            }catch (Exception e){

            }

        }else if(completedRequestType== AppConstant.HttpRequestType.UPDATE_PROPERTY){

            try {
                JSONObject respObj = new JSONObject(response);
                String status = respObj.getString("status");

                if (status.equals("success")) {
                    Intent intent = new Intent(EditPropertyActivity.this, AddedOfferDetailActivity.class);
                    intent.putExtra("OfferID", mOfferId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else throw new Exception();

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Something wrong ,please try again",Toast.LENGTH_SHORT).show();
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
        if (!isFinishing() && pdialog == null) {
            pdialog = ProgressDialog.show(this, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog() {
        if (pdialog != null && !isFinishing()) {
            pdialog.dismiss();
            pdialog = null;
        }

    }

    private class GridAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public GridAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return attachments.size();
        }

        @Override
        public Object getItem(int position) {
            return attachments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridAdapter.CellHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cell_grid, parent, false);
                holder = new  GridAdapter.CellHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.ic_img);
                holder.imgDelete = (ImageView) convertView.findViewById(R.id.ic_delete);

                convertView.setTag(holder);
            } else {
                holder = (GridAdapter.CellHolder) convertView.getTag();
            }

            String attachment = attachments.get(position);
            holder.img.setOnClickListener(null);
            holder.img.setImageResource(R.color.transparent);
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgDelete.setOnClickListener(EditPropertyActivity.this);
            holder.imgDelete.setTag(attachment);


            if (attachment != null) {
                int width = getResources().getDimensionPixelSize(R.dimen.profile_width);
                if(attachment.startsWith("http")){
                    Picasso.get()
                            .load(attachment)
                            .into(holder.img);
                }else appData.setPic(holder.img, width, width, attachment);
            }

            return convertView;
        }

        private class CellHolder {
            private ImageView img;
            private ImageView imgDelete;
        }
    }



    public void captureImage() {
        dispatchTakePictureIntent();
    }

    private void selectFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE);
    }


    //Start camera image
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //convert file// to content://
                String authority = getPackageName() + ".fileprovider";
                Uri photoURI = FileProvider.getUriForFile(EditPropertyActivity.this, authority, photoFile);
                AppDebugLog.println("photoURI : " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private String captureImagePath;

    //Create Captured image file
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = AppConstant.captureImgDateTimeFormat.format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        captureImagePath = image.getAbsolutePath();
        return image;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = AppConstant.NULL_STRING;

        if (resultCode == Activity.RESULT_OK) {
            AppDebugLog.println("In onActivityResult : " + resultCode + " : " + requestCode + " : " + data);
            if (requestCode == REQUEST_CAMERA) {

               /* AppDebugLog.println("captureImagePath : " + captureImagePath);
                if (captureImagePath.length() > 0) {
                    filePath = captureImagePath;
                }*/
            } else if (requestCode == REQUEST_IMAGE && data != null) {
                Uri uri = data.getData();
                String path = ApplicationData.getPath(EditPropertyActivity.this, uri);
                if (path == null) path = uri.getPath();
                filePath = path;
                AppDebugLog.println("Selected Image Path : " + path);
            }
        }

        if (filePath != null && filePath.length() > 0) {
            attachments.add(filePath);
            gridAdapter.notifyDataSetChanged();
            updateUploadButton();
        }
    }

    private void updateUploadButton(){
        if(attachments.size() >= 8) {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.upload_button_no_more_images));
            btnUploadImage.setText("No more images can be uploaded.");
            btnUploadImage.setEnabled(false);
        }
        else {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.upload_button));
            btnUploadImage.setText("Upload Images\n(You can add "+(8-attachments.size())+" more)");
            btnUploadImage.setEnabled(true);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId()==R.id.action_logout){


            Intent loginIntent=new Intent(getApplicationContext(),MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);

    }


}
