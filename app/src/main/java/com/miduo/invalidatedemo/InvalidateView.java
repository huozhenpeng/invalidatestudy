package com.miduo.invalidatedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class InvalidateView extends View {
    public InvalidateView(Context context) {
        this(context,null);
    }

    public InvalidateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public InvalidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Path mPath;
    private void init() {
        mPath=new Path();
        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

        mSlideHandler = new SlideHandler();
    }


    private  float pointX;
    private  float pointY;

    private int mViewWidth;
    private int mViewHeight;

    private Paint mPaint;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth=w;
        mViewHeight=h;
        mMoveValid = mViewWidth * 1 / 100F;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("abc","onDraw");
        // 重绘时重置路径
        mPath.reset();

        // 绘制底色
        canvas.drawColor(Color.WHITE);

        /*
         * 如果坐标点在右下角则不执行绘制
         */
        if (pointX == 0 && pointY == 0) {
            return;
        }

        /*
         * 额，这个该怎么注释好呢……根据图来
         */
        float mK = mViewWidth - pointX;
        float mL = mViewHeight - pointY;

        // 需要重复使用的参数存值避免重复计算
        float temp = (float) (Math.pow(mL, 2) + Math.pow(mK, 2));

        /*
         * 计算短边长边长度
         */
        float sizeShort = temp / (2F * mK);
        float sizeLong = temp / (2F * mL);

        /*
         * 生成路径
         */
        mPath.moveTo(pointX, pointY);
        mPath.lineTo(mViewWidth, mViewHeight - sizeLong);
        mPath.lineTo(mViewWidth - sizeShort, mViewHeight);
        mPath.close();

        // 绘制路径
        canvas.drawPath(mPath, mPaint);
    }

    private float mCurPointX;
    private float mMoveValid;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
         * 判断当前事件类型
         */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:// 触摸屏幕时
                // 获取当前事件点x坐标
                mCurPointX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:// 滑动时
                float SlideDis = mCurPointX - event.getX();
                if (Math.abs(SlideDis) > mMoveValid) {
                    pointX=event.getX();
                    pointY=event.getY();
                    // 获取触摸点的x坐标
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:// 触点抬起时

                /*
                 * 获取当前事件点
                 */
                float x = event.getX();
                float y = event.getY();
                mStart_BR_X=x;
                mStart_BR_Y=y;
                slide();
                break;
        }
        return true;
    }

    private SlideHandler mSlideHandler;
    private float mStart_BR_X;
    private float mStart_BR_Y;
    /**
     * 计算滑动参数变化
     */
    private void slide() {
        /*
         * 如果x坐标恒小于控件宽度
         */
        if (pointX < mViewWidth) {
            // 则让x坐标自加
            pointX++;

            // 并根据x坐标的值重新计算y坐标的值
            pointY = mStart_BR_Y + ((pointX - mStart_BR_X) * (mViewHeight - mStart_BR_Y)) / (mViewWidth - mStart_BR_X);

            // 让SlideHandler处理重绘
            mSlideHandler.sleep(1);
        }
    }


    /**
     * 处理滑动的Handler
     */
    @SuppressLint("HandlerLeak")
    private class SlideHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // 循环调用滑动计算
            InvalidateView.this.slide();

            // 重绘视图
            InvalidateView.this.invalidate();
        }

        /**
         * 延迟向Handler发送消息实现时间间隔
         *
         * @param delayMillis
         *            间隔时间
         */
        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }
}
