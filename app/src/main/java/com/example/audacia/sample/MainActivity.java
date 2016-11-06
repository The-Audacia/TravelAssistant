package com.example.audacia.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.daum.android.map.MapActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Daum map api key
    static final String API_KEY = "847e6978474f073d697d46982b459a1f";

    Button btn_secondPage;
    DialogPopup dialog_popup;
    private static final String KEY_PHOTO_PATH = "photoPath";
    private String photoName = null;
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn_secondPage = (Button)findViewById(R.id.btn_start);
        findViewById(R.id.btn_start).setOnClickListener(mBtnSecondPage);

        dialog_popup = new DialogPopup(MainActivity.this);
        dialog_popup.setTitle("여행명 입력"); //다이얼로그 타이틀 설정

        dialog_popup.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                //intent.putExtra("travelName", dialog_popup.getTravelName());
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "여행지 이름을 입력하였습니다", Toast.LENGTH_SHORT).show();
            }
        });

        dialog_popup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getApplicationContext(), "입력을 취소하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.bottom2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    Button.OnClickListener mBtnSecondPage = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog_popup.show();
        }
    };

}
