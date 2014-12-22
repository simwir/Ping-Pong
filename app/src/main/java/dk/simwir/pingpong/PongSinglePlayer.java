package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;


public class PongSinglePlayer extends Activity implements View.OnTouchListener{

    PongSurface surfaceView;
    float x1, x2, bx, by, br;
    DisplayMetrics metrics;
    int p1Score, p2Score;
    boolean ballMoveDown, ballMoveRight;
    int ballSpeed = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        surfaceView = new PongSurface(this);
        surfaceView.setOnTouchListener(this);
        x1 = x2 = bx = by = 0;
        setContentView(surfaceView);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    private void createBall(){
        Random r = new Random();
        resetBall();
        ballSpeed = metrics.heightPixels / 140;
        ballMoveDown = r.nextInt(2) == 1;
        ballMoveRight = r.nextInt(2) == 1;
    }

    private void resetBall(){
        bx = metrics.widthPixels / 2;
        by = metrics.heightPixels / 2;
        br = metrics.heightPixels / 50;
    }

    @Override
    protected void onStart(){
        super.onStart();
        createBall();
        p1Score = p2Score = 0;
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

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                x1 = event.getX();
                break;
        }


        return true;
    }


    public class PongSurface extends SurfaceView implements Runnable{

        SurfaceHolder holder;
        Thread thread = null;
        boolean isRunning = false;
        Paint greenPaint = new Paint();
        Paint whitePaint = new Paint();

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
            whitePaint.setColor(Color.WHITE);
            whitePaint.setTextAlign(Paint.Align.CENTER);
            whitePaint.setTextSize(metrics.widthPixels / 4);

        }

        @Override
        public void run(){
            while(isRunning){
                if(!holder.getSurface().isValid())
                    continue;

                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(0, 0, 0);
                canvas.drawText(Integer.toString(p1Score), canvas.getWidth() / 2, canvas.getHeight() / 4 * 3 - metrics.widthPixels / 8, whitePaint);
                canvas.drawText(Integer.toString(p2Score), canvas.getWidth() / 2, canvas.getHeight() / 4 + metrics.widthPixels / 8, whitePaint);
                if(x1 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }
                if(x1 != 0){
                    canvas.drawRect(x1 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, x1 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }

                holder.unlockCanvasAndPost(canvas);

            }
        }
    }

}
