package com.example.tom.gameproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class BallObj {

    private class exGameObj extends GameObj {

        /**
         *下次更新時的座標點位置
         */
        public PointF nextMove = new PointF();

        /**
         * 歷史的座標點位置
         */
        public PointF[] logPath = new PointF[3];

        public exGameObj(Drawable drawable) {
            super(drawable);
            init();
        }

        public exGameObj(exGameObj gameObj, Drawable drawable) {
            super(gameObj, drawable);
            init();
        }

        /**
         * 物件初始化
         */
        private void init() {
            for (int i = 0; i < logPath.length; i++) {
                logPath[i] = new PointF();
            }
        }

        /**
         *進行座標點更新
         */
        public void updataMove() {

            // 取得位置移動向量
            float dx = nextMove.x - logPath[0].x;
            float dy = nextMove.y - logPath[0].y;

            // 歷史座標更新
            for (int i = logPath.length - 1; i > 0; i--) {
                logPath[i].set(logPath[i - 1].x, logPath[i - 1].y);
            }
            logPath[0].set(nextMove.x, nextMove.y);

            // 座標點更新
            this.moveTo(nextMove.x - this.getWidth() / 2, nextMove.y
                    - this.getHeight() / 2);

            // 判斷移動向量距離大於5以上進行角度更新
            if (dx * dx + dy * dy > 4 * 4) {
                this.angle = (float) (Math.atan2(dy, dx) * 180 / Math.PI);
            }
        }
    }

    /**
     * 頭部物件
     */
    private exGameObj head;

    /**
     * 資源檔
     */
    private Resources rs;

    /**
     * 活動限制範圍
     */
    private Rect actRect;

    /**
     * 目標移動向量X
     */
    private float dstVectorX = 1;

    /**
     * 目標移動向量Y
     */
    private float dstVectorY = 0;

    private boolean moveFlag = true;

    public BallObj(Activity content, Rect actRect) {
        this.actRect = actRect;
        rs = content.getResources();
        // 頭部影像資源
        Drawable d_head = rs.getDrawable(R.drawable.head);
        // 身體影像資源
        Drawable d_body = rs.getDrawable(R.drawable.body);
        // 尾部影像資源
        Drawable d_tail = rs.getDrawable(R.drawable.tail);

        //初始化貪食蛇物件
        head = new exGameObj(d_head);
        init(head);
        Random r=new Random();
        head.angle = r.nextInt(360);
        this.dstVectorX = (float) Math.cos(head.angle * Math.PI / 180);
        this.dstVectorY = (float) Math.sin(head.angle * Math.PI / 180);
    }

    /**
     * 設定物件起始位置
     */
    private void init(exGameObj obj) {
        float x = actRect.centerX();
        float y = actRect.centerY();
        obj.nextMove.set(x, y);
        for (int i = 0; i < obj.logPath.length; i++) {
            obj.logPath[i].set(x, y);
        }
    }

    /**
     * 畫出貪食蛇
     */
    public void draw(Canvas canvas) {
        head.draw(canvas);
    }

    /**
     * 進行貪食蛇更新
     */
    public void update() {
        //更新頭部位置
        head.updataMove();
    }

    //更新貪食蛇物件節點的移動
    private void updataMove(exGameObj fd, exGameObj bk) {
        bk.updataMove();
        PointF fwp = fd.logPath[fd.logPath.length - 1];
        bk.nextMove.set(fwp.x, fwp.y);
    }

    /**
     * 範圍縮放長寬調整
     * @param rect
     * @param scaleX
     * @param scaleY
     */
    private void scaleRect(Rect rect, int scaleX, int scaleY) {
        rect.set(rect.left - scaleX, rect.top - scaleY, rect.right + scaleX,
                rect.bottom + scaleY);
    }

    /**
     * 範圍縮放長寬調整
     */
    private void scaleRect(RectF rect, int scaleX, int scaleY) {
        rect.set(rect.left - scaleX, rect.top - scaleY, rect.right + scaleX,
                rect.bottom + scaleY);
    }

    /**
     * 得到一個-180~180之間的角度
     */
    private float getAngle(float angle, float addAngle) {
        angle += addAngle;
        angle %= 360;
        if (angle > 180)
            angle -= 360;
        if (angle < -180)
            angle += 360;
        return angle;
    }

    /**
     * 設定移動向量
     * @param dx
     * 移動向量X
     * @param dy
     * 移動向量Y
     */
    public void move(float dx, float dy) {
        this.dstVectorX = dx;
        this.dstVectorY = dy;
        //目標旋轉角度
        float rotateAngle = getAngleByXY(dx, dy);

        //設定頭部方向
        head.angle = rotateAngle;

        //取得頭部下次更新座標
        double dreg = head.angle * Math.PI / 180;
        int moveDistance=60;
        dx = (float) Math.cos(dreg) * moveDistance;
        dy = (float) Math.sin(dreg) * moveDistance;
        float ndx = head.logPath[0].x + dx;
        float ndy = head.logPath[0].y + dy;

        //取得邊緣範圍
        RectF limitRect = new RectF(actRect);

        //取得物件可移動範圍
        scaleRect(limitRect, -head.getWidth() / 2, -head.getHeight() / 2);

        //進行邊緣碰撞偵測調整
        if (!limitRect.contains(ndx, ndy)) {
            boolean isTouchEdge = false;
            if (ndx < limitRect.left) {//左邊邊緣偵測
                head.angle = 180-head.angle;

                ndx = limitRect.left;
                isTouchEdge = true;
            }
            if (ndx > limitRect.right) {//右邊邊緣偵測
                head.angle = 180-head.angle;

                ndx = limitRect.right;
                isTouchEdge = true;
            }

            if (ndy < limitRect.top) {//頂部邊緣偵測
                head.angle = -head.angle;

                ndy = limitRect.top;
                isTouchEdge = true;
            }
            if (ndy > limitRect.bottom) {//底部邊緣偵測
                head.angle = -head.angle;

                ndy = limitRect.bottom;
                isTouchEdge = true;
            }

            if (isTouchEdge) {
                //調整目標向量
                this.dstVectorX = (float) Math.cos(head.angle * Math.PI / 180);
                this.dstVectorY = (float) Math.sin(head.angle * Math.PI / 180);
            }
        }
        //下次頭部移動點設置
        head.nextMove.set(ndx, ndy);
        moveFlag = true;
    }

    //移動之前方向
    public void move() {
        move(this.dstVectorX, this.dstVectorY);
    }

    //取得直角坐標角度
    private float getAngleByXY(float dx, float dy) {
        return (float) (Math.atan2(dy, dx) * 180 / Math.PI);
    }

    //判斷是否有吃到蘋果
    public boolean isEatApple(GameObj apple) {
        return Rect.intersects(apple.getRect(), head.getRect());
    }

    public void reFlection(){

    }
}