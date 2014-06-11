package com.jack.xposed.hooks;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jack.xposed.utils.J;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XposedHookZygoteInit implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        J.log("init Zygote [%s]", startupParam.modulePath);

        findAndHookMethod(
            "com.android.internal.policy.impl.PhoneWindow",
            null,
            "generateLayout",
            "com.android.internal.policy.impl.PhoneWindow.DecorView",
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Window window = (Window)param.thisObject;
                Context context = window.getContext();
                View decor = window.getDecorView();
                TextView textView = new TextView(context);
                textView.setText(context.getPackageName());
                decor.setBackgroundDrawable(textView.getBackground());

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
                layoutParams.format = PixelFormat.RGBA_8888;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                windowManager.addView(textView, layoutParams);
                }
            }
        );
    }
}
