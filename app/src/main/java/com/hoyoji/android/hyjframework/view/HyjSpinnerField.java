package com.hoyoji.android.hyjframework.view;

import com.hoyoji.btcontrol.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class HyjSpinnerField extends LinearLayout {
	private String mLabelText;
	private String mEditText;
	private String mHintText;
	
	private TextView mTextViewLabel;
	private Spinner mEditTextEdit;
	ArrayAdapter<CharSequence> mAdapter;
	String[] mValues;
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public HyjSpinnerField(Context context, AttributeSet attrs) {
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
			border  = a.getString(R.styleable.HyjTextField_editTextBorder);
			if(border == null){
				border = "";
			}
		} finally {
			a.recycle();
		}

		final LayoutInflater inflater = (LayoutInflater)
			       context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.spinner_field, this);
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mEditTextEdit = (Spinner)findViewById(R.id.text_field_edit);
		if(border.equals("none")){
			if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
				mEditTextEdit.setBackground(null);
			} else {
				mEditTextEdit.setBackgroundDrawable(null);
			}
		}		
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
//		mEditTextEdit.setHint(mHintText);
//		mEditTextEdit.setText(mEditText);
		mTextViewLabel.setText(mLabelText);
	}


	public void setItems(int items, String[] values){
		mValues = values;
		// Create an ArrayAdapter using the string array and a default spinner layout
		mAdapter = ArrayAdapter.createFromResource(this.getContext(),
				items, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mEditTextEdit.setAdapter(mAdapter);
	}
	
	public String getSelectedValue(){
		if(mEditTextEdit.getSelectedItemPosition() < 0){
			return null;
		}
		return mValues[mEditTextEdit.getSelectedItemPosition()];
	}
	
	public void setSelectedValue(int position){
		mEditTextEdit.setSelection(position);
	}
	
	public void setSelectedValue(String value){
		int position = -1;
		for(int i=0; i < mValues.length; i++){
			if(mValues[i].equalsIgnoreCase(value)){
				position = i;
				break;
			}
		}
		mEditTextEdit.setSelection(position);
	}
	
	public void setError(String error){
//		mEditTextEdit.setError(error);
	}
	
//	public void setText(String text){
////		mEditTextEdit.setText(text);
//	}
//	
//	public Editable getText(){
////		return mEditTextEdit.getText();
//	}
	
	public void setEnabled(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener){
		mEditTextEdit.setOnItemSelectedListener(onItemSelectedListener);
	}
}
