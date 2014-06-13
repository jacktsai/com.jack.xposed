package com.jack.xposed.mod;

import android.app.Activity;
import android.app.ActivityThread;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.os.Message;
import android.view.CompatibilityInfoHolder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.IccPhoneBookInterfaceManager;
import com.android.internal.util.ArrayUtils;
import com.jack.xposed.R;
import com.jack.xposed.hooks.BeforeAfterMethodHook;
import com.jack.xposed.hooks.GeneralMethodHook;
import com.jack.xposed.utils.J;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getByteField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static de.robv.android.xposed.XposedBridge.hookAllMethods;

public class GlobalHandler {
    private static final String TAG = GlobalHandler.class.getSimpleName();

    public void onZygoteInit() throws Throwable {
        hook_PhoneWindow();
//        hook_View();
        hook_DecorView();
        hook_ViewRootImpl();
        hook_WindowManagerImpl();
        hook_Activity();
        hook_ActivityThread();
    }

    private void hook_PhoneWindow() throws Throwable {
        Class<?> clazz = XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindow", null);

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            XposedBridge.hookMethod(method, new GeneralMethodHook());
        }

        XC_MethodHook hook = new GeneralMethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
//                String methodName = param.method.getName();
//                if (methodName.equals("getDecorView")) {
//                    J.printStackTrace(TAG);
//                }
            }
        };

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("toString") ||
                    methodName.equals("superDispatchKeyEvent") ||
                    methodName.equals("superDispatchTouchEvent"))
                    continue;

                XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_View() throws Throwable {
        Class<?> clazz = View.class;

        XC_MethodHook hook = new GeneralMethodHook();

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("toString"))
                    continue;

                if (method.equals("draw"))
                    XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_DecorView() throws Throwable {
        Class<?> clazz = XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindow.DecorView", null);

        XC_MethodHook hook = new BeforeAfterMethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//
////                FrameLayout view = (FrameLayout)param.thisObject;
////                if (param.method.getName().equals("draw")) {
//////                    J.printStackTrace(TAG);
////                    Canvas canvas = (Canvas)param.args[0];
////                    Rect rect = new Rect();
////                    view.getDrawingRect(rect);
////                    drawRect(canvas, rect, Color.WHITE, 10);
////                }
//            }
//
//            private void drawRect(Canvas canvas, Rect rect, int color, int width) {
//                Paint paint = new Paint();
//                paint.setColor(color);
//                paint.setStrokeWidth(width);
//                paint.setStyle(Paint.Style.STROKE);
//
//                canvas.drawRect(rect, paint);
//            }
        };

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            XposedBridge.hookMethod(method, hook);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("toString") ||
                    methodName.equals("dispatchKeyEvent") || methodName.equals("onKeyDown") || methodName.equals("onKeyUp") || methodName.equals("superDispatchKeyEvent") ||
                        methodName.equals("onTouchEvent") || methodName.equals("onInterceptTouchEvent") || methodName.equals("dispatchTouchEvent") || methodName.equals("superDispatchTouchEvent"))
                    continue;

                XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_ViewRootImpl() throws Throwable {
        Class<?> clazz = ViewRootImpl.class;

        XC_MethodHook hook = new GeneralMethodHook();

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            XposedBridge.hookMethod(method, hook);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("toString"))
                    continue;

                XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_WindowManagerImpl() throws Throwable {
        Class<?> clazz = WindowManagerImpl.class;

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            XposedBridge.hookMethod(method, new GeneralMethodHook());
        }

        XC_MethodHook hook = new BeforeAfterMethodHook();

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("toString"))
                    continue;

                XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_Activity() throws Throwable {
        Class<?> clazz = Activity.class;

        XposedBridge.hookAllMethods(clazz, "onPostCreate", new GeneralMethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity context = (Activity)param.thisObject;

                // 亂改其它 app 的 ID_ANDROID_CONTENT 會導致出錯，僅作於本 app 學習與測試用。
                if (context.getPackageName().equals("com.jack.xposed")) {
                    Window window = context.getWindow();
                    ViewGroup decor = (ViewGroup) window.getDecorView();
                    ViewGroup androidContent = (ViewGroup) decor.findViewById(Window.ID_ANDROID_CONTENT);
                    View oldContent = androidContent.getChildAt(0);
                    androidContent.removeAllViews();
                    View newContent = context.getLayoutInflater().inflate(R.layout.content_view, androidContent);
                    FrameLayout contentHolder = (FrameLayout) newContent.findViewById(R.id.content);
                    contentHolder.addView(oldContent);
                }

            }
        });
    }


    private void hook_ActivityThread() throws Throwable {
        Class<?> clazz = ActivityThread.class;

        hookAllMethods(clazz, "handleMessage", new GeneralMethodHook() {
            private static final int LAUNCH_ACTIVITY         = 100;
            private static final int PAUSE_ACTIVITY          = 101;
            private static final int PAUSE_ACTIVITY_FINISHING= 102;
            private static final int STOP_ACTIVITY_SHOW      = 103;
            private static final int STOP_ACTIVITY_HIDE      = 104;
            private static final int SHOW_WINDOW             = 105;
            private static final int HIDE_WINDOW             = 106;
            private static final int RESUME_ACTIVITY         = 107;
            private static final int SEND_RESULT             = 108;
            private static final int DESTROY_ACTIVITY        = 109;
            private static final int BIND_APPLICATION        = 110;
            private static final int EXIT_APPLICATION        = 111;
            private static final int NEW_INTENT              = 112;
            private static final int RECEIVER                = 113;
            private static final int CREATE_SERVICE          = 114;
            private static final int SERVICE_ARGS            = 115;
            private static final int STOP_SERVICE            = 116;
            private static final int REQUEST_THUMBNAIL       = 117;
            private static final int CONFIGURATION_CHANGED   = 118;
            private static final int CLEAN_UP_CONTEXT        = 119;
            private static final int GC_WHEN_IDLE            = 120;
            private static final int BIND_SERVICE            = 121;
            private static final int UNBIND_SERVICE          = 122;
            private static final int DUMP_SERVICE            = 123;
            private static final int LOW_MEMORY              = 124;
            private static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
            private static final int RELAUNCH_ACTIVITY       = 126;
            private static final int PROFILER_CONTROL        = 127;
            private static final int CREATE_BACKUP_AGENT     = 128;
            private static final int DESTROY_BACKUP_AGENT    = 129;
            private static final int SUICIDE                 = 130;
            private static final int REMOVE_PROVIDER         = 131;
            private static final int ENABLE_JIT              = 132;
            private static final int DISPATCH_PACKAGE_BROADCAST = 133;
            private static final int SCHEDULE_CRASH          = 134;
            private static final int DUMP_HEAP               = 135;
            private static final int DUMP_ACTIVITY           = 136;
            private static final int SLEEPING                = 137;
            private static final int SET_CORE_SETTINGS       = 138;
            private static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;
            private static final int TRIM_MEMORY             = 140;

            HashMap<Context, View> viewMap = new HashMap<Context, View>();

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ActivityThread activityThread = (ActivityThread)param.thisObject;
                Message msg = (Message)param.args[0];

                switch (msg.what) {
                    case DESTROY_ACTIVITY:
                        IBinder token = (IBinder)msg.obj;
                        removeMyView(activityThread, token);
                        break;
                }

                J.d(TAG, ">>> handling: %s", msg.toString());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ActivityThread activityThread = (ActivityThread)param.thisObject;
                Message msg = (Message)param.args[0];

                switch (msg.what) {
                    case LAUNCH_ACTIVITY:
                        Object activityRecord = msg.obj;
                        addMyView(activityThread, activityRecord);
                        break;
                }

                J.d(TAG, "<<< done: %s", msg.toString());
            }

            private void addMyView(ActivityThread activityThread, Object activityRecord) {
                HashMap<?, ?> mActivities = (HashMap<?, ?>)getObjectField(activityThread, "mActivities");
                Activity activity = (Activity)getObjectField(activityRecord, "activity");

                TextView textView = new TextView(activity);
                String text = String.format("(%d)%s", mActivities.size(), activity.getPackageName());
                textView.setText(text);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
                layoutParams.format = PixelFormat.RGBA_8888;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

                WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
                windowManager.addView(textView, layoutParams);

                viewMap.put(activity, textView);
            }

            private void removeMyView(ActivityThread activityThread, IBinder token) {
                Activity activity = activityThread.getActivity(token);
                View view = viewMap.get(activity);

                WindowManager windowManager = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
                windowManager.removeViewImmediate(view);

                viewMap.remove(activity);
            }
        });
    }
}
