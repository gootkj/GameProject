package com.example.tom.gameproject;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.Random;

public class BoardObj extends GameObj{
    private PointF point = new PointF();
    private int speed = 60;
    private Random r=new Random();
    private Rect actRect;
    //活動範圍
    int Left, Top, Right , Bottom;

    public BoardObj(Drawable drawable, int Left, int Top, int Right , int Bottom) {
        super(drawable);

        this.Left = Left;
        this.Top = Top;
        this.Right = Right;
        this.Bottom = Bottom;

        point.x = Left;
        point.y = Top;
        this.moveTo(point.x,point.y);

    }

    /**
     * 物件移動()
     */
    @Override
    public void moveTo(int newLeft, int newTop) {
        newLeft = newLeft - (this.getWidth()/2);
        newTop = newTop - (this.getHeight()/2);
        super.moveTo(newLeft, newTop);
    }

    /**
     * 物件移動直接(速度)
     */
    public void move(float x,float y){
        //算向量
        float dx = x - point.x;
        float dy = y - point.y;
        float l = (float) Math.sqrt((dx*dx) + (dy * dy));
        //太接近不移動
        if(l > speed) {
            point.x += (dx / l * speed);
            point.y += (dy / l * speed);

            //超出範圍修正
            if (point.x < Left + (this.getWidth()/2) )
            {
                point.x = Left + (this.getWidth()/2);
            }
            else if(point.x > Right - (this.getWidth()/2))
            {
                point.x = Right - (this.getWidth()/2);
            }
            if(point.y < Top + (this.getHeight()/2))
            {
                point.y = Top + (this.getHeight()/2);
            }
            else if (point.y > Bottom - (this.getHeight()/2))
            {
                point.y = Bottom - (this.getHeight()/2);
            }

            this.moveTo(point.x,point.y);
        }

    }
    public PointF getPoint(){
        PointF tempPoint = new PointF();
        tempPoint.x = point.x;
        tempPoint.y = point.y;
        return tempPoint;
    }
//    public int getLeft(){
//        return Left;
//    }
//    public int getTop(){
//        return Top;
//    }
//    public int getRight(){
//        return Right;
//    }
//    public int getBottom(){
//        return Bottom;
//    }
}
