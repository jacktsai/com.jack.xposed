package com.jack.xposed.utils;

import android.app.ActivityThread;
import android.app.Application;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;

import de.robv.android.xposed.XposedBridge;

public class J {
    public static void v(String tag, String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

        Log.v(tag, message);
    }

    public static void d(String tag, String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

        Log.d(tag, message);
    }

    public static void i(String tag, String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

        Log.i(tag, message);
    }

    public static void w(String tag, String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

        Log.w(tag, message);
    }

    public static void e(String tag, String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

        Log.e(tag, message);
    }

    public static void a(String tag, String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

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
                String packageName = "android";
                Application context = ActivityThread.currentApplication();
                if (context != null) {
                    packageName = context.getPackageName();
                }

                d(tag, "[%s][<%d> %s] %s.%s [%s #%d]", packageName, t.getId(), t.getName(), e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
                firstPrint = false;
            } else {
                d(tag, "-> %s.%s [%s #%d]", e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
            }
        }
    }

    public static void printStackTrace(BufferedWriter writer) throws IOException {
        Thread t = Thread.currentThread();
        StackTraceElement[] elements = t.getStackTrace();

        boolean firstPrint = true;
        for (StackTraceElement e : elements) {
            if (e.getMethodName().contains("StackTrace")) {
                continue;
            }

            String line;
            if (firstPrint) {
                String packageName = "android";
                Application context = ActivityThread.currentApplication();
                if (context != null) {
                    packageName = context.getPackageName();
                }

                line = String.format("[%s][<%d> %s] %s.%s [%s #%d]", packageName, t.getId(), t.getName(), e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
                firstPrint = false;
            } else {
                line = String.format("-> %s.%s [%s #%d]", e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
            }

            writer.write(line);
            writer.write("\n");
        }
    }

    public static void log(String format, Object... args) {
        String message = format;
        if (args.length > 0)
            message = String.format(format, args);

        XposedBridge.log(message);
    }
}

