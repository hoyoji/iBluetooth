package com.hoyoji.hoyoji.models;

import java.util.List;
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

@Table(name = "MoneyTransfer", id = BaseColumns._ID)
public class MoneyTransfer extends HyjModel{
     
	@Column(name = "id", index = true, unique = true)
	private String mUUID;
	
	@Column(name = "pictureId")
	private String mPictureId;

	@Column(name = "pictures")
	private String mPictures;
	
	@Column(name = "date")
	private Long mDate;

	@Column(name = "transferOutAmount")
	private Double mTransferOutAmount;
	
	@Column(name = "transferOutFriendUserId")
	private String mTransferOutFriendUserId;
	
	@Column(name = "transferOutLocalFriendId")
	private String mTransferOutLocalFriendId;
	
	@Column(name = "transferOutId")
	private String mTransferOutId;
	
	@Column(name = "transferInAmount")
	private Double mTransferInAmount;
	
	@Column(name = "transferInFriendUserId")
	private String mTransferInFriendUserId;
	
	@Column(name = "transferInLocalFriendId")
	private String mTransferInLocalFriendId;
	
	@Column(name = "transferInId")
	private String mTransferInId;
	
	@Column(name = "exchangeRate")
	private Double mExchangeRate;

	@Column(name = "transferInExchangeRate")
	private Double mTransferInExchangeRate;
	
	@Column(name = "transferOutExchangeRate")
	private Double mTransferOutExchangeRate;
	
	@Column(name = "projectId")
	private String mProjectId;
	
	@Column(name = "eventId")
	private String mEventId;

	@Column(name = "projectCurrencyId")
	private String mProjectCurrencyId;
	
	@Column(name = "transferType")
	private String mTransferType;
	
	@Column(name = "remark")
	private String mRemark;

	@Column(name = "lastSyncTime")
	private String mLastSyncTime;

	@Column(name = "ownerUserId")
	private String mOwnerUserId;

	@Column(name = "location")
	private String mLocation;
	
	@Column(name = "geoLon")
	private String mGeoLon;
	
	@Column(name = "geoLat")
	private String mGeoLat;
	
	@Column(name = "address")
	private String mAddress;	

	@Column(name = "_creatorId")
	private String m_creatorId;

	@Column(name = "serverRecordHash")
	private String mServerRecordHash;

	@Column(name = "lastServerUpdateTime")
	private String mLastServerUpdateTime;

	@Column(name = "lastClientUpdateTime")
	private Long mLastClientUpdateTime;
	
	public MoneyTransfer(){
		super();
		UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
		mUUID = UUID.randomUUID().toString();
		mTransferOutId = userData.getActiveMoneyAccountId();
		mTransferInId = userData.getActiveMoneyAccountId();
		this.setProjectId(userData.getActiveProjectId());
		if(userData.getActiveProject() != null){
			this.setProjectCurrencyId(userData.getActiveProject().getCurrencyId());
		}
		mExchangeRate = 1.00;
		mTransferType = "Transfer";
	}
	
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getPictureId() {
		return mPictureId;
	}

	public void setPictureId(String mPictureId) {
		this.mPictureId = mPictureId;
	}

	public Picture getPicture(){
		if(mPictureId == null){
			return null;
		}
		
		Picture pic = getModel(Picture.class, mPictureId);
		if(pic == null && this.get_mId() != null && !this.isClientNew()){
			HyjUtil.asyncLoadPicture(mPictureId, this.getClass().getSimpleName(), this.getId());
		}
		
		return (Picture) getModel(Picture.class, mPictureId);
	}

	public void setPicture(Picture picture){
		this.setPictureId(picture.getId());
	}
	
	public List<Picture> getPictures(){
		return getMany(Picture.class, "recordId", "displayOrder");
	}
	
	public Long getDate() {
		return mDate;
	}

	public void setDate(Long mDate) {
		this.mDate = mDate;
	}

	public Double getTransferOutAmount() {
		return mTransferOutAmount;
	}
	
	public Double getTransferOutAmount0() {
		if(mTransferOutAmount == null){
			return 0.00;
		}
		return mTransferOutAmount;
	}
	
	public Double getTransferProjectAmount() {
		Double rate = 1.0;
//		Currency projectCurrency = this.getProject().getCurrency();
		if(this.getTransferOutExchangeRate() != null){
//			if(!projectCurrency.getId().equals(this.getTransferOut().getCurrencyId())){
//				Double exchange = Exchange.getExchangeRate(this.getTransferOut().getCurrencyId(),projectCurrency.getId());
//			    if(exchange != null){
//			    	rate = exchange;
//			    }
//			}
			return this.getTransferOutAmount0()*this.getTransferOutExchangeRate();
		}else if(this.getTransferInExchangeRate() != null){
//			if(!projectCurrency.getId().equals(this.getTransferIn().getCurrencyId())){
//				Double exchange = Exchange.getExchangeRate(this.getTransferIn().getCurrencyId(),projectCurrency.getId());
//			    if(exchange != null){
//			    	rate = exchange;
//			    }
//			}
			return this.getTransferInAmount0()*this.getTransferInExchangeRate();
		}else{
			return this.getTransferInAmount0()*rate;
		}
//		return null;
	}

	public void setTransferOutAmount(Double mTransferOutAmount) {
		if(mTransferOutAmount != null) {
			mTransferOutAmount = HyjUtil.toFixed2(mTransferOutAmount);
		}
		this.mTransferOutAmount = mTransferOutAmount;
	}

	public String getTransferOutFriendUserId() {
		return mTransferOutFriendUserId;
	}

	public void setTransferOutFriendUserId(String mTransferOutFriendUserId) {
		this.mTransferOutFriendUserId = mTransferOutFriendUserId;
	}
	
	public String getTransferOutLocalFriendId() {
		return mTransferOutLocalFriendId;
	}

	public void setTransferOutLocalFriendId(String mTransferOutLocalFriendId) {
		this.mTransferOutLocalFriendId = mTransferOutLocalFriendId;
	}
	
	public Friend getTransferOutFriend(){
		if(mTransferOutFriendUserId != null){
			return new Select().from(Friend.class).where("friendUserId=?",mTransferOutFriendUserId).executeSingle();
		}else if(mTransferOutLocalFriendId != null){
			return getModel(Friend.class,mTransferOutLocalFriendId);
		}
		return null;
	}
	
	public void setTransferOutFriend(Friend mTransferOutFriendUser){
		if(mTransferOutFriendUser == null){
			this.mTransferOutFriendUserId = null;
			this.mTransferOutLocalFriendId = null;
		}else if(mTransferOutFriendUser.getFriendUserId() != null){
			this.mTransferOutFriendUserId = mTransferOutFriendUser.getFriendUserId();
			this.mTransferOutLocalFriendId = null;
		}else{
			this.mTransferOutFriendUserId = null;
			this.mTransferOutLocalFriendId = mTransferOutFriendUser.getId();
		}
	}
	
	public String getTransferOutId() {
		return mTransferOutId;
	}

	public void setTransferOutId(String mTransferOutId) {
		this.mTransferOutId = mTransferOutId;
	}
	
	public MoneyAccount getTransferOut(){
		if(mTransferOutId == null){
			return null;
		}
		return (MoneyAccount) getModel(MoneyAccount.class, mTransferOutId);
	}
	
	public void setTransferOut(MoneyAccount mTransferOut) {
		if(mTransferOut == null){
			this.mTransferOutId = null;
			return;
		}
		this.mTransferOutId = mTransferOut.getId();
	}

	public Double getTransferInAmount() {
		return mTransferInAmount;
	}
	
	public Double getTransferInAmount0() {
		if(mTransferInAmount == null){
			return 0.00;
		}
		return mTransferInAmount;
	}
	
	public Double getTransferInLocalAmount() {
		Double rate = null;
		Currency userCurrency = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrency();
		if(this.getTransferIn() != null){
			if(userCurrency.getId().equals(this.getTransferIn().getCurrencyId())){
				rate = 1.0;
			}else{
				Double exchange = Exchange.getExchangeRate(this.getTransferIn().getCurrencyId(),userCurrency.getId());
			    if(exchange != null){
			    	rate = exchange;
			    }
			}
			return this.getTransferInAmount0()*rate;
		} else if(this.getTransferOut() != null){
			if(userCurrency.getId().equals(this.getTransferOut().getCurrencyId())){
				rate = 1.0;
			}else{
				Double exchange = Exchange.getExchangeRate(this.getTransferOut().getCurrencyId(),userCurrency.getId());
			    if(exchange != null){
			    	rate = exchange;
			    }
			}
			return this.getTransferOutAmount0()*rate;
		}
		return null;
	}

	public void setTransferInAmount(Double mTransferInAmount) {
		if(mTransferInAmount != null){
			mTransferInAmount = HyjUtil.toFixed2(mTransferInAmount);
		}
		this.mTransferInAmount = mTransferInAmount;
	}
	
	public String getTransferInFriendUserId() {
		return mTransferInFriendUserId;
	}

	public void setTransferInFriendUserId(String mTransferInFriendUserId) {
		this.mTransferInFriendUserId = mTransferInFriendUserId;
	}
	
	public String getTransferInLocalFriendId() {
		return mTransferInLocalFriendId;
	}

	public void setTransferInLocalFriendId(String mTransferInLocalFriendId) {
		this.mTransferInLocalFriendId = mTransferInLocalFriendId;
	}
	
	public Friend getTransferInFriend(){
		if(mTransferInFriendUserId != null){
			return new Select().from(Friend.class).where("friendUserId=?",mTransferInFriendUserId).executeSingle();
		}else if(mTransferInLocalFriendId != null){
			return getModel(Friend.class,mTransferInLocalFriendId);
		}
		return null;
	}
	
	public void setTransferInFriend(Friend mTransferInFriendUser){
		if(mTransferInFriendUser == null) {
			this.mTransferInFriendUserId = null;
			this.mTransferInLocalFriendId = null;
		}else if(mTransferInFriendUser.getFriendUserId() != null){
			this.mTransferInFriendUserId = mTransferInFriendUser.getFriendUserId();
			this.mTransferInLocalFriendId = null;
		}else{
			this.mTransferInFriendUserId= null;
			this.mTransferInLocalFriendId = mTransferInFriendUser.getId();
		}
	}

	public String getTransferInId() {
		return mTransferInId;
	}

	public void setTransferInId(String mTransferInId) {
		this.mTransferInId = mTransferInId;
	}
	
	public MoneyAccount getTransferIn(){
		if(mTransferInId == null){
			return null;
		}
		return (MoneyAccount) getModel(MoneyAccount.class, mTransferInId);
	}
	
	public void setTransferIn(MoneyAccount mTransferIn) {
		if(mTransferIn == null){
			this.mTransferInId = null;
			return;
		}
		this.mTransferInId = mTransferIn.getId();
	}

	public Double getExchangeRate() {
		return mExchangeRate;
	}

	public void setExchangeRate(Double exchangeRate) {
		if(exchangeRate != null){
			exchangeRate = HyjUtil.toFixed2(exchangeRate);
		}
		this.mExchangeRate = exchangeRate;
	}
	
	public Double getTransferInExchangeRate() {
		return mTransferInExchangeRate;
	}

	public void setTransferInExchangeRate(Double exchangeRate) {
		if(exchangeRate != null){
			exchangeRate = HyjUtil.toFixed2(exchangeRate);
		}
		this.mTransferInExchangeRate = exchangeRate;
	}
	
	public Double getTransferOutExchangeRate() {
		return mTransferOutExchangeRate;
	}

	public void setTransferOutExchangeRate(Double exchangeRate) {
		if(exchangeRate != null){
			exchangeRate = HyjUtil.toFixed2(exchangeRate);
		}
		this.mTransferOutExchangeRate = exchangeRate;
	}

	public String getProjectId() {
		return mProjectId;
	}

	public void setProjectId(String mProjectId) {
		this.mProjectId = mProjectId;
	}
	
	public Project getProject(){
        if(mProjectId == null){
            return null;
        }
		return getModel(Project.class, mProjectId);
	}
	
	public Event getEvent(){
		if(mEventId == null){
			return null;
		}
		return getModel(Event.class, mEventId);
	}
	
	public void setEvent(Event mEvent) {
		if(mEvent == null){
			this.mEventId = null;
		} else {
			this.mEventId = mEvent.getId();
		}
	}

	public String getEventId() {
		return mEventId;
	}

	public void setEventId(String mEventId) {
		this.mEventId = mEventId;
	}

	public String getRemark() {
		return mRemark;
	}
	
	public String getDisplayRemark() {
		if(mRemark != null && mRemark.length() > 0){
			return mRemark;
		} else {
			return HyjApplication.getInstance().getString(R.string.app_no_remark);
		}
	}

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

	public void setOwnerUserId(String mOwnerUserId) {
		this.mOwnerUserId = mOwnerUserId;
	}
	
	public String getProjectCurrencyId() {
		return mProjectCurrencyId;
	}

	public void setProjectCurrencyId(String mProjectCurrencyId) {
		this.mProjectCurrencyId = mProjectCurrencyId;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getGeoLon() {
		return mGeoLon;
	}

	public void setGeoLon(String mGeoLon) {
		this.mGeoLon = mGeoLon;
	}

	public String getGeoLat() {
		return mGeoLat;
	}

	public void setGeoLat(String mGeoLat) {
		this.mGeoLat = mGeoLat;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String mAddress) {
		this.mAddress = mAddress;
	}

	@Override
	public void validate(HyjModelEditor modelEditor) {
		if(this.getDate() == null){
			modelEditor.setValidationError("date",R.string.moneyTransferFormFragment_editText_hint_date);
		}else{
			modelEditor.removeValidationError("date");
		}
		
		if(this.getTransferOutAmount() == null){
			modelEditor.setValidationError("transferOutAmount",R.string.moneyTransferFormFragment_editText_hint_transferOutAmount);
		}else if(this.getTransferOutAmount() < 0){
			modelEditor.setValidationError("transferOutAmount",R.string.moneyTransferFormFragment_editText_validationError_negative_transferOutAmount);
		}else if(this.getTransferOutAmount() > 99999999){
			modelEditor.setValidationError("transferOutAmount",R.string.moneyTransferFormFragment_editText_validationError_beyondMAX_transferOutAmount);
		}
		else{
			modelEditor.removeValidationError("transferOutAmount");
		}
		
		if(this.getProjectId() == null){
			modelEditor.setValidationError("project",R.string.moneyTransferFormFragment_editText_hint_project);
		}else{
			modelEditor.removeValidationError("project");
		}
		
		if(this.getTransferOutId() == null && this.getTransferInId() == null){
			modelEditor.setValidationError("transferOutFriend",R.string.moneyTransferFormFragment_editText_validationError_both_friend);
			modelEditor.setValidationError("transferInFriend",R.string.moneyTransferFormFragment_editText_validationError_both_friend);
		}else{
			modelEditor.removeValidationError("transferOutFriend");
			modelEditor.removeValidationError("transferInFriend");
		}
		
		if(this.getExchangeRate() != null){
//			modelEditor.setValidationError("exchangeRate",R.string.moneyTransferFormFragment_editText_hint_exchangeRate);
//		}else 
			if(this.getExchangeRate() == 0){
				modelEditor.setValidationError("exchangeRate",R.string.moneyTransferFormFragment_editText_validationError_zero_exchangeRate);
			}else if(this.getExchangeRate() < 0){
				modelEditor.setValidationError("exchangeRate",R.string.moneyTransferFormFragment_editText_validationError_negative_exchangeRate);
			}else if(this.getExchangeRate() > 99999999){
				modelEditor.setValidationError("exchangeRate",R.string.moneyTransferFormFragment_editText_validationError_beyondMAX_exchangeRate);
			}
			else{
				modelEditor.removeValidationError("exchangeRate");
			}
		}
		if(this.getTransferOutId() == null && this.getTransferOutFriend() == null){
			modelEditor.setValidationError("transferOut",R.string.moneyTransferFormFragment_editText_hint_transferOut);
		}else if(this.getTransferInId() == null && this.getTransferInFriend() == null){
			modelEditor.setValidationError("transferIn",R.string.moneyTransferFormFragment_editText_hint_transferIn);
		}else if(this.getTransferOutId() != null && this.getTransferOutId().equals(this.getTransferInId())){
			 modelEditor.setValidationError("transferOut",R.string.moneyTransferFormFragment_editText_validationError_same_account);
			 modelEditor.setValidationError("transferIn",R.string.moneyTransferFormFragment_editText_validationError_same_account);
		}else{
			 modelEditor.removeValidationError("transferOut");
		   	 modelEditor.removeValidationError("transferIn");
		}
	}

	@Override
	public void save(){
		if(this.getOwnerUserId() == null){
			this.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
		}
		super.save();
	}

	public User getOwnerUser() {
		return getModel(User.class, mOwnerUserId);
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

	public void setTransferType(String mTransferType) {
		this.mTransferType = mTransferType;
	}
	
	
	public String getTransferType() {
		if(mTransferType == null){
			return "Transfer";
		}
		return mTransferType;
	}	
	
}
