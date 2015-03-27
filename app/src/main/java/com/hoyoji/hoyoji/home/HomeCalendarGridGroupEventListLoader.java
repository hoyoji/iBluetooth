package com.hoyoji.hoyoji.home;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;

public class HomeCalendarGridGroupEventListLoader extends
		AsyncTaskLoader<List<Map<String, Object>>> {

	private List<Map<String, Object>> mGroupList;
	private long mStartDateInMillis = -1;
	private long mEndDateInMillis = -1;
	private ChangeObserver mChangeObserver;
	private boolean mIsLoading;

	public HomeCalendarGridGroupEventListLoader(Context context, Bundle queryParams) {
		super(context);
		if (queryParams != null) {
			mStartDateInMillis = queryParams.getLong("startDateInMillis", -1);
			mEndDateInMillis = queryParams.getLong("endDateInMillis", -1);
		}

		mChangeObserver = new ChangeObserver();
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(Event.class, null), true,
				mChangeObserver);
		context.getContentResolver().registerContentObserver(
				ContentProvider.createUri(EventMember.class, null), true,
				mChangeObserver);
	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<Map<String, Object>> loadInBackground() {
		mIsLoading = true;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Calendar calToday = Calendar.getInstance();
		calToday.setTimeInMillis(mStartDateInMillis);
		calToday.set(Calendar.HOUR_OF_DAY, 0);
		calToday.clear(Calendar.MINUTE);
		calToday.clear(Calendar.SECOND);
		calToday.clear(Calendar.MILLISECOND);
		
// get start of this week in milliseconds
//		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		// cal.add(Calendar.WEEK_OF_YEAR, -1);

		while (calToday.getTimeInMillis() <= mEndDateInMillis) {
			int count = 0;
			String[] args = new String[] {String.valueOf(calToday.getTimeInMillis()), String.valueOf(calToday.getTimeInMillis() + 24 * 3600000) };
			
			Cursor cursor = Cache
					.openDatabase()
					.rawQuery(
							"SELECT COUNT(*) AS count, * FROM EVENT "
									+ "WHERE startDate > ? AND startDate <= ? ", args);
			if (cursor != null) {
				cursor.moveToFirst();
				count += cursor.getDouble(0);
				cursor.close();
				cursor = null;
			}
			HashMap<String, Object> groupObject = new HashMap<String, Object>();
			groupObject.put("count", count);
			groupObject.put("dateInMilliSeconds", calToday.getTimeInMillis());
			list.add(groupObject);
		
			calToday.add(Calendar.DAY_OF_YEAR, 1);
		}
		return list;
	}
	
	public boolean isLoading(){
		return mIsLoading;
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
		mIsLoading = false;
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
//		super.onStartLoading();
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
//		super.onStopLoading();
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

//	/**
//	 * Handles a request to cancel a load.
//	 */
//	@Override
//	public void onCanceled(List<Map<String, Object>> objects) {
//		super.onCanceled(objects);
//	}

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
