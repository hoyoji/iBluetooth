package com.hoyoji.hoyoji.friend;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.FriendCategory;
import com.hoyoji.hoyoji.models.Message;
import com.hoyoji.hoyoji.models.Picture;


public class FriendFormFragment extends HyjUserFormFragment {
	private final static int GET_FRIEND_CATEGORY_ID = 1;
	
	private HyjModelEditor<Friend> mFriendEditor = null;
	private HyjSelectorField mSelectorFieldFriendCategory = null;
	private HyjTextField mTextFieldUserNickName = null;
	private HyjTextField mTextFieldNickName = null;
	private HyjTextField mTextFieldPhoneNumber = null;
	private TextView mTextFieldUserName = null;

	private HyjImageView mPicture = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.friend_formfragment_friend;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		Friend friend;
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			friend =  new Select().from(Friend.class).where("_id=?", modelId).executeSingle();
		} else {
			friend = new Friend();
		}
		mFriendEditor = friend.newModelEditor();
		
//		暂不支持删除
//		setupDeleteButton(mFriendEditor);
		
		
		mTextFieldUserName = (TextView) getView().findViewById(R.id.friendFormFragment_textField_userName1);
		mTextFieldUserNickName = (HyjTextField) getView().findViewById(R.id.friendFormFragment_textField_userNickName);

		mPicture = (HyjImageView) getView().findViewById(R.id.friendFormFragment_imageView_picture);	
		mPicture.setDefaultImage(R.drawable.ic_action_person_white);
		if(friend.getFriendUserId() != null){
			mTextFieldUserNickName.setEnabled(false);
			if(friend.getFriendUser() != null){
				mPicture.setImage(friend.getFriendUser().getPicture());
				
				mTextFieldUserName.setText(friend.getFriendUser().getUserName());
				mTextFieldUserNickName.setText(friend.getFriendUser().getNickName());
			} else {
				mPicture.setImage((Picture)null);
				if(friend.getFriendUserName() != null && friend.getFriendUserName().length() > 0){
					mTextFieldUserName.setText(friend.getFriendUserName());
				} else {
					mTextFieldUserName.setText("[无用户名]");
				}
				mTextFieldUserNickName.setText("[无好友昵称]");
			}
			if(friend.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				mPicture.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
			} else {
				mPicture.setBackgroundColor(getResources().getColor(R.color.hoyoji_green));
			}
		} else {
			mPicture.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
			mPicture.setImage((Picture)null);
			mTextFieldUserName.setText("[本地好友]");
			mTextFieldUserNickName.setVisibility(View.GONE);
			getView().findViewById(R.id.field_separator_userNickName).setVisibility(View.GONE);
		}
		
		mTextFieldNickName = (HyjTextField) getView().findViewById(R.id.friendFormFragment_textField_nickName);
		mTextFieldNickName.setText(friend.getNickName());
		mTextFieldNickName.requestFocus();
		
		mTextFieldPhoneNumber = (HyjTextField) getView().findViewById(R.id.friendFormFragment_textField_phoneNumber);
		mTextFieldPhoneNumber.setText(friend.getPhoneNumber());
		
		FriendCategory friendCategory = friend.getFriendCategory();
		mSelectorFieldFriendCategory = (HyjSelectorField) getView().findViewById(R.id.friendFormFragment_selectorField_friend_category);
		
		if(friendCategory != null){
			mSelectorFieldFriendCategory.setModelId(friendCategory.getId());
			mSelectorFieldFriendCategory.setText(friendCategory.getName());
		}
		mSelectorFieldFriendCategory.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				FriendFormFragment.this
				.openActivityWithFragmentForResult(FriendCategoryListFragment.class, R.string.friendCategoryListFragment_title_select_friend_category, null, GET_FRIEND_CATEGORY_ID);
			}
		});

		if(modelId == -1){
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		} else if(friend.getToBeDetermined()){
			getView().findViewById(R.id.button_save).setVisibility(View.GONE);
			if(this.mOptionsMenu != null){
				hideSaveAction();
			}
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(mFriendEditor != null && mFriendEditor.getModel().get_mId() != null && mFriendEditor.getModel().getToBeDetermined()){
			hideSaveAction();
		}
	}
	
	private void setupDeleteButton(HyjModelEditor<Friend> friendEditor) {

		Button buttonDelete = (Button) getView().findViewById(
				R.id.button_delete);
		
		final Friend friend = friendEditor.getModelCopy();
		
		if (friend.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.friendFormFragment_confirm_delete, R.string.alert_dialog_ok, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									if(friend.getFriendUser() == null) {
										friend.delete();
										HyjUtil.displayToast(R.string.app_delete_success);
										getActivity().finish();
									} else { 
										sendDeleteFriendMessage();
									}
								}
							});
				}
			});
		}
		
	}
	
	private void sendDeleteFriendMessage() {
		final Message msg = new Message();
		msg.setDate((new Date()).getTime());
		msg.setMessageState("new");
		msg.setType("System.Friend.Delete");
		msg.setOwnerUserId(mFriendEditor.getModelCopy().getFriendUserId());
		msg.setFromUserId(HyjApplication.getInstance().getCurrentUser().getId());
		msg.setToUserId(mFriendEditor.getModelCopy().getFriendUserId());
		msg.setMessageTitle("删除好友");
		msg.setMessageDetail("用户"
				+ HyjApplication.getInstance().getCurrentUser()
						.getDisplayName() + "把您从好友列表删除");
//		msg.setMessageBoxId(mFriendEditor.getModelCopy().getFriendUser().getMessageBoxId());
		JSONObject msgData = new JSONObject();
		try {
			msgData.put("fromUserDisplayName", HyjApplication.getInstance()
					.getCurrentUser().getDisplayName());
			msgData.put("toUserDisplayName",mFriendEditor.getModelCopy().getFriendUser().getDisplayName());
		} catch (JSONException e) {
		}
		msg.setMessageData(msgData.toString());

		// send message to server to request add new friend
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				((HyjActivity) FriendFormFragment.this.getActivity())
						.dismissProgressDialog();
				try {
					ActiveAndroid.beginTransaction();
					msg.save();
					mFriendEditor.getModelCopy().delete();
					HyjUtil.displayToast(R.string.app_delete_success);
					ActiveAndroid.setTransactionSuccessful();
					ActiveAndroid.endTransaction();
					getActivity().finish();
				} catch (Exception e) {
					ActiveAndroid.endTransaction();
					HyjUtil.displayToast(R.string.app_delete_failed);
				} 
			}

			@Override
			public void errorCallback(Object object) {
				((HyjActivity) FriendFormFragment.this.getActivity())
				.dismissProgressDialog();
				JSONObject json = (JSONObject) object;
				HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
			}
		};

		HyjHttpPostAsyncTask.newInstance(serverCallbacks, "["
				+ msg.toJSON().toString() + "]", "postData");
		((HyjActivity) this.getActivity()).displayProgressDialog(
				R.string.friendFormFragment_title_delete,
				R.string.friendFormFragment_toast_progress_deleting);

	}
	
	
	private void fillData(){
		Friend modelCopy = (Friend) mFriendEditor.getModelCopy();
		modelCopy.setNickName(mTextFieldNickName.getText().toString().trim());
		modelCopy.setPhoneNumber(mTextFieldPhoneNumber.getText().toString().trim());
		modelCopy.setFriendCategoryId(mSelectorFieldFriendCategory.getModelId());
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		mSelectorFieldFriendCategory.setError(mFriendEditor.getValidationError("friendCategory"));
		mTextFieldNickName.setError(mFriendEditor.getValidationError("nickName"));
		mTextFieldPhoneNumber.setError(mFriendEditor.getValidationError("phoneNumber"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		mFriendEditor.validate();
		
		if(mFriendEditor.hasValidationErrors()){
			showValidatioErrors();
		} else {
			mFriendEditor.save();
			HyjUtil.displayToast(R.string.app_save_success);
			getActivity().finish();
		}
	}	
	
	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch(requestCode){
             case GET_FRIEND_CATEGORY_ID:
            	 if(resultCode == Activity.RESULT_OK){
            		long _id = data.getLongExtra("MODEL_ID", -1);
            		FriendCategory friendCategory = FriendCategory.load(FriendCategory.class, _id);
            		mSelectorFieldFriendCategory.setText(friendCategory.getName());
            		mSelectorFieldFriendCategory.setModelId(friendCategory.getId());
            	 }
             case 2:

          }
    }
}
