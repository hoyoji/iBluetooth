package com.hoyoji.hoyoji.project;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.MainActivity;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyApportion;
import com.hoyoji.hoyoji.models.MoneyExpenseApportion;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.money.MoneyApportionField;
import com.hoyoji.hoyoji.money.SelectApportionMemberListFragment;
import com.hoyoji.hoyoji.money.MoneyApportionField.ApportionItem;


public class ProjectMemberSplitTBDFormFragment extends HyjUserFormFragment {

	protected static final int GET_APPORTION_MEMBER_ID = 0;
	private static final int ADD_AS_PROJECT_MEMBER = 1;

	ProjectShareAuthorization projectShareAuthorization;
	private HyjTextField mTextFieldProjectName = null;

	private MoneyApportionField mApportionFieldApportions;
	
	@Override
	public Integer useContentView() {
		return R.layout.project_formfragment_member_tbd;
	}
	
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		final Project project;
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		projectShareAuthorization =  ProjectShareAuthorization.load(ProjectShareAuthorization.class, modelId);
		project = projectShareAuthorization.getProject();
		
		boolean _canNotEdit = false;
		if(!projectShareAuthorization.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
			_canNotEdit = true;
		}
		final boolean canNotEdit = _canNotEdit;
		
//		mProjectShareAuthorizations = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND state <> ? AND id <> ?", project.getId(), "Delete", projectShareAuthorization.getId()).execute();
//		mProjectShareAuthorizations.add(projectShareAuthorization);
		
		mTextFieldProjectName = (HyjTextField) getView().findViewById(R.id.memberTBDFormFragment_textField_projectName);
		mTextFieldProjectName.setText(project.getDisplayName());
		mTextFieldProjectName.setEnabled(false);
		
		mApportionFieldApportions = (MoneyApportionField) getView().findViewById(R.id.memberTBDFormFragment_apportionField);
		mApportionFieldApportions.setHideMoney(true);
		getView().findViewById(R.id.memberTBDFormFragment_imageButton_apportion_add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				Project project = projectShareAuthorization.getProject();
				bundle.putLong("MODEL_ID", project.get_mId());
				if(!project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
				} else {
					openActivityWithFragmentForResult(SelectApportionMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
				}
			}
		});

		getView().findViewById(R.id.memberTBDFormFragment_imageButton_apportion_add_all).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addAllProjectMemberIntoApportionsField(projectShareAuthorization.getProject());
			}
		});
		
		getView().findViewById(R.id.memberTBDFormFragment_imageButton_apportion_more_actions).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupMenu popup = new PopupMenu(getActivity(), v);
						MenuInflater inflater = popup.getMenuInflater();
						inflater.inflate(R.menu.money_apportionfield_more_actions, popup.getMenu());
						popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								if (item.getItemId() == R.id.moneyApportionField_menu_moreActions_clear) {
									mApportionFieldApportions.clearAll();
//									mApportionFieldApportions.setTotalAmount(0.0);
									return true;
								} else if (item.getItemId() == R.id.moneyApportionField_menu_moreActions_all_average) {
									mApportionFieldApportions.setAllApportionAverage();
//									mApportionFieldApportions.setTotalAmount(0.0);
									return true;
								} else if (item.getItemId() == R.id.moneyApportionField_menu_moreActions_all_share) {
									mApportionFieldApportions.setAllApportionShare();
//									mApportionFieldApportions.setTotalAmount(0.0);
									return true;
								}
								return false;
							}
		
		
						});
						
						if(canNotEdit){
							for(int i = 0; i<popup.getMenu().size();i++){
								popup.getMenu().setGroupEnabled(i, false);
							}
						}
						
						popup.show();
					}
				});

		
		getView().findViewById(R.id.memberTBDFormFragment_button_transactions).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("PROJECT_ID", project.get_mId());
				bundle.putString("LOCAL_FRIENDID", projectShareAuthorization.getLocalFriendId());
				openActivityWithFragment(ProjectMoneyTBDListFragment.class, R.string.memberTBDFormFragment_title_transactions, bundle);
			}
		});
		
		if(canNotEdit){
			getView().findViewById(R.id.button_save).setVisibility(View.GONE);
			if(this.mOptionsMenu != null){
				hideSaveAction();
			}
		}
	}
	
	private void addAllProjectMemberIntoApportionsField(Project project) {
		List<ProjectShareAuthorization> projectShareAuthorizations = project.getShareAuthorizations();
		for (int i = 0; i < projectShareAuthorizations.size(); i++) {
			if(projectShareAuthorizations.get(i).getState().equalsIgnoreCase("Delete") ||
							projectShareAuthorizations.get(i).getToBeDetermined()){
				continue;
			}
			MoneyExpenseApportion apportion = new MoneyExpenseApportion();
			apportion.setAmount(0.0);
			apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
			apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
			if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
				apportion.setApportionType("Average");
			} else {
				apportion.setApportionType("Share");
			}
			mApportionFieldApportions.addApportion(apportion, project.getId(), null, ApportionItem.NEW);
//			mApportionFieldApportions.setTotalAmount(0.0);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(projectShareAuthorization != null && projectShareAuthorization.get_mId() != null){
				boolean canNotEdit = false;
				if(!projectShareAuthorization.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					canNotEdit = true;
				} 
				if(canNotEdit){
					hideSaveAction();
				}
		}
	}
	
	
	private void fillData() {

	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		if(!projectShareAuthorization.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
			HyjUtil.displayToast("只有圈子拥有人才能进行拆分");
			return;
		}
		if(mApportionFieldApportions.getAdapter().getCount() == 0){
			HyjUtil.displayToast("请选择拆分成员");
			return;
		}

	
//		ActiveAndroid.beginTransaction();
//		try {
//			
//			doSplitExpenseContainers();
//			doSplitIncomeContainers();
//			doSplitDepositIncomeContainers();
//			doSplitDepositReturnContainers();
//
//			
//			ActiveAndroid.setTransactionSuccessful();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		ActiveAndroid.endTransaction();
		int count = 0;
		if (HyjApplication.getInstance().getCurrentUser() != null) {
			Cursor cursor = Cache.openDatabase().rawQuery("SELECT COUNT(*) FROM ClientSyncRecord", null);
			if (cursor != null) {
				cursor.moveToFirst();
				count = cursor.getInt(0);
				cursor.close();
				cursor = null;
			}
		}
		if(count > 0){
			((HyjActivity) getActivity())
			.displayProgressDialog(
					R.string.memberTBDFormFragment_title_split,
					R.string.memberTBDFormFragment_progress_uploading_data);
			MainActivity.uploadData(false, getActivity(), null, new HyjAsyncTaskCallbacks(){
				@Override
				public void finishCallback(Object object) {
					((HyjActivity) getActivity())
					.displayProgressDialog(
							R.string.memberTBDFormFragment_title_split,
							R.string.memberTBDFormFragment_progress_splitting);
					doSplitOnServer();
				}
				@Override
				public void errorCallback(Object object) {
					displayError(object);
				}
			});
		} else {
			((HyjActivity) getActivity())
			.displayProgressDialog(
					R.string.memberTBDFormFragment_title_split,
					R.string.memberTBDFormFragment_progress_splitting);
			doSplitOnServer();
		}
		
	}	
	 
	 private void doSplitOnServer() {
		 JSONArray jsonArray = new JSONArray();
			for(int i = 0; i < mApportionFieldApportions.getAdapter().getCount(); i++){
				ApportionItem<MoneyApportion> api = mApportionFieldApportions.getAdapter().getItem(i);
				MoneyApportion apiApportion = api.getApportion();
				
				try {

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("apportionType", api.getApportionType());
					jsonObject.put("sharePercentage", api.getSharePercentage());
					jsonObject.put("friendUserId", apiApportion.getFriendUserId());
					jsonObject.put("localFriendId", apiApportion.getLocalFriendId());
					jsonArray.put(jsonObject);
					
				} catch (JSONException e) {
					HyjUtil.displayToast(e.getMessage());
				}
			}

			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
					HyjUtil.displayToast(R.string.memberTBDFormFragment_toast_split_success);
					MainActivity.uploadData(true, getActivity(), null, null);
				}

				@Override
				public void errorCallback(Object object) {
					displayError(object);
				}
			};

			
			JSONObject data = new JSONObject();
			try {
				data.put("projectId", projectShareAuthorization.getProjectId());
				data.put("tbdFriendId", projectShareAuthorization.getLocalFriendId());
				data.put("apportions", jsonArray);
			} catch (JSONException e) {
				HyjUtil.displayToast(e.getMessage());
			}
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "projectSplitTBDTransactions");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch(requestCode){
         case GET_APPORTION_MEMBER_ID:
 			if (resultCode == Activity.RESULT_OK) {
 				String type = data.getStringExtra("MODEL_TYPE");
 				long _id = data.getLongExtra("MODEL_ID", -1);
 				if(_id != -1){
 					AddApportionMember(type, _id, new long[0]);
 				} else {
 					long[] _ids = data.getLongArrayExtra("MODEL_IDS");
					if(_ids != null && _ids.length > 0) {
//						for(int i=0; i<_ids.length; i++){
							AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
//						}
					}
 				}
 				
 			}
 			break;
 		case ADD_AS_PROJECT_MEMBER:
 			if (resultCode == Activity.RESULT_OK) {
 				String id = data.getStringExtra("MODELID");
 				ProjectShareAuthorization psa = HyjModel.getModel(ProjectShareAuthorization.class, id);
 				if(psa != null){
 					addAsProjectMember(psa, null, "ProjectShareAuthorization", new long[]{});
 				} else {
 					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
 				}
 			} else {
 				HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
 				
 			}
 			break;
          }
    }

	private void AddApportionMember(final String type, final long _id, final long[] _ids) {
			ProjectShareAuthorization psa = null;
			if("ProjectShareAuthorization".equalsIgnoreCase(type)){
				psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
			} else {
				final Friend friend = Friend.load(Friend.class, _id);
				//看一下该好友是不是圈子成员
				if(friend.getFriendUserId() != null){
					psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", friend.getFriendUserId(), projectShareAuthorization.getProjectId()).executeSingle();
				} else {
					psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", friend.getId(), projectShareAuthorization.getProjectId()).executeSingle();
				}
				
				if(psa == null){
					((HyjActivity)getActivity()).displayDialog(R.string.moneyApportionField_select_toast_apportion_user_not_member, R.string.moneyApportionField_select_confirm_apportion_add_as_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									Bundle bundle = new Bundle();
									bundle.putString("PROJECTID", projectShareAuthorization.getProjectId());
									if(friend.getFriendUserId() != null){
										bundle.putString("FRIEND_USERID", friend.getFriendUserId());
									} else {
										bundle.putString("LOCAL_FRIENDID", friend.getId());
									}
									openActivityWithFragmentForResult(ProjectMemberFormFragment.class, R.string.memberFormFragment_title_addnew, bundle, ADD_AS_PROJECT_MEMBER);
									if(_ids.length > 0){
										AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
									}
								}
		
								@Override
								public void doNegativeClick() {
									if(_ids.length > 0){
										AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
									} else {
										HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
									}
								}
							});
					
//					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
					return;
				}
			}
			addAsProjectMember(psa, null, type, _ids);
		
	}

	private void addAsProjectMember(ProjectShareAuthorization psa, String eventId, final String type, final long _ids[]){
		MoneyExpenseApportion apportion = new MoneyExpenseApportion();
		apportion.setFriendUserId(psa.getFriendUserId());
		apportion.setLocalFriendId(psa.getLocalFriendId());
		apportion.setAmount(0.0);
		if(psa.getSharePercentageType() != null && psa.getSharePercentageType().equals("Average")){
			apportion.setApportionType("Average");
		} else {
			apportion.setApportionType("Share");
		}
		if (mApportionFieldApportions.addApportion(apportion,projectShareAuthorization.getProjectId(), eventId, ApportionItem.NEW)) {
//			mApportionFieldApportions.setTotalAmount(0.0);
		} else {
			HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_already_exists);
		}
		if(_ids.length > 0){
			AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
		}
	}

	private void displayError(Object object){
		((HyjActivity) this.getActivity())
		.dismissProgressDialog();
		if(object != null){
			JSONObject json = (JSONObject) object;
			HyjUtil.displayToast(json.optJSONObject("__summary").optString(
					"msg"));
		}
	}
 
}
