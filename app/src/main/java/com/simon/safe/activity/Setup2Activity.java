package com.simon.safe.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.simon.safe.R;
import com.simon.safe.utils.ConstantValue;
import com.simon.safe.utils.SpUtil;
import com.simon.safe.utils.ToastUtil;
import com.simon.safe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity implements View.OnClickListener {

    private static final int REQUEST_PHONE_STATE = 0xff;
    private SettingItemView mSivSimBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initView();
        initData();
    }

    @Override
    protected void showNextPage() {
        String serialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(serialNumber)) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请绑定sim卡");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initData() {
        //1,回显(读取已有的绑定状态,用作显示,sp中是否存储了sim卡的序列号)
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        //2,判断是否序列卡号为""
        if (TextUtils.isEmpty(sim_number)) {
            mSivSimBound.setCheck(false);
        } else {
            mSivSimBound.setCheck(true);
        }
    }

    private void initView() {
        mSivSimBound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        mSivSimBound.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.siv_sim_bound:
                setSimBound();
                break;
        }
    }

    private void setSimBound() {
        //3,获取原有的状态
        boolean isCheck = mSivSimBound.isCheck();
        //4,将原有状态取反
        //5,状态设置给当前条目
        mSivSimBound.setCheck(!isCheck);
        if (!isCheck) {
            //6,存储(序列卡号)
            //6.1获取sim卡序列号TelephoneManager
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //6.2获取sim卡的序列卡号
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Setup2Activity.this, new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PHONE_STATE);
            } else {
                String simSerialNumber = manager.getSimSerialNumber();
                SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
            }

        } else {
            //7,将存储序列卡号的节点,从sp中删除掉
            SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
        }
    }

    /**
     * 加个获取权限的监听
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            String simSerialNumber = manager.getSimSerialNumber();
            SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
        }
    }
}
