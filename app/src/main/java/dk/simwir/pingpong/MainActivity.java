package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener{

    Button bPong1Player, bPong2Player, bJuggle;

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
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.bPong1Player:
                Intent openPong1Player = new Intent(MainActivity.this, PongSinglePlayer.class);
                startActivity(openPong1Player);
                break;
            case R.id.bPong2Player:
                Intent openPong2Player = new Intent(MainActivity.this, PongTwoPlayer.class);
                startActivity(openPong2Player);
                break;
            case (R.id.bJuggle):
                Intent openJuggle = new Intent(MainActivity.this, Juggle.class);
                startActivity(openJuggle);
                break;

        }
    }
}
