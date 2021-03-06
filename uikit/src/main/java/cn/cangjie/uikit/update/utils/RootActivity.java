package cn.cangjie.uikit.update.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cn.cangjie.uikit.R;
import cn.cangjie.uikit.update.interfaces.AppDownloadListener;
import cn.cangjie.uikit.update.interfaces.AppUpdateInfoListener;
import cn.cangjie.uikit.update.interfaces.MD5CheckListener;
import cn.cangjie.uikit.update.interfaces.OnDialogClickListener;
import cn.cangjie.uikit.update.model.DownloadInfo;
import cn.cangjie.uikit.update.service.UpdateService;

public abstract class RootActivity extends AppCompatActivity {

    public static final String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int PERMISSION_CODE = 1001;

    public DownloadInfo downloadInfo;

    private AppDownloadListener appDownloadListener = obtainDownloadListener();

    private MD5CheckListener md5CheckListener = obtainMD5CheckListener();

    private AppUpdateInfoListener appUpdateInfoListener = obtainAppUpdateInfoListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downloadInfo = getIntent().getParcelableExtra("info");
        AppUpdateUtils.getInstance().addAppDownloadListener(appDownloadListener);
        AppUpdateUtils.getInstance().addAppUpdateInfoListener(appUpdateInfoListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        downloadInfo = getIntent().getParcelableExtra("info");
        AppUpdateUtils.getInstance().addAppDownloadListener(appDownloadListener);
        AppUpdateUtils.getInstance().addAppUpdateInfoListener(appUpdateInfoListener);
    }

    /**
     * ????????????
     */
    private void checkDownload() {
        // ?????????????????????8.0 ?????????????????????
        // ????????????????????????????????????Activity??????????????????????????????Activity???????????????
        if (AppUpdateUtils.getInstance().getUpdateConfig().isShowNotification())
            startService(new Intent(this, UpdateService.class));

        if (AppUpdateUtils.getInstance().getUpdateConfig().isAutoDownloadBackground()) {
            doDownload();
        } else {
            if (!NetWorkUtils.getCurrentNetType(this).equals("wifi")) {
                AppUtils.showDialog(this, ResUtils.getString(R.string.wifi_tips), new OnDialogClickListener() {
                    @Override
                    public void onOkClick(DialogInterface dialog) {
                        dialog.dismiss();
                        //??????
                        doDownload();
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                }, true, ResUtils.getString(R.string.tips), ResUtils.getString(R.string.cancel), ResUtils.getString(R.string.confirm));
            } else {
                doDownload();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //??????????????????
                checkDownload();
            } else {
                //??????????????????
                // ?????????????????????
                AppUtils.showDialog(this, ResUtils.getString(R.string.permission_to_store), new OnDialogClickListener() {
                    @Override
                    public void onOkClick(DialogInterface dialog) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                }, true, "", ResUtils.getString(R.string.cancel), ResUtils.getString(R.string.go_to));
            }
        }
    }

    /**
     * ????????????
     */
    public void cancelTask() {
        AppUpdateUtils.getInstance().cancelTask();
    }

    /**
     * ????????????
     */
    private void doDownload() {
        AppUpdateUtils.getInstance()
                .addMd5CheckListener(md5CheckListener)
                .download(downloadInfo);
    }

    public abstract AppDownloadListener obtainDownloadListener();

    /**
     * ??????????????????MD5???????????? ?????????????????????
     *
     * @return
     */
    public MD5CheckListener obtainMD5CheckListener() {
        return null;
    }

    /**
     * ???????????????????????????????????????????????? ?????????????????????
     *
     * @return
     */
    public AppUpdateInfoListener obtainAppUpdateInfoListener() {
        return null;
    }

    /**
     * ????????????
     */
    public void requestPermission() {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
            checkDownload();
        } else {
            //????????????
            int writePermission = ContextCompat.checkSelfPermission(this, permission);
            if (writePermission == PackageManager.PERMISSION_GRANTED) {
                //???????????????????????????
                checkDownload();
            } else {
                // ????????????
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_CODE);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_out);
    }

    @Override
    public void onBackPressed() {
        if (downloadInfo != null) {
            if (!downloadInfo.isForceUpdateFlag()) {
                super.onBackPressed();
            }
        } else
            super.onBackPressed();
    }

    /**
     * ??????
     */
    public void download() {
        if (!AppUpdateUtils.isDownloading()) {
            requestPermission();
        } else {
            if (appDownloadListener != null)
                appDownloadListener.downloadStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUpdateUtils.getInstance().clearAllListener();
    }

    /**
     * ??????Activity
     *
     * @param context
     * @param info
     */
    public static void launchActivity(Context context, DownloadInfo info, Class cla) {
        Intent intent = new Intent(context, cla);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP + Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }
}
