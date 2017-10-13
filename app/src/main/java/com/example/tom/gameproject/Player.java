package com.example.tom.gameproject;

import android.graphics.drawable.Drawable;

/**
 * Created by student on 2017/10/11.
 */

public class Player {
    BoardObj board;
    FingerPoint fingerPoint;

    public Player(Drawable drawable, int Left, int Top, int Right , int Bottom) {
        board = new BoardObj(drawable,Left, Top, Right , Bottom);
        fingerPoint = new FingerPoint(Left,Top,Right,Bottom);
    }

    public void update(android.view.MotionEvent event){
        fingerPoint.update(event);
    }

    public void move(){
        board.move(fingerPoint.pointX,fingerPoint.pointY);
    }
}
