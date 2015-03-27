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

@Table(name = "MoneyDepositIncomeContainer", id = BaseColumns._ID)
public class MoneyDepositIncomeContainer extends HyjModel {

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

	public MoneyDepositIncomeContainer() {
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

	public List<MoneyDepositIncomeApportion> getApportions() {
		return getMany(MoneyDepositIncomeApportion.class, "moneyDepositIncomeContainerId");
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
		mIsImported = b;
	}
	
	// inner class
	public static class MoneyDepositIncomeContainerEditor extends HyjModelEditor<MoneyDepositIncomeContainer> {
		private  ProjectShareAuthorization mOldProjectShareAuthorization;
		private  ProjectShareAuthorization mNewProjectShareAuthorization;
		
		public MoneyDepositIncomeContainerEditor(MoneyDepositIncomeContainer model) {
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

	public static void deleteApportion(MoneyDepositIncomeApportion moneyDepositIncomeApportion, MoneyDepositIncomeContainerEditor mMoneyDepositIncomeContainerEditor){
		// 维护缴款人的 ProjectShareAuthorization
		ProjectShareAuthorization psa = moneyDepositIncomeApportion.getProjectShareAuthorization();
		HyjModelEditor<ProjectShareAuthorization> psaEditor = psa.newModelEditor();
		psaEditor.getModelCopy().setActualTotalLend(psa.getActualTotalLend() - moneyDepositIncomeApportion.getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
		psaEditor.save();
		
		List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=?", moneyDepositIncomeApportion.getId()).execute();
		for(MoneyBorrow moneyBorrow : moneyBorrows){
			if(HyjApplication.getInstance().getCurrentUser().getId().equals(moneyBorrow.getOwnerUserId())){
				MoneyAccount debtAccount;
				if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null){
					if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), moneyBorrow.getLocalFriendId(), moneyBorrow.getFriendUserId());
					} else {
						debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId());
					}
				} else {
					debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), moneyBorrow.getLocalFriendId(), moneyBorrow.getFriendUserId());
				}
				HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
				debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + moneyBorrow.getProjectAmount());
				debtAccountEditor.save();
			}
			moneyBorrow.delete();
		} 
		List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=?", moneyDepositIncomeApportion.getId()).execute();
		for(MoneyLend moneyLend : moneyLends){
			moneyLend.delete();
		}

		moneyDepositIncomeApportion.delete();
	}
	
	public static int saveApportions(List<ApportionItem> apportionItems,
			MoneyDepositIncomeContainerEditor mMoneyDepositIncomeContainerEditor) {
		int count = apportionItems.size();
		int savedCount = 0;
		for (int i = 0; i < count; i++) {
			ApportionItem<MoneyApportion> api = apportionItems.get(i);
			MoneyDepositIncomeApportion apportion = (MoneyDepositIncomeApportion) api.getApportion();
            
				if(api.getState() == ApportionItem.DELETED ){
					deleteApportion(apportion, mMoneyDepositIncomeContainerEditor);
					
//					// 维护缴款人的 ProjectShareAuthorization
//					ProjectShareAuthorization psa = api.getProjectShareAuthorization();
//					HyjModelEditor<ProjectShareAuthorization> psaEditor = psa.newModelEditor();
//					psaEditor.getModelCopy().setActualTotalLend(psa.getActualTotalLend() - apportion.getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
//					psaEditor.save();
//					
//					List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=?", apportion.getId()).execute();
//					for(MoneyBorrow moneyBorrow : moneyBorrows){
//						if(HyjApplication.getInstance().getCurrentUser().getId().equals(moneyBorrow.getOwnerUserId())) {
//							MoneyAccount debtAccount = null;
//							if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null){
//								debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId());
//							} else {
//								debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), moneyBorrow.getLocalFriendId(), moneyBorrow.getFriendUserId());
//							}
//						
//							HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//							debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + moneyBorrow.getProjectAmount());
//							debtAccountEditor.save(); 
//						}
//						moneyBorrow.delete();
//					} 
//					List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=?", apportion.getId()).execute();
//					for(MoneyLend moneyLend : moneyLends){
//						moneyLend.delete();
//					}
//					apportion.delete();
				} else {
					HyjModelEditor<MoneyDepositIncomeApportion> apportionEditor = apportion.newModelEditor();
					Double oldApportionAmount = apportionEditor.getModel().getAmount0();
					if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() == null){
						oldApportionAmount = 0.0;
					}
					if(api.getState() != ApportionItem.UNCHANGED		
							|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositIncomeContainerEditor.getModel().getProjectId())
							|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyDepositIncomeContainerEditor.getModel().getMoneyAccountId())) {
						api.saveToCopy(apportionEditor.getModelCopy());
					}
					
					MoneyAccount debtAccount = null;
					// 该好友是网络好友 或 该好友是本地好友
					if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
						} else {
							debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
						}
					} else {
						debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
					}
					if(api.getState() == ApportionItem.NEW){
		                if(debtAccount != null){
		                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
		                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
		                	debtAccountEditor.save();
		                }else {
		                	if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
								if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
			                	} else {
			                		MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), null, mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
			                	}
		                	} else {
		                		MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
				            }
			            }
					} else {
						MoneyAccount oldDebtAccount = null;
						if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null){
							if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
								oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
							} else {
								oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModel().getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId());
							}
						} else {
							oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
						}
						if(debtAccount == null){
							if(oldDebtAccount != null){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldApportionAmount*apportionEditor.getModel().getExchangeRate());
								oldDebtAccountEditor.save();
							}
							if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
								if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
								} else {
									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), null, mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
								}
							} else {
								MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getOwnerUserId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
							}
						}else if(oldDebtAccount != null && debtAccount.getId().equals(oldDebtAccount.getId())){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldApportionAmount*apportionEditor.getModel().getExchangeRate() - apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
								oldDebtAccountEditor.save();
						}else {
							if(oldDebtAccount != null){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldApportionAmount*apportionEditor.getModel().getExchangeRate());
								oldDebtAccountEditor.save();
							}
							HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
		                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
		                	debtAccountEditor.save();
						}
					}
					
					// 维护缴款人的 ProjectShareAuthorization
					ProjectShareAuthorization newPsa = api.getProjectShareAuthorization();
					HyjModelEditor<ProjectShareAuthorization> newPsaEditor = newPsa.newModelEditor();
					if(apportion.get_mId() == null) {
						newPsaEditor.getModelCopy().setActualTotalLend(newPsa.getActualTotalLend() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
					} else if(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositIncomeContainerEditor.getModel().getProjectId())){
						newPsaEditor.getModelCopy().setActualTotalLend(newPsa.getActualTotalLend() - oldApportionAmount*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
					} else {
						ProjectShareAuthorization oldPsa;
						if(apportion.getFriendUserId() != null){
							oldPsa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId AND state <> 'Delete'", mMoneyDepositIncomeContainerEditor.getModel().getProjectId(), apportion.getFriendUserId()).executeSingle();
						} else {
							oldPsa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId AND state <> 'Delete'", mMoneyDepositIncomeContainerEditor.getModel().getProjectId(), apportion.getLocalFriendId()).executeSingle();
						}
						HyjModelEditor<ProjectShareAuthorization> oldPsaEditor = oldPsa.newModelEditor();
						oldPsaEditor.getModelCopy().setActualTotalLend(oldPsa.getActualTotalLend() - oldApportionAmount*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
						newPsaEditor.getModelCopy().setActualTotalLend(newPsa.getActualTotalLend() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
					}
					newPsaEditor.save();
					
					MoneyBorrow moneyBorrow = null;
					MoneyLend moneyLend = null;
					MoneyBorrow moneyBorrowOfFinancialOwner = null;
					MoneyLend moneyLendOfFinancialOwner = null;
					if(apportion.get_mId() != null){
						moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						if(apportionEditor.getModel().getLocalFriendId() != null){
							moneyLend = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=? AND ownerFriendId=?", apportionEditor.getModel().getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
						} else {
							moneyLend = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
						}
						if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null &&
								!mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
							moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
						}
						String previousFinancialOwnerUserId = HyjUtil.ifNull(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() , "");
						String currentFinancialOwnerUserId = HyjUtil.ifNull(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() , "");
						if(!previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
							if(moneyBorrow != null){
								moneyBorrow.delete();
								moneyBorrow = new MoneyBorrow();
							}
							if(moneyLend != null){
								moneyLend.delete();
								moneyLend = new MoneyLend();
							}
							if(moneyBorrowOfFinancialOwner != null){
								moneyBorrowOfFinancialOwner.delete();
								moneyBorrowOfFinancialOwner = new MoneyBorrow();
							}
							if(moneyLendOfFinancialOwner != null){
								moneyLendOfFinancialOwner.delete();
								moneyLendOfFinancialOwner = new MoneyLend();
							}
						}
					}
					if(moneyBorrow == null){
						moneyBorrow = new MoneyBorrow();
					}
					if(moneyLend == null){
						moneyLend = new MoneyLend();
					}
					if(moneyBorrowOfFinancialOwner == null){
						moneyBorrowOfFinancialOwner = new MoneyBorrow();
					}
					if(moneyLendOfFinancialOwner == null){
						moneyLendOfFinancialOwner = new MoneyLend();
					}
					
					moneyBorrow.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
					moneyBorrow.setAmount(apportionEditor.getModelCopy().getAmount0());
					moneyBorrow.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
					moneyBorrow.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
					if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
							|| HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId())){
						moneyBorrow.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
						moneyBorrow.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
					} else {
						moneyBorrow.setFriendUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
					}
					moneyBorrow.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
					moneyBorrow.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
					moneyBorrow.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
					
					if(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
						MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId());
						moneyBorrow.setMoneyAccountId(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
					} else {
						moneyBorrow.setMoneyAccountId(null, null);
					}

					moneyBorrow.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
					moneyBorrow.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
					moneyBorrow.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
					moneyBorrow.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
					moneyBorrow.save();
					
					if(apportionEditor.getModelCopy().getLocalFriendId() != null){
						moneyLend.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
//						moneyLend.setMoneyBorrowId(moneyBorrow.getId());
						moneyLend.setAmount(apportionEditor.getModelCopy().getAmount0());
						moneyLend.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
						moneyLend.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
								|| HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId())){
							moneyLend.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
						} else {
							moneyLend.setFriendUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
						}
						moneyLend.setLocalFriendId(null);
						moneyLend.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
						moneyLend.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
						moneyLend.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
						moneyLend.setMoneyAccountId(null, moneyBorrow.getCurrencyId1());
						moneyLend.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
						moneyLend.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
						moneyLend.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
						moneyLend.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
						moneyLend.setOwnerFriendId(apportionEditor.getModel().getLocalFriendId());
						moneyLend.setOwnerUserId("");
						moneyLend.save();
					}
					
					if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null
							&& !HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId())){
						moneyBorrowOfFinancialOwner.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
						moneyBorrowOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
						moneyBorrowOfFinancialOwner.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
						moneyBorrowOfFinancialOwner.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
						moneyBorrowOfFinancialOwner.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
						moneyBorrowOfFinancialOwner.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
						moneyBorrowOfFinancialOwner.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
						moneyBorrowOfFinancialOwner.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
						moneyBorrowOfFinancialOwner.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
						moneyBorrowOfFinancialOwner.setOwnerUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
						
						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
							MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId());
							moneyBorrowOfFinancialOwner.setMoneyAccountId(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
						} else {
							moneyBorrowOfFinancialOwner.setMoneyAccountId(null, null);
						}
						moneyBorrowOfFinancialOwner.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
						moneyBorrowOfFinancialOwner.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
						moneyBorrowOfFinancialOwner.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
						moneyBorrowOfFinancialOwner.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
						moneyBorrowOfFinancialOwner.save();
						
						moneyLendOfFinancialOwner.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
//						moneyLendOfFinancialOwner.setMoneyBorrowId(moneyBorrow.getId());
						moneyLendOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
						moneyLendOfFinancialOwner.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
						moneyLendOfFinancialOwner.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
						moneyLendOfFinancialOwner.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
						moneyLendOfFinancialOwner.setLocalFriendId(null);
						moneyLendOfFinancialOwner.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
						moneyLendOfFinancialOwner.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
						moneyLendOfFinancialOwner.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
						moneyLendOfFinancialOwner.setMoneyAccountId(null, moneyBorrow.getCurrencyId1());
						moneyLendOfFinancialOwner.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
						moneyLendOfFinancialOwner.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
						moneyLendOfFinancialOwner.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
						moneyLendOfFinancialOwner.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
						moneyLendOfFinancialOwner.setOwnerUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
						moneyLendOfFinancialOwner.save();
					}
//					if(api.getState() != ApportionItem.UNCHANGED
//							|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositIncomeContainerEditor.getModel().getProjectId())
//							|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyDepositIncomeContainerEditor.getModel().getMoneyAccountId())) {
						apportionEditor.save();
//					}
					savedCount++;
				}
		}
		return savedCount;
	}
}
