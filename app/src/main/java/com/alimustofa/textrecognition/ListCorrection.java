package com.alimustofa.textrecognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListCorrection extends BaseAdapter {

    Context context;
    String[] countryList;
    int[] flags;
    LayoutInflater inflter;

    public ListCorrection(Context applicationContext, String[] countryList) {
        this.context = context;
        this.countryList = countryList;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_correction, null);
        TextView country = view.findViewById(R.id.resultEt);
        country.setText(countryList[i]);
        return view;
    }
}
