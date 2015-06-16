package com.jack.xposed.tos;

import android.content.ContextWrapper;
import android.util.Log;

import java.lang.reflect.Method;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

class ContextWrapperHook {
    private static ContextWrapper firstInstance = null;

    public ContextWrapperHook(XC_LoadPackage.LoadPackageParam packageParam) {
        Class clazz = findClass("android.content.ContextWrapper", packageParam.classLoader);

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getPackageCodePath")) {
                hookMethod(method, new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (firstInstance == null)
                            firstInstance = (ContextWrapper)param.thisObject;

                        // 第一個 instance 是載入用的，不可以改變；之後的可以改指向原版 apk 讓程式無法察覺異動。
                        if (param.thisObject != firstInstance) {
                            String result = (String) param.getResult();
                            if (result.contains("com.madhead.tos.zh")) {
                                param.setResult("/sdcard/tos/tos-origin.apk");
                            }
                        }
                    }
                });
            }
        }
    }
}
