package com.hoyoji.hoyoji.setting;

import android.view.WindowManager;
import android.widget.EditText;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.btcontrol.R;


public class ChangeNickNameFormFragment extends HyjUserFormFragment {
	private EditText mEditTextNickName = null;
	String mNickName = "";
	boolean mHasError = false;
	
	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_changenickname;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		mEditTextNickName = (EditText) getView().findViewById(R.id.changeNickNameFormFragment_editText_nickName);
		mEditTextNickName.setText(HyjApplication.getInstance().getCurrentUser().getNickName());
//		getView().findViewById(R.id.changeNickNameFormFragment_button_onSave).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				fillData();
//				if(!validateData()){
//					HyjUtil.displayToast(R.string.app_validation_error);
//				} else {	
////					changeNickName_submit(v);
//				}
//			}
//		});
		
		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	private void fillData(){
		mNickName = mEditTextNickName.getText().toString();
	}
	
	public boolean validateData(){
		boolean validatePass = true;
		
		if(mNickName.length() == 0){
			mEditTextNickName.setError(getString(R.string.changeNickNameFormFragment_validation_wrong_nickName));
	   		validatePass = false;
		}else{
			mEditTextNickName.setError(null);
		}
	
		return validatePass;
	}
	
	public void onSave(){
		super.onSave();
		fillData();
		if(!validateData()){
			HyjUtil.displayToast(R.string.app_validation_error);
		} else {	
//			changeNickName_submit(null);
			HyjApplication.getInstance().getCurrentUser().setNickName(mNickName);
			HyjApplication.getInstance().getCurrentUser().save();
			HyjUtil.displayToast(R.string.app_save_success);
			getActivity().finish();
		}
	}
	
//	private void changeNickName_submit(View v){
//			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
//				@Override
//				public void finishCallback(Object object) {
//					
//					HyjApplication.getInstance().getCurrentUser().setNickName(mNickName);
//					HyjApplication.getInstance().getCurrentUser().setSyncFromServer(true);
//					
//
//					((HyjActivity) ChangeNickNameFragment.this.getActivity()).dismissProgressDialog();
//					HyjUtil.displayToast(R.string.app_save_success);
//					getActivity().finish();
//				}
//
//				@Override
//				public void errorCallback(Object object) {
//					((HyjActivity) ChangeNickNameFragment.this.getActivity()).dismissProgressDialog();
//					
//					JSONObject json = (JSONObject) object;
//					HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
//				}
//			};
//
//			try {
//				JSONObject data = new JSONObject();
//				data.put("id", HyjApplication.getInstance().getCurrentUser().getId());
//				data.put("__dataType", "User");
//				data.put("nickName", mNickName);
//				
//				HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString() , "putData");
//				
//				((HyjActivity) this.getActivity()).displayProgressDialog(
//								R.string.changeNickNameFormFragment_title,
//								R.string.changeNickNameFormFragment_toast_changing);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//	}

	 
}
