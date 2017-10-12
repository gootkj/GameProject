package com.example.tom.gameproject;

import android.graphics.drawable.Drawable;

/**
 * Created by student on 2017/10/11.
 */

public class Player {
    BoardObj board;

    public Player(Drawable drawable, int Left, int Top, int Right , int Bottom) {
        board = new BoardObj(drawable,Left, Top, Right , Bottom);
    }
}
