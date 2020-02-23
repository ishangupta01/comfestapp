package com.comfest.cf18;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by ishan on 2/8/18.
 */

public class TwoThreeImage extends ImageView {
    public TwoThreeImage(Context context) {
        super(context);
    }

    public TwoThreeImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoThreeImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width= getMeasuredWidth();
        int height= (width/3)*2;
        setMeasuredDimension(width,height);
    }
}
