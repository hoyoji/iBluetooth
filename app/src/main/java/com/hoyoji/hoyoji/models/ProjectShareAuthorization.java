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

@Table(name = "ProjectShareAuthorization", id = BaseColumns._ID)
public class ProjectShareAuthorization extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "shareType")
	private String mShareType;

	@Column(name = "remark")
	private String mRemark;

	@Column(name = "friendUserId")
	private String mFriendUserId;

	// 该共享的好友是圈子拥有者的本地好友
	@Column(name = "localFriendId")
	private String mLocalFriendId;

	@Column(name = "friendUserName")
	private String mFriendUserName;
	
	@Column(name = "state")
	private String mState = "Wait";
	
	@Column(name = "projectId")
	private String mProjectId;
	
	@Column(name = "ownerUserId")
	private String mOwnerUserId;
	
	@Column(name = "sharePercentage")
	private Double mSharePercentage = 100.0;

	@Column(name = "sharePercentageType")
	private String mSharePercentageType = "Average";

	@Column(name = "shareAllSubProjects")
	private Boolean mShareAllSubProjects = false;
	
	@Column(name = "toBeDetermined")
	private Boolean mToBeDetermined = false;
	
//	actualTotalIncome : "REAL NOT NULL",
	@Column(name = "actualTotalIncome")
	private Double mActualTotalIncome = 0.0;
	
//	actualTotalExpense : "REAL NOT NULL",
	@Column(name = "actualTotalExpense")
	private Double mActualTotalExpense = 0.0;
	
//	actualTotalBorrow : "REAL NOT NULL",
	@Column(name = "actualTotalBorrow")
	private Double mActualTotalBorrow = 0.0;
	
//	actualTotalLend : "REAL NOT NULL",
	@Column(name = "actualTotalLend")
	private Double mActualTotalLend = 0.0;
	
//	actualTotalReturn : "REAL NOT NULL",
	@Column(name = "actualTotalReturn")
	private Double mActualTotalReturn = 0.0;
	
//	actualTotalPayback : "REAL NOT NULL",
	@Column(name = "actualTotalPayback")
	private Double mActualTotalPayback = 0.0;
	
//	apportionedTotalIncome : "REAL NOT NULL",
	@Column(name = "apportionedTotalIncome")
	private Double mApportionedTotalIncome = 0.0;
	
//	apportionedTotalExpense : "REAL NOT NULL",
	@Column(name = "apportionedTotalExpense")
	private Double mApportionedTotalExpense = 0.0;
	
//	apportionedTotalBorrow : "REAL NOT NULL",
	@Column(name = "apportionedTotalBorrow")
	private Double mApportionedTotalBorrow = 0.0;
	
//	apportionedTotalLend : "REAL NOT NULL",
	@Column(name = "apportionedTotalLend")
	private Double mApportionedTotalLend = 0.0;
	
//	apportionedTotalReturn : "REAL NOT NULL",
	@Column(name = "apportionedTotalReturn")
	private Double mApportionedTotalReturn = 0.0;
	
//	apportionedTotalPayback : "REAL NOT NULL",
	@Column(name = "apportionedTotalPayback")
	private Double mApportionedTotalPayback = 0.0;
	
//	sharedTotalIncome : "REAL NOT NULL",
	@Column(name = "sharedTotalIncome")
	private Double mSharedTotalIncome = 0.0;
	
//	sharedTotalExpense : "REAL NOT NULL",
	@Column(name = "sharedTotalExpense")
	private Double mSharedTotalExpense = 0.0;
	
//	sharedTotalBorrow : "REAL NOT NULL",
	@Column(name = "sharedTotalBorrow")
	private Double mSharedTotalBorrow = 0.0;
	
//	sharedTotalLend : "REAL NOT NULL",
	@Column(name = "sharedTotalLend")
	private Double mSharedTotalLend = 0.0;
	
//	sharedTotalReturn : "REAL NOT NULL",
	@Column(name = "sharedTotalReturn")
	private Double mSharedTotalReturn = 0.0;
	
//	sharedTotalPayback : "REAL NOT NULL",
	@Column(name = "sharedTotalPayback")
	private Double mSharedTotalPayback = 0.0;
	
//	projectShareMoneyExpenseOwnerDataOnly : "INTEGER NOT NULL",
	@Column(name = "projectShareMoneyExpenseOwnerDataOnly")
	private Boolean mProjectShareMoneyExpenseOwnerDataOnly = false;
	
//	projectShareMoneyExpenseAddNew : "INTEGER NOT NULL",
	@Column(name = "projectShareMoneyExpenseAddNew")
	private Boolean mProjectShareMoneyExpenseAddNew = true;
	
//	projectShareMoneyExpenseEdit : "INTEGER NOT NULL",
	@Column(name = "projectShareMoneyExpenseEdit")
	private Boolean mProjectShareMoneyExpenseEdit = true;
	
//	projectShareMoneyExpenseDelete : "INTEGER NOT NULL",
	@Column(name = "projectShareMoneyExpenseDelete")
	private Boolean projectShareMoneyExpenseDelete = true;
	
////
////	projectShareMoneyExpenseDetailOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseDetailOwnerDataOnly")
//	private Boolean projectShareMoneyExpenseDetailOwnerDataOnly = false;
//	
////	projectShareMoneyExpenseDetailAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseDetailAddNew")
//	private Boolean mProjectShareMoneyExpenseDetailAddNew = true;
//	
////	projectShareMoneyExpenseDetailEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseDetailEdit")
//	private Boolean mProjectShareMoneyExpenseDetailEdit = true;
//	
////	projectShareMoneyExpenseDetailDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseDetailDelete")
//	private Boolean mProjectShareMoneyExpenseDetailDelete = true;
//	
////
////	projectShareMoneyIncomeOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeOwnerDataOnly")
//	private Boolean mProjectShareMoneyIncomeOwnerDataOnly = false;
//	
////	projectShareMoneyIncomeAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeAddNew")
//	private Boolean mProjectShareMoneyIncomeAddNew = true;
//	
////	projectShareMoneyIncomeEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeEdit")
//	private Boolean mProjectShareMoneyIncomeEdit = true;
//	
////	projectShareMoneyIncomeDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeDelete")
//	private Boolean mProjectShareMoneyIncomeDelete = true;
//	
////
////	projectShareMoneyIncomeDetailOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeDetailOwnerDataOnly")
//	private Boolean mProjectShareMoneyIncomeDetailOwnerDataOnly = false;
//	
////	projectShareMoneyIncomeDetailAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeDetailAddNew")
//	private Boolean mProjectShareMoneyIncomeDetailAddNew = true;
//	
////	projectShareMoneyIncomeDetailEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeDetailEdit")
//	private Boolean mProjectShareMoneyIncomeDetailEdit = true;
//	
////	projectShareMoneyIncomeDetailDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeDetailDelete")
//	private Boolean mProjectShareMoneyIncomeDetailDelete = true;
//	
////
////	projectShareMoneyExpenseCategoryAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseCategoryAddNew")
//	private Boolean mProjectShareMoneyExpenseCategoryAddNew = true;
//	
////	projectShareMoneyExpenseCategoryEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseCategoryEdit")
//	private Boolean mProjectShareMoneyExpenseCategoryEdit = true;
//	
////	projectShareMoneyExpenseCategoryDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyExpenseCategoryDelete")
//	private Boolean mProjectShareMoneyExpenseCategoryDelete = true;
//	
////
////	projectShareMoneyIncomeCategoryAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeCategoryAddNew")
//	private Boolean mProjectShareMoneyIncomeCategoryAddNew = true;
//	
////	projectShareMoneyIncomeCategoryEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeCategoryEdit")
//	private Boolean mProjectShareMoneyIncomeCategoryEdit = true;
//	
////	projectShareMoneyIncomeCategoryDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyIncomeCategoryDelete")
//	private Boolean mProjectShareMoneyIncomeCategoryDelete = true;
//	
////
////	// projectShareMoneyTransferOwnerDataOnly : "INTEGER NOT NULL",
////	// projectShareMoneyTransferAddNew : "INTEGER NOT NULL",
////	// projectShareMoneyTransferEdit : "INTEGER NOT NULL",
////	// projectShareMoneyTransferDelete : "INTEGER NOT NULL",
////
////	projectShareMoneyLendOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyLendOwnerDataOnly")
//	private Boolean mProjectShareMoneyLendOwnerDataOnly = false;
//	
////	projectShareMoneyLendAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyLendAddNew")
//	private Boolean mProjectShareMoneyLendAddNew = true;
//	
////	projectShareMoneyLendEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyLendEdit")
//	private Boolean mProjectShareMoneyLendEdit = true;
//	
////	projectShareMoneyLendDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyLendDelete")
//	private Boolean mProjectShareMoneyLendDelete = true;
//	
////
////	projectShareMoneyBorrowOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyBorrowOwnerDataOnly")
//	private Boolean mProjectShareMoneyBorrowOwnerDataOnly = false;
//	
////	projectShareMoneyBorrowAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyBorrowAddNew")
//	private Boolean mProjectShareMoneyBorrowAddNew = true;
//	
////	projectShareMoneyBorrowEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyBorrowEdit")
//	private Boolean mProjectShareMoneyBorrowEdit = true;
//	
////	projectShareMoneyBorrowDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyBorrowDelete")
//	private Boolean mProjectShareMoneyBorrowDelete = true;
//	
////
////	projectShareMoneyPaybackOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyPaybackOwnerDataOnly")
//	private Boolean mProjectShareMoneyPaybackOwnerDataOnly = false;
//	
////	projectShareMoneyPaybackAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyPaybackAddNew")
//	private Boolean mProjectShareMoneyPaybackAddNew = true;
//	
////	projectShareMoneyPaybackEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyPaybackEdit")
//	private Boolean mProjectShareMoneyPaybackEdit = true;
//	
////	projectShareMoneyPaybackDelete : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyPaybackDelete")
//	private Boolean mProjectShareMoneyPaybackDelete = true;
//	
////
////	projectShareMoneyReturnOwnerDataOnly : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyReturnOwnerDataOnly")
//	private Boolean mProjectShareMoneyReturnOwnerDataOnly = false;
//	
////	projectShareMoneyReturnAddNew : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyReturnAddNew")
//	private Boolean mProjectShareMoneyReturnAddNew = true;
//	
////	projectShareMoneyReturnEdit : "INTEGER NOT NULL",
//	@Column(name = "projectShareMoneyReturnEdit")
//	private Boolean mProjectShareMoneyReturnEdit = true;
//	
////	projectShareMoneyReturnDelete : "INTEGER NOT NULL"
//	@Column(name = "projectShareMoneyReturnDelete")
//	private Boolean mProjectShareMoneyReturnDelete = true;
	

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public ProjectShareAuthorization(){
		super();
		mUUID = UUID.randomUUID().toString();
	}
	
	@Override
	public void validate(HyjModelEditor modelEditor) {
		if(this.getSharePercentage() == null){
			modelEditor.setValidationError("sharePercentage", R.string.memberFormFragment_editText_hint_sharePercentage);
		} else {
			modelEditor.removeValidationError("sharePercentage");
		}
		if(this.getFriendUserId() == null && this.getLocalFriendId() == null){
			modelEditor.setValidationError("friendUser", R.string.memberFormFragment_editText_hint_friend);
		} else {
			modelEditor.removeValidationError("friendUser");
		}
	}

	public static ProjectShareAuthorization getSelfProjectShareAuthorization(String projectId) {
		
		return new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
				projectId, HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
	
	}
	
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getState() {
		return mState;
	}

	public void setState(String mState) {
		this.mState = mState;
	}

	public String getShareType() {
		return mShareType;
	}

	public void setShareType(String mShareType) {
		this.mShareType = mShareType;
	}
	
	public Double getSharePercentage() {
		return mSharePercentage;
	}

	public void setSharePercentage(Double mSharePercentage) {
		this.mSharePercentage = HyjUtil.toFixed4(mSharePercentage);
	}

	public String getRemark() {
		return mRemark;
	}

	public void setRemark(String mRemark) {
		this.mRemark = mRemark;
	}
	
	
//	public List<ParentProject> getParentProjects() {
//		return getMany(ParentProject.class, "");
//	}
	
	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}

	public Project getProject(){
		if(mProjectId == null){
			return null;
		}
		return getModel(Project.class, mProjectId);
	}

	public User getFriendUser(){
		if(mFriendUserId != null){
			return getModel(User.class, mFriendUserId);
		}
		return null;
	}
	
	public Friend getFriend(){
		if(mFriendUserId != null){
			return new Select().from(Friend.class).where("friendUserId=?", mFriendUserId).executeSingle();
		} else if(mLocalFriendId != null){
			return Friend.getModel(Friend.class, mLocalFriendId);
		}
		return null;
	}
	
	public String getFriendDisplayName(){
		if(mFriendUserId != null){
			Friend friend = new Select().from(Friend.class).where("friendUserId=?", mFriendUserId).executeSingle();
			if(friend != null){
				return friend.getDisplayName();
			} else {
				User user = HyjModel.getModel(User.class, mFriendUserId);
				if(user != null){
					return user.getDisplayName();
				}
			}
		} else if(mLocalFriendId != null){
			Friend friend = Friend.getModel(Friend.class, mLocalFriendId);
			if(friend != null){
				return friend.getDisplayName();
			}
		}
		return this.getFriendUserName();
	}
	
	public String getFriendDisplayName_pinYin(){
		if(mFriendUserId != null){
			Friend friend = new Select().from(Friend.class).where("friendUserId=?", mFriendUserId).executeSingle();
			if(friend != null){
				return friend.getDisplayName_pinYin();
			} else {
				User user = HyjModel.getModel(User.class, mFriendUserId);
				if(user != null){
					return user.getDisplayName_pinYin();
				}
			}
		} else if(mLocalFriendId != null){
			Friend friend = Friend.getModel(Friend.class, mLocalFriendId);
			if(friend != null){
				return friend.getDisplayName_pinYin();
			}
		}
		return this.getFriendUserName_pinYin();
	}
	
	
	public String getProjectId() {
		return mProjectId;
	}

	public void setProjectId(String mProjectId) {
		this.mProjectId = mProjectId;
	}

	public String getFriendUserId() {
		return mFriendUserId;
	}

	public void setFriendUserId(String mFriendUserId) {
		this.mFriendUserId = mFriendUserId;
	}
	
	public String getFriendUserName() {
		return mFriendUserName;
	}
	
	public String getFriendUserName_pinYin() {
		if(mFriendUserName != null){
			return HyjUtil.convertToPinYin(mFriendUserName);
		}
		return mFriendUserName;
	}

	public void setFriendUserName(String mFriendUserName) {
		this.mFriendUserName = mFriendUserName;
	}

	public Boolean getShareAllSubProjects() {
		return mShareAllSubProjects;
	}

	public void setShareAllSubProjects(Boolean mShareAllSubProjects) {
		this.mShareAllSubProjects = mShareAllSubProjects;
	}

	public Double getActualTotalIncome() {
		return mActualTotalIncome;
	}

	public void setActualTotalIncome(Double mActualTotalIncome) {
		this.mActualTotalIncome = HyjUtil.toFixed2(mActualTotalIncome);
	}

	public Double getActualTotalExpense() {
		return mActualTotalExpense;
	}

	public void setActualTotalExpense(Double mActualTotalExpense) {
		this.mActualTotalExpense = HyjUtil.toFixed2(mActualTotalExpense);
	}

	public Double getActualTotalBorrow() {
		return mActualTotalBorrow;
	}

	public void setActualTotalBorrow(Double mActualTotalBorrow) {
		this.mActualTotalBorrow = HyjUtil.toFixed2(mActualTotalBorrow);
	}

	public Double getActualTotalLend() {
		return mActualTotalLend;
	}

	public void setActualTotalLend(Double mActualTotalLend) {
		this.mActualTotalLend = HyjUtil.toFixed2(mActualTotalLend);
	}

	public Double getActualTotalReturn() {
		return mActualTotalReturn;
	}

	public void setActualTotalReturn(Double mActualTotalReturn) {
		this.mActualTotalReturn = HyjUtil.toFixed2(mActualTotalReturn);
	}

	public Double getActualTotalPayback() {
		return mActualTotalPayback;
	}

	public void setActualTotalPayback(Double mActualTotalPayback) {
		this.mActualTotalPayback = HyjUtil.toFixed2(mActualTotalPayback);
	}

	public Double getApportionedTotalIncome() {
		return mApportionedTotalIncome;
	}

	public void setApportionedTotalIncome(Double mApportionedTotalIncome) {
		this.mApportionedTotalIncome = HyjUtil.toFixed2(mApportionedTotalIncome);
	}

	public Double getApportionedTotalExpense() {
		return mApportionedTotalExpense;
	}

	public void setApportionedTotalExpense(Double mApportionedTotalExpense) {
		this.mApportionedTotalExpense = HyjUtil.toFixed2(mApportionedTotalExpense);
	}

	public Double getApportionedTotalBorrow() {
		return mApportionedTotalBorrow;
	}

	public void setApportionedTotalBorrow(Double mApportionedTotalBorrow) {
		this.mApportionedTotalBorrow = HyjUtil.toFixed2(mApportionedTotalBorrow);
	}

	public Double getApportionedTotalLend1() {
		return mApportionedTotalLend;
	}

	public void setApportionedTotalLend(Double mApportionedTotalLend) {
		this.mApportionedTotalLend = HyjUtil.toFixed2(mApportionedTotalLend);
	}

	public Double getApportionedTotalReturn() {
		return mApportionedTotalReturn;
	}

	public void setApportionedTotalReturn(Double mApportionedTotalReturn) {
		this.mApportionedTotalReturn = HyjUtil.toFixed2(mApportionedTotalReturn);
	}

	public Double getApportionedTotalPayback() {
		return mApportionedTotalPayback;
	}

	public void setApportionedTotalPayback(Double mApportionedTotalPayback) {
		this.mApportionedTotalPayback = HyjUtil.toFixed2(mApportionedTotalPayback);
	}

	public Double getSharedTotalIncome() {
		return mSharedTotalIncome;
	}

	public void setSharedTotalIncome(Double mSharedTotalIncome) {
		this.mSharedTotalIncome = HyjUtil.toFixed2(mSharedTotalIncome);
	}

	public Double getSharedTotalExpense() {
		return mSharedTotalExpense;
	}

	public void setSharedTotalExpense(Double mSharedTotalExpense) {
		this.mSharedTotalExpense = HyjUtil.toFixed2(mSharedTotalExpense);
	}

	public Double getSharedTotalBorrow() {
		return mSharedTotalBorrow;
	}

	public void setSharedTotalBorrow(Double mSharedTotalBorrow) {
		this.mSharedTotalBorrow = HyjUtil.toFixed2(mSharedTotalBorrow);
	}

	public Double getSharedTotalLend() {
		return mSharedTotalLend;
	}

	public void setSharedTotalLend(Double mSharedTotalLend) {
		this.mSharedTotalLend = HyjUtil.toFixed2(mSharedTotalLend);
	}

	public Double getSharedTotalReturn() {
		return mSharedTotalReturn;
	}

	public void setSharedTotalReturn(Double mSharedTotalReturn) {
		this.mSharedTotalReturn = HyjUtil.toFixed2(mSharedTotalReturn);
	}

	public Double getSharedTotalPayback() {
		return mSharedTotalPayback;
	}

	public void setSharedTotalPayback(Double mSharedTotalPayback) {
		this.mSharedTotalPayback = HyjUtil.toFixed2(mSharedTotalPayback);
	}

	public Boolean getProjectShareMoneyExpenseOwnerDataOnly() {
		return mProjectShareMoneyExpenseOwnerDataOnly;
	}

	public void setProjectShareMoneyExpenseOwnerDataOnly(
			Boolean mProjectShareMoneyExpenseOwnerDataOnly) {
		this.mProjectShareMoneyExpenseOwnerDataOnly = mProjectShareMoneyExpenseOwnerDataOnly;
	}

	public Boolean getProjectShareMoneyExpenseAddNew() {
		return mProjectShareMoneyExpenseAddNew;
	}

	public void setProjectShareMoneyExpenseAddNew(
			Boolean mProjectShareMoneyExpenseAddNew) {
		this.mProjectShareMoneyExpenseAddNew = mProjectShareMoneyExpenseAddNew;
	}

	public Boolean getProjectShareMoneyExpenseEdit() {
		return mProjectShareMoneyExpenseEdit;
	}

	public void setProjectShareMoneyExpenseEdit(
			Boolean mProjectShareMoneyExpenseEdit) {
		this.mProjectShareMoneyExpenseEdit = mProjectShareMoneyExpenseEdit;
	}

	public Boolean getProjectShareMoneyExpenseDelete() {
		return projectShareMoneyExpenseDelete;
	}

	public void setProjectShareMoneyExpenseDelete(
			Boolean projectShareMoneyExpenseDelete) {
		this.projectShareMoneyExpenseDelete = projectShareMoneyExpenseDelete;
	}

//	public Boolean getProjectShareMoneyExpenseDetailOwnerDataOnly() {
//		return projectShareMoneyExpenseDetailOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyExpenseDetailOwnerDataOnly(
//			Boolean projectShareMoneyExpenseDetailOwnerDataOnly) {
//		this.projectShareMoneyExpenseDetailOwnerDataOnly = projectShareMoneyExpenseDetailOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyExpenseDetailAddNew() {
//		return mProjectShareMoneyExpenseDetailAddNew;
//	}
//
//	public void setProjectShareMoneyExpenseDetailAddNew(
//			Boolean mProjectShareMoneyExpenseDetailAddNew) {
//		this.mProjectShareMoneyExpenseDetailAddNew = mProjectShareMoneyExpenseDetailAddNew;
//	}
//
//	public Boolean getProjectShareMoneyExpenseDetailEdit() {
//		return mProjectShareMoneyExpenseDetailEdit;
//	}
//
//	public void setProjectShareMoneyExpenseDetailEdit(
//			Boolean mProjectShareMoneyExpenseDetailEdit) {
//		this.mProjectShareMoneyExpenseDetailEdit = mProjectShareMoneyExpenseDetailEdit;
//	}
//
//	public Boolean getProjectShareMoneyExpenseDetailDelete() {
//		return mProjectShareMoneyExpenseDetailDelete;
//	}
//
//	public void setProjectShareMoneyExpenseDetailDelete(
//			Boolean mProjectShareMoneyExpenseDetailDelete) {
//		this.mProjectShareMoneyExpenseDetailDelete = mProjectShareMoneyExpenseDetailDelete;
//	}
//
//	public Boolean getProjectShareMoneyIncomeOwnerDataOnly() {
//		return mProjectShareMoneyIncomeOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyIncomeOwnerDataOnly(
//			Boolean mProjectShareMoneyIncomeOwnerDataOnly) {
//		this.mProjectShareMoneyIncomeOwnerDataOnly = mProjectShareMoneyIncomeOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyIncomeAddNew() {
//		return mProjectShareMoneyIncomeAddNew;
//	}
//
//	public void setProjectShareMoneyIncomeAddNew(
//			Boolean mProjectShareMoneyIncomeAddNew) {
//		this.mProjectShareMoneyIncomeAddNew = mProjectShareMoneyIncomeAddNew;
//	}
//
//	public Boolean getProjectShareMoneyIncomeEdit() {
//		return mProjectShareMoneyIncomeEdit;
//	}
//
//	public void setProjectShareMoneyIncomeEdit(Boolean mProjectShareMoneyIncomeEdit) {
//		this.mProjectShareMoneyIncomeEdit = mProjectShareMoneyIncomeEdit;
//	}
//
//	public Boolean getProjectShareMoneyIncomeDelete() {
//		return mProjectShareMoneyIncomeDelete;
//	}
//
//	public void setProjectShareMoneyIncomeDelete(
//			Boolean mProjectShareMoneyIncomeDelete) {
//		this.mProjectShareMoneyIncomeDelete = mProjectShareMoneyIncomeDelete;
//	}
//
//	public Boolean getProjectShareMoneyIncomeDetailOwnerDataOnly() {
//		return mProjectShareMoneyIncomeDetailOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyIncomeDetailOwnerDataOnly(
//			Boolean mProjectShareMoneyIncomeDetailOwnerDataOnly) {
//		this.mProjectShareMoneyIncomeDetailOwnerDataOnly = mProjectShareMoneyIncomeDetailOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyIncomeDetailAddNew() {
//		return mProjectShareMoneyIncomeDetailAddNew;
//	}
//
//	public void setProjectShareMoneyIncomeDetailAddNew(
//			Boolean mProjectShareMoneyIncomeDetailAddNew) {
//		this.mProjectShareMoneyIncomeDetailAddNew = mProjectShareMoneyIncomeDetailAddNew;
//	}
//
//	public Boolean getProjectShareMoneyIncomeDetailEdit() {
//		return mProjectShareMoneyIncomeDetailEdit;
//	}
//
//	public void setProjectShareMoneyIncomeDetailEdit(
//			Boolean mProjectShareMoneyIncomeDetailEdit) {
//		this.mProjectShareMoneyIncomeDetailEdit = mProjectShareMoneyIncomeDetailEdit;
//	}
//
//	public Boolean getProjectShareMoneyIncomeDetailDelete() {
//		return mProjectShareMoneyIncomeDetailDelete;
//	}
//
//	public void setProjectShareMoneyIncomeDetailDelete(
//			Boolean mProjectShareMoneyIncomeDetailDelete) {
//		this.mProjectShareMoneyIncomeDetailDelete = mProjectShareMoneyIncomeDetailDelete;
//	}
//
//	public Boolean getProjectShareMoneyExpenseCategoryAddNew() {
//		return mProjectShareMoneyExpenseCategoryAddNew;
//	}
//
//	public void setProjectShareMoneyExpenseCategoryAddNew(
//			Boolean mProjectShareMoneyExpenseCategoryAddNew) {
//		this.mProjectShareMoneyExpenseCategoryAddNew = mProjectShareMoneyExpenseCategoryAddNew;
//	}
//
//	public Boolean getProjectShareMoneyExpenseCategoryEdit() {
//		return mProjectShareMoneyExpenseCategoryEdit;
//	}
//
//	public void setProjectShareMoneyExpenseCategoryEdit(
//			Boolean mProjectShareMoneyExpenseCategoryEdit) {
//		this.mProjectShareMoneyExpenseCategoryEdit = mProjectShareMoneyExpenseCategoryEdit;
//	}
//
//	public Boolean getProjectShareMoneyExpenseCategoryDelete() {
//		return mProjectShareMoneyExpenseCategoryDelete;
//	}
//
//	public void setProjectShareMoneyExpenseCategoryDelete(
//			Boolean mProjectShareMoneyExpenseCategoryDelete) {
//		this.mProjectShareMoneyExpenseCategoryDelete = mProjectShareMoneyExpenseCategoryDelete;
//	}
//
//	public Boolean getProjectShareMoneyIncomeCategoryAddNew() {
//		return mProjectShareMoneyIncomeCategoryAddNew;
//	}
//
//	public void setProjectShareMoneyIncomeCategoryAddNew(
//			Boolean mProjectShareMoneyIncomeCategoryAddNew) {
//		this.mProjectShareMoneyIncomeCategoryAddNew = mProjectShareMoneyIncomeCategoryAddNew;
//	}
//
//	public Boolean getProjectShareMoneyIncomeCategoryEdit() {
//		return mProjectShareMoneyIncomeCategoryEdit;
//	}
//
//	public void setProjectShareMoneyIncomeCategoryEdit(
//			Boolean mProjectShareMoneyIncomeCategoryEdit) {
//		this.mProjectShareMoneyIncomeCategoryEdit = mProjectShareMoneyIncomeCategoryEdit;
//	}
//
//	public Boolean getProjectShareMoneyIncomeCategoryDelete() {
//		return mProjectShareMoneyIncomeCategoryDelete;
//	}
//
//	public void setProjectShareMoneyIncomeCategoryDelete(
//			Boolean mProjectShareMoneyIncomeCategoryDelete) {
//		this.mProjectShareMoneyIncomeCategoryDelete = mProjectShareMoneyIncomeCategoryDelete;
//	}
//
//	public Boolean getProjectShareMoneyLendOwnerDataOnly() {
//		return mProjectShareMoneyLendOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyLendOwnerDataOnly(
//			Boolean mProjectShareMoneyLendOwnerDataOnly) {
//		this.mProjectShareMoneyLendOwnerDataOnly = mProjectShareMoneyLendOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyLendAddNew() {
//		return mProjectShareMoneyLendAddNew;
//	}
//
//	public void setProjectShareMoneyLendAddNew(Boolean mProjectShareMoneyLendAddNew) {
//		this.mProjectShareMoneyLendAddNew = mProjectShareMoneyLendAddNew;
//	}
//
//	public Boolean getProjectShareMoneyLendEdit() {
//		return mProjectShareMoneyLendEdit;
//	}
//
//	public void setProjectShareMoneyLendEdit(Boolean mProjectShareMoneyLendEdit) {
//		this.mProjectShareMoneyLendEdit = mProjectShareMoneyLendEdit;
//	}
//
//	public Boolean getProjectShareMoneyLendDelete() {
//		return mProjectShareMoneyLendDelete;
//	}
//
//	public void setProjectShareMoneyLendDelete(Boolean mProjectShareMoneyLendDelete) {
//		this.mProjectShareMoneyLendDelete = mProjectShareMoneyLendDelete;
//	}
//
//	public Boolean getProjectShareMoneyBorrowOwnerDataOnly() {
//		return mProjectShareMoneyBorrowOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyBorrowOwnerDataOnly(
//			Boolean mProjectShareMoneyBorrowOwnerDataOnly) {
//		this.mProjectShareMoneyBorrowOwnerDataOnly = mProjectShareMoneyBorrowOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyBorrowAddNew() {
//		return mProjectShareMoneyBorrowAddNew;
//	}
//
//	public void setProjectShareMoneyBorrowAddNew(
//			Boolean mProjectShareMoneyBorrowAddNew) {
//		this.mProjectShareMoneyBorrowAddNew = mProjectShareMoneyBorrowAddNew;
//	}
//
//	public Boolean getProjectShareMoneyBorrowEdit() {
//		return mProjectShareMoneyBorrowEdit;
//	}
//
//	public void setProjectShareMoneyBorrowEdit(Boolean mProjectShareMoneyBorrowEdit) {
//		this.mProjectShareMoneyBorrowEdit = mProjectShareMoneyBorrowEdit;
//	}
//
//	public Boolean getProjectShareMoneyBorrowDelete() {
//		return mProjectShareMoneyBorrowDelete;
//	}
//
//	public void setProjectShareMoneyBorrowDelete(
//			Boolean mProjectShareMoneyBorrowDelete) {
//		this.mProjectShareMoneyBorrowDelete = mProjectShareMoneyBorrowDelete;
//	}
//
//	public Boolean getProjectShareMoneyPaybackOwnerDataOnly() {
//		return mProjectShareMoneyPaybackOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyPaybackOwnerDataOnly(
//			Boolean mProjectShareMoneyPaybackOwnerDataOnly) {
//		this.mProjectShareMoneyPaybackOwnerDataOnly = mProjectShareMoneyPaybackOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyPaybackAddNew() {
//		return mProjectShareMoneyPaybackAddNew;
//	}
//
//	public void setProjectShareMoneyPaybackAddNew(
//			Boolean mProjectShareMoneyPaybackAddNew) {
//		this.mProjectShareMoneyPaybackAddNew = mProjectShareMoneyPaybackAddNew;
//	}
//
//	public Boolean getProjectShareMoneyPaybackEdit() {
//		return mProjectShareMoneyPaybackEdit;
//	}
//
//	public void setProjectShareMoneyPaybackEdit(
//			Boolean mProjectShareMoneyPaybackEdit) {
//		this.mProjectShareMoneyPaybackEdit = mProjectShareMoneyPaybackEdit;
//	}
//
//	public Boolean getProjectShareMoneyPaybackDelete() {
//		return mProjectShareMoneyPaybackDelete;
//	}
//
//	public void setProjectShareMoneyPaybackDelete(
//			Boolean mProjectShareMoneyPaybackDelete) {
//		this.mProjectShareMoneyPaybackDelete = mProjectShareMoneyPaybackDelete;
//	}
//
//	public Boolean getProjectShareMoneyReturnOwnerDataOnly() {
//		return mProjectShareMoneyReturnOwnerDataOnly;
//	}
//
//	public void setProjectShareMoneyReturnOwnerDataOnly(
//			Boolean mProjectShareMoneyReturnOwnerDataOnly) {
//		this.mProjectShareMoneyReturnOwnerDataOnly = mProjectShareMoneyReturnOwnerDataOnly;
//	}
//
//	public Boolean getProjectShareMoneyReturnAddNew() {
//		return mProjectShareMoneyReturnAddNew;
//	}
//
//	public void setProjectShareMoneyReturnAddNew(
//			Boolean mProjectShareMoneyReturnAddNew) {
//		this.mProjectShareMoneyReturnAddNew = mProjectShareMoneyReturnAddNew;
//	}
//
//	public Boolean getProjectShareMoneyReturnEdit() {
//		return mProjectShareMoneyReturnEdit;
//	}
//
//	public void setProjectShareMoneyReturnEdit(Boolean mProjectShareMoneyReturnEdit) {
//		this.mProjectShareMoneyReturnEdit = mProjectShareMoneyReturnEdit;
//	}
//
//	public Boolean getProjectShareMoneyReturnDelete() {
//		return mProjectShareMoneyReturnDelete;
//	}
//
//	public void setProjectShareMoneyReturnDelete(
//			Boolean mProjectShareMoneyReturnDelete) {
//		this.mProjectShareMoneyReturnDelete = mProjectShareMoneyReturnDelete;
//	}
	
	public String getSharePercentageType() {
		return mSharePercentageType;
	}
	
	public void setSharePercentageType(String sharePercentageType) {
		mSharePercentageType = sharePercentageType;
	}
	@Override
	public void save(){
		if(this.getOwnerUserId() == null){
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		super.save();
	}

	public Double getExpenseTotal() {
		return mActualTotalExpense + mActualTotalLend + mActualTotalReturn;
	}
	
	public Double getIncomeTotal() {
		return mActualTotalIncome + mActualTotalBorrow + mActualTotalPayback;
	}
	
	public Double getActualTotal() {		
//		return mActualTotalExpense - mActualTotalIncome + mActualTotalLend - mActualTotalPayback - mActualTotalBorrow + mActualTotalReturn;
	    return this.getExpenseTotal() - this.getIncomeTotal();
	}

	public Double getApportionTotal() {
		return mApportionedTotalExpense - mApportionedTotalIncome + mApportionedTotalLend - mApportionedTotalPayback - mApportionedTotalBorrow + mApportionedTotalReturn;
	}

	public Double getSettlement() {
		return this.getActualTotal() - this.getApportionTotal();
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

//		actualTotalIncome : 0,
//		actualTotalExpense : 0,
//		actualTotalBorrow : 0,
//		actualTotalLend : 0,
//		actualTotalReturn : 0,
//		actualTotalPayback : 0,
//		apportionedTotalIncome : 0,
//		apportionedTotalExpense : 0,
//		apportionedTotalBorrow : 0,
//		apportionedTotalLend : 0,
//		apportionedTotalReturn : 0,
//		apportionedTotalPayback : 0,
//		sharedTotalIncome : 0,
//		sharedTotalExpense : 0,
//		sharedTotalBorrow : 0,
//		sharedTotalLend : 0,
//		sharedTotalReturn : 0,
//		sharedTotalPayback : 0,
		
		jsonObj.remove("actualTotalIncome");
		jsonObj.remove("actualTotalExpense");
		jsonObj.remove("actualTotalBorrow");
		jsonObj.remove("actualTotalLend");
		jsonObj.remove("actualTotalReturn");
		jsonObj.remove("actualTotalPayback");
		jsonObj.remove("apportionedTotalIncome");
		jsonObj.remove("apportionedTotalExpense");
		jsonObj.remove("apportionedTotalBorrow");
		jsonObj.remove("apportionedTotalLend");
		jsonObj.remove("apportionedTotalReturn");
		jsonObj.remove("apportionedTotalPayback");
		jsonObj.remove("sharedTotalIncome");
		jsonObj.remove("sharedTotalExpense");
		jsonObj.remove("sharedTotalBorrow");
		jsonObj.remove("sharedTotalLend");
		jsonObj.remove("sharedTotalReturn");
		jsonObj.remove("sharedTotalPayback");
		
		return jsonObj;
	}

	public Friend getLocalFriend() {
		if(mLocalFriendId == null){
			return null;
		} 
		
		return Friend.getModel(Friend.class, mLocalFriendId);
	}
	
	public String getLocalFriendId() {
		return mLocalFriendId;
	}

	public void setLocalFriendId(String id) {
		mLocalFriendId = id;
	}	

	public Boolean getToBeDetermined() {
		return this.mToBeDetermined;
	}

	public void setToBeDetermined(boolean b) {
		this.mToBeDetermined = b;
	}	
	

}
