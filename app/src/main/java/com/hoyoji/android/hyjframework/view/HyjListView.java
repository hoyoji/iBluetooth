package com.hoyoji.android.hyjframework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

public class HyjListView extends ListView {
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;
	private Context mContext;
	private int mMaxYOverscrollDistance;
	final DisplayMetrics metrics;
	private OnOverScrollByListener onOverScrollByListener;
	
	public interface OnOverScrollByListener{
		public void onOverScrollBy(int deltaX, int deltaY, int scrollX,
				int scrollY, int scrollRangeX, int scrollRangeY,
				int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
	}
	
	public HyjListView(Context context) {
		super(context);
		mContext = context;
		metrics = mContext.getResources()
				.getDisplayMetrics();
		initBounceListView();
	}

	public HyjListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		metrics = mContext.getResources()
				.getDisplayMetrics();
		initBounceListView();
	}

	public HyjListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		metrics = mContext.getResources()
				.getDisplayMetrics();
		initBounceListView();
	}

	private void initBounceListView() {
		final float density = metrics.density;
		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
		setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
	}

	public void setOnOverScrollByListener(OnOverScrollByListener onOverScrollByListener){
		this.onOverScrollByListener = onOverScrollByListener;
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		if(onOverScrollByListener != null){
			onOverScrollByListener.onOverScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
		}
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
				scrollRangeX, scrollRangeY, maxOverScrollX,
				mMaxYOverscrollDistance, isTouchEvent);
	}

}
