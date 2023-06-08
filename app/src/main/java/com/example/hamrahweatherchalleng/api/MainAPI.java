package com.example.hamrahweatherchalleng.api;

import com.example.hamrahweatherchalleng.models.ResponseMain;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainAPI {

    @GET("/data/2.5/weather?")
    Call<ResponseMain> getWeatherData(
            @Query("lat") Double lat,
            @Query("lon") Double lon,
            @Query("appid") String appid);

}
