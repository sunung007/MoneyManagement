package org.androidtown.moneymanagement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
