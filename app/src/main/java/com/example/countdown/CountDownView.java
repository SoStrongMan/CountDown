package com.example.countdown;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 作者：徐欣
 * 日期：2017/6/12
 * 描述：倒计时控件
 */

public class CountDownView extends LinearLayout {

    private static final int SECOND = 1;
    private static final int MINUTE = 60;
    private static final int HOUR = 60 * 60;

    private TimeView mSecondView;
    private TimeView mMinuteView;
    private TimeView mHourView;
    private OnTimeEndListener onTimeEndListener;

    private Handler mHandler = new Handler();
    private int mTime;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        init(context);
    }

    private void init(Context c) {
        mSecondView = new TimeView(c);
        mSecondView.setUnitText("秒");
        mMinuteView = new TimeView(c);
        mMinuteView.setUnitText("分");
        mHourView = new TimeView(c);
        mHourView.setUnitText("时");

        //添加视图
        addView(mHourView);
        addView(mMinuteView);
        addView(mSecondView);
    }

    /**
     * 设置倒计时的时间
     *
     * @param time 时间(转化为秒，以秒为单位)
     */
    public void setTime(int time) {
        mTime = time;
    }

    /**
     * 开始倒计时
     */
    public void start() {
        if (mTime == 0) {
            mHourView.setNumber(0);
            mMinuteView.setNumber(0);
            mSecondView.setNumber(0);
            return;
        }
        //开始延时1秒进行
        mHandler.postDelayed(runnable, 1000);
    }

    /**
     * 暂停
     */
    public void pause() {
        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
        }
    }

    /**
     * 停止倒计时
     */
    public void stop() {
        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mTime--;
            int hour = mTime / HOUR;
            int minute = (mTime % HOUR) / MINUTE;
            int second = (mTime % MINUTE) / SECOND;
            mHourView.setNumber(hour);
            mMinuteView.setNumber(minute);
            mSecondView.setNumber(second);
            if (mTime == 0) {
                if (onTimeEndListener != null) {
                    onTimeEndListener.onEnd();
                }
                mHandler.removeCallbacks(runnable);
            } else {
                mHandler.postDelayed(this, 1000); //延时1秒
            }
        }
    };

    public interface OnTimeEndListener {
        void onEnd();
    }

    public void setOnTimeEndListener(OnTimeEndListener listener) {
        this.onTimeEndListener = listener;
    }

    /**
     * 计时器的一个单位
     */
    private class TimeView extends LinearLayout {

        private TextView tvTen; //十位数
        private TextView tvOne; //个位数
        private TextView tvUnit; //单位

        public TimeView(Context context) {
            this(context, null);
        }

        public TimeView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            View view = View.inflate(context, R.layout.item_count, this);
            initView(view);
        }

        private void initView(View view) {
            tvTen = (TextView) view.findViewById(R.id.tv_ten);
            tvOne = (TextView) view.findViewById(R.id.tv_one);
            tvUnit = (TextView) view.findViewById(R.id.tv_unit);
        }

        /**
         * 设置数字
         *
         * @param number 数字
         */
        public void setNumber(int number) {
            int ten = number / 10;
            int one = number % 10;
            tvTen.setText(String.valueOf(ten));
            tvOne.setText(String.valueOf(one));
        }

        /**
         * 设置单位
         *
         * @param unit 单位
         */
        public void setUnitText(String unit) {
            tvUnit.setText(unit);
        }
    }
}
