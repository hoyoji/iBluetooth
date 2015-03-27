package com.hoyoji.hoyoji.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.UserData;


public class ChangePasswordFragment extends HyjUserFragment {
	private EditText mEditTextOldPassword = null;
	private EditText mEditTextNewPassword1 = null;
	private EditText mEditTextNewPassword2 = null;
	String mOldPassword = "";
	String mNewPassword1 = "";
	String mNewPassword2 = "";
	boolean mHasError = false;
	
	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_changepassword;
	}
	 
	@Override
	public void onInitViewData(){
		
		if(!HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword()){
			getView().findViewById(R.id.changePasswordFragment_linearLayout_oldPassword).setVisibility(View.GONE);
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("设置密码");
		} else {
			mEditTextOldPassword = (EditText) getView().findViewById(R.id.changePasswordFragment_editText_oldPassword);
		}
		
		mEditTextNewPassword1 = (EditText) getView().findViewById(R.id.changePasswordFragment_editText_newPassword1);
		mEditTextNewPassword2 = (EditText) getView().findViewById(R.id.changePasswordFragment_editText_newPassword2);
		mEditTextNewPassword2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.imeAction_changepassword || id == EditorInfo.IME_ACTION_DONE) {
							onSave();
							return true;
						}
						return false;
					}
				});
		
		getView().findViewById(R.id.changePasswordFragment_button_onSave).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fillData();
				if(!validateData()){
					HyjUtil.displayToast(R.string.app_validation_error);
				} else {	
					changePassword_submit(v);
				}
			}
		});
		
		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	private void fillData(){
		if(HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword()){
			mOldPassword = mEditTextOldPassword.getText().toString();
		}
		mNewPassword1 = mEditTextNewPassword1.getText().toString();
		mNewPassword2 = mEditTextNewPassword2.getText().toString();
	}
	
	public boolean validateData(){
		boolean validatePass = true;
		
		if(HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword()){
			if(!HyjUtil.getSHA1(mOldPassword).equals(HyjApplication.getInstance().getCurrentUser().getUserData().getPassword())){
				mEditTextOldPassword.setError(getString(R.string.changePasswordFragment_validation_wrong_oldPassword));
		   		validatePass = false;
			}else{
				mEditTextOldPassword.setError(null);
			}
		}
		
		if(mNewPassword1.length() == 0){
	   		mEditTextNewPassword1.setError(getString(R.string.changePasswordFragment_editText_hint_newPassword1));
	   		validatePass = false;
		} else if(!mNewPassword1.matches("^.{6,18}$")){
			mEditTextNewPassword1.setError(getString(R.string.changePasswordFragment_validation_password_too_short));
	   		validatePass = false;
		} else if(checkPassWordComplexity(mNewPassword1)){
			mEditTextNewPassword1.setError(getString(R.string.changePasswordFragment_validation_password_too_simple));
			validatePass = false;
		}else {
			mEditTextNewPassword1.setError(null);
		}
		
		if(!mNewPassword1.equals(mNewPassword2)){
			mEditTextNewPassword2.setError(getString(R.string.changePasswordFragment_validation_two_password_not_same));
	   		validatePass = false;
		} else {
			mEditTextNewPassword2.setError(null);
		}
		return validatePass;
	}
	
	private boolean checkPassWordComplexity(String psw) {
		boolean repeat = true;
		boolean series = true;
		char first = psw.charAt(0);
		for (int i = 1; i < psw.length(); i++) {
			repeat = repeat && psw.charAt(i) == first;
			series = series && (int)psw.charAt(i) == (int)psw.charAt(i - 1) + 1;
		}
		if (repeat || series) {
			return true;
		}
		return false;
	}

	private void onSave(){
		fillData();
		if(!validateData()){
			HyjUtil.displayToast(R.string.app_validation_error);
		} else {	
			changePassword_submit(null);
		}
	}
	
	private void changePassword_submit(View v){
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					
					HyjModelEditor<UserData> editor = HyjApplication.getInstance().getCurrentUser().getUserData().newModelEditor();
					editor.getModelCopy().setPassword(HyjUtil.getSHA1(mNewPassword1));
					if(!editor.getModel().getHasPassword()){
						editor.getModelCopy().setHasPassword(true);
					}
					editor.getModelCopy().setSyncFromServer(true);
					editor.save();

					((HyjActivity) ChangePasswordFragment.this.getActivity()).dismissProgressDialog();
					HyjUtil.displayToast(R.string.app_save_success);
					getActivity().finish();
				}

				@Override
				public void errorCallback(Object object) {
					((HyjActivity) ChangePasswordFragment.this.getActivity()).dismissProgressDialog();
					
					JSONObject json = (JSONObject) object;
					HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
				}
			};

			try {
				JSONObject data = new JSONObject();
				data.put("userId", HyjApplication.getInstance().getCurrentUser().getId());
				if(HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword()){
					data.put("oldPassword", HyjUtil.getSHA1(mOldPassword));
				}
				data.put("newPassword", HyjUtil.getSHA1(mNewPassword1));
				data.put("newPassword2", HyjUtil.getSHA1(mNewPassword2));
				
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString() , "changePassword");
				
				((HyjActivity) this.getActivity())
						.displayProgressDialog(
								R.string.changePasswordFragment_title,
								R.string.changePasswordFragment_toast_changing);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}

	 
}
