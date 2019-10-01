package com.pb.ensadelivery.retrofit;

import com.pb.ensadelivery.models.OrderModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
 
 
public interface ApiInterface {
    @GET("order")
    Call<List<OrderModel>> getOrders(@Query("assigned_to") String assignedTo);

}