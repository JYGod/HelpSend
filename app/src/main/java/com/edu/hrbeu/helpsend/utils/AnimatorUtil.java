package com.edu.hrbeu.helpsend.utils;

import android.animation.ValueAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

/**
 * Created on 2016/7/14.
 *
 * @author Yan Zhenjie.
 */
public class AnimatorUtil {

    public static  final String TAG="xujun";

    public static final LinearOutSlowInInterpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();

    // 显示view
    public static void scaleShow(View view, ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        view.setVisibility(View.VISIBLE);
        ViewCompat.animate(view)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setDuration(800)
                .setListener(viewPropertyAnimatorListener)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .start();
    }

    // 隐藏view
    public static void scaleHide(View view, ViewPropertyAnimatorListener viewPropertyAnimatorListener) {
        ViewCompat.animate(view)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .alpha(0.0f)
                .setDuration(800)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setListener(viewPropertyAnimatorListener)
                .start();
    }

    public static void tanslation(final View view,float  start,float end){
        final ValueAnimator animator=ValueAnimator.ofFloat(start,end);
        view.setVisibility(View.VISIBLE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) animator.getAnimatedValue();
                Log.i(TAG, "tanslation: value="+value);
                view.setTranslationY(value);
            }
        });
        animator.setDuration(200);
        animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.start();
    }

    public static void showHeight(final View view,float  start,float end){
        final ValueAnimator animator=ValueAnimator.ofFloat(start,end);
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (Float) animator.getAnimatedValue();
                layoutParams.height=(int) value;
                view.setLayoutParams(layoutParams);
                Log.i(TAG, "onAnimationUpdate: value="+value);

            }
        });
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    public static void show(final View view,int  start,int end){
        final int height = view.getHeight();
        final ValueAnimator animator=ValueAnimator.ofInt(start,end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) animator.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate: value="+value);
                view.setTop(value);
                view.setBottom(value+height);
            }
        });
        view.setVisibility(View.VISIBLE);
        animator.setDuration(200);
        animator.setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR);
        animator.start();
    }

}
