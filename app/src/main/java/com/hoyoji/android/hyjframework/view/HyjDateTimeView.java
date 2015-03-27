package com.hoyoji.android.hyjframework.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class HyjDateTimeView extends TextView {
	public HyjDateTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Date mDate;
	private String mDateFormatStr = "HH:mm:ss";
	private DateFormat mDateFormat = new SimpleDateFormat(mDateFormatStr);
	public void setTime(Long timeInMillisec){
		if(timeInMillisec == null){
			setDate(null);
		} else {
			setDate(new Date(timeInMillisec));
		}
	}
	
	public void setDate(Date date){
		mDate = date;
		if(date == null){
			super.setText("");
		} else {
			super.setText(mDateFormat.format(date));
		}
	}
	
	public void setDateFormat(String dateFormatStr){
		if(!mDateFormatStr.equals(dateFormatStr)){
			mDateFormatStr = dateFormatStr;
			mDateFormat = new SimpleDateFormat(dateFormatStr);
		}
	}
	
	public Long getDate(){
		if(mDate == null){
			return null;
		} 
		return mDate.getTime();
	}
	
}
