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

    @Override
    public boolean onTouch(View v, MotionEvent event){

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
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //Gets the pointerIndex of this action
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
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

            case MotionEvent.ACTION_MOVE:
                if(p1PointerID != -1){
                    x1 = event.getX(event.findPointerIndex(p1PointerID));
                }
                if(p2PointerID != -1){
                    x2 = event.getX(event.findPointerIndex(p2PointerID));
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                //Gets the pointerIndex of this action
                final int pointerIndex2 = (action&MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                //Gets the pointerId of this event
                final int pointerId2 = event.getPointerId(pointerIndex2);
                //If the movement was from one of the active pointers it moves that paddle
                if(p1PointerID == pointerId2){
                    p1PointerID = INVALID_POINTER_ID;
                }else if(p2PointerID == pointerId2){
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
        return true;
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
        startTime = System.currentTimeMillis();
    }

    private float getBallSpeed(){
        float speedUp = ballSpeed * (System.currentTimeMillis() - startTime) / 10000;
        if(speedUp > ballSpeed){
            return speedUp;
        }else{
            return ballSpeed;
        }
    }

    private void haveWon(){
        if(p1Score > 10){
            showWinnerDialog(1);
        }else if(p2Score > 10){
            showWinnerDialog(2);
        }
    }

    private void showWinnerDialog(int winner){
        DialogFragment newFragment = PongTwoPlayerWinDialogFragment.newInstance(winner);
        newFragment.show(getFragmentManager(), "winner");
        newFragment.setCancelable(false);
        surfaceView.pause();
    }

    @Override
     public void onDialogPositiveClick(DialogFragment dialog){
        surfaceView.resume();
        createBall();
        p1Score = 0;
        p2Score = 0;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        finish();
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

        public void stop(){
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

        public void pause(){
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
                if(x2 == 0){
                    canvas.drawRect(canvas.getWidth() / 2 - canvas.getWidth() / 8, (canvas.getHeight() / 25) * 2, canvas.getWidth() / 2 + canvas.getWidth() / 8, canvas.getHeight() / 25, greenPaint);

                }
                if(x1 != 0){
                    canvas.drawRect(x1 - canvas.getWidth() / 8, canvas.getHeight() - (canvas.getHeight() / 25) * 2, x1 + canvas.getWidth() / 8, canvas.getHeight() - canvas.getHeight() / 25, greenPaint);
                }
                if(x2 != 0){
                    canvas.drawRect(x2 - canvas.getWidth() / 8, (canvas.getHeight() / 25) * 2, x2 + canvas.getWidth() / 8, canvas.getHeight() / 25, greenPaint);
                }

                moveBall(canvas);

                canvas.drawCircle(bx, by, br, greenPaint);

                holder.unlockCanvasAndPost(canvas);
            }
        }

        private void moveBall(Canvas canvas){

            if(by - br > canvas.getHeight()){
                resetBall();
                p2Score++;
                haveWon();
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
                    ballMoveDown = false;
                }
            }else if(by - br < (canvas.getHeight() / 25) * 2 && bx > x2 - canvas.getWidth() / 8 && bx < x2 + canvas.getWidth() / 8){
                if(by + br > (canvas.getHeight() / 25) * 2){
                    ballMoveDown = true;
                }
            }

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
