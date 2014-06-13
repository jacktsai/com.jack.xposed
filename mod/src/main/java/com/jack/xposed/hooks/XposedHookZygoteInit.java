package com.jack.xposed.hooks;

import com.jack.xposed.mod.GlobalHandler;
import com.jack.xposed.utils.J;

import de.robv.android.xposed.IXposedHookZygoteInit;

public class XposedHookZygoteInit implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        J.log("init Zygote [%s]", startupParam.modulePath);

        GlobalHandler.getInstance().onZygoteInit(startupParam);
    }
}
