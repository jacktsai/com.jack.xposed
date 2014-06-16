package com.jack.xposed.mod;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;

import com.jack.xposed.hooks.GeneralMethodHook;
import com.jack.xposed.hooks.PackageMaanger_Tracer;
import com.jack.xposed.utils.J;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.callbacks.XCallback;

import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class TosHandler {
    private static final String TAG = TosHandler.class.getSimpleName();
    private static final String LOG_DIR = "/sdcard/ToS_traces";
    private static HashMap<String, FileOutputStream> fileMap = new HashMap<String, FileOutputStream>();

    private static FileOutputStream getOutputFile(URL url) {
        String filePath = String.format("%s/%s", LOG_DIR, url.getAuthority());
        FileOutputStream file = fileMap.get(filePath);
        if (file == null) {
            try {
                file = new FileOutputStream(filePath, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            fileMap.put(filePath, file);
        }

        return file;
    }

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
//        new PackageMaanger_Tracer(packageParam);
        new Portal_Decorator(packageParam);
        new PackageMaanger_Decorator();
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
                if (urlString.contains("floor/complete")) {
//                    URLConnection urlConnection = (URLConnection) param.getResult();
//                    InputStream inputStream = urlConnection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        J.d(TAG, line);
//                    }
//                    reader.close();
//                    inputStream.close();
//
//                InputStream()
//                BufferedWriter writer = new BufferedWriter(OutputStreamWriter());
                }
            }
        };
        XposedBridge.hookAllMethods(clazz, "openConnection", hook);
    }

    private class PackageMaanger_Decorator extends XC_MethodHook {
        public PackageMaanger_Decorator() {
            super(XCallback.PRIORITY_LOWEST);

            Class clazz = findClass("android.app.ApplicationPackageManager", null);

            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
                if (methodName.equals("getPackageInfo"))
                    hookMethod(method, this);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            String packageName = (String) param.args[0];
            if (packageName.equals("com.madhead.tos.zh")) {
//                int flags = (Integer) param.args[1];
//                if (flags == PackageManager.GET_SIGNATURES)
                {
                    PackageInfo packageInfo = (PackageInfo) param.getResult();
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        J.d("MethodHook", "count of signature(s): %d", packageInfo.signatures.length);
                        packageInfo.signatures[0] = new Signature("3082026f308201d8a00302010202044ff45bcf300d06092a864886f70d0101050500307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e673020170d3132303730343135303535315a180f32303632303632323135303535315a307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e6730819f300d06092a864886f70d010101050003818d003081890281810089adafb3a3072e35d55608931ed94d9ef628e913224d275af4ad35d69ec8a57637848db8235961be1e1fe2d7f66655dbe962433b79180f492fa65ec3364d1039b6c3a313449701b8600cac255a02aec6935515063de2efac5acdd6e169b86a9a3a05d6c0e50d796ffed62829c763f011b35d37c1db0b861e1d9a1c125eb7debd0203010001300d06092a864886f70d010105050003818100500ea259a144f04fcb6aa1ae3d63ef0dade0c6abbc98359f0a91753f12787514edd1f3f28ae78513def110ebf4a68523875525091cad0ab58d087569c6071acf3cddb1a1d2c461c318c1aaba52bb0c0d550243f9190ab0277cf73dcaa01408aedd7a9d8c0eba1328ed03fa592731c2db2036c47ea38d13d2b9b9b5da0959eb38");
                        J.d("MethodHook", "first signature has been replaced!");
                    }
                }
            }
        }
    }

    private class Portal_Decorator extends GeneralMethodHook {
        public Portal_Decorator(LoadPackageParam packageParam) {
            super(packageParam, XCallback.PRIORITY_LOWEST);

            Class clazz = findClass("com.madhead.tos.plugins.Portal", packageParam.classLoader);

            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
                if (methodName.equals("function5580") || methodName.equals("function5581") || methodName.equals("function5582") || methodName.equals("function5583") || methodName.equals("function5584") || methodName.equals("function5585") || methodName.equals("function5586"))
                    hookMethod(method, this);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            //param.setResult("3082026f308201d8a00302010202044ff45bcf300d06092a864886f70d0101050500307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e673020170d3132303730343135303535315a180f32303632303632323135303535315a307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e6730819f300d06092a864886f70d010101050003818d003081890281810089adafb3a3072e35d55608931ed94d9ef628e913224d275af4ad35d69ec8a57637848db8235961be1e1fe2d7f66655dbe962433b79180f492fa65ec3364d1039b6c3a313449701b8600cac255a02aec6935515063de2efac5acdd6e169b86a9a3a05d6c0e50d796ffed62829c763f011b35d37c1db0b861e1d9a1c125eb7debd0203010001300d06092a864886f70d010105050003818100500ea259a144f04fcb6aa1ae3d63ef0dade0c6abbc98359f0a91753f12787514edd1f3f28ae78513def110ebf4a68523875525091cad0ab58d087569c6071acf3cddb1a1d2c461c318c1aaba52bb0c0d550243f9190ab0277cf73dcaa01408aedd7a9d8c0eba1328ed03fa592731c2db2036c47ea38d13d2b9b9b5da0959eb38,3082026f308201d8a00302010202044ff45bcf300d06092a864886f70d0101050500307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e673020170d3132303730343135303535315a180f32303632303632323135303535315a307b310c300a06035504061303383532310e300c060355040813054368696e613112301006035504071309486f6e67204b6f6e6731193017060355040a13104d61642048656164204c696d6974656431143012060355040b130b4d6f62696c652047616d65311630140603550403130d546572656e6365205473616e6730819f300d06092a864886f70d010101050003818d003081890281810089adafb3a3072e35d55608931ed94d9ef628e913224d275af4ad35d69ec8a57637848db8235961be1e1fe2d7f66655dbe962433b79180f492fa65ec3364d1039b6c3a313449701b8600cac255a02aec6935515063de2efac5acdd6e169b86a9a3a05d6c0e50d796ffed62829c763f011b35d37c1db0b861e1d9a1c125eb7debd0203010001300d06092a864886f70d010105050003818100500ea259a144f04fcb6aa1ae3d63ef0dade0c6abbc98359f0a91753f12787514edd1f3f28ae78513def110ebf4a68523875525091cad0ab58d087569c6071acf3cddb1a1d2c461c318c1aaba52bb0c0d550243f9190ab0277cf73dcaa01408aedd7a9d8c0eba1328ed03fa592731c2db2036c47ea38d13d2b9b9b5da0959eb38,;");
            String methodName = param.method.getName();
            if (methodName.equals("function5585")) {
                String fileIndex = (String)param.args[6];
                if (fileIndex.equals("0")) {
                    param.setResult("assets/bin/Data/Managed/Assembly-CSharp-origin.dll");
                    J.d(TAG, "result has changed");
                }
            }

            super.afterHookedMethod(param);
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

    private void hook_URLConnection(LoadPackageParam packageParam) throws Throwable {
        XC_MethodHook hook = new GeneralMethodHook(packageParam) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                URLConnection connection = (URLConnection) param.thisObject;
                URL url = connection.getURL();
                if (url.getAuthority().contains("towerofsaviors")) {
                    String methodName = param.method.getName();

//                    if (methodName.equals("getOutputStream")) {
//                        OutputStream origin = (OutputStream) param.getResult();
//                        if (origin instanceof MyOutputStream) {
//                        } else {
//                            OutputStream substitute = new MyOutputStream(origin, url);
//                            param.setResult(substitute);
//                        }
//                    } else if (param.method.getName().equals("getInputStream")) {
//                        InputStream origin = (InputStream) param.getResult();
//                        if (origin instanceof MyInputStream) {
//                        } else {
//                            InputStream substitute = new MyInputStream(origin, url);
//                            param.setResult(substitute);
//                        }
//                    }

                    super.afterHookedMethod(param);
                }
            }
        };

        for (Method method : Class.forName("com.android.okhttp.internal.http.HttpURLConnectionImpl", false, packageParam.classLoader).getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("toString") || methodName.equals("getURL"))
                continue;

            if (Modifier.isPublic(method.getModifiers()))
                XposedBridge.hookMethod(method, hook);
        }

        for (Method method : Class.forName("com.android.okhttp.internal.http.HttpsURLConnectionImpl", false, packageParam.classLoader).getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("toString") || methodName.equals("getURL"))
                continue;

            if (Modifier.isPublic(method.getModifiers()))
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

    private class MyOutputStream extends OutputStream {
        private final OutputStream inner;
        private final FileOutputStream log;

        public MyOutputStream(OutputStream inner, URL url) {
            this.inner = inner;

            String message = String.format("\n[request]\n");
            log = getOutputFile(url);
            try {
                log.write(message.getBytes("UTF-8"));
                log.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() throws IOException {
            inner.close();
        }

        @Override
        public void flush() throws IOException {
            inner.flush();
        }

        @Override
        public void write(int oneByte) throws IOException {
            inner.write(oneByte);
            log.write(oneByte);
            log.flush();
        }
    }

    private class MyInputStream extends InputStream {
        private final InputStream inner;
        private final FileOutputStream log;

        public MyInputStream(InputStream inner, URL url) {
            this.inner = inner;

            String message = String.format("\n[response]\n");
            log = getOutputFile(url);
            try {
                log.write(message.getBytes("UTF-8"));
                log.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int available() throws IOException {
            return inner.available();
        }

        @Override
        public void close() throws IOException {
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
            int oneByte = inner.read();

            if (oneByte == -1) {
                log.write("\n".getBytes("UTF-8"));
            } else {
                log.write(oneByte);
            }
            log.flush();

            return oneByte;
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
