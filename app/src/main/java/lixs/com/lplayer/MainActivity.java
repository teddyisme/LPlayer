package lixs.com.lplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import lixs.com.iplayerlib.LPlayerInterface;
import lixs.com.iplayerlib.LPlayerView;

public class MainActivity extends AppCompatActivity {
    private LPlayerView lplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lplayer = findViewById(R.id.lplayer);

//        String url = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear3/prog_index.m3u8";
        String url = "http://pl.cp31.ott.cibntv.net/playlist/m3u8?vid=XMzUzMzcwNjYxNg%3D%3D&type=flv&ups_client_netip=74e43e26&utid=oWVoEunqH3oCAWXkPaO7U6l2&ccode=01030150000A&psid=243f0930da7666a1c96e2b4b77cd86d6&duration=3397&expire=18000&drm_type=1&drm_device=4&ups_ts=1523677962&onOff=0&encr=0&ups_key=86f6c8f123269145f385bf53efc3a6e4";
        lplayer.setPlayUrl(url);
        lplayer.getHeaderlView().findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        lplayer.setLPlayerListener(new LPlayerInterface() {
            @Override
            public void onPlayerError() {
                super.onPlayerError();
            }

            @Override
            public void onPlayerPause(int currentPosition) {
                super.onPlayerPause(currentPosition);
            }

            @Override
            public void onPlayerStart() {
                super.onPlayerStart();
            }

            @Override
            public void onScreenChanged(boolean isFullScreen) {
                super.onScreenChanged(isFullScreen);
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
