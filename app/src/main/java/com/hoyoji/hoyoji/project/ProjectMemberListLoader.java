package com.hoyoji.hoyoji.project;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectRemark;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;


public class ProjectMemberListLoader extends AsyncTaskLoader<List<HyjModel>> {

	/**
	 * Perform alphabetical comparison of application entry objects.
	 */

	    private List<HyjModel> mChildList;
	    private Integer mLoadLimit = null;
	    private ProjectShareAuthorizationComparator mProjectShareAuthorizationComparator = new ProjectShareAuthorizationComparator();
		private String mProjectId;
		private ChangeObserver mChangeObserver;
		public ProjectMemberListLoader(Context context, Bundle queryParams) {
	    	super(context);
	    	
			if (queryParams != null) {
				mLoadLimit = queryParams.getInt("LIMIT", 10);
				mProjectId = queryParams.getString("PROJECTID");
			} else {
				mLoadLimit += 10;
			}
	    	
	    	mChangeObserver = new ChangeObserver();
	    	context.getContentResolver().registerContentObserver(
	    			ContentProvider.createUri(UserData.class, null), true, mChangeObserver);
	    	context.getContentResolver().registerContentObserver(
	    			ContentProvider.createUri(ProjectShareAuthorization.class, null), true, mChangeObserver);

	    }
	    
		
	    
	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<HyjModel> loadInBackground() {

	    	List<HyjModel> list;
    		list = new Select("main.*").from(ProjectShareAuthorization.class).as("main").where("projectId=? AND state <> 'Delete'", mProjectId).orderBy("friendUserId").limit(this.mLoadLimit).execute();
	    	Collections.sort(list, mProjectShareAuthorizationComparator);
	    	return list;
		}

	    static class ProjectShareAuthorizationComparator implements Comparator<HyjModel> {
			@Override
			public int compare(HyjModel lhs, HyjModel rhs) {
				ProjectShareAuthorization lhsProjectShareAuthorization = ((ProjectShareAuthorization) lhs);
				ProjectShareAuthorization rhsProjectShareAuthorization = ((ProjectShareAuthorization) rhs);
				
				if(lhsProjectShareAuthorization.getToBeDetermined()){
					return -1;
				} else if(rhsProjectShareAuthorization.getToBeDetermined()){
					return 1;
				}
				
				if(HyjApplication.getInstance().getCurrentUser().getId().equals(lhsProjectShareAuthorization.getFriendUserId())){
					return -1;
				} else if(HyjApplication.getInstance().getCurrentUser().getId().equals(rhsProjectShareAuthorization.getFriendUserId())){
					return 1;
				}
				
				String lhsStr = lhsProjectShareAuthorization.getFriendDisplayName_pinYin();
				String rhsStr = rhsProjectShareAuthorization.getFriendDisplayName_pinYin();

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
			  this.getContext().getContentResolver().unregisterContentObserver(mChangeObserver);
		  }


		/**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override 
	    public void deliverResult(List<HyjModel> objects) {
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