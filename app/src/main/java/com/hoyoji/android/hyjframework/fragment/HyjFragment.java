package com.hoyoji.android.hyjframework.fragment;

import java.util.List;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.activity.HyjBlankActivity;
import com.hoyoji.android.hyjframework.activity.HyjBlankUserActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class HyjFragment extends Fragment {

	public abstract Integer useContentView();
	private boolean mIsViewInited = false;
	protected Menu mOptionsMenu;
	
	public Integer useToolbarView(){
		return null;
	}
	
	public Integer useOptionsMenuView(){
		return null;
	}
	
	public void onInitViewData(){
		
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(useOptionsMenuView() != null){
			setHasOptionsMenu (true);
		}
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		if(useContentView() != null){
			View rootView = inflater.inflate(useContentView(), container, false);
			if(useToolbarView() != null){
				// populate bottom toolbar
				
			}
			return rootView;
		} else {
			return null;
		}
	}

	@Override
	public void onStart(){
		super.onStart();
		if(!this.mIsViewInited){
			onInitViewData();
			this.mIsViewInited = true;
		}
	}
	
	public void onStartWithoutInitViewData(){
		super.onStart();
	}

	public Menu getOptionsMenu(){
		return mOptionsMenu;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mOptionsMenu = menu;
	    // Inflate the menu items for use in the action bar
		if(useOptionsMenuView() != null){
			inflater.inflate(useOptionsMenuView(), menu);
		}
	    super.onCreateOptionsMenu(menu, inflater);
	}

	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, false, null);
	}
	
	public void openActivityWithFragmentForResult(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle, int requestCode){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, true, requestCode);
	}
	
	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this.getActivity(), HyjBlankUserActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			if(getParentFragment() != null){
				getParentFragment().startActivityForResult(intent, requestCode);
			} else {
				startActivityForResult(intent, requestCode);
			}
		} else {
			if(getParentFragment() != null){
				getParentFragment().startActivity(intent);
			} else {
				startActivity(intent);
			}
		}
	}
	
	public void openBlankActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this.getActivity(), HyjBlankActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			if(getParentFragment() != null){
				getParentFragment().startActivityForResult(intent, requestCode);
			} else {
				startActivityForResult(intent, requestCode);
			}
		} else {
			if(getParentFragment() != null){
				getParentFragment().startActivity(intent);
			} else {
				startActivity(intent);
			}
		}
	}
	
	public boolean handleBackPressed() {
		boolean backPressedHandled = false;
		if(getChildFragmentManager().getFragments() != null){
			for(Fragment f : this.getChildFragmentManager().getFragments()){
				if(f instanceof HyjFragment){
					backPressedHandled = backPressedHandled || ((HyjFragment)f).handleBackPressed();
				} else if(f instanceof HyjUserListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserListFragment)f).handleBackPressed();
				} else if(f instanceof HyjUserExpandableListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserExpandableListFragment)f).handleBackPressed();
				} 
			}
		}
		return backPressedHandled;
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
            	if(fragment.getUserVisibleHint()){
            		fragment.onActivityResult(requestCode, resultCode, data);
            	}
            }
        }
    }
}
