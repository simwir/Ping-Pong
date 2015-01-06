package dk.simwir.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;


public class Wall extends ActionBarActivity implements View.OnTouchListener{

    wallSurface surfaceView;
    DisplayMetrics metrics;
    float x, bx, by, br;
    int ballSpeed = 15;
    int score;
    boolean ballMoveDown, ballMoveRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new wallSurface(this);
        surfaceView.setOnTouchListener(this);
        setContentView(surfaceView);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    private void createBall(){
        Random r = new Random();
        resetBall();
        ballSpeed = metrics.heightPixels / 140;
        ballMoveRight = r.nextInt(2)==1;
    }

    private void resetBall(){
        bx = metrics.widthPixels / 2;
        by = metrics.heightPixels / 2;
        br = metrics.heightPixels / 50;
        ballMoveDown = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        createBall();
        score = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        surfaceView.stop();
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
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                break;
        }


        return true;
    }

    private float getBallSpeed() {
        if(score < 10){
            return ballSpeed;
        }else if(score >=10 && score < 15){
            return ballSpeed * 2;
        }else if(score >= 15 && score < 20){
            return  ballSpeed * 3;
        }else if(score >=20 && score < 30){
            return  ballSpeed * 4;
        }else{
            return ballSpeed * 5;
        }
    }

    private void ballLost(){
        resetBall();
        score=0;
    }


    public class wallSurface extends SurfaceView implements Runnable{

        SurfaceHolder holder;
        Thread thread = null;
        boolean isRunning = false;
        Paint greenPaint = new Paint();
        Paint whitePaint = new Paint();

        public wallSurface(Context context) {
            super(context);
            holder = getHolder();
        }

        public void stop() {
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

        public void pause() {
            isRunning = false;
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
        public void run() {
            while(isRunning){
                if(!holder.getSurface().isValid())
                    continue;

                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(0, 0, 0);
                canvas.drawText(Integer.toString(score), canvas.getWidth() / 2, canvas.getHeight() / 4 + metrics.widthPixels / 8, whitePaint);

                if(x==0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }

                if(x != 0){
                    canvas.drawRect(x - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, x + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }

                moveBall(canvas);

                canvas.drawCircle(bx, by, br, greenPaint);

                holder.unlockCanvasAndPost(canvas);
            }
        }

        private void moveBall(Canvas canvas) {
            if(by - br > canvas.getHeight()){
                ballLost();
            }

            //Determent if the ball has hit the side
            if(bx + br > canvas.getWidth()){
                ballMoveRight = false;
            }else if(bx - br < 0){
                ballMoveRight = true;
            }

            //Determent if the ball has hit the paddle
            if(by + br > canvas.getHeight() - (canvas.getHeight() / 25) * 2 && bx > x - canvas.getWidth() / 8 && bx < x + canvas.getWidth() / 8){
                if(by - br < canvas.getHeight() - (canvas.getHeight() / 25) * 2){
                    ballMoveDown = false;
                    score++;
                }
            }else if(by-br <= 0) ballMoveDown=true;

            if(ballMoveDown){
                by += getBallSpeed();
            }else{
                by -= getBallSpeed();
            }
            if(ballMoveRight){
                bx += getBallSpeed();
            }else{
                bx -= getBallSpeed();
            }
        }


    }


}
