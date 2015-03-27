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

@Table(name = "MoneyExpenseContainer", id = BaseColumns._ID)
public class MoneyExpenseContainer extends HyjModel{

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
	
	@Column(name = "expenseType")
	private String mExpenseType;

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
	
	@Column(name = "moneyExpenseCategory")
	private String mMoneyExpenseCategory;

	@Column(name = "moneyExpenseCategoryMain")
	private String mMoneyExpenseCategoryMain;
	
	@Column(name = "exchangeRate")
	private Double mExchangeRate;

//	@Column(name = "moneyIncomeId")
//	private String mMoneyIncomeId;

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
	
	public MoneyExpenseContainer(){
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
		mUUID = UUID.randomUUID().toString();
		mExpenseType = "MoneyExpense";
		mMoneyAccountId = userData.getActiveMoneyAccountId();
		this.setProject(userData.getActiveProject());
		mExchangeRate = 1.00;
		if(this.getProject() != null){
			mMoneyExpenseCategory = this.getProject().getDefaultExpenseCategory();
			mMoneyExpenseCategoryMain = this.getProject().getDefaultExpenseCategoryMain();
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

	public Picture getPicture(){
		if(mPictureId == null){
			return null;
		}
		
		Picture pic = getModel(Picture.class, mPictureId);
		if(pic == null && this.get_mId() != null && !this.isClientNew()){
			HyjUtil.asyncLoadPicture(mPictureId, this.getClass().getSimpleName(), this.getId());
		}
		
		return getModel(Picture.class, mPictureId);
	}

	public void setPicture(Picture picture){
		this.setPictureId(picture.getId());
	}
	
	public List<Picture> getPictures(){
		return getMany(Picture.class, "recordId", "displayOrder");
	}

	public List<MoneyExpenseApportion> getApportions(){
		return getMany(MoneyExpenseApportion.class, "moneyExpenseContainerId");
	}
	
	public Long getDate() {
		return mDate;
	}

	public void setDate(Long mDate) {
		this.mDate = mDate;
	}
//	
//	public void setDate(long dateInMillisec) {
//		Date date = new Date(dateInMillisec);
//		setDate(HyjUtil.formatDateToIOS(date));
//	}
	
	public Double getAmount() {
		return mAmount;
	}
	
	public Double getAmount0(){
		if(mAmount == null){
			return 0.00;
		}
		return mAmount;
	}
	
	public Double getLocalAmount(){
		Double rate = 1.0;
		String userCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();

			if(!userCurrencyId.equals(this.getProject().getCurrencyId())){
				Double exchange = Exchange.getExchangeRate(userCurrencyId, this.getProject().getCurrencyId());
				if(exchange != null){
				   	rate = exchange;
			    }
			}
			return this.getAmount0()*this.getExchangeRate()/rate;
	}
	
	public Double getProjectAmount(){
			return this.getAmount0()*this.getExchangeRate();
	}

	public void setAmount(Double mAmount) {
		if(mAmount != null){
			mAmount = HyjUtil.toFixed2(mAmount);
		}
		this.mAmount = mAmount;
	}
	
	public String getExpenseType() {
		return mExpenseType;
	}

	public void setExpenseType(String mExpenseType) {
		this.mExpenseType = mExpenseType;
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
	
	public Friend getFriend(){
		if(mFriendUserId != null){
			return new Select().from(Friend.class).where("friendUserId=?",mFriendUserId).executeSingle();
		}else if(mLocalFriendId != null){
			return getModel(Friend.class, mLocalFriendId);
		}
		return null;
	}
	
	public void setFriend(Friend mFriend) {
		if(mFriend == null){
			this.mFriendUserId = null;
			this.mLocalFriendId = null;
		}else if(mFriend.getFriendUserId() != null){
			this.mFriendUserId = mFriend.getFriendUserId();
			this.mLocalFriendId = null;
		}
		else {
			this.mFriendUserId= null;
			this.mLocalFriendId = mFriend.getId();
		}
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
		return getModel(MoneyAccount.class, mMoneyAccountId);
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

	public Project getProject(){
		if(mProjectId == null){
			return null;
		}
		return getModel(Project.class, mProjectId);
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

	public String getMoneyExpenseCategory() {
		return mMoneyExpenseCategory;
	}

	public void setMoneyExpenseCategory(String mMoneyExpenseCategory) {
		this.mMoneyExpenseCategory = mMoneyExpenseCategory;
	}

	public String getMoneyExpenseCategoryMain() {
		return mMoneyExpenseCategoryMain;
	}

	public void setMoneyExpenseCategoryMain(String mMoneyExpenseCategoryMain) {
		this.mMoneyExpenseCategoryMain = mMoneyExpenseCategoryMain;
	}	
	
	public Double getExchangeRate() {
		return mExchangeRate;
	}

	public void setExchangeRate(Double mExchangeRate) {
		if(mExchangeRate != null) {
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

	public User getOwnerUser() {
		return getModel(User.class, mOwnerUserId);
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
		if(this.getDate() == null){
			modelEditor.setValidationError("date",R.string.moneyExpenseFormFragment_editText_hint_date);
		}else{
			modelEditor.removeValidationError("date");
		}
		
		if(this.getAmount() == null){
			modelEditor.setValidationError("amount",R.string.moneyExpenseFormFragment_editText_hint_amount);
		}else if(this.getAmount() < 0){
			modelEditor.setValidationError("amount",R.string.moneyExpenseFormFragment_editText_validationError_negative_amount);
		}else if(this.getAmount() > 99999999){
			modelEditor.setValidationError("amount",R.string.moneyExpenseFormFragment_editText_validationError_beyondMAX_amount);
		}
		else{
			modelEditor.removeValidationError("amount");
		}
		
		if(this.getMoneyExpenseCategory() == null || this.getMoneyExpenseCategory().length() == 0){
			modelEditor.setValidationError("moneyExpenseCategory", R.string.moneyExpenseFormFragment_editText_hint_moneyExpenseCategory);
		}else{
			modelEditor.removeValidationError("moneyExpenseCategory");
		}
		
		if(this.getExchangeRate() == null){
			modelEditor.setValidationError("exchangeRate",R.string.moneyExpenseFormFragment_editText_hint_exchangeRate);
		}else if(this.getExchangeRate() == 0){
			modelEditor.setValidationError("exchangeRate",R.string.moneyExpenseFormFragment_editText_validationError_zero_exchangeRate);
		}else if(this.getExchangeRate() < 0){
			modelEditor.setValidationError("exchangeRate",R.string.moneyExpenseFormFragment_editText_validationError_negative_exchangeRate);
		}else if(this.getExchangeRate() > 99999999){
			modelEditor.setValidationError("exchangeRate",R.string.moneyExpenseFormFragment_editText_validationError_beyondMAX_exchangeRate);
		}
		else{
			modelEditor.removeValidationError("exchangeRate");
		}
		
		if(this.getMoneyAccountId() == null){
			modelEditor.setValidationError("moneyAccount",R.string.moneyExpenseFormFragment_editText_hint_moneyAccount);
		}else{
			modelEditor.removeValidationError("moneyAccount");
		}
		
		if(this.getProjectId() == null){
			modelEditor.setValidationError("project",R.string.moneyExpenseFormFragment_editText_hint_project);
		}else{
			modelEditor.removeValidationError("project");
		}
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

	public Boolean getIsImported() {
		return this.mIsImported;
	}

	public void setIsImported(Boolean b){
		this.mIsImported = b;
	}
	
//	public String getMoneyIncomeId(){
//		return mMoneyIncomeId;
//	}
//	
//	public void setMoneyIncomeId(String moneyIncomeId) {
//		this.mMoneyIncomeId = moneyIncomeId;
//	}

	public static int saveApportions(List<ApportionItem> apportionItems, MoneyExpenseContainerEditor mMoneyExpenseContainerEditor){
		int count = apportionItems.size();
		int savedCount = 0;
		for (int i = 0; i < count; i++) {
			ApportionItem<MoneyApportion> api = apportionItems.get(i);
			MoneyExpenseApportion apportion = (MoneyExpenseApportion) api.getApportion();
			HyjModelEditor<MoneyExpenseApportion> apportionEditor = apportion.newModelEditor();

					// 分摊好友是圈子成员
					if(api.getState() == ApportionItem.DELETED ){
						deleteApportion(apportion, mMoneyExpenseContainerEditor);
					} else {
						 	if(api.getState() != ApportionItem.UNCHANGED
									|| !mMoneyExpenseContainerEditor.getModelCopy().getProjectId().equals(mMoneyExpenseContainerEditor.getModel().getProjectId())
									|| !mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyExpenseContainerEditor.getModel().getMoneyAccountId())) {
								api.saveToCopy(apportionEditor.getModelCopy());
							}
							Double oldRate = mMoneyExpenseContainerEditor.getModel().getExchangeRate(); 
							Double rate = mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate();
							Double oldApportionAmount = apportionEditor.getModel().getAmount0();
							if(mMoneyExpenseContainerEditor.getModelCopy().get_mId() == null){
								oldApportionAmount = 0.0;
							}
							ProjectShareAuthorization projectShareAuthorization = null;
							//维护圈子成员金额
							if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
								projectShareAuthorization = mMoneyExpenseContainerEditor.getNewSelfProjectShareAuthorization();
							} else if(apportion.getFriendUserId() != null){
								projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
										mMoneyExpenseContainerEditor.getModelCopy().getProjectId(), apportion.getFriendUserId()).executeSingle();
							} else if(apportion.getLocalFriendId() != null){
								projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
										mMoneyExpenseContainerEditor.getModelCopy().getProjectId(), apportion.getLocalFriendId()).executeSingle();
							} 
//							
							HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
							
							if(mMoneyExpenseContainerEditor.getModelCopy().get_mId() == null || 
									mMoneyExpenseContainerEditor.getModel().getProjectId().equals(mMoneyExpenseContainerEditor.getModelCopy().getProjectId())){
								 // 该支出是新的，或者该支出的圈子没有改变：无旧圈子需要更新，只需更新新圈子的projectShareAuthorization
								projectShareAuthorizationEditor.getModelCopy().setApportionedTotalExpense(projectShareAuthorization.getApportionedTotalExpense() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
								projectShareAuthorizationEditor.getModelCopy().setActualTotalExpense(projectShareAuthorization.getActualTotalExpense() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
									projectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(projectShareAuthorization.getActualTotalBorrow() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
									projectShareAuthorizationEditor.save();
									
									ProjectShareAuthorization selfProjectShareAuthorization = mMoneyExpenseContainerEditor.getNewSelfProjectShareAuthorization();
									projectShareAuthorizationEditor = selfProjectShareAuthorization.newModelEditor();
									projectShareAuthorizationEditor.getModelCopy().setActualTotalLend(selfProjectShareAuthorization.getActualTotalLend() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
									projectShareAuthorizationEditor.save();
								} else {
									projectShareAuthorizationEditor.save();
								}
							} else {
								//更新新圈子分摊支出
								projectShareAuthorizationEditor.getModelCopy().setApportionedTotalExpense(projectShareAuthorization.getApportionedTotalExpense() + (apportionEditor.getModelCopy().getAmount0() * rate));
								projectShareAuthorizationEditor.getModelCopy().setActualTotalExpense(projectShareAuthorization.getActualTotalExpense() + (apportionEditor.getModelCopy().getAmount0() * rate));
								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
									projectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(projectShareAuthorization.getActualTotalBorrow() + (apportionEditor.getModelCopy().getAmount0() * rate));
									projectShareAuthorizationEditor.save();
										
									ProjectShareAuthorization selfProjectShareAuthorization = mMoneyExpenseContainerEditor.getNewSelfProjectShareAuthorization();
									projectShareAuthorizationEditor = selfProjectShareAuthorization.newModelEditor();
									projectShareAuthorizationEditor.getModelCopy().setActualTotalLend(selfProjectShareAuthorization.getActualTotalLend() + (apportionEditor.getModelCopy().getAmount0() * rate));
									projectShareAuthorizationEditor.save();
								} else {
									projectShareAuthorizationEditor.save();
								}
								
								//更新老圈子分摊支出
								ProjectShareAuthorization oldProjectAuthorization;
								if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
									oldProjectAuthorization = mMoneyExpenseContainerEditor.getOldSelfProjectShareAuthorization();
								} else if(apportion.getFriendUserId() != null){
									oldProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
											mMoneyExpenseContainerEditor.getModel().getProjectId(), apportion.getFriendUserId()).executeSingle();
								} else {
									oldProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
											mMoneyExpenseContainerEditor.getModel().getProjectId(), apportion.getLocalFriendId()).executeSingle();
								}
								if(oldProjectAuthorization != null){
									HyjModelEditor<ProjectShareAuthorization> oldProjectAuthorizationEditor = oldProjectAuthorization.newModelEditor();
									oldProjectAuthorizationEditor.getModelCopy().setApportionedTotalExpense(oldProjectAuthorization.getApportionedTotalExpense() - (oldApportionAmount * oldRate));
									oldProjectAuthorizationEditor.getModelCopy().setActualTotalExpense(oldProjectAuthorization.getActualTotalExpense() - (oldApportionAmount * oldRate));
									if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
										oldProjectAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectAuthorization.getActualTotalBorrow() - (oldApportionAmount * oldRate));
										oldProjectAuthorizationEditor.save();

										ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyExpenseContainerEditor.getOldSelfProjectShareAuthorization();
										oldProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
										oldProjectAuthorizationEditor.getModelCopy().setActualTotalLend(oldSelfProjectAuthorization.getActualTotalLend() - (oldApportionAmount * oldRate));
										oldProjectAuthorizationEditor.save();
									} else {
										oldProjectAuthorizationEditor.save();
									}
								}
							}
							
							if(mMoneyExpenseContainerEditor.getModelCopy().getEventId() != null){
								// 维护活动成员余额
								EventMember eventMember = null;
								if(apportion.getFriendUserId() != null){
									eventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
											mMoneyExpenseContainerEditor.getModelCopy().getEventId(), apportion.getFriendUserId()).executeSingle();
								} else if(apportion.getLocalFriendId() != null){
									eventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
											mMoneyExpenseContainerEditor.getModelCopy().getEventId(), apportion.getLocalFriendId()).executeSingle();
								}
								if(eventMember != null){
								HyjModelEditor<EventMember> eventMemberEditor = eventMember.newModelEditor();
									if(mMoneyExpenseContainerEditor.getModelCopy().get_mId() == null || 
											mMoneyExpenseContainerEditor.getModelCopy().getEventId().equals(mMoneyExpenseContainerEditor.getModel().getEventId())){
										 // 该支出是新的，或者该支出的圈子没有改变：无旧圈子需要更新，只需更新新圈子的projectShareAuthorization
										eventMemberEditor.getModelCopy().setApportionedTotalExpense(eventMember.getApportionedTotalExpense() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
										eventMemberEditor.save();
									} else {
										//更新新圈子分摊支出
										eventMemberEditor.getModelCopy().setApportionedTotalExpense(eventMember.getApportionedTotalExpense() + (apportionEditor.getModelCopy().getAmount0() * rate));
										eventMemberEditor.save();
										
										if(mMoneyExpenseContainerEditor.getModel().getEventId() != null){
											//更新老圈子分摊支出
											EventMember oldEventMember;
											if(apportion.getFriendUserId() != null){
												oldEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
														mMoneyExpenseContainerEditor.getModel().getEventId(), apportion.getFriendUserId()).executeSingle();
											} else {
												oldEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
														mMoneyExpenseContainerEditor.getModel().getEventId(), apportion.getLocalFriendId()).executeSingle();
											}
											if(oldEventMember != null){
												HyjModelEditor<EventMember> oldEventMemberEditor = oldEventMember.newModelEditor();
												oldEventMemberEditor.getModelCopy().setApportionedTotalExpense(oldEventMember.getApportionedTotalExpense() - (oldApportionAmount * oldRate));
												oldEventMemberEditor.save();
											}
										}
									}
								}
							} else {
								// 新的没有选择活动
								if(mMoneyExpenseContainerEditor.getModel().get_mId() == null){
									// 新增的支出，又没有活动，什么都不用做
								} else if(mMoneyExpenseContainerEditor.getModel().getEventId() != null){
									// 不是新增的支出，当前没有活动，看老的支出记录有没有活动
									//更新老圈子分摊支出
									EventMember oldEventMember;
									if(apportion.getFriendUserId() != null){
										oldEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
												mMoneyExpenseContainerEditor.getModel().getEventId(), apportion.getFriendUserId()).executeSingle();
									} else {
										oldEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
												mMoneyExpenseContainerEditor.getModel().getEventId(), apportion.getLocalFriendId()).executeSingle();
									}
									if(oldEventMember != null){
										HyjModelEditor<EventMember> oldEventMemberEditor = oldEventMember.newModelEditor();
										oldEventMemberEditor.getModelCopy().setApportionedTotalExpense(oldEventMember.getApportionedTotalExpense() - (oldApportionAmount * oldRate));
										oldEventMemberEditor.save();
									}
								}
							}
							
							//更新相关好友的借贷账户
							if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
								MoneyAccount debtAccount = null;
								if(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
									if(!mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
										debtAccount = MoneyAccount.getDebtAccount(mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), null, mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId());
									} else {
										debtAccount = MoneyAccount.getDebtAccount(mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId());
									}
								} else {
									debtAccount = MoneyAccount.getDebtAccount(mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId());
								}
								if(api.getState() == ApportionItem.NEW) {
									// 该分摊是新增加的，我们更新借贷账户的余额即可
					                if(debtAccount != null) {
					                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
					                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
					                	debtAccountEditor.save();
					                } else {
					                	// 创建新的借贷账户
					                	if(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
					                		if(!mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
												MoneyAccount.createDebtAccount(null, null, mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
					                		} else {
						                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
							                }
					                	} else {
					                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
						                }
							        }
								} else {
									// 该分摊是旧的，我们要更新其对应的旧借贷账户
									MoneyAccount oldDebtAccount = null;
									if(mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId() != null) {
										if(!mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
											oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyExpenseContainerEditor.getModel().getProject().getCurrencyId(), null, mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId());
										} else {
											oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyExpenseContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
										}
									} else {
										oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyExpenseContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
									}
									if(debtAccount == null) {
										// 新账户不存在：更新老账户，创建新账户
										if(oldDebtAccount != null){
											HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
											oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldApportionAmount*apportionEditor.getModel().getExchangeRate());
											oldDebtAccountEditor.save();
										}
										// 创建新的借贷账户
					                	if(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
					                		if(!mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
												MoneyAccount.createDebtAccount(null, null, mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
					                		} else {
						                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
						                	}
					                	} else {
					                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyExpenseContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
					                	}
									}else if(oldDebtAccount != null && debtAccount.getId().equals(oldDebtAccount.getId())){
										// 新老借贷账户一样
					                	HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
										oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldApportionAmount*apportionEditor.getModel().getExchangeRate() + apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
										oldDebtAccountEditor.save();
									}else{
										// 新账户存在，更新新账户
										if(oldDebtAccount != null){
											HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
											oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldApportionAmount*apportionEditor.getModel().getExchangeRate());
											oldDebtAccountEditor.save();
										}	

										HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
										debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + apportionEditor.getModelCopy().getAmount0()*mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
										debtAccountEditor.save();
					                }
								}
						    }
							
							createMoneyExpenseForApportion(mMoneyExpenseContainerEditor, apportion, apportionEditor);
							
								MoneyBorrow moneyBorrow = null;
								MoneyLend moneyLendOfFinancialOwner = null;
								
								MoneyBorrow moneyBorrowOfFinancialOwner = null;
								MoneyLend moneyLend = null;
								
								if(apportion.get_mId() == null){
									moneyLend = new MoneyLend();
									moneyBorrowOfFinancialOwner = new MoneyBorrow();
									
									moneyBorrow = new MoneyBorrow();
									moneyLendOfFinancialOwner = new MoneyLend();
								} else {
									if(mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId() == null){
										if(apportionEditor.getModel().getFriendUserId() != null){
											moneyLend = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
											moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=? AND ownerUserId=?", apportion.getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
										} else {
											moneyLend = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=? AND ownerUserId=? AND localFriendId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
											moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=? AND ownerFriendId=?", apportion.getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
										}

										moneyLendOfFinancialOwner = new MoneyLend();
										moneyBorrowOfFinancialOwner = new MoneyBorrow();
									} else {
										moneyLend = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
										
										moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=? AND ownerUserId=?", apportion.getId(), mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
										if(apportionEditor.getModel().getFriendUserId() != null){
											moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=? AND ownerUserId=?", apportion.getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
											moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
										} else {
											moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=? AND ownerFriendId=?", apportion.getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
											moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=? AND ownerUserId=? AND localFriendId = ?", apportion.getId(), mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
										}
									}
									
									String previousFinancialOwnerUserId = HyjUtil.ifNull(mMoneyExpenseContainerEditor.getModel().getFinancialOwnerUserId() , "");
									String currentFinancialOwnerUserId = HyjUtil.ifNull(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId() , "");
									if(moneyLendOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
										moneyLendOfFinancialOwner.delete();
										moneyLendOfFinancialOwner = new MoneyLend();
									}
									if(moneyLendOfFinancialOwner == null) {
										moneyLendOfFinancialOwner = new MoneyLend();
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
									
									if(moneyBorrowOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
										moneyBorrowOfFinancialOwner.delete();
										moneyBorrowOfFinancialOwner = new MoneyBorrow();
									}
									if(moneyBorrowOfFinancialOwner == null) {
										moneyBorrowOfFinancialOwner = new MoneyBorrow();
									}
								}
								
								
								moneyLend.setMoneyExpenseApportionId(apportionEditor.getModelCopy().getId());
								moneyLend.setAmount(apportionEditor.getModelCopy().getAmount0());
								moneyLend.setDate(mMoneyExpenseContainerEditor.getModelCopy().getDate());
								moneyLend.setRemark(mMoneyExpenseContainerEditor.getModelCopy().getRemark());
								moneyLend.setFriendAccountId(mMoneyExpenseContainerEditor.getModelCopy().getFriendAccountId());
								moneyLend.setExchangeRate(mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
								moneyLend.setGeoLat(mMoneyExpenseContainerEditor.getModelCopy().getGeoLat());
								moneyLend.setGeoLon(mMoneyExpenseContainerEditor.getModelCopy().getGeoLon());
								moneyLend.setLocation(mMoneyExpenseContainerEditor.getModelCopy().getLocation());
								moneyLend.setAddress(mMoneyExpenseContainerEditor.getModelCopy().getAddress());
								moneyLend.setPictureId(mMoneyExpenseContainerEditor.getModelCopy().getPictureId());
								moneyLend.setProject(mMoneyExpenseContainerEditor.getModelCopy().getProject());
								if(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId() != null){
									MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId());
									moneyLend.setMoneyAccountId(moneyAccount.getId(), moneyAccount.getCurrencyId());
								} else {
									moneyLend.setMoneyAccountId(null, null);
								}
								
								if(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId() == null){
									// 如果分摊成员是自己，我们不生成自己到自己的借贷
									if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
										moneyLend.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
										moneyLend.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
										moneyLend.save();
									}
								} else {
									// 如果财务负责人是自己，我们不生成自己到自己的借贷
									if(!HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId())){
										moneyLend.setFriendUserId(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId());
										moneyLend.setLocalFriendId(null);
										moneyLend.save();
									}

									if(!mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(apportionEditor.getModelCopy().getFriendUserId())){
										moneyLendOfFinancialOwner.setMoneyExpenseApportionId(apportionEditor.getModelCopy().getId());
										moneyLendOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
										moneyLendOfFinancialOwner.setDate(mMoneyExpenseContainerEditor.getModelCopy().getDate());
										moneyLendOfFinancialOwner.setRemark(mMoneyExpenseContainerEditor.getModelCopy().getRemark());
										moneyLendOfFinancialOwner.setFriendAccountId(mMoneyExpenseContainerEditor.getModelCopy().getFriendAccountId());
										moneyLendOfFinancialOwner.setOwnerUserId(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId());
										moneyLendOfFinancialOwner.setOwnerFriendId(null);
										if(apportionEditor.getModelCopy().getFriendUserId() != null){
											moneyLendOfFinancialOwner.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
											moneyLendOfFinancialOwner.setLocalFriendId(null);
										} else {
											moneyLendOfFinancialOwner.setFriendUserId(null);
											moneyLendOfFinancialOwner.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
										}
										moneyLendOfFinancialOwner.setExchangeRate(mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
										moneyLendOfFinancialOwner.setGeoLat(mMoneyExpenseContainerEditor.getModelCopy().getGeoLat());
										moneyLendOfFinancialOwner.setGeoLon(mMoneyExpenseContainerEditor.getModelCopy().getGeoLon());
										
										if(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId() != null){
											MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId());
											moneyLendOfFinancialOwner.setMoneyAccountId(null, moneyAccount.getCurrencyId());
										} else {
											moneyLendOfFinancialOwner.setMoneyAccountId(null, null);
										}
	
										moneyLendOfFinancialOwner.setLocation(mMoneyExpenseContainerEditor.getModelCopy().getLocation());
										moneyLendOfFinancialOwner.setAddress(mMoneyExpenseContainerEditor.getModelCopy().getAddress());
										moneyLendOfFinancialOwner.setPictureId(mMoneyExpenseContainerEditor.getModelCopy().getPictureId());
										moneyLendOfFinancialOwner.setProject(mMoneyExpenseContainerEditor.getModelCopy().getProject());
										moneyLendOfFinancialOwner.save();
									}
									
								}
								

								moneyBorrow.setMoneyExpenseApportionId(apportionEditor.getModelCopy().getId());
								moneyBorrow.setAmount(apportionEditor.getModelCopy().getAmount0());
								moneyBorrow.setDate(mMoneyExpenseContainerEditor.getModelCopy().getDate());
								moneyBorrow.setRemark(mMoneyExpenseContainerEditor.getModelCopy().getRemark());
								moneyBorrow.setFriendAccountId(mMoneyExpenseContainerEditor.getModelCopy().getFriendAccountId());
								moneyBorrow.setExchangeRate(mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
								moneyBorrow.setGeoLat(mMoneyExpenseContainerEditor.getModelCopy().getGeoLat());
								moneyBorrow.setGeoLon(mMoneyExpenseContainerEditor.getModelCopy().getGeoLon());
								moneyBorrow.setLocation(mMoneyExpenseContainerEditor.getModelCopy().getLocation());
								moneyBorrow.setAddress(mMoneyExpenseContainerEditor.getModelCopy().getAddress());
								moneyBorrow.setPictureId(mMoneyExpenseContainerEditor.getModelCopy().getPictureId());
								moneyBorrow.setProject(mMoneyExpenseContainerEditor.getModelCopy().getProject());
								moneyBorrow.setLocalFriendId(null);
								if(apportionEditor.getModelCopy().getFriendUserId() != null){
									moneyBorrow.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
									moneyBorrow.setOwnerFriendId(null);
								} else {
									moneyBorrow.setOwnerUserId(""); // 设为"",使他不会自动使用当前的用户id
									moneyBorrow.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
								}
								if(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId() != null){
									MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId());
									if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportionEditor.getModelCopy().getFriendUserId())){
										moneyBorrow.setMoneyAccountId(moneyAccount.getId(), moneyAccount.getCurrencyId());
									} else {
										moneyBorrow.setMoneyAccountId(null, moneyAccount.getCurrencyId());
									}
								} else {
									moneyBorrow.setMoneyAccountId(null, null);
								}
								
								if(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId() == null){
									// 如果分摊成员是自己，我们不生成自己到自己的借贷
									if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
										moneyBorrow.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
										moneyBorrow.save();
									}
								} else {
									// 如果分摊成员是自己，我们不生成自己到自己的借贷
									if(!mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(apportion.getFriendUserId())){
										moneyBorrow.setFriendUserId(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId());
										moneyBorrow.save();
									}

									if(!mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
										moneyBorrowOfFinancialOwner.setMoneyExpenseApportionId(apportionEditor.getModelCopy().getId());
										moneyBorrowOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
										moneyBorrowOfFinancialOwner.setOwnerUserId(mMoneyExpenseContainerEditor.getModelCopy().getFinancialOwnerUserId());
										moneyBorrowOfFinancialOwner.setOwnerFriendId(null);
										moneyBorrowOfFinancialOwner.setDate(mMoneyExpenseContainerEditor.getModelCopy().getDate());
										moneyBorrowOfFinancialOwner.setRemark(mMoneyExpenseContainerEditor.getModelCopy().getRemark());
										moneyBorrowOfFinancialOwner.setFriendAccountId(mMoneyExpenseContainerEditor.getModelCopy().getFriendAccountId());
										moneyBorrowOfFinancialOwner.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
										moneyBorrowOfFinancialOwner.setLocalFriendId(null);
										moneyBorrowOfFinancialOwner.setExchangeRate(mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
										moneyBorrowOfFinancialOwner.setGeoLat(mMoneyExpenseContainerEditor.getModelCopy().getGeoLat());
										moneyBorrowOfFinancialOwner.setGeoLon(mMoneyExpenseContainerEditor.getModelCopy().getGeoLon());

										if(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId() != null){
											MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId());
											moneyBorrowOfFinancialOwner.setMoneyAccountId(null, moneyAccount.getCurrencyId());
										} else {
											moneyBorrowOfFinancialOwner.setMoneyAccountId(null, null);
										}
										
										moneyBorrowOfFinancialOwner.setLocation(mMoneyExpenseContainerEditor.getModelCopy().getLocation());
										moneyBorrowOfFinancialOwner.setAddress(mMoneyExpenseContainerEditor.getModelCopy().getAddress());
										moneyBorrowOfFinancialOwner.setPictureId(mMoneyExpenseContainerEditor.getModelCopy().getPictureId());
										moneyBorrowOfFinancialOwner.setProject(mMoneyExpenseContainerEditor.getModelCopy().getProject());
										moneyBorrowOfFinancialOwner.save();
									}
								}	
							
								apportionEditor.save();
								savedCount++;
				}
			}
		return savedCount;
	}

	private static void createMoneyExpenseForApportion(MoneyExpenseContainerEditor mMoneyExpenseContainerEditor, MoneyExpenseApportion apportion, HyjModelEditor<MoneyExpenseApportion> apportionEditor) {
		MoneyExpense moneyExpense = null;
		if(apportion.get_mId() == null){
			moneyExpense = new MoneyExpense();
		} else {
			moneyExpense = new Select().from(MoneyExpense.class).where("moneyExpenseApportionId=?", apportion.getId()).executeSingle();
		}
		moneyExpense.setMoneyExpenseApportionId(apportionEditor.getModelCopy().getId());
		moneyExpense.setAmount(apportionEditor.getModelCopy().getAmount0());		
		moneyExpense.setDate(mMoneyExpenseContainerEditor.getModelCopy().getDate());
		moneyExpense.setRemark(mMoneyExpenseContainerEditor.getModelCopy().getRemark());
		moneyExpense.setFriendAccountId(mMoneyExpenseContainerEditor.getModelCopy().getFriendAccountId());
		moneyExpense.setFriendUserId(mMoneyExpenseContainerEditor.getModelCopy().getFriendUserId());
		moneyExpense.setLocalFriendId(mMoneyExpenseContainerEditor.getModelCopy().getLocalFriendId());
		moneyExpense.setExchangeRate(mMoneyExpenseContainerEditor.getModelCopy().getExchangeRate());
		moneyExpense.setGeoLat(mMoneyExpenseContainerEditor.getModelCopy().getGeoLat());
		moneyExpense.setGeoLon(mMoneyExpenseContainerEditor.getModelCopy().getGeoLon());
		moneyExpense.setMoneyExpenseCategory(mMoneyExpenseContainerEditor.getModelCopy().getMoneyExpenseCategory());
		moneyExpense.setMoneyExpenseCategoryMain(mMoneyExpenseContainerEditor.getModelCopy().getMoneyExpenseCategoryMain());
		moneyExpense.setLocation(mMoneyExpenseContainerEditor.getModelCopy().getLocation());
		moneyExpense.setAddress(mMoneyExpenseContainerEditor.getModelCopy().getAddress());
		moneyExpense.setPictureId(mMoneyExpenseContainerEditor.getModelCopy().getPictureId());
		moneyExpense.setProject(mMoneyExpenseContainerEditor.getModelCopy().getProject());
		moneyExpense.setEventId(mMoneyExpenseContainerEditor.getModelCopy().getEventId());
		
		if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
			// 帮自己生成支出，我们知道自己的支出账户
			if(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId() != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId());
				moneyExpense.setMoneyAccountId(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
			} else {
				moneyExpense.setMoneyAccountId(null, null);
			}
		} else {
			moneyExpense.setLocalFriendId(null);
			
			if(apportionEditor.getModelCopy().getFriendUserId() != null){
				moneyExpense.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
				moneyExpense.setOwnerFriendId(null);
			} else {
				moneyExpense.setOwnerUserId("");  // 设为"",使他在保存时不会自动使用当前的用户id
				moneyExpense.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
			}

			// 帮别人生成支出，指出的账户为null, 因为我们不能帮别人选择支出账户
			if(mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId() != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyExpenseContainerEditor.getModelCopy().getMoneyAccountId());
				moneyExpense.setMoneyAccountId(null, moneyAccount.getCurrencyId());
			} else {
				moneyExpense.setMoneyAccountId(null, null);
			}
		}
		
		moneyExpense.save();
	}

	public static void deleteApportion(MoneyExpenseApportion apportion, MoneyExpenseContainerEditor mMoneyExpenseContainerEditor) {
		ProjectShareAuthorization oldProjectShareAuthorization;
		if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())) {
			// 把自己从分摊成员中移除：
			// 1、要更新自己在旧圈子中的分摊总额和支出总额，
			// 2、要删除自己对应的分摊支出
			// 更新旧圈子的分摊支出
			oldProjectShareAuthorization = mMoneyExpenseContainerEditor.getOldSelfProjectShareAuthorization();
			HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
			oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalExpense(oldProjectShareAuthorization.getApportionedTotalExpense() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalExpense(oldProjectShareAuthorization.getActualTotalExpense() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.save();
		} else {
			// 把别人从分摊成员中移除：
			// 1、要更新别人在旧圈子中的分摊总额、支出总额、借入总额，
			// 2、要删除别人对应的分摊支出和借入，
			// 3、要删除自己对应的分摊借出，
			// 4、要更新自己在该圈子中的借出总额
			// 更新旧圈子分摊支出
			oldProjectShareAuthorization = apportion.getProjectShareAuthorization();
			HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
			
			oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalExpense(oldProjectShareAuthorization.getApportionedTotalExpense() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalExpense(oldProjectShareAuthorization.getActualTotalExpense() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
		
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorization.getActualTotalBorrow() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.save();
			
			oldProjectShareAuthorization = mMoneyExpenseContainerEditor.getOldSelfProjectShareAuthorization();
			oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
			oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorization.getActualTotalLend() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
			oldProjectShareAuthorizationEditor.save();
		}
		
		if(mMoneyExpenseContainerEditor.getModel().getEventId() != null){
			//更新老圈子分摊支出
			EventMember oldEventMember;
			if(apportion.getFriendUserId() != null){
				oldEventMember = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", 
						mMoneyExpenseContainerEditor.getModel().getEventId(), apportion.getFriendUserId()).executeSingle();
			} else {
				oldEventMember = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", 
						mMoneyExpenseContainerEditor.getModel().getEventId(), apportion.getLocalFriendId()).executeSingle();
			}
			if(oldEventMember != null){
				HyjModelEditor<EventMember> oldEventMemberEditor = oldEventMember.newModelEditor();
				oldEventMemberEditor.getModelCopy().setApportionedTotalExpense(oldEventMember.getApportionedTotalExpense() - (apportion.getAmount0() * apportion.getMoneyExpenseContainer().getExchangeRate()));
				oldEventMemberEditor.save();
			}
		}
		
		MoneyExpense moneyExpense = new Select().from(MoneyExpense.class).where("moneyExpenseApportionId=?", apportion.getId()).executeSingle();
		if(moneyExpense != null){
			moneyExpense.delete();
		} 

		List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=?", apportion.getId()).execute();
		for(MoneyBorrow moneyBorrow : moneyBorrows){
			moneyBorrow.delete();
		}
		
		List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=?", apportion.getId()).execute();
		for(MoneyLend moneyLend : moneyLends){
			moneyLend.delete();
		} 
		apportion.delete();
	}

	// inner class
	public static class MoneyExpenseContainerEditor extends HyjModelEditor<MoneyExpenseContainer> {
		private  ProjectShareAuthorization mOldProjectShareAuthorization;
		private  ProjectShareAuthorization mNewProjectShareAuthorization;
		
		public MoneyExpenseContainerEditor(MoneyExpenseContainer moneyExpenseContainer) {
			super(moneyExpenseContainer);
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

}
