package com.hoyoji.hoyoji.models;
import org.json.JSONObject;

import android.support.v4.app.Fragment;

import com.hoyoji.android.hyjframework.HyjModel;

public interface MoneyApportion {
	public Long get_mId();
	public String getId();
	public Project getProject();
	public Double getAmount();
	public void setAmount(Double totalAmount);
//	public Project getProject();
//	public ProjectShareAuthorization getProjectShareAuthorization();
	public String getFriendUserId();
	public User getFriendUser();
	public String getApportionType();
	public void setApportionType(String type);
	public void setMoneyId(String moneyTransactionId);
	public void setFriendUserId(String friendUserId);
	public void setLocalFriendId(String friendId);
	public String getMoneyAccountId();
	public String getCurrencyId();
	public String getLocalFriendId();
	public Double getExchangeRate();
	public String getOwnerUserId();
	public Long getDate();
	public JSONObject toJSON();
	public String getEventId();
}
