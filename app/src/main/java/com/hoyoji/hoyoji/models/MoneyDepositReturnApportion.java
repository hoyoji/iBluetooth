package com.hoyoji.hoyoji.models;

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

@Table(name = "MoneyDepositReturnApportion", id = BaseColumns._ID)
public class MoneyDepositReturnApportion extends HyjModel implements MoneyApportion{

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "amount")
	private Double mAmount;

	@Column(name = "moneyDepositReturnContainerId")
	private String mMoneyDepositReturnContainerId;

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
	
	public MoneyDepositReturnApportion(){
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
		if(mAmount != null){
			mAmount = HyjUtil.toFixed2(mAmount);
		}
		this.mAmount = mAmount;
	}
	
	public String getMoneyDepositReturnContainerId() {
		return mMoneyDepositReturnContainerId;
	}

	public void setMoneyDepositReturnContainerId(String mMoneyDepositReturnContainerId) {
		this.mMoneyDepositReturnContainerId = mMoneyDepositReturnContainerId;
	}
	
	public MoneyDepositReturnContainer getMoneyDepositReturnContainer(){
		return getModel(MoneyDepositReturnContainer.class, mMoneyDepositReturnContainerId);
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
			modelEditor.setValidationError("amount",R.string.moneyIncomeFormFragment_editText_hint_amount);
		}else if(this.getAmount() < 0){
			modelEditor.setValidationError("amount",R.string.moneyIncomeFormFragment_editText_validationError_negative_amount);
		}else if(this.getAmount() > 99999999){
			modelEditor.setValidationError("amount",R.string.moneyIncomeFormFragment_editText_validationError_beyondMAX_amount);
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

//	@Override
//	public void delete(){
////		ProjectShareAuthorization projectShareAuthorization = this.getProjectShareAuthorization();
////		HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
////		projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (this.getAmount0() * this.getMoneyIncome().getExchangeRate()));
////		projectShareAuthorizationEditor.save();
////		
////		if(!this.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
////			MoneyAccount debtAccount = MoneyAccount.getDebtAccount(this.getMoneyIncome().getMoneyAccount().getCurrencyId(), this.getFriendUserId());
////			HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
////			debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + this.getAmount0());
////			debtAccountEditor.save();
////		}
//		
//		// 维护借贷账户余额
//		MoneyAccount debtAccount = null;
//		if(this.getFriendUserId() != null){
//			if(!this.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//			    debtAccount = MoneyAccount.getDebtAccount(this.getMoneyDepositIncomeContainer().getMoneyAccount().getCurrencyId(), this.getFriendUserId());
//			}
//		}else{
//			// 该好友不是圈子成员
//			Friend friend = HyjModel.getModel(Friend.class, this.getLocalFriendId());
//			// 该好友是本地好友 或 该好友是网络好友（不是自己） 
//			if(friend.getFriendUserId() == null || !friend.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//				debtAccount = MoneyAccount.getDebtAccount(this.getMoneyDepositIncomeContainer().getMoneyAccount().getCurrencyId(), friend);
//			}
//		}
//		if(debtAccount != null){
//			HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//			debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + this.getAmount0());
//			debtAccountEditor.save();
//		}
//		
//		super.delete();
//	}
	
//	public void _delete() {
//		ProjectShareAuthorization projectShareAuthorization = this.getProjectShareAuthorization();
//		HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
//		projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (this.getAmount0() * this.getMoneyIncome().getExchangeRate()));
//		projectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(projectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (this.getMoneyIncome().getAmount0() * this.getMoneyIncome().getExchangeRate()));
//		projectShareAuthorizationEditor.save();
//		super.delete();
//	}

	public Project getProject() {
		return this.getMoneyDepositReturnContainer().getProject();
	}

	@Override
	public User getFriendUser() {
		if(this.mFriendUserId != null){
			return HyjModel.getModel(User.class, this.mFriendUserId);
		}
		return null;
	}

	public ProjectShareAuthorization getProjectShareAuthorization() {
		if(this.getMoneyDepositReturnContainer() == null){
			return null;
		} else {	
			if(this.getFriendUserId() != null){
				return new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=? AND state <> 'Delete'", 
				   this.getMoneyDepositReturnContainer().getProjectId(), this.getFriendUserId()).executeSingle();
			} else {
				return new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=? AND state <> 'Delete'", 
				   this.getMoneyDepositReturnContainer().getProjectId(), this.getLocalFriendId()).executeSingle();
			}
		}
	}
	
	
	@Override
	public void setMoneyId(String moneyTransactionId) {
		this.setMoneyDepositReturnContainerId(moneyTransactionId);
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
		if(this.getMoneyDepositReturnContainer() != null){
			return this.getMoneyDepositReturnContainer().getMoneyAccountId();
		}
		return null;
	}	
	@Override
	public String getCurrencyId() {
		return this.getMoneyDepositReturnContainer().getMoneyAccount().getCurrencyId();
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
		return this.getMoneyDepositReturnContainer().getExchangeRate();
	}
	@Override
	public Long getDate() {
		return this.getMoneyDepositReturnContainer().getDate();
	}

	public String getFriendDisplayName(String projectId) {
		String displayName = "";
		if(this.getLocalFriendId() != null){
			Friend f = Friend.getModel(Friend.class, this.getLocalFriendId());
			if(f != null){
				displayName = f.getDisplayName();
			} else {
				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", this.getLocalFriendId(), projectId).executeSingle();
				if(psa != null){
					return psa.getFriendUserName();
				} else {
					return "";
				}
			}
		} else if(this.getFriendUserId() != null){
			displayName = Friend.getFriendUserDisplayName(this.getFriendUserId());
//			if(displayName.length() == 0){
//				displayName = "自己";
//			}
		}
		return displayName;
	}
	
	@Override
	public String getEventId() {
		return this.getMoneyDepositReturnContainer().getEventId();
	}

//	public String getRemoteLocalFriendName() {
//		// TODO Auto-generated method stub
//		return "本地好友";
//	}

}
