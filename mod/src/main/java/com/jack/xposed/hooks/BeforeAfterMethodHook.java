package com.jack.xposed.hooks;

import com.jack.xposed.utils.J;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class BeforeAfterMethodHook extends GeneralMethodHook {
    public BeforeAfterMethodHook(LoadPackageParam packageParam) {
        super(packageParam);
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
        J.d(TAG, "[%s] before %s.%s(%s)", packageParam.packageName, slot.thisName, slot.methodName, slot.argString);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
        if (slot.returnString.length() == 0)
            J.d(TAG, "[%s] after %s.%s", packageParam.packageName, slot.thisName, slot.methodName);
        else
            J.d(TAG, "[%s] after %s.%s => %s", packageParam.packageName, slot.thisName, slot.methodName, slot.returnString);
    }
}
