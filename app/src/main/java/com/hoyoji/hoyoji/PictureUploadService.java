package com.hoyoji.hoyoji;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaFile;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener.FileProgressListener;
import com.baidu.frontia.api.FrontiaStorageListener.FileTransferListener;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Picture;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class PictureUploadService extends Service {
	public static final String TAG = "PictureUploadService";
	private static int mPictureUploading = 0;
	private static FrontiaStorage mCloudStorage;

	@Override
	public void onCreate() {
		super.onCreate();
		((HyjApplication)this.getApplicationContext()).initFrontia();
		mCloudStorage = Frontia.getStorage();
		
		UpdateReceiver  receiver = new UpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter); //注册,开始接听广播
		
	}
	
	// 接收来自Service的广播消息
	private static class UpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Handler handler = new Handler(Looper
						.getMainLooper());
				handler.postDelayed(new Runnable() {
					public void run() {
						uploadPictures();
					}
				}, 2000);
						
			}
		}

	}
	     
	static void uploadPictures() {
			if (mPictureUploading <= 0) {
						mPictureUploading = 1;
						final ConnectivityManager connectivityManager = (ConnectivityManager)HyjApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						if(wifiNetworkInfo != null && wifiNetworkInfo.isConnected()){
							if(HyjApplication.getInstance().getCurrentUser() == null){
								mPictureUploading = 0;
								return;
							}
							try{
								List<Picture> pics = new Select().from(Picture.class).where("toBeUploaded = ? AND ownerUserId = ? AND lastServerUpdateTime IS NOT NULL", 1, HyjApplication.getInstance().getCurrentUser().getId()).execute();
									mPictureUploading = pics.size();
									for(int i = 0; i < pics.size(); i++){
										final Picture picToUpload = pics.get(i);
										File f = HyjUtil.createImageFile(picToUpload.getId(), picToUpload.getPictureType());
										if(f != null && !f.exists()){
					        				HyjModelEditor<Picture> picEditor = picToUpload.newModelEditor();
					        				picEditor.getModelCopy().setToBeUploaded(false);
					        				picEditor.save();
											mPictureUploading -- ;
										} else {
											// send to cloud storage ...
											final FrontiaFile mFile = new FrontiaFile();
											mFile.setNativePath(f.getAbsolutePath());
											mFile.setRemotePath("/" + f.getName());
											mCloudStorage.uploadFile(mFile,
									                new FileProgressListener() {
									                    @Override
									                    public void onProgress(String source, long bytes, long total) {
									                    }
									                },
									                new FileTransferListener() {
									                    @Override
									                    public void onSuccess(String source, String newTargetName) {
									        				HyjModelEditor<Picture> picEditor = picToUpload.newModelEditor();
									        				picEditor.getModelCopy().setToBeUploaded(false);
									        				picEditor.save();
															mPictureUploading -- ;
									                    }
	
									                    @Override
									                    public void onFailure(String source, int errCode, final String errMsg) {
															//HyjUtil.displayToast("上传大图时遇到错误：" + errMsg);
															mPictureUploading -- ;
									                    }
									                }
									        );
										}
									}
							} catch(Exception e){
								mPictureUploading = 0;
							}
						} else {
							mPictureUploading = 0;
						}
			}
	}

        
	protected static void uploadSingleBigPicture(final Picture picToUpload) throws IOException {

			File f = HyjUtil.createImageFile(picToUpload.getId(), picToUpload.getPictureType());
			if(f != null && f.exists()){
				// send to cloud storage ...
				final FrontiaFile mFile = new FrontiaFile();
				mFile.setNativePath(f.getAbsolutePath());
				mFile.setRemotePath(f.getName());
		    	mCloudStorage.uploadFile(mFile,
		                new FileProgressListener() {
		                    @Override
		                    public void onProgress(String source, long bytes, long total) {
//		                    	mInfoView.setText(source + " upload......:"
//		                                + bytes * 100 / total + "%");
		                    }
		                },
		                new FileTransferListener() {
		                    @Override
		                    public void onSuccess(String source, String newTargetName) {
		                    	mFile.setRemotePath(newTargetName);
//		                        mInfoView.setText(source + " uploaded as "
//		                                + newTargetName + " in the cloud.\n������:������������������������������������������������������������������������������~");

		        				HyjModelEditor<Picture> picEditor = picToUpload.newModelEditor();
		        				picEditor.getModelCopy().setToBeUploaded(false);
		        				picEditor.save();
		                    }

		                    @Override
		                    public void onFailure(String source, int errCode, String errMsg) {
		                    	if(errCode == -1){
			        				HyjModelEditor<Picture> picEditor = picToUpload.newModelEditor();
			        				picEditor.getModelCopy().setToBeUploaded(false);
			        				picEditor.save();
		                    	}
		                    	Log.i(TAG, errMsg);
//		                    	mInfoView.setText(source + " errCode:"
//		                                + errCode + ", errMsg:" + errMsg);
		                    }
		                }
		        );
				
			}
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null && intent.getBooleanExtra("init", false) == false){
			uploadPictures();
		}
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() executed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}