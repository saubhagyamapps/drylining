package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener,RequestTaskDelegate {
    private ApplicationData appData;
    private ProgressDialog pdialog;

    private EditText txtEmail;
    private TextView lblError, msgError, btnBack,lblTitle;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        lblTitle = (TextView) findViewById(R.id.lbl_title);
        txtEmail = (EditText) findViewById(R.id.txt_email);
        lblError = (TextView) findViewById(R.id.lbl_error);
        msgError = (TextView) findViewById(R.id.msg_error);
        btnBack = (TextView) findViewById(R.id.btn_back);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

//        txtEmail.setText("panda1988501@hotmail.com");

        initialize();
    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();
        lblError.setVisibility(View.GONE);

        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        AppDebugLog.println("In resume of LoginFragment");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submitClicked();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    public void submitClicked()
    {
        String email=txtEmail.getText().toString().trim();

        if(email.length()==0)
        {
            Toast.makeText(this, "Please type e-mail...", Toast.LENGTH_SHORT).show();
            return;
        }else if(email.split("@").length!=2){
            Toast.makeText(this, "InvalidEmail Address...", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        RequestTask reqTask=new RequestTask(AppConstant.FORGOT_PASSWORD,AppConstant.HttpRequestType.ForgotPwdRequest);
        reqTask.delegate = this;
        String params = "email=" + email + "&req_type=send_code";
        Log.e("forgot_pwd request",params);
        reqTask.execute(AppConstant.FORGOT_PASSWORD + "?" + params);

/*
        showProgressDialog();
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    cancelProgressDialog();
                    startActivity(new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class));
                    ForgotPasswordActivity.this.finish();
                }
            }
        };
        timerThread.start();*/
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
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        if(completedRequestType== AppConstant.HttpRequestType.ForgotPwdRequest){
            Log.e("Send code",response);

            try {
                JSONObject object=new JSONObject(response);
                if(object.getString("status").equals("success")){
                    Intent intent=new Intent(getApplicationContext(),OtpVerificationActivity.class);
                    intent.putExtra("email_id",object.getString("email"));
                    startActivity(intent);
                }else {
                    if(object.getString("msg").equals("no_email")){
                        lblError.setVisibility(View.VISIBLE);
                        msgError.setVisibility(View.VISIBLE);
                        msgError.setText(R.string.msg_forgot_email_error);
                        lblTitle.setText(R.string.lbl_email_not_found);
                    }
                    else if(object.getString("msg").equals("failed_send_mail"))
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Couldn't send mail from server.\n Please try again."
                                ,Toast.LENGTH_SHORT ).show();
                    }
                }
            } catch (JSONException e) {
                Util.logSnack(btnSubmit,e.getMessage());
            }
        }
    }

    @Override
    public void timeOut() {
        cancelProgressDialog();
        Util.logSnack(btnSubmit,"Check your network connection");
       // Toast.makeText(this, "Check your network connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void codeError(int code) {
        cancelProgressDialog();
        Util.logSnack(btnSubmit,"Try again, network issue");
        //Toast.makeText(this,"Try again, network issue",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record) {

    }
}
