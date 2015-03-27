package com.hoyoji.android.hyjframework.activity;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjUserActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;

public class HyjBlankUserActivity extends HyjUserActivity {

	@Override
	protected Integer getContentView() {
		//return R.layout.activity_blank_user;
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String title = intent.getStringExtra("TITLE");
		this.getSupportActionBar().setTitle(title);
		
	    if(getSupportFragmentManager().getFragments() == null){
			String fragmentClassName = getIntent().getStringExtra("FRAGMENT_NAME");
			Class<? extends Fragment> fragmentClass = HyjApplication.getInstance().getFragmentClassMap(fragmentClassName);
			addFragment(fragmentClass);
	    }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		HyjUtil.detectMemoryLeak(this);
	}

}
