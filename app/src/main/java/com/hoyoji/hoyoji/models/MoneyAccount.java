package com.hoyoji.hoyoji.models;

import java.util.UUID;

import org.json.JSONObject;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

@Table(name = "MoneyAccount", id = BaseColumns._ID)
public class MoneyAccount extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "name")
	private String mName;

	@Column(name = "name_pinYin")
	private String mName_pinYin;
	
	@Column(name = "currencyId")
	private String mCurrencyId;

	@Column(name = "currentBalance")
	private Double mCurrentBalance;

	@Column(name = "remark")
	private String mRemark;

	@Column(name = "sharingType")
	private String mSharingType;

	@Column(name = "accountType")
	private String mAccountType;

	@Column(name = "accountNumber")
	private String mAccountNumber;
	
	@Column(name = "autoHide")
	private String mAutoHide;
	
	@Column(name = "bankAddress")
	private String mBankAddress;

	@Column(name = "friendUserId")
	private String mFriendUserId;
	
	@Column(name = "localFriendId")
	private String mLocalFriendId;
	
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
	
	public MoneyAccount(){
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
		mUUID = UUID.randomUUID().toString();
		mCurrencyId = userData.getActiveCurrencyId();
		mAccountType = "Cash";
		mCurrentBalance = 0.00;
		mSharingType = "Private";
		mAutoHide = "Show";
	}
	
	public String getDisplayName(){
		if(this.get_mId() == null && this.getName() == null){
			return null;
		}
		if(this.getAccountType().equalsIgnoreCase("Debt")){
			if(this.getLocalFriendId() != null){
				Friend friend = HyjModel.getModel(Friend.class, this.getLocalFriendId());
				if(friend != null){
					return friend.getDisplayName();
				}
			} else if(this.getFriendUserId() != null){
//				if(this.getName() != null && this.getName().equalsIgnoreCase("__ANONYMOUS__")){
//					return HyjApplication.getInstance().getString(R.string.app_moneyaccount_anonymous);
//				}
				
				Friend friend = new Select().from(Friend.class).where("friendUserId=?", this.getFriendUserId()).executeSingle();
				if(friend != null){
					return friend.getDisplayName();
				} else {
					User user = HyjModel.getModel(User.class, this.getFriendUserId());
					if(user != null){
						return user.getDisplayName();
					}
				}
			}
		}
		
		return this.getName();
	}
	
	public String getDisplayName_pinYin(){
		if(this.get_mId() == null){
			return null;
		}
		if(this.getAccountType().equalsIgnoreCase("Debt")){
			if(this.getLocalFriendId() != null){
				Friend friend = HyjModel.getModel(Friend.class, this.getLocalFriendId());
				if(friend != null){
					return friend.getDisplayName_pinYin();
				}
			} else if(this.getFriendUserId() != null){
				Friend friend = new Select().from(Friend.class).where("friendUserId=?", this.getFriendUserId()).executeSingle();
				if(friend != null){
					return friend.getDisplayName_pinYin();
				} else {
					User user = HyjModel.getModel(User.class, this.getFriendUserId());
					if(user != null){
						return user.getDisplayName_pinYin();
					}
				}
			}
		}
		
		return this.getName_pinYin();
	}
	
	private String getName_pinYin() {
		return this.mName_pinYin;
	}

	public String getFriendUserId() {
		return this.mFriendUserId;
	}

	public static MoneyAccount getDebtAccount(String currencyId, String localFriendId, String friendUserId){
//		if(friendId == null && friendUserId == null){
//			return new Select().from(MoneyAccount.class).where("accountType=? AND currencyId=? AND friendId IS NULL AND name=?", "Debt", currencyId, "__ANONYMOUS__").executeSingle();
//		}
		
		if(friendUserId == null){
//			Friend friend = HyjModel.getModel(Friend.class, friendId);
//			if(friend != null && friend.getFriendUserId() != null){
//				return new Select().from(MoneyAccount.class).where("accountType=? AND currencyId=? AND name=? AND friendId IS NULL", "Debt", currencyId, friend.getFriendUserId()).executeSingle();
//			} else {
				return new Select().from(MoneyAccount.class).where("accountType=? AND currencyId=? AND friendUserId IS NULL AND localFriendId=?", "Debt", currencyId, localFriendId).executeSingle();
//			}
		} else {	
			return new Select().from(MoneyAccount.class).where("accountType=? AND currencyId=? AND friendUserId=? AND localFriendId IS NULL", "Debt", currencyId, friendUserId).executeSingle();
//			return getDebtAccount(currencyId, friendUserId);
		}
	}
	

//	public static MoneyAccount getDebtAccount(String currencyId, String friendUserId) {
//		return new Select().from(MoneyAccount.class).where("accountType=? AND currencyId=? AND name=? AND friendId IS NULL", "Debt", currencyId, friendUserId).executeSingle();
//	}
	
	public static void createDebtAccount(String friendName, String localFriendId, String friendUserId, String currencyId, String projectOwnerUserId, Double amount){
		MoneyAccount createDebtAccount = new MoneyAccount();
//		String debtAccountName = "__ANONYMOUS__";
//		if(localFriendId != null && friendUserId == null){
//			// 是本地好友
//			// 如果是本地好友，我们将MoneyAccount.friendId设为该好友的Id
//			debtAccountName = friendName;
//		} else if(friendUserId != null){
//			//如果是网络好友，我们将MoneyAccount.name设为该好友的用户ID
//			debtAccountName = friendUserId;
//			localFriendId = null;
//		}
		createDebtAccount.setName(friendName);
		createDebtAccount.setCurrencyId(currencyId);
		createDebtAccount.setCurrentBalance(amount);
		createDebtAccount.setSharingType("Private");
		createDebtAccount.setAccountType("Debt");
		createDebtAccount.setLocalFriendId(localFriendId);
		createDebtAccount.setFriendUserId(friendUserId);
		
		if(createDebtAccount.getLocalFriendId() != null && createDebtAccount.getLocalFriend() == null){
			// 该本地好友不是我的本地好友，是项目拥有者的本地好友
			String projectOwnerUserName = Friend.getFriendUserDisplayName(projectOwnerUserId);
			if(projectOwnerUserName != null){
				createDebtAccount.setRemark("[" + projectOwnerUserName + "]");
			}
		}
		createDebtAccount.save();
	}
	 
//	public static void createDebtAccount(String friendUserId, String currencyId, Double amount){
//		MoneyAccount createDebtAccount = new MoneyAccount();
//		createDebtAccount.setName(friendUserId);
//		createDebtAccount.setCurrencyId(currencyId);
//		createDebtAccount.setCurrentBalance(amount);
//		createDebtAccount.setSharingType("Private");
//		createDebtAccount.setAccountType("Debt");
//		createDebtAccount.save();
//	}
	
	private void setFriendUserId(String friendUserId) {
		this.mFriendUserId = friendUserId;
	}

	@Override
	public void validate(HyjModelEditor modelEditor) {
		if(this.getName().length() == 0){
			modelEditor.setValidationError("name", R.string.moneyAccountFormFragment_editText_hint_name);
		} else {
			modelEditor.removeValidationError("name");
		}
		if(this.getCurrencyId() == null){
			modelEditor.setValidationError("currency", R.string.moneyAccountFormFragment_editText_hint_currency);
		} else {
			modelEditor.removeValidationError("currency");
		}
		if(this.getCurrentBalance() == null){
			modelEditor.setValidationError("currentBalance", R.string.moneyAccountFormFragment_editText_hint_currentBalance);
		} else {
			modelEditor.removeValidationError("currentBalance");
		}
	}

	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		if(mName == null){
			this.mName_pinYin = "";
		} else if(this.mName == null || this.mName == null || !this.mName.equals(mName) || this.mName_pinYin == null){
			this.mName_pinYin = HyjUtil.convertToPinYin(mName);
		}

		this.mName = mName;
	}
	
	public String getCurrencySymbol(){
		Currency currency = getCurrency();
		if(currency == null){
			return this.getCurrencyId();
		}
		return currency.getSymbol();
	}
	
	public Currency getCurrency(){
		if(mCurrencyId == null){
			return null;
		}
		return (Currency) getModel(Currency.class, mCurrencyId);
	}
	
	public void setCurrency(Currency mCurrency) {
		this.mCurrencyId = mCurrency.getId();
	}
	
	public String getCurrencyId() {
		return mCurrencyId;
	}

	public void setCurrencyId(String mCurrencyId) {
		this.mCurrencyId = mCurrencyId;
	}

	public Double getCurrentBalance() {
		return mCurrentBalance;
	}
	
	public Double getCurrentBalance0() {
		if(mCurrentBalance == null){
			return 0.0;
		}
		return mCurrentBalance;
	}

	public void setCurrentBalance(Double mCurrentBalance) {
		if(mCurrentBalance != null) {
			mCurrentBalance = HyjUtil.toFixed2(mCurrentBalance);
		}
		this.mCurrentBalance = mCurrentBalance;
	}

	public String getRemark() {
		return mRemark;
	}

	public void setRemark(String mRemark) {
		this.mRemark = mRemark;
	}

	public String getSharingType() {
		return mSharingType;
	}

	public void setSharingType(String mSharingType) {
		this.mSharingType = mSharingType;
	}

	public String getAccountType() {
		return mAccountType;
	}

	public void setAccountType(String mAccountType) {
		this.mAccountType = mAccountType;
		if(mAccountType.equalsIgnoreCase("Debt") && this.get_mId() == null){
			this.setAutoHide("Auto");
		}
	}

	public String getAccountNumber() {
		return mAccountNumber;
	}

	public void setAccountNumber(String mAccountNumber) {
		this.mAccountNumber = mAccountNumber;
	}

	public String getAutoHide() {
		return mAutoHide;
	}

	public void setAutoHide(String mAutoHide) {
		this.mAutoHide = mAutoHide;
	}

	public String getBankAddress() {
		return mBankAddress;
	}

	public void setBankAddress(String mBankAddress) {
		this.mBankAddress = mBankAddress;
	}

//	public String getFriendId() {
//		if(mFriendId != null && mFriendId.length() == 0){
//			return null;
//		}
//		return mFriendId;
//	}
//
//	public void setFriendId(String mFriendId) {
//		if(mFriendId != null && mFriendId.length() == 0){
//			this.mFriendId = null;
//		} else {
//			this.mFriendId = mFriendId;
//		}
//	}

	public String getLocalFriendId() {
		if(mLocalFriendId != null && mLocalFriendId.length() == 0){
			return null;
		}
		return mLocalFriendId;
	}

	public void setLocalFriendId(String mLocalFriendId) {
		this.mLocalFriendId = mLocalFriendId;
	}

	
	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
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

	public JSONObject toJSON() {
		final JSONObject jsonObj = super.toJSON();
		jsonObj.remove("currentBalance");
		return jsonObj;
	}
	
	public Friend getLocalFriend() {
		if(mLocalFriendId == null || mLocalFriendId.length() == 0){
			return null;
		}
		return HyjModel.getModel(Friend.class, mLocalFriendId);
	}	
	
	@Override 
	public void loadFromJSON(JSONObject json, boolean syncFromServer) {
		super.loadFromJSON(json, syncFromServer);
		if(this.mName_pinYin == null && this.getName() != null){
			this.mName_pinYin = HyjUtil.convertToPinYin(this.getName());
		}
	
	}

}
