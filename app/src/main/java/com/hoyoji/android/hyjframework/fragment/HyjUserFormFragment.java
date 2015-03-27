package com.hoyoji.android.hyjframework.fragment;

import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import com.hoyoji.btcontrol.R;

public abstract class HyjUserFormFragment extends HyjUserFragment {
	private View mSaveButton = null;
	
	@Override
	public Integer useOptionsMenuView(){
		return R.menu.form_fragment;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.formFragment_action_save:
			onSave();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onInitViewData (){
			mSaveButton = getView().findViewById(R.id.button_save);
			if(mSaveButton != null){
				mSaveButton.setOnClickListener(
			        new OnClickListener() {
			            @Override
			            public void onClick(View v) {
			                onSave();
			            }
			        }
			    );
			}
	}
	
	public void onSave(){
		onSave(null);
	}
	
	public void onSave(View v){
		
	}
	
	public void hideSaveAction(){
		mSaveButton.setVisibility(View.GONE);
		getOptionsMenu().findItem(R.id.formFragment_action_save).setVisible(false);
	}
	
	public void setSaveActionEnable(boolean enable){
		mSaveButton.setEnabled(enable);
		getOptionsMenu().findItem(R.id.formFragment_action_save).setEnabled(enable);
	}
}
