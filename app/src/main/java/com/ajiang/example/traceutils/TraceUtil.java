package com.ajiang.example.traceutils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import com.dovar.dtoast.inner.DovaToast;

import will.github.com.xuexuan.androidaop.R;

/**
 * Created by will on 2018/3/9.
 */

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
}
