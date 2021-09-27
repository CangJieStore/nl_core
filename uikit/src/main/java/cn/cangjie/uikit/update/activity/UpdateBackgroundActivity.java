package cn.cangjie.uikit.update.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import cn.cangjie.uikit.update.interfaces.AppDownloadListener;
import cn.cangjie.uikit.update.model.DownloadInfo;
import cn.cangjie.uikit.update.utils.RootActivity;


public class UpdateBackgroundActivity extends RootActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        attributes.height = 1;
        attributes.width = 1;

        download();
        finish();
    }

    @Override
    public AppDownloadListener obtainDownloadListener() {
        return null;
    }

    /**
     * 启动Activity
     *
     * @param context
     * @param info
     */
    public static void launch(Context context, DownloadInfo info) {
        launchActivity(context, info, UpdateBackgroundActivity.class);
    }

}
