package com.hoyoji.android.hyjframework.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

public class HyjCalendarGrid extends GridView {
	private HyjCalendarGridAdapter mCalendarGridAdapter;
	private	Resources r = getResources();
	
	public HyjCalendarGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setNumColumns(7);
		this.setGravity(Gravity.CENTER);
//		this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
	
	
	
	public HyjCalendarGridAdapter getAdapter(){
		return mCalendarGridAdapter;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter){
		mCalendarGridAdapter = (HyjCalendarGridAdapter) adapter;
		super.setAdapter(adapter);
	}

}
