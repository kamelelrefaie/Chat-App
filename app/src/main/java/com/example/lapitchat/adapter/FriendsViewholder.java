package com.example.lapitchat.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapitchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrl;
import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrlOff;

public class FriendsViewholder extends RecyclerView.ViewHolder {
    @BindView(R.id.users_adapter_img)
    CircleImageView usersAdapterImg;
    @BindView(R.id.users_adapter_txt_display)
    TextView usersAdapterTxtDisplay;
    @BindView(R.id.users_adapter_txt_status)
    TextView usersAdapterTxtStatus;
    @BindView(R.id.users_adapter_img_online)
    ImageView usersAdapterImgOnline;
    public View view;

    public FriendsViewholder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);

    }
    public void setTxtDisplay(String string) {
        usersAdapterTxtDisplay.setText(string);
    }

    public void setDate(String date){
        usersAdapterTxtStatus.setText(date);

    }

    public void setImage(String string, Context context) {
        onLoadImageFromUrlOff(usersAdapterImg,string,context);
    }

    public void setImageOnline(boolean online) {
        if(online){
            usersAdapterImgOnline.setVisibility(View.VISIBLE);
        }else{
            usersAdapterImgOnline.setVisibility(View.INVISIBLE);

        }

    }
}
