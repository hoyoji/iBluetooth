package com.hoyoji.hoyoji.setting;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjFragment;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.UserData;

public class BindPhoneFragment extends HyjFragment {
	private HyjTextField mTextViewPhone = null;
	private HyjTextField mTextViewAuthCode = null;
	private Button mButtonSendAuthCode = null;
	private Button mButtonSubmit = null;
	private String mAuthCodeFromServer = null;
	private String mPhoneText = null;
	private String mAuthCodeText = null;
	private BroadcastReceiver mBroadcastReceiver = null;
	private TimeCount mTime = null;
	private String clickType = null;

	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_bindphone;
	}

	@Override
	public void onInitViewData() {
		Intent intent = getActivity().getIntent();
		clickType = intent.getStringExtra("clickType");
		
		mTextViewPhone = (HyjTextField) getView().findViewById(R.id.bindPhoneFragment_textField_phone);
		if(clickType != null && clickType.equalsIgnoreCase("unBindPhone")){
			mTextViewPhone.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getPhone());
			mTextViewPhone.setEnabled(false);
		}else{
			 SIMCardInfo siminfo = new SIMCardInfo(this.getActivity());
			 mTextViewPhone.setText(siminfo.getNativePhoneNumber());
		}
		
		mTextViewAuthCode = (HyjTextField) getView().findViewById(R.id.bindPhoneFragment_textField_authCode);
		mTextViewAuthCode.setEnabled(false);
		
		mButtonSendAuthCode = (Button) getView().findViewById(R.id.bindPhoneFragment_button_sendAuthCode);
		mButtonSubmit = (Button) getView().findViewById(R.id.bindPhoneFragment_button_submit);
		
		mTime = new TimeCount(60000, 1000);
		mButtonSendAuthCode.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mTextViewPhone.getText().length() != 0){
							mTextViewAuthCode.setEnabled(true);
							mButtonSubmit.setClickable(true);
							sendAuthCodeToPhone();
							mTime.start();
						}
					}
				});

		
		
		if(clickType != null && clickType.equalsIgnoreCase("unBindPhone")){
			mButtonSubmit.setText(getString(R.string.bindPhoneFragment_button_unbind));
			mButtonSubmit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					unBindPhone_submit(v);
				}
			});
		}else if(clickType != null && clickType.equalsIgnoreCase("findPassword")){
			mButtonSubmit.setText(getString(R.string.bindPhoneFragment_button_submit));
			mButtonSubmit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					findPassword_submit(v);
				}
			});
		}else{
			mButtonSubmit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					bindPhone_submit(v);
				}
			});
		}
		mButtonSubmit.setClickable(false);
		
		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	protected void sendAuthCodeToPhone() {
		mAuthCodeFromServer = Double.toString((Math.random() + 0.1)*10000).substring(0, 4);
		
		Intent sentIntent = new Intent("SENT_SMS_ACTION");  
		SmsManager smsManager = SmsManager.getDefault(); 
		PendingIntent sentPI = PendingIntent.getBroadcast(this.getActivity(), 0, sentIntent, 0);  

		String msg = "尊敬的用户，您正在进行iBluetooth账号（绑定/解绑）手机或找回密码操作，短信验证码为：" + mAuthCodeFromServer;
		try{  
			smsManager.sendTextMessage(mTextViewPhone.getText().toString().trim(), null, msg, sentPI, null); 
        }catch(Exception e){  
            Toast.makeText(this.getActivity(), "短信发送失败，请检查是系统否限制本应用发送短信", 5000).show();  
            e.printStackTrace();  
        }  
		
		if(mBroadcastReceiver == null){
			mBroadcastReceiver = new BroadcastReceiver(){  
            @Override  
            public void onReceive(Context context, Intent intent) {  
                switch(getResultCode()){  
                    case Activity.RESULT_OK:  
                    	Toast.makeText(context,"信息已发出",Toast.LENGTH_LONG).show();  
                        break;  
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:  
                        Toast.makeText(context, mAuthCodeFromServer +")未指定失败 \n 信息未发出，请重试", Toast.LENGTH_LONG).show();  
                        break;  
                    case SmsManager.RESULT_ERROR_RADIO_OFF:  
                        Toast.makeText(context, mAuthCodeFromServer +")无线连接关闭 \n 信息未发出，请重试", Toast.LENGTH_LONG).show();  
                        break;  
                    case SmsManager.RESULT_ERROR_NULL_PDU:  
                        Toast.makeText(context, mAuthCodeFromServer +")PDU失败 \n 信息未发出，请重试", Toast.LENGTH_LONG).show();  
                        break;  
                }  
  
            }  
        };
		//短信发送状态监控  
        
        this.getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter("SENT_SMS_ACTION")); 
        }
	}

	private void fillData() {
		mPhoneText = mTextViewPhone.getText();
		mAuthCodeText = mTextViewAuthCode.getText();
	}
	
	public boolean validateData(){
		boolean valiatePass = true;
		fillData();
		if(mPhoneText.length() == 0){
			mTextViewPhone.setError(getString(R.string.bindPhoneFragment_editText_hint_phone));
	   		valiatePass = false;
		}else {
			mTextViewPhone.setError(null);
	   	}
		
		if(mAuthCodeText.length() != 4 || !mAuthCodeText.equalsIgnoreCase(mAuthCodeFromServer)){
			mTextViewAuthCode.setError(getString(R.string.bindPhoneFragment_editText_validate_error_authCode));
	   		valiatePass = false;
		}else {
			mTextViewAuthCode.setError(null);
	   	}
		return valiatePass;
	}

    private void bindPhone_submit(View v) {
       if(!validateData()){
    	   HyjUtil.displayToast(R.string.app_validation_error);
       }else{
    	   UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
    	   HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
    	   userDataEditor.getModelCopy().setPhone(mTextViewPhone.getText().toString().trim());
    	   userDataEditor.save();
    	   mTime.cancel();
    	   getActivity().finish();
       }
	}

    private void unBindPhone_submit(View v) {
    	if(!validateData()){
     	   HyjUtil.displayToast(R.string.app_validation_error);
        }else{
	       UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
	 	   HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
	 	   userDataEditor.getModelCopy().setPhone(null);
	 	   userDataEditor.save();
	       mTime.cancel();
	 	   getActivity().finish();
        }
	}
    
	protected void findPassword_submit(View v) {
		 if(!validateData()){
	    	   HyjUtil.displayToast(R.string.app_validation_error);
	       }else{
//	    	   Bundle bundle = new Bundle();
//			   bundle.putString("phone", mPhoneText);
//	    	   BindPhoneFragment.this.openActivityWithFragment(ChangePasswordFragment.class, R.string.bindPhoneFragment_findPassword_title, bundle);
//	    	   mTime.cancel();
//	    	   getActivity().finish();
	       }
	}
    
    private class TimeCount extends CountDownTimer{
    	public TimeCount(long millisInFuture, long countDownInterval) {
    		super(millisInFuture, countDownInterval);//参数为总时长，和计时的时间间隔
    		// TODO Auto-generated constructor stub
    	}

    	@Override
    	public void onFinish() {
    		// TODO Auto-generated method stub
    		mButtonSendAuthCode.setText(getString(R.string.bindPhoneFragment_button_sendAuthCodeAgain));
    		mButtonSendAuthCode.setClickable(true);
    	}

    	@Override
    	public void onTick(long millisUntilFinished) {
    		// TODO Auto-generated method stub
    		mButtonSendAuthCode.setClickable(false);
    		mButtonSendAuthCode.setText(millisUntilFinished/1000 + getString(R.string.bindPhoneFragment_button_sendAuthCodeAgainTimeCount));
    	}

    }
    /**
     * class name：SIMCardInfo<BR>
     * class description：读取Sim卡信息<BR>
     * PS： 必须在加入各种权限 <BR>
     * Date:2014-4-22<BR>
     * @version 1.00
     * @author CODYY)peijiangping
     */
    public class SIMCardInfo {
        /**
         * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家 以及某些类型的用户访问信息。
         * 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类
         * 使用Context.getSystemService(Context.TELEPHONY_SERVICE)来获取这个类的实例。
         */
        private TelephonyManager telephonyManager;
        /**
         * 国际移动用户识别码
         */
        private String IMSI;
        public SIMCardInfo(Context context) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        /**
         * Role:获取当前设置的电话号码
         * <BR>Date:2014-4-22
         * <BR>@author CODYY)peijiangping
         */
        public String getNativePhoneNumber() {
            String NativePhoneNumber=null;
            NativePhoneNumber=telephonyManager.getLine1Number();
            return NativePhoneNumber;
        }
    
        /**
         * Role:Telecom service providers获取手机服务商信息 <BR>
         * 需要加入权限<uses-permission
         * android:name="android.permission.READ_PHONE_STATE"/> <BR>
         * Date:2014-4-22 <BR>
         *
         * @author CODYY)peijiangping
         */
    
        public String getProvidersName() {
            String ProvidersName = null;
            // 返回唯一的用户ID;就是这张卡的编号神马的
            IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = getString(R.string.bindPhoneFragment_providersName_CMCC);
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = getString(R.string.bindPhoneFragment_providersName_WCDMA);
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = getString(R.string.bindPhoneFragment_providersName_CHA);
            }
            return ProvidersName;
        }
    }
    @Override
	public void onDestroy() {
    	if(mTime != null){
    		mTime.cancel();
    	}
    	
    	if(mBroadcastReceiver != null){
    		this.getActivity().unregisterReceiver(mBroadcastReceiver);
    	}
		super.onDestroy();
	}
}


