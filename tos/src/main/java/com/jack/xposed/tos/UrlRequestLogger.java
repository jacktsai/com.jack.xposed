package com.jack.xposed.tos;

import android.net.Uri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedBridge.hookMethod;

public class UrlRequestLogger {

    public UrlRequestLogger(LoadPackageParam packageParam) throws Throwable {
        hook_URL();
        hook_URLConnection(packageParam);
    }

    private void hook_URL() throws Throwable {
        Class<?> clazz = URL.class;

        hookAllMethods(clazz, "openConnection", new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                URL url = (URL) param.thisObject;

                if (url.getAuthority().contains("towerofsaviors")) {
                    OutputStream log = getOutputStream(url);
                    if (log != null) {
                        appendToLog(log, url.toExternalForm() + "\r\n");
                        log.close();
                    }
                }
            }
        });
    }

    private void hook_URLConnection(LoadPackageParam packageParam) throws Throwable {
        XC_MethodHook hook = new XC_MethodHook(XC_MethodHook.PRIORITY_LOWEST) {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                URLConnection connection = (URLConnection) param.thisObject;
                URL url = connection.getURL();
                String methodName = param.method.getName();

                if (url.getAuthority().contains("towerofsaviors")) {
                    if (methodName.equals("getOutputStream")) {
                        OutputStream origin = (OutputStream) param.getResult();
                        if (!(origin instanceof OutputStreamWrapper)) {
                            OutputStream log = getOutputStream(url);
                            if (log != null) {
                                appendToLog(log, "\r\n[REQUEST CONTENT]\r\n");
                                OutputStream substitute = new OutputStreamWrapper(origin, log);
                                param.setResult(substitute);
                            }
                        }
                    } else if (methodName.equals("getInputStream")) {
                        InputStream origin = (InputStream) param.getResult();
                        if (!(origin instanceof InputStreamWrapper)) {
                            OutputStream log = getOutputStream(url);
                            if (log != null) {
                                appendToLog(log, "\r\n[RESPONSE CONTENT]\r\n");
                                InputStream substitute = new InputStreamWrapper(origin, log);
                                param.setResult(substitute);
                            }
                        }
                    }
                }
            }
        };

        for (Method method : Class.forName("com.android.okhttp.internal.http.HttpURLConnectionImpl", false, packageParam.classLoader).getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getOutputStream") || methodName.equals("getInputStream"))
                hookMethod(method, hook);
        }

        for (Method method : Class.forName("com.android.okhttp.internal.http.HttpsURLConnectionImpl", false, packageParam.classLoader).getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("getOutputStream") || methodName.equals("getInputStream"))
                hookMethod(method, hook);
        }
    }

    private OutputStream getOutputStream(URL url) throws Throwable {
        String urlString = url.toExternalForm();
        Uri uri = Uri.parse(urlString);

        String timestamp = uri.getQueryParameter("timestamp");
        if (timestamp == null || timestamp.length() < 1)
            return null;

        File dir = new File(String.format("/sdcard/ToS/%s", uri.getPath()));
        dir.mkdirs();
        File file = new File(dir, timestamp + ".txt");

        return new FileOutputStream(file, true);
    }

    private void appendToLog(OutputStream log, String message) throws IOException {
        BufferedWriter logWriter = new BufferedWriter(new OutputStreamWriter(log));
        logWriter.write(message);
        logWriter.flush();
    }

    private class OutputStreamWrapper extends OutputStream {
        private final OutputStream log;
        private final OutputStream inner;

        public OutputStreamWrapper(OutputStream source, OutputStream log) throws Throwable {
            this.log = log;
            this.inner = source;
        }

        @Override
        public void close() throws IOException {
            appendToLog(log, "\r\n");
            log.close();
            inner.close();
        }

        @Override
        public void flush() throws IOException {
            log.flush();
            inner.flush();
        }

        @Override
        public void write(int oneByte) throws IOException {
            log.write(oneByte);
            inner.write(oneByte);
        }
    }

    private class InputStreamWrapper extends InputStream {
        private final OutputStream log;
        private final InputStream inner;

        public InputStreamWrapper(InputStream source, OutputStream log) throws Throwable {
            this.log = log;
            this.inner = source;
        }

        @Override
        public int available() throws IOException {
            return inner.available();
        }

        @Override
        public void close() throws IOException {
            appendToLog(log, "\r\n");
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
