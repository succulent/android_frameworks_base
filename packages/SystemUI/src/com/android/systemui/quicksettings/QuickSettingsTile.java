/*
 * Copyright (C) 2013-2014 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.quicksettings;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.StateSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.QuickSettingsTileView;

public class QuickSettingsTile implements View.OnClickListener {
    protected final Context mContext;
    protected QuickSettingsContainerView mContainer;
    protected QuickSettingsTileView mTile;
    protected View.OnClickListener mOnClick;
    protected View.OnLongClickListener mOnLongClick;
    protected final int mTileLayout;
    protected int mDrawable;
    protected String mLabel;
    protected int mTileTextSize;
    protected int mTileTextPadding;
    protected PhoneStatusBar mStatusbarService;
    protected QuickSettingsController mQsc;
    protected SharedPreferences mPrefs;

    public QuickSettingsTile(Context context, QuickSettingsController qsc) {
        this(context, qsc, R.layout.quick_settings_tile_basic);
    }

    public QuickSettingsTile(Context context, QuickSettingsController qsc, int layout) {
        mContext = context;
        mDrawable = R.drawable.ic_notifications;
        mLabel = mContext.getString(R.string.quick_settings_label_enabled);
        mStatusbarService = qsc.mStatusBarService;
        mQsc = qsc;
        mTileLayout = layout;
        mPrefs = mContext.getSharedPreferences("quicksettings", Context.MODE_PRIVATE);
    }

    public void setupQuickSettingsTile(LayoutInflater inflater,
            QuickSettingsContainerView container) {
        container.updateResources();
        mTileTextSize = container.getTileTextSize();
        mTileTextPadding = container.getTileTextPadding();
        mTile = (QuickSettingsTileView) inflater.inflate(
                R.layout.quick_settings_tile, container, false);
        mTile.setContent(mTileLayout, inflater);
        int color = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.SETTINGS_TILE_COLOR, 0xFF161616, UserHandle.USER_CURRENT);
        if (color != 0xFF161616) {
            StateListDrawable background = new StateListDrawable();
            background.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(0xFF212121));
            background.addState(StateSet.WILD_CARD, new ColorDrawable(color));
            mTile.setBackgroundDrawable(background);
        }
        mContainer = container;
        mContainer.addView(mTile);
        onPostCreate();
        updateQuickSettings();
        mTile.setOnClickListener(this);
        mTile.setOnLongClickListener(mOnLongClick);
    }

    public void switchToRibbonMode() {
        TextView tv = getLabelView();
        if (tv != null) {
            tv.setVisibility(View.GONE);
        }
        // Image margins are set by the controller, so no need to set them here
    }

    public void setImageMargins(int margin) {
        View image = getImageView();
        if (image != null) {
            MarginLayoutParams params = (MarginLayoutParams) image.getLayoutParams();
            params.topMargin = params.bottomMargin = margin;
            image.setLayoutParams(params);
        }
    }

    public void switchToSmallIcons() {
        TextView tv = getLabelView();
        if (tv != null) {
            tv.setText(mLabel);
            tv.setTextSize(mTileTextSize);
            int dpi = mContext.getResources().getDisplayMetrics().densityDpi;
            if (dpi > DisplayMetrics.DENSITY_HIGH) {
                tv.setPadding(0, mTileTextPadding, 0, 0);
            }
        }
    }

    protected View getImageView() {
        return mTile.findViewById(R.id.image);
    }

    protected TextView getLabelView() {
        return (TextView) mTile.findViewById(R.id.text);
    }

    void onPostCreate() {}

    public void onDestroy() {}

    public void onReceive(Context context, Intent intent) {}

    public void onChangeUri(ContentResolver resolver, Uri uri) {}

    public void updateResources() {
        if (mTile != null) {
            updateQuickSettings();
        }
    }

    void updateQuickSettings() {
        TextView tv = getLabelView();
        if (tv != null) {
            tv.setText(mLabel);
            tv.setTextSize(mTileTextSize);
            int dpi = mContext.getResources().getDisplayMetrics().densityDpi;
            if (dpi > DisplayMetrics.DENSITY_HIGH) {
                tv.setPadding(0, mTileTextPadding, 0, 0);
            }
        }
        View image = getImageView();
        if (image != null && image instanceof ImageView) {
            ((ImageView) image).setImageResource(mDrawable);
        }
    }

    void startSettingsActivity(String action) {
        Intent intent = new Intent(action);
        startSettingsActivity(intent);
    }

    void startSettingsActivity(Intent intent) {
        startSettingsActivity(intent, true);
    }

    private void startSettingsActivity(Intent intent, boolean onlyProvisioned) {
        if (onlyProvisioned && !mStatusbarService.isDeviceProvisioned()) {
            return;
        }
        try {
            // Dismiss the lock screen when Settings starts.
            ActivityManagerNative.getDefault().dismissKeyguardOnNextActivity();
        } catch (RemoteException e) {
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivityAsUser(intent, new UserHandle(UserHandle.USER_CURRENT));
        mStatusbarService.animateCollapsePanels();
    }

    @Override
    public void onClick(View v) {
        if (mOnClick != null) {
            mOnClick.onClick(v);
        }

        ContentResolver resolver = mContext.getContentResolver();
        boolean shouldCollapse = Settings.System.getIntForUser(resolver,
                Settings.System.QS_COLLAPSE_PANEL, 0, UserHandle.USER_CURRENT) == 1;
        if (shouldCollapse) {
            mQsc.mBar.collapseAllPanels(true);
        }
    }
}
