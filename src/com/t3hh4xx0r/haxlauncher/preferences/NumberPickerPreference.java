package com.t3hh4xx0r.haxlauncher.preferences;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.t3hh4xx0r.haxlauncher.R;


public class NumberPickerPreference extends DialogPreference implements OnValueChangeListener{

	private static final String TAG = NumberPickerPreference.class.getSimpleName();
	private NumberPicker mPicker;
	private Integer mInitialValue;
	private int mMaxValue;
	private int mMinValue;
	
	public NumberPickerPreference(Context context) {
		this(context, null);
	}
	public NumberPickerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}public NumberPickerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDialogLayoutResource(R.xml.number_pref);
		this.setPositiveButtonText(R.string.Select);
		this.setNegativeButtonText(R.string.Cancel);
		Resources r = context.getResources();
		mMaxValue = r.getInteger(R.integer.max_columns_rows);
		mMinValue = r.getInteger(R.integer.min_columns_rows);
		Log.i(TAG,"The preference key is" + this.getKey());
		
	}
	
	
	 @Override 
	  protected View onCreateDialogView() {
		View superv =  super.onCreateDialogView();
		if(superv != null){
			Log.i(TAG, 
					"Returning the default implementation," +
					" setDialogLayoutResource was called with id = " +this.getDialogLayoutResource());
			
			 // Find the picker and save it 
			 mPicker  = (NumberPicker)superv.findViewById(R.id.pref_num_picker);
	         mPicker.setOnValueChangedListener(this);
	         
			return superv; // Return the default implementation if a view was created
		}
		
		Log.i(TAG, 
				"Creating the default layout");
		
		 LinearLayout dialogLayout = new LinearLayout(this.getContext());
         mPicker = new NumberPicker(getContext());


         //Set View attributes
         dialogLayout.setOrientation(LinearLayout.VERTICAL);

         mPicker.setOnValueChangedListener(this);
         dialogLayout.addView(mPicker, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    
       

         return dialogLayout; // Create and add a number picker if a layout was not specified

	 }
	 
	 

	@Override
	protected void onBindDialogView(View view) {

		Log.i(TAG,"Binding the dialog view");
		super.onBindDialogView(view);
		if(mPicker == null)
			mPicker  = (NumberPicker)view.findViewById(R.id.pref_num_picker);

		  if (shouldPersist())
             mInitialValue = getPersistedInt(4);
	    mPicker.setMaxValue(mMaxValue);
        mPicker.setMinValue(mMinValue);
		mPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		if (mInitialValue != null ) mPicker.setValue(mInitialValue);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if ( which == DialogInterface.BUTTON_POSITIVE ) {
			mInitialValue = mPicker.getValue();
			persistInt( mInitialValue );
			callChangeListener( mInitialValue );
			Log.i(TAG,"Persiting value" + mInitialValue);
		}
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {
		int def = ( defaultValue instanceof Number ) ? (Integer)defaultValue
				: ( defaultValue != null ) ? Integer.parseInt(defaultValue.toString()) : 1;
		if ( restorePersistedValue ) {
			mInitialValue = getPersistedInt(def);
		}
		else mInitialValue = (Integer)defaultValue;
	}
		
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, 4);
	}
	
	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

		Log.i(TAG,"Value changed from " + oldVal +"to " + newVal);
		// TODO Auto-generated method stub
		
	}
	
		
}