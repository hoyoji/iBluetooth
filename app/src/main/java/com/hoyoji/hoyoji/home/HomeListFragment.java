package com.hoyoji.hoyoji.home;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.Loader;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjSimpleExpandableListAdapter;
import com.hoyoji.android.hyjframework.HyjSimpleExpandableListAdapter.OnFetchMoreListener;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjImagePreviewFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserExpandableListFragment;
import com.hoyoji.android.hyjframework.view.HyjDateTimeView;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
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
import com.hoyoji.hoyoji.money.MoneyTemplateListFragment;
import com.hoyoji.hoyoji.money.MoneyTopupFormFragment;
import com.hoyoji.hoyoji.money.MoneyTransferFormFragment;

public class HomeListFragment extends HyjUserExpandableListFragment implements OnFetchMoreListener {
	private List<Map<String, Object>> mListGroupData = new ArrayList<Map<String, Object>>();
	private ArrayList<List<HyjModel>> mListChildData = new ArrayList<List<HyjModel>>();
	private ContentObserver mChangeObserver = null;
	private Button mExpenseButton;
	private Button mIncomeButton;
	private TextView mIncomeStat;
	private TextView mExpenseStat;

	private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//	private int mImageBackgroundColor = Color.parseColor("#FF4C32");
	@Override
	public Integer useContentView() {
		return R.layout.home_listfragment_home;
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
	protected View useHeaderView(Bundle savedInstanceState){
		LinearLayout view =  (LinearLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.home_listfragment_home_header, null);
		mExpenseStat = (TextView) view.findViewById(R.id.home_stat_expenseStat);
		mIncomeStat = (TextView) view.findViewById(R.id.home_stat_incomeStat);
		return view;
	}
	
	@Override
	public void onInitViewData() {
		super.onInitViewData();
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		getListView().setGroupIndicator(null);
		
		mExpenseButton = (Button)getView().findViewById(R.id.homeListFragment_action_money_expense);
		mExpenseButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
		mExpenseButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openActivityWithFragment(MoneyExpenseViewPagerFragment.class, R.string.moneyExpenseFormFragment_title_addnew, null);
    		}
		});
		
		mIncomeButton = (Button)getView().findViewById(R.id.homeListFragment_action_money_income);
		mIncomeButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
		mIncomeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openActivityWithFragment(MoneyIncomeViewPagerFragment.class, R.string.moneyIncomeFormFragment_title_addnew, null);
    		}
		});
		
		mExpenseStat.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
		mIncomeStat.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
		
//		getView().findViewById(R.id.homeListFragment_action_money_transfer).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_addnew, null);
//    		}
//		});
		
//		getView().findViewById(R.id.homeListFragment_action_money_debt).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				PopupMenu popup = new PopupMenu(getActivity(), v);
//				MenuInflater inflater = popup.getMenuInflater();
//				inflater.inflate(R.menu.home_debt_actions, popup.getMenu());
//				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						if (item.getItemId() == R.id.homeDebt_action_money_addnew_borrow) {
//							openActivityWithFragment(MoneyBorrowFormFragment.class, R.string.moneyBorrowFormFragment_title_addnew, null);
//						} 
//						else if (item.getItemId() == R.id.homeDebt_action_money_addnew_lend) {
//							openActivityWithFragment(MoneyLendFormFragment.class, R.string.moneyLendFormFragment_title_addnew, null);
//						} 
//						else if (item.getItemId() == R.id.homeDebt_action_money_addnew_return) {
//							openActivityWithFragment(MoneyReturnFormFragment.class, R.string.moneyReturnFormFragment_title_addnew, null);
//						} 
//						else if (item.getItemId() == R.id.homeDebt_action_money_addnew_payback) {
//							openActivityWithFragment(MoneyPaybackFormFragment.class, R.string.moneyPaybackFormFragment_title_addnew, null);
//						} 
//						return false;
//					}
//				});
//				popup.show();
//			}
//		});
		
		getView().findViewById(R.id.homeListFragment_action_money_template).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				openActivityWithFragment(MoneyTemplateListFragment.class, R.string.moneyTemplateListFragment_title, null);
    		}
		});
		
//		getView().findViewById(R.id.homeListFragment_action_money_topup).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				PopupMenu popup = new PopupMenu(getActivity(), v);
//				MenuInflater inflater = popup.getMenuInflater();
//				inflater.inflate(R.menu.home_topup_actions, popup.getMenu());
//				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//					@Override
//					public boolean onMenuItemClick(MenuItem item) {
//						if (item.getItemId() == R.id.homeTopup_action_money_addnew_depositExpense) {
//							openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, null);
//						} else if (item.getItemId() == R.id.homeTopup_action_money_addnew_depositIncome) {
//							openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_addnew, null);
//						} else if (item.getItemId() == R.id.homeTopup_action_money_addnew_depositReturn) {
//							openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_addnew, null);
//						} else if (item.getItemId() == R.id.homeTopup_action_money_addnew_depositPayback) {
//							openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_addnew, null);
//						}
//						return false;
//					}
//				});
//				popup.show();
//			}
//		});
		
		getView().findViewById(R.id.homeListFragment_action_more).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v) {
				PopupMenu popup = new PopupMenu(getActivity(), v);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.home_more_actions, popup.getMenu());
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.homeTopup_action_money_addnew_debt) {

							PopupMenu popup = new PopupMenu(getActivity(), v);
							MenuInflater inflater = popup.getMenuInflater();
							inflater.inflate(R.menu.home_debt_actions, popup.getMenu());
							popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
								@Override
								public boolean onMenuItemClick(MenuItem item) {
									if (item.getItemId() == R.id.homeDebt_action_money_addnew_borrow) {
										openActivityWithFragment(MoneyBorrowFormFragment.class, R.string.moneyBorrowFormFragment_title_addnew, null);
									} 
									else if (item.getItemId() == R.id.homeDebt_action_money_addnew_lend) {
										openActivityWithFragment(MoneyLendFormFragment.class, R.string.moneyLendFormFragment_title_addnew, null);
									} 
									else if (item.getItemId() == R.id.homeDebt_action_money_addnew_return) {
										openActivityWithFragment(MoneyReturnFormFragment.class, R.string.moneyReturnFormFragment_title_addnew, null);
									} 
									else if (item.getItemId() == R.id.homeDebt_action_money_addnew_payback) {
										openActivityWithFragment(MoneyPaybackFormFragment.class, R.string.moneyPaybackFormFragment_title_addnew, null);
									} 
									return false;
								}
							});
							popup.show();
						
						} else if (item.getItemId() == R.id.homeTopup_action_money_addnew_transfer) {
							openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_addnew, null);
						} else if (item.getItemId() == R.id.homeTopup_action_money_addnew_topup) {
							openActivityWithFragment(MoneyTopupFormFragment.class, R.string.moneyTopupFormFragment_title_addnew, null);
						} 
						return false;
					}
				});
				popup.show();
			}
		});
		
		updateHeaderStat();
		
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
		
	}

	private void updateHeaderStat() {
		String currentUserId = HyjApplication.getInstance().getCurrentUser().getId();
		String localCurrencyId = HyjApplication.getInstance().getCurrentUser()
				.getUserData().getActiveCurrencyId();
		String localCurrencySymbol = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol();

		Calendar calToday = Calendar.getInstance();
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		calToday.clear(Calendar.MINUTE);
		calToday.clear(Calendar.SECOND);
		calToday.clear(Calendar.MILLISECOND);
		
		calToday.set(Calendar.DATE, 1);
		long dateFrom = calToday.getTimeInMillis();
		
		calToday.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号  
		calToday.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天  
		long dateTo = calToday.getTimeInMillis() + 1000*60*60*24;

		String[] args = new String[] {String.valueOf(dateFrom), String.valueOf(dateTo)};
		double expenseTotal = 0.0;
		double incomeTotal = 0.0;
		Cursor cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyExpense main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			expenseTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		this.mExpenseStat.setText(localCurrencySymbol + HyjUtil.toFixed2(expenseTotal));
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyIncome main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "'", args);
		if (cursor != null) {
			cursor.moveToFirst();
			incomeTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		this.mIncomeStat.setText(localCurrencySymbol + HyjUtil.toFixed2(incomeTotal));
		
	}

	@Override
	public ExpandableListAdapter useListViewAdapter() {
		HomeGroupListAdapter adapter = new HomeGroupListAdapter(
				getActivity(), mListGroupData, R.layout.home_listitem_group,
				new String[] { "date", "expenseTotal", "incomeTotal" },
				new int[] { R.id.homeListItem_group_date, 
							R.id.homeListItem_group_expenseTotal, 
							R.id.homeListItem_group_incomeTotal }, 
				mListChildData,
				R.layout.home_listitem_row, 
				new String[] {"picture", "subTitle", "title", "remark", "date", "amount", "owner"}, 
				new int[] {R.id.homeListItem_picture, R.id.homeListItem_subTitle, R.id.homeListItem_title, 
							R.id.homeListItem_remark, R.id.homeListItem_date,
							R.id.homeListItem_amount, R.id.homeListItem_owner});
		return adapter;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == R.id.homeListFragment_action_display_transaction_type_project) {
//			
//			return true;
//		}
		// Handle your other action bar items...
		
//		switch (item.getItemId()) {
//		 case R.id.homeListFragment_action_transactions :
//				 openActivityWithFragment(MoneySearchListFragment.class,
//				 R.string.moneySearchListFragment_title, null);
//				return true;
//		 case R.id.homeListFragment_action_report :
//				 openActivityWithFragment(MoneyReportFragment.class,
//				 R.string.moneyReportFragment_title, null);
//					return true;
////		case 2:
////			openActivityWithFragment(MessageListFragment.class,
////					R.string.friendListFragment_title_manage_message, null);
////			break;
//		
//		case R.id.homeListFragment_action_currency:
//			openActivityWithFragment(CurrencyExchangeViewPagerFragment.class,
//					R.string.currency_exchang_eviewpager_listFragment_title, null);
//			return true;
//		
////		case 3:
////			openActivityWithFragment(ExchangeListFragment.class,
////					R.string.exchangeListFragment_title_manage_exchange, null);
////			break;
////		case 4:
////			openActivityWithFragment(CurrencyListFragment.class,
////					R.string.currencyListFragment_title_manage_currency, null);
////			break;
////		case 4:
////			openActivityWithFragment(MoneyExpenseCategoryListFragment.class,
////					R.string.moneyCategoryFormDialogFragment_title_manage_expense, null);
////			break;
////		case 5:
////			openActivityWithFragment(MoneyIncomeCategoryListFragment.class,
////					R.string.moneyCategoryFormDialogFragment_title_manage_income, null);
////			break;
//		case R.id.homeListFragment_action_category:
//			openActivityWithFragment(ExpenseIncomeCategoryViewPagerFragment.class,
//					R.string.expense_income_viewpager_listFragment_title, null);
//			return true;
//		case R.id.homeListFragment_action_setting:
//			openActivityWithFragment(SystemSettingFormFragment.class,
//					R.string.systemSettingFormFragment_title, null);
//			return true;
////		case 5:
////			HyjApplication.getInstance().switchUser();
////			break;
//		}
		
		
		
//		if (item.getItemId() == R.id.mainActivity_action_money_addnew_expense) {
//			openActivityWithFragment(MoneyExpenseContainerFormFragment.class,
//					R.string.moneyExpenseFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_income) {
//			openActivityWithFragment(MoneyIncomeContainerFormFragment.class,
//					R.string.moneyIncomeFormFragment_title_addnew, null);
//			return true;
//		} 
//		else if (item.getItemId() == R.id.mainActivity_action_money_addnew_transfer) {
//			openActivityWithFragment(MoneyTransferFormFragment.class,
//					R.string.moneyTransferFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_borrow) {
//			openActivityWithFragment(MoneyBorrowFormFragment.class,
//					R.string.moneyBorrowFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_lend) {
//			openActivityWithFragment(MoneyLendFormFragment.class,
//					R.string.moneyLendFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_return) {
//			openActivityWithFragment(MoneyReturnFormFragment.class,
//					R.string.moneyReturnFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_payback) {
//			openActivityWithFragment(MoneyPaybackFormFragment.class,
//					R.string.moneyPaybackFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_depositExpense) {
//			openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class,
//					R.string.moneyDepositExpenseFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_depositIncome) {
//			openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class,
//					R.string.moneyDepositIncomeContainerFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_depositReturn) {
//			openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class,
//					R.string.moneyDepositReturnContainerFormFragment_title_addnew, null);
//			return true;
//		} else if (item.getItemId() == R.id.mainActivity_action_money_addnew_depositPayback) {
//			openActivityWithFragment(MoneyDepositPaybackFormFragment.class,
//					R.string.moneyDepositPaybackFormFragment_title_addnew, null);
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		
//	}
	
	@Override
	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
//		super.onCreateLoader(groupPos, arg1);
		Object loader;
		if (groupPos < 0) { // 这个是分类
			loader = new HomeGroupListLoader(getActivity(), arg1);
		} else {
			loader = new HomeChildListLoader(getActivity(), arg1);
		}
		return (Loader<Object>) loader;
	}

	@Override
	public void onLoadFinished(Loader loader, Object list) {
		HyjSimpleExpandableListAdapter adapter = (HyjSimpleExpandableListAdapter) getListView()
				.getExpandableListAdapter();
		if (loader.getId() < 0) {
			ArrayList<Map<String, Object>> groupList = (ArrayList<Map<String, Object>>) list;
			mListGroupData.clear();
			mListGroupData.addAll(groupList);
			for(int i = 0; i < groupList.size(); i++){
				if(mListChildData.size() <= i){
					mListChildData.add(null);
					getListView().expandGroup(i);
				} else if(getListView().collapseGroup(i)){
					getListView().expandGroup(i);
				}
			}
			adapter.notifyDataSetChanged();
			this.setFooterLoadFinished(((HomeGroupListLoader)loader).hasMoreData());
		} else {
				ArrayList<HyjModel> childList = (ArrayList<HyjModel>) list;
				mListChildData.set(loader.getId(), childList);
				adapter.notifyDataSetChanged();
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
		HyjSimpleExpandableListAdapter adapter = (HyjSimpleExpandableListAdapter)
		 getListView().getExpandableListAdapter();
		 if(loader.getId() < 0){
				this.mListGroupData.clear();
		 } else {
			 if(adapter.getGroupCount() > loader.getId()){
					this.mListChildData.set(loader.getId(), null);
			 } else {
				 getLoaderManager().destroyLoader(loader.getId());
			 }
		 }
		
	}

	@Override
	public void onGroupExpand(int groupPosition) {
//		int i = 0;
//		for(Map.Entry<String, Map<String, Object>> entry : mListGroupData.entrySet()){
//			if(i == groupPosition){
				long dateInMilliSeconds = (Long) mListGroupData.get(groupPosition).get("dateInMilliSeconds");
				Bundle bundle = new Bundle();
				bundle.putLong("dateFrom", dateInMilliSeconds);
				bundle.putLong("dateTo", dateInMilliSeconds + 24*3600000);
				getLoaderManager().restartLoader(groupPosition, bundle, this);
//			}
//			i++;
//		}
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
			imageView.setDefaultImage(R.drawable.ic_action_person);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			if(event == null){
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
			imageView.setImage(moneyExpense.getPicture());
			
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
			view.setTag(moneyExpense.getPicture());
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
			if(event == null){
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
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
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
			if(((MoneyExpenseContainer)object).getEvent() == null) {
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
			imageView.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			
			numericView.setPrefix(((MoneyDepositIncomeContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyDepositIncomeContainer)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_subTitle){
			if(((MoneyDepositReturnContainer)object).getEvent() == null) {
				((TextView)view).setText(((MoneyDepositReturnContainer)object).getProject().getDisplayName());
			} else {
				((TextView)view).setText(((MoneyDepositReturnContainer)object).getEvent().getName());
			}
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			
			numericView.setPrefix(((MoneyDepositReturnContainer)object).getProject().getCurrencySymbol());
			numericView.setNumber(((MoneyDepositReturnContainer)object).getProjectAmount());
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			} else {
				if(moneyTransfer.getTransferIn() != null && moneyTransfer.getTransferOut() != null){
					((TextView)view).setText("从"+moneyTransfer.getTransferOut().getName()+"转到"+moneyTransfer.getTransferIn().getName());
				} else if(moneyTransfer.getTransferOut() != null){
					((TextView)view).setText("从"+moneyTransfer.getTransferOut().getName()+"转出");
				} else if(moneyTransfer.getTransferIn() != null){
					((TextView)view).setText("转入到"+moneyTransfer.getTransferIn().getName());
				} else {
					((TextView)view).setText("转账");
				}
			}
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
			imageView.setImage(((MoneyTransfer)object).getPicture());

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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
			((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_yellow));
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
			numericView.setTextColor(getResources().getColor(R.color.hoyoji_yellow));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_picture_white);
			imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow) );
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
	public void onFetchMore() {
//		Bundle bundle = new Bundle();
//		bundle.putString("target", "findData");
//		bundle.putString("postData", (new JSONArray()).put(data).toString());
//		Loader loader = getLoaderManager().getLoader(-1);
//		((HomeGroupListLoader)loader).fetchMore(null);	
	}

	@Override
	public void doFetchMore(int offset, int pageSize){
		Loader loader = getLoaderManager().getLoader(-1);
		if(loader != null && ((HomeGroupListLoader)loader).isLoading()){
			return;
		}
		setFooterLoadStart();
		((HomeGroupListLoader)loader).fetchMore(null);	
	}
	
	@Override  
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if(id == -1) {
			 return false;
		}
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
			return true;
		} else {
			HyjModel object = (HyjModel) ((HyjSimpleExpandableListAdapter)parent.getExpandableListAdapter()).getChild(groupPosition, childPosition);
			Bundle bundle = new Bundle();
			bundle.putLong("MODEL_ID", object.get_mId());
			if(object instanceof MoneyExpense){
					openActivityWithFragment(MoneyExpenseFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyIncome){
					openActivityWithFragment(MoneyIncomeFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyExpenseContainer){
					openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyIncomeContainer){
					openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyDepositIncomeContainer){
				openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyDepositReturnContainer){
				openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyTransfer){
				MoneyTransfer moneyTransfer = (MoneyTransfer) object;
				if(moneyTransfer.getTransferType().equalsIgnoreCase("Topup")){
					openActivityWithFragment(MoneyTopupFormFragment.class, R.string.moneyTopupFormFragment_title_edit, bundle);
				} else {
					openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_edit, bundle);
				}
				return true;
			} else if(object instanceof MoneyBorrow){
				MoneyBorrow moneyBorrow = (MoneyBorrow) object;
				if(moneyBorrow.getMoneyDepositIncomeApportionId() != null){
					bundle.putLong("MODEL_ID", moneyBorrow.getMoneyDepositIncomeApportion().getMoneyDepositIncomeContainer().get_mId());
					openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
				}else{
					openActivityWithFragment(MoneyBorrowFormFragment.class, R.string.moneyBorrowFormFragment_title_edit, bundle);
				}
				return true;
			} else if(object instanceof MoneyLend){
				MoneyLend moneyLend = (MoneyLend) object;
				if(moneyLend.getMoneyDepositExpenseContainerId() != null){
					MoneyDepositExpenseContainer moneyDepositExpenseContainer = HyjModel.getModel(MoneyDepositExpenseContainer.class, moneyLend.getMoneyDepositExpenseContainerId());
					bundle.putLong("MODEL_ID", moneyDepositExpenseContainer.get_mId());
					openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_edit, bundle);
				} else {
					openActivityWithFragment(MoneyLendFormFragment.class, R.string.moneyLendFormFragment_title_edit, bundle);
				}
				return true;
			}  else if(object instanceof MoneyDepositExpenseContainer){
				openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof MoneyReturn){
				MoneyReturn moneyReturn = (MoneyReturn) object;
				if(moneyReturn.getMoneyDepositReturnApportionId() != null){
					bundle.putLong("MODEL_ID", moneyReturn.getMoneyDepositReturnApportion().getMoneyDepositReturnContainer().get_mId());
					openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_edit, bundle);
				}else{
					openActivityWithFragment(MoneyReturnFormFragment.class, R.string.moneyReturnFormFragment_title_edit, bundle);
				}
				return true;
			} else if(object instanceof MoneyPayback){
				MoneyPayback moneyPayback = (MoneyPayback) object;
				if(moneyPayback.getMoneyDepositPaybackContainerId() != null){
					MoneyDepositPaybackContainer moneyDepositPaybackContainer = HyjModel.getModel(MoneyDepositPaybackContainer.class, moneyPayback.getMoneyDepositPaybackContainerId());
					bundle.putLong("MODEL_ID", moneyDepositPaybackContainer.get_mId());
					openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_edit, bundle);
				} else {
					openActivityWithFragment(MoneyPaybackFormFragment.class, R.string.moneyPaybackFormFragment_title_edit, bundle);
				}
				return true;
			} else if(object instanceof MoneyDepositPaybackContainer){
				openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_edit, bundle);
				return true;
			} else if(object instanceof Message){
				Message msg = (Message)object;
				if(msg.getType().equals("System.Friend.AddRequest") ){
					openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addrequest, bundle);
					return true;
				} else if(msg.getType().equals("System.Friend.AddResponse") ){
					openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_addresponse, bundle);
					return true;
				} else if(msg.getType().equals("System.Friend.Delete") ){
					openActivityWithFragment(FriendMessageFormFragment.class, R.string.friendAddRequestMessageFormFragment_title_delete, bundle);
					return true;
				} else if(msg.getType().equals("Project.Share.AddRequest") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_addrequest, bundle);
					return true;
				} else if(msg.getType().equals("Project.Share.Accept") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_accept, bundle);
					return true;
				} else if(msg.getType().equals("Project.Share.Delete") ){
					openActivityWithFragment(ProjectMessageFormFragment.class, R.string.projectMessageFormFragment_title_delete, bundle);
					return true;
				} else if(msg.getType().startsWith("Money.Share.Add") ){
					openActivityWithFragment(MoneyShareMessageFormFragment.class, msg.getMessageTitle(), bundle, false, null);
					return true;
				}
			}
		}
		return false;
    } 
	private static class HomeGroupListAdapter extends HyjSimpleExpandableListAdapter{

		public HomeGroupListAdapter(Context context,
	            List<Map<String, Object>> groupData, int expandedGroupLayout,
	                    String[] groupFrom, int[] groupTo,
	                    List<? extends List<? extends HyjModel>> childData,
	                    int childLayout, String[] childFrom,
	                    int[] childTo) {
			super( context, groupData, expandedGroupLayout, groupFrom, groupTo,childData, childLayout, 
					childFrom, childTo) ;
		}
		
		@Override
		 public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
		            ViewGroup parent) {
		        View v;
		        if (convertView == null) {
		            v = newGroupView(isExpanded, parent);
		        } else {
		            v = convertView;
		        }
		        bindGroupView(v, (Map<String, ?>) this.getGroup(groupPosition), mGroupFrom, mGroupTo);
		        
		        return v;
		    }
		 
		 private void bindGroupView(View view, Map<String, ?> data, String[] from, int[] to) {
		        int len = to.length;

		        for (int i = 0; i < len; i++) {
		            View v = view.findViewById(to[i]);
		            if (v != null) {
		            	if(v instanceof HyjNumericView){
		            		HyjNumericView balanceTotalView = (HyjNumericView)v;
		            		if(v.getId() == R.id.homeListItem_group_expenseTotal){
		            			balanceTotalView.setPrefix("流出"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol());
			            	} else if(v.getId() == R.id.homeListItem_group_incomeTotal){
		            			balanceTotalView.setPrefix("流入"+HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol());
				            }
		            		balanceTotalView.setNumber(Double.valueOf(data.get(from[i]).toString()));
		            	} else if(v instanceof TextView){
		            		((TextView)v).setText((String)data.get(from[i]));
		            	}
		            }
		        }
		    }
	}
	private class ChangeObserver extends ContentObserver {
//		AsyncTask<String, Void, String> mTask = null;
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

//		@Override
//		public void onChange(boolean selfChange, Uri uri) {
//			super.onChange(selfChange, uri);
////			if(uri.toString().startsWith("content://com.hoyoji.hoyoji_android/userdata")){
////				expenseButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
////				incomeButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
////			}
//		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
//			if(mTask == null){
//				mTask = new AsyncTask<String, Void, String>() {
//			        @Override
//			        protected String doInBackground(String... params) {
//						try {
//							//等待其他的更新都到齐后再更新界面
//							Thread.sleep(0);
//						} catch (InterruptedException e) {}
//						return null;
//			        }
//			        @Override
//			        protected void onPostExecute(String result) {
//						((HyjSimpleExpandableListAdapter) getListView().getExpandableListAdapter()).notifyDataSetChanged();
//						mTask = null;
//			        }
//			    };
//			    mTask.execute();
//			}	
			mExpenseButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			mIncomeButton.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));

			mExpenseStat.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
			mIncomeStat.setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			
			Handler handler = new Handler(Looper.getMainLooper());
			handler.postDelayed(new Runnable() {
				public void run() {
					((HyjSimpleExpandableListAdapter) getListView().getExpandableListAdapter()).notifyDataSetChanged();
				}
			}, 50);
			updateHeaderStat();
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
