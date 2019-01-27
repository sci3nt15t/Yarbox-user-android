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
import android.widget.TextView;

/**
 * Created by shayanrhm on 1/18/19.
 */

public class Faq extends AppCompatActivity{
    float mTouchPosition = 0f;
    float mReleasePosition = 0f;
    public void goback(){
        finish();
    }
    @Override
    protected void onCreate(Bundle SavedInstance)
    {
        super.onCreate(SavedInstance);
        setContentView(R.layout.faq);

        android.support.v7.widget.Toolbar tool = (android.support.v7.widget.Toolbar)findViewById(R.id.my_toolbar);
        tool.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        TextView toolbar_title = (TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(tool);
        tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("clicked!");
                goback();
            }
        });
        toolbar_title.setText("سوالات متداول");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ScrollView scroll = (ScrollView)findViewById(R.id.faq_scroll);
        final ImageView faq_logo = (ImageView)findViewById(R.id.faq_logo);
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
