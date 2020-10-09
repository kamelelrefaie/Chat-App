package com.example.lapitchat.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapitchat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrl;


public class UsersViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.users_adapter_img)
    CircleImageView usersAdapterImg;
    @BindView(R.id.users_adapter_txt_display)
    TextView usersAdapterTxtDisplay;
    @BindView(R.id.users_adapter_txt_status)
    TextView usersAdapterTxtStatus;
    @BindView(R.id.users_adapter_root)
    public  LinearLayout usersAdapterRoot;
    public View view;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
    }

    public void setTxtDisplay(String string) {
        usersAdapterTxtDisplay.setText(string);
    }


    public void setTxtStatus(String string) {
        usersAdapterTxtStatus.setText(string);
    }
    public void setImage(String string, Context context) {
        onLoadImageFromUrl(usersAdapterImg,string,context);
    }
}



