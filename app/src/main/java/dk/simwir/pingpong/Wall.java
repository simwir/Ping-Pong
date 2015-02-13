package dk.simwir.pingpong;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

import dk.simwir.pingpong.dialogs.WallLoseDialogFragment;


public class Wall extends ActionBarActivity implements View.OnTouchListener, WallLoseDialogFragment.LoseDialogListener{

    wallSurface surfaceView;
    DisplayMetrics metrics;
    float x, bx, by, br;
    int ballSpeed = 15;
    int score;
    boolean ballMoveDown, ballMoveRight;
    boolean overlay = false;
    SharedPreferences sharedPreferences;

    public static final String PREFERENCES = "wallpreferences";
    public static final String HIGHSCORE = "highscore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new wallSurface(this);
        surfaceView.setOnTouchListener(this);
        setContentView(surfaceView);

        //Gets the size of the screen
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    private void createBall(){
        Random r = new Random();
        resetBall();
        //Sets the speed of the ball to 1/1400 of the height of the display
        ballSpeed = metrics.heightPixels / 1400;
        //Randomizes if the ball starts moving right left
        ballMoveRight = r.nextInt(2)==1;
    }

    private void resetBall(){
        //Resets the ball to the center
        bx = metrics.widthPixels / 2;
        by = metrics.heightPixels / 2;
        br = metrics.heightPixels / 50;
        //makes the ball move up
        ballMoveDown = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        createBall();
        //Resets the scoreboard
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
            //Sets the paddle to where you press
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                break;
            //Sets the paddle to where you moved the finger
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                break;
        }


        return true;
    }

    /**
     * Returns the current speed of the ball
     * @return float representing the ball speed
     */
    private float getBallSpeed() {
        //Makes the ball speed up by your score
        if(score == 0){
            return ballSpeed * 15;
        }else{
            return ballSpeed * (15 + score);
        }
    }

    /**
     * Initiates the lose sequenze
     */
    private void ballLost(){
        //Only opens the lose dialog if another dialog is not already open.
        if(!overlay) {
            //Gets the highscore from the sharedPreferences
            int highscore = sharedPreferences.getInt(HIGHSCORE, 0);
            //if the new score is bigger is sets the new score as the high score and saves it.
            if (highscore < score) {
                saveHighScore();
                highscore = score;
            }

            //Creates a fragment and send the score and highscore to that fragment
            DialogFragment newFragment = WallLoseDialogFragment.newInstance(score, highscore);
            //Displays the fragment
            newFragment.show(getFragmentManager(), "lost");
            //Disables pressing besides the dialog will close
            newFragment.setCancelable(false);
            surfaceView.pause();
            //Sets overlay to make sure only one dialog will be shown at a time.
            overlay = true;
        }
    }

    /**
     * Saves the highscore to sharedPreferences
     */
    private void saveHighScore(){
        //Saves the score to the sharePreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(HIGHSCORE, score);

        editor.commit();
    }

    /**
     * This code is run when the restart button is pressed
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog){
        surfaceView.resume();
        overlay = false;
        createBall();
        //Resets the score
        score = 0;
    }

    /**
     * This code is run when the menu button is pressed
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        //Finishes the activity
        finish();
    }

    /**
     * This class holds the SurfaceView, which is where all the drawing to the screen is done.
     */
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

        /**
         * Terminates the thread that runs the surface view
         */
        public void stop() {
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
        public void pause() {
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
        public void run() {
            //Makes the animation pauseble
            while(isRunning){
                if(!holder.getSurface().isValid())
                    continue;

                //Locks the canvas so that we can draw on it.
                Canvas canvas = holder.lockCanvas();
                //Sets the background color to black
                canvas.drawRGB(0, 0, 0);
                //Draws the score to the screen
                canvas.drawText(Integer.toString(score), canvas.getWidth() / 2, canvas.getHeight() / 4 + metrics.widthPixels / 8, whitePaint);

                //if there haven't been a finger on the screen the paddle is drawn in the middle
                if(x==0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }

                //Draws the paddle at the position of the finger
                if(x != 0){
                    canvas.drawRect(x - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, x + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
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
         * @param canvas The canvas on where the ball is drawn.
         */
        private void moveBall(Canvas canvas) {
            //if the ball has moved out of the bottom it is lost
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
                    if(ballMoveDown){
                        //makes the ball move up
                        ballMoveDown = false;
                        //Adds one to the score
                        score++;
                    }
                }
            //Determent if the ball has hit the top
            }else if(by-br <= 0) ballMoveDown=true;

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
