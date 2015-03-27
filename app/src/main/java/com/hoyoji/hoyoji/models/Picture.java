package com.hoyoji.hoyoji.models;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.provider.BaseColumns;
import android.util.Base64;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

@Table(name = "Picture", id = BaseColumns._ID)
public class Picture extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "title")
	private String mTitle;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "path")
	private String mPath;

	@Column(name = "pictureType")
	private String mPictureType;

	@Column(name = "recordId")
	private String mRecordId;

	@Column(name = "recordType")
	private String mRecordType;
	
	@Column(name = "projectId")
	private String mProjectId;

	@Column(name = "toBeUploaded")
	private Boolean mToBeUploaded;

	@Column(name = "toBeDownloaded")
	private Boolean mToBeDownloaded;

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	@Column(name = "displayOrder")
	private Integer mDisplayOrder;
	
	public Picture(){
		super();
		mUUID = UUID.randomUUID().toString();
		this.setToBeUploaded(true);
		this.setToBeDownloaded(false);
		this.setLastServerUpdateTime(null);
	}
	
	@Override
	public void validate(HyjModelEditor modelEditor) {
		if(this.getPath().length() == 0){
			modelEditor.setValidationError("path", R.string.projectFormFragment_editText_hint_projectName);
		} else {
			modelEditor.removeValidationError("path");
		}	
	}

	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}
	
	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		this.mPath = path;
	}

	public String getPictureType() {
		return mPictureType;
	}

	public void setPictureType(String pictureType) {
		this.mPictureType = pictureType;
	}

	public String getRecordId() {
		return mRecordId;
	}

	public void setRecordId(String recordId) {
		this.mRecordId = recordId;
	}

	public String getRecordType() {
		return mRecordType;
	}

	public void setRecordType(String recordType) {
		this.mRecordType = recordType;
	}

	public String getProjectId() {
		return mProjectId;
	}

	public void setProjectId(String projectId) {
		this.mProjectId = projectId;
	}

	public Boolean getToBeUploaded() {
		return mToBeUploaded;
	}

	public void setToBeUploaded(Boolean toBeUploaded) {
		this.mToBeUploaded = toBeUploaded;
	}

	public Boolean getToBeDownloaded() {
		return mToBeDownloaded;
	}

	public void setToBeDownloaded(boolean toBeDownloaded) {
		this.mToBeDownloaded = toBeDownloaded;
	}

	@Override
	public void save(){
		if(this.getOwnerUserId() == null){
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		super.save();
	}	

	public void setCreatorId(String id){
		m_creatorId = id;
	}
	
	public String getCreatorId(){
		return m_creatorId;
	}
	
	public String getServerRecordHash(){
		return mServerRecordHash;
	}

	public void setServerRecordHash(String mServerRecordHash){
		this.mServerRecordHash = mServerRecordHash;
	}

	public String getLastServerUpdateTime(){
		return mLastServerUpdateTime;
	}

	public void setLastServerUpdateTime(String mLastServerUpdateTime){
		this.mLastServerUpdateTime = mLastServerUpdateTime;
	}
	
	public Integer getDisplayOrder(){
		return mDisplayOrder;
	}

	public void setDisplayOrder(Integer mDisplayOrder){
		this.mDisplayOrder = mDisplayOrder;
	}

	public Long getLastClientUpdateTime(){
		return mLastClientUpdateTime;
	}

	public void setLastClientUpdateTime(Long mLastClientUpdateTime){
		this.mLastClientUpdateTime = mLastClientUpdateTime;
	}	
	public JSONObject toJSON() {
		final JSONObject jsonObj = super.toJSON();

		try {

			if(this.getLastServerUpdateTime() == null){
				File f;
				f = HyjUtil.createImageFile(this.getId()+"_icon", this.getPictureType());
				if(f != null){
					Bitmap bmp = HyjUtil.decodeSampledBitmapFromFile(f.getAbsolutePath(), null, null);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
					jsonObj.put("base64PictureIcon", Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
					
					f = null;
					bmp.recycle();
					bmp = null;
					baos.close();
					baos = null;
				}
			}
//			
//			f = HyjUtil.createImageFile(this.getId(), this.getPictureType());
//			bmp = HyjUtil.decodeSampledBitmapFromFile(f.getAbsolutePath(), null, null);
//			baos = new ByteArrayOutputStream();  
//			bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
//			f = null;
//			bmp.recycle();
//			bmp = null;
//			jsonObj.put("base64Picture", Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
//			baos.close();
//			baos = null;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObj;
	}	
	
	@Override
	public void delete(){
		super.delete();

		File f;
		try {
			f = HyjUtil.createImageFile(this.getId()+"_icon", this.getPictureType());
			if(f != null && f.exists()){
				f.delete();
			}
			f = HyjUtil.createImageFile(this.getId(), this.getPictureType());
			if(f != null && f.exists()){
				f.delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void loadFromJSON(JSONObject json, boolean syncFromServer) {
//		super.loadFromJSON(json, syncFromServer);
//
//		try {
//
//			File f;
//		    FileOutputStream out;
//		    Bitmap bmp;
//			if(!json.isNull("base64PictureIcon")){
//				f = HyjUtil.createImageFile(this.getId()+"_icon", this.getPictureType());
//				byte[] decodedByte = Base64.decode(json.getString("base64PictureIcon"), 0);
//			    bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
//			     
//			    out = new FileOutputStream(f);
//				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
//				out.close();
//				out = null;
//				f = null;
//				bmp.recycle();
//				bmp = null;
//			}
//			if(!json.isNull("base64Picture")){
//				f = HyjUtil.createImageFile(this.getId(), this.getPictureType());
//				byte[] decodedByte = Base64.decode(json.getString("base64Picture"), 0);
//			    bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length); 
//			     
//			    out = new FileOutputStream(f);
//				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
//				out.close();
//				out = null;
//				f = null;
//				bmp.recycle();
//				bmp = null;
//			}
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
}
