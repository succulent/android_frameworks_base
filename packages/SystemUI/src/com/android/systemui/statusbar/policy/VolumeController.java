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
import android.media.AudioManager;

public class VolumeController implements ToggleSlider.Listener {
    private static final int STREAM = AudioManager.STREAM_MUSIC;

    private AudioManager mAudioManager;
    private ToggleSlider mControl;

    public VolumeController(Context context, ToggleSlider control) {
        mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mControl = control;

        boolean mute = mAudioManager.isStreamMute(STREAM);
        int volume = mute ? mAudioManager.getLastAudibleStreamVolume(STREAM) :
                mAudioManager.getStreamVolume(STREAM);
        control.setMax(mAudioManager.getStreamMaxVolume(STREAM));
        control.setValue(volume);
        control.setChecked(mute);
        control.setOnChangedListener(this);
    }

    public void onChanged(ToggleSlider view, boolean tracking, boolean mute, int level) {
        if (!tracking) {
            if (level == 0) mute = true;
            if (mute && !mAudioManager.isStreamMute(STREAM)) {
                mAudioManager.setStreamMute(STREAM, mute);
                mControl.setChecked(mute);
            } else if (!mute && mAudioManager.isStreamMute(STREAM)) {
                mAudioManager.setStreamMute(STREAM, mute);
            }
            mAudioManager.setStreamVolume(STREAM, level, 0);
        } else {
            mAudioManager.setStreamVolume(STREAM, level, 0);
        }
    }
}
