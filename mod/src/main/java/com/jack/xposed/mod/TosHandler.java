package com.jack.xposed.mod;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;

import com.jack.xposed.hooks.GeneralMethodHook;
import com.jack.xposed.utils.J;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XCallback;

import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class TosHandler {
    public void onLoadPackage(LoadPackageParam packageParam) throws Throwable {
//        hook_UnityPlayer(packageParam);
//        hook_PlayerPrefs(packageParam);

//        hook_WWW(packageParam);
        hook_URL(packageParam);
//        hook_URLConnection(packageParam);
//        hook_OutputStream(packageParam);
//        hook_InputStream(packageParam);

//        new FMODAudioDevice_Tracer(packageParam);
//        new ActivityManager_Tracer(packageParam);
//        new ActivityManager_Decorator();
        new PackageManager_Tracer(packageParam);
        new Portal_Tracer(packageParam);
//        new PackageManager_Decorator();
    }

    private class PackageManager_Decorator extends XC_MethodHook {
        public PackageManager_Decorator() {
            super(XCallback.PRIORITY_LOWEST);

            Class clazz = findClass("android.app.ApplicationPackageManager", null);

            for (Method method : clazz.getDeclaredMethods()) {
            }
        }
    }

    private class PackageManager_Tracer extends GeneralMethodHook {
        public PackageManager_Tracer(LoadPackageParam packageParam) {
            super(packageParam, XCallback.PRIORITY_HIGHEST);

            Class clazz = findClass("android.app.ApplicationPackageManager", packageParam.classLoader);

            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
//                if (methodName.equals("getPackageInfo"))
                    hookMethod(method, this);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);

            if (param.method.getName().equals("getPackageInfo")) {
                PackageInfo result = (PackageInfo) param.getResult();
                if (result.signatures != null && result.signatures.length == 1) {
                    J.d(TAG, "signature: %s", result.signatures[0].toCharsString());
                }
            }
        }
    }

    private class Portal_Tracer extends GeneralMethodHook {
        public Portal_Tracer(LoadPackageParam packageParam) {
            super(packageParam, XCallback.PRIORITY_HIGHEST);

            Class clazz = findClass("com.madhead.tos.plugins.Portal", packageParam.classLoader);

            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
                if (methodName.equals("GetAvailableSpace"))
                    continue;

                hookMethod(method, this);
            }
        }
    }

    private class ActivityManager_Decorator extends XC_MethodHook {
        public ActivityManager_Decorator() {
            super(0);

            Class clazz = ActivityManager.class;

            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
                if (methodName.equals("getRunningAppProcesses"))
                    hookMethod(method, this);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            List<RunningAppProcessInfo> processInfoList = (List<RunningAppProcessInfo>) param.getResult();

            RunningAppProcessInfo sbToolInfo = null;
            for (RunningAppProcessInfo processInfo : processInfoList) {
                if (processInfo.processName == "org.sbtools.gamehack") {
                    sbToolInfo = processInfo;
                    break;
                }
            }

            if (sbToolInfo != null)
                processInfoList.remove(sbToolInfo);
        }
    }

    private static class FMODAudioDevice_Tracer extends GeneralMethodHook {
        public FMODAudioDevice_Tracer(LoadPackageParam packageParam) {
            super(packageParam);

            Class clazz = findClass("org.fmod.FMODAudioDevice", packageParam.classLoader);

            for (Constructor<?> method : clazz.getDeclaredConstructors()) {
                hookMethod(method, this);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    String methodName = method.getName();
                    if (methodName.equals("toString"))
                        continue;

                    hookMethod(method, this);
                }
            }
        }

        private boolean isInitialized_printed = true;

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            if (param.method.getName().equals("isInitialized")) {
                if (!isInitialized_printed) {
                    super.beforeHookedMethod(param);
                    J.printStackTrace(TAG);
                    isInitialized_printed = true;
                }
            } else
                super.beforeHookedMethod(param);

//            if (param.method instanceof Constructor)
//                J.printStackTrace(TAG);
        }
    }

    private void hook_UnityPlayer(LoadPackageParam packageParam) throws Throwable {
        Class<?> clazz = Class.forName("com.unity3d.player.UnityPlayer", false, packageParam.classLoader);
        XC_MethodHook hook = new GeneralMethodHook(packageParam);

        for (Method method : clazz.getDeclaredMethods()) {
            String name = method.getName();
            Class<?>[] p = method.getParameterTypes();

            if (name.equals("isFinishing") ||
                    name.equals("onDrawFrame") ||
                    name.equals("queueEvent") ||
                    name.equals("getFilesDir") ||
                    name.equals("dispatchTouchEvent") ||
                    name.equals("onTouchEvent"))
                continue;

            if (name.equals("a")) {
                if (p.length == 1 && p[0] == String.class) // a(String)
                    continue;

                if (p.length == 2) {
                    if (p[0] == String.class && p[1] == File.class) // a(String, File)
                        continue;
                    if (p[0] == Integer.class && p[1] == Integer.class) // a(int, int)
                        continue;
                }
            }

            XposedBridge.hookMethod(method, hook);
        }
    }

    private void hook_PlayerPrefs(LoadPackageParam packageParam) throws Throwable {
        Class<?> clazz = Class.forName("com.unity3d.player.PlayerPrefs", false, packageParam.classLoader);
        XC_MethodHook hook = new GeneralMethodHook(packageParam) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Method method = (Method) param.method;
                String methodName = method.getName();
                if (methodName.equals("GetFloat") || methodName.equals("SetFloat")) {
                    String arg0 = (String) param.args[0];
                    if (arg0.equals("UserConfig_storedChatBubbleVectorX") || arg0.equals("UserConfig_storedChatBubbleVectorY"))
                        return;
                }

                super.afterHookedMethod(param);
//                J.printStackTrace(TAG);
            }
        };

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("Sync"))
                continue;

            XposedBridge.hookMethod(method, hook);
        }
    }

    private void hook_WWW(LoadPackageParam packageParam) throws Throwable {
        Class<?> clazz = Class.forName("com.unity3d.player.WWW", false, packageParam.classLoader);
        XC_MethodHook hook = new GeneralMethodHook(packageParam) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                J.printStackTrace(TAG);
            }
        };

        XposedBridge.hookAllConstructors(clazz, hook);
    }

    private void hook_URL(LoadPackageParam packageParam) throws Throwable {
        Class<?> clazz = Class.forName("java.net.URL", false, packageParam.classLoader);
        XC_MethodHook hook = new GeneralMethodHook(packageParam) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                URL url = (URL) param.thisObject;
                String urlString = url.toExternalForm();
                J.d(TAG, "open URL: %s", urlString);
            }
        };
        XposedBridge.hookAllMethods(clazz, "openConnection", hook);
    }

    private void hook_URLConnection(LoadPackageParam packageParam) throws Throwable {
        XC_MethodHook hook = new GeneralMethodHook(packageParam) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                URLConnection connection = (URLConnection) param.thisObject;
                URL url = connection.getURL();
                String urlString = url.toExternalForm();
                Uri uri = Uri.parse(urlString);
                String methodName = param.method.getName();

                if (url.getAuthority().contains("towerofsaviors")) {
                    if (methodName.equals("getOutputStream")) {
                        OutputStream origin = (OutputStream) param.getResult();
                        if (origin instanceof MyOutputStream) {
                        } else {
                            OutputStream log = getOutputStream(uri);
                            BufferedWriter logWriter;
                            if (log != null) {
                                logWriter = new BufferedWriter(new OutputStreamWriter(log));
                                logWriter.write("[REQUEST CONTENT]\r\n");
                                logWriter.flush();
                            }

                            OutputStream substitute = new MyOutputStream(origin, log);
                            param.setResult(substitute);
                        }
                    } else if (methodName.equals("getInputStream")) {
                        InputStream origin = (InputStream) param.getResult();
                        if (origin instanceof MyInputStream) {
                        } else {
                            OutputStream log = getOutputStream(uri);
                            BufferedWriter logWriter;
                            if (log != null) {
                                logWriter = new BufferedWriter(new OutputStreamWriter(log));
                                logWriter.write("[RESPONSE CONTENT]\r\n");
                                logWriter.flush();
                            }

                            InputStream substitute = new MyInputStream(origin, log);
                            param.setResult(substitute);
                        }
                    }
                }

                super.afterHookedMethod(param);
            }
        };

        for (Method method : Class.forName("com.android.okhttp.internal.http.HttpURLConnectionImpl", false, packageParam.classLoader).getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("toString") || methodName.equals("getURL"))
                continue;

//            if (Modifier.isPublic(method.getModifiers()))
            if (methodName.equals("getInputStream"))
                XposedBridge.hookMethod(method, hook);
        }

        for (Method method : Class.forName("com.android.okhttp.internal.http.HttpsURLConnectionImpl", false, packageParam.classLoader).getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("toString") || methodName.equals("getURL"))
                continue;

//            if (Modifier.isPublic(method.getModifiers()))
            if (methodName.equals("getInputStream"))
                XposedBridge.hookMethod(method, hook);
        }
    }

    private void hook_OutputStream(LoadPackageParam packageParam) throws Throwable {
//        Class<?> clazz = Class.forName("com.android.okhttp.internal.http.RetryableOutputStream", false, packageParam.classLoader);
        Class<?> clazz = MyOutputStream.class;
        XC_MethodHook hook = new GeneralMethodHook(packageParam);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("toString"))
                continue;

            XposedBridge.hookMethod(method, hook);
        }
    }

    private void hook_InputStream(LoadPackageParam packageParam) throws Throwable {
//        Class<?> clazz = Class.forName("java.util.zip.GZIPInputStream", false, packageParam.classLoader);
        Class<?> clazz = MyInputStream.class;
        XC_MethodHook hook = new GeneralMethodHook(packageParam);

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("toString"))
                continue;

            XposedBridge.hookMethod(method, hook);
        }
    }

    private OutputStream getOutputStream(Uri uri) throws Throwable{
        String timestamp = uri.getQueryParameter("timestamp");
        if (timestamp == null || timestamp.length() < 1)
            return null;

        File dir = new File(String.format("/sdcard/xposed/%s", uri.getPath()));
        dir.mkdirs();
        File file = new File(dir, timestamp + ".txt");

        return new FileOutputStream(file, true);
    }

    private class MyOutputStream extends OutputStream {
        private final OutputStream log;
        private final OutputStream inner;

        public MyOutputStream(OutputStream source, OutputStream log) throws Throwable {
            this.log = log;
            this.inner = source;
        }

        @Override
        public void close() throws IOException {
            if (log != null)
                log.close();

            inner.close();
        }

        @Override
        public void flush() throws IOException {
            if (log != null)
                log.flush();

            inner.flush();
        }

        @Override
        public void write(int oneByte) throws IOException {
            if (log != null)
                log.write(oneByte);

            inner.write(oneByte);
        }
    }

    private class MyInputStream extends InputStream {
        private final OutputStream log;
        private final InputStream inner;

        public MyInputStream(InputStream source, OutputStream log) throws Throwable {
            this.log = log;
            this.inner = source;
        }

        @Override
        public int available() throws IOException {
            return inner.available();
        }

        @Override
        public void close() throws IOException {
            if (log != null)
                log.close();

            inner.close();
        }

        @Override
        public void mark(int readlimit) {
            inner.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return inner.markSupported();
        }

        @Override
        public int read() throws IOException {
            int data = inner.read();

            if (log != null)
                log.write(data);

            return data;
        }

        @Override
        public synchronized void reset() throws IOException {
            inner.reset();
        }

        @Override
        public long skip(long byteCount) throws IOException {
            return inner.skip(byteCount);
        }
    }
}
