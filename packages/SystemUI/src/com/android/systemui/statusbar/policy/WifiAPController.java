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
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.widget.CompoundButton;

public class WifiAPController implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;

    public WifiAPController(Context context, CompoundButton checkbox) {
        mContext = context;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int state = 0;
        if (wifiManager != null) {
            state = wifiManager.getWifiApState();
        }

        boolean enabled = state == WifiManager.WIFI_AP_STATE_ENABLED;
        checkbox.setChecked(enabled);
        checkbox.setEnabled(wifiManager != null && getDataState(context));
        checkbox.setOnCheckedChangeListener(this);
    }

    private static boolean getDataState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getMobileDataEnabled();
    }

    public void onCheckedChanged(CompoundButton view, boolean checked) {
        final boolean desiredState = checked;
        final WifiManager wifiManager =
                (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        if (checked) wifiManager.setWifiEnabled(true);
        // Actually request the Wi-Fi AP change and persistent
        // settings write off the UI thread, as it can take a
        // user-noticeable amount of time, especially if there's
        // disk contention.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... args) {
                /**
                 * Disable Wif if enabling tethering
                 */
                int wifiState = wifiManager.getWifiState();
                if (desiredState && ((wifiState == WifiManager.WIFI_STATE_ENABLING) ||
                        (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
                    wifiManager.setWifiEnabled(false);
                }

                wifiManager.setWifiApEnabled(null, desiredState);
                if (!desiredState) wifiManager.setWifiEnabled(true);
                return null;
            }
        }.execute();
    }
}

