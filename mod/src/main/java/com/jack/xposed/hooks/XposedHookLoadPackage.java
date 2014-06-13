package com.jack.xposed.hooks;

import com.jack.xposed.mod.GlobalHandler;
import com.jack.xposed.utils.J;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedHookLoadPackage implements IXposedHookLoadPackage {
    private static final String TAG = XposedHookLoadPackage.class.getSimpleName();

    @Override
    public void handleLoadPackage(LoadPackageParam packageParam) throws Throwable {
        String packageName = packageParam.packageName;
        J.i(TAG, "load package [%s]", packageName);

        GlobalHandler.getInstance().onLoadPackage(packageParam);

        if (packageName.equals("com.madhead.tos.zh")) {
//            TosHandler tosHandler = new TosHandler();
//            tosHandler.onLoadPackage(packageParam);
        }
    }
}
