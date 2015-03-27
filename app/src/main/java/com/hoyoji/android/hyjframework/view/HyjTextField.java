package com.hoyoji.android.hyjframework.view;

import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HyjTextField extends LinearLayout {
	private String mLabelText;
	private String mEditText;
	private String mHintText;
	
	private TextView mTextViewLabel;
	private EditText mEditTextEdit;

	public HyjTextField(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HyjTextField, 0, 0);
		
		String style;
		int color = -1;
		boolean bold;
		String border;
		try {
			mLabelText = a.getString(R.styleable.HyjTextField_labelText);
			mEditText = a.getString(R.styleable.HyjTextField_editText);
			mHintText = a.getString(R.styleable.HyjTextField_hintText);
			style  = a.getString(R.styleable.HyjTextField_style);
			if(style == null){
				style = "";
			}
			color  = a.getColor(R.styleable.HyjTextField_editTextColor, -1);
			bold  = a.getBoolean(R.styleable.HyjTextField_editTextBold, false);
//			border  = a.getString(R.styleable.HyjTextField_editTextBorder);
//			if(border == null){
//				border = "";
//			}
		} finally {
			a.recycle();
		}
		
		final LayoutInflater inflater = (LayoutInflater)
			       context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.text_field, this);
			
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mEditTextEdit = (EditText)findViewById(R.id.text_field_edit);
		if(color != -1){
			mEditTextEdit.setTextColor(color);
		}
		if(bold){
			mEditTextEdit.setTypeface(null, Typeface.BOLD);
		}
		if(style.equals("no_label")){
			mTextViewLabel.setVisibility(GONE);
			this.setGravity(Gravity.CENTER_HORIZONTAL);
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
		mEditTextEdit.setText(mEditText);
		mTextViewLabel.setText(mLabelText);
		
		mEditTextEdit.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mEditTextEdit.setSelection(mEditTextEdit.getText().toString().length());
				}
			}
		});
		
	}

	public void setError(String error){
		mEditTextEdit.setError(error);
	}
	
	public void setText(String text){
		mEditTextEdit.setText(text);
		if(text != null){
			mEditTextEdit.setSelection(text.length());
		}
	}
	
	public String getText(){
		return mEditTextEdit.getText().toString();
	}

	public void setEnabled(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
	}
	
	public void setEditable(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
		if(enabled == false){
			mEditTextEdit.setTextColor(Color.BLACK);
		}
	}

	public void setLabel(int resId){
		mTextViewLabel.setText(resId);
	}
	

	public void setHint(int resId){
		mEditTextEdit.setHint(resId);
	}
	
	public void showSoftKeyboard(){
		mEditTextEdit.post(
			new Runnable() {
			    public void run() {
			        InputMethodManager inputMethodManager =  (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			        inputMethodManager.toggleSoftInputFromWindow(mEditTextEdit.getApplicationWindowToken(),  InputMethodManager.SHOW_IMPLICIT, 0);
			    }
			});
	}
	
	@Override
	protected void onDetachedFromWindow() {
		mEditTextEdit.setText(null);
		mEditTextEdit.setHint("");
		super.onDetachedFromWindow();
	}

	public void setTextColor(int color) {
		mEditTextEdit.setTextColor(color);
	}

	public void setHint(String hint) {
		mEditTextEdit.setHint(hint);
	}
	
}
