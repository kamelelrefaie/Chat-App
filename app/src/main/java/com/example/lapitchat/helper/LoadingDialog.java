package com.example.lapitchat.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.lapitchat.R;

public class LoadingDialog {
    private AlertDialog alertDialog;
    private Activity activity;

    public LoadingDialog(Activity activity){
        this.activity =activity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progressbar,null));
        builder.setCancelable(false);
        alertDialog =builder.create();
        alertDialog.show();
    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }
}
