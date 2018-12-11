package com.app.drylining.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.drylining.R;

/**
 * Created by Panda on 5/30/2017.
 */

public class CustomSpinnerAdapter extends BaseAdapter
{
    Context context;
    String[] entryNames;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, String[] entryNames) {
        this.context = applicationContext;
        this.entryNames = entryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return entryNames.length;
    }

    @Override
    public Object getItem(int position)
    {
        return entryNames[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView = inflter.inflate(R.layout.custom_spinner_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.txt_spinner_item);
        int topBtmPadding = name.getPaddingTop();

        //set left padding
        name.setPadding(topBtmPadding, topBtmPadding, topBtmPadding, topBtmPadding);

        name.setText(entryNames[position]);
        return convertView;
    }
}
