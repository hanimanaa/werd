package com.dimatechs.werd.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAAL3QHrs:APA91bHVvWNG2q0DIpO_qBhjsXKLnzpVRdlqdeYVld25kg6lSqYquNVEwPoXG_iv4Rg4Yw5M9kF3V-BnWULxICUVLTL3mSNDOUbrMF1nNLY7t2eb36BL0UxeVD_wyR3D7NyUKcgLeO6p"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
