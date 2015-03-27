package com.hoyoji.android.hyjframework.view;

import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HyjSelectorField extends LinearLayout {
	private String mLabelText;
	private String mEditText;
	private String mHintText;
	private String mModelId;
	
	private TextView mTextViewLabel;
	private TextView mEditTextEdit;

	public HyjSelectorField(Context context, AttributeSet attrs) {
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
			inflater.inflate(R.layout.selector_field, this);
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mEditTextEdit = (TextView)findViewById(R.id.text_field_edit);
//		if(border.equals("none")){
//			if(android.os.Build.VERSION.SDK_INT >= 16){
//				mEditTextEdit.setBackground(null);
//			} else {
//				mEditTextEdit.setBackgroundDrawable(null);
//			}
//		}		
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
		mEditTextEdit.setText(mEditText);
		mTextViewLabel.setText(mLabelText);
		
	}

	public void setError(String error){
		mEditTextEdit.setError(error);
	}
	
	public void setText(String text){
		mEditTextEdit.setError(null);
		mEditTextEdit.setText(text);
	}
	
	public String getText(){
		return (String) mEditTextEdit.getText();
	}
	
	public void setModelId(String modelId){
		mModelId = modelId;
	}

	public String getModelId() {
		return mModelId;
	}
	
	public void setOnClickListener(OnClickListener onClickListener){
		mEditTextEdit.setOnClickListener(onClickListener);
	}
	
	public void setEnabled(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
	}

	public void setLabel(String label) {
		if(label == null){
			mTextViewLabel.setText(mLabelText);
		} else {
			mTextViewLabel.setText(label);
		}
	}

	public String getLabel() {
		return mTextViewLabel.getText().toString();
	}
	
	public TextView getEditText(){
		return mEditTextEdit;
	}

	public CharSequence getHint() {
		return mEditTextEdit.getHint();
	}
	
	public ImageButton showHelpButton(){
//		mEditTextEdit.setPadding(5, 0, 20, 0);
		View button = findViewById(R.id.selector_field_help);
		button.setVisibility(VISIBLE);
		return (ImageButton) button;
	}
	
}
