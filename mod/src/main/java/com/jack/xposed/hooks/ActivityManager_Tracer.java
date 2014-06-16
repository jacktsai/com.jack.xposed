package com.jack.xposed.hooks;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;

import com.jack.xposed.utils.J;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookMethod;

public class ActivityManager_Tracer extends GeneralMethodHook {
    public ActivityManager_Tracer(XC_LoadPackage.LoadPackageParam packageParam) {
        super(packageParam);

        Class clazz = ActivityManager.class;

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            hookMethod(method, this);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("toString"))
                continue;

            hookMethod(method, this);
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);

        if (param.method.getName().equals("getRunningAppProcesses")) {
            List<RunningAppProcessInfo> processInfoList = (List<RunningAppProcessInfo>) param.getResult();
            for (RunningAppProcessInfo processInfo : processInfoList) {
                J.d(TAG, "process name [%s]", processInfo.processName);
            }
        }
    }
}