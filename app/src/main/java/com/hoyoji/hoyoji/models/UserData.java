package com.hoyoji.hoyoji.models;

import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.btcontrol.R;

import android.provider.BaseColumns;

@Table(name = "UserData", id = BaseColumns._ID)
public class UserData extends HyjModel {
	
	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "userId", index = true, unique = true)
	private String mUserId;
	
	@Column(name = "password")
	private String mPassword;
	
	@Column(name = "email")
	private String mEmail;
	
	@Column(name = "emailVerified")
	private Boolean mEmailVerified = false;

	@Column(name = "phone")
	private String mPhone;
	
	@Column(name = "phoneVerified")
	private Boolean mPhoneVerified = false;
	
	@Column(name = "incomeColor")
	private String mIncomeColor;
	
	@Column(name = "expenseColor")
	private String mExpenseColor;
	
	@Column(name = "hasPassword")
	private Boolean mHasPassword;
	
	@Column(name = "activeProjectId")
	private String mActiveProjectId;

	@Column(name = "activeCurrencyId")
	private String mActiveCurrencyId;

	@Column(name = "activeMoneyAccountId")
	private String mActiveMoneyAccountId;

	@Column(name = "defaultFriendCategoryId")
	private String mDefaultFriendCategoryId;

	@Column(name = "lastSyncTime")
	private String mLastSyncTime;

	@Column(name = "lastMessagesDownloadTime")
	private String mLastMessagesDownloadTime;
	
	@Column(name = "ownerUserId")
	private String mOwnerUserId;

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
	
	
	public UserData(){
		super();
		mUUID = UUID.randomUUID().toString();
	}
	
	public List<Currency> getCurrencies(){
		return getMany(Currency.class, "ownerUserId");
	}

	public User getUser(){
		if(mUserId == null){
			return null;
		}
		return (User) getModel(User.class, mUserId);
	}
	
	public void setUser(User user){
		mUserId = user.getId();
	}

	@Override
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String mUserId) {
		this.mUserId = mUserId;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public Boolean ismEmailVerified() {
		return mEmailVerified;
	}

	public void setEmailVerified(boolean mEmailVerified) {
		this.mEmailVerified = mEmailVerified;
	}
	
	public Boolean getHasPassword() {
		if(mHasPassword == null){
			return false;
		}
		return mHasPassword;
	}

	public void setHasPassword(boolean mHasPassword) {
		this.mHasPassword = mHasPassword;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	public String getIncomeColor() {
		if(mIncomeColor == null || mIncomeColor.length() == 0){
			return "#" + Integer.toHexString(HyjApplication.getInstance().getResources().getColor(R.color.hoyoji_red)) ;//"#E95055";
		}
		return mIncomeColor;
	}

	public void setIncomeColor(String mIncomeColor) {
		this.mIncomeColor = "#" + mIncomeColor;
	}
	
	public String getExpenseColor() {
		if(mExpenseColor == null || mExpenseColor.length() == 0){
			return "#" + Integer.toHexString(HyjApplication.getInstance().getResources().getColor(R.color.hoyoji_green)); //"#339900";
		}
		return mExpenseColor;
	}

	public void setExpenseColor(String mExpenseColor) {
		this.mExpenseColor = "#" + mExpenseColor;
	}
	
	public boolean ismPhoneVerified() {
		return mPhoneVerified;
	}

	public void setPhoneVerified(boolean mPhoneVerified) {
		this.mPhoneVerified = mPhoneVerified;
	}

	public String getActiveProjectId() {
//		return mActiveProjectId;
		return null;
	}
	

	public Project getActiveProject() {
//		if(mActiveProjectId == null){
			return null;
//		}
//		return HyjModel.getModel(Project.class, mActiveProjectId);
	}

	public void setActiveProjectId(String mActiveProjectId) {
		this.mActiveProjectId = mActiveProjectId;
	}

	public String getActiveCurrencyId() {
		return mActiveCurrencyId;
	}

	public Currency getActiveCurrency() {
		if(mActiveCurrencyId == null){
			return null;
		}
		return (Currency) getModel(Currency.class, mActiveCurrencyId);
	}
	
	public String getActiveCurrencySymbol() {
		if(mActiveCurrencyId == null){
			return null;
		}
		Currency currency = getModel(Currency.class, mActiveCurrencyId);
		if(currency != null){
			return currency.getSymbol();
		}
		return mActiveCurrencyId;
	}
	
	public void setActiveCurrencyId(String mActiveCurrencyId) {
		this.mActiveCurrencyId = mActiveCurrencyId;
	}

	public MoneyAccount getActiveMoneyAccount(){
		if(mActiveMoneyAccountId == null) {
			return null;
		}
		return HyjModel.getModel(MoneyAccount.class, mActiveMoneyAccountId);
	}
	
	public String getActiveMoneyAccountId() {
		return mActiveMoneyAccountId;
	}

	public void setActiveMoneyAccountId(String mActiveMoneyAccountId) {
		this.mActiveMoneyAccountId = mActiveMoneyAccountId;
	}

	public String getDefaultFriendCategoryId() {
		return mDefaultFriendCategoryId;
	}

	public void setDefaultFriendCategory(String mDefaultFriendCategoryId) {
		this.mDefaultFriendCategoryId = mDefaultFriendCategoryId;
	}

	public String getLastSyncTime() {
		return mLastSyncTime;
	}

	public void setLastSyncTime(String mLastSyncTime) {
		if(mLastSyncTime != null && mLastSyncTime.length() == 0){
			this.mLastSyncTime = null;
		} else {
			this.mLastSyncTime = mLastSyncTime;
		}
	}
	
	public String getLastMessagesDownloadTime() {
		return mLastMessagesDownloadTime;
	}

	public void setLastMessagesDownloadTime(String mLastMessagesDownloadTime) {
		if(mLastMessagesDownloadTime != null && mLastMessagesDownloadTime.length() == 0){
			this.mLastMessagesDownloadTime = null;
		} else {
			this.mLastMessagesDownloadTime = mLastMessagesDownloadTime;
		}
	}

	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
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
	
	@Override
	public void validate(HyjModelEditor modelEditor) {
		if(this.getPassword().length() == 0){
			modelEditor.setValidationError("password", R.string.registerActivity_editText_hint_password1);
		} else if(this.getPassword().length() < 6){
			modelEditor.setValidationError("password", R.string.registerActivity_validation_password_too_short);
		} else {
			modelEditor.removeValidationError("password");
		}
		
	}


	@Override
	public void save(){
		if(this.getOwnerUserId() == null){
			this.setOwnerUserId(this.getUserId());
		}
		super.save();
	}
	
	public JSONObject toJSON() {
		final JSONObject jsonObj = super.toJSON();
		jsonObj.remove("hasPassword");
		jsonObj.remove("password");
		jsonObj.remove("lastMessagesDownloadTime");
		jsonObj.remove("lastSyncTime");
		return jsonObj;
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
