package com.jack.xposed.hooks;

import android.content.Context;

import com.jack.xposed.utils.J;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class GeneralMethodHook extends XC_MethodHook {
    protected static final String TAG = GeneralMethodHook.class.getSimpleName();

    public GeneralMethodHook() {
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Class<?> contextClass = XposedHelpers.findClass("android.app.ContextImpl", null);
        try {
            Context context = (Context)contextClass.newInstance();
            J.a(TAG, "package name is %s", context.getPackageName());
        } catch (Exception e) {
            J.e(TAG, e.toString());
        }

        Member member = param.method;
        String className = member.getDeclaringClass().getSimpleName();
        String methodName = member.getName();

        Class<?>[] paramTypes = null;
        Class<?> returnType = null;
        if (member instanceof Constructor<?>) {
            Constructor<?> ctor = (Constructor<?>)member;
            paramTypes = ctor.getParameterTypes();

        } else if (member instanceof Method) {
            Method method = (Method)member;
            paramTypes = method.getParameterTypes();
            returnType = method.getReturnType();
        }

        StringBuilder argsString = new StringBuilder();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0)
                argsString.append(", ");

            argsString.append("<" + paramTypes[i].getSimpleName() + ">");

            Object arg = param.args[i];
            if (arg == null)
                argsString.append("null");
            else
                argsString.append(arg.toString());
        }

        String returnString = "";
        if (returnType != null && returnType != void.class) {
            returnString = String.format("<%s>", returnType.getSimpleName());

            Object returnValue = param.getResult();
            if (returnValue == null)
                returnString = returnString + "null";
            else
                returnString = returnString + returnValue.toString();
        }

        J.d(TAG, "%s.%s(%s)%s", className, methodName, argsString.toString(), returnString);
    }
}
