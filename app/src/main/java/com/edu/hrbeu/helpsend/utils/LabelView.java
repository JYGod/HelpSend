package com.edu.hrbeu.helpsend.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.edu.hrbeu.helpsend.R;

public class LabelView extends View {
    private String mTextContent;//文字内容
    private int mTextColor;//文字颜色
    private float mTextSize;//文字大小
    private boolean mTextBold;//文字是否加粗
    private boolean mFillTriangle;//是否三角形填充
    private boolean mTextAllCaps;//文字是否支持全部大写
    private int mBackgroundColor;//文字背景颜色
    private float mMinSize;//View所在矩形最小宽高
    private float mPadding;//文字上下padding
    private int mGravity;//LabelView方向
    private static final int DEFAULT_DEGREES = 45;//默认角度

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();

    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        obtainAttributes(context, attrs);
        mTextPaint.setTextAlign(Paint.Align.CENTER);//实现水平居中
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LabelView);
        mTextContent = ta.getString(R.styleable.LabelView_lv_text);
        mTextColor = ta.getColor(R.styleable.LabelView_lv_text_color, Color.parseColor("#ffffff"));
        mTextSize = ta.getDimension(R.styleable.LabelView_lv_text_size, sp2px(11));
        mTextBold = ta.getBoolean(R.styleable.LabelView_lv_text_bold, true);
        mTextAllCaps = ta.getBoolean(R.styleable.LabelView_lv_text_all_caps, true);
        mFillTriangle = ta.getBoolean(R.styleable.LabelView_lv_fill_triangle, false);
        mBackgroundColor = ta.getColor(R.styleable.LabelView_lv_background_color, Color.parseColor("#FF4081"));
        mMinSize = ta.getDimension(R.styleable.LabelView_lv_min_size, mFillTriangle ? dp2px(35) : dp2px(50));
        mPadding = ta.getDimension(R.styleable.LabelView_lv_padding, dp2px(3.5f));
        mGravity = ta.getInt(R.styleable.LabelView_lv_gravity, Gravity.TOP | Gravity.LEFT);
        ta.recycle();
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        invalidate();
    }

    public void setText(String text) {
        mTextContent = text;
        requestLayout();
        //#### invalidate();
    }
    public void setTextSize(float textSize) {
        mTextSize = sp2px(textSize);
        requestLayout();
        //#### invalidate();
    }

    public void setTextBold(boolean textBold) {
        mTextBold = textBold;
        invalidate();
    }

    public void setFillTriangle(boolean fillTriangle) {
        mFillTriangle = fillTriangle;
        invalidate();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        mTextAllCaps = textAllCaps;
        invalidate();
    }

    public void setBgColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        invalidate();
    }

    public void setMinSize(float minSize) {
        mMinSize = dp2px(minSize);
        invalidate();
    }

    public void setPadding(float padding) {
        mPadding = dp2px(padding);
        invalidate();
    }

    /**
     * Gravity.TOP | Gravity.LEFT
     * Gravity.TOP | Gravity.RIGHT
     * Gravity.BOTTOM | Gravity.LEFT
     * Gravity.BOTTOM | Gravity.RIGHT
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public String getText() {
        return mTextContent;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public boolean isTextBold() {
        return mTextBold;
    }

    public boolean isFillTriangle() {
        return mFillTriangle;
    }

    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    public int getBgColor() {
        return mBackgroundColor;
    }

    public float getMinSize() {
        return mMinSize;
    }

    public float getPadding() {
        return mPadding;
    }

    public int getGravity() {
        return mGravity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = getHeight();

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setFakeBoldText(mTextBold); //true为粗体，false为非粗体
        mBackgroundPaint.setColor(mBackgroundColor);

        /**
         * Baseline是基线，在Android中，文字的绘制都是从Baseline处开始的，Baseline往上至字符“最高处”的距离我们称之为ascent（上坡度），
         * Baseline往下至字符“最低处”的距离我们称之为descent（下坡度）；
         *
         * Leading文档说的很含糊，其实是上一行字符的descent到下一行的ascent之间的距离
         Top指的是指的是最高字符到baseline的值，即ascent的最大值
         同上，bottom指的是最下字符到baseline的值，即descent的最大值

         * 因为基线上方为负，所以ascent和top的值都是负数，而且top要大于ascent，原因是要为符号留出位置。
         */
        //取得文字的高度
        float textHeight = mTextPaint.descent() - mTextPaint.ascent();
        if (mFillTriangle) {
            if (mGravity == (Gravity.TOP | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, 0);
                mPath.lineTo(0, size);
                mPath.lineTo(size, 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, -DEFAULT_DEGREES, canvas, true);
            } else if (mGravity == (Gravity.TOP | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(size, 0);
                mPath.lineTo(0, 0);
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, DEFAULT_DEGREES, canvas, true);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, size);
                mPath.lineTo(0, 0);
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, DEFAULT_DEGREES, canvas, false);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(size, size);
                mPath.lineTo(0, size);
                mPath.lineTo(size, 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawTextWhenFill(size, -DEFAULT_DEGREES, canvas, false);
            }
        } else {
            double delta = (textHeight + mPadding * 2) * Math.sqrt(2);
            if (mGravity == (Gravity.TOP | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, (float) (size - delta));
                mPath.lineTo(0, size);
                mPath.lineTo(size, 0);
                mPath.lineTo((float) (size - delta), 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, -DEFAULT_DEGREES, canvas, textHeight, true);
            } else if (mGravity == (Gravity.TOP | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(0, 0);
                mPath.lineTo((float) delta, 0);
                mPath.lineTo(size, (float) (size - delta));
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, DEFAULT_DEGREES, canvas, textHeight, true);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.LEFT)) {
                mPath.reset();
                mPath.moveTo(0, 0);
                mPath.lineTo(0, (float) delta);
                mPath.lineTo((float) (size - delta), size);
                mPath.lineTo(size, size);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, DEFAULT_DEGREES, canvas, textHeight, false);
            } else if (mGravity == (Gravity.BOTTOM | Gravity.RIGHT)) {
                mPath.reset();
                mPath.moveTo(0, size);
                mPath.lineTo((float) delta, size);
                mPath.lineTo(size, (float) delta);
                mPath.lineTo(size, 0);
                mPath.close();
                canvas.drawPath(mPath, mBackgroundPaint);

                drawText(size, -DEFAULT_DEGREES, canvas, textHeight, false);
            }
        }
    }

    private void drawText(int size, float degrees, Canvas canvas, float textHeight, boolean isTop) {
        canvas.save();
        canvas.rotate(degrees, size / 2f, size / 2f);
        float delta = isTop ? -(textHeight + mPadding * 2) / 2 : (textHeight + mPadding * 2) / 2;
        float textBaseY = size / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2 + delta;
        canvas.drawText(mTextAllCaps ? mTextContent.toUpperCase() : mTextContent,
                getPaddingLeft() + (size - getPaddingLeft() - getPaddingRight()) / 2, textBaseY, mTextPaint);
        canvas.restore();
    }

    private void drawTextWhenFill(int size, float degrees, Canvas canvas, boolean isTop) {
        canvas.save();
        canvas.rotate(degrees, size / 2f, size / 2f);
        float delta = isTop ? -size / 4 : size / 4;
        float textBaseY = size / 2 - (mTextPaint.descent() + mTextPaint.ascent()) / 2 + delta;
        canvas.drawText(mTextAllCaps ? mTextContent.toUpperCase() : mTextContent,
                getPaddingLeft() + (size - getPaddingLeft() - getPaddingRight()) / 2, textBaseY, mTextPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredWidth);
    }

    /**
     * 确定View宽度大小
     */
    private int measureWidth(int widthMeasureSpec) {
        int result;
        /**
         * 边界参数——widthMeasureSpec和heightMeasureSpec ，效率的原因以整数的方式传入。在它们使用之前，
         * 首先要做的是使用MeasureSpec类的静态方法getMode和getSize来译解
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        /**
         * 如果是AT_MOST，specSize 代表的是最大可获得的空间；
         如果是EXACTLY，specSize 代表的是精确的尺寸；
         如果是UNSPECIFIED，对于控件尺寸来说，没有任何参考意义。
         */
        if (specMode == MeasureSpec.EXACTLY) {//大小确定直接使用
            result = specSize;
        } else {
            /**
             * 设置为 wrap_content时，容器传进去的是AT_MOST
             */
            int padding = getPaddingLeft() + getPaddingRight();
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextSize(mTextSize);
            float textWidth = mTextPaint.measureText(mTextContent + "");//测量文字宽
            result = (int) ((padding + (int) textWidth) * Math.sqrt(2));//sqrt  平方根
            //如果父视图的测量要求为AT_MOST,即限定了一个最大值,则再从系统建议值和自己计算值中去一个较小值
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }

            result = Math.max((int) mMinSize, result);
        }

        return result;
    }

    protected int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}