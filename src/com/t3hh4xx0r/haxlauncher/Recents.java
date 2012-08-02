/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.t3hh4xx0r.haxlauncher;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.LauncherMenu;

public class Recents extends FrameLayout {
    private static final String TAG = "Recents";

    private Launcher mLauncher;
    private CellLayout mContent;

    private int mCellCountX;
    private int mCellCountY;
    private boolean mIsLandscape;
    Context ctx;

    public Recents(Context context) {
        this(context, null);
    }

    public Recents(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Recents(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ctx = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Hotseat, defStyle, 0);
        mCellCountX = a.getInt(R.styleable.Hotseat_cellCountX, -1);
        mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
        mIsLandscape = context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
    }

    CellLayout getLayout() {
        return mContent;
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return mIsLandscape ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return mIsLandscape ? 0 : rank;
    }
    int getCellYFromOrder(int rank) {
        return mIsLandscape ? (mContent.getCountY() - (rank + 1)) : 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCellCountX < 0) mCellCountX = LauncherModel.getCellCountX();
        if (mCellCountY < 0) mCellCountY = LauncherModel.getCellCountY();
        mContent = (CellLayout) findViewById(R.id.layout);
        mContent.setGridSize(mCellCountX, mCellCountY);
        resetLayout();
    }

    void resetLayout() {
        mContent.removeAllViewsInLayout();
        setupRecents();
    }

    private void setupRecents() {
    	ActivityManager actvityManager = (ActivityManager)
    	ctx.getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager pm = ctx.getPackageManager();
    	List<RecentTaskInfo> apps = actvityManager.getRecentTasks(5, ActivityManager.RECENT_IGNORE_UNAVAILABLE);
    	ResolveInfo homeInfo = pm.resolveActivity(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
                0);
    	int j = 0;
    	for (int i=0;i<apps.size();i++) {
    		final ActivityManager.RecentTaskInfo info = apps.get(i);
            Intent intent = new Intent(info.baseIntent);
            if (info.origActivity != null) {
                intent.setComponent(info.origActivity);
            }

            intent.setFlags((intent.getFlags()&~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                final ActivityInfo activityInfo = resolveInfo.activityInfo;
                final Drawable icon = activityInfo.loadIcon(pm);
                ImageView image = new ImageView(ctx);
                image.setImageDrawable(icon);
                int x = getCellXFromOrder(j+i+1);
                int y = getCellYFromOrder(j+1+i);
                if (!activityInfo.name.equals("com.t3hh4xx0r.haxlauncher")) {                	
                	mContent.addViewToCellLayout(image, -1, 0, new CellLayout.LayoutParams(x,y,1,1),
                			true);                
                	image.setOnClickListener(new OnClickListener() {
                		@Override
                		public void onClick(View v) {
                			LauncherMenu.startApplication(activityInfo.packageName);
                		}
                	});
                } else {
                	j++;
                	Log.d("APP", activityInfo.name);
                }
            }
    	}
    }
}
