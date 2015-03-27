package com.hoyoji.hoyoji.event;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.hoyoji.money.SelectApportionMemberListFragment;

public class EventMemberFormFragment extends HyjUserFormFragment {
	private final static int GET_FRIEND_ID = 1;
	private static final int TAG_MEMBER_IS_LOCAL_FRIEND = R.id.projectEventMemberFormFragment_selectorField_friend;
	
	private HyjModelEditor<EventMember> mEventMemberEditor = null;
	private List<EventMember> mEventMembers;
//	private HyjTextField mTextFieldName = null;
	private HyjTextField mEventName = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjDateTimeField mDateTimeFieldStartDate = null;
	private HyjDateTimeField mDateTimeFieldEndDate = null;
	private HyjSelectorField mSelectorFieldFriend = null;
	private RadioGroup stateRadioGroup = null;
	private RadioButton cancelSignUpRadioButton = null;
	private RadioButton unSignUpRadioButton = null;
	private RadioButton signUpRadioButton = null;
	private RadioButton unSignInRadioButton = null;
	private RadioButton signInRadioButton = null;
	private ProjectShareAuthorization jsonPSA = null;
	
	private CheckBox mCheckBoxEventShareOwnerDataOnly = null;
	
//	private Button button_cancel_signUp;
	
	private Button button_setting_nickName;
	private HyjTextField mEventMemberNickName = null;
	private ChangeObserver mChangeObserver;
	
	private String oldState;

	@Override
	public Integer useContentView() {
		return R.layout.project_formfragment_event_member;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		EventMember eventMember;
		Event event;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			eventMember = new Select().from(EventMember.class).where("_id=?", modelId).executeSingle();
			event = eventMember.getEvent();
		} else {
			eventMember = new EventMember();
			Long event_id = intent.getLongExtra("EVENT_ID", -1);
			if(event_id != -1){
				event = Event.load(Event.class, event_id);
			} else {
				String eventId = intent.getStringExtra("EVENTID");
				event = Event.getModel(Event.class, eventId);
			}
			eventMember.setEventId(event.getId());
			eventMember.setState("UnSignUp");
		}
		mEventMemberEditor = eventMember.newModelEditor();
		
		mEventMembers = new Select().from(EventMember.class).where("eventId = ? AND id <> ?", event.getId(), eventMember.getId()).execute();
		mEventMembers.add(mEventMemberEditor.getModelCopy());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.projectEventMemberFormFragment_hyjDateTimeField_date);
		mDateTimeFieldDate.setEnabled(false);
		
		mEventName = (HyjTextField) getView().findViewById(R.id.projectEventMemberFormFragment_textField_eventName);
		mEventName.setText(event.getName());
		mEventName.setEnabled(false);
		
		mDateTimeFieldStartDate = (HyjDateTimeField) getView().findViewById(R.id.projectEventMemberFormFragment_hyjDateTimeField_startDate);
		
		mDateTimeFieldEndDate = (HyjDateTimeField) getView().findViewById(R.id.projectEventMemberFormFragment_hyjDateTimeField_endDate);
		
		mDateTimeFieldDate.setTime(event.getDate());
		mDateTimeFieldStartDate.setTime(event.getStartDate());
		mDateTimeFieldEndDate.setTime(event.getEndDate());
		mDateTimeFieldDate.setEnabled(false);
		mDateTimeFieldStartDate.setEnabled(false);
		mDateTimeFieldEndDate.setEnabled(false);
		
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.projectEventMemberFormFragment_selectorField_friend);

		if(modelId != -1){
			mSelectorFieldFriend.setEnabled(false);
			if(eventMember.getFriendUserId() != null){
				Friend friend = new Select().from(Friend.class).where("friendUserId=?", eventMember.getFriendUserId()).executeSingle();
				if(friend != null){
					mSelectorFieldFriend.setModelId(friend.getFriendUserId());
					mSelectorFieldFriend.setText(friend.getDisplayName());
				} else {
					User user = new Select().from(User.class).where("id=?", eventMember.getFriendUserId()).executeSingle();
					if(user != null){
						mSelectorFieldFriend.setModelId(user.getId());
						mSelectorFieldFriend.setText(user.getDisplayName());
					}
				}
  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, false);
			} else if(eventMember.getLocalFriendId() != null){
				Friend friend = HyjModel.getModel(Friend.class, eventMember.getLocalFriendId());
				if(friend != null){
					mSelectorFieldFriend.setModelId(friend.getId());
					mSelectorFieldFriend.setText(friend.getDisplayName());
				} else {
					mSelectorFieldFriend.setModelId(null);
					mSelectorFieldFriend.setText(eventMember.getFriendUserName());
				}
  				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, true);
			} else {
				mSelectorFieldFriend.setModelId(null);
				mSelectorFieldFriend.setText(eventMember.getFriendUserName());
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
		final Event thisEvent = event;
		mSelectorFieldFriend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("MODEL_ID", thisEvent.getProject().get_mId());
				bundle.putBoolean("disableMultiChoiceMode", true);
				EventMemberFormFragment.this.openActivityWithFragmentForResult(SelectApportionMemberListFragment.class, R.string.projectEventMemberFormFragment_selectorField_hint_friend, bundle, GET_FRIEND_ID);
//				EventMemberFormFragment.this.openActivityWithFragmentForResult(FriendListFragment.class, R.string.projectEventMemberFormFragment_selectorField_hint_friend, null, GET_FRIEND_ID);
			}
		});	
		
		stateRadioGroup = (RadioGroup) getView().findViewById(R.id.projectEventMemberFormFragment_radioGroup_autoHide);
		cancelSignUpRadioButton = (RadioButton) getView().findViewById(R.id.projectEventMemberFormFragment_radioButton_cancelSignUp);
		unSignUpRadioButton = (RadioButton) getView().findViewById(R.id.projectEventMemberFormFragment_radioButton_unSignUp);
		signUpRadioButton = (RadioButton) getView().findViewById(R.id.projectEventMemberFormFragment_radioButton_signUp);
		unSignInRadioButton = (RadioButton) getView().findViewById(R.id.projectEventMemberFormFragment_radioButton_unSignIn);
		signInRadioButton = (RadioButton) getView().findViewById(R.id.projectEventMemberFormFragment_radioButton_signIn);
		if("SignIn".equals(eventMember.getState())) {
			signInRadioButton.setChecked(true);
		} else if("SignUp".equals(eventMember.getState())){
			signUpRadioButton.setChecked(true);
		} else if("UnSignIn".equals(eventMember.getState())){
			unSignInRadioButton.setChecked(true);
		} else if("UnSignUp".equals(eventMember.getState())){
			unSignUpRadioButton.setChecked(true);
		} else if("CancelSignUp".equals(eventMember.getState())){
			cancelSignUpRadioButton.setChecked(true);
		}
		
//		if(eventMember.getLocalFriendId() == null){
//			stateRadioGroup.setEnabled(false);
//			unSignUpRadioButton.setEnabled(false);
//			signUpRadioButton.setEnabled(false);
//			signInRadioButton.setEnabled(false);
//		}
//		button_cancel_signUp = (Button) getView().findViewById(R.id.button_cancel_signUp);
//		button_cancel_signUp.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				cancelSignUp();
//			}
//		});	
		mCheckBoxEventShareOwnerDataOnly = (CheckBox)getView().findViewById(R.id.projectEventMemberFormFragment_checkBox_shareAuthorization_self);
		mCheckBoxEventShareOwnerDataOnly.setChecked(eventMember.getEventShareOwnerDataOnly());
		
		mEventMemberNickName = (HyjTextField) getView().findViewById(R.id.projectEventMemberFormFragment_hyjTextField_nickName);
		mEventMemberNickName.setText(eventMember.getNickName());
		
		button_setting_nickName = (Button) getView().findViewById(R.id.projectEventMemberFormFragment_button_nickName);
		button_setting_nickName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENTMEMBERID", mEventMemberEditor.getModelCopy().get_mId());
				EventMemberSetNickNameDialogFragment.newInstance(bundle).show(getActivity().getSupportFragmentManager(), "EventMemberSetNickNameDialogFragment");
			}
		});	
		
		if (modelId == -1){
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			
			button_setting_nickName.setVisibility(View.GONE);
		}else{
//			if(!"UnSignUp".equals(eventMember.getState()) && HyjApplication.getInstance().getCurrentUser().getId().equals(eventMember.getFriendUserId())) {
//				button_cancel_signUp.setVisibility(View.VISIBLE);
//			}
			if(!mEventMemberEditor.getModel().getEvent().getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				getView().findViewById(R.id.button_save).setVisibility(View.GONE);
				button_setting_nickName.setVisibility(View.VISIBLE);
				stateRadioGroup.setEnabled(false);
				cancelSignUpRadioButton.setEnabled(false);
				unSignUpRadioButton.setEnabled(false);
				unSignInRadioButton.setEnabled(false);
				signUpRadioButton.setEnabled(false);
				signInRadioButton.setEnabled(false);
				mEventMemberNickName.setEnabled(false);
				mCheckBoxEventShareOwnerDataOnly.setEnabled(false);
				if(this.mOptionsMenu != null){
					hideSaveAction();
				}
			}
			oldState = mEventMemberEditor.getModel().getState();
//			else {
//				getView().findViewById(R.id.button_save).setVisibility(View.VISIBLE);
//			}
		}
		

		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(EventMember.class, null), true,
					mChangeObserver);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    if(mEventMemberEditor != null && !mEventMemberEditor.getModel().getEvent().getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
	    	hideSaveAction();
	    }
	}
	
	private void fillData() {
		EventMember modelCopy = (EventMember) mEventMemberEditor.getModelCopy();
//		modelCopy.setDate(mDateTimeFieldDate.getText());
//		modelCopy.setStartDate(mDateTimeFieldStartDate.getText());
//		modelCopy.setEndDate(mDateTimeFieldEndDate.getText());
//		modelCopy.setName(mTextFieldName.getText().toString().trim());
		if(mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) != null && (Boolean)mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) == false){
			modelCopy.setFriendUserId(mSelectorFieldFriend.getModelId());
			modelCopy.setLocalFriendId(null);
			if(modelCopy.getFriendUserId() != null){
				modelCopy.setFriendUserName(modelCopy.getFriend().getDisplayName());
			}
		} else if(mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) != null && (Boolean)mSelectorFieldFriend.getTag(TAG_MEMBER_IS_LOCAL_FRIEND) == true){
			modelCopy.setLocalFriendId(mSelectorFieldFriend.getModelId());
			modelCopy.setFriendUserId(null);
			if(modelCopy.getLocalFriendId() != null){
				modelCopy.setFriendUserName(modelCopy.getFriend().getDisplayName());
			}
		}
		modelCopy.setEventShareOwnerDataOnly(mCheckBoxEventShareOwnerDataOnly.isChecked());
		
		modelCopy.setNickName(mEventMemberNickName.getText().toString().trim());
		if(stateRadioGroup.getCheckedRadioButtonId() == R.id.projectEventMemberFormFragment_radioButton_cancelSignUp){
			modelCopy.setState("CancelSignUp");
		} else if(stateRadioGroup.getCheckedRadioButtonId() == R.id.projectEventMemberFormFragment_radioButton_unSignUp){
			modelCopy.setState("UnSignUp");
		} else if(stateRadioGroup.getCheckedRadioButtonId() == R.id.projectEventMemberFormFragment_radioButton_signUp){
			modelCopy.setState("SignUp");
		} else if(stateRadioGroup.getCheckedRadioButtonId() == R.id.projectEventMemberFormFragment_radioButton_unSignIn){
			modelCopy.setState("UnSignIn");
		} else if(stateRadioGroup.getCheckedRadioButtonId() == R.id.projectEventMemberFormFragment_radioButton_signIn){
			modelCopy.setState("SignIn");
		}
		
	}

	private void showValidatioErrors() {
		HyjUtil.displayToast(R.string.app_validation_error);
		mSelectorFieldFriend.setError(mEventMemberEditor.getValidationError("friendUser"));
	}

	@Override
	public void onSave(View v) {
		super.onSave(v);

		fillData();

		mEventMemberEditor.validate();
		if (mEventMemberEditor.hasValidationErrors()) {
			showValidatioErrors();
		} else{
			if(mEventMemberEditor.getModelCopy().get_mId()== null){
	//			ProjectShareAuthorization importFiendPSA = null;
	//			importFiendPSA = new Select()
	//					.from(ProjectShareAuthorization.class)
	//					.where("projectId=? and friendUserId=?",
	//							mEventMemberEditor.getModelCopy().getEvent().getProjectId(), mEventMemberEditor.getModelCopy().getFriendUserId())
	//					.executeSingle();
	//	        if(importFiendPSA == null){
	//	        	HyjUtil.displayToast(R.string.projectEventMemberFormFragment_toast_eventMember_add_projectAuthorization);
	//	        	Bundle bundle = new Bundle();
	//				bundle.putLong("PROJECT_ID", mEventMemberEditor.getModelCopy().getEvent().getProject().get_mId());
	//				bundle.putString("FRIEND_USERID", mEventMemberEditor.getModelCopy().getFriendUserId());
	//				openActivityWithFragment(MemberFormFragment.class, R.string.memberFormFragment_title_addnew, bundle);
	//	        } else {
				if(HyjApplication.getInstance().getCurrentUser().getId().equals(mEventMemberEditor.getModelCopy().getFriendUserId())){
					doSave();
				} else {
					sendNewEventMemberToServer();
					
	//				((HyjActivity) ProjectEventMemberFormFragment.this.getActivity()).dismissProgressDialog();
				}
	//	        }
			} else {
				doSave();
			}
			
		}
		
	}

	protected void doSave() {
		mEventMemberEditor.save();
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			if((oldState.equals("UnSignUp") || oldState.equals("CancelSignUp")) 
					&& (!mEventMemberEditor.getModelCopy().getState().equals("UnSignUp") && !mEventMemberEditor.getModelCopy().getState().equals("CancelSignUp"))) {
				mEventMemberEditor.getModelCopy().getEvent().setSignUpCount(mEventMemberEditor.getModelCopy().getEvent().getSignUpCount()+1);
				mEventMemberEditor.getModelCopy().getEvent().setSyncFromServer(true);
				mEventMemberEditor.getModelCopy().getEvent().save();
			} else if((!oldState.equals("UnSignUp") && !oldState.equals("CancelSignUp")) 
					&& (mEventMemberEditor.getModelCopy().getState().equals("UnSignUp") || mEventMemberEditor.getModelCopy().getState().equals("CancelSignUp"))) {
				mEventMemberEditor.getModelCopy().getEvent().setSignUpCount(mEventMemberEditor.getModelCopy().getEvent().getSignUpCount()-1);
				mEventMemberEditor.getModelCopy().getEvent().setSyncFromServer(true);
				mEventMemberEditor.getModelCopy().getEvent().save();
			}
		} else {
			if(!mEventMemberEditor.getModelCopy().getState().equals("UnSignUp") && !mEventMemberEditor.getModelCopy().getState().equals("CancelSignUp")) {
				mEventMemberEditor.getModelCopy().getEvent().setSignUpCount(mEventMemberEditor.getModelCopy().getEvent().getSignUpCount()+1);
				mEventMemberEditor.getModelCopy().getEvent().setSyncFromServer(true);
				mEventMemberEditor.getModelCopy().getEvent().save();
			}
		}
		
		HyjUtil.displayToast(R.string.app_save_success);
		getActivity().finish();
	}
	
	protected void doSaveServer() {
		mEventMemberEditor.save();
		HyjUtil.displayToast(R.string.app_save_success);
		getActivity().finish();
	}
	
	private double setAveragePercentage(ProjectShareAuthorization projectShareAuthorization) {
		//将成员设成平均分摊
		double fixedPercentageTotal = 0.0;
//		double averageTotal = 0.0;
		int numOfAverage = 0;
		List<ProjectShareAuthorization> mProjectShareAuthorizations;
		mProjectShareAuthorizations = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND state <> ? AND id <> ?", projectShareAuthorization.getProjectId(), "Delete", projectShareAuthorization.getId()).execute();
		mProjectShareAuthorizations.add(projectShareAuthorization);
		
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
				return averageAmount;
			} 
		}
		
		return adjsutedAverageAmount;
	}
	
	private void sendNewEventMemberToServer() {
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				loadProjectProjectShareAuthorizations(object);
				HyjUtil.displayToast(R.string.projectEventMemberFormFragment_toast_eventMember_add_success);
			}

			@Override
			public void errorCallback(Object object) {
				JSONObject json = (JSONObject) object;
				HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
				((HyjActivity) EventMemberFormFragment.this.getActivity()).dismissProgressDialog();
			}
		};
		String data = "[";
		
		JSONObject jsonEM = mEventMemberEditor.getModelCopy().toJSON();
		data += jsonEM.toString();
		if(mEventMemberEditor.getModelCopy().getFriendUserId() != null){
			jsonPSA = new Select().from(ProjectShareAuthorization.class).where("projectId=? and friendUserId=?",
				mEventMemberEditor.getModelCopy().getEvent().getProjectId(), mEventMemberEditor.getModelCopy().getFriendUserId()).executeSingle();
			
		} else if(mEventMemberEditor.getModelCopy().getLocalFriendId() != null){
			jsonPSA = new Select().from(ProjectShareAuthorization.class).where("projectId=? and localFriendId=?",
				mEventMemberEditor.getModelCopy().getEvent().getProjectId(), mEventMemberEditor.getModelCopy().getLocalFriendId()).executeSingle();
		}
		
		if(jsonPSA != null) {
			if (jsonPSA.isClientNew()) {
				data += "," + jsonPSA.toString();
			}
		} else {
			jsonPSA = new ProjectShareAuthorization();
			jsonPSA.setProjectShareMoneyExpenseAddNew(false);
			jsonPSA.setProjectShareMoneyExpenseDelete(false);
			jsonPSA.setProjectShareMoneyExpenseEdit(false);
			jsonPSA.setProjectShareMoneyExpenseOwnerDataOnly(true);
			jsonPSA.setShareAllSubProjects(false);
			jsonPSA.setSharePercentageType("Average");
			jsonPSA.setProjectId(mEventMemberEditor.getModelCopy().getEvent().getProjectId());
			jsonPSA.setState("Wait");
			if(mEventMemberEditor.getModelCopy().getFriendUserId() != null){
				jsonPSA.setFriendUserId(mEventMemberEditor.getModelCopy().getFriendUserId());
			} else if(mEventMemberEditor.getModelCopy().getLocalFriendId() != null){
				jsonPSA.setLocalFriendId(mEventMemberEditor.getModelCopy().getLocalFriendId());
			}
			jsonPSA.setFriendUserName(mEventMemberEditor.getModelCopy().getFriendDisplayName());
			jsonPSA.setSharePercentage(0.0);
			jsonPSA.save();
			if (jsonPSA.isClientNew()) {
				data += "," + jsonPSA.toJSON();
			}
		}
		
		//如果圈子也是新建的，一同保存到服务器
		if(mEventMemberEditor.getModelCopy().getEvent().getProject().isClientNew()){
			data += "," + mEventMemberEditor.getModelCopy().getEvent().getProject().toJSON().toString();
		}
		//如果活动也是新建的，一同保存到服务器
		if(mEventMemberEditor.getModelCopy().getEvent().isClientNew()){
			data += "," + mEventMemberEditor.getModelCopy().getEvent().toJSON().toString();
		}
		JSONObject msg = getInviteMessage();
		data += "," + msg.toString() + "]";
		
		HyjHttpPostAsyncTask.newInstance(serverCallbacks, data, "eventMemberAdd");
		((HyjActivity) EventMemberFormFragment.this.getActivity()).displayProgressDialog(R.string.inviteMemberMessageFormFragment_title_eventmember_addnew,R.string.inviteMemberMessageFormFragment_progress_eventmember_adding);
	}
	
	private JSONObject getInviteMessage() {
		JSONObject msg = new JSONObject();
		try {
			msg.put("__dataType", "Message");
			msg.put("toUserId", mEventMemberEditor.getModelCopy().getFriendUserId());
			msg.put("fromUserId", HyjApplication.getInstance().getCurrentUser().getId());
			msg.put("type", "Event.Member.AddRequest");
			msg.put("messageState", "new");
			msg.put("messageTitle", "邀请活动请求");
			msg.put("date", (new Date()).getTime());
			msg.put("detail", "用户"
					+ HyjApplication.getInstance().getCurrentUser()
							.getDisplayName() + "邀请您参加活动: " + mEventMemberEditor.getModelCopy().getEvent().getName());
			msg.put("ownerUserId", mEventMemberEditor.getModelCopy().getFriendUserId());
			
			JSONObject msgData = new JSONObject();
			msgData.put("fromUserDisplayName", HyjApplication.getInstance().getCurrentUser().getDisplayName());
			msgData.put("toUserDisplayName", mEventMemberEditor.getModelCopy().getFriend().getFriendUserName());
			msgData.put("eventMemberId", mEventMemberEditor.getModelCopy().getId());
			msgData.put("projectId", mEventMemberEditor.getModelCopy().getEvent().getProjectId());
			msgData.put("eventId", mEventMemberEditor.getModelCopy().getEventId());
			msgData.put("projectName", mEventMemberEditor.getModelCopy().getEvent().getProject().getName());
			msgData.put("eventName", mEventMemberEditor.getModelCopy().getEvent().getName());
			
//			if(jsonPSA.getState() == "Wait"){
				msgData.put("shareAllSubProjects", false);
				msgData.put("projectShareAuthorizationId", jsonPSA.getId());
				msgData.put("projectIds", new JSONArray("[" + mEventMemberEditor.getModelCopy().getEvent().getProjectId()  + "]"));
				msgData.put("projectCurrencyIds", new JSONArray("[" + mEventMemberEditor.getModelCopy().getEvent().getProject().getCurrencyId()  + "]"));
//			}
			
			msg.put("messageData", msgData.toString());
			
//			if(mEventMemberEditor.getModelCopy().getState().equals("UnSignUp")){
			if(mEventMemberEditor.getModelCopy().getFriendUserId() != null){
				// 该消息不会发给用户，只在服务器上做处理，所以没有id。在服务器上，没有id的消息是不会被保存的。
				msg.put("id", UUID.randomUUID().toString());
			}
		} catch (JSONException e) {
		}
		return msg;
	}
	
	protected void loadProjectProjectShareAuthorizations(Object object) {
		try {
			JSONArray jsonObjects = (JSONArray) object;
			ActiveAndroid.beginTransaction();
				for (int j = 0; j < jsonObjects.length(); j++) {
					if (jsonObjects.optJSONObject(j).optString("__dataType").equals("ProjectShareAuthorization")) {
						String id = jsonObjects.optJSONObject(j).optString("id");
						ProjectShareAuthorization newProjectShareAuthorization = HyjModel.getModel(ProjectShareAuthorization.class, id);
						if(newProjectShareAuthorization == null){
							newProjectShareAuthorization = new ProjectShareAuthorization();
						}
						newProjectShareAuthorization.loadFromJSON(jsonObjects.optJSONObject(j), true);
						newProjectShareAuthorization.save();
					} else if (jsonObjects.optJSONObject(j).optString("__dataType").equals("Event")) {
						String id = jsonObjects.optJSONObject(j).optString("id");
						Event newEvent = HyjModel.getModel(Event.class, id);
						if(newEvent == null){
							newEvent = new Event();
						}
						newEvent.loadFromJSON(jsonObjects.optJSONObject(j), true);
						newEvent.save();
					}
				}

			ActiveAndroid.setTransactionSuccessful();
			if(getActivity().getCallingActivity() != null){
				Intent data = new Intent();
				data.putExtra("MODELID", jsonPSA.getId());
				getActivity().setResult(Activity.RESULT_OK, data);
			}
//			getActivity().finish();
		} finally {
			ActiveAndroid.endTransaction();
		}
		doSaveServer();
		((HyjActivity) EventMemberFormFragment.this.getActivity()).dismissProgressDialog();
	}

//	protected void cancelSignUp() {
//		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
//			@Override
//			public void finishCallback(Object object) {
////				loadProjectProjectShareAuthorizations(object);
//				HyjUtil.displayToast(R.string.projectEventMemberFormFragment_eventMember_cancel_success);
//				loadEventAndMembers(object);
////				doSave();
//			}
//
//			@Override
//			public void errorCallback(Object object) {
//				JSONObject json = (JSONObject) object;
//				HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
//				((HyjActivity) EventMemberFormFragment.this.getActivity()).dismissProgressDialog();
//			}
//		};
//		HyjHttpPostAsyncTask.newInstance(serverCallbacks, "["+ mEventMemberEditor.getModelCopy().toJSON() +"]", "eventMemberUnSignUp");
//		((HyjActivity) EventMemberFormFragment.this.getActivity()).displayProgressDialog(R.string.projectEventMemberFormFragment_eventMember_cancel,R.string.projectEventMemberFormFragment_eventMember_canceling);
//	}
	
//	protected void loadEventAndMembers(Object object) {
//		try {
//			JSONArray jsonObjects = (JSONArray) object;
//			ActiveAndroid.beginTransaction();
//				for (int j = 0; j < jsonObjects.length(); j++) {
//					if (jsonObjects.optJSONObject(j).optString("__dataType").equals("EventMember")) {
//						String id = jsonObjects.optJSONObject(j).optString("id");
//						EventMember newEventMember = HyjModel.getModel(EventMember.class, id);
//						if(newEventMember == null){
//							newEventMember = new EventMember();
//						}
//						newEventMember.loadFromJSON(jsonObjects.optJSONObject(j), true);
//						newEventMember.save();
//					} else if (jsonObjects.optJSONObject(j).optString("__dataType").equals("Event")) {
//						String id = jsonObjects.optJSONObject(j).optString("id");
//						Event newEvent = HyjModel.getModel(Event.class, id);
//						if(newEvent == null){
//							newEvent = new Event();
//						}
//						newEvent.loadFromJSON(jsonObjects.optJSONObject(j), true);
//						newEvent.save();
//					}
//				}
//
//			ActiveAndroid.setTransactionSuccessful();
//		} finally {
//			ActiveAndroid.endTransaction();
//		}
//		getActivity().finish();
//		((HyjActivity) EventMemberFormFragment.this.getActivity()).dismissProgressDialog();
//	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case GET_FRIEND_ID:
	       	 if(resultCode == Activity.RESULT_OK){
	       		 long _id = data.getLongExtra("MODEL_ID", -1);
	       		 if(_id == -1){
	 	   	       		mSelectorFieldFriend.setText(null);
	 	   	       		mSelectorFieldFriend.setModelId(null);
					} else {
						String _type = data.getStringExtra("MODEL_TYPE");
						Friend friend = null;
						if("Friend".equals(_type)){
							friend = Friend.load(Friend.class, _id);
						} else if("ProjectShareAuthorization".equals(_type)){
							ProjectShareAuthorization psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
							friend = psa.getFriend();
						}
	         			 for(EventMember em : mEventMembers) {
		       				if(friend.getFriendUserId() != null){
		       					if(em.getFriendUserId() != null && em.getFriendUserId().equalsIgnoreCase(friend.getFriendUserId())){
		 	       					HyjUtil.displayToast(R.string.memberFormFragment_toast_friend_already_exists);
		 	       					return;
		 	       				}
		 	 	         	} else {
		 	       				if(em.getLocalFriendId() != null && em.getLocalFriendId().equalsIgnoreCase(friend.getId())){
		 	       					HyjUtil.displayToast(R.string.memberFormFragment_toast_friend_already_exists);
		 	       					return;
		       					}
		 	       			}
	         			 }
	         			if(friend.getFriendUserId() != null){
	         				mSelectorFieldFriend.setText(friend.getDisplayName());
	         				mSelectorFieldFriend.setModelId(friend.getFriendUserId());
	         				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, false);
	         				if(friend.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
	         					signUpRadioButton.setChecked(true);
	         				} else {
	         					unSignUpRadioButton.setChecked(true);
	         				}
//	         				unSignUpRadioButton.setEnabled(false);
//	         				signInRadioButton.setEnabled(false);
//	         				signUpRadioButton.setEnabled(false);
	         			} else {
	         				mSelectorFieldFriend.setText(friend.getDisplayName());
	         				mSelectorFieldFriend.setModelId(friend.getId());
	         				mSelectorFieldFriend.setTag(TAG_MEMBER_IS_LOCAL_FRIEND, true);
//	         				unSignUpRadioButton.setEnabled(true);
//	         				signInRadioButton.setEnabled(true);
//	         				signUpRadioButton.setEnabled(true);
	         				signUpRadioButton.setChecked(true);
	         			}
					}
	       	 }
	       	 break;
		}
	}

	private class ChangeObserver extends ContentObserver {
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if(mEventMemberEditor.getModel().get_mId() != null){
				EventMember eventMember = HyjModel.getModel(EventMember.class, mEventMemberEditor.getModel().getId());
				mEventMemberNickName.setText(eventMember.getNickName());
			}
		}
	}
	
	@Override
	public void onDestroy() {
		if (mChangeObserver != null) {
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mChangeObserver);
		}
	
		super.onDestroy();
	}
}
