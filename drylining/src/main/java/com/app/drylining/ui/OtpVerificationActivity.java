package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.data.AppConstant;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpVerificationActivity extends AppCompatActivity implements RequestTaskDelegate{


    private AppCompatEditText mEditOtp;
    private AppCompatButton mButVerify;

    private ProgressDialog pdialog;
    private String txtEmail;

    private AppCompatTextView errTitle;
    private AppCompatTextView errMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("OTP Verification");*/

        mEditOtp=(AppCompatEditText)findViewById(R.id.txt_otp);
        mButVerify=(AppCompatButton)findViewById(R.id.btn_verify);

        errTitle=(AppCompatTextView)findViewById(R.id.lbl_error) ;
        errMsg=(AppCompatTextView)findViewById(R.id.msg_error) ;

        errTitle.setVisibility(View.GONE);

        txtEmail=getIntent().getStringExtra("email_id");
        mButVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String otpCode=mEditOtp.getText().toString().trim();

                if(otpCode.length() != 6){
                   // Util.logSnack(mButVerify,"Enter received Otp");
                    showError("Enter received Otp");
                    return;
                }

                showProgressDialog();
                RequestTask reqTask=new RequestTask(AppConstant.FORGOT_PASSWORD,AppConstant.HttpRequestType.VERIFY_EMAIL_OTP);
                reqTask.delegate = OtpVerificationActivity.this;
                String params = "email=" + txtEmail + "&req_type=check_code&security_code=" + otpCode;
                Log.e("verify_code",params);
                reqTask.execute(AppConstant.FORGOT_PASSWORD+"?"+params);

            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void showError(String txt){

        errTitle.setVisibility(View.VISIBLE);
        errMsg.setVisibility(View.VISIBLE);

        errMsg.setText(txt);
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
       if(completedRequestType== AppConstant.HttpRequestType.VERIFY_EMAIL_OTP){
            Log.e("Verify otp code",response);

           try {
               JSONObject object=new JSONObject(response);
               if(object.getString("status").equals("success")){
                   Intent intent=new Intent(getApplicationContext(),ChangePasswordActivity.class);
                   intent.putExtra("email_id",txtEmail);
                   startActivity(intent);
                 /*  intent.putExtra("email_id",object.getString("email"));
                   startActivity(intent);*/
               }else {
                   showError("Invalid OTP, Please enter the code received in your email");
                   //Util.logSnack(mButVerify,"Invalid OTP ,please enter the code received in your email");
               }
           } catch (JSONException e) {
               showError(e.getMessage());
           }

        }

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

    @Override
    public void timeOut() {
        cancelProgressDialog();
        Util.logSnack(mButVerify,"Check your network connection");
    }

    @Override
    public void codeError(int code) {
        cancelProgressDialog();
        Util.logSnack(mButVerify,"Try again after some time");
    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record) {

    }
}
