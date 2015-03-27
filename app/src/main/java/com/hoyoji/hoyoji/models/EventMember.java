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

@Table(name = "EventMember", id = BaseColumns._ID)
public class EventMember extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "eventId")
	private String mEventId;

	@Column(name = "localFriendId")
	private String mLocalFriendId;

	@Column(name = "friendUserId")
	private String mFriendUserId;
	
	@Column(name = "friendUserName")
	private String mFriendUserName;
	
	@Column(name = "nickName")
	private String mNickName;
	
	@Column(name = "payState")
	private String mPayState = "UnPaid";
	
	@Column(name = "paidAmount")
	private Double mPaidAmount = 0.0;
	
	@Column(name = "state")
	private String mState;

	@Column(name = "apportionedTotalIncome")
	private Double mApportionedTotalIncome = 0.0;
	
	@Column(name = "apportionedTotalExpense")
	private Double mApportionedTotalExpense = 0.0;
	
	@Column(name = "toBeDetermined")
	private Boolean mToBeDetermined = false;
	
	@Column(name = "eventShareOwnerDataOnly")
	private Boolean mEventShareOwnerDataOnly = false;

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
	
	
	public EventMember(){
		super();
		mUUID = UUID.randomUUID().toString();
	}

	
	@Override
	public void validate(HyjModelEditor<?> modelEditor) {
		if(this.getFriendUserId() == null && this.getLocalFriendId() == null){
			modelEditor.setValidationError("friendUser", R.string.projectEventMemberFormFragment_selectorField_hint_friend);
		} else {
			modelEditor.removeValidationError("friendUser");
		}
	}

	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}
	
	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}

	public String getEventId() {
		return mEventId;
	}

	public void setEventId(String mEventId) {
		this.mEventId = mEventId;
	}
	
	public Event getEvent(){
		if(mEventId == null){
			return null;
		}
		return getModel(Event.class, mEventId);
	}
	
	public String getLocalFriendId() {
		return mLocalFriendId;
	}

	public void setLocalFriendId(String mLocalFriendId) {
		this.mLocalFriendId = mLocalFriendId;
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

	public void setFriendUserName(String mFriendUserName) {
		this.mFriendUserName = mFriendUserName;
	}
	
	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String mNickName) {
		this.mNickName = mNickName;
	}

	public Boolean getEventShareOwnerDataOnly() {
		return mEventShareOwnerDataOnly;
	}

	public void setEventShareOwnerDataOnly(Boolean mEventShareOwnerDataOnly) {
		this.mEventShareOwnerDataOnly = mEventShareOwnerDataOnly;
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
	
	public String getState() {
		return mState;
	}

	public void setState(String mState) {
		this.mState = mState;
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

	public Double getApportionTotal() {
		return this.mApportionedTotalExpense - this.mApportionedTotalIncome;
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


	public JSONObject toJSON() {
		final JSONObject jsonObj = super.toJSON();
		
		jsonObj.remove("apportionedTotalIncome");
		jsonObj.remove("apportionedTotalExpense");
		
		return jsonObj;
	}


	public Boolean getToBeDetermined() {
		return this.mToBeDetermined;
	}

	public void setToBeDetermined(boolean b) {
		this.mToBeDetermined = b;
	}


	public Project getProject() {
		return HyjModel.getModel(Project.class, this.getEvent().getProjectId());
	}


	public String getProjectId() {
		return this.getEvent().getProjectId();
	}


	public ProjectShareAuthorization getProjectShareAuthorization() {
		if(this.mFriendUserId != null){
			return new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId = ? AND state <> 'Delete'", this.getProjectId(), this.mFriendUserId).executeSingle();
		} else {
			return new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND localFriendId = ? AND state <> 'Delete'", this.getProjectId(), this.mLocalFriendId).executeSingle();
		}
	}	
}
