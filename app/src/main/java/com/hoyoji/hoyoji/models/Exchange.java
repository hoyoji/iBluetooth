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

@Table(name = "Exchange", id = BaseColumns._ID)
public class Exchange extends HyjModel {

	@Column(name = "id", index = true, unique = true)
	private String mUUId;

	@Column(name = "rate")
	private Double mRate;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "localCurrencyId")
	private String mLocalCurrencyId;

	@Column(name = "foreignCurrencyId")
	private String mForeignCurrencyId;
	
	@Column(name = "autoUpdate")
	private Boolean mAutoUpdate;


	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public Exchange(){
		super();
		mUUId = UUID.randomUUID().toString();
		mLocalCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
		setAutoUpdate(true);
	}
	
	public static Exchange getExchange(String fromCurrency, String toCurrency){
		return new Select().from(Exchange.class).where("localCurrencyId=? AND foreignCurrencyId=?", fromCurrency, toCurrency).executeSingle();
		
	}
	
	public static Double getExchangeRate(String fromCurrency, String toCurrency){
		Double rate = null;
		Exchange exchange =  new Select().from(Exchange.class).where("localCurrencyId=? AND foreignCurrencyId=?", fromCurrency, toCurrency).executeSingle();
	    if(exchange != null){
	    	rate = exchange.getRate();
	    }else{
	    	exchange = new Select().from(Exchange.class).where("localCurrencyId=? AND foreignCurrencyId=?", toCurrency, fromCurrency).executeSingle();
	        if(exchange != null){
	        	rate = 1/(exchange.getRate());
	        }
	    }
	    return rate;
	}
	
	@Override
	public void validate(HyjModelEditor<?> modelEditor) {
		if(this.getLocalCurrencyId() == null){
			modelEditor.setValidationError("localCurrency",R.string.exchangeFormFragment_editText_hint_localCurrency);
		}else{
			modelEditor.removeValidationError("localCurrency");
		}
		
		if(this.getForeignCurrencyId() == null){
			modelEditor.setValidationError("foreignCurrency",R.string.exchangeFormFragment_editText_hint_foreignCurrency);
		}else{
			modelEditor.removeValidationError("foreignCurrency");
		}
		
		if(this.getRate() == null){
			modelEditor.setValidationError("rate",R.string.exchangeFormFragment_editText_hint_rate);
		}else{
			modelEditor.removeValidationError("rate");
		}
	}

	public String getId() {
		return mUUId;
	}

	public void setId(String mId) {
		this.mUUId = mId;
	}
	
	public String getOwnerUserId() {
		return mOwnerUserId;
	}

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}

	public Double getRate() {
		return mRate;
	}

	public void setRate(Double mRate) {
		if(mRate != null){
			mRate = HyjUtil.toFixed2(mRate);
		}
		this.mRate = mRate;
	}

	public String getLocalCurrencyId() {
		return mLocalCurrencyId;
	}
	
	public Currency getLocalCurrency() {
		if(mLocalCurrencyId == null){
			return null;
		}
		return getModel(Currency.class, mLocalCurrencyId);
	}

	public void setLocalCurrencyId(String mLocalCurrencyId) {
		this.mLocalCurrencyId = mLocalCurrencyId;
	}

	public String getForeignCurrencyId() {
		return mForeignCurrencyId;
	}

	public Currency getForeignCurrency() {
		if(mForeignCurrencyId == null){
			return null;
		}
		return getModel(Currency.class, mForeignCurrencyId);
	}
	
	public void setForeignCurrencyId(String mForeignCurrencyId) {
		this.mForeignCurrencyId = mForeignCurrencyId;
	}

	public Boolean getAutoUpdate() {
		return mAutoUpdate;
	}

	public void setAutoUpdate(Boolean mAutoUpdate) {
		this.mAutoUpdate = mAutoUpdate;
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
