package com.app.drylining.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.app.drylining.R;
import com.app.drylining.custom.AppDebugLog;
import com.app.drylining.data.AppConstant;
import com.app.drylining.data.ApplicationData;
import com.app.drylining.network.RequestTask;
import com.app.drylining.network.RequestTaskDelegate;

import org.json.JSONException;
import org.json.JSONObject;


public class ActivityRemoveTool extends Activity implements RequestTaskDelegate
{
    private ApplicationData appData;
    private int selectedToolId;
    private Button btnRemoveYes, btnRemoveNo;
    private ProgressDialog pdialog;
    private ActivityRemoveTool activity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_tool_confirm);
        appData = ApplicationData.getSharedInstance();
        activity = this;

        selectedToolId = getIntent().getIntExtra("SELECTED_TOOL_ID", -1);

        btnRemoveYes = (Button)findViewById(R.id.btn_remove_yes);
        btnRemoveYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appData.getConnectionDetector().isConnectingToInternet())
                {
                    showProgressDialog();
                    RequestTask requestTask = new RequestTask(AppConstant.REMOVE_TOOL, AppConstant.HttpRequestType.RemoveTOOL);
                    requestTask.delegate = ActivityRemoveTool.this;
                    requestTask.execute(AppConstant.REMOVE_TOOL + selectedToolId);
                }
            }
        });

        btnRemoveNo  = (Button)findViewById(R.id.btn_remove_no);
        btnRemoveNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityRemoveTool.this.finish();
            }
        });
    }

    @Override
    public void backgroundActivityComp(String response, AppConstant.HttpRequestType completedRequestType)
    {
        cancelProgressDialog();
        JSONObject resultObj = null;
        try {
            resultObj = new JSONObject(response);
            int result = resultObj.getInt("status");
            String msgResult = resultObj.getString("msg");
            AppDebugLog.println(msgResult);
            if(result == 1)
            {
/*                Intent intent = new Intent(ActivityRemoveTool.this, AddedOffersActivity.class);
                intent.putExtra("OFFER_REMOVED", 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                ActivityRemoveTool.this.finish();
            }

            else
            {
                appData.showUserAlert(activity, "Remove failed", "Tool remove failed. " +
                        " \n Please try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        startActivity((new Intent(ActivityRemoveTool.this, AddedOffersActivity.class)));
                        //ActivityRemoveOffer.this.finish();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showProgressDialog()
    {
        if (!activity.isFinishing() && pdialog == null)
        {
            pdialog = ProgressDialog.show(activity, getResources().getString(R.string.alert_title_loading), getResources()
                    .getString(R.string.alert_body_wait));
        }
    }

    private void cancelProgressDialog()
    {
        if (pdialog != null && !activity.isFinishing())
        {
            pdialog.dismiss();
            pdialog = null;
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
}
