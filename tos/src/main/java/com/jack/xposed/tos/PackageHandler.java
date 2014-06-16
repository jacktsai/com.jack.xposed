package com.jack.xposed.tos;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class PackageHandler implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam packageParam) throws Throwable {
        if (packageParam.packageName.equals("com.madhead.tos.zh")) {
            new PackageManagerHook(packageParam);
            new PortalHook(packageParam);
        }
    }
}
