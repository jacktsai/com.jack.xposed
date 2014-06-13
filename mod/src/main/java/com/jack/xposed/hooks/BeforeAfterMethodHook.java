package com.jack.xposed.hooks;

import com.jack.xposed.utils.J;

public class BeforeAfterMethodHook extends GeneralMethodHook {
    protected static final String TAG = "MethodHook";

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
            J.d(TAG, "[%s] before %s.%s(%s)", slot.packageName, slot.thisName, slot.methodName, slot.argString);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
        if (slot.returnString.length() == 0)
            J.d(TAG, "[%s] after %s.%s", slot.packageName, slot.thisName, slot.methodName);
        else
            J.d(TAG, "[%s] after %s.%s => %s", slot.packageName, slot.thisName, slot.methodName, slot.returnString);
    }
}
