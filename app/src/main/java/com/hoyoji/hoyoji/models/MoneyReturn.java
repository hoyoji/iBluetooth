package com.hoyoji.hoyoji.models;

import java.util.List;
import java.util.UUID;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

@Table(name = "MoneyReturn", id = BaseColumns._ID)
public class MoneyReturn extends HyjModel{

	@Column(name = "id", index = true, unique = true)
	private String mUUID;
	
	@Column(name = "pictureId")
	private String mPictureId;

	@Column(name = "pictures")
	private String mPictures;
	
	@Column(name = "date")
	private Long mDate;

	@Column(name = "amount")
	private Double mAmount;

	@Column(name = "friendUserId")
	private String mFriendUserId;

	@Column(name = "localFriendId")
	private String mLocalFriendId;

	@Column(name = "friendAccountId")
	private String mFriendAccountId;

	@Column(name = "moneyAccountId")
	private String mMoneyAccountId;
	
	@Column(name = "projectId")
	private String mProjectId;

	@Column(name = "eventId")
	private String mEventId;
	
	@Column(name = "exchangeRate")
	private Double mExchangeRate;
	
	// 记录amount对应的币种，应该和 moneyAccount 的币种一致
	@Column(name = "currencyId")
	private String mCurrencyId;
	
	@Column(name = "projectCurrencyId")
	private String mProjectCurrencyId;
	
	@Column(name = "interest")
	private Double mInterest;
	
//	@Column(name = "moneyBorrowId")
//	private String mMoneyBorrowId;
	
	@Column(name = "isImported")
	private Boolean mIsImported = false;
	
//	@Column(name = "returnType")
//	private String mReturnType;

	// 如果有，说明该还款是从该收款导入生成的
	@Column(name = "moneyPaybackId")
	private String mMoneyPaybackId;

//	@Column(name = "moneyReturnApportionId")
//	private String mMoneyReturnApportionId;

	// 该借入是由 会费退款分摊（MoneyDepositReturnContainer） 生成的
	@Column(name = "moneyDepositReturnApportionId")
	private String mMoneyDepositReturnApportionId;
	
	@Column(name = "remark")
	private String mRemark;

	@Column(name = "lastSyncTime")
	private String mLastSyncTime;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

//	@Column(name = "financialOwnerUserId")
//	private String mFinancialOwnerUserId;
	
	@Column(name = "ownerFriendId")
	private String mOwnerFriendId;

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
	
	public MoneyReturn(){
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
		mUUID = UUID.randomUUID().toString();
		this.setMoneyAccount(userData.getActiveMoneyAccount());
		this.setProject(userData.getActiveProject());
		mExchangeRate = 1.00;
		mInterest = 0.00;
//		mReturnType = "Return";
	}

	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
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
		
		Picture pic = getModel(Picture.class, mPictureId);
		if(pic == null && this.get_mId() != null && !this.isClientNew()){
			HyjUtil.asyncLoadPicture(mPictureId, this.getClass().getSimpleName(), this.getId());
		}
		
		return (Picture) getModel(Picture.class, mPictureId);
	}

	public void setPicture(Picture picture){
		this.setPictureId(picture.getId());
	}
	
	public List<Picture> getPictures(){
		return getMany(Picture.class, "recordId", "displayOrder");
	}

	public List<MoneyReturnApportion> getApportions(){
		return getMany(MoneyReturnApportion.class, "moneyReturnId");
	}
	
	public Long getDate() {
		return mDate;
	}

	public void setDate(Long mDate) {
		this.mDate = mDate;
	}

	public Double getAmount() {
		return mAmount;
	}
	
	public Double getAmount0() {
		if(mAmount == null){
			return 0.00;
		}
		return mAmount;
	}
	
	public Double getLocalAmount(){
		Double rate = 1.0;
		String userCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
		if (!userCurrencyId.equals(this.getProject().getCurrencyId())) {
			Double exchange = Exchange.getExchangeRate(userCurrencyId, this
					.getProject().getCurrencyId());
			if (exchange != null) {
				rate = exchange;
			}
		}
		return this.getAmount0() * this.getExchangeRate() / rate;
	}

	public Double getProjectAmount(){
		return this.getAmount0() * this.getExchangeRate();
	}
	
	public void setAmount(Double mAmount) {
		if(mAmount != null){
			mAmount = HyjUtil.toFixed2(mAmount);
		}
		this.mAmount = mAmount;
	}
	
//	public Friend getFriend(){
//		if(mFriendUserId != null){
//			return new Select().from(Friend.class).where("friendUserId=?",mFriendUserId).executeSingle();
//		}else if(mLocalFriendId != null){
//			return (Friend) getModel(Friend.class, mLocalFriendId);
//		}
//		return null;
//	}
//	
//	public void setFriend(Friend mFriend) {
//		if(mFriend == null){
//			this.mFriendUserId = null;
//			this.mLocalFriendId = null;
//		}else if(mFriend.getFriendUserId() != null){
//			this.mFriendUserId = mFriend.getFriendUserId();
//			this.mLocalFriendId = null;
//		}
//		else {
//			this.mFriendUserId = null;
//			this.mLocalFriendId = mFriend.getId();
//		}
//	}
	
	public Friend getLocalFriend(){
		if(mLocalFriendId != null){
			return  getModel(Friend.class, mLocalFriendId);
		}
		return null;
	}
	
	public User getFriendUser(){
		if(mFriendUserId != null){
			return getModel(User.class, mFriendUserId);
		}
		return null;
	}
	
	public String getFriendUserId() {
		return mFriendUserId;
	}

	public void setFriendUserId(String mFriendUserId) {
		this.mFriendUserId = mFriendUserId;
	}

	public String getLocalFriendId() {
		return mLocalFriendId;
	}

	public void setLocalFriendId(String mLocalFriendId) {
		this.mLocalFriendId = mLocalFriendId;
	}
	public String getOwnerFriendId() {
		return mOwnerFriendId;
	}

	public void setOwnerFriendId(String mOwnerFriendId) {
		this.mOwnerFriendId = mOwnerFriendId;
	}

	public String getFriendAccountId() {
		return mFriendAccountId;
	}

	public void setFriendAccountId(String mFriendAccountId) {
		this.mFriendAccountId = mFriendAccountId;
	}

	public MoneyAccount getMoneyAccount(){
		if(mMoneyAccountId == null){
			return null;
		}
		return (MoneyAccount) getModel(MoneyAccount.class, mMoneyAccountId);
	}

	public void setMoneyAccount(MoneyAccount mMoneyAccount) {
		if(mMoneyAccount == null){
			this.setMoneyAccountId(null, null);
		} else {
			this.setMoneyAccountId(mMoneyAccount.getId(), mMoneyAccount.getCurrencyId());
		}
	}
	
	public String getMoneyAccountId() {
		return mMoneyAccountId;
	}

	public void setMoneyAccountId(String mMoneyAccountId, String currencyId) {
		this.mMoneyAccountId = mMoneyAccountId;
		this.mCurrencyId = currencyId;
	}


	public Project getProject(){
		if(mProjectId == null){
			return null;
		}
		return (Project) getModel(Project.class, mProjectId);
	}
	
	public void setProject(Project mProject) {
		if(mProject == null){
			this.mProjectId = null;
			this.mProjectCurrencyId = null;
			return;
		}
		this.mProjectId = mProject.getId();
		this.mProjectCurrencyId = mProject.getCurrencyId();
	}
	
	public String getProjectId() {
		return mProjectId;
	}

	public void setProjectId(String mProjectId, String projectCurrencyId) {
		this.mProjectId = mProjectId;
		this.mProjectCurrencyId = projectCurrencyId;
	}

	public Event getEvent(){
		if(mEventId == null){
			return null;
		}
		return getModel(Event.class, mEventId);
	}
	
	public void setEvent(Event mEvent) {
		if(mEvent == null){
			this.mEventId = null;
		} else {
			this.mEventId = mEvent.getId();
		}
	}

	public String getEventId() {
		return mEventId;
	}

	public void setEventId(String mEventId) {
		this.mEventId = mEventId;
	}
	
	public Double getExchangeRate() {
		return mExchangeRate;
	}

	public void setExchangeRate(Double mExchangeRate) {
		if(mExchangeRate != null){
			mExchangeRate = HyjUtil.toFixed2(mExchangeRate);
		}
		this.mExchangeRate = mExchangeRate;
	}

	public Double getInterest() {
		return mInterest;
	}
	
	public Double getInterest0() {
		if(mInterest == null){
			return 0.00;
		}
		return mInterest;
	}

	public void setInterest(Double mInterest) {
		if(mInterest != null) {
			mInterest = HyjUtil.toFixed2(mInterest);
		}
		this.mInterest = mInterest;
	}

//	public String getMoneyBorrowId() {
//		return mMoneyBorrowId;
//	}
//
//	public void setMoneyBorrowId(String mMoneyBorrowId) {
//		this.mMoneyBorrowId = mMoneyBorrowId;
//	}
//	
//	public MoneyBorrow getMoneyBorrow(){
//		if(mMoneyBorrowId == null){
//			return null;
//		}
//		return (MoneyBorrow) getModel(MoneyBorrow.class, mMoneyBorrowId);
//	}
//	
//	public void setMoneyBorrow(MoneyBorrow mMoneyBorrow) {
//		this.mMoneyBorrowId = mMoneyBorrow.getId();
//	}
	
	public String getRemark() {
		return mRemark;
	}

	public String getDisplayRemark() {
		String ownerUser = this.getOwnerDisplayName();
		if(ownerUser.length() > 0){
			ownerUser = "[" + ownerUser + "] ";
		} else {
			ownerUser = "";
		}
		
		if(mRemark != null && (mRemark.length() > 0 || ownerUser.length() > 0)){
			return ownerUser + mRemark;
		} else {
			return HyjApplication.getInstance().getString(R.string.app_no_remark);
		}
	}


	public String getOwnerDisplayName() {
		assert(this.get_mId() != null);
		
		String displayName = "";
		if(HyjApplication.getInstance().getCurrentUser().getId().equals(this.getOwnerUserId())){
			return "";
		} else if(this.getOwnerUserId() != null && this.getOwnerUserId().length() != 0){
			displayName = Friend.getFriendUserDisplayName(null, this.getOwnerUserId(), this.getProjectId());
		} else if(this.getOwnerFriendId() != null){
			displayName = Friend.getFriendUserDisplayName(this.getOwnerFriendId(), null, this.getProjectId());
		}
		return displayName;
	}
	public void setRemark(String mRemark) {
		this.mRemark = mRemark;
	}

	public String getLastSyncTime() {
		return mLastSyncTime;
	}

	public void setLastSyncTime(String mLastSyncTime) {
		this.mLastSyncTime = mLastSyncTime;
	}

	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}

//	public String getFinancialOwnerUserId() {
//		return mFinancialOwnerUserId;
//	}
//
//	public void setFinancialOwnerUserId(String financialOwnerUserId) {
//		this.mFinancialOwnerUserId = financialOwnerUserId;
//	}
	
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
		if(this.getDate() == null){
			modelEditor.setValidationError("date",R.string.moneyReturnFormFragment_editText_hint_date);
		}else{
			modelEditor.removeValidationError("date");
		}
		
		if(this.getAmount() == null){
			modelEditor.setValidationError("amount",R.string.moneyReturnFormFragment_editText_hint_amount);
		}else if(this.getAmount() < 0){
			modelEditor.setValidationError("amount",R.string.moneyReturnFormFragment_editText_validationError_negative_amount);
		}else if(this.getAmount() > 99999999){
			modelEditor.setValidationError("amount",R.string.moneyReturnFormFragment_editText_validationError_beyondMAX_amount);
		}
		else{
			modelEditor.removeValidationError("amount");
		}
		
		if(this.getMoneyAccountId() == null){
			modelEditor.setValidationError("moneyAccount",R.string.moneyReturnFormFragment_editText_hint_moneyAccount);
		}else{
			modelEditor.removeValidationError("moneyAccount");
		}
		

		if(this.getProjectId() == null){
			modelEditor.setValidationError("project",R.string.moneyReturnFormFragment_editText_hint_project);
		}else{
			modelEditor.removeValidationError("project");
		}
		
		if(this.getInterest() == null){
			modelEditor.setValidationError("interest",R.string.moneyReturnFormFragment_editText_hint_amount);
		}else if(this.getInterest() < 0){
			modelEditor.setValidationError("interest",R.string.moneyReturnFormFragment_editText_validationError_negative_amount);
		}else if(this.getInterest() > 99999999){
			modelEditor.setValidationError("interest",R.string.moneyReturnFormFragment_editText_validationError_beyondMAX_amount);
		}
		else{
			modelEditor.removeValidationError("interest");
		}
	}

	@Override
	public void save(){
		if(this.getOwnerUserId() == null){
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		super.save();
	}

	public User getOwnerUser() {
		return getModel(User.class, mOwnerUserId);
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
	public boolean hasEditPermission(){
		if(!this.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
			return false;
		}
		
		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", this.getProjectId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
		if(psa == null){
			return false;
		}
		return psa.getProjectShareMoneyExpenseEdit();
	}
	
	public boolean hasAddNewPermission(String projectId){
        if(projectId == null){
            return true;
        }
		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", projectId, HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
		if(psa == null){
			return false;
		}
		return psa.getProjectShareMoneyExpenseAddNew();
	}

	public boolean hasDeletePermission(){
		if(!this.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
			return false;
		}
		
		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", this.getProjectId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
		if(psa == null){
			return false;
		}
		return psa.getProjectShareMoneyExpenseDelete();
	}

	public void setMoneyPaybackId(String moneyPaybackId) {
		this.mMoneyPaybackId = moneyPaybackId;
	}
//	public String getMoneyPaybackId() {
//		return this.mMoneyPaybackId;
//	}
//	public MoneyPayback getMoneyPayback() {
//		if(this.mMoneyPaybackId == null){
//			return null;
//		}
//		return HyjModel.getModel(MoneyPayback.class, this.mMoneyPaybackId);
//	}
	
	public String getCurrencyId1() {
		return mCurrencyId;
	}

	public void setCurrencyId1(String mCurrencyId) {
		this.mCurrencyId = mCurrencyId;
	}


	public void setMoneyDepositReturnApportionId(String id) {
		this.mMoneyDepositReturnApportionId = id;
		
	}

	public String getMoneyDepositReturnApportionId() {
		return mMoneyDepositReturnApportionId;
	}

//	public void setReturnType(String type) {
//		this.mReturnType = type;
//		
//	}
//
//	public String getReturnType() {
//		return this.mReturnType;
//		
//	}

	public MoneyDepositReturnApportion getMoneyDepositReturnApportion() {
		if(mMoneyDepositReturnApportionId != null){
			return HyjModel.getModel(MoneyDepositReturnApportion.class, mMoneyDepositReturnApportionId);
		}
		return null;
	}

	public String getFriendDisplayName() {
		String displayName = "";
		if(this.getLocalFriendId() != null){
			Friend f = Friend.getModel(Friend.class, this.getLocalFriendId());
			if(f != null){
				displayName = f.getDisplayName();
			} else {
				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", this.getLocalFriendId(), this.getProjectId()).executeSingle();
				if(psa != null){
					return psa.getFriendUserName();
				} else {
					return "";
				}
			}
		} else if(this.getFriendUserId() != null){
			displayName = Friend.getFriendUserDisplayName(this.getFriendUserId());
			if(displayName.length() == 0){
				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", this.getFriendUserId(), this.getProjectId()).executeSingle();
				if(psa != null){
					return psa.getFriendUserName();
				} else {
					return "";
				}
			}
//			if(displayName.length() == 0){
//				displayName = "自己";
//			}
		}
		return displayName;
	}

	public void setIsImported(boolean b) {
		this.mIsImported = b;
		
	}

}
