package com.simon.safe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.simon.safe.R;
import com.simon.safe.engine.SmsBackUp;

import java.io.File;

public class AToolActivity extends BaseActivity implements View.OnClickListener {

    /** 归属地查询 */
    private TextView mTvQueryPhoneAddress;
    /** 短信备份 */
    private TextView mTvSmsBackup;
    /** 常用号码查询 */
    private TextView mTvCommonnumberQuery;
    /** 程序锁 */
    private TextView mTvAppLock;
//    private ProgressBar mPbBar;


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
        mTvSmsBackup = (TextView) findViewById(R.id.tv_sms_backup);
        mTvSmsBackup.setOnClickListener(this);
//        mPbBar = (ProgressBar) findViewById(R.id.pb_bar);
        mTvCommonnumberQuery = (TextView) findViewById(R.id.tv_commonnumber_query);
        mTvCommonnumberQuery.setOnClickListener(this);
        mTvAppLock = (TextView) findViewById(R.id.tv_app_lock);
        mTvAppLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_query_phone_address:
                startActivity(new Intent(getApplicationContext(), QueryAddressActivity.class));
                break;
            case R.id.tv_sms_backup:
                showSmsBackUpDialog();
                break;
            case R.id.tv_commonnumber_query:
                startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
                break;
            case R.id.tv_app_lock:
                startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
                break;
        }
    }

    protected void showSmsBackUpDialog() {
        //1,创建一个带进度条的对话框
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle("短信备份");
        //2,指定进度条的样式为水平
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //3,展示进度条
        progressDialog.show();
        //4,直接调用备份短信方法即可
        new Thread() {

            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms74.xml";
                SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.CallBack() {

                    @Override
                    public void setProgress(int index) {
                        //由开发者自己决定,使用对话框还是进度条
                        progressDialog.setProgress(index);
//                        mPbBar.setProgress(index);
                    }

                    @Override
                    public void setMax(int max) {
                        //由开发者自己决定,使用对话框还是进度条
                        progressDialog.setMax(max);
//                        mPbBar.setMax(max);
                    }
                });

                progressDialog.dismiss();
            }
        }.start();
    }
}
