package cn.cangjie.uikit.nine;

import android.content.Context;

public class DensityUtil {
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
