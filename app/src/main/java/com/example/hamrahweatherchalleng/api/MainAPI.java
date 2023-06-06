package com.example.hamrahweatherchalleng.api;

import com.example.hamrahweatherchalleng.models.MainResponse;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainAPI {

    @GET("/v1/forecast?daily=temperature_2m_max")
    Call<MainResponse> getWeatherData(
            @Query("latitude") Double lat ,
            @Query("longitude") Double lon);

}
