package com.hoyoji.android.hyjframework.fragment;

import com.hoyoji.android.hyjframework.HyjApplication;

public abstract class HyjUserFragment extends HyjFragment {

	@Override
	public void onStart() {
		if(HyjApplication.getInstance().isLoggedIn()) {
			super.onStart();
		} else {
			super.onStartWithoutInitViewData();
		}
	}
	
}
