package com.hoyoji.hoyoji.friend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.btcontrol.R;

public class UserFormFragment extends HyjUserFormFragment {

	private HyjTextField mTextFieldUserNickName = null;
	private TextView mTextFieldUserName = null;
	private HyjRemarkField mRemarkFieldRemark = null;
	private HyjImageView mPicture = null;

	JSONObject mJsonUser = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.user_formfragment_user;
	}

	@Override
	public Integer useOptionsMenuView() {
		return null;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();

		Intent intent = getActivity().getIntent();
		try {
			mJsonUser = new JSONObject(intent.getStringExtra("USER_JSON_OBJECT"));

			mPicture = (HyjImageView) getView().findViewById(
					R.id.userFormFragment_imageView_picture);
			mPicture.setDefaultImage(R.drawable.ic_action_person_white);
			mPicture.loadRemoteImage(mJsonUser.optString("pictureId"));

			mTextFieldUserName = (TextView) getView().findViewById(
					R.id.userFormFragment_textField_userName1);
			mTextFieldUserNickName = (HyjTextField) getView().findViewById(
					R.id.userFormFragment_textField_userNickName);
			
			mTextFieldUserNickName.setEnabled(false);
			
			mTextFieldUserName.setText(mJsonUser.optString("userName"));
			if(!mJsonUser.has("nickName") || mJsonUser.optString("nickName").equals("null")) {
				mTextFieldUserNickName.setText("(无昵称)");
			} else {
				mTextFieldUserNickName.setText(mJsonUser.optString("nickName"));
			}

			mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.userFormFragment_textField_remark);
			mRemarkFieldRemark.setText("用户"
					+ HyjApplication.getInstance().getCurrentUser()
					.getDisplayName() + "请求将您添加为好友");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onSave(View v) {
		super.onSave(v);
		
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				JSONArray jsonArray = (JSONArray) object;
				if (jsonArray.optJSONArray(0).length() > 0) {
					// 好友已经在服务器上存在，如果该好友不在本地（可能是未同步，或是同步出错？），我们将其加进来
					JSONObject jsonFriend = jsonArray.optJSONArray(0).optJSONObject(0);
//					Friend newFriend = new Select().from(Friend.class).where("friendUserId=?",
//							jsonFriend.optString("id")).executeSingle();
					Friend newFriend = HyjModel.getModel(Friend.class, jsonFriend.optString("id"));
					if(newFriend == null){
						loadFriendPicturesAndSaveFriend(mJsonUser, jsonFriend);
					} else {
						((HyjActivity) UserFormFragment.this.getActivity())
								.dismissProgressDialog();
						HyjUtil.displayToast(R.string.friendListFragment_addFriend_error_exists);
					}
				} else if (mJsonUser.optString("id").equals(
						HyjApplication.getInstance().getCurrentUser()
								.getId())) {
					// 添加自己为好友
					addSelfAsFriend(mJsonUser);
					((HyjActivity) UserFormFragment.this.getActivity())
							.dismissProgressDialog();
					
				} else if(mJsonUser.optString("newFriendAuthentication").equals("none")) {
					
					sendAddFriendResponseMessage(mJsonUser);
					
				} 
				else {
					sendAddFriendRequestMessage(mJsonUser);
				}
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

		try {
			JSONObject data = new JSONObject();
			data.put("__dataType", "Friend");
			data.put("friendUserId", mJsonUser.optString("id"));
			data.put("ownerUserId", HyjApplication.getInstance()
					.getCurrentUser().getId());
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,
					"[" + data.toString() + "]", "getData");
			((HyjActivity) this.getActivity()).displayProgressDialog(
					R.string.addFriendListFragment_title_add,
					R.string.friendListFragment_addFriend_progress_adding);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void addSelfAsFriend(final JSONObject jsonUser) {
		((HyjActivity) UserFormFragment.this.getActivity()).displayDialog(
				-1, R.string.friendListFragment_addFriend_addSelf_title,
				R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
				new DialogCallbackListener() {
					@Override
					public void doPositiveClick(Object object) {
						sendAddFriendResponseMessage(jsonUser);
//						final Friend newFriend = new Friend();
//						newFriend.setFriendUser(HyjApplication.getInstance()
//								.getCurrentUser());
//						newFriend.setOwnerUserId(HyjApplication.getInstance()
//								.getCurrentUser().getId());
//						newFriend.setFriendCategoryId(HyjApplication
//								.getInstance().getCurrentUser().getUserData()
//								.getDefaultFriendCategoryId());
//						HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
//							@Override
//							public void finishCallback(Object object) {
//								newFriend.save();
//								((HyjActivity) UserFormFragment.this
//										.getActivity()).dismissProgressDialog();
//								HyjUtil.displayToast(R.string.friendListFragment_addFriend_progress_add_success);
//							}
//
//							@Override
//							public void errorCallback(Object object) {
//								displayError(object);
//							}
//						};
//
//						HyjHttpPostAsyncTask.newInstance(serverCallbacks, "["
//								+ newFriend.toJSON().toString() + "]",
//								"postData");
//						((HyjActivity) UserFormFragment.this.getActivity())
//								.displayProgressDialog(
//										R.string.addFriendListFragment_title_add,
//										R.string.friendListFragment_addFriend_progress_adding);
					}

					@Override
					public void doNegativeClick() {
					}
				});
	}

	private void sendAddFriendResponseMessage(final JSONObject jsonUser) {
		final Message msg = new Message();
		msg.setDate((new Date()).getTime());
		msg.setMessageState("new");
		msg.setType("System.Friend.AddResponse");
		msg.setOwnerUserId(jsonUser.optString("id"));
		msg.setFromUserId(HyjApplication.getInstance().getCurrentUser().getId());
		msg.setToUserId(jsonUser.optString("id"));
		msg.setMessageTitle("好友请求");
		if(jsonUser.optString("newFriendAuthentication").equals("none")){
			msg.setMessageDetail("用户"
					+ HyjApplication.getInstance().getCurrentUser()
							.getDisplayName() + "已成功添加您为好友");
		}else{
			msg.setMessageDetail("用户"
					+ HyjApplication.getInstance().getCurrentUser()
							.getDisplayName() + "同意您的添加好友请求");
		}
		
//		msg.setMessageBoxId(jsonUser.optString("messageBoxId"));
		JSONObject msgData = new JSONObject();
		try {
			msgData.put("fromUserDisplayName", HyjApplication.getInstance()
					.getCurrentUser().getDisplayName());
			msgData.put(
					"toUserDisplayName",
					HyjUtil.ifJSONNull(jsonUser, "nickName",
							jsonUser.getString("userName")));
		} catch (JSONException e) {
		}
		msg.setMessageData(msgData.toString());

		// send message to server to request add new friend
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
//				msg.save();
//				((HyjActivity) UserFormFragment.this.getActivity())
//						.dismissProgressDialog();
				loadNewlyAddedFriend(jsonUser);
//				HyjUtil.displayToast(R.string.friendAddRequestMessageFormFragment_toast_accept_success);
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

		HyjHttpPostAsyncTask.newInstance(serverCallbacks, "["
				+ msg.toJSON().toString() + "]", "postData");
//		((HyjActivity) this.getActivity()).displayProgressDialog(
//				R.string.addFriendListFragment_title_add,
//				R.string.friendListFragment_addFriend_progress_adding);

	}
	
	
//	private void addFriendWithoutAuthorization(final JSONObject jsonUser) {
//		// send message to server to request add new friend
//		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
//			@Override
//			public void finishCallback(Object object) {
//				loadNewlyAddedFriend(jsonUser);
//			}
//
//			@Override
//			public void errorCallback(Object object) {
//				displayError(object);
//			}
//		};
//
//		JSONObject data = new JSONObject();
//		try {
//			data.put("__dataType", "Message");
//			data.put("id", UUID.randomUUID().toString());
//			data.put("toUserId", jsonUser.optString("id"));
//			data.put("fromUserId", HyjApplication.getInstance()
//					.getCurrentUser().getId());
//			data.put("type", "System.Friend.AddResponse");
//			data.put("messageState", "new");
//			data.put("messageTitle", "好友请求");
//			data.put("date", HyjUtil.formatDateToIOS(new Date()));
//			data.put("detail", "用户"
//					+ HyjApplication.getInstance().getCurrentUser()
//							.getDisplayName() + "成功添加您为好友");
//			data.put("messageBoxId", jsonUser.optString("messageBoxId"));
//			data.put("ownerUserId", jsonUser.optString("id"));
//			JSONObject msgData = new JSONObject();
//			try {
//				msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
//				msgData.put("toUserDisplayName", HyjUtil.ifJSONNull(jsonUser, "nickName", jsonUser.getString("userName")));
//			} catch (JSONException e) {
//			}
//			data.put("messageData", msgData);
//			HyjHttpPostAsyncTask.newInstance(serverCallbacks,
//					"[" + data.toString() + "]", "postData");
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

	private void loadFriendPicturesAndSaveFriend(final JSONObject jsonUser, final JSONObject jsonFriend){
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				Friend newFriend = HyjModel.getModel(Friend.class,
						jsonFriend.optString("id"));
				if (newFriend == null) {
					newFriend = new Friend();
				}
				newFriend.loadFromJSON(jsonFriend, true);

				User newUser = HyjModel.getModel(User.class,
						jsonUser.optString("id"));
				if (newUser == null) {
					newUser = new User();
				}
				newUser.loadFromJSON(jsonUser, true);

				saveUserPictures(object);
				newUser.save();
				newFriend.save();
				
				((HyjActivity) UserFormFragment.this.getActivity())
				.dismissProgressDialog();
				HyjUtil.displayToast(R.string.friendListFragment_addFriend_progress_add_success);
				UserFormFragment.this.getActivity().finish();
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("recordType", "User");
			jsonObj.put("recordId", jsonUser.optString("id"));
		} catch (JSONException e) {
		}
		HyjHttpPostAsyncTask.newInstance(serverCallbacks, jsonObj.toString(), "fetchRecordPictures");
	}
	
	private void loadNewlyAddedFriend(final JSONObject jsonUser) {
		// load new friend from server
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				try {
					JSONObject jsonFriend;
					jsonFriend = ((JSONArray) object).getJSONArray(0)
							.getJSONObject(0);
					loadFriendPicturesAndSaveFriend(jsonUser, jsonFriend);
				} catch (JSONException e) {
					e.printStackTrace();
					((HyjActivity) UserFormFragment.this.getActivity())
					.dismissProgressDialog();
				}
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

		JSONObject data = new JSONObject();
		try {
			data.put("__dataType", "Friend");
			data.put("ownerUserId", HyjApplication.getInstance()
					.getCurrentUser().getId());
			data.put("friendUserId", jsonUser.optString("id"));
			HyjHttpPostAsyncTask.newInstance(serverCallbacks,
					"[" + data.toString() + "]", "getData");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sendAddFriendRequestMessage(JSONObject jsonUser) {
		final Message msg = new Message();
		msg.setDate((new Date()).getTime());
		msg.setMessageState("new");
		msg.setType("System.Friend.AddRequest");
		msg.setOwnerUserId(jsonUser.optString("id"));
		msg.setFromUserId(HyjApplication.getInstance().getCurrentUser().getId());
		msg.setToUserId(jsonUser.optString("id"));
		msg.setMessageTitle("好友请求");
		msg.setMessageDetail(mRemarkFieldRemark.getText().trim());
//		msg.setMessageBoxId(jsonUser.optString("messageBoxId"));
		JSONObject msgData = new JSONObject();
		try {
			msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
			msgData.put("toUserDisplayName", HyjUtil.ifJSONNull(jsonUser, "nickName", jsonUser.getString("userName")));
		} catch (JSONException e) {
		}
		msg.setMessageData(msgData.toString());
		
		// send message to server to request add new friend
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
//				msg.save();
				((HyjActivity) UserFormFragment.this.getActivity())
				.dismissProgressDialog();
				HyjUtil.displayToast(R.string.friendListFragment_addFriend_toast_send_success);
		   }

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};


		HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + msg.toJSON().toString()
				+ "]", "postData");
//		((HyjActivity) this.getActivity()).displayProgressDialog(
//				R.string.addFriendListFragment_title_add,
//				R.string.friendListFragment_addFriend_progress_adding);

	}
	
	private void saveUserPictures(Object object){
		JSONArray pictureArray = (JSONArray)object;
		for(int i=0; i < pictureArray.length(); i++){
			try {
				JSONObject jsonPic = pictureArray.getJSONObject(i);
				String base64PictureIcon = jsonPic.optString("base64PictureIcon");
				if(base64PictureIcon.length() > 0){
					 byte[] decodedByte = Base64.decode(base64PictureIcon, 0);
				    Bitmap icon = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
				    File imageFile = HyjUtil.createImageFile(jsonPic.optString("id")+"_icon");
				    if(imageFile != null){
				    FileOutputStream out = new FileOutputStream(imageFile);
				    icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
				    out.close();
				    out = null;
				    }
				    jsonPic.remove("base64PictureIcon");
				}
				Picture newPicture = new Picture();
				newPicture.loadFromJSON(jsonPic, true);
				
				newPicture.save();
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void displayError(Object object){
		((HyjActivity) UserFormFragment.this.getActivity())
		.dismissProgressDialog();
		JSONObject json = (JSONObject) object;
		HyjUtil.displayToast(json.optJSONObject("__summary").optString(
				"msg"));
	}
}
