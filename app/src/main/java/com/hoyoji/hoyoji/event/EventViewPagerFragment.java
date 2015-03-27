package com.hoyoji.hoyoji.event;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.Button;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjTabStrip;
import com.hoyoji.android.hyjframework.view.HyjViewPager;
import com.hoyoji.android.hyjframework.view.HyjTabStrip.OnTabSelectedListener;
import com.hoyoji.android.hyjframework.view.HyjViewPager.OnOverScrollListener;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.money.MoneySearchListFragment;

public class EventViewPagerFragment extends HyjUserFragment {
	
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public ViewPager mViewPager;

	protected boolean isClosingActivity = false;

	private HyjTabStrip mTabStrip;

	private DisplayMetrics mDisplayMetrics;

	private Button mBtnSignUpEvent;

	private ViewGroup mEventDetailView;

	private ContentObserver mChangeObserver = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.event_viewpager_tabstrip;
	}
	
	@Override
	public void onInitViewData() {
		mDisplayMetrics = getResources().getDisplayMetrics();

		mEventDetailView = (ViewGroup) getView().findViewById(R.id.event_viewpager_eventdetail);
		setupEventDetail();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) getView().findViewById(R.id.viewpager);
		//.setBackgroundColor(Color.LTGRAY);
//		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		((HyjViewPager)mViewPager).setOnOverScrollListener(new OnOverScrollListener(){
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
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("活动"+mSectionsPagerAdapter.getPageTitle(position));
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
		
		mBtnSignUpEvent = (Button)getView().findViewById(R.id.eventviewpager_signup_event);
		setupSignUp();

		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(
					ContentProvider.createUri(
							Event.class, null), true,
							mChangeObserver);
			this.getActivity().getContentResolver().registerContentObserver(
					ContentProvider.createUri(
							EventMember.class, null), true,
							mChangeObserver);
		}
	}
	

	private void setupSignUp() {
		String subTitle = null;
		long model_id = this.getActivity().getIntent().getLongExtra("MODEL_ID", -1);
		if(model_id != -1){
			final Event event = HyjModel.load(Event.class, model_id);
			if(event != null){
				if(!"Cancel".equals(event.getState())){
					subTitle = event.getName();
					
					final EventMember eventMember = new Select().from(EventMember.class).where("eventId = ? AND friendUserId = ?", event.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
					if(eventMember == null || eventMember.getState().equalsIgnoreCase("UnSignUp") || eventMember.getState().equalsIgnoreCase("CancelSignUp")){
						mBtnSignUpEvent.setVisibility(View.VISIBLE);
						mBtnSignUpEvent.setText("报名");
						mBtnSignUpEvent.setOnClickListener(new OnClickListener(){
							@Override
							public void onClick(View v) {
//								if(event.getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//									eventMember.setState("SignUp");
//									eventMember.save();
////									mBtnSignUpEvent.setVisibility(View.GONE);
//									setupSignIn(eventMember, event);
//									setupEventDetail();
////									mViewPager.setPadding(mTabStrip.getPaddingLeft(), (int) (35*mDisplayMetrics.density), mViewPager.getPaddingRight(), mViewPager.getPaddingBottom());
//									HyjUtil.displayToast("报名成功");
//								} else {
//								}
									sendSignUpMessageToServer(event, eventMember);
								}
						});
//						mViewPager.setPadding(mTabStrip.getPaddingLeft(), (int) (103*mDisplayMetrics.density), mViewPager.getPaddingRight(), mViewPager.getPaddingBottom());
					} else {
						setupSignIn(eventMember, event);
					}
					
					if(event.getStartDate() < (new Date()).getTime()){
						if (!event.getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
							if (eventMember != null && eventMember.getState().equalsIgnoreCase("SignUp")){
								sendUnSignInMessageToServer(event, eventMember);
							}
						} else {
							List<EventMember> ems = new Select().from(EventMember.class).where("eventId = ? AND state = ? and toBeDetermined = 0", event.getId(), "SignUp").execute();
							for(int i = 0; i < ems.size(); i++){
								EventMember em = ems.get(i);
								if(em != null){
									em.setState("UnSignIn");
									em.save();
								}
							}
						}
					}
					
				}
				if(subTitle != null){
					((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(subTitle);
				}
			}
		}
	}

	private void setupSignIn(final EventMember eventMember, final Event event) {
		if(eventMember != null 
				&& (eventMember.getState().equalsIgnoreCase("SignUp") || eventMember.getState().equalsIgnoreCase("UnSignIn"))
				&& event.getStartDate() < (new Date()).getTime()){
			mBtnSignUpEvent.setVisibility(View.VISIBLE);
			mBtnSignUpEvent.setText("签到");
			
			mBtnSignUpEvent.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
//					if(event.getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//						eventMember.setState("SignIn");
//						eventMember.save();
//						mBtnSignUpEvent.setVisibility(View.GONE);
////						setupEventDetail();
////						mViewPager.setPadding(mTabStrip.getPaddingLeft(), (int) (35*mDisplayMetrics.density), mViewPager.getPaddingRight(), mViewPager.getPaddingBottom());
//						HyjUtil.displayToast("签到成功");
//					} else {
//						ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId = ? AND state <> ?", HyjApplication.getInstance().getCurrentUser().getId(), "Delete").executeSingle();
						
						sendSignInMessageToServer(event, eventMember);
						
//					}
				}
			});
//			mViewPager.setPadding(mTabStrip.getPaddingLeft(), (int) (103*mDisplayMetrics.density), mViewPager.getPaddingRight(), mViewPager.getPaddingBottom());
		} else {
			mBtnSignUpEvent.setVisibility(View.GONE);
		}
	}

	public void setupEventDetail(){
	
			final Long modelId = getActivity().getIntent().getLongExtra("MODEL_ID", -1);
			Event event = HyjModel.load(Event.class, modelId);

			View view = mEventDetailView.findViewById(R.id.homeListItem_title);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_title");

			view = mEventDetailView.findViewById(R.id.homeListItem_remark);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_remark");
			
			view = mEventDetailView.findViewById(R.id.homeListItem_owner);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_owner");

			view = mEventDetailView.findViewById(R.id.homeListItem_amount);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_amount");

			view = mEventDetailView.findViewById(R.id.homeListItem_picture);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_picture");
			
			view = mEventDetailView.findViewById(R.id.homeListItem_date);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_date");

			view = mEventDetailView.findViewById(R.id.homeListItem_subTitle);
			EventListFragment.setEventViewValue(this, view, event, "homeListItem_subTitle");
			
			mEventDetailView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putLong("MODEL_ID", modelId);
					openActivityWithFragment(EventFormFragment.class, R.string.projectEventFormFragment_action_edit, bundle);
				}
			});
	}
	
	
	private void sendSignUpMessageToServer(final Event event, EventMember em) {
		try {
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					loadEventMembers(object);
				}
	
				@Override
				public void errorCallback(Object object) {
					((HyjActivity) EventViewPagerFragment.this.getActivity()).dismissProgressDialog();
					JSONObject json = (JSONObject) object;
					HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
				}
			};
			JSONObject evt = new JSONObject();
			evt.put("eventId", event.getId());
	
//			JSONObject msg = new JSONObject();
//			msg.put("__dataType", "Message");
//			
//			msg.put("toUserId", event.getOwnerUserId());
//			msg.put("fromUserId", HyjApplication.getInstance().getCurrentUser().getId());
//			msg.put("type", "Event.Member.SignUp");
//			msg.put("messageState", "new");
//			msg.put("messageTitle", "活动报名");
//			msg.put("date", (new Date()).getTime());
//			msg.put("detail", "用户"+ HyjApplication.getInstance().getCurrentUser().getDisplayName() + "报名参加活动: "+ event.getName());
//			msg.put("messageBoxId", event.getOwnerUser().getMessageBoxId1());
//			msg.put("ownerUserId", event.getOwnerUserId());
//	
//			JSONObject msgData = new JSONObject();
//			msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
//			msgData.put("projectIds", new JSONArray("[" + event.getProjectId()  + "]"));
//			msgData.put("eventId", event.getId());
//			if(em == null){
//				msgData.put("eventMemberId", null);
//			} else {
//				msgData.put("eventMemberId", em.getId());
//			}
//			msg.put("messageData", msgData.toString());
//			
//			if(!event.getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//				msg.put("id", UUID.randomUUID().toString());
//			}
	
//			HyjHttpPostAsyncTask.newInstance(serverCallbacks,"[{eventId:'" + event.getId() + "'}]", "eventMemberSignUp");
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,"[" + evt.toString() + "]", "eventMemberSignUp");
			
			((HyjActivity) this.getActivity()).displayProgressDialog(
							R.string.eventListFragment_unSignIn_request,
							R.string.eventListFragment_unSignIn_progress_request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendSignInMessageToServer(final Event event, EventMember em) {
		try {
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					loadEventMembers(object);
				}
	
				@Override
				public void errorCallback(Object object) {
					((HyjActivity) EventViewPagerFragment.this.getActivity()).dismissProgressDialog();
					JSONObject json = (JSONObject) object;
					HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
				}
			};
			
			JSONObject evt = new JSONObject();
			evt.put("eventId", event.getId());
	
//			JSONObject msg = new JSONObject();
//			msg.put("__dataType", "Message");
//			msg.put("toUserId", event.getOwnerUserId());
//			msg.put("fromUserId", HyjApplication.getInstance().getCurrentUser().getId());
//			msg.put("type", "Event.Member.SignIn");
//			msg.put("messageState", "new");
//			msg.put("messageTitle", "活动签到");
//			msg.put("date", (new Date()).getTime());
//			msg.put("detail", "用户"+ HyjApplication.getInstance().getCurrentUser().getDisplayName() + "签到了活动: "+ event.getName());
//			msg.put("messageBoxId", event.getOwnerUser().getMessageBoxId1());
//			msg.put("ownerUserId", event.getOwnerUserId());
//	
//			JSONObject msgData = new JSONObject();
//			msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
//			msgData.put("projectIds", new JSONArray("[" + event.getProjectId()  + "]"));
//			msgData.put("eventId", event.getId());
//			if(em == null){
//				msgData.put("eventMemberId", null);
//			} else {
//				msgData.put("eventMemberId", em.getId());
//			}
//			msg.put("messageData", msgData.toString());
//			
//			if(!event.getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//				msg.put("id", UUID.randomUUID().toString());
//			}
//			HyjHttpPostAsyncTask.newInstance(serverCallbacks,"[{eventId:\"" + event.getId() + "\"}]", "eventMemberSignIn");
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,"[" + evt.toString() + "]", "eventMemberSignIn");
			((HyjActivity) this.getActivity()).displayProgressDialog(
							R.string.eventListFragment_signIn_request,
							R.string.eventListFragment_signIn_progress_request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendUnSignInMessageToServer(final Event event, EventMember em) {
		try {
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					loadEventMembers(object);
				}
	
				@Override
				public void errorCallback(Object object) {
//					((HyjActivity) EventViewPagerFragment.this.getActivity()).dismissProgressDialog();
					JSONObject json = (JSONObject) object;
					HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
				}
			};
			
			JSONObject evt = new JSONObject();
			evt.put("eventId", event.getId());
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,"[" + evt.toString() + "]", "eventMemberUnSignIn");
//			((HyjActivity) this.getActivity()).displayProgressDialog(
//							R.string.eventListFragment_signIn_request,
//							R.string.eventListFragment_signIn_progress_request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	protected void loadEventMembers(Object object) {
		try {
			JSONArray jsonObjects = (JSONArray) object;
			ActiveAndroid.beginTransaction();
				for (int j = 0; j < jsonObjects.length(); j++) {
					if (jsonObjects.optJSONObject(j).optString("__dataType").equals("EventMember")) {
						String id = jsonObjects.optJSONObject(j).optString("id");
						EventMember newEventMember = HyjModel.getModel(EventMember.class, id);
						if(newEventMember == null){
							newEventMember = new EventMember();
						}
						newEventMember.loadFromJSON(jsonObjects.optJSONObject(j), true);
						newEventMember.save();
					} else if (jsonObjects.optJSONObject(j).optString("__dataType").equals("Event")) {
						String id = jsonObjects.optJSONObject(j).optString("id");
						Event newEvent = HyjModel.getModel(Event.class, id);
						if(newEvent == null){
							newEvent = new Event();
						}
						newEvent.loadFromJSON(jsonObjects.optJSONObject(j), true);
						newEvent.save();
					}
				}

			ActiveAndroid.setTransactionSuccessful();
			mBtnSignUpEvent.setVisibility(View.GONE);
		} finally {
			ActiveAndroid.endTransaction();
		}
		((HyjActivity) EventViewPagerFragment.this.getActivity()).dismissProgressDialog();
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
				return new EventMemberListFragment();
			case 1 :
				return new MoneySearchListFragment();
//			case 2:
//				return new EventFormFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch(position){
			case 0 :
				return "成员";
			case 1 :
				return "流水";
//			case 2:
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
			        	setupEventDetail();
			        	setupSignUp();
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
