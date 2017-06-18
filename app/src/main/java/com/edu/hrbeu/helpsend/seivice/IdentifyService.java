package com.edu.hrbeu.helpsend.seivice;


import com.edu.hrbeu.helpsend.pojo.ResponsePojo;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface IdentifyService {


    @GET("queryidentifystatus")
    Call<ResponsePojo> getUserIdentifyState(@Query("userId") String userId);

    @Multipart
    @POST("createOrderWithImgURL")
    Call<ResponsePojo> submitIdentifyInfo(@PartMap Map<String, RequestBody> params,
                                          @Part("name") RequestBody name, @Part("identify") RequestBody identify);


    /**
     * 获取实例
     */
    Retrofit retrofit = new Retrofit.Builder()
            //设置OKHttpClient,如果不设置会提供一个默认的
            .client(new OkHttpClient())
            //设置baseUrl
            .baseUrl("http://123.207.138.180:7012/webapi/identify/")
            //添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
