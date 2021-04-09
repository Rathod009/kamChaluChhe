package com.example.augmentedreality.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.augmentedreality.MainActivity;
import com.example.augmentedreality.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //splash screen for 2sec
        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }
                catch(Exception e){

                }
                finally {
                    //starting main activity after 2 second
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            }
        };
        thread.start();
    }
}