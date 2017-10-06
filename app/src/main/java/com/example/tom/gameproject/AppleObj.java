package com.example.tom.gameproject;

import java.util.Random;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class AppleObj extends GameObj{
	private Random r=new Random();
	private Rect actRect;
	public AppleObj(Drawable drawable,Rect limitRect) {
		super(drawable);
		this.actRect=limitRect;
	}

	/**
	 * 物件移動到隨機區域
	 */
	public void random(Rect limitRect){
		this.actRect=limitRect;
		this.moveTo(actRect.left+r.nextInt(actRect.width()-this.getWidth()),actRect.top+r.nextInt(actRect.height()-this.getHeight()));
	}

	/**
	 * 物件移動到隨機區域
	 */
	public void random(){
		this.random(this.actRect);
	}
}
