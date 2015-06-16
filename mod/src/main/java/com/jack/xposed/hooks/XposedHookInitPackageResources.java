package com.jack.xposed.hooks;

import com.jack.xposed.utils.J;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

public class XposedHookInitPackageResources implements IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) throws Throwable {
        J.log("init package resources [%s]", initPackageResourcesParam.packageName);
    }
}
