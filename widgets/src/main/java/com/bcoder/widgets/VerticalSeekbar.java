package com.bcoder.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VerticalSeekbar extends View {
    private Context mContext ;

    private Drawable mThumb;
    private Drawable mBar;
    private Drawable mBorder;
    private Drawable mBarBackground;

    private TextPaint mTextPaint;

    private Rect mBorderRect = new Rect();
    private Paint mBorderPaint = new Paint();

    private Rect mThumbRect = new Rect();
    private Boolean mThumbDown = false;

    private int mMin = 0;
    private int mMax = 100;
    private int mValue = 66;

    private String LOG_TAG = "VerticalSeekbar";

    public VerticalSeekbar(Context context) {
        super(context);
        mContext = context;
        init(null, 0);
    }

    public VerticalSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs, 0);
    }

    public VerticalSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.VerticalSeekbar, defStyle, 0);

        mBorderPaint.setStrokeWidth(1);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        if(a.hasValue(R.styleable.VerticalSeekbar_minValue)){
            mMin = a.getInteger(R.styleable.VerticalSeekbar_minValue, 0);
        }

        if(a.hasValue(R.styleable.VerticalSeekbar_maxValue)){
            mMax = a.getInteger(R.styleable.VerticalSeekbar_maxValue, 100);
        }

        if(mMax < mMin){
            int tmp = mMin;
            mMin = mMax;
            mMax = tmp;
        }

        if(mValue > mMax){
            mValue = (int) (mMax * 0.66);
        }

        if (a.hasValue(R.styleable.VerticalSeekbar_drawableBorder)){
            mBorder = a.getDrawable(R.styleable.VerticalSeekbar_drawableBorder);
            mBorder.setCallback(this);

            if(mBorder instanceof ColorDrawable){
                mBorderPaint.setColor(((ColorDrawable) mBorder).getColor());
            }
        }

        if (a.hasValue(R.styleable.VerticalSeekbar_drawableBarBackground)){
            mBarBackground = a.getDrawable(R.styleable.VerticalSeekbar_drawableBarBackground);
            mBarBackground.setCallback(this);
        } else {
            if(mBorder == null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBarBackground = mContext.getDrawable(R.drawable.default_bar_background);
                } else {
                    mBarBackground = mContext.getResources().getDrawable(R.drawable.default_bar_background);
                }
            }
        }

        if (a.hasValue(R.styleable.VerticalSeekbar_drawableThumb)) {
            mThumb = a.getDrawable(R.styleable.VerticalSeekbar_drawableThumb);
            mThumb.setCallback(this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mThumb = mContext.getDrawable(R.drawable.defulat_thumb);
            } else {
                mThumb = mContext.getResources().getDrawable(R.drawable.defulat_thumb);
            }
        }

        if(a.hasValue(R.styleable.VerticalSeekbar_drawableBar)){
            mBar = a.getDrawable(R.styleable.VerticalSeekbar_drawableBar);
            mBar.setCallback(this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBar = mContext.getDrawable(R.drawable.default_bar);
            } else {
                mBar = mContext.getResources().getDrawable(R.drawable.default_bar);
            }
        }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        int thumbPos = (contentHeight - getThumbHeight()) * mValue / mMax ;
        thumbPos = paddingTop + contentHeight - thumbPos;

        Rect barBkgRect = new Rect((getWidth() - mBarBackground.getIntrinsicWidth()) / 2,
            0, (getWidth() + mBarBackground.getIntrinsicWidth()) / 2, getBottom());
        mBarBackground.setBounds(barBkgRect);
        mBarBackground.draw(canvas);

        if(mBar != null){
            int barTop = 0;
            if(mValue == mMax){
                barTop = thumbPos - getThumbHeight();
            } else if (mValue == mMin){
                barTop = thumbPos ;
            } else {
                barTop = thumbPos - getThumbHeight() / 2;
            }
            mBar.setBounds(paddingLeft, barTop, paddingLeft + contentWidth, paddingTop + contentHeight);
            mBar.draw(canvas);
        }

        mThumbRect.set(getWidth() / 2 - getThumbWidth() /  2 , thumbPos - getThumbHeight(),
                getWidth() / 2 + getThumbWidth() /  2, thumbPos );

        if (mThumb != null) {
            mThumb.setBounds(mThumbRect);
            mThumb.draw(canvas);
        }

        if(mBorder != null) {
            mBorderRect.set(1, 1, getWidth(), getHeight());
            canvas.drawRect(mBorderRect, mBorderPaint);
        }
    }

    private int getThumbWidth(){
        return getWidth() ;
    }

    private int getThumbHeight(){
        if(mThumb == null){
            return 10;
        }
        return getThumbWidth() * mThumb.getIntrinsicHeight() / mThumb.getIntrinsicWidth();
    }

    private int getBarContentHeight(){
        return getHeight() - getPaddingTop() - getPaddingBottom() - getThumbHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (widthMode){
            case MeasureSpec.AT_MOST:
                width = 30;
                break;
        }

        switch (heightMode){
            case MeasureSpec.AT_MOST:
                height = 100;
                break;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mThumbRect.contains((int)event.getX(), (int)event.getY())){
                    mThumbDown = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                if(mThumbDown && mThumb != null){
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        mThumbDown = false;
                    }

                    int tmpY = (int) event.getY();
                    if(event.getY() < 0){
                        tmpY = 0;
                    }
                    if(event.getY() > getHeight() - getPaddingBottom() - getThumbHeight()){
                        tmpY = getHeight() - getPaddingBottom() - getThumbHeight();
                    }

                    int contentHeight = getHeight() - getPaddingTop() - getPaddingBottom() - getThumbHeight() ;
                    mValue = (int) ((contentHeight - tmpY) * (mMax - mMin ) / contentHeight);
                    if (mOnValueChanged != null){
                        mOnValueChanged.onValueChange(mValue);
                    }
                    invalidate();

                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public Drawable getThumb() {
        return mThumb;
    }

    public void setThumb(Drawable exampleDrawable) {
        mThumb = exampleDrawable;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int mValue) {
        if(mValue > mMax){
            this.mValue = mMax;
            return;
        }

        if(mValue < mMin){
            mValue = mMin;
            return;
        }

        this.mValue = mValue;
    }

    public int getMinValue() {
        return mMin;
    }

    public void setMinValue(int mMin) {
        this.mMin = mMin;
        if(mValue < mMin){
            mValue = mMin;
        }
    }

    public int getMaxValue() {
        return mMax;
    }

    public void setMaxValue(int mMax) {
        this.mMax = mMax;
        if(mValue > mMax){
            mValue = mMax;
        }
    }

    private OnValueChanged mOnValueChanged;

    public OnValueChanged getmOnValueChanged() {
        return mOnValueChanged;
    }

    public void setmOnValueChanged(OnValueChanged mOnValueChanged) {
        this.mOnValueChanged = mOnValueChanged;
    }

    public interface OnValueChanged{
        void onValueChange(int value);
    }
}
