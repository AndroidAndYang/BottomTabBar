package com.android.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.bottombar.animate.AnimationType;
import com.android.bottombar.animate.FlipAniMater;
import com.android.bottombar.animate.JumpAniMater;
import com.android.bottombar.animate.RotateAniMater;
import com.android.bottombar.animate.Scale2AniMater;
import com.android.bottombar.animate.ScaleAniMater;
import com.android.bottombar.anno.NormalIcons;
import com.android.bottombar.anno.SelectIcons;
import com.android.bottombar.anno.Titles;
import com.android.bottombar.exception.TabException;
import com.android.bottombar.libary.R;
import com.android.bottombar.listener.Animatable;
import com.android.bottombar.listener.BadgeDismissListener;
import com.android.bottombar.listener.OnTabSelectListener;
import com.android.bottombar.util.DensityUtils;

import java.lang.reflect.Field;

/**
 * author:  YJZ
 * date: 2017/12/11
 * description：主要的底部导航操作类,控制导航的行为(显示隐藏徽章等等)
 */

public class BottomTabBar extends LinearLayout implements ViewPager.OnPageChangeListener {
    //默认的图标大小
    private static final int DEFAULT_ICONSIZE = 24;
    //字体默认大小
    private static final int DEFAULT_TEXTSIZE = 14;
    //默认的没有选中的文字和图标的颜色
    private static final int DEFAULT_NORMAL_COLOR = 0xffAEAEAE;
    //默认的上下背景间隔
    private static final int DEFAULT_MARGIN = 8;
    //默认的选中颜色
    private static final int DEFAULT_SELECT_COLOR = 0xff59D9B9;
    //默认是否接受颜色随着字体变化
    private static final boolean DEFAULT_ACEEPTFILTER = true;
    //默认的徽章背景颜色
    private static final int DEFAULT_BADGE_COLOR = 0xffff0000;
    //默认的徽章字体大小
    private static final int DEFAULT_BADGE_TEXTSIZE = 10;
    //默认的徽章狂站距离
    private static final int DEFAULT_PADDING = 4;
    //默认徽章距离右边间距
    private static final int DEFAULT_BADGEHORIZONAL_MARGIN = 20;
    //默认徽章距离上面间距
    private static final int DEFAULT_BADGEVERTICAL_MARGIN = 3;
    //默认中间图标底部距离
    private static final int DEFAULT_MIDDLEICONBOTTOM = 20;
    //默认中间的左右间距
    private static final int DEFAULT_MIDDLEMARGIN = 24;
    private Context mContext;
    private int mLimit = 99;
    private TypedArray mAttribute;
    // 选中的当前Tab的位置
    private int mSelectIndex;
    // 标题的数组
    private String[] mTitles;
    // 没有选中的图标数组
    private int[] mNormalIcons;
    // 选中的图标数组
    private int[] mSelectedIcons;
    //所有Tabitem
    private BottomTabItem[] mBottomTabItems;
    // 中间按钮
    private View mMiddleItem;
    // 监听点击Tab回调的观察者
    private OnTabSelectListener mTabSelectLis;
    //判断是否需要动画,解决Viewpager回调onpageChange冲突事件
    private boolean mNeedAnimate = true;
    // Tab对应的ViewPager
    private ViewPager mTabPager;
    // 渐变判断(用于滑动的渐变)
    private boolean mFilter;
    /**
     * 是否滚动页面的动画
     * 注意:这个变量是全局控制滚动动画是否执行
     * 与Animatable的isNeedPageAnimate不同的地方就是:
     * isNeedPageAnimate控制单一动画是否需要滚动页面动画
     * 一旦为false,所有动画者的滑动页面动画不会调用
     */
    private boolean mNeedScrollAnimate;

    public BottomTabBar(Context context)
    {
        super(context);
        init(context, null);
    }

    public BottomTabBar(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化TabBar
     *
     * @param context 上下文
     * @param set     XML结点集合
     */
    private void init(Context context, AttributeSet set)
    {
        mContext = context;
        mAttribute = context.obtainStyledAttributes(set, R.styleable.BottomTabBar);
        setMinimumHeight(DensityUtils.dp2px(mContext, 48));
        boolean haveAnno = reflectAnnotation();
        if (haveAnno)
        {
            initFromAttribute();
        }
    }

    /**
     * 从类获取注解,映射值到mTiles,mNormalIcons,mSelectedIcons
     *
     * @return boolean 表示是否有注解的存在
     */
    private boolean reflectAnnotation()
    {
        int total = 0;//表示获得注解的总数
        //反射注解
        Field[] fields = mContext.getClass().getDeclaredFields();
        //遍历所有字段,寻找标记
        for (Field field : fields)
        {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Titles.class))
            {
                try
                {
                    if (field.get(mContext).getClass().equals(String[].class))
                    {
                        mTitles = (String[]) field.get(mContext);
                    } else if (field.get(mContext).getClass().equals(int[].class))
                    {
                        int[] title_Res = (int[]) field.get(mContext);
                        mTitles = new String[title_Res.length];
                        for (int i = 0; i < title_Res.length; i++)
                        {
                            mTitles[i] = mContext.getString(title_Res[i]);
                        }
                    }
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                if (mTitles != null) total++;
            } else if (field.isAnnotationPresent(NormalIcons.class))
            {
                try
                {
                    mNormalIcons = (int[]) field.get(mContext);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                if (mNormalIcons != null) total++;
            } else if (field.isAnnotationPresent(SelectIcons.class))
            {
                try
                {
                    mSelectedIcons = (int[]) field.get(mContext);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                if (mSelectedIcons != null) total++;
            }
        }
        return total > 0;
    }


    /**
     * 获取所有节点属性
     */
    private void initFromAttribute()
    {
        int normalColor = mAttribute.getColor(R.styleable.BottomTabBar_TabNormalColor, DEFAULT_NORMAL_COLOR);
        int selectColor = mAttribute.getColor(R.styleable.BottomTabBar_TabSelectColor, DEFAULT_SELECT_COLOR);
        int textSize = DensityUtils.px2sp(mContext, mAttribute.getDimensionPixelSize(R.styleable.BottomTabBar_TabTextSize, DensityUtils.sp2px(mContext, DEFAULT_TEXTSIZE)));
        int iconSize = mAttribute.getDimensionPixelSize(R.styleable.BottomTabBar_TabIconSize, DensityUtils.dp2px(mContext, DEFAULT_ICONSIZE));
        int margin = mAttribute.getDimensionPixelOffset(R.styleable.BottomTabBar_TabMargin, DensityUtils.dp2px(mContext, DEFAULT_MARGIN));
        AnimationType AnimateType = AnimationType.values()[mAttribute.getInt(R.styleable.BottomTabBar_TabAnimate, AnimationType.NONE.ordinal())];
        int BadgeColor = mAttribute.getColor(R.styleable.BottomTabBar_BadgeColor, DEFAULT_BADGE_COLOR);
        int BadgetextSize = DensityUtils.px2sp(mContext, mAttribute.getDimensionPixelSize(R.styleable.BottomTabBar_BadgeTextSize, DensityUtils.sp2px(mContext, DEFAULT_BADGE_TEXTSIZE)));
        int badgePadding = DensityUtils.px2dp(mContext, mAttribute.getDimensionPixelOffset(R.styleable.BottomTabBar_BadgePadding, DensityUtils.dp2px(mContext, DEFAULT_PADDING)));
        int badgeVerMargin = DensityUtils.px2dp(mContext, mAttribute.getDimensionPixelOffset(R.styleable.BottomTabBar_BadgeVerticalMargin, DensityUtils.dp2px(mContext, DEFAULT_BADGEVERTICAL_MARGIN)));
        int badgeHorMargin = DensityUtils.px2dp(mContext, mAttribute.getDimensionPixelOffset(R.styleable.BottomTabBar_BadgeHorizonalMargin, DensityUtils.dp2px(mContext, DEFAULT_BADGEHORIZONAL_MARGIN)));
        int hMargin = mAttribute.getDimensionPixelOffset(R.styleable.BottomTabBar_TabMiddleHMargin, DensityUtils.dp2px(mContext, DEFAULT_MIDDLEMARGIN));
        boolean acceptFilter = mAttribute.getBoolean(R.styleable.BottomTabBar_TabIconFilter, DEFAULT_ACEEPTFILTER);
        Drawable tabselectbg = mAttribute.getDrawable(R.styleable.BottomTabBar_TabSelectBg);
        //假如所有都为空默认已经开启了
        if (!isInEditMode())
        {
            CheckIfAssertError(mTitles, mNormalIcons, mSelectedIcons);
            //计算Tab的宽度
            mBottomTabItems = new BottomTabItem[mNormalIcons.length];
            //实例化TabItem添加进去
            for (int i = 0; i < mBottomTabItems.length; i++)
            {
                final int temp = i;
                Animatable animater = AnimateType == AnimationType.SCALE ? new ScaleAniMater() : AnimateType == AnimationType.ROTATE ? new RotateAniMater() :
                        AnimateType == AnimationType.FLIP ? new FlipAniMater() : AnimateType == AnimationType.JUMP ? new JumpAniMater() : AnimateType == AnimationType.SCALE2 ? new Scale2AniMater() : null;
                mBottomTabItems[i] = new BottomTabItem.Builder(mContext).setTitle(mTitles == null ? null : mTitles[i]).setIndex(temp).setTextSize(textSize)
                        .setNormalColor(normalColor).setSelectBg(tabselectbg).setBadgeColor(BadgeColor)
                        .setBadgeTextSize(BadgetextSize).setNormalIcon(mNormalIcons[i])
                        .setSelectedColor(selectColor).setBadgeHorMargin(badgeHorMargin)
                        .setBadgePadding(badgePadding).setIconSize(iconSize).setIconFilte(acceptFilter)
                        .setBadgeVerMargin(badgeVerMargin).setMargin(margin).setAnimater(animater)
                        .setSelectIcon(mSelectedIcons == null ? 0 : mSelectedIcons[i]).build();
                mBottomTabItems[i].setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (v == mBottomTabItems[mSelectIndex])
                        {
                            return false;
                        }
                        switch (event.getAction())
                        {
                            case MotionEvent.ACTION_DOWN:
                                if (mBottomTabItems[mSelectIndex].getAnimater() != null)
                                {
                                    mBottomTabItems[mSelectIndex].getAnimater().onPressDown(mBottomTabItems[mSelectIndex].getIconView(), true);
                                    ((BottomTabItem) v).getAnimater().onPressDown(((BottomTabItem) v).getIconView(), false);
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                if (isInRect(v, event))
                                {
                                    if (mTabPager != null && mTabPager.getAdapter() != null && mTabPager.getAdapter().getCount() >= mBottomTabItems.length)
                                    {
                                        mNeedAnimate = true;
                                        mTabPager.setCurrentItem(temp, false);
                                    } else if (mTabPager != null && mTabPager.getAdapter() != null && mTabPager.getAdapter().getCount() <= mBottomTabItems.length)
                                    {
                                        mNeedAnimate = true;
                                        mTabPager.setCurrentItem(temp, false);
                                        setSelectTab(temp);
                                    } else
                                    {
                                        setSelectTab(temp, true);
                                    }
                                } else
                                {
                                    if (mBottomTabItems[mSelectIndex].getAnimater() != null)
                                    {
                                        mBottomTabItems[mSelectIndex].getAnimater().onTouchOut(mBottomTabItems[mSelectIndex].getIconView(), true);
                                        ((BottomTabItem) v).getAnimater().onTouchOut(((BottomTabItem) v).getIconView(), false);
                                    }
                                }
                                break;

                        }
                        return true;
                    }
                });
                addView(mBottomTabItems[i]);
                //判断是不是准备到中间的tab,假如设置了中间图标就添加进去
                if (i == (mBottomTabItems.length / 2 - 1) && mAttribute.getResourceId(R.styleable.BottomTabBar_TabMiddleView, 0) != 0)
                {
                    //添加中间的占位距离控件
                    View stement_view = new View(mContext);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(hMargin, ViewGroup.LayoutParams.MATCH_PARENT);
                    stement_view.setLayoutParams(params);
                    addView(stement_view);
                }
            }

            mBottomTabItems[0].setSelect(true, true, false);
            for (int i = 1; i < mBottomTabItems.length; i++)
            {
                mBottomTabItems[i].setSelect(false, false);
            }

        }
    }

    /**
     * 切换Tab页面,是否带动画
     */
    private void setSelectTab(int index, boolean animated)
    {
        if (mBottomTabItems == null || index > mBottomTabItems.length - 1) return;
        mSelectIndex = index;
        for (int i = 0; i < mBottomTabItems.length; i++)
        {
            if (i == index)
            {
                continue;
            }
            if (!mBottomTabItems[i].isSelect())
            {
                mBottomTabItems[i].setSelect(false, animated);
            } else
            {
                mBottomTabItems[i].setSelect(false, animated);
            }
        }
        mBottomTabItems[index].setSelect(true, animated);
        if (mTabSelectLis != null)
        {
            mTabSelectLis.onTabSelect(index);
        }
    }

    /**
     * 判断有没有声明变量的错误
     *
     * @param titles       标题
     * @param normalIcon   没有选中的图标
     * @param selectedIcon 选中的图标
     */
    private void CheckIfAssertError(String[] titles, int[] normalIcon, int[] selectedIcon)
    {
        if (normalIcon == null)
        {
            throw new TabException("you must set the NormalIcon for the JPTabbar!!!");
        }
        int originlen = normalIcon.length;
        //判断注解里面的数组长度是否一样
        if ((mTitles != null && originlen != titles.length)
                || (selectedIcon != null && originlen != selectedIcon.length))
        {
            throw new TabException("Every Array length is not equal,Please Check Your Annotation in your Activity,Ensure Every Array length is equals!");
        }
    }


    private void BuildMiddleView()
    {
        int layout_res = mAttribute.getResourceId(R.styleable.BottomTabBar_TabMiddleView, 0);
        if (layout_res == 0)
        {
            return;
        }
        mMiddleItem = LayoutInflater.from(mContext).inflate(layout_res, (ViewGroup) getParent(), false);
        //给中间自定义View填充额外参数,令布局在父View的中间和最下边的位置(父View指的是TabBar的父控件)
        fillMiddleParams();
    }

    //判断触摸位置是否在View的区域
    private boolean isInRect(View v, MotionEvent e)
    {
        float x = e.getRawX();
        float y = e.getRawY();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        RectF rectF = new RectF(location[0], location[1], location[0] + v.getWidth(), location[1] + v.getHeight());
        return rectF.contains(x, y);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (mMiddleItem == null)
        {
            BuildMiddleView();
        }
        mAttribute.recycle();
    }

    private void fillMiddleParams()
    {
        int bottom_dis = mAttribute.getDimensionPixelSize(R.styleable.BottomTabBar_TabMiddleBottomDis, DensityUtils.dp2px(mContext, DEFAULT_MIDDLEICONBOTTOM));
        if (getParent().getClass().equals(RelativeLayout.class))
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMiddleItem.getLayoutParams();
            params.setMargins(0, 0, 0, bottom_dis);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mMiddleItem.setLayoutParams(params);
        } else if (getParent().getClass().equals(FrameLayout.class))
        {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mMiddleItem.getLayoutParams();
            params.setMargins(0, 0, 0, bottom_dis);
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            mMiddleItem.setLayoutParams(params);
        }
        ((ViewGroup) getParent()).addView(mMiddleItem);
    }

    /****-------提供给开发者调用的方法---------****/

    /**
     * 切换Tab页面
     */
    public void setSelectTab(int index)
    {
        setSelectTab(index, true);
    }

    /**
     * 得到TabItem的数量
     */
    public int getTabsCount()
    {
        return mBottomTabItems == null ? 0 : mBottomTabItems.length;
    }

    /**
     * 设置容器和TabBar联系在一起
     */
    public void setContainer(ViewPager pager)
    {
        if (pager != null)
        {
            mTabPager = pager;
            mTabPager.setOnPageChangeListener(this);
        }
    }

    /**
     * 设置Badge消息数量最大限制
     */
    public void setCountLimit(int limit)
    {
        mLimit = limit;

    }

    /**
     * 设置图标和标题的滑动渐变以及点击渐变是否使用
     */
    public BottomTabBar setUseFilter(boolean filter)
    {
        this.mFilter = filter;
        return this;
    }

    /**
     * 设置是否需要页面滚动动画
     */
    public BottomTabBar setUseScrollAnimate(boolean scrollAnimate)
    {
        this.mNeedScrollAnimate = scrollAnimate;
        return this;
    }

    /**
     * 显示图标的Badge，默认不可拖动
     */
    public void showBadge(int pos, String text)
    {
        showBadge(pos, text, false);
    }

    /**
     * 显示图标的Badge
     */
    public void showBadge(int pos, String text, boolean draggable)
    {
        if (mBottomTabItems != null)
        {
            mBottomTabItems[pos].showTextBadge(text);
            mBottomTabItems[pos].getBadgeViewHelper().setDragable(draggable);
        }
    }

    /**
     * 显示圆点徽章,默认为不可拖动
     */
    public void showCircleBadge(int pos)
    {
        showCircleBadge(pos, false);
    }

    /**
     * 显示圆点徽章,是否可以拖动
     */
    public void showCircleBadge(int pos, boolean draggable)
    {
        if (mBottomTabItems != null)
        {
            mBottomTabItems[pos].showCirclePointBadge();
            mBottomTabItems[pos].getBadgeViewHelper().setDragable(draggable);
        }
    }

    /**
     * 重载方法
     * 设置徽章,传入int,,默认为不可拖动
     */
    public void showBadge(int pos, int count)
    {
        showBadge(pos, count, false);
    }

    /**
     * 重载方法
     * 设置徽章,传入int,是否可拖动
     */
    public void showBadge(int pos, int count, boolean draggable)
    {
        if (mBottomTabItems == null || mBottomTabItems[pos] == null) return;
        mBottomTabItems[pos].getBadgeViewHelper().setDragable(draggable);
        if (count == 0)
        {
            mBottomTabItems[pos].hiddenBadge();
        } else if (count > mLimit)
        {
            mBottomTabItems[pos].showTextBadge(mLimit + "+");
        } else
        {
            mBottomTabItems[pos].showTextBadge(count + "");
        }
    }


    /**
     * 隐藏徽章
     *
     * @param position
     */
    public void hideBadge(int position)
    {
        if (mBottomTabItems != null)
            mBottomTabItems[position].hiddenBadge();
    }

    /**
     * 设置标题数组
     *
     * @param titles
     */
    public BottomTabBar setTitles(String... titles)
    {
        this.mTitles = titles;
        return this;
    }

    public BottomTabBar setTitles(int... titles)
    {
        if (titles != null && titles.length > 0)
        {
            mTitles = new String[titles.length];
            for (int i = 0; i < titles.length; i++)
            {
                mTitles[i] = mContext.getString(titles[i]);
            }
        }
        return this;
    }

    /**
     * 设置为选中的图标数组
     */
    public BottomTabBar setNormalIcons(int... normalIcons)
    {
        this.mNormalIcons = normalIcons;
        return this;
    }

    /**
     * 设置选中图标
     */
    public BottomTabBar setSelectedIcons(int... selectedIcons)
    {
        this.mSelectedIcons = selectedIcons;
        return this;
    }

    /**
     * 生成TabItem
     */
    public void generate()
    {
        if (mBottomTabItems == null)
            initFromAttribute();
    }


    /**
     * 获得选中的位置
     */
    public int getSelectPosition()
    {
        return mSelectIndex;
    }

    /**
     * 设置动画
     */
    public void setAnimation(AnimationType animationType)
    {
        for (int i = 0; i < mBottomTabItems.length; i++)
        {
            mBottomTabItems[i].setAnimater(animationType == AnimationType.SCALE ? new ScaleAniMater() : animationType == AnimationType.ROTATE ? new RotateAniMater() :
                    animationType == AnimationType.JUMP ? new JumpAniMater() : animationType == AnimationType.FLIP ? new FlipAniMater() : animationType == AnimationType.SCALE2 ? new Scale2AniMater() : null);
        }
    }

    /**
     * 获取徽章是否在显示
     */
    public boolean isBadgeShow(int index)
    {
        if (mBottomTabItems != null)
            return mBottomTabItems[index].isBadgeShow();
        return false;
    }

    /**
     * 获得中间的TABItem
     *
     * @return
     */
    public View getMiddleView()
    {
        //解决开发者在activity引用了注解并且在oncreate方法调用这个方法空指针的问题
        if (mMiddleItem == null)
        {
            BuildMiddleView();
        }
        return mMiddleItem;
    }

    /**
     * 改变图标大小
     */
    public void setIconSize(int size)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.getIconView().getLayoutParams().width = DensityUtils.dp2px(mContext, size);
                item.getIconView().getLayoutParams().height = DensityUtils.dp2px(mContext, size);
            }
        }
    }

    /**
     * 改变TabBar上边距
     */
    public void setTabMargin(int margin)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                ((RelativeLayout.LayoutParams) item.getIconView().getLayoutParams()).topMargin = DensityUtils.dp2px(mContext, margin);
            }
        }
    }

    /**
     * 改变普通颜色(包括字体和图标)
     */
    public void setNormalColor(@ColorInt int color)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.setNormalColor(color);
            }
        }
    }

    /**
     * 改变选中颜色(包括字体和图标)
     */
    public void setSelectedColor(@ColorInt int color)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.setSelectedColor(color);
            }
        }
    }

    /**
     * 改变文字大小
     */
    public void setTabTextSize(int textSize)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.setTextSize(DensityUtils.sp2px(mContext, textSize));
            }
        }
    }

    public void setBadgeColor(@ColorInt int badgeColor)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.getBadgeViewHelper().setBadgeBgColorInt(badgeColor);
            }
        }
    }

    public void setBadgeHorMargin(@ColorInt int horMargin)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.getBadgeViewHelper().setBadgeHorizontalMarginDp(horMargin);
            }
        }
    }

    public void setBadgeTextSize(@ColorInt int badgeTextSize)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.getBadgeViewHelper().setBadgeTextSizeSp(badgeTextSize);
            }
        }
    }

    public void setBadgePadding(@ColorInt int padding)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.getBadgeViewHelper().setBadgePaddingDp(padding);
            }
        }
    }

    public void setBadgeVerMargin(@ColorInt int verMargin)
    {
        if (mBottomTabItems != null)
        {
            for (BottomTabItem item : mBottomTabItems)
            {
                item.getBadgeViewHelper().setBadgeVerticalMarginDp(verMargin);
            }
        }
    }


    /**
     * 设置点击TabBar事件的观察者
     */
    public void setTabListener(OnTabSelectListener listener)
    {
        mTabSelectLis = listener;
    }

    /**
     * 设置badgeView消失的回调事件
     */
    public void setDismissListener(BadgeDismissListener listener)
    {
        if (mBottomTabItems != null)
            for (BottomTabItem item : mBottomTabItems)
            {
                item.setDismissDelegate(listener);
            }
    }

    /****---------------****/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        if (mBottomTabItems == null || position > mBottomTabItems.length - 1 || 1 + position > mBottomTabItems.length - 1) return;
        if (positionOffset > 0f)
        {
            if (mFilter)
            {
                mBottomTabItems[position].changeAlpha(1 - positionOffset);
                mBottomTabItems[position + 1].changeAlpha(positionOffset);
            }

            if (mBottomTabItems[position].getAnimater() != null && mNeedScrollAnimate)
            {
                if (mBottomTabItems[position].getAnimater().isNeedPageAnimate())
                {
                    mNeedAnimate = false;
                    mBottomTabItems[position].getAnimater().onPageAnimate(mBottomTabItems[position].getIconView(), 1 - positionOffset);
                    mBottomTabItems[position + 1].getAnimater().onPageAnimate(mBottomTabItems[position + 1].getIconView(), positionOffset);
                } else
                {
                    mNeedAnimate = true;
                }
            } else mNeedAnimate = true;
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        setSelectTab(position, mNeedAnimate);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
        if (state == 1)
        {
            mNeedAnimate = false;
        }
    }

}

