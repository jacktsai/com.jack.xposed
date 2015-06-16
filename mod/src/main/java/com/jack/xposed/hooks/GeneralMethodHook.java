package com.jack.xposed.hooks;

import com.jack.xposed.utils.J;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XCallback;

public class GeneralMethodHook extends XC_MethodHook {
    protected static final String TAG = "MethodHook";

    protected final LoadPackageParam packageParam;
    private HashMap<Thread, Integer> indentMap = new HashMap<Thread, Integer>();

    public GeneralMethodHook(LoadPackageParam packageParam, int priority) {
        super(priority);
        this.packageParam = packageParam;
    }

    public GeneralMethodHook(LoadPackageParam packageParam) {
        this(packageParam, XCallback.PRIORITY_DEFAULT);
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
        Thread thread = Thread.currentThread();
        Integer indent = indentMap.get(thread);
        if (indent == null)
            indent = 0;

        StringBuilder pad = new StringBuilder(indent);
        for (int i = 0; i < indent; i++)
            pad.append("-");

        J.d(TAG, "[%s] >> %s%s.%s(%s)", packageParam.packageName, pad, slot.thisName, slot.methodName, slot.argString);

        indent++;
        indentMap.put(thread, indent);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        DataSlot slot = new DataSlot(param);
        Thread thread = Thread.currentThread();
        Integer indent = indentMap.get(thread);
        indent--;

        StringBuilder pad = new StringBuilder(indent);
        for (int i = 0; i < indent; i++)
            pad.append("-");

        if (slot.returnString.length() == 0)
            J.d(TAG, "[%s] << %s%s.%s", packageParam.packageName, pad, slot.thisName, slot.methodName);
        else
            J.d(TAG, "[%s] << %s%s.%s :: %s", packageParam.packageName, pad, slot.thisName, slot.methodName, slot.returnString);

        indentMap.put(thread, indent);
    }

    protected static class DataSlot {
        String thisName;
        String methodName;
        String argString;
        String returnString;

        public DataSlot(MethodHookParam param) {
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
    }
}
