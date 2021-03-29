package com.example.glovetalk;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utilities=new Utilities();
    }

    public void next(View view){

        Intent intent;

        EditText ip_text=(EditText)findViewById(R.id.text_ip);
        EditText port_text=(EditText)findViewById(R.id.text_port);
        EditText name_text =  (EditText)findViewById(R.id.text_name);

        utilities.IP=ip_text.getText().toString();
        utilities.PORT=Integer.valueOf(port_text.getText().toString());

        utilities.user_name =name_text.getText().toString();

        intent=new Intent(this,MenuActivity.class);
        startActivity(intent);
    }
}