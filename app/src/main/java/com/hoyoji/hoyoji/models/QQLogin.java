package com.hoyoji.hoyoji.models;

import java.util.UUID;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import android.provider.BaseColumns;

@Table(name = "QQLogin", id = BaseColumns._ID)
public class QQLogin extends HyjModel {
	
	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "openid", index = true, unique = true)
	private String mOpenId;

	@Column(name = "userId")
	private String mUserId;
	
	@Column(name = "nickName")
	private String mNickName;
	
	@Column(name = "figureUrl")
	private String mFigureUrl;
	
	@Column(name = "access_token")
	private String mAccessToken;
	
	@Column(name = "expires_in")
	private String mExpiresIn;
	
	@Column(name = "pay_token")
	private String mPayToken;
	
	@Column(name = "pf")
	private String mPf;
	
	@Column(name = "pfkey")
	private String mPfkey;
	
	@Column(name = "msg")
	private String mMsg;

	@Column(name = "query_authority_cost")
	private String mQueryAuthorityCost;

	@Column(name = "authority_cost")
	private String mAuthorityCost;

	@Column(name = "login_cost")
	private String mLoginCost;
	
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
	
	
	public QQLogin(){
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
	

	public String getFigureUrl() {
		return mFigureUrl;
	}

	public void setFigureUrl(String mFigureUrl) {
		this.mFigureUrl = mFigureUrl;
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

	public String getExpiresIn() {
		return mExpiresIn;
	}

	public void setExpiresIn(String mExpiresIn) {
		this.mExpiresIn = mExpiresIn;
	}

	public String getPayToken() {
		return mPayToken;
	}

	public void setPhone(String mPayToken) {
		this.mPayToken = mPayToken;
	}

	public String getPf() {
		return mPf;
	}

	public void setPf(String mPf) {
		this.mPf = mPf;
	}
	
	public String getPfkey() {
		return mPfkey;
	}

	public void setPfkey(String mPfkey) {
		this.mPfkey = mPfkey;
	}
	
	public String getActiveProjectId() {
		return mMsg;
	}
	

	public void setMsg(String mMsg) {
		this.mMsg = mMsg;
	}

	public String getQueryAuthorityCost() {
		return mQueryAuthorityCost;
	}
	
	public void setQueryAuthorityCost(String mQueryAuthorityCost) {
		this.mQueryAuthorityCost = mQueryAuthorityCost;
	}
	
	public String getAuthorityCost() {
		return mAuthorityCost;
	}

	public void setAuthorityCost(String mAuthorityCost) {
		this.mAuthorityCost = mAuthorityCost;
	}

	public String getDefaultFriendCategoryId() {
		return mLoginCost;
	}

	public void setLoginCost(String mLoginCost) {
		this.mLoginCost = mLoginCost;
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
