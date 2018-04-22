package com.simon.safe.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;

import com.simon.safe.R;
import com.simon.safe.permission.DefaultRationale;
import com.simon.safe.permission.PermissionSetting;
import com.simon.safe.service.AddressService;
import com.simon.safe.service.BlackNumberService;
import com.simon.safe.service.RocketService;
import com.simon.safe.service.WatchDogService;
import com.simon.safe.utils.ConstantValue;
import com.simon.safe.utils.ServiceUtil;
import com.simon.safe.utils.SpUtil;
import com.simon.safe.utils.ToastUtil;
import com.simon.safe.view.SettingClickView;
import com.simon.safe.view.SettingItemView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import java.util.List;


public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";
    private static final int RC_PROCESS_OUTGOING_CALLS = 99;
    public static int OVERLAY_PERMISSION_REQUEST_CODE = 100;
    private static final String PACKAGE_URL_SCHEME = "package:";
    private SettingItemView siv_address;
    private SettingClickView mScvToastStyle;
    private SettingClickView mScvLocation;
    private SettingItemView siv_blacknumber;
    private String[] mToastStyleDes;
    private int mToastStyle;
    private SettingItemView mSivRocket;
    private Rationale mRationale;
    private PermissionSetting mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(this);
        setTitle("设置中心");
        initUpdate();
        initRocket();
        initAddress();
        initToastStyle();
        initLocation();
        initBlackNumber();
        initAppLock();
    }

    /**
     * 初始化程序锁方法
     */
    private void initAppLock() {
        final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
        boolean isRunning = ServiceUtil.isRunning(this, "com.simon.safe.service.WatchDogService");
        siv_app_lock.setCheck(isRunning);

        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_app_lock.isCheck();
                siv_app_lock.setCheck(!isCheck);
                if(!isCheck){
                    //开启服务
                    startService(new Intent(getApplicationContext(), WatchDogService.class));
                }else{
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), WatchDogService.class));
                }
            }
        });
    }
    /**
     * 拦截黑名单短信电话
     */
    private void initBlackNumber() {
        siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
        boolean isRunning = ServiceUtil.isRunning(this, "com.simon.safe.service.BlackNumberService");
        siv_blacknumber.setCheck(isRunning);

        siv_blacknumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    isGranted = false;
                    boolean isGranted = requestPermission(Permission.READ_CALL_LOG,
                            Permission.WRITE_CALL_LOG,
                            Permission.PROCESS_OUTGOING_CALLS);
                    if (isGranted) {
                        setBlackNumberData();
                    }
//                } else {
//                    setBlackNumberData();
//                }
            }
        });
    }

    private void setBlackNumberData() {
        boolean isCheck = siv_blacknumber.isCheck();
        siv_blacknumber.setCheck(!isCheck);
        if (!isCheck) {
            //开启服务
            startService(new Intent(getApplicationContext(), BlackNumberService.class));
        } else {
            //关闭服务
            stopService(new Intent(getApplicationContext(), BlackNumberService.class));
        }
    }

    private void initRocket() {
        mSivRocket = (SettingItemView) findViewById(R.id.siv_rocket);
        //对服务是否开的状态做显示
        boolean isRunning = ServiceUtil.isRunning(this, "com.simon.safe.service.RocketService");
        mSivRocket.setCheck(isRunning);
        //点击过程中,状态(是否开启电话号码归属地)的切换过程
        mSivRocket.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //返回点击前的选中状态
                boolean isCheck = mSivRocket.isCheck();
                mSivRocket.setCheck(!isCheck);
                if (!isCheck) {
                    //开启服务,管理吐司
                    startService(new Intent(getApplicationContext(), RocketService.class));
                } else {
                    //关闭服务,不需要显示吐司
                    stopService(new Intent(getApplicationContext(), RocketService.class));
                }

            }
        });
    }

    /**
     * 双击居中view所在屏幕位置的处理方法
     */
    private void initLocation() {
        SettingClickView scv_location = (SettingClickView) findViewById(R.id.scv_location);
        scv_location.setTitle("归属地提示框的位置");
        scv_location.setDes("设置归属地提示框的位置");
        scv_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        mScvToastStyle = (SettingClickView) findViewById(R.id.scv_toast_style);
        mScvLocation = (SettingClickView) findViewById(R.id.scv_location);
        //话述(产品)
        mScvToastStyle.setTitle("设置归属地显示风格");
        //1,创建描述文字所在的string类型数组
        mToastStyleDes = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        //2,SP获取吐司显示样式的索引值(int),用于获取描述文字

        mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);

        //3,通过索引,获取字符串数组中的文字,显示给描述内容控件
        mScvToastStyle.setDes(mToastStyleDes[mToastStyle]);
        //4,监听点击事件,弹出对话框
        mScvToastStyle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //5,显示吐司样式的对话框
                showToastStyleDialog();
            }
        });
    }

    /**
     * 创建选中显示样式的对话框
     */
    protected void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("请选择归属地样式");
        //选择单个条目事件监听
        mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {//which选中的索引值
                //(1,记录选中的索引值,2,关闭对话框,3,显示选中色值文字)
                SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                dialog.dismiss();
                mScvToastStyle.setDes(mToastStyleDes[which]);
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 是否显示电话号码归属地的方法
     */
    private void setAddressCheckState() {
        //返回点击前的选中状态
        boolean isCheck = siv_address.isCheck();
        siv_address.setCheck(!isCheck);
        if (!isCheck) {
            //开启服务,管理吐司
            startService(new Intent(getApplicationContext(), AddressService.class));
        } else {
            //关闭服务,不需要显示吐司
            stopService(new Intent(getApplicationContext(), AddressService.class));
        }
    }


    private void initAddress() {
        siv_address = (SettingItemView) findViewById(R.id.siv_address);
        //对服务是否开的状态做显示
        boolean isRunning = ServiceUtil.isRunning(this, "com.simon.safe.service.AddressService");
        siv_address.setCheck(isRunning);
        //点击过程中,状态(是否开启电话号码归属地)的切换过程
        siv_address.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 检查是否有悬浮窗的权限
                    if (!Settings.canDrawOverlays(SettingActivity.this)) {
                        //没有权限，给出提示，并跳转界面然用户enable
                        startOverlaySetting();
                    } else {
                        //有权限，正常逻辑走起
                        setAddressCheckState();
                    }
                } else {
                    setAddressCheckState();
                }

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // 还是没权限，说明用户不想给你这个权限，可以弹框说明
                showPermissionDialog();
            } else {
                siv_address.setCheck(true);
                startService(new Intent(getApplicationContext(), AddressService.class));
            }
        }
    }


    /**
     * 提示对话框
     */
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-打开所需权限。");
        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startOverlaySetting();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    // 启动应用的设置
    private void startOverlaySetting() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    /**
     * 版本更新开关
     */
    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);

        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(open_update);

        siv_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }

    boolean isGranted = false;

    private boolean requestPermission(String... permissions) {

        AndPermission.with(this)
                .permission(permissions)
                .rationale(mRationale)
                .onGranted(new Action() {

                    @Override
                    public void onAction(List<String> permissions) {
                        ToastUtil.show(getApplicationContext(), "授权成功");
                        isGranted = true;
                    }
                })
                .onDenied(new Action() {

                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        isGranted = false;
                        ToastUtil.show(getApplicationContext(), "授权失败");
                        if (AndPermission.hasAlwaysDeniedPermission(SettingActivity.this, permissions)) {
                            mSetting.showSetting(permissions);
                        }
                    }
                })
                .start();
        return isGranted;
    }
}
