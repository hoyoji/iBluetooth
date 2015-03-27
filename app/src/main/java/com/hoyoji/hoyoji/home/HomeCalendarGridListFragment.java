package com.hoyoji.hoyoji.home;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjImagePreviewFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.view.HyjCalendarGrid;
import com.hoyoji.android.hyjframework.view.HyjCalendarGridAdapter;
import com.hoyoji.android.hyjframework.view.HyjDateTimeView;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.message.EventMessageFormFragment;
import com.hoyoji.hoyoji.message.FriendMessageFormFragment;
import com.hoyoji.hoyoji.message.MoneyShareMessageFormFragment;
import com.hoyoji.hoyoji.message.ProjectMessageFormFragment;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyDepositExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositPaybackContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.MoneyReturn;
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.MoneyAddNewDialogFragment;
import com.hoyoji.hoyoji.money.MoneyBorrowFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositExpenseContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositIncomeContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositPaybackContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyDepositReturnContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyExpenseContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyExpenseFormFragment;
import com.hoyoji.hoyoji.money.MoneyExpenseViewPagerFragment;
import com.hoyoji.hoyoji.money.MoneyIncomeContainerFormFragment;
import com.hoyoji.hoyoji.money.MoneyIncomeFormFragment;
import com.hoyoji.hoyoji.money.MoneyIncomeViewPagerFragment;
import com.hoyoji.hoyoji.money.MoneyLendFormFragment;
import com.hoyoji.hoyoji.money.MoneyPaybackFormFragment;
import com.hoyoji.hoyoji.money.MoneyReturnFormFragment;
import com.hoyoji.hoyoji.money.MoneyTopupFormFragment;
import com.hoyoji.hoyoji.money.MoneyTransferFormFragment;

public class HomeCalendarGridListFragment extends HyjUserListFragment {
	private List<Map<String, Object>> mListGroupData = new ArrayList<Map<String, Object>>();
	private List<HyjModel> mListChildData = new ArrayList<HyjModel>();
	private ContentObserver mChangeObserver = null;
//	private ContentObserver mMessageChangeObserver;
	private Button mExpenseButton;
	private Button mIncomeButton;
	private TextView mIncomeStat;
	private TextView mExpenseStat;
	private TextView mCurrentMonth;
	private TextView mCurrentYear;

//	private HyjNumericView mGroupHeaderIncome;
//	private HyjNumericView mGroupHeaderExpense;
//	private TextView mGroupHeaderDate;
	
	private HyjCalendarGrid mCalendarGridView;
//	private TextView mTextViewLatestNewMessage;
	private FrameLayout mLayoutActionMessage;
	
	DateFormat df = SimpleDateFormat.getDateInstance();
	
	private DateFormat mDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	@Override
	public Integer useContentView() {
		return R.layout.home_listfragment_home_calendargrid;
	}

	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}

	@Override
	public Integer useOptionsMenuView() {
//		return R.menu.home_listfragment_home;
		return null;
	}

	@Override
	protected CharSequence getNoContentText() {
		return "无财务流水";
	}
	@Override
	protected View useHeaderView(Bundle savedInstanceState){
		final LinearLayout view =  (LinearLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.home_calendargrid_header, null);
		mExpenseStat = (TextView) view.findViewById(R.id.home_stat_expenseStat);
		mIncomeStat = (TextView) view.findViewById(R.id.home_stat_incomeStat);
		mCalendarGridView = (HyjCalendarGrid) view.findViewById(R.id.home_calendar_grid);
		mCalendarGridView.setAdapter(new HyjCalendarGridAdapter(getActivity(), getResources()));
		mCalendarGridView.getAdapter().setData(mListGroupData);
		mCurrentMonth = (TextView) view.findViewById(R.id.home_stat_month);
		mCurrentYear = (TextView) view.findViewById(R.id.home_stat_year);

		mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
		mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"");
		
		mCalendarGridView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mCalendarGridView.getAdapter().setSelectedYear(mCalendarGridView.getAdapter().getYearAtPosition(arg2));
				mCalendarGridView.getAdapter().setSelectedMonth(mCalendarGridView.getAdapter().getMonthAtPosition(arg2));
				mCalendarGridView.getAdapter().setSelectedDay(mCalendarGridView.getAdapter().getDayAtPosition(arg2));

//				Calendar calToday = Calendar.getInstance();
//				calToday.set(Calendar.HOUR_OF_DAY, 0);
//				calToday.clear(Calendar.MINUTE);
//				calToday.clear(Calendar.SECOND);
//				calToday.clear(Calendar.MILLISECOND);
//				int year = mCalendarGridView.getAdapter().getSelectedYear();
//				int month = mCalendarGridView.getAdapter().getSelectedMonth()-1;
//				int day = mCalendarGridView.getAdapter().getSelectedDay();
//				calToday.set(Calendar.YEAR, year);
//				calToday.set(Calendar.MONTH, month);
//				calToday.set(Calendar.DATE, day);
//				long dateFrom = calToday.getTimeInMillis();
//				long dateTo = dateFrom + 24 * 3600000;
//				Bundle bundle = new Bundle();
//				bundle.putLong("dateFrom", dateFrom);
//				bundle.putLong("dateTo", dateTo);
//				openActivityWithFragment(MoneySearchListFragment.class, R.string.moneySearchListFragment_title, bundle);
				mCalendarGridView.getAdapter().notifyDataSetChanged();
				getLoaderManager().restartLoader(0, null, HomeCalendarGridListFragment.this);
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
					
					mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
					mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"");
					
					mListGroupData.clear();
//					updateHeaderStat();
//					updateGroupHeader();
					mCalendarGridView.getAdapter().notifyDataSetChanged();
					getLoaderManager().restartLoader(-1, null, HomeCalendarGridListFragment.this);
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
				getLoaderManager().restartLoader(-1, null, HomeCalendarGridListFragment.this);
			}
		});
		view.findViewById(R.id.home_calendar_control_previous_month).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				mCalendarGridView.getAdapter().setSelectedYear(mCalendarGridView.getAdapter().getCurrentYear());
//				mCalendarGridView.getAdapter().setSelectedMonth(mCalendarGridView.getAdapter().getCurrentMonth());
				mCalendarGridView.getAdapter().setJumpCalendar(-1, 0);

				mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
				mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"");
				
				mListGroupData.clear();
				mCalendarGridView.getAdapter().notifyDataSetChanged();
				getLoaderManager().restartLoader(-1, null, HomeCalendarGridListFragment.this);
			}
		});
		view.findViewById(R.id.home_calendar_control_next_month).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
//				mCalendarGridView.getAdapter().setSelectedYear(mCalendarGridView.getAdapter().getCurrentYear());
//				mCalendarGridView.getAdapter().setSelectedMonth(mCalendarGridView.getAdapter().getCurrentMonth());
				mCalendarGridView.getAdapter().setJumpCalendar(1, 0);
				
				mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
				mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"");
				
				mListGroupData.clear();
//				GStat();
//				updateGroupHeader();
				mCalendarGridView.getAdapter().notifyDataSetChanged();
				getLoaderManager().restartLoader(-1, null, HomeCalendarGridListFragment.this);
			}
		});
		view.findViewById(R.id.home_stat_center).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {  
				    //下面的参数是用户设置完之后的时间  
				    @Override  
				    public void onDateSet(DatePicker view, int year, int monthOfYear,  
				            int dayOfMonth) {  
				    	if(monthOfYear + 1 != mCalendarGridView.getAdapter().getCurrentMonth() 
				    			|| mCalendarGridView.getAdapter().getCurrentYear() != year
				    			|| mCalendarGridView.getAdapter().getSelectedDay() != dayOfMonth){
							
//							mCalendarGridView.getAdapter().setSelectedYear(year);
//							mCalendarGridView.getAdapter().setSelectedMonth(monthOfYear+1);
							mCalendarGridView.getAdapter().setSelectedDay(dayOfMonth);
							
							mCalendarGridView.getAdapter().setCalendar(year, monthOfYear+1);
							
							
							mCurrentMonth.setText(mCalendarGridView.getAdapter().getCurrentMonth() + "月");
							mCurrentYear.setText(mCalendarGridView.getAdapter().getCurrentYear()+"");
							
							mListGroupData.clear();
//							updateHeaderStat();
//							updateGroupHeader();
							mCalendarGridView.getAdapter().notifyDataSetChanged();
							getLoaderManager().restartLoader(-1, null, HomeCalendarGridListFragment.this);
				    	}
				    }  
				};  
				Calendar calendar = Calendar.getInstance();
				DatePickerDialog dialog = new DatePickerDialog(getActivity(),  
	                    mDateSetListener,  
	                    calendar.get(Calendar.YEAR), 
	                    calendar.get(Calendar.MONTH),
	                    calendar.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});

//		view.findViewById(R.id.homeListFragment_action_message).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				openActivityWithFragment(MessageListFragment.class, R.string.messageListFragment_title, null);
//    		}
//		});
//		mTextViewLatestNewMessage = (TextView)view.findViewById(R.id.homeListFragment_new_message);
//		mLayoutActionMessage = (FrameLayout)view.findViewById(R.id.homeListFragment_action_message_layout);
//		mTextViewLatestNewMessage.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				if(mLatestNewMessage != null){
//					onLatestModifiedModelClick();
//				}
//			}
//		});
		
		return view;
	}

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
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		mExpenseButton = (Button)getView().findViewById(R.id.homeListFragment_action_money_expense);
		mExpenseButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				Calendar calToday = Calendar.getInstance();
				if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSelectedYear() 
						|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSelectedMonth() - 1 
						|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSelectedDay() ){
					calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getSelectedYear());
					calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getSelectedMonth()-1);
					calToday.set(Calendar.DAY_OF_MONTH, mCalendarGridView.getAdapter().getSelectedDay());
					
					bundle.putLong("DATE_IN_MILLISEC", calToday.getTimeInMillis());
				}
				openActivityWithFragment(MoneyExpenseViewPagerFragment.class, R.string.moneyExpenseFormFragment_title_addnew, bundle);
    		}
		});
		
		mIncomeButton = (Button)getView().findViewById(R.id.homeListFragment_action_money_income);
		mIncomeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Calendar calToday = Calendar.getInstance();
				Bundle bundle = new Bundle();
				if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSelectedYear() 
						|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSelectedMonth() - 1 
						|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSelectedDay() ){
					calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getSelectedYear());
					calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getSelectedMonth()-1);
					calToday.set(Calendar.DAY_OF_MONTH, mCalendarGridView.getAdapter().getSelectedDay());
					
					bundle.putLong("DATE_IN_MILLISEC", calToday.getTimeInMillis());
				}
				openActivityWithFragment(MoneyIncomeViewPagerFragment.class, R.string.moneyIncomeFormFragment_title_addnew, bundle);
    		}
		});

		mExpenseButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
		mIncomeButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
		mExpenseStat.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
		mIncomeStat.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
				
//		getView().findViewById(R.id.homeListFragment_action_messages).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				openActivityWithFragment(MessageListFragment.class, R.string.messageListFragment_title, null);
//    		}
//		});
////		getView().findViewById(R.id.homeListFragment_action_money_transaction).setOnClickListener(new OnClickListener(){
////			@Override
////			public void onClick(View v) {
////				openActivityWithFragment(MoneySearchListFragment.class, R.string.moneySearchListFragment_title, null);
////    		}
////		});
//		getView().findViewById(R.id.homeListFragment_action_friend).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				openActivityWithFragment(FriendListFragment.class, R.string.friendListFragment_title, null);
//    		}
//		});
		getView().findViewById(R.id.homelistfragment_money_addnew).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				Calendar calToday = Calendar.getInstance();
				if(calToday.get(Calendar.YEAR) != mCalendarGridView.getAdapter().getSelectedYear() 
						|| calToday.get(Calendar.MONTH) != mCalendarGridView.getAdapter().getSelectedMonth() - 1 
						|| calToday.get(Calendar.DAY_OF_MONTH) != mCalendarGridView.getAdapter().getSelectedDay() ){
					calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getSelectedYear());
					calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getSelectedMonth()-1);
					calToday.set(Calendar.DAY_OF_MONTH, mCalendarGridView.getAdapter().getSelectedDay());
					
					bundle.putLong("DATE_IN_MILLISEC", calToday.getTimeInMillis());
				}
				MoneyAddNewDialogFragment.newInstance(bundle).show(getActivity().getSupportFragmentManager(), "MoneyAddNewDialogFragment");
    		}
		});
		
		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
					mChangeObserver);
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(Project.class, null), true,
					mChangeObserver);
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(Friend.class, null), true,
					mChangeObserver);
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(User.class, null), true,
					mChangeObserver);
		}
//		if (mMessageChangeObserver == null) {
//			mMessageChangeObserver = new MessageChangeObserver();
//			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(Message.class, null), true,
//					mMessageChangeObserver);
//		}
		
		
		// 加载日历
		initLoader(-1);
//		updateLatestModifiedModel();
	}
	

	private void updateHeaderStat() {
		String currentUserId = HyjApplication.getInstance().getCurrentUser().getId();
		String localCurrencyId = HyjApplication.getInstance().getCurrentUser()
				.getUserData().getActiveCurrencyId();
		String localCurrencySymbol = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol();

		Calendar calToday = Calendar.getInstance();
		calToday.set(Calendar.YEAR, mCalendarGridView.getAdapter().getCurrentYear());
		calToday.set(Calendar.MONTH, mCalendarGridView.getAdapter().getCurrentMonth()-1);
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		calToday.clear(Calendar.MINUTE);
		calToday.clear(Calendar.SECOND);
		calToday.clear(Calendar.MILLISECOND);
		
		calToday.set(Calendar.DATE, 1);
		long dateFrom = calToday.getTimeInMillis();
		
		calToday.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号  
		calToday.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天  
		long dateTo = calToday.getTimeInMillis() + 3600000*24;
		
//		long dateFrom = mCalendarGridView.getAdapter().getDateFrom();
//		long dateTo = mCalendarGridView.getAdapter().getDateTo();
		String[] args = new String[] {String.valueOf(dateFrom), String.valueOf(dateTo)};
		DecimalFormat df=new DecimalFormat("#0.00"); 
		double expenseTotal = 0.0;
		double incomeTotal = 0.0;
		Cursor cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyExpense main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex "
								+ "ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			expenseTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyLend main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			expenseTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyReturn main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			expenseTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.transferOutAmount * main.transferOutExchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyTransfer main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE main.transferOutId IS NOT NULL AND date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			expenseTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		
		this.mExpenseStat.setText(localCurrencySymbol + df.format(expenseTotal));
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyIncome main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			incomeTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyBorrow main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			incomeTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		
//		List<MoneyBorrow> list = new Select().from(MoneyBorrow.class).where("ownerUserId = '" + currentUserId + "'").execute();
		
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyPayback main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			incomeTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.transferInAmount * main.transferInExchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyTransfer main LEFT JOIN (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE main.transferInId IS NOT NULL AND date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			incomeTotal += cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		this.mIncomeStat.setText(localCurrencySymbol + df.format(incomeTotal));
	}

	@Override
	public ListAdapter useListViewAdapter() {
		HomeListAdapter adapter = new HomeListAdapter(
				getActivity(), 
				mListChildData,
				R.layout.home_listitem_row, 
				new String[] {"picture", "subTitle", "title", "remark", "date", "amount", "_id"}, 
				new int[] {R.id.homeListItem_picture, R.id.homeListItem_subTitle, R.id.homeListItem_title, 
							R.id.homeListItem_remark, R.id.homeListItem_date,
							R.id.homeListItem_amount, R.id.homeListItem_owner});
		return adapter;
	}

	@Override
	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
		super.onCreateLoader(groupPos, arg1);
		Object loader;
		if(arg1 == null){
			arg1 = new Bundle();
		}

		if (groupPos < 0) { 
			long dateFrom = mCalendarGridView.getAdapter().getDateFrom();
			long dateTo = mCalendarGridView.getAdapter().getDateTo();
			
			arg1.putLong("startDateInMillis", dateFrom);
			arg1.putLong("endDateInMillis", dateTo);
			
			loader = new HomeCalendarGridGroupListLoader(getActivity(), arg1);
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
			arg1.putLong("dateFrom", calToday.getTimeInMillis());
			arg1.putLong("dateTo", calToday.getTimeInMillis() + 24 * 3600000);
			

			loader = new HomeCalendarGridChildListLoader(getActivity(), arg1);
		}
		return (Loader<Object>) loader;
	}

	@Override
	public void onLoadFinished(Loader loader, Object list) {
		if (loader.getId() < 0) {
			
			ArrayList<Map<String, Object>> groupList = (ArrayList<Map<String, Object>>) list;
			mListGroupData.clear();
			mListGroupData.addAll(groupList);
			mCalendarGridView.getAdapter().notifyDataSetChanged();
//			adapter.notifyDataSetChanged();
//			updateGroupHeader();
			updateHeaderStat();
			getLoaderManager().restartLoader(0, null, this);
		} else {
			ArrayList<HyjModel> childList = (ArrayList<HyjModel>) list;
			mListChildData.clear();
			mListChildData.addAll(childList);

			((HomeListAdapter)getListAdapter()).notifyDataSetChanged();
	        setFooterLoadFinished(getListView(), childList.size());
		}
		// The list should now be shown.
		if (isResumed()) {
			// setListShown(true);
		} else {
			// setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		 if(loader.getId() < 0){
				this.mListGroupData.clear();
		 } else {
				this.mListChildData.clear();
		 }
	}
	
	@Override
	public boolean setViewValue(View view, Object object, String name) {
		if(object instanceof MoneyExpense){
			return setMoneyExpenseItemValue(view, object, name);
		} else if(object instanceof MoneyIncome){
			return setMoneyIncomeItemValue(view, object, name);
		} else if(object instanceof MoneyExpenseContainer){
			return setMoneyExpenseContainerItemValue(view, object, name);
		} else if(object instanceof MoneyDepositExpenseContainer){
			return setMoneyDepositExpenseContainerItemValue(view, object, name);
		} else if(object instanceof MoneyIncomeContainer){
			return setMoneyIncomeContainerItemValue(view, object, name);
		} else if(object instanceof MoneyDepositIncomeContainer){
			return setMoneyDepositIncomeItemValue(view, object, name);
		}  else if(object instanceof MoneyDepositReturnContainer){
			return setMoneyDepositReturnItemValue(view, object, name);
		} else if(object instanceof MoneyTransfer){
			return setMoneyTransferItemValue(view, object, name);
		} else if(object instanceof MoneyBorrow){
			return setMoneyBorrowItemValue(view, object, name);
		} else if(object instanceof MoneyLend){
			return setMoneyLendItemValue(view, object, name);
		} else if(object instanceof MoneyReturn){
			return setMoneyReturnItemValue(view, object, name);
		} else if(object instanceof MoneyDepositPaybackContainer){
			return setMoneyDepositPaybackContainerItemValue(view, object, name);
		} else if(object instanceof MoneyDepositPaybackContainer){
			return setMoneyDepositPaybackContainerItemValue(view, object, name);
		} else if(object instanceof MoneyPayback){
			return setMoneyPaybackItemValue(view, object, name);
		}else if(object instanceof Message){
			return setMessageItemValue(view, object, name);
		}
		return false;
	}
	private boolean setMessageItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((Message)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText(((Message)object).getMessageTitle());
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			Message msg = (Message) object;
			((TextView)view).setText(msg.getFromUserDisplayName());
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(null);
			numericView.setSuffix(null);
			numericView.setNumber(null);
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setBackgroundResource(R.drawable.ic_action_unread);
			imageView.setImage(((Message)object).getFromUserId());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			Message message = (Message) object;
			if(message.getToUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText(message.getToUserDisplayName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			Message msg = (Message)object;
			try {
				JSONObject messageData = null;
				messageData = new JSONObject(msg.getMessageData());
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
				((TextView)view).setText(String.format(msg.getMessageDetail(), msg.getFromUserDisplayName(), currencySymbol, amount));
			} catch (Exception e){
				((TextView)view).setText(msg.getMessageDetail());
			}

			return true;
		} else {
			return false;
		}
	}
	private boolean setMoneyExpenseItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyExpense)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText(((MoneyExpense)object).getMoneyExpenseCategory());
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			MoneyExpense moneyExpense = (MoneyExpense)object;
			Event event = moneyExpense.getEvent();
			if(event == null) {
				Project project = moneyExpense.getProject();
				if(project == null){
					((TextView)view).setText("共享来的收支");
				} else {
					((TextView)view).setText(project.getDisplayName());
				}
			} else {
				((TextView)view).setText(event.getName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			Project project = ((MoneyExpense)object).getProject();
			if(project != null){
				numericView.setPrefix(project.getCurrencySymbol());
			} else {
				numericView.setPrefix(((MoneyExpense)object).getProjectCurrencySymbol());
			}
			numericView.setSuffix(null);
			numericView.setNumber(((MoneyExpense)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			MoneyExpense moneyExpense = (MoneyExpense)object;
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			imageView.setImage(moneyExpense .getPicture());
			
			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(moneyExpense .getPicture());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyExpense)object).getProjectCurrencyId())){
				((TextView)view).setText("");
			} else {
				Double localAmount = ((MoneyExpense)object).getLocalAmount();
				if(localAmount == null){
					((TextView)view).setText("折合:［无汇率］");
				} else {
					((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(localAmount)));
				}
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyExpense)object).getDisplayRemark());
			return true;
		} else {
			return false;
		}
	}
	
	private boolean setMoneyIncomeItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyIncome)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText(((MoneyIncome)object).getMoneyIncomeCategory());
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			MoneyIncome moneyIncome = (MoneyIncome)object;
			Event event = moneyIncome.getEvent();
			if(event == null) {
				Project project = moneyIncome.getProject();
				if(project == null){
					((TextView)view).setText("共享来的收支");
				} else {
					((TextView)view).setText(project.getDisplayName());
				}
			} else {
				((TextView)view).setText(event.getName());
			}
			
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			
			Project project = ((MoneyIncome)object).getProject();
			if(project != null){
				numericView.setPrefix(project.getCurrencySymbol());
			} else {
				numericView.setPrefix(((MoneyIncome)object).getProjectCurrencySymbol());
			}
			numericView.setSuffix(null);
			numericView.setNumber(((MoneyIncome)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			imageView.setImage(((MoneyIncome)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyIncome)object).getPicture());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyIncome)object).getProjectCurrencyId())){
				((TextView)view).setText("");
			} else {
				Double localAmount = ((MoneyIncome)object).getLocalAmount();
				if(localAmount == null){
					((TextView)view).setText("折合:［无汇率］");
				} else {
					((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(localAmount)));
				}
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyIncome)object).getDisplayRemark());
			return true;
		} else {
			return false;
		}
	}
	private boolean setMoneyExpenseContainerItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyExpenseContainer)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText(((MoneyExpenseContainer)object).getMoneyExpenseCategory());
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			if (((MoneyExpenseContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyExpenseContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyExpenseContainer)object).getEvent().getName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			
			numericView.setPrefix(((MoneyExpenseContainer)object).getProject().getCurrencySymbol());
			numericView.setSuffix(null);
			numericView.setNumber(((MoneyExpenseContainer)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			imageView.setImage(((MoneyExpenseContainer)object).getPicture());
			
			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyExpenseContainer)object).getPicture());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyExpenseContainer)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyExpenseContainer)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyExpenseContainer)object).getDisplayRemark());
			return true;
		} else {
			return false;
		}
	}
	
	private boolean setMoneyIncomeContainerItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyIncomeContainer)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText(((MoneyIncomeContainer)object).getMoneyIncomeCategory());
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyIncomeContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyIncomeContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyIncomeContainer)object).getEvent().getName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			
			numericView.setPrefix(((MoneyIncomeContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyIncomeContainer)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()) );
			imageView.setImage(((MoneyIncomeContainer)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyIncomeContainer)object).getPicture());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyIncomeContainer)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyIncomeContainer)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyIncomeContainer)object).getDisplayRemark());
			return true;
		} else {
			return false;
		}
	}
	private boolean setMoneyDepositIncomeItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyDepositIncomeContainer)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText("预收会费");
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyDepositIncomeContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyDepositIncomeContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyDepositIncomeContainer)object).getEvent().getName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			
			numericView.setPrefix(((MoneyDepositIncomeContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyDepositIncomeContainer)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			imageView.setImage(((MoneyDepositIncomeContainer)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyDepositIncomeContainer)object).getPicture());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyDepositIncomeContainer)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyDepositIncomeContainer)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyDepositIncomeContainer)object).getDisplayRemark());
			return true;
		} else {
			return false;
		}
	}
	
	private boolean setMoneyDepositReturnItemValue(View view, Object object, String name){
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyDepositReturnContainer)object).getDate());
			return true;
		} else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText("会费退款");
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			if (((MoneyDepositReturnContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyDepositReturnContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyDepositReturnContainer)object).getEvent().getName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			
			numericView.setPrefix(((MoneyDepositReturnContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyDepositReturnContainer)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			imageView.setImage(((MoneyDepositReturnContainer)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyDepositReturnContainer)object).getPicture());
			return true;
		}  else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyDepositReturnContainer)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyDepositReturnContainer)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyDepositReturnContainer)object).getDisplayRemark());
			return true;
		} else {
			return false;
		}
	}
	
	
	private boolean setMoneyTransferItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyTransfer)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			MoneyTransfer moneyTransfer = (MoneyTransfer) object;
			if(moneyTransfer.getTransferType().equalsIgnoreCase("Topup")){
				((TextView)view).setText("充值卡充值");
				((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			} else {
				if(moneyTransfer.getTransferIn() != null && moneyTransfer.getTransferOut() != null){
					((TextView)view).setText("从"+moneyTransfer.getTransferOut().getName()+"转到"+moneyTransfer.getTransferIn().getName());
					((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
				} else if(moneyTransfer.getTransferOut() != null){
					((TextView)view).setText("从"+moneyTransfer.getTransferOut().getName()+"转出");
					((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
				} else if(moneyTransfer.getTransferIn() != null){
					((TextView)view).setText("转入到"+moneyTransfer.getTransferIn().getName());
					((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
				} else {
					((TextView)view).setText("转账");
					((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
				}
			}
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyTransfer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyTransfer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyTransfer)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			MoneyTransfer moneyTransfer = (MoneyTransfer)object;
			numericView.setPrefix(moneyTransfer.getProject().getCurrencySymbol());
			numericView.setNumber(moneyTransfer.getTransferProjectAmount());
			if(moneyTransfer.getTransferIn() != null && moneyTransfer.getTransferOut() != null){
				numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			} else if(moneyTransfer.getTransferOut() != null){
				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			} else if(moneyTransfer.getTransferIn() != null){
				numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			} else {
				numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			MoneyTransfer moneyTransfer = (MoneyTransfer)object;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setImage(moneyTransfer.getPicture());

			if(moneyTransfer.getTransferIn() != null && moneyTransfer.getTransferOut() != null){
				imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
			} else if(moneyTransfer.getTransferOut() != null){
				imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			} else if(moneyTransfer.getTransferIn() != null){
				imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			} else {
				imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
			}
			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyTransfer)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			MoneyTransfer moneyTransfer = ((MoneyTransfer)object);
			if(moneyTransfer.getTransferType().equalsIgnoreCase("Topup")){
				((TextView)view).setText(moneyTransfer.getTransferInFriend().getDisplayName());
			} else {
				((TextView)view).setText("");
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyTransfer)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	
	private boolean setMoneyBorrowItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyBorrow)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			if(((MoneyBorrow)object).getMoneyDepositIncomeApportionId() != null){
				((TextView)view).setText("预收会费");
			} else {
				((TextView)view).setText("向" + ((MoneyBorrow)object).getFriendDisplayName() + "借入");
			}
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyBorrow)object).getEvent() == null) {
				((TextView)view).setText(((MoneyBorrow)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyBorrow)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(((MoneyBorrow)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyBorrow)object).getProjectAmount());
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			imageView.setImage(((MoneyBorrow)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyBorrow)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyBorrow)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyBorrow)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyBorrow)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	private boolean setMoneyDepositExpenseContainerItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyDepositExpenseContainer)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText("预缴会费");
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyDepositExpenseContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyDepositExpenseContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyDepositExpenseContainer)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(((MoneyDepositExpenseContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyDepositExpenseContainer)object).getProjectAmount());
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			imageView.setImage(((MoneyDepositExpenseContainer)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyDepositExpenseContainer)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyDepositExpenseContainer)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyDepositExpenseContainer)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyDepositExpenseContainer)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	private boolean setMoneyLendItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyLend)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			if(((MoneyLend)object).getMoneyDepositExpenseContainerId() != null){
				((TextView)view).setText("预缴会费");
			}else{
				((TextView)view).setText("借出给" + ((MoneyLend)object).getFriendDisplayName());
			}
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyLend)object).getEvent() == null) {
				((TextView)view).setText(((MoneyLend)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyLend)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(((MoneyLend)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyLend)object).getProjectAmount());
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			imageView.setImage(((MoneyLend)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyLend)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyLend)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyLend)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyLend)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	
	private boolean setMoneyReturnItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyReturn)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText("还款给" + ((MoneyReturn)object).getFriendDisplayName());
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyReturn)object).getEvent() == null) {
				((TextView)view).setText(((MoneyReturn)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyReturn)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(((MoneyReturn)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyReturn)object).getProjectAmount());
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			imageView.setImage(((MoneyReturn)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyReturn)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyReturn)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyReturn)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyReturn)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	
	private boolean setMoneyDepositPaybackContainerItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyDepositPaybackContainer)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			((TextView)view).setText("会费收回");
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyDepositPaybackContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyDepositPaybackContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyDepositPaybackContainer)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(((MoneyDepositPaybackContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyDepositPaybackContainer)object).getProjectAmount());
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			imageView.setImage(((MoneyDepositPaybackContainer)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyDepositPaybackContainer)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyDepositPaybackContainer)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyDepositPaybackContainer)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyDepositPaybackContainer)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	
	private boolean setMoneyPaybackItemValue(View view, Object object, String name) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setTime(((MoneyPayback)object).getDate());
			return true;
		}  else if(view.getId() == R.id.homeListItem_title){
			if(((MoneyPayback)object).getMoneyDepositPaybackContainerId() != null){
				((TextView)view).setText("会费收回");
			}else{
				((TextView)view).setText("向" + ((MoneyPayback)object).getFriendDisplayName()+"收款");
			}
			((TextView)view).setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		}  else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyPayback)object).getEvent() == null) {
				((TextView)view).setText(((MoneyPayback)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyPayback)object).getEvent().getName());
			}
			return true;
	    } else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix(((MoneyPayback)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyPayback)object).getProjectAmount());
			numericView.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			imageView.setImage(((MoneyPayback)object).getPicture());

			if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Picture pic = (Picture)v.getTag();
						if(pic == null){
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("pictureName", pic.getId());
						bundle.putString("pictureType", pic.getPictureType());
						openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
					}
				});
			}
			view.setTag(((MoneyPayback)object).getPicture());
			return true;
		} else if(view.getId() == R.id.homeListItem_owner){
			if(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId().equalsIgnoreCase(((MoneyPayback)object).getProject().getCurrencyId())){
				((TextView)view).setText("");
			} else {
				((TextView)view).setText("折合:"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol() + String.format("%.2f", HyjUtil.toFixed2(((MoneyPayback)object).getLocalAmount())));
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_remark){
			((TextView)view).setText(((MoneyPayback)object).getDisplayRemark());
			return true;
		} else{
			return false;
		}
	}
	@Override  
	public void onListItemClick(ListView parent, View v,
			int position, long id) {
		if(parent.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
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
			return;
		} else {
			HyjModel object = (HyjModel) getListAdapter().getItem(position-1);
			Bundle bundle = new Bundle();
			bundle.putLong("MODEL_ID", id);
			if(object instanceof MoneyExpense){
					openActivityWithFragment(MoneyExpenseFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyIncome){
					openActivityWithFragment(MoneyIncomeFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyExpenseContainer){
					openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyIncomeContainer){
					openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyDepositIncomeContainer){
				openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyDepositReturnContainer){
				openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyTransfer){
				MoneyTransfer moneyTransfer = (MoneyTransfer) object;
				if(moneyTransfer.getTransferType().equalsIgnoreCase("Topup")){
					openActivityWithFragment(MoneyTopupFormFragment.class, R.string.moneyTopupFormFragment_title_edit, bundle);
				} else {
					openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_edit, bundle);
				}
				return ;
			} else if(object instanceof MoneyBorrow){
				MoneyBorrow moneyBorrow = (MoneyBorrow) object;
				if(moneyBorrow.getMoneyDepositIncomeApportionId() != null){
					bundle.putLong("MODEL_ID", moneyBorrow.getMoneyDepositIncomeApportion().getMoneyDepositIncomeContainer().get_mId());
					openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
				}else{
					openActivityWithFragment(MoneyBorrowFormFragment.class, R.string.moneyBorrowFormFragment_title_edit, bundle);
				}
				return ;
			} else if(object instanceof MoneyLend){
				MoneyLend moneyLend = (MoneyLend) object;
				if(moneyLend.getMoneyDepositExpenseContainerId() != null){
					MoneyDepositExpenseContainer moneyDepositExpenseContainer = HyjModel.getModel(MoneyDepositExpenseContainer.class, moneyLend.getMoneyDepositExpenseContainerId());
					bundle.putLong("MODEL_ID", moneyDepositExpenseContainer.get_mId());
					openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_edit, bundle);
				} else {
					openActivityWithFragment(MoneyLendFormFragment.class, R.string.moneyLendFormFragment_title_edit, bundle);
				}
				return ;
			}  else if(object instanceof MoneyDepositExpenseContainer){
				openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof MoneyReturn){
				MoneyReturn moneyReturn = (MoneyReturn) object;
				if(moneyReturn.getMoneyDepositReturnApportionId() != null){
					bundle.putLong("MODEL_ID", moneyReturn.getMoneyDepositReturnApportion().getMoneyDepositReturnContainer().get_mId());
					openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
				}else{
					openActivityWithFragment(MoneyReturnFormFragment.class, R.string.moneyReturnFormFragment_title_edit, bundle);
				}
				return ;
			} else if(object instanceof MoneyPayback){
				MoneyPayback moneyPayback = (MoneyPayback) object;
				if(moneyPayback.getMoneyDepositPaybackContainerId() != null){
					MoneyDepositPaybackContainer moneyDepositPaybackContainer = HyjModel.getModel(MoneyDepositPaybackContainer.class, moneyPayback.getMoneyDepositPaybackContainerId());
					bundle.putLong("MODEL_ID", moneyDepositPaybackContainer.get_mId());
					openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_edit, bundle);
				} else {
					openActivityWithFragment(MoneyPaybackFormFragment.class, R.string.moneyPaybackFormFragment_title_edit, bundle);
				}
				return ;
			} else if(object instanceof MoneyDepositPaybackContainer){
				openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_edit, bundle);
				return ;
			} else if(object instanceof Message){
				Message msg = (Message)object;
				if(msg.getType().equals("System.Friend.AddRequest") ){
					openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addrequest, bundle);
					return ;
				} else if(msg.getType().equals("System.Friend.AddResponse") ){
					openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addresponse, bundle);
					return ;
				} else if(msg.getType().equals("System.Friend.Delete") ){
					openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_delete, bundle);
					return ;
				} else if(msg.getType().equals("Project.Share.AddRequest") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_addrequest, bundle);
					return ;
				} else if(msg.getType().equals("Project.Share.Accept") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_accept, bundle);
					return ;
				} else if(msg.getType().equals("Project.Share.Delete") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_delete, bundle);
					return ;
				} else if(msg.getType().startsWith("Money.Share.Add") ){
					openActivityWithFragment(MoneyShareMessageFormFragment.class, msg.getMessageTitle(), bundle, false, null);
					return ;
				} else if(msg.getType().equals("Event.Member.AddRequest") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_addrequest, bundle);
					return ;
				} else if(msg.getType().equals("Event.Member.Accept") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_accept, bundle);
					return ;
				} else if(msg.getType().equals("Event.Member.SignIn") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_signIn, bundle);
					return ;
				} else if(msg.getType().equals("Event.Member.SignUp") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_signUp, bundle);
					return ;
				} else if(msg.getType().equals("Event.Member.Cancel") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_Cancel, bundle);
					return ;
				} else if(msg.getType().equals("Event.Member.CancelSignUp") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_cancelSignUp, bundle);
					return ;
				} else if(msg.getType().equals("Project.Share.AcceptInviteLink") ){
					openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_accept, bundle);
					return ;
				} else if(msg.getType().equals("System.Message.Welcome") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_system_welcome, bundle);
					return ;
				}
			}
		}
		return ;
    } 
//
//    public void onLatestModifiedModelClick() { 
//		if(mLatestNewMessage == null) {
//			 return;
//		}
//			Bundle bundle = new Bundle();
//			bundle.putLong("MODEL_ID", mLatestNewMessage.get_mId());
//			Message msg = mLatestNewMessage;
//			if(msg.getType().equals("System.Friend.AddRequest") ){
//				openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addrequest, bundle);
//			} else if(msg.getType().equals("System.Friend.AddResponse") ){
//				openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addresponse, bundle);
//			} else if(msg.getType().equals("System.Friend.Delete") ){
//				openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_delete, bundle);
//			} else if(msg.getType().equals("Project.Share.AddRequest") ){
//				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_addrequest, bundle);
//			} else if(msg.getType().equals("Project.Share.Accept") ){
//				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_accept, bundle);
//			} else if(msg.getType().equals("Project.Share.Delete") ){
//				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_delete, bundle);
//			} else if(msg.getType().equals("Project.Share.Edit") ){
//				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_edit, bundle);
//			} else if(msg.getType().startsWith("Money.Share.Add") ){
//				openActivityWithFragment(MoneyShareMessageFormFragment.class, msg.getMessageTitle(), bundle, false, null);
//			} else if(msg.getType().equals("Event.Member.AddRequest") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_addrequest, bundle);
//			} else if(msg.getType().equals("Event.Member.Accept") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_accept, bundle);
//			} else if(msg.getType().equals("Event.Member.SignIn") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_signIn, bundle);
//			} else if(msg.getType().equals("Event.Member.SignUp") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_signUp, bundle);
//			} else if(msg.getType().equals("Event.Member.Cancel") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_Cancel, bundle);
//			} else if(msg.getType().equals("Event.Member.CancelSignUp") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_cancelSignUp, bundle);
//			} else if(msg.getType().equals("Project.Share.AcceptInviteLink") ){
//				openActivityWithFragment(EventMessageFormFragment.class, R.string.eventMessageFormFragment_title_accept, bundle);
//			} else if(msg.getType().equals("System.Message.Welcome") ){
//				openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_system_welcome, bundle);
//			}
//    }  
	
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

			int incomeColor = Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor());
			int expenseColor = Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor());
			
			mExpenseButton.setTextColor(expenseColor);
			mIncomeButton.setTextColor(incomeColor);

			mExpenseStat.setTextColor(expenseColor);
			mIncomeStat.setTextColor(incomeColor);
			
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				public void run() {
					mCalendarGridView.getAdapter().notifyDataSetChanged();
				}
			}, 50);
		}
	}
//	private class MessageChangeObserver extends ContentObserver {
//		public MessageChangeObserver() {
//			super(new Handler());
//		}
//
//		@Override
//		public boolean deliverSelfNotifications() {
//			return true;
//		}
//
//
//		@Override
//		public void onChange(boolean selfChange) {
//			super.onChange(selfChange);
//
//			Handler handler = new Handler(Looper.getMainLooper());
//			handler.postDelayed(new Runnable() {
//				public void run() {
//					updateLatestModifiedModel();
//				}
//			}, 50);
//		}
//	}
	@Override
	public void onDestroy() {
		if (mChangeObserver != null) {
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mChangeObserver);
		}
//		if (mMessageChangeObserver != null) {
//			this.getActivity().getContentResolver()
//					.unregisterContentObserver(mMessageChangeObserver);
//		}
	
		super.onDestroy();
	}
//	
//	Message mLatestNewMessage;
//	HyjAsyncTask mLatestNewMessageLoader = null;
//	HyjAsyncTaskCallbacks mLatestNewMessageLoaderCallback = new HyjAsyncTaskCallbacks(){
//
//		@Override
//		public void finishCallback(Object object) {
//			HashMap<String, Object> map = (HashMap<String, Object>)object;
//			if(map.get("message") != null){
//				mTextViewLatestNewMessage.setVisibility(View.VISIBLE);
//				mLatestNewMessage = (Message)map.get("message");
//				try {
//					JSONObject messageData = null;
//					messageData = new JSONObject(mLatestNewMessage.getMessageData());
//					double amount = 0;
//					try{
//						amount = messageData.getDouble("amount") * messageData.getDouble("exchangeRate");
//					} catch(Exception e) {
//						amount = messageData.optDouble("amount");
//					}
//					java.util.Currency localeCurrency = java.util.Currency
//							.getInstance(messageData.optString("currencyCode"));
//					String currencySymbol = "";
//					currencySymbol = localeCurrency.getSymbol();
//					if(currencySymbol.length() == 0){
//						currencySymbol = messageData.optString("currencyCode");
//					}
//							
//					mTextViewLatestNewMessage.setText(String.format(mLatestNewMessage.getMessageDetail(), mLatestNewMessage.getFromUserDisplayName(), currencySymbol, amount));
//				} catch (Exception e){
//					mTextViewLatestNewMessage.setText(mLatestNewMessage.getMessageDetail());
//				}
//			} else {
//				mTextViewLatestNewMessage.setVisibility(View.GONE);
//			}
//			mLatestNewMessageLoader = null;
//			
//			BadgeView badgeView = (BadgeView)(mLayoutActionMessage.getTag());
//			if(badgeView == null){
//				badgeView = new BadgeView(getActivity());
//				badgeView.setHideOnNull(true);
//				badgeView.setBadgeCount(0);
////				badgeView.setMaxLines(1);
//				badgeView.setSingleLine();
//				badgeView.setEllipsize(TruncateAt.END);
////				tab.removeView(badgeView);
//				mLayoutActionMessage.addView(badgeView);
//				mLayoutActionMessage.setTag(badgeView);
//			}
//
//			badgeView.setBadgeCount((Integer) map.get("count"));
//		}
//
//		@Override
//		public Object doInBackground(String... string) {
//			HyjModel message;
//			message = new Select().from(Message.class).where("messageState = 'new' OR messageState = 'unread'").orderBy("date DESC").limit(1).executeSingle();
//			
//			int count = 0;
//			Cursor cursor = Cache.openDatabase().rawQuery(
//					"SELECT COUNT(*) FROM Message WHERE messageState = 'new' OR messageState = 'unread'",
//					null);
//			if (cursor != null) {
//				cursor.moveToFirst();
//				count = cursor.getInt(0);
//				cursor.close();
//				cursor = null;
//			}
//
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("count", count);
//			map.put("message", message);
//	    	return map;
//		}
//	};
//	
//	public void updateLatestModifiedModel(){
//		if(mLatestNewMessageLoader == null){
//			mLatestNewMessageLoader = HyjAsyncTask.newInstance(mLatestNewMessageLoaderCallback);
//		}
//	}
//	
//	
	
	private static class HomeListAdapter extends SimpleAdapter{
		private Context mContext;
		private int[] mViewIds;
	    private String[] mFields;
	    private int mLayoutResource;
//	    private ViewBinder mViewBinder;
	    
		public HomeListAdapter(Context context,
	                    List<? extends HyjModel> childData,
	                    int childLayout, String[] childFrom,
	                    int[] childTo) {
			super(context, (List<? extends Map<String, ?>>) childData, childLayout, childFrom, childTo);

			mContext = context;
	        mLayoutResource = childLayout;
	        mViewIds = childTo;
	        mFields = childFrom;
		}

		@Override
	    public long getItemId(int position) {
	        return ((HyjModel)getItem(position)).get_mId();
	    }
	    
		@Override
		public boolean hasStableIds(){
			return true;
		}
	    
		/**
	     * Populate new items in the list.
	     */
	    @Override public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        View[] viewHolder;
	        if (view == null) {
	        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = vi.inflate(mLayoutResource, null);
	            viewHolder = new View[mViewIds.length];
	            for(int i=0; i<mViewIds.length; i++){
	            	View v = view.findViewById(mViewIds[i]);
	            	viewHolder[i] = v;
	            }
	            view.setTag(viewHolder);
	        } else {
	        	viewHolder = (View[])view.getTag();
	        }

	        Object item = getItem(position);
	        for(int i=0; i<mViewIds.length; i++){
	        	View v = viewHolder[i];
	        	getViewBinder().setViewValue(v, item, mFields[i]);
	        }
	        
	        return view;
	    }
	}
}
