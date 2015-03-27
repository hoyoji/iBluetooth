package com.hoyoji.hoyoji.project;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.project.SubProjectListFragment.OnSelectSubProjectsListener;

public class ProjectListFragment extends HyjUserFragment implements OnSelectSubProjectsListener, OnPageChangeListener{
	
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public ViewPager mViewPager;

	private View mPageStrip;
	
	
	@Override
	public Integer useContentView() {
		return R.layout.project_listfragment_project;
	}
	
	@Override
	public void onInitViewData() {
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) getView().findViewById(R.id.projectListFragment_pager);
		mPageStrip = getView().findViewById(R.id.projectListFragment_pager_title_strip);
		//.setBackgroundColor(Color.LTGRAY);
//		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(100);
		SubProjectListFragment firstFragment = SubProjectListFragment.newInstance(getActivity().getIntent().getStringExtra("parentProjectId"), null);
		firstFragment.setOnSelectSubProjectsListener(this);
		mSectionsPagerAdapter.addPage(firstFragment);
		mSectionsPagerAdapter.notifyDataSetChanged();
		//mViewPager.setCurrentItem(0);
		//firstFragment.setUserVisibleHint(this.getUserVisibleHint());
	}

	@Override
	public Integer useOptionsMenuView() {
//		return R.menu.project_listfragment_project;
		return null;
	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if(item.getItemId() == R.id.projectListFragment_action_project_addnew){
//			openActivityWithFragment(ProjectFormFragment.class, R.string.projectFormFragment_title_addnew, null);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	
	@Override
	public void onSelectSubProjectsListener(final String parentProjectId, final String title) {
		final SubProjectListFragment nextFragment = SubProjectListFragment.newInstance(parentProjectId, title);
		nextFragment.setOnSelectSubProjectsListener(this);
		mSectionsPagerAdapter.addPage(nextFragment);
		mSectionsPagerAdapter.notifyDataSetChanged();
		mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1, true);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		for(int i = mSectionsPagerAdapter.getCount()-1; i > mViewPager.getCurrentItem(); i--){
			mSectionsPagerAdapter.removePageAt(i);
		}
		mSectionsPagerAdapter.notifyDataSetChanged();
		mPageStrip.postDelayed(new Runnable(){
			@Override
			public void run() {
				if(mSectionsPagerAdapter.getCount() <= 1){
					Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
					fadeInAnimation.setFillAfter(true);
					mPageStrip.startAnimation(fadeInAnimation);
					mPageStrip.setVisibility(View.GONE);
				} else {
					mPageStrip.setVisibility(View.VISIBLE);
					Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
					fadeOutAnimation.setFillBefore(true);
					fadeOutAnimation.setFillAfter(true);
					mPageStrip.startAnimation(fadeOutAnimation);
				}
			}
		}, 100);
		
	}
	
	@Override
	public boolean handleBackPressed() {
		boolean backPressedHandled = false; //super.handleBackPressed();
		if(mViewPager != null && mViewPager.getCurrentItem() > 0){
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
			backPressedHandled = true;
		}
		return backPressedHandled;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	    super.setUserVisibleHint(isVisibleToUser);
	    
	    if(mViewPager != null && mViewPager.getCurrentItem() >= 0 && mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()) != null){
	    	mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem()).setUserVisibleHint(isVisibleToUser);
	    }
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public static class SectionsPagerAdapter extends FragmentPagerAdapter {

		private List<SubProjectListFragment> pages;
		private final FragmentManager mFragmentManager;
		private FragmentTransaction mCurTransaction = null;


		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentManager = fm;
			pages = new ArrayList<SubProjectListFragment>();
		}

		public void addPage(SubProjectListFragment fragment) {
			pages.add(fragment);
		}

		public void removePageAt(int position) {
			pages.remove(position);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			return pages.get(position);
		}

		@Override
		public int getCount() {
			return pages.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return pages.get(position).getTitle();
		}

		// -----------------------------------------------------------------------------
		@Override
		public int getItemPosition(Object object) {
			int index = pages.indexOf(object);
			if (index == -1)
				return POSITION_NONE;
			else
				return index;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}

			// Do we already have this fragment?
//			String name = makeFragmentName(container.getId(), position);
//			Fragment fragment = mFragmentManager.findFragmentByTag(name);
//			if (fragment != null) {
//				if (DEBUG)
//					Log.v(TAG, "Attaching item #" + position + ": f="
//							+ fragment);
//				mCurTransaction.attach(fragment);
//			} else {
				Fragment fragment = getItem(position);
				mCurTransaction.add(container.getId(), fragment,
						makeFragmentName(container.getId(), position));
//			}
			return fragment;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if (mCurTransaction == null) {
				mCurTransaction = mFragmentManager.beginTransaction();
			}
			mCurTransaction.remove((Fragment) object);
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			if (mCurTransaction != null) {
				mCurTransaction.commit();
				mCurTransaction = null;
				mFragmentManager.executePendingTransactions();
			}
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return ((Fragment) object).getView() == view;
		}

		private String makeFragmentName(int viewId, int index) {
			return "android:switcher:"
					+ viewId
					+ ":"
					+ getItem(index).getArguments()
							.getString("parentProjectId");
		}

//		@Override
//		public void setPrimaryItem(ViewGroup container, int position,
//				Object object) {
//			super.setPrimaryItem(container, position, object);
//			if(object != null && getCount() == 1){
//				((Fragment)object).getParentFragment().getUserVisibleHint();
//				((Fragment)object).setUserVisibleHint(false);
//			}
//		}

	}
//	
//	public class DepthPageTransformer implements ViewPager.PageTransformer {
//	    private static final float MIN_SCALE = 0.75f;
//
//	    public void transformPage(View view, float position) {
//	        int pageWidth = view.getWidth();
//
//	        if (position < -1) { // [-Infinity,-1)
//	            // This page is way off-screen to the left.
//	            view.setAlpha(0);
//
//	        } else if (position <= 0) { // [-1,0]
//	            // Use the default slide transition when moving to the left page
//	            view.setAlpha(1);
//	            view.setTranslationX(0);
//	            view.setScaleX(1);
//	            view.setScaleY(1);
//
//	        } else if (position <= 1) { // (0,1]
//	            // Fade the page out.
//	            view.setAlpha(1 - position);
//
//	            // Counteract the default slide transition
//	            view.setTranslationX(pageWidth * -position);
//
//	            // Scale the page down (between MIN_SCALE and 1)
//	            float scaleFactor = MIN_SCALE
//	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
//	            view.setScaleX(scaleFactor);
//	            view.setScaleY(scaleFactor);
//
//	        } else { // (1,+Infinity]
//	            // This page is way off-screen to the right.
//	            view.setAlpha(0);
//	        }
//	    }
//	}
}
