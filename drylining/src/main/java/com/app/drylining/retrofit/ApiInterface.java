package com.app.drylining.retrofit;


import com.app.drylining.model.LogoutModel;
import com.app.drylining.model.MSGCountModel;
import com.app.drylining.model.NotificationReadModel;
import com.app.drylining.model.OTPModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ApiInterface {
    //http://dryliningapp.com/admin/api/verify_otp.php
    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<OTPModel> getOTPSeesionId(@Field("OTPSessionId") String OTPSessionId, @Field("OTPNumber") String OTPNumber, @Field("uid") String userID);

    //http://dryliningapp.com/admin/api/notification_read.php?notification_id=2980
    @FormUrlEncoded
    @POST("notification_read.php")
    Call<NotificationReadModel> getNotificationBit(@Field("notification_id") String notification_id,
                                                   @Field("interest_id") String interest_id,
                                                   @Field("confirm_id") String confirm_id);

    //http://dryliningapp.com/admin/api/all_notification_count.php?click=1
    @FormUrlEncoded
    @POST("all_notification_count.php")
    Call<MSGCountModel> getCountBit(@Field("click") String click, @Field("userId") String userId);

    //http://dryliningapp.com/admin/api/logout.php?user_id=156
    @FormUrlEncoded
    @POST("logout.php")
    Call<LogoutModel> logout(@Field("user_id") String userId);

}
