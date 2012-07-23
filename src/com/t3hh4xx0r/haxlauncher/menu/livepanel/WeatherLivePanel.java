package com.t3hh4xx0r.haxlauncher.menu.livepanel;

import com.t3hh4xx0r.haxlauncher.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class WeatherLivePanel extends RelativeLayout {
	
	public WeatherLivePanel(Context context) {
		super(context);
		   LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   View view = layoutInflater.inflate(R.layout.weather_lp, this);
	}
}
