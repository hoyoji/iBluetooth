package com.hoyoji.hoyoji.home;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;


public class InviteLinkFormFragment extends HyjUserFormFragment{
	private HyjDateTimeField mHyjDateTimeFieldDate = null;
	private EditText mEditTextDescription = null;
	private HyjTextField mVerificationCode =null;
	private Button mCloseLink;
	private Button mOpenLink;
	boolean mHasError = false;
	private JSONObject jsonObj = null;
	private String state = null;
	private Intent intent;
	@Override
	public Integer useContentView() {
		return R.layout.link_formfragment_invite;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		intent = getActivity().getIntent();
		String inviteLink = intent.getStringExtra("INVITELINK_JSON_OBJECT");
		
		try {
			jsonObj = new JSONObject(inviteLink);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mHyjDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.inviteLinkFormFragment_editText_date);
		mHyjDateTimeFieldDate.setTime(jsonObj.optLong("date"));
		mHyjDateTimeFieldDate.setEnabled(false);
		mEditTextDescription = (EditText) getView().findViewById(R.id.inviteLinkFormFragment_hyjRemarkField_description);
		mEditTextDescription.setText(jsonObj.optString("description"));
//		mEditTextDescription.setEnabled(false);
		mVerificationCode = (HyjTextField) getView().findViewById(R.id.inviteLinkFormFragment_hyjTextField_verificationCode);
		mVerificationCode.setText(jsonObj.optString("verificationCode"));
//		mVerificationCode.setEnabled(false);
		
		RadioButton closeRadio = (RadioButton) getView().findViewById(R.id.inviteLinkFormFragment_RadioButton_closeState);
		RadioButton openRadio = (RadioButton) getView().findViewById(R.id.inviteLinkFormFragment_RadioButton_openState);
		if(jsonObj.optString("state").equals("Open")) {
			openRadio.setChecked(true);
			state = "Open";
		} else {
			closeRadio.setChecked(true);
			state = "Close";
		}
		RadioGroup group = (RadioGroup)getView().findViewById(R.id.radioGroup);
         //绑定一个匿名监听器
         group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
             
             @Override
             public void onCheckedChanged(RadioGroup arg0, int arg1) {
                 // TODO Auto-generated method stub
                 //获取变更后的选中项的ID
                 int radioButtonId = arg0.getCheckedRadioButtonId();
                 //根据ID获取RadioButton的实例
                 RadioButton rb = (RadioButton)getView().findViewById(radioButtonId);
                 if(rb.getText().equals("打开")) {
                	 state = "Open";
                 } else {
                	 state = "Close";
                 }
                 rb.setChecked(true);
             }
         });
	}
	
	public void changeLinkState() {
		((HyjActivity) getActivity()).displayProgressDialog(R.string.inviteLinkFormFragment_action_change_title,R.string.inviteLinkFormFragment_action_change_content);
		
		JSONObject inviteObject = new JSONObject();
   		try {
   				inviteObject.put("id", jsonObj.optString("id"));
   				inviteObject.put("__dataType", "InviteLink");
   				inviteObject.put("state", state);
   				inviteObject.put("verificationCode", mVerificationCode.getText().toString().trim());
   				inviteObject.put("description", mEditTextDescription.getText().toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
   	 
   	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				((HyjActivity) getActivity()).dismissProgressDialog();
				getActivity().finish();
			}

			@Override
			public void errorCallback(Object object) {
				((HyjActivity) getActivity()).dismissProgressDialog();
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
   	 
   	 	HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + inviteObject.toString() + "]", "putData");
	 }
	
	public void onSave(){
		super.onSave();
		changeLinkState();
		intent.putExtra("state", state);
		intent.putExtra("description", mEditTextDescription.getText().toString());
		intent.putExtra("verificationCode", mVerificationCode.getText().toString().trim());
		getActivity().setResult(Activity.RESULT_OK, intent);
	}
	
}
