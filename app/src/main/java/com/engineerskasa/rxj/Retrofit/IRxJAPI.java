package com.engineerskasa.rxj.Retrofit;

import com.engineerskasa.rxj.Model.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IRxJAPI {
    @FormUrlEncoded
    @POST("register.php")
    Observable<Response<User>> registerNewUser(@Field("phone") String phone,
                                               @Field("name") String name,
                                               @Field("birthdate") String birthdate,
                                               @Field("address") String address);


}
