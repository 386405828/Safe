package com.simon.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.simon.safe.R;
import com.simon.safe.utils.ConstantValue;
import com.simon.safe.utils.SpUtil;
import com.simon.safe.utils.ToastUtil;

public class Setup4Activity extends BaseSetupActivity  {

    /** 您没有开启防盗保护 */
    private CheckBox mCbBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initView();
        initData();
    }

    @Override
    protected void showNextPage() {
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            finish();
            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(), "请开启防盗保护");
        }
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initData() {
        //1,是否选中状态的回显
        boolean open_security = SpUtil.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        //2,根据状态,修改checkbox后续的文字显示
        mCbBox.setChecked(open_security);
        if (open_security) {
            mCbBox.setText("安全设置已开启");
        } else {
            mCbBox.setText("安全设置已关闭");
        }

//		cb_box.setChecked(!cb_box.isChecked());
        //3,点击过程中,监听选中状态发生改变过程,
        mCbBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //4,isChecked点击后的状态,存储点击后状态
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_SECURITY, isChecked);
                //5,根据开启关闭状态,去修改显示的文字
                if (isChecked) {
                    mCbBox.setText("安全设置已开启");
                } else {
                    mCbBox.setText("安全设置已关闭");
                }
            }
        });
    }

    private void initView() {
        mCbBox = (CheckBox) findViewById(R.id.cb_box);
    }


}
