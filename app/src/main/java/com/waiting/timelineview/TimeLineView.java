package com.waiting.timelineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by:     何超
 * Create Time:   2017/10/17
 * Brief Desc:    自定义时间轴View
 */

public class TimeLineView extends View {

    private Context mContext;

    private final static int POINT_GRAVITY_TOP = 0;
    private final static int POINT_GRAVITY_CENTER = 1;
    private final static int POINT_GRAVITY_BOTTOM = 2;

    /**
     * 右侧点
     */
    private Paint mCirclePaint;

    /**
     * 点颜色
     */
    private int mCircleColor;

    /**
     * 当前选中点颜色
     */
    private int mCurrentCircleColor;

    private Drawable mCurrentDrawable;
    private Drawable mDefaultDrawable;
    private Bitmap mBitmap;
    private Drawable mLastDrawable;
    private Drawable mErrorDrawable;

    /**
     * 圆半径
     */
    private float mRadius = 20f;

    private float mCurrentRadius = mRadius;

    private float mDefaultRadius = mCurrentRadius;

    /**
     * 线条
     */
    private Paint mLinePaint;

    /**
     * 线条颜色
     */
    private int mLineColor;

    /**
     * 标题文字
     */
    private Paint mTitlePaint;

    /**
     * 标题文字颜色
     */
    private int mTitleColor;

    /**
     * 标题文字大小
     */
    private float mTitleSize;

    /**
     * 日期文字
     */
    private Paint mDatePaint;

    /**
     * 日期文字颜色
     */
    private int mDateColor;

    private int mPointGravity;

    /**
     * 日期文字大小
     */
    private float mDateSize;

    /**
     * 数据源
     */
    private List<Item> mItems;

    /**
     * 默认item间隔
     */
    private float mVerticalSpacing = 100f;

    /**
     * 画笔宽度
     */
    private float mStrokeWidth = 5f;

    /**
     * 当前步骤
     */
    private int mCurrentItem = -1, mErrorItem = -1;


    public TimeLineView(Context context) {
        this(context, null);
    }

    public TimeLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TimeLineView, 0, 0);
        mCircleColor = typedArray.getColor(R.styleable.TimeLineView_circleColor, ContextCompat.getColor(context, R.color.colorPrimary));
        mCurrentCircleColor = typedArray.getColor(R.styleable.TimeLineView_currentCircleColor, ContextCompat.getColor(context, R.color.colorAccent));
        mLineColor = typedArray.getColor(R.styleable.TimeLineView_lineColor, ContextCompat.getColor(context, R.color.colorAccent));
        mTitleColor = typedArray.getColor(R.styleable.TimeLineView_titleTextColor, ContextCompat.getColor(context, R.color.colorAccent));
        mTitleSize = typedArray.getDimension(R.styleable.TimeLineView_titleTextSize, 14);
        mDateColor = typedArray.getColor(R.styleable.TimeLineView_dateTextColor, ContextCompat.getColor(context, R.color.colorAccent));
        mDateSize = typedArray.getDimension(R.styleable.TimeLineView_dateTextSize, 12);
        mPointGravity = typedArray.getInteger(R.styleable.TimeLineView_pointGravity, POINT_GRAVITY_TOP);
        mCurrentDrawable = typedArray.getDrawable(R.styleable.TimeLineView_currentDrawable);
        if (mCurrentDrawable == null) {
            mCurrentDrawable = getResources().getDrawable(R.mipmap.ic_time_line_current);
        }

        mDefaultDrawable = typedArray.getDrawable(R.styleable.TimeLineView_drawable);
        if (mDefaultDrawable == null)
            mDefaultDrawable = getResources().getDrawable(R.mipmap.ic_time_line_time);

        mLastDrawable = typedArray.getDrawable(R.styleable.TimeLineView_lastDrawable);
        if (mLastDrawable == null)
            mLastDrawable = mDefaultDrawable;

        mErrorDrawable = typedArray.getDrawable(R.styleable.TimeLineView_errorDrawable);
        if (mErrorDrawable == null)
            mErrorDrawable = getResources().getDrawable(R.mipmap.error);

        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mStrokeWidth);

        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextSize(mTitleSize);

        mDatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDatePaint.setColor(mDateColor);
        mDatePaint.setTextSize(mDateSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = mItems.size();
        float marginLeft = 0;
        float itemHeight = 0f;
        float textMarginLeft = 10;
        float textMarginTop = 15;
        Rect rect = new Rect();
        Rect dataRect = new Rect();
        float textHeight;
        float radiusOffset;
        boolean selected;
        float currentStartY, pointStartY = 0, lineStartY = 0, lineStopY = 0, textStartY;
        for (int i = 0; i < size; i++) {
            Item item = mItems.get(i);
            String title = item.title;
            String date = item.date;

            mTitlePaint.getTextBounds(title, 0, title.length(), rect);
            mTitlePaint.getTextBounds(date, 0, date.length(), dataRect);
            textHeight = rect.height() + dataRect.height() + textMarginTop;

            if (i <= mCurrentItem || i <= mErrorItem) {
                selected = true;
                mBitmap = ((BitmapDrawable) mCurrentDrawable).getBitmap();
                mDefaultRadius = mCurrentRadius = mBitmap.getWidth() / 2;
                if (mErrorItem > 0 && i == mErrorItem) {
                    mBitmap = ((BitmapDrawable) mErrorDrawable).getBitmap();
                    mDefaultRadius = mBitmap.getWidth() / 2;
                }
            } else {
                selected = false;

                if (i == size - 1 && mLastDrawable != null) {
                    mBitmap = ((BitmapDrawable) mLastDrawable).getBitmap();
                } else
                    mBitmap = ((BitmapDrawable) mDefaultDrawable).getBitmap();
                mDefaultRadius = mBitmap.getWidth() / 2;
            }

            radiusOffset = mCurrentRadius - mDefaultRadius;
            mRadius = mDefaultRadius + radiusOffset;

            currentStartY = itemHeight * i;

            switch (mPointGravity) {
                case POINT_GRAVITY_TOP:
                    pointStartY = currentStartY;
                    break;
                case POINT_GRAVITY_CENTER:
                    pointStartY = currentStartY + textHeight / 2 - (selected ? mCurrentRadius : mDefaultRadius);
                    break;
                case POINT_GRAVITY_BOTTOM:
                    pointStartY = currentStartY + textHeight - (selected ? mCurrentRadius * 2 : mDefaultRadius * 2);
                    break;
            }

            textStartY = currentStartY - radiusOffset + rect.height();

            if (mBitmap != null && !mBitmap.isRecycled()) {
                canvas.drawBitmap(mBitmap, marginLeft + radiusOffset,
                        pointStartY - (selected ? 0 : radiusOffset), mCirclePaint);
            }

            canvas.drawText(title, marginLeft + mRadius * 2 + textMarginLeft, textStartY, mTitlePaint);
            canvas.drawText(date, marginLeft + mRadius * 2 + textMarginLeft, textStartY + dataRect.height() + textMarginTop, mDatePaint);

            itemHeight = (textHeight + mVerticalSpacing);

            if (i < size - 1) {
                if (i == mCurrentItem) {
                    radiusOffset = mCurrentDrawable.getIntrinsicWidth() / 2 - mDefaultDrawable.getIntrinsicWidth() / 2;
                } else if (i == mErrorItem) {
                    radiusOffset = mCurrentDrawable.getIntrinsicWidth() / 2 - mErrorDrawable.getIntrinsicWidth() / 2;
                }


                switch (mPointGravity) {
                    case POINT_GRAVITY_TOP:
                        lineStartY = currentStartY + mRadius * 2 + mStrokeWidth + radiusOffset + (selected ? 0 : -mDefaultRadius);
                        lineStopY = lineStartY + itemHeight - mRadius * 2 - radiusOffset * 2 + (selected ? -5 : radiusOffset * 2 + 1);
                        break;
                    case POINT_GRAVITY_CENTER:
                        lineStartY = currentStartY + textHeight / 2 + (selected ? mCurrentRadius + mStrokeWidth : mDefaultRadius) + radiusOffset;
                        lineStopY = lineStartY + itemHeight - (selected ? mCurrentRadius * 2 : mDefaultRadius * 2) - radiusOffset - mStrokeWidth;
                        break;
                    case POINT_GRAVITY_BOTTOM:
                        lineStartY = currentStartY + textHeight + mStrokeWidth + radiusOffset + (selected ? 0 : -radiusOffset);
                        lineStopY = lineStartY + itemHeight - mRadius * 2 - radiusOffset + (selected ? 1 : radiusOffset + 1);
                        break;
                }
                canvas.drawLine(marginLeft + mRadius, lineStartY - radiusOffset - mStrokeWidth, marginLeft + mRadius, lineStopY, mLinePaint);
            }
        }
    }

    public void setCurrentItem(int position) {
        if (position < 0) {
            mCurrentItem = -1;
        } else if (position >= mItems.size()) {
            mCurrentItem = mItems.size() - 1;
        } else {
            mCurrentItem = position;
        }
        invalidate();
    }

    public void setErrorItem(int position) {
        if (position >= mItems.size()) {
            mErrorItem = mItems.size() - 1;
        } else mErrorItem = position;

        invalidate();
    }

    public void setItems(List<Item> items) {
        mItems.addAll(items);
        invalidate();
    }


    public static class Item {

        public String title;
        public String date;
        public boolean checked;

        public Item(String title, String date, boolean checked) {
            this.title = title;
            this.date = date;
            this.checked = checked;
        }
    }
}
