package com.hoyoji.hoyoji.models;

import java.util.UUID;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.btcontrol.R;

@Table(name = "MoneyExpenseApportion", id = BaseColumns._ID)
public class MoneyExpenseApportion extends HyjModel implements MoneyApportion{

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "amount")
	private Double mAmount;

	@Column(name = "moneyExpenseContainerId")
	private String mMoneyExpenseContainerId;

	@Column(name = "friendUserId")
	private String mFriendUserId;

	@Column(name = "localFriendId")
	private String mLocalFriendId;
	
	@Column(name = "apportionType")
	private String mApportionType;

	@Column(name = "remark")
	private String mRemark;

	@Column(name = "lastSyncTime")
	private String mLastSyncTime;

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

	
	public MoneyExpenseApportion(){
		super();
		mUUID = UUID.randomUUID().toString();
		mApportionType = "Share";
	}

	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public Double getAmount() {
		return mAmount;
	}
	
	public Double getAmount0(){
		if(mAmount == null){
			return 0.00;
		}
		return mAmount;
	}

	public void setAmount(Double mAmount) {
		this.mAmount = mAmount;
	}
	
	public String getMoneyExpenseContainerId() {
		return mMoneyExpenseContainerId;
	}

	public void setMoneyExpenseContainerId(String mMoneyExpenseContainerId) {
		this.mMoneyExpenseContainerId = mMoneyExpenseContainerId;
	}
	
	public MoneyExpenseContainer getMoneyExpenseContainer(){
		if(mMoneyExpenseContainerId == null){
			return null;
		}
		return getModel(MoneyExpenseContainer.class, mMoneyExpenseContainerId);
	}
	
	public String getFriendUserId() {
		return mFriendUserId;
	}

	public void setFriendUserId(String mFriendUserId) {
		this.mFriendUserId = mFriendUserId;
	}
	
//	public Friend getFriend(){
//		return getModel(Friend.class, mFriendUserId);
//	}
	
	public String getApportionType() {
		return mApportionType;
	}
	
	public void setApportionType(String mApportionType) {
		this.mApportionType = mApportionType;
	}

	public String getRemark() {
		return mRemark;
	}
	
//	public String getDisplayRemark() {
//		String ownerUser = Friend.getFriendUserDisplayName(this.getOwnerUserId(), this.getProject().getId());
//		if(ownerUser.length() > 0){
//			ownerUser = "[" + ownerUser + "] ";
//		} else {
//			ownerUser = "";
//		}
//		
//		if(mRemark != null && (mRemark.length() > 0 || ownerUser.length() > 0)){
//			return ownerUser + mRemark;
//		} else {
//			return HyjApplication.getInstance().getString(R.string.app_no_remark);
//		}
//	}
//	
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
	
	@Override
	public void validate(HyjModelEditor<? extends HyjModel> modelEditor) {
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
	}
	
	@Override
	public void save(){
		if(this.getOwnerUserId() == null){
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		
		super.save();
		
	}

	@Override
	public void delete(){
		// 维护借贷账户余额
		MoneyAccount debtAccount = null;
//		if(this.getFriendUserId() != null){
//			// 该好友是圈子成员（非自己）
//			if(!this.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//				debtAccount = MoneyAccount.getDebtAccount(this.getMoneyExpenseContainer().getProject().getCurrencyId(), this.getLocalFriendId(), this.getFriendUserId());
//			}
//		} else {
//			// 该好友不是圈子成员
//			Friend friend = HyjModel.getModel(Friend.class, this.getLocalFriendId());
//			// 该好友是本地好友 或 该好友是网络好友（不是自己） 
//			if(this.getFriendUserId() == null){
//			if(!this.getProject().isProjectMember(this.getLocalFriendId(), this.getFriendUserId())){
			if(!HyjApplication.getInstance().getCurrentUser().getId().equals(this.getFriendUserId())){
				if(getMoneyExpenseContainer().getFinancialOwnerUserId() != null){
					if(!getMoneyExpenseContainer().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						debtAccount = MoneyAccount.getDebtAccount(this.getProject().getCurrencyId(), null, getMoneyExpenseContainer().getFinancialOwnerUserId());
					} else {
						debtAccount = MoneyAccount.getDebtAccount(this.getProject().getCurrencyId(), this.getLocalFriendId(), this.getFriendUserId());
					}
				} else {
					debtAccount = MoneyAccount.getDebtAccount(this.getProject().getCurrencyId(), this.getLocalFriendId(), this.getFriendUserId());
				}
			}
//			}
//		}
		if(debtAccount != null){
			HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
			debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - this.getAmount0()*this.getExchangeRate());
			debtAccountEditor.save();
		}
		super.delete();
	}
	

	public Project getProject() {
		return this.getMoneyExpenseContainer().getProject();
	}

	@Override
	public User getFriendUser() {
		if(this.mFriendUserId != null){
			return HyjModel.getModel(User.class, this.mFriendUserId);
		}
		return null;
	}

	public ProjectShareAuthorization getProjectShareAuthorization() {
		if(this.getMoneyExpenseContainer() == null){
			return null;
		} else {
			if(this.getFriendUserId() != null){
				return new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
					this.getMoneyExpenseContainer().getProjectId(), this.getFriendUserId()).executeSingle();
			} else {
				return new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
						this.getMoneyExpenseContainer().getProjectId(), this.getLocalFriendId()).executeSingle();
			}
		}
	}
	
	@Override
	public void setMoneyId(String moneyTransactionId) {
		this.setMoneyExpenseContainerId(moneyTransactionId);
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

	@Override
	public String getMoneyAccountId() {
		if(this.getMoneyExpenseContainer() != null){
			return this.getMoneyExpenseContainer().getMoneyAccountId();
		}
		return null;
	}	
	@Override
	public String getCurrencyId() {
		return this.getMoneyExpenseContainer().getMoneyAccount().getCurrencyId();
	}

	public void setLocalFriendId(String id) {
		this.mLocalFriendId = id;
	}
	
	@Override
	public String getLocalFriendId() {
		return this.mLocalFriendId;
	}
	@Override
	public Double getExchangeRate() {
		return this.getMoneyExpenseContainer().getExchangeRate();
	}
	@Override
	public Long getDate() {
		return this.getMoneyExpenseContainer().getDate();
	}
	
	@Override
	public String getEventId() {
		return this.getMoneyExpenseContainer().getEventId();
	}

//	public String getRemoteLocalFriendName() {
//		return "本地好友";
//	}
}
