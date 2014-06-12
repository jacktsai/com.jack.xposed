package com.jack.xposed.mod;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jack.xposed.hooks.GeneralMethodHook;
import com.jack.xposed.utils.J;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class GlobalHandler {
    private static final String TAG = GlobalHandler.class.getSimpleName();

    public void onZygoteInit() throws Throwable {
//        hook_Context();
        hook_PhoneWindow();
        hook_DecorView();
    }

    private void hook_Context() throws Throwable {
        Class<?> clazz = XposedHelpers.findClass("android.app.ContextImpl", null);

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            XposedBridge.hookMethod(method, new GeneralMethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    J.printStackTrace(TAG);
                }
            });
        }

        XC_MethodHook hook = new GeneralMethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                J.printStackTrace(TAG);
            }
        };

        for (Method method : clazz.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("createSystemContext") || methodName.equals("createAppContext") || methodName.equals("createActivityContext")) {
                XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_PhoneWindow() throws Throwable {
        Class<?> clazz = XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindow", null);

        XC_MethodHook hook = new GeneralMethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                String methodName = param.method.getName();
                if (methodName.equals("setContentView")) {
                    Window window = (Window)param.thisObject;
                    Context context = window.getContext();

                    TextView textView = new TextView(context);
                    textView.setText(context.getPackageName());

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
                    layoutParams.format = PixelFormat.RGBA_8888;
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

//                    WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
//                    windowManager.addView(textView, layoutParams);
                }
            }

            private void drawRect(Canvas canvas, Rect rect, int color, int width) {
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStrokeWidth(width);
                paint.setStyle(Paint.Style.STROKE);

                canvas.drawRect(rect, paint);
            }
        };

        for (Constructor<?> method : clazz.getDeclaredConstructors()) {
            XposedBridge.hookMethod(method, hook);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("superDispatchKeyEvent") ||
                    methodName.equals("superDispatchTouchEvent"))
                    continue;

                XposedBridge.hookMethod(method, hook);
            }
        }
    }

    private void hook_DecorView() throws Throwable {
        Class<?> clazz = XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindow.DecorView", null);
        XC_MethodHook hook = new GeneralMethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

//                FrameLayout view = (FrameLayout)param.thisObject;
//                if (param.method.getName().equals("draw")) {
////                    J.printStackTrace(TAG);
//                    Canvas canvas = (Canvas)param.args[0];
//                    Rect rect = new Rect();
//                    view.getDrawingRect(rect);
//                    drawRect(canvas, rect, Color.WHITE, 10);
//                }
            }

            private void drawRect(Canvas canvas, Rect rect, int color, int width) {
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStrokeWidth(width);
                paint.setStyle(Paint.Style.STROKE);

                canvas.drawRect(rect, paint);
            }
        };

        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                String methodName = method.getName();
                if (methodName.equals("dispatchKeyEvent") || methodName.equals("onKeyDown") || methodName.equals("onKeyUp") || methodName.equals("superDispatchKeyEvent") ||
                    methodName.equals("onInterceptTouchEvent") || methodName.equals("dispatchTouchEvent") || methodName.equals("superDispatchTouchEvent"))
                    continue;

                XposedBridge.hookMethod(method, hook);
            }
        }
    }
}
