package com.hoyoji.hoyoji.models;

import java.util.UUID;

import android.provider.BaseColumns;

import com.activeandroid.Cache;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;

@Table(name = "ClientSyncRecord", id = BaseColumns._ID)
public class ClientSyncRecord extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

//	@Column(name = "recordId", index = true, unique = true)
//	private String mRecordId;

	@Column(name = "tableName", index = true)
	private String mTableName;
	
	@Column(name = "operation", index = true)
	private String mOperation;

	@Column(name = "transactionId")
	private String mTransactionId;
	
	@Column(name = "uploading")
	private Integer mUploading;

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public ClientSyncRecord(){
		super();
		mUploading = 0;
	}
	

	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	
	public Boolean getUploading() {
		return mUploading.intValue() == 1;
	}

	public void setUploading(Boolean uploading) {
		this.mUploading = uploading ? 1 : 0;
	}

//	public String getRecordId(){
//		return mRecordId;
//	}
//
//	public void setRecordId(String recordId) {
//		this.mRecordId = recordId;
//	}

	public String getOperation() {
		return mOperation;
	}

	public void setOperation(String opertion) {
		this.mOperation = opertion;
	}
	
	public String getTableName() {
		return mTableName;
	}

	public void setTableName(String tableName) {
		this.mTableName = tableName;
	}
	
	public String getTransactionId() {
		return mTransactionId;
	}

	public void setTransactionId(String transactionId) {
		this.mTransactionId = transactionId;
	}

	@Override
	public void save(){
//		if(this.getOwnerUserId() == null){
//			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
//		}
		super.save();
	}


	@Override
	public void validate(HyjModelEditor<? extends HyjModel> hyjModelEditor) {
		// TODO Auto-generated method stub
		
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
	
}
