package com.comfest.cf18;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BackdropImage extends ImageView {

    public BackdropImage(Context context) {
        super(context);
    }

    public BackdropImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackdropImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width= getMeasuredWidth();
        int height= Resources.getSystem().getDisplayMetrics().heightPixels;
        setMeasuredDimension(width,height);
    }
}
