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

@Table(name = "MoneyDepositReturnContainer", id = BaseColumns._ID)
public class MoneyDepositReturnContainer extends HyjModel {

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

	@Column(name = "moneyAccountId")
	private String mMoneyAccountId;

	@Column(name = "projectId")
	private String mProjectId;
	
	@Column(name = "eventId")
	private String mEventId;
	
	@Column(name = "exchangeRate")
	private Double mExchangeRate;

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

	public MoneyDepositReturnContainer() {
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser()
				.getUserData();
		mUUID = UUID.randomUUID().toString();
		mMoneyAccountId = userData.getActiveMoneyAccountId();
		this.setProject(userData.getActiveProject());
		mExchangeRate = 1.00;
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

	public List<MoneyDepositReturnApportion> getApportions() {
		return getMany(MoneyDepositReturnApportion.class, "moneyDepositReturnContainerId");
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
			return;
		}
		this.mProjectId = mProject.getId();
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

	@Override
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
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
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

	public Boolean getIsImported() {
		return this.mIsImported;
	}

	public void setIsImported(Boolean b) {
		this.mIsImported = b;
	}
	
	// inner class
	public static class MoneyDepositReturnContainerEditor extends HyjModelEditor<MoneyDepositReturnContainer> {
		private  ProjectShareAuthorization mOldProjectShareAuthorization;
		private  ProjectShareAuthorization mNewProjectShareAuthorization;
		
		public MoneyDepositReturnContainerEditor(MoneyDepositReturnContainer model) {
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
	
	public static void deleteApportion(MoneyDepositReturnApportion apportion,
			MoneyDepositReturnContainerEditor mMoneyDepositReturnContainerEditor){
		// 维护缴款人的 ProjectShareAuthorization
		ProjectShareAuthorization psa = apportion.getProjectShareAuthorization();
		HyjModelEditor<ProjectShareAuthorization> psaEditor = psa.newModelEditor();
		psaEditor.getModelCopy().setActualTotalPayback(psa.getActualTotalPayback() - apportion.getAmount0()*mMoneyDepositReturnContainerEditor.getModel().getExchangeRate());
		psaEditor.save();

		List<MoneyReturn> moneyReturns = new Select().from(MoneyReturn.class).where("moneyDepositReturnApportionId=?", apportion.getId()).execute();
		for(MoneyReturn moneyReturn : moneyReturns){
			if(HyjApplication.getInstance().getCurrentUser().getId().equals(moneyReturn.getOwnerUserId())) {
				MoneyAccount debtAccount = null;
				if(mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId() != null){
					debtAccount = MoneyAccount.getDebtAccount(moneyReturn.getProject().getCurrencyId(), null, mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId());
				} else {
					debtAccount = MoneyAccount.getDebtAccount(moneyReturn.getProject().getCurrencyId(), moneyReturn.getLocalFriendId(), moneyReturn.getFriendUserId());
				}
				HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
				debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - moneyReturn.getProjectAmount());
				debtAccountEditor.save();
				moneyReturn.delete();
			}
		}
		List<MoneyPayback> moneyPaybacks = new Select().from(MoneyPayback.class).where("moneyDepositReturnApportionId=?", apportion.getId()).execute();
		for(MoneyPayback moneyPayback : moneyPaybacks){
			moneyPayback.delete();
		}
		apportion.delete();
	}

	public static int saveApportions(List<ApportionItem> apportionItems, MoneyDepositReturnContainerEditor mMoneyDepositReturnContainerEditor) {
		int count = apportionItems.size();
		int savedCount = 0;
		for (int i = 0; i < count; i++) {
			ApportionItem<MoneyApportion> api = apportionItems.get(i);
			MoneyDepositReturnApportion apportion = (MoneyDepositReturnApportion) api.getApportion();
            
				if(api.getState() == ApportionItem.DELETED ){
					deleteApportion(apportion, mMoneyDepositReturnContainerEditor);
				} else {
					HyjModelEditor<MoneyDepositReturnApportion> apportionEditor = apportion.newModelEditor();
					Double oldApportionAmount = apportionEditor.getModel().getAmount0();
					if(mMoneyDepositReturnContainerEditor.getModelCopy().get_mId() == null){
						oldApportionAmount = 0.0;
					}
					if(api.getState() != ApportionItem.UNCHANGED		
							|| !mMoneyDepositReturnContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositReturnContainerEditor.getModel().getProjectId())
							|| !mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyDepositReturnContainerEditor.getModel().getMoneyAccountId())) {
						api.saveToCopy(apportionEditor.getModelCopy());
					}
					
					MoneyAccount debtAccount = null;
					// 该好友是网络好友 或 该好友是本地好友
					if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
						if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
						} else {
							debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), null, mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId());
						}
					} else {
						debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
					}
					if(api.getState() == ApportionItem.NEW){
		                if(debtAccount != null){
		                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
		                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
		                	debtAccountEditor.save();
		                }else{
		                	if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
								if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
								} else {
			                		MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositReturnContainerEditor.getModelCopy().getProjectId()), null, mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
			                	}
		                	} else {
		                		MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositReturnContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
				            }
						}
					} else{
						MoneyAccount oldDebtAccount = null;
						if(mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId() != null){
							if(mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
								oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositReturnContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
							} else {
								oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositReturnContainerEditor.getModel().getProject().getCurrencyId(), null, mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId());
							}
						} else {
							oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositReturnContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
						}
						
						if(debtAccount == null){
							if(oldDebtAccount != null){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldApportionAmount*apportionEditor.getModel().getExchangeRate());
								oldDebtAccountEditor.save();
							}
							if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
								if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
								} else {
									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositReturnContainerEditor.getModelCopy().getProjectId()), null, mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
								}
							} else {
								MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositReturnContainerEditor.getModelCopy().getProject().getOwnerUserId(), apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
							}
						}else if(oldDebtAccount != null && debtAccount.getId().equals(oldDebtAccount.getId())){
							HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
		                	oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldApportionAmount*apportionEditor.getModel().getExchangeRate() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
							oldDebtAccountEditor.save();
						}else{
							if(oldDebtAccount != null){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
			                	oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldApportionAmount*apportionEditor.getModel().getExchangeRate());
								oldDebtAccountEditor.save();
							}
							HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
			               	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
			               	debtAccountEditor.save();
						}
					}
					

					// 维护缴款人的 ProjectShareAuthorization
					ProjectShareAuthorization newPsa = api.getProjectShareAuthorization();
					HyjModelEditor<ProjectShareAuthorization> newPsaEditor = newPsa.newModelEditor();
					if(apportion.get_mId() == null) {
						newPsaEditor.getModelCopy().setActualTotalPayback(newPsa.getActualTotalPayback() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
					} else if(mMoneyDepositReturnContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositReturnContainerEditor.getModel().getProjectId())){
						newPsaEditor.getModelCopy().setActualTotalPayback(newPsa.getActualTotalPayback() - oldApportionAmount*mMoneyDepositReturnContainerEditor.getModel().getExchangeRate() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
					} else {
						ProjectShareAuthorization oldPsa;
						if(apportion.getFriendUserId() != null){
							oldPsa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId AND state <> 'Delete'", mMoneyDepositReturnContainerEditor.getModel().getProjectId(), apportion.getFriendUserId()).executeSingle();
						} else {
							oldPsa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId AND state <> 'Delete'", mMoneyDepositReturnContainerEditor.getModel().getProjectId(), apportion.getLocalFriendId()).executeSingle();
						}
						HyjModelEditor<ProjectShareAuthorization> oldPsaEditor = oldPsa.newModelEditor();
						oldPsaEditor.getModelCopy().setActualTotalPayback(oldPsa.getActualTotalPayback() - oldApportionAmount*mMoneyDepositReturnContainerEditor.getModel().getExchangeRate());
						newPsaEditor.getModelCopy().setActualTotalPayback(newPsa.getActualTotalPayback() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
					}
					newPsaEditor.save();
					
					MoneyReturn moneyReturn = null;
					MoneyPayback moneyPayback = null;
					MoneyReturn moneyReturnOfFinancialOwner = null;
					MoneyPayback moneyPaybackOfFinancialOwner = null;
					if(apportion.get_mId() != null){
						moneyReturn = new Select().from(MoneyReturn.class).where("moneyDepositReturnApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(apportionEditor.getModel().getLocalFriendId() != null){
							moneyPayback = new Select().from(MoneyPayback.class).where("moneyDepositReturnApportionId=? AND ownerFriendId=?", apportionEditor.getModel().getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
						} else {
							moneyPayback = new Select().from(MoneyPayback.class).where("moneyDepositReturnApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
						}
						if(mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId() != null &&
								!mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							moneyReturnOfFinancialOwner = new Select().from(MoneyReturn.class).where("moneyDepositReturnApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
							moneyPaybackOfFinancialOwner = new Select().from(MoneyPayback.class).where("moneyDepositReturnApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
						}
						String previousFinancialOwnerUserId = HyjUtil.ifNull(mMoneyDepositReturnContainerEditor.getModel().getFinancialOwnerUserId() , "");
						String currentFinancialOwnerUserId = HyjUtil.ifNull(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() , "");
						if(!previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
							if(moneyReturn != null){
								moneyReturn.delete();
								moneyReturn = new MoneyReturn();
							}
							if(moneyPayback != null){
								moneyPayback.delete();
								moneyPayback = new MoneyPayback();
							}
							if(moneyReturnOfFinancialOwner != null){
								moneyReturnOfFinancialOwner.delete();
								moneyReturnOfFinancialOwner = new MoneyReturn();
							}
							if(moneyPaybackOfFinancialOwner != null){
								moneyPaybackOfFinancialOwner.delete();
								moneyPaybackOfFinancialOwner = new MoneyPayback();
							}
						}
					}
					if(moneyReturn == null){
						moneyReturn = new MoneyReturn();
					}
					if(moneyPayback == null){
						moneyPayback = new MoneyPayback();
					}
					if(moneyReturnOfFinancialOwner == null){
						moneyReturnOfFinancialOwner = new MoneyReturn();
					}
					if(moneyPaybackOfFinancialOwner == null){
						moneyPaybackOfFinancialOwner = new MoneyPayback();
					}
					
					moneyReturn.setMoneyDepositReturnApportionId(apportionEditor.getModelCopy().getId());
					moneyReturn.setAmount(apportionEditor.getModelCopy().getAmount0());
					moneyReturn.setDate(mMoneyDepositReturnContainerEditor.getModelCopy().getDate());
					moneyReturn.setRemark(mMoneyDepositReturnContainerEditor.getModelCopy().getRemark());
					if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
							|| HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId())){
						moneyReturn.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
						moneyReturn.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
					} else {
						moneyReturn.setFriendUserId(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId());
					}
					moneyReturn.setExchangeRate(mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
					moneyReturn.setGeoLat(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLat());
					moneyReturn.setGeoLon(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLon());
					
					if(mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId() != null){
						MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId());
						moneyReturn.setMoneyAccountId(mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
					} else {
						moneyReturn.setMoneyAccountId(null, null);
					}
					moneyReturn.setLocation(mMoneyDepositReturnContainerEditor.getModelCopy().getLocation());
					moneyReturn.setAddress(mMoneyDepositReturnContainerEditor.getModelCopy().getAddress());
					moneyReturn.setPictureId(mMoneyDepositReturnContainerEditor.getModelCopy().getPictureId());
					moneyReturn.setProject(mMoneyDepositReturnContainerEditor.getModelCopy().getProject());
					moneyReturn.save();
					
					if(apportionEditor.getModelCopy().getLocalFriendId() != null){
						moneyPayback.setMoneyDepositReturnApportionId(apportionEditor.getModelCopy().getId());
//						moneyPayback.setmoneyReturnId(moneyReturn.getId());
						moneyPayback.setAmount(apportionEditor.getModelCopy().getAmount0());
						moneyPayback.setDate(mMoneyDepositReturnContainerEditor.getModelCopy().getDate());
						moneyPayback.setRemark(mMoneyDepositReturnContainerEditor.getModelCopy().getRemark());
						if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
								|| HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId())){
							moneyPayback.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
						} else {
							moneyPayback.setFriendUserId(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId());
						}
						moneyPayback.setLocalFriendId(null);
						moneyPayback.setExchangeRate(mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
						moneyPayback.setGeoLat(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLat());
						moneyPayback.setGeoLon(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLon());
						moneyPayback.setMoneyAccountId(null, moneyReturn.getCurrencyId1());
						moneyPayback.setLocation(mMoneyDepositReturnContainerEditor.getModelCopy().getLocation());
						moneyPayback.setAddress(mMoneyDepositReturnContainerEditor.getModelCopy().getAddress());
						moneyPayback.setPictureId(mMoneyDepositReturnContainerEditor.getModelCopy().getPictureId());
						moneyPayback.setProject(mMoneyDepositReturnContainerEditor.getModelCopy().getProject());
						moneyPayback.setOwnerFriendId(apportionEditor.getModel().getLocalFriendId());
						moneyPayback.setOwnerUserId("");
						moneyPayback.save();
					}
					
					if(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId() != null
							&& !HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId())){
						moneyReturnOfFinancialOwner.setMoneyDepositReturnApportionId(apportionEditor.getModelCopy().getId());
						moneyReturnOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
						moneyReturnOfFinancialOwner.setDate(mMoneyDepositReturnContainerEditor.getModelCopy().getDate());
						moneyReturnOfFinancialOwner.setRemark(mMoneyDepositReturnContainerEditor.getModelCopy().getRemark());
						moneyReturnOfFinancialOwner.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
						moneyReturnOfFinancialOwner.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
						moneyReturnOfFinancialOwner.setExchangeRate(mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
						moneyReturnOfFinancialOwner.setGeoLat(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLat());
						moneyReturnOfFinancialOwner.setGeoLon(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLon());
						moneyReturnOfFinancialOwner.setOwnerUserId(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId());
						
						if(mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId() != null){
							MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId());
							moneyReturnOfFinancialOwner.setMoneyAccountId(mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
						} else {
							moneyReturnOfFinancialOwner.setMoneyAccountId(null, null);
						}
						moneyReturnOfFinancialOwner.setLocation(mMoneyDepositReturnContainerEditor.getModelCopy().getLocation());
						moneyReturnOfFinancialOwner.setAddress(mMoneyDepositReturnContainerEditor.getModelCopy().getAddress());
						moneyReturnOfFinancialOwner.setPictureId(mMoneyDepositReturnContainerEditor.getModelCopy().getPictureId());
						moneyReturnOfFinancialOwner.setProject(mMoneyDepositReturnContainerEditor.getModelCopy().getProject());
						moneyReturnOfFinancialOwner.save();
						
						moneyPaybackOfFinancialOwner.setMoneyDepositReturnApportionId(apportionEditor.getModelCopy().getId());
//						moneyPaybackOfFinancialOwner.setmoneyReturnId(moneyReturn.getId());
						moneyPaybackOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
						moneyPaybackOfFinancialOwner.setDate(mMoneyDepositReturnContainerEditor.getModelCopy().getDate());
						moneyPaybackOfFinancialOwner.setRemark(mMoneyDepositReturnContainerEditor.getModelCopy().getRemark());
						moneyPaybackOfFinancialOwner.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
						moneyPaybackOfFinancialOwner.setLocalFriendId(null);
						moneyPaybackOfFinancialOwner.setExchangeRate(mMoneyDepositReturnContainerEditor.getModelCopy().getExchangeRate());
						moneyPaybackOfFinancialOwner.setGeoLat(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLat());
						moneyPaybackOfFinancialOwner.setGeoLon(mMoneyDepositReturnContainerEditor.getModelCopy().getGeoLon());
						moneyPaybackOfFinancialOwner.setMoneyAccountId(null, moneyReturn.getCurrencyId1());
						moneyPaybackOfFinancialOwner.setLocation(mMoneyDepositReturnContainerEditor.getModelCopy().getLocation());
						moneyPaybackOfFinancialOwner.setAddress(mMoneyDepositReturnContainerEditor.getModelCopy().getAddress());
						moneyPaybackOfFinancialOwner.setPictureId(mMoneyDepositReturnContainerEditor.getModelCopy().getPictureId());
						moneyPaybackOfFinancialOwner.setProject(mMoneyDepositReturnContainerEditor.getModelCopy().getProject());
						moneyPaybackOfFinancialOwner.setOwnerUserId(mMoneyDepositReturnContainerEditor.getModelCopy().getFinancialOwnerUserId());
						moneyPaybackOfFinancialOwner.save();
					}
					
//					if(api.getState() != ApportionItem.UNCHANGED
//							|| !mMoneyDepositReturnContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositReturnContainerEditor.getModel().getProjectId())
//							|| !mMoneyDepositReturnContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyDepositReturnContainerEditor.getModel().getMoneyAccountId())) {
						apportionEditor.save();
//					}
					savedCount++;
				}
		}
		return savedCount;
		
	}

}
