package com.hoyoji.android.hyjframework.server;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class HyjJSONListAdapter extends ArrayAdapter<JSONObject> {
    private int[] mViewIds;
    private String[] mFields;
    private int mLayoutResource;
    private ViewBinder mViewBinder;
    
    
    public HyjJSONListAdapter(Context context, int layoutResource, String[] fields, int[] viewIds) {
        super(context, layoutResource);
        mLayoutResource = layoutResource;
        mViewIds = viewIds;
        mFields = fields;
    }

    
//    public void setData(List<JSONObject> data) {
//        clear();
//        if (data != null) {
//	        addData(data);
//        }
//    }

    public void addData(List<JSONObject> data) {
        if (data != null) {
        	for(JSONObject o : data){
        		add(o);
        	}
        }
    }
    
    public void setViewBinder(ViewBinder viewBinder){
    	mViewBinder = viewBinder;
    }
    
    public ViewBinder setViewBinder(){
    	return mViewBinder;
    }
    
    
    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        View[] viewHolder;
        if (view == null) {
        	LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(mLayoutResource, null);
            viewHolder = new View[mViewIds.length];
            for(int i=0; i<mViewIds.length; i++){
            	View v = view.findViewById(mViewIds[i]);
            	viewHolder[i] = v;
            }
            view.setTag(viewHolder);
        } else {
        	viewHolder = (View[])view.getTag();
        }

        JSONObject item = getItem(position);
        for(int i=0; i<mViewIds.length; i++){
        	View v = viewHolder[i];
        	if(this.mViewBinder == null || !mViewBinder.setViewValue(v, item, mFields[i])){
        		((TextView)v).setText(item.optString(mFields[i]));
            }
        }
        
        return view;
    }
}
