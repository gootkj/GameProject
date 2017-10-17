package com.example.tom.gameproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    SurfaceView gameSurfaceView;
    SurfaceHolder surfaceHolder;
    Thread gameThread;
    Boolean isGameThreadStop = true;
    GameObj backimg;
    int gameFPS = 25;
    KeyHandler keyHandler = new KeyHandler();
    PowerManager.WakeLock wakeLock;
    drawAction nowDrawWork;
    Resources rs ;

    GameApplication app;
    //螢幕Size
    int Left, Top, Right , Bottom;
    Player p_1,p_2;
    BallObj ball;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (GameApplication) getApplication();
        // 隱藏狀態列
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隱藏視窗標題
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 防止手機因手持方向不同 而觸發螢幕方向旋轉
        //setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);

        // 電源管理服務取得
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "GameSnake PowerControl");

    }

    @Override
    protected void onStart() {
        super.onStart();
        rs = getResources();

        gameSurfaceView = new SurfaceView(this);
        surfaceHolder = gameSurfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceDestroyed(SurfaceHolder arg0) {
            }

            public void surfaceCreated(SurfaceHolder arg0) {
                if (app.gameStat == GameApplication.action.ready) {
                    readyGame();
                }else {
                    // 經由Activity返回載入時
                    draw(nowDrawWork);
                    openOptionsMenu();

                }
            }

            public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                                       int arg3) {

            }
        });
        setContentView(gameSurfaceView);
    }

    /**
     * 電源控制 防止進入休眠狀態切換
     */
    protected void powerControl(boolean needWake) {
        if (needWake && !wakeLock.isHeld()) {
            wakeLock.acquire();
        } else if (!needWake && wakeLock.isHeld()) {
            wakeLock.release();
        }

    }

    @Override
    protected void onPause() {
        pauseGame();
        super.onPause();
    };

    protected static final int MENU_Resume = Menu.FIRST;
    protected static final int MENU_Reset = Menu.FIRST + 1;
    protected static final int MENU_Quit = Menu.FIRST + 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_Resume, 0, "繼續");
        menu.add(0, MENU_Reset, 0, "重新開始");
        menu.add(0, MENU_Quit, 0, "離開");
        return super.onCreateOptionsMenu(menu);
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_Resume:
                resumeGame();
                break;
            case MENU_Reset:
                readyGame();
                break;
            case MENU_Quit:
                gameExit();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        pauseGame();
        return super.onMenuOpened(featureId, menu);
    };

    void gameExit() {
        gameThreadStop();
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {

            }
        }
        finish();// 結束遊戲
    }

    //設定螢幕Size
    public void playerSetSize(int Left, int Top, int Right , int Bottom){
        this.Left = Left;
        this.Top = Top;
        this.Right = Right;
        this.Bottom = Bottom;

        //圖片和活動範圍
        p_1 = new Player(rs.getDrawable(R.drawable.rd1), Left,(Bottom/2)+100 ,Right,Bottom);
        p_2 = new Player(rs.getDrawable(R.drawable.d1), Left,Top,Right,(Bottom/2) - 100);

    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (nowDrawWork == drawAction.game) {
            p_1.update(event);
            p_2.update(event);
        }
        return true;
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        keyHandler.keyDown(keyCode);
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        keyHandler.keyUp(keyCode);
        return super.onKeyUp(keyCode, event);
    }

    public void gameThreadStart() {
        isGameThreadStop = false;
        powerControl(true);
        if (gameThread == null) {
            gameThread = new Thread(gameRun);
            gameThread.start();
        } else if (!gameThread.isAlive()) {
            gameThread = new Thread(gameRun);
            gameThread.start();
        }
    }

    public void gameThreadStop() {
        isGameThreadStop = true;
        powerControl(false);
    }

    // 準備遊戲
    void readyGame() {
        //背景
        backimg = new GameObj(rs.getDrawable(R.drawable.backimg));
        SurfaceView sv = gameSurfaceView;

        backimg.setRect(new Rect(sv.getLeft(), sv.getTop(), sv
                .getRight(), sv.getBottom()));

        //設定上下兩個玩家
        playerSetSize(sv.getLeft(), sv.getTop(), sv.getRight(), sv.getBottom());

        gameThreadStop();
        nowDrawWork = drawAction.ready;

        ball = new BallObj(MainActivity.this, backimg.getRect(),app);

//        app.setTime(System.currentTimeMillis() + 3000);

        gameThreadStart();
    }

    // 開始遊戲
    void startGame() {
        app.gameStat = GameApplication.action.game;
        nowDrawWork = drawAction.game;
    }

    // 暫停遊戲
    void pauseGame() {
        gameThreadStop();
        if (nowDrawWork != drawAction.over) {
            //gameStat.timePause();
            draw(drawAction.pause);
        }

    }

    // 繼續遊戲
    void resumeGame() {
        if (nowDrawWork != drawAction.over) {
            gameThreadStart();
            //gameStat.timeResume();
        }
    }

    Runnable gameRun = new Runnable() {
        public void run() {
            long delayTime = 1000 / gameFPS;
            while (!isGameThreadStop) {
                long startTime = System.currentTimeMillis();

                if (nowDrawWork == drawAction.game)
                {gameUpdate();}
                draw(nowDrawWork);
                long endTime = System.currentTimeMillis();
                long waitTime = delayTime - (endTime - startTime);
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    };

    boolean isKeyDown(int keyCode) {
        return keyHandler.isKeyDown(keyCode);
    }

    /**
     * 遊戲更新
     */
    void gameUpdate() {
        // 觸控事件處理
        if (true) {
            p_1.move();
            p_2.move();
        }
        ball.move(p_1.board,p_2.board);

        ball.update();

        // 判斷是否結束遊戲
        if (app.gameStat == GameApplication.action.over) {
            nowDrawWork = drawAction.over;
        }
    }

    // 畫面繪圖種類
    enum drawAction {
        ready, game, pause, over
    }

    // 畫面繪圖處理
    void draw(drawAction action) {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas(null);
            synchronized (surfaceHolder) {
                draw(action, canvas);
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    // 畫面繪圖函數選擇
    void draw(drawAction action, Canvas canvas) {
        switch (action) {
            case ready:
                drawReady(canvas);
                break;
            case game:
                drawGame(canvas);
                break;
            case pause:
                drawPause(canvas);
                break;
            case over:
                drawOver(canvas);
                break;
        }
    }

    // 畫準備開始
    void drawReady(Canvas canvas) {
        //clear(canvas);
//        Paint pt = new Paint();
//        pt.setTextAlign(Paint.Align.CENTER);
//        pt.setARGB(255, 0, 0, 255);
//        pt.setTextSize(60);
//        canvas.drawText(app.getCountdownTime() + "秒後遊戲開始-", backimg
//                .centerX(), backimg.centerY(), pt);
//        if (app.isTimeOver())
//        {startGame();}
        //倒數放到menu頁
        startGame();
        app.setStartTime();
    }

    // 畫遊戲中
    void drawGame(Canvas canvas) {
        clear(canvas);
        p_1.board.draw(canvas);
        p_2.board.draw(canvas);
        ball.draw(canvas);
        //gameStat.draw(canvas);
    }

    // 畫暫停
    void drawPause(Canvas canvas) {
        draw(nowDrawWork, canvas);
        Paint pt = new Paint();
        pt.setARGB(30, 0, 0, 100);
        canvas.drawRect(backimg.getRect(), pt);
        pt.setTextAlign(Paint.Align.CENTER);
        pt.setARGB(150, 200, 200, 200);
        pt.setTextSize(50);
        canvas.drawText("-遊戲暫停-", backimg.centerX(), backimg.centerY(), pt);
    }

    // 畫遊戲結束
    void drawOver(Canvas canvas) {
        gameThreadStop();
        //回上頁
        Intent it = new Intent();
        setResult(RESULT_OK, it);
        finish();

    }

    void clear(Canvas canvas) {
//        Paint p = new Paint();
//        p.setARGB(100, 0, 0, 0);
        backimg.draw(canvas);
    }

}