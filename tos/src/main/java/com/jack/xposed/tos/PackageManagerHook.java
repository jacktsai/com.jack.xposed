package com.jack.xposed.tos;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

class PackageManagerHook {
    private static final Signature VERSION_6_05 = new Signature("3082026f308201d8a00302010202044ff45bcf300d06092a864886f70d0101050500307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e673020170d3132303730343135303535315a180f32303632303632323135303535315a307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e6730819f300d06092a864886f70d010101050003818d003081890281810089adafb3a3072e35d55608931ed94d9ef628e913224d275af4ad35d69ec8a57637848db8235961be1e1fe2d7f66655dbe962433b79180f492fa65ec3364d1039b6c3a313449701b8600cac255a02aec6935515063de2efac5acdd6e169b86a9a3a05d6c0e50d796ffed62829c763f011b35d37c1db0b861e1d9a1c125eb7debd0203010001300d06092a864886f70d010105050003818100500ea259a144f04fcb6aa1ae3d63ef0dade0c6abbc98359f0a91753f12787514edd1f3f28ae78513def110ebf4a68523875525091cad0ab58d087569c6071acf3cddb1a1d2c461c318c1aaba52bb0c0d550243f9190ab0277cf73dcaa01408aedd7a9d8c0eba1328ed03fa592731c2db2036c47ea38d13d2b9b9b5da0959eb38");

    public PackageManagerHook(XC_LoadPackage.LoadPackageParam packageParam) {
        Class clazz = findClass("android.app.ApplicationPackageManager", packageParam.classLoader);

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getPackageInfo")) {
                hookMethod(method, new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PackageInfo packageInfo = (PackageInfo) param.getResult();
                        if (packageInfo.signatures != null && packageInfo.signatures.length == 1) {
                            packageInfo.signatures[0] = VERSION_6_05;
                        }
                    }
                });
            }
        }
    }
}
