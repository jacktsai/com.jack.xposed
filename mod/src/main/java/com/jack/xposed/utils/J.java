package com.jack.xposed.utils;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

public class J {
    public static void v(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.v(tag, message);
    }

    public static void d(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.d(tag, message);
    }

    public static void i(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.i(tag, message);
    }

    public static void w(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.w(tag, message);
    }

    public static void e(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.e(tag, message);
    }

    public static void a(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.wtf(tag, message);
    }

    public static void printStackTrace(String tag) {
        Thread t = Thread.currentThread();
        StackTraceElement[] elements = t.getStackTrace();

        boolean firstPrint = true;
        for (StackTraceElement e : elements) {
            if (e.getMethodName().contains("StackTrace")) {
                continue;
            }

            if (firstPrint) {
                d(tag, "[<%d> %s] %s.%s [%s #%d]", t.getId(), t.getName(), e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
                firstPrint = false;
            } else {
                d(tag, "-> %s.%s [%s #%d]", e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
            }
        }
    }

    public static void log(String format, Object... args) {
        String message = String.format(format, args);
        XposedBridge.log(message);
    }
}

