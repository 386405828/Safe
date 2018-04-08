package com.simon.safe.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.simon.safe.R;
import com.simon.safe.activity.BackgroundActivity;

public class RocketService extends Service {

    private WindowManager mWM;
    private int mScreenHeight;
    private int mScreenWidth;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mRocketView;
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            params.y = (Integer) msg.obj;
            //告知窗体更新火箭view的所在位置
            mWM.updateViewLayout(mRocketView, params);
        }

        ;
    };


    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();

        //开启火箭
        showRocket();
        super.onCreate();
    }

    private void showRocket() {
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	默认能够被触摸
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.setTitle("Toast");

        //指定吐司的所在位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT + Gravity.TOP;

        //定义吐司所在的布局,并且将其转换成view对象,添加至窗体(权限)

        mRocketView = View.inflate(this, R.layout.rocket_view, null);

        ImageView iv_rocket = (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();

        mWM.addView(mRocketView, params);

        mRocketView.setOnTouchListener(new View.OnTouchListener() {

            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        //容错处理
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.x > mScreenWidth - mRocketView.getWidth()) {
                            params.x = mScreenWidth - mRocketView.getWidth();
                        }

                        if (params.y > mScreenHeight - mRocketView.getHeight() - 22) {
                            params.y = mScreenHeight - mRocketView.getHeight() - 22;
                        }

                        //告知窗体吐司需要按照手势的移动,去做位置的更新
                        mWM.updateViewLayout(mRocketView, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x > mScreenWidth / 3 && params.x < mScreenWidth * 2 / 3
                                && params.y > mScreenHeight*4/5) {
                            //发射火箭
                            sendRocket();
                            //开启产生尾气的activity
                            Intent intent = new Intent(getApplicationContext(), BackgroundActivity.class);
                            //开启火箭后,关闭了唯一的activity对应的任务栈,所以在此次需要告知新开启的activity开辟一个新的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });
    }

    protected void sendRocket() {
        //在向上的移动过程中,一直去减少y轴的大小,直到减少为0为止
        //在主线程中不能去睡眠,可能会导致主线程阻塞
        new Thread() {

            public void run() {
                for (int i = 0; i < 11; i++) {
                    int height = 350 - i * 35;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = height;
                    mHandler.sendMessage(msg);
                }
            }

            ;
        }.start();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mWM != null && mRocketView != null) {
            mWM.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
