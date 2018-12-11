package com.app.drylining.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.app.drylining.custom.CustomAutoCompleteListener;
import com.app.drylining.custom.CustomSpinnerAdapter;
import com.app.drylining.custom.ExpandableHeightGridView;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask1;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.AddNewToolActivity;
import com.app.drylining.ui.DashboardActivity;
import com.app.drylining.ui.MultiPhotoSelectActivity;
import com.app.drylining.ui.SearchActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddToolByAddressFragment extends Fragment implements View.OnClickListener, RequestTaskDelegate
{
    private ApplicationData appData;
    private AddNewToolActivity activity;
    private View view = null;

    private ProgressDialog pdialog;

    private TextView lblError, msgError;
    private AutoCompleteTextView txtCityName;
    private AppCompatEditText txtPostName, txtStreet, txtUnity, txtDescription;
    private TextView tPrice;

    private ArrayList<String> attachments;
    private LayoutInflater inflater;
    private ExpandableHeightGridView gridView;
    private GridAdapter gridAdapter;

    private RelativeLayout selectionDialog;
    private TextView btnAlbum, btnCamera,lblImageCount;

    private RelativeLayout spPropContainer, spRoomContainer, spGarageContainer;
    private Spinner spToolType,spRoom,spType;
    private CustomSpinnerAdapter spPropertyAdapter, spRoomAdapter, spTypeAdapter;

    private String postToolType, postCurrentType, postGarage;

    private Button btnUploadImage, btnAddOffer;

    protected static final int REQUEST_CAMERA = 1;
    protected static final int REQUEST_IMAGE = 2;

    private  CustomAutoCompleteListener listener;

    public AddToolByAddressFragment() {
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
            view = inflater.inflate(R.layout.fragment_add_tool_by_address, container, false);

            lblError = (TextView) view.findViewById(R.id.lbl_error);
            txtPostName = (AppCompatEditText) view.findViewById(R.id.txt_post_name);
            txtCityName = (AutoCompleteTextView) view.findViewById(R.id.txt_city_name);
            txtStreet = (AppCompatEditText) view.findViewById(R.id.txt_street_name);
            //txtUnity = (AppCompatEditText) view.findViewById(R.id.txt_unit_number);
            txtDescription = (AppCompatEditText) view.findViewById(R.id.txt_description);
            tPrice = (TextView) view.findViewById(R.id.txt_price);
            msgError = (TextView) view.findViewById(R.id.msg_error);
            //btnCamera = (TextView) view.findViewById(R.id.btnCamera);
            btnAlbum = (TextView) view.findViewById(R.id.btnAlbum);
            lblImageCount = (TextView) view.findViewById(R.id.lblImgCount);

            spPropContainer = (RelativeLayout) view.findViewById(R.id.sp_PropContainer);
            spRoomContainer = (RelativeLayout) view.findViewById(R.id.sp_RoomTypeContainer);
            //spGarageContainer = (RelativeLayout) view.findViewById(R.id.sp_GarageContainer);
            spToolType = (Spinner) view.findViewById(R.id.spinerProperty);
            spRoom = (Spinner) view.findViewById(R.id.spinnerRoom);
            //spType = (Spinner) view.findViewById(R.id.sppinerType);
            btnUploadImage = (Button) view.findViewById(R.id.btn_upload);
            btnAddOffer = (Button) view.findViewById(R.id.btn_add_offer);
            gridView = (ExpandableHeightGridView) view.findViewById(R.id.gridView);
            selectionDialog = (RelativeLayout) view.findViewById(R.id.selectionDialog);

            setHasOptionsMenu(true);

            initialize();

            updateUploadButton();
        }
        return view;
    }

    private void initialize()
    {
        appData = ApplicationData.getSharedInstance();
        activity = (AddNewToolActivity) getActivity();

        attachments = new ArrayList<String>();

        listener=new CustomAutoCompleteListener(getContext(),txtCityName,true);
        txtCityName.addTextChangedListener(listener);

        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);
        selectionDialog.setVisibility(View.GONE);
        btnAlbum.setOnClickListener(this);
        //btnCamera.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);

        spPropertyAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.tool_types));
        spToolType.setAdapter(spPropertyAdapter);
        spToolType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                if (tv != null) tv.setText("Type of tool - "+tv.getText());

                postToolType = getResources().getStringArray(R.array.tool_types)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spToolType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int width= txtCityName.getWidth();
                spToolType.setDropDownWidth(width);
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
                int width= txtCityName.getWidth();
                spRoom.setDropDownWidth(width);
                return false;
            }
        });

        /*spTypeAdapter = new CustomSpinnerAdapter(getActivity(), getResources().getStringArray(R.array.property_extra_spaces));
        spType.setAdapter(spTypeAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) parent.getSelectedView();
                tv.setText("Type of Work - "+tv.getText());

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
        });*/

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
        setGridAdapter();
    }

    private void sendAddOfferRequest()
    {
        String postName = txtPostName.getText().toString();
        String postPrice = tPrice.getText().toString();
        String postCity = txtCityName.getText().toString();
        String postStreet = txtStreet.getText().toString();

        String postDesc = txtDescription.getText().toString();
        String postUserId = appData.getUserId();
        String postToolType = "";
        String postCurrencyType = "";

        postName = "name~" + postName;
        postPrice = "price~" + postPrice;
        postCurrencyType = "currency~" + this.postCurrentType;
        postCurrencyType = postCurrencyType.substring(0, postCurrencyType.indexOf('('));

        postCity = "city~" + postCity;
        postStreet = "street~" + postStreet;
        postToolType = "toolType~" + this.postToolType;
        postDesc = "description~" + postDesc;

        String location = appData.getCurrentLocation();
        Double currentLatitude = 0.0;
        Double currentLongitude = 0.0;
        if (location.length() > 0) {
            currentLatitude = Double.parseDouble(location.split(",")[0]);
            currentLongitude = Double.parseDouble(location.split(",")[1]);
        }

        String postLongitude = "postLongitude~" + currentLongitude;
        String postLatitude = "postLatitude~" + currentLatitude;

        postUserId = "postUserId~" + postUserId;
        String postAddType = "postUserType~" + appData.getUserType();

        ArrayList<String> postAttachments = new ArrayList<>();
        for (int i = 0; i < attachments.size(); i++) {
            String attachment = AppConstant.NULL_STRING;
            Log.e("Total attachments are", "" + attachments.size());
            if (!attachments.get(i).equals("")) {
                attachment = "propertyImage~" + attachments.get(i);
                postAttachments.add(attachment);
            }
        }

        if (appData.getConnectionDetector().isConnectingToInternet()) {
            showProgressDialog();
            lblError.setVisibility(View.GONE);
            msgError.setVisibility(View.GONE);
            RequestTask1 requestTask = new RequestTask1(AppConstant.ADD_TOOL, AppConstant.HttpRequestType.addTool, activity, postAttachments, true);
            requestTask.delegate = AddToolByAddressFragment.this;
            requestTask.execute(AppConstant.ADD_TOOL, postName, postPrice, postCurrencyType, postCity, postStreet, postToolType,
                    postLongitude, postLatitude, postUserId, postDesc, postAddType);
        } else {
            Util.showNoConnectionDialog(getActivity());
        }
    }

    private void setGridAdapter() {
        if (gridAdapter == null) {
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

        if(attachments.size()==0)
        {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.gray_red));
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
                updateSelectionDialogLabel();
                break;
            case R.id.ic_delete:
                String attachment = (String) view.getTag();
                attachments.remove(attachment);
                gridAdapter.notifyDataSetChanged();
                updateUploadButton();
                break;
        }
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
                if (appData.getUserType().equals("L"))
                    i = new Intent(activity, SearchActivity.class);
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            CellHolder holder = null;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cell_grid, parent, false);
                holder = new GridAdapter.CellHolder();
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
            holder.imgDelete.setOnClickListener(AddToolByAddressFragment.this);
            holder.imgDelete.setTag(attachment);

            if (attachment != null)
            {
                int width = getResources().getDimensionPixelSize(R.dimen.profile_width);
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
            if (photoFile != null) {
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
                if (captureImagePath.length() > 0)
                {
                    filePath = captureImagePath;

                    if (filePath != null && filePath.length() > 0)
                    {
                        attachments.add(filePath);
                        gridAdapter.notifyDataSetChanged();
                        updateUploadButton();
                    }
                }
            }
            else if (requestCode == REQUEST_IMAGE && data != null)
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
                    gridAdapter.notifyDataSetChanged();
                    updateUploadButton();
                }
            }

            else if(requestCode == AppConstant.PAYMENT_REQUEST)
            {
                sendAddOfferRequest();
            }
        }
    }

    private void updateUploadButton()
    {
        if(attachments.size() >= 8)
        {
            btnUploadImage.setBackgroundColor(getResources().getColor(R.color.upload_button_no_more_images));
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
            span1.setSpan(new StyleSpan(Typeface.BOLD), 0, over.length(), SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span2 = new SpannableString(under);
            span2.setSpan(new AbsoluteSizeSpan(size2), 0, under.length(), SPAN_INCLUSIVE_INCLUSIVE);

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
