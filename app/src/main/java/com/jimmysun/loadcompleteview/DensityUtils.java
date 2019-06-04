/**
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package com.jimmysun.loadcompleteview;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 屏幕分辨率转换
 *
 * @author SunQiang
 */
public class DensityUtils {

    /**
     * 根据手机的分辨率从 dp 的单位转成为 px(像素)
     *
     * @param context  上下文
     * @param dipValue dip或dp大小
     * @return 像素值
     */
    public static int dip2px(Context context, float dipValue) {
        if (context != null) {
            try {
                return (int) (dipValue * getScreenDensity(context) + 0.5f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位转成为 dp
     *
     * @param context 上下文
     * @param pxValue px大小
     * @return dp值
     */
    public static int px2dip(Context context, float pxValue) {
        if (context != null) {
            return (int) (pxValue / getScreenDensity(context) + 0.5f);
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕密度
     *
     * @param context 上下文
     * @return 屏幕密度
     */
    public static float getScreenDensity(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 根据手机的分辨率从 sp 的单位转成为 px(像素)
     *
     * @param context 上下文
     * @param spValue sp大小
     * @return 像素值
     */
    public static int sp2px(Context context, float spValue) {
        if (context != null) {
            return (int) (spValue * getScaledDensity(context) + 0.5f);
        } else {
            return 0;
        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位转成为 sp
     *
     * @param context 上下文
     * @param pxValue px大小
     * @return sp值
     */
    public static int px2sp(Context context, float pxValue) {
        if (context != null) {
            return (int) (pxValue / getScaledDensity(context) + 0.5f);
        } else {
            return 0;
        }
    }

    public static float getScaledDensity(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }
}
