package com.edu.hrbeu.helpsend.seivice;


import com.edu.hrbeu.helpsend.bean.Order;
import com.edu.hrbeu.helpsend.pojo.GrabDetailResponse;
import com.edu.hrbeu.helpsend.pojo.GrabResponse;
import com.edu.hrbeu.helpsend.pojo.MyOrderPojo;
import com.edu.hrbeu.helpsend.pojo.OrderResponse;
import com.edu.hrbeu.helpsend.bean.UpdateInfo;
import com.edu.hrbeu.helpsend.pojo.ResponsePojo;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface OrderService {

   // @Headers({"Content-type:application/json", "Content-Length:59"})
   // @FormUrlEncoded
    @Multipart
    @POST("createorderwithimg")
    Call<UpdateInfo> submitOrder(@Part MultipartBody.Part file, @Part("orderinfo")RequestBody orderinfo);//提交订单+附件
   // Call<String> submitOrder(@Field("orderinfo") String orderinfo);


    @FormUrlEncoded
    @POST("createorderwithoutimg")
    Call<UpdateInfo> submitOrderWithoutAc(@Field("orderinfo") String orderinfo);//提交订单

    @GET("queryselfallorder")
    Call<OrderResponse>getOwnerOrders(@Query("orderOwnerId") String orderOwnerId);//查询该用户的下单情况


    @GET("graborder")
    Call<GrabResponse>getGrabOrders(@QueryMap Map<String,String> map);//查询所有可抢的订单

    @GET("graborderdetails")
    Call<GrabDetailResponse>getGrabOrderDetail(@Query("orderId") String orderId);//查询抢单详细信息

    @GET("querymyputorder")
    Call<ArrayList<MyOrderPojo>>getMyPutOrders(@Query("orderOwnerId") String orderOwnerId,@Query("orderStatus") String orderStatus);//查询抢单详细信息


    @GET("querymyreceiveorder")
    Call<ArrayList<MyOrderPojo>>getMyReceiveOrders(@Query("orderReceiverId") String orderOwnerId,@Query("orderStatus") String orderStatus);

    @GET("queryorderprogress")
    Call<ArrayList<String>>getOrderProgress(@Query("orderId") String orderId);//查询订单流程情况

    @GET("receiveorder")
    Call<ResponsePojo>grabOrder(@Query("orderId") String orderId, @Query("orderReceiverId") String orderReceiverId);//抢单

    @GET("getprice")
    Call<ResponsePojo>getPrice(@Query("location") String positionInfo);//抢单

    @GET("cancelorder")
    Call<ResponsePojo>cancelOrder(@Query("orderId") String orderId);//撤单

    @GET("deliveryorder")
    Call<ResponsePojo>deliveryOrder(@Query("orderId") String orderId);//订单已送达

    @GET("commitorder")
    Call<ResponsePojo>ratingOrder(@Query("orderId") String orderId,@Query("commit") String commit);//评分



}
