package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class PongTwoPlayer extends Activity implements View.OnTouchListener{

    PongSurface surfaceView;
    float x1, x2, sX1, y;
    Bitmap greenball;
    DisplayMetrics metrics;
    private static final int INVALID_POINTER_ID = -1;
    private int p1PointerID = INVALID_POINTER_ID;
    private int p2PointerID = INVALID_POINTER_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        surfaceView = new PongSurface(this);
        surfaceView.setOnTouchListener(this);
        x1 = x2 = sX1 = y = 0;
        greenball = BitmapFactory.decodeResource(getResources(), R.drawable.greenball);
        setContentView(surfaceView);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event){

        try{
            Thread.sleep(50);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        final int action = event.getAction();
        switch(action&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                if(event.getY() > metrics.heightPixels / 2){
                    x1 = event.getX();
                    p1PointerID = event.getPointerId(0);
                }else if(event.getY() < metrics.heightPixels / 2){
                    x2 = event.getX();
                    p2PointerID = event.getPointerId(0);
                }

                //activePointerID = event.getPointerId(0);
                //TODO Fix the touch event so that it supports multitouch.
                //Inspiration http://android-developers.blogspot.dk/2010/06/making-sense-of-multitouch.html
                //Edit to create bleeding edge branch
                break;
            case MotionEvent.ACTION_MOVE:
                if(p1PointerID != -1){
                    final int p1PointerIndex = event.findPointerIndex(p1PointerID);
                    x1 = event.getX(p1PointerIndex);
                }else if(p2PointerID != -1){
                    final int p2PointerIndex = event.findPointerIndex(p2PointerID);
                    x2 = event.getX(p2PointerIndex);
                }
                break;
            case MotionEvent.ACTION_UP:
                p1PointerID = INVALID_POINTER_ID;
                p2PointerID = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                p1PointerID = INVALID_POINTER_ID;
                p2PointerID = INVALID_POINTER_ID;
                break;

        }
        /*
        if(event.getY() > metrics.heightPixels / 2){
            x1 = event.getX();
        }else if(event.getY() < metrics.heightPixels / 2){
            x2 = event.getX();
        }

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                sX1 = event.getX();

                break;
            case MotionEvent.ACTION_UP:
                y = 0;
                break;
        }
        */
        return true;
    }


    @Override
    protected void onPause(){
        super.onPause();
        surfaceView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        surfaceView.resume();
    }

    public class PongSurface extends SurfaceView implements Runnable{

        SurfaceHolder holder;
        Thread thread = null;
        boolean isRunning = false;
        Paint greenPaint = new Paint();

        public PongSurface(Context context){
            super(context);
            holder = getHolder();
        }

        public void pause(){
            isRunning = false;
            while(true){
                try{
                    thread.join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            thread = null;
        }

        public void resume(){
            isRunning = true;
            thread = new Thread(this);
            thread.start();
            greenPaint.setColor(Color.GREEN);
        }

        @Override
        public void run(){
            while(isRunning){
                if(!holder.getSurface().isValid())
                    continue;

                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(0, 0, 0);
                /*
                TODO implement this, but for a rectangle
                if(x1!=0){
                    canvas.drawBitmap(greenball, x1-greenball.getWidth()/2, canvas.getHeight()-100, null);
                }
                if(x2!=0){
                    canvas.drawBitmap(greenball, x2-greenball.getWidth()/2, 100, null);
                }
                */
                //if()
                if(x1 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, canvas.getHeight() - 100, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() - 50, greenPaint);
                }
                if(x2 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, 100, canvas.getWidth() / 2 + canvas.getWidth() / 8, 50, greenPaint);

                }
                if(x1 != 0){
                    canvas.drawRect(x1 - canvas.getWidth() / 8, canvas.getHeight() - 100, x1 + canvas.getWidth() / 8, canvas.getHeight() - 50, greenPaint);
                }
                if(x2 != 0){
                    canvas.drawRect(x2 - canvas.getWidth() / 8, 100, x2 + canvas.getWidth() / 8, 50, greenPaint);
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
