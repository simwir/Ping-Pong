package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;


public class PongTwoPlayerSettings extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    SeekBar sbPaddleSize, sbBallSpeed, sbBallSize;
    EditText etPaddleSize, etBallSpeed, etBallSize;
    Button bOk, bCancel;
    int paddleSize, ballSpeed, ballSize;

    public static final String PADDLE_SIZE = "paddleSize";
    public static final String BALL_SPEED = "ballSpeed";
    public static final String BALL_SIZE = "ballSize";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong_two_player_settings);

        sbPaddleSize = (SeekBar) findViewById(R.id.sbPaddleSize);
        sbBallSpeed = (SeekBar) findViewById(R.id.sbBallSpeed);
        sbBallSize = (SeekBar) findViewById(R.id.sbBallSize);

        etPaddleSize = (EditText) findViewById(R.id.etPaddleSize);
        etBallSpeed = (EditText) findViewById(R.id.etBallSpeed);
        etBallSize = (EditText) findViewById(R.id.etBallSize);

        bOk = (Button) findViewById(R.id.bP2POk);
        bCancel = (Button) findViewById(R.id.bP2PCancel);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        paddleSize = pref.getInt(PADDLE_SIZE, 25);
        ballSpeed = pref.getInt(BALL_SPEED, 70);
        ballSize = pref.getInt(BALL_SIZE, 2);

        sbPaddleSize.setProgress(paddleSize);
        sbBallSpeed.setProgress(ballSpeed);
        sbBallSize.setProgress(ballSize);

        // etPaddleSize.setText(Integer.toString(paddleSize));
        // etBallSpeed.setText(Integer.toString(ballSpeed/100));
        // etBallSize.setText(Integer.toString(ballSize));
    }

    private void savePrefs(){
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PADDLE_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
        editor.putInt(BALL_SPEED, Integer.parseInt(etBallSpeed.getText().toString()));
        editor.putInt(BALL_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        switch(seekBar.getId()){
            case R.id.sbPaddleSize:
                etPaddleSize.setText(Integer.toString(progress));
                break;
            case R.id.sbBallSpeed:
                etBallSpeed.setText(Integer.toString(progress / 100));
                break;
            case R.id.sbBallSize:
                etBallSize.setText(Integer.toString(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){}

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.bP2POk:
                savePrefs();
                break;
            case R.id.bP2PCancel:

                break;
        }
    }


}
