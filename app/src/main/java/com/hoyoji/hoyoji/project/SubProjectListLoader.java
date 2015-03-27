package com.hoyoji.hoyoji.project;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectRemark;
import com.hoyoji.hoyoji.models.UserData;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;


public class SubProjectListLoader extends AsyncTaskLoader<List<HyjModel>> {

	/**
	 * Perform alphabetical comparison of application entry objects.
	 */

	    private List<HyjModel> mChildList;
	    private Integer mLoadLimit = null;
	    private ProjectComparator mProjectComparator = new ProjectComparator();
		private String mParentProjectId;
		private ChangeObserver mChangeObserver;
		public SubProjectListLoader(Context context, Bundle queryParams) {
	    	super(context);
	    	
			if (queryParams != null) {
				mLoadLimit = queryParams.getInt("LIMIT", 10);
				mParentProjectId = queryParams.getString("PARENT_PROJECTID");
			} else {
				mLoadLimit += 10;
			}
	    	
	    	mChangeObserver = new ChangeObserver();
	    	context.getContentResolver().registerContentObserver(
	    			ContentProvider.createUri(UserData.class, null), true, mChangeObserver);
	    	context.getContentResolver().registerContentObserver(
	    			ContentProvider.createUri(Project.class, null), true, mChangeObserver);

	    }
	    
		
	    
	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<HyjModel> loadInBackground() {

	    	List<HyjModel> list;
	    	if(mParentProjectId != null){
	    		list = new Select("main.*").from(Project.class).as("main").where("id IN (SELECT subProjectId FROM ParentProject WHERE parentProjectId = ?)", mParentProjectId).orderBy("name_pinYin").limit(this.mLoadLimit).execute();
	    	} else {
	    		list = new Select("main.*, id AS _subProjectId").from(Project.class).as("main").where("NOT EXISTS (SELECT id FROM ParentProject WHERE subProjectId = _subProjectId) OR EXISTS (SELECT id FROM ParentProject WHERE subProjectId = _subProjectId AND parentProjectId IS NULL)").orderBy("name_pinYin").limit(this.mLoadLimit).execute();
		    		
	    	}
	    	
	    	Collections.sort(list, mProjectComparator);
	    	return list;
		}

	    static class ProjectComparator implements Comparator<HyjModel> {
			@Override
			public int compare(HyjModel lhs, HyjModel rhs) {
				String lhsStr = ((Project) lhs).getDisplayName_pinYin();
				String rhsStr = ((Project) rhs).getDisplayName_pinYin();

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