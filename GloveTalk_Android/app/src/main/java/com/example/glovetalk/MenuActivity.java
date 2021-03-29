package com.example.glovetalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void train(View view){
        Intent intent=new Intent(this,TrainActivity.class);
        startActivity(intent);
    }

    public void reset(View view){
        Intent intent=new Intent(this,ResetActivity.class);
        startActivity(intent);
    }

    public void predict(View view){
        Intent intent=new Intent(this,PredictActivity.class);
        startActivity(intent);
    }

}