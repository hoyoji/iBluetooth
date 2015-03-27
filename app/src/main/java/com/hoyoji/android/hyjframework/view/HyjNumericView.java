package com.hoyoji.android.hyjframework.view;

import com.hoyoji.android.hyjframework.HyjUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class HyjNumericView extends TextView {
	public HyjNumericView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Double mNumber;
	private String mPrefix = "";
	private String mSuffix = "";
	
	public void setText(String number){
		if(number != null){
			setNumber(Double.parseDouble(number));
		} else {
			setNumber(null);
		}
	}
	
	public void setNumber(Double number){
		if(number != null){
			mNumber = HyjUtil.toFixed2(number);
		} else {
			mNumber = null;
		}
		super.setText(mPrefix + getText() + mSuffix);
	}
	
	public String getText(){
		if(mNumber == null){
			return "";
		}
		return String.format("%.2f", HyjUtil.toFixed2(mNumber));
	}
	
	public void setPrefix(String prefix){
		if(prefix == null){
			mPrefix = "";
		} else {
			mPrefix = prefix;
		}
	}
	
	
	public void setSuffix(String suffix){
		if(suffix == null){
			mSuffix = "";
		} else {
			mSuffix = suffix;
		}
	}
	
	public void setPrefix(int prefix){
		mPrefix = getContext().getString(prefix);
	}
}
