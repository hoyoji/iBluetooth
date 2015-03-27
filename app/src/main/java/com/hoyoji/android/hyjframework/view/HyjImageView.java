package com.hoyoji.android.hyjframework.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjBitmapWorkerAsyncTask;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.hoyoji.models.Picture;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HyjImageView extends ImageView {
	private String mPictureId = "";
//	private int mBackgroundResource = -1;
	private Drawable mDefaultImage = null;
	private int mDefaultImageId = -1;
	private Resources res;
	
	public HyjImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		res = context.getResources();
//		this.setScaleType(ScaleType.FIT_XY);
//		Resources r = context.getResources();
//		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());
//		this.setLayoutParams(new LayoutParams((int)px, (int)px));
	}
		
	public HyjImageView(Context context) {
		super(context);
		res = context.getResources();
	}
//	public void setBackgroundResource(int resId){
//		if(mBackgroundResource == -1 || resId != mBackgroundResource){
//			mBackgroundResource = resId;
//			this.setBackgroundResource(resId);
//		}
//	}
	public void setImage(Picture picture){
		if(picture == null){
			if(mPictureId != null){
				mPictureId = null;
			}
			if(this.mDefaultImage != null) {
				this.setImageDrawable(this.mDefaultImage);
			} else {
				setImageDrawable(null);
			}
		} else if(picture.getId().equals(mPictureId)){
			return;
		} else {
			mPictureId = picture.getId();
			File f;
				try {
					f = HyjUtil.createImageFile(mPictureId+"_icon", picture.getPictureType());
					if(f != null && f.exists()){
						HyjBitmapWorkerAsyncTask.loadBitmap(f.getAbsolutePath(), this);
					} else {
						// 如果能在cache里找到这张图片，我们直接拿来用，并保存起来
						File dir = HyjApplication.getInstance().getCacheDir();
			    		String[] l = dir.list(new FilenameFilter(){
			    			@Override
			    			public boolean accept(File dir, String filename) {
			    				if(filename.startsWith(mPictureId+"_icon")){
			    					return true;
			    				}
			    				return false;
			    			}
			    		});
			    		
			    		Bitmap bitmap = null;
			    		if(l.length > 0){
			        		bitmap = HyjUtil.decodeSampledBitmapFromFile(dir+"/"+l[0], null, null);
			    		}
			    		if(bitmap != null){
			    		    FileOutputStream out;
		    				out = new FileOutputStream(f);
		    				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		    				out.close();
		    				this.setImageBitmap(bitmap);
		    				
		    				out = null;
		    				f = null;
		    				bitmap = null;
			    		} else {
			    			HyjBitmapWorkerAsyncTask.loadRemoteBitmap(mPictureId, HyjApplication.getServerUrl()+"fetchImageIcon.php", this);
			    		}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	public void setImage(String id){
		if(id != null && mPictureId != null && mPictureId.equals(id)){
			return;
		}

//		mPictureId = id;
		if(id != null){
			setImage((Picture)Picture.getModel(Picture.class, id));
		} else {
			setImage((Picture)null);
		}
	}
	
	public void loadRemoteImage(final String id){
		if(id == null || id.length() == 0 || id.equals("null")){
			setImage((Picture)null);
			return;
		}
		
		if(id.equals(mPictureId)){
			return;
		}
		
		mPictureId = id;
		HyjBitmapWorkerAsyncTask.loadRemoteBitmap(id, HyjApplication.getServerUrl()+"fetchImageIcon.php", this);
		
	}

	public void setDefaultImage(int img) {
		if(img != this.mDefaultImageId){
			this.mDefaultImageId = img;
			mDefaultImage = res.getDrawable(img);
		}
	}
}
