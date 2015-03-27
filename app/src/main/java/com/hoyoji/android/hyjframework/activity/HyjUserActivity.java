package com.hoyoji.android.hyjframework.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.hoyoji.LoginActivity;
import com.hoyoji.btcontrol.R;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public abstract class HyjUserActivity extends HyjActivity {
	private boolean mIsFirstTimeStart = true;
	
	@Override
	protected void onStart() {
		if(!HyjApplication.getInstance().isLoggedIn()) {
			  SharedPreferences userInfo = getSharedPreferences("current_user_info", 0);  
		      final String userId = userInfo.getString("userId", "");  
		      final String password = userInfo.getString("password", "");  
		      if(userId.length() > 0 && password.length() > 0){
		    	  HyjApplication.getInstance().login(userId, password);
		      }
		}
		
		if(HyjApplication.getInstance().isLoggedIn()) {
//			// 2.30及以上版本
//			enableComponentIfNeeded(getApplicationContext(), XGPushActivity.class.getName());
//			// CustomPushReceiver改为自己继承XGPushBaseReceiver的类，若有的话
//			enableComponentIfNeeded(getApplicationContext(), PushMessageReceiver.class.getName());

			XGPushConfig.enableDebug(this, true);
			XGPushManager.registerPush(getApplicationContext(), HyjApplication.getInstance().getCurrentUser().getId());
			super.onStart();
		} else {
			super.onStartWithoutInitViewData();
			if(mIsFirstTimeStart){ 
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			} else {
				displayDialog(-1, R.string.loginActivity_alert_require_login, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
						new DialogCallbackListener() {
							@Override
							public void doPositiveClick(Object object) {
								Intent intent = new Intent(
										HyjUserActivity.this,
										LoginActivity.class);
								startActivity(intent);
							}
	
							@Override
							public void doNegativeClick() {
								finish();
							}
						});
			}
		}
		mIsFirstTimeStart = false;
	}
	
//	// 启用被禁用组件方法
//	private static void enableComponentIfNeeded(Context context,
//			String componentName) {
//	 PackageManager pmManager = context.getPackageManager();
//	 if (pmManager != null) {
//	  ComponentName cnComponentName = new ComponentName(
//	  		context.getPackageName(), componentName);
//	   int status = pmManager
//	   		.getComponentEnabledSetting(cnComponentName);
//	   if (status != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
//	   	pmManager.setComponentEnabledSetting(cnComponentName,
//	   			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//	   			PackageManager.DONT_KILL_APP);
//	   }
//	  }
//	}
}
