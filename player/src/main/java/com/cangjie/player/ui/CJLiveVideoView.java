package com.cangjie.player.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.cangjie.player.core.player.CJLiveMediaPlayer;
import com.cangjie.player.core.player.PlayerFactory;
import com.cangjie.player.core.player.VideoView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guruohan
 * @time 1/15/21 10:19 AM
 */
public class CJLiveVideoView extends VideoView<CJLiveMediaPlayer> {

    private HashMap<String, Object> mPlayerOptions = new HashMap<>();
    private HashMap<String, Object> mFormatOptions = new HashMap<>();
    private HashMap<String, Object> mCodecOptions = new HashMap<>();
    private HashMap<String, Object> mSwsOptions = new HashMap<>();

    public CJLiveVideoView(@NonNull Context context) {
        super(context);
    }

    public CJLiveVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CJLiveVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setPlayerFactory(new PlayerFactory<CJLiveMediaPlayer>() {
            @Override
            public CJLiveMediaPlayer createPlayer(Context context) {
                return new CJLiveMediaPlayer(context);
            }
        });
    }

    @Override
    protected void setOptions() {
        super.setOptions();
        for (Map.Entry<String, Object> next : mPlayerOptions.entrySet()) {
            String key = next.getKey();
            Object value = next.getValue();
            if (value instanceof String) {
                mMediaPlayer.setPlayerOption(key, (String) value);
            } else if (value instanceof Long) {
                mMediaPlayer.setPlayerOption(key, (Long) value);
            }
        }
        for (Map.Entry<String, Object> next : mFormatOptions.entrySet()) {
            String key = next.getKey();
            Object value = next.getValue();
            if (value instanceof String) {
                mMediaPlayer.setFormatOption(key, (String) value);
            } else if (value instanceof Long) {
                mMediaPlayer.setFormatOption(key, (Long) value);
            }
        }
        for (Map.Entry<String, Object> next : mCodecOptions.entrySet()) {
            String key = next.getKey();
            Object value = next.getValue();
            if (value instanceof String) {
                mMediaPlayer.setCodecOption(key, (String) value);
            } else if (value instanceof Long) {
                mMediaPlayer.setCodecOption(key, (Long) value);
            }
        }
        for (Map.Entry<String, Object> next : mSwsOptions.entrySet()) {
            String key = next.getKey();
            Object value = next.getValue();
            if (value instanceof String) {
                mMediaPlayer.setSwsOption(key, (String) value);
            } else if (value instanceof Long) {
                mMediaPlayer.setSwsOption(key, (Long) value);
            }
        }
    }

    /**
     * ????????????
     */
    public void setEnableMediaCodec(boolean isEnable) {
        int value = isEnable ? 1 : 0;
        addPlayerOption("mediacodec-all-videos", value);
        addPlayerOption("mediacodec-sync", value);
        addPlayerOption("mediacodec-auto-rotate", value);
        addPlayerOption("mediacodec-handle-resolution-change", value);
    }

    /**
     * ????????????seek???????????????????????????????????????????????????seek???????????????
     */
    public void setEnableAccurateSeek(boolean isEnable) {
        addPlayerOption("enable-accurate-seek", isEnable ? 1 : 0);
    }


    public void addPlayerOption(String name, String value) {
        mPlayerOptions.put(name, value);
    }

    public void addPlayerOption(String name, long value) {
        mPlayerOptions.put(name, value);
    }


    public void addFormatOption(String name, String value) {
        mFormatOptions.put(name, value);
    }

    public void addFormatOption(String name, long value) {
        mFormatOptions.put(name, value);
    }


    public void addCodecOption(String name, String value) {
        mCodecOptions.put(name, value);
    }

    public void addCodecOption(String name, long value) {
        mCodecOptions.put(name, value);
    }


    public void addSwsOption(String name, String value) {
        mSwsOptions.put(name, value);
    }

    public void addSwsOption(String name, long value) {
        mSwsOptions.put(name, value);
    }

    @Override
    protected void setInitOptions() {
        super.setInitOptions();
        if (mCurrentPosition > 0) {
            addPlayerOption("seek-at-start", mCurrentPosition);
        }
    }

    @Override
    public void onPrepared() {
        setPlayState(STATE_PREPARED);
    }


    @Override
    public void skipPositionWhenPlay(int position) {
        addPlayerOption("seek-at-start", position);
    }

    public void startRecord(String path) {
        mMediaPlayer.startRecord(path);
    }

    public void stopRecord() {
        mMediaPlayer.stopRecord();
    }

    public boolean isRecord() {
        return mMediaPlayer.isRecord();
    }
}
