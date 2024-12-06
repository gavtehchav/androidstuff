package com.example.goodname.workingandroidstuff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private Canvas canvas;
    private Bitmap bitmap;;
    private boolean isMoving = true;
    private float velocity = 250; // 250 px/s
    private float xPos = 10, yPos = 10;
    private int frameW = 600, frameH = 600;
    private int frameCount = 7;
    private int currentFrame = 0;
    private long fps;
    private long timeThisFrame;
    private long lastFrameChangeTime = 0;
    private int frameLengthInMS = 100;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private RectF whereToDraw = new RectF(xPos, yPos, xPos + frameW, frameH);

    private boolean presseddown;

    private SensorManager manager;


    public void spritehomie(int spritedaddy){
        bitmap = BitmapFactory.decodeResource(getResources(), spritedaddy);
        bitmap = Bitmap.createScaledBitmap(bitmap, frameW * frameCount, frameH, false);
    }

    private int potheat = 0;
    private boolean flicker = false;
    private int shownheat = 0;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        spritehomie(R.drawable.run);
        spritehomie(R.drawable.pot);

        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        boolean hasAccel = !manager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty();
        
        // 1. Find the sensors
        // 2. register
        // 3. implement the sensor listener interface
//        sensorManager.registerListener()

    }


    @Override
    public void run() {
        while (playing)
        {
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1)
            {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        if(isMoving)
        {
            //xPos = xPos + velocity / fps;
            //if (xPos > getWidth())
            //{
           //     yPos += frameH;
           //     xPos = 10;
           // }
           // if (yPos + frameH > getHeight())
           // {
           //     yPos = 10;
           // }
        }
    }

    public void manageCurrentFrame()
    {
        long time = System.currentTimeMillis();
        if (isMoving)
        {
            if (time > lastFrameChangeTime + frameLengthInMS)
            {
                lastFrameChangeTime = time;
                flicker = !flicker;
                shownheat = potheat;


                if (flicker)
                {
                    if (shownheat == 0);
                    else if (shownheat < 6 )
                        shownheat = shownheat +1;
                    if (potheat == 6)
                        shownheat = 5;
                }
            }
        }
        if (potheat > 6)
            potheat = 0;
        frameToDraw.left = shownheat * frameW;
        frameToDraw.right = frameToDraw.left + frameW;
       // potToDraw.left = currentFrame * potW;
       // potToDraw.right = potToDraw.left + potW;
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid())
        {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            whereToDraw.set(xPos, yPos, xPos+frameW, yPos + frameH);
            manageCurrentFrame();
            canvas.drawBitmap(bitmap, frameToDraw, whereToDraw, null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("GameView", "Interrupted");
        }
    }

    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN :
                potheat = potheat + 1;
                presseddown = true;
                Log.v("GameView", event.getRawX() + " " + event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                Log.v("GameView", event.getRawX() + " " + event.getRawY());
                xPos = event.getRawX() - 50;
                yPos = event.getRawY() - 100;
                break;
            default:
                break;
        }
        return true;
    }
}