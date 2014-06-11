package com.jack.xposed.hooks;

import com.jack.xposed.utils.J;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;

public class GeneralMethodHook extends XC_MethodHook {
    protected static final String TAG = GeneralMethodHook.class.getSimpleName();

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Method method = (Method) param.method;
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();

        Class<?>[] paramTypes = method.getParameterTypes();
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

        Class<?> returnType = method.getReturnType();
        String returnString = "";
        if (returnType != void.class) {
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
