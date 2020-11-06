package com.example.lapitchat.data.api;

import com.example.lapitchat.data.model.MyResponse;
import com.example.lapitchat.helper.notification.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAb_-vQYo:APA91bHSNLR01G40bxDRluZ-0MVesa_VfkGkocDbMtaSEsXEbtZNKPo1Y428rkearDtUfxxUeRWGZNx0WNHTIUGvELf6TKJRoEooGXsVME3mPhonMgrGUcaZAyArEZCVTiz-hcpLPh2C"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
