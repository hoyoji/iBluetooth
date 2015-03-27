package com.hoyoji.hoyoji.money.moneyaccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.UserData;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public class MoneyAccountGroupListLoader extends
		AsyncTaskLoader<List<Map<String, Object>>> {

	private List<Map<String, Object>> mGroupList;
	private Integer mLoadLimit = 10;
	private boolean mHasMoreData = true;
	private ChangeObserver mChangeObserver;
	private String mExcludeType;
	private String mFriendId;
	private String mAccountType;

	public MoneyAccountGroupListLoader(Context context, Bundle queryParams) {
		super(context);
		if(queryParams != null){
    		mLoadLimit = queryParams.getInt("LIMIT");
    		mExcludeType = queryParams.getString("excludeType");
    		mAccountType = queryParams.getString("accountType");
    		mFriendId = queryParams.getString("friendId");
    	}

		mChangeObserver = new ChangeObserver();
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(MoneyAccount.class, null), true,
				mChangeObserver);
		
		context.getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
				mChangeObserver);
		
	}

	public void fetchMore(Bundle queryParams) {
		if (queryParams != null) {
			mLoadLimit += queryParams.getInt("pageSize", 10);
		} else {
			mLoadLimit += 10;
		}
		this.onContentChanged();
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<Map<String, Object>> loadInBackground() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		   <string-array name="moneyAccountFormFragment_spinnerField_accountType_array">
//	        <item>现金账户</item>
//	        <item>银行卡账户</item>
//    		<item>充值卡账户</item>
//	        <item>信用卡账户</item>
//	        <item>虚拟账户</item>
//	        <item>借贷账户</item>
//	   </string-array>
				HashMap<String, Object> groupObject = null; 
				String localCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
				double balanceTotal = 0;
				int count = 0;
				String query = "SELECT COUNT(*) AS count, SUM(currentBalance * CASE WHEN ex.localCurrencyId = '" + localCurrencyId + "' THEN 1/IFNULL(ex.rate,1) ELSE IFNULL(ex.rate, 1) END) as balanceTotal " +
						"FROM MoneyAccount ma LEFT JOIN  (SELECT * FROM Exchange GROUP BY localCurrencyId) ex ON (ex.localCurrencyId = ? AND ma.currencyId = ex.foreignCurrencyId) OR (ex.foreignCurrencyId = ? AND ma.currencyId = ex.localCurrencyId) " +
						"WHERE accountType = ?";
				String[] args = null;
				Cursor cursor = null;

				if(mFriendId != null){
					query += " AND localFriendId = ?";
				}
				if(mAccountType == null || mAccountType.equalsIgnoreCase("Cash")){
					if(mFriendId == null){
						args = new String[] {localCurrencyId, localCurrencyId, "Cash" };
					} else {
						args = new String[] {localCurrencyId, localCurrencyId, "Cash", mFriendId };
					}
					cursor = Cache
							.openDatabase()
							.rawQuery(query, args);
					if (cursor != null) {
						balanceTotal = 0;
						cursor.moveToFirst();
						count = cursor.getInt(0);
						balanceTotal += cursor.getDouble(1);
						cursor.close();
						cursor = null;
					}
					if(count > 0){
						groupObject = new HashMap<String, Object>();
						groupObject.put("name", "现金账户");
						groupObject.put("accountType", "Cash");
						groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
						list.add(groupObject);
					}
				}

				if(mAccountType == null || mAccountType.equalsIgnoreCase("Deposit")){
					if(mFriendId == null){
						args = new String[] {localCurrencyId, localCurrencyId, "Deposit" };
					} else {
						args = new String[] {localCurrencyId, localCurrencyId, "Deposit", mFriendId };
					}
					cursor = Cache
							.openDatabase()
							.rawQuery(query, args);
					if (cursor != null) {
						balanceTotal = 0;
						cursor.moveToFirst();
						count = cursor.getInt(0);
						balanceTotal += cursor.getDouble(1);
						cursor.close();
						cursor = null;
					}
					if(count > 0){
						groupObject = new HashMap<String, Object>();
						groupObject.put("name", "银行卡账户");
						groupObject.put("accountType", "Deposit");
						groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
						list.add(groupObject);
					}
				}
				if(mAccountType == null || mAccountType.equalsIgnoreCase("Topup")){
					if(mFriendId == null){
						args = new String[] {localCurrencyId, localCurrencyId, "Topup" };
					} else {
						args = new String[] {localCurrencyId, localCurrencyId, "Topup", mFriendId };
					}
					cursor = Cache
							.openDatabase()
							.rawQuery(query, args);
					if (cursor != null) {
						balanceTotal = 0;
						cursor.moveToFirst();
						count = cursor.getInt(0);
						balanceTotal += cursor.getDouble(1);
						cursor.close();
						cursor = null;
					}
					if(count > 0){
						groupObject = new HashMap<String, Object>();
						groupObject.put("name", "充值卡账户");
						groupObject.put("accountType", "Topup");
						groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
						list.add(groupObject);
					}
				}
				if(mAccountType == null || mAccountType.equalsIgnoreCase("Credit")){
					if(mFriendId == null){
						args = new String[] {localCurrencyId, localCurrencyId, "Credit" };
					} else {
						args = new String[] {localCurrencyId, localCurrencyId, "Credit", mFriendId };
					}
					cursor = Cache
							.openDatabase()
							.rawQuery(query, args);
					if (cursor != null) {
						balanceTotal = 0;
						cursor.moveToFirst();
						count = cursor.getInt(0);
						balanceTotal += cursor.getDouble(1);
						cursor.close();
						cursor = null;
					}
					if(count > 0){
						groupObject = new HashMap<String, Object>();
						groupObject.put("name", "信用卡账户");
						groupObject.put("accountType", "Credit");
						groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
						list.add(groupObject);
					}
				}
				if(mAccountType == null || mAccountType.equalsIgnoreCase("Online")){
					if(mFriendId == null){
						args = new String[] {localCurrencyId, localCurrencyId, "Online" };
					} else {
						args = new String[] {localCurrencyId, localCurrencyId, "Online", mFriendId };
					}
					cursor = Cache
							.openDatabase()
							.rawQuery(query, args);
					if (cursor != null) {
						balanceTotal = 0;
						cursor.moveToFirst();
						count = cursor.getInt(0);
						balanceTotal += cursor.getDouble(1);
						cursor.close();
						cursor = null;
					}
					if(count > 0){
						groupObject = new HashMap<String, Object>();
						groupObject.put("name", "虚拟账户");
						groupObject.put("accountType", "Online");
						groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
						list.add(groupObject);
					}
				}
				if(mAccountType == null || mAccountType.equalsIgnoreCase("Debt")){
					String _query = query + " AND (autoHide = 'Show' OR autoHide = 'Auto' AND currentBalance <> 0)";
					if(mExcludeType == null || !"Debt".equalsIgnoreCase(mExcludeType)){
						if(mFriendId == null){
							args = new String[] {localCurrencyId, localCurrencyId, "Debt" };
						} else {
							args = new String[] {localCurrencyId, localCurrencyId, "Debt", mFriendId };
						}
						cursor = Cache
								.openDatabase()
								.rawQuery(_query, args);
						if (cursor != null) {
							balanceTotal = 0;
							cursor.moveToFirst();
							count = cursor.getInt(0);
							balanceTotal += cursor.getDouble(1);
							cursor.close();
							cursor = null;
						}
						if(count > 0){
							groupObject = new HashMap<String, Object>();
							groupObject.put("name", "借贷账户");
							groupObject.put("accountType", "Debt");
							groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
							list.add(groupObject);
						}
					}
				} 
				if(mAccountType == null || mAccountType.equalsIgnoreCase("Debt")){
					if(mExcludeType == null || !"Debt".equalsIgnoreCase(mExcludeType)){
						String _query = query + " AND (autoHide = 'Hide' OR autoHide = 'Auto' AND currentBalance = 0)";
						if(mFriendId == null){
							args = new String[] {localCurrencyId, localCurrencyId, "Debt"};
						} else {
							args = new String[] {localCurrencyId, localCurrencyId, "Debt", mFriendId};
						}
						cursor = Cache
								.openDatabase()
								.rawQuery(_query, args);
						if (cursor != null) {
							balanceTotal = 0;
							cursor.moveToFirst();
							count = cursor.getInt(0);
							balanceTotal += cursor.getDouble(1);
							cursor.close();
							cursor = null;
						}
						if(count > 0){
							groupObject = new HashMap<String, Object>();
							groupObject.put("name", "隐藏借贷账户");
							groupObject.put("accountType", "AutoHide");
							groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
							list.add(groupObject);
						} 
//							else {
//							if(mFriendId == null){
//								args = new String[] {localCurrencyId, localCurrencyId, "Debt", "Auto"};
//							} else {
//								args = new String[] {localCurrencyId, localCurrencyId, "Debt", mFriendId, "Auto"};
//							}
//							cursor = Cache
//									.openDatabase()
//									.rawQuery(query, args);
//							if (cursor != null) {
//								balanceTotal = 0;
//								cursor.moveToFirst();
//								count = cursor.getInt(0);
//								balanceTotal += cursor.getDouble(1);
//								cursor.close();
//								cursor = null;
//							}
//							if(count > 0 && balanceTotal == 0){
//								groupObject = new HashMap<String, Object>();
//								groupObject.put("name", "隐藏借贷账户");
//								groupObject.put("accountType", "AutoHide");
//								groupObject.put("balanceTotal", HyjUtil.toFixed2(balanceTotal));
//								list.add(groupObject);
//							}
//						}
					}
				}
				mHasMoreData = false;
				return list;
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