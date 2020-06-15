package om.virinchi.com.retrofitpost;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Belal on 11/5/2015.
 */
public interface RegisterAPI {
    @FormUrlEncoded
    @POST("/api/values/SampleDataPost")
    public void insertUser(
            @Field("vcName") String name,

            Callback<Response> callback);
}
