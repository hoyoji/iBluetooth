package com.hoyoji.hoyoji.models;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;

import android.os.Build;
import android.provider.BaseColumns;

@Table(name = "Currency", id = BaseColumns._ID)
public class Currency extends HyjModel {
	
	@Column(name = "id", index = true, unique = true)
	private String mUUID;
	
	@Column(name = "name")
	private String mName;

	@Column(name = "name_pinYin")
	private String mName_pinYin;
	
	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "symbol")
	private String mSymbol;
	
	@Column(name = "code")
	private String mCode;
	

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public Currency(){
		super();
		mUUID = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		if(mName == null){
			this.mName_pinYin = "";
		} else if(this.mName == null || !this.mName.equals(mName) || this.mName_pinYin == null){
			this.mName_pinYin = HyjUtil.convertToPinYin(mName);
		}

		this.mName = mName;
	}

	public String getSymbol() {
		return mSymbol;
	}

	public void setSymbol(String mSymbol) {
		this.mSymbol = mSymbol;
	}

	public String getCode() {
		return mCode;
	}

	public void setCode(String mCode) {
		this.mCode = mCode;
	}



	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}
	
	@Override
	public void validate(HyjModelEditor modelEditor) {
		
	}

	@Override
	public void loadFromJSON(JSONObject obj, boolean syncFromServer){
		
		//if (obj.isNull("symbol")) {
			java.util.Currency localeCurrency = java.util.Currency
					.getInstance(obj
							.optString("code"));
			try {
				obj.put("symbol",
						localeCurrency.getSymbol());
				
				if(Build.VERSION.SDK_INT >= 15){
					obj.put("name",
						localeCurrency.getDisplayName());
				}
				this.setName(obj.optString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		//}
		super.loadFromJSON(obj, syncFromServer);
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
