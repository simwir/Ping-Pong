package dk.simwir.pingpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
/*
    Copyright © 2015  Simon Virenfeldt

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    Button bPong1Player, bPong2Player, bWall;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        //ad handler
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
        //        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //        .addTestDevice("9A2901CD115BDD574FF600EFEF130B55")
                .build();
        mAdView.loadAd(adRequest);
    }

    private void init(){

        bPong1Player = (Button) findViewById(R.id.bPong1Player);
        bPong1Player.setOnClickListener(this);
        bPong2Player = (Button) findViewById(R.id.bPong2Player);
        bPong2Player.setOnClickListener(this);
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
                Intent openPong2Player = new Intent(MainActivity.this, PongTwoPlayer.class);
                startActivity(openPong2Player);
                break;
            case (R.id.bWall):
                Intent openWall = new Intent(MainActivity.this, Wall.class);
                startActivity(openWall);
                break;

        }
    }
}
