package com.example.farming_partner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultActivity extends AppCompatActivity {
    private TextView city_name;
    private TextView temperature;
    private TextView description;
    private ImageView image_weather;
    private ImageView image_humidity;
    private ImageView image_pressure;
    private ImageView image_wind;
    private TextView humidity_number;
    private TextView pressure_number;
    private TextView wind_speed;

    private Button back_button;
    private final String apiKey = "6839f384497ede58a02f06caa5a4f0c4";
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String city = intent.getStringExtra("userInputCity");

        city_name = findViewById(R.id.city_name);
        temperature = findViewById(R.id.temperature);
        description = findViewById(R.id.description);
        image_weather = findViewById(R.id.image_weather);
        back_button = findViewById(R.id.back_button);
        humidity_number = findViewById(R.id.humidity_number);
        pressure_number = findViewById(R.id.pressure_number);
        wind_speed = findViewById(R.id.wind_speed);
        image_humidity = findViewById(R.id.image_humidity);
        image_pressure = findViewById(R.id.image_pressure);
        image_wind = findViewById(R.id.image_wind);

        getWeatherData(city);

        back_button.setOnClickListener(view -> {
            finish();
        });
    }

    private void getWeatherData(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherData> call = weatherApi.getWeatherData(city, apiKey, "metric"); // metric для градусів Цельсія

        call.enqueue(new Callback<WeatherData>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (!response.isSuccessful()) {
                    handleApiError(response);
                } else {
                    WeatherData data = response.body();
                    if (data != null) {
                        city_name.setText(data.name);
                        temperature.setText((int) Math.round(data.main.temp) + "°C");
                        description.setText(data.weather[0].description);
                        humidity_number.setText((int) Math.round(data.main.humidity) + "%");
                        wind_speed.setText(data.wind.speed + "m/s");
                        pressure_number.setText((int) Math.round(data.main.pressure) + "hPa");
                        setDescriptionImage(data);
                        setIcons();
                    } else {
                        // For valid inout city name, but API do not have weather info
                        description.setText(R.string.error_not_have_info);
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                handleError(t);
            }
        });
    }

    private void setDescriptionImage(WeatherData data) {
        String iconName = "im" + data.weather[0].icon;
        int iconId = getResources().getIdentifier(iconName, "drawable", getPackageName());
        if (iconId != 0) {
            image_weather.setImageResource(iconId);
        } else {
            image_weather.setImageResource(R.drawable.im01d);
        }
    }
    private void setIcons() {
        image_wind.setImageResource(R.drawable.wind);
        image_humidity.setImageResource(R.drawable.humidity);
        image_pressure.setImageResource(R.drawable.pressure);
    }

    private void handleApiError(Response<WeatherData> response) {
        if (response.code() == 404) {
            // For invalid input city name
            description.setText(R.string.error_not_found_city);
        } else {
            String errorMessage = "API Error: " + response.code();
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(Throwable t) {
        String errorMessage = "Network Error: " + t.getLocalizedMessage();
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
