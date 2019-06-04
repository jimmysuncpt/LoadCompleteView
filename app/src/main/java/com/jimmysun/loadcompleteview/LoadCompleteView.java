package com.jimmysun.loadcompleteview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 加载及完成View
 *
 * @author SunQiang
 * @since 2018/7/24
 */
public class LoadCompleteView extends View {
    private static final int DEFAULT_LENGTH = 74;
    private static final int DEFAULT_STROKE_WIDTH = 4;

    private static final int TIME_LOADING = 1500;
    private static final int TIME_LOADING_NOT_SEEN = 200;
    private static final int TIME_COMPLETE = 900;

    private static final int STATUS_DISMISS = 0;
    private static final int STATUS_LOADING = 1;
    private static final int STATUS_COMPLETE = 2;

    private Context mContext;
    private Paint mPaint;
    private RectF mRectF;
    // 线条宽度（px）
    private float mStrokeWidth;
    // 状态
    private int mStatus;

    private long mStartTime;
    private Interpolator mInterpolator;

    private Callback mCallback;
    private boolean mCallbackEnable;

    public LoadCompleteView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadCompleteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadCompleteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context.getApplicationContext();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mRectF = new RectF();
        mInterpolator = new AccelerateDecelerateInterpolator();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadCompleteView);
        setColor(typedArray.getColor(R.styleable.LoadCompleteView_color, 0xff000000));
        setStrokeWidth(typedArray.getDimension(R.styleable.LoadCompleteView_strokeWidth,
                DensityUtils.dip2px(context, DEFAULT_STROKE_WIDTH)));
        typedArray.recycle();
    }

    /**
     * 设置线条颜色
     *
     * @param color 线条颜色
     */
    public void setColor(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
        }
    }

    /**
     * 设置线条宽度
     *
     * @param width 线条宽度（px）
     */
    public void setStrokeWidth(float width) {
        if (mPaint != null) {
            mStrokeWidth = width;
            mPaint.setStrokeWidth(mStrokeWidth);
        }
    }

    /**
     * 设置状态回调
     *
     * @param callback 回调
     */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     * 开始等待
     */
    public void startLoading() {
        mStatus = STATUS_LOADING;
        mStartTime = System.currentTimeMillis();
        mCallbackEnable = true;
        invalidate();
    }

    /**
     * 完成
     */
    public void complete() {
        mStatus = STATUS_COMPLETE;
        mStartTime = System.currentTimeMillis();
        mCallbackEnable = true;
        invalidate();
    }

    /**
     * 消失
     */
    public void dismiss() {
        mStatus = STATUS_DISMISS;
        mCallbackEnable = true;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int length = DensityUtils.dip2px(mContext, DEFAULT_LENGTH);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(length, length);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(length, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, length);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStatus == STATUS_DISMISS) {
            return;
        }
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int centerX = getPaddingLeft() + width / 2;
        int centerY = getPaddingTop() + height / 2;
        float radius = Math.min(width, height) / 2f;
        long deltaTime = System.currentTimeMillis() - mStartTime;
        if (mStatus == STATUS_LOADING) {
            updateRectF(centerX, centerY, radius);
            float deltaAngle;
            float halfTime = TIME_LOADING / 2f;
            if (deltaTime < halfTime) {
                // 画出圆
                deltaAngle = 315 * mInterpolator.getInterpolation(deltaTime / halfTime);
                canvas.drawArc(mRectF, 225 - deltaAngle, deltaAngle, false, mPaint);
            } else if (deltaTime < TIME_LOADING) {
                // 收回圆
                deltaAngle = 315 * mInterpolator.getInterpolation((deltaTime - halfTime) /
                        halfTime);
                canvas.drawArc(mRectF, -90, 315 - deltaAngle, false, mPaint);
            } else if (deltaTime >= TIME_LOADING + TIME_LOADING_NOT_SEEN) {
                mStartTime = System.currentTimeMillis();
            }
        } else if (mStatus == STATUS_COMPLETE) {
            float quarterTime = TIME_COMPLETE / 4f;
            float halfTime = TIME_COMPLETE / 2f;
            float deltaXY;
            // 对号前半部分
            if (deltaTime < quarterTime) {
                deltaXY = radius / 2 * deltaTime / quarterTime;
            } else {
                deltaXY = radius / 2;
            }
            canvas.drawLine(centerX - radius / 2, centerY - radius / 6, centerX - radius / 2 +
                    deltaXY, centerY - radius / 6 + deltaXY, mPaint);
            // 对号后半部分
            if (deltaTime > quarterTime) {
                if (deltaTime < halfTime) {
                    // 余弦定理 + 一元二次方程算出来的
                    deltaXY = (3 + (float) Math.sqrt(158)) / 18 * (radius - mStrokeWidth / 2) *
                            (deltaTime - quarterTime) / quarterTime;
                } else {
                    deltaXY = (3 + (float) Math.sqrt(158)) / 18 * (radius - mStrokeWidth / 2);
                }
                canvas.drawLine(centerX, centerY + radius / 3, centerX + deltaXY, centerY +
                        radius / 3 - deltaXY, mPaint);
            }
            // 画圈，起点325°，余弦定理所得，再加3°和对号连上
            if (deltaTime > halfTime) {
                float deltaAngle;
                if (deltaTime < TIME_COMPLETE) {
                    deltaAngle = 328 * mInterpolator.getInterpolation((deltaTime - halfTime) /
                            halfTime);
                } else {
                    deltaAngle = 328;
                }
                updateRectF(centerX, centerY, radius);
                canvas.drawArc(mRectF, 328 - deltaAngle, deltaAngle, false, mPaint);
            }
            // 结束绘制
            if (deltaTime > TIME_COMPLETE) {
                if (mCallback != null && mCallbackEnable) {
                    mCallbackEnable = false;
                    mCallback.onComplete();
                }
                return;
            }
        }
        invalidate();
    }

    private void updateRectF(int centerX, int centerY, float radius) {
        mRectF.left = centerX - radius + mStrokeWidth / 2;
        mRectF.top = centerY - radius + mStrokeWidth / 2;
        mRectF.right = centerX + radius - mStrokeWidth / 2;
        mRectF.bottom = centerY + radius - mStrokeWidth / 2;
    }

    public interface Callback {
        void onComplete();
    }
}
