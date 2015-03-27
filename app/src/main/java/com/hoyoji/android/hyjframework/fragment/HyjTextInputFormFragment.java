package com.hoyoji.android.hyjframework.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.hoyoji.btcontrol.R;

public class HyjTextInputFormFragment extends HyjUserFormFragment {

	private EditText mTextFieldRemark = null;
	boolean mTextCannotbeEmpty = false;
	
	@Override
	public Integer useContentView() {
		return R.layout.textinput_formfragment;
	}

//	@Override
//	public Integer useOptionsMenuView() {
//		return null;
//	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();

		Intent intent = getActivity().getIntent();
		String text = intent.getStringExtra("TEXT");
		String hint = intent.getStringExtra("HINT");
//		String title = intent.getStringExtra("TITLE");
		mTextCannotbeEmpty = intent.getBooleanExtra("NOT_EMPTY", false);
		
		mTextFieldRemark = (EditText) getView().findViewById(
				R.id.textInputFormFragment_textField_remark);
		mTextFieldRemark.setText(text);
		mTextFieldRemark.setHint(hint);
		
		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	 @Override
	 public void onSave(View v){
		 super.onSave(v);
 		
		 if(this.mTextCannotbeEmpty && mTextFieldRemark.getText().toString().isEmpty()){
			 mTextFieldRemark.setError("请输入内容");
			 return;
		 }
		 Intent intent = new Intent();
		 intent.putExtra("TEXT", mTextFieldRemark.getText().toString());
		 getActivity().setResult(Activity.RESULT_OK, intent);
		 
		 getActivity().finish();
	 }
}
