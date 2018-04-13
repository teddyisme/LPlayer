# LPlayer
基于IJKplayer进行封装，方便使用，随意定制UI
### 简介
#### LPlayer 
![image](https://github.com/teddyisme/LPlayer/blob/master/something/git.gif)

- 基于ijkplayer
- 控制面板UI随意定制
- 头部UI随意定制
- 控制面板划入划出动画
- 手势控制音量和亮度
- 双击横竖屏切换

### 基本使用:
#### 1、导入库：
 
     implementation 'lixs.com.LPlayer:iplayerlib:1.0'
    
 #### 2、在布局文件中创建播放组件   
``` 
  <lixs.com.iplayerlib.LPlayerView
        android:id="@+id/lplayer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/black"
        app:controllerViewLayoutID="@layout/player_controller"
        app:currentTimeTextViewID="@id/current_time_tv"
        app:headerViewLayoutID="@layout/player_header"
        app:playButtonEndImage="@mipmap/simple_player_stop_white_24dp"
        app:playButtonID="@id/play_btn"
        app:playButtonPlayingImage="@mipmap/simple_player_arrow_white_24dp"
        app:seekBarID="@id/seek_bar"
        app:stretchButtonID="@id/full_screen_v"
        app:totalTimeTextViewID="@id/total_time_tv"
        app:stretchButtonImage="@mipmap/fullscreen"
        app:shrinkButtonImage="@mipmap/exit_fullscreen"/>
        
```
   属性说明：
   

attrs | description
---|---
controllerViewLayoutID | 自定义控制面板UI视图
headerViewLayoutID | 自定义头部UI视图
playButtonID | 控制视图中播放按钮ID
playButtonPlayingImage | 控制视图中播放按钮播放图片资源
playButtonEndImage | 控制视图中播放按钮播放暂停图片资源
seekBarID|控制视图中进度条控件ID
stretchButtonID|控制视图中全屏按钮ID
stretchButtonImage|控制视图中全屏按钮拉伸图片资源
shrinkButtonImage|控制视图中全屏按钮缩小图片资源
currentTimeTextViewID|控制视图中播放进度textview ID
totalTimeTextViewID|视频资源时间长度
coverButtonImage|封面控制播放按钮图片资源
```
- 属性设置暂不提供java方法
```
#### 3、java代码
```
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
```
- 在生命周期中调用player的相应方法即可。
- 头部视图传入之后通过方法getHeaderlView() 获取视图，进行操作。

#### 4、其他相关代码 
- 配置清单中activity
> android:configChanges="orientation|keyboardHidden|screenSize"

  

---
    在学习视频播放中进行封装整合，希望和大家一起学习进步。该项目会不定期不定量更新。

