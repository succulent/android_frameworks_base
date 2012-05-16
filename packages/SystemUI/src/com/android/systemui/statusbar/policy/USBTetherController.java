/*
 * Copyright (C) 2008 The Android Open Source Project
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
import android.net.ConnectivityManager;
import android.os.Environment;
import android.provider.Settings;
import android.widget.CompoundButton;

public class USBTetherController implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private boolean mMassStorageActive;
    private String[] mUsbRegexs;
    private CompoundButton mCheckBox;
    private ConnectivityManager mCm;

    public USBTetherController(Context context, CompoundButton checkbox) {
        mContext = context;
        mCheckBox = checkbox;
        mMassStorageActive = Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState());

        mCm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mUsbRegexs = mCm.getTetherableUsbRegexs();

        mCheckBox.setOnCheckedChangeListener(this);

        updateState();
    }

    public void onCheckedChanged(CompoundButton view, boolean checked) {
        setUsbTethering(checked);
    }

    private void setUsbTethering(boolean enabled) {
        if (mCm.setUsbTethering(enabled) != ConnectivityManager.TETHER_ERROR_NO_ERROR) {
            mCheckBox.setChecked(!enabled);
            return;
        }
    }

    private void updateState() {
        String[] available = mCm.getTetherableIfaces();
        String[] tethered = mCm.getTetheredIfaces();
        String[] errored = mCm.getTetheringErroredIfaces();
        updateUsbState(available, tethered, errored);
    }

    private void updateUsbState(String[] available, String[] tethered,
            String[] errored) {
        boolean usbAvailable = !mMassStorageActive;
        int usbError = ConnectivityManager.TETHER_ERROR_NO_ERROR;
        for (String s : available) {
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) {
                    if (usbError == ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                        usbError = mCm.getLastTetherError(s);
                    }
                }
            }
        }
        boolean usbTethered = false;
        for (String s : tethered) {
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) usbTethered = true;
            }
        }
        boolean usbErrored = false;
        for (String s: errored) {
            for (String regex : mUsbRegexs) {
                if (s.matches(regex)) usbErrored = true;
            }
        }

        if (usbTethered) {
            mCheckBox.setEnabled(true);
            mCheckBox.setChecked(true);
        } else if (mMassStorageActive) {
            mCheckBox.setEnabled(false);
        } else {
            mCheckBox.setEnabled(true);
            mCheckBox.setChecked(false);
        }
    }
}

