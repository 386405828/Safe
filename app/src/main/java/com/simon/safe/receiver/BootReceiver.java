package com.simon.safe.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.simon.safe.utils.ConstantValue;
import com.simon.safe.utils.LogUtils;
import com.simon.safe.utils.SpUtil;

public class BootReceiver extends BroadcastReceiver {

    private static final String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(tag, "重启手机成功了,并且监听到了相应的广播......");
        //1,获取开机后手机的sim卡的序列号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String simSerialNumber = tm.getSimSerialNumber() + "xxx";
        //2,sp中存储的序列卡号
        String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
        //3,比对不一致
        if(!simSerialNumber.equals(sim_number)){
            //4,发送短信给选中联系人号码
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("5556", null, "sim change!!!", null, null);
        }
    }
}
