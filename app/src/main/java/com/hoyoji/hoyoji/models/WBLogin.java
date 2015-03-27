package com.hoyoji.hoyoji.models;

import java.util.UUID;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import android.provider.BaseColumns;

@Table(name = "WBLogin", id = BaseColumns._ID)
public class WBLogin extends HyjModel {
	
	@Column(name = "id", index = true, unique = true)
	private String mUUID;
	
	@Column(name = "weiboId")
	private String mWeiboId;

	@Column(name = "weiboIdStr")
	private String mWeiboIdStr;
	
	// User.id
	@Column(name = "userId")
	private String mUserId;
	
	// SINA Weibo uid
	@Column(name = "openid", index = true, unique = true)
	private String mOpenId;

	// SINA Weibo Screen_name
	@Column(name = "nickName")
	private String mNickName;
	
	// SINA Weibo Screen_name
	@Column(name = "name")
	private String mName;
		
	// 
	@Column(name = "access_token")
	private String mAccessToken;
	
	// 
	@Column(name = "expires_in")
	private Long mExpiresIn;
	
//	@Column(name = "remind_in")
//	private String mRemindIn;
	
	@Column(name = "weiboClass")
	private String mWeiboClass;
	
	@Column(name = "province")
	private String mProvince;
		
	@Column(name = "city")
	private String mCity;
	
	@Column(name = "location")
	private String mLocation;
	
	@Column(name = "description")
	private String mDescription;
	
	@Column(name = "url")
	private String mUrl;
	
	@Column(name = "profile_image_url")
	private String mProfile_image_url;
	
	@Column(name = "cover_image_phone")
	private String mCover_image_phone;
	
	@Column(name = "profile_url")
	private String mProfile_url;
	
	@Column(name = "domain")
	private String mDomain;
	
	@Column(name = "weihao")
	private String mWeihao;
	
	@Column(name = "gender")
	private String mGender;
	
	@Column(name = "followers_count")
	private String mFollowers_count;
	
	@Column(name = "friends_count")
	private String mFriends_count;
	
	@Column(name = "statuses_count")
	private String mStatuses_count;
	
	@Column(name = "favourites_count")
	private String mFavourites_count;
	
	@Column(name = "created_at")
	private String mCreated_at;
	
	@Column(name = "following")
	private String mFollowing;
	
	@Column(name = "allow_all_act_msg")
	private String mAllow_all_act_msg;
	
	@Column(name = "geo_enabled")
	private String mGeo_enabled;
	
	@Column(name = "verified")
	private String mVerified;
	
	@Column(name = "verified_type")
	private String mVerified_type;
	
	@Column(name = "remark")
	private String mRemark;
	
	@Column(name = "ptype")
	private String mPtype;
	
	@Column(name = "allow_all_comment")
	private String mAllow_all_comment;
	
	@Column(name = "avatar_large")
	private String mAvatar_large;
	
	@Column(name = "avatar_hd")
	private String mAvatar_hd;
	
	@Column(name = "verified_reason")
	private String mVerified_reason;
	
	@Column(name = "verified_trade")
	private String mVerified_trade;
	
	@Column(name = "verified_reason_url")
	private String mVerified_reason_url;
	
	@Column(name = "verified_source")
	private String mVerified_source;
	
	@Column(name = "verified_source_url")
	private String mVerified_source_url;
	
	@Column(name = "follow_me")
	private String mFollow_me;
	
	@Column(name = "online_status")
	private String mOnline_status;
	
	@Column(name = "bi_followers_count")
	private String mBi_followers_count;
	
	@Column(name = "lang")
	private String mLang;
	
	@Column(name = "mbtype")
	private String mMbtype;
	
	@Column(name = "mbrank")
	private String mMbrank;
	
	@Column(name = "block_word")
	private String mBlock_word;
	
	@Column(name = "block_app")
	private String mBlock_app;
	
//	@Column(name = "pf")
//	private String mPf;
//	
//	@Column(name = "pfkey")
//	private String mPfkey;
//	
//	@Column(name = "msg")
//	private String mMsg;
//
//	@Column(name = "query_authority_cost")
//	private String mQueryAuthorityCost;
//
//	@Column(name = "authority_cost")
//	private String mAuthorityCost;
//
//	@Column(name = "login_cost")
//	private String mLoginCost;
	
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
	
	
	public WBLogin(){
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

	public Long getExpiresIn() {
		return mExpiresIn;
	}

	public void setExpiresIn(long l) {
		this.mExpiresIn = l;
	}

//	public String getPayToken() {
//		return mPayToken;
//	}
//
//	public void setPhone(String mPayToken) {
//		this.mPayToken = mPayToken;
//	}
//
//	public String getPf() {
//		return mPf;
//	}
//
//	public void setPf(String mPf) {
//		this.mPf = mPf;
//	}
//	
//	public String getPfkey() {
//		return mPfkey;
//	}
//
//	public void setPfkey(String mPfkey) {
//		this.mPfkey = mPfkey;
//	}
//	
//	public String getActiveProjectId() {
//		return mMsg;
//	}
//	
//
//	public void setMsg(String mMsg) {
//		this.mMsg = mMsg;
//	}
//
//	public String getQueryAuthorityCost() {
//		return mQueryAuthorityCost;
//	}
//	
//	public void setQueryAuthorityCost(String mQueryAuthorityCost) {
//		this.mQueryAuthorityCost = mQueryAuthorityCost;
//	}
//	
//	public String getAuthorityCost() {
//		return mAuthorityCost;
//	}
//
//	public void setAuthorityCost(String mAuthorityCost) {
//		this.mAuthorityCost = mAuthorityCost;
//	}
//
//	public String getDefaultFriendCategoryId() {
//		return mLoginCost;
//	}
//
//	public void setLoginCost(String mLoginCost) {
//		this.mLoginCost = mLoginCost;
//	}

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


	public String getWeiboId() {
		return mWeiboId;
	}


	public void setWeiboId(String mWeiboId) {
		this.mWeiboId = mWeiboId;
	}


	public String getWeiboIdStr() {
		return mWeiboIdStr;
	}


	public void setWeiboIdStr(String mWeiboIdStr) {
		this.mWeiboIdStr = mWeiboIdStr;
	}


	public String getName() {
		return mName;
	}


	public void setName(String mName) {
		this.mName = mName;
	}


	public String getWeiboClass() {
		return mWeiboClass;
	}


	public void setWeiboClass(String mWeiboClass) {
		this.mWeiboClass = mWeiboClass;
	}


	public String getProvince() {
		return mProvince;
	}


	public void setProvince(String mProvince) {
		this.mProvince = mProvince;
	}


	public String getCity() {
		return mCity;
	}


	public void setCity(String mCity) {
		this.mCity = mCity;
	}


	public String getLocation() {
		return mLocation;
	}


	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}


	public String getDescription() {
		return mDescription;
	}


	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}


	public String getUrl() {
		return mUrl;
	}


	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}


	public String getProfile_image_url() {
		return mProfile_image_url;
	}


	public void setProfile_image_url(String mProfile_image_url) {
		this.mProfile_image_url = mProfile_image_url;
	}


	public String getCover_image_phone() {
		return mCover_image_phone;
	}


	public void setCover_image_phone(String mCover_image_phone) {
		this.mCover_image_phone = mCover_image_phone;
	}


	public String getProfile_url() {
		return mProfile_url;
	}


	public void setProfile_url(String mProfile_url) {
		this.mProfile_url = mProfile_url;
	}


	public String getDomain() {
		return mDomain;
	}


	public void setDomain(String mDomain) {
		this.mDomain = mDomain;
	}


	public String getWeihao() {
		return mWeihao;
	}


	public void setWeihao(String mWeihao) {
		this.mWeihao = mWeihao;
	}


	public String getGender() {
		return mGender;
	}


	public void setGender(String mGender) {
		this.mGender = mGender;
	}


	public String getFollowers_count() {
		return mFollowers_count;
	}


	public void setFollowers_count(String mFollowers_count) {
		this.mFollowers_count = mFollowers_count;
	}


	public String getFriends_count() {
		return mFriends_count;
	}


	public void setFriends_count(String mFriends_count) {
		this.mFriends_count = mFriends_count;
	}


	public String getStatuses_count() {
		return mStatuses_count;
	}


	public void setStatuses_count(String mStatuses_count) {
		this.mStatuses_count = mStatuses_count;
	}


	public String getFavourites_count() {
		return mFavourites_count;
	}


	public void setFavourites_count(String mFavourites_count) {
		this.mFavourites_count = mFavourites_count;
	}


	public String getCreated_at() {
		return mCreated_at;
	}


	public void setCreated_at(String mCreated_at) {
		this.mCreated_at = mCreated_at;
	}


	public String getFollowing() {
		return mFollowing;
	}


	public void setFollowing(String mFollowing) {
		this.mFollowing = mFollowing;
	}


	public String getAllow_all_act_msg() {
		return mAllow_all_act_msg;
	}


	public void setAllow_all_act_msg(String mAllow_all_act_msg) {
		this.mAllow_all_act_msg = mAllow_all_act_msg;
	}


	public String getGeo_enabled() {
		return mGeo_enabled;
	}


	public void setGeo_enabled(String mGeo_enabled) {
		this.mGeo_enabled = mGeo_enabled;
	}


	public String getVerified_type() {
		return mVerified_type;
	}


	public void setVerified_type(String mVerified_type) {
		this.mVerified_type = mVerified_type;
	}


	public String getRemark() {
		return mRemark;
	}


	public void setRemark(String mRemark) {
		this.mRemark = mRemark;
	}


	public String getPtype() {
		return mPtype;
	}


	public void setPtype(String mPtype) {
		this.mPtype = mPtype;
	}


	public String getAllow_all_comment() {
		return mAllow_all_comment;
	}


	public void setAllow_all_comment(String mAllow_all_comment) {
		this.mAllow_all_comment = mAllow_all_comment;
	}


	public String getAvatar_large() {
		return mAvatar_large;
	}


	public void setAvatar_large(String mAvatar_large) {
		this.mAvatar_large = mAvatar_large;
	}


	public String getAvatar_hd() {
		return mAvatar_hd;
	}


	public void setAvatar_hd(String mAvatar_hd) {
		this.mAvatar_hd = mAvatar_hd;
	}


	public String getVerified_reason() {
		return mVerified_reason;
	}


	public void setVerified_reason(String mVerified_reason) {
		this.mVerified_reason = mVerified_reason;
	}


	public String getVerified_trade() {
		return mVerified_trade;
	}


	public void setVerified_trade(String mVerified_trade) {
		this.mVerified_trade = mVerified_trade;
	}


	public String getVerified_reason_url() {
		return mVerified_reason_url;
	}


	public void setVerified_reason_url(String mVerified_reason_url) {
		this.mVerified_reason_url = mVerified_reason_url;
	}


	public String getVerified_source() {
		return mVerified_source;
	}


	public void setVerified_source(String mVerified_source) {
		this.mVerified_source = mVerified_source;
	}


	public String getVerified_source_url() {
		return mVerified_source_url;
	}


	public void setVerified_source_url(String mVerified_source_url) {
		this.mVerified_source_url = mVerified_source_url;
	}


	public String getFollow_me() {
		return mFollow_me;
	}


	public void setFollow_me(String mFollow_me) {
		this.mFollow_me = mFollow_me;
	}


	public String getOnline_status() {
		return mOnline_status;
	}


	public void setOnline_status(String mOnline_status) {
		this.mOnline_status = mOnline_status;
	}


	public String getBi_followers_count() {
		return mBi_followers_count;
	}


	public void setBi_followers_count(String mBi_followers_count) {
		this.mBi_followers_count = mBi_followers_count;
	}


	public String getLang() {
		return mLang;
	}


	public void setLang(String mLang) {
		this.mLang = mLang;
	}


	public String getMbtype() {
		return mMbtype;
	}


	public void setMbtype(String mMbtype) {
		this.mMbtype = mMbtype;
	}


	public String getMbrank() {
		return mMbrank;
	}


	public void setMbrank(String mMbrank) {
		this.mMbrank = mMbrank;
	}


	public String getBlock_word() {
		return mBlock_word;
	}


	public void setBlock_word(String mBlock_word) {
		this.mBlock_word = mBlock_word;
	}


	public String getBlock_app() {
		return mBlock_app;
	}


	public void setBlock_app(String mBlock_app) {
		this.mBlock_app = mBlock_app;
	}	
	
	
	
}
