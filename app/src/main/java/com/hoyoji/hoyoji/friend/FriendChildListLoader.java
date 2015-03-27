package com.hoyoji.hoyoji.friend;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;


public class FriendChildListLoader extends AsyncTaskLoader<List<HyjModel>> {

		private List<HyjModel> mChildList;
	    private Integer mLoadLimit = null;
	    private String mFriendCategoryId = null;
	    
//	    private ChangeObserver mChangeObserver;
	    private FriendComparator mFriendComparator = new FriendComparator();
	    
	    public FriendChildListLoader(Context context, Bundle queryParams) {
	    	super(context);
			if(queryParams != null){
	    		mLoadLimit = queryParams.getInt("LIMIT");
	    		mFriendCategoryId = queryParams.getString("friendCategoryId");
	    	}
//	    	mChangeObserver = new ChangeObserver();
//	    	context.getContentResolver().registerContentObserver(
//	    			ContentProvider.createUri(Friend.class, null), true, mChangeObserver);

	    }
	    
	    
	    
	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<HyjModel> loadInBackground() {
	    	
	    	
			mChildList = new Select().from(Friend.class).where("friendCategoryId=?", mFriendCategoryId).orderBy("nickName_pinYin").execute();
			
	    	Collections.sort(mChildList, mFriendComparator);
	    	return mChildList;
		}
	    
	    static class FriendComparator implements Comparator<HyjModel> {
			@Override
			public int compare(HyjModel lhs, HyjModel rhs) {
				Friend lhsFriend = ((Friend) lhs);
				Friend rhsFriend = ((Friend) rhs);
				
				if(HyjApplication.getInstance().getCurrentUser().getId().equals(lhsFriend.getFriendUserId())){
					return -1;
				} else if(HyjApplication.getInstance().getCurrentUser().getId().equals(rhsFriend.getFriendUserId())){
					return 1;
				}
				
				String lhsStr = lhsFriend.getDisplayName_pinYin();
				String rhsStr = rhsFriend.getDisplayName_pinYin();

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