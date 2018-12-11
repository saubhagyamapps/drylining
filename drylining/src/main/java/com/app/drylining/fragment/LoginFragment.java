package com.app.drylining.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.app.drylining.R;
import com.app.drylining.Util;
import com.app.drylining.chat.utils.chat.ChatHelper;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;
import com.app.drylining.ui.DashboardActivity;
import com.app.drylining.ui.ForgotPasswordActivity;
import com.app.drylining.ui.MainActivity;
import com.app.drylining.ui.SearchActivity;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, RequestTaskDelegate {
    private ApplicationData appData;
    private MainActivity activity;
    private static final String TAG = "LoginFragment";
    private View view = null;

    private ProgressDialog pdialog;

    private EditText txtEmail, txtPassword;
    private TextView lblError, msgError, btnForgotPwd;
    private Button btnLogin;

    private CheckBox cb_remember;

    public LoginFragment() {
        // Required empty public constructor
    }

    public void tabChanged() {
        AppDebugLog.println("In tabChanged of LoginFragment : ");
    }

    public void tabUnSelected() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppDebugLog.println("In onCreateView of LoginFragment : ");
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_login, container, false);

            txtPassword = (EditText) view.findViewById(R.id.txt_password);
            txtEmail = (EditText) view.findViewById(R.id.txt_email);
            lblError = (TextView) view.findViewById(R.id.lbl_error);
            msgError = (TextView) view.findViewById(R.id.msg_error);
            btnForgotPwd = (TextView) view.findViewById(R.id.btn_forgot_pwd);
            btnLogin = (Button) view.findViewById(R.id.btn_login);

            cb_remember = (CheckBox) view.findViewById(R.id.cb_remember);


            final ViewGroup.LayoutParams params = txtPassword.getLayoutParams();

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

            setHasOptionsMenu(true);
            initialize();
        }
        return view;
    }

    private void initialize() {
        appData = ApplicationData.getSharedInstance();
        activity = (MainActivity) getActivity();
        lblError.setVisibility(View.GONE);
        msgError.setVisibility(View.GONE);

        sendAutoLoginRequest();

        btnForgotPwd.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        txtEmail.setText(appData.getRMB_email());
        txtPassword.setText(appData.getRMB_password());
    }

    @Override
    public void onResume() {
        AppDebugLog.println("In resume of LoginFragment");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                loginClicked();
                break;
            case R.id.btn_forgot_pwd:
                forgotPwdClicked();
                break;
        }
    }

    public void loginClicked() {
        if (isValid()) {
            AppDebugLog.println("coming in yes");
            remember();
            QBLogin();
        } else {
            AppDebugLog.println("coming in not");
            lblError.setVisibility(View.VISIBLE);
            msgError.setVisibility(View.VISIBLE);
        }
    }

    public void remember()
    {
        if(cb_remember.isChecked())
        {
            appData.setRMB_email(txtEmail.getText().toString());
            appData.setRMB_password(txtPassword.getText().toString());
        }
        appData.setUserEmail(txtEmail.getText().toString());
        appData.setUserPassword(txtPassword.getText().toString());
    }

    private void sendAutoLoginRequest() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            String isLogin = appData.getIsLoggedIn();
            if (isLogin.equals("Y")) {
                QBLogin();
            }
        }
        else
        {
            Util.showNoConnectionDialog(activity);
        }
    }

    private void sendLoginRequest() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            String email = appData.getUserEmail();
            String password = appData.getUserPassword();
            RequestTask requestTask = new RequestTask(AppConstant.LOGIN, AppConstant.HttpRequestType.LogInRequest);
            requestTask.delegate = LoginFragment.this;
            String post_content = "username=" + email + "&password=" + password + "&firebase_id=" + appData.getGCMTokenId();
            requestTask.execute(AppConstant.LOGIN, post_content);
        }
        else
        {
            Util.showNoConnectionDialog(activity);
        }
    }

    private void QBLogin() {
        if (appData.getConnectionDetector().isConnectingToInternet()) {
            String email = appData.getUserEmail();
            String password = appData.getUserPassword();

            QBUser user = new QBUser();
            user.setEmail(email);
            user.setLogin(email);
            user.setPassword(password);

            showProgressDialog();
            ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void result, Bundle bundle) {
                    Log.e(TAG, "onSuccess: "+result );
                    sendLoginRequest();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e(TAG, "onError: " +e);
                }
            });
        }
        else
        {
            Util.showNoConnectionDialog(activity);
        }
    }

    public void forgotPwdClicked()
    {
        if (appData.getConnectionDetector().isConnectingToInternet())
        {
            startActivity(new Intent(activity, ForgotPasswordActivity.class));
        }
        else{
            Util.showNoConnectionDialog(activity);
        }
    }

    private boolean isValid() {
        String emailStr = txtEmail.getText().toString();
        String passwordStr = txtPassword.getText().toString();
        boolean isValid = true;
        AppDebugLog.println(emailStr + passwordStr);
        if (passwordStr.length() == 0) {
            msgError.setText("Please enter valid password");
            txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }else{
            txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        }

        if (emailStr.length() == 0 || (!Pattern.matches(AppConstant.EMAIL_VALIDATION, emailStr) && !Pattern.matches(AppConstant.PHONE_VALIDATION, emailStr))) {
            msgError.setText("Please enter valid email");
            txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_error_border));
            isValid = false;
        }else{
            txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
        }

        return isValid;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.txt_email:
                if (hasFocus) {
                    AppDebugLog.println("hasFocus in email:" + hasFocus);
                    txtEmail.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
                }
                break;
            case R.id.txt_password:
                if (hasFocus) {
                    AppDebugLog.println("hasFocus in password:" + hasFocus);
                    txtPassword.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_filled));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType) {
        cancelProgressDialog();
        JSONObject login_response = null;
        if(completedRequestType == AppConstant.HttpRequestType.AutoLogInRequest)
        {
            Log.e("AutoLogin Response", response.toString());
            try {
                login_response = new JSONObject(response);
                String status = login_response.getString("status");

                Log.e("AutoLogin Response", response.toString());

                if (status.equals("success")) {
                    cancelProgressDialog();
                    appData.setIsLoggedIn("Y");

                    String id = "", email = "", name = "",type = "",phone = "";
                    JSONObject c = login_response.getJSONObject("user_info");
                    id = c.getString("id");
                    phone = c.getString("phone");
                    type = c.getString("type");
                    name = c.getString("name");
                    lblError.setVisibility(View.GONE);
                    msgError.setVisibility(View.GONE);


                    if(type.equals("R"))
                    {
                        startActivity(new Intent(activity, DashboardActivity.class));
                        activity.finish();
                    }
                    else
                    {
                        startActivity(new Intent(activity, SearchActivity.class));
                        activity.finish();
                    }

                } else if(status.equals("failed")) {
                    cancelProgressDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (completedRequestType == AppConstant.HttpRequestType.LogInRequest){
            Log.e("Login Response",response.toString());
            try {
                login_response = new JSONObject(response);
                String status = login_response.getString("status");

                Log.e("Login Response",response.toString());

                if (status.equals("success")) {
                    appData.setIsLoggedIn("Y");

                    String id = "", name = "",type = "",phone = "";
                    JSONObject c = login_response.getJSONObject("user_info");
                    id = c.getString("id");
                    phone = c.getString("phone");
                    type = c.getString("type");
                    name = c.getString("name");
                    String email = c.getString("email");
                    String password = c.getString("password");

                    lblError.setVisibility(View.GONE);
                    msgError.setVisibility(View.GONE);

                    appData.setUserId(id);
                    appData.setUserName(name);
                    appData.setUserType(type);
                    appData.setPhoneNumber(phone);
                    appData.setUserEmail(email);
                    appData.setUserPassword(password);

                    if(type.equals("R"))
                    {
                        startActivity(new Intent(activity, DashboardActivity.class));
                        //startActivity(new Intent(activity, AddedOffersActivity.class));
                        activity.finish();
                    }
                    else
                    {
                        startActivity(new Intent(activity, SearchActivity.class));
                        //startActivity(new Intent(activity, SearchedOffersActivity.class));
                        activity.finish();
                    }

                } else {
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
}