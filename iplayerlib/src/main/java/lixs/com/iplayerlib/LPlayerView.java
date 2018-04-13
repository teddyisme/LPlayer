package lixs.com.iplayerlib;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import lixs.com.iplayerlib.widget.PlayState;
import lixs.com.iplayerlib.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.content.Context.AUDIO_SERVICE;
import static lixs.com.iplayerlib.widget.PlayState.STATE_COMPLETED;
import static lixs.com.iplayerlib.widget.PlayState.STATE_IDLE;
import static lixs.com.iplayerlib.widget.PlayState.STATE_PAUSED;
import static lixs.com.iplayerlib.widget.PlayState.STATE_PLAYING;
import static lixs.com.iplayerlib.widget.PlayState.STATE_SEEKING;

/**
 * @author XinSheng
 * @description
 * @date 2018/4/10
 */
public class LPlayerView extends FrameLayout implements View.OnTouchListener {
    private Context mContext;
    /**
     * 播放地址
     */
    private String playUrl;
    /**
     * 手势处理
     */
    public GestureDetector gestureDetector;
    /**
     * 播放器
     */
    private IjkVideoView mVideoView;
    /**
     * 是否全屏
     */
    private boolean isFullScreen;
    /**
     * 控制视图容器
     */
    private LinearLayout controllContianer;
    /**
     * 控制器视图
     */
    private ViewGroup controllerView;
    /**
     * 头部视图容器
     */
    private LinearLayout headerContianer;
    /**
     * 控制器视图
     */
    private ViewGroup headerlView;

    /**
     * 当前播放进度textview
     */
    private TextView currentTimeTv;
    /**
     * 总播放长度textview
     */
    private TextView totalTimeTv;
    /**
     * 当前播放位置
     */
    private int currentPosition;
    /**
     * 播放按钮
     */
    private Button playBtn;
    /**
     * 正在播放的按钮图片
     */
    private int playingImage;
    /**
     * 结束播放时的按钮图片
     */
    private int endPlayImage;
    /**
     * 是否在显示控制栏
     */
    private boolean isShowController = true;
    /**
     * 状态
     */
    private int userState = STATE_IDLE;
    /**
     * 播放进度条
     */
    private SeekBar seekBar;
    /**
     * 播放进度条最大值
     */
    private int seekBarMax = 1000;
    /**
     * 刷新播放进度的消息
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 控制面板自动定时隐藏任务
     */
    private AutoPlayRunnable autoPlayRunnable = new AutoPlayRunnable();
    /**
     * 全屏按钮拉伸图片资源ID
     */
    private int strentchImageID;
    /**
     * 全屏按钮缩小图片资源ID
     */
    private int shrinkImageID;
    /**
     * 拉伸按钮
     */
    private Button strentchButton;
    /**
     * 音频管理器
     */
    private AudioManager mAudioManager;
    /**
     * 提示容器
     */
    private LinearLayout alertView;
    /**
     * 提示图片视图
     */
    private ImageView alertImageView;
    /**
     * 提示文字
     */
    private TextView alertTextView;
    /**
     * 封面控制按钮容器
     */
    private LinearLayout coverBuutonsView;
    /**
     * 封面播放控制按钮
     */
    private Button coverBtn;
    /**
     * 声音滑动值
     */
    private int volume;
    /**
     * 当前声音值
     */
    private int currentVolume;
    /**
     * 声音最大值
     */
    private int maxVolume;
    /**
     * 亮度滑动值
     */
    private float brightness = -1;
    /**
     * 封面播放按钮
     */
    private int coverPlayImageID;
    private ProgressBar mProgressBar;

    public LPlayerView(@NonNull Context context) {
        super(context);
        initUI(context, null);
    }

    public LPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    public LPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        this.mContext = context;
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        gestureDetector = new GestureDetector(mContext, new PlayerGestureListener());

        addViews();

        analysisAttrs(attrs);

        setOnTouchListener(this);

        //初始化音频管理器
        mAudioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /*刷新进度条*/
                case MESSAGE_SHOW_PROGRESS:
                    long position = mVideoView.getCurrentPosition();
                    long duration = mVideoView.getDuration();
                    if (seekBar != null) {
                        if (duration > 0) {
                            long pos = seekBarMax * position / duration;
                            seekBar.setProgress((int) pos);
                        }
                        int percent = mVideoView.getBufferPercentage();
                        seekBar.setSecondaryProgress(percent * 10);
                    }
                    setTimeText();
                    sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
                    break;
                default:
                    break;
            }

        }
    };

    private void setTimeText() {
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        if (currentTimeTv != null) {
            currentTimeTv.setText(Utils.generateTime(position));
        }
        if (totalTimeTv != null) {
            totalTimeTv.setText(Utils.generateTime(duration));
        }
    }

    private void addViews() {
        mVideoView = new IjkVideoView(mContext);
        mVideoView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(mContext, 300)));
        mVideoView.setOnCompletionListener(completionListener);
        mVideoView.setOnErrorListener(errorListener);
        mVideoView.setOnInfoListener(infoListener);
        mVideoView.setOnPreparedListener(preparedListener);
        addView(mVideoView);

        controllContianer = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        controllContianer.setLayoutParams(params);
        controllContianer.setOrientation(LinearLayout.VERTICAL);
        addView(controllContianer);

        headerContianer = new LinearLayout(mContext);
        params.gravity = Gravity.TOP;
        headerContianer.setLayoutParams(params);
        headerContianer.setOrientation(LinearLayout.VERTICAL);
        addView(headerContianer);

        alertView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.lvideo_alert_view, null);
        alertView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        alertView.setVisibility(GONE);
        addView(alertView);
        alertImageView = alertView.findViewById(R.id.icon);
        alertTextView = alertView.findViewById(R.id.alert_tv);

        coverBuutonsView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.lvideo_cover_button_view, null);
        coverBuutonsView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(coverBuutonsView);
        coverBtn = coverBuutonsView.findViewById(R.id.lvideo_cover_btn);
        coverBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });
        mProgressBar = coverBuutonsView.findViewById(R.id.lvideo_progressbar);
    }


    private void analysisAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.LPlayerView);
            int controllerViewLayoutID = ta.getResourceId(R.styleable.LPlayerView_controllerViewLayoutID, 0);
            int headerViewLayoutID = ta.getResourceId(R.styleable.LPlayerView_headerViewLayoutID, 0);
            int playButtonID = ta.getResourceId(R.styleable.LPlayerView_playButtonID, 0);
            playingImage = ta.getResourceId(R.styleable.LPlayerView_playButtonPlayingImage, 0);
            endPlayImage = ta.getResourceId(R.styleable.LPlayerView_playButtonEndImage, 0);
            int seekBarID = ta.getResourceId(R.styleable.LPlayerView_seekBarID, 0);
            int stretchButton = ta.getResourceId(R.styleable.LPlayerView_stretchButtonID, 0);
            int currentTimeTvID = ta.getResourceId(R.styleable.LPlayerView_currentTimeTextViewID, 0);
            int totalTimeTvID = ta.getResourceId(R.styleable.LPlayerView_totalTimeTextViewID, 0);
            strentchImageID = ta.getResourceId(R.styleable.LPlayerView_stretchButtonImage, 0);
            shrinkImageID = ta.getResourceId(R.styleable.LPlayerView_shrinkButtonImage, 0);
            coverPlayImageID = ta.getResourceId(R.styleable.LPlayerView_coverButtonImage, R.mipmap.lvideo_cover_play);
            ta.recycle();

            if (headerViewLayoutID != 0)
                setHeaderView(headerViewLayoutID);
            if (controllerViewLayoutID != 0)
                setControllerView(controllerViewLayoutID);
            if (playButtonID != 0)
                setPlayBtn(playButtonID);
            if (seekBarID != 0)
                setSeekBar(seekBarID);
            if (stretchButton != 0)
                setStretchButton(stretchButton);
            if (currentTimeTvID != 0)
                currentTimeTv = controllContianer.findViewById(currentTimeTvID);
            if (totalTimeTvID != 0)
                totalTimeTv = controllContianer.findViewById(totalTimeTvID);

        }
    }

    private void setHeaderView(int headerViewLayoutID) {
        LinearLayout headerView = (LinearLayout) LayoutInflater.from(mContext).inflate(headerViewLayoutID, null);
        if (this.headerContianer != null) {
            headerContianer.removeAllViews();
        }
        this.headerlView = headerView;
        headerContianer.addView(this.headerlView);
    }

    private void setStretchButton(int stretchButton) {
        if (stretchButton != 0) {
            strentchButton = controllContianer.findViewById(stretchButton);
            if (strentchImageID != 0) {
                strentchButton.setBackgroundResource(strentchImageID);
            }
            strentchButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFullScreen();
                }
            });
        }
    }

    private void setSeekBar(int seekBarID) {
        seekBar = controllContianer.findViewById(seekBarID);
        seekBar.setMax(seekBarMax);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b) {
                    return;
                }
                userState = STATE_SEEKING;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int duration = mVideoView.getDuration();
                mVideoView.seekTo(seekBar.getProgress() * duration / seekBar.getMax());
                userState = STATE_IDLE;
                hideController();
                coverBuutonsView.setVisibility(VISIBLE);
                coverBtn.setVisibility(GONE);
                mProgressBar.setVisibility(VISIBLE);
                Log.d("PPP", "onStopTrackingTouch");
            }
        });
    }

    private void setPlayBtn(int playButtonID) {
        playBtn = controllContianer.findViewById(playButtonID);
        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });
    }

    private void play() {
        if (mVideoView.isPlaying()) {
            whenPause();
        } else {
            mVideoView.start();
            handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
            userState = STATE_PLAYING;
            if (endPlayImage != 0) {
                playBtn.setBackgroundResource(endPlayImage);
            }
            coverBuutonsView.setVisibility(GONE);
            hideController();
        }
    }

    private void whenPause() {
        mVideoView.pause();
        if (playingImage != 0) {
            playBtn.setBackgroundResource(playingImage);
            coverBuutonsView.setVisibility(VISIBLE);
            coverBtn.setBackgroundResource(coverPlayImageID);
        }
        getCurrentPosition();
        userState = STATE_PAUSED;
        controllerIn();
        coverBuutonsView.setVisibility(GONE);
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
        mVideoView.setVideoPath(playUrl);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (controllerView != null) {
            controllContianer.layout(left, bottom - controllerView.getHeight(), right, bottom);
        }
        if (headerContianer != null) {
            headerContianer.layout(left, top, right, headerContianer.getHeight());
        }
        if (alertView != null) {
            alertView.layout(right / 2 - alertView.getWidth() / 2,
                    bottom / 2 - (alertView.getHeight() / 2),
                    right / 2 + alertView.getWidth() / 2,
                    bottom / 2 + (alertView.getHeight() / 2));
        }
    }

    /**
     * 设置控制视图
     *
     * @param resId 布局ID
     * @return
     */
    private void setControllerView(int resId) {
        LinearLayout controllerView = (LinearLayout) LayoutInflater.from(mContext).inflate(resId, null);
        if (this.controllerView != null) {
            controllContianer.removeAllViews();
        }
        this.controllerView = controllerView;
        controllContianer.addView(this.controllerView);
        hideController();
    }

    /**
     * 隐藏控制栏
     */
    private void hideController() {
        autoPlayRunnable.start();
    }

    /**
     * 是否自动隐藏控制栏
     */
//    public void setIsAutoHideController() {
//
//    }

    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 5000;

        /**
         * 五秒无操作，收起控制面板
         */
        public AutoPlayRunnable() {

        }

        public void start() {
            handler.removeCallbacks(this);
            handler.postDelayed(this, AUTO_PLAY_INTERVAL);
        }

        public void stop() {
            handler.removeCallbacks(this);
        }

        @Override
        public void run() {
            handler.removeCallbacks(this);
            if (isShowController && userState == STATE_PLAYING) {
                controllerOut();
            }
        }
    }

    /**
     * 向上退出
     */
    private void headerOut() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.down_up_out);
        animation.setFillAfter(true);
        headerContianer.startAnimation(animation);
    }

    /**
     * 向下进来
     */
    private void headerIn() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_down_enter);
        animation.setFillAfter(true);
        headerContianer.startAnimation(animation);
    }

    /**
     * 向下退出
     */
    private void controllerOut() {
        if (isShowController && mVideoView.isPlaying()) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.up_down_out);
            animation.setFillAfter(true);
            controllContianer.startAnimation(animation);
            isShowController = false;
            headerOut();
            autoPlayRunnable.stop();
        }
    }

    /**
     * 向上进来
     */
    private void controllerIn() {
        if (!isShowController) {
            headerIn();
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.down_up_enter);
            animation.setFillAfter(true);
            controllContianer.startAnimation(animation);
            isShowController = true;
        }
    }

    /**
     * 获取当前播放位置
     *
     * @return
     */
    public int getCurrentPosition() {
        currentPosition = mVideoView.getCurrentPosition();
        return currentPosition;
    }

    public void onDestroy() {
        mVideoView.stopPlayback();
        mVideoView.release(true);
        mVideoView.stopBackgroundPlay();
        IjkMediaPlayer.native_profileEnd();
    }

    public void onResum() {
        mVideoView.resume();
        mVideoView.seekTo(currentPosition);
    }

    public void onPause() {
        whenPause();
    }

    public ViewGroup getHeaderlView() {
        return headerlView;
    }

    public ViewGroup getControllerView() {
        return controllerView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (gestureDetector.onTouchEvent(motionEvent))
            return gestureDetector.onTouchEvent(motionEvent);
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * 结束手势
     */
    private void endGesture() {
        alertView.setVisibility(GONE);

    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0) {
                volume = 0;
            }
        }

        int index = (int) (percent * maxVolume) + volume + currentVolume;
        if (index > maxVolume) {
            index = maxVolume;
        } else if (index < 0) {
            index = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        int i = (int) (index * 1.0 / maxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }

        alertTextView.setText(s);
    }


    private void onBrightnessSlide(float percent) {
        percent = percent / 2;
        if (brightness < 0) {
            brightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        WindowManager.LayoutParams lpa = ((Activity) mContext).getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        brightness = lpa.screenBrightness;
        ((Activity) mContext).getWindow().setAttributes(lpa);
        alertTextView.setText((int) (lpa.screenBrightness * 100) + "%");
    }

    private void onProgressSlide(float percent) {
//        long position = mVideoView.getCurrentPosition();
//        long duration = mVideoView.getDuration();
//        long deltaMax = Math.min(100 * 1000, duration - position);
//        long delta = (long) (deltaMax * percent);
//
//        long newPosition = delta + position;
//        if (newPosition > duration) {
//            newPosition = duration;
//        } else if (newPosition <= 0) {
//            newPosition = 0;
//            delta = -position;
//        }
//
//        int showDelta = (int) delta / 1000;
//        if (showDelta != 0) {
//            String text = showDelta > 0 ? ("+" + showDelta) : "" + showDelta;
//            Log.d("PPP", "onProgressSlide:" + text);
//        }
//
//        seekBar.setProgress((int) (seekBarMax * newPosition / duration));
//        mVideoView.seekTo((int) newPosition);
    }

    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            boolean toSeek = Math.abs(distanceX) - Math.abs(distanceY) > 40;
            boolean volumeControl = mOldX > Utils.getScreenWidth(mContext) * 0.5f;

            if (toSeek) {
                onProgressSlide(-deltaX / mVideoView.getWidth());
            } else {
                float percent = deltaY / mVideoView.getHeight();
                if (volumeControl) {
                    alertView.setVisibility(VISIBLE);
                    alertTextView.setText((100 * currentVolume / maxVolume) + "%");
                    alertImageView.setImageResource(R.mipmap.simple_player_volume_up_white_36dp);
                    onVolumeSlide(percent);
                } else {
                    alertView.setVisibility(VISIBLE);
                    alertTextView.setText((brightness * 100) + "%");
                    alertImageView.setImageResource(R.mipmap.lvideo_light);
                    onBrightnessSlide(percent);
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            startTouchAction();
            autoPlayRunnable.stop();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (!isShowController) {
                controllerIn();
            } else {
                controllerOut();
            }
            autoPlayRunnable.start();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            toggleFullScreen();
            return super.onDoubleTap(e);
        }
    }

    private void startTouchAction() {
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 处理返回键
     *
     * @return false 为已处理全屏返回小屏
     */
    public boolean onBackPressed() {
        if (Utils.getScreenOrientation(((Activity) mContext)) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setFullScreen(false);
            return false;
        }
        return true;
    }

    /**
     * 切换横竖屏
     */
    public void toggleFullScreen() {
        if (Utils.getScreenOrientation(((Activity) mContext)) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setFullScreen(false);
            if (strentchImageID != 0) {
                strentchButton.setBackgroundResource(strentchImageID);
            }
        } else {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setFullScreen(true);
            if (shrinkImageID != 0) {
                strentchButton.setBackgroundResource(shrinkImageID);
            }
        }
    }

    /**
     * 设置界面方向
     */
    public void setFullScreen(boolean fullScreen) {
        WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
        if (fullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((Activity) mContext).getWindow().setAttributes(attrs);
            ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mContext).getWindow().setAttributes(attrs);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setVideoHeight(fullScreen);
    }

    /**
     * 记录视频高度
     */
    private int videoHeight = 0;

    private void setVideoHeight(boolean fullScreen) {
        if (fullScreen) {
            videoHeight = mVideoView.getHeight();
            mVideoView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            ViewGroup.LayoutParams params = mVideoView.getLayoutParams();
            params.height = videoHeight;
            mVideoView.setLayoutParams(params);
        }
    }

    private IMediaPlayer.OnCompletionListener completionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            userState = STATE_COMPLETED;
            whenPause();
        }
    };
    private IMediaPlayer.OnErrorListener errorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Toast.makeText(mContext, "视频播放错误", Toast.LENGTH_LONG).show();
            coverBuutonsView.setVisibility(VISIBLE);
            coverBtn.setBackgroundResource(R.mipmap.lvideo_error);

            return false;
        }
    };

    private IMediaPlayer.OnPreparedListener preparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            setTimeText();
        }
    };

    private IMediaPlayer.OnInfoListener infoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            if (mVideoView.isPlaying()) {
                coverBuutonsView.setVisibility(GONE);
                mProgressBar.setVisibility(GONE);
            }
            return false;
        }
    };

}
