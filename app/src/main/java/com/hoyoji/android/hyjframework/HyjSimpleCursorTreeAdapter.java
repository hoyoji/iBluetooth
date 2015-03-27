package com.hoyoji.android.hyjframework;

import com.hoyoji.hoyoji.friend.FriendListFragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.SimpleCursorTreeAdapter;


public class HyjSimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {
	public interface OnGetChildrenCursorListener{
		public void onGetChildrenCursor(Cursor groupCursor);
	}
	
	OnGetChildrenCursorListener mGetChildrenCursorListener;
	
	public HyjSimpleCursorTreeAdapter(Context context, Cursor cursor,
			int collapsedGroupLayout, String[] groupFrom, int[] groupTo, int childLayout,
			String[] childFrom, int[] childTo) {
		super(context, cursor, collapsedGroupLayout, groupFrom,
				groupTo, childLayout, childFrom, childTo);
	}

	public void setGetChildrenCursorListener(OnGetChildrenCursorListener getChildrenCursorListener){
		mGetChildrenCursorListener = getChildrenCursorListener;
	}
	
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		mGetChildrenCursorListener.onGetChildrenCursor(groupCursor);
		return null;
	}

	
}
