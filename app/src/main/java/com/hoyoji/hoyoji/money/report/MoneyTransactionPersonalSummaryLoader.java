package com.hoyoji.hoyoji.money.report;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.MoneyReturn;
import com.hoyoji.hoyoji.models.MoneyTransfer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public class MoneyTransactionPersonalSummaryLoader extends
		AsyncTaskLoader<Map<String, Object>> {

	private DateFormat mDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private Map<String, Object> mTransactionSummaryMap;
	private ChangeObserver mChangeObserver;
	private String mProjectId;
	private String mMoneyAccountId;
	private String mFriendUserId;
	private String mLocalFriendId;
	private long mDateFrom = 0;
	private long mDateTo = 0;

	public MoneyTransactionPersonalSummaryLoader(Context context, Bundle queryParams) {
		super(context);
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		copyQueryParams(queryParams);

		mChangeObserver = new ChangeObserver();
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyExpense.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyIncome.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyTransfer.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyBorrow.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyLend.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyReturn.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyPayback.class, null), true,
				mChangeObserver);
	}

	private void copyQueryParams(Bundle queryParams) {
		if (queryParams != null) {
			mDateFrom = queryParams.getLong("dateFrom", 0);
			mDateTo = queryParams.getLong("dateTo", 0);
			mProjectId = queryParams.getString("projectId");
			mMoneyAccountId = queryParams.getString("moneyAccountId");
			mFriendUserId = queryParams.getString("friendUserId");
			mLocalFriendId = queryParams.getString("localFriendId");
		}
	}

	public void requery(Bundle queryParams) {
		copyQueryParams(queryParams);
		this.onContentChanged();
	}

	public void fetchMore(Bundle queryParams) {
		copyQueryParams(queryParams);
		this.onContentChanged();
	}

	private String buildSearchQuery(String type) {
		StringBuilder queryStringBuilder = new StringBuilder(" 1 = 1 ");
		if (mProjectId != null) {
			queryStringBuilder.append(" AND projectId = '" + mProjectId + "' ");
		}
		if (mMoneyAccountId != null) {
			queryStringBuilder.append(" AND moneyAccountId = '"
					+ mMoneyAccountId + "' ");
		}
		if (mFriendUserId != null) {
			queryStringBuilder.append(" AND (main.ownerUserId = '"
					+ mFriendUserId + "' OR friendUserId = '" + mFriendUserId
					+ "' OR EXISTS(SELECT apr.id FROM Money" + type
					+ "Apportion apr WHERE apr.money" + type
					+ "ContainerId = main.id AND (apr.friendUserId = '" + mFriendUserId
					+ "')))");
		}
		if (mLocalFriendId != null) {
			queryStringBuilder.append(" AND (localFriendId = '"
					+ mLocalFriendId + "' OR EXISTS(SELECT apr.id FROM Money"
					+ type + "Apportion apr WHERE apr.money" + type
					+ "ContainerId = main.id AND apr.localFriendId = '" + mLocalFriendId
					+ "'))");
		}
		return queryStringBuilder.toString();
	}

	private String buildTransferSearchQuery() {
		StringBuilder queryStringBuilder = new StringBuilder(" 1 = 1 ");
		if (mProjectId != null) {
			queryStringBuilder.append(" AND projectId = '" + mProjectId + "' ");
		}
		if (mMoneyAccountId != null) {
			queryStringBuilder.append(" AND (transferInId = '"
					+ mMoneyAccountId + "' OR transferOutId = '"
					+ mMoneyAccountId + "') ");
		}
		if (mFriendUserId != null) {
			queryStringBuilder.append(" AND (main.ownerUserId = '"
					+ mFriendUserId + "' OR transferInFriendUserId = '"
					+ mFriendUserId + "' OR transferOutFriendUserId = '"
					+ mFriendUserId + "') ");
		}
		if (mLocalFriendId != null) {
			queryStringBuilder.append(" AND (transferInLocalFriendId = '"
					+ mLocalFriendId + "' OR transferOutLocalFriendId = '"
					+ mLocalFriendId + "') ");
		}
		return queryStringBuilder.toString();
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public Map<String, Object> loadInBackground() {
		Map<String, Object> transactionSummaryMap = new HashMap<String, Object>();

		String currentUserId = HyjApplication.getInstance().getCurrentUser().getId();
		String localCurrencyId = HyjApplication.getInstance().getCurrentUser()
				.getUserData().getActiveCurrencyId();
		String localCurrencySymbol = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol();
		
		Calendar calDateFrom = Calendar.getInstance();
		if (mDateFrom != 0) {
			calDateFrom.setTimeInMillis(mDateFrom);
		} else {
			calDateFrom.set(Calendar.HOUR_OF_DAY, 0);
			calDateFrom.clear(Calendar.MINUTE);
			calDateFrom.clear(Calendar.SECOND);
			calDateFrom.clear(Calendar.MILLISECOND);
		}
		
		long dateTo = mDateTo;
		if (mDateTo == 0) {
			Calendar calToday = Calendar.getInstance();
			calToday.set(Calendar.HOUR_OF_DAY, 0);
			calToday.clear(Calendar.MINUTE);
			calToday.clear(Calendar.SECOND);
			calToday.clear(Calendar.MILLISECOND);
			dateTo = calToday.getTimeInMillis() + 24 * 3600000;
		}

//		long dateFromInMillis = mDateFrom;
//		if (mDateFrom != 0) {
//			if (calDateFrom.getTimeInMillis() < dateFromInMillis) {
//				calDateFrom.setTimeInMillis(dateFromInMillis - 1);
//			}
//		}

		String[] args = new String[] {
				String.valueOf(calDateFrom.getTimeInMillis()),
				String.valueOf(dateTo) };
		double expenseTotal = 0.0;
		double incomeTotal = 0.0;
		double transferInTotal = 0.0;
		double transferOutTotal = 0.0;
		double borrowTotal = 0.0;
		double lendTotal = 0.0;
		double returnTotal = 0.0;
		double paybackTotal = 0.0;
		double inoutTotal = 0.0;
		double debtTotal = 0.0;
		double transferTotal = 0.0;
		
		Cursor cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyExpense main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ? AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildSearchQuery("Expense"), args);
		if (cursor != null) {
			cursor.moveToFirst();
			expenseTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("expenseTotal", localCurrencySymbol + HyjUtil.toFixed2(expenseTotal));
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyIncome main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildSearchQuery("Income"), args);
		if (cursor != null) {
			cursor.moveToFirst();
			incomeTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("incomeTotal", localCurrencySymbol + HyjUtil.toFixed2(incomeTotal));
		inoutTotal = incomeTotal - expenseTotal;
		transactionSummaryMap.put("inoutTotal", localCurrencySymbol + HyjUtil.toFixed2(inoutTotal));
		
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.transferInAmount * main.transferInExchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyTransfer main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " 
								+ "WHERE transferInId IS NOT NULL AND date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildTransferSearchQuery(), args);
		
		if (cursor != null) {
			cursor.moveToFirst();
			transferInTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT  COUNT(*) AS count, SUM(main.transferOutAmount * main.transferOutExchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyTransfer main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " 
								+ "WHERE transferOutId IS NOT NULL AND date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildTransferSearchQuery(), args);
		if (cursor != null) {
			cursor.moveToFirst();
			 transferOutTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("transferOutTotal", localCurrencySymbol + HyjUtil.toFixed2(transferOutTotal));
		transactionSummaryMap.put("transferInTotal", localCurrencySymbol + HyjUtil.toFixed2(transferInTotal));
		transferTotal = transferInTotal - transferOutTotal;
		transactionSummaryMap.put("transferTotal", localCurrencySymbol + HyjUtil.toFixed2(transferTotal));
		
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total "
								+ "FROM MoneyBorrow main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildSearchQuery("Borrow"), args);
		if (cursor != null) {
			cursor.moveToFirst();
			 borrowTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("borrowTotal", localCurrencySymbol + HyjUtil.toFixed2(borrowTotal));
		
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total "
								+ "FROM MoneyLend main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildSearchQuery("Lend"), args);
		if (cursor != null) {
			cursor.moveToFirst();
			 lendTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("lendTotal", localCurrencySymbol + HyjUtil.toFixed2(lendTotal));
		
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyReturn main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildSearchQuery("Return"), args);
		if (cursor != null) {
			cursor.moveToFirst();
			 returnTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("returnTotal", localCurrencySymbol + HyjUtil.toFixed2(returnTotal));
		
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total " 
								+ "FROM MoneyPayback main LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '"
								+ localCurrencyId
								+ "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '"
								+ localCurrencyId + "') "
								+ "WHERE date > ? AND date <= ?  AND main.ownerUserId = '" + currentUserId + "' AND "
								+ buildSearchQuery("Payback"), args);
		if (cursor != null) {
			cursor.moveToFirst();
			 paybackTotal = cursor.getDouble(1);
			cursor.close();
			cursor = null;
		}
		transactionSummaryMap.put("paybackTotal", localCurrencySymbol + HyjUtil.toFixed2(paybackTotal));
		debtTotal = borrowTotal - returnTotal - lendTotal + paybackTotal;
		transactionSummaryMap.put("debtTotal", localCurrencySymbol + HyjUtil.toFixed2(debtTotal));
		return transactionSummaryMap;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(Map<String, Object> objects) {
		mTransactionSummaryMap = objects;

		if (isStarted() && mTransactionSummaryMap != null) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(objects);
		}
	}

	@Override
	protected void onAbandon() {
		super.onAbandon();
		this.getContext().getContentResolver()
				.unregisterContentObserver(mChangeObserver);
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (mTransactionSummaryMap != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mTransactionSummaryMap);
		}

		if (takeContentChanged() || mTransactionSummaryMap == null) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(Map<String, Object> objects) {
		super.onCanceled(objects);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		mTransactionSummaryMap = null;
	}

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
			onContentChanged();
		}
	}

}