package com.example.countdown;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CountDownView mCountDownView;
    private TextView startBtn; //开始
    private TextView pauseBtn; //暂停
    private TextView stopBtn; //停止

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        bindEvent();
    }

    private void initView() {
        mCountDownView = (CountDownView) findViewById(R.id.cd_view);
        startBtn = (TextView) findViewById(R.id.tv_start_btn);
        pauseBtn = (TextView) findViewById(R.id.tv_pause_btn);
        stopBtn = (TextView) findViewById(R.id.tv_stop_btn);

        startBtn.setEnabled(true);
        pauseBtn.setEnabled(false);
        stopBtn.setEnabled(false);
    }

    private void bindEvent() {
        //开始按钮
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        //暂停按钮
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = pauseBtn.getText().toString();
                if ("暂停".equals(name)) {
                    pauseBtn.setText("继续");
                    mCountDownView.pause();
                } else {
                    pauseBtn.setText("暂停");
                    mCountDownView.start();
                }
            }
        });
        //结束按钮
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                mCountDownView.stop();
            }
        });
        //结束倒计时的监听事件
        mCountDownView.setOnTimeEndListener(new CountDownView.OnTimeEndListener() {
            @Override
            public void onEnd() {
                startBtn.setEnabled(true);
                pauseBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                showToast("倒计时结束了");
            }
        });
    }

    /**
     * 显示弹框
     */
    private void showDialog() {
        final EditText edt = new EditText(MainActivity.this);
        edt.setBackground(null);
        edt.setPadding(
                SizeUtils.dp2px(MainActivity.this, 16),
                SizeUtils.dp2px(MainActivity.this, 8),
                SizeUtils.dp2px(MainActivity.this, 16),
                SizeUtils.dp2px(MainActivity.this, 8));
        edt.setHint("请输入时间");
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("设置时间(单位：秒)")
                .setView(edt)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = edt.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            showToast("请输入时间");
                            return;
                        }
                        if (content.startsWith("0")) {
                            showToast("输入的时间不合法");
                            return;
                        }
                        startBtn.setEnabled(false);
                        pauseBtn.setEnabled(true);
                        stopBtn.setEnabled(true);
                        mCountDownView.setTime(Integer.parseInt(content));
                        mCountDownView.start();
                    }
                })
                .show();
    }

    /**
     * 显示吐司信息
     *
     * @param content 内容
     */
    private void showToast(String content) {
        Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownView.stop();
    }
}
