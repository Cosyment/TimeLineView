package com.waiting.timelineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by:     何超
 * Create Time:   2017/10/17
 * Brief Desc:    自定义时间轴View
 */

public class TimeLineView extends View {

    private Context mContext;

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

    /**
     * 圆半径
     */
    private float mRadius = 20f;

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
    private float mVerticalSpacing = 60f;

    /**
     * 画笔宽度
     */
    private float mStrokeWidth = 5f;

    /**
     * 当前步骤
     */
    private int mCurrentItem = -1;


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
        float marginLeft = 40;
        float itemHeight = 0f;
        Rect rect = new Rect();
        float textHeight;
        for (int i = 0; i < size; i++) {
            Item item = mItems.get(i);
            if (mCurrentItem <= i) {
                mCirclePaint.setColor(mCurrentCircleColor);
            } else {
                mCirclePaint.setColor(mCircleColor);
            }

            canvas.drawCircle(marginLeft + mRadius, mRadius + itemHeight * i + mStrokeWidth, mRadius, mCirclePaint);
            String title = item.title;
            String date = item.date;
            mTitlePaint.getTextBounds(title, 0, title.length(), rect);
            textHeight = rect.height();
            canvas.drawText(title, marginLeft + mRadius * 2 + 10, textHeight + itemHeight * i, mTitlePaint);
            canvas.drawText(date, marginLeft + mRadius * 2 + 10, textHeight * 2 + 10 + itemHeight * i, mDatePaint);

            itemHeight = (textHeight * 2 + mVerticalSpacing);

            if (i < size - 1) {
                float startY = itemHeight * i + mRadius * 2 + mStrokeWidth * 2 - 3;
                float stopY = startY + itemHeight - mRadius * 2;
                canvas.drawLine(marginLeft + mRadius, startY, marginLeft + mRadius, stopY, mLinePaint);
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

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
