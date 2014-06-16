package com.jack.xposed.tos;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

class PortalHook {
    public PortalHook(LoadPackageParam packageParam) {
        Class clazz = findClass("com.madhead.tos.plugins.Portal", packageParam.classLoader);

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("function5585")) {
                hookMethod(method, new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String fileIndex = (String)param.args[6];
                        if (fileIndex.equals("0")) {
                            param.setResult("assets/bin/Data/Managed/Assembly-CSharp-origin.dll");
                        }
                    }
                });
            }
        }
    }
}
