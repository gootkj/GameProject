package com.example.tom.gameproject;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.Random;

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
        //發球角度
        Random r=new Random();
        int angle = 0;
        while (angle % 180 < 30){
            angle = r.nextInt(360);
        }
        head.angle = angle;

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
    public void move(float dx, float dy, BoardObj b_1 , BoardObj b_2) {
        PointF tempPoint = new PointF();
        //預測下個座標點
        tempPoint = getNextPoint(dx,dy);

        //如果碰到牆壁改變方向
        tempPoint = touchEdge(tempPoint);

        //碰到玩家
        tempPoint = touchPlayer(tempPoint,b_1);
        tempPoint = touchPlayer(tempPoint,b_2);

        //下次頭部移動點設置
        head.nextMove.set(tempPoint.x, tempPoint.y);
        moveFlag = true;
    }

    //移動之前方向
    public void move(BoardObj b_1,BoardObj b_2) {
        move(this.dstVectorX, this.dstVectorY,b_1,b_2);
    }

    //取得直角坐標角度
    private float getAngleByXY(float dx, float dy) {
        return (float) (Math.atan2(dy, dx) * 180 / Math.PI);
    }

    //預測下個座標點
    public PointF getNextPoint(float dx, float dy){
        PointF tempPoint = new PointF();

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
        tempPoint.x = head.logPath[0].x + dx;
        tempPoint.y = head.logPath[0].y + dy;

        return tempPoint;
    }

    //如果下一步會碰到牆壁就改變方向
    public PointF touchEdge(PointF tempPoint){
        //取得邊緣範圍
        RectF limitRect = new RectF(actRect);

        //取得物件可移動範圍
        scaleRect(limitRect, -head.getWidth() / 2, -head.getHeight() / 2);

        //進行邊緣碰撞偵測調整
        if (!limitRect.contains(tempPoint.x, tempPoint.y)) {
            boolean isTouchEdge = false;
            if (tempPoint.x < limitRect.left) {//左邊邊緣偵測
                head.angle = 180-head.angle;

                tempPoint.x = limitRect.left;
                isTouchEdge = true;
            }
            if (tempPoint.x > limitRect.right) {//右邊邊緣偵測
                head.angle = 180-head.angle;

                tempPoint.x = limitRect.right;
                isTouchEdge = true;
            }

            if (tempPoint.y < limitRect.top) {//頂部邊緣偵測
                head.angle = -head.angle;

                tempPoint.y = limitRect.top;
                isTouchEdge = true;
            }
            if (tempPoint.y > limitRect.bottom) {//底部邊緣偵測
                head.angle = -head.angle;

                tempPoint.y = limitRect.bottom;
                isTouchEdge = true;
            }

            if (isTouchEdge) {
                //調整目標向量
                this.dstVectorX = (float) Math.cos(head.angle * Math.PI / 180);
                this.dstVectorY = (float) Math.sin(head.angle * Math.PI / 180);
            }
        }

        return tempPoint;
    }

    //如果下一步會碰到玩家
    public PointF touchPlayer(PointF tempPoint, BoardObj borad){
        //假設球移動到下個位置
        exGameObj tempHead = new exGameObj(rs.getDrawable(R.drawable.head));
        tempHead.move(tempPoint.x - (tempHead.getWidth()/2),tempPoint.y - (tempHead.getHeight()/2));

        //碰到板子
        if(Rect.intersects(tempHead.getRect(),borad.getRect()))
        {

            float dx = tempPoint.x - borad.getPoint().x;
            float dy = tempPoint.y - borad.getPoint().y;
            //目標旋轉角度
            float rotateAngle = getAngleByXY(dx, dy);

            //設定頭部方向
            head.angle = rotateAngle;
//            System.out.println(rotateAngle);
            //取得頭部下次更新座標
//            double dreg = head.angle * Math.PI / 180;
//            dx = (float) Math.cos(dreg);
//            dy = (float) Math.sin(dreg);
//
//            int i = 100;
//            //一直找到沒有接觸為止
//            while (tempHead.intersect(borad)) {
//
//                i++;
//                tempPoint.x = borad.getPoint().x + dx * i ;
//                tempPoint.y = borad.getPoint().y + dy * i ;
//                tempHead.move(tempPoint.x,tempPoint.y);
////                System.out.println(tempHead.intersect(borad));
//            }

        }
        this.dstVectorX = (float) Math.cos(head.angle * Math.PI / 180);
        this.dstVectorY = (float) Math.sin(head.angle * Math.PI / 180);
        return tempPoint;
    }
}