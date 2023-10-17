package com.example.farming_partner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherData> getWeatherData(@Query("q") String city, @Query("appid") String apiKey, @Query("units") String units);
}