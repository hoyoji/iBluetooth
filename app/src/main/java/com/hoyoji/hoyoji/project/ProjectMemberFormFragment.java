package com.hoyoji.hoyoji.project;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjBooleanView;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.friend.FriendListFragment;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.User;


public class ProjectMemberFormFragment extends HyjUserFormFragment {
	private final static int GET_FRIEND_ID = 2;

	private static final int TAG_MEMBER_IS_LOCAL_FRIEND = R.id.memberFormFragment_selectorField_friend;
	
	private HyjModelEditor<ProjectShareAuthorization> mProjectShareAuthorizationEditor = null;
	private List<ProjectShareAuthorization> mProjectShareAuthorizations;
	private HyjTextField mTextFieldProjectName = null;
	private HyjNumericField mNumericFieldSharePercentage = null;
	private HyjBooleanView mBooleanFieldSharePercentageType = null;
	private HyjSelectorField mSelectorFieldFriend = null;
	private CheckBox mCheckBoxShareAllSubProjects = null;
	
	private CheckBox mCheckBoxShareAuthExpenseSelf = null;
	private CheckBox mCheckBoxShareAuthExpenseAdd = null;
	private CheckBox mCheckBoxShareAuthExpenseEdit = null;
	private CheckBox mCheckBoxShareAuthExpenseDelete = null;

	private CheckBox mCheckBoxSendInvite = null;
	private Button mButtonSendInvite = null;
	
//	private CheckBox mCheckBoxShareAuthIncomeSelf = null;
//	private CheckBox mCheckBoxShareAuthIncomeAdd = null;
//	private CheckBox mCheckBoxShareAuthIncomeEdit = null;
//	private CheckBox mCheckBoxShareAuthIncomeDelete = null;
//
//	private CheckBox mCheckBoxShareAuthBorrowSelf = null;
//	private CheckBox mCheckBoxShareAuthBorrowAdd = null;
//	private CheckBox mCheckBoxShareAuthBorrowEdit = null;
//	private CheckBox mCheckBoxShareAuthBorrowDelete = null;
//
//	private CheckBox mCheckBoxShareAuthLendSelf = null;
//	private CheckBox mCheckBoxShareAuthLendAdd = null;
//	private CheckBox mCheckBoxShareAuthLendEdit = null;
//	private CheckBox mCheckBoxShareAuthLendDelete = null;
//	
//	private CheckBox mCheckBoxShareAuthReturnSelf = null;
//	private CheckBox mCheckBoxShareAuthReturnAdd = null;
//	private CheckBox mCheckBoxShareAuthReturnEdit = null;
//	private CheckBox mCheckBoxShareAuthReturnDelete = null;
//
//	private CheckBox mCheckBoxShareAuthPaybackSelf = null;
//	private CheckBox mCheckBoxShareAuthPaybackAdd = null;
//	private CheckBox mCheckBoxShareAuthPaybackEdit = null;
//	private CheckBox mCheckBoxShareAuthPaybackDelete = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.project_formfragment_member;
	}
	
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		final ProjectShareAuthorization projectShareAuthorization;
		Project project;
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			projectShareAuthorization =  new Select().from(ProjectShareAuthorization.class).where("_id=?", modelId).executeSingle();
			project = projectShareAuthorization.getProject();
		} else {
			projectShareAuthorization = new ProjectShareAuthorization();
			Long project_id = intent.getLongExtra("PROJECT_ID", -1);
			if(project_id != -1){
				project = Project.load(Project.class, project_id);
			} else {
				String projectId = intent.getStringExtra("PROJECTID");
				project = Project.getModel(Project.class, projectId);
			}
			projectShareAuthorization.setProjectId(project.getId());
			projectShareAuthorization.setState("NotInvite");
		}

		mProjectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
//		final ProjectShareAuthorization projectShareAuthorization = mProjectShareAuthorizationEditor.getModelCopy();
		
		mProjectShareAuthorizations = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND state <> ? AND id <> ?", project.getId(), "Delete", projectShareAuthorization.getId()).execute();
		mProjectShareAuthorizations.add(mProjectShareAuthorizationEditor.getModelCopy());
		
		mTextFieldProjectName = (HyjTextField) getView().findViewById(R.id.memberFormFragment_textField_projectName);
		mTextFieldProjectName.setText(project.getDisplayName());
		mTextFieldProjectName.setEnabled(false);
		
		mNumericFieldSharePercentage = (HyjNumericField) getView().findViewById(R.id.memberFormFragment_textField_sharePercentage);
		mNumericFieldSharePercentage.setNumber(projectShareAuthorization.getSharePercentage());
		mNumericFieldSharePercentage.setEnabled(!projectShareAuthorization.getSharePercentageType().equalsIgnoreCase("Average"));
		if(modelId == -1){
			setAveragePercentage(mProjectShareAuthorizationEditor.getModelCopy());
		}
		
		mBooleanFieldSharePercentageType = (HyjBooleanView) getView().findViewById(R.id.memberFormFragment_textField_sharePercentageType);
		mBooleanFieldSharePercentageType.setBoolean(projectShareAuthorization.getSharePercentageType().equalsIgnoreCase("Average"));
		mBooleanFieldSharePercentageType.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!mBooleanFieldSharePercentageType.getBoolean()){
					mBooleanFieldSharePercentageType.setBoolean(true);
					mNumericFieldSharePercentage.setEnabled(false);
					setAveragePercentage(mProjectShareAuthorizationEditor.getModelCopy());
				} else {
					mBooleanFieldSharePercentageType.setBoolean(false);
					mNumericFieldSharePercentage.setEnabled(true);
				}
			}
		});
		
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.memberFormFragment_selectorField_friend);
		if(modelId != -1){
			mSelectorFieldFriend.setEnabled(false);
			if(projectShareAuthorization.getFriendUserId() != null){
				Friend friend = new Select().from(Friend.class).where("friendUserId=?", projectShareAuthorization.getFriendUserId()).executeSingle();
				if(friend != null){
					mSelectorFieldFriend.setModelId(friend.getFriendUserId());
					mSelectorFieldFriend.setText(friend.getDisplayName());
				} else {
					User user = new Select().from(User.class).where("id=?", projectShareAuthorization.getFriendUserId()).executeSingle();
					if(user != null){
						mSelectorFieldFriend.setModelId(user.getId());
						mSelectorFieldFriend.setText(user.getDisplayName());
					}
				}
  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, false);
			} else if(projectShareAuthorization.getLocalFriendId() != null){
				Friend friend = HyjModel.getModel(Friend.class, projectShareAuthorization.getLocalFriendId());
				if(friend != null){
					mSelectorFieldFriend.setModelId(friend.getId());
					mSelectorFieldFriend.setText(friend.getDisplayName());
				} else {
					mSelectorFieldFriend.setModelId(null);
					mSelectorFieldFriend.setText(projectShareAuthorization.getFriendUserName());
				}
  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, true);
			} else {
				mSelectorFieldFriend.setModelId(null);
				mSelectorFieldFriend.setText(projectShareAuthorization.getFriendUserName());
  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, true);
			}
		} else {
			String friendUserId = intent.getStringExtra("FRIEND_USERID");
			if(friendUserId != null){
				Friend friend = new Select().from(Friend.class).where("friendUserId=?", friendUserId).executeSingle();
				if(friend != null){
					mSelectorFieldFriend.setModelId(friend.getFriendUserId());
					mSelectorFieldFriend.setText(friend.getDisplayName());
				} else {
					User user = new Select().from(User.class).where("id=?", friendUserId).executeSingle();
					if(user != null){
						mSelectorFieldFriend.setModelId(user.getId());
						mSelectorFieldFriend.setText(user.getDisplayName());
					}
				}
  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, false);
			} else {
				String localFriendId = intent.getStringExtra("LOCAL_FRIENDID");
				if(localFriendId != null){
					Friend friend = HyjModel.getModel(Friend.class, localFriendId);
					if(friend != null){
						mSelectorFieldFriend.setModelId(friend.getId());
						mSelectorFieldFriend.setText(friend.getDisplayName());
					} else {
						mSelectorFieldFriend.setModelId(null);
						mSelectorFieldFriend.setText(null);
					}
	  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, true);
				}
			}
		}
		mSelectorFieldFriend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ProjectMemberFormFragment.this.openActivityWithFragmentForResult(FriendListFragment.class, R.string.memberFormFragment_editText_hint_friend, null, GET_FRIEND_ID);
			}
		});	
		
		mCheckBoxShareAllSubProjects = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAllSubProjects);
		mCheckBoxShareAllSubProjects.setChecked(projectShareAuthorization.getShareAllSubProjects());
	
		mCheckBoxShareAuthExpenseSelf = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_expense_self);
		mCheckBoxShareAuthExpenseSelf.setChecked(projectShareAuthorization.getProjectShareMoneyExpenseOwnerDataOnly());
		mCheckBoxShareAuthExpenseAdd = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_expense_add);
		mCheckBoxShareAuthExpenseAdd.setChecked(projectShareAuthorization.getProjectShareMoneyExpenseAddNew());
		mCheckBoxShareAuthExpenseEdit = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_expense_edit);
		mCheckBoxShareAuthExpenseEdit.setChecked(projectShareAuthorization.getProjectShareMoneyExpenseEdit());
		mCheckBoxShareAuthExpenseDelete = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_expense_delete);
		mCheckBoxShareAuthExpenseDelete.setChecked(projectShareAuthorization.getProjectShareMoneyExpenseDelete());

		mCheckBoxSendInvite = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_send_invite);
		mButtonSendInvite = (Button)getView().findViewById(R.id.memberFormFragment_button_send_invite);
		if(modelId == -1){
			mCheckBoxSendInvite.setVisibility(View.VISIBLE);
			mButtonSendInvite.setVisibility(View.GONE);
		}  else {
			mCheckBoxSendInvite.setVisibility(View.GONE);
			if(projectShareAuthorization.getFriendUserId() != null && !projectShareAuthorization.getState().equals("Accept")){
				mButtonSendInvite.setVisibility(View.VISIBLE);
			}
		}
		mButtonSendInvite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sendInviteMessage();
			}
		});
		if(modelId != -1){
			boolean canNotEdit = false;
			if(!mProjectShareAuthorizationEditor.getModel().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				canNotEdit = true;
			} else {
				// 如果是待定成员也不能修改
				 if(projectShareAuthorization.getToBeDetermined()){
						 canNotEdit = true;
				 }
			}
			if(canNotEdit){
				mButtonSendInvite.setVisibility(View.GONE);
				mCheckBoxSendInvite.setVisibility(View.GONE);
				mCheckBoxShareAllSubProjects.setEnabled(false);
				mCheckBoxShareAuthExpenseSelf.setEnabled(false);
				mCheckBoxShareAuthExpenseAdd.setEnabled(false);
				mCheckBoxShareAuthExpenseEdit.setEnabled(false);
				mCheckBoxShareAuthExpenseDelete.setEnabled(false);
				mBooleanFieldSharePercentageType.setEnabled(false);
				mNumericFieldSharePercentage.setEnabled(false);
				getView().findViewById(R.id.button_save).setVisibility(View.GONE);
				if(this.mOptionsMenu != null){
					hideSaveAction();
				}
			}
		}
		
		
//		mCheckBoxShareAuthIncomeSelf = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_income_self);
//		mCheckBoxShareAuthIncomeSelf.setChecked(projectShareAuthorization.getProjectShareMoneyIncomeOwnerDataOnly());
//		mCheckBoxShareAuthIncomeAdd = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_income_add);
//		mCheckBoxShareAuthIncomeAdd.setChecked(projectShareAuthorization.getProjectShareMoneyIncomeAddNew());
//		mCheckBoxShareAuthIncomeEdit = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_income_edit);
//		mCheckBoxShareAuthIncomeEdit.setChecked(projectShareAuthorization.getProjectShareMoneyIncomeEdit());
//		mCheckBoxShareAuthIncomeDelete = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_income_delete);
//		mCheckBoxShareAuthIncomeDelete.setChecked(projectShareAuthorization.getProjectShareMoneyIncomeDelete());
//		
//		mCheckBoxShareAuthBorrowSelf = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_borrow_self);
//		mCheckBoxShareAuthBorrowSelf.setChecked(projectShareAuthorization.getProjectShareMoneyBorrowOwnerDataOnly());
//		mCheckBoxShareAuthBorrowAdd = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_borrow_add);
//		mCheckBoxShareAuthBorrowAdd.setChecked(projectShareAuthorization.getProjectShareMoneyBorrowAddNew());
//		mCheckBoxShareAuthBorrowEdit = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_borrow_edit);
//		mCheckBoxShareAuthBorrowEdit.setChecked(projectShareAuthorization.getProjectShareMoneyBorrowEdit());
//		mCheckBoxShareAuthBorrowDelete = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_borrow_delete);
//		mCheckBoxShareAuthBorrowDelete.setChecked(projectShareAuthorization.getProjectShareMoneyBorrowDelete());
//		
//		mCheckBoxShareAuthLendSelf = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_lend_self);
//		mCheckBoxShareAuthLendSelf.setChecked(projectShareAuthorization.getProjectShareMoneyLendOwnerDataOnly());
//		mCheckBoxShareAuthLendAdd = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_lend_add);
//		mCheckBoxShareAuthLendAdd.setChecked(projectShareAuthorization.getProjectShareMoneyLendAddNew());
//		mCheckBoxShareAuthLendEdit = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_lend_edit);
//		mCheckBoxShareAuthLendEdit.setChecked(projectShareAuthorization.getProjectShareMoneyLendEdit());
//		mCheckBoxShareAuthLendDelete = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_lend_delete);
//		mCheckBoxShareAuthLendDelete.setChecked(projectShareAuthorization.getProjectShareMoneyLendDelete());
//		
//		mCheckBoxShareAuthReturnSelf = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_return_self);
//		mCheckBoxShareAuthReturnSelf.setChecked(projectShareAuthorization.getProjectShareMoneyReturnOwnerDataOnly());
//		mCheckBoxShareAuthReturnAdd = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_return_add);
//		mCheckBoxShareAuthReturnAdd.setChecked(projectShareAuthorization.getProjectShareMoneyReturnAddNew());
//		mCheckBoxShareAuthReturnEdit = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_return_edit);
//		mCheckBoxShareAuthReturnEdit.setChecked(projectShareAuthorization.getProjectShareMoneyReturnEdit());
//		mCheckBoxShareAuthReturnDelete = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_return_delete);
//		mCheckBoxShareAuthReturnDelete.setChecked(projectShareAuthorization.getProjectShareMoneyReturnDelete());
//
//		mCheckBoxShareAuthPaybackSelf = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_payback_self);
//		mCheckBoxShareAuthPaybackSelf.setChecked(projectShareAuthorization.getProjectShareMoneyPaybackOwnerDataOnly());
//		mCheckBoxShareAuthPaybackAdd = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_payback_add);
//		mCheckBoxShareAuthPaybackAdd.setChecked(projectShareAuthorization.getProjectShareMoneyPaybackAddNew());
//		mCheckBoxShareAuthPaybackEdit = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_payback_edit);
//		mCheckBoxShareAuthPaybackEdit.setChecked(projectShareAuthorization.getProjectShareMoneyPaybackEdit());
//		mCheckBoxShareAuthPaybackDelete = (CheckBox)getView().findViewById(R.id.memberFormFragment_checkBox_shareAuthorization_payback_delete);
//		mCheckBoxShareAuthPaybackDelete.setChecked(projectShareAuthorization.getProjectShareMoneyPaybackDelete());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(mProjectShareAuthorizationEditor != null && mProjectShareAuthorizationEditor.getModel().get_mId() != null){
				boolean canNotEdit = false;
				if(!mProjectShareAuthorizationEditor.getModel().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					canNotEdit = true;
				} else {
					// 如果是待定成员也不能修改
					 if(mProjectShareAuthorizationEditor.getModel().getToBeDetermined()){
							 canNotEdit = true;
					 }
				}
				if(canNotEdit){
					hideSaveAction();
				}
		}
	}
	
	private void setAveragePercentage(ProjectShareAuthorization projectShareAuthorization) {
		//将成员设成平均分摊
		double fixedPercentageTotal = 0.0;
//		double averageTotal = 0.0;
		int numOfAverage = 0;
		for(ProjectShareAuthorization psa : mProjectShareAuthorizations) {
			if(!psa.getSharePercentageType().equalsIgnoreCase("Average") && psa != projectShareAuthorization){
				fixedPercentageTotal += psa.getSharePercentage();
			} else {
				numOfAverage++;
//				averageTotal += psa.getSharePercentage();
			}
		}
		double averageAmount = HyjUtil.toFixed2((100.0 - Math.min(fixedPercentageTotal, 100.0)) / numOfAverage);
		double adjsutedAverageAmount = HyjUtil.toFixed2(averageAmount + (100.0 - fixedPercentageTotal - averageAmount * numOfAverage));
		for(ProjectShareAuthorization psa : mProjectShareAuthorizations) {
			if(psa.getSharePercentage().doubleValue() == adjsutedAverageAmount && psa != projectShareAuthorization){
				mNumericFieldSharePercentage.setNumber(averageAmount);
				return;
			} 
		}
		
		mNumericFieldSharePercentage.setNumber(adjsutedAverageAmount);
	}
	
	
	private void fillData() {
		ProjectShareAuthorization modelCopy = mProjectShareAuthorizationEditor.getModelCopy();
		modelCopy.setSharePercentage(mNumericFieldSharePercentage.getNumber());
		modelCopy.setSharePercentageType(mBooleanFieldSharePercentageType.getBoolean() ? "Average" : "Fix");
		modelCopy.setShareAllSubProjects(mCheckBoxShareAllSubProjects.isChecked());
		
		if(mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) != null && (Boolean)mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) == false){
			modelCopy.setFriendUserId(mSelectorFieldFriend.getModelId());
			modelCopy.setLocalFriendId(null);
			if(modelCopy.getFriendUserId() != null){
				modelCopy.setFriendUserName(modelCopy.getFriend().getDisplayName());
			}

			if(modelCopy.get_mId() == null  || modelCopy.isClientNew()){
				if(mCheckBoxSendInvite.isChecked()){
					modelCopy.setState("Wait");
				} else {
					modelCopy.setState("NotInvite");
				}
			}
		} else if(mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) != null && (Boolean)mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) == true){
			modelCopy.setLocalFriendId(mSelectorFieldFriend.getModelId());
			modelCopy.setFriendUserId(null);
			if(modelCopy.getLocalFriendId() != null){
				modelCopy.setFriendUserName(modelCopy.getFriend().getDisplayName());
			}
			modelCopy.setState("NotInvite");
		}
		

		modelCopy.setProjectShareMoneyExpenseOwnerDataOnly(mCheckBoxShareAuthExpenseSelf.isChecked());
		modelCopy.setProjectShareMoneyExpenseAddNew(mCheckBoxShareAuthExpenseAdd.isChecked());
		modelCopy.setProjectShareMoneyExpenseEdit(mCheckBoxShareAuthExpenseEdit.isChecked());
		modelCopy.setProjectShareMoneyExpenseDelete(mCheckBoxShareAuthExpenseDelete.isChecked());	
		
//		modelCopy.setProjectShareMoneyIncomeOwnerDataOnly(mCheckBoxShareAuthIncomeSelf.isChecked());
//		modelCopy.setProjectShareMoneyIncomeAddNew(mCheckBoxShareAuthIncomeAdd.isChecked());
//		modelCopy.setProjectShareMoneyIncomeEdit(mCheckBoxShareAuthIncomeEdit.isChecked());
//		modelCopy.setProjectShareMoneyIncomeDelete(mCheckBoxShareAuthIncomeDelete.isChecked());
//		
//		modelCopy.setProjectShareMoneyBorrowOwnerDataOnly(mCheckBoxShareAuthBorrowSelf.isChecked());
//		modelCopy.setProjectShareMoneyBorrowAddNew(mCheckBoxShareAuthBorrowAdd.isChecked());
//		modelCopy.setProjectShareMoneyBorrowEdit(mCheckBoxShareAuthBorrowEdit.isChecked());
//		modelCopy.setProjectShareMoneyBorrowDelete(mCheckBoxShareAuthBorrowDelete.isChecked());
//		
//		modelCopy.setProjectShareMoneyLendOwnerDataOnly(mCheckBoxShareAuthLendSelf.isChecked());
//		modelCopy.setProjectShareMoneyLendAddNew(mCheckBoxShareAuthLendAdd.isChecked());
//		modelCopy.setProjectShareMoneyLendEdit(mCheckBoxShareAuthLendEdit.isChecked());
//		modelCopy.setProjectShareMoneyLendDelete(mCheckBoxShareAuthLendDelete.isChecked());
//		
//		modelCopy.setProjectShareMoneyReturnOwnerDataOnly(mCheckBoxShareAuthReturnSelf.isChecked());
//		modelCopy.setProjectShareMoneyReturnAddNew(mCheckBoxShareAuthReturnAdd.isChecked());
//		modelCopy.setProjectShareMoneyReturnEdit(mCheckBoxShareAuthReturnEdit.isChecked());
//		modelCopy.setProjectShareMoneyReturnDelete(mCheckBoxShareAuthReturnDelete.isChecked());
//		
//		modelCopy.setProjectShareMoneyPaybackOwnerDataOnly(mCheckBoxShareAuthPaybackSelf.isChecked());
//		modelCopy.setProjectShareMoneyPaybackAddNew(mCheckBoxShareAuthPaybackAdd.isChecked());
//		modelCopy.setProjectShareMoneyPaybackEdit(mCheckBoxShareAuthPaybackEdit.isChecked());
//		modelCopy.setProjectShareMoneyPaybackDelete(mCheckBoxShareAuthPaybackDelete.isChecked());
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		
		mNumericFieldSharePercentage.setError(mProjectShareAuthorizationEditor.getValidationError("sharePercentage"));
		mSelectorFieldFriend.setError(mProjectShareAuthorizationEditor.getValidationError("friendUser"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		if(mProjectShareAuthorizationEditor.getModel().get_mId() != null && !mProjectShareAuthorizationEditor.getModel().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
			return;
		}
		
		fillData();

		mProjectShareAuthorizationEditor.validate();
		validateFixedPercentageTotal(mProjectShareAuthorizationEditor);
		
		if(mProjectShareAuthorizationEditor.hasValidationErrors()){
			showValidatioErrors();
		} else if(mProjectShareAuthorizationEditor.getModelCopy().get_mId() == null){ 
			sendNewProjectShareAuthorizationToServer();
		} else {
			sendEditProjectShareAuthorizationToServer();
			
		}
	}	

	private void sendInviteMessage() {
		
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				loadProjectProjectShareAuthorizations(object);
				HyjUtil.displayToast(R.string.memberFormFragment_toast_share_request_sent_success);
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

		mProjectShareAuthorizationEditor.getModelCopy().setState("Wait");
		JSONObject msg = getInviteMessage();
		String data = "[" + msg.toString() + "]";
		
		HyjHttpPostAsyncTask.newInstance(serverCallbacks, data, "projectShareInvite");
		((HyjActivity) ProjectMemberFormFragment.this.getActivity())
		.displayProgressDialog(
				R.string.memberFormFragment_title_edit,
				R.string.memberFormFragment_progress_inviting);
	}

	 
	private void sendNewProjectShareAuthorizationToServer() {
		
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				loadProjectProjectShareAuthorizations(object);
				HyjUtil.displayToast(R.string.memberFormFragment_toast_share_add_success);
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

//		try {
			String data = "[";
			
			JSONObject jsonPSA = mProjectShareAuthorizationEditor.getModelCopy().toJSON();
			data += jsonPSA.toString();
			
			//如果圈子也是新建的，一同保存到服务器
			if(mProjectShareAuthorizationEditor.getModelCopy().getProject().isClientNew()){
				data += "," + mProjectShareAuthorizationEditor.getModelCopy().getProject().toJSON().toString();
			}
			
			JSONObject msg = getInviteMessage();
			data += "," + msg.toString() + "]";
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data, "projectShareAdd");
			((HyjActivity) ProjectMemberFormFragment.this.getActivity())
			.displayProgressDialog(
					R.string.memberFormFragment_title_addnew,
					R.string.memberFormFragment_progress_adding);
//		} catch (JSONException e1) {
//		}
	}
	
private void sendEditProjectShareAuthorizationToServer() {
		
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				loadProjectProjectShareAuthorizations(object);
				HyjUtil.displayToast(R.string.memberFormFragment_toast_share_edit_success);
			}

			@Override
			public void errorCallback(Object object) {
				displayError(object);
			}
		};

//		try {
			String data = "[";
			
			JSONObject jsonPSA = mProjectShareAuthorizationEditor.getModelCopy().toJSON();
			data += jsonPSA.toString();
			
			//如果圈子也是新建的，一同保存到服务器
			if(mProjectShareAuthorizationEditor.getModelCopy().getProject().isClientNew()){
				data += "," + mProjectShareAuthorizationEditor.getModelCopy().getProject().toJSON().toString();
			}
			
			JSONObject msg = new JSONObject();
			try {
				msg.put("__dataType", "Message");
				msg.put("toUserId", mProjectShareAuthorizationEditor.getModelCopy().getFriendUserId());
				msg.put("fromUserId", HyjApplication.getInstance()
						.getCurrentUser().getId());
				msg.put("type", "Project.Share.Edit");
				msg.put("messageState", "new");
				msg.put("messageTitle", "修改圈子共享");
				msg.put("date", (new Date()).getTime());
				msg.put("detail", "用户"
						+ HyjApplication.getInstance().getCurrentUser()
								.getDisplayName() + "修改了圈子共享权限: " + mProjectShareAuthorizationEditor.getModelCopy().getProject().getName());
		//		msg.put("messageBoxId", mProjectShareAuthorizationEditor.getModelCopy().getFriendUser().getMessageBoxId());
				msg.put("ownerUserId", mProjectShareAuthorizationEditor.getModelCopy().getFriendUserId());
				
				JSONObject msgData = new JSONObject();
				msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
				msgData.put("toUserDisplayName", mProjectShareAuthorizationEditor.getModelCopy().getFriend().getFriendUserName());
				msgData.put("shareAllSubProjects", mProjectShareAuthorizationEditor.getModelCopy().getShareAllSubProjects());
				msgData.put("projectShareAuthorizationId", mProjectShareAuthorizationEditor.getModelCopy().getId());
		//		msgData.put("fromMessageBoxId", HyjApplication.getInstance().getCurrentUser().getMessageBoxId());
				msgData.put("projectName", mProjectShareAuthorizationEditor.getModelCopy().getProject().getName());
				msgData.put("projectIds", new JSONArray("[" + mProjectShareAuthorizationEditor.getModelCopy().getProjectId()  + "]"));
				msgData.put("projectCurrencyIds", new JSONArray("[" + mProjectShareAuthorizationEditor.getModelCopy().getProject().getCurrencyId()  + "]"));
				msg.put("messageData", msgData.toString());
				
				if(mProjectShareAuthorizationEditor.getModelCopy().getState().equals("Accept")){
					//只有接受了的圈子，我们才发送修改消息
					msg.put("id", UUID.randomUUID().toString());
				}
			} catch (JSONException e1) {
			}
				
				
			data += "," + msg.toString() + "]";
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data, "projectShareEdit");
			((HyjActivity) ProjectMemberFormFragment.this.getActivity())
			.displayProgressDialog(
					R.string.memberFormFragment_title_edit,
					R.string.memberFormFragment_progress_saving);
	}

	private JSONObject getInviteMessage() {
		JSONObject msg = new JSONObject();
		try {
			msg.put("__dataType", "Message");
			msg.put("toUserId", mProjectShareAuthorizationEditor.getModelCopy().getFriendUserId());
			msg.put("fromUserId", HyjApplication.getInstance()
					.getCurrentUser().getId());
			msg.put("type", "Project.Share.AddRequest");
			msg.put("messageState", "new");
			msg.put("messageTitle", "圈子共享请求");
			msg.put("date", (new Date()).getTime());
			msg.put("detail", "用户"
					+ HyjApplication.getInstance().getCurrentUser()
							.getDisplayName() + "给您共享圈子: " + mProjectShareAuthorizationEditor.getModelCopy().getProject().getName());
	//		msg.put("messageBoxId", mProjectShareAuthorizationEditor.getModelCopy().getFriendUser().getMessageBoxId());
			msg.put("ownerUserId", mProjectShareAuthorizationEditor.getModelCopy().getFriendUserId());
			
			JSONObject msgData = new JSONObject();
			msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
			msgData.put("toUserDisplayName", mProjectShareAuthorizationEditor.getModelCopy().getFriend().getFriendUserName());
			msgData.put("shareAllSubProjects", mProjectShareAuthorizationEditor.getModelCopy().getShareAllSubProjects());
			msgData.put("projectShareAuthorizationId", mProjectShareAuthorizationEditor.getModelCopy().getId());
	//		msgData.put("fromMessageBoxId", HyjApplication.getInstance().getCurrentUser().getMessageBoxId());
			msgData.put("projectName", mProjectShareAuthorizationEditor.getModelCopy().getProject().getName());
			msgData.put("projectIds", new JSONArray("[" + mProjectShareAuthorizationEditor.getModelCopy().getProjectId()  + "]"));
			msgData.put("projectCurrencyIds", new JSONArray("[" + mProjectShareAuthorizationEditor.getModelCopy().getProject().getCurrencyId()  + "]"));
			msg.put("messageData", msgData.toString());
			
			if(!mProjectShareAuthorizationEditor.getModelCopy().getState().equals("NotInvite")){
					// 该消息不会发给用户，只在服务器上做处理，所以没有id。在服务器上，没有id的消息是不会被保存的。
					msg.put("id", UUID.randomUUID().toString());
			}
		} catch (JSONException e) {
		}
		return msg;
		
	}

	protected void loadProjectProjectShareAuthorizations(Object object) {
//		// load new ProjectData from server
//		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
//			@Override
//			public void finishCallback(Object object) {
				try {

					JSONArray jsonObjects = (JSONArray) object;
					ActiveAndroid.beginTransaction();
//
//					for (int i = 0; i < jsonArray.length(); i++) {
//						JSONArray jsonObjects = jsonArray.getJSONArray(i);
						for (int j = 0; j < jsonObjects.length(); j++) {
							if (jsonObjects.optJSONObject(j)
									.optString("__dataType")
									.equals("ProjectShareAuthorization")) {
								String id = jsonObjects.optJSONObject(j).optString("id");
								ProjectShareAuthorization newProjectShareAuthorization = HyjModel.getModel(ProjectShareAuthorization.class, id);
								if(newProjectShareAuthorization == null){
									newProjectShareAuthorization = new ProjectShareAuthorization();
								}
								newProjectShareAuthorization.loadFromJSON(
										jsonObjects.optJSONObject(j), true);
								newProjectShareAuthorization.save();
							}
						}
//					}

					ActiveAndroid.setTransactionSuccessful();
					if(getActivity().getCallingActivity() != null){
						Intent data = new Intent();
						data.putExtra("MODELID", mProjectShareAuthorizationEditor.getModelCopy().getId());
						getActivity().setResult(Activity.RESULT_OK, data);
					}
					getActivity().finish();
//				} catch (JSONException e) {
//					e.printStackTrace();
				} finally {
					ActiveAndroid.endTransaction();
				}

				((HyjActivity) ProjectMemberFormFragment.this.getActivity())
				.dismissProgressDialog();
//			}
//
//			@Override
//			public void errorCallback(Object object) {
//				displayError(object);
//			}
//		};
//
//		JSONArray data = new JSONArray();
//		try {
//				JSONObject newObj = new JSONObject();
//				newObj = new JSONObject();
//				newObj.put("__dataType", "ProjectShareAuthorization");
//				newObj.put("main.projectId", projectId);
////				newObj.put("main.state", "Accept");
//				data.put(newObj);
//			HyjHttpPostAsyncTask.newInstance(serverCallbacks, data.toString(), "getData");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	
//	 private void saveAverageTotal(){
//		 //重新计算所有均摊成员的占股比例并保存
//			double fixedPercentageTotal = 0.0;
//			int numOfAverage = 0;
//			for(ProjectShareAuthorization psa : mProjectShareAuthorizations) {
//				if(psa.getSharePercentageType().equalsIgnoreCase("Fix")){
//					fixedPercentageTotal += psa.getSharePercentage();
//				} else {
//					numOfAverage++;
//				}
//			}
//			
//			double averageTotal = 0.0;
//			double averageAmount = 0.0;
//			double averageTotalAmount = 100.0 - Math.min(fixedPercentageTotal, 100.0);
//			if(numOfAverage > 0) {
//				averageAmount = HyjUtil.toFixed2(averageTotalAmount / numOfAverage);
//			}
//			double diff = HyjUtil.toFixed2(100.0 - fixedPercentageTotal - averageAmount * numOfAverage);
//			double adjustedAverageTotal = HyjUtil.toFixed2(averageAmount + diff);
//			
//			for(ProjectShareAuthorization psa : mProjectShareAuthorizations) {
//				if(psa.getSharePercentageType().equalsIgnoreCase("Average")){
//					if(!psa.getId().equals(mProjectShareAuthorizationEditor.getModelCopy().getId()) &&
//							(psa.getSharePercentage().doubleValue() != averageAmount && 
//							psa.getSharePercentage().doubleValue() != adjustedAverageTotal)){
////						HyjModelEditor<ProjectShareAuthorization> editor = psa.newModelEditor();
////						editor.getModelCopy().setSharePercentage(averageAmount);
//						averageTotal += averageAmount;
////						editor.save();
//					} else {
//						averageTotal += psa.getSharePercentage();
//					}
//				}
//			}
//			if(HyjUtil.toFixed2(averageTotal) != HyjUtil.toFixed2(100.0 - fixedPercentageTotal)){
//				mProjectShareAuthorizationEditor.getModelCopy().setSharePercentage(
//						mProjectShareAuthorizationEditor.getModelCopy().getSharePercentage() + diff);
//			}
//	 }
	 
	 private double validateFixedPercentageTotal(HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor) {
			if(projectShareAuthorizationEditor.getModelCopy().getSharePercentageType().equalsIgnoreCase("Average")){
				return 0.0;
			}
			if(projectShareAuthorizationEditor.getValidationError("sharePercentage") != null){
				return 0.0;
			}
			
			Double fixedPercentageTotal = 0.0;
			int numOfAverage = 0;
			for(ProjectShareAuthorization psa : mProjectShareAuthorizations) {
				if(psa.getSharePercentageType().equalsIgnoreCase("Fix")){
					fixedPercentageTotal += psa.getSharePercentage();
				} else {
					numOfAverage ++;
				}
			}
			if(fixedPercentageTotal > 100.00 || (fixedPercentageTotal < 100.0 && numOfAverage == 0)){
				projectShareAuthorizationEditor.setValidationError("sharePercentage", this.getActivity().getString(R.string.memberFormFragment_editText_error_sharePercentage));
			} else {
				projectShareAuthorizationEditor.removeValidationError("sharePercentage");
			}
			return fixedPercentageTotal;
		}
	 
		 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
	         switch(requestCode){
	              case GET_FRIEND_ID:
	            	 if(resultCode == Activity.RESULT_OK){
	            		 long _id = data.getLongExtra("MODEL_ID", -1);
	            		 if(_id == -1){
	      	   	       		mSelectorFieldFriend.setText(null);
	      	   	       		mSelectorFieldFriend.setModelId(null);
	     				} else {
		 	         		 Friend friend = Friend.load(Friend.class, _id);
	 	         			 for(ProjectShareAuthorization psa : mProjectShareAuthorizations) {
		 	       				if(friend.getFriendUserId() != null){
		 	       					if(psa.getFriendUserId() != null && psa.getFriendUserId().equalsIgnoreCase(friend.getFriendUserId())){
			 	       					HyjUtil.displayToast(R.string.memberFormFragment_toast_friend_already_exists);
			 	       					return;
			 	       				}
			 	 	         	} else {
			 	       				if(psa.getLocalFriendId() != null && psa.getLocalFriendId().equalsIgnoreCase(friend.getId())){
			 	       					HyjUtil.displayToast(R.string.memberFormFragment_toast_friend_already_exists);
			 	       					return;
		 	       					}
			 	       			}
	 	         			 }
	 	         			if(friend.getFriendUserId() != null){
	 	         				mSelectorFieldFriend.setText(friend.getDisplayName());
	 	         				mSelectorFieldFriend.setModelId(friend.getFriendUserId());
	 	         				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, false);
	 	         			} else {
	 	         				mSelectorFieldFriend.setText(friend.getDisplayName());
	 	         				mSelectorFieldFriend.setModelId(friend.getId());
	 	         				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, true);
	 	         			}
	     				}
	            	 }
	            	 break;
	          }
	    }
	 

		private void displayError(Object object){
			((HyjActivity) this.getActivity())
			.dismissProgressDialog();
			JSONObject json = (JSONObject) object;
			HyjUtil.displayToast(json.optJSONObject("__summary").optString(
					"msg"));
		}
	 
}
