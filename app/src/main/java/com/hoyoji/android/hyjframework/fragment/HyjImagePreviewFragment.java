package com.hoyoji.android.hyjframework.fragment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaFile;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener.FileProgressListener;
import com.baidu.frontia.api.FrontiaStorageListener.FileTransferListener;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjBlankUserActivity;
import com.hoyoji.android.hyjframework.view.HyjImagePreview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class HyjImagePreviewFragment extends HyjUserFragment {
	
	
	private ActionBar mActionBar = null;

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		final HyjImagePreview img = new HyjImagePreview(this.getActivity());
		mActionBar  = ((ActionBarActivity)getActivity()).getSupportActionBar();
		Intent intent = this.getActivity().getIntent();
		String pictureName = intent.getStringExtra("pictureName");
		String pictureType = intent.getStringExtra("pictureType");
		
		
		try {
			File icon = HyjUtil.createImageFile(pictureName+"_icon", pictureType);
			img.setImageURI(Uri.fromFile(icon));
			

			final File f = HyjUtil.createImageFile(pictureName, pictureType);
			if(f != null && f.exists()){
				Bitmap bmp = HyjUtil.decodeSampledBitmapFromFile(f.getAbsolutePath(), 400, 600);
		        img.setMaxZoom(4f);
				img.setImageBitmap(bmp);
			} else {
				FrontiaStorage mCloudStorage = Frontia.getStorage();
				FrontiaFile mFile = new FrontiaFile();
				mFile.setNativePath(f.getAbsolutePath());
				mFile.setRemotePath("/" + f.getName());
				mActionBar.setSubtitle("正在下载图片...");
				mCloudStorage.downloadFile(mFile, 
						new FileProgressListener(){
					@Override
					public void onProgress( String arg0, long arg1, long arg2) {
						mActionBar.setSubtitle("正在下载图片..." + (arg1 / arg2 * 100) + "%");
						
					}}, new FileTransferListener(){

						@Override
						public void onFailure( String arg0, int arg1, String arg2) {
							mActionBar.setSubtitle("图片下载失败");
						}

						@Override
						public void onSuccess(String arg0, String arg1) {
							Bitmap bmp = HyjUtil.decodeSampledBitmapFromFile(f.getAbsolutePath(), 400, 600);
					        img.setMaxZoom(4f);
							img.setImageBitmap(bmp);
					        
							mActionBar.setSubtitle(null);
						}});
													                    	

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return img;
	}

	@Override
	public Integer useContentView() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
