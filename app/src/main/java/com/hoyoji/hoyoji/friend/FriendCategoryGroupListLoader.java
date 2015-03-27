package com.hoyoji.hoyoji.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.FriendCategory;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public class FriendCategoryGroupListLoader extends
		AsyncTaskLoader<List<Map<String, Object>>> {

	private List<Map<String, Object>> mGroupList;
	private Integer mLoadLimit = 10;
	private boolean mHasMoreData = true;
	private ChangeObserver mChangeObserver;

	public FriendCategoryGroupListLoader(Context context, Bundle queryParams) {
		super(context);
		if (queryParams != null) {
			mLoadLimit = queryParams.getInt("LIMIT", 10);
		}

		mChangeObserver = new ChangeObserver();
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(FriendCategory.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(Friend.class, null), true,
				mChangeObserver);

	}

	public void fetchMore(Bundle queryParams) {
		if (queryParams != null) {
			mLoadLimit = queryParams.getInt("LIMIT", 10);
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
		

		int loadCount = 0;
		Cursor cursor = Cache
				.openDatabase()
				.rawQuery(
						"SELECT C._id, C.id, C.name, COUNT(F.id) as count FROM FriendCategory C LEFT JOIN Friend F ON F.friendCategoryId = C.id " +
						"GROUP BY C.id ORDER BY name_pinYin",
						null);
		
		
		if (cursor != null) {
			cursor.moveToFirst();
			if(!cursor.isAfterLast()){
				do{
					HashMap<String, Object> groupObject = new HashMap<String, Object>();
					groupObject.put("_id", cursor.getLong(0));
					groupObject.put("friendCategoryId", cursor.getString(1));
					groupObject.put("friendCategoryName", cursor.getString(2));
					groupObject.put("count", cursor.getInt(3));
					list.add(groupObject);
					loadCount ++;
				} while(cursor.moveToNext());
			}
			cursor.close();
			cursor = null;
		}

		mHasMoreData = false;
		
//		Collections.sort(list,new Comparator<Map<String, Object>>(){
//			@Override
//			public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
//				return (int) ((Long)lhs.get("dateInMilliSeconds") - (Long)rhs.get("dateInMilliSeconds"));
//			}
//		});
		return list;
	}
	
	
	public boolean hasMoreData() {
		return mHasMoreData;
	}

	public List<Map<String, Object>> getGroupList(){
		return mGroupList;
	}
	
	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
//	@Override
//	public void deliverResult(List<Map<String, Object>> objects) {
//		if(mGroupList == null){
//			mGroupList = objects;
//		} else {
//			mGroupList.addAll(objects);
//		}
//
//		if (isStarted() && mGroupList != null) {
//			// If the Loader is currently started, we can immediately
//			// deliver its results.
//			super.deliverResult(objects);
//		}
//	}
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