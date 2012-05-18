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

package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Slog;
import android.view.IWindowManager;
import android.widget.CompoundButton;

public class VolumeController implements ToggleSlider.Listener,
        AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "StatusBar.VolumeController";
    private static final int STREAM_NOTIFICATION = AudioManager.STREAM_NOTIFICATION;
    private static final int STREAM_MUSIC = AudioManager.STREAM_MUSIC;

    private Context mContext;
    private ToggleSlider mControl;
    private AudioManager mAudioManager;

    private boolean mMute;
    private int mVolume;
    private int mStream;
    private boolean mVibeInSilent;

    public VolumeController(Context context, ToggleSlider control) {
        mContext = context;
        mControl = control;
        mControl.setOnChangedListener(this);
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        setupVolume();

        // receive broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction(AudioManager.VOLUME_CHANGED_ACTION);
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    private void setupVolume() {
        boolean isMusicActive = mAudioManager.isMusicActive();
        mStream = isMusicActive ? STREAM_MUSIC : STREAM_NOTIFICATION;
        mVolume = mAudioManager.getStreamVolume(mStream);
        mMute = isMusicActive ? mVolume == 0 :
                mAudioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
        mControl.setMax(mAudioManager.getStreamMaxVolume(mStream));
        mControl.setValue(mVolume);
        mControl.setChecked(mMute);
        mVibeInSilent = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.VIBRATE_IN_SILENT, 1) == 1;
    }

    public void onChanged(ToggleSlider view, boolean tracking, boolean mute, int level) {
        int mStream = mAudioManager.isMusicActive() ? STREAM_MUSIC : STREAM_NOTIFICATION;
        if (!tracking) {
            if (mute && !mAudioManager.isMusicActive()) {
                mAudioManager.setRingerMode(mVibeInSilent ? AudioManager.RINGER_MODE_VIBRATE
                        : AudioManager.RINGER_MODE_SILENT);
                return;
            } else {
                if (!mAudioManager.isMusicActive()) {
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    return;
                } else {
                    mAudioManager.setStreamMute(mStream, mute);
                    return;
                }
            }
        }
        if (!mute) mAudioManager.setStreamVolume(mStream, level, 0);
    }

    public void onAudioFocusChange(int focusChange) {
        setupVolume();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioManager.VOLUME_CHANGED_ACTION)) {
                mVolume = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, mVolume);
            }
            setupVolume();
        }
    };
}
