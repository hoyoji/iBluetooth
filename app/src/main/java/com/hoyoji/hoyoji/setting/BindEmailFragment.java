package com.hoyoji.hoyoji.setting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.btcontrol.R;


public class BindEmailFragment extends HyjUserFragment {
	private EditText mEditTextEmail = null;
	private EditText mEditTextVerificationCode = null;
	String mEmail = "";
	String mVerificationCode = "";
	boolean mHasError = false;
	private Button mButtonSubmie = null;
	private Button mButtonVerificationCode = null;
	private LinearLayout verificationCodeLayput;
//	private int authCode = 0;
	
	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_bindemail;
	}
	 
	@Override
	public void onInitViewData(){
		mEditTextEmail = (EditText) getView().findViewById(R.id.bindEmailFragment_editText_email);
		mEditTextVerificationCode = (EditText) getView().findViewById(R.id.bindEmailFragment_editText_verificationCode);
		mButtonSubmie = (Button) getView().findViewById(R.id.bindEmailFragment_button_submit);
		mButtonVerificationCode = (Button) getView().findViewById(R.id.bindEmailFragment_button_verify);
		verificationCodeLayput =(LinearLayout) getView().findViewById(R.id.bindEmailFragment_linearLayout_verificationCode);
		
		if(HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() == false
		&& (HyjApplication.getInstance().getCurrentUser().getUserData().getEmail() == null 
		|| HyjApplication.getInstance().getCurrentUser().getUserData().getEmail().length() == 0)){
			verificationCodeLayput.setVisibility(View.GONE);
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("绑定邮箱");
		} else if(HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() == false
		&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail() != null 
		&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail().length() != 0){
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("验证邮箱");
			mEditTextEmail.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getEmail());
		}else if(HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() != false 
		&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail() != null 
		&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail().length() != 0){
			((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("更改邮箱");
			mEditTextEmail.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getEmail());
			verificationCodeLayput.setVisibility(View.GONE);
		}
		
		
		
		
		mButtonSubmie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEmail = mEditTextEmail.getText().toString();
				if(!validateEmailData()){
					HyjUtil.displayToast(R.string.app_validation_error);
				} else {
					doBindEmail();
				}
			}
		});
		
		mButtonVerificationCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mVerificationCode = mEditTextVerificationCode.getText().toString();
				if(!validateVerificationCodeData()){
					HyjUtil.displayToast(R.string.app_validation_error);
				} else {
					doCheckAuthCode();
				}
			}
		});
	}
	
	public boolean validateEmailData(){
		boolean validatePass = true;
		
		// Check for a valid email address.
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(mEmail);
//				System.out.println(matcher.matches());
		
		
		if (TextUtils.isEmpty(mEmail) || !matcher.matches()) {
			mEditTextEmail.setError(getString(R.string.bindEmailFragment_validation_wrong_email));
			validatePass = false;
		} else {
			mEditTextEmail.setError(null);
		}
		return validatePass;
	}
	
	public boolean validateVerificationCodeData(){
		boolean validatePass = true;
		
		if(mVerificationCode.length() != 6){
			mEditTextVerificationCode.setError(getString(R.string.bindEmailFragment_validation_wrong_verificationcode));
	   		validatePass = false;
		} else {
			mEditTextVerificationCode.setError(null);
		}
		return validatePass;
	}

	public void doBindEmail() {
		 JSONObject findPasswordJsonObject = new JSONObject();
    		try {
				findPasswordJsonObject.put("email", mEditTextEmail.getText().toString());
				findPasswordJsonObject.put("type", "BindEmail");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	 
    	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONObject jsonObject = (JSONObject) object;
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				userData.setEmail(mEmail);
				userData.setSyncFromServer(true);
				userData.save();
				verificationCodeLayput.setVisibility(View.VISIBLE);
				((HyjActivity) getActivity()).displayDialog(null,jsonObject.opt("result").toString());
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
    	 
    	 HyjHttpPostAsyncTask.newInstance(serverCallbacks, findPasswordJsonObject.toString(), "bindEmail");
	 }
	
	public void doCheckAuthCode() {
		 JSONObject findPasswordJsonObject = new JSONObject();
		 try {
			findPasswordJsonObject.put("email", mEditTextEmail.getText().toString());
			findPasswordJsonObject.put("verificationCode", mVerificationCode);
			
		 } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		 }
   	 
   	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONObject jsonObject = (JSONObject) object;
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				userData.setEmailVerified(true);
				userData.setSyncFromServer(true);
				userData.save();
				((HyjActivity) getActivity()).displayDialog(null,jsonObject.opt("result").toString());
				getActivity().finish();
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
   	 
   	 HyjHttpPostAsyncTask.newInstance(serverCallbacks, findPasswordJsonObject.toString(), "checkAuthCode");
	 }
	 
}
