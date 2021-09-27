package cn.cangjie.uikit.titlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.cangjie.uikit.R;
import cn.cangjie.uikit.titlebar.utils.DrawableUtil;
import cn.cangjie.uikit.titlebar.utils.NotchUtil;
import cn.cangjie.uikit.titlebar.utils.ResourceUtil;
import cn.cangjie.uikit.titlebar.utils.StatusBarUtil;
import cn.cangjie.uikit.titlebar.utils.ViewGroupUtils;
import cn.cangjie.uikit.titlebar.widget.AlphaImageView;
import cn.cangjie.uikit.titlebar.widget.AlphaTextView;

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/16 15:00
 */
public class TitleBar extends ViewGroup {

    /**
     * 默认透明度--5.0以上优化半透明状态栏一致
     */
    public static final int DEFAULT_STATUS_BAR_ALPHA = 102;
    /**
     * 默认文本颜色
     */
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    /**
     * 主标题size dp
     */
    private static final float DEFAULT_MAIN_TEXT_SIZE = 18;
    /**
     * 文本默认size dp
     */
    private static final float DEFAULT_TEXT_SIZE = 14;
    /**
     * 副标题默认size dp
     */
    private static final float DEFAULT_SUB_TEXT_SIZE = 12;
    /**
     * 左右padding dp--ToolBar默认16dp
     */
    private static final float DEFAULT_OUT_PADDING = 12;
    /**
     * 左右padding dp--ToolBar默认32dp
     */
    private static final float DEFAULT_CENTER_GRAVITY_LEFT_PADDING = 24;
    /**
     * 状态栏高度
     */
    private int mStatusBarHeight;
    /**
     * TitleBarView实际占用宽度
     */
    private int mScreenWidth;

    private Context mContext;
    /**
     * 自定义View-状态栏View-用于单独设置颜色
     */
    private View mVStatus;
    /**
     * 左边容器
     */
    private LinearLayout mLLayoutLeft;
    /**
     * 中间容器
     */
    private LinearLayout mLLayoutCenter;
    /**
     * 右边容器
     */
    private LinearLayout mLLayoutRight;
    /**
     * 左边TextView
     */
    private AlphaTextView mTvLeft;
    /**
     * 主标题
     */
    private TextView mTvTitleMain;
    /**
     * 副标题
     */
    private TextView mTvTitleSub;
    /**
     * 右边TextView
     */
    private AlphaTextView mTvRight;
    /**
     * 下方下划线
     */
    private View mVDivider;

    /**
     * 是否增加状态栏高度
     */
    private boolean mStatusBarPlusEnable = true;
    /**
     * 设置状态栏浅色或深色模式类型标记;>0则表示支持模式切换
     */
    private int mStatusBarModeType = StatusBarUtil.STATUS_BAR_TYPE_DEFAULT;
    /**
     * xml属性
     */
    private boolean mImmersible = false;
    private int mOutPadding;
    private int mActionPadding;
    /**
     * 中间部分是Layout左右padding
     */
    private int mCenterLayoutPadding;
    /**
     * 中间部分是否左对齐--默认居中
     */
    private boolean mCenterGravityLeft = false;
    /**
     * 中间部分左对齐是Layout左padding
     */
    private int mCenterGravityLeftPadding;
    /**
     * 是否浅色状态栏(黑色文字及图标)
     */
    private boolean mStatusBarLightMode = false;
    private float mViewPressedAlpha;

    private Drawable mStatusBackground;
    private Drawable mDividerBackground;
    private int mDividerHeight;
    private boolean mDividerVisible;

    private CharSequence mLeftText;
    private int mLeftTextSize;
    private ColorStateList mLeftTextColor;
    private Drawable mLeftTextBackground;
    private Drawable mLeftTextDrawable;
    private ColorStateList mLeftTextDrawableTint;
    private PorterDuff.Mode mLeftTextDrawableTintMode;
    private int mLeftTextDrawableWidth;
    private int mLeftTextDrawableHeight;
    private int mLeftTextDrawablePadding;

    private CharSequence mTitleMainText;
    private int mTitleMainTextSize;
    private ColorStateList mTitleMainTextColor;
    private Drawable mTitleMainTextBackground;
    private boolean mTitleMainTextFakeBold;
    private boolean mTitleMainTextMarquee;

    private CharSequence mTitleSubText;
    private int mTitleSubTextSize;
    private ColorStateList mTitleSubTextColor;
    private Drawable mTitleSubTextBackground;
    private boolean mTitleSubTextFakeBold;
    private boolean mTitleSubTextMarquee;

    private CharSequence mRightText;
    private int mRightTextSize;
    private ColorStateList mRightTextColor;
    private Drawable mRightTextBackground;
    private Drawable mRightTextDrawable;
    private ColorStateList mRightTextDrawableTint;
    private PorterDuff.Mode mRightTextDrawableTintMode;
    private int mRightTextDrawableWidth;
    private int mRightTextDrawableHeight;
    private int mRightTextDrawablePadding;


    private int mActionTextSize;
    private ColorStateList mActionTextColor;
    private Drawable mActionTextBackground;
    private ColorStateList mActionTint;
    private PorterDuff.Mode mActionTintMode;
    private Rect mTitleContainerRect;
    private ResourceUtil mResourceUtil;

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mResourceUtil = new ResourceUtil(mContext);
        initAttributes(context, attrs);
        initView(context);
        setViewAttributes(context);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        mImmersible = ta.getBoolean(R.styleable.TitleBar_title_immersible, true);
        mStatusBarPlusEnable = ta.getBoolean(R.styleable.TitleBar_title_statusBarPlusEnable, true);
        mOutPadding = ta.getDimensionPixelSize(R.styleable.TitleBar_title_outPadding, dip2px(DEFAULT_OUT_PADDING));
        mActionPadding = ta.getDimensionPixelSize(R.styleable.TitleBar_title_actionPadding, dip2px(2));
        mCenterLayoutPadding = ta.getDimensionPixelSize(R.styleable.TitleBar_title_centerLayoutPadding, dip2px(2));
        mCenterGravityLeft = ta.getBoolean(R.styleable.TitleBar_title_centerGravityLeft, false);
        mCenterGravityLeftPadding = ta.getDimensionPixelSize(R.styleable.TitleBar_title_centerGravityLeftPadding, dip2px(DEFAULT_CENTER_GRAVITY_LEFT_PADDING));
        mStatusBarLightMode = ta.getBoolean(R.styleable.TitleBar_title_statusBarLightMode, false);
        mViewPressedAlpha = ta.getFloat(R.styleable.TitleBar_title_viewPressedAlpha, mResourceUtil.getAttrFloat(R.attr.pressedAlpha));

        mStatusBackground = ta.getDrawable(R.styleable.TitleBar_title_statusBackground);
        mDividerBackground = ta.getDrawable(R.styleable.TitleBar_title_dividerBackground);
        mDividerHeight = ta.getDimensionPixelSize(R.styleable.TitleBar_title_dividerHeight, dip2px(0.5f));
        mDividerVisible = ta.getBoolean(R.styleable.TitleBar_title_dividerVisible, true);

        mLeftText = ta.getString(R.styleable.TitleBar_title_leftText);
        mLeftTextSize = ta.getDimensionPixelSize(R.styleable.TitleBar_title_leftTextSize, dip2px(DEFAULT_TEXT_SIZE));
        mLeftTextColor = ta.getColorStateList(R.styleable.TitleBar_title_leftTextColor);
        mLeftTextBackground = ta.getDrawable(R.styleable.TitleBar_title_leftTextBackground);
        mLeftTextDrawable = ta.getDrawable(R.styleable.TitleBar_title_leftTextDrawable);
        mLeftTextDrawableTint = ta.getColorStateList(R.styleable.TitleBar_title_leftTextDrawableTint);
        mLeftTextDrawableTintMode = parseTintMode(ta.getInt(R.styleable.TitleBar_title_leftTextDrawableTintMode, -1), mLeftTextDrawableTintMode);
        mLeftTextDrawableWidth = ta.getDimensionPixelSize(R.styleable.TitleBar_title_leftTextDrawableWidth, -1);
        mLeftTextDrawableHeight = ta.getDimensionPixelSize(R.styleable.TitleBar_title_leftTextDrawableHeight, -1);
        mLeftTextDrawablePadding = ta.getDimensionPixelSize(R.styleable.TitleBar_title_leftTextDrawablePadding, dip2px(1));

        mTitleMainText = ta.getString(R.styleable.TitleBar_title_titleMainText);
        mTitleMainTextSize = ta.getDimensionPixelSize(R.styleable.TitleBar_title_titleMainTextSize, dip2px(DEFAULT_MAIN_TEXT_SIZE));
        mTitleMainTextColor = ta.getColorStateList(R.styleable.TitleBar_title_titleMainTextColor);
        mTitleMainTextBackground = ta.getDrawable(R.styleable.TitleBar_title_titleMainTextBackground);
        mTitleMainTextFakeBold = ta.getBoolean(R.styleable.TitleBar_title_titleMainTextFakeBold, false);
        mTitleMainTextMarquee = ta.getBoolean(R.styleable.TitleBar_title_titleMainTextMarquee, false);

        mTitleSubText = ta.getString(R.styleable.TitleBar_title_titleSubText);
        mTitleSubTextSize = ta.getDimensionPixelSize(R.styleable.TitleBar_title_titleSubTextSize, dip2px(DEFAULT_SUB_TEXT_SIZE));
        mTitleSubTextColor = ta.getColorStateList(R.styleable.TitleBar_title_titleSubTextColor);
        mTitleSubTextBackground = ta.getDrawable(R.styleable.TitleBar_title_titleSubTextBackground);
        mTitleSubTextFakeBold = ta.getBoolean(R.styleable.TitleBar_title_titleSubTextFakeBold, false);
        mTitleSubTextMarquee = ta.getBoolean(R.styleable.TitleBar_title_titleSubTextMarquee, false);

        mRightText = ta.getString(R.styleable.TitleBar_title_rightText);
        mRightTextSize = ta.getDimensionPixelSize(R.styleable.TitleBar_title_rightTextSize, dip2px(DEFAULT_TEXT_SIZE));
        mRightTextColor = ta.getColorStateList(R.styleable.TitleBar_title_rightTextColor);
        mRightTextBackground = ta.getDrawable(R.styleable.TitleBar_title_rightTextBackground);
        mRightTextDrawable = ta.getDrawable(R.styleable.TitleBar_title_rightTextDrawable);
        mRightTextDrawableTint = ta.getColorStateList(R.styleable.TitleBar_title_rightTextDrawableTint);
        mRightTextDrawableTintMode = parseTintMode(ta.getInt(R.styleable.TitleBar_title_rightTextDrawableTintMode, -1), mRightTextDrawableTintMode);
        mRightTextDrawableWidth = ta.getDimensionPixelSize(R.styleable.TitleBar_title_rightTextDrawableWidth, -1);
        mRightTextDrawableHeight = ta.getDimensionPixelSize(R.styleable.TitleBar_title_rightTextDrawableHeight, -1);
        mRightTextDrawablePadding = ta.getDimensionPixelSize(R.styleable.TitleBar_title_rightTextDrawablePadding, dip2px(1));

        mActionTextSize = ta.getDimensionPixelSize(R.styleable.TitleBar_title_actionTextSize, dip2px(DEFAULT_TEXT_SIZE));
        mActionTextColor = ta.getColorStateList(R.styleable.TitleBar_title_actionTextColor);
        mActionTextBackground = ta.getDrawable(R.styleable.TitleBar_title_actionTextBackground);
        mActionTint = ta.getColorStateList(R.styleable.TitleBar_title_actionTint);
        mActionTintMode = parseTintMode(ta.getInt(R.styleable.TitleBar_title_actionTintMode, -1), mActionTintMode);
        ta.recycle();//回收
    }

    public PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            case 16:
                return PorterDuff.Mode.ADD;
            default:
                return defaultMode;
        }
    }

    /**
     * 初始化子View
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LayoutParams dividerParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, mDividerHeight);

        mLLayoutLeft = new LinearLayout(context);
        mLLayoutCenter = new LinearLayout(context);
        mLLayoutRight = new LinearLayout(context);
        mVStatus = new View(context);
        mVDivider = new View(context);

        mLLayoutLeft.setGravity(Gravity.CENTER_VERTICAL);
        mLLayoutRight.setGravity(Gravity.CENTER_VERTICAL);

        mTvLeft = new AlphaTextView(context);
        mTvLeft.setGravity(Gravity.CENTER);
        mTvLeft.setLines(1);

        mTvTitleMain = new TextView(context);
        mTvTitleSub = new TextView(context);

        mTvRight = new AlphaTextView(context);
        mTvRight.setGravity(Gravity.CENTER);
        mTvRight.setLines(1);

        mLLayoutLeft.addView(mTvLeft, params);
        mLLayoutRight.addView(mTvRight, params);
        //添加左边容器
        addView(mLLayoutLeft, params);
        //添加中间容器
        addView(mLLayoutCenter, params);
        //添加右边容器
        addView(mLLayoutRight, params);
        //添加下划线View
        addView(mVDivider, dividerParams);
        //添加状态栏View
        addView(mVStatus);
    }

    /**
     * 设置xml默认属性
     *
     * @param context
     */
    private void setViewAttributes(final Context context) {
        mScreenWidth = getMeasuredWidth();
        mStatusBarHeight = getNeedStatusBarHeight();
        if (context instanceof Activity) {
            setImmersible((Activity) context, mImmersible);
            if (mStatusBarLightMode) {
                setStatusBarLightMode(true);
            }
        }
        setOutPadding(mOutPadding);
        setActionPadding(mActionPadding);
        setCenterLayoutPadding(mCenterLayoutPadding);
        setCenterGravityLeft(mCenterGravityLeft);
        setStatusBackground(mStatusBackground);
        setDividerBackground(mDividerBackground);
        setDividerHeight(mDividerHeight);
        setDividerVisible(mDividerVisible);
        setViewPressedAlpha(mViewPressedAlpha);

        setLeftText(mLeftText);
        setLeftTextSize(TypedValue.COMPLEX_UNIT_PX, mLeftTextSize);
        setLeftTextColor(DEFAULT_TEXT_COLOR);
        setLeftTextColor(mLeftTextColor);
        setLeftTextBackground(mLeftTextBackground);
        setLeftTextDrawable(mLeftTextDrawable);
        setLeftTextDrawableTint(mLeftTextDrawableTint);
        setLeftTextDrawableTintMode(mLeftTextDrawableTintMode);
        setLeftTextDrawableWidth(mLeftTextDrawableWidth);
        setLeftTextDrawableHeight(mLeftTextDrawableHeight);
        setLeftTextDrawablePadding(mLeftTextDrawablePadding);

        setTitleMainText(mTitleMainText);
        setTitleMainTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleMainTextSize);
        setTitleMainTextColor(DEFAULT_TEXT_COLOR);
        setTitleMainTextColor(mTitleMainTextColor);
        setTitleMainTextBackground(mTitleMainTextBackground);
        setTitleMainTextFakeBold(mTitleMainTextFakeBold);
        setTitleMainTextMarquee(mTitleMainTextMarquee);

        setTitleSubText(mTitleSubText);
        setTitleSubTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSubTextSize);
        setTitleSubTextColor(DEFAULT_TEXT_COLOR);
        setTitleSubTextColor(mTitleSubTextColor);
        setTitleSubTextBackground(mTitleSubTextBackground);
        setTitleSubTextFakeBold(mTitleSubTextFakeBold);
        setTitleSubTextMarquee(mTitleSubTextMarquee);

        setRightText(mRightText);
        setRightTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
        setRightTextColor(DEFAULT_TEXT_COLOR);
        setRightTextColor(mRightTextColor);
        setRightTextBackground(mRightTextBackground);
        setRightTextDrawable(mRightTextDrawable);
        setRightTextDrawable(mRightTextDrawable);
        setRightTextDrawableTint(mRightTextDrawableTint);
        setRightTextDrawableWidth(mRightTextDrawableWidth);
        setRightTextDrawableHeight(mRightTextDrawableHeight);
        setRightTextDrawablePadding(mRightTextDrawablePadding);
    }


    public Rect getTitleContainerRect() {
        if (mTitleContainerRect == null) {
            mTitleContainerRect = new Rect();
        }
        if (mLLayoutCenter == null) {
            mTitleContainerRect.set(0, 0, 0, 0);
        } else {
            ViewGroupUtils.getDescendantRect(this, mLLayoutCenter, mTitleContainerRect);
        }
        mTitleContainerRect.set(mTitleContainerRect.left + mLLayoutCenter.getPaddingLeft(),
                mTitleContainerRect.top, mTitleContainerRect.right, mTitleContainerRect.bottom);
        return mTitleContainerRect;
    }

    /**
     * 根据位置获取 LinearLayout
     *
     * @param gravity 参考{@link Gravity}
     * @return
     */
    public LinearLayout getLinearLayout(int gravity) {
        if (gravity == Gravity.LEFT || gravity == Gravity.START) {
            return mLLayoutLeft;
        } else if (gravity == Gravity.CENTER) {
            return mLLayoutCenter;
        } else if (gravity == Gravity.END || gravity == Gravity.RIGHT) {
            return mLLayoutRight;
        }
        return mLLayoutCenter;
    }

    /**
     * 根据位置获取TextView
     *
     * @param gravity 参考{@link Gravity}
     * @return
     */
    public TextView getTextView(int gravity) {
        if (gravity == Gravity.LEFT || gravity == Gravity.START) {
            return mTvLeft;
        } else if (gravity == (Gravity.CENTER | Gravity.TOP)) {
            return mTvTitleMain;
        } else if (gravity == (Gravity.CENTER | Gravity.BOTTOM)) {
            return mTvTitleSub;
        } else if (gravity == Gravity.END || gravity == Gravity.RIGHT) {
            return mTvRight;
        }
        return mTvTitleMain;
    }

    /**
     * 根据位置获取View
     *
     * @param gravity 参考{@link Gravity}
     * @return
     */
    public View getView(int gravity) {
        if (gravity == Gravity.TOP) {
            return mVStatus;
        } else if (gravity == Gravity.BOTTOM) {
            return mVDivider;
        }
        return mVStatus;
    }

    /**
     * 获取设置状态栏文字图标样式模式
     *
     * @return >0则表示设置成功 参考{@link StatusBarUtil}
     */
    public int getStatusBarModeType() {
        return mStatusBarModeType;
    }

    public TitleBar setImmersible(Activity activity, boolean immersible) {
        return setImmersible(activity, immersible, mStatusBarPlusEnable);
    }

    public TitleBar setImmersible(Activity activity, boolean immersible, boolean isTransStatusBar) {
        return setImmersible(activity, immersible, isTransStatusBar, mStatusBarPlusEnable);
    }

    /**
     * 设置沉浸式状态栏，4.4以上系统支持
     *
     * @param activity
     * @param immersible       是否沉浸
     * @param isTransStatusBar 是否透明状态栏 --xml未设置statusBackground 属性才会执行
     * @param isPlusStatusBar  是否增加状态栏高度--用于控制底部有输入框 (设置false/xml背景色必须保持和状态栏一致)
     */
    public TitleBar setImmersible(Activity activity, boolean immersible, boolean isTransStatusBar, boolean isPlusStatusBar) {
        this.mImmersible = immersible;
        this.mStatusBarPlusEnable = isPlusStatusBar;
        mStatusBarHeight = getNeedStatusBarHeight();
        if (activity == null) {
            return this;
        }
        //透明状态栏
        Window window = activity.getWindow();
        //Android 4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVStatus.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, mStatusBarHeight));
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Android 5.1以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                int now = window.getDecorView().getSystemUiVisibility();
                int systemUi = mImmersible ?
                        now | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN :
                        (now & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ? now ^ View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : now;
                window.getDecorView().setSystemUiVisibility(systemUi);
                if (mImmersible) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                }
                window.setStatusBarColor(!mImmersible ? Color.BLACK : Color.TRANSPARENT);
            }
        }
        StatusBarUtil.fitsNotchScreen(window, mImmersible);
        setStatusAlpha(immersible ? isTransStatusBar ? 0 : 102 : 255);
        return this;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //实时获取避免因横竖屏切换造成测量错误
        mScreenWidth = getMeasuredWidth();
        mStatusBarHeight = getNeedStatusBarHeight();
        int left = mLLayoutLeft.getMeasuredWidth();
        int right = mLLayoutRight.getMeasuredWidth();
        int center = mLLayoutCenter.getMeasuredWidth();
        //左边 中间 右边layout 顶部在状态栏以下 底部在下划线以上
        mLLayoutLeft.layout(0, mStatusBarHeight, left, getMeasuredHeight() - mDividerHeight);
        //右边layout 左边 整个控件宽度- layout本身宽度
        mLLayoutRight.layout(mScreenWidth - right, mStatusBarHeight, mScreenWidth, getMeasuredHeight() - mDividerHeight);
        boolean isMuchScreen = left + right + center >= mScreenWidth;
        if (left > right) {
            mLLayoutCenter.layout(left, mStatusBarHeight, isMuchScreen ? mScreenWidth - right : mScreenWidth - left, getMeasuredHeight() - mDividerHeight);
        } else {
            mLLayoutCenter.layout(isMuchScreen ? left : right, mStatusBarHeight, mScreenWidth - right, getMeasuredHeight() - mDividerHeight);
        }
        mVDivider.layout(0, getMeasuredHeight() - mVDivider.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        mVStatus.layout(0, 0, getMeasuredWidth(), mStatusBarHeight);
    }

    private int mHeight = -1;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mStatusBarHeight = getNeedStatusBarHeight();
        //测量子控件宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //重新测量宽高--增加状态栏及下划线的高度开始
        //父容器为ConstraintLayout约束布局特殊处理
        if (mHeight <= 0) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec) + mStatusBarHeight + mDividerHeight;
        }
        //普通的父容器正常操作
        if (isNormalParent()) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec) + mStatusBarHeight + mDividerHeight;
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mHeight);
        //重新测量宽高--增加状态栏及下划线的高度结束

        //重新测量左 中 右 宽度 保证中间布局 居正中
        mScreenWidth = getMeasuredWidth();
        int left = mLLayoutLeft.getMeasuredWidth();
        int right = mLLayoutRight.getMeasuredWidth();
        int center = mLLayoutCenter.getMeasuredWidth();
        //判断左、中、右实际占用宽度是否等于或者超过屏幕宽度
        boolean isMuchScreen = left + right + center >= mScreenWidth;
        if (mCenterGravityLeft) {
            return;
        }
        //不设置中间布局左对齐才进行中间布局重新测量
        if (isMuchScreen) {
            center = mScreenWidth - left - right;
        } else {
            if (left > right) {
                center = mScreenWidth - 2 * left;
            } else {
                center = mScreenWidth - 2 * right;
            }
        }
        mLLayoutCenter.measure(MeasureSpec.makeMeasureSpec(center, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    /**
     * 设置TitleBarView高度--不包含状态栏及下划线
     *
     * @param height
     * @return
     */
    public TitleBar setHeight(int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params != null) {
            if (params.height != height) {
                mHeight = -1;
            }
            params.height = height;
            setLayoutParams(params);
        }
        return this;
    }

    @Override
    public void setLayoutParams(LayoutParams params) {
        ViewGroup.LayoutParams par = getLayoutParams();
        if (par != null && params != null && par.height != params.height) {
            mHeight = -1;
        }
        super.setLayoutParams(params);
    }

    public TitleBar setBgDrawable(Drawable background) {
        return setViewBackground(this, background);
    }

    public TitleBar setBgColor(int color) {
        setBackgroundColor(color);
        return this;
    }

    public TitleBar setBgResource(int res) {
        super.setBackgroundResource(res);
        return this;
    }

    /**
     * 距左右边距--根据具体情况设置
     *
     * @param paddingValue
     * @return
     */
    public TitleBar setOutPadding(int paddingValue) {
        mOutPadding = paddingValue;
        if (TextUtils.isEmpty(mLeftText)
                && mLeftTextDrawable == null
                || mLLayoutLeft.indexOfChild(mTvLeft) != 0) {
            mLLayoutLeft.setPadding(mOutPadding, 0, 0, 0);
            mTvLeft.setPadding(0, 0, 0, 0);
        } else {
            mLLayoutLeft.setPadding(0, 0, 0, 0);
            mTvLeft.setPadding(mOutPadding, 0, mActionPadding, 0);
        }
        if (TextUtils.isEmpty(mRightText)
                && mRightTextDrawable == null
                || mLLayoutRight.indexOfChild(mTvRight) != mLLayoutRight.getChildCount() - 1) {
            mLLayoutRight.setPadding(0, 0, mOutPadding, 0);
            mTvRight.setPadding(0, 0, 0, 0);
        } else {
            mLLayoutRight.setPadding(0, 0, 0, 0);
            mTvRight.setPadding(mActionPadding, 0, mOutPadding, 0);
        }
        return this;
    }

    public TitleBar setCenterLayoutPadding(int centerLayoutPadding) {
        this.mCenterLayoutPadding = centerLayoutPadding;
        mLLayoutCenter.setPadding(mCenterLayoutPadding, mLLayoutCenter.getPaddingTop(), mCenterLayoutPadding, mLLayoutCenter.getPaddingBottom());
        return this;
    }

    /**
     * 设置中间是否左对齐
     *
     * @param enable
     * @return
     */
    public TitleBar setCenterGravityLeft(boolean enable) {
        this.mCenterGravityLeft = enable;
        mTvTitleMain.setGravity(mCenterGravityLeft ? Gravity.LEFT : Gravity.CENTER);
        mLLayoutCenter.setGravity(mCenterGravityLeft ? Gravity.LEFT | Gravity.CENTER_VERTICAL : Gravity.CENTER);
        mTvTitleSub.setGravity(mCenterGravityLeft ? Gravity.LEFT : Gravity.CENTER);
        return setCenterGravityLeftPadding(mCenterGravityLeftPadding);
    }

    /**
     * 设置title 左边距--当设置setCenterGravityLeft(true)生效
     *
     * @param padding
     * @return
     */
    public TitleBar setCenterGravityLeftPadding(int padding) {
        if (mCenterGravityLeft) {
            mCenterGravityLeftPadding = padding;
            mLLayoutCenter.setPadding(mCenterGravityLeftPadding, mLLayoutCenter.getPaddingTop(), mLLayoutCenter.getPaddingRight(), mLLayoutCenter.getPaddingBottom());
        } else {
            return setCenterLayoutPadding(mCenterLayoutPadding);
        }
        return this;
    }

    public TitleBar setStatusBarLightMode(boolean mStatusBarLightMode) {
        if (mContext instanceof Activity) {
            return setStatusBarLightMode((Activity) mContext, mStatusBarLightMode);
        }
        return this;
    }

    /**
     * 设置状态栏文字黑白颜色切换
     *
     * @param mActivity
     * @param mStatusBarLightMode
     * @return
     */
    public TitleBar setStatusBarLightMode(Activity mActivity, boolean mStatusBarLightMode) {
        this.mStatusBarLightMode = mStatusBarLightMode;
        if (mActivity != null) {
            if (mStatusBarLightMode) {
                mStatusBarModeType = StatusBarUtil.setStatusBarLightMode(mActivity);
            } else {
                mStatusBarModeType = StatusBarUtil.setStatusBarDarkMode(mActivity);
            }
        }
        return this;
    }

    /**
     * 返回是否支持状态栏颜色切换
     *
     * @return
     */
    public boolean isStatusBarLightModeEnable() {
        return StatusBarUtil.isSupportStatusBarFontChange();
    }

    /**
     * 设置view左右两边内边距
     *
     * @param actionPadding
     * @return
     */
    public TitleBar setActionPadding(int actionPadding) {
        mActionPadding = actionPadding;
        return this;
    }

    /**
     * 设置状态栏背景
     *
     * @param drawable
     * @return
     */
    public TitleBar setStatusBackground(Drawable drawable) {
        mStatusBackground = drawable;
        return setViewBackground(mVStatus, mStatusBackground);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    public TitleBar setStatusBackgroundColor(int color) {
        return setStatusBackground(new ColorDrawable(color));
    }

    /**
     * 设置透明度
     *
     * @param alpha
     * @return
     */
    public TitleBar setStatusAlpha(int alpha) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }
        return setStatusBackgroundColor(Color.argb(alpha, 0, 0, 0));
    }

    public TitleBar setStatusBackgroundResource(int resId) {
        return setStatusBackground(mResourceUtil.getDrawable(resId));
    }

    /**
     * 设置下划线背景
     *
     * @param drawable
     * @return
     */
    public TitleBar setDividerBackground(Drawable drawable) {
        mDividerBackground = drawable;
        return setViewBackground(mVDivider, mDividerBackground);
    }

    public TitleBar setDividerBackgroundColor(int color) {
        return setDividerBackground(new ColorDrawable(color));
    }

    public TitleBar setDividerBackgroundResource(int resId) {
        return setDividerBackground(mResourceUtil.getDrawable(resId));
    }

    public TitleBar setDividerHeight(int dividerHeight) {
        mDividerHeight = dividerHeight;
        mVDivider.getLayoutParams().height = dividerHeight;
        return this;
    }

    public TitleBar setDividerVisible(boolean visible) {
        mDividerVisible = visible;
        mVDivider.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }

    /**
     * 设置view点击按下时alpha变化
     *
     * @param alpha
     * @return
     */
    public TitleBar setViewPressedAlpha(float alpha) {
        if (alpha > 1) {
            alpha = 1.0f;
        } else if (alpha < 0) {
            alpha = 0f;
        }
        this.mViewPressedAlpha = alpha;
        mTvLeft.getDelegate().getAlphaViewHelper().setPressedAlpha(mViewPressedAlpha);
        mTvRight.getDelegate().getAlphaViewHelper().setPressedAlpha(mViewPressedAlpha);
        return this;
    }

    /**
     * 设置所有TextView的文本颜色--注意和其它方法的先后顺序
     *
     * @param color
     * @return
     */
    public TitleBar setTextColor(int color) {
        return setLeftTextColor(color)
                .setTitleMainTextColor(color)
                .setTitleSubTextColor(color)
                .setRightTextColor(color)
                .setActionTextColor(color);
    }

    public TitleBar setTextColor(ColorStateList colors) {
        return setLeftTextColor(colors)
                .setTitleMainTextColor(colors)
                .setTitleSubTextColor(colors)
                .setRightTextColor(colors)
                .setActionTextColor(colors);
    }

    public TitleBar setTextColorResource(int res) {
        return setTextColor(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setLeftText(CharSequence title) {
        mLeftText = title;
        mTvLeft.setText(title);
        return setOutPadding(mOutPadding);
    }

    public TitleBar setLeftText(int id) {
        return setLeftText(mResourceUtil.getText(id));
    }

    /**
     * 设置文字大小
     *
     * @param unit 文字单位{@link TypedValue}
     * @param size
     * @return
     */
    public TitleBar setLeftTextSize(int unit, float size) {
        mTvLeft.setTextSize(unit, size);
        return this;
    }

    public TitleBar setLeftTextSize(float size) {
        return setLeftTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public TitleBar setLeftTextColor(int color) {
        mTvLeft.setTextColor(color);
        return this;
    }

    /**
     * 设置文字状态颜色-如按下颜色变化
     *
     * @param colors
     * @return
     */
    public TitleBar setLeftTextColor(ColorStateList colors) {
        if (colors != null) {
            mTvLeft.setTextColor(colors);
        }
        return this;
    }

    public TitleBar setLeftTextColorResource(int res) {
        return setLeftTextColor(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setLeftTextBackground(Drawable drawable) {
        mLeftTextBackground = drawable;
        return setViewBackground(mTvLeft, mLeftTextBackground);
    }

    public TitleBar setLeftTextBackgroundColor(int color) {
        return setLeftTextBackground(new ColorDrawable(color));
    }

    /**
     * @param resId
     */
    public TitleBar setLeftTextBackgroundResource(int resId) {
        return setLeftTextBackground(mResourceUtil.getDrawable(resId));
    }

    /**
     * 设置左边图片资源
     *
     * @param drawable
     * @return
     */
    public TitleBar setLeftTextDrawable(Drawable drawable) {
        mLeftTextDrawable = drawable;
        DrawableUtil.setDrawableWidthHeight(mLeftTextDrawable, mLeftTextDrawableWidth, mLeftTextDrawableHeight);
        Drawable[] drawables = mTvLeft.getCompoundDrawables();
        mTvLeft.setCompoundDrawables(mLeftTextDrawable, drawables[1], drawables[2], drawables[3]);
        setTextDrawableTint(mTvLeft, mLeftTextDrawableTint, mLeftTextDrawableTintMode);
        return setOutPadding(mOutPadding);
    }

    public TitleBar setLeftTextDrawable(int resId) {
        return setLeftTextDrawable(mResourceUtil.getDrawable(resId));
    }

    public TitleBar setLeftTextDrawableTint(int color) {
        return setLeftTextDrawableTint(ColorStateList.valueOf(color));
    }

    public TitleBar setLeftTextDrawableTint(ColorStateList colors) {
        if (colors == null) {
            return this;
        }
        mLeftTextDrawableTint = colors;
        return setTextDrawableTint(mTvLeft, mLeftTextDrawableTint, mLeftTextDrawableTintMode);
    }

    public TitleBar setLeftTextDrawableTintResource(int res) {
        return setLeftTextDrawableTint(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setLeftTextDrawableTintMode(PorterDuff.Mode mode) {
        if (mode == null) {
            return this;
        }
        mLeftTextDrawableTintMode = mode;
        return setTextDrawableTint(mTvLeft, mLeftTextDrawableTint, mLeftTextDrawableTintMode);
    }

    private TitleBar setTextDrawableTint(TextView textView, ColorStateList
            tint, PorterDuff.Mode tintMode) {
        if (tint == null && tintMode == null) {
            return this;
        }
        Drawable[] drawables = textView.getCompoundDrawables();
        for (Drawable item : drawables) {
            if (item != null) {
                item = item.mutate();
                if (tint != null) {
                    DrawableUtil.setTintDrawable(item, tint);
                }
                if (tintMode != null) {
                    DrawableUtil.setTintMode(item, tintMode, tint != null ? tint.getDefaultColor() : Color.BLACK);
                }
            }
        }
        return this;
    }

    public TitleBar setLeftTextDrawableWidth(int width) {
        mLeftTextDrawableWidth = width;
        return setLeftTextDrawable(mLeftTextDrawable);
    }

    public TitleBar setLeftTextDrawableHeight(int height) {
        mLeftTextDrawableHeight = height;
        return setLeftTextDrawable(mLeftTextDrawable);
    }

    public TitleBar setLeftTextDrawablePadding(int drawablePadding) {
        this.mLeftTextDrawablePadding = drawablePadding;
        mTvLeft.setCompoundDrawablePadding(mLeftTextDrawablePadding);
        return this;
    }

    public TitleBar setLeftTextPadding(int left, int top, int right, int bottom) {
        mTvLeft.setPadding(left, top, right, bottom);
        return this;
    }

    public TitleBar setOnLeftTextClickListener(OnClickListener l) {
        mTvLeft.setOnClickListener(l);
        return this;
    }

    public TitleBar setLeftVisible(boolean visible) {
        mTvLeft.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public TitleBar setTitleMainText(int id) {
        return setTitleMainText(mResourceUtil.getText(id));
    }

    public TitleBar setTitleMainText(CharSequence charSequence) {
        mTvTitleMain.setText(charSequence);
        if (hasChildView(mLLayoutCenter, mTvTitleMain)) {
            return this;
        }
        if (TextUtils.isEmpty(charSequence)) {
            return this;
        }
        //CollapsingTitleBarLayout 作为父控件则不进行操作
        if ((getParent() != null && getParent().getClass().getSimpleName().equals("CollapsingTitleBarLayout"))) {
            return this;
        }
        mLLayoutCenter.addView(mTvTitleMain, 0);
        //有主副标题竖直布局
        if (hasChildView(mLLayoutCenter, mTvTitleMain) && hasChildView(mLLayoutCenter, mTvTitleSub)) {
            mLLayoutCenter.setOrientation(LinearLayout.VERTICAL);
        }
        return this;
    }

    /**
     * {@link TextView#setTextSize(int, float)}
     *
     * @param unit
     * @param titleMainTextSpValue
     * @return
     */
    public TitleBar setTitleMainTextSize(int unit, float titleMainTextSpValue) {
        mTvTitleMain.setTextSize(unit, titleMainTextSpValue);
        return this;
    }

    /**
     * 设置文字大小 参考{@link TypedValue}
     *
     * @param titleMainTextSpValue
     * @return
     */
    public TitleBar setTitleMainTextSize(float titleMainTextSpValue) {
        return setTitleMainTextSize(TypedValue.COMPLEX_UNIT_SP, titleMainTextSpValue);
    }

    public TitleBar setTitleMainTextColor(int color) {
        mTvTitleMain.setTextColor(color);
        return this;
    }

    public TitleBar setTitleMainTextColor(ColorStateList colors) {
        if (colors != null) {
            mTvTitleMain.setTextColor(colors);
        }
        return this;
    }

    public TitleBar setTitleMainTextColorResource(int res) {
        return setTitleMainTextColor(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setTitleMainTextBackground(Drawable drawable) {
        mTitleMainTextBackground = drawable;
        return setViewBackground(mTvTitleMain, mTitleMainTextBackground);
    }

    public TitleBar setTitleMainTextBackgroundColor(int color) {
        return setTitleMainTextBackground(new ColorDrawable(color));
    }

    public TitleBar setTitleMainTextBackgroundResource(int resId) {
        return setTitleMainTextBackground(mResourceUtil.getDrawable(resId));
    }

    /**
     * 设置粗体标题
     *
     * @param isFakeBold
     */
    public TitleBar setTitleMainTextFakeBold(boolean isFakeBold) {
        this.mTitleMainTextFakeBold = isFakeBold;
        mTvTitleMain.getPaint().setFakeBoldText(mTitleMainTextFakeBold);
        return this;
    }

    public TitleBar setTitleMainTextMarquee(boolean enable) {
        this.mTitleMainTextMarquee = enable;
        if (enable) {
            setTitleSubTextMarquee(false);
            mTvTitleMain.setSingleLine();
            mTvTitleMain.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTvTitleMain.setFocusable(true);
            mTvTitleMain.setFocusableInTouchMode(true);
            mTvTitleMain.requestFocus();
            mTvTitleMain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && mTitleMainTextMarquee) {
                        mTvTitleMain.requestFocus();
                    }
                }
            });
            //开启硬件加速
            mTvTitleMain.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mTvTitleMain.setMaxLines(1);
            mTvTitleMain.setEllipsize(TextUtils.TruncateAt.END);
            mTvTitleMain.setOnFocusChangeListener(null);
            //关闭硬件加速
            mTvTitleMain.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        return this;
    }

    public TitleBar setTitleMainTextPadding(int left, int top, int right, int bottom) {
        mTvTitleMain.setPadding(left, top, right, bottom);
        return this;
    }

    public TitleBar setTitleSubText(CharSequence charSequence) {
        if (charSequence == null || charSequence.toString().isEmpty()) {
            mTvTitleSub.setVisibility(GONE);
        } else {
            mTvTitleSub.setVisibility(VISIBLE);
        }
        mTvTitleSub.setText(charSequence);
        //非空且还未添加副标题
        if (!TextUtils.isEmpty(charSequence) && !hasChildView(mLLayoutCenter, mTvTitleSub)) {
            if (hasChildView(mLLayoutCenter, mTvTitleMain)) {
                mTvTitleMain.setSingleLine();
                mTvTitleSub.setSingleLine();
            }
            mLLayoutCenter.addView(mTvTitleSub);
        }
        //有主副标题竖直布局
        if (hasChildView(mLLayoutCenter, mTvTitleMain) && hasChildView(mLLayoutCenter, mTvTitleSub)) {
            mLLayoutCenter.setOrientation(LinearLayout.VERTICAL);
        }
        return this;
    }

    public TitleBar setTitleSubText(int id) {
        return setTitleSubText(mResourceUtil.getText(id));
    }

    /**
     * 设置文字大小
     *
     * @param unit  单位 参考{@link TypedValue}
     * @param value
     * @return
     */
    public TitleBar setTitleSubTextSize(int unit, float value) {
        mTvTitleSub.setTextSize(unit, value);
        return this;
    }

    public TitleBar setTitleSubTextSize(float spValue) {
        return setTitleSubTextSize(TypedValue.COMPLEX_UNIT_SP, spValue);
    }

    public TitleBar setTitleSubTextColor(int color) {
        mTvTitleSub.setTextColor(color);
        return this;
    }

    public TitleBar setTitleSubTextColor(ColorStateList colors) {
        if (colors != null) {
            mTvTitleSub.setTextColor(colors);
        }
        return this;
    }

    public TitleBar setTitleSubTextColorResource(int res) {
        return setTitleSubTextColor(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setTitleSubTextBackground(Drawable drawable) {
        mTitleSubTextBackground = drawable;
        return setViewBackground(mTvTitleSub, mTitleSubTextBackground);
    }

    public TitleBar setTitleSubTextBackgroundColor(int color) {
        return setTitleSubTextBackground(new ColorDrawable(color));
    }

    public TitleBar setTitleSubTextBackgroundResource(int resId) {
        return setTitleSubTextBackground(mResourceUtil.getDrawable(resId));
    }

    /**
     * 设置粗体标题
     *
     * @param isFakeBold
     */
    public TitleBar setTitleSubTextFakeBold(boolean isFakeBold) {
        this.mTitleSubTextFakeBold = isFakeBold;
        mTvTitleSub.getPaint().setFakeBoldText(mTitleSubTextFakeBold);
        return this;
    }

    /**
     * 设置TextView 跑马灯
     *
     * @param enable
     */
    public TitleBar setTitleSubTextMarquee(boolean enable) {
        this.mTitleSubTextMarquee = enable;
        if (enable) {
            setTitleMainTextMarquee(false);
            mTvTitleSub.setSingleLine();
            mTvTitleSub.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mTvTitleSub.setFocusable(true);
            mTvTitleSub.setFocusableInTouchMode(true);
            mTvTitleSub.requestFocus();
            mTvTitleSub.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus && mTitleSubTextMarquee) {
                        mTvTitleMain.requestFocus();
                    }
                }
            });
            //开启硬件加速
            mTvTitleSub.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mTvTitleSub.setMaxLines(1);
            mTvTitleSub.setEllipsize(TextUtils.TruncateAt.END);
            mTvTitleSub.setOnFocusChangeListener(null);
            //关闭硬件加速
            mTvTitleSub.setLayerType(View.LAYER_TYPE_NONE, null);
        }
        return this;
    }

    public TitleBar setOnCenterClickListener(OnClickListener l) {
        mLLayoutCenter.setOnClickListener(l);
        return this;
    }

    public TitleBar setRightText(CharSequence title) {
        mRightText = title;
        mTvRight.setText(title);
        return setOutPadding(mOutPadding);
    }

    public TitleBar setRightText(int id) {
        return setRightText(mResourceUtil.getText(id));
    }

    /**
     * 设置文字大小
     *
     * @param unit 单位 参考{@link TypedValue}
     * @param size
     * @return
     */
    public TitleBar setRightTextSize(int unit, float size) {
        mTvRight.setTextSize(unit, size);
        return this;
    }

    public TitleBar setRightTextSize(float size) {
        return setRightTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public TitleBar setRightTextColor(int color) {
        mTvRight.setTextColor(color);
        return this;
    }

    public TitleBar setRightTextColor(ColorStateList colors) {
        if (colors != null) {
            mTvRight.setTextColor(colors);
        }
        return this;
    }

    public TitleBar setRightTextColorResource(int res) {
        return setRightTextColor(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setRightTextBackground(Drawable drawable) {
        mRightTextBackground = drawable;
        return setViewBackground(mTvRight, mRightTextBackground);
    }

    public TitleBar setRightTextBackgroundColor(int color) {
        return setRightTextBackground(new ColorDrawable(color));
    }

    public TitleBar setRightTextBackgroundResource(int resId) {
        return setRightTextBackground(mResourceUtil.getDrawable(resId));
    }

    /**
     * 右边文本添加图片
     *
     * @param drawable 资源
     */
    public TitleBar setRightTextDrawable(Drawable drawable) {
        mRightTextDrawable = drawable;
        DrawableUtil.setDrawableWidthHeight(mRightTextDrawable, mRightTextDrawableWidth, mRightTextDrawableHeight);
        Drawable[] drawables = mTvRight.getCompoundDrawables();
        mTvRight.setCompoundDrawables(drawables[0], drawables[1], mRightTextDrawable, drawables[3]);
        setTextDrawableTint(mTvRight, mRightTextDrawableTint, mRightTextDrawableTintMode);
        return setOutPadding(mOutPadding);
    }

    public TitleBar setRightTextDrawable(int resId) {
        return setRightTextDrawable(mResourceUtil.getDrawable(resId));
    }

    public TitleBar setRightTextDrawableTint(int color) {
        return setRightTextDrawableTint(ColorStateList.valueOf(color));
    }

    public TitleBar setRightTextDrawableTint(ColorStateList colors) {
        if (colors == null) {
            return this;
        }
        mRightTextDrawableTint = colors;
        return setTextDrawableTint(mTvRight, mRightTextDrawableTint, mRightTextDrawableTintMode);
    }

    public TitleBar setRightTextDrawableTintResource(int res) {
        return setRightTextDrawableTint(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setRightTextDrawableTintMode(PorterDuff.Mode mode) {
        if (mode == null) {
            return this;
        }
        mRightTextDrawableTintMode = mode;
        return setTextDrawableTint(mTvRight, mRightTextDrawableTint, mRightTextDrawableTintMode);
    }

    public TitleBar setRightTextDrawablePadding(int drawablePadding) {
        this.mRightTextDrawablePadding = drawablePadding;
        mTvRight.setCompoundDrawablePadding(mRightTextDrawablePadding);
        return this;
    }

    public TitleBar setRightTextDrawableWidth(int width) {
        mRightTextDrawableWidth = width;
        return setRightTextDrawable(mRightTextDrawable);
    }

    public TitleBar setRightTextDrawableHeight(int height) {
        mRightTextDrawableHeight = height;
        return setRightTextDrawable(mRightTextDrawable);
    }

    public TitleBar setRightTextPadding(int left, int top, int right, int bottom) {
        mTvRight.setPadding(left, top, right, bottom);
        return this;
    }

    public TitleBar setOnRightTextClickListener(OnClickListener l) {
        mTvRight.setOnClickListener(l);
        return this;
    }

    public TitleBar setRightVisible(boolean visible) {
        mTvRight.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public TitleBar setActionTextSize(int mActionTextSize) {
        this.mActionTextSize = mActionTextSize;
        return this;
    }

    public TitleBar setActionTextColor(int mActionTextColor) {
        this.mActionTextColor = ColorStateList.valueOf(mActionTextColor);
        return this;
    }

    public TitleBar setActionTextColor(ColorStateList mActionTextColor) {
        this.mActionTextColor = mActionTextColor;
        return this;
    }

    public TitleBar setActionTextColorResource(int res) {
        return setActionTextColor(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setActionTextBackground(Drawable drawable) {
        this.mActionTextBackground = drawable;
        return this;
    }

    public TitleBar setActionTextBackgroundColor(int color) {
        return setActionTextBackground(new ColorDrawable(color));
    }

    public TitleBar setActionTextBackgroundResource(int resId) {
        return setActionTextBackground(mResourceUtil.getDrawable(resId));
    }

    public TitleBar setActionTint(int color) {
        return setActionTint(ColorStateList.valueOf(color));
    }

    public TitleBar setActionTint(ColorStateList colors) {
        if (colors == null) {
            return this;
        }
        mActionTint = colors;
        return setActionTint();
    }

    public TitleBar setActionTintResource(int res) {
        return setActionTint(mResourceUtil.getColorStateList(res));
    }

    public TitleBar setActionTintMode(PorterDuff.Mode mode) {
        if (mode == null) {
            return this;
        }
        mActionTintMode = mode;
        return setActionTint();
    }

    private TitleBar setActionTint() {
        if (mActionTint == null && mActionTintMode == null) {
            return this;
        }
        int sizeLeft = mLLayoutLeft.getChildCount();
        int sizeCenter = mLLayoutCenter.getChildCount();
        int sizeRight = mLLayoutRight.getChildCount();
        for (int i = 0; i < sizeLeft; i++) {
            View view = mLLayoutLeft.getChildAt(i);
            if (view instanceof TextView) {
                if (view != mTvLeft) {
                    setTextDrawableTint((TextView) view, mActionTint, mActionTintMode);
                }
            } else if (view instanceof ImageView) {
                setImageTint((ImageView) view, mActionTint, mActionTintMode);
            }
        }
        for (int i = 0; i < sizeCenter; i++) {
            View view = mLLayoutCenter.getChildAt(i);
            if (view instanceof TextView) {
                if (view != mTvTitleMain && view != mTvTitleSub) {
                    setTextDrawableTint((TextView) view, mActionTint, mActionTintMode);
                }
            } else if (view instanceof ImageView) {
                setImageTint((ImageView) view, mActionTint, mActionTintMode);
            }
        }
        for (int i = 0; i < sizeRight; i++) {
            View view = mLLayoutRight.getChildAt(i);
            if (view instanceof TextView) {
                if (view != mTvRight) {
                    setTextDrawableTint((TextView) view, mActionTint, mActionTintMode);
                }
            } else if (view instanceof ImageView) {
                setImageTint((ImageView) view, mActionTint, mActionTintMode);
            }
        }
        return this;
    }

    private void setImageTint(ImageView imageView, ColorStateList tint, PorterDuff.Mode
            tintMode) {
        if (imageView.getDrawable() == null) {
            return;
        }
        if (tint == null && tintMode == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mActionTint != null) {
                imageView.setImageTintList(mActionTint);
            }
            if (mActionTintMode != null) {
                imageView.setImageTintMode(mActionTintMode);
            }
        } else {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && mActionTint != null) {
                drawable = drawable.mutate();
                drawable.setColorFilter(mActionTint.getDefaultColor(), mActionTintMode != null ? mActionTintMode : PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    /**
     * 自定义左边部分布局
     *
     * @param action
     * @return
     */
    public TitleBar addLeftAction(Action action) {
        return addLeftAction(action, -1);
    }

    public TitleBar addLeftAction(Action action, int position) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        return addLeftAction(action, position, view.getLayoutParams());
    }

    public TitleBar addLeftAction(Action action, int width, int height) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        return addLeftAction(action, -1, params);
    }

    public TitleBar addLeftAction(Action action, LayoutParams params) {
        return addLeftAction(action, -1, params);
    }

    public TitleBar addLeftAction(Action action, int position, LayoutParams params) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        mLLayoutLeft.addView(view, position, params);
        return this;
    }

    /**
     * 自定义中间部分布局
     */
    public TitleBar addCenterAction(Action action) {
        return addCenterAction(action, -1);
    }

    public TitleBar addCenterAction(Action action, int position) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        return addCenterAction(action, position, view.getLayoutParams());
    }

    public TitleBar addCenterAction(Action action, int width, int height) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        params.width = width;
        params.height = height;
        return addCenterAction(action, -1, params);
    }

    public TitleBar addCenterAction(Action action, LayoutParams params) {
        return addCenterAction(action, -1, params);
    }

    public TitleBar addCenterAction(Action action, int position, LayoutParams params) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        mLLayoutCenter.addView(view, position, params);
        return this;
    }

    /**
     * 在标题栏右边添加action
     *
     * @param action
     * @return
     */
    public TitleBar addRightAction(Action action) {
        return addRightAction(action, -1);
    }

    public TitleBar addRightAction(Action action, int position) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        return addRightAction(action, position, view.getLayoutParams());
    }

    public TitleBar addRightAction(Action action, int width, int height) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        params.width = width;
        params.height = height;
        return addRightAction(action, -1, params);
    }

    public TitleBar addRightAction(Action action, LayoutParams params) {
        return addLeftAction(action, -1, params);
    }

    public TitleBar addRightAction(Action action, int position, LayoutParams params) {
        View view = inflateAction(action);
        if (view == null) {
            return this;
        }
        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        mLLayoutRight.addView(view, position, params);
        return this;
    }

    /**
     * 通过action加载一个View
     *
     * @param action
     * @return
     */
    private View inflateAction(Action action) {
        View view = null;
        Object obj = action.getData();
        if (obj == null) {
            return null;
        }
        if (obj instanceof View) {
            view = (View) obj;
        } else if (obj instanceof String) {
            AlphaTextView text = new AlphaTextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setText((String) obj);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActionTextSize);
            if (mActionTextColor != null) {
                text.setTextColor(mActionTextColor);
            } else {
                text.setTextColor(DEFAULT_TEXT_COLOR);
            }
            text.getDelegate().getAlphaViewHelper().setPressedAlpha(mViewPressedAlpha);
            setViewBackground(text, mActionTextBackground);
            view = text;
            setTextDrawableTint(text, mActionTint, mActionTintMode);
        } else if (obj instanceof Drawable) {
            AlphaImageView img = new AlphaImageView(getContext());
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setImageDrawable((Drawable) obj);
            img.getDelegate().getAlphaViewHelper().setPressedAlpha(mViewPressedAlpha);
            view = img;
            setImageTint(img, mActionTint, mActionTintMode);
        }
        view.setPadding(mActionPadding, 0, mActionPadding, 0);
        view.setTag(action);
        view.setOnClickListener(action.getOnClickListener());
        return view;
    }

    /**
     * 添加View以及相应的动作接口
     */
    public interface Action<T> {
        T getData();

        OnClickListener getOnClickListener();
    }

    public class ImageAction implements Action<Drawable> {


        private Drawable mDrawable;
        private OnClickListener onClickListener;

        public ImageAction(Drawable mDrawable, OnClickListener onClickListener) {
            this.mDrawable = mDrawable;
            this.onClickListener = onClickListener;
        }

        public ImageAction(int drawableId, OnClickListener onClickListener) {
            this.mDrawable = mResourceUtil.getDrawable(drawableId);
            this.onClickListener = onClickListener;
        }

        public ImageAction(int resId) {
            this.mDrawable = mResourceUtil.getDrawable(resId);
        }

        public ImageAction(Drawable drawable) {
            this.mDrawable = drawable;
        }

        @Override
        public Drawable getData() {
            return mDrawable;
        }

        @Override
        public OnClickListener getOnClickListener() {
            return onClickListener;
        }

    }

    public class TextAction implements Action<CharSequence> {

        private CharSequence mText;
        private OnClickListener onClickListener;

        public TextAction(CharSequence mText, OnClickListener onClickListener) {
            this.mText = mText;
            this.onClickListener = onClickListener;
        }

        public TextAction(CharSequence mText) {
            this.mText = mText;
        }

        public TextAction(int mText) {
            this.mText = mResourceUtil.getText(mText);
        }

        public TextAction(int mText, OnClickListener onClickListener) {
            this.mText = mResourceUtil.getText(mText);
            this.onClickListener = onClickListener;
        }

        @Override
        public CharSequence getData() {
            return mText;
        }

        @Override
        public OnClickListener getOnClickListener() {
            return onClickListener;
        }

    }

    public class ViewAction implements Action<View> {

        private View mView;
        private OnClickListener onClickListener;

        public ViewAction(View mView, OnClickListener onClickListener) {
            this.mView = mView;
            this.onClickListener = onClickListener;
        }

        public ViewAction(View mView) {
            this.mView = mView;
        }

        @Override
        public View getData() {
            return mView;
        }

        @Override
        public OnClickListener getOnClickListener() {
            return onClickListener;
        }

    }

    /**
     * 设置view背景drawable
     *
     * @param view
     * @param drawable
     */
    private TitleBar setViewBackground(View view, Drawable drawable) {
        if (view == null) {
            return this;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
        return this;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取真实需要的状态栏高度
     *
     * @return
     */
    private int getNeedStatusBarHeight() {
        int status = StatusBarUtil.getStatusBarHeight();
        int safe = NotchUtil.getSafeInsetTop(this);
        return isNeedStatusBar() ? Math.max(status, safe) : 0;
    }

    /**
     * 当TitleBarView的父容器为ConstraintLayout(约束布局)时TitleBarView新增的高度会变成状态栏高度2倍需做特殊处理--暂不知原因
     *
     * @return
     */
    private boolean isNormalParent() {
        return !(getParent() != null && getParent().getClass().getSimpleName().contains("ConstraintLayout"));
    }

    private boolean isNeedStatusBar() {
        return mImmersible && mStatusBarPlusEnable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    @Deprecated
    public static int getStatusBarHeight() {
        return StatusBarUtil.getStatusBarHeight();
    }

    /**
     * 将dip或dp值转换为px值
     *
     * @param dipValue dp值
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 判断父控件是否包含某个子View
     *
     * @param father
     * @param child
     * @return
     */
    public static boolean hasChildView(ViewGroup father, View child) {
        boolean had = false;
        try {
            had = father.indexOfChild(child) != -1;
        } catch (Exception e) {
        }
        return had;
    }

}
