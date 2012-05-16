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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.widget.CompoundButton;

public class BluetoothTetherController implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private String[] mBluetoothRegexs;
    private BluetoothPan mBluetoothPan;
    private CompoundButton mCheckBox;
    private ConnectivityManager mCm;

    public BluetoothTetherController(Context context, CompoundButton checkbox) {
        mContext = context;
        mCheckBox = checkbox;

        mCm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mBluetoothRegexs = mCm.getTetherableBluetoothRegexs();

        mCheckBox.setOnCheckedChangeListener(this);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(mContext, mProfileServiceListener,
                    BluetoothProfile.PAN);
        }

        updateState();
    }

    public void onCheckedChanged(CompoundButton view, boolean checked) {
        if (checked) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter.getState() == BluetoothAdapter.STATE_OFF) {
                adapter.enable();
            }
            mBluetoothPan.setBluetoothTethering(true);
        } else {
            boolean errored = false;

            String [] tethered = mCm.getTetheredIfaces();
            String bluetoothIface = findIface(tethered, mBluetoothRegexs);
            if (bluetoothIface != null &&
                    mCm.untether(bluetoothIface) != ConnectivityManager.TETHER_ERROR_NO_ERROR) {
                errored = true;
            }

            mBluetoothPan.setBluetoothTethering(false);
        }

    }

    private BluetoothProfile.ServiceListener mProfileServiceListener =
        new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            mBluetoothPan = (BluetoothPan) proxy;
        }
        public void onServiceDisconnected(int profile) {
            mBluetoothPan = null;
        }
    };

    private void updateState() {
        String[] available = mCm.getTetherableIfaces();
        String[] tethered = mCm.getTetheredIfaces();
        String[] errored = mCm.getTetheringErroredIfaces();
        updateBluetoothState(available, tethered, errored);
    }

    private void updateBluetoothState(String[] available, String[] tethered,
            String[] errored) {
        int bluetoothTethered = 0;
        for (String s : tethered) {
            for (String regex : mBluetoothRegexs) {
                if (s.matches(regex)) bluetoothTethered++;
            }
        }
        boolean bluetoothErrored = false;
        for (String s: errored) {
            for (String regex : mBluetoothRegexs) {
                if (s.matches(regex)) bluetoothErrored = true;
            }
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        int btState = adapter.getState();
        if (btState == BluetoothAdapter.STATE_TURNING_OFF) {
            mCheckBox.setEnabled(false);
        } else if (btState == BluetoothAdapter.STATE_TURNING_ON) {
            mCheckBox.setEnabled(false);
        } else if (btState == BluetoothAdapter.STATE_ON && mBluetoothPan.isTetheringOn()) {
            mCheckBox.setChecked(true);
            mCheckBox.setEnabled(true);
        } else {
            mCheckBox.setEnabled(true);
            mCheckBox.setChecked(false);
        }
    }

    private static String findIface(String[] ifaces, String[] regexes) {
        for (String iface : ifaces) {
            for (String regex : regexes) {
                if (iface.matches(regex)) {
                    return iface;
                }
            }
        }
        return null;
    }
}

