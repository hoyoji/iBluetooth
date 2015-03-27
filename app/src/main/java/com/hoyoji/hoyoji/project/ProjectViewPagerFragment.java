package com.hoyoji.hoyoji.project;

import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.android.hyjframework.view.HyjTabStrip;
import com.hoyoji.android.hyjframework.view.HyjViewPager;
import com.hoyoji.android.hyjframework.view.HyjTabStrip.OnTabSelectedListener;
import com.hoyoji.android.hyjframework.view.HyjViewPager.OnOverScrollListener;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.event.EventListFragment;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.money.MoneySearchListFragment;

public class ProjectViewPagerFragment extends HyjUserFragment {
	
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public ViewPager mViewPager;

	protected boolean isClosingActivity = false;

	private HyjTabStrip mTabStrip;

	private DisplayMetrics mDisplayMetrics;

	private ChangeObserver mChangeObserver;

	
	@Override
	public Integer useContentView() {
		return R.layout.project_viewpager_tabstrip;
	}
	
	@Override
	public void onInitViewData() {
		mDisplayMetrics = getResources().getDisplayMetrics();
		
		setupProjectDetail();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
		//.setBackgroundColor(Color.LTGRAY);
//		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
		((HyjViewPager)mViewPager).setOnOverScrollListener(new OnOverScrollListener() {
			@Override
			public void onOverScroll(float mOverscroll) {
//				Log.i("mOverscroll", "" + mOverscroll);
				if(mOverscroll / mDisplayMetrics.density < -150){
					if(!isClosingActivity ){
						isClosingActivity = true;
						((HyjViewPager)mViewPager).setStopBounceBack(true);
						getActivity().finish();
					}
				}
			}
		});
		
		mTabStrip = (HyjTabStrip) getView().findViewById(R.id.tabstrip);
		mTabStrip.initTabLine(mSectionsPagerAdapter.getCount());
		for(int i = 0; i < mSectionsPagerAdapter.getCount(); i ++){
			CharSequence title = mSectionsPagerAdapter.getPageTitle(i);
			mTabStrip.addTab(title.toString());
		}
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position) {
				((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("圈子"+mSectionsPagerAdapter.getPageTitle(position));
				mTabStrip.setTabSelected(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				mTabStrip.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		mTabStrip.setOnTabSelectedListener(new OnTabSelectedListener(){
			@Override
			public void onTabSelected(int tag) {
				mViewPager.setCurrentItem(tag);
			}
		});
		mViewPager.setCurrentItem(1);
		

		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(
					ContentProvider.createUri(
							Project.class, null), true,
							mChangeObserver);
		}
	}
	
	public void setupProjectDetail(){
			ViewGroup projectDetailView = (ViewGroup) getView().findViewById(R.id.project_viewpager_projectdetail);
	
			final Long modelId = getActivity().getIntent().getLongExtra("MODEL_ID", -1);
			
			Project project = HyjModel.load(Project.class, modelId);
			View view = projectDetailView.findViewById(R.id.projectListItem_name);
			SubProjectListFragment.setProjectViewValue(this, view, project, "projectListItem_name", null, null);

			view = projectDetailView.findViewById(R.id.projectListItem_owner);
			SubProjectListFragment.setProjectViewValue(this, view, project, "projectListItem_owner", null, null);

			view = projectDetailView.findViewById(R.id.projectListItem_depositTotal);
			SubProjectListFragment.setProjectViewValue(this, view, project, "projectListItem_depositTotal", null, null);

			view = projectDetailView.findViewById(R.id.projectListItem_picture);
			SubProjectListFragment.setProjectViewValue(this, view, project, "projectListItem_picture", null, new OnClickListener(){
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putLong("MODEL_ID", modelId);
					openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_edit, bundle);
				}
			});
			
			view = projectDetailView.findViewById(R.id.projectListItem_action_viewSubProjects);
			SubProjectListFragment.setProjectViewValue(this, view, project, "projectListItem_action_viewSubProjects", new OnClickListener(){
				@Override
				public void onClick(View v) {
					String parentProjectId = v.getTag().toString();
					Bundle bundle = new Bundle();
					bundle.putString("parentProjectId", parentProjectId);
					openActivityWithFragment(ProjectListFragment.class, R.string.projectListFragment_title_subprojects, bundle);
				}
			}, null);
			
			projectDetailView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putLong("MODEL_ID", modelId);
					openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_edit, bundle);
				}
			});
	}
	
	
	
//	@Override
//	public boolean handleBackPressed() {
//		boolean backPressedHandled = false; //super.handleBackPressed();
////		if(mViewPager.getCurrentItem() > 0){
////			mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
////			backPressedHandled = true;
////		}
//		return backPressedHandled;
//	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public static class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0 :
				return new EventListFragment();
			case 1 :
				return new MoneySearchListFragment();
			case 2:
				return new ProjectMemberListFragment();
//			case 3:
//				return new ProjectFormFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch(position){
			case 0 :
				return "活动";
			case 1 :
				return "流水";
			case 2:
				return "成员";
//			case 3:
//				return "资料";
			}
			return null;
		}
	}

	
	private class ChangeObserver extends ContentObserver {
		AsyncTask<String, Void, String> mTask = null;
		public ChangeObserver() {
			super(new Handler());
		}
	
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}
	
	//	@Override
	//	public void onChange(boolean selfChange, Uri uri) {
	//		super.onChange(selfChange, uri);
	//	}
	
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if(mTask == null){
				mTask = new AsyncTask<String, Void, String>() {
			        @Override
			        protected String doInBackground(String... params) {
						try {
							//等待其他的更新都到齐后再更新界面
							Thread.sleep(200);
						} catch (InterruptedException e) {}
						return null;
			        }
			        @Override
			        protected void onPostExecute(String result) {
			        	setupProjectDetail();
	
	//			    	getLoaderManager().restartLoader(0, new Bundle(), SubProjectListFragment.this);
						mTask = null;
			        }
			    };
			    mTask.execute();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		if (mChangeObserver != null) {
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mChangeObserver);
		}
		
		super.onDestroy();
	}

}
