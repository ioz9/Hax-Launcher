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

package com.t3hh4xx0r.haxlauncher.menu;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.Launcher;
import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.SearchAdapter;
import com.t3hh4xx0r.haxlauncher.menu.livepanel.WeatherLivePanel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class LauncherMenu extends RelativeLayout {

	private LauncherMenu mMenu;
    private Launcher mLauncher;
    private View root;
    private ViewFlipper flipper;
    RelativeLayout back;
    EditText searchBox;
    boolean attatched = false;
    Context mContext;
    LinearLayout dock;
    static String[] hotseatIntents;
    DockItemView hotseat;
    Cursor c;
    WeatherLivePanel panel;
    public static final int WEATHER_PANEL_ID = 9999;
    
    private static Animation slideLeftIn;
    private static Animation slideLeftOut;
    private static Animation slideRightIn;
    private static Animation slideRightOut;
    private static Animation slideLeftInHalf;
    private static Animation slideLeftOutHalf;
    private static Animation slideRightInHalf;
    private static Animation slideRightOutHalf;   
    
    public LauncherMenu(final Context context) {
        this(context, null, 0);
        mLauncher = (Launcher) context;
        mMenu = this;
        mContext = context;
        
        slideLeftIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
        slideLeftOut = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_left);
        slideRightIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
        slideRightOut = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right);
        slideLeftInHalf = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left_half);
        slideLeftOutHalf = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_left_half);
        slideRightInHalf = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right_half);
        slideRightOutHalf = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right_half);                        
        
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = layoutInflater.inflate(R.layout.launcher_menu, this);
        getLivePanel(context, root);

    	root.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        Animation slideLeftIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        root.startAnimation(slideLeftIn);	

        back = (RelativeLayout) root.findViewById(R.id.back);
        back.setOnClickListener(listener);
        TextView mainSearch = (TextView) root.findViewById(R.id.main_search);
        mainSearch.setOnClickListener(listener);
        
        searchBox = (EditText) root.findViewById(R.id.search_box);
        final ListView list = (ListView) root.findViewById(R.id.list);
        list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> v1, View v,
					int pos, long id) {
				boolean found = false;
				 List<ApplicationInfo> applications = v.getContext().getPackageManager().getInstalledApplications(0);
				 for (int n=0; n < applications.size(); n++) {
					if (applications.get(n).loadLabel(v.getContext().getPackageManager()).equals((list.getItemAtPosition(pos)))) {							
						startApplication(applications.get(n).packageName);
						found = true;
						break;
					}
				 }
				 
				 if(!found) {
					 Intent i = new Intent(); 
					 i.setAction(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT); 
					 i.setData(Uri.fromParts("tel", parsePhone(list.getItemAtPosition(pos).toString()), null)); 
					 v.getContext().startActivity(i);
				 }
			}
        });
        final String lv_arr[]= getSearchableItems(mContext);
        final ArrayList<String> arr_sort= new ArrayList<String>();
        final SearchAdapter a = new SearchAdapter(mContext, arr_sort, (Activity) mContext);
        list.setAdapter(a);
        searchBox.addTextChangedListener(new TextWatcher() {
        	public void afterTextChanged(Editable s) {
        	}

        	public void beforeTextChanged(CharSequence s, int start, int count,
        			int after) {
        	}

			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {
				int length = searchBox.getText().length();
				arr_sort.clear();
				for(int i=0;i<lv_arr.length;i++){
					if(length<=lv_arr[i].length() && length != 0){
						if(searchBox.getText().toString().equalsIgnoreCase((String) lv_arr[i].subSequence(0, length))){
							arr_sort.add(lv_arr[i]);
						}
					}										
				}
				a.notifyDataSetChanged();
			}					
        });
        flipper = (ViewFlipper) root.findViewById(R.id.flipper);
        dock = (LinearLayout) root.findViewById(R.id.dock);
        TextView apps = (TextView) findViewById(R.id.all_apps);
        apps.setOnClickListener(listener);
        setupHotseats(dock);        
    }

    public LauncherMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LauncherMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    public boolean onTouchEvent(android.view.MotionEvent event) {
//    	super.onTouchEvent(event);
//    	return false;
//    }
    

    public boolean isVisible() {
    	return attatched;
    }
    
    @Override
    protected void onAttachedToWindow() {
    	attatched = true;
        Animation slideLeftIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
        root.startAnimation(slideLeftIn);	
        setupHotseats(dock);
    	super.onAttachedToWindow();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        dock.removeAllViews();
    	attatched = false;        
        super.onDetachedFromWindow();
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
            DisplayMetrics metrics = new DisplayMetrics();
            mLauncher.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            Bitmap b = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            canvas.drawBitmap(b, 0, 0, null);
            c.drawColor(0x99000000);
            c.setBitmap(null);
            b = null;
        super.dispatchDraw(canvas);
    }    
    
    private void log(String message) {
    	Log.d("LAUNCHER MENU", message);
    }
    
	private void setupHotseats(final LinearLayout dock) {
		DBAdapter db = new DBAdapter(mContext);
		db.open();
		c = db.getAllHotseats();
		hotseatIntents = new String[c.getCount()];
		while (c.moveToNext()) {
			hotseatIntents[c.getPosition()] = c.getString(c.getColumnIndex("intent"));
			hotseat = new DockItemView(mContext);
			hotseat.setId(c.getPosition());
			hotseat.setParent(dock);
			hotseat.setData(c.getString(c.getColumnIndex("intent")));
			hotseat.setOnSwipeCallback(new Runnable() {
                public void run() {                	
                	((ViewGroup) hotseat.getParent()).removeView(dock.findViewById(c.getPosition()));
                	log(Integer.toString(hotseat.getId()));
                	log(Integer.toString(c.getPosition()));
                	//log(Integer.toString(dock.findViewById(c.getPosition()).getId()));
                	log(c.getString(c.getColumnIndex("name")));
                	//log(c.getString(c.getColumnIndex("id")));
                	//hotseatIntents = ArrayUtils.removeElement(hotseatIntents, get);
                }
            });
			byte[] icon = c.getBlob(c.getColumnIndex("icon"));
			Bitmap b = BitmapFactory.decodeByteArray(icon, 0, icon.length);
			hotseat.setImageBitmap(b);
			hotseat.setPadding(5, 5, 5, 5);
			hotseat.setScaleType(ImageView.ScaleType.FIT_XY);
			ScrollView.LayoutParams lp = new ScrollView.LayoutParams(75, 75);
			dock.addView(hotseat, lp);
			hotseat.setOnClickListener(listener);
			hotseat.setId(c.getPosition());
		}
		c.close();
		db.close();
		if (dock.getChildCount() < 1) {
			dock.setVisibility(View.GONE);
		}
	}
    
	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.main_search) {
				searchBox.setText("");
				flipTo(1);
			//} else if (v.getId() == R.id.main_main) {
				//flipTo(0);
				//searchBox.setText("");
			} else if (v.getId() == R.id.all_apps) {
				mLauncher.showAllApps(true);
			//} else if (v.getId() == R.id.settings) {
				
			} else if (v.getId() < dock.getChildCount()) {
				startApplication(hotseatIntents[v.getId()]);
			} else if (v.getId() == R.id.back) {
				flipTo(flipper.getDisplayedChild()-1);
			} else if (v.getId() == WEATHER_PANEL_ID) {
				if (!panel.mHandler.hasMessages(WeatherLivePanel.QUERY_WEATHER)) {
	                panel.mHandler.sendEmptyMessage(WeatherLivePanel.QUERY_WEATHER);
	            }
			}
		}
	};

	public void startApplication(String packageName) {
	    try  {
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");
	        List<ResolveInfo> resolveInfoList = mContext.getPackageManager().queryIntentActivities(intent, 0);

	        for(ResolveInfo info : resolveInfoList)
	            if(info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
	                launchComponent(info.activityInfo.packageName, info.activityInfo.name);
	                return;
	            }

	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	private void launchComponent(String packageName, String name) {
	    Intent intent = new Intent("android.intent.action.MAIN");
	    intent.addCategory("android.intent.category.LAUNCHER");
	    intent.setComponent(new ComponentName(packageName, name));
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    mContext.startActivity(intent);
	}
	
	private void inFromRight() {
		flipper.setInAnimation(slideRightIn);
		flipper.setOutAnimation(slideLeftOut);
	}
	
	private void inFromLeft() {
		flipper.setInAnimation(slideLeftIn);
		flipper.setOutAnimation(slideRightOut);			
	}
	
	public void flipTo(int where) {
		if (where == 0) {
			back.setVisibility(View.GONE);
		} else{
			back.setVisibility(View.VISIBLE);
		}
		if (where != flipper.getDisplayedChild()) {
			if (where > flipper.getDisplayedChild()) {
				inFromRight();				
			} else {
				inFromLeft();
			}
			flipper.setDisplayedChild(where);
		}
	}		
	
	protected String parsePhone(String full) {
		String phone = null;
		if (Character.isLetter(full.charAt(0))) {
			phone = full.split("- ")[1];
		} else {
			if (full.startsWith("+")) {
				full.replace("+", "");
			}
			phone = full;
		}
		return phone;
	}

	private String[] getSearchableItems(Context context) {
		ArrayList<String> list = new ArrayList<String>();
		Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		final PackageManager pm = context.getPackageManager();
		while (c.moveToNext()) {
			String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String num = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			if (name != null && !name.equals("") && num != null && !num.equals("")) {
				list.add(name + " - " + num);
			}
		}
	
		List<ApplicationInfo> applications = context.getPackageManager().getInstalledApplications(0);
		for (int n=0; n < applications.size(); n++) {
			if ((applications.get(n).flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
		        list.add(applications.get(n).loadLabel(pm).toString());
		    }
	    }			
		Collection<String> itemsFinal = new TreeSet<String>(Collator.getInstance());
		for (int i=0;i<list.size();i++) {
			itemsFinal.add(list.get(i));
		}
		return (String[]) itemsFinal.toArray(new String[itemsFinal.size()]);	
	}	
	
	private void getLivePanel(Context context, View v) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp.addRule(RelativeLayout.LEFT_OF, R.id.dock_holder);
		lp.setMargins(0, 0, 0, 64);
		//modify please
		if (true) {
			panel = new WeatherLivePanel(context);
			panel.setId(WEATHER_PANEL_ID);
			((ViewGroup) v).addView(panel, lp);
			panel.setOnClickListener(listener);
		}
	}
}
