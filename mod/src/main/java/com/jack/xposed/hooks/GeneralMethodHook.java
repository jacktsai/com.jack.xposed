package com.jack.xposed.hooks;

import android.app.ActivityThread;
import android.content.Context;

import com.jack.xposed.utils.J;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;

public class GeneralMethodHook extends XC_MethodHook {
    protected static final String TAG = "MethodHook";

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
        if (slot.canPrint())
            J.d(TAG, "[%s] %s.%s(%s)%s", slot.packageName, slot.thisName, slot.methodName, slot.argString, slot.returnString);
    }

    protected static class DataSlot {
        Context context;
        String packageName;
        String thisName;
        String methodName;
        String argString;
        String returnString;

        public DataSlot(MethodHookParam param) {
            context = ActivityThread.currentApplication();
            packageName = "android";
            if (context != null) {
                packageName = context.getPackageName();
            }

            if (!packageName.equals("com.jack.xposed"))
                return;

            Member member = param.method;
            thisName = member.getDeclaringClass().getSimpleName();
            methodName = null;
            Class<?>[] paramTypes = null;
            Class<?> returnType = null;
            Object returnValue = null;
            if (member instanceof Constructor<?>) {
                Constructor<?> constructor = (Constructor<?>)member;
                methodName = "<init>";
                paramTypes = constructor.getParameterTypes();
                returnType = constructor.getDeclaringClass();
                returnValue = param.thisObject;
            } else if (member instanceof Method) {
                if (param.thisObject != null)
                    thisName = thisName + "@" + Integer.toHexString(param.thisObject.hashCode());

                Method method = (Method)member;
                methodName = method.getName();
                paramTypes = method.getParameterTypes();
                returnType = method.getReturnType();
                returnValue = param.getResult();
            }

            StringBuilder argStringBuilder = new StringBuilder();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0)
                    argStringBuilder.append(", ");

                argStringBuilder.append("<" + paramTypes[i].getSimpleName() + ">");

                Object arg = param.args[i];
                if (arg == null)
                    argStringBuilder.append("null");
                else
                    argStringBuilder.append(arg.toString());
            }
            argString = argStringBuilder.toString();

            returnString = "";
            if (returnType != void.class) {
                returnString = String.format("<%s>", returnType.getSimpleName());

                if (returnValue == null)
                    returnString = returnString + "null";
                else
                    returnString = returnString + returnValue.toString();
            }
        }

        public boolean canPrint() {
            return methodName != null;
        }
    }
}
