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
public class Scale2AniMater implements Animatable {

    @Override
    public void onPressDown(View v, boolean selected) {
        if(!selected) {
            ViewHelper.setScaleX(v,0.75f);
            ViewHelper.setScaleY(v,0.75f);
        }
    }

    @Override
    public void onTouchOut(View v, boolean selected) {
        if(!selected) {
            ViewHelper.setScaleX(v,1f);
            ViewHelper.setScaleY(v,1f);
        }
    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        if(!selected)return;
        AnimatorSet set= new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v,"scaleX",0.75f,1.3f,1f,1.2f,1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v,"scaleY",0.75f,1.3f,1f,1.2f,1f);
        set.playTogether(animator1,animator2);
        set.setDuration(800);
        set.start();

    }

    @Override
    public void onPageAnimate(View v, float offset) {

    }

    @Override
    public boolean isNeedPageAnimate() {
        return false;
    }
}
