package com.hoyoji.hoyoji.models;

import java.util.List;
import java.util.UUID;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

import android.provider.BaseColumns;

@Table(name = "User", id = BaseColumns._ID)
public class User extends HyjModel {
	@Column(name = "id", index = true, unique = true)
	private String mUUID;
	
	@Column(name = "userDataId", index = true, unique = true)
	private String mUserDataId;
	
	@Column(name = "userName", index = true, unique = true)
	private String mUserName;
	
	@Column(name = "nickName")
	private String mNickName;
	
	@Column(name = "nickName_pinYin")
	private String mNickName_pinYin;

	@Column(name = "isMerchant")
	private boolean mIsMerchant;
	
	@Column(name = "messageBoxId")
	private String mMessageBoxId;
	
	@Column(name = "newFriendAuthentication")
	private String mNewFriendAuthentication;
	
	@Column(name = "pictureId")
	private String mPictureId;

	@Column(name = "pictures")
	private String mPictures;

	@Column(name = "location")
	private String mLocation;
	
	@Column(name = "geoLon")
	private String mGeoLon;
	
	@Column(name = "geoLat")
	private String mGeoLat;
	
	@Column(name = "address")
	private String mAddress;

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public User(){
		super();
		mUUID = UUID.randomUUID().toString();
	}

	public UserData getUserData(){
		if(mUserDataId == null){
			return null;
		}
		return (UserData) getModel(UserData.class, mUserDataId);
	}

	public void setUserData(UserData userData){
		mUserDataId = userData.getId();
	}
	
	public boolean getIsMerchant(){
		return mIsMerchant;
	}

	@Override
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getUserDataId() {
		return mUserDataId;
	}

	public void setUserDataId(String mUserDataId) {
		this.mUserDataId = mUserDataId;
	}

	public String getUserName() {
		return mUserName;
	}
	
	public String getUserName_pinYin() {
		return HyjUtil.convertToPinYin(mUserName);
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName.trim();
	}

	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String mNickName) {
		if(mNickName == null || mNickName.length() == 0){
			this.mNickName_pinYin = HyjUtil.convertToPinYin(this.getUserName());
		} else if(this.mNickName == null || !this.mNickName.equals(mNickName) || this.mNickName_pinYin == null){
			this.mNickName_pinYin = HyjUtil.convertToPinYin(mNickName);
		}

		if(mNickName.length() == 0){
			this.mNickName = null;
		} else {
			this.mNickName = mNickName;
		}
	}

	public String getDisplayName() {
		if(this.getNickName() != null && this.getNickName().length() != 0){
			return this.getNickName();
		}
		return this.getUserName();
	}
	
	public String getDisplayName_pinYin() {
		if(this.mNickName_pinYin != null && this.mNickName_pinYin.length() != 0){
			return this.mNickName_pinYin;
		}
		return this.getUserName_pinYin();
	}
	
	public boolean ismIsMerchant() {
		return mIsMerchant;
	}

	public void setIsMerchant(boolean mIsMerchant) {
		this.mIsMerchant = mIsMerchant;
	}

	public String getMessageBoxId1() {
		return mMessageBoxId;
	}

//	public void setMessageBoxId(String mMessageBoxId) {
//		this.mMessageBoxId = mMessageBoxId;
//	}

	public boolean getNewFriendAuthentication() {
		if(mNewFriendAuthentication != null && mNewFriendAuthentication.equalsIgnoreCase("none")){
			return false;
		}
		return true;
	}

	public void setNewFriendAuthentication(boolean mNewFriendAuthentication) {
        if(mNewFriendAuthentication){
        	this.mNewFriendAuthentication = "required";
        	return;
        }
        this.mNewFriendAuthentication = "none";
	}

	public String getPictureId() {
		return mPictureId;
	}

	public void setPictureId(String mPictureId) {
		this.mPictureId = mPictureId;
	}
	
	public Picture getPicture(){
		if(mPictureId == null){
			return null;
		}
		return (Picture) getModel(Picture.class, mPictureId);
	}

	public void setPicture(Picture picture){
		this.setPictureId(picture.getId());
	}
	
	public List<Picture> getPictures(){
		return getMany(Picture.class, "recordId", "displayOrder");
	}
	


	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getGeoLon() {
		return mGeoLon;
	}

	public void setGeoLon(String mGeoLon) {
		this.mGeoLon = mGeoLon;
	}

	public String getGeoLat() {
		return mGeoLat;
	}

	public void setGeoLat(String mGeoLat) {
		this.mGeoLat = mGeoLat;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}
	
	public List<Currency> getCurrencies(){
		return getMany(Currency.class, "ownerUserId");
	}

	@Override
	public void validate(HyjModelEditor modelEditor) {
		if(this.getUserName().length() == 0){
			modelEditor.setValidationError("userName", R.string.registerActivity_editText_hint_username);
		} else if(this.getUserName().length() < 3){
			modelEditor.setValidationError("userName", R.string.registerActivity_validation_username_too_short);
		} else {
			modelEditor.removeValidationError("userName");
		}		
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

	public Long getLastClientUpdateTime(){
		return mLastClientUpdateTime;
	}

	public void setLastClientUpdateTime(Long mLastClientUpdateTime){
		this.mLastClientUpdateTime = mLastClientUpdateTime;
	}
	
}
