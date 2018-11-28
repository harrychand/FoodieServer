package foodie.example.com.foodieserver.Remote;

import foodie.example.com.foodieserver.Model.MyResponse;
import foodie.example.com.foodieserver.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_SvSWTo:APA91bFpA1-gUhA0RtUxrO9M59A8r74piQoTb0nQq5Scy4RmuOxQaDI_dKtysDlZPZ_tWPC3b_5kt3pu5LmjsmjvIta5Lv3cL9Kjl56p_dvm8c04RV7KiHLFsOKAWyjjY1t2ZGqkQvio"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
