package com.comfest.cf18;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by ishan on 14/3/18.
 */

public class SquareImage extends ImageButton {
    public SquareImage(Context context) {
        super(context);
    }

    public SquareImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width= getMeasuredWidth();
        setMeasuredDimension(width,width);
    }
}
