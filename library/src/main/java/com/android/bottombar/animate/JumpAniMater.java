package com.android.bottombar.animate;

import android.view.View;
import android.view.animation.AnticipateInterpolator;

import com.android.bottombar.listener.Animatable;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by jpeng on 16-11-15.
 * 实现跳跃图标的动画类
 */
public class JumpAniMater implements Animatable {

    @Override
    public void onPressDown(View v, boolean selected) {
        ViewHelper.setTranslationY(v,-3f);
    }

    @Override
    public void onTouchOut(View v, boolean selected) {
        ViewHelper.setTranslationY(v,selected?-10f:0f);
    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        int end = selected?-10:0;
        ObjectAnimator jumpAnimator = ObjectAnimator.ofFloat(v,"translationY",end);
        jumpAnimator.setDuration(300);
        jumpAnimator.setInterpolator(new AnticipateInterpolator());
        jumpAnimator.start();
    }

    @Override
    public void onPageAnimate(View v, float offset) {
        ViewHelper.setTranslationY(v, offset * -10);
    }

    @Override
    public boolean isNeedPageAnimate() {
        return true;
    }

}
