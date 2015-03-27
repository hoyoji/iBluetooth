package com.hoyoji.hoyoji.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.hoyoji.AppConstants;
import com.hoyoji.hoyoji.models.QQLogin;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.models.WBLogin;
import com.hoyoji.hoyoji.models.WXLogin;
import com.hoyoji.btcontrol.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.auth.QQAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.sample.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;


public class BindIDFragment extends HyjFragment {
	private HyjTextField mTextFieldEmail = null;
	private Button mButtonEmail = null;
	private HyjTextField mTextFieldPhone = null;
	private Button mButtonPhone = null;
	private HyjTextField mTextFieldQQ = null;
	private HyjTextField mTextFieldWX = null;
	private HyjTextField mTextFieldWB = null;
	private Button mButtonQQ = null;
	private Button mButtonWX = null;
	private Button mButtonWB = null;
	
	private SsoHandler mSsoHandler;
	
	public static QQAuth mQQAuth;
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWBAuth;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    
	private Tencent mTencent;
	private String mAppid;
	
	private IWXAPI api;
	
	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_bindid;
	}
	
	 public void onInitViewData() {
		 mTextFieldEmail = (HyjTextField) getView().findViewById(R.id.bindIDFragment_textField_email);
			mTextFieldEmail.setEditable(false);

			
			mButtonEmail = (Button) getView().findViewById(R.id.bindIDFragment_button_emailBinding);

			setEmailField();
			
//			mTextFieldPhone = (HyjTextField) getView().findViewById(R.id.bindIDFragment_textField_phone);
//			mTextFieldPhone.setEditable(false);
//			
//			mButtonPhone = (Button) getView().findViewById(R.id.bindIDFragment_button_phoneBinding);
			
//			setPhoneField();

			mTextFieldQQ = (HyjTextField) getView().findViewById(R.id.bindIDFragment_textField_QQ);
			mTextFieldQQ.setEditable(false);
			
			mTextFieldWX = (HyjTextField) getView().findViewById(R.id.bindIDFragment_textField_WX);
			mTextFieldWX.setEditable(false);
			
			mTextFieldWB = (HyjTextField) getView().findViewById(R.id.bindIDFragment_textField_WB);
			mTextFieldWB.setEditable(false);
			
			mButtonQQ = (Button) getView().findViewById(R.id.bindIDFragment_button_QQBinding);
			
			setQQField();
			
			mButtonWX = (Button) getView().findViewById(R.id.bindIDFragment_button_WXBinding);
			
			setWXField();
			
			mButtonWB = (Button) getView().findViewById(R.id.bindIDFragment_button_WBBinding);
			
			setWBField();
			
			mWBAuth = new WeiboAuth(this.getActivity(), AppConstants.WEIBO_CONNECT_APP_KEY, AppConstants.WEIBO_CONNECT_REDIRECT_URL, AppConstants.WEIBO_CONNECT_SCOPE);
			
		
      }
	 
	 private void setEmailField() {
			
			if((HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() == null 
			|| HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() == false)
			&& (HyjApplication.getInstance().getCurrentUser().getUserData().getEmail() == null 
			|| HyjApplication.getInstance().getCurrentUser().getUserData().getEmail().length() == 0)){
				mButtonEmail.setText("绑定");
				mTextFieldEmail.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getEmail());
				mButtonEmail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						BindIDFragment.this.openActivityWithFragment(BindEmailFragment.class, R.string.bindEmailFragment_title, null);
					}
				});
			}else if(HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() == false
			&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail() != null 
			&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail().length() != 0){
				mButtonEmail.setText("验证");
				mTextFieldEmail.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getEmail());
				mButtonEmail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						BindIDFragment  .this.openActivityWithFragment(BindEmailFragment.class, R.string.bindEmailFragment_title, null);
					}
				});
			}else if(HyjApplication.getInstance().getCurrentUser().getUserData().ismEmailVerified() != false 
			&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail() != null 
			&& HyjApplication.getInstance().getCurrentUser().getUserData().getEmail().length() != 0){
				mButtonEmail.setText("解绑");
				mTextFieldEmail.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getEmail());
				mButtonEmail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						unBindEmail();
					}
				});
			} 
		}
		
		public void unBindEmail() {
			 JSONObject findPasswordJsonObject = new JSONObject();
	   		try {
					findPasswordJsonObject.put("email", mTextFieldEmail.getText().toString());
					findPasswordJsonObject.put("type", "unBindEmail");
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
					userData.setEmail(null);
					userData.setEmailVerified(false);
					userData.setSyncFromServer(true);
					userData.save();
					setEmailField();
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
	   	 
	   	 	HyjHttpPostAsyncTask.newInstance(serverCallbacks, findPasswordJsonObject.toString(), "unBindEmail");
		 }
		
//		private void setPhoneField() {
//			mTextFieldPhone.setText(HyjApplication.getInstance().getCurrentUser().getUserData().getPhone());
//			if(mTextFieldPhone.getText() != null && mTextFieldPhone.getText().length() > 0){
//				mButtonPhone.setText("解绑");
//				mButtonPhone.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						Bundle bundle = new Bundle();
//						bundle.putString("clickType", "unBindPhone");
//						BindIDFragment.this.openActivityWithFragment(BindPhoneFragment.class, R.string.bindPhoneFragment_unBindPhone_title, bundle);
//						
//					}
//				});
//			}else{
//				mButtonPhone.setText("绑定");
//				mButtonPhone.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						BindIDFragment.this.openActivityWithFragment(BindPhoneFragment.class, R.string.bindPhoneFragment_title, null);
//						
//					}
//				});
//			}
//		}
	 
		
		private void setQQField() {
			QQLogin qqLogin = new Select().from(QQLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(qqLogin != null){
				mTextFieldQQ.setText(qqLogin.getNickName());
				mButtonQQ.setText("解绑");
				mButtonQQ.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						WXLogin hasWXLogin = new Select().from(WXLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						WBLogin hasWBLogin = new Select().from(WBLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(!HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword() && hasWBLogin == null && hasWXLogin == null){
							HyjUtil.displayToast("您尚未设置登录密码，请先设置登录密码再解绑");
							return;
						}
						unBindQQ();
					}
				});
			}else{
				mButtonQQ.setText("绑定");
				mTextFieldQQ.setText(null);
				mButtonQQ.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptQQLogin();
					}
				});
			}
		}
		
		public void attemptQQLogin() {
			if(mTencent == null){
				final Context ctxContext = getActivity().getApplicationContext();
				mAppid = AppConstants.TENTCENT_CONNECT_APP_ID;
				mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
				mTencent = Tencent.createInstance(mAppid, getActivity());
			}
			
			if (mQQAuth.isSessionValid()) {
				mQQAuth.logout(getActivity());
			}
			
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					doBindQQ(values);
				}
			};
			mTencent.login(getActivity(), "all", listener);
		}
		
		private void doBindQQ(final JSONObject loginInfo) {    
			// 从服务器上下载用户数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					JSONObject jsonObject = (JSONObject) object;
					QQLogin qqLogin = new QQLogin();
					qqLogin.loadFromJSON(jsonObject, true);
					qqLogin.save();
					
					final User user = HyjApplication.getInstance().getCurrentUser();
					if(jsonObject.optString("nickName").length() > 0){
						// 设置用户的昵称拼音, 并同步回服务器
						if(!jsonObject.optString("nickName").equals(user.getNickName())){
							user.setNickName(jsonObject.optString("nickName"));
//							mTextFieldNickName.setText(user.getNickName());
//							mTextFieldNickName.setEnabled(true);
						}
					}
//					final String figureUrl1 = jsonObject.optString("figureUrl");
//					if(figureUrl1.length() > 0){
//						LoginActivity.downloadUserHeadImage(figureUrl1, 1);
//						
////						HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
////							@Override
////							public void finishCallback(Object object) {
////								Bitmap thumbnail = null;
////								if(object != null){
////									thumbnail = (Bitmap) object;
////									FileOutputStream out;
////									try {
////										Picture figure = new Picture();
////										File imgFile = HyjUtil.createImageFile(figure.getId() + "_icon");
////										if(imgFile != null){
////											out = new FileOutputStream(imgFile);
////											thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
////											out.close();
////											out = null;
////											
////											Picture oldPicture = HyjApplication.getInstance().getCurrentUser().getPicture();
////											if(oldPicture != null){
////												oldPicture.delete();
////											}
////											figure.setRecordId(HyjApplication.getInstance().getCurrentUser().getId());
////											figure.setRecordType("User");
////											figure.setDisplayOrder(0);
////											figure.setPictureType("JPEG");
////											
////											user.setPicture(figure);
////											figure.save();								
////											
//////											takePictureButton.setImage(figure);
////										}
////									} catch (FileNotFoundException e) {
////										e.printStackTrace();
////									} catch (IOException e) {
////										e.printStackTrace();
////									}
////								}
////								
////								user.save();
////								HyjUtil.displayToast("QQ帐号绑定成功");
////								setQQField();
////							}
////		
////							@Override
////							public Object doInBackground(String... string) {
////								Bitmap thumbnail = null;
////								thumbnail = Util.getBitmapFromUrl(figureUrl1, 1);
////								return thumbnail;
////							}
////						});
//					}
					user.save();
					HyjUtil.displayToast("QQ帐号绑定成功");
					setQQField();
				}

				@Override
				public void errorCallback(Object object) {
					try {
						JSONObject json = (JSONObject) object;
//						((HyjActivity)getActivity()).dismissProgressDialog();
						((HyjActivity)getActivity()).displayDialog("绑定QQ失败", json.getJSONObject("__summary").getString("msg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, loginInfo.toString(), "bindQQ");
		}
		
		private class BaseUiListener implements IUiListener {

			@Override
			public void onComplete(Object response) {
//				Util.showResultDialog(LoginActivity.this, response.toString(), " ");
				doComplete((JSONObject)response);
			}

			protected void doComplete(JSONObject values) {

			}

			@Override
			public void onError(UiError e) {
				Util.toastMessage(getActivity(), "出错啦: " + e.errorDetail);
				Util.dismissDialog();
			}

			@Override
			public void onCancel() {
				//Util.toastMessage(LoginActivity.this, "onCancel: ");
				Util.dismissDialog();
			}
		}
		
		private void unBindQQ() {
			// 从服务器上下载用户数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
						QQLogin qqLogin = new Select().from(QQLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(qqLogin != null){
							qqLogin.deleteFromServer();
						}
						setQQField();
						((HyjActivity)getActivity()).dismissProgressDialog();
						HyjUtil.displayToast("解绑成功");
				}

				@Override
				public void errorCallback(Object object) {
					try {
						JSONObject json = (JSONObject) object;
						((HyjActivity)getActivity()).dismissProgressDialog();
						((HyjActivity)getActivity()).displayDialog("解绑QQ不成功",
							json.getJSONObject("__summary").getString("msg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			QQLogin qqLogin = new Select().from(QQLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(qqLogin != null){
				((HyjActivity)getActivity()).displayProgressDialog(R.string.systemSettingFormFragment_toast_unBindQQ,
						R.string.systemSettingFormFragment_toast_unBindingQQ);
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, qqLogin.toJSON().toString(), "unBindQQ");
			} else {
				HyjUtil.displayToast("找不到已绑定的QQ帐户");
			}

		}
	
		private void setWXField() {
			WXLogin wxLogin = new Select().from(WXLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(wxLogin != null){
				mTextFieldWX.setText(wxLogin.getNickName());
				mButtonWX.setText("解绑");
				mButtonWX.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						QQLogin hasQQLogin = new Select().from(QQLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						WBLogin hasWBLogin = new Select().from(WBLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(!HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword() && hasWBLogin == null && hasQQLogin == null){
							HyjUtil.displayToast("您尚未设置登录密码，请先设置登录密码再解绑");
							return;
						}
						unBindWX();
					}
				});
			}else{
				mButtonWX.setText("绑定");
				mTextFieldWX.setText(null);
				mButtonWX.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptWXLogin();
					}
				});
			}
		}
		
		@Override
		public void onResume() {
			super.onResume();
			setWXField();
			setEmailField();
	    }
		
		public void attemptWXLogin() {
			api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID);
			final SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "bindWX";
			api.sendReq(req);
			
		}
		
		private void unBindWX() {
			// 从服务器上下载用户数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
						WXLogin wxLogin = new Select().from(WXLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(wxLogin != null){
							wxLogin.deleteFromServer();
						}
						setWXField();
						((HyjActivity)getActivity()).dismissProgressDialog();
						HyjUtil.displayToast("解绑成功");
				}

				@Override
				public void errorCallback(Object object) {
					try {
						JSONObject json = (JSONObject) object;
						((HyjActivity)getActivity()).displayDialog("解绑微信不成功",
							json.getJSONObject("__summary").getString("msg"));
						((HyjActivity)getActivity()).dismissProgressDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			WXLogin wxLogin = new Select().from(WXLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(wxLogin != null){
				((HyjActivity)getActivity()).displayProgressDialog(R.string.systemSettingFormFragment_toast_unBindWX,
						R.string.systemSettingFormFragment_toast_unBindingWX);
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, wxLogin.toJSON().toString(), "unBindWX");
			} else {
				HyjUtil.displayToast("找不到已绑定的微信帐户");
			}

		}
		
		private void setWBField() {
			WBLogin wbLogin = new Select().from(WBLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(wbLogin != null){
				mTextFieldWB.setText(wbLogin.getNickName());
				mButtonWB.setText("解绑");
				mButtonWB.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						QQLogin hasQQLogin = new Select().from(QQLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(!HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword() && hasQQLogin == null){
							HyjUtil.displayToast("您尚未设置登录密码，请先设置登录密码再解绑");
							return;
						}
						unBindWB();
					}
				});
			}else{
				mButtonWB.setText("绑定");
				mTextFieldWB.setText(null);
				mButtonWB.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptWBLogin();
					}
				});
			}
		}
		
		public void attemptWBLogin() {
			mSsoHandler = new SsoHandler(this.getActivity(), mWBAuth);
	        mSsoHandler.authorize(new AuthListener());
		}
		
		/**
	     * 微博认证授权回调类。
	     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
	     *    该回调才会被执行。
	     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
	     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
	     */
	    class AuthListener implements WeiboAuthListener {
	        
	        @Override
	        public void onComplete(Bundle values) {
	            // 从 Bundle 中解析 Token
	            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
	            if (mAccessToken.isSessionValid()) {
	                // 显示 Token
//	                updateTokenView(false);
	                
	            	JSONObject wbJsonObject = new JSONObject();
	            	
	            	try {
						wbJsonObject.put("openid", values.get("uid"));
						wbJsonObject.put("access_token", values.get("access_token"));
						wbJsonObject.put("expires_in", values.get("expires_in"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	
	            	doBindWB(wbJsonObject);
	            	
	                // 保存 Token 到 SharedPreferences
	                //HyjUtil.writeAccessToken(mAccessToken);
	                Toast.makeText(BindIDFragment.this.getActivity(), 
	                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
	            } else {
	                // 以下几种情况，您会收到 Code：
	                // 1. 当您未在平台上注册的应用程序的包名与签名时；
	                // 2. 当您注册的应用程序包名与签名不正确时；
	                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
	                String code = values.getString("code");
	                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
	                if (!TextUtils.isEmpty(code)) {
	                    message = message + "\nObtained the code: " + code;
	                }
	                Toast.makeText(BindIDFragment.this.getActivity(), message, Toast.LENGTH_LONG).show();
	            }
	        }

	        @Override
	        public void onCancel() {
	            Toast.makeText(BindIDFragment.this.getActivity(), 
	                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
	        }

	        @Override
	        public void onWeiboException(WeiboException e) {
	            Toast.makeText(BindIDFragment.this.getActivity(), 
	                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
	        }
	    }
		
		private void doBindWB(final JSONObject loginInfo) {    
			// 从服务器上下载用户数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					JSONObject jsonObject = (JSONObject) object;
					WBLogin wbLogin = new WBLogin();
					wbLogin.loadFromJSON(jsonObject, true);
					wbLogin.save();
					
					final User user = HyjApplication.getInstance().getCurrentUser();
					if(jsonObject.optString("nickName").length() > 0){
						// 设置用户的昵称拼音, 并同步回服务器
						if(!jsonObject.optString("nickName").equals(user.getNickName())){
							user.setNickName(jsonObject.optString("nickName"));
//							mTextFieldNickName.setText(user.getNickName());
						}
					}
//					final String profile_image_url1 = jsonObject.optString("profile_image_url");
//					if(profile_image_url1.length() > 0){
//						LoginActivity.downloadUserHeadImage(profile_image_url1, 1);
////						HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
////							@Override
////							public void finishCallback(Object object) {
////								Bitmap thumbnail = null;
////								if(object != null){
////									thumbnail = (Bitmap) object;
////									FileOutputStream out;
////									try {
////										Picture figure = new Picture();
////										File imgFile = HyjUtil.createImageFile(figure.getId() + "_icon");
////										if(imgFile != null){
////											out = new FileOutputStream(imgFile);
////											thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
////											out.close();
////											out = null;
////											
////											Picture oldPicture = HyjApplication.getInstance().getCurrentUser().getPicture();
////											if(oldPicture != null){
////												oldPicture.delete();
////											}
////											figure.setRecordId(HyjApplication.getInstance().getCurrentUser().getId());
////											figure.setRecordType("User");
////											figure.setDisplayOrder(0);
////											figure.setPictureType("JPEG");
////											
////											user.setPicture(figure);
////											figure.save();								
////											
//////											takePictureButton.setImage(figure);
////										}
////									} catch (FileNotFoundException e) {
////										e.printStackTrace();
////									} catch (IOException e) {
////										e.printStackTrace();
////									}
////								}
////								
////								user.save();
////								HyjUtil.displayToast("WB帐号绑定成功");
////								setWBField();
////							}
////		
////							@Override
////							public Object doInBackground(String... string) {
////								Bitmap thumbnail = null;
////								thumbnail = Util.getBitmapFromUrl(profile_image_url1, 1);
////								return thumbnail;
////							}
////						});
//
//					} 
					user.save();
					HyjUtil.displayToast("WB帐号绑定成功");
					setWBField();
				}

				@Override
				public void errorCallback(Object object) {
					try {
						JSONObject json = (JSONObject) object;
//						((HyjActivity)getActivity()).dismissProgressDialog();
						((HyjActivity)getActivity()).displayDialog("绑定WB失败", json.getJSONObject("__summary").getString("msg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, loginInfo.toString(), "bindWB");
		}
		
		private void unBindWB() {
			// 从服务器上下载用户数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
						WBLogin wbLogin = new Select().from(WBLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(wbLogin != null){
							wbLogin.deleteFromServer();
						}
						setWBField();
						((HyjActivity)getActivity()).dismissProgressDialog();
						HyjUtil.displayToast("解绑成功");
				}

				@Override
				public void errorCallback(Object object) {
					try {
						JSONObject json = (JSONObject) object;
						((HyjActivity)getActivity()).dismissProgressDialog();
						((HyjActivity)getActivity()).displayDialog("解绑WB不成功",
							json.getJSONObject("__summary").getString("msg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			WBLogin wbLogin = new Select().from(WBLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(wbLogin != null){
				((HyjActivity)getActivity()).displayProgressDialog(R.string.systemSettingFormFragment_toast_unBindWB,
						R.string.systemSettingFormFragment_toast_unBindingWB);
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, wbLogin.toJSON().toString(), "unBindWB");
			} else {
				HyjUtil.displayToast("找不到已绑定的WB帐户");
			}

		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (mSsoHandler != null) {
				mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
		 
		 
//		 private class ChangeObserver extends ContentObserver {
//				public ChangeObserver() {
//					super(new Handler());
//				}
//
//				@Override
//				public boolean deliverSelfNotifications() {
//					return true;
//				}
//
//				@Override
//				public void onChange(boolean selfChange) {
////					setPhoneField();
//					if(HyjApplication.getInstance().getCurrentUser().getUserData().getHasPassword()){
////						mButtonChangePassword.setText("修改密码");
//					} 
//				}
//			}
	 
}
