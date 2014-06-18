package com.jack.xposed.mod;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioTrack;
import android.os.IBinder;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HardwareCanvas;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jack.xposed.R;
import com.jack.xposed.hooks.GeneralMethodHook;
import com.jack.xposed.utils.J;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedBridge.hookMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class GlobalHandler {
    protected static GlobalHandler instance = new GlobalHandler();

    public static GlobalHandler getInstance() {
        return instance;
    }

    protected GlobalHandler() {
    }

    public void onZygoteInit(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
    }

    public void onLoadPackage(LoadPackageParam packageParam) throws Throwable {
        String packageName = packageParam.packageName;

//        new ActivityThread_MessageDecorator(packageParam);
//        new ViewRootImpl_Decorator(packageParam);
//        new FileInputStream_Tracer(packageParam);

        if (packageName.equals("com.jack.xposed")) {
            new ActivityThread_MessageTracer(packageParam);
            new Activity_Tracer(packageParam);
            new Activity_Decorator(packageParam);

            new WindowManagerImpl_Tracer(packageParam);
            new WindowManagerGlobal_Tracer(packageParam);
            new PhoneWindow_Tracer(packageParam);
            new DecorView_Tracer(packageParam);
//            new ViewRootImpl_Tracer(packageParam);
            new ViewRoot_MessageTracer(packageParam);
        }
    }

    private class AudioTrack_Tracer extends GeneralMethodHook {
        public AudioTrack_Tracer(LoadPackageParam packageParam) {
            super(packageParam);

            Class clazz = AudioTrack.class;

            for (Constructor<?> method : clazz.getDeclaredConstructors()) {
                hookMethod(method, this);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    String methodName = method.getName();
                    if (methodName.equals("toString") || methodName.equals("write"))
                        continue;

                    hookMethod(method, this);
                }
            }
        }
    }

    private class FileInputStream_Tracer extends GeneralMethodHook {
        public FileInputStream_Tracer(LoadPackageParam packageParam) {
            super(packageParam);

            if (packageParam.packageName.equals("com.madhead.tos.zh")) {
                Class clazz = FileInputStream.class;

                for (Constructor<?> method : clazz.getDeclaredConstructors()) {
                    hookMethod(method, this);
                }

//                for (Method method : clazz.getDeclaredMethods()) {
//                    if (Modifier.isPublic(method.getModifiers())) {
//                        String methodName = method.getName();
//                        if (methodName.equals("toString"))
//                            continue;
//
//                        hookMethod(method, this);
//                    }
//                }
            }
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);

            if (param.method instanceof Constructor)
                J.printStackTrace(TAG);
        }
    }

    private class PhoneWindow_Tracer extends GeneralMethodHook {
        public PhoneWindow_Tracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class clazz = findClass("com.android.internal.policy.impl.PhoneWindow", null);

            for (Constructor<?> method : clazz.getDeclaredConstructors()) {
                hookMethod(method, this);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    String methodName = method.getName();
                    if (methodName.equals("toString") ||
                            methodName.equals("superDispatchKeyEvent") ||
                            methodName.equals("superDispatchTouchEvent"))
                        continue;

                    hookMethod(method, this);
                }
            }
        }
    }

    private class DecorView_Tracer extends GeneralMethodHook {
        public DecorView_Tracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = findClass("com.android.internal.policy.impl.PhoneWindow.DecorView", null);

            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    String methodName = method.getName();
                    if (methodName.equals("toString") ||
                            methodName.equals("dispatchKeyEvent") || methodName.equals("onKeyDown") || methodName.equals("onKeyUp") || methodName.equals("superDispatchKeyEvent") ||
                            methodName.equals("onTouchEvent") || methodName.equals("onInterceptTouchEvent") || methodName.equals("dispatchTouchEvent") || methodName.equals("superDispatchTouchEvent"))
                        continue;

                    hookMethod(method, this);
                }
            }
        }
    }

    private class ViewRootImpl_Tracer extends GeneralMethodHook {
        public ViewRootImpl_Tracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = ViewRootImpl.class;

            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers())) {
                    String methodName = method.getName();
                    if (methodName.equals("toString"))
                        continue;

                    hookMethod(method, this);
                }
            }
        }
    }

    private class ViewRootImpl_Decorator extends XC_MethodHook {
        public ViewRootImpl_Decorator(LoadPackageParam packageParam) throws Throwable {
            Class<?> clazz = ViewRootImpl.class;

            findAndHookMethod(clazz, "onHardwarePostDraw", HardwareCanvas.class, this);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Method method = (Method) param.method;
            String methodName = method.getName();
            if (methodName.equals("onHardwarePostDraw")) {
                HardwareCanvas canvas = (HardwareCanvas) param.args[0];
                Rect mWinFrame = (Rect) getObjectField(param.thisObject, "mWinFrame");

                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(4);
                paint.setStyle(Paint.Style.STROKE);

                canvas.drawRect(mWinFrame, paint);
            }
        }
    }

    private class WindowManagerImpl_Tracer extends GeneralMethodHook {
        public WindowManagerImpl_Tracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = findClass("android.view.WindowManagerImpl", null);

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
    }

    /**
     * 已查證，每一個 app 會有自己一個 WindowManagerGlobal instance
     */
    private class WindowManagerGlobal_Tracer extends GeneralMethodHook {
        public WindowManagerGlobal_Tracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = findClass("android.view.WindowManagerGlobal", null);

            for (Constructor<?> method : clazz.getDeclaredConstructors()) {
                hookMethod(method, this);
            }
        }
    }

    private class Activity_Tracer extends GeneralMethodHook {
        public Activity_Tracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = Activity.class;

            for (Constructor<?> method : clazz.getDeclaredConstructors()) {
                hookMethod(method, this);
            }

            String packageName = packageParam.packageName;
            for (Method method : clazz.getDeclaredMethods()) {
                String methodName = method.getName();
                if (methodName.equals("toString") || methodName.equals("getSystemService"))
                    continue;

                if (packageName.equals("com.jack.xposed") ||
                        methodName.equals("onCreate") || methodName.equals("onResume") || methodName.equals("onPause") || methodName.equals("doDestroy"))
                    hookMethod(method, this);
            }
        }
    }

    private class Activity_Decorator extends XC_MethodHook {
        private final LoadPackageParam packageParam;
        private final HashMap<Context, View> viewMap = new HashMap<Context, View>();

        public Activity_Decorator(LoadPackageParam packageParam) {
            this.packageParam = packageParam;

            hookAllMethods(Activity.class, "onResume", this);
            hookAllMethods(Activity.class, "onPause", this);
            hookAllMethods(Activity.class, "onPostCreate", this);
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            Activity activity = (Activity) param.thisObject;
            String methodName = param.method.getName();

            if (methodName.equals("onPause")) {

                removeCustomView(activity);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Activity activity = (Activity) param.thisObject;
            String methodName = param.method.getName();

            if (methodName.equals("onResume")) {

                addCustomView(activity);

            } else if (methodName.equals("onPostCreate")) {

                // 亂改其它 app 的 ID_ANDROID_CONTENT 會導致出錯，僅作於本 app 學習與測試用。
                if (packageParam.packageName.equals("com.jack.xposed")) {
                    Window window = activity.getWindow();
                    ViewGroup decor = (ViewGroup) window.getDecorView();
                    ViewGroup androidContent = (ViewGroup) decor.findViewById(Window.ID_ANDROID_CONTENT);
                    View oldContent = androidContent.getChildAt(0);
                    androidContent.removeAllViews();
                    View newContent = activity.getLayoutInflater().inflate(R.layout.content_view, androidContent);
                    FrameLayout contentHolder = (FrameLayout) newContent.findViewById(R.id.content);
                    contentHolder.addView(oldContent);
                }
            }
        }

        private void addCustomView(Activity activity) {
            TextView textView = new TextView(activity);
            String text = String.format("%s", activity.getComponentName().getClassName());
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            textView.setTextColor(Color.YELLOW);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
            layoutParams.format = PixelFormat.OPAQUE;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

            activity.getWindowManager().addView(textView, layoutParams);

            viewMap.put(activity, textView);
        }

        private void removeCustomView(Activity activity) {
            View view = viewMap.get(activity);
            activity.getWindowManager().removeView(view);
            viewMap.remove(activity);
        }
    }

    private class ActivityThread_MessageTracer extends GeneralMethodHook {
        protected static final int LAUNCH_ACTIVITY = 100;
        protected static final int PAUSE_ACTIVITY = 101;
        protected static final int PAUSE_ACTIVITY_FINISHING = 102;
        protected static final int STOP_ACTIVITY_SHOW = 103;
        protected static final int STOP_ACTIVITY_HIDE = 104;
        protected static final int SHOW_WINDOW = 105;
        protected static final int HIDE_WINDOW = 106;
        protected static final int RESUME_ACTIVITY = 107;
        protected static final int SEND_RESULT = 108;
        protected static final int DESTROY_ACTIVITY = 109;
        protected static final int BIND_APPLICATION = 110;
        protected static final int EXIT_APPLICATION = 111;
        protected static final int NEW_INTENT = 112;
        protected static final int RECEIVER = 113;
        protected static final int CREATE_SERVICE = 114;
        protected static final int SERVICE_ARGS = 115;
        protected static final int STOP_SERVICE = 116;
        protected static final int REQUEST_THUMBNAIL = 117;
        protected static final int CONFIGURATION_CHANGED = 118;
        protected static final int CLEAN_UP_CONTEXT = 119;
        protected static final int GC_WHEN_IDLE = 120;
        protected static final int BIND_SERVICE = 121;
        protected static final int UNBIND_SERVICE = 122;
        protected static final int DUMP_SERVICE = 123;
        protected static final int LOW_MEMORY = 124;
        protected static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
        protected static final int RELAUNCH_ACTIVITY = 126;
        protected static final int PROFILER_CONTROL = 127;
        protected static final int CREATE_BACKUP_AGENT = 128;
        protected static final int DESTROY_BACKUP_AGENT = 129;
        protected static final int SUICIDE = 130;
        protected static final int REMOVE_PROVIDER = 131;
        protected static final int ENABLE_JIT = 132;
        protected static final int DISPATCH_PACKAGE_BROADCAST = 133;
        protected static final int SCHEDULE_CRASH = 134;
        protected static final int DUMP_HEAP = 135;
        protected static final int DUMP_ACTIVITY = 136;
        protected static final int SLEEPING = 137;
        protected static final int SET_CORE_SETTINGS = 138;
        protected static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
        protected static final int TRIM_MEMORY = 140;
        protected static final int DUMP_PROVIDER = 141;
        protected static final int UNSTABLE_PROVIDER_DIED = 142;
        protected static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        protected static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
        protected static final int INSTALL_PROVIDER = 145;

        public ActivityThread_MessageTracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = findClass("android.app.ActivityThread$H", null);
            findAndHookMethod(clazz, "handleMessage", Message.class, this);
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            Message message = (Message) param.args[0];
            beforeMessageHandled(message);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Message message = (Message) param.args[0];
            afterMessageHandled(message);
        }

        protected void beforeMessageHandled(Message message) {
            String packageName = packageParam.packageName;
            int m = message.what;
            if (packageName.equals("com.jack.xposed") ||
                    m == LAUNCH_ACTIVITY || m == RESUME_ACTIVITY || m == PAUSE_ACTIVITY || m == DESTROY_ACTIVITY)
                J.d(TAG, "[%s] >> handling: %s", packageParam.packageName, codeToString(message.what));
        }

        protected void afterMessageHandled(Message message) {
            String packageName = packageParam.packageName;
            int m = message.what;
            if (packageName.equals("com.jack.xposed") ||
                    message.what == LAUNCH_ACTIVITY || m == RESUME_ACTIVITY || m == PAUSE_ACTIVITY || message.what == DESTROY_ACTIVITY)
                J.d(TAG, "[%s] << handling: %s", packageParam.packageName, codeToString(message.what));
        }

        private String codeToString(int code) {
            switch (code) {
                case LAUNCH_ACTIVITY:
                    return "LAUNCH_ACTIVITY";
                case PAUSE_ACTIVITY:
                    return "PAUSE_ACTIVITY";
                case PAUSE_ACTIVITY_FINISHING:
                    return "PAUSE_ACTIVITY_FINISHING";
                case STOP_ACTIVITY_SHOW:
                    return "STOP_ACTIVITY_SHOW";
                case STOP_ACTIVITY_HIDE:
                    return "STOP_ACTIVITY_HIDE";
                case SHOW_WINDOW:
                    return "SHOW_WINDOW";
                case HIDE_WINDOW:
                    return "HIDE_WINDOW";
                case RESUME_ACTIVITY:
                    return "RESUME_ACTIVITY";
                case SEND_RESULT:
                    return "SEND_RESULT";
                case DESTROY_ACTIVITY:
                    return "DESTROY_ACTIVITY";
                case BIND_APPLICATION:
                    return "BIND_APPLICATION";
                case EXIT_APPLICATION:
                    return "EXIT_APPLICATION";
                case NEW_INTENT:
                    return "NEW_INTENT";
                case RECEIVER:
                    return "RECEIVER";
                case CREATE_SERVICE:
                    return "CREATE_SERVICE";
                case SERVICE_ARGS:
                    return "SERVICE_ARGS";
                case STOP_SERVICE:
                    return "STOP_SERVICE";
                case REQUEST_THUMBNAIL:
                    return "REQUEST_THUMBNAIL";
                case CONFIGURATION_CHANGED:
                    return "CONFIGURATION_CHANGED";
                case CLEAN_UP_CONTEXT:
                    return "CLEAN_UP_CONTEXT";
                case GC_WHEN_IDLE:
                    return "GC_WHEN_IDLE";
                case BIND_SERVICE:
                    return "BIND_SERVICE";
                case UNBIND_SERVICE:
                    return "UNBIND_SERVICE";
                case DUMP_SERVICE:
                    return "DUMP_SERVICE";
                case LOW_MEMORY:
                    return "LOW_MEMORY";
                case ACTIVITY_CONFIGURATION_CHANGED:
                    return "ACTIVITY_CONFIGURATION_CHANGED";
                case RELAUNCH_ACTIVITY:
                    return "RELAUNCH_ACTIVITY";
                case PROFILER_CONTROL:
                    return "PROFILER_CONTROL";
                case CREATE_BACKUP_AGENT:
                    return "CREATE_BACKUP_AGENT";
                case DESTROY_BACKUP_AGENT:
                    return "DESTROY_BACKUP_AGENT";
                case SUICIDE:
                    return "SUICIDE";
                case REMOVE_PROVIDER:
                    return "REMOVE_PROVIDER";
                case ENABLE_JIT:
                    return "ENABLE_JIT";
                case DISPATCH_PACKAGE_BROADCAST:
                    return "DISPATCH_PACKAGE_BROADCAST";
                case SCHEDULE_CRASH:
                    return "SCHEDULE_CRASH";
                case DUMP_HEAP:
                    return "DUMP_HEAP";
                case DUMP_ACTIVITY:
                    return "DUMP_ACTIVITY";
                case SLEEPING:
                    return "SLEEPING";
                case SET_CORE_SETTINGS:
                    return "SET_CORE_SETTINGS";
                case UPDATE_PACKAGE_COMPATIBILITY_INFO:
                    return "UPDATE_PACKAGE_COMPATIBILITY_INFO";
                case TRIM_MEMORY:
                    return "TRIM_MEMORY";
                case DUMP_PROVIDER:
                    return "DUMP_PROVIDER";
                case UNSTABLE_PROVIDER_DIED:
                    return "UNSTABLE_PROVIDER_DIED";
                case REQUEST_ASSIST_CONTEXT_EXTRAS:
                    return "REQUEST_ASSIST_CONTEXT_EXTRAS";
                case TRANSLUCENT_CONVERSION_COMPLETE:
                    return "TRANSLUCENT_CONVERSION_COMPLETE";
                case INSTALL_PROVIDER:
                    return "INSTALL_PROVIDER";
            }

            return String.format("(unknown of code %d)", code);
        }
    }

    private class ActivityThread_MessageDecorator extends ActivityThread_MessageTracer {
        public ActivityThread_MessageDecorator(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);
        }

        @Override
        protected void beforeMessageHandled(Message message) {
            if (message.what == DESTROY_ACTIVITY) {
                ActivityThread activityThread = ActivityThread.currentActivityThread();
                IBinder token = (IBinder) message.obj;
                Activity activity = activityThread.getActivity(token);
//                removeCustomView(activity);
            }
        }

        @Override
        protected void afterMessageHandled(Message message) {
            if (message.what == LAUNCH_ACTIVITY) {
                Object activityRecord = message.obj;
                Activity activity = (Activity) getObjectField(activityRecord, "activity");
//                addCustomView(activity);
            }
        }
    }

    private class ViewRoot_MessageTracer extends GeneralMethodHook {
        protected static final int MSG_INVALIDATE = 1;
        protected static final int MSG_INVALIDATE_RECT = 2;
        protected static final int MSG_DIE = 3;
        protected static final int MSG_RESIZED = 4;
        protected static final int MSG_RESIZED_REPORT = 5;
        protected static final int MSG_WINDOW_FOCUS_CHANGED = 6;
        protected static final int MSG_DISPATCH_INPUT_EVENT = 7;
        protected static final int MSG_DISPATCH_APP_VISIBILITY = 8;
        protected static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
        protected static final int MSG_DISPATCH_KEY_FROM_IME = 11;
        protected static final int MSG_FINISH_INPUT_CONNECTION = 12;
        protected static final int MSG_CHECK_FOCUS = 13;
        protected static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
        protected static final int MSG_DISPATCH_DRAG_EVENT = 15;
        protected static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
        protected static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
        protected static final int MSG_UPDATE_CONFIGURATION = 18;
        protected static final int MSG_PROCESS_INPUT_EVENTS = 19;
        protected static final int MSG_DISPATCH_SCREEN_STATE = 20;
        protected static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
        protected static final int MSG_DISPATCH_DONE_ANIMATING = 22;
        protected static final int MSG_INVALIDATE_WORLD = 23;
        protected static final int MSG_WINDOW_MOVED = 24;
        protected static final int MSG_FLUSH_LAYER_UPDATES = 25;

        public ViewRoot_MessageTracer(LoadPackageParam packageParam) throws Throwable {
            super(packageParam);

            Class<?> clazz = findClass("android.view.ViewRootImpl$ViewRootHandler", null);
            findAndHookMethod(clazz, "handleMessage", Message.class, this);
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            Message message = (Message) param.args[0];
            beforeMessageHandled(message);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Message message = (Message) param.args[0];
            afterMessageHandled(message);
        }

        protected void beforeMessageHandled(Message message) {
            J.d(TAG, "[%s] >> handling: %s", packageParam.packageName, codeToString(message.what));
        }

        protected void afterMessageHandled(Message message) {
            J.d(TAG, "[%s] << handling: %s", packageParam.packageName, codeToString(message.what));
        }

        private String codeToString(int code) {
            switch (code) {
                case MSG_INVALIDATE:
                    return "MSG_INVALIDATE";
                case MSG_INVALIDATE_RECT:
                    return "MSG_INVALIDATE_RECT";
                case MSG_DIE:
                    return "MSG_DIE";
                case MSG_RESIZED:
                    return "MSG_RESIZED";
                case MSG_RESIZED_REPORT:
                    return "MSG_RESIZED_REPORT";
                case MSG_WINDOW_FOCUS_CHANGED:
                    return "MSG_WINDOW_FOCUS_CHANGED";
                case MSG_DISPATCH_INPUT_EVENT:
                    return "MSG_DISPATCH_INPUT_EVENT";
                case MSG_DISPATCH_APP_VISIBILITY:
                    return "MSG_DISPATCH_APP_VISIBILITY";
                case MSG_DISPATCH_GET_NEW_SURFACE:
                    return "MSG_DISPATCH_GET_NEW_SURFACE";
                case MSG_DISPATCH_KEY_FROM_IME:
                    return "MSG_DISPATCH_KEY_FROM_IME";
                case MSG_FINISH_INPUT_CONNECTION:
                    return "MSG_FINISH_INPUT_CONNECTION";
                case MSG_CHECK_FOCUS:
                    return "MSG_CHECK_FOCUS";
                case MSG_CLOSE_SYSTEM_DIALOGS:
                    return "MSG_CLOSE_SYSTEM_DIALOGS";
                case MSG_DISPATCH_DRAG_EVENT:
                    return "MSG_DISPATCH_DRAG_EVENT";
                case MSG_DISPATCH_DRAG_LOCATION_EVENT:
                    return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
                case MSG_DISPATCH_SYSTEM_UI_VISIBILITY:
                    return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
                case MSG_UPDATE_CONFIGURATION:
                    return "MSG_UPDATE_CONFIGURATION";
                case MSG_PROCESS_INPUT_EVENTS:
                    return "MSG_PROCESS_INPUT_EVENTS";
                case MSG_DISPATCH_SCREEN_STATE:
                    return "MSG_DISPATCH_SCREEN_STATE";
                case MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST:
                    return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
                case MSG_DISPATCH_DONE_ANIMATING:
                    return "MSG_DISPATCH_DONE_ANIMATING";
                case MSG_INVALIDATE_WORLD:
                    return "MSG_INVALIDATE_WORLD";
                case MSG_WINDOW_MOVED:
                    return "MSG_WINDOW_MOVED";
                case MSG_FLUSH_LAYER_UPDATES:
                    return "MSG_FLUSH_LAYER_UPDATES";
            }

            return String.format("(unknown of code %d)", code);
        }
    }
}
