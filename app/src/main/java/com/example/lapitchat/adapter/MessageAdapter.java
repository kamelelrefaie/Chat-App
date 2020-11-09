package com.example.lapitchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapitchat.R;
import com.example.lapitchat.data.model.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.lapitchat.R.drawable.messgae_shape_white;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {


    private ArrayList<Messages> messagesArrayList = new ArrayList<>();
    private String currentId;
    private Context context;
    public MessageAdapter(ArrayList<Messages> messagesArrayList, String currentId, Context context) {
        this.messagesArrayList = messagesArrayList;
        this.currentId = currentId;
        this.context=context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        String fromUser = messagesArrayList.get(position).getFrom();
        if (currentId.equals(fromUser)) {

                holder.messageImgPerson.setVisibility(View.GONE);
        } else {
            holder.messageTxtMsg.setBackground(ContextCompat.getDrawable(context, messgae_shape_white));
            holder.messageTxtMsg.setTextColor(Color.parseColor("#FF4081"));
        }
        holder.messageTxtMsg.setText(messagesArrayList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public void setList(ArrayList<Messages> list) {
        this.messagesArrayList = list;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_img_person)
        CircleImageView messageImgPerson;
        @BindView(R.id.message_txt_msg)
        TextView messageTxtMsg;

        @BindView(R.id.message_item_root)
        RelativeLayout messageItemRoot;
        public View view;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }


}
