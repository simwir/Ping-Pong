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

public class PongSinglePlayer extends Activity{


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
        ballHitTopCalculated = false;
        aiPaddleInPlace = false;
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

    @Override
    protected void onStart(){
        super.onStart();
        createBall();
        p1Score = p2Score = 0;
        ballHitTop = 0;
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
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                x1 = event.getX();
                break;
        }


        return true;
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

    private void haveWon(){
        if(p1Score > 10){
            showWinnerDialog(true);
        }else if(p2Score > 10){
            showWinnerDialog(false);
        }
    }

    private void showWinnerDialog(boolean playerWin){
        DialogFragment newFragment = PongSinglePlayerWinDialogFragment.newInstance(playerWin);
        newFragment.show(getFragmentManager(), "winner");
        newFragment.setCancelable(false);
        surfaceView.pause();
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

                moveAIPaddle(canvas);

                canvas.drawCircle(bx, by, br, greenPaint);

                holder.unlockCanvasAndPost(canvas);

            }


        }

        private void moveAIPaddle(Canvas canvas){
            Random r = new Random();
            if(!ballHitTopCalculated){
                float x = bx;
                float y = by;
                boolean ballMoveRight2 = ballMoveRight;
                while(true){
                    if(!ballMoveDown){
                        if(ballMoveRight2){
                            if(x + y < canvas.getWidth() - br){
                                ballHitTop = x + y - canvas.getHeight() / 25;
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
                    aiPaddleInPlace = false;
                }
            }else if(by - br < (canvas.getHeight() / 25) * 2 && bx > x2 - canvas.getWidth() / 8 && bx < x2 + canvas.getWidth() / 8){
                if(by + br > (canvas.getHeight() / 25) * 2){
                    ballMoveDown = true;
                    ballHitTopCalculated = false;

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
