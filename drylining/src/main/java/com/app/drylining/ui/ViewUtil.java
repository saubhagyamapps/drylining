package com.app.drylining.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.app.drylining.R;

/**
 * Created by sakthivel on 4/30/2017.
 */

public class ViewUtil {

    public static void setSpinnerColor(Context contxt,Spinner spinner,int colorCode){
        spinner.getBackground().setColorFilter(ContextCompat.getColor(contxt,colorCode), PorterDuff.Mode.SRC_ATOP);
    }

    public static void setSpinnerDropDownView(Context contxt,Spinner spinner,int arrayId){

        String[] array=contxt.getResources().getStringArray(arrayId);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String> (contxt, android.R.layout.simple_spinner_item,array);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);

    }

}
