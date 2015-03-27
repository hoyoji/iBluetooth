package com.hoyoji.hoyoji.money;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyDepositExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositPaybackContainer;
import com.hoyoji.hoyoji.models.MoneyDepositReturnContainer;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseApportion;
import com.hoyoji.hoyoji.models.MoneyExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
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


public class MoneySearchChildListLoader extends AsyncTaskLoader<List<HyjModel>> {

	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
//	public static final Comparator<JSONObject> ALPHA_COMPARATOR = new Comparator<JSONObject>() {
//	    private final Collator sCollator = Collator.getInstance();
//	    @Override
//	    public int compare(JSONObject object1, JSONObject object2) {
//	        return sCollator.compare(object1.getString(mSortByField), object1.getString(mSortByField));
//	    }
//	};

//		private DateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	    private List<HyjModel> mChildList;
	    private Integer mLoadLimit = null;
	    private long mDateFrom = 0;
	    private long mDateTo = 0;
	    private ChangeObserver mChangeObserver;
	    private DateComparator mDateComparator = new DateComparator();
		private String mProjectId;
		private String mEventId;
		private String mMoneyAccountId;
		private String mFriendUserId;
		private String mLocalFriendId;
		private String mOwnerUserId;

	    
	    public MoneySearchChildListLoader(Context context, Bundle queryParams) {
	    	super(context);
//			mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			copyQueryParams(queryParams);
			
//	    	mChangeObserver = new ChangeObserver();
//	    	context.getContentResolver().registerContentObserver(
//	    			ContentProvider.createUri(MoneyExpenseContainer.class, null), true, mChangeObserver);
//	    	context.getContentResolver().registerContentObserver(
//	    			ContentProvider.createUri(MoneyIncome.class, null), true, mChangeObserver);

	    }
	    
		private void copyQueryParams(Bundle queryParams) {
			if (queryParams != null) {
				mDateFrom = queryParams.getLong("dateFrom", 0);
				mDateTo = queryParams.getLong("dateTo", 0);
				mLoadLimit = queryParams.getInt("LIMIT", 10);
				mProjectId = queryParams.getString("projectId");
				mEventId = queryParams.getString("eventId");
				mMoneyAccountId = queryParams.getString("moneyAccountId");
				mFriendUserId = queryParams.getString("friendUserId");
				mLocalFriendId = queryParams.getString("localFriendId");
				mOwnerUserId = queryParams.getString("ownerUserId");
				mLoadLimit += queryParams.getInt("pageSize", 10);
			} else {
				mLoadLimit += 10;
			}
			
		}
		
	    public void changeQuery(Bundle queryParams){
			copyQueryParams(queryParams);
	    	this.onContentChanged();
	    }

		private String buildSearchQuery(String type){
			StringBuilder queryStringBuilder = new StringBuilder(" 1 = 1 ");
			if(mProjectId != null){
				queryStringBuilder.append(" AND projectId = '" + mProjectId + "' ");
			}
			if(mEventId != null){
				queryStringBuilder.append(" AND eventId = '" + mEventId + "' ");
			}
			if(mMoneyAccountId != null){
				queryStringBuilder.append(" AND main.moneyAccountId = '" + mMoneyAccountId + "' ");
			}

			if(type.equalsIgnoreCase("DepositIncome") || type.equalsIgnoreCase("DepositReturn")){
				if(mFriendUserId != null){
					queryStringBuilder.append(" AND (main.ownerUserId = '" + mFriendUserId + "' OR EXISTS(SELECT apr.id FROM Money"+type+"Apportion apr WHERE apr.money"+type+"ContainerId = main.id AND apr.friendUserId = '" + mFriendUserId + "'))");
				} else if(mLocalFriendId != null){
					queryStringBuilder.append(" AND (EXISTS(SELECT apr.id FROM Money"+type+"Apportion apr WHERE apr.money"+type+"ContainerId = main.id AND apr.localFriendId = '" + mLocalFriendId + "'))");
				}
			} else if(type.equals("SharedProjectExpense")){
				if(mFriendUserId != null){
					queryStringBuilder.append(" AND (main.ownerUserId = '" + mFriendUserId + "' OR EXISTS (SELECT id FROM MoneyBorrow mb WHERE mb.moneyExpenseApportionId = main.moneyExpenseApportionId AND mb.friendUserId = '" + mFriendUserId + "')) ");
				}
				if(mLocalFriendId != null){
					queryStringBuilder.append(" AND 1 <> 1 ");
				}
			} else if(type.equals("SharedProjectIncome")){
				if(mFriendUserId != null){
					queryStringBuilder.append(" AND (main.ownerUserId = '" + mFriendUserId + "' OR EXISTS (SELECT id FROM MoneyLend ml WHERE ml.moneyIncomeApportionId = main.moneyIncomeApportionId AND ml.friendUserId = '" + mFriendUserId + "')) ");
				}
				if(mLocalFriendId != null){
					queryStringBuilder.append(" AND 1 <> 1 ");
				}
			} else {
				if(mFriendUserId != null){
					queryStringBuilder.append(" AND (main.ownerUserId = '" + mFriendUserId + "' OR main.friendUserId = '" + mFriendUserId + "' OR EXISTS(SELECT apr.id FROM Money"+type+"Apportion apr WHERE apr.money"+type+"ContainerId = main.id AND (apr.friendUserId = '" + mFriendUserId + "' OR apr.localFriendId = (SELECT id FROM Friend WHERE friendUserId = '"+mFriendUserId+"'))))");
				} else if(mLocalFriendId != null){
					queryStringBuilder.append(" AND (localFriendId = '" + mLocalFriendId + "' OR EXISTS(SELECT apr.id FROM Money"+type+"Apportion apr WHERE apr.money"+type+"ContainerId = main.id AND apr.localFriendId = '" + mLocalFriendId + "')");
					queryStringBuilder.append(" OR main.ownerUserId = '" + mLocalFriendId + "' ");
					queryStringBuilder.append(")");
				}
			}
			return queryStringBuilder.toString();
		}
		
		private String buildTransferSearchQuery(){
			StringBuilder queryStringBuilder = new StringBuilder(" 1 = 1 ");
			if(mProjectId != null){
				queryStringBuilder.append(" AND projectId = '" + mProjectId + "' ");
			}
			if(mMoneyAccountId != null){
				queryStringBuilder.append(" AND (transferInId = '" + mMoneyAccountId + "' OR transferOutId = '" + mMoneyAccountId + "') ");
			}
			if(mFriendUserId != null){
				queryStringBuilder.append(" AND (main.ownerUserId = '" + mFriendUserId + "' OR transferInFriendUserId = '" + mFriendUserId + "' OR transferOutFriendUserId = '" + mFriendUserId + "') ");
			}
			if(mLocalFriendId != null){
				queryStringBuilder.append(" AND (transferInLocalFriendId = '" + mLocalFriendId + "' OR transferOutLocalFriendId = '" + mLocalFriendId + "') ");
			}
			return queryStringBuilder.toString();
		}
	    
	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<HyjModel> loadInBackground() {
	    	
	    	long dateFrom = mDateFrom;
	    	long dateTo = mDateTo;
	    	ArrayList<HyjModel> list = new ArrayList<HyjModel>();
//	    	if(mProjectId == null){
		    	List<HyjModel> moneyExpenses = new Select("main.*").from(MoneyExpense.class).as("main").leftJoin(MoneyExpenseApportion.class).as("mea").on("main.moneyExpenseApportionId = mea.id").where(" mea.id IS NULL AND date > ? AND date <= ? AND " + buildSearchQuery("SharedProjectExpense"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyExpenses);
		    	
		    	List<HyjModel> moneyIncomes = new Select("main.*").from(MoneyIncome.class).as("main").leftJoin(MoneyIncomeApportion.class).as("mea").on("main.moneyIncomeApportionId = mea.id").where(" mea.id IS NULL AND date > ? AND date <= ? AND " + buildSearchQuery("SharedProjectIncome"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyIncomes);
//			}
	    	List<HyjModel> moneyExpenseContainers = new Select().from(MoneyExpenseContainer.class).as("main").where("date > ? AND date <= ? AND " + buildSearchQuery("Expense"), dateFrom, dateTo).orderBy("date DESC").execute();
	    	list.addAll(moneyExpenseContainers);
	    	
	    	List<HyjModel> moneyIncomeContainers = new Select().from(MoneyIncomeContainer.class).as("main").where("date > ? AND date <= ? AND " + buildSearchQuery("Income"), dateFrom, dateTo).orderBy("date DESC").execute();
	    	list.addAll(moneyIncomeContainers);
	    	
		    	List<HyjModel> moneyDepositExpenses = new Select().from(MoneyDepositExpenseContainer.class).as("main").where("date > ? AND date <= ? AND " + buildSearchQuery("Lend"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyDepositExpenses);
		    	
		    	List<HyjModel> moneyDepositPaybacks = new Select().from(MoneyDepositPaybackContainer.class).as("main").where("date > ? AND date <= ? AND " + buildSearchQuery("Payback"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyDepositPaybacks);
		    	
		    	List<HyjModel> moneyDepositIncomes = new Select().from(MoneyDepositIncomeContainer.class).as("main").where("date > ? AND date <= ? AND " + buildSearchQuery("DepositIncome"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyDepositIncomes);
		    	
		    	List<HyjModel> moneyDepositReturns = new Select().from(MoneyDepositReturnContainer.class).as("main").where("date > ? AND date <= ? AND " + buildSearchQuery("DepositReturn"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyDepositReturns);

		    	List<HyjModel> moneyTransfers = new Select().from(MoneyTransfer.class).as("main").where("date > ? AND date <= ? AND " + buildTransferSearchQuery(), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyTransfers);
		    	
		    	List<HyjModel> moneyBorrows = new Select().from(MoneyBorrow.class).as("main").where("ownerFriendId IS NULL AND moneyDepositIncomeApportionId IS NULL AND moneyIncomeApportionId IS NULL AND moneyExpenseApportionId IS NULL AND date > ? AND date <= ? AND " + buildSearchQuery("Borrow"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyBorrows);
		    	
		    	List<HyjModel> moneyLends = new Select().from(MoneyLend.class).as("main").where("ownerFriendId IS NULL AND moneyIncomeApportionId IS NULL AND moneyExpenseApportionId IS NULL AND moneyDepositExpenseContainerId IS NULL AND  moneyDepositIncomeApportionId IS NULL AND date > ? AND date <= ? AND " + buildSearchQuery("Lend"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyLends);
		    	
		    	List<HyjModel> moneyReturns = new Select().from(MoneyReturn.class).as("main").where("ownerFriendId IS NULL AND moneyDepositReturnApportionId IS NULL AND date > ? AND date <= ? AND " + buildSearchQuery("Return"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyReturns);
		    	
		    	List<HyjModel> moneyPaybacks = new Select().from(MoneyPayback.class).as("main").where("ownerFriendId IS NULL AND moneyDepositPaybackContainerId IS NULL AND moneyDepositReturnApportionId IS NULL AND date > ? AND date <= ? AND " + buildSearchQuery("Payback"), dateFrom, dateTo).orderBy("date DESC").execute();
		    	list.addAll(moneyPaybacks);
	    	
	    	Collections.sort(list, mDateComparator);
	    	return list;
		}

	    static class DateComparator implements Comparator<HyjModel> {
			@Override
			public int compare(HyjModel lhs, HyjModel rhs) {
				Long lhsStr = 0l;
				Long rhsStr = 0l;
				if(lhs instanceof MoneyExpense){
					lhsStr = ((MoneyExpense) lhs).getDate();
				}
				if(rhs instanceof MoneyExpense){
					rhsStr = ((MoneyExpense) rhs).getDate();
				}
				
				if(lhs instanceof MoneyIncome){
					lhsStr = ((MoneyIncome) lhs).getDate();
				}
				if(rhs instanceof MoneyIncome){
					rhsStr = ((MoneyIncome) rhs).getDate();
				}
				
				if(lhs instanceof MoneyExpenseContainer){
					lhsStr = ((MoneyExpenseContainer) lhs).getDate();
				}
				if(rhs instanceof MoneyExpenseContainer){
					rhsStr = ((MoneyExpenseContainer) rhs).getDate();
				}
				
				if(lhs instanceof MoneyIncomeContainer){
					lhsStr = ((MoneyIncomeContainer) lhs).getDate();
				}
				if(rhs instanceof MoneyIncomeContainer){
					rhsStr = ((MoneyIncomeContainer) rhs).getDate();
				}

				if(lhs instanceof MoneyDepositExpenseContainer){
					lhsStr = ((MoneyDepositExpenseContainer) lhs).getDate();
				}
				if(rhs instanceof MoneyDepositExpenseContainer){
					rhsStr = ((MoneyDepositExpenseContainer) rhs).getDate();
				}


				if(lhs instanceof MoneyDepositIncomeContainer){
					lhsStr = ((MoneyDepositIncomeContainer) lhs).getDate();
				}
				if(rhs instanceof MoneyDepositIncomeContainer){
					rhsStr = ((MoneyDepositIncomeContainer) rhs).getDate();
				}

				if(lhs instanceof MoneyDepositReturnContainer){
					lhsStr = ((MoneyDepositReturnContainer) lhs).getDate();
				}
				if(rhs instanceof MoneyDepositReturnContainer){
					rhsStr = ((MoneyDepositReturnContainer) rhs).getDate();
				}
				
				if(lhs instanceof MoneyTransfer){
					lhsStr = ((MoneyTransfer) lhs).getDate();
				}
				if(rhs instanceof MoneyTransfer){
					rhsStr = ((MoneyTransfer) rhs).getDate();
				}
				
				if(lhs instanceof MoneyBorrow){
					lhsStr = ((MoneyBorrow) lhs).getDate();
				}
				if(rhs instanceof MoneyBorrow){
					rhsStr = ((MoneyBorrow) rhs).getDate();
				}
				
				if(lhs instanceof MoneyLend){
					lhsStr = ((MoneyLend) lhs).getDate();
				}
				if(rhs instanceof MoneyLend){
					rhsStr = ((MoneyLend) rhs).getDate();
				}
				
				if(lhs instanceof MoneyReturn){
					lhsStr = ((MoneyReturn) lhs).getDate();
				}
				if(rhs instanceof MoneyReturn){
					rhsStr = ((MoneyReturn) rhs).getDate();
				}
				
				if(lhs instanceof MoneyPayback){
					lhsStr = ((MoneyPayback) lhs).getDate();
				}
				if(rhs instanceof MoneyPayback){
					rhsStr = ((MoneyPayback) rhs).getDate();
				}
				
				return rhsStr.compareTo(lhsStr);
			}
	    } 
	    
		  @Override
		  protected void onAbandon (){
			  super.onAbandon();
//			  this.getContext().getContentResolver().unregisterContentObserver(mChangeObserver);
		  }


		/**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override public void deliverResult(List<HyjModel> objects) {
	        mChildList = objects;

	        if (isStarted() && mChildList != null) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(objects);
	        }
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	        if (mChildList != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            deliverResult(mChildList);
	        }

	        if (takeContentChanged() || mChildList == null) {
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
	    public void onCanceled(List<HyjModel> objects) {
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
	        
	        mChildList = null;
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