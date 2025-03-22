package com.ajiang.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import will.github.com.xuexuan.androidaop.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_jump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_jump = findViewById(R.id.tv_jump);
        Toast.makeText(this
                ,  " !!!!!!"
                , Toast.LENGTH_LONG).show();
        Toast toast = Toast.makeText(this
                , " !!!!!!"
                , Toast.LENGTH_LONG);
        toast.show();
        tv_jump.setOnClickListener(this);
//        new DovaToast(this)
//                .setText(R.id.tv_content_default, "@@@@@")
//                .setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30)
//                .show();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
