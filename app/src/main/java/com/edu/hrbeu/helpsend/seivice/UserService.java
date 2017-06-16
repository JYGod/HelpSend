package com.edu.hrbeu.helpsend.seivice;

import com.edu.hrbeu.helpsend.pojo.ResponsePojo;
import com.edu.hrbeu.helpsend.pojo.UserPojo;
import com.edu.hrbeu.helpsend.pojo.VrfCodeResponsePojo;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface UserService {


    @GET("sendquicklogincode")
    Call<VrfCodeResponsePojo> getOwnerOrders(@Query("telNumber") String telNumber);//短信快速登录

    @GET("quicklogin")
    Call<UserPojo> quickLogin(@Query("telNumber") String telNumber);//确认快速登录

    @GET("getexpbyuserid")
    Call<ResponsePojo> getCurrentExp(@Query("userId") String userId);


    /**
     * 获取实例
     */
    Retrofit retrofit = new Retrofit.Builder()
            //设置OKHttpClient,如果不设置会提供一个默认的
            .client(new OkHttpClient())
            //设置baseUrl
            .baseUrl("http://123.207.138.180:7012/webapi/user/")
            //添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
