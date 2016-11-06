package com.example.audacia.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Camera_Finish extends AppCompatActivity {

    TextView addtravelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera__finish);

        addtravelName = (TextView)findViewById(R.id.addtravelName);
        Intent intent = getIntent();
        String travelName = intent.getExtras().getString("travelName");
        addtravelName.setText(travelName);


    }

}
