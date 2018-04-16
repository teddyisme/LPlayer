package lixs.com.iplayerlib;

/**
 * @author XinSheng
 */
public interface LPlayerListener {
    /**
     * 暂停
     * @param currentPosition 当前播放位置
     */
    void onPlayerPause(int currentPosition);

    /**
     * 开始播放
     */
    void onPlayerStart();

    /**
     * 播放错误
     */
    void onPlayerError();

    /**
     * 屏幕全屏转换
     * @param isFullScreen  是否全屏
     */
    void onScreenChanged(boolean isFullScreen);
}
