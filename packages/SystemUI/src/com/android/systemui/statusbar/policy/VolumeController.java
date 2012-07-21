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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.provider.Settings;

public class VolumeController implements ToggleSlider.Listener {
    private AudioManager mAudioManager;
    private ToggleSlider mControl;
    private Context mContext;

    public VolumeController(Context context, ToggleSlider control) {
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mControl = control;
        mContext = context;

        boolean mute = mAudioManager.isMasterMute();
        int volume = mute ? mAudioManager.getLastAudibleMasterVolume() :
                mAudioManager.getMasterVolume();
        control.setMax(mAudioManager.getMasterMaxVolume());
        control.setValue(volume);
        control.setChecked(mute);
        control.setOnChangedListener(this);
    }

    public void onChanged(ToggleSlider view, boolean tracking, boolean mute, int level) {
        if (!tracking) {
            if (mute && !mAudioManager.isMasterMute()) {
                mAudioManager.setMasterMute(mute, 0);
                mControl.setChecked(mute);
            } else if (!mute && mAudioManager.isMasterMute()) {
                mAudioManager.setMasterMute(mute, 0);
            }
            mAudioManager.setMasterVolume(level, AudioManager.FLAG_PLAY_SOUND);
        } else {
            mAudioManager.setMasterVolume(level, 0);
        }
    }
}
