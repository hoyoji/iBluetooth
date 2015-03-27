package com.hoyoji.android.hyjframework.view;

import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HyjNumericField extends LinearLayout {
	private String mLabelText;
	private String mEditText;
	private String mHintText;
	
	private TextView mTextViewLabel;
	private EditText mEditTextEdit;
	protected boolean mClearOldText;

	public HyjNumericField(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HyjTextField, 0, 0);

		String style;
		String border;
		int color = -1;
		boolean bold;
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
			inflater.inflate(R.layout.numeric_field, this);
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mEditTextEdit = (EditText)findViewById(R.id.text_field_edit);
		if(color != -1){
			mEditTextEdit.setTextColor(color);
			mEditTextEdit.setHintTextColor(color);
		}
		if(bold){
			mEditTextEdit.setTypeface(null, Typeface.BOLD);
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
		mEditTextEdit.setHint(mHintText);
		setText(mEditText);
		mTextViewLabel.setText(mLabelText);
		
		mEditTextEdit.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mClearOldText = true;
				if(hasFocus){
					mEditTextEdit.setSelection(mEditTextEdit.getText().toString().length());
				}
			}
		});
		mEditTextEdit
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == EditorInfo.IME_ACTION_DONE
						|| id == EditorInfo.IME_NULL) {	
					InputMethodManager inputMethodManager =  (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			        inputMethodManager.hideSoftInputFromWindow(mEditTextEdit.getApplicationWindowToken(), 0);
				   	return true;
				}
				return false;
			}
		});
		mEditTextEdit.addTextChangedListener(new TextWatcher() {
			CharSequence oldText = null;
	        @Override
	        public void beforeTextChanged(CharSequence s, int arg1, int arg2,
	                int arg3) {
	        	oldText = s;
	        }
	       
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mClearOldText == true){
					mClearOldText = false;
					if(s != null && s.length() > 0 && oldText != null && oldText.length() > 0){
						mEditTextEdit.setText(s.subSequence(start, start + count));
						mEditTextEdit.setSelection(mEditTextEdit.getText().toString().length());
					}
				}
	        }
	       
	        @Override
	        public void afterTextChanged(Editable s) {
	        }
	    });
	}
	
	public void setOnFocusChangeListener(OnFocusChangeListener l){
		mEditTextEdit.setOnFocusChangeListener(l);
	}
			
	public void setError(String error){
		mEditTextEdit.setError(error);
	}
	
	public void addTextChangedListener(TextWatcher watcher){
		mEditTextEdit.addTextChangedListener(watcher);
	}
	
	private void setText(String text){
		mEditTextEdit.setText(text);
		mClearOldText = true;
		if(mEditTextEdit.getText() != null){
			mEditTextEdit.setSelection(mEditTextEdit.getText().length());
		}
	}
	
	public Editable getText(){
		return mEditTextEdit.getText();
	}
	
	public void setTextViewLabel(String text){
		mTextViewLabel.setText(text);
	}
	
	public void setNumber(Double number){
		if(number == null){
			setText("");
		} else {
			setText(String.format("%.2f", HyjUtil.toFixed2(number)));
		}
	}
	
	public Double getNumber(){
		try{
			return HyjUtil.toFixed2(Double.valueOf(getText().toString()));
		} catch (NumberFormatException e){
			return null;
		}
	}
	
	public void setEnabled(boolean enabled){
		mEditTextEdit.setEnabled(enabled);
	}
	
	public EditText getEditText(){
		return mEditTextEdit;
	}
	
	public void showSoftKeyboard(){
		mEditTextEdit.postDelayed(
			new Runnable() {
			    public void run() {
			        InputMethodManager inputMethodManager =  (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			        inputMethodManager.toggleSoftInputFromWindow(mEditTextEdit.getApplicationWindowToken(),  InputMethodManager.SHOW_IMPLICIT, 0);
//			        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			    }
			}, 500);
	}

	@Override
	protected void onDetachedFromWindow() {
		mEditTextEdit.setText(null);
		mEditTextEdit.setHint("");
		super.onDetachedFromWindow();
	}
	
}
