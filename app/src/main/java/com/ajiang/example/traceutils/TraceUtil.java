package com.ajiang.example.traceutils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dovar.dtoast.inner.DovaToast;

import will.github.com.xuexuan.androidaop.R;


public class TraceUtil {
    private final String TAG = "TraceUtil";

    /**
     * 当Activity执行了onCreate时触发
     *
     * @param activity
     */
    public static void onActivityCreate(Activity activity) {
//        Toast.makeText(activity
//                , activity.getClass().getName() + "call onCreate"
//                , Toast.LENGTH_LONG).show();
    }


    /**
     * 当Activity执行了onDestroy时触发
     *
     * @param activity
     */
    public static void onActivityDestroy(Activity activity) {
//        Toast.makeText(activity
//                , activity.getClass().getName() + "call onDestroy"
//                , Toast.LENGTH_LONG).show();
    }
    private static boolean isReplacing = false;

    public static void showToast(Context context, CharSequence text, int duration) {
        // 实现自定义的 Toast 逻辑
        Log.e("ajiang","showToast"+context+text+duration);
//        Toast.makeText(context, "Custom Toast: " + text, duration).show();//防止死循环
        // 直接创建 Toast 对象，避免调用 Toast.makeText()
        new DovaToast(context)
                .setText(R.id.tv_content_default, "修改后的toast"+text)
                .setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30)
                .show();
    }
    public static void showToast(Toast toast) {
        try {
            // 获取 Toast 的视图
            View toastView = toast.getView();
            if (toastView != null) {
                // 查找 TextView（Toast 的文本视图）
                TextView textView = toastView.findViewById(android.R.id.message);
                if (textView != null) {
                    // 获取文本内容
                    CharSequence text = textView.getText();
                    Log.e("ajiang", "Toast text: " + text);
                    new DovaToast(toastView.getContext())
                            .setText(R.id.tv_content_default, "修改后的toast"+text)
                            .setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 30)
                            .show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
