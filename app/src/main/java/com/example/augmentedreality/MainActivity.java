package com.example.augmentedreality;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.augmentedreality.Modules.Doodler.Doodler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent Doodler = new Intent(MainActivity.this, Doodler.class);
        startActivity(Doodler);
        finish();
    }
}
