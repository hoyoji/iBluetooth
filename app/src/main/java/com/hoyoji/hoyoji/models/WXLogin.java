package com.hoyoji.hoyoji.models;

import java.util.UUID;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import android.provider.BaseColumns;

@Table(name = "WXLogin", id = BaseColumns._ID)
public class WXLogin extends HyjModel {
	
	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "openid", index = true, unique = true)
	private String mOpenId;

	@Column(name = "userId")
	private String mUserId;
	
	@Column(name = "nickName")
	private String mNickName;
	
	@Column(name = "refresh_token")
	private String mRefreshToken;
	
	@Column(name = "access_token")
	private String mAccessToken;
	
	@Column(name = "expires_in")
	private String mExpiresIn;
	
	@Column(name = "scope")
	private String mScope;
	
	@Column(name = "sex")
	private String mSex;
	
	@Column(name = "province")
	private String mProvince;
	
	@Column(name = "city")
	private String mCity;
	
	@Column(name = "country")
	private String mCountry;

	@Column(name = "headimgurl")
	private String mHeadimgurl;

	@Column(name = "privilege")
	private String mPrivilege;

	@Column(name = "unionid")
	private String mUnionid;
	
	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	
	public WXLogin(){
		super();
		mUUID = UUID.randomUUID().toString();
	}
	

	@Override
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getOpenId() {
		return mOpenId;
	}

	public void setOpenId(String mOpenId) {
		this.mOpenId = mOpenId;
	}

	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String mNickName) {
		this.mNickName = mNickName;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(String mAccessToken) {
		this.mAccessToken = mAccessToken;
	}
	
	public String getScope() {
		return mScope;
	}

	public void setScope(String mScope) {
		this.mScope = mScope;
	}


	public String getSex() {
		return mSex;
	}


	public void setSex(String mSex) {
		this.mSex = mSex;
	}


	public String getProvince() {
		return mProvince;
	}


	public void setProvince(String mProvince) {
		this.mProvince = mProvince;
	}


	public String getExpiresIn() {
		return mExpiresIn;
	}

	public void setExpiresIn(String mExpiresIn) {
		this.mExpiresIn = mExpiresIn;
	}
	
	public String getCity() {
		return mCity;
	}

	public void setCity(String mCity) {
		this.mCity = mCity;
	}
	
	public String getCountry() {
		return mCountry;
	}
	
	public void setCountry(String mCountry) {
		this.mCountry = mCountry;
	}

	public String getHeadimgurl() {
		return mHeadimgurl;
	}
	
	public void setHeadimgurl(String mHeadimgurl) {
		this.mHeadimgurl = mHeadimgurl;
	}
	
	public String getPrivilege() {
		return mPrivilege;
	}

	public void setPrivilege(String mPrivilege) {
		this.mPrivilege = mPrivilege;
	}

	public String getUnionid() {
		return mUnionid;
	}

	public void setUnionid(String mUnionid) {
		this.mUnionid = mUnionid;
	}

	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}
	
	@Override
	public void validate(HyjModelEditor modelEditor) {
		
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

	public Long getLastClientUpdateTime(){
		return mLastClientUpdateTime;
	}

	public void setLastClientUpdateTime(Long mLastClientUpdateTime){
		this.mLastClientUpdateTime = mLastClientUpdateTime;
	}	
	
}
