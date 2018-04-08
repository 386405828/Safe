package com.simon.safe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.simon.safe.db.dao.BlackNumberDao;
import com.simon.safe.utils.LogUtils;

import java.lang.reflect.Method;

public class BlackNumberService extends Service {

    private static final String TAG = "BlackNumberService";

    public BlackNumberService() {
    }

    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;

    @Override
    public void onCreate() {
        mDao = BlackNumberDao.getInstance(getApplicationContext());
        LogUtils.i(TAG, "拦截电话onCreate");
        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);

        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, intentFilter);

        //监听电话的状态
        //1,电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2,监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();
    }

    class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容,获取发送短信电话号码,如果此电话号码在黑名单中,并且拦截模式也为1(短信)或者3(所有),拦截短信
            //1,获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //2,循环遍历短信过程
            for (Object object : objects) {
                //3,获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //4,获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();

                int mode = mDao.getMode(originatingAddress);

                if (mode == 1 || mode == 3) {
                    //拦截短信(android 4.4版本失效	短信数据库,删除)
                    abortBroadcast();
                }
            }
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {

        //3,手动重写,电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    LogUtils.i(TAG, "拦截电话CALL_STATE_RINGING");
                    //挂断电话 	aidl文件中去了
//				mTM.endCall();
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void endCall(String phone) {
        int mode = mDao.getMode(phone);

        if (mode == 2 || mode == 3) {
//			ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
            //ServiceManager此类android对开发者隐藏,所以不能去直接调用其方法,需要反射调用
            try {
                //1,获取ServiceManager字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2,获取方法
                Method method = clazz.getMethod("getService", String.class);
                //3,反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4,调用获取aidl文件对象方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //5,调用在aidl中隐藏的endCall方法
                iTelephony.endCall();
                LogUtils.i(TAG, "拦截电话");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        super.onDestroy();
    }
}
