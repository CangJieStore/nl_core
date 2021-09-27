package com.cangjie.player.core.controller;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
/**
 * @author guruohan
 * @time 1/15/21 10:02 AM
*/
public interface IControlComponent {

    void attach(@NonNull ControlWrapper controlWrapper);

    View getView();

    void onVisibilityChanged(boolean isVisible, Animation anim);

    void onPlayStateChanged(int playState);

    void onPlayerStateChanged(int playerState);

    void setProgress(int duration, int position);

    void onLockStateChanged(boolean isLocked);

}
