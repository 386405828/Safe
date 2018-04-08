package com.simon.safe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.simon.safe.R;
import com.simon.safe.engine.AddressDao;

public class QueryAddressActivity extends BaseActivity implements View.OnClickListener {

    /** 请输入电话号码 */
    private EditText mEtPhone;
    /** 查询 */
    private Button mBtQuery;
    private TextView mTvQueryResult;
    private String mAddress;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            //4,控件使用查询结果
            mTvQueryResult.setText(mAddress);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        setTitle("归属地查询");
        initView();
        initEvent();
    }

    private void initEvent() {
        //5,实时查询(监听输入框文本变化)
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = mEtPhone.getText().toString();
                query(phone);
            }
        });
    }

    private void initView() {
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mBtQuery = (Button) findViewById(R.id.bt_query);
        mBtQuery.setOnClickListener(this);
        mTvQueryResult = (TextView) findViewById(R.id.tv_query_result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.bt_query:
                String phone = mEtPhone.getText().toString();
                if(!TextUtils.isEmpty(phone)){
                    //2,查询是耗时操作,开启子线程
                    query(phone);
                }else {
                    //抖动
                    Animation shake = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.shake);
                    mEtPhone.startAnimation(shake);
                    //手机震动效果(vibrator 震动)
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    //震动毫秒值
//                    vibrator.vibrate(2000);
                    //规律震动(震动规则(不震动时间,震动时间,不震动时间,震动时间.......),重复次数)
                    vibrator.vibrate(new long[]{2000,3000,2000,3000,2000,3000}, -1);
                }
                break;
        }
    }

    private void query(final String phone) {
        new Thread(){
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                //3,消息机制,告知主线程查询结束,可以去使用查询结果
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
