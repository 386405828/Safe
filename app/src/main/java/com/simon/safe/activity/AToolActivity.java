package com.simon.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.simon.safe.R;

public class AToolActivity extends BaseActivity implements View.OnClickListener {

    /** 归属地查询 */
    private TextView mTvQueryPhoneAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        initView();
        setTitle("高级工具");
    }

    private void initView() {
        mTvQueryPhoneAddress = (TextView) findViewById(R.id.tv_query_phone_address);
        mTvQueryPhoneAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_query_phone_address:
                startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
                break;
        }
    }
}
