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

@Table(name = "MoneyDepositExpenseContainer", id = BaseColumns._ID)
public class MoneyDepositExpenseContainer extends HyjModel {

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
//
//	// 导入的或新增借入时，替本地好友生成的
//	@Column(name = "moneyBorrowId")
//	private String mMoneyBorrowId;
	@Column(name = "isImported")
	private Boolean mIsImported = false;
	
//	// 当预交会费有财务负责人时，会生成多一笔借出（从财务负责人到收款人）
//	// 此时，两笔指出将会用这个字段关连
//	@Column(name = "moneyDepositExpenseContainerId")
//	private String mMoneyDepositExpenseContainerId;
//
//	@Column(name = "moneyExpenseApportionId")
//	private String mMoneyExpenseApportionId;
//	
//	@Column(name = "moneyIncomeApportionId")
//	private String mMoneyIncomeApportionId;

	@Column(name = "remark")
	private String mRemark;
	
	@Column(name = "lastSyncTime")
	private String mLastSyncTime;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "financialOwnerUserId")
	private String mFinancialOwnerUserId;

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

	public MoneyDepositExpenseContainer() {
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser()
				.getUserData();
		mUUID = UUID.randomUUID().toString();
		this.setMoneyAccount(userData.getActiveMoneyAccount());
		this.setProject(userData.getActiveProject());
		mExchangeRate = 1.00;
		mPaybackedAmount = 0.00;
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
		if(mMoneyAccount == null){
			this.setMoneyAccountId(null, null);
		} else {
			this.setMoneyAccountId(mMoneyAccount.getId(), mMoneyAccount.getCurrencyId());
		}
//		this.mMoneyAccountId = mMoneyAccount.getId();
//		this.mCurrencyId = mMoneyAccount.getCurrencyId();
	}

	public String getMoneyAccountId() {
		return mMoneyAccountId;
	}

	public void setMoneyAccountId(String mMoneyAccountId, String currencyId) {
		this.mMoneyAccountId = mMoneyAccountId;
		this.mCurrencyId = currencyId;
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
			this.mProjectCurrencyId = null;
			return;
		}
		this.mProjectId = mProject.getId();
		this.mProjectCurrencyId = mProject.getCurrencyId();
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

	public void setFriendUserId(String id) {
		this.mFriendUserId = id;
		
	}

	public String getFriendUserId() {
		return this.mFriendUserId;
	}

	public String getLocalFriendId() {
		return this.mLocalFriendId;
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

	public String getCurrencyId() {
		return this.mCurrencyId;
	}

	public Long getPaybackDate() {
		return this.mPaybackDate;
	}

	public Double getPaybackedAmount() {
		return this.mPaybackedAmount;
	}

	public String getProjectCurrencyId() {
		return this.mProjectCurrencyId;
	}

	public void setIsImported(boolean b) {
		this.mIsImported = b;
		
	}

}
