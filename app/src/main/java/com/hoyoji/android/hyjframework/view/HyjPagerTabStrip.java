package com.hoyoji.android.hyjframework.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

public class HyjPagerTabStrip extends PagerTitleStrip {
    private static final String TAG = "HyjPagerTabStrip";

    ViewPager mPager;
    TextView mFirstText;
    TextView mPrevText;
    TextView mCurrText;
    TextView mNextText;
    TextView mLastText;

    private int mLastKnownCurrentPage = -1;
    private float mLastKnownPositionOffset = -1;
    private int mScaledTextSpacing;
    private int mGravity;

    private boolean mUpdatingText;
    private boolean mUpdatingPositions;

//    private final PageListener mPageListener = new PageListener();

    private static final int[] ATTRS = new int[] {
        android.R.attr.textAppearance,
        android.R.attr.textSize,
        android.R.attr.textColor,
        android.R.attr.gravity
    };

    private static final float SIDE_ALPHA = 0.6f;
    private static final int TEXT_SPACING = 16; // dip

    private int mNonPrimaryAlpha;
    int mTextColor;

    public HyjPagerTabStrip(Context context) {
        this(context, null);
    }

    public HyjPagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

        addView(mFirstText = new TextView(context));
        addView(mPrevText = new TextView(context));
        addView(mCurrText = new TextView(context));
        addView(mNextText = new TextView(context));
        addView(mLastText = new TextView(context));

        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        final int textAppearance = a.getResourceId(0, 0);
        if (textAppearance != 0) {
            mFirstText.setTextAppearance(context, textAppearance);
            mPrevText.setTextAppearance(context, textAppearance);
            mCurrText.setTextAppearance(context, textAppearance);
            mNextText.setTextAppearance(context, textAppearance);
            mLastText.setTextAppearance(context, textAppearance);
        }
        final int textSize = a.getDimensionPixelSize(1, 0);
        if (textSize != 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        if (a.hasValue(2)) {
            final int textColor = a.getColor(2, 0);
            mFirstText.setTextColor(textColor);
            mPrevText.setTextColor(textColor);
            mCurrText.setTextColor(textColor);
            mNextText.setTextColor(textColor);
            mLastText.setTextColor(textColor);
        }
        mGravity = a.getInteger(3, Gravity.BOTTOM);
        a.recycle();

        mTextColor = mCurrText.getTextColors().getDefaultColor();
        setNonPrimaryAlpha(SIDE_ALPHA);

        mFirstText.setEllipsize(TruncateAt.END);
        mLastText.setEllipsize(TruncateAt.END);
        mPrevText.setEllipsize(TruncateAt.END);
        mCurrText.setEllipsize(TruncateAt.END);
        mNextText.setEllipsize(TruncateAt.END);
        mFirstText.setSingleLine();
        mLastText.setSingleLine();
        mPrevText.setSingleLine();
        mCurrText.setSingleLine();
        mNextText.setSingleLine();

        final float density = context.getResources().getDisplayMetrics().density;
        mScaledTextSpacing = (int) (TEXT_SPACING * density);
    }

    /**
     * Set the required spacing between title segments.
     *
     * @param spacingPixels Spacing between each title displayed in pixels
     */
    public void setTextSpacing(int spacingPixels) {
        mScaledTextSpacing = spacingPixels;
        requestLayout();
    }

    /**
     * @return The required spacing between title segments in pixels
     */
    public int getTextSpacing() {
        return mScaledTextSpacing;
    }

    /**
     * Set the alpha value used for non-primary page titles.
     *
     * @param alpha Opacity value in the range 0-1f
     */
    public void setNonPrimaryAlpha(float alpha) {
//        mNonPrimaryAlpha = (int) (alpha * 255) & 0xFF;
//        final int transparentColor = (mNonPrimaryAlpha << 24) | (mTextColor & 0xFFFFFF);
//        mPrevText.setTextColor(transparentColor);
//        mNextText.setTextColor(transparentColor);
    }

    /**
     * Set the color value used as the base color for all displayed page titles.
     * Alpha will be ignored for non-primary page titles. See {@link #setNonPrimaryAlpha(float)}.
     *
     * @param color Color hex code in 0xAARRGGBB format
     */
    public void setTextColor(int color) {
        mTextColor = color;
        mCurrText.setTextColor(color);
        mNextText.setTextColor(color);
        mPrevText.setTextColor(color);
        mFirstText.setTextColor(color);
        mLastText.setTextColor(color);
//        final int transparentColor = (mNonPrimaryAlpha << 24) | (mTextColor & 0xFFFFFF);
//        mPrevText.setTextColor(transparentColor);
//        mNextText.setTextColor(transparentColor);
    }

    /**
     * Set the default text size to a given unit and value.
     * See {@link TypedValue} for the possible dimension units.
     *
     * <p>Example: to set the text size to 14px, use
     * setTextSize(TypedValue.COMPLEX_UNIT_PX, 14);</p>
     *
     * @param unit The desired dimension unit
     * @param size The desired size in the given units
     */
    public void setTextSize(int unit, float size) {
        mFirstText.setTextSize(unit, size);
        mLastText.setTextSize(unit, size);
        mPrevText.setTextSize(unit, size);
        mCurrText.setTextSize(unit, size);
        mNextText.setTextSize(unit, size);
    }

    /**
     * Set the {@link Gravity} used to position text within the title strip.
     * Only the vertical gravity component is used.
     *
     * @param gravity {@link Gravity} constant for positioning title text
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        requestLayout();
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//
//        final ViewParent parent = getParent();
//        if (!(parent instanceof ViewPager)) {
//            throw new IllegalStateException(
//                    "PagerTitleStrip must be a direct child of a ViewPager.");
//        }
//
//        final ViewPager pager = (ViewPager) parent;
//        final PagerAdapter adapter = pager.getAdapter();
//
//        pager.setInternalPageChangeListener(mPageListener);
//        pager.setOnAdapterChangeListener(mPageListener);
//        mPager = pager;
//        updateAdapter(null, adapter);
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (mPager != null) {
//            updateAdapter(mPager.getAdapter(), null);
//            mPager.setInternalPageChangeListener(null);
//            mPager.setOnAdapterChangeListener(null);
//            mPager = null;
//        }
//    }

    void updateText(int currentItem, PagerAdapter adapter) {
        final int itemCount = adapter != null ? adapter.getCount() : 0;
        mUpdatingText = true;
//
        CharSequence text = null;
        if (currentItem == 1 && adapter != null) {
            text = adapter.getPageTitle(currentItem - 1);
            mFirstText.setText(text);
        }
        if (currentItem == 2 && adapter != null) {
            text = adapter.getPageTitle(currentItem - 1);
            mPrevText.setText(text);
        }
        if (currentItem == 3 && adapter != null) {
            text = adapter.getPageTitle(currentItem - 1);
            mCurrText.setText(text);
        }
        if (currentItem == 4 && adapter != null) {
            text = adapter.getPageTitle(currentItem - 1);
            mNextText.setText(text);
        }
        if (currentItem == 5 && adapter != null) {
            text = adapter.getPageTitle(currentItem - 1);
            mLastText.setText(text);
        }
//
//        mCurrText.setText(adapter != null ? adapter.getPageTitle(currentItem) : null);
//
//        text = null;
//        if (currentItem + 1 < itemCount && adapter != null) {
//            text = adapter.getPageTitle(currentItem + 1);
//        }
//        mNextText.setText(text);
//
        // Measure everything
        final int width = getWidth() - getPaddingLeft() - getPaddingRight();
        final int childHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (width * 0.8f),
                MeasureSpec.AT_MOST);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);
        mPrevText.measure(childWidthSpec, childHeightSpec);
        mCurrText.measure(childWidthSpec, childHeightSpec);
        mNextText.measure(childWidthSpec, childHeightSpec);

        mLastKnownCurrentPage = currentItem;

        if (!mUpdatingPositions) {
            updateTextPositions(currentItem, mLastKnownPositionOffset, false);
        }

        mUpdatingText = false;
    }

    @Override
    public void requestLayout() {
        if (!mUpdatingText) {
            super.requestLayout();
        }
    }

//    void updateAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
//        if (oldAdapter != null) {
//            oldAdapter.unregisterDataSetObserver(mPageListener);
//        }
//        if (newAdapter != null) {
//            newAdapter.registerDataSetObserver(mPageListener);
//        }
//        if (mPager != null) {
//            mLastKnownCurrentPage = -1;
//            mLastKnownPositionOffset = -1;
//            updateText(mPager.getCurrentItem(), newAdapter);
//            requestLayout();
//        }
//    }

    void updateTextPositions(int position, float positionOffset, boolean force) {
//        if (position != mLastKnownCurrentPage) {
//            updateText(position, mPager.getAdapter());
//        } else if (!force && positionOffset == mLastKnownPositionOffset) {
//            return;
//        }
//
//        mUpdatingPositions = true;
//
//        final int prevWidth = mPrevText.getMeasuredWidth();
//        final int currWidth = mCurrText.getMeasuredWidth();
//        final int nextWidth = mNextText.getMeasuredWidth();
//        final int halfCurrWidth = currWidth / 2;
//
//        final int stripWidth = getWidth();
//        final int stripHeight = getHeight();
//        final int paddingLeft = getPaddingLeft();
//        final int paddingRight = getPaddingRight();
//        final int paddingTop = getPaddingTop();
//        final int paddingBottom = getPaddingBottom();
//        final int textPaddedLeft = paddingLeft + halfCurrWidth;
//        final int textPaddedRight = paddingRight + halfCurrWidth;
//        final int contentWidth = stripWidth - textPaddedLeft - textPaddedRight;
//
//        float currOffset = positionOffset + 0.5f;
//        if (currOffset > 1.f) {
//            currOffset -= 1.f;
//        }
//        final int currCenter = stripWidth - textPaddedRight - (int) (contentWidth * currOffset);
//        final int currLeft = currCenter - currWidth / 2;
//        final int currRight = currLeft + currWidth;
//
//        final int prevBaseline = mPrevText.getBaseline();
//        final int currBaseline = mCurrText.getBaseline();
//        final int nextBaseline = mNextText.getBaseline();
//        final int maxBaseline = Math.max(Math.max(prevBaseline, currBaseline), nextBaseline);
//        final int prevTopOffset = maxBaseline - prevBaseline;
//        final int currTopOffset = maxBaseline - currBaseline;
//        final int nextTopOffset = maxBaseline - nextBaseline;
//        final int alignedPrevHeight = prevTopOffset + mPrevText.getMeasuredHeight();
//        final int alignedCurrHeight = currTopOffset + mCurrText.getMeasuredHeight();
//        final int alignedNextHeight = nextTopOffset + mNextText.getMeasuredHeight();
//        final int maxTextHeight = Math.max(Math.max(alignedPrevHeight, alignedCurrHeight),
//                alignedNextHeight);
//
//        final int vgrav = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
//
//        int prevTop;
//        int currTop;
//        int nextTop;
//        switch (vgrav) {
//            default:
//            case Gravity.TOP:
//                prevTop = paddingTop + prevTopOffset;
//                currTop = paddingTop + currTopOffset;
//                nextTop = paddingTop + nextTopOffset;
//                break;
//            case Gravity.CENTER_VERTICAL:
//                final int paddedHeight = stripHeight - paddingTop - paddingBottom;
//                final int centeredTop = (paddedHeight - maxTextHeight) / 2;
//                prevTop = centeredTop + prevTopOffset;
//                currTop = centeredTop + currTopOffset;
//                nextTop = centeredTop + nextTopOffset;
//                break;
//            case Gravity.BOTTOM:
//                final int bottomGravTop = stripHeight - paddingBottom - maxTextHeight;
//                prevTop = bottomGravTop + prevTopOffset;
//                currTop = bottomGravTop + currTopOffset;
//                nextTop = bottomGravTop + nextTopOffset;
//                break;
//        }
//
//        mCurrText.layout(currLeft, currTop, currRight,
//                currTop + mCurrText.getMeasuredHeight());
//
//        final int prevLeft = Math.min(paddingLeft, currLeft - mScaledTextSpacing - prevWidth);
//        mPrevText.layout(prevLeft, prevTop, prevLeft + prevWidth,
//                prevTop + mPrevText.getMeasuredHeight());
//
//        final int nextLeft = Math.max(stripWidth - paddingRight - nextWidth,
//                currRight + mScaledTextSpacing);
//        mNextText.layout(nextLeft, nextTop, nextLeft + nextWidth,
//                nextTop + mNextText.getMeasuredHeight());
//
//        mLastKnownPositionOffset = positionOffset;
//        mUpdatingPositions = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("Must measure with an exact width");
        }

        int childHeight = heightSize;
        int minHeight = 0;
        int padding = 0;
        final Drawable bg = getBackground();
        if (bg != null) {
            minHeight = bg.getIntrinsicHeight();
        }
        padding = getPaddingTop() + getPaddingBottom();
        childHeight -= padding;

        final int childWidthSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * 0.8f),
                MeasureSpec.AT_MOST);
        final int childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);

        mFirstText.measure(childWidthSpec, childHeightSpec);
        mLastText.measure(childWidthSpec, childHeightSpec);
        mPrevText.measure(childWidthSpec, childHeightSpec);
        mCurrText.measure(childWidthSpec, childHeightSpec);
        mNextText.measure(childWidthSpec, childHeightSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            int textHeight = mCurrText.getMeasuredHeight();
            setMeasuredDimension(widthSize, Math.max(minHeight, textHeight + padding));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPager != null) {
            updateTextPositions(mPager.getCurrentItem(), 0.f, true);
        }
    }

//    private class PageListener extends DataSetObserver implements ViewPager.OnPageChangeListener,
//            ViewPager.OnAdapterChangeListener {
//        private int mScrollState;
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            if (positionOffset > 0.5f) {
//                // Consider ourselves to be on the next page when we're 50% of the way there.
//                position++;
//            }
//            updateTextPositions(position, positionOffset, false);
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
//                // Only update the text here if we're not dragging or settling.
//                updateText(mPager.getCurrentItem(), mPager.getAdapter());
//            }
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//            mScrollState = state;
//        }
//
//        @Override
//        public void onAdapterChanged(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
//            updateAdapter(oldAdapter, newAdapter);
//        }
//
//        @Override
//        public void onChanged() {
//            updateText(mPager.getCurrentItem(), mPager.getAdapter());
//        }
//    }
}