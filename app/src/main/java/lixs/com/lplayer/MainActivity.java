package lixs.com.lplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import lixs.com.iplayerlib.LPlayerView;

public class MainActivity extends AppCompatActivity {
    private LPlayerView lplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lplayer = findViewById(R.id.lplayer);
        String url = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear3/prog_index.m3u8";
        lplayer.setPlayUrl(url);
        lplayer.getHeaderlView().findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        lplayer.setFullScreen(newConfig.getLayoutDirection() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lplayer.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lplayer.onResum();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lplayer.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (lplayer.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
