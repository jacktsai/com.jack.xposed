package com.jack.xposed.hooks;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jack.xposed.utils.J;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import android.content.pm.Signature;

import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XCallback;

import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class PackageMaanger_Tracer extends GeneralMethodHook {
    public PackageMaanger_Tracer(XC_LoadPackage.LoadPackageParam packageParam) {
        super(packageParam, XCallback.PRIORITY_HIGHEST);

        Class clazz = findClass("android.app.ApplicationPackageManager", null);

//        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
//            hookMethod(method, this);
//        }

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("toString"))
                continue;

//            if (methodName.equals("getPackageInfo") || methodName.equals("getApplicationInfo"))
                hookMethod(method, this);
        }
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);

        String methodName = param.method.getName();
        if (methodName.equals("getPackageInfo")) {
            String packageName = (String)param.args[0];
            PackageInfo packageInfo = (PackageInfo) param.getResult();
            int flags = (Integer)param.args[1];
            if (flags == PackageManager.GET_SIGNATURES) {
                J.d(TAG, "signature(s) of %s:", packageName);
                for (Signature signature : packageInfo.signatures) {
                    J.d(TAG, signature.toCharsString());
                }
            }
        }
    }
}