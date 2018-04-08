package com.simon.safe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.simon.safe.R;
import com.simon.safe.utils.ActivityStackManager;
import com.simon.safe.utils.LogUtils;
import com.simon.safe.utils.StatusBarUtil;

/**
 * Created by 38640 on 2018/3/20.
 */

public  class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    /**
     * context
     **/
    protected Context ctx;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "--->onCreate()-->" + getClass().getSimpleName());
        ctx = this;
        ActivityStackManager.getActivityStackManager().pushActivity(this);
        setStatusBar();
    }
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }
    protected void setTitle(String name){
        View backView = findViewById(R.id.title_back_iv);
        backView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleTv = findViewById(R.id.title_center_tv);
        titleTv.setText(name);

    }
    /**
     * 跳转Activity
     * skip Another Activity
     *
     * @param activity
     * @param cls
     */
    public static void skipAnotherActivity(Activity activity,
                                           Class<? extends Activity> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 退出应用
     * called while exit app.
     */
    public void exitLogic() {
        ActivityStackManager.getActivityStackManager().popAllActivity();//remove all activity.
        System.exit(0);//system exit.
    }




    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i(TAG, "--->onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "--->onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i(TAG, "--->onRestart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i(TAG, "--->onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i(TAG, "--->onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "--->onDestroy()");
        ActivityStackManager.getActivityStackManager().popActivity(this);
    }
}
