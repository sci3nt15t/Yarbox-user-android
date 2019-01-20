package app.yarbax.com;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ScrollView;

/**
 * Created by shayanrhm on 1/18/19.
 */

public class Faq extends AppCompatActivity{
    float mTouchPosition = 0f;
    float mReleasePosition = 0f;
    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.faq);
        ScrollView scroll = (ScrollView)findViewById(R.id.faq_scroll);
        ImageView faq_logo = (ImageView)findViewById(R.id.faq_logo);
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                    ObjectAnimator hide = ObjectAnimator.ofFloat(faq_logo, "alpha", 1f, 0f);
                    hide.setDuration(2000);

                    ObjectAnimator show = ObjectAnimator.ofFloat(faq_logo, "alpha", 0f, 1f);
                    show.setDuration(2000);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mTouchPosition = event.getY();
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mReleasePosition = event.getY();
                    }
                    final AnimatorSet mAnimationSet = new AnimatorSet();
                    if (mTouchPosition - mReleasePosition > 0) {
                        if (faq_logo.getAlpha() != 0) {
                            mAnimationSet.play(hide);
                            mAnimationSet.start();
                        }
                    } else {
                        if (faq_logo.getAlpha() != 1) {
                            mAnimationSet.play(show);
                            mAnimationSet.start();
                        }
                    }
                return false;
            }
        });
    }
}
