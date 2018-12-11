package com.app.drylining;


import android.app.Activity;
import android.app.Dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;

import com.app.drylining.R;

import org.json.JSONArray;


public class Util
{
    public static void logSnack(View view, String msg){
            Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show();
    }


    public static boolean hasJsonArrayContains(JSONArray array,String text){

        int size=array.length();

        for(int i=0;i<size;i++){
            if(array.optString(i).equalsIgnoreCase(text))return true;
        }

        return false;
    }

    public static int getStringIndx(String[] array,String text){

        for(int i=0;i<array.length;i++){
            if(array[i].equalsIgnoreCase(text))return i;
        }

        return 0;
    }

    public static void showNoConnectionDialog(Activity context)
    {
        CountDownTimer lTimer;

        final Dialog dlg= new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setContentView(context.getLayoutInflater().inflate(R.layout.activity_no_net_message, null));
        dlg.show();

        lTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                dlg.dismiss();
            }
        }.start();
    }


}
