package com.example.drawabletest.test;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.example.drawabletest.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 动画1 - 设置图片一直旋转
        ImageView imageView = findViewById(R.id.test_iv);
        final ValueAnimator valueAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float num = (float) valueAnimator.getAnimatedValue();
                Log.d("AAAAAAAAA", "循环打印0 - 360之间的值: "+num);
            }
        });

        // 动画2 - 循环获取 0 - 360之间的值
        final ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(0, 360f);
        valueAnimator2.setDuration(1000);
        valueAnimator2.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.setInterpolator(new LinearInterpolator());
        valueAnimator2.start();
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float num = (float) valueAnimator.getAnimatedValue();
                Log.d("BBBBBBBB", "循环打印0 - 360之间的值: "+num);
            }
        });

        // 停止动画
        Button stop_bt = findViewById(R.id.stop_bt);
        stop_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueAnimator.pause();
                valueAnimator2.pause();
            }
        });

        // 启动动画
        Button start_bt = findViewById(R.id.start_bt);
        start_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueAnimator.start();
                valueAnimator2.start();
            }
        });
    }
}
