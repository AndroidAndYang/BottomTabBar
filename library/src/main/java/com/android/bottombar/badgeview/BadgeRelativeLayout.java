/**
 * Copyright 2015 bingoogolapple
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.bottombar.badgeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.android.bottombar.listener.Badgeable;
import com.android.bottombar.listener.DragDismissDelegate;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：气泡
 */
public class BadgeRelativeLayout extends RelativeLayout implements Badgeable {
    private BadgeViewHelper mBadgeViewHelper;

    public BadgeRelativeLayout(Context context)
    {
        this(context, null);
    }

    public BadgeRelativeLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BadgeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mBadgeViewHelper = new BadgeViewHelper(this, context, attrs, BadgeViewHelper.BadgeGravity.RightCenter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mBadgeViewHelper.onTouchEvent(event);
    }

    @Override
    public boolean callSuperOnTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        mBadgeViewHelper.drawBadge(canvas);
    }

    @Override
    public void showCirclePointBadge()
    {
        mBadgeViewHelper.showCirclePointBadge();
    }

    @Override
    public void showTextBadge(String badgeText)
    {
        mBadgeViewHelper.showTextBadge(badgeText);
    }

    @Override
    public void hiddenBadge()
    {
        mBadgeViewHelper.hiddenBadge();
    }

    @Override
    public void showDrawableBadge(Bitmap bitmap)
    {
        mBadgeViewHelper.showDrawable(bitmap);
    }

    @Override
    public void setDragDismissDelegage(DragDismissDelegate delegate)
    {
        mBadgeViewHelper.setDragDismissDelegage(delegate);
    }

    @Override
    public boolean isShowBadge()
    {
        return mBadgeViewHelper.isShowBadge();
    }

    @Override
    public BadgeViewHelper getBadgeViewHelper()
    {
        return mBadgeViewHelper;
    }
}