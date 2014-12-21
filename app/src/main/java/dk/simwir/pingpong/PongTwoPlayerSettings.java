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
    public static final String SETTINGS_CHANGED_EXTRAS = "extras";

    private static final String TAG = "PongTwoPlayerSettingsActivity";

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
        Log.d(TAG, "Saving preferences");
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PADDLE_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
        editor.putInt(BALL_SPEED, Integer.parseInt(etBallSpeed.getText().toString()));
        editor.putInt(BALL_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
        editor.apply();
        Log.d(TAG, "Preferences saved");

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        Log.d(TAG, "Progress Changes");
        switch(seekBar.getId()){
            case R.id.sbPaddleSize:
                Log.d(TAG, "Paddle size changed to " + progress);
                etPaddleSize.setText(Integer.toString(progress));
                break;
            case R.id.sbBallSpeed:
                Log.d(TAG, "Ball speed changed to " + progress);
                // etBallSpeed.setText(Integer.toString(progress / 100));
                etBallSpeed.setText(Double.toString((double) progress / 100));
                break;
            case R.id.sbBallSize:
                Log.d(TAG, "Ball size changed to " + progress);
                etBallSize.setText(Integer.toString(progress));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){
        Log.d(TAG, "Tracking started");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){
        Log.d(TAG, "Tracking ended");
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.bP2POk:
                Log.d(TAG, "OK, clicked");
                savePrefs();
                Intent returnIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean(SETTINGS_CHANGED_BUNDLE, true);
                bundle.putInt(PADDLE_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
                bundle.putInt(BALL_SPEED, Integer.parseInt(etBallSpeed.getText().toString()));
                bundle.putInt(BALL_SIZE, Integer.parseInt(etPaddleSize.getText().toString()));
                returnIntent.putExtra(SETTINGS_CHANGED_EXTRAS, bundle);
                setResult(RESULT_OK, returnIntent);
                Log.d(TAG, "Intent returned");
                finish();
                break;
            case R.id.bP2PCancel:
                Log.d(TAG, "Cancel pressed");
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }


}
