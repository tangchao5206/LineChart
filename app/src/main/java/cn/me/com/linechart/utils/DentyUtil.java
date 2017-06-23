package cn.me.com.linechart.utils;

import android.content.Context;

/**
 * 创 建 人: tangchao
 * 创建日期: 2016/12/23 14:19
 * 修改时间：
 * 修改备注：
 */
public class DentyUtil {


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
