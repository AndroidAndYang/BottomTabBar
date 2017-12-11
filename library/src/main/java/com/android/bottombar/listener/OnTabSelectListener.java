package com.android.bottombar.listener;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：点击回调监听
 */

public interface OnTabSelectListener {
    /**
     *  用户每次点击不同的Tab将会触发这个方法
     * @param index
     * 当前选择的TAB的索引值
     */
    void onTabSelect(int index);

}
