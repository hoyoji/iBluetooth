package com.hoyoji.android.hyjframework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HanziToPinyin.Token;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.ClientSyncRecord;
import com.hoyoji.hoyoji.models.WBLogin;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

public class HyjUtil {
	public static void displayToast(int msg){
		ToastUtils.showMessageLong(HyjApplication.getInstance(), msg);
//		Toast.makeText(HyjApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
	}
	
	public static void displayToast(String msg){
		ToastUtils.showMessageLong(HyjApplication.getInstance(), msg);
//		Toast.makeText(HyjApplication.getInstance(), msg, Toast.LENGTH_LONG).show();
	}
	
	public static void flattenJSONArray(JSONArray array, List<JSONObject> list){
		for (int i = 0; i < array.length(); i++) {
            try {
            	Object o = array.get(i); 
            	if(o instanceof JSONArray){
            		flattenJSONArray((JSONArray) o, list);
            	} else {
            		list.add((JSONObject) o);
            	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
	}
	
	 public static String getSHA1(String s) {
	        try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-1");
	            digest.update(s.getBytes());
	            byte messageDigest[] = digest.digest();
	            return toHexString(messageDigest);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        return "";
	    }
	  
	 public static boolean hasNetworkConnection(){
		 ConnectivityManager connMgr = (ConnectivityManager) HyjApplication
					.getInstance().getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				return true;
			} else {
				return false;
			}
	 }
	 
	    public static String toHexString(byte[] keyData) {
	        if (keyData == null) {
	            return null;
	        }
	        int expectedStringLen = keyData.length * 2;
	        StringBuilder sb = new StringBuilder(expectedStringLen);
	        for (int i = 0; i < keyData.length; i++) {
	            String hexStr = Integer.toString(keyData[i] & 0x00FF,16);
	            if (hexStr.length() == 1) {
	                hexStr = "0" + hexStr;
	            }
	            sb.append(hexStr);
	        }
	        return sb.toString();
	    }
	    
		public static File createImageFile(String imageFileName) throws IOException {
		    // Create an image file name
			File path = HyjApplication.getInstance().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			if(path == null){
				return null;
			}
		    File image = new File(path, imageFileName+".JPEG");

		    return image;
		}
		
		public static File createTempImageFile(String imageFileName) throws IOException {
		    // Create an image file name
			File outputDir = HyjApplication.getInstance().getCacheDir(); // context being the Activity pointer
			return File.createTempFile(imageFileName, ".JPEG", outputDir);
		}
		
		public static File getTempImageFile(String imageFileName) throws IOException {
		    // Create an image file name
			File tempDir = HyjApplication.getInstance().getCacheDir(); // context being the Activity pointer
			File image = new File(tempDir, imageFileName+".JPEG");
			return image;
		}
		
		public static File createImageFile(String imageFileName, String type) throws IOException {
		    // Create an image file name
		    File image = new File(
		    	HyjApplication.getInstance().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
		        imageFileName+"."+type
		    );

		    return image;
		}
		

		public static void startRoateView(View v){
//			if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
				RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				anim.setInterpolator(new LinearInterpolator());
				anim.setRepeatCount(Animation.INFINITE);
				anim.setDuration(1000);
				v.startAnimation(anim);
				
//				Animation rotation = AnimationUtils.loadAnimation(v.getContext(), R.anim.clockwise_rotate);
//			     rotation.setRepeatCount(Animation.INFINITE);
//			     v.startAnimation(rotation);
				
//			} else {
//				RotateDrawable d = (RotateDrawable)v.getDrawable();
//				ObjectAnimator anim = ObjectAnimator.ofInt(d, "Level", 10000);
//				anim.setRepeatCount(ObjectAnimator.INFINITE);
//				anim.setDuration(1000);
//				anim.start();
//			}
		}
		
		public static void stopRoateView(View view){
			view.setAnimation(null);
		}
		
		public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}
		
		public static Bitmap decodeSampledBitmapFromResource(int resId,
				Integer reqWidth, Integer reqHeight) {
			Resources res = HyjApplication.getInstance().getResources();
		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    if(reqWidth != null && reqHeight != null){
			    options.inJustDecodeBounds = true;
			    BitmapFactory.decodeResource(res, resId, options);
	
			    // Calculate inSampleSize
			    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
			    // Decode bitmap with inSampleSize set
			    options.inJustDecodeBounds = false;
		    }
		    options.inPurgeable = true;
		    Bitmap bmp = BitmapFactory.decodeResource(res, resId, options);
		    if(bmp == null){
		    	return HyjUtil.getCommonBitmap(R.drawable.ic_action_refresh);
		    }
		    return bmp;
		}
		
		public static Bitmap decodeSampledBitmapFromFile(String photoPath, Integer targetW, Integer targetH){
		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    if(targetW != null && targetH != null){
			    bmOptions.inJustDecodeBounds = true;
			    BitmapFactory.decodeFile(photoPath, bmOptions);
	
			    // Determine how much to scale down the image
			    bmOptions.inSampleSize = HyjUtil.calculateInSampleSize(bmOptions, targetW, targetH);
	
			    // Decode the image file into a Bitmap sized to fill the View
			    bmOptions.inJustDecodeBounds = false;
		    }
		    bmOptions.inPurgeable = true;
		    Bitmap bmp = BitmapFactory.decodeFile(photoPath, bmOptions);
		    if(bmp == null){
		    	return HyjUtil.getCommonBitmap(R.drawable.ic_action_picture);
		    }
		    return bmp;
		}
		
		static LinkedHashMap<String, Bitmap> commonBitmaps = new LinkedHashMap<String, Bitmap>();
		public static Bitmap getCommonBitmap(int resId) {
			Bitmap bitmap = commonBitmaps.get(String.valueOf(resId));
			if(bitmap == null){
				bitmap = decodeSampledBitmapFromResource(resId, null, null);
				commonBitmaps.put(String.valueOf(resId), bitmap);
			}
			return bitmap;
		}
		
		public static <T extends Object> T ifNull(T obj1, T obj2){
			if(obj1 == null){
				return obj2;
			} else {
				return obj1;
			}
		}
		
		public static String ifEmpty(String obj1, String obj2){
			if(obj1 == null || obj1.length() == 0){
				return obj2;
			} else {
				return obj1;
			}
		}
		
		public static <T extends Object> T ifJSONNull(JSONObject jsonObj, String field1, T obj){
			if(jsonObj.isNull(field1)){
				return obj;
			} else {
				try {
					return (T)jsonObj.opt(field1);
				} catch(Exception e){
					return null;
				}
			}
		}
//		static SimpleDateFormat mIsoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//		public static Date parseDateFromISO(String dateString){
//			try {
//				mIsoDateFormat.setTimeZone(TimeZone.getDefault());
//				return mIsoDateFormat.parse(dateString.replaceAll("Z$", "+0000"));
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//		public static String formatDateToIOS(Date date){
//			mIsoDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//			return mIsoDateFormat.format(date).replaceAll("\\+0000$", "Z");
//		}
		
		public static double toFixed2(Double number){
			return Math.round(number*100)/100.0;
//			BigDecimal bd = new BigDecimal(number);
//			bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);  
//			return bd.doubleValue();
		}
		
		public static double toFixed4(Double number){
			return Math.round(number*10000)/10000.0;
		}
		
		public static void updateClicentSyncRecord(String tableName, String recordId, String operation, String lastSeverUpdateTime, boolean syncFromServer){
			
			if(!tableName.equalsIgnoreCase("ClientSyncRecord")){
				ClientSyncRecord clientSyncRecord = new Select().from(ClientSyncRecord.class).where("id=?", recordId).executeSingle();
				
				if(operation.equalsIgnoreCase("Delete")){
					if(syncFromServer){

						if(clientSyncRecord != null){
							clientSyncRecord.delete();
						}
						
					} else {
					
						if(clientSyncRecord == null){
							clientSyncRecord = new ClientSyncRecord();
							clientSyncRecord.setId(recordId);
							clientSyncRecord.setOperation(operation);
							clientSyncRecord.setTableName(tableName);
							clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
							clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
							clientSyncRecord.save();
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Create")) {
							if(clientSyncRecord.getUploading()){
								// 新记录，正在上传时被删除。如果上传失败，我们会回来删除它
								clientSyncRecord.setOperation(operation);
								clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
								clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
								clientSyncRecord.save();
							} else {
								clientSyncRecord.delete();
							}
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Update")) {
							if(clientSyncRecord.getUploading()){
								clientSyncRecord.setUploading(false);
							}
							clientSyncRecord.setOperation(operation);
							clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
							clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
							clientSyncRecord.save();
						}
					
					}
				} else 
				if(operation.equalsIgnoreCase("Update")){
					if(syncFromServer){
						
						if(clientSyncRecord != null){
							clientSyncRecord.delete();
						}
						
					} else {
					
						if(clientSyncRecord == null){
							clientSyncRecord = new ClientSyncRecord();
							clientSyncRecord.setId(recordId);
							clientSyncRecord.setOperation(operation);
							clientSyncRecord.setTableName(tableName);
							clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
							clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
							clientSyncRecord.save();
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Create")) {
							if(clientSyncRecord.getUploading()){
								// 新记录，正在上传时被更新。如果上传失败，我们会回来将起改回到 "Create"
								clientSyncRecord.setOperation(operation);
								clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
								clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
								clientSyncRecord.save();
							}
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Update")) {
							if(clientSyncRecord.getUploading()){
								clientSyncRecord.setUploading(false);
								clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
								clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
								clientSyncRecord.save();
							}
						}
						
					}
				} else
				if(operation.equalsIgnoreCase("Create")){
					if(syncFromServer){
						
						if(clientSyncRecord != null){
							clientSyncRecord.delete();
						}
						
					} else {
						if(clientSyncRecord == null){
							clientSyncRecord = new ClientSyncRecord();
							clientSyncRecord.setId(recordId);
							clientSyncRecord.setOperation(operation);
							clientSyncRecord.setTableName(tableName);
							clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
							clientSyncRecord.setTransactionId(ActiveAndroid.getTransactionId());
							clientSyncRecord.save();
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Create")) {
							//clientSyncRecord.delete();
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Update")) {
							clientSyncRecord.setOperation(operation);
							clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
							clientSyncRecord.save();
						} else if(clientSyncRecord.getOperation().equalsIgnoreCase("Delete")) {
							clientSyncRecord.setOperation(operation);
							clientSyncRecord.setLastServerUpdateTime(lastSeverUpdateTime);
							clientSyncRecord.save();
						}
					
					}
				}
			}
		}

		public static void updateExchangeRate(final String fromCurrency, final String toCurrency, ImageView mImageViewRefreshRate, HyjNumericField mNumericExchangeRate) {
			final WeakReference<ImageView> refreshRateRefrence = new WeakReference<ImageView>(mImageViewRefreshRate);
			final WeakReference<HyjNumericField> exchangeRateRefrence = new WeakReference<HyjNumericField>(mNumericExchangeRate);
			
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					System.gc();
					ImageView imageViewRefreshRate = refreshRateRefrence.get();
					HyjNumericField numericExchangeRate = exchangeRateRefrence.get();
					if(imageViewRefreshRate != null){
						HyjUtil.stopRoateView(imageViewRefreshRate);
						imageViewRefreshRate.setEnabled(true);
						numericExchangeRate.setEnabled(true);
						numericExchangeRate.setNumber((Double) object);
					}
				}

				@Override
				public void errorCallback(Object object) {
					HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
						@Override
						public void finishCallback(Object object) {
							System.gc();
							ImageView imageViewRefreshRate = refreshRateRefrence.get();
							HyjNumericField numericExchangeRate = exchangeRateRefrence.get();
							if(imageViewRefreshRate != null){
								HyjUtil.stopRoateView(imageViewRefreshRate);
								imageViewRefreshRate.setEnabled(true);
								numericExchangeRate.setEnabled(true);
								numericExchangeRate.setNumber((Double) object);
							}
						}

						@Override
						public void errorCallback(Object object) {
							ImageView imageViewRefreshRate = refreshRateRefrence.get();
							HyjNumericField numericExchangeRate = exchangeRateRefrence.get();
							if(imageViewRefreshRate != null){
								HyjUtil.stopRoateView(imageViewRefreshRate);
								imageViewRefreshRate.setEnabled(true);
								numericExchangeRate.setEnabled(true);
							}
							if (object != null) {
								HyjUtil.displayToast(object.toString());
							} else {
								HyjUtil.displayToast(R.string.moneyExpenseFormFragment_toast_cannot_refresh_rate);
							}
						}
					};
					HyjHttpGetExchangeRateAsyncTask.newInstance(fromCurrency, toCurrency, serverCallbacks);
				}
			};
			HyjWebServiceExchangeRateAsyncTask.newInstance(fromCurrency, toCurrency, serverCallbacks);
		}
		
		public static void detectMemoryLeak(Activity activity) {
//			if(activity == null){
//				return;
//			}
//			
//			final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
//			Handler handler = new Handler(Looper
//					.getMainLooper());
//			handler.postDelayed(new Runnable() {
//				public void run() {
//					System.gc();
//					Activity activity = mActivity.get();
//					if(activity != null){
//						HyjUtil.displayToast("检测到内存泄漏啦... " + Integer.toHexString(activity.hashCode()));
//						detectMemoryLeak(activity);
//					} else {
//						//HyjUtil.displayToast("很好，无内存泄漏！");
//					}
//				}
//			}, 1000);
		}

		public static String convertToPinYin(String mName) {
			if(mName.length() == 0){
				return "";
			}
			ArrayList<Token> tokens = HanziToPinyin.getInstance().get(mName);
	        StringBuilder sb = new StringBuilder();
	        if (tokens != null && tokens.size() > 0) {
	            for (Token token : tokens) {
	                if (Token.PINYIN == token.type) {
	                    sb.append(token.target + " ");
	                } else {
	                    sb.append(token.source);
	                }
	            }
	        }
	        if(sb.length() > 0){
	        	return sb.toString().toUpperCase();
	        } else {
	        	return mName.toUpperCase();
	        }
//			return ContactLocaleUtils.getIntance().getSortKey(mName.toString(), FullNameStyle.CHINESE);
		}
		
		/**
	     * 保存 Token 对象到 User Sqlite Database。
	     * 
	     * @param context 应用程序上下文环境
	     * @param token   Token 对象
	     */
	    public static void writeAccessToken(Oauth2AccessToken token) {
	        WBLogin wbLogin = new Select().from(WBLogin.class).where("userId = ?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
	        wbLogin.setAccessToken(token.getToken());
	        wbLogin.setOpenId(token.getUid());
	        wbLogin.setExpiresIn(token.getExpiresTime());
	        wbLogin.save();
	    }
		
	    /**
	     * 从 SharedPreferences 读取 Token 信息。
	     * 
	     * @param context 应用程序上下文环境
	     * 
	     * @return 返回 Token 对象
	     */
	    public static Oauth2AccessToken readAccessToken() {
	        Oauth2AccessToken token = new Oauth2AccessToken();
	        WBLogin wbLogin = new Select().from(WBLogin.class).where("userId = ?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
	        token.setUid(wbLogin.getOpenId());
	        token.setToken(wbLogin.getAccessToken());
	        token.setExpiresTime(wbLogin.getExpiresIn());
	        return token;
	    }
	    

		public static byte[] getHtmlByteArray(final String url) {
			 URL htmlUrl = null;     
			 InputStream inStream = null;     
			 try {         
				 htmlUrl = new URL(url);         
				 URLConnection connection = htmlUrl.openConnection();         
				 HttpURLConnection httpConnection = (HttpURLConnection)connection;         
				 int responseCode = httpConnection.getResponseCode();         
				 if(responseCode == HttpURLConnection.HTTP_OK){             
					 inStream = httpConnection.getInputStream();         
				  }     
				 } catch (MalformedURLException e) {               
					 e.printStackTrace();     
				 } catch (IOException e) {              
					e.printStackTrace();    
			  } 
			byte[] data = inputStreamToByte(inStream);
			
			return data;
		}
		

		public static byte[] inputStreamToByte(InputStream is) {
			try{
				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
				int ch;
				while ((ch = is.read()) != -1) {
					bytestream.write(ch);
				}
				byte imgdata[] = bytestream.toByteArray();
				bytestream.close();
				return imgdata;
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return null;
		}

		private static HashSet<String> asyncLoading = new HashSet<String>();
		public static void asyncLoad(final String modelName, final String id) {
			if(!asyncLoading.contains(id)){
				asyncLoading.add(id);
				HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
					@Override
					public void finishCallback(Object object) {
						JSONArray array = (JSONArray) object;
						if(array.length() > 0) {
							JSONObject json = array.optJSONArray(0).optJSONObject(0);
							if(json != null){
								HyjModel model = HyjModel.createModel(modelName, id);
								model.loadFromJSON(json, true);
								model.save();
							}
						}
						asyncLoading.remove(id);
					}

					@Override
					public void errorCallback(Object object) {
						asyncLoading.remove(id);
					}
				};
				
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("__dataType", modelName);
					jsonObj.put("id", id);
				} catch (JSONException e) {
				}
				
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + jsonObj.toString() + "]", "getData");
			}
		}
		
		private static HashSet<String> asyncLoadingRecordPictures = new HashSet<String>();
		public static void asyncLoadRecordPictures(final String recordType, final String recordId) {
			if(!asyncLoadingRecordPictures.contains(recordId)){
				asyncLoadingRecordPictures.add(recordId);
				HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
					@Override
					public void finishCallback(Object object) {
						JSONArray pictureArray = (JSONArray)object;
						for(int i=0; i < pictureArray.length(); i++){

							try {
								JSONObject jsonPic = pictureArray.getJSONObject(i);
								String base64PictureIcon = jsonPic.optString("base64PictureIcon");
								if(base64PictureIcon.length() > 0){
									 byte[] decodedByte = Base64.decode(base64PictureIcon, 0);
								    Bitmap icon = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
								    File imageFile = HyjUtil.createImageFile(jsonPic.optString("id")+"_icon");
								    if(imageFile != null){
									    FileOutputStream out = new FileOutputStream(imageFile);
									    icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
									    out.close();
									    out = null;
								    }
								    jsonPic.remove("base64PictureIcon");
								}
								HyjModel newPicture = HyjModel.createModel("Picture", jsonPic.optString("id"));
								newPicture.loadFromJSON(jsonPic, true);
								newPicture.save();
								
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
						}
						if(pictureArray.length() > 0){	
							// 触发更新图片显示
							HyjModel record = HyjModel.createModel(recordType, recordId);
							record.setSyncFromServer(true);
							record.save();
						}
						asyncLoadingRecordPictures.remove(recordId);
					}

					@Override
					public void errorCallback(Object object) {
						asyncLoadingRecordPictures.remove(recordId);
					}
				};
				
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("recordType", recordType);
					jsonObj.put("recordId", recordId);
				} catch (JSONException e) {
				}
				
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, jsonObj.toString(), "fetchRecordPictures");
			}
		}
		public static void asyncLoadPicture(final String id, final String recordType, final String recordId) {
			if(!asyncLoading.contains(id)){
				asyncLoading.add(id);
				HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
					@Override
					public void finishCallback(Object object) {
						JSONArray pictureArray = (JSONArray)object;
						for(int i=0; i < pictureArray.length(); i++){

							try {
								JSONObject jsonPic = pictureArray.getJSONObject(i);
								String base64PictureIcon = jsonPic.optString("base64PictureIcon");
								if(base64PictureIcon.length() > 0){
									 byte[] decodedByte = Base64.decode(base64PictureIcon, 0);
								    Bitmap icon = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
								    File imageFile = HyjUtil.createImageFile(jsonPic.optString("id")+"_icon");
								    if(imageFile != null){
									    FileOutputStream out = new FileOutputStream(imageFile);
									    icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
									    out.close();
									    out = null;
								    }
								    jsonPic.remove("base64PictureIcon");
								}
								HyjModel newPicture = HyjModel.createModel("Picture", jsonPic.optString("id"));
								newPicture.loadFromJSON(jsonPic, true);
								newPicture.save();
								
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
						if(pictureArray.length() > 0){	
							// 触发更新图片显示
							HyjModel record = HyjModel.createModel(recordType, recordId);
							record.setSyncFromServer(true);
							record.save();
						}
						asyncLoading.remove(id);
					}

					@Override
					public void errorCallback(Object object) {
						asyncLoading.remove(id);
					}
				};
				
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("id", id);
					jsonObj.put("recordType", recordType);
					jsonObj.put("recordId", recordId);
				} catch (JSONException e) {
				}
				
				HyjHttpPostAsyncTask.newInstance(serverCallbacks, jsonObj.toString(), "getPicture");
			}
		}

		public static long[] arrayTail(long[] _ids) {
			if(_ids.length == 0){
				return new long[0];
			}
			long tail[] = new long[_ids.length-1];
			for(int i = 1; i < _ids.length; i++){
				tail[i-1] = _ids[i];
			}
			return tail;
		}
}
