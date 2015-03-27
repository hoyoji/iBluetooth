package com.hoyoji.android.hyjframework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class HyjExpandableListView extends ExpandableListView {
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;
	private Context mContext;
	private int mMaxYOverscrollDistance;
	final DisplayMetrics metrics;
	private OnOverScrollByListener onOverScrollByListener;

	public interface OnOverScrollByListener {
		public void onOverScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
	}

	public HyjExpandableListView(Context context) {
		super(context);
		mContext = context;
		metrics = mContext.getResources().getDisplayMetrics();
		initBounceListView();
	}

	public HyjExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		metrics = mContext.getResources().getDisplayMetrics();
		initBounceListView();
	}

	public HyjExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		metrics = mContext.getResources().getDisplayMetrics();
		initBounceListView();
	}

	private void initBounceListView() {
		final float density = metrics.density;
		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
		setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
	}

	public void setOnOverScrollByListener(
			OnOverScrollByListener onOverScrollByListener) {
		this.onOverScrollByListener = onOverScrollByListener;
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if (onOverScrollByListener != null) {
			onOverScrollByListener.onOverScrollBy(deltaX, deltaY, scrollX,
					scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
					maxOverScrollY, isTouchEvent);
		}
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX,
				mMaxYOverscrollDistance, isTouchEvent);
	}

//	@Override
//	public void setItemChecked(int position, boolean value) {
//		if (getChoiceMode() == CHOICE_MODE_NONE) {
//			return;
//		}
//		
//		super.setItemChecked(position, value);
//
////		if (getChoiceMode() == CHOICE_MODE_MULTIPLE
////				|| getChoiceMode() == CHOICE_MODE_MULTIPLE_MODAL) {
////				if (value) {
////					mCheckedIdStates
////							.put(getAdapter().getItemId(position), position);
////				} else {
////					mCheckedIdStates.delete(getAdapter().getItemId(position));
////				}
////		} else {
////			// this may end up selecting the value we just cleared but this way
////			// we ensure length of mCheckStates is 1, a fact
////			// getCheckedItemPosition relies on
////			if (value) {
////					mCheckedIdStates
////							.put(mAdapter.getItemId(position), position);
////			} 
////		}
//
//		if (getAdapter().hasStableIds()) {
//			if(this.getItemIdAtPosition(position) != -1){
//				this.setItemId
//			}
//		}
//	}

	public int getCheckedItemCount(){
		return super.getCheckedItemIds().length;
	}
	
	@Override
	public long[] getCheckedItemIds() {
		         if (getChoiceMode() == CHOICE_MODE_NONE || getExpandableListAdapter() == null) {
		             return new long[0];
		         }
		 
		         SparseBooleanArray checkedItems = this.getCheckedItemPositions();
		         
		         final long[] ids = new long[super.getCheckedItemIds().length];
		         
		         ExpandableListAdapter adapter = this.getExpandableListAdapter();
		         int checkedCount = 0;
//		         for (int pos = 0; pos < count; pos++) {
//		             if(checkedItems.get(pos)){
//		            	long packedPosition = this.getExpandableListPosition(pos);
//		            	ids[checkedCount] = adapter.getChildId(this.get, childPosition);
//		            	checkedCount++;
//		             }
//		         }
		 
		         for(int g = 0; g < this.getExpandableListAdapter().getGroupCount(); g++){
						for(int c = 0; c < this.getExpandableListAdapter().getChildrenCount(g); c++){
							int position = this.getFlatListPosition(ExpandableListView.getPackedPositionForChild(g, c));
							if(checkedItems.get(position)){
								ids[checkedCount] = adapter.getChildId(g, c);
								checkedCount++;
							}
						}
					}
		         return ids;
		     }


}
