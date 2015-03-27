package com.hoyoji.android.hyjframework.view;

import com.hoyoji.btcontrol.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

public class HyjBooleanView extends TextView {
	public HyjBooleanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HyjTextField, 0, 0);

		try {
			mTrueText = a.getString(R.styleable.HyjTextField_trueText);
			mFalseText = a.getString(R.styleable.HyjTextField_falseText);
		} finally {
			a.recycle();
		}
	}

	private Boolean mBoolean;
	private String mTrueText;
	private String mFalseText;
	
	public void setBoolean(Boolean value){
		mBoolean = value;
		this.setText(getText());
	}
	
	public void setBoolean(int value){
		if(value == 0){
			mBoolean = false;
		} else {
			mBoolean = true;
		}
		this.setText(getText());
	}
	
	public String getText(){
		if(mBoolean != null && mBoolean){
			return mTrueText;
		} else {
			return mFalseText;
		}
	}
	
	public Boolean getBoolean(){
		return mBoolean;
	}
}
