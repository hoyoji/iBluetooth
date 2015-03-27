package com.hoyoji.hoyoji.money.report;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.money.MoneySearchFormFragment;

public class MoneyTransactionPersonalSummaryFragment extends HyjUserFragment implements
		LoaderManager.LoaderCallbacks<Object> {
	private static final int GET_SEARCH_QUERY = 10;

	private Project mProject;
	private MoneyAccount mMoneyAccount;
	private Friend mFriend;
	private Long mDateFrom;
	private Long mDateTo;
	private String mDisplayType;

	private TextView mTextViewInOutTotal;
	private TextView mTextViewExpenseTotal;
	private TextView mTextViewIncomeTotal;

	private TextView mTextViewTransferTotal;
	private TextView mTextViewTransferInTotal;
	private TextView mTextViewTransferOutTotal;

	private TextView mTextViewDebtTotal;
	private TextView mTextViewBorrowTotal;
	private TextView mTextViewLendTotal;
	private TextView mTextViewReturnTotal;
	private TextView mTextViewPaybackTotal;

	private Button mButtonDay;

	private Button mButtonWeek;

	private Button mButtonMonth;

	@Override
	public Integer useContentView() {
		return R.layout.money_fragment_transactionsummary;
	}

	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}

	@Override
	public Integer useOptionsMenuView() {
		return R.menu.money_listfragment_transactionsummary;
	}
	
	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Intent intent = getActivity().getIntent();
		String subTitle = null;
		final Long project_id = intent.getLongExtra("project_id", -1);
		if (project_id != -1) {
			mProject = new Select().from(Project.class)
					.where("_id=?", project_id).executeSingle();
			subTitle = mProject.getDisplayName();
		}
		final Long moneyAccount_id = intent.getLongExtra("moneyAccount_id", -1);
		if (moneyAccount_id != -1) {
			mMoneyAccount = new Select().from(MoneyAccount.class)
					.where("_id=?", moneyAccount_id).executeSingle();
			subTitle = mMoneyAccount.getDisplayName();
		}
		final Long friend_id = intent.getLongExtra("friend_id", -1);
		if (friend_id != -1) {
			mFriend = new Select().from(Friend.class).where("_id=?", friend_id)
					.executeSingle();
			subTitle = mFriend.getDisplayName();
		}

		if (subTitle != null) {
			((ActionBarActivity) getActivity()).getSupportActionBar()
					.setSubtitle(subTitle);
		}

		mTextViewInOutTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_inoutTotal);

		mTextViewExpenseTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_expenseTotal);
		mTextViewIncomeTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_incomeTotal);

		mTextViewTransferTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_transferTotal);
		mTextViewTransferInTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_transferInTotal);
		mTextViewTransferOutTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_transferOutTotal);

		mTextViewDebtTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_debtTotal);
		mTextViewBorrowTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_borrowTotal);
		mTextViewLendTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_lendTotal);
		mTextViewReturnTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_returnTotal);
		mTextViewPaybackTotal = (TextView) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_paybackTotal);

		mButtonDay = (Button) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_day);
		mButtonWeek = (Button) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_week);
		mButtonMonth = (Button) getView().findViewById(
				R.id.moneyTransactionSummaryFragment_month);
		mButtonDay.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
		mButtonDay.setTextColor(Color.WHITE);
		
		mButtonDay.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				clearButtonState();
				mButtonDay.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
				mButtonDay.setTextColor(Color.WHITE);
				
				Calendar calToday = Calendar.getInstance();
				calToday.set(Calendar.HOUR_OF_DAY, 0);
				calToday.clear(Calendar.MINUTE);
				calToday.clear(Calendar.SECOND);
				calToday.clear(Calendar.MILLISECOND);

				// get start of this week in milliseconds
				//				cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
				// cal.add(Calendar.WEEK_OF_YEAR, -1);
				
				mDateFrom = calToday.getTimeInMillis();
				mDateTo = calToday.getTimeInMillis() + 1000*60*60*24;
				initLoader(0);
			}
		});
		
		mButtonWeek.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				clearButtonState();
				mButtonWeek.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
				mButtonWeek.setTextColor(Color.WHITE);
				Calendar calToday = Calendar.getInstance();
				calToday.set(Calendar.HOUR_OF_DAY, 0);
				calToday.clear(Calendar.MINUTE);
				calToday.clear(Calendar.SECOND);
				calToday.clear(Calendar.MILLISECOND);
				
				// get start of this week in milliseconds
				int firstDayOfWeek = calToday.getFirstDayOfWeek();
				if(firstDayOfWeek == 0){
					firstDayOfWeek = 1;
				}
				calToday.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
				calToday.add(Calendar.DATE, 1);
				mDateFrom = calToday.getTimeInMillis();
				Date d = new Date(mDateFrom);
				calToday.add(Calendar.DATE, 7);
				mDateTo = calToday.getTimeInMillis() + 1000*60*60*24;
				
				initLoader(0);
			}
		});
		
		mButtonMonth.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				clearButtonState();
				mButtonMonth.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
				mButtonMonth.setTextColor(Color.WHITE);
				Calendar calToday = Calendar.getInstance();
				calToday.set(Calendar.HOUR_OF_DAY, 0);
				calToday.clear(Calendar.MINUTE);
				calToday.clear(Calendar.SECOND);
				calToday.clear(Calendar.MILLISECOND);

				calToday.set(Calendar.DATE, 1);
				mDateFrom = calToday.getTimeInMillis();

				calToday.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号  
				calToday.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天  
				mDateTo = calToday.getTimeInMillis() + 1000*60*60*24;
				
				initLoader(0);
			}
		});
		
		initLoader(0);
		
	}

	private void clearButtonState(){
		mButtonDay.setBackgroundColor(Color.TRANSPARENT);
		mButtonDay.setTextColor(Color.BLACK);
		mButtonWeek.setBackgroundColor(Color.TRANSPARENT);
		mButtonWeek.setTextColor(Color.BLACK);
		mButtonMonth.setBackgroundColor(Color.TRANSPARENT);
		mButtonMonth.setTextColor(Color.BLACK);
	}
	
	public void initLoader(int loaderId) {
		Bundle queryParams = buildQueryParams();
		// if(!queryParams.isEmpty()){
		Loader<Object> loader = getLoaderManager().getLoader(loaderId);
		if (loader != null && !loader.isReset()) {
			getLoaderManager().restartLoader(loaderId, queryParams, this);
		} else {
			getLoaderManager().initLoader(loaderId, queryParams, this);
		}
		// }
	}

	private Bundle buildQueryParams() {
		Bundle queryParams = new Bundle();
		if (mProject != null) {
			queryParams.putString("projectId", mProject.getId());
		}
		if (mMoneyAccount != null) {
			queryParams.putString("moneyAccountId", mMoneyAccount.getId());
		}
		if (mFriend != null) {
			if (mFriend.getFriendUserId() != null) {
				queryParams
						.putString("friendUserId", mFriend.getFriendUserId());
			} else {
				queryParams.putString("localFriendId", mFriend.getId());
			}
		}

		if (mDateFrom != null) {
			queryParams.putLong("dateFrom", mDateFrom);
		}
		if (mDateTo != null) {
			queryParams.putLong("dateTo", mDateTo);
		}
		if (mDisplayType != null) {
			queryParams.putString("displayType", mDisplayType);
		}
		return queryParams;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.moneyTransactionSummaryFragment_action_search) {
			Bundle queryParams = buildQueryParams();
			openActivityWithFragmentForResult(MoneySearchFormFragment.class,
					R.string.searchDialogFragment_title, queryParams,
					GET_SEARCH_QUERY);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_SEARCH_QUERY:
			if (resultCode == Activity.RESULT_OK) {
				mDateFrom = data.getLongExtra("dateFrom", 0);
				if (mDateFrom == 0) {
					mDateFrom = null;
				}
				mDateTo = data.getLongExtra("dateTo", 0);
				if (mDateTo == 0) {
					mDateTo = null;
				}
				mDisplayType = data.getStringExtra("displayType");

				String friendId = data.getStringExtra("friendId");
				if (friendId != null) {
					mFriend = HyjModel.getModel(Friend.class, friendId);
				}
				String projectId = data.getStringExtra("projectId");
				if (projectId != null) {
					mProject = HyjModel.getModel(Project.class, projectId);
				}
				String moneyAccountId = data.getStringExtra("moneyAccountId");
				if (moneyAccountId != null) {
					mMoneyAccount = HyjModel.getModel(MoneyAccount.class,
							moneyAccountId);
				}

				initLoader(0);
			}
			break;
		}
	}

	@Override
	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
		// super.onCreateLoader(groupPos, arg1);
		Object loader = new MoneyTransactionPersonalSummaryLoader(getActivity(), arg1);
		return (Loader<Object>) loader;
	}

	@Override
	public void onLoadFinished(Loader<Object> arg0, Object arg1) {
		Map<String, Object> transactionSummaryMap = (Map<String, Object>) arg1;
		mTextViewInOutTotal.setText(transactionSummaryMap.get("inoutTotal")
				.toString());
		mTextViewExpenseTotal.setText(transactionSummaryMap.get("expenseTotal")
				.toString());
		mTextViewIncomeTotal.setText(transactionSummaryMap.get("incomeTotal")
				.toString());

		mTextViewTransferTotal.setText(transactionSummaryMap.get(
				"transferTotal").toString());
		mTextViewTransferInTotal.setText(transactionSummaryMap.get(
				"transferInTotal").toString());
		mTextViewTransferOutTotal.setText(transactionSummaryMap.get(
				"transferOutTotal").toString());

		mTextViewDebtTotal.setText(transactionSummaryMap.get("debtTotal")
				.toString());
		mTextViewBorrowTotal.setText(transactionSummaryMap.get("borrowTotal")
				.toString());
		mTextViewLendTotal.setText(transactionSummaryMap.get("lendTotal")
				.toString());
		mTextViewReturnTotal.setText(transactionSummaryMap.get("returnTotal")
				.toString());
		mTextViewPaybackTotal.setText(transactionSummaryMap.get("paybackTotal")
				.toString());
	}

	@Override
	public void onLoaderReset(Loader<Object> arg0) {
		// TODO Auto-generated method stub

	}

}
