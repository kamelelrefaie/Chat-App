package com.example.lapitchat.helper;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.lapitchat.R;
import com.example.lapitchat.helper.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class HelperMethods {
    public static void replaceFragment(FragmentManager getChildFragmentManager, int id, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager.beginTransaction();
        transaction.replace(id, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public static void onLoadImageFromUrl(ImageView imageView, String URl, Context context) {
        Glide.with(context)
                .load(URl)
                .placeholder(R.drawable.ic_default) //create place holder
                .into(imageView);
    }
    public static void onLoadImageFromUrlOff(ImageView imageView, String URl, Context context) {
        Glide.with(context)
                .load(URl)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_default) //create place holder
                .into(imageView);
    }

    public static void updateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }


}
