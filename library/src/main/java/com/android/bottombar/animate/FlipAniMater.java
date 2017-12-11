package com.android.bottombar.animate;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.android.bottombar.listener.Animatable;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：实现翻转的动画类
 */

public class FlipAniMater implements Animatable {

    @Override
    public void onPressDown(View v, boolean selected)
    {
        ViewHelper.setRotationY(v, selected ? 54f : 126f);
    }

    @Override
    public void onTouchOut(View v, boolean selected)
    {
        ViewHelper.setRotationY(v, selected ? 180f : 0f);
    }

    @Override
    public void onSelectChanged(View v, boolean selected)
    {
        float end = selected ? 180f : 0f;
        ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(v, "rotationY", end);
        flipAnimator.setDuration(400);
        flipAnimator.setInterpolator(new DecelerateInterpolator());
        flipAnimator.start();
    }

    @Override
    public void onPageAnimate(View v, float offset)
    {
        ViewHelper.setRotationY(v, 180 * offset);
    }

    @Override
    public boolean isNeedPageAnimate()
    {
        return true;
    }

}
