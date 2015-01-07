package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener{

    Button bPong1Player, bPong2Player, bJuggle, bWall;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){

        bPong1Player = (Button) findViewById(R.id.bPong1Player);
        bPong1Player.setOnClickListener(this);
        bPong2Player = (Button) findViewById(R.id.bPong2Player);
        bPong2Player.setOnClickListener(this);
        bJuggle = (Button) findViewById(R.id.bJuggle);
        bJuggle.setOnClickListener(this);
        bWall = (Button) findViewById(R.id.bWall);
        bWall.setOnClickListener(this);
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.bPong1Player:
                Intent openPong1Player = new Intent(MainActivity.this, PongSinglePlayer.class);
                startActivity(openPong1Player);
                break;
            case R.id.bPong2Player:
                Intent openPong2Player = new Intent(MainActivity.this, PongTwoPlayerSettings.class);
                startActivity(openPong2Player);
                break;
            case (R.id.bJuggle):
                Intent openJuggle = new Intent(MainActivity.this, Juggle.class);
                startActivity(openJuggle);
                break;
            case (R.id.bWall):
                Intent openWall = new Intent(MainActivity.this, Wall.class);
                startActivity(openWall);
                break;

        }
    }
}
