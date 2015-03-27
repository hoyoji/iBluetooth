package com.hoyoji.hoyoji.project;

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
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.MoneyReturn;
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.UserData;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public class SharedProjectMoneySearchGroupListLoader extends
		AsyncTaskLoader<List<Map<String, Object>>> {

	private DateFormat mDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private List<Map<String, Object>> mGroupList;
	private Integer mLoadLimit = 10;
	private boolean mHasMoreData = true;
	private ChangeObserver mChangeObserver;
	private String mFriendUserId;
	private String mLocalFriendId;
	private long mDateFrom = 0;
	private long mDateTo = 0;

	public SharedProjectMoneySearchGroupListLoader(Context context, Bundle queryParams) {
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
		context.getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
				mChangeObserver);
		
	}

	private void copyQueryParams(Bundle queryParams) {
		if (queryParams != null) {
			mDateFrom = queryParams.getLong("dateFrom", 0);
			mDateTo = queryParams.getLong("dateTo", 0);
			mLoadLimit = queryParams.getInt("LIMIT", 10);
			mFriendUserId = queryParams.getString("friendUserId");
			mLocalFriendId = queryParams.getString("localFriendId");
			mLoadLimit += queryParams.getInt("pageSize", 10);
		} else {
			mLoadLimit += 10;
		}
		
	}

	public void requery(Bundle queryParams){
		copyQueryParams(queryParams);
		this.onContentChanged();
	}
	
	public void fetchMore(Bundle queryParams) {
		copyQueryParams(queryParams);
		this.onContentChanged();
	}

	private String buildSearchQuery(String type){
		StringBuilder queryStringBuilder = new StringBuilder(" 1 = 1 ");
		if(mFriendUserId != null){
			queryStringBuilder.append(" AND (main.ownerUserId = '" + mFriendUserId + "' OR friendUserId = '" + mFriendUserId + "' OR EXISTS(SELECT apr.id FROM Money"+type+"Apportion apr WHERE apr.money"+type+"ContainerId = main.id AND (apr.friendUserId = '" + mFriendUserId + "' OR apr.localFriendId = (SELECT id FROM Friend WHERE friendUserId = '"+mFriendUserId+"'))))");
		}
		if(mLocalFriendId != null){
			queryStringBuilder.append(" AND (localFriendId = '" + mLocalFriendId + "' OR EXISTS(SELECT apr.id FROM Money"+type+"Apportion apr WHERE apr.money"+type+"ContainerId = main.id AND apr.localFriendId = '" + mLocalFriendId + "'))");
		}
		return queryStringBuilder.toString();
	}
	
	
	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<Map<String, Object>> loadInBackground() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String currentUserId = HyjApplication.getInstance().getCurrentUser().getId();
		String localCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();

		DateFormat df = SimpleDateFormat.getDateInstance();
		Calendar calDateFrom = Calendar.getInstance();
		if(mDateTo != 0){
			calDateFrom.setTimeInMillis(mDateTo);
		} else {
			long maxDateInMillis = getMaxDateInMillis();
			if(maxDateInMillis != -1){
				calDateFrom.setTimeInMillis(maxDateInMillis);
			}
		}
		
		calDateFrom.set(Calendar.HOUR_OF_DAY, 0);
		calDateFrom.clear(Calendar.MINUTE);
		calDateFrom.clear(Calendar.SECOND);
		calDateFrom.clear(Calendar.MILLISECOND);
		
		long dateTo = mDateTo;	
		if(mDateTo == 0){
			dateTo = calDateFrom.getTimeInMillis() + 24 * 3600000;
		}

		boolean lastTime = false;
		long dateFromInMillis = mDateFrom;
		if(mDateFrom != 0){
			if(calDateFrom.getTimeInMillis() < dateFromInMillis){
				calDateFrom.setTimeInMillis(dateFromInMillis-1);
				lastTime = true;
			}
		}
		
		int loadCount = 0;
		while (loadCount < mLoadLimit) {
			int count = 0;
			String[] args = new String[] {
					String.valueOf(calDateFrom.getTimeInMillis()),
					String.valueOf(dateTo) };
			double expenseTotal = 0;
			double incomeTotal = 0;
			Cursor cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyExpense main LEFT JOIN Project prj ON main.projectId = prj.id LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
							"WHERE prj.id IS NULL AND date > ? AND date <= ?",
							args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getInt(0);
				expenseTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, SUM(main.amount * main.exchangeRate * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) AS total FROM MoneyIncome main LEFT JOIN Project prj ON main.projectId = prj.id LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.foreignCurrencyId = main.projectCurrencyId AND ex.localCurrencyId = '" + localCurrencyId + "' ) OR (ex.localCurrencyId = main.projectCurrencyId AND ex.foreignCurrencyId = '" + localCurrencyId + "') " +
							"WHERE prj.id IS NULL AND date > ? AND date <= ?",
							args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getInt(0);
				incomeTotal += cursor.getDouble(1);
				cursor.close();
				cursor = null;
			}
			if (count > 0) {
				String ds = df.format(calDateFrom.getTime());
//				ds = ds.replaceAll("Z$", "+0000");
				HashMap<String, Object> groupObject = new HashMap<String, Object>();
				groupObject.put("date", ds);
				groupObject.put("dateInMilliSeconds", calDateFrom.getTimeInMillis());
				groupObject.put("expenseTotal",
						HyjUtil.toFixed2(expenseTotal));
				groupObject.put("incomeTotal",
						HyjUtil.toFixed2(incomeTotal));
				list.add(groupObject);
				loadCount += count + 1;
			}

			// 我们要检查还有没有数据可以加载的，如果没有了，我们就break出。否则会进入无限循环。
			if(count == 0){
				long moreDataInMillis = getHasMoreDataDateInMillis(calDateFrom.getTimeInMillis());
				if(moreDataInMillis == -1){
					break;
				} else {
					calDateFrom.setTimeInMillis(moreDataInMillis);
				}
			} else {
				calDateFrom.add(Calendar.DAY_OF_YEAR, -1);
				if(dateFromInMillis != 0) {
					if(lastTime){
						break;
					}
					if(calDateFrom.getTimeInMillis() < dateFromInMillis){
						calDateFrom.setTimeInMillis(dateFromInMillis-1);
						lastTime = true;
					}
				}
			}
			Calendar calDateTo = Calendar.getInstance();
			calDateTo.setTimeInMillis(calDateFrom.getTimeInMillis());
			calDateTo.set(Calendar.HOUR_OF_DAY, 0);
			calDateTo.clear(Calendar.MINUTE);
			calDateTo.clear(Calendar.SECOND);
			calDateTo.clear(Calendar.MILLISECOND);
			dateTo = calDateTo.getTimeInMillis() + 24 * 3600000;
		}
		mHasMoreData = loadCount >= mLoadLimit;
		return list;
	}
	
	private long getHasMoreDataDateInMillis(long fromDateInMillis){
		String[] args = new String[] {
				String.valueOf(fromDateInMillis) };
		String dateString = null;
		Cursor cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT MAX(date) FROM MoneyExpense main WHERE date <= ?",
						args);
		if (cursor != null) {
			cursor.moveToFirst();
			dateString = cursor.getString(0);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT MAX(date) FROM MoneyIncome main WHERE date <= ?",
						args);
		if (cursor != null) {
			cursor.moveToFirst();
			if(cursor.getString(0) != null){
				if(dateString == null
						|| dateString.compareTo(cursor.getString(0)) < 0){
					dateString = cursor.getString(0);
				}
			}
			cursor.close();
			cursor = null;
		}
		if(dateString != null){
			Long dateInMillis = Long.valueOf(dateString);
				Calendar calToday = Calendar.getInstance();
				calToday.setTimeInMillis(dateInMillis);
				calToday.set(Calendar.HOUR_OF_DAY, 0);
				calToday.clear(Calendar.MINUTE);
				calToday.clear(Calendar.SECOND);
				calToday.clear(Calendar.MILLISECOND);
				return calToday.getTimeInMillis();
		} else {
			return -1;
		}
	}
	private long getMaxDateInMillis(){
		String[] args = new String[] { };
		String dateString = null;
		Cursor cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT MAX(date) FROM MoneyExpense",
						args);
		if (cursor != null) {
			cursor.moveToFirst();
			dateString = cursor.getString(0);
			cursor.close();
			cursor = null;
		}
		cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT MAX(date) FROM MoneyIncome",
						args);
		if (cursor != null) {
			cursor.moveToFirst();
			if(cursor.getString(0) != null){
				if(dateString == null
						|| dateString.compareTo(cursor.getString(0)) < 0){
					dateString = cursor.getString(0);
				}
			}
			cursor.close();
			cursor = null;
		}
		if(dateString != null){
			Long dateInMillis = Long.valueOf(dateString);
				Calendar calToday = Calendar.getInstance();
				calToday.setTimeInMillis(dateInMillis);
				calToday.set(Calendar.HOUR_OF_DAY, 0);
				calToday.clear(Calendar.MINUTE);
				calToday.clear(Calendar.SECOND);
				calToday.clear(Calendar.MILLISECOND);
				return calToday.getTimeInMillis();
		} else {
			return -1;
		}
	}
	
	public boolean hasMoreData() {
		return mHasMoreData;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(List<Map<String, Object>> objects) {
		mGroupList = objects;

		if (isStarted() && mGroupList != null) {
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
		if (mGroupList != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mGroupList);
		}

		if (takeContentChanged() || mGroupList == null) {
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
	public void onCanceled(List<Map<String, Object>> objects) {
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

		mGroupList = null;
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