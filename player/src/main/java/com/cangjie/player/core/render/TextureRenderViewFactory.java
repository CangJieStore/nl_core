package com.cangjie.player.core.render;

import android.content.Context;
/**
 * @author guruohan
 * @time 1/15/21 10:05 AM
*/
public class TextureRenderViewFactory extends RenderViewFactory {

    public static TextureRenderViewFactory create() {
        return new TextureRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new TextureRenderView(context);
    }
}
