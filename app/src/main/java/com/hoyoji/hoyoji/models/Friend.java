package com.hoyoji.hoyoji.models;

import java.util.UUID;

import org.json.JSONObject;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.btcontrol.R;

import android.provider.BaseColumns;

@Table(name = "Friend", id = BaseColumns._ID)
public class Friend extends HyjModel {
	
	@Column(name = "id", index = true, unique = true)
	private String mUUID;
	
	@Column(name = "nickName")
	private String mNickName;
	
	@Column(name = "nickName_pinYin")
	private String mNickName_pinYin;
	
	@Column(name = "friendUserId")
	private String mFriendUserId;

	@Column(name = "friendUserName")
	private String mFriendUserName;

	@Column(name = "phoneNumber")
	private String mPhoneNumber;
	
	@Column(name = "friendCategoryId")
	private String mFriendCategoryId;
	
	@Column(name = "toBeDetermined")
	private Boolean mToBeDetermined = false;

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
	
	
	public Friend(){
		super();
		mUUID = UUID.randomUUID().toString();
		mFriendCategoryId = HyjApplication.getInstance().getCurrentUser().getUserData().getDefaultFriendCategoryId();
	}

	public User getFriendUser(){
		if(mFriendUserId == null){
			return null;
		}
		return (User) getModel(User.class, mFriendUserId);
	}
	
	public void setFriendUser(User user){
		mFriendUserId = user.getId();
	}

	@Override
	public String getId() {
		return mUUID;
	}

	public void setId(String mUUID) {
		this.mUUID = mUUID;
	}

	public String getDisplayName(){
		if(this.getNickName() != null && this.getNickName().length() > 0){
			return this.getNickName();
		} else {
			User friendUser = this.getFriendUser();
			if(friendUser != null){
				return friendUser.getDisplayName();
			} else {
				return this.getFriendUserName();
			}
		}
	}
	
	public String getDisplayName_pinYin(){
		if(this.mNickName_pinYin != null && this.mNickName_pinYin.length() > 0){
			return this.mNickName_pinYin;
		} else {
			User friendUser = this.getFriendUser();
			if(friendUser != null){
				return friendUser.getDisplayName_pinYin();
			} else {
				return this.getFriendUserName_pinYin();
			}
		}
	}
	
	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String nickName) {
		
		if(nickName == null || nickName.length() == 0){
//			this.mNickName = "";
//			String displayName = this.getDisplayName();
//			if(displayName != null){
//				this.mNickName_pinYin = HyjUtil.convertToPinYin(displayName);
//			} else {
				this.mNickName_pinYin = "";
//			}

			if(this.getFriendUserId() != null && HyjApplication.getInstance().getCurrentUser().getId().equals(this.getFriendUserId())){
				this.mNickName_pinYin = " " + this.mNickName_pinYin;
			}
		} else if(this.mNickName == null || !this.mNickName.equals(nickName) || this.mNickName_pinYin == null){
			this.mNickName_pinYin = HyjUtil.convertToPinYin(nickName);

			if(this.getFriendUserId() != null && HyjApplication.getInstance().getCurrentUser().getId().equals(this.getFriendUserId())){
				this.mNickName_pinYin = " " + this.mNickName_pinYin;
			}
		}
		this.mNickName = nickName;
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

//	public void setFriendUserName1(String mFriendUserName) {
//		this.mFriendUserName = mFriendUserName;
//	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	public void setPhoneNumber(String mPhoneNumber) {
		this.mPhoneNumber = mPhoneNumber;
	}

	public FriendCategory getFriendCategory() {
		if(mFriendCategoryId == null){
			return null;
		}
		return (FriendCategory) getModel(FriendCategory.class, mFriendCategoryId);
	}

	public void setFriendCategory(FriendCategory mFriendCategory) {
		this.mFriendCategoryId = mFriendCategory.getId();
	}
	
	public String getFriendCategoryId() {
		return mFriendCategoryId;
	}

	public void setFriendCategoryId(String mFriendCategoryId) {
		this.mFriendCategoryId = mFriendCategoryId;
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
		if(this.getFriendCategoryId() == null){
			modelEditor.setValidationError("friendCategory", R.string.friendFormFragment_editText_hint_friend_category);
		} else {
			modelEditor.removeValidationError("friendCategory");
		}
		if(this.getFriendUserId() == null){
			if(this.getNickName().length() == 0){
				modelEditor.setValidationError("nickName", R.string.friendFormFragment_editText_hint_friendName);
			} else {
				modelEditor.removeValidationError("nickName");
			}		
		}
		
		if(this.getPhoneNumber() != null){
			Friend importFiend = new Select().from(Friend.class).where("phoneNumber=? and id<>?",this.getPhoneNumber(),this.getId()).executeSingle();
	        if(importFiend == null || "".equals(this.getPhoneNumber())){
	        	modelEditor.removeValidationError("phoneNumber");
	        } else {
	        	modelEditor.setValidationError("phoneNumber", R.string.friendFormFragment_editText_error_friendPhoneNumber);
	        }		
		} else {
			modelEditor.removeValidationError("phoneNumber");
		}
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

	public static String getFriendUserDisplayName(String localFriendId, String friendUserId, String projectId) {
		if(localFriendId != null){
			Friend localFriend = HyjModel.getModel(Friend.class, localFriendId);
			if(localFriend != null){
				return localFriend.getDisplayName();
			} else {
				if(projectId != null){
					ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=? AND state <> 'Delete'", projectId, localFriendId).executeSingle();
					if(psa != null){
						return psa.getFriendUserName();
					}
				}
			}
		} else if(friendUserId != null) {
			Friend friend = new Select().from(Friend.class).where("friendUserId=?",friendUserId).executeSingle();
			if(friend != null){
				return friend.getDisplayName();
			} else {
				User user = HyjModel.getModel(User.class, friendUserId);
				if(user != null){
					return user.getDisplayName();
				} else {
					if(projectId != null){
						ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=? AND state <> 'Delete'", projectId, friendUserId).executeSingle();
						if(psa != null){
							return psa.getFriendUserName();
						}
					}
				}
			}
		}
		return "";
	}	
	
	public static String getFriendUserDisplayName(String friendUserId) {
//		if(ownerUserId.equalsIgnoreCase(HyjApplication.getInstance().getCurrentUser().getId())){
//			return "自己";
//		}else{
			Friend friend = new Select().from(Friend.class).where("friendUserId=?",friendUserId).executeSingle();
			if(friend != null){
				return friend.getDisplayName();
			} else {
				User user = HyjModel.getModel(User.class, friendUserId);
				if(user != null){
					return user.getDisplayName();
				} else {
//					Friend localFriend = HyjModel.getModel(Friend.class, friendUserId);
//					if(localFriend != null){
//						return localFriend.getDisplayName();
//					} else {
//						if(projectId != null){
//							ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=? AND state <> 'Delete'", projectId, friendUserId).executeSingle();
//							if(psa != null){
//								return psa.getFriendUserName();
//							}
//						}
					HyjUtil.asyncLoad("User", friendUserId);
					return friendUserId.substring(0, 10);
//					}
				}
			}
//		}
	}

	public Boolean getToBeDetermined() {
		return this.mToBeDetermined;
	}	
	
	@Override 
	public void loadFromJSON(JSONObject json, boolean syncFromServer) {
		super.loadFromJSON(json, syncFromServer);
		if(this.mNickName_pinYin == null){
//			if(this.getFriendUserId() != null && HyjApplication.getInstance().getCurrentUser().getId().equals(this.getFriendUserId())){
//				this.mNickName_pinYin = " ";
//			} else 
				if(this.getNickName() != null){
					this.mNickName_pinYin = HyjUtil.convertToPinYin(this.getNickName());
				}
//			}
		}
	
	}
	
//	@Override 
//	public void loadFromJSON(JSONObject json, boolean syncFromServer) {
//		super.loadFromJSON(json, syncFromServer);
//		
//		if(json.isNull("nickName_pinYin")){
//			String displayName = this.getDisplayName();
//			if(displayName != null){
//				this.mNickName_pinYin = HyjUtil.convertToPinYin(displayName);
//			} else {
//				this.mNickName_pinYin = "";
//			}
//
//			if(this.getFriendUserId() != null && HyjApplication.getInstance().getCurrentUser().getId().equals(this.getFriendUserId())){
//				this.mNickName_pinYin = " " + this.mNickName_pinYin;
//			} else {
//				this.mNickName_pinYin = "1" + this.mNickName_pinYin;
//			}
//		}
//	}

	
}
