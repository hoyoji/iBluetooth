package com.hoyoji.android.hyjframework.server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;


public class HyjHttpPostJSONLoader extends AsyncTaskLoader<List<JSONObject>> {

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

	    private List<JSONObject> mJSONList;
	    private String mTarget = null;
	    private String mPostData = "";
	    private Integer errorMsg = null;
		private boolean mIsLoading;
	    
	    public HyjHttpPostJSONLoader(Context context, Bundle queryParams) {
	    	super(context);
	    	if(queryParams != null){
	    		mTarget = queryParams.getString("target");
	    		mPostData = queryParams.getString("postData");
	    	}
	    }
	    
//	    public HyjHttpPostJsonLoader(Context context, String target, String sortByField) {
//	    	super(context);
//	    	mSortByField = sortByField;
//	    	mTarget = target;
//	    }

	    public void changePostQuery(Bundle queryParams){
	    	if(queryParams != null){
	    		mTarget = queryParams.getString("target");
	    		mPostData = queryParams.getString("postData");
	    	}
	    	this.onContentChanged();
	    }
	    
	    
	    /**
	     * This is where the bulk of our work is done.  This function is
	     * called in a background thread and should generate a new set of
	     * data to be published by the loader.
	     */
	    @Override 
	    public List<JSONObject> loadInBackground() {
	        mIsLoading = true;
			if (HyjUtil.hasNetworkConnection()) {
		    	Object object = null;
		    	if(mTarget != null){
		    		object = HyjServer.doHttpPost(this, HyjApplication.getServerUrl()+mTarget+".php", mPostData, false);
		    	}
		    	
				List<JSONObject> list = new ArrayList<JSONObject>();
		        if(object == null){
		        	return null;
		        } else if(object instanceof JSONObject){
		        	list.add((JSONObject) object);
				} else {
					JSONArray array = ((JSONArray)object);
					HyjUtil.flattenJSONArray(array, list);
					return list;
				}
		    } else {
		    	errorMsg = R.string.server_connection_disconnected;
		    }
			return null;
		}

		  /**
	     * Called when there is new data to deliver to the client.  The
	     * super class will take care of delivering it; the implementation
	     * here just adds a little more logic.
	     */
	    @Override public void deliverResult(List<JSONObject> objects) {
	        mJSONList = objects;

	        if(errorMsg != null) {
		    	HyjUtil.displayToast(errorMsg);
		    }
	        if (isStarted()) {
	            // If the Loader is currently started, we can immediately
	            // deliver its results.
	            super.deliverResult(objects);
	        }
	        mIsLoading = false;
	    }

	    /**
	     * Handles a request to start the Loader.
	     */
	    @Override 
	    protected void onStartLoading() {
	        if (mJSONList != null) {
	            // If we currently have a result available, deliver it
	            // immediately.
	            deliverResult(mJSONList);
	        }

	        if (takeContentChanged() || mJSONList == null) {
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
	    public void onCanceled(List<JSONObject> objects) {
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
	        
	        mJSONList = null;
	    }

		public boolean isLoading() {
			return mIsLoading;
		}
}