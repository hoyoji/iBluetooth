package com.hoyoji.hoyoji;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.btcontrol.R;


public class FindPasswordFragment extends HyjFragment {
	private EditText mUserNameView;
	private EditText mFindPasswordEmailView;

	private EditText mVerificationCodeView = null;
	private EditText mNewPassword1View = null;
	private EditText mNewPassword2View = null;
//	private Spinner mFindPasswordSpinner;
	private Button mGetverificationCodeButton;
	private Button mSubmitButton;
	
	private String mUserName;
	private String mFindPasswordEmail;
	private String mVerificationCode = "";
	private String mNewPassword1 = "";
	private String mNewPassword2 = "";
	
	@Override
	public Integer useContentView() {
		return R.layout.login_fragment_findpassword;
	}
	
	 public void onInitViewData() {
        mUserNameView = (EditText) getView().findViewById(R.id.findpasswordFragment_editText_username);
        mFindPasswordEmailView = (EditText) getView().findViewById(R.id.findpasswordFragment_editText_email);
        mGetverificationCodeButton = (Button) getView().findViewById(R.id.findpasswordFragment_button_getverificationCode);
        mVerificationCodeView = (EditText) getView().findViewById(R.id.findpasswordFragment_editText_verificationCode);
        mNewPassword1View = (EditText) getView().findViewById(R.id.findpasswordFragment_editText_newPassword1);
        mNewPassword2View = (EditText) getView().findViewById(R.id.findpasswordFragment_editText_newPassword2);
        mSubmitButton = (Button) getView().findViewById(R.id.findpasswordFragment_button_submit);
        
//        mFindPasswordSpinner=  (Spinner)findViewById(R.id.spinner_findpasswordway);
//        List<String> findPasswordList = new ArrayList<String>();
//        findPasswordList.add("通过邮箱找回?");
//        findPasswordList.add("通过手机短信方式找回?");
//		ArrayAdapter<String> Qadapter1 = new ArrayAdapter<String>(FindPasswordActivity.this, android.R.layout.simple_spinner_item, findPasswordList);
//		mFindPasswordSpinner.setAdapter(Qadapter1);
        
		mGetverificationCodeButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					findPassword();
				}
			});
		
		mSubmitButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					fillData();
					if(!validatePasswordData()){
						HyjUtil.displayToast(R.string.app_validation_error);
					} else {	
						resetPassword_submit();
					}
				}
			});
		
      }
	 
	 public void findPassword() {
		// Reset errors.
		mUserNameView.setError(null);
		mFindPasswordEmailView.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mUserNameView.getText().toString();
		mFindPasswordEmail = mFindPasswordEmailView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(mFindPasswordEmail);
//		System.out.println(matcher.matches());
		
		
		if (TextUtils.isEmpty(mFindPasswordEmail) || !matcher.matches()) {
			mFindPasswordEmailView
					.setError(getString(R.string.findpasswordFragment_validation_email_error));
			focusView = mFindPasswordEmailView;
			cancel = true;
		}
		
		// Check for a valid userName.
		if (TextUtils.isEmpty(mUserName) || mUserName.length() < 3) {
			mUserNameView
					.setError(getString(R.string.findpasswordFragment_validation_username_error_shortandempty));
			focusView = mUserNameView;
			cancel = true;
		}


		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			doFindPassword();
		}
	 }
	 
	 public void doFindPassword() {
		 mUserName= mUserNameView.getText().toString();
		 mFindPasswordEmail = mFindPasswordEmailView.getText().toString();
		 
		 JSONObject findPasswordJsonObject = new JSONObject();
     	 try {
     		findPasswordJsonObject.put("userName", mUserName);
     		findPasswordJsonObject.put("email", mFindPasswordEmail);
     		findPasswordJsonObject.put("type", "ResetPassword");
		 } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
     	 
     	 
     	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				try {
					JSONObject jsonObject = (JSONObject) object;
					((HyjActivity) getActivity()).displayDialog(null,jsonObject.opt("result").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void errorCallback(Object object) {
				try {
					JSONObject json = (JSONObject) object;
					((HyjActivity) getActivity()).displayDialog(null,
							json.getJSONObject("__summary")
									.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
     	 
     	 HyjHttpPostAsyncTask.newInstance(serverCallbacks, findPasswordJsonObject.toString(), "resetPasswordSendEmail");
	 }
	 
	 private void fillData(){
		mVerificationCode = mVerificationCodeView.getText().toString();
		mNewPassword1 = mNewPassword1View.getText().toString();
		mNewPassword2 = mNewPassword2View.getText().toString();
	}
	 
	 public boolean validatePasswordData(){
		boolean validatePass = true;
		
		if(mVerificationCode.length() != 6){
			mVerificationCodeView.setError(getString(R.string.findpasswordFragment_validation_wrong_verificationcode));
	   		validatePass = false;
		} else {
			mVerificationCodeView.setError(null);
		}
		
		if(mNewPassword1.length() == 0){
			mNewPassword1View.setError(getString(R.string.findpasswordFragment_editText_hint_newPassword1));
	   		validatePass = false;
		} else if(!mNewPassword1.matches("^.{6,18}$")){
			mNewPassword1View.setError(getString(R.string.findpasswordFragment_validation_password_too_short));
	   		validatePass = false;
		} else if(checkPassWordComplexity(mNewPassword1)){
			mNewPassword1View.setError(getString(R.string.findpasswordFragment_validation_password_too_simple));
			validatePass = false;
		}else {
			mNewPassword1View.setError(null);
		}
		
		if(!mNewPassword1.equals(mNewPassword2)){
			mNewPassword2View.setError(getString(R.string.findpasswordFragment_validation_two_password_not_same));
	   		validatePass = false;
		} else {
			mNewPassword2View.setError(null);
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
	 
	 private void resetPassword_submit(){
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONObject jsonObject = (JSONObject) object;
				if(HyjApplication.getInstance().getCurrentUser()!=null){
					if(HyjApplication.getInstance().getCurrentUser().getId().equals(jsonObject.optString("userId"))){
						HyjModelEditor<UserData> editor = HyjApplication.getInstance().getCurrentUser().getUserData().newModelEditor();
						editor.getModelCopy().setPassword(HyjUtil.getSHA1(mNewPassword1));
						if(!editor.getModel().getHasPassword()){
							editor.getModelCopy().setHasPassword(true);
						}
						editor.getModelCopy().setSyncFromServer(true);
						editor.save();
					}
				}
//				((HyjActivity) FindPasswordFragment.this.getActivity()).dismissProgressDialog();
				((HyjActivity) getActivity()).displayDialog(null,"密码找回成功");
//				HyjUtil.displayToast(R.string.app_save_success);
				getActivity().finish();
			}

			@Override
			public void errorCallback(Object object) {
				((HyjActivity) FindPasswordFragment.this.getActivity()).dismissProgressDialog();
				
				JSONObject json = (JSONObject) object;
				HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
			}
		};

		try {
			JSONObject data = new JSONObject();
			data.put("userName", mUserNameView.getText().toString());
			data.put("email", mFindPasswordEmailView.getText().toString());
			data.put("verificationCode", mVerificationCodeView.getText().toString());
			data.put("newPassword", HyjUtil.getSHA1(mNewPassword1));
			data.put("newPassword2", HyjUtil.getSHA1(mNewPassword2));
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString() , "resetPassword");
			
			((HyjActivity) this.getActivity())
					.displayProgressDialog(
							R.string.findPasswordFragment_title,
							R.string.findPasswordFragment_toast_changing);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	 
}
