package com.example.farming_partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

        // Splash
        View rootView = getWindow().getDecorView().getRootView();
        rootView.postDelayed(() -> {
            Intent intent = new Intent(SplashPage.this, login.class);
            startActivity(intent);
            finish(); // Optional: Depending on your use case, you might want to finish the Splash_Screen activity here.
        }, 1000);
    }
}
