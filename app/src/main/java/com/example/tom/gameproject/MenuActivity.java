package com.example.tom.gameproject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    private GameApplication app;
    private final int REQUEST_CODE_PINGPONG = 123;
    private ImageView iv_p1,iv_p2;
    private AnimationDrawable animationDrawable_p1,animationDrawable_p2;
    private Handler m_handler = new Handler();
    Runnable stopTask = new StopTask();
    private LinearLayout btn_layout;
    private LinearLayout text_layout;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //取得遊戲狀態
        app = (GameApplication) getApplication();
        btn_layout = (LinearLayout)findViewById(R.id.btn_layout);
        text_layout = (LinearLayout)findViewById(R.id.text_layout);
        tv = (TextView)findViewById(R.id.textView);

        iv_p1 = (ImageView)findViewById(R.id.img_p1);
        iv_p1.setVisibility(View.GONE);
        iv_p1.setBackgroundResource(R.drawable.p1_animation);//設定動畫資源
        animationDrawable_p1 = (AnimationDrawable)iv_p1.getBackground();//取得控制

        iv_p2 = (ImageView)findViewById(R.id.img_p2);
        iv_p2.setVisibility(View.GONE);
        iv_p2.setBackgroundResource(R.drawable.p2_animation);//設定動畫資源
        animationDrawable_p2 = (AnimationDrawable)iv_p2.getBackground();//取得控制

        System.out.println("OK");
    }

    public void winner(){
        iv_p1.setVisibility(View.GONE);
        iv_p2.setVisibility(View.GONE);
        switch (app.winner){
            case 1:
                iv_p1.setVisibility(View.VISIBLE);
                animation10secs(animationDrawable_p1);
                break;
            case 2:
                iv_p2.setVisibility(View.VISIBLE);
                animation10secs(animationDrawable_p2);
                break;
        }
    }

    //勝利撥3秒動畫
    private void animation10secs(AnimationDrawable animationDrawable) {
        int delayMillis = 3 * 1000;

        boolean result = m_handler.postDelayed(stopTask,delayMillis);

        animationDrawable.start();
    }

    private class StopTask implements Runnable {
        @Override
        public void run() {
            animationStop();
        }
    }

    //結束動畫
    public void animationStop(){
        animationDrawable_p1.stop();
        animationDrawable_p2.stop();
    }

    public void click(View view) {
        int id = view.getId();
        System.out.println(id);
        switch (id){
            case R.id.btn_go:
                goClick();
                break;
            case R.id.btn_quit:
                finish();
                break;
        }
    }

    //開始按鈕被按下時
    public void goClick(){
        //倒數
        showText();
        new CountDownTimer(3000, 100) {
            public void onTick(long millisUntilFinished) {
                tv.setText(((millisUntilFinished / 1000) + 1)  + "秒後遊戲開始");
            }

            public void onFinish() {
//                        tv1.setText("done!");
                gameStart();
            }
        }.start();
    }

    //中間位置顯示按鈕
    public void showButton(){
        btn_layout.setVisibility(View.VISIBLE);
        text_layout.setVisibility(View.GONE);
    }

    //中間位置顯示倒數文字
    public void showText(){
        btn_layout.setVisibility(View.GONE);
        text_layout.setVisibility(View.VISIBLE);
    }

    //進入遊戲頁面
    public void gameStart(){
        Intent it = new Intent();
        it.setClass(MenuActivity.this,MainActivity.class);
        app.gameStat = GameApplication.action.ready;
        startActivityForResult(it, REQUEST_CODE_PINGPONG);
    }

    //遊戲頁面返回 勝利者顯示動畫
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PINGPONG)
        {
            if (resultCode == RESULT_OK)
            {
                winner();
                showButton();
            }
        }
    }
}
