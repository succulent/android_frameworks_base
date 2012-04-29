package com.android.systemui.statusbar.powerwidget;

import com.android.systemui.R;

import android.content.Intent;

import com.android.internal.widget.LockPatternUtils;

public class LockScreenButton extends PowerButton {

    private LockPatternUtils mLockPatternUtils;
    private boolean mLockScreenState;

    public LockScreenButton() {
        mType = BUTTON_LOCKSCREEN;
    }

    @Override
    protected void updateState() {
        if (mLockPatternUtils == null) {
            mLockPatternUtils = new LockPatternUtils(mView.getContext());
        }
        mLockScreenState = !mLockPatternUtils.isLockScreenDisabled();
        if (mLockPatternUtils.isSecure()) {
            mIcon = R.drawable.stat_lock_screen_off;
            mState = STATE_INTERMEDIATE;
        } else if (mLockScreenState) {
            mIcon = R.drawable.stat_lock_screen_on;
            mState = STATE_ENABLED;
        } else {
            mIcon = R.drawable.stat_lock_screen_off;
            mState = STATE_DISABLED;
        }
    }

    @Override
    protected void toggleState() {
        if (mLockPatternUtils == null) {
            mLockPatternUtils = new LockPatternUtils(mView.getContext());
        }
        mLockScreenState = !mLockPatternUtils.isLockScreenDisabled();
        if (!mLockPatternUtils.isSecure()) {
            if (mLockScreenState) {
                mLockPatternUtils.setLockScreenDisabled(true);
            } else {
                mLockPatternUtils.setLockScreenDisabled(false);
            }
        } else {
            launchSecuritySettings();
        }

        // we're handling this, so just update our buttons now
        // this is UGLY, do it better later >.>
        update();
    }

    @Override
    protected boolean handleLongClick() {
        launchSecuritySettings();
        return true;
    }

    private void launchSecuritySettings() {
        Intent intent = new Intent("android.settings.SECURITY_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mView.getContext().startActivity(intent);
    }
}
