package com.simon.safe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.simon.safe.R;

public class BackgroundActivity extends AppCompatActivity {

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            finish();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_background);

        ImageView iv_top = (ImageView) findViewById(R.id.iv_top);
        ImageView iv_bottom = (ImageView) findViewById(R.id.iv_bottom);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        iv_top.startAnimation(alphaAnimation);
        iv_bottom.startAnimation(alphaAnimation);
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }
}
