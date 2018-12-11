package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.custom.CustomMainActivity;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;

public class JobReviewActivity extends CustomMainActivity implements RequestTaskDelegate {

    private ApplicationData appData;

    public RatingBar ratingBar;
    private Button btnSubmit;
    private EditText txtDescription;

    private JSONObject selectedOfferObj;
    private String selectedOfferId;

    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_review);

        // Initialize RatingBar
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        /*float marks = 5.0f;
        ratingBar.setRating(marks);*/
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        txtDescription = (EditText) findViewById(R.id.txt_description);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        String.valueOf(ratingBar.getRating()), Toast.LENGTH_LONG).show();
                reviewJob();
            }
        });

        setInitialValues();
    }

    private void reviewJob() {
        String marks = String.valueOf(ratingBar.getRating());
        String description = String.valueOf(txtDescription.getText());
        showProgressDialog();
        RequestTask requestTask = new RequestTask(AppConstant.SET_REVIEW_JOB, AppConstant.HttpRequestType.ReviewJobRequest);
        requestTask.delegate = JobReviewActivity.this;
        String type = appData.getUserType();
        String reviewType = "RtoL";
        if (type.equals("L")) reviewType = "LtoR";
        String requestParameter = "&marks=" + marks + "&description=" + description + "&reviewType=" + reviewType;
        requestTask.execute(AppConstant.SET_REVIEW_JOB + selectedOfferId + requestParameter);
    }

    private void setInitialValues() {
        appData = ApplicationData.getSharedInstance();
        try {
            selectedOfferObj = new JSONObject(getIntent().getStringExtra("propertyInfo"));

            JSONObject offerObj = selectedOfferObj.getJSONObject("info");

            selectedOfferId = offerObj.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        AppDebugLog.println("Response is " + response);
        JSONObject properties_response = null;
        try {
            properties_response = new JSONObject(response);
            String status = properties_response.getString("status");
            if (status.equals("success")) {
                String type = appData.getUserType();
                Intent intent;
                if (type.equals("R")) {
                    intent = new Intent(JobReviewActivity.this, AddedOfferDetailActivity.class);
                    //intent.putExtra("propertyInfo", selectedOfferObj.toString());
                } else {
                    intent = new Intent(JobReviewActivity.this, SearchedOfferDetailActivity.class);
                }
                int OfferId = Integer.parseInt(selectedOfferId);
                intent.putExtra("OfferID", OfferId);
                startActivity(intent);
                finish();
            } else {

            }

        } catch (JSONException e) {
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

    @Override
    public void openAdminPanel() {

    }

    @Override
    public void closeAdminPanel() {

    }

    private void showProgressDialog()
    {
        if (!isFinishing() && pdialog == null)
        {
            pdialog = ProgressDialog.show(this, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog()
    {
        if (pdialog != null && !isFinishing())
        {
            pdialog.dismiss();
            pdialog = null;
        }
    }
}
