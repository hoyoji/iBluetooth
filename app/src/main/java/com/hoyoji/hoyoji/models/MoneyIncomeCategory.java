package com.hoyoji.hoyoji.models;

import java.util.UUID;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;

@Table(name = "MoneyIncomeCategory", id = BaseColumns._ID)
public class MoneyIncomeCategory extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUID;

	@Column(name = "name")
	private String mName;

	@Column(name = "name_pinYin")
	private String mName_pinYin;
	
	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "parentIncomeCategoryId")
	private String mParentIncomeCategoryId;


	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public MoneyIncomeCategory(){
		super();
		mUUID = UUID.randomUUID().toString();
	}

	
	@Override
	public void validate(HyjModelEditor<?> modelEditor) {
		if(this.getName() == null){
			modelEditor.setValidationError("name", "请输入分类名称");
		}else{
			modelEditor.removeValidationError("name");
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

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		if(name == null){
			this.mName_pinYin = "";
		} else if(this.mName == null || !this.mName.equals(name) || this.mName_pinYin == null){
			this.mName_pinYin = HyjUtil.convertToPinYin(name);
		}
		this.mName = name;
	}

	public String getParentIncomeCategoryId() {
		return mParentIncomeCategoryId;
	}
	
	public MoneyIncomeCategory getParentIncomeCategory() {
		if(mParentIncomeCategoryId == null){
			return null;
		}
		return getModel(MoneyIncomeCategory.class, mParentIncomeCategoryId);
	}

	public void setParentIncomeCategoryId(String parentIncomeCategoryId) {
		this.mParentIncomeCategoryId = parentIncomeCategoryId;
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
	
}
