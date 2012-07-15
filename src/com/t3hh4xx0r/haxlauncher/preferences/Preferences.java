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

package com.t3hh4xx0r.haxlauncher.preferences;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.t3hh4xx0r.haxlauncher.R;

public class Preferences extends PreferenceActivity {

    private static final String TAG = "Launcher.Preferences";
    NumberPickerPreference mXCount;
    
    private static final String LAUNCHER = "com.t3hh4xx0r.haxlauncher";

    // Cell count preferences
    public static final String SCREEN_CELL_COUNT= "Launcher.Cell.Preferences";
    public static final String SCREEN_CELL_COUNT_X= "Launcher.Cell.Preferences.X";
    public static final String SCREEN_CELL_COUNT_Y= "Launcher.Cell.Preferences.Y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
    }
    
    @Override
    protected void onPause(){
    	restartLauncher2();
    }

	public void restartLauncher2() {

		Log.d(TAG, "About to kill the launcher application"); 
		((ActivityManager)this.getSystemService(PreferenceActivity.ACTIVITY_SERVICE)).killBackgroundProcesses(LAUNCHER);
		Intent i = this.getIntent();
		finish();
		startActivity(i);
		  }
    
}