package com.hoyoji.android.hyjframework.view;

import java.text.DateFormat;
import java.util.Date;

import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjDateTimePickerDialogFragment;
import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HyjDateTimeField extends LinearLayout {
	private String mLabelText;
	private String mEditText;
	private String mHintText;
	
	private TextView mTextViewLabel;
	private TextView mEditTextEdit;

	private Date mDate;
//	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public HyjDateTimeField(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HyjTextField, 0, 0);

		String style;
		String border;
		try {
			mLabelText = a.getString(R.styleable.HyjTextField_labelText);
			mEditText = a.getString(R.styleable.HyjTextField_editText);
			mHintText = a.getString(R.styleable.HyjTextField_hintText);
			style  = a.getString(R.styleable.HyjTextField_style);
			if(style == null){
				style = "";
			}
//			border  = a.getString(R.styleable.HyjTextField_editTextBorder);
//			if(border == null){
//				border = "";
//			}
		} finally {
			a.recycle();
		}
		
		final LayoutInflater inflater = (LayoutInflater)
			       context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.datetime_field, this);
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mEditTextEdit = (TextView)findViewById(R.id.text_field_edit);
		if(style.equals("no_label")){
			mTextViewLabel.setVisibility(GONE);
			mEditTextEdit.setGravity(Gravity.CENTER_HORIZONTAL);
		} else if(style.equals("top_label")){
			this.setOrientation(LinearLayout.VERTICAL);
			this.setGravity(Gravity.CENTER_HORIZONTAL);
			
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mTextViewLabel.setLayoutParams(layoutParams);
			mEditTextEdit.setLayoutParams(layoutParams);
			
			mTextViewLabel.setTextSize(10);
			mTextViewLabel.setTextColor(Color.GRAY);
			mTextViewLabel.setGravity(Gravity.CENTER_HORIZONTAL);
			mEditTextEdit.setGravity(Gravity.CENTER_HORIZONTAL);
		}
		mEditTextEdit.setHint(mHintText);
		if(mEditText != null){
			setTime(Long.valueOf(mEditText));
		} else {
			setDate(new Date());
		}
		
		mEditTextEdit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				HyjActivity activity = ((HyjActivity) getContext());
				if(activity.mDialogFragment != null){
					activity.mDialogFragment.dismiss();
				}
				
				activity.mDialogCallback = new HyjActivity.DialogCallbackListener() {
					@Override
					public void doPositiveClick(Object date) {
						setDate((Date)date);
					}
				};
				
				activity.mDialogFragment = HyjDateTimePickerDialogFragment.newInstance(getContext().getString(R.string.app_please_select) + mLabelText, mDate);
				activity.mDialogFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "dateTimePicker");
			}
		});
		
		mTextViewLabel.setText(mLabelText);
	}

	public void setError(String error){
		mEditTextEdit.setError(error);
	}
	
	public void setTime(Long timeInMillisec){
		if(timeInMillisec == null){
			setDate(null);
		}else{
			setDate(new Date(timeInMillisec));
		}
	}
	
	public void setDate(Date date){
		mDate = date;
		if(date == null){
			mEditTextEdit.setText(null);
		} else {
			DateFormat df = DateFormat.getDateTimeInstance();
			mEditTextEdit.setText(df.format(date));
		}
	}
	
	public Date getDate(){
		return mDate;
	}
	
	
	public Long getTime(){
		if(mDate == null){
			return null;
		} 
		return mDate.getTime();
	}

	public void setEnabled(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
	}
	
	public void setLabel(int resId){
		mTextViewLabel.setText(resId);
	}

	public Long getDateInMillis() {
		if(mDate != null){
			return mDate.getTime();
		}
		return null;
	}

	public void setTextColor(int color) {
		mEditTextEdit.setTextColor(color);
	}

	public void addTextChangedListener(TextWatcher watcher){
		mEditTextEdit.addTextChangedListener(watcher);
	}
}
