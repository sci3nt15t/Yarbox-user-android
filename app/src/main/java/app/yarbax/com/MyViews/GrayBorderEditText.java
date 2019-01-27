package app.yarbax.com.MyViews;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import app.yarbax.com.R;

/**
 * Created by shayanrhm on 12/29/18.
 */

public class GrayBorderEditText extends AppCompatEditText {

    public GrayBorderEditText(Context context) {
        super(context);
    }


    public GrayBorderEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GrayBorderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setBackground(getResources().getDrawable(R.drawable.graybordereditttext));
    }
}
