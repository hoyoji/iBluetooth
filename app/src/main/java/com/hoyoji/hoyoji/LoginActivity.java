package com.hoyoji.hoyoji;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.DatabaseHelper;
import com.activeandroid.util.Log;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTask;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.userdatabase.HyjUserDbHelper;
import com.hoyoji.android.hyjframework.userdatabase.HyjUserDbContract.UserDatabaseEntry;
import com.hoyoji.hoyoji.models.MessageBox;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.btcontrol.R;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.sample.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends HyjActivity {
	// Values for email and password at the time of the login attempt.
//	private final static int GET_WELCOME_MESSAGE = 0;
	private String mUserName;
	private String mPassword;

	// UI references.
	private EditText mUserNameView;
	private EditText mPasswordView;
	private ImageButton mLoginQQButton;
	private ImageButton mLoginWBButton;
	private ImageButton mLoginWXButton;
//	private Button mFindPasswordButton;

    private UserInfo mInfo;
    public static QQAuth mQQAuth;
	private Tencent mTencent;
	private String mAppid;
	
	private IWXAPI api;
//	
//	/** 显示认证后的信息，如 AccessToken */
//    private TextView mTokenText;
    /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the login form.
		mUserName = "";
		mUserNameView = (EditText) findViewById(R.id.editText_username);
		mUserNameView.setText(mUserName);

		mPasswordView = (EditText) findViewById(R.id.editText_password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.imeAction_login
								|| id == EditorInfo.IME_ACTION_DONE
								|| id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		findViewById(R.id.button_sign_in).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		mLoginQQButton = (ImageButton)findViewById(R.id.button_sign_in_qq);
		mLoginQQButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptQQLogin();
					}
				});
		
		mLoginWXButton = (ImageButton)findViewById(R.id.button_sign_in_wx);
		mLoginWXButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptWXLogin();
				}
			});

//		mFindPasswordButton = (Button)findViewById(R.id.button_find_password);
//		mFindPasswordButton.setOnClickListener(
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						LoginActivity.this.openBlankActivityWithFragment(FindPasswordFragment.class, R.string.findPasswordFragment_title, null);
//					}
//				});
		
		if(mWeiboAuth == null){
	        mWeiboAuth = new WeiboAuth(this, AppConstants.WEIBO_CONNECT_APP_KEY, AppConstants.WEIBO_CONNECT_REDIRECT_URL, AppConstants.WEIBO_CONNECT_SCOPE);

			mLoginWBButton = (ImageButton)findViewById(R.id.button_sign_in_wb);
			mLoginWBButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptWBLogin();
					}
				});
		}
		
		findViewById(R.id.button_register).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
						startActivity(intent);
//						startActivityForResult(intent, GET_WELCOME_MESSAGE);
					}
				});
	}

	@Override
	protected void onInitViewData() {
		ActionBar actionBar = ((ActionBarActivity)this).getSupportActionBar();
		if(HyjApplication.getIsDebuggable()){
			actionBar.setTitle("iBluetooth(测试版)");
		}
		// init view data here
//		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	protected Integer getContentView() {
		return R.layout.activity_login;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.loginActivity_action_forgot_password:
//		   Bundle bundle = new Bundle();
//		   bundle.putString("clickType", "findPassword");
//    	   LoginActivity.this.openActivityWithFragment(BindPhoneFragment.class, R.string.bindPhoneFragment_findPassword_title, bundle);
		   LoginActivity.this.openBlankActivityWithFragment(FindPasswordFragment.class, R.string.findPasswordFragment_title, null);
    	   return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public void attemptLogin() {

		// Reset errors.
		mUserNameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserName = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView
					.setError(getString(R.string.loginActivity_error_field_required)
							+ getString(R.string.loginActivity_editText_hint_password));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView
					.setError(getString(R.string.loginActivity_error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		mPassword = HyjUtil.getSHA1(mPassword);

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserName)) {
			mUserNameView
					.setError(getString(R.string.loginActivity_error_field_required)
							+ getString(R.string.loginActivity_editText_hint_username));
			focusView = mUserNameView;
			cancel = true;
		}

		// else if (!mUserName.contains("@")) {
		// mUserNameView.setError(getString(R.string.error_invalid_email));
		// focusView = mUserNameView;
		// cancel = true;
		// }

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			doLogin();
		}
	}
	
	public void attemptQQLogin() {
		if(mTencent == null){
			final Context ctxContext = LoginActivity.this.getApplicationContext();
			mAppid = AppConstants.TENTCENT_CONNECT_APP_ID;
			mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
			mTencent = Tencent.createInstance(mAppid, LoginActivity.this);
		}
		
		if (mQQAuth.isSessionValid()) {
			mQQAuth.logout(this);
		}
		
		IUiListener listener = new BaseUiListener() {
			@Override
			protected void doComplete(JSONObject values) {
				// 检查该用户有没有在本地数据库，如果在本地，就直接登录
				// 如果不在本地，就要到服务器上去看该用户有没有注册过
				// 如果有注册过，将该用户的资料下载到本地，然后登录
				// 如果没有注册过，就在服务器上进行注册
				// 将该此的登录信息(expires_in)保存好

				doQQLogin(values);
			}
		};
		mTencent.login(this, "all", listener);
	}
	
	public void attemptWXLogin() {
		api = WXAPIFactory.createWXAPI(this, AppConstants.WX_APP_ID);
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "loginWX";
		api.sendReq(req);
		
	}
	
	public void attemptWBLogin() {
//		if(mWeiboAuth == null){
//			// 获取 Token View，并让提示 View 的内容可滚动（小屏幕可能显示不全）
////	        mTokenText = (TextView) findViewById(R.id.token_text_view);
////	        TextView hintView = (TextView) findViewById(R.id.obtain_token_hint);
////	        hintView.setMovementMethod(new ScrollingMovementMethod());
//
//	        // 创建微博实例
//	        mWeiboAuth = new WeiboAuth(this, AppConstants.WEIBO_CONNECT_APP_KEY, AppConstants.WEIBO_CONNECT_REDIRECT_URL, AppConstants.WEIBO_CONNECT_SCOPE);
	        
	        // SSO 授权
//	        findViewById(R.id.obtain_token_via_sso).setOnClickListener(new OnClickListener() {
//	            @Override
//	            public void onClick(View v) {
	                mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
	                mSsoHandler.authorize(new AuthListener());
	                
//	            }
//	        });
	        
//	        // Web 授权
//	        findViewById(R.id.obtain_token_via_signature).setOnClickListener(new OnClickListener() {
//	            @Override
//	            public void onClick(View v) {
//	                mWeiboAuth.anthorize(new AuthListener());
//	                // 或者使用：mWeiboAuth.authorize(new AuthListener(), Weibo.OBTAIN_AUTH_TOKEN);
//	            }
//	        });
//	        
//	        // 通过 Code 获取 Token
//	        findViewById(R.id.obtain_token_via_code).setOnClickListener(new OnClickListener() {
//	            @Override
//	            public void onClick(View v) {
//	                startActivity(new Intent(LoginActivity.this, WBAuthCodeActivity.class));
//	            }
//	        });

	        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
	        // 第一次启动本应用，AccessToken 不可用
//	        mAccessToken = HyjUtil.readAccessToken();
//	        if (mAccessToken.isSessionValid()) {
////	            updateTokenView(true);
//	        	
//	        }
//		}
	}
	
	 private void doWBLogin(final JSONObject values) {    

 		this.displayProgressDialog(R.string.loginActivity_action_sign_in,
 				R.string.loginActivity_progress_signing_in);
 		HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
 			@Override
 			public void finishCallback(Object object) {
 				if (object != null) {
 					// 在本地找到该QQ用户
// 					String userId = (String) object;
 					
 					loginWBFromServer(false, values);
 					
// 					((HyjApplication) getApplication()).loginQQ(userId, values);
// 					relogin();
// 					LoginActivity.this.dismissProgressDialog();
 				} else {
 					// 在本地找不到该QQ用户，我们到服务器上去找
 					loginWBFromServer(true, values);
 				}
 			}

 			@Override
 			public Object doInBackground(String... string) {
 				File file = new File(getApplicationContext().getFilesDir() + HyjApplication.getInstance().getPackageName() + "/databases/");
 			      if(file.isDirectory()){
 			           File [] fileArray = file.listFiles();
 			           if(null != fileArray && 0 != fileArray.length){
 			                for(int i = 0; i < fileArray.length; i++){

 			    				String dbName = fileArray[i].getName();
 			    				if(dbName.endsWith(".db") || dbName.endsWith("-journal")){
 			    					continue;
 			    				}
 			    				
 			    				Cursor cursor = null;
 			    				SQLiteDatabase rDb = null;
 			    				DatabaseHelper mDbHelper = null;
 			    				try{
 					    				Configuration config = new Configuration.Builder(HyjApplication.getInstance())
 					    											.setDatabaseName(dbName)
 					    											.create(); 
 					    				mDbHelper = new DatabaseHelper(config);
 					    				rDb = mDbHelper.getReadableDatabase();
 		
 					    				// Define a projection that specifies which columns from the
 					    				// database
 					    				// you will actually use after this query.
 					    				String[] projection = { "userId", "openid" };
 					    				String[] args = { values.optString("openid") };
 					    				cursor = rDb.query("QQLogin", 
 					    						projection, // The columns to return
 					    						"openid=?", 
 					    						args, // The values for the WHERE clause
 					    						null, // don't group the rows
 					    						null, // don't filter by row groups
 					    						null // The sort order
 					    				);
 					    				String userId = null;
 					    				if (cursor.getCount() > 0) {
 					    					cursor.moveToFirst();
 					    					userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));
 					    					cursor.close();
 					    					rDb.close();
 					    					mDbHelper.close();
 					    					return userId;
 					    				}
 			    				} catch (Exception e){
 			    					
 			    				} finally{
 			    					if(cursor != null){
 			    						cursor.close();
 			    					}
 			    					if(rDb != null){
 			    						rDb.close();
 			    					}
 			    					if(mDbHelper != null){
 			    						mDbHelper.close();
 			    					}
 			    				}
 			                }
 			           }
 			      }
 				return null;
 			}
 		});

 	}
	
	 private void loginWBFromServer(final boolean createUserDatabaseEntry, final JSONObject loginInfo) {
			if(createUserDatabaseEntry == true){
				java.util.Currency currency = java.util.Currency.getInstance(Locale.getDefault());
				try {
					loginInfo.put("currencyId", currency.getCurrencyCode());
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
			
			// 从服务器上下载用户数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					JSONObject jsonObject = (JSONObject) object;
					String userId;
					try {
						userId = jsonObject.getJSONObject("user").getString("id");

						if (createUserDatabaseEntry == true) {
							final HyjUserDbHelper mDbHelper = new HyjUserDbHelper(
									LoginActivity.this);
							final SQLiteDatabase wDb = mDbHelper
									.getWritableDatabase();
							ContentValues values = new ContentValues();
							values.put(UserDatabaseEntry.COLUMN_NAME_ID, userId);
							values.put(UserDatabaseEntry.COLUMN_NAME_USERNAME, mUserName);

							wDb.insert(UserDatabaseEntry.TABLE_NAME, null, values);
							wDb.close();
							mDbHelper.close();
							loginWBUserFirstTime(userId, HyjUtil.ifNull(jsonObject.getJSONObject("userData").optString("password"), loginInfo.optString("access_token")), jsonObject);
						} else {
							if(((HyjApplication) getApplication()).loginWBFirstTime(userId, HyjUtil.ifNull(jsonObject.getJSONObject("userData").optString("password"), loginInfo.optString("access_token")), jsonObject)){
								HyjApplication.relogin(LoginActivity.this);
							}
							LoginActivity.this.dismissProgressDialog();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void errorCallback(Object object) {
					try {
						JSONObject json = (JSONObject) object;
						LoginActivity.this.dismissProgressDialog();
						LoginActivity.this.displayDialog("登录失败",
							json.getJSONObject("__summary").getString("msg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, loginInfo.toString(), "loginWB");
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
//                updateTokenView(false);
                
            	JSONObject wbJsonObject = new JSONObject();
            	
            	try {
					wbJsonObject.put("openid", values.get("uid"));
					wbJsonObject.put("access_token", values.get("access_token"));
					wbJsonObject.put("expires_in", values.get("expires_in"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	doWBLogin(wbJsonObject);
            	
                // 保存 Token 到 SharedPreferences
                //HyjUtil.writeAccessToken(mAccessToken);
                Toast.makeText(LoginActivity.this, 
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
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
        
        
       
        /**
         * 显示当前 Token 信息。
         * 
         * @param hasExisted 配置文件中是否已存在 token 信息并且合法
         */
//        private void updateTokenView(boolean hasExisted) {
//            String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
//                    new java.util.Date(mAccessToken.getExpiresTime()));
//            String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
////            mTokenText.setText(String.format(format, mAccessToken.getToken(), date));
//            
//            String message = String.format(format, mAccessToken.getToken(), date);
//            if (hasExisted) {
//                message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
//            }
////            mTokenText.setText(message);
//        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, 
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    

	static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try {
//						mUserInfo.setVisibility(android.view.View.VISIBLE);
//						mUserInfo.setText(response.getString("nickname"));
						HyjUtil.displayToast(response.getString("nickname"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else if(msg.what == 1){
				//保存头像
				Bitmap bitmap = (Bitmap)msg.obj;
			}
		}

	};

	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
//			Util.showResultDialog(LoginActivity.this, response.toString(), " ");
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(LoginActivity.this, "出错啦: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			//Util.toastMessage(LoginActivity.this, "onCancel: ");
			Util.dismissDialog();
		}
	}

	private void doQQLogin(final JSONObject values) {    

		this.displayProgressDialog(R.string.loginActivity_action_sign_in,
				R.string.loginActivity_progress_signing_in);
		HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				if (object != null) {
					// 在本地找到该QQ用户
//					String userId = (String) object;
					
					loginQQFromServer(false, values);
					
//					((HyjApplication) getApplication()).loginQQ(userId, values);
//					relogin();
//					LoginActivity.this.dismissProgressDialog();
				} else {
					// 在本地找不到该QQ用户，我们到服务器上去找
					loginQQFromServer(true, values);
				}
			}

			@Override
			public Object doInBackground(String... string) {
				File file = new File(getApplicationContext().getFilesDir() + HyjApplication.getInstance().getPackageName() + "/databases/");
			      if(file.isDirectory()){
			           File [] fileArray = file.listFiles();
			           if(null != fileArray && 0 != fileArray.length){
			                for(int i = 0; i < fileArray.length; i++){

			    				String dbName = fileArray[i].getName();
			    				if(dbName.endsWith(".db") || dbName.endsWith("-journal")){
			    					continue;
			    				}
			    				
			    				Cursor cursor = null;
			    				SQLiteDatabase rDb = null;
			    				DatabaseHelper mDbHelper = null;
			    				try{
					    				Configuration config = new Configuration.Builder(HyjApplication.getInstance())
					    											.setDatabaseName(dbName)
					    											.create(); 
					    				mDbHelper = new DatabaseHelper(config);
					    				rDb = mDbHelper.getReadableDatabase();
		
					    				// Define a projection that specifies which columns from the
					    				// database
					    				// you will actually use after this query.
					    				String[] projection = { "userId", "openid" };
					    				String[] args = { values.optString("openid") };
					    				cursor = rDb.query("QQLogin", 
					    						projection, // The columns to return
					    						"openid=?", 
					    						args, // The values for the WHERE clause
					    						null, // don't group the rows
					    						null, // don't filter by row groups
					    						null // The sort order
					    				);
					    				String userId = null;
					    				if (cursor.getCount() > 0) {
					    					cursor.moveToFirst();
					    					userId = cursor.getString(cursor.getColumnIndexOrThrow("userId"));
					    					cursor.close();
					    					rDb.close();
					    					mDbHelper.close();
					    					return userId;
					    				}
			    				} catch (Exception e){
			    					
			    				} finally{
			    					if(cursor != null){
			    						cursor.close();
			    					}
			    					if(rDb != null){
			    						rDb.close();
			    					}
			    					if(mDbHelper != null){
			    						mDbHelper.close();
			    					}
			    				}
			                }
			           }
			      }
				return null;
			}
		});

	}
	
	private void doLogin() {
		this.displayProgressDialog(R.string.loginActivity_action_sign_in,
				R.string.loginActivity_progress_signing_in);
		HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				if (object != null) {
					String userId = (String) object;
					loginUser(userId);
				} else {
					loginFromServer(true);
				}
			}

			@Override
			public Object doInBackground(String... string) {
				final HyjUserDbHelper mDbHelper = new HyjUserDbHelper(
						LoginActivity.this);
				final SQLiteDatabase rDb = mDbHelper.getReadableDatabase();

				// Define a projection that specifies which columns from the
				// database
				// you will actually use after this query.
				String[] projection = { "id", "userName" };
				String[] args = { mUserName };
				Cursor cursor = rDb.query("UserDatabase", 
						projection, // The columns to return
						"userName=?", 
						args, // The values for the WHERE clause
						null, // don't group the rows
						null, // don't filter by row groups
						null // The sort order
				);
				String userId = null;
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					userId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
					cursor.close();
					rDb.close();
					mDbHelper.close();
					return userId;
				} else {
					cursor.close();
					rDb.close();
					mDbHelper.close();
					return null;
				}
			}
		});

	}

	private void loginUser(String userId) {
		if (((HyjApplication) getApplication()).login(userId, mPassword)) {
			downloadUserData(this, null);
		} else {
			loginFromServer(false);
		}
	}
	
	private void loginUser(String userId, JSONObject jsonUser)
			throws JSONException {
		if (((HyjApplication) getApplication()).login(userId, mPassword,
				jsonUser)) {
			downloadUserData(this, null);
		} else {
			mPasswordView
					.setError(getString(R.string.loginActivity_error_incorrect_password));
			mPasswordView.requestFocus();
			this.dismissProgressDialog();
		}
	}
	
	// 该QQ好友第一次在本机登录，我们需要下载好友数据到本地
	private void loginQQUserFirstTime(String userId, String password, JSONObject jsonUser)
			throws JSONException {
		if (((HyjApplication) getApplication()).loginQQFirstTime(userId, password, jsonUser)) {
			downloadUserData(this, null);
//					new HyjAsyncTaskCallbacks(){
//				@Override
//				public void finishCallback(Object object) {
//					// TODO Auto-generated method stub
////					QQLogin qqLogin = new Select().from(QQLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
////					downloadUserHeadImage(qqLogin.getFigureUrl(), 1);
//				}
//			});

		} else {
			this.dismissProgressDialog();
		}
	}
	
	// 该WB好友第一次在本机登录，我们需要下载好友数据到本地
	private void loginWBUserFirstTime(String userId, String password, JSONObject jsonUser)
			throws JSONException {
		if (((HyjApplication) getApplication()).loginWBFirstTime(userId, password, jsonUser)) {
			downloadUserData(this, null);
//			new HyjAsyncTaskCallbacks(){
//				@Override
//				public void finishCallback(Object object) {
//					// TODO Auto-generated method stub
////					WBLogin wbLogin = new Select().from(WBLogin.class).where("userId=?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
////					downloadUserHeadImage(wbLogin.getProfile_image_url(), 1);
//				}
//			});
		} else {
			this.dismissProgressDialog();
		}
	}
	
	

//	public static void downloadUserHeadImage(String figureUrl, final int sampleSize){
//		if(figureUrl != null && figureUrl.length() != 0){
//		final String figureUrl1 = figureUrl;
//		HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
//			@Override
//			public void finishCallback(Object object) {
//				Bitmap thumbnail = null;
//				if(object != null){
//					thumbnail = (Bitmap) object;
//				}
//				
//				FileOutputStream out;
//				try {
//					Picture figure = new Picture();
//					File imgFile = HyjUtil.createImageFile(figure.getId() + "_icon");
//					if(imgFile != null){
//						out = new FileOutputStream(imgFile);
//						thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
//						out.close();
//						out = null;
//						
//						figure.setRecordId(HyjApplication.getInstance().getCurrentUser().getId());
//						figure.setRecordType("User");
//						figure.setDisplayOrder(0);
//						figure.setPictureType("JPEG");
//						
//						Picture oldPicture = HyjApplication.getInstance().getCurrentUser().getPicture();
//						if(oldPicture != null){
//							oldPicture.delete();
//						}
//						HyjApplication.getInstance().getCurrentUser().setPicture(figure);
//						HyjApplication.getInstance().getCurrentUser().save();
//						figure.save();								
//					}
//				} catch (FileNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public Object doInBackground(String... string) {
//				Bitmap thumbnail = null;
//				thumbnail = Util.getBitmapFromUrl(figureUrl1, sampleSize);
//				return thumbnail;
//			}
//		});
//	}
//	}
	
	public static void downloadUserData(final HyjActivity activity, final HyjAsyncTaskCallbacks callback) {
		User user = HyjApplication.getInstance().getCurrentUser();
		
		MessageBox msgBox = HyjModel.getModel(MessageBox.class,
				user.getMessageBoxId1());
		if (msgBox != null) {
			activity.dismissProgressDialog();
			HyjApplication.relogin(activity);
			return;
		}
		
		// 如果有WIFI，就做一次初始大同步，否则只下载一些必要的数据
		final ConnectivityManager connectivityManager = (ConnectivityManager)HyjApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiNetworkInfo != null && wifiNetworkInfo.isConnected()){
			activity.displayProgressDialog("登录","正在下载用户数据...");
			MainActivity.downloadData(activity, null, new HyjAsyncTaskCallbacks(){
				@Override
				public void finishCallback(Object object) {
					if(callback != null){
						callback.finishCallback(null);
					}
					HyjApplication.relogin(activity);
					activity.dismissProgressDialog();
				}
			});
			return;
		}
		
		// UserData userData = HyjApplication.getInstance().getCurrentUser()
		// .getUserData();

		// 下载一些用户必须的资料
		JSONArray belongsToes = new JSONArray();
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("__dataType", "QQLogin");
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "WBLogin");
			belongsToes.put(jsonObj);
			
			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "WXLogin");
			belongsToes.put(jsonObj);
			
			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "MessageBox");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "Project");
			jsonObj.put("pst.friendUserId", HyjApplication.getInstance()
					.getCurrentUser().getId());
			// jsonObj.put("id", userData.getActiveProjectId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "Event");
			jsonObj.put("pst.friendUserId", HyjApplication.getInstance()
					.getCurrentUser().getId());
			belongsToes.put(jsonObj);
			
			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "EventMember");
//			jsonObj.put("main.friendUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			belongsToes.put(jsonObj);
			
			// jsonObj = new JSONObject();
			// jsonObj.put("__dataType", "ProjectShareAuthorization");
			// jsonObj.put("state", "Accept");
			// jsonObj.put("friendUserId", user.getId());
			//
			// JSONObject notFilter = new JSONObject();
			// notFilter.put("ownerUserId", user.getId());
			// jsonObj.put("__NOT_FILTER__", notFilter);
			// belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "ProjectShareAuthorization");
			// jsonObj.put("ownerUserId", user.getId());
//			JSONObject notFilter = new JSONObject();
//			notFilter.put("ownerUserId", "");
//			jsonObj.put("__NOT_FILTER__", notFilter);
			belongsToes.put(jsonObj);
			
			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "ProjectRemark");
//			 jsonObj.put("ownerUserId", user.getId());
			belongsToes.put(jsonObj);


			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "ParentProject");
			jsonObj.put("parentProjectId", null);
			// jsonObj.put("subProjectId", userData.getActiveProjectId());
//			jsonObj.put("ownerUserId", user.getId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "FriendCategory");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			// jsonObj.put("id", userData.getDefaultFriendCategoryId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "Friend");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			// jsonObj.put("id", userData.getDefaultFriendCategoryId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "Currency");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "Exchange");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "MoneyAccount");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			// jsonObj.put("id", userData.getActiveMoneyAccountId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "MoneyExpenseCategory");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			belongsToes.put(jsonObj);

			jsonObj = new JSONObject();
			jsonObj.put("__dataType", "MoneyIncomeCategory");
//			jsonObj.put("ownerUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
			belongsToes.put(jsonObj);
			
			String userPictureId = HyjApplication.getInstance()
					.getCurrentUser().getPictureId();
			if(userPictureId != null && userPictureId.length() > 0){
				jsonObj = new JSONObject();
				jsonObj.put("__dataType", "UserPicture");
				jsonObj.put("id", userPictureId);
				belongsToes.put(jsonObj);
			}

			// 从服务器上下载基础数据
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					try {
						ActiveAndroid.beginTransaction();
//						String figureUrl = null;
						JSONArray jsonArray = (JSONArray) object;
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONArray array = jsonArray.optJSONArray(i);
							for (int j = 0; j < array.length(); j++) {
								JSONObject obj = array.optJSONObject(j);
								if (obj != null) {
									if(HyjApplication.getIsDebuggable()){
										Log.i("Login Download Data : ",  obj.optString("__dataType"));
									}
									HyjModel model = HyjModel.createModel(obj.optString("__dataType"));
									model.loadFromJSON(obj, true);
									model.save();
									
//									if (obj.optString("__dataType").equals(
//											"MoneyAccount")) {
//										MoneyAccount moneyAccount = new MoneyAccount();
//										moneyAccount.loadFromJSON(obj, true);
//										moneyAccount.save();
//									} else if (obj
//											.optString("__dataType")
//											.equals("ProjectShareAuthorization")) {
//										ProjectShareAuthorization newProjectShareAuthorization = new ProjectShareAuthorization();
//										newProjectShareAuthorization
//												.loadFromJSON(obj, true);
//										newProjectShareAuthorization.save();
//									} else if (obj.optString("__dataType")
//											.equals("Currency")) {
//										Currency newCurrency = new Currency();
//										newCurrency.loadFromJSON(obj, true);
//										newCurrency.save();
//									} else if (obj.optString("__dataType")
//											.equals("ParentProject")) {
//										ParentProject parentProject = new ParentProject();
//										parentProject.loadFromJSON(obj, true);
//										parentProject.save();
//									} else if (obj.optString("__dataType")
//											.equals("FriendCategory")) {
//										FriendCategory friendCategory = new FriendCategory();
//										friendCategory.loadFromJSON(obj, true);
//										friendCategory.save();
//									} else if (obj.optString("__dataType")
//											.equals("Project")) {
//										Project project = new Project();
//										project.loadFromJSON(obj, true);
//										project.save();
//									} else if (obj.optString("__dataType")
//											.equals("MessageBox")) {
//										MessageBox messageBox = new MessageBox();
//										messageBox.loadFromJSON(obj, true);
//										messageBox.save();
//									} else if (obj.optString("__dataType")
//											.equals("QQLogin")) {
//										QQLogin qqLogin = new QQLogin();
//										qqLogin.loadFromJSON(obj, true);
////										if(!obj.isNull("figureUrl")){
////											figureUrl = obj.getString("figureUrl");
////										}
//										qqLogin.save();
//									}  else if (obj.optString("__dataType")
//											.equals("WBLogin")) {
//										WBLogin wbLogin = new WBLogin();
//										wbLogin.loadFromJSON(obj, true);
////										if(!obj.isNull("profile_image_url")){
////											figureUrl = obj.getString("profile_image_url");
////										}
//										wbLogin.save();
//									} else if (obj.optString("__dataType")
//											.equals("WXLogin")) {
//										WXLogin wxLogin = new WXLogin();
//										wxLogin.loadFromJSON(obj, true);
////										if(!obj.isNull("headimgurl")){
////											figureUrl = obj.getString("headimgurl");
////										}
//										wxLogin.save();
//									} else if (obj.optString("__dataType")
//											.equals("Friend")) {
//										Friend friend = new Friend();
//										friend.loadFromJSON(obj, true);
//										friend.save();
//									} else if (obj.optString("__dataType")
//											.equals("Exchange")) {
//										Exchange exchange = new Exchange();
//										exchange.loadFromJSON(obj, true);
//										exchange.save();
//									} else if (obj.optString("__dataType")
//											.equals("MoneyExpenseCategory")) {
//										MoneyExpenseCategory moneyExpenseCategory = new MoneyExpenseCategory();
//										moneyExpenseCategory.loadFromJSON(obj,
//												true);
//										moneyExpenseCategory.save();
//									} else if (obj.optString("__dataType")
//											.equals("MoneyIncomeCategory")) {
//										MoneyIncomeCategory moneyIncomeCategory = new MoneyIncomeCategory();
//										moneyIncomeCategory.loadFromJSON(obj,
//												true);
//										moneyIncomeCategory.save();
//									} else if (obj.optString("__dataType")
//											.equals("ProjectRemark")) {
//										ProjectRemark projectRemark = new ProjectRemark();
//										projectRemark.loadFromJSON(obj,
//												true);
//										projectRemark.save();
//									}
								}
							}
						}

						ActiveAndroid.setTransactionSuccessful();
						ActiveAndroid.endTransaction();


//						if(figureUrl != null){
//							final String figureUrl1 = figureUrl;
//							HyjAsyncTask.newInstance(new HyjAsyncTaskCallbacks() {
//								@Override
//								public void finishCallback(Object object) {
//									Bitmap thumbnail = null;
//									if(object != null){
//										thumbnail = (Bitmap) object;
//									}
//									
//									FileOutputStream out;
//									try {
//										Picture figure = new Picture();
//										File imgFile = HyjUtil.createImageFile(figure.getId() + "_icon");
//										if(imgFile != null){
//											out = new FileOutputStream(imgFile);
//											thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
//											out.close();
//											out = null;
//											
//											figure.setRecordId(HyjApplication.getInstance().getCurrentUser().getId());
//											figure.setRecordType("User");
//											figure.setDisplayOrder(0);
//											figure.setPictureType("JPEG");
//											
//											HyjApplication.getInstance().getCurrentUser().setPicture(figure);
//											HyjApplication.getInstance().getCurrentUser().save();
//											figure.save();								
//										}
//									} catch (FileNotFoundException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (IOException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//			
//								@Override
//								public Object doInBackground(String... string) {
//									Bitmap thumbnail = null;
//									thumbnail = Util.getBitmapFromUrl(figureUrl1, 1);
//									return thumbnail;
//								}
//							});
//						}
						
						
						
						activity.dismissProgressDialog();
						if(callback != null){
							callback.finishCallback(null);
						}
						HyjApplication.relogin(activity);
						
					} catch (Exception e) {
						ActiveAndroid.endTransaction();
						activity.dismissProgressDialog();
					}
				}

				@Override
				public void errorCallback(Object object) {
					activity.dismissProgressDialog();
					try {
						JSONObject json = (JSONObject) object;
						activity.displayDialog(null,
								json.getJSONObject("__summary")
										.getString("msg"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			HyjHttpPostAsyncTask.newInstance(serverCallbacks,
					belongsToes.toString(), "getData");

		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	private void loginFromServer(final boolean createUserDatabaseEntry) {
		// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONObject jsonObject = (JSONObject) object;
				String userId;
				try {
					userId = jsonObject.getJSONObject("user").getString("id");

					if (createUserDatabaseEntry) {
						final HyjUserDbHelper mDbHelper = new HyjUserDbHelper(
								LoginActivity.this);
						final SQLiteDatabase wDb = mDbHelper
								.getWritableDatabase();
						ContentValues values = new ContentValues();
						values.put(UserDatabaseEntry.COLUMN_NAME_ID, userId);
						values.put(UserDatabaseEntry.COLUMN_NAME_USERNAME, mUserName);

						wDb.insert(UserDatabaseEntry.TABLE_NAME, null, values);
						wDb.close();
						mDbHelper.close();
					}
					loginUser(userId, jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void errorCallback(Object object) {
				LoginActivity.this.dismissProgressDialog();
				try {
					JSONObject json = (JSONObject) object;
					LoginActivity.this.displayDialog("登录失败",
							json.getJSONObject("__summary").getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		JSONObject postData = new JSONObject();
		try {
			postData.put("userName", mUserName);
			postData.put("password", mPassword);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		HyjHttpPostAsyncTask.newInstance(serverCallbacks, postData.toString(), "login");
	}
	
	private void loginQQFromServer(final boolean createUserDatabaseEntry, final JSONObject loginInfo) {
	
		
		// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONObject jsonObject = (JSONObject) object;
				String userId;
				try {
					userId = jsonObject.getJSONObject("user").getString("id");

					if (createUserDatabaseEntry == true) {
//						final HyjUserDbHelper mDbHelper = new HyjUserDbHelper(
//								LoginActivity.this);
//						final SQLiteDatabase wDb = mDbHelper
//								.getWritableDatabase();
//						ContentValues values = new ContentValues();
//						values.put(UserDatabaseEntry.COLUMN_NAME_ID, userId);
//						values.put(UserDatabaseEntry.COLUMN_NAME_USERNAME, mUserName);
//
//						wDb.insert(UserDatabaseEntry.TABLE_NAME, null, values);
//						wDb.close();
//						mDbHelper.close();
						loginQQUserFirstTime(userId, HyjUtil.ifNull(jsonObject.getJSONObject("userData").optString("password"), loginInfo.optString("access_token")), jsonObject);
					} else {
						if(((HyjApplication) getApplication()).loginQQFirstTime(userId, HyjUtil.ifNull(jsonObject.getJSONObject("userData").optString("password"), loginInfo.optString("access_token")), jsonObject)){
							HyjApplication.relogin(LoginActivity.this);
						}
						LoginActivity.this.dismissProgressDialog();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void errorCallback(Object object) {
				try {
					JSONObject json = (JSONObject) object;
					LoginActivity.this.dismissProgressDialog();
					LoginActivity.this.displayDialog("登录失败",
						json.getJSONObject("__summary").getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		if(createUserDatabaseEntry == true){
			java.util.Currency currency = java.util.Currency.getInstance(Locale.getDefault());
			try {
				loginInfo.put("currencyId", currency.getCurrencyCode());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		HyjHttpPostAsyncTask.newInstance(serverCallbacks, loginInfo.toString(), "loginQQ");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
//		switch(requestCode){
//	        case GET_WELCOME_MESSAGE:
//	       	 if(resultCode == Activity.RESULT_OK){
//	       		 String welcomeMessage = data.getStringExtra("WELCOME_MESSAGE");
//	       		 LoginActivity.this.displayDialog("欢迎使用iBluetooth", welcomeMessage);
//	       	 }
//	       	 break;
//		}
	}
}
