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

@Table(name = "MoneyLend", id = BaseColumns._ID)
public class MoneyLend extends HyjModel{

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
	
	@Column(name = "paybackDate")
	private Long mPaybackDate;
	
	// 记录amount对应的币种，应该和 moneyAccount 的币种一致
	@Column(name = "currencyId")
	private String mCurrencyId;
	
	@Column(name = "projectCurrencyId")
	private String mProjectCurrencyId;
	
	@Column(name = "paybackedAmount")
	private Double mPaybackedAmount;

	// 导入的或新增借入时，替本地好友生成的
	@Column(name = "moneyBorrowId")
	private String mMoneyBorrowId;
	
	@Column(name = "isImported")
	private Boolean mIsImported = false;
	
	// 当预交会费有财务负责人时，会生成多一笔借出（从财务负责人到收款人）
	// 此时，两笔指出将会用这个字段关连
	@Column(name = "moneyDepositExpenseContainerId")
	private String mMoneyDepositExpenseContainerId;
	
	@Column(name = "moneyDepositIncomeApportionId")
	private String mMoneyDepositIncomeApportionId;

	@Column(name = "moneyExpenseApportionId")
	private String mMoneyExpenseApportionId;
	
	@Column(name = "moneyIncomeApportionId")
	private String mMoneyIncomeApportionId;

//	@Column(name = "lendType")
//	private String mLendType;
	
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
	
	public MoneyLend(){
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
		mUUID = UUID.randomUUID().toString();
		this.setMoneyAccount(userData.getActiveMoneyAccount());
		this.setProject(userData.getActiveProject());
		mExchangeRate = 1.00;
		mPaybackedAmount = 0.00;
//		mLendType = "Lend";
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

	public List<MoneyPayback> getMoneyPaybacks(){
		return getMany(MoneyPayback.class, "moneyPaybackId");
	}
	
	public List<MoneyLendApportion> getApportions(){
		return getMany(MoneyLendApportion.class, "moneyLendId");
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
		Double rate = null;
		String userCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
		if (!userCurrencyId.equals(this.getProject().getCurrencyId())) {
			Double exchange = Exchange.getExchangeRate(userCurrencyId, this
					.getProject().getCurrencyId());
			if (exchange != null) {
				rate = exchange;
			}
			if(rate == null){
				return null;
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
//			this.mLocalFriendId= null;
//		}
//		else {
//			this.mFriendUserId = null;
//			this.mLocalFriendId = mFriend.getId();
//		}
//	}
	
	public Friend getLocalFriend(){
		if(mLocalFriendId != null){
			return getModel(Friend.class, mLocalFriendId);
		}
		return null;
	}
	
	public User getFriendUser() {
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

	public void setOwnerFriendId(String ownerFriendId) {
		this.mOwnerFriendId = ownerFriendId;
	}

	public String getOwnerFriendId() {
		return mOwnerFriendId;
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

	public Long getPaybackDate() {
		return mPaybackDate;
	}

	public void setPaybackDate(Long mPaybackDate) {
		this.mPaybackDate = mPaybackDate;
	}

	public Double getPaybackedAmount() {
		return mPaybackedAmount;
	}

	public void setPaybackedAmount(Double mPaybackedAmount) {
		if(mPaybackedAmount != null){
			mPaybackedAmount = HyjUtil.toFixed2(mPaybackedAmount);
		}
		this.mPaybackedAmount = mPaybackedAmount;
	}

//	public String getLendType() {
//		if(mLendType == null){
//			return "Lend";
//		}
//		return mLendType;
//	}
//	
//	public void setLendType(String mLendType) {
//		this.mLendType = mLendType;
//	}
//	
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
			modelEditor.setValidationError("date",R.string.moneyLendFormFragment_editText_hint_date);
		}else{
			modelEditor.removeValidationError("date");
		}
		
		if(this.getAmount() == null){
			modelEditor.setValidationError("amount",R.string.moneyLendFormFragment_editText_hint_amount);
		}else if(this.getAmount() < 0){
			modelEditor.setValidationError("amount",R.string.moneyLendFormFragment_editText_validationError_negative_amount);
		}else if(this.getAmount() > 99999999){
			modelEditor.setValidationError("amount",R.string.moneyLendFormFragment_editText_validationError_beyondMAX_amount);
		}
		else{
			modelEditor.removeValidationError("amount");
		}
		
		if(this.getPaybackDate() != null && this.getPaybackDate() < this.getDate()){
		    modelEditor.setValidationError("paybackDate", R.string.moneyLendFormFragment_editText_validationError_paybackDate_before_date);
		}else{
			modelEditor.removeValidationError("paybackDate");
		}
		
		
		if(this.getMoneyAccountId() == null){
			modelEditor.setValidationError("moneyAccount",R.string.moneyLendFormFragment_editText_hint_moneyAccount);
		}else{
			modelEditor.removeValidationError("moneyAccount");
		}
		
		if(this.getProjectId() == null){
			modelEditor.setValidationError("project",R.string.moneyLendFormFragment_editText_hint_project);
		}else{
			modelEditor.removeValidationError("project");
		}
		
		if(this.getExchangeRate() == null){
			modelEditor.setValidationError("exchangeRate",R.string.moneyLendFormFragment_editText_hint_exchangeRate);
		}else if(this.getExchangeRate() == 0){
			modelEditor.setValidationError("exchangeRate",R.string.moneyLendFormFragment_editText_validationError_zero_exchangeRate);
		}else if(this.getExchangeRate() < 0){
			modelEditor.setValidationError("exchangeRate",R.string.moneyLendFormFragment_editText_validationError_negative_exchangeRate);
		}else if(this.getExchangeRate() > 99999999){
			modelEditor.setValidationError("exchangeRate",R.string.moneyLendFormFragment_editText_validationError_beyondMAX_exchangeRate);
		}
		else{
			modelEditor.removeValidationError("exchangeRate");
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
		if(this.getProject() == null){
			return false;
		}
		if(this.getMoneyAccount() == null){
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
		if(this.getProject() == null){
			return false;
		}
		if(this.getMoneyAccount() == null){
			return false;
		}
		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", this.getProjectId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
		if(psa == null){
			return false;
		}
		return psa.getProjectShareMoneyExpenseDelete();
	}

	public void setMoneyBorrowId(String moneyBorrowId) {
		this.mMoneyBorrowId = moneyBorrowId;
	}

	public void setMoneyExpenseApportionId(String id) {
		this.mMoneyExpenseApportionId = id;
	}
	public String getMoneyExpenseApportionId() {
		return this.mMoneyExpenseApportionId;
	}

	public MoneyExpenseApportion getMoneyExpenseApportion() {
		if(this.mMoneyExpenseApportionId == null){
			return null;
		} 
		return HyjModel.getModel(MoneyExpenseApportion.class, this.mMoneyExpenseApportionId);
	}

	public void setMoneyIncomeApportionId(String id) {
		this.mMoneyIncomeApportionId = id;
	}

	public String getMoneyIncomeApportionId() {
		return this.mMoneyIncomeApportionId;
	}

	public MoneyIncomeApportion getMoneyIncomeApportion() {
		if(this.mMoneyIncomeApportionId == null){
			return null;
		}
		return HyjModel.getModel(MoneyIncomeApportion.class, this.mMoneyIncomeApportionId);
	}
	
	public String getCurrencyId1() {
		return mCurrencyId;
	}

	public void setCurrencyId1(String mCurrencyId) {
		this.mCurrencyId = mCurrencyId;
	}

	public String getProjectCurrencySymbol() {
		if (mProjectCurrencyId == null) {
			return "";
		}
		Currency currency = getModel(Currency.class, mProjectCurrencyId);
		if (currency != null) {
			return currency.getSymbol();
		}
		return mProjectCurrencyId;
	}

	public String getProjectCurrencyId() {
		return this.mProjectCurrencyId;
	}

	public String getFriendDisplayName() {
		String displayName = "";
		if(this.getLocalFriendId() != null){
			Friend f = Friend.getModel(Friend.class, this.getLocalFriendId());
			if(f != null) {
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

	public void setMoneyDepositExpenseContainerId(String id) {
		this.mMoneyDepositExpenseContainerId = id;
	}
	public void setMoneyDepositIncomeApportionId(String id) {
		this.mMoneyDepositIncomeApportionId = id;
	}

	public String getMoneyDepositExpenseContainerId() {
		return this.mMoneyDepositExpenseContainerId;
	}

	public String getMoneyDepositIncomeApportionId() {
		return this.mMoneyDepositIncomeApportionId;
	}

	public void setIsImported(boolean b) {
		this.mIsImported = b;
		
	}

	public MoneyDepositIncomeApportion getMoneyDepositIncomeApportion() {
		if(this.getMoneyDepositIncomeApportionId() == null){
			return null;
		}
		return HyjModel.getModel(MoneyDepositIncomeApportion.class, this.getMoneyDepositIncomeApportionId());
	}

	public MoneyDepositExpenseContainer getMoneyDepositExpenseContainer() {
		if(this.getMoneyDepositExpenseContainerId() == null){
			return null;
		}
		return HyjModel.getModel(MoneyDepositExpenseContainer.class, this.getMoneyDepositExpenseContainerId());
	}

}
