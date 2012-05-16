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

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Slog;
import android.view.View;
import android.widget.TextView;

import com.android.systemui.R;

public class SleepController implements View.OnClickListener {
    private static final String TAG = "StatusBar.SleepController";

    private Context mContext;
    private TextView mText;
    private int mValue;

    public SleepController(Context context, TextView text) {
        mContext = context;
        mText = text;

        mValue = Settings.System.getInt(mContext.getContentResolver(), 
                Settings.System.SCREEN_OFF_TIMEOUT, 60000);

        mText.setOnClickListener(this);
        updateSummary();
    }

    private void updateSummary() {
        String[] entries = mContext.getResources().getStringArray(R.array.screen_timeout_entries);
        String[] values = mContext.getResources().getStringArray(R.array.screen_timeout_values);
        int best = 0;
        for (int i = 0; i < values.length; i++) {
            int timeout = Integer.parseInt(values[i].toString());
            if (mValue >= timeout) {
                best = i;
            }
        }
        mText.setText(mContext.getString(R.string.status_bar_screen_timeout,
                    entries[best]));
    }

    public void onClick(View v) {
        String[] entries = mContext.getResources().getStringArray(R.array.screen_timeout_entries);
        String[] values = mContext.getResources().getStringArray(R.array.screen_timeout_values);
        int best = 0;
        for (int i = 0; i < values.length; i++) {
            int timeout = Integer.parseInt(values[i].toString());
            if (mValue >= timeout) {
                best = i;
            }
        }
        mValue = Integer.parseInt(values[values.length - 1 == best ? 0 : best + 1].toString());
        Settings.System.putInt(mContext.getContentResolver(), 
                Settings.System.SCREEN_OFF_TIMEOUT, mValue);
        updateSummary();
    }
}
