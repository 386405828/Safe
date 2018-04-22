package com.simon.safe.global;

import android.app.Application;
import android.os.Environment;

import com.simon.safe.utils.LogUtils;

import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Created by 38640 on 2018/3/20.
 */

public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        //捕获全局(应用任意模块)异常

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                //在获取到了未捕获的异常后,处理的方法
                ex.printStackTrace();
                LogUtils.i(TAG, "捕获到了一个程序的异常");

                //将捕获的异常存储到sd卡中
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "errorSafe.log";
                File file = new File(path);
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    ex.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //上传公司的服务器
                //结束应用
                System.exit(0);
            }
        });
        x.Ext.init(this);
        //初始化数据库
        initDB();
    }

    private void initDB() {
        //1,归属地数据拷贝过程
        initAddressDB("address.db");
        //2,常用号码数据库拷贝过程
        initAddressDB("commonnum.db");
        //3,拷贝病毒数据库
        initAddressDB("antivirus.db");
    }

    /**
     * 拷贝数据库值files文件夹下
     *
     * @param dbName 数据库名称
     */
    private void initAddressDB(String dbName) {
        //1,在files文件夹下创建同名dbName数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        //2,输入流读取第三方资产目录下的文件
        try {
            stream = getAssets().open(dbName);
            //3,将读取的内容写入到指定文件夹的文件中去
            fos = new FileOutputStream(file);
            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs)) != -1) {
                fos.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null && fos != null) {
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
