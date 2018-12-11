package com.app.drylining.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener,RequestTaskDelegate {
    private ApplicationData appData;
    private ProgressDialog pdialog;

    private EditText txtPassword, txtReTypePass;
    private TextView lblError, msgError, lblTitle;
    private Button btnSubmit;

    private String txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        lblTitle = (TextView) findViewById(R.id.lbl_title);
        txtPassword = (EditText) findViewById(R.id.txt_password);
        txtReTypePass = (EditText) findViewById(R.id.txt_retype_password);
        lblError = (TextView) findViewById(R.id.lbl_error);
        msgError = (TextView) findViewById(R.id.msg_error);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        txtEmail=getIntent().getStringExtra("email_id");

        txtPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0)
                {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    txtPassword.setSelection(s.length());
                }
                else if(s.length() == 0)
                {
                    txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtReTypePass.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0)
                {
                    txtReTypePass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    txtReTypePass.setSelection(s.length());
                }
                else if(s.length() == 0)
                {
                    txtReTypePass.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initialize();
    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();
        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

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
        }
    }

    public void submitClicked()
    {
        if(mIsPasswordChanged)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        if(txtPassword.getText().toString().equals("") || txtPassword.getText().toString().equals(txtReTypePass.getText().toString()) == false)
        {
            showError("Sorry, passwords you typed don't match. \nTry again.");
            lblTitle.setText("Passwords don't match");
            return;
        }

        showProgressDialog();
        RequestTask reqTask=new RequestTask(AppConstant.FORGOT_PASSWORD,AppConstant.HttpRequestType.ChangePwdRequest);
        reqTask.delegate = this;
        String params = "email="+txtEmail+"&req_type=update_pass&password="+txtPassword.getText().toString().trim();
        Log.e("change password",params);
        reqTask.execute(AppConstant.FORGOT_PASSWORD+"?"+params);

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

    private boolean mIsPasswordChanged;
    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {

        cancelProgressDialog();

        if(completedRequestType== AppConstant.HttpRequestType.ChangePwdRequest) {
            Log.e("Change Password", response);

            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success"))
                {
                    mIsPasswordChanged = true;

                    lblTitle.setText("Password changed");
                    lblError.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.color_success));
                    btnSubmit.setText("Login");
                    txtPassword.setVisibility(View.GONE);
                    txtReTypePass.setVisibility(View.GONE);

                    lblError.setText("SUCCESS!");

                    showError("You successfully changed your password. \nYou can login now.");

                } else {
                   showError("Password update failed,Try again");
                }
            } catch (JSONException e) {
                showError(e.getMessage());
            }
        }
    }


    private void showError(String text){
        msgError.setText(text);
        lblError.setVisibility(View.VISIBLE);
        msgError.setVisibility(View.VISIBLE);
    }

    @Override
    public void timeOut() {
        cancelProgressDialog();
    }

    @Override
    public void codeError(int code) {
        cancelProgressDialog();
    }

    @Override
    public void percentageDownloadCompleted(int percentage, Object record) {

    }
}
