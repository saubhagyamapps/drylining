package com.app.drylining.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.chat.utils.chat.ChatHelper;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.MainActivity;
import com.app.drylining.ui.OTPAcivity;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener, RequestTaskDelegate {
    private ApplicationData appData;
    private MainActivity activity;
    private View view = null;

    private ProgressDialog pdialog;

    private EditText txtName, txtEmail, txtPhone, txtPassword, txtRetypePwd;
    private TextView lblError, msgError;
    private Button btnSignUp;
    private CheckBox cbRenter, cbLessee, cbAgree;
    private TextView txt_agree;

    private boolean isRenter = true;
    private ScrollView scrollView;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public void tabChanged() {
        AppDebugLog.println("In tabChanged of SignUpFragment : ");
    }

    public void tabUnSelected() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppDebugLog.println("In onCreateView of SignUpFragment : ");
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_signup, container, false);
            scrollView = (ScrollView) view;
            txtName = (EditText) view.findViewById(R.id.txt_name);
            txtEmail = (EditText) view.findViewById(R.id.txt_email);
            txtPhone = (EditText) view.findViewById(R.id.txt_mobile);
            txtPassword = (EditText) view.findViewById(R.id.txt_password);
            txtRetypePwd = (EditText) view.findViewById(R.id.txt_retype_password);
            lblError = (TextView) view.findViewById(R.id.lbl_error);
            msgError = (TextView) view.findViewById(R.id.msg_error);
            btnSignUp = (Button) view.findViewById(R.id.btn_sign_up);
            cbRenter = (CheckBox) view.findViewById(R.id.cb_renter);
            cbLessee = (CheckBox) view.findViewById(R.id.cb_lessee);
            cbAgree = (CheckBox) view.findViewById(R.id.cb_agree);
            txt_agree = (TextView) view.findViewById(R.id.txt_agree);

            cbRenter.setChecked(true);
            cbLessee.setChecked(false);
            cbAgree.setChecked(false);

            updateCheckTextStatus(R.id.txt_chk_renter, true);
            updateCheckTextStatus(R.id.txt_chk_lessee, false);

            txtPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtPassword.setSelection(s.length());
                    } else if (s.length() == 0) {
                        txtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            txtRetypePwd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        txtRetypePwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        txtRetypePwd.setSelection(s.length());
                    } else if (s.length() == 0) {
                        txtRetypePwd.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            cbLessee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    updateCheckTextStatus(R.id.txt_chk_lessee, b);
                }
            });

            cbRenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    updateCheckTextStatus(R.id.txt_chk_renter, b);
                }
            });

            cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    view.findViewById(R.id.chkLayout).setBackgroundColor(0x00ffffff);
                    txt_agree.setTextColor(ContextCompat.getColor(activity, R.color.edit_text_color));
                }
            });

            cbRenter.setOnClickListener(this);
            cbLessee.setOnClickListener(this);
            // btnSignUp.setOnClickListener(this);

            setHasOptionsMenu(true);
            initialize();
            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signUpClicked();
                }
            });
        }
        return view;
    }

    private void updateCheckTextStatus(int id, boolean checked) {
        ((TextView) view.findViewById(id)).setTextColor(checked ? ContextCompat.getColor(getActivity(), R.color.color_checked) : ContextCompat.getColor(getActivity(), R.color.color_unchecked));
    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();
        activity = (MainActivity) getActivity();
        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);
        TelephonyManager tMgr = (TelephonyManager) this.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String phoneNumber = tMgr.getLine1Number();
        txtPhone.setText(phoneNumber);
    }

    @Override
    public void onResume() {
        AppDebugLog.println("In resume of SignUpFragment");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_renter:
                CheckBox renterCB = (CheckBox) view;
                if (renterCB.isChecked()) {
                    cbLessee.setChecked(false);
                } else {
                    cbLessee.setChecked(true);
                }
                break;
            case R.id.cb_lessee:
                CheckBox lesseeCB = (CheckBox) view;
                if (lesseeCB.isChecked()) {
                    cbRenter.setChecked(false);
                } else {
                    cbRenter.setChecked(true);
                }
                break;
            case R.id.btn_sign_up:
                signUpClicked();
                break;
            default:
                break;
        }
    }

    private void signUpClicked() {
        if (isValid()) {
            qbSignup();
        } else {
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            focusToError();
        }
    }

    private void qbSignup() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            showProgressDialog();
            String strName = txtName.getText().toString();
            String strEmail = txtEmail.getText().toString();
            final String strPass = txtPassword.getText().toString();

            final QBUser user = new QBUser(strEmail, strPass, strEmail);
            user.setFullName(strName);
            StringifyArrayList<String> tags = new StringifyArrayList<>();
            tags.add("live");
            user.setTags(tags);

            QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    user.setPassword(strPass);
                    ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void result, Bundle bundle) {
                            sendRegisterRequest();
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }

                @Override
                public void onError(QBResponseException error) {
                    if (error != null) {
                        String message = error.getMessage();
                        msgError.setText(message);
                        msgError.setVisibility(View.VISIBLE);
                        lblError.setVisibility(View.VISIBLE);
                        focusToError();
                        cancelProgressDialog();
                    }
                }
            });
        } else {
            Util.showNoConnectionDialog(activity);
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        txtName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtRetypePwd.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        txtPhone.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));

        AppDebugLog.println("Hurray here");
        String strName = txtName.getText().toString();
        String strEmail = txtEmail.getText().toString();
        String strPass = txtPassword.getText().toString();
        String strRepass = txtRetypePwd.getText().toString();
        String strPhone = txtPhone.getText().toString();

        if (strRepass.length() == 0 || !strPass.equals(strRepass)) {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtRetypePwd.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        if (strPass.length() == 0) {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }
        if (strPass.length() < 8) {
            isValid = false;
            msgError.setText(R.string.msg_no_8_password);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }
        if (strPhone.equals("") || !Pattern.matches(AppConstant.PHONE_VALIDATION, strPhone)) {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtPhone.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }
        if (strEmail.equals("") || !Pattern.matches(AppConstant.EMAIL_VALIDATION, strEmail)) {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }
        if (strName.equals("")) {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            txtName.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
        }

        if (!cbAgree.isChecked()) {
            isValid = false;
            msgError.setText(R.string.msg_signup_error);
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
            view.findViewById(R.id.chkLayout).setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error));
            txt_agree.setTextColor(ContextCompat.getColor(activity, R.color.white));
        }

        return isValid;
    }

    private String getPostContent() {
        String strName = txtName.getText().toString();
        String strEmail = txtEmail.getText().toString();
        String strPhone = txtPhone.getText().toString();
        String strPass = txtPassword.getText().toString();
        String strShareProfile = "0";
        String strType = "R";
        boolean isRenter = cbRenter.isChecked();
        boolean shareProfile = cbAgree.isChecked();

        if (shareProfile) {
            strShareProfile = "1";
        }
        if (!isRenter) {
            strType = "L";
        }

        // String postContent = "name=" + strName + "&password=" + strPass + "&email=" + strEmail + "&type=" + strType + "&shareProfile=" + strShareProfile;
        String postContent = "name=" + strName + "&password=" + strPass + "&phone=" + strPhone + "&email=" + strEmail +
                "&type=" + strType + "&shareProfile=" + strShareProfile + "&firebase_id=" + appData.getGCMTokenId();
        return postContent;
    }

    private void sendRegisterRequest() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
//            showProgressDialog();
            lblError.setVisibility(View.GONE);
            msgError.setVisibility(View.GONE);
            RequestTask requestTask = new RequestTask(AppConstant.SIGNUP, AppConstant.HttpRequestType.RegisterRequest);
            requestTask.delegate = SignUpFragment.this;
            requestTask.execute(AppConstant.SIGNUP, getPostContent());
        } else {
            Util.showNoConnectionDialog(activity);
        }
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        AppDebugLog.println(response);
        JSONObject signup_response = null;
        try {
            signup_response = new JSONObject(response);
            String status = signup_response.getString("status");
            String msg = signup_response.getString("msg");
            if (status.equals("success")) {

                String id = "", name = "", type = "", phone = "";
                JSONObject c = signup_response.getJSONObject("user_info");
                id = c.getString("id");
                phone = c.getString("phone");
                type = c.getString("type");
                name = c.getString("name");
                String email = c.getString("email");
                String password = c.getString("password");
                String mOTP_sessionId = c.getString("otp_response");

                lblError.setVisibility(View.GONE);
                msgError.setVisibility(View.GONE);

                appData.setIsLoggedIn("Y");
                appData.setUserId(id);
                appData.setUserName(name);
                appData.setUserType(type);
                appData.setPhoneNumber(phone);
                appData.setUserEmail(email);
                appData.setUserPassword(password);


                if (type.equals("R")) {
                    // startActivity(new Intent(activity, DashboardActivity.class));
                    Intent intent = new Intent(activity, OTPAcivity.class);
                    intent.putExtra("otp_response", mOTP_sessionId);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    activity.finish();
                } else {
                    //startActivity(new Intent(activity, SearchActivity.class));
//                    startActivity(new Intent(activity, OTPAcivity.class));
                    Intent intent = new Intent(activity, OTPAcivity.class);
                    intent.putExtra("otp_response", mOTP_sessionId);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    activity.finish();
                }
            } else {
               // cancelProgressDialog();
                msgError.setText(msg);
                msgError.setVisibility(View.VISIBLE);
                lblError.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
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


    private void focusToError() {

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, lblError.getTop());
            }
        });

    }

}
