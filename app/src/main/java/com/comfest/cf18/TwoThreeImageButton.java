package com.comfest.cf18;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by ishan on 8/9/18.
 */

public class TwoThreeImageButton extends ImageButton {
    public TwoThreeImageButton(Context context) {
        super(context);
    }

    public TwoThreeImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoThreeImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
