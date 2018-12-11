package com.app.drylining.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alahammad.otp_view.OtpView;
import com.app.drylining.R;
import com.app.drylining.model.OTPModel;
import com.app.drylining.retrofit.ApiClient;
import com.app.drylining.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPAcivity extends AppCompatActivity {
    private static final String TAG = "OTPAcivity";
    OtpView otpView;
    ApiInterface apiService;
    String mOTPNumber;
    Button btnVerify;
    String mOTP_SessionId, mUserId;
    EditText etOTPNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_otp_verification);


        Bundle extras = getIntent().getExtras();

        mOTP_SessionId = extras.getString("otp_response");
        mUserId = extras.getString("id");


        initialization();

    }

    private void initialization() {
        etOTPNumber = findViewById(R.id.txt_otp);
        btnVerify = findViewById(R.id.btn_verify);
        brnOkClick();

    }

    private void brnOkClick() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOTPNumber = etOTPNumber.getText().toString();
                if(mOTPNumber.length()==6){
                    Log.e(TAG, "onCreate: OTP NUMBER--->" + mOTPNumber);

                    apiService = ApiClient.getClient().create(ApiInterface.class);
                    Call<OTPModel> otpModelCall = apiService.getOTPSeesionId(mOTP_SessionId, mOTPNumber, mUserId);
                    otpModelCall.enqueue(new Callback<OTPModel>() {
                        @Override
                        public void onResponse(Call<OTPModel> call, Response<OTPModel> response) {
                            Log.e(TAG, "onResponse: " + response.body().getMessage());
                            Log.e(TAG, "onResponse: " + response.body().getStatus());
                            if(response.body().getStatus()==0){
                                startActivity(new Intent(OTPAcivity.this, DashboardActivity.class));
                            }else {
                                Toast.makeText(OTPAcivity.this, "Please Enter Valida OTP", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<OTPModel> call, Throwable t) {

                        }
                    });
                }else {
                    Toast.makeText(OTPAcivity.this, "Please Enter Valida OTP", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

