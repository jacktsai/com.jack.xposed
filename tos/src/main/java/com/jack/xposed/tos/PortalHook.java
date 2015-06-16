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
            if (methodName.equals("function5583")) {
                hookMethod(method, new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("442194630,2112096144,1470101502,1222842144,1007548744,1007548744,2112096144");
                    }
                });
            } else if (methodName.equals("function5585")) {
//                hookMethod(method, new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        String result = (String)param.getResult();
//                        if (result.equals("assets/bin/Data/Managed/Assembly-CSharp.dll")) {
//                            param.setResult("assets/bin/Data/Managed/Assembly-CSharp-origin.dll");
//                        }
//                    }
//                });
            } else if (methodName.equals("function5587") || methodName.equals("function5588")) {
                hookMethod(method, new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("0");
                    }
                });
            }
        }
    }
}
