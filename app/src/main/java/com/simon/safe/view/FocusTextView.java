package com.simon.safe.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by 38640 on 2018/3/27.
 */

public class FocusTextView extends android.support.v7.widget.AppCompatTextView {

    //使用在通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }

    //由系统调用(带属性+上下文环境构造方法)
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //由系统调用(带属性+上下文环境构造方法+布局文件中定义样式文件构造方法)
    public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //重写获取焦点的方法,由系统调用,调用的时候默认就能获取焦点

    @Override
    public boolean isFocused() {
        return true;
    }
}
