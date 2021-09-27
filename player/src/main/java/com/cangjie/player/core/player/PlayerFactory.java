package com.cangjie.player.core.player;

import android.content.Context;

/**
 * @author guruohan
 * @time 1/15/21 10:04 AM
*/
public abstract class PlayerFactory<P extends AbstractPlayer> {

    public abstract P createPlayer(Context context);
}
