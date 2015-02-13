package dk.simwir.pingpong;

import android.app.Activity;
import android.app.DialogFragment;
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

import dk.simwir.pingpong.dialogs.PongTwoPlayerWinDialogFragment;
/*
    Copyright © 2015  Simon Virenfeldt

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */
public class PongTwoPlayer extends Activity implements View.OnTouchListener, PongTwoPlayerWinDialogFragment.WinnerDialogListener{

    PongSurface surfaceView;
    float x1, x2, bx, by, br;
    boolean ballMoveDown, ballMoveRight;
    DisplayMetrics metrics;
    private static final int INVALID_POINTER_ID = -1;
    private int p1PointerID = INVALID_POINTER_ID;
    private int p2PointerID = INVALID_POINTER_ID;
    int ballSpeed = 15;
    int p1Score, p2Score;
    long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        surfaceView = new PongSurface(this);
        surfaceView.setOnTouchListener(this);
        x1 = x2 = bx = by = 0;
        setContentView(surfaceView);

        //Gets the size of the screen
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

    }

    @Override
    protected void onStart(){
        super.onStart();
        createBall();
        p1Score = p2Score = 0;

    }

    @Override
    protected void onStop(){
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

    /**
     * This runs whenever something happens to the touch screen.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event){

        final int action = event.getAction();

        //A switch that runs different code depending on the event than happened
        switch(action&MotionEvent.ACTION_MASK){
            //If the action is the first finger on the screen.
            case MotionEvent.ACTION_DOWN:
                //Decides if the finger is on the top or bottom half of the screen,
                //and setting the paddle to that position
                if(event.getY() > metrics.heightPixels / 2){
                    x1 = event.getX();
                    p1PointerID = event.getPointerId(0);
                }else if(event.getY() < metrics.heightPixels / 2){
                    x2 = event.getX();
                    p2PointerID = event.getPointerId(0);
                }
                break;
            //If the finger not is the first finger on the screen
            case MotionEvent.ACTION_POINTER_DOWN:
                //Gets the pointerIndex of this action
                final int pointerIndex = (action&MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                //Gets the pointerId of this event
                final int pointerId = event.getPointerId(pointerIndex);
                //Determent if we already have a p1- or p2PointerId and if the press was on the top half or bottom
                //It then assigns the pointerId to the player
                if(p1PointerID == -1 && event.getY(pointerIndex) > metrics.heightPixels / 2){
                    x1 = event.getX(pointerIndex);
                    p1PointerID = pointerId;
                }else if(p2PointerID == -1 && event.getY(pointerIndex) < metrics.heightPixels / 2){
                    x2 = event.getX(pointerIndex);
                    p2PointerID = pointerId;
                }
                break;

            //If the action was a finger moving
            //It then moves the paddle accordingly
            case MotionEvent.ACTION_MOVE:
                if(p1PointerID != -1){
                    x1 = event.getX(event.findPointerIndex(p1PointerID));
                }
                if(p2PointerID != -1){
                    x2 = event.getX(event.findPointerIndex(p2PointerID));
                }
                break;

            //If a finger, but not the last finger, was removed from the screen
            //This just resets the pointer
            case MotionEvent.ACTION_POINTER_UP:
                //Gets the pointerIndex of this action
                final int pointerIndex2 = (action&MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                //Gets the pointerId of this event
                final int pointerId2 = event.getPointerId(pointerIndex2);
                //If the movement was from one of the active pointers it moves that paddle
                if(p1PointerID == pointerId2){
                    p1PointerID = INVALID_POINTER_ID;
                }else if(p2PointerID == pointerId2){
                    p2PointerID = INVALID_POINTER_ID;
                }
                break;

            //if the last finger is removed from the screen
            //Resets the pointers
            case MotionEvent.ACTION_UP:
                p1PointerID = INVALID_POINTER_ID;
                p2PointerID = INVALID_POINTER_ID;
                break;
            //If the event in other ways cancels the pointer
            case MotionEvent.ACTION_CANCEL:
                p1PointerID = INVALID_POINTER_ID;
                p2PointerID = INVALID_POINTER_ID;
                break;

        }
        return true;
    }

    /**
     * The first step in creating the ball.
     */
    private void createBall(){
        Random r = new Random();
        resetBall();
        //Sets the speed of the ball to 1/140 of the height of the display
        ballSpeed = metrics.heightPixels / 140;
        //Randomizes what way the ball moved, if it moves up or down or left for right
        ballMoveDown = r.nextInt(2) == 1;
        ballMoveRight = r.nextInt(2) == 1;
    }

    /**
     * Resets the ball when it has gone out of bounce
     */
    private void resetBall(){
        //Resets the ball to the center
        bx = metrics.widthPixels / 2;
        by = metrics.heightPixels / 2;
        br = metrics.heightPixels / 50;

        startTime = System.currentTimeMillis();
    }

    /**
     * Calculates the speed of the ball depending on how long it has been alive
     *
     * @return float ball speed
     */
    private float getBallSpeed(){
        float speedUp = ballSpeed * (System.currentTimeMillis() - startTime) / 10000;
        if(speedUp > ballSpeed){
            return speedUp;
        }else{
            return ballSpeed;
        }
    }

    /**
     * Determent if somebody have won and who
     */
    private void haveWon(){
        if(p1Score > 10){
            showWinnerDialog(1);
        }else if(p2Score > 10){
            showWinnerDialog(2);
        }
    }

    /**
     * Displays the winning dialog
     *
     * @param winner 1 if player one won, 2 if player two won
     */
    private void showWinnerDialog(int winner){
        DialogFragment newFragment = PongTwoPlayerWinDialogFragment.newInstance(winner);
        newFragment.show(getFragmentManager(), "winner");
        newFragment.setCancelable(false);
        surfaceView.pause();
    }

    /**
     * This code is run when the restart button is pressed
     *
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog){
        surfaceView.resume();
        createBall();
        p1Score = 0;
        p2Score = 0;
    }

    /**
     * This code is run when the menu button is pressed
     *
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        finish();
    }

    /**
     * This class holds the SurfaceView, which is where all the drawing to the screen is done.
     */
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

        /**
         * Terminates the thread that runs the surface view
         */
        public void stop(){
            try{
                thread.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            thread = null;
        }

        /**
         * Pauses the surface view making any animation stop
         */
        public void pause(){
            isRunning = false;
        }

        /**
         * Resumes the surface view starting the animations again
         */
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
            //Makes the animation pauseble
            while(isRunning){
                if(!holder.getSurface().isValid())
                    continue;

                //Locks the canvas so that we can draw on it.
                Canvas canvas = holder.lockCanvas();
                //Sets the background color to black
                canvas.drawRGB(0, 0, 0);
                //Draws the scores to the screen
                canvas.drawText(Integer.toString(p1Score), canvas.getWidth() / 2, canvas.getHeight() / 4 * 3 - metrics.widthPixels / 8, whitePaint);
                canvas.drawText(Integer.toString(p2Score), canvas.getWidth() / 2, canvas.getHeight() / 4 + metrics.widthPixels / 8, whitePaint);

                //if there haven't been a finger on the screen the paddle is drawn in the middle
                if(x1 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }
                if(x2 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() / 25, greenPaint);

                }
                //Draws the player 1 paddle at the position of the finger
                if(x1 != 0){
                    canvas.drawRect(x1 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, x1 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }
                //Draws the player 2 paddle at the position of the finger
                if(x2 != 0){
                    canvas.drawRect(x2 - canvas.getWidth() / 8, (canvas.getHeight() / 25) * 2, x2 + canvas.getWidth() / 8, canvas.getHeight() / 25, greenPaint);
                }

                moveBall(canvas);

                //Draws the ball
                canvas.drawCircle(bx, by, br, greenPaint);

                //Unlocks the canvas making it visible on screen
                holder.unlockCanvasAndPost(canvas);
            }
        }

        /**
         * Moves the ball to the appropriate place
         *
         * @param canvas The canvas on where the ball is drawn.
         */
        private void moveBall(Canvas canvas){

            //If the ball have moved out the bottom
            if(by - br > canvas.getHeight()){
                resetBall();
                p2Score++;
                haveWon();
                //If the ball have moved out the top
            }else if(by + br < 0){
                resetBall();
                p1Score++;
                haveWon();
            }

            //Determent if the ball has hit the side
            if(bx + br > canvas.getWidth()){
                ballMoveRight = false;
            }else if(bx - br < 0){
                ballMoveRight = true;
            }

            //Determent if the ball has hit a paddle
            if(by + br > canvas.getHeight() - (canvas.getHeight() / 25) * 2 && bx > x1 - canvas.getWidth() / 8 && bx < x1 + canvas.getWidth() / 8){
                if(by - br < canvas.getHeight() - (canvas.getHeight() / 25) * 2){
                    //makes the ball move up
                    ballMoveDown = false;
                }
            }else if(by - br < (canvas.getHeight() / 25) * 2 && bx > x2 - canvas.getWidth() / 8 && bx < x2 + canvas.getWidth() / 8){
                if(by + br > (canvas.getHeight() / 25) * 2){
                    //Makes the ball move down
                    ballMoveDown = true;
                }
            }

            //Moves the ball
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
