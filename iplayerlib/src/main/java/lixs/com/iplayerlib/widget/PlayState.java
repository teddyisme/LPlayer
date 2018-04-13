package lixs.com.iplayerlib.widget;

/**
 * @author XinSheng
 * @description
 * @date 2018/4/11
 */
public class PlayState {
    /**
     * 空闲
     */
    public static final int STATE_IDLE = 330;
    /**
     * 播放出错
     */
    public static final int STATE_ERROR = 331;
    /**
     * 准备中/加载中
     */
    public static final int STATE_PREPARING = 332;
    /**
     * 准备完成
     */
    public static final int STATE_PREPARED = 333;
    /**
     * 播放中
     */
    public static final int STATE_PLAYING = 334;
    /**
     * 暂停
     */
    public static final int STATE_PAUSED = 335;
    /**
     * 播放完成
     */
    public static final int STATE_COMPLETED = 336;
    /**
     * 正在滑动
     */
    public static final int STATE_SEEKING = 337;
}
