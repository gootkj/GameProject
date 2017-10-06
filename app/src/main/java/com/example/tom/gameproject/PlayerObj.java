package com.example.tom.gameproject;

import java.util.Random;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class PlayerObj extends GameObj{
    private Random r=new Random();
    private Rect actRect;
    public PlayerObj(Drawable drawable, Rect limitRect) {
        super(drawable);
        this.actRect=limitRect;
    }

    /**
     * 物件移動到隨機區域
     */
    public void random(Rect limitRect){
        this.actRect=limitRect;
        this.moveTo(actRect.left+r.nextInt(actRect.width()-this.getWidth()),actRect.top+r.nextInt(actRect.height()-this.getHeight()));
//        System.out.println(actRect.width());
//        System.out.println(this.getWidth());
//        System.out.println(actRect.height());
//        System.out.println(this.getHeight());
    }

    /**
     * 物件移動到隨機區域
     */
    public void random(){
        this.random(this.actRect);
    }
    /**
     * 物件移動到隨機區域
     */
    @Override
    public void moveTo(int newLeft, int newTop) {
        newLeft = newLeft - (this.getWidth()/2);
        newTop = newTop - (this.getHeight()/2);
        super.moveTo(newLeft, newTop);
    }

    /**
     * 物件移動到隨機區域
     */
    public void moveTo(Rect limitRect){
        this.actRect=limitRect;
        this.moveTo(actRect.left+r.nextInt(actRect.width()-this.getWidth()),actRect.top+r.nextInt(actRect.height()-this.getHeight()));
    }
}
