package com.android.bottombar.listener;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：徽章消失回调监听者
 */
public interface BadgeDismissListener {
    /**
     * TabItem徽章消失的回调
     */
    void onDismiss(int position);
}
