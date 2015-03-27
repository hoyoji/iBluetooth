package com.hoyoji.hoyoji.money.moneyaccount;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.hoyoji.models.MoneyAccount;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;


public class MoneyAccountChildListLoader extends AsyncTaskLoader<List<HyjModel>> {

		private List<HyjModel> mChildList;
	    private Integer mLoadLimit = null;
	    private String mAccountType = null;
	    
	    private String mExcludeType = null;
//	    private ChangeObserver mChangeObserver;
		private String mFriendId;
	    private MoneyAccountComparator mMoneyAccountComparator = new MoneyAccountComparator();
	    
	    public MoneyAccountChildListLoader(Context context, Bundle queryParams) {
	    	super(context);
			if(queryParams != null){
	    		mLoadLimit = queryParams.getInt("LIMIT");
	    		mAccountType = queryParams.getString("accountType");
	    		mExcludeType = queryParams.getString("excludeType");
	    		mFriendId = queryParams.getString("localFriendId");
	    	}
//	    	mChangeObserver = new ChangeObserver();
//	    	context.getContentResolver().registerContentObserver(
//	    			ContentProvider.createUri(MoneyExpense.class, null), true, mChangeObserver);
//	    	context.getContentResolver().registerContentObserver(
//	    			ContentProvider.createUri(MoneyIncome.class, null), true, mChangeObserver);

	    }
	    

	    public void changeQuery(Bundle queryParams){
	    	if(queryParams != null){
	    		mLoadLimit = queryParams.getInt("LIMIT");
	    		mAccountType = queryParams.getString("accountType");
	    		mExcludeType = queryParams.getString("excludeType");
	    		mFriendId = queryParams.getString("localFriendId");
	    	}
	    	this.onContentChanged();
	    }
	    
	    
	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<HyjModel> loadInBackground() {
	    	
	    	if(mAccountType.equals("AutoHide")){
	    		if(!"Debt".equalsIgnoreCase(mExcludeType)){
					if(mFriendId == null){
						mChildList = new Select().from(MoneyAccount.class).where("accountType=? AND (autoHide = 'Hide' OR autoHide = 'Auto' AND currentBalance = 0)", "Debt").orderBy("name_pinYin ASC").execute();
					} else {
						mChildList = new Select().from(MoneyAccount.class).where("accountType=? AND localFriendId = ? AND (autoHide = 'Hide' OR autoHide = 'Auto' AND currentBalance = 0)", "Debt", mFriendId).orderBy("name_pinYin ASC").execute();
					}
				}
	    	} else if(mAccountType.equals("Debt")){
	    		if(!"Debt".equalsIgnoreCase(mExcludeType)){
					if(mFriendId == null){
						mChildList = new Select().from(MoneyAccount.class).where("accountType=? AND (autoHide = 'Show' OR autoHide = 'Auto' AND currentBalance <> 0)", "Debt").orderBy("name_pinYin ASC").execute();
					} else {
						mChildList = new Select().from(MoneyAccount.class).where("accountType=? AND localFriendId = ? AND (autoHide = 'Show' OR autoHide = 'Auto' AND currentBalance <> 0)", "Debt", mFriendId).orderBy("name_pinYin ASC").execute();
					}
				}
	    	} else {
				if(!mAccountType.equalsIgnoreCase(mExcludeType)){
					if(mFriendId == null){
						mChildList = new Select().from(MoneyAccount.class).where("accountType=?", mAccountType).orderBy("name_pinYin ASC").execute();
					} else {
						mChildList = new Select().from(MoneyAccount.class).where("accountType=? AND localFriendId = ?", mAccountType, mFriendId).orderBy("name_pinYin ASC").execute();
					}
				}
	    	}
	    	Collections.sort(mChildList, mMoneyAccountComparator);
	    	return mChildList;
		}
	    
	    static class MoneyAccountComparator implements Comparator<HyjModel> {
			@Override
			public int compare(HyjModel lhs, HyjModel rhs) {
				MoneyAccount lhsMoneyAccount = ((MoneyAccount) lhs);
				MoneyAccount rhsMoneyAccount = ((MoneyAccount) rhs);
				
				if(lhsMoneyAccount.getLocalFriend() != null && lhsMoneyAccount.getLocalFriend().getToBeDetermined()){
					return -1;
				} else if(rhsMoneyAccount.getLocalFriend() != null && rhsMoneyAccount.getLocalFriend().getToBeDetermined()){
					return 1;
				}
				
				String lhsStr = lhsMoneyAccount.getDisplayName_pinYin();
				String rhsStr = rhsMoneyAccount.getDisplayName_pinYin();

				if(lhsStr == null){
					lhsStr = "";
				}
				if(rhsStr == null){
					rhsStr = "";
				}
								
				return lhsStr.compareTo(rhsStr);
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
	    
//	    private class ChangeObserver extends ContentObserver {
//	        public ChangeObserver() {
//	            super(new Handler());
//	        }
//
//	        @Override
//	        public boolean deliverSelfNotifications() {
//	            return true;
//	        }
//
//	        @Override
//	        public void onChange(boolean selfChange) {
//	            onContentChanged();
//	        }
//	    }
	    
}