package com.simon.safe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast;

    /**
     * @param context 上下文环境
     * @param content 打印文本内容
     */
    public static void show(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
