package com.hoyoji.android.hyjframework.view;

import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HyjRemarkField extends LinearLayout {
	private String mLabelText;
	private String mEditText;
	private String mHintText;

	private int mMinLines;
	
	private TextView mTextViewLabel;
	private EditText mEditTextEdit;

	public HyjRemarkField(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HyjTextField, 0, 0);

		String style;
		String border;
		try {
			mLabelText = a.getString(R.styleable.HyjTextField_labelText);
			mEditText = a.getString(R.styleable.HyjTextField_editText);
			mHintText = a.getString(R.styleable.HyjTextField_hintText);
			mMinLines = a.getInt(R.styleable.HyjTextField_minLines, 1);
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
			inflater.inflate(R.layout.remark_field, this);
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mEditTextEdit = (EditText)findViewById(R.id.text_field_edit);
		mEditTextEdit.setMinLines(mMinLines);
		if(style.equals("no_label")){
			mTextViewLabel.setVisibility(GONE);
//			mEditTextEdit.setGravity(Gravity.CENTER_HORIZONTAL);
		} else if(style.equals("top_label")){
			this.setOrientation(LinearLayout.VERTICAL);
			this.setGravity(Gravity.CENTER_HORIZONTAL);
			
			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
			mTextViewLabel.setLayoutParams(layoutParams);
			mEditTextEdit.setLayoutParams(layoutParams);
			mEditTextEdit.setPadding(5, 5, 5, 5);
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
		if(text == null || "".equals(text)){
			mEditTextEdit.setGravity(Gravity.CENTER_HORIZONTAL);
		} else {
			mEditTextEdit.setGravity(Gravity.LEFT);
		}
		mEditTextEdit.setText(text);
	}
	
	public String getText(){
		return mEditTextEdit.getText().toString();
	}

	public String getLabelText(){
		return mTextViewLabel.getText().toString();
	}
	
	public void setEnabled(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
	}

	public void setEditable(boolean editable){
		mEditTextEdit.setFocusable(editable);
//		mEditTextEdit.setClickable(!editable);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l){
		mEditTextEdit.setOnClickListener(l);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		mEditTextEdit.setText(null);
		mEditTextEdit.setHint("");
		super.onDetachedFromWindow();
	}
}
