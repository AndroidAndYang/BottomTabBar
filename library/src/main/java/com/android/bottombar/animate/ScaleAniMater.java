package com.android.bottombar.animate;

import android.view.View;

import com.android.bottombar.listener.Animatable;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：实现缩放的动画类
 */
public class ScaleAniMater implements Animatable {

    @Override
    public void onPressDown(View v, boolean selected) {
        ViewHelper.setScaleX(v,1.1f);
        ViewHelper.setScaleY(v,1.1f);
    }

    @Override
    public void onTouchOut(View v, boolean selected) {
        ViewHelper.setScaleX(v,selected?1.2f:1f);
        ViewHelper.setScaleY(v,selected?1.2f:1f);
    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        AnimatorSet scaleAnimator = new AnimatorSet();
        float end = selected?1.2f:1f;
        ObjectAnimator scaleX ;
        ObjectAnimator scaleY;
        scaleX = ObjectAnimator.ofFloat(v,"scaleX",end);
        scaleY  = ObjectAnimator.ofFloat(v,"scaleY",end);
        scaleAnimator.playTogether(scaleX,scaleY);
        scaleAnimator.setDuration(300);
        scaleAnimator.start();
    }

    @Override
    public void onPageAnimate(View v, float offset){
        ViewHelper.setScaleX(v, offset*0.2f+1f);
        ViewHelper.setScaleY(v, offset*0.2f+1f);
    }

    @Override
    public boolean isNeedPageAnimate() {
        return true;
    }

}