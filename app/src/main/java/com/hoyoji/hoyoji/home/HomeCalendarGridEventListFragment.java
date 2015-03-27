package com.hoyoji.hoyoji.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjAsyncTask;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.view.HyjCalendarGrid;
import com.hoyoji.android.hyjframework.view.HyjCalendarGridAdapter;
import com.hoyoji.android.hyjframework.view.HyjDateTimeView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.event.EventListFragment;
import com.hoyoji.hoyoji.event.EventViewPagerFragment;
import com.hoyoji.hoyoji.message.EventMessageFormFragment;
import com.hoyoji.hoyoji.message.FriendMessageFormFragment;
import com.hoyoji.hoyoji.message.MessageListFragment;
import com.hoyoji.hoyoji.message.MoneyShareMessageFormFragment;
import com.hoyoji.hoyoji.message.ProjectMessageFormFragment;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.UserData;
import com.jauker.widget.BadgeView;

public class HomeCalendarGridEventListFragment extends HyjUserListFragment {
	private List<Map<String, Object>> mListGroupData = new ArrayList<Map<String, Object>>();
	private ContentObserver mChangeObserver = null;
	private ContentObserver mMessageChangeObserver;

//	private TextView mCurrentMonth;
//	private TextView mCurrentYear;
//	private TextView mSelectedDay;
	private RelativeLayout mNearestEventLayout;
	private Event mNearestEvent;

	private HyjCalendarGrid mCalendarGridView;
//	private Button mDepositExpenseButton;
//	private Button mDepositIncomeButton;
	private TextView mTextViewLatestNewMessage;
	private FrameLayout mLayoutActionMessage;
	
	@Override
	public Integer useContentView() {
		return R.layout.home_listfragment_event_calendargrid;
	}

	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}

	@Override
	public Integer useOptionsMenuView() {
//		return R.menu.project_listfragment_event;
		return null;
	}

	@Override
	protected CharSequence getNoContentText() {
		return "无活动";
	}
	
	@Override
	protected View useHeaderView(Bundle savedInstanceState){
		LinearLayout view =  (LinearLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.home_calendargrid_event_header, null);
		mCalendarGridView = (HyjCalendarGrid) view.findViewById(R.id.home_calendar_grid);
		mCalendarGridView.setAdapter(new HomeCalendarGridEventAdapter(getActivity(), getResources()));
		mCalendarGridView.getAdapter().setData(mListGroupData);
//		mCurrentMonth = (TextView) view.findViewById(R.id.home_stat_month);
//		mCurrentYear = (TextView) view.findViewById(R.id.home_stat_year);
//		mSelectedDay = (TextView) view.findViewById(R.id.home_stat_day);

//		mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
//		mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"年");
//		mSelectedDay.setText(mCalendarGridView.getAdapter().getSelectedDay()+"日及往后的活动");
		
		mCalendarGridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int year = mCalendarGridView.getAdapter().getYearAtPosition(arg2);
				int month = mCalendarGridView.getAdapter().getMonthAtPosition(arg2);
				int day = mCalendarGridView.getAdapter().getDayAtPosition(arg2);
				mCalendarGridView.getAdapter().setSelectedYear(year);
				mCalendarGridView.getAdapter().setSelectedMonth(month);
				mCalendarGridView.getAdapter().setSelectedDay(day);
				mCalendarGridView.getAdapter().notifyDataSetChanged();
//				if(isBeforeToday(year, month, day)){
//					mSelectedDay.setText(mCalendarGridView.getAdapter().getSelectedDay()+"日及以前的活动");
//				} else {
//					mSelectedDay.setText(mCalendarGridView.getAdapter().getSelectedDay()+"日及往后的活动");
//				}
				
				getLoaderManager().restartLoader(0, null, HomeCalendarGridEventListFragment.this);
				
			}
		});
		
		view.findViewById(R.id.home_calendar_control_today).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Calendar today = Calendar.getInstance();
				
				int year = today.get(Calendar.YEAR);
		    	int monthOfYear = today.get(Calendar.MONTH);
		    	int dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
		    	if(monthOfYear + 1 != mCalendarGridView.getAdapter().getCurrentMonth() 
		    			|| mCalendarGridView.getAdapter().getCurrentYear() != year
		    			|| mCalendarGridView.getAdapter().getSelectedDay() != dayOfMonth){
					
					mCalendarGridView.getAdapter().setSelectedDay(dayOfMonth);
					mCalendarGridView.getAdapter().setCalendar(year, monthOfYear+1);
					
//					mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
//					mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"年");
//					mSelectedDay.setText(mCalendarGridView.getAdapter().getSelectedDay()+"日及往后的活动");
					
					mListGroupData.clear();
//					updateHeaderStat();
//					updateGroupHeader();
					mCalendarGridView.getAdapter().notifyDataSetChanged();
					getLoaderManager().restartLoader(-1, null, HomeCalendarGridEventListFragment.this);
		    	}
			}
		});
		final View calendarControl = view.findViewById(R.id.home_calendar_control);
		view.findViewById(R.id.home_stat_group_calendarMode).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mCalendarGridView.getAdapter().getCalendarMode() == HyjCalendarGridAdapter.CALENDAR_MODE_MONTH){
					mCalendarGridView.getAdapter().setCalendarMode(HyjCalendarGridAdapter.CALENDAR_MODE_WEEK);
					calendarControl.setVisibility(View.GONE);
				} else {
					mCalendarGridView.getAdapter().setCalendarMode(HyjCalendarGridAdapter.CALENDAR_MODE_MONTH);
					calendarControl.setVisibility(View.VISIBLE);
				}
				mCalendarGridView.getAdapter().getDayNumber();

				mListGroupData.clear();
				mCalendarGridView.getAdapter().notifyDataSetChanged();
				getLoaderManager().restartLoader(-1, null, HomeCalendarGridEventListFragment.this);
			}
		});
		view.findViewById(R.id.home_calendar_control_previous_month).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				mCalendarGridView.getAdapter().setSelectedYear(mCalendarGridView.getAdapter().getCurrentYear());
//				mCalendarGridView.getAdapter().setSelectedMonth(mCalendarGridView.getAdapter().getCurrentMonth());
				mCalendarGridView.getAdapter().setJumpCalendar(-1, 0);

//				mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
//				mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"年");
//				mSelectedDay.setText(mCalendarGridView.getAdapter().getSelectedDay()+"日及往后的活动");
				
				mListGroupData.clear();
				mCalendarGridView.getAdapter().notifyDataSetChanged();
				getLoaderManager().restartLoader(-1, null, HomeCalendarGridEventListFragment.this);
			}
		});
		view.findViewById(R.id.home_calendar_control_next_month).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				mCalendarGridView.getAdapter().setSelectedYear(mCalendarGridView.getAdapter().getCurrentYear());
//				mCalendarGridView.getAdapter().setSelectedMonth(mCalendarGridView.getAdapter().getCurrentMonth());
				mCalendarGridView.getAdapter().setJumpCalendar(1, 0);
				
//				mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
//				mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"年");
//				mSelectedDay.setText(mCalendarGridView.getAdapter().getSelectedDay()+"日及往后的活动");
				
				mListGroupData.clear();
//				GStat();
//				updateGroupHeader();
				mCalendarGridView.getAdapter().notifyDataSetChanged();
				getLoaderManager().restartLoader(-1, null, HomeCalendarGridEventListFragment.this);
			}
		});
		mNearestEventLayout = (RelativeLayout) view.findViewById(R.id.home_listfragment_event_nearestevent);
		mNearestEventLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mNearestEvent != null){
					onListItemClick(getListView(), mNearestEventLayout, -1, mNearestEvent.get_mId());
				}
			}
		});

		mTextViewLatestNewMessage = (TextView)view.findViewById(R.id.homeListFragment_new_message);
		mLayoutActionMessage = (FrameLayout)view.findViewById(R.id.homeListFragment_action_message_layout);
		mTextViewLatestNewMessage.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mLatestNewMessage != null){
					onLatestModifiedModelClick();
				}
			}
		});

		view.findViewById(R.id.homeListFragment_action_message).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openActivityWithFragment(MessageListFragment.class, R.string.messageListFragment_title, null);
    		}
		});
		return view;
	}

//	private boolean isBeforeToday(int year, int month, int day){
//		Calendar calendar = Calendar.getInstance();
//		if(year < calendar.get(Calendar.YEAR)){
//			return true;
//		} else if(year > calendar.get(Calendar.YEAR)){
//			return false;
//		}
//		if(month < calendar.get(Calendar.MONTH)+1){
//			return true;
//		} else if(month > calendar.get(Calendar.MONTH)+1){
//			return false;
//		}
//		if(day < calendar.get(Calendar.DAY_OF_MONTH)){
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public void onResume(){
		super.onResume();
		Calendar calToday = Calendar.getInstance();
		if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSysYear() 
				|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSysMonth() - 1
				|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSysDay() ){
			mCalendarGridView.getAdapter().setSysYear(calToday.get(Calendar.YEAR));
			mCalendarGridView.getAdapter().setSysMonth(calToday.get(Calendar.MONTH)+1);
			mCalendarGridView.getAdapter().setSysDay(calToday.get(Calendar.DAY_OF_MONTH));

			mCalendarGridView.getAdapter().setSelectedYear(calToday.get(Calendar.YEAR));
			mCalendarGridView.getAdapter().setSelectedMonth(calToday.get(Calendar.MONTH)+1);
			mCalendarGridView.getAdapter().setSelectedDay(calToday.get(Calendar.DAY_OF_MONTH));
			
			initLoader(-1);
		}
	}
	
	@Override
	public void onInitViewData() {
		super.onInitViewData();

//		getView().findViewById(R.id.homelistfragment_event_addnew).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//
//				Bundle bundle = new Bundle();
//				Calendar calToday = Calendar.getInstance();
//				if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSelectedYear() 
//						|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSelectedMonth() - 1 
//						|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSelectedDay() ){
//					calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getSelectedYear());
//					calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getSelectedMonth()-1);
//					calToday.set(Calendar.DAY_OF_MONTH, mCalendarGridView.getAdapter().getSelectedDay());
//					
//					bundle.putLong("DATE_IN_MILLISEC", calToday.getTimeInMillis());
//				}
//				openActivityWithFragment(EventFormFragment.class, R.string.projectEventListFragment_action_addnew, bundle);
//			}
//		});
//		
//		mDepositExpenseButton = (Button)getView().findViewById(R.id.homeListFragment_event_action_money_deposit_expense);
//		mDepositIncomeButton = (Button)getView().findViewById(R.id.homeListFragment_event_action_money_deposit_income);
//		mDepositExpenseButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
//		mDepositIncomeButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
//		mDepositExpenseButton.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//
//				Bundle bundle = new Bundle();
//				Calendar calToday = Calendar.getInstance();
//				if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSelectedYear() 
//						|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSelectedMonth() - 1 
//						|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSelectedDay() ){
//					calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getSelectedYear());
//					calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getSelectedMonth()-1);
//					calToday.set(Calendar.DAY_OF_MONTH, mCalendarGridView.getAdapter().getSelectedDay());
//					
//					bundle.putLong("DATE_IN_MILLISEC", calToday.getTimeInMillis());
//				}
//				openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, bundle);
//			}
//		});
//		mDepositIncomeButton.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//
//				Bundle bundle = new Bundle();
//				Calendar calToday = Calendar.getInstance();
//				if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSelectedYear() 
//						|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSelectedMonth() - 1 
//						|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSelectedDay() ){
//					calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getSelectedYear());
//					calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getSelectedMonth()-1);
//					calToday.set(Calendar.DAY_OF_MONTH, mCalendarGridView.getAdapter().getSelectedDay());
//					
//					bundle.putLong("DATE_IN_MILLISEC", calToday.getTimeInMillis());
//				}
//				openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_addnew, bundle);
//			}
//		});
//		
//		updateNearestEvent();
		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
					mChangeObserver);
		}
		if (mMessageChangeObserver == null) {
			mMessageChangeObserver = new MessageChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(Message.class, null), true,
					mMessageChangeObserver);
		}
		
		// 加载日历
		initLoader(-1);
		updateLatestModifiedModel();
	}
	
	
	@Override
	public ListAdapter useListViewAdapter() {
		return new SimpleCursorAdapter(getActivity(),
				R.layout.home_listitem_row,
				null,
				new String[] {"_id", "id", "startDate", "name", "id", "ownerUserId" ,"id", "id"},
				new int[] {R.id.homeListItem_picture, R.id.homeListItem_owner, R.id.homeListItem_date, R.id.homeListItem_title, R.id.homeListItem_remark, R.id.homeListItem_subTitle, R.id.homeListItem_owner, R.id.homeListItem_amount},
				0); 
	}	
	

	@Override
	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
		super.onCreateLoader(groupPos, arg1);
		Object loader;
		if(arg1 == null){
			arg1 = new Bundle();
		}
		int offset = arg1.getInt("OFFSET");
		int limit = arg1.getInt("LIMIT");
		if(limit == 0){
			limit = getListPageSize();
		}

		if (groupPos < 0) { 
			long dateFrom = mCalendarGridView.getAdapter().getDateFrom();
			long dateTo = mCalendarGridView.getAdapter().getDateTo();
			
			arg1.putLong("startDateInMillis", dateFrom);
			arg1.putLong("endDateInMillis", dateTo);
			
			loader = new HomeCalendarGridGroupEventListLoader(getActivity(), arg1);
		} else {

			Calendar calToday = Calendar.getInstance();
			calToday.set(Calendar.HOUR_OF_DAY, 0);
			calToday.clear(Calendar.MINUTE);
			calToday.clear(Calendar.SECOND);
			calToday.clear(Calendar.MILLISECOND);
			int year = mCalendarGridView.getAdapter().getSelectedYear();
			int month = mCalendarGridView.getAdapter().getSelectedMonth()-1;
			int day = mCalendarGridView.getAdapter().getSelectedDay();
			calToday.set(Calendar.YEAR, year);
			calToday.set(Calendar.MONTH, month);
			calToday.set(Calendar.DATE, day);
//			arg1.putLong("dateFrom", calToday.getTimeInMillis());
//			arg1.putLong("dateTo", calToday.getTimeInMillis() + 24 * 3600000);
			long dateFrom = calToday.getTimeInMillis();
			long dateTo = dateFrom + 24 * 3600000;
			loader = new CursorLoader(getActivity(),
				ContentProvider.createUri(Event.class, null),
				null,
				"startDate > ?",
				new String[]{String.valueOf(dateFrom)}, 
				"startDate DESC LIMIT " + (limit + offset) 
			);
		}
		return (Loader<Object>)loader;
	}
	
	

	@Override
	public void onLoadFinished(Loader loader, Object list) {
		if (loader.getId() < 0) {
			
			ArrayList<Map<String, Object>> groupList = (ArrayList<Map<String, Object>>) list;
			mListGroupData.clear();
			mListGroupData.addAll(groupList);
			mCalendarGridView.getAdapter().notifyDataSetChanged();
			getLoaderManager().restartLoader(0, null, this);
		} else {
//			updateNearestEvent();
	        super.onLoadFinished(loader, list);
//			((CursorAdapter)getListAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		 if(loader.getId() < 0){
				this.mListGroupData.clear();
		 } else {
				super.onLoaderReset(loader);
		 }
	}

	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) { 
		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			return;
		}
		if(id == -1) {
			 return;
		}
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong("event_id", id);
			bundle.putLong("MODEL_ID", id);
			openActivityWithFragment(EventViewPagerFragment.class, R.string.projectEventMemberViewPagerFragment_title, bundle);
		}
    }
	
//	HyjAsyncTask mNearestEventLoader = null;
//	HyjAsyncTaskCallbacks mNearestEventLoaderCallback = new HyjAsyncTaskCallbacks(){
//		@Override
//		public void finishCallback(Object object) {
//			if(object != null){
//				Event event = (Event)object;
//				mNearestEvent = event;
//				
////				ImageView imageView= (ImageView)mNearestEventLayout.findViewById(R.id.homeListItem_picture);
////				imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
////				imageView.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.event));
////				
////				HyjNumericView textView = (HyjNumericView)mNearestEventLayout.findViewById(R.id.homeListItem_amount);
////				Project project = event.getProject();
////				String projectId = event.getProjectId();
////				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", projectId, HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
////				if(psa != null && psa.getProjectShareMoneyExpenseOwnerDataOnly() == true){
////					textView.setTextColor(Color.BLACK);
////					((TextView) textView).setText("-");
////				} else {
////					Double depositBalance = event.getBalance();
////					if(depositBalance == 0){
////						textView.setTextColor(Color.BLACK);
////						textView.setPrefix(project.getCurrencySymbol());
////					} else if(depositBalance < 0){
////						textView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
////						textView.setPrefix("支出"+project.getCurrencySymbol());
////					}else{
////						textView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
////						textView.setPrefix("收入"+project.getCurrencySymbol());
////					}
////					
////					textView.setNumber(Math.abs(depositBalance));
////				}
//				View view = mNearestEventLayout.findViewById(R.id.homeListItem_title);
//				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_title");
//
//				view = mNearestEventLayout.findViewById(R.id.homeListItem_remark);
//				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_remark");
//				
//				view = mNearestEventLayout.findViewById(R.id.homeListItem_owner);
//				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_owner");
//
//				view = mNearestEventLayout.findViewById(R.id.homeListItem_amount);
//				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_amount");
//
//				view = mNearestEventLayout.findViewById(R.id.homeListItem_picture);
//				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_picture");
//				
//				view = mNearestEventLayout.findViewById(R.id.homeListItem_subTitle);
//				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_subTitle");
//				
//				HyjDateTimeView dateTimeView = ((HyjDateTimeView)mNearestEventLayout.findViewById(R.id.homeListItem_date));
//				dateTimeView.setDateFormat("yyyy-MM-dd HH:mm");
//				dateTimeView.setTime(event.getStartDate());
//				
////				((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_title)).setText(event.getName());
////				
////					
////					long date = event.getDate();
////					long startDate = event.getStartDate();
////					long endDate = event.getEndDate(); 
////					long dt = (new Date()).getTime();
////					if(dt >= date && dt < startDate) {
////						((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_remark)).setText("[报名中]" + event.getSignUpCount() + "人");
////					} else if(dt >= startDate && dt < endDate) {
////						((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_remark)).setText("[进行中]" + event.getSignUpCount() + "人");
////					} else if(dt >= endDate) {
////						((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_remark)).setText("[已结束]" + event.getSignUpCount() + "人");
////					}
////
////					((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_subTitle)).setText(Friend.getFriendUserDisplayName(event.getOwnerUserId()));
////
////
////					EventMember em = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", event.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
////					if(em != null){
////						if("UnSignUp".equals(em.getState())){
////							((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_owner)).setText("[未报名]");
////						} else if("SignUp".equals(em.getState())){
////							((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_owner)).setText("[已报名]");
////						} else if("SignIn".equals(em.getState())){
////							((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_owner)).setText("[已签到]");
////						} 
////					} else {
////						((TextView)mNearestEventLayout.findViewById(R.id.homeListItem_owner)).setText("[未报名]");
////					}
//					
//					mNearestEventLayout.setVisibility(View.VISIBLE);
//			}
//			mNearestEventLoader = null;
//		}
//
//		@Override
//		public Object doInBackground(String... string) {
//			long currentTime = (new Date()).getTime();
//			Event event;
//			// 我报了名，而且进行中的活动
//			event = new Select("ev.*").from(Event.class).as("ev").join(EventMember.class).as("em").on("ev.id = em.eventId AND em.state <> 'UnSignUp'").where("startDate < ? AND endDate > ?", currentTime, currentTime).orderBy("startDate DESC").limit(1).executeSingle();
//			if(event == null){
//				// 我报了名，下一个开始的活动
//				event = new Select("ev.*").from(Event.class).as("ev").join(EventMember.class).as("em").on("ev.id = em.eventId AND em.state <> 'UnSignUp'").where("startDate > ?", currentTime).orderBy("startDate").limit(1).executeSingle();
//				if(event == null){
//					// 我没报名，下一个开始的活动
//					event = new Select().from(Event.class).where("startDate > ?", currentTime).orderBy("startDate").limit(1).executeSingle();
//					if(event == null){
//						// 我报了名，刚结束的活动
//						event = new Select("ev.*").from(Event.class).as("ev").join(EventMember.class).as("em").on("ev.id = em.eventId AND em.state <> 'UnSignUp'").where("endDate < ?", currentTime).orderBy("endDate DESC").limit(1).executeSingle();
//						if(event == null){
//							// 我没报名，刚开始的活动
//							event = new Select().from(Event.class).where("startDate < ?", currentTime).orderBy("endDate DESC").limit(1).executeSingle();
//						}
//					}
//				}
//			}
//			return event;
//		}
//	};
//	public void updateNearestEvent(){
//		if(mNearestEventLoader == null){
//			mNearestEventLoader = HyjAsyncTask.newInstance(mNearestEventLoaderCallback);
//		}
//	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			Event event = HyjModel.getModel(Event.class, cursor.getString(cursor.getColumnIndex("id")));

			if(view.getId() == R.id.homeListItem_date){
				HyjDateTimeView dateTimeView = ((HyjDateTimeView)view);
				dateTimeView.setDateFormat("yyyy-MM-dd HH:mm");
				dateTimeView.setTime(event.getStartDate());
			} else {
				EventListFragment.setEventViewValue(HomeCalendarGridEventListFragment.this, view, event, "homeListItem_"+columnIndex);
			}
			return true;
	}
	

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if(item.getItemId() == R.id.projectEventListFragment_action_add){
//			Bundle bundle = new Bundle();
////			bundle.putLong("PROJECT_ID", modelId);
//			openActivityWithFragment(EventFormFragment.class, R.string.projectEventListFragment_action_addnew, bundle);
//			return true;
//		} 
//		return super.onOptionsItemSelected(item);
//	}
	
	private class ChangeObserver extends ContentObserver {
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				public void run() {
					((CursorAdapter) getListAdapter()).notifyDataSetChanged();
//					mCalendarGridView.getAdapter().notifyDataSetChanged();
				}
			}, 50);

//			mDepositExpenseButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
//			mDepositIncomeButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
		}
	}
	
	@Override
	public void onDestroy() {
		if (mChangeObserver != null) {
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mChangeObserver);
		}

		if (mMessageChangeObserver != null) {
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mMessageChangeObserver);
		}
		super.onDestroy();
	}
	

	Message mLatestNewMessage;
	HyjAsyncTask mLatestNewMessageLoader = null;
	HyjAsyncTaskCallbacks mLatestNewMessageLoaderCallback = new HyjAsyncTaskCallbacks(){

		@Override
		public void finishCallback(Object object) {
			HashMap<String, Object> map = (HashMap<String, Object>)object;
			if(map.get("message") != null){
				mTextViewLatestNewMessage.setVisibility(View.VISIBLE);
				mLatestNewMessage = (Message)map.get("message");
				try {
					JSONObject messageData = null;
					messageData = new JSONObject(mLatestNewMessage.getMessageData());
					double amount = 0;
					try{
						amount = messageData.getDouble("amount") * messageData.getDouble("exchangeRate");
					} catch(Exception e) {
						amount = messageData.optDouble("amount");
					}
					java.util.Currency localeCurrency = java.util.Currency
							.getInstance(messageData.optString("currencyCode"));
					String currencySymbol = "";
					currencySymbol = localeCurrency.getSymbol();
					if(currencySymbol.length() == 0){
						currencySymbol = messageData.optString("currencyCode");
					}
							
					mTextViewLatestNewMessage.setText(String.format(mLatestNewMessage.getMessageDetail(), mLatestNewMessage.getFromUserDisplayName(), currencySymbol, amount));
				} catch (Exception e){
					mTextViewLatestNewMessage.setText(mLatestNewMessage.getMessageDetail());
				}
			} else {
				mTextViewLatestNewMessage.setVisibility(View.GONE);
			}
			mLatestNewMessageLoader = null;
			
			BadgeView badgeView = (BadgeView)(mLayoutActionMessage.getTag());
			if(badgeView == null){
				badgeView = new BadgeView(getActivity());
				badgeView.setHideOnNull(true);
				badgeView.setBadgeCount(0);
//				badgeView.setMaxLines(1);
				badgeView.setSingleLine();
				badgeView.setEllipsize(TruncateAt.END);
//				tab.removeView(badgeView);
				mLayoutActionMessage.addView(badgeView);
				mLayoutActionMessage.setTag(badgeView);
			}

			badgeView.setBadgeCount((Integer) map.get("count"));
		}

		@Override
		public Object doInBackground(String... string) {
			HyjModel message;
			message = new Select().from(Message.class).where("messageState = 'new' OR messageState = 'unread'").orderBy("date DESC").limit(1).executeSingle();
			
			int count = 0;
			Cursor cursor = Cache.openDatabase().rawQuery(
					"SELECT COUNT(*) FROM Message WHERE messageState = 'new' OR messageState = 'unread'",
					null);
			if (cursor != null) {
				cursor.moveToFirst();
				count = cursor.getInt(0);
				cursor.close();
				cursor = null;
			}

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("count", count);
			map.put("message", message);
	    	return map;
		}
	};
	
	public void updateLatestModifiedModel(){
		if(mLatestNewMessageLoader == null){
			mLatestNewMessageLoader = HyjAsyncTask.newInstance(mLatestNewMessageLoaderCallback);
		}
	}

    public void onLatestModifiedModelClick() { 
		if(mLatestNewMessage == null) {
			 return;
		}
			Bundle bundle = new Bundle();
			bundle.putLong("MODEL_ID", mLatestNewMessage.get_mId());
			Message msg = mLatestNewMessage;
			if(msg.getType().equals("System.Friend.AddRequest") ){
				openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addrequest, bundle);
			} else if(msg.getType().equals("System.Friend.AddResponse") ){
				openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addresponse, bundle);
			} else if(msg.getType().equals("System.Friend.Delete") ){
				openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_delete, bundle);
			} else if(msg.getType().equals("Project.Share.AddRequest") ){
				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_addrequest, bundle);
			} else if(msg.getType().equals("Project.Share.Accept") ){
				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_accept, bundle);
			} else if(msg.getType().equals("Project.Share.Delete") ){
				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_delete, bundle);
			} else if(msg.getType().equals("Project.Share.Edit") ){
				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_edit, bundle);
			} else if(msg.getType().startsWith("Money.Share.Add") ){
				openActivityWithFragment(MoneyShareMessageFormFragment.class, msg.getMessageTitle(), bundle, false, null);
			} else if(msg.getType().equals("Event.Member.AddRequest") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_addrequest, bundle);
			} else if(msg.getType().equals("Event.Member.Accept") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_accept, bundle);
			} else if(msg.getType().equals("Event.Member.SignIn") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_signIn, bundle);
			} else if(msg.getType().equals("Event.Member.SignUp") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_signUp, bundle);
			} else if(msg.getType().equals("Event.Member.Cancel") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_Cancel, bundle);
			} else if(msg.getType().equals("Event.Member.CancelSignUp") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_cancelSignUp, bundle);
			} else if(msg.getType().equals("Project.Share.AcceptInviteLink") ){
				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_accept, bundle);
			} else if(msg.getType().equals("System.Message.Welcome") ){
				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_system_welcome, bundle);
			}
    }  

	private class MessageChangeObserver extends ContentObserver {
		public MessageChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}


		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);

			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				public void run() {
					updateLatestModifiedModel();
				}
			}, 50);
		}
	}
}
