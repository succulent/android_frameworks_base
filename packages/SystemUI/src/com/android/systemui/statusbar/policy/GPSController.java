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

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Slog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.android.systemui.R;

public class GPSController implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;

    public GPSController(Context context, CompoundButton checkbox) {
        mContext = context;
        boolean enabled = getGpsState(context);
        checkbox.setChecked(enabled);
        checkbox.setOnCheckedChangeListener(this);
    }

    private boolean getGpsState(Context context) {
        return Settings.Secure.isLocationProviderEnabled(context.getContentResolver(),
                LocationManager.GPS_PROVIDER);
    }

    private void setGpsState(Context context) {
        boolean enabled = getGpsState(context);
        Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(),
                LocationManager.GPS_PROVIDER, !enabled);
    }

    public void onCheckedChanged(CompoundButton view, boolean checked) {
        setGpsState(mContext);
    }
}

