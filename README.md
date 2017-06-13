# CountDown
基于Handler+Runnable的倒计时控件（时分秒），支持暂停、继续、结束倒计时、监听倒计时结束事件功能

## 整体思路
`注：可用Android封装好的CountDownTimer进行倒计时操作，这里是方便理解，就自己写了一个计时器`

1.首先定义一个基本单位，如：秒，以秒为单位输入一个数字，比如说`3610秒`，也就是`1小时10秒`，在倒计时控件上显示的应该就是`01时00分10秒`。<br>

2.接下来就是分发这个`3610`到各个位置上，这里可以理解为时、分、秒是三个团体，每个团体里有两个成员，`3610/3600`取整就是分发到时团体的数，`3610%3600/60`
先取余再取整就是分发到分团体的数，`3610%60/1`先取余再取整就是分发到秒团体的数（这里除以1的是因为秒就是基本单位）。<br>

3.之后就是把分发到每个团体的数字进行拆分，分给每个成员即可。分发到时团体的数是`1`，`1/10`取整即是十位的数，`1%10`取余即是个位的数，
后面每个团体都是这样进行。<br>

4.最后只要在开启一个计时器，传入的倒计时时间每次减1，Runnable中进行延时1秒操作即可实现倒计时功能。

## 关键代码解析
```
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
```
上述代码是一个内部类，相当于一个团体，团体中的`setNumber`和`setUnitText`方法都是分发到每个成员的方法。

```
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
```
总共添加了3个团体，也就是时、分、秒，具体的分发操作在Runnable中进行。
```
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
```
在这Runnable中将计算得到的数字各自通过`setNumber`方法分发到相应的团体中，代码中的`mTime`是通过该类的公用方法`setTime`从外部传进来的倒计时时间，
最后调用`start`方法，就能开启一个计时器，开始倒计时了。
```
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
```
`注意：上述的start方法要在setTime方法后执行，否则拿不到倒计时的时间`
