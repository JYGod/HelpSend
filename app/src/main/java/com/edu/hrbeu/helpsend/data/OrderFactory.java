package com.edu.hrbeu.helpsend.data;


import com.edu.hrbeu.helpsend.seivice.OrderService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderFactory {

    private final static String BASE_URL="http://123.207.138.180:7012/webapi/order/";


    public static OrderService create(){

        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                //设置OKHttpClient,如果不设置会提供一个默认的
                .client(client)
                //设置baseUrl
                .baseUrl(BASE_URL)
                //添加Gson转换器
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(OrderService.class);
    }
}
