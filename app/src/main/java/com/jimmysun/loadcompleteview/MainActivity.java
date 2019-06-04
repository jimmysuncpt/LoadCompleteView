package com.jimmysun.loadcompleteview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private LoadCompleteView mLoadCompleteView;
    private EditText mColorEditText, mWidthEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadCompleteView = findViewById(R.id.load_view);
        mLoadCompleteView.setCallback(new LoadCompleteView.Callback() {
            @Override
            public void onComplete() {
                Toast.makeText(MainActivity.this, "已完成", Toast.LENGTH_SHORT).show();
            }
        });
        mColorEditText = findViewById(R.id.et_color);
        mWidthEditText = findViewById(R.id.et_width);
        findViewById(R.id.btn_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int color = Color.parseColor(mColorEditText.getText().toString());
                    mLoadCompleteView.setColor(color);
                    Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btn_width).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int width = Integer.parseInt(mWidthEditText.getText().toString());
                    mLoadCompleteView.setStrokeWidth(DensityUtils.dip2px(MainActivity.this, width));
                    Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadCompleteView.startLoading();
            }
        });
        findViewById(R.id.btn_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadCompleteView.complete();
            }
        });
        findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadCompleteView.dismiss();
            }
        });
    }
}
