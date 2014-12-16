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
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //Gets the pointerIndex of this action
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                //Gets the pointerId of this event
                final int pointerId = event.getPointerId(pointerIndex);
                //Determent if we already have a p1- or p2PointerId and if the press was on the top half or bottom
                //It then assigns the pointerId to the player
                if(p1PointerID != -1 && event.getY(pointerIndex)>metrics.heightPixels/2){
                    x1 = event.getX(pointerIndex);
                    p1PointerID = pointerId;
                }else if(p2PointerID != -1 && event.getY(pointerIndex)<metrics.heightPixels/2){
                    x2 = event.getX(pointerIndex);
                    p2PointerID = pointerId;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                //Gets the pointerIndex of this action
                final int pointerIndex2 = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                //Gets the pointerId of this event
                final int pointerId2 = event.getPointerId(pointerIndex2);

                //If the movement was from one of the active pointers it moves that paddle
                if(p1PointerID == pointerId2){
                    x1 = event.getX(pointerIndex2);
                }else if(p2PointerID == pointerId2){
                    x2 = event.getX(pointerIndex2);
                }
                /*
                if(p1PointerID != -1){
                    final int p1PointerIndex = event.findPointerIndex(p1PointerID);
                    x1 = event.getX(p1PointerIndex);
                }else if(p2PointerID != -1){
                    final int p2PointerIndex = event.findPointerIndex(p2PointerID);
                    x2 = event.getX(p2PointerIndex);
                }
                */
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //Gets the pointerIndex of this action
                final int pointerIndex3 = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                //Gets the pointerId of this event
                final int pointerId3 = event.getPointerId(pointerIndex3);

                //If the movement was from one of the active pointers it moves that paddle
                if(p1PointerID == pointerId3){
                    p1PointerID = INVALID_POINTER_ID;
                }else if(p2PointerID == pointerId3){
                    p2PointerID = INVALID_POINTER_ID;
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
            //TODO is the while loop and break statement necessary
            //while(true){
                try{
                    thread.join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                //break;
            //}
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
