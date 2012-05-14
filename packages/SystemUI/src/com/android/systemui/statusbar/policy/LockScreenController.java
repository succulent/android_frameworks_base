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
import android.provider.Settings;
import android.widget.CompoundButton;
import com.android.internal.widget.LockPatternUtils;

public class LockScreenController implements CompoundButton.OnCheckedChangeListener {
    private Context mContext;
    private LockPatternUtils mLockPatternUtils;

    public LockScreenController(Context context, CompoundButton checkbox) {
        mContext = context;
        mLockPatternUtils = new LockPatternUtils(context);
        boolean enabled = !mLockPatternUtils.isLockScreenDisabled();
        checkbox.setChecked(enabled);
        checkbox.setEnabled(!mLockPatternUtils.isSecure());
        checkbox.setOnCheckedChangeListener(this);
    }

    public void onCheckedChanged(CompoundButton view, boolean checked) {
        if (!mLockPatternUtils.isSecure()) {
            if (!checked) {
                mLockPatternUtils.setLockScreenDisabled(true);
            } else {
                mLockPatternUtils.setLockScreenDisabled(false);
            }
        } else {
            view.setEnabled(false);
        }
    }
}

