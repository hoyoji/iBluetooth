package com.hoyoji.android.hyjframework.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.fragment.HyjDialogFragment;
import com.hoyoji.android.hyjframework.fragment.HyjFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.btcontrol.R;

public abstract class HyjActivity extends ActionBarActivity 
{
	public static final int REQUEST_TAKE_PHOTO = 1024;
	
	private ProgressDialog mProgressDialog;
	public DialogCallbackListener mDialogCallback;
	public DialogFragment mDialogFragment;
	private boolean mIsViewInited = false;

	private int mChoiceMode = ListView.CHOICE_MODE_NONE;
	

//	private GestureDetector gestureScanner;
//	GestureListener gestureListener = new GestureListener();
	protected abstract Integer getContentView();
	
	protected Integer getOptionsMenuView(){
		return null;
	}
	
	protected void onInitViewData() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        gestureScanner = new GestureDetector(HyjActivity.this, gestureListener);
		if(getContentView() != null){
			setContentView(getContentView());
	    }
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(!mIsViewInited){
			onInitViewData();	
			mIsViewInited = true;
		}
	}

	protected void onStartWithoutInitViewData() {
		super.onStart();
	}
	
	public ProgressDialog displayProgressDialog(String title, String msg){
		dismissProgressDialog();
		mProgressDialog = ProgressDialog.show(this, title, msg, true, false);  
		return mProgressDialog;
	}
	
	public ProgressDialog displayProgressDialog(int title, int msg){
		dismissProgressDialog();
		mProgressDialog = ProgressDialog.show(this, this.getString(title), this.getString(msg), true, false); 
		return mProgressDialog;
	}
	
	public void dismissProgressDialog(){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
		}
	}
	
	public void displayDialog(String title, String msg) {
		if(mDialogFragment != null){
			mDialogFragment.dismiss();
		}
		mDialogFragment = HyjDialogFragment.newInstance(title, msg, R.string.alert_dialog_ok, -1, -1);
		mDialogFragment.show(getSupportFragmentManager(), "dialog");
	} 
	
	public void displayDialog(int title, int msg) {
		if(mDialogFragment != null){
			mDialogFragment.dismiss();
		}
		
		mDialogFragment = HyjDialogFragment.newInstance(title, msg, R.string.alert_dialog_ok, -1, -1);
		mDialogFragment.show(getSupportFragmentManager(), "dialog");
	} 

	public void displayDialog(int title, int msg, int positiveButton, int negativeButton, int NeutralButton, DialogCallbackListener dialogCallback) {
		this.mDialogCallback = dialogCallback;
		
		if(mDialogFragment != null){
			mDialogFragment.dismiss();
		}
		
		if(positiveButton == -1){
			positiveButton = R.string.alert_dialog_ok;
		}
		
		mDialogFragment = HyjDialogFragment.newInstance(title, msg, positiveButton, negativeButton, NeutralButton);
		mDialogFragment.show(getSupportFragmentManager(), "dialog");
	} 
	
	public void displayDialog(String title, String msg, int positiveButton, int negativeButton, int NeutralButton, DialogCallbackListener dialogCallback) {
		this.mDialogCallback = dialogCallback;
		
		if(mDialogFragment != null){
			mDialogFragment.dismiss();
		}
		
		if(positiveButton == -1){
			positiveButton = R.string.alert_dialog_ok;
		}
		
		mDialogFragment = HyjDialogFragment.newInstance(title, msg, positiveButton, negativeButton, NeutralButton);
		mDialogFragment.show(getSupportFragmentManager(), "dialog");
	} 
	
	public void displayDialog(DialogFragment dialogFragment, DialogCallbackListener dialogCallback) {
		this.mDialogCallback = dialogCallback;
		
		if(mDialogFragment != null){
			mDialogFragment.dismiss();
		}
		
		mDialogFragment = dialogFragment;
		mDialogFragment.show(getSupportFragmentManager(), "dialog");
	} 
	
	public static class DialogCallbackListener {
		public void doNegativeClick() {}
		public void doNeutralClick() {}
		public void doPositiveClick(Object object) {
			// TODO Auto-generated method stub
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		if(getOptionsMenuView() != null){
			MenuInflater inflater = this.getMenuInflater();
			inflater.inflate(getOptionsMenuView(), menu);
		}
	    return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (item.getItemId() == android.R.id.home) {
        	finish();
        	return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
	
	public void dialogDoPositiveClick(Object object) {
		if(mDialogCallback != null){
			mDialogCallback.doPositiveClick(object);
		}
	}
	
	public void dialogDoNegativeClick() {
		if(mDialogCallback != null){
			mDialogCallback.doNegativeClick();
		}
	}
	
	public void dialogDoNeutralClick() {      
		if(mDialogCallback != null){
			mDialogCallback.doNeutralClick();
		}
	}

	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, false, null);
	}
	
	public void openActivityWithFragmentForResult(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle, int requestCode){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, true, requestCode);
	}
	public void openBlankActivityWithFragment(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle){
		openBlankActivityWithFragment(fragmentClass, getString(titleRes), bundle, false, null);
	}
	
	public void openBlankActivityWithFragmentForResult(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle, int requestCode){
		openBlankActivityWithFragment(fragmentClass, getString(titleRes), bundle, true, requestCode);
	}
	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this, HyjBlankUserActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			startActivityForResult(intent, requestCode);
		} else {
			startActivity(intent);
		}
	}
	
	public void openBlankActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this, HyjBlankActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			startActivityForResult(intent, requestCode);
		} else {
			startActivity(intent);
		}
	}
	
	public void addFragment(Class<? extends Fragment> fragment){
		FragmentManager fragmentManager = getSupportFragmentManager();
		try {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			Fragment newFragment = fragment.newInstance();
			fragmentTransaction.add(android.R.id.content, newFragment, "fragment");
			fragmentTransaction.commit();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

// @Override
// protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//     int index = requestCode>>16;
//     if (index != 0) {
//         index--;
////         if (mFragments.mActive == null || index < 0 || index >= mFragments.mActive.size()) {
////             Log.w(TAG, "Activity result fragment index out of range: 0x"
////                     + Integer.toHexString(requestCode));
////             return;
////         }
////         Fragment frag = mFragments.mActive.get(index);
////         if (frag == null) {
////             Log.w(TAG, "Activity result no fragment exists for index: 0x"
////                     + Integer.toHexString(requestCode));
////         }
////         frag.onActivityResult(requestCode&0xffff, resultCode, data);
////         return;
//     }
//     
//     super.onActivityResult(requestCode, resultCode, data);
// }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO) {
	    	Intent intent = new Intent("REQUEST_TAKE_PHOTO");
	    	intent.putExtra("resultCode", resultCode);
	    	if(data != null){
		    	intent.putExtra("selectedImage", data.getDataString());
	    	}
	    	this.sendBroadcast(intent);
	    } else {
	    	super.onActivityResult(requestCode, resultCode, data);
	    	if(requestCode == 32973){ // 微博验证返回时不会调用子fragment的onActivityResult, 所以我们在这里手动调用。
		        List<Fragment> fragments = getSupportFragmentManager().getFragments();
		        if (fragments != null) {
		            for (Fragment fragment : fragments) {
		            	if(fragment.getUserVisibleHint()){
		            		fragment.onActivityResult(requestCode, resultCode, data);
		            	}
		            }
		        }
	    	}
	    }
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
		
//		if(event.getAction() == MotionEvent.ACTION_MOVE){
//
//		     if (mChoiceMode == ListView.CHOICE_MODE_MULTIPLE) {
//		    	 if(gestureListener.isSwipingLeftRight){
//		    		 return false;
//		    	 }
//		     }
//		}
		
	    View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_DOWN && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) { 
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

	
	

	@Override
	public void onBackPressed() {
		if(HyjApplication.getInstance().getCurrentUser() == null){
			super.onBackPressed();
		} else {
			boolean backPressedHandled = false;
			if(getSupportFragmentManager().getFragments() != null){
				for(Fragment f : getSupportFragmentManager().getFragments()){
					if(f instanceof HyjFragment){
						backPressedHandled = backPressedHandled || ((HyjFragment)f).handleBackPressed();
					} else if(f instanceof HyjUserListFragment){
						backPressedHandled = backPressedHandled || ((HyjUserListFragment)f).handleBackPressed();
					} 
				}
			}
			if(!backPressedHandled){
				if(this.isTaskRoot()){
					this.displayDialog(-1, R.string.app_confirm_exit, R.string.alert_dialog_yes, -1, R.string.alert_dialog_no, new DialogCallbackListener(){
						@Override
						public void doPositiveClick(Object object){
							HyjActivity.super.onBackPressed();
						}
					});
				} else {
					HyjActivity.super.onBackPressed();
				}
			}
		}
	}

	public void setChoiceMode(int choiceMode) {
		mChoiceMode  = choiceMode;
	}
	
	public int getChoiceMode(){
		return mChoiceMode;
	}

	

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//      return gestureScanner.onTouchEvent(event);
////         return super.onTouchEvent(event);
//	}
//	
//	
//	private final class GestureListener extends SimpleOnGestureListener {
//
//        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
//        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
//		public boolean isSwipingLeftRight;
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            float distanceX = e2.getX() - e1.getX();
//            float distanceY = e2.getY() - e1.getY();
//            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
////                if (distanceX > 0)
//                	isSwipingLeftRight = true;
////                else
////                    onSwipeLeft();
//            } else {
//            	isSwipingLeftRight = false;
//            }
//            return false;
//        }
//    }
}
