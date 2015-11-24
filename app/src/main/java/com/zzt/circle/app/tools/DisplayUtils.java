package com.zzt.circle.app.tools;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by zzt on 15-6-9.
 */
public class DisplayUtils {
    public static int dp2px(Context context, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));

    }
}
