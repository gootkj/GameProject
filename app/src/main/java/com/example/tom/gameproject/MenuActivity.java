package com.example.tom.gameproject;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MenuActivity extends AppCompatActivity {

    private ImageView iv_p1,iv_p2;
    private AnimationDrawable animationDrawable_p1,animationDrawable_p2;
    private Handler m_handler = new Handler();
    Runnable stopTask = new StopTask();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        iv_p1 = (ImageView)findViewById(R.id.img_p1);
        iv_p1.setVisibility(View.GONE);
        iv_p1.setBackgroundResource(R.drawable.p1_animation);//設定動畫資源
        animationDrawable_p1 = (AnimationDrawable)iv_p1.getBackground();//取得控制

        iv_p2 = (ImageView)findViewById(R.id.img_p2);
        iv_p2.setVisibility(View.GONE);
        iv_p2.setBackgroundResource(R.drawable.p2_animation);//設定動畫資源
        animationDrawable_p2 = (AnimationDrawable)iv_p2.getBackground();//取得控制

        winner();
    }

    public void winner(){
        iv_p1.setVisibility(View.GONE);
        iv_p2.setVisibility(View.GONE);
        int winner = 1;
        switch (winner){
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

    //撥10秒動畫
    private void animation10secs(AnimationDrawable animationDrawable) {
        int delayMillis = 10 * 1000;

        boolean result = m_handler.postDelayed(stopTask,delayMillis);

        animationDrawable.start();
    }

    private class StopTask implements Runnable {
        @Override
        public void run() {
            animationDrawable_p1.stop();
            animationDrawable_p2.stop();
        }
    }
}
