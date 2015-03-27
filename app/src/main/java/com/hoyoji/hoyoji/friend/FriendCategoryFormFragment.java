package com.hoyoji.hoyoji.friend;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.FriendCategory;
 

public class FriendCategoryFormFragment extends HyjUserFormFragment {
	private HyjModelEditor mFriendCategoryEditor = null;
	private HyjTextField mTextFieldName = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.friend_formfragment_friend_category;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		FriendCategory friendCategory;
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			friendCategory =  new Select().from(FriendCategory.class).where("_id=?", modelId).executeSingle();
		} else {
			friendCategory = new FriendCategory();
		}
		mFriendCategoryEditor = friendCategory.newModelEditor();
		
		mTextFieldName = (HyjTextField) getView().findViewById(R.id.friendCategoryFormFragment_textField_name);
		mTextFieldName.setText(friendCategory.getName());

		if(modelId == -1){
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}
	
	private void fillData(){
		FriendCategory modelCopy = (FriendCategory) mFriendCategoryEditor.getModelCopy();
		modelCopy.setName(mTextFieldName.getText().toString().trim());
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		
		mTextFieldName.setError(mFriendCategoryEditor.getValidationError("name"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		mFriendCategoryEditor.validate();
		
		if(mFriendCategoryEditor.hasValidationErrors()){
			showValidatioErrors();
		} else {
			mFriendCategoryEditor.save();
			HyjUtil.displayToast(R.string.app_save_success);
			getActivity().finish();
		}
	}	
}
