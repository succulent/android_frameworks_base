/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.tablet;

import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Slog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.systemui.R;
import com.android.systemui.statusbar.policy.AirplaneModeController;
import com.android.systemui.statusbar.policy.AutoRotateController;
import com.android.systemui.statusbar.policy.BrightnessController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.DoNotDisturbController;
import com.android.systemui.statusbar.policy.GPSController;
import com.android.systemui.statusbar.policy.LockScreenController;
import com.android.systemui.statusbar.policy.MobileDataController;
import com.android.systemui.statusbar.policy.ToggleSlider;
import com.android.systemui.statusbar.policy.VolumeController;
import com.android.systemui.statusbar.policy.WifiController;

public class SettingsView extends LinearLayout implements View.OnClickListener {
    static final String TAG = "SettingsView";

    public static final String BUTTON_WIFI = "toggleWifi";
    public static final String BUTTON_BLUETOOTH = "toggleBluetooth";
    public static final String BUTTON_BRIGHTNESS = "toggleBrightness";
    public static final String BUTTON_SOUND = "toggleSound";
    public static final String BUTTON_NOTIFICATIONS = "toggleNotifications";
    public static final String BUTTON_SETTINGS = "toggleSettings";
    public static final String BUTTON_AUTOROTATE = "toggleAutoRotate";
    public static final String BUTTON_AIRPLANE = "toggleAirplane";
    public static final String BUTTON_GPS = "toggleGPS";
    public static final String BUTTON_MEDIA = "toggleMedia";
    public static final String BUTTON_MOBILEDATA = "toggleMobileData";
    public static final String BUTTON_LOCKSCREEN = "toggleLockScreen";
    public static final String BUTTON_DELIMITER = "|";
    public static final String BUTTONS_DEFAULT = BUTTON_AIRPLANE + BUTTON_DELIMITER +
            BUTTON_WIFI + BUTTON_DELIMITER + BUTTON_BLUETOOTH + BUTTON_DELIMITER +
            BUTTON_BRIGHTNESS + BUTTON_DELIMITER + BUTTON_SOUND + BUTTON_DELIMITER +
            BUTTON_AUTOROTATE + BUTTON_DELIMITER + BUTTON_NOTIFICATIONS + BUTTON_DELIMITER +
            BUTTON_SETTINGS;

    AirplaneModeController mAirplane;
    AutoRotateController mRotate;
    BrightnessController mBrightness;
    DoNotDisturbController mDoNotDisturb;
    BluetoothController mBluetooth;
    GPSController mGPS;
    LockScreenController mLock;
    MobileDataController mData;
    WifiController mWifi;
    VolumeController mVolume;

    public SettingsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final Context context = getContext();

        String rows = Settings.System.getString(context.getContentResolver(),
                Settings.System.COMBINED_BAR_SETTINGS);

        if (rows == null) {
            rows = BUTTONS_DEFAULT;
        }

        setupRows(context, rows);
    }

    private void setupRows(Context context, String rows) {
        String[] settingsRow = rows.split("\\|");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);
        LinearLayout.LayoutParams iconlp = new LinearLayout.LayoutParams(
                64, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams spacerlp = new LinearLayout.LayoutParams(
                90, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams separatorlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        LinearLayout.LayoutParams textlp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textlp.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams sliderlp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout.LayoutParams switchlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        switchlp.gravity = Gravity.CENTER_VERTICAL;
        for (int i = 0; i < settingsRow.length; i++) {
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setPadding(0, 0, 64, 0);
            ImageView icon = new ImageView(context);
            icon.setScaleType(ImageView.ScaleType.CENTER);
            if (settingsRow[i].contains(BUTTON_WIFI)) {
                icon.setImageResource(R.drawable.ic_sysbar_wifi_on);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_wifi_button);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mWifi = new WifiController(context, toggle);
                ll.addView(toggle, switchlp);
                ll.setId(1);
                ll.setOnClickListener(this);
            } else if (settingsRow[i].contains(BUTTON_BLUETOOTH)) {
                icon.setImageResource(R.drawable.stat_sys_data_bluetooth);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_bluetooth_button);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mBluetooth = new BluetoothController(context, toggle);
                ll.addView(toggle, switchlp);
                ll.setId(2);
                ll.setOnClickListener(this);
            } else if (settingsRow[i].contains(BUTTON_BRIGHTNESS)) {
                icon.setImageResource(R.drawable.ic_sysbar_brightness);
                ll.addView(icon, iconlp);
                ToggleSlider toggle = new ToggleSlider(context);
                toggle.setLabel(R.string.status_bar_settings_auto_brightness_label);
                mBrightness = new BrightnessController(context, toggle);
                ll.addView(toggle, sliderlp);
            } else if (settingsRow[i].contains(BUTTON_SOUND)) {
                icon.setImageResource(R.drawable.stat_ring_on);
                ll.addView(icon, iconlp);
                ToggleSlider toggle = new ToggleSlider(context);
                toggle.setLabel(R.string.status_bar_settings_mute_label);
                mVolume = new VolumeController(context, toggle);
                ll.addView(toggle, sliderlp);
            } else if (settingsRow[i].contains(BUTTON_NOTIFICATIONS)) {
                icon.setImageResource(R.drawable.ic_notification_open);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_notifications);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mDoNotDisturb = new DoNotDisturbController(context, toggle);
                ll.addView(toggle, switchlp);
            } else if (settingsRow[i].contains(BUTTON_SETTINGS)) {
                icon.setImageResource(R.drawable.ic_sysbar_quicksettings);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_settings_button);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                ll.setId(3);
                ll.setOnClickListener(this);
            } else if (settingsRow[i].contains(BUTTON_AUTOROTATE)) {
                icon.setImageResource(R.drawable.ic_sysbar_rotate_on);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_auto_rotation);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mRotate = new AutoRotateController(context, toggle);
                ll.addView(toggle, switchlp);
            } else if (settingsRow[i].contains(BUTTON_AIRPLANE)) {
                icon.setImageResource(R.drawable.ic_sysbar_airplane_on);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_airplane);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mAirplane = new AirplaneModeController(context, toggle);
                ll.addView(toggle, switchlp);
            } else if (settingsRow[i].contains(BUTTON_GPS)) {
                icon.setImageResource(R.drawable.stat_gps_on);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_gps_button);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mGPS = new GPSController(context, toggle);
                ll.addView(toggle, switchlp);
                ll.setId(4);
                ll.setOnClickListener(this);
            } else if (settingsRow[i].contains(BUTTON_MEDIA)) {
                float screenDensity = getResources().getDisplayMetrics().density;
                if (screenDensity == 1.0f) {
                    View spacer = new View(context);
                    ll.addView(spacer, spacerlp);
                }
                icon.setImageResource(R.drawable.stat_media_previous);
                icon.setId(5);
                icon.setOnClickListener(this);
                icon.setBackgroundResource(R.drawable.expanded_settings_background);
                ll.addView(icon, iconlp);
                ImageView iconTwo = new ImageView(context);
                iconTwo.setScaleType(ImageView.ScaleType.CENTER);
                iconTwo.setImageResource(R.drawable.stat_media_pause);
                iconTwo.setId(6);
                iconTwo.setOnClickListener(this);
                iconTwo.setBackgroundResource(R.drawable.expanded_settings_background);
                ll.addView(iconTwo, iconlp);
                ImageView iconThree = new ImageView(context);
                iconThree.setScaleType(ImageView.ScaleType.CENTER);
                iconThree.setImageResource(R.drawable.stat_media_play);
                iconThree.setId(7);
                iconThree.setOnClickListener(this);
                iconThree.setBackgroundResource(R.drawable.expanded_settings_background);
                ll.addView(iconThree, iconlp);
                ImageView iconFour = new ImageView(context);
                iconFour.setScaleType(ImageView.ScaleType.CENTER);
                iconFour.setImageResource(R.drawable.stat_media_next);
                iconFour.setId(8);
                iconFour.setOnClickListener(this);
                iconFour.setBackgroundResource(R.drawable.expanded_settings_background);
                ll.addView(iconFour, iconlp);
            } else if (settingsRow[i].contains(BUTTON_MOBILEDATA)) {
                icon.setImageResource(R.drawable.stat_data_on);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_data_button);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mData = new MobileDataController(context, toggle);
                ll.addView(toggle, switchlp);
                ll.setId(9);
                ll.setOnClickListener(this);
            } else if (settingsRow[i].contains(BUTTON_LOCKSCREEN)) {
                icon.setImageResource(R.drawable.stat_lock_screen_on);
                ll.addView(icon, iconlp);
                TextView text = new TextView(context);
                text.setText(R.string.status_bar_settings_lockscreen_button);
                text.setGravity(Gravity.CENTER_VERTICAL);
                text.setTextSize(18);
                ll.addView(text, textlp);
                Switch toggle = new Switch(context);
                toggle.setGravity(Gravity.CENTER_VERTICAL);
                mLock = new LockScreenController(context, toggle);
                ll.addView(toggle, switchlp);
                ll.setId(10);
                ll.setOnClickListener(this);
            }

            addView(ll, lp);
            View separator = new View(context);
            separator.setBackgroundResource(com.android.internal.R.drawable.divider_horizontal_dark);
            addView(separator, separatorlp);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAirplane != null) mAirplane.release();
        if (mDoNotDisturb != null) mDoNotDisturb.release();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                onClickNetwork();
                break;
            case 2:
                onClickBluetooth();
                break;
            case 3:
                onClickSettings();
                break;
            case 4:
                onClickGPS();
                break;
            case 5:
                onClickMedia(1);
                break;
            case 6:
                onClickMedia(2);
                break;
            case 7:
                onClickMedia(3);
                break;
            case 8:
                onClickMedia(4);
                break;
            case 9:
                onClickMobileData();
                break;
            case 10:
                onClickLockScreen();
                break;
        }
    }

    private StatusBarManager getStatusBarManager() {
        return (StatusBarManager)getContext().getSystemService(Context.STATUS_BAR_SERVICE);
    }

    // Network
    // ----------------------------
    private void onClickNetwork() {
        getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        getStatusBarManager().collapse();
    }

// Bluetooth
    // ----------------------------
    private void onClickBluetooth() {
        getContext().startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        getStatusBarManager().collapse();
    }

    // Settings
    // ----------------------------
    private void onClickSettings() {
        getContext().startActivity(new Intent(Settings.ACTION_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        getStatusBarManager().collapse();
    }

    // GPS
    // ----------------------------
    private void onClickGPS() {
        Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        getStatusBarManager().collapse();
    }

    // Media
    // ----------------------------
    private void onClickMedia(int index) {
        long eventtime = SystemClock.uptimeMillis();
        int code = 0;

        switch (index) {
            case 1:
                code = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
                break;
            case 2:
                code = KeyEvent.KEYCODE_MEDIA_PAUSE;
                break;
            case 3:
                code = KeyEvent.KEYCODE_MEDIA_PLAY;
                break;
            case 4:
                code = KeyEvent.KEYCODE_MEDIA_NEXT;
                break;
        }

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, code, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        getContext().sendOrderedBroadcast(downIntent, null);

        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, code, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        getContext().sendOrderedBroadcast(upIntent, null);
    }

    // Mobile data
    // ----------------------------
    private void onClickMobileData() {
        Intent intent = new Intent("android.settings.DATA_USAGE");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        getStatusBarManager().collapse();
    }

    // Lock screen
    // ----------------------------
    private void onClickLockScreen() {
        Intent intent = new Intent("android.settings.SECURITY_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        getStatusBarManager().collapse();
    }
}

