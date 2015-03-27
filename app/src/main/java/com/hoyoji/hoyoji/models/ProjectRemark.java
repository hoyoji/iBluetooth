package com.hoyoji.hoyoji.models;

import java.util.UUID;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;

@Table(name = "ProjectRemark", id = BaseColumns._ID)
public class ProjectRemark extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "remark")
	private String mRemark;

	@Column(name = "remark_pinYin")
	private String mRemark_pinYin;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "projectId")
	private String mProjectId;

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	
	public ProjectRemark(){
		super();
		mUUID = UUID.randomUUID().toString();
	}

	
	@Override
	public void validate(HyjModelEditor<?> modelEditor) {
		if(this.getRemark() == null || this.getRemark().length() == 0){
			modelEditor.setValidationError("remark", "请输入备注名称");
		}else{
			modelEditor.removeValidationError("remark");
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

	public String getRemark() {
		return mRemark;
	}

	public void setRemark(String remark) {
		if(remark == null){
			this.mRemark_pinYin = "";
		} else if(this.mRemark == null || !this.mRemark.equals(remark) || this.mRemark_pinYin == null || this.mRemark_pinYin == null){
			this.mRemark_pinYin = HyjUtil.convertToPinYin(remark);
		}
		
		this.mRemark = remark;
	}

	public String getProjectId() {
		return mProjectId;
	}
	
	public Project getProject() {
		if(mProjectId == null){
			return null;
		}
		return getModel(Project.class, mProjectId);
	}

	public void setProjectIdId(String projectId) {
		this.mProjectId = projectId;
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


	public String getRemark_pinYin() {
		return mRemark_pinYin;
	}	
	
}
