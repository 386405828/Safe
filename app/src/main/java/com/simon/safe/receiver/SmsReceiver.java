package com.simon.safe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.simon.safe.R;
import com.simon.safe.service.LocationService;
import com.simon.safe.utils.ConstantValue;
import com.simon.safe.utils.LogUtils;
import com.simon.safe.utils.SpUtil;
import com.simon.safe.utils.ToastUtil;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(TAG, "onReceive");
        //1,判断是否开启了防盗保护
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            LogUtils.i(TAG, "open_security");
            //2,获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3,循环遍历短信过程
            for (Object object : objects) {
                //4,获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //5,获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                LogUtils.i(TAG, "object");
                //6,判断是否包含播放音乐的关键字
                if (messageBody.contains("#*alarm*#")) {
                    LogUtils.i(TAG, "alarm");
                    //7,播放音乐(准备音乐,MediaPlayer)
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

                if (messageBody.contains("#*location*#")) {
                    //8,开启获取位置服务
                    context.startService(new Intent(context, LocationService.class));
                }
                mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
                mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (messageBody.contains("#*lockscrenn*#")) {
                    //是否开启的判断
                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        //激活--->锁屏
                        mDPM.lockNow();
                        //锁屏同时去设置密码
//                        mDPM.resetPassword("123456", 0);
                    } else {
                        ToastUtil.show(context,"请先激活");
                    }
                }
                if (messageBody.contains("#*wipedate*#")) {
                    //组件对象可以作为是否激活的判断标志

                    if (mDPM.isAdminActive(mDeviceAdminSample)) {
                        mDPM.wipeData(0);//手机数据
//					mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//手机sd卡数据
                    } else {
                        ToastUtil.show(context,"请先激活");
                    }
                }
            }
        }
    }
}
