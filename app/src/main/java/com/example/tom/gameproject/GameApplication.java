package com.example.tom.gameproject;

import android.app.Application;

/**
 * Created by student on 2017/10/16.
 */

public class GameApplication extends Application {

    // 畫面繪圖種類
    public enum action {
        ready, game, pause, over
    }
    public action gameStat = action.ready;
    public long startTime;
    public int winner = 0;

    public void setStartTime(){
        this.startTime=System.currentTimeMillis();
    }
    public int getGameTime(){
        return (int) ((System.currentTimeMillis() - startTime) / 1000);
    }
}
