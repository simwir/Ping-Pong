package dk.simwir.pingpong;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;


public class PongTwoPlayerSettings extends Activity{

    SeekBar sbPaddleSize, sbBallSpeed, sbBallSize;
    EditText etPaddleSize, etBallSpeed, etBallSize;
    Button bOk, bCancel;

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


}
