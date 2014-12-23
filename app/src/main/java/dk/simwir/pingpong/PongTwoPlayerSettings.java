package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;


public class PongTwoPlayerSettings extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    SeekBar sbPaddleSize, sbBallSpeed, sbBallSize;
    EditText etPaddleSize, etBallSpeed, etBallSize;
    Button bOk, bCancel;
    int paddleSize, ballSize, ballSpeed;

    public static final String PADDLE_SIZE = "paddleSize";
    public static final String BALL_SPEED = "ballSpeed";
    public static final String BALL_SIZE = "ballSize";

    public static final String SETTINGS_CHANGED_BUNDLE = "settingsChanged";
    public static final String SETTINGS_EXTRAS = "dk.simwir.pingpong.EXTRAS";

    //Default values
    public static final int DEFAULT_PADDLE_SIZE = 25;
    public static final int DEFAULT_BALL_SPEED = 70;
    public static final int DEFAULT_BALL_SIZE = 2;

    private static final String TAG = "PongTwoPlayerSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pong_two_player_settings);

        sbPaddleSize = (SeekBar) findViewById(R.id.sbPaddleSize);
        sbPaddleSize.setOnSeekBarChangeListener(this);
        sbBallSpeed = (SeekBar) findViewById(R.id.sbBallSpeed);
        sbBallSpeed.setOnSeekBarChangeListener(this);
        sbBallSize = (SeekBar) findViewById(R.id.sbBallSize);
        sbBallSize.setOnSeekBarChangeListener(this);

        etPaddleSize = (EditText) findViewById(R.id.etPaddleSize);
        etBallSpeed = (EditText) findViewById(R.id.etBallSpeed);
        etBallSize = (EditText) findViewById(R.id.etBallSize);

        bOk = (Button) findViewById(R.id.bP2POk);
        bOk.setOnClickListener(this);
        bCancel = (Button) findViewById(R.id.bP2PCancel);
        bCancel.setOnClickListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        paddleSize = pref.getInt(PADDLE_SIZE, DEFAULT_PADDLE_SIZE);
        ballSpeed = pref.getInt(BALL_SPEED, DEFAULT_BALL_SPEED);
        ballSize = pref.getInt(BALL_SIZE, DEFAULT_BALL_SIZE);

        sbPaddleSize.setProgress(paddleSize);
        sbBallSpeed.setProgress(ballSpeed);
        sbBallSize.setProgress(ballSize);

        etPaddleSize.setText(Integer.toString(paddleSize));
        etBallSpeed.setText(Integer.toString(ballSpeed / 100));
        etBallSize.setText(Integer.toString(ballSize));
    }

    private void savePrefs(){
        Log.d(TAG, "Saving preferences");
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PADDLE_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
        editor.putInt(BALL_SPEED, (int) Double.parseDouble(etBallSpeed.getText().toString()) * 100);
        editor.putInt(BALL_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
        editor.apply();
        Log.d(TAG, "Preferences saved");

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        switch(seekBar.getId()){
            case R.id.sbPaddleSize:
                etPaddleSize.setText(Integer.toString(progress));
                break;
            case R.id.sbBallSpeed:
                // etBallSpeed.setText(Integer.toString(progress / 100));
                etBallSpeed.setText(Double.toString((double) progress / 100));
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
                Intent intent = new Intent(PongTwoPlayerSettings.this, PongTwoPlayer.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(SETTINGS_CHANGED_BUNDLE, true);
                bundle.putInt(PADDLE_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
                bundle.putInt(BALL_SPEED, (int) Double.parseDouble(etBallSpeed.getText().toString()) * 100);
                bundle.putInt(BALL_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
                intent.putExtra(SETTINGS_EXTRAS, bundle);

                startActivity(intent);

                break;
            case R.id.bP2PCancel:
                finish();
                break;
        }
    }


}
