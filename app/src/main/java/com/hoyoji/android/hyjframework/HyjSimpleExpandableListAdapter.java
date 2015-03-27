package com.hoyoji.android.hyjframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.hoyoji.android.hyjframework.view.HyjNumericView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;


public class HyjSimpleExpandableListAdapter extends BaseExpandableListAdapter {
	private SimpleAdapter.ViewBinder mViewBinder;
	
	private List<? extends Map<String, ?>> mGroupData;
    private int mExpandedGroupLayout;
    private int mCollapsedGroupLayout;
    protected String[] mGroupFrom;
    protected int[] mGroupTo;
    
    private List<? extends List<? extends HyjModel>> mChildData;
    private int mChildLayout;
    private int mLastChildLayout;
    private String[] mChildFrom;
    private int[] mChildTo;
    
    private LayoutInflater mInflater;
    
    public interface OnFetchMoreListener{
    	public void onFetchMore();
    }
    
//    private OnFetchMoreListener mOnFetchMoreListener = null;
    
    /**
     * Constructor
     * 
     * @param context The context where the {@link ExpandableListView}
     *            associated with this {@link SimpleExpandableListAdapter} is
     *            running
     * @param mListGroupData A List of Maps. Each entry in the List corresponds to
     *            one group in the list. The Maps contain the data for each
     *            group, and should include all the entries specified in
     *            "groupFrom"
     * @param groupFrom A list of keys that will be fetched from the Map
     *            associated with each group.
     * @param groupTo The group views that should display column in the
     *            "groupFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the groupFrom parameter.
     * @param groupLayout resource identifier of a view layout that defines the
     *            views for a group. The layout file should include at least
     *            those named views defined in "groupTo"
     * @param childData A List of List of Maps. Each entry in the outer List
     *            corresponds to a group (index by group position), each entry
     *            in the inner List corresponds to a child within the group
     *            (index by child position), and the Map corresponds to the data
     *            for a child (index by values in the childFrom array). The Map
     *            contains the data for each child, and should include all the
     *            entries specified in "childFrom"
     * @param childFrom A list of keys that will be fetched from the Map
     *            associated with each child.
     * @param childTo The child views that should display column in the
     *            "childFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the childFrom parameter.
     * @param childLayout resource identifier of a view layout that defines the
     *            views for a child. The layout file should include at least
     *            those named views defined in "childTo"
     */
    public HyjSimpleExpandableListAdapter(Context context,
            List<Map<String, Object>> mListGroupData, int groupLayout,
            String[] groupFrom, int[] groupTo,
            List<? extends List<? extends HyjModel>> childData,
            int childLayout, String[] childFrom, int[] childTo) {
        this(context, mListGroupData, groupLayout, groupLayout, groupFrom, groupTo, childData,
                childLayout, childLayout, childFrom, childTo);
    }

    /**
     * Constructor
     * 
     * @param context The context where the {@link ExpandableListView}
     *            associated with this {@link SimpleExpandableListAdapter} is
     *            running
     * @param groupData A List of Maps. Each entry in the List corresponds to
     *            one group in the list. The Maps contain the data for each
     *            group, and should include all the entries specified in
     *            "groupFrom"
     * @param groupFrom A list of keys that will be fetched from the Map
     *            associated with each group.
     * @param groupTo The group views that should display column in the
     *            "groupFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the groupFrom parameter.
     * @param expandedGroupLayout resource identifier of a view layout that
     *            defines the views for an expanded group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param collapsedGroupLayout resource identifier of a view layout that
     *            defines the views for a collapsed group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param childData A List of List of Maps. Each entry in the outer List
     *            corresponds to a group (index by group position), each entry
     *            in the inner List corresponds to a child within the group
     *            (index by child position), and the Map corresponds to the data
     *            for a child (index by values in the childFrom array). The Map
     *            contains the data for each child, and should include all the
     *            entries specified in "childFrom"
     * @param childFrom A list of keys that will be fetched from the Map
     *            associated with each child.
     * @param childTo The child views that should display column in the
     *            "childFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the childFrom parameter.
     * @param childLayout resource identifier of a view layout that defines the
     *            views for a child. The layout file should include at least
     *            those named views defined in "childTo"
     */
    public HyjSimpleExpandableListAdapter(Context context,
            List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
            int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
            List<? extends List<? extends HyjModel>> childData,
            int childLayout, String[] childFrom, int[] childTo) {
        this(context, groupData, expandedGroupLayout, collapsedGroupLayout,
                groupFrom, groupTo, childData, childLayout, childLayout,
                childFrom, childTo);
    }

    /**
     * Constructor
     * 
     * @param context The context where the {@link ExpandableListView}
     *            associated with this {@link SimpleExpandableListAdapter} is
     *            running
     * @param groupData A List of Maps. Each entry in the List corresponds to
     *            one group in the list. The Maps contain the data for each
     *            group, and should include all the entries specified in
     *            "groupFrom"
     * @param groupFrom A list of keys that will be fetched from the Map
     *            associated with each group.
     * @param groupTo The group views that should display column in the
     *            "groupFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the groupFrom parameter.
     * @param expandedGroupLayout resource identifier of a view layout that
     *            defines the views for an expanded group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param collapsedGroupLayout resource identifier of a view layout that
     *            defines the views for a collapsed group. The layout file
     *            should include at least those named views defined in "groupTo"
     * @param childData A List of List of Maps. Each entry in the outer List
     *            corresponds to a group (index by group position), each entry
     *            in the inner List corresponds to a child within the group
     *            (index by child position), and the Map corresponds to the data
     *            for a child (index by values in the childFrom array). The Map
     *            contains the data for each child, and should include all the
     *            entries specified in "childFrom"
     * @param childFrom A list of keys that will be fetched from the Map
     *            associated with each child.
     * @param childTo The child views that should display column in the
     *            "childFrom" parameter. These should all be TextViews. The
     *            first N views in this list are given the values of the first N
     *            columns in the childFrom parameter.
     * @param childLayout resource identifier of a view layout that defines the
     *            views for a child (unless it is the last child within a group,
     *            in which case the lastChildLayout is used). The layout file
     *            should include at least those named views defined in "childTo"
     * @param lastChildLayout resource identifier of a view layout that defines
     *            the views for the last child within each group. The layout
     *            file should include at least those named views defined in
     *            "childTo"
     */
    public HyjSimpleExpandableListAdapter(Context context,
            List<? extends Map<String, ?>> groupData, int expandedGroupLayout,
            int collapsedGroupLayout, String[] groupFrom, int[] groupTo,
            List<? extends List<? extends HyjModel>> childData,
            int childLayout, int lastChildLayout, String[] childFrom,
            int[] childTo) {
        mGroupData = groupData;
        mExpandedGroupLayout = expandedGroupLayout;
        mCollapsedGroupLayout = collapsedGroupLayout;
        mGroupFrom = groupFrom;
        mGroupTo = groupTo;
        
        mChildData = childData;
        mChildLayout = childLayout;
        mLastChildLayout = lastChildLayout;
        mChildFrom = childFrom;
        mChildTo = childTo;
        
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public Object getChild(int groupPosition, int childPosition) {
        return mChildData.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return ((HyjModel)getChild(groupPosition, childPosition)).get_mId();
    }

//    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
//            View convertView, ViewGroup parent) {
//        View v;
//        if (convertView == null) {
//            v = newChildView(isLastChild, parent);
//        } else {
//            v = convertView;
//        }
//        bindView(v, mChildData.get(groupPosition).get(childPosition), mChildFrom, mChildTo);
//        return v;
//    }

    /**
     * Instantiates a new View for a child.
     * @param isLastChild Whether the child is the last child within its group.
     * @param parent The eventual parent of this new View.
     * @return A new child View
     */
    public View newChildView(boolean isLastChild, ViewGroup parent) {
        return mInflater.inflate((isLastChild) ? mLastChildLayout : mChildLayout, parent, false);
    }
    
    private void bindView(View view, Map<String, ?> data, String[] from, int[] to) {
        int len = to.length;

        for (int i = 0; i < len; i++) {
            View v = view.findViewById(to[i]);
            if (v != null) {
            	if(v instanceof HyjNumericView){
            		((HyjNumericView)v).setNumber(Double.valueOf(data.get(from[i]).toString()));
            	} else if(v instanceof TextView){
            		((TextView)v).setText((String)data.get(from[i]));
            	}
            }
        }
    }

    public int getChildrenCount(int groupPosition) {
    	if(mChildData.size() <= groupPosition || mChildData.get(groupPosition) == null){
    		return 0;
    	}
        return mChildData.get(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
    	return mGroupData.get(groupPosition);
    }

    public int getGroupCount() {
        return mGroupData.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newGroupView(isExpanded, parent);
        } else {
            v = convertView;
        }
        bindView(v, (Map<String, ?>) mGroupData.get(groupPosition), mGroupFrom, mGroupTo);
        
        return v;
    }

    /**
     * Instantiates a new View for a group.
     * @param isExpanded Whether the group is currently expanded.
     * @param parent The eventual parent of this new View.
     * @return A new group View
     */
    public View newGroupView(boolean isExpanded, ViewGroup parent) {
        return mInflater.inflate((isExpanded) ? mExpandedGroupLayout : mCollapsedGroupLayout,
                parent, false);
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    
	public void setViewBinder(SimpleAdapter.ViewBinder viewBinder){
		mViewBinder = viewBinder;
	}
	
//	public void setOnFetchMoreListener(OnFetchMoreListener onFetchMoreListener){
//		mOnFetchMoreListener = onFetchMoreListener;
//	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if(mViewBinder == null){
//			return super.getChildView(groupPosition, childPosition, isLastChild,
//					convertView, parent);
		}
		View view = convertView;
		View[] viewHolder;
        if (view == null) {
        	//LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = newChildView(isLastChild, parent);
            viewHolder = new View[mChildTo.length];
            for(int i=0; i<mChildTo.length; i++){
            	View v = view.findViewById(mChildTo[i]);
            	viewHolder[i] = v;
            }
            view.setTag(viewHolder);
        } else {
        	viewHolder = (View[])view.getTag();
        }

		Object data = this.getChild(groupPosition, childPosition); 
        for(int i=0; i<mChildFrom.length; i++){
        	View v = viewHolder[i];
        	if(!mViewBinder.setViewValue(v, data, mChildFrom[i])){
        		((TextView)v).setText(data.toString());
            }
        }

//      if(groupPosition == mGroupData.size()-1 && isLastChild){
//      	if(this.mOnFetchMoreListener != null){
//      		this.mOnFetchMoreListener.onFetchMore();
//      	}
//      }
        return view;
	}

}
