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
import com.hoyoji.hoyoji.money.MoneyApportionField.ApportionItem;
import com.hoyoji.btcontrol.R;

@Table(name = "MoneyIncomeContainer", id = BaseColumns._ID)
public class MoneyIncomeContainer extends HyjModel {

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

	@Column(name = "incomeType")
	private String mIncomeType;

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

	@Column(name = "moneyIncomeCategory")
	private String mMoneyIncomeCategory;

	@Column(name = "moneyIncomeCategoryMain")
	private String mMoneyIncomeCategoryMain;

	@Column(name = "exchangeRate")
	private Double mExchangeRate;

//	@Column(name = "moneyExpenseId")
//	private String mMoneyExpenseId;
	
	@Column(name = "isImported")
	private Boolean mIsImported = false;
		
	@Column(name = "remark")
	private String mRemark;

	@Column(name = "lastSyncTime")
	private String mLastSyncTime;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "financialOwnerUserId")
	private String mFinancialOwnerUserId;

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

	public MoneyIncomeContainer() {
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser()
				.getUserData();
		mUUID = UUID.randomUUID().toString();
		mIncomeType = "MoneyIncome";
		mMoneyAccountId = userData.getActiveMoneyAccountId();
		this.setProject(userData.getActiveProject());
		mExchangeRate = 1.00;
		if (mProjectId != null) {
			mMoneyIncomeCategory = this.getProject().getDefaultIncomeCategory();
			mMoneyIncomeCategoryMain = this.getProject()
					.getDefaultIncomeCategoryMain();
		}
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

	public Picture getPicture() {
		if (mPictureId == null) {
			return null;
		}
		
		Picture pic = getModel(Picture.class, mPictureId);
		if(pic == null && this.get_mId() != null && !this.isClientNew()){
			HyjUtil.asyncLoadPicture(mPictureId, this.getClass().getSimpleName(), this.getId());
		}
		
		return (Picture) getModel(Picture.class, mPictureId);
	}

	public void setPicture(Picture picture) {
		this.setPictureId(picture.getId());
	}

	public List<Picture> getPictures() {
		return getMany(Picture.class, "recordId");
	}

	public List<MoneyIncomeApportion> getApportions() {
		return getMany(MoneyIncomeApportion.class, "moneyIncomeContainerId");
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
		if (mAmount == null) {
			return 0.00;
		}
		return mAmount;
	}

	public Double getLocalAmount() {
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

	public Double getProjectAmount() {
		return this.getAmount0() * this.getExchangeRate();

	}

	public void setAmount(Double mAmount) {
		if (mAmount != null) {
			mAmount = HyjUtil.toFixed2(mAmount);
		}
		this.mAmount = mAmount;
	}

	public String getIncomeType() {
		return mIncomeType;
	}

	public void setIncomeType(String mIncomeType) {
		this.mIncomeType = mIncomeType;
	}

	public Friend getFriend() {
		if (mFriendUserId != null) {
			return new Select().from(Friend.class)
					.where("friendUserId=?", mFriendUserId).executeSingle();
		} else if (mLocalFriendId != null) {
			return getModel(Friend.class, mLocalFriendId);
		}
		return null;
	}

	public void setFriend(Friend mFriend) {
		if (mFriend == null) {
			this.mFriendUserId = null;
			this.mLocalFriendId = null;
		} else if (mFriend.getFriendUserId() != null) {
			this.mFriendUserId = mFriend.getFriendUserId();
			this.mLocalFriendId = null;
		} else {
			this.mFriendUserId = null;
			this.mLocalFriendId = mFriend.getId();
		}
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

	public String getFriendAccountId() {
		return mFriendAccountId;
	}

	public void setFriendAccountId(String mFriendAccountId) {
		this.mFriendAccountId = mFriendAccountId;
	}

	public MoneyAccount getMoneyAccount() {
		if (mMoneyAccountId == null) {
			return null;
		}
		return (MoneyAccount) getModel(MoneyAccount.class, mMoneyAccountId);
	}

	public void setMoneyAccount(MoneyAccount mMoneyAccount) {
		this.mMoneyAccountId = mMoneyAccount.getId();
	}

	public String getMoneyAccountId() {
		return mMoneyAccountId;
	}

	public void setMoneyAccountId(String mMoneyAccountId) {
		this.mMoneyAccountId = mMoneyAccountId;
	}

	public Project getProject() {
		if (mProjectId == null) {
			return null;
		}
		return (Project) getModel(Project.class, mProjectId);
	}

	public void setProject(Project mProject) {
		if(mProject == null){
			this.mProjectId = null;
		} else {
			this.mProjectId = mProject.getId();
		}
	}

	public String getProjectId() {
		return mProjectId;
	}

	public void setProjectId(String mProjectId) {
		this.mProjectId = mProjectId;
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

	public String getMoneyIncomeCategory() {
		return mMoneyIncomeCategory;
	}

	public void setMoneyIncomeCategory(String mMoneyIncomeCategory) {
		this.mMoneyIncomeCategory = mMoneyIncomeCategory;
	}

	public String getMoneyIncomeCategoryMain() {
		return mMoneyIncomeCategoryMain;
	}

	public void setMoneyIncomeCategoryMain(String mMoneyIncomeCategoryMain) {
		this.mMoneyIncomeCategoryMain = mMoneyIncomeCategoryMain;
	}

	public Double getExchangeRate() {
		return mExchangeRate;
	}

	public void setExchangeRate(Double mExchangeRate) {
		if (mExchangeRate != null) {
			mExchangeRate = HyjUtil.toFixed2(mExchangeRate);
		}
		this.mExchangeRate = mExchangeRate;
	}

	public String getRemark() {
		return mRemark;
	}

	public String getDisplayRemark() {
		String ownerUser = "";
		if(!this.getOwnerUserId().equalsIgnoreCase(HyjApplication.getInstance().getCurrentUser().getId())){
			ownerUser = Friend.getFriendUserDisplayName(this.getOwnerUserId());
			if(ownerUser.length() > 0){
				ownerUser = "[" + ownerUser + "] ";
			}
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

	public String getFinancialOwnerUserId() {
		return mFinancialOwnerUserId;
	}

	public void setFinancialOwnerUserId(String financialOwnerUserId) {
		this.mFinancialOwnerUserId = financialOwnerUserId;
	}
	
	public User getOwnerUser() {
		return getModel(User.class, mOwnerUserId);
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


	public void validate(HyjModelEditor<?> modelEditor) {
		if (this.getDate() == null) {
			modelEditor.setValidationError("date",
					R.string.moneyIncomeFormFragment_editText_hint_date);
		} else {
			modelEditor.removeValidationError("date");
		}

		if (this.getAmount() == null) {
			modelEditor.setValidationError("amount",
					R.string.moneyIncomeFormFragment_editText_hint_amount);
		} else if (this.getAmount() < 0) {
			modelEditor
					.setValidationError(
							"amount",
							R.string.moneyIncomeFormFragment_editText_validationError_negative_amount);
		} else if (this.getAmount() > 99999999) {
			modelEditor
					.setValidationError(
							"amount",
							R.string.moneyIncomeFormFragment_editText_validationError_beyondMAX_amount);
		} else {
			modelEditor.removeValidationError("amount");
		}

		if (this.getMoneyIncomeCategory() == null
				|| this.getMoneyIncomeCategory().length() == 0) {
			modelEditor
					.setValidationError(
							"moneyIncomeCategory",
							R.string.moneyIncomeFormFragment_editText_hint_moneyIncomeCategory);
		} else {
			modelEditor.removeValidationError("moneyIncomeCategory");
		}

		if (this.getExchangeRate() == null) {
			modelEditor
					.setValidationError(
							"exchangeRate",
							R.string.moneyIncomeFormFragment_editText_hint_exchangeRate);
		} else if (this.getExchangeRate() == 0) {
			modelEditor
					.setValidationError(
							"exchangeRate",
							R.string.moneyIncomeFormFragment_editText_validationError_zero_exchangeRate);
		} else if (this.getExchangeRate() < 0) {
			modelEditor
					.setValidationError(
							"exchangeRate",
							R.string.moneyIncomeFormFragment_editText_validationError_negative_exchangeRate);
		} else if (this.getExchangeRate() > 99999999) {
			modelEditor
					.setValidationError(
							"exchangeRate",
							R.string.moneyIncomeFormFragment_editText_validationError_beyondMAX_exchangeRate);
		} else {
			modelEditor.removeValidationError("exchangeRate");
		}

		if (this.getMoneyAccountId() == null) {
			modelEditor
					.setValidationError(
							"moneyAccount",
							R.string.moneyIncomeFormFragment_editText_hint_moneyAccount);
		} else {
			modelEditor.removeValidationError("moneyAccount");
		}
		if (this.getMoneyIncomeCategory() == null
				|| this.getMoneyIncomeCategory().length() == 0) {
			modelEditor
					.setValidationError(
							"moneyIncomeCategory",
							R.string.moneyIncomeFormFragment_editText_hint_moneyIncomeCategory);
		} else {
			modelEditor.removeValidationError("moneyIncomeCategory");
		}
		if (this.getProjectId() == null) {
			modelEditor.setValidationError("project",
					R.string.moneyIncomeFormFragment_editText_hint_project);
		} else {
			modelEditor.removeValidationError("project");
		}
	}

	@Override
	public void save() {
		if (this.getOwnerUserId() == null) {
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser()
					.getId());
		}
		super.save();
	}

	public void setCreatorId(String id) {
		m_creatorId = id;
	}

	public String getCreatorId() {
		return m_creatorId;
	}

	public String getServerRecordHash() {
		return mServerRecordHash;
	}

	public void setServerRecordHash(String mServerRecordHash) {
		this.mServerRecordHash = mServerRecordHash;
	}

	public String getLastServerUpdateTime() {
		return mLastServerUpdateTime;
	}

	public void setLastServerUpdateTime(String mLastServerUpdateTime) {
		this.mLastServerUpdateTime = mLastServerUpdateTime;
	}

	public Long getLastClientUpdateTime() {
		return mLastClientUpdateTime;
	}

	public void setLastClientUpdateTime(Long mLastClientUpdateTime) {
		this.mLastClientUpdateTime = mLastClientUpdateTime;
	}

	public boolean hasEditPermission() {
		if (!this.getOwnerUserId().equals(
				HyjApplication.getInstance().getCurrentUser().getId())) {
			return false;
		}

		ProjectShareAuthorization psa = new Select()
				.from(ProjectShareAuthorization.class)
				.where("projectId = ? AND friendUserId=?", this.getProjectId(),
						HyjApplication.getInstance().getCurrentUser().getId())
				.executeSingle();
		if (psa == null) {
			return false;
		}
		return psa.getProjectShareMoneyExpenseEdit();
	}

	public boolean hasAddNewPermission(String projectId) {
        if(projectId == null){
            return true;
        }
		ProjectShareAuthorization psa = new Select()
				.from(ProjectShareAuthorization.class)
				.where("projectId = ? AND friendUserId=?", projectId,
						HyjApplication.getInstance().getCurrentUser().getId())
				.executeSingle();
		if (psa == null) {
			return false;
		}
		return psa.getProjectShareMoneyExpenseAddNew();
	}

	public boolean hasDeletePermission() {
		if (!this.getOwnerUserId().equals(
				HyjApplication.getInstance().getCurrentUser().getId())) {
			return false;
		}

		ProjectShareAuthorization psa = new Select()
				.from(ProjectShareAuthorization.class)
				.where("projectId = ? AND friendUserId=?", this.getProjectId(),
						HyjApplication.getInstance().getCurrentUser().getId())
				.executeSingle();
		if (psa == null) {
			return false;
		}
		return psa.getProjectShareMoneyExpenseDelete();
	}

//	public void setMoneyExpenseId(String moneyExpenseId) {
//		this.mMoneyExpenseId = moneyExpenseId;
//	}
//
//	public String getMoneyExpenseId() {
//		return this.mMoneyExpenseId;
//	}

	public void setIsImported(boolean b) {
		this.mIsImported = b;
	}

	public Boolean getIsImported() {
		return this.mIsImported;
	}
	

	// inner class
	public static class MoneyIncomeContainerEditor extends HyjModelEditor<MoneyIncomeContainer> {
		private  ProjectShareAuthorization mOldProjectShareAuthorization;
		private  ProjectShareAuthorization mNewProjectShareAuthorization;
		
		public MoneyIncomeContainerEditor(MoneyIncomeContainer model) {
			super(model);
		}
		
		public ProjectShareAuthorization getOldSelfProjectShareAuthorization(){
			if(mOldProjectShareAuthorization == null){
				return new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=?", this.getModel().getOwnerUserId(), this.getModel().getProjectId()).executeSingle();
			}
			return mOldProjectShareAuthorization;
		}
		
		public ProjectShareAuthorization getNewSelfProjectShareAuthorization(){
			if(mNewProjectShareAuthorization == null){
				return new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=?", HyjApplication.getInstance().getCurrentUser().getId(), this.getModelCopy().getProjectId()).executeSingle();
			}
			return mNewProjectShareAuthorization;
		}
	}

	public static void deleteApportion(MoneyIncomeApportion apportion, MoneyIncomeContainerEditor mMoneyIncomeContainerEditor){
		ProjectShareAuthorization oldProjectShareAuthorization;
		if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
			// 更新旧圈子的分摊支出
			oldProjectShareAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
			HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
			oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorization.getApportionedTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorization.getActualTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.save();
			
			MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
			if(moneyIncome != null){
				moneyIncome.delete();
			}
		} else {
			// 更新旧圈子分摊支出
			oldProjectShareAuthorization = apportion.getProjectShareAuthorization();
			HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
			
			oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorization.getApportionedTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorization.getActualTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
		
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorization.getActualTotalLend() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.save();
			
			oldProjectShareAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
			oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorization.getActualTotalBorrow() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.save();
		}
		
		if(mMoneyIncomeContainerEditor.getModel().getEventId() != null){
			//更新老圈子分摊支出
			EventMember oldEventMember;
			if(apportion.getFriendUserId() != null){
				oldEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
						mMoneyIncomeContainerEditor.getModel().getEventId(), apportion.getFriendUserId()).executeSingle();
			} else {
				oldEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
						mMoneyIncomeContainerEditor.getModel().getEventId(), apportion.getLocalFriendId()).executeSingle();
			}
			if(oldEventMember != null){
				HyjModelEditor<EventMember> oldEventMemberEditor = oldEventMember.newModelEditor();
				oldEventMemberEditor.getModelCopy().setApportionedTotalIncome(oldEventMember.getApportionedTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
				oldEventMemberEditor.save();
			}
		}
		
		MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
		if(moneyIncome != null){
			moneyIncome.delete();
		}
		
		List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=?", apportion.getId()).execute();
		for(MoneyLend moneyLend : moneyLends){
			moneyLend.delete();
		} 

		List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=?", apportion.getId()).execute();
		for(MoneyBorrow moneyBorrow : moneyBorrows){
			moneyBorrow.delete();
		}
		
		apportion.delete();
	}
	
	public static int saveApportions(List<ApportionItem> apportionItems,
			MoneyIncomeContainerEditor mMoneyIncomeContainerEditor) {
		int count = apportionItems.size();
		int savedCount = 0;
		for (int i = 0; i < count; i++) {
			ApportionItem<MoneyApportion> api = apportionItems.get(i);
			MoneyIncomeApportion apportion = (MoneyIncomeApportion) api.getApportion();
			HyjModelEditor<MoneyIncomeApportion> apportionEditor = apportion.newModelEditor();
            
				if(api.getState() == ApportionItem.DELETED ){
					deleteApportion(apportion, mMoneyIncomeContainerEditor);
				} else {
					if(api.getState() != ApportionItem.UNCHANGED
						|| !mMoneyIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyIncomeContainerEditor.getModel().getProjectId())
						|| !mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyIncomeContainerEditor.getModel().getMoneyAccountId())) {
						api.saveToCopy(apportionEditor.getModelCopy());
					}
					Double oldRate = mMoneyIncomeContainerEditor.getModel().getExchangeRate(); 
					Double rate = mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate();
					Double oldApportionAmount = apportionEditor.getModel().getAmount0();
					if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null){
						oldApportionAmount = 0.0;
					}
					ProjectShareAuthorization projectShareAuthorization;
						//维护圈子成员金额
					if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
						projectShareAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
					} else if(apportion.getLocalFriendId() != null){
						projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
								mMoneyIncomeContainerEditor.getModelCopy().getProjectId(), apportion.getLocalFriendId()).executeSingle();
					} else {
						projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
								mMoneyIncomeContainerEditor.getModelCopy().getProjectId(), apportion.getFriendUserId()).executeSingle();
					}
						HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
						
						
						if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null || 
								mMoneyIncomeContainerEditor.getModel().getProjectId().equals(mMoneyIncomeContainerEditor.getModelCopy().getProjectId())){
							 // 无旧圈子可更新
							projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorization.getApportionedTotalIncome() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
							projectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(projectShareAuthorization.getActualTotalIncome() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
							if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
								projectShareAuthorizationEditor.getModelCopy().setActualTotalLend(projectShareAuthorization.getActualTotalLend() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
								projectShareAuthorizationEditor.save();
								
								ProjectShareAuthorization selfProjectShareAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
								projectShareAuthorizationEditor = selfProjectShareAuthorization.newModelEditor();
								projectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(selfProjectShareAuthorization.getActualTotalBorrow() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
								projectShareAuthorizationEditor.save();
							} else {
								projectShareAuthorizationEditor.save();
							}
						
						}else{
							//更新新圈子分摊支出
							projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorization.getApportionedTotalIncome() + (apportionEditor.getModelCopy().getAmount0() * rate));
							projectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(projectShareAuthorization.getActualTotalIncome() + (apportionEditor.getModelCopy().getAmount0() * rate));
							if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
								projectShareAuthorizationEditor.getModelCopy().setActualTotalLend(projectShareAuthorization.getActualTotalLend() + (apportionEditor.getModelCopy().getAmount0() * rate));
								projectShareAuthorizationEditor.save();
									
								ProjectShareAuthorization selfProjectShareAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
								projectShareAuthorizationEditor = selfProjectShareAuthorization.newModelEditor();
								projectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(selfProjectShareAuthorization.getActualTotalBorrow() + (apportionEditor.getModelCopy().getAmount0() * rate));
								projectShareAuthorizationEditor.save();
							} else {
								projectShareAuthorizationEditor.save();
							}
							
							//更新老圈子分摊支出
							ProjectShareAuthorization oldProjectAuthorization;

							if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
								oldProjectAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
							} else if(apportion.getLocalFriendId() != null){
								oldProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
										mMoneyIncomeContainerEditor.getModel().getProjectId(), apportion.getLocalFriendId()).executeSingle();
							} else {
								oldProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
										mMoneyIncomeContainerEditor.getModel().getProjectId(), apportion.getFriendUserId()).executeSingle();
								
							}
							if(oldProjectAuthorization != null){
								HyjModelEditor<ProjectShareAuthorization> oldProjectAuthorizationEditor = oldProjectAuthorization.newModelEditor();
								oldProjectAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectAuthorization.getApportionedTotalIncome() - (oldApportionAmount * oldRate));
								oldProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectAuthorization.getActualTotalIncome() - (oldApportionAmount * oldRate));
								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
									oldProjectAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectAuthorization.getActualTotalLend() - (oldApportionAmount * oldRate));
									oldProjectAuthorizationEditor.save();

									ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
									oldProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
									oldProjectAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldSelfProjectAuthorization.getActualTotalBorrow() - (oldApportionAmount * oldRate));
									oldProjectAuthorizationEditor.save();
								} else {
									oldProjectAuthorizationEditor.save();
								}
							}
						}
						

						if(mMoneyIncomeContainerEditor.getModelCopy().getEventId() != null){
							// 维护活动成员余额
							EventMember eventMember = null;
							if(apportion.getFriendUserId() != null){
								eventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
										mMoneyIncomeContainerEditor.getModelCopy().getEventId(), apportion.getFriendUserId()).executeSingle();
							} else if(apportion.getLocalFriendId() != null){
								eventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
										mMoneyIncomeContainerEditor.getModelCopy().getEventId(), apportion.getLocalFriendId()).executeSingle();
							}
							HyjModelEditor<EventMember> eventMemberEditor = eventMember.newModelEditor();
							
							if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null || 
									mMoneyIncomeContainerEditor.getModelCopy().getEventId().equals(mMoneyIncomeContainerEditor.getModel().getEventId())){
								 // 该支出是新的，或者该支出的圈子没有改变：无旧圈子需要更新，只需更新新圈子的projectShareAuthorization
								eventMemberEditor.getModelCopy().setApportionedTotalIncome(eventMember.getApportionedTotalExpense() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
								eventMemberEditor.save();
							} else {
								//更新新圈子分摊支出
								eventMemberEditor.getModelCopy().setApportionedTotalIncome(eventMember.getApportionedTotalExpense() + (apportionEditor.getModelCopy().getAmount0() * rate));
								eventMemberEditor.save();
								if(mMoneyIncomeContainerEditor.getModel().getEventId() != null){
									//更新老圈子分摊支出
									EventMember oldEventMember;
									if(apportion.getFriendUserId() != null){
										oldEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
												mMoneyIncomeContainerEditor.getModel().getEventId(), apportion.getFriendUserId()).executeSingle();
									} else {
										oldEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
												mMoneyIncomeContainerEditor.getModel().getEventId(), apportion.getLocalFriendId()).executeSingle();
									}
									if(oldEventMember != null){
										HyjModelEditor<EventMember> oldEventMemberEditor = oldEventMember.newModelEditor();
										oldEventMemberEditor.getModelCopy().setApportionedTotalIncome(oldEventMember.getApportionedTotalExpense() - (oldApportionAmount * oldRate));
										oldEventMemberEditor.save();
									}
								}
							}
						} else {
							// 新的没有选择活动
							if(mMoneyIncomeContainerEditor.getModel().get_mId() == null){
								// 新增的收入，又没有活动，什么都不用做
							} else if(mMoneyIncomeContainerEditor.getModel().getEventId() != null){
								// 不是新增的收入，当前没有活动，看老的收入记录有没有活动
								//更新老圈子分摊支出
								EventMember oldEventMember;
								if(apportion.getFriendUserId() != null){
									oldEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
											mMoneyIncomeContainerEditor.getModel().getEventId(), apportion.getFriendUserId()).executeSingle();
								} else {
									oldEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
											mMoneyIncomeContainerEditor.getModel().getEventId(), apportion.getLocalFriendId()).executeSingle();
								}
								if(oldEventMember != null){
									HyjModelEditor<EventMember> oldEventMemberEditor = oldEventMember.newModelEditor();
									oldEventMemberEditor.getModelCopy().setApportionedTotalIncome(oldEventMember.getApportionedTotalExpense() - (oldApportionAmount * oldRate));
									oldEventMemberEditor.save();
								}
							}
						}
						
						//更新相关好友的借贷账户
						if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
							MoneyAccount debtAccount = null;
							if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
								if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									debtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), null, mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
								} else {
									debtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId());
								}
							} else {
								debtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId());
							}
							if(api.getState() == ApportionItem.NEW){
				                if(debtAccount != null){
				                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
				                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
				                	debtAccountEditor.save();
				                }else{
				                	// 创建新的借贷账户
				                	if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
				                		if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
											MoneyAccount.createDebtAccount(null, null, mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
				                		} else {
					                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
						                }
				                	} else {
				                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
					                }
				                }
							} else{
								MoneyAccount oldDebtAccount = null;
								oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
								if(mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null) {
									if(!mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
										oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), null, mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId());
									} else {
										oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
									}
								} else {
									oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
								}
								if(debtAccount == null){
				                	if(oldDebtAccount != null){
										HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
										oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldApportionAmount*apportionEditor.getModel().getExchangeRate());
										oldDebtAccountEditor.save();
				                	}// 创建新的借贷账户
				                	if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
				                		if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
											MoneyAccount.createDebtAccount(null, null, mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
				                		} else {
					                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
					                	}
				                	} else {
				                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
				                	}
								} else if(oldDebtAccount != null && debtAccount.getId().equals(oldDebtAccount.getId())){
				                	HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldApportionAmount*apportionEditor.getModel().getExchangeRate() - apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
				                	oldDebtAccountEditor.save();
				                } else {
				                	if(oldDebtAccount != null){
					                	HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
										oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldApportionAmount*apportionEditor.getModel().getExchangeRate());
					                	oldDebtAccountEditor.save();
				                	}
						    		HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
				                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
			                		debtAccountEditor.save();
						        }
							}
					    }
						
						createMoneyIncomeApportion(mMoneyIncomeContainerEditor, apportion, apportionEditor);
						
							MoneyLend moneyLendOfFinancialOwner = null; // 财务负责人向记账人借出
							MoneyBorrow moneyBorrow = null; // 记账人向财务负责人借入
							
							MoneyLend moneyLend = null;		// 分摊人向财务负责人借出
							MoneyBorrow moneyBorrowOfFinancialOwner = null; // 财务负责人向分摊人借入
							
							if(apportion.get_mId() == null){
								moneyBorrow = new MoneyBorrow();
								moneyLendOfFinancialOwner = new MoneyLend();
								
								moneyLend = new MoneyLend();
								moneyBorrowOfFinancialOwner = new MoneyBorrow();
							} else {
								if(mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() == null){
									if(apportionEditor.getModel().getFriendUserId() != null){
										// 记账人借入
										moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
										// 分摊人借出
										moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerUserId=?", apportion.getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
									} else {
										// 记账人借入
										moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND localFriendId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
										// 分摊人借出
										moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerFriendId=?", apportion.getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
									}
									moneyBorrowOfFinancialOwner = new MoneyBorrow();
									moneyLendOfFinancialOwner = new MoneyLend();
								} else {
									// 记账人向财务负责人借入
									moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() ).executeSingle();
									
									// 财务负责人向记账人借出
									moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerUserId=?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
									if(apportionEditor.getModel().getFriendUserId() != null){
										// 分摊人向财务负责人借出
										moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerUserId=?", apportion.getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
											// 财务负责人向分摊人借入
										moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
									} else {
										// 分摊人向财务负责人借出
										moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerFriendId=?", apportion.getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
										// 财务负责人向分摊人借入
										moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND localFriendId = ?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
									}
								}

								String previousFinancialOwnerUserId = HyjUtil.ifNull(mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() , "");
								String currentFinancialOwnerUserId = HyjUtil.ifNull(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() , "");
								if(moneyBorrowOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
									// 删除老的 财务负责人向分摊人借入 
									moneyBorrowOfFinancialOwner.delete();
									// 生成新的 财务负责人向分摊人借入
									moneyBorrowOfFinancialOwner = new MoneyBorrow();
								}
								if(moneyBorrowOfFinancialOwner == null) {
									moneyBorrowOfFinancialOwner = new MoneyBorrow();
								}
								
								if(moneyLend != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
									moneyLend.delete();
									moneyLend = new MoneyLend();
								}
								if(moneyLend == null){
									moneyLend = new MoneyLend();
								}
								
								if(moneyBorrow != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
									moneyBorrow.delete();
									moneyBorrow = new MoneyBorrow();
								}
								if(moneyBorrow == null){
									moneyBorrow = new MoneyBorrow();
								}
								
								if(moneyLendOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
									moneyLendOfFinancialOwner.delete();
									moneyLendOfFinancialOwner = new MoneyLend();
								}
								if(moneyLendOfFinancialOwner == null) {
									moneyLendOfFinancialOwner = new MoneyLend();
								}
							}
							
							moneyBorrow.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
							moneyBorrow.setAmount(apportionEditor.getModelCopy().getAmount0());
							moneyBorrow.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
							moneyBorrow.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
							moneyBorrow.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
							moneyBorrow.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
							moneyBorrow.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
							moneyBorrow.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
							moneyBorrow.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
							moneyBorrow.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
							moneyBorrow.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
							moneyBorrow.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
							
							if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
								MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
								moneyBorrow.setMoneyAccountId(moneyAccount.getId(), moneyAccount.getCurrencyId());
							} else {
								moneyBorrow.setMoneyAccountId(null, null);
							}
							
							if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null){
								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
									moneyBorrow.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
									moneyBorrow.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
									moneyBorrow.save();
								}
							} else {
								if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
									moneyBorrow.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
									moneyBorrow.setLocalFriendId(null);
									moneyBorrow.save();
								}
								
								//===================================================================================================
								if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(apportionEditor.getModelCopy().getFriendUserId())){
									moneyBorrowOfFinancialOwner.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
									moneyBorrowOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
									moneyBorrowOfFinancialOwner.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
									moneyBorrowOfFinancialOwner.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
									moneyBorrowOfFinancialOwner.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
									moneyBorrowOfFinancialOwner.setOwnerUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
									moneyBorrowOfFinancialOwner.setOwnerFriendId(null);
									if(apportionEditor.getModelCopy().getFriendUserId() != null){
										moneyBorrowOfFinancialOwner.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
										moneyBorrowOfFinancialOwner.setLocalFriendId(null);
									} else {
										moneyBorrowOfFinancialOwner.setFriendUserId(null);
										moneyBorrowOfFinancialOwner.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
									}
									moneyBorrowOfFinancialOwner.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
									moneyBorrowOfFinancialOwner.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
									moneyBorrowOfFinancialOwner.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
									
									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
										moneyBorrowOfFinancialOwner.setMoneyAccountId(null, moneyAccount.getCurrencyId());
									} else {
										moneyBorrowOfFinancialOwner.setMoneyAccountId(null, null);
									}

									moneyBorrowOfFinancialOwner.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
									moneyBorrowOfFinancialOwner.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
									moneyBorrowOfFinancialOwner.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
									moneyBorrowOfFinancialOwner.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
									moneyBorrowOfFinancialOwner.save();
									
								}
							}
							
							moneyLend.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
							moneyLend.setAmount(apportionEditor.getModelCopy().getAmount0());
							moneyLend.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
							moneyLend.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
							moneyLend.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
							moneyLend.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
							moneyLend.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
							moneyLend.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
							moneyLend.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
							moneyLend.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
							moneyLend.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
							moneyLend.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
							moneyLend.setLocalFriendId(null);
							
							if(apportionEditor.getModelCopy().getFriendUserId() != null){
								moneyLend.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
								moneyLend.setOwnerFriendId(null);
							} else {
								moneyLend.setOwnerUserId(""); // 设为"",使他不会自动使用当前的用户id
								moneyLend.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
							}
							if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
								MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
								if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportionEditor.getModelCopy().getFriendUserId())){
									moneyLend.setMoneyAccountId(moneyAccount.getId(), moneyAccount.getCurrencyId());
								} else {
									moneyLend.setMoneyAccountId(null, moneyAccount.getCurrencyId());
								}
							} else {
								moneyLend.setMoneyAccountId(null, null);
							}
							if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null){
								// 如果分摊成员是自己，我们不生成自己到自己的借贷
								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
									moneyLend.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
									moneyLend.save();
								}
							} else{
								if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(apportionEditor.getModelCopy().getFriendUserId())){
									moneyLend.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
									moneyLend.save();
								}

								if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									moneyLendOfFinancialOwner.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
									moneyLendOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
									moneyLendOfFinancialOwner.setOwnerUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
									moneyLendOfFinancialOwner.setOwnerFriendId(null);
									moneyLendOfFinancialOwner.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
									moneyLendOfFinancialOwner.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
									moneyLendOfFinancialOwner.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
									moneyLendOfFinancialOwner.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
									moneyLendOfFinancialOwner.setLocalFriendId(null);
									moneyLendOfFinancialOwner.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
									moneyLendOfFinancialOwner.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
									moneyLendOfFinancialOwner.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
	
									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
										moneyLendOfFinancialOwner.setMoneyAccountId(null, moneyAccount.getCurrencyId());
									} else {
										moneyLendOfFinancialOwner.setMoneyAccountId(null, null);
									}
									
									moneyLendOfFinancialOwner.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
									moneyLendOfFinancialOwner.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
									moneyLendOfFinancialOwner.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
									moneyLendOfFinancialOwner.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
									moneyLendOfFinancialOwner.save();
								}

							}

							apportionEditor.save();
							savedCount++;
				}
		}
		return savedCount;
	}

	private static void createMoneyIncomeApportion(
			MoneyIncomeContainerEditor mMoneyIncomeContainerEditor,
			MoneyIncomeApportion apportion,
			HyjModelEditor<MoneyIncomeApportion> apportionEditor) {

		MoneyIncome moneyIncome = null;
		if(apportion.get_mId() == null){
			moneyIncome = new MoneyIncome();
		} else {
			moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
		}
		moneyIncome.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
		moneyIncome.setAmount(apportionEditor.getModelCopy().getAmount0());		
		moneyIncome.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
		moneyIncome.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
		moneyIncome.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
		moneyIncome.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFriendUserId());
		moneyIncome.setLocalFriendId(mMoneyIncomeContainerEditor.getModelCopy().getLocalFriendId());
		moneyIncome.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
		moneyIncome.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
		moneyIncome.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
		moneyIncome.setMoneyIncomeCategory(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategory());
		moneyIncome.setMoneyIncomeCategoryMain(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategoryMain());
		moneyIncome.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
		moneyIncome.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
		moneyIncome.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
		moneyIncome.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
		moneyIncome.setEventId(mMoneyIncomeContainerEditor.getModelCopy().getEventId());
		
		if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
			// 帮自己生成支出，我们知道自己的支出账户
			if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
				moneyIncome.setMoneyAccountId(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
			} else {
				moneyIncome.setMoneyAccountId(null, null);
			}
		} else {
			moneyIncome.setLocalFriendId(null);
			
			if(apportionEditor.getModelCopy().getFriendUserId() != null){
				moneyIncome.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
				moneyIncome.setOwnerFriendId(null);
			} else {
				moneyIncome.setOwnerUserId("");  // 设为"",使他在保存时不会自动使用当前的用户id
				moneyIncome.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
			}

			// 帮别人生成支出，指出的账户为null, 因为我们不能帮别人选择支出账户
			if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
				moneyIncome.setMoneyAccountId(null, moneyAccount.getCurrencyId());
			} else {
				moneyIncome.setMoneyAccountId(null, null);
			}
		}
		
		moneyIncome.save();
		
	}
}
