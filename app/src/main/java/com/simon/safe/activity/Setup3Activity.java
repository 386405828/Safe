package com.simon.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.simon.safe.R;
import com.simon.safe.utils.ConstantValue;
import com.simon.safe.utils.SpUtil;
import com.simon.safe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity implements View.OnClickListener {

    /** 请输入电话号码 */
    private EditText mEtPhoneNumber;
    /** 选择联系人 */
    private Button mBtSelectNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initView();
        initData();
    }

    @Override
    protected void showNextPage() {
        //点击按钮以后,需要获取输入框中的联系人,再做下一页操作
        String phone = mEtPhoneNumber.getText().toString();

        //在sp存储了相关联系人以后才可以跳转到下一个界面
//		String contact_phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if(!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            //如果现在是输入电话号码,则需要去保存
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        }else{
            ToastUtil.show(this,"请输入电话号码");
        }

    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initData() {
        //获取联系人电话号码回显过程
        String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        mEtPhoneNumber.setText(phone);
    }

    private void initView() {
        mEtPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mBtSelectNumber = (Button) findViewById(R.id.bt_select_number);
        mBtSelectNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.bt_select_number:
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.pre_btn:

                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            //1,返回到当前界面的时候,接受结果的方法
            String phone = data.getStringExtra("phone");
            //2,将特殊字符过滤(中划线转换成空字符串)
            if (TextUtils.isEmpty(phone)){
                return;
            }
            phone = phone.replace("-", "").replace(" ", "").trim();
            mEtPhoneNumber.setText(phone);

            //3,存储联系人至sp中
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
