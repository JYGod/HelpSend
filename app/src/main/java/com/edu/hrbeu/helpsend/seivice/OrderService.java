package com.edu.hrbeu.helpsend.seivice;


import com.edu.hrbeu.helpsend.bean.Order;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface OrderService {

  //  @Multipart
   // @Headers({"Content-type:application/json", "Content-Length:59"})
    @FormUrlEncoded
    @POST("createorder")
    //Call<String> submitOrder(@Part MultipartBody.Part photo, @Part("orderinfo")RequestBody orderinfo);
    Call<String> submitOrder(@Field("orderinfo") String orderinfo);

    /**获取实例*/
    Retrofit retrofit = new Retrofit.Builder()
            //设置OKHttpClient,如果不设置会提供一个默认的
            .client(new OkHttpClient())
            //设置baseUrl
            .baseUrl("http://mengqipoet.cn:8080/webapi/order/")
            //添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
