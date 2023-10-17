package com.example.farming_partner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WeatherActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_button;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        user_field = findViewById(R.id.user_field);
        main_button = findViewById(R.id.main_button);

        main_button.setOnClickListener(view -> {
            if (user_field.getText().toString().trim().equals("")) {
                Toast.makeText(WeatherActivity.this, R.string.not_valid_input, Toast.LENGTH_LONG).show();
            } else {
                String userInputCity = user_field.getText().toString();
                Intent intent = new Intent(WeatherActivity.this, ResultActivity.class);
                intent.putExtra("userInputCity", userInputCity);
                startActivity(intent);
            }
        });
    }
}
