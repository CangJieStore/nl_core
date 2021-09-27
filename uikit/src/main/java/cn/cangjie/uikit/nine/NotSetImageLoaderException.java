package cn.cangjie.uikit.nine;

import android.util.Log;

public class NotSetImageLoaderException extends RuntimeException {
    public static final String TAG = NotSetImageLoaderException.class.getSimpleName();

    public NotSetImageLoaderException(String msg) {
        super(msg);
        Log.w(TAG, getMessage());
    }
}
