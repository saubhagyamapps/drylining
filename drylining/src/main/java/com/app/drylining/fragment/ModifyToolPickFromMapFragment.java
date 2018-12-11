package com.app.drylining.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomSpinnerAdapter;
import com.app.drylining.custom.ExpandableHeightGridView;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.GetAddressFromLocation;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTask1;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.ActivityModifyTool;
import com.app.drylining.ui.MultiPhotoSelectActivity;
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

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModifyToolPickFromMapFragment extends Fragment implements View.OnClickListener, RequestTaskDelegate
{
    private ApplicationData appData;
    private ActivityModifyTool activity;
    private View view = null;
    private ArrayList<String> attachments;

    private LayoutInflater inflater;
    private ExpandableHeightGridView gridView;
    private GridAdapter gridAdapter;

    private RelativeLayout selectionDialog;
    private TextView btnAlbum, btnCamera,lblImageCount,tPrice;

    private RelativeLayout spPropContainer, spRoomContainer, spGarageContainer;
    private Spinner spProperty,spRoom,spType;
    private CustomSpinnerAdapter spPropertyAdapter, spRoomAdapter, spTypeAdapter;

    private String postProperty, postRoom, postGarage;

    private Button btnUploadImage,btnSaveOffer;

    private JSONObject selectedOfferObj;
    private String selectedOfferId;
    private double selectedOfferLat, selectedOfferLng;
    private HashMap<String,JSONObject> imageMap=new HashMap<>();

    private String removedImage;

    Geocoder geocoder;

    private ProgressDialog pdialog;

    private AutoCompleteTextView txtCityName;
    private AppCompatEditText txtName, txtStreet, txtUnity, txtDescription;
    private TextView lblError, msgError;
    private boolean isLocationFind = false;

    protected static final int REQUEST_CAMERA = 1;
    protected static final int REQUEST_IMAGE = 2;

    private String city = AppConstant.NULL_STRING;
    private String streetName = AppConstant.NULL_STRING;
    private String pinCode = AppConstant.NULL_STRING;

    private String strLongitude = AppConstant.NULL_STRING;
    private String strLatitude = AppConstant.NULL_STRING;

    public ModifyToolPickFromMapFragment() {
        // Required empty public constructor
    }

    public void tabChanged() {
        AppDebugLog.println("In tabChanged of AddOfferPickFromMapFragment : ");
    }

    public void tabUnSelected() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AppDebugLog.println("In onCreateView of AddOfferPickFromMapFragment : ");
        // Inflate the layout for this fragment
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_modify_tool_pick_from_map, container, false);
            lblError = (TextView) view.findViewById(R.id.lbl_error);
            txtName = (AppCompatEditText) view.findViewById(R.id.txt_post_name);
            txtCityName = (AutoCompleteTextView) view.findViewById(R.id.txt_city_name);
            txtStreet = (AppCompatEditText) view.findViewById(R.id.txt_street_name);
            //txtUnity = (AppCompatEditText) view.findViewById(R.id.txt_unit_number);
            txtCityName = (AutoCompleteTextView) view.findViewById(R.id.txt_city_name);
            txtDescription = (AppCompatEditText) view.findViewById(R.id.txt_description);
            tPrice = (TextView) view.findViewById(R.id.txt_price);
            msgError = (TextView) view.findViewById(R.id.msg_error);
            btnCamera = (TextView) view.findViewById(R.id.btnCamera);
            btnAlbum = (TextView) view.findViewById(R.id.btnAlbum);
            lblImageCount = (TextView) view.findViewById(R.id.lblImgCount);

            spPropContainer = (RelativeLayout) view.findViewById(R.id.sp_PropContainer);
            spProperty = (Spinner) view.findViewById(R.id.sppinerProperty);
            btnUploadImage = (Button) view.findViewById(R.id.btn_upload);
            btnSaveOffer = (Button) view.findViewById(R.id.btn_save);
            gridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
            selectionDialog = (RelativeLayout) view.findViewById(R.id.selectionDialog);

            setHasOptionsMenu(true);

            initialize();

            setInitialValues();

            updateUploadButton();
        }
        return view;
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = (ActivityModifyTool) getActivity();

        attachments = new ArrayList<String>();

        String names[] = {"Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, names);
        txtCityName.setAdapter(arrayAdapter);

        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);
        selectionDialog.setVisibility(View.GONE);
        btnAlbum.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);

        spPropertyAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.tool_types));
        spProperty.setAdapter(spPropertyAdapter);
        spProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                TextView tv = (TextView) parent.getSelectedView();
                tv.setText("Type of job - "+tv.getText());

                postProperty = getResources().getStringArray(R.array.tool_types)[position];
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

        btnSaveOffer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkFields())
                {
                    String postName = txtName.getText().toString();
                    String postCity = txtCityName.getText().toString();
                    String postStreet = txtStreet.getText().toString();
                    String postDesc = txtDescription.getText().toString();
                    String postPrice = tPrice.getText().toString();
                    String postUserId = appData.getUserId();
                    String postToolType = "";

                    String postOfferId= "postOfferId~" + selectedOfferId;

                    if(postStreet.length()==0)
                        postStreet = postStreet + " ";

                    postName = "name~" + postName;
                    postPrice = "price~" + postPrice;
                    postCity = "city~" + postCity;
                    postStreet = "street~" + postStreet;
                    postToolType = "toolType~" + postProperty;
                    postDesc = "description~" + postDesc;
                    String postLongitude = "postLongitude~" + strLongitude;
                    String postLatitude = "postLatitude~" + strLatitude;

                    String postAddType = "postModifyType~" + "ByPickMap";

                    ArrayList<String> postAttachments = new ArrayList<>();
                    for (int i = 0; i < attachments.size(); i++)
                    {
                        String attachment = AppConstant.NULL_STRING;
                        Log.d("Total attachments are", ""+attachments.size());
                        if (!attachments.get(i).equals("") && !attachments.get(i).startsWith("http"))
                        {
                            attachment = "propertyImage~" + attachments.get(i);
                            postAttachments.add(attachment);
                        }
                    }

                    if (appData.getConnectionDetector().isConnectingToInternet())
                    {
                        showProgressDialog();
                        lblError.setVisibility(View.GONE);
                        msgError.setVisibility(View.GONE);
                        RequestTask1 requestTask = new RequestTask1(AppConstant.UPDATE_TOOL,
                                AppConstant.HttpRequestType.UPDATE_TOOL, activity, postAttachments,true);
                        requestTask.delegate = ModifyToolPickFromMapFragment.this;
                        requestTask.execute(AppConstant.UPDATE_TOOL, postOfferId, postName, postPrice, postCity,postStreet, postToolType,
                                postLongitude, postLatitude, postUserId,postDesc, postAddType);
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
        geocoder = new Geocoder(activity, Locale.getDefault());
    }

    private void setInitialValues()
    {
        try {
            selectedOfferObj = new JSONObject(activity.getIntent().getStringExtra("propertyInfo"));

            JSONArray imageObjArray = selectedOfferObj.getJSONArray("images");

            for(int i=0; i<imageObjArray.length(); i++)
            {
                JSONObject imageObj = imageObjArray.getJSONObject(i);

                imageMap.put(imageObj.getString("image_path"), imageObj);   //use when click each image delete button

                attachments.add(imageObj.getString("image_path"));
            }

            JSONObject offerObj = selectedOfferObj.getJSONObject("info");

            selectedOfferId  = offerObj.getString("id");

            String propName = offerObj.getString("name");
            String proType = offerObj.getString("room_type");
            String propPrice = offerObj.getString("price");
            String propCity = offerObj.getString("postCity") ;
            String propStreet=offerObj.getString("streetName");
            String propDesc = offerObj.getString("description");

            selectedOfferLng = Double.parseDouble(offerObj.getString("longitude"));
            selectedOfferLat = Double.parseDouble(offerObj.getString("latitude"));

            strLongitude = String.valueOf(selectedOfferLng);
            strLatitude = String.valueOf(selectedOfferLat);

            txtName.setText(propName);
            txtCityName.setText(propCity);
            txtStreet.setText(propStreet);
            tPrice.setText(propPrice);

            String[] array=getResources().getStringArray(R.array.tool_types);
            spProperty.setSelection(Util.getStringIndx(array,proType));

            txtDescription.setText(propDesc);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setGridAdapter();
    }

    private void setGridAdapter()
    {
        if (gridAdapter == null)
        {
            gridAdapter = new GridAdapter(activity);
            gridView.setAdapter(gridAdapter);
            gridView.setExpanded(true);
        }
        gridAdapter.notifyDataSetChanged();
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
        else if(tPrice.getText().toString().trim().length()==0){
            msgError.setText("Please correct the below fields.");
            tPrice.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }
        else if(txtDescription.getText().toString().trim().equals(""))
        {
            msgError.setText("Please correct the below fields.");
            txtDescription.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }

        else if(attachments.size()==0)
        {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.gray_red));
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
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
                if(attachment.startsWith("http"))
                {
                    showProgressDialog();
                    removedImage = attachment;
                    JSONObject obj = imageMap.get(attachment);
                    RequestTask reqTask=new RequestTask(AppConstant.EDIT_IMAGE,AppConstant.HttpRequestType.DELETE_IMAGE);
                    reqTask.delegate = this;
                    String params = "action_type=delete&id="+obj.optInt("id")+"&property_id="+obj.optInt("property_id");
                    reqTask.execute(AppConstant.EDIT_IMAGE+params);
                }
                else
                {
                    attachments.remove(attachment);
                    gridAdapter.notifyDataSetChanged();

                    updateUploadButton();
                }
                break;
        }
    }


    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject login_response = null;

        if(completedRequestType== AppConstant.HttpRequestType.DELETE_IMAGE)
        {
            try {
                JSONObject respObj = new JSONObject(response);
                String status = respObj.getString("status");

                if (status.equals("success"))
                {
                    if (removedImage != null && attachments.contains(removedImage))
                    {
                        attachments.remove(removedImage);
                        gridAdapter.notifyDataSetChanged();
                        updateUploadButton();
                    }
                }
            } catch (Exception e) {

            }
        }
        else if(completedRequestType== AppConstant.HttpRequestType.UPDATE_TOOL)
        {
            try
            {
                login_response = new JSONObject(response);
                String status = login_response.getString("status");
                if (status.equals("1"))
                {
                    /*Intent i = new Intent(activity, AddedOffersActivity.class);
                    i.putExtra("offerUpdated", 1);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                    gridAdapter = null;*/

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

    @Override
    public void onResume() {
        AppDebugLog.println("In resume of TaskDetailsSummaryFragment");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        gridAdapter = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private class GridAdapter extends BaseAdapter
    {
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
            CellHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cell_grid, parent, false);
                holder = new CellHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.ic_img);
                holder.imgDelete = (ImageView) convertView.findViewById(R.id.ic_delete);

                convertView.setTag(holder);
            } else {
                holder = (CellHolder) convertView.getTag();
            }

            String attachment = attachments.get(position);
            holder.img.setOnClickListener(null);
            holder.img.setImageResource(R.color.transparent);
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.imgDelete.setOnClickListener(ModifyToolPickFromMapFragment.this);
            holder.imgDelete.setTag(attachment);


            if (attachment != null) {
                int width = getResources().getDimensionPixelSize(R.dimen.profile_width);
                if(attachment.startsWith("http"))
                {
                    Picasso.get()
                            .load(attachment)
                            .resizeDimen(R.dimen.grid_cell_image_size, R.dimen.grid_cell_image_size)
                            .centerCrop()
                            .into(holder.img);
                }
                else
                    appData.setPic(holder.img, width, width, attachment);
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

    private void selectFromGallery()
    {
        Intent intent = new Intent(getActivity(), MultiPhotoSelectActivity.class);
        intent.putExtra("ADDABLE_NUM", 8-attachments.size());
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    //Start camera image
    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                //convert file// to content://
                String authority = activity.getPackageName() + ".fileprovider";
                Uri photoURI = FileProvider.getUriForFile(activity, authority, photoFile);
                AppDebugLog.println("photoURI : " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private String captureImagePath;

    //Create Captured image file
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = AppConstant.captureImgDateTimeFormat.format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        captureImagePath = image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = AppConstant.NULL_STRING;

        if (resultCode == Activity.RESULT_OK)
        {
            AppDebugLog.println("In onActivityResult : " + resultCode + " : " + requestCode + " : " + data);
            if (requestCode == REQUEST_CAMERA)
            {
                AppDebugLog.println("captureImagePath : " + captureImagePath);
                if (captureImagePath.length() > 0) {
                    filePath = captureImagePath;

                    if (filePath != null && filePath.length() > 0)
                    {
                        attachments.add(filePath);
                        gridAdapter.notifyDataSetChanged();
                        updateUploadButton();
                    }
                }
            } else if (requestCode == REQUEST_IMAGE && data != null)
            {
                ArrayList<String> addedImages = data.getStringArrayListExtra("IMAGES");
                if(addedImages != null && addedImages.size() > 0)
                {
                    for(int j=0; j<addedImages.size(); j++)
                    {
                        attachments.add(addedImages.get(j));
                    }
                }

                if (attachments != null && attachments.size() > 0)
                {
                    //gridAdapter.notifyDataSetChanged();
                    setGridAdapter();
                    updateUploadButton();
                }
            }
        }
    }

    private void updateUploadButton()
    {
        if(attachments.size() >= 8)
        {
            btnUploadImage.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.upload_button_no_more_images));
            btnUploadImage.setText("No more images can be uploaded.");
            btnUploadImage.setEnabled(false);
        }
        else
        {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.upload_button));
            int size1 = getResources().getDimensionPixelSize(R.dimen.big_btn_txt_size);
            int size2 = getResources().getDimensionPixelSize(R.dimen.mid_btn_txt_size);

            String over = "Upload Images";
            String under = "(you can add " + (8-attachments.size()) + " more)";

            SpannableString span1 = new SpannableString(over);
            span1.setSpan(new AbsoluteSizeSpan(size1), 0, over.length(), SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span2 = new SpannableString(under);
            span2.setSpan(new AbsoluteSizeSpan(size2), 0, under.length(), SPAN_INCLUSIVE_INCLUSIVE);
            span1.setSpan(new StyleSpan(Typeface.BOLD), 0, over.length(), SPAN_INCLUSIVE_INCLUSIVE);

            // let's put both spans together with a separator and all
            CharSequence finalText = TextUtils.concat(span1, "\n", span2);
            btnUploadImage.setText(finalText);
            btnUploadImage.setText("Upload Images\n(You can add "+(8-attachments.size())+" more)");
            btnUploadImage.setEnabled(true);
        }
    }

    private void updateSelectionDialogLabel()
    {
        lblImageCount.setText("(You can add " + (8-attachments.size()) + " more)");
    }
}
