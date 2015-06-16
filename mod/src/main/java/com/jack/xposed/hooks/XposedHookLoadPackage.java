package com.jack.xposed.hooks;

import com.jack.xposed.mod.GlobalHandler;
import com.jack.xposed.mod.TosHandler;
import com.jack.xposed.utils.J;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedHookLoadPackage implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam packageParam) throws Throwable {
        String packageName = packageParam.packageName;
        if (packageParam.appInfo != null)
            J.log("load package [%s] for [%s] from [%s]", packageName, packageParam.processName, packageParam.appInfo.sourceDir);
        else
            J.log("load package [%s] for [%s]", packageName, packageParam.processName);

        GlobalHandler.getInstance().onLoadPackage(packageParam);

        if (packageName.equals("com.madhead.tos.zh")) {
            TosHandler tosHandler = new TosHandler();
            tosHandler.onLoadPackage(packageParam);
        }
    }
}
