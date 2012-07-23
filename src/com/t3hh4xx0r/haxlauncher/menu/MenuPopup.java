package com.t3hh4xx0r.haxlauncher.menu;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.Launcher;
import com.t3hh4xx0r.haxlauncher.R;
import com.t3hh4xx0r.haxlauncher.menu.livepanel.WeatherLivePanel;

/**
 * This class does most of the work of wrapping the {@link PopupWindow} so it's simpler to use.
 * 
 * @author qberticus
 * 
 */
public class MenuPopup {
	protected final View anchor;
	private final PopupWindow window;
	private View root;
	private Drawable background = null;
	private final WindowManager windowManager;
    private static ViewFlipper flipper;
    private static ViewFlipper tFlipper;
    private static Launcher mLauncher;

    // animations
    private static Animation slideLeftIn;
    private static Animation slideLeftOut;
    private static Animation slideRightIn;
    private static Animation slideRightOut;
    private static Animation slideLeftInHalf;
    private static Animation slideLeftOutHalf;
    private static Animation slideRightInHalf;
    private static Animation slideRightOutHalf;    
    
    EditText searchBox;
    LinearLayout dock;
    String[] hotseatIntents;
    String[] sortedNums;

	/**
	 * Create a BetterPopupWindow
	 * 
	 * @param anchor
	 *            the view that the BetterPopupWindow will be displaying 'from'
	 */
	public MenuPopup(View anchor) {
		this.anchor = anchor;
		this.window = new PopupWindow(anchor.getContext());
		// when a touch even happens outside of the window
		// make the window go away
		this.window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					MenuPopup.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		this.windowManager = (WindowManager) this.anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		onCreate();
	}

	/**
	 * Anything you want to have happen when created. Probably should create a view and setup the event listeners on
	 * child views.
	 */
	protected void onCreate() {}
	
	protected void onFlash() {}

	/**
	 * In case there is stuff to do right before displaying.
	 */
	protected void onShow() {}

	private void preShow() {
		if(this.root == null) {
			throw new IllegalStateException("setContentView was not called with a view to display.");
		}
		onShow();

		if(this.background == null) {
			this.window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			this.window.setBackgroundDrawable(this.background);
		}

		// if using PopupWindow#setBackgroundDrawable this is the only values of the width and hight that make it work
		// otherwise you need to set the background of the root viewgroup
		// and set the popupwindow background to an empty BitmapDrawable
		this.window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		this.window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		this.window.setTouchable(true);
		this.window.setFocusable(true);
		this.window.setOutsideTouchable(true);

		this.window.setContentView(this.root);
	}

	public void setBackgroundDrawable(Drawable background) {
		this.background = background;
	}

	/**
	 * Sets the content view. Probably should be called from {@link onCreate}
	 * 
	 * @param root
	 *            the view the popup will display
	 */
	public void setContentView(View root) {
		this.root = root;
		this.window.setContentView(root);
	}

	/**
	 * Will inflate and set the view from a resource id
	 * 
	 * @param layoutResID
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator =
				(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * If you want to do anything when {@link dismiss} is called
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		this.window.setOnDismissListener(listener);
	}

	/**
	 * Displays like a popdown menu from the anchor view
	 */
	public void showLikePopDownMenu() {
		this.showLikePopDownMenu(0, 0);
	}

	/**
	 * Displays like a popdown menu from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in X direction
	 * @param yOffset
	 *            offset in Y direction
	 */
	public void showLikePopDownMenu(int xOffset, int yOffset) {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_PopDownMenu);

		this.window.showAsDropDown(this.anchor, xOffset, yOffset);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 */
	public void showLikeQuickAction() {
		this.showLikeQuickAction(0, 0);
	}

	/**
	 * Displays like a QuickAction from the anchor view.
	 * 
	 * @param xOffset
	 *            offset in the X direction
	 * @param yOffset
	 *            offset in the Y direction
	 */
	public void showLikeQuickAction(int xOffset, int yOffset) {
		this.preShow();

		this.window.setAnimationStyle(R.style.Animations_GrowFromBottom);

		int[] location = new int[2];
		this.anchor.getLocationOnScreen(location);

		Rect anchorRect =
				new Rect(location[0], location[1], location[0] + this.anchor.getWidth(), location[1]
					+ this.anchor.getHeight());

		this.root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootWidth = this.root.getMeasuredWidth();
		int rootHeight = this.root.getMeasuredHeight();

		int screenWidth = this.windowManager.getDefaultDisplay().getWidth();

		int xPos = ((screenWidth - rootWidth) / 2) + xOffset;
		int yPos = ((anchorRect.top - rootHeight) / 3);

		this.window.showAtLocation(this.anchor, Gravity.TOP, xPos, yPos);
	}

	public void dismiss() {
		this.window.dismiss();
	}
	
	public static class DemoPopupWindow extends MenuPopup implements OnClickListener {
		public DemoPopupWindow(View anchor, Launcher launcher) {
                  super(anchor);
                  mLauncher = launcher;
        }
	
        @Override
        protected void onCreate() {
			LayoutInflater inflater = (LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup, null);
         	flipper = (ViewFlipper) root.findViewById(R.id.flipper);
         	tFlipper = (ViewFlipper) root.findViewById(R.id.title_flipper);
            slideLeftIn = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_in_left);
            slideLeftOut = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_out_left);
            slideRightIn = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_in_right);
            slideRightOut = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_out_right);
            slideLeftInHalf = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_in_left_half);
            slideLeftOutHalf = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_out_left_half);
            slideRightInHalf = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_in_right_half);
            slideRightOutHalf = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.slide_out_right_half);                        
            getLivePanel(this.anchor.getContext(), root);
            dock = (LinearLayout) root.findViewById(R.id.dock);
            TextView menum = (TextView) root.findViewById(R.id.main_main);
            menum.setOnClickListener(this);
            TextView searchm = (TextView) root.findViewById(R.id.search_main);
            searchm.setOnClickListener(this);
            TextView menus = (TextView) root.findViewById(R.id.main_search);
            menus.setOnClickListener(this);
            TextView searchs = (TextView) root.findViewById(R.id.search_search);
            searchs.setOnClickListener(this);
            RelativeLayout allApps = (RelativeLayout) root.findViewById(R.id.apps);
            allApps.setOnClickListener(this);
            RelativeLayout search = (RelativeLayout) root.findViewById(R.id.search);
            RelativeLayout settings = (RelativeLayout) root.findViewById(R.id.settings);
            search.setOnClickListener(this);
            settings.setOnClickListener(this);
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
            final String lv_arr[]= getSearchableItems(anchor.getContext());
            final ArrayList<String> arr_sort= new ArrayList<String>();
            final SearchAdapter a = new SearchAdapter(anchor.getContext(), arr_sort, (Activity) anchor.getContext());
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
//         	for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
//             	View v = root.getChildAt(i);
//            	if(v instanceof LinearLayout) {
//                	LinearLayout lL = (LinearLayout) v;
//                	for(int j = 0, jcount = lL.getChildCount() ; j < jcount ; j++) {
//                    	View item = lL.getChildAt(j);
//                    	item.setOnClickListener(this);
//               	     }
//                }
//            }
            setupHotseats(dock, this.anchor.getContext());
            this.setContentView(root);
        }

		private void getLivePanel(Context context, ViewGroup root) {
			//modify please
			if (true) {
				WeatherLivePanel panel = new WeatherLivePanel(context);
				root.addView(panel);
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
		
		private void setupHotseats(LinearLayout dock, Context ctx) {
			DBAdapter db = new DBAdapter(ctx);
			db.open();
			Cursor c = db.getAllHotseats();
			hotseatIntents = new String[c.getCount()];
			while (c.moveToNext()) {
				hotseatIntents[c.getPosition()] = c.getString(c.getColumnIndex("intent"));
				ImageView hotseat = new ImageView(ctx);
				byte[] icon = c.getBlob(c.getColumnIndex("icon"));
				Bitmap b = BitmapFactory.decodeByteArray(icon, 0, icon.length);
				hotseat.setImageBitmap(b);
				dock.addView(hotseat);
				hotseat.setOnClickListener(this);
				hotseat.setId(c.getPosition());
			}
			c.close();
			db.close();
			if (dock.getChildCount() < 1) {
				dock.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.search_main || v.getId() == R.id.search_search || v.getId() == R.id.search) {
				searchBox.setText("");
				flipTo(1);
			} else if (v.getId() == R.id.main_main || v.getId() == R.id.main_search) {
				flipTo(0);
				searchBox.setText("");
			} else if (v.getId() == R.id.apps) {
				dismiss();
				mLauncher.showAllApps(true);
			} else if (v.getId() == R.id.settings) {
				
			} else if (v.getId() < dock.getChildCount()) {
				startApplication(hotseatIntents[v.getId()]);
			}
		}
		
		private void inFromRight() {
			flipper.setInAnimation(slideRightIn);
			flipper.setOutAnimation(slideLeftOut);
			tFlipper.setInAnimation(slideRightInHalf);
			tFlipper.setOutAnimation(slideLeftOutHalf);
		}
		
		private void inFromLeft() {
			flipper.setInAnimation(slideLeftIn);
			flipper.setOutAnimation(slideRightOut);
			tFlipper.setInAnimation(slideLeftInHalf);
			tFlipper.setOutAnimation(slideRightOutHalf);			
		}
		
		public void flipTo(int where) {
			if (where != flipper.getDisplayedChild()) {
				if (where > flipper.getDisplayedChild()) {
					inFromRight();				
				} else {
					inFromLeft();
				}
				flipper.setDisplayedChild(where);
				tFlipper.setDisplayedChild(where);
			}
		}	
		
		public void setHotseat(int pos, Bitmap icon) {
			((ImageView) dock.getChildAt(2)).setImageBitmap(icon);
		}
	}
	
	
	public void startApplication(String packageName) {
	    try  {
	        Intent intent = new Intent("android.intent.action.MAIN");
	        intent.addCategory("android.intent.category.LAUNCHER");
	        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        List<ResolveInfo> resolveInfoList = anchor.getContext().getPackageManager().queryIntentActivities(intent, 0);

	        for(ResolveInfo info : resolveInfoList)
	            if(info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
	                launchComponent(info.activityInfo.packageName, info.activityInfo.name);
	                return;
	            } else {
	            	Log.d(info.activityInfo.packageName, packageName);
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

	    anchor.getContext().startActivity(intent);
	}
}
