package cn.cangjie.uikit.titlebar.widget;

import android.view.View;


public class AlphaDelegate {

    private View mView;
    private AlphaViewHelper mAlphaViewHelper;

    public AlphaDelegate(View view) {
        this.mView = view;
    }

    public AlphaViewHelper getAlphaViewHelper() {
        if (mAlphaViewHelper == null) {
            mAlphaViewHelper = new AlphaViewHelper(mView);
        }
        return mAlphaViewHelper;
    }
}
