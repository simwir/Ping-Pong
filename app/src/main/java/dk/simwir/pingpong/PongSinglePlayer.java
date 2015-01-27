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

import dk.simwir.pingpong.dialogs.PongSinglePlayerWinDialogFragment;


public class PongSinglePlayer extends Activity implements View.OnTouchListener, PongSinglePlayerWinDialogFragment.WinnerDialogListener{

    PongSurface surfaceView;
    float x1, x2, bx, by, br, ballHitTop;
    DisplayMetrics metrics;
    int p1Score, p2Score;
    boolean ballMoveDown, ballMoveRight, ballHitTopCalculated, aiPaddleInPlace;
    int ballSpeed = 15;
    int aiDifficulty = 20;
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
        ballHitTopCalculated = false;
        aiPaddleInPlace = false;
        startTime = System.currentTimeMillis();
    }

    /**
     * Calculates the speed of the ball depending on how long it has been alive
     * @return float ball speed
     */
    private float getBallSpeed(){
        //Calculates the ball speed
        float speedUp = ballSpeed * (System.currentTimeMillis() - startTime) / 10000;
        //True if the ball has been alive for so little that the ball would move too slow
        if(speedUp > ballSpeed){
            return speedUp;
        }else{
            return ballSpeed;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        createBall();
        //Resets the score
        p1Score = p2Score = 0;
        ballHitTop = 0;
        //Sets x2 to the width of the screen
        x2 = metrics.widthPixels;
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
    public boolean onTouch(View v, MotionEvent event){
        switch(event.getAction()){
            //Sets the paddle to where you press
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            //Sets the paddle to where you moved the finger
            case MotionEvent.ACTION_MOVE:
                x1 = event.getX();
                break;
        }


        return true;
    }

    /**
     * This code is run when the restart button is pressed
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
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        finish();
    }

    /**
     * Determent if a player have won
     */
    private void haveWon(){
        if(p1Score > 10){
            showWinnerDialog(true);
        }else if(p2Score > 10){
            showWinnerDialog(false);
        }
    }

    /**
     * Displays the wining dialog
     * @param playerWin true if the player won and false if the AI won
     */
    private void showWinnerDialog(boolean playerWin){
        //Creates a instance of the Dialog class and passes the playerWin boolean
        DialogFragment newFragment = PongSinglePlayerWinDialogFragment.newInstance(playerWin);
        //Displays the fragment
        newFragment.show(getFragmentManager(), "winner");
        //Disables pressing besides the dialog will close
        newFragment.setCancelable(false);
        surfaceView.pause();
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
                //If the AI paddle haven't moved it draws it in the center
                if(x2 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() / 25, greenPaint);

                }
                //Draws the paddle at the position of the finger
                if(x1 != 0){
                    canvas.drawRect(x1 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, x1 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }
                //Draws the AI paddle where it have moved to
                if(x2 != 0){
                    canvas.drawRect(x2 - canvas.getWidth() / 8, (canvas.getHeight() / 25) * 2, x2 + canvas.getWidth() / 8, canvas.getHeight() / 25, greenPaint);
                }
                moveBall(canvas);

                moveAIPaddle(canvas);

                //Draws the ball
                canvas.drawCircle(bx, by, br, greenPaint);

                //Unlocks the canvas making it visible on screen
                holder.unlockCanvasAndPost(canvas);

            }


        }

        /**
         * Moves the AI paddle to the place where the ball is going to be.
         * @param canvas The canvas on which the paddle is.
         */
        private void moveAIPaddle(Canvas canvas){
            Random r = new Random();
            //If it already know where the ball will hit it does not keep calculating
            if(!ballHitTopCalculated){
                //local variables used in the calculation steps
                float x = bx;
                float y = by;
                boolean ballMoveRight2 = ballMoveRight;
                //Calculation takes several steps, loop is broken by break statement when calculation is done
                while(true){
                    if(!ballMoveDown){
                        if(ballMoveRight2){
                            /*
                             * True if the ball does not hit the side before the ball hits the top.
                             * the ball moves 1:1 so to calculate if it will hit the side can be done
                             * by adding the coordinates.
                             */
                            if(x + y < canvas.getWidth() - br){
                                //The ball hits the top at the sum of the coordinates minus the
                                //distance to the top of the paddle.
                                ballHitTop = x + y - canvas.getHeight() / 25;
                                ballHitTopCalculated = true;
                                //Makes the AI able to fail, by moving to the wrong place
                                if(r.nextInt(aiDifficulty) == 0){
                                    if(ballHitTop < canvas.getWidth() / 2){
                                        ballHitTop = ballHitTop + canvas.getWidth() / 2;
                                    }else{
                                        ballHitTop = ballHitTop - canvas.getWidth() / 2;
                                    }
                                }
                                break;
                            }else{
                                //Calculates where the ball will hit the side next
                                y = y - canvas.getWidth() + x - br;
                                x = canvas.getWidth() - br;
                                ballMoveRight2 = false;
                            }
                        }else{

                            if(x - y > 0 + br){
                                ballHitTop = x - y;
                                ballHitTopCalculated = true;
                                if(r.nextInt(aiDifficulty) == 0){
                                    if(ballHitTop < canvas.getWidth() / 2){
                                        ballHitTop = ballHitTop + canvas.getWidth() / 2;
                                    }else{
                                        ballHitTop = ballHitTop - canvas.getWidth() / 2;
                                    }
                                }
                                break;
                            }else{
                                y = y - x + br;
                                x = 0 + br;
                                ballMoveRight2 = true;
                            }
                        }
                    }else{
                        break;
                    }
                }
            }

            if(ballHitTop != 0){
                if(!aiPaddleInPlace){
                    if(x2 <= ballHitTop){
                        if(x2 + 50 > ballHitTop){
                            x2++;
                        }else{
                            x2 = x2 + ballSpeed;
                        }
                    }else{
                        if(x2 + 50 < ballHitTop){
                            x2--;
                        }else{
                            x2 = x2 - ballSpeed;
                        }
                    }
                }
            }
            if(x2 == ballHitTop){
                aiPaddleInPlace = true;
            }


        }

        /**
         * Moves the ball to the appropriate place
         * @param canvas The canvas on where the ball is drawn.
         */
        private void moveBall(Canvas canvas){
            //If the ball have moved out the button
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
                    //Makes the AI paddle move into place
                    aiPaddleInPlace = false;
                }
            }else if(by - br < (canvas.getHeight() / 25) * 2 && bx > x2 - canvas.getWidth() / 8 && bx < x2 + canvas.getWidth() / 8){
                if(by + br > (canvas.getHeight() / 25) * 2){
                    //Makes the ball move down
                    ballMoveDown = true;
                    //Makes the program recalculate where the ball will hit
                    ballHitTopCalculated = false;

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
