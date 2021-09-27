package com.cangjie.player.core.player;

import android.content.Context;

public class CJPlayerFactory extends PlayerFactory<CJPlayer> {

    public static CJPlayerFactory create() {
        return new CJPlayerFactory();
    }

    @Override
    public CJPlayer createPlayer(Context context) {
        return new CJPlayer(context);
    }
}
