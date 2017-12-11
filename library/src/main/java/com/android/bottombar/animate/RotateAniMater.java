package com.android.bottombar.animate;

import android.view.View;
import android.view.animation.AnticipateInterpolator;

import com.android.bottombar.listener.Animatable;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：实现旋转的动画类
 */

public class RotateAniMater implements Animatable {

    @Override
    public void onPressDown(View v, boolean selected) {

    }

    @Override
    public void onTouchOut(View v, boolean selected) {

    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        int end = selected ? 360 : 0;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(v, "rotation",  end);
        rotateAnimator.setDuration(400);
        rotateAnimator.setInterpolator(new AnticipateInterpolator());
        rotateAnimator.start();
    }

    @Override
    public void onPageAnimate(View v, float offset) {
        ViewHelper.setRotation(v, offset * 360);
    }

    @Override
    public boolean isNeedPageAnimate() {
        return true;
    }

}
