package com.example.audacia.sample;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Finish_Dialogpopup extends Dialog implements View.OnTouchListener {

    private Button addOK, addCancel;


    public Finish_Dialogpopup(Context context){
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish__dialogpopup);

        addOK = (Button)findViewById(R.id.addOK);
        addCancel = (Button)findViewById(R.id.addCancel);

        addOK.setOnTouchListener(this);
        addCancel.setOnTouchListener(this);
    }


    public boolean onTouch(View v, MotionEvent event){
        if(v == addOK){
            dismiss();
        }
        else if(v == addCancel)
            cancel();

        return false;
    }

}
