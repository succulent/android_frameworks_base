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
import android.provider.Settings;
import android.widget.CompoundButton;

public class MobileDataController implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;

    public MobileDataController(Context context, CompoundButton checkbox) {
        mContext = context;
        boolean enabled = getDataState(context);
        checkbox.setChecked(enabled);
        checkbox.setOnCheckedChangeListener(this);
    }

    private static boolean getDataState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getMobileDataEnabled();
    }

    private void setDataState(Context context) {
        boolean enabled = getDataState(context);
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (enabled) {
            cm.setMobileDataEnabled(false);
        } else {
            cm.setMobileDataEnabled(true);
        }
    }

    public void onCheckedChanged(CompoundButton view, boolean checked) {
        setDataState(mContext);
    }
}

