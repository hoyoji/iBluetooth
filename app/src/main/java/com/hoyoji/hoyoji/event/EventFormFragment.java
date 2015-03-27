package com.hoyoji.hoyoji.event;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.fragment.HyjTextInputFormFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.map.PoiSearchDemo;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectFormFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;

public class EventFormFragment extends HyjUserFormFragment {
	private final static int GET_PROJECT_ID = 0;
	private static final int GET_REMARK = 1;
	private static final int CREATE_NEW_PROJECT_AND_SAVE = 2;
	private static final int GET_FINANCIALOWNER_ID = 3;
	private static final int GET_ADDRESS_MAP = 4;
	private static final int GET_ADDRESS = 5;

	private HyjModelEditor<Event> mEventEditor = null;
	private HyjTextField mTextFieldName = null;
//	private HyjTextField mProjectName = null;
	private HyjSelectorField mSelectorFieldProject;
	private HyjRemarkField mRemarkFieldDescription = null;
	
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjDateTimeField mDateTimeFieldStartDate = null;
	private HyjDateTimeField mDateTimeFieldEndDate = null;
	
	private Button cancelBtn = null;
	
	private boolean cancel = false;
	
	private HyjSelectorField mSelectorFieldFinancialOwner = null;
	private TextView mTextViewFinancialOwner;
	
	private Button button_cancel_signUp;
//	private ImageButton mButtonExpandMore;
//	private LinearLayout mLinearLayoutExpandMore;   
	private HyjRemarkField mHyjRemarkFieldAddress;
	private Button mButtonAddress;
	private double mLatitude;
	private double mLongitude;
	
	@Override
	public Integer useContentView() {
		return R.layout.event_formfragment_event;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		Event event;
		Project project = null;

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			event = new Select().from(Event.class).where("_id=?", modelId).executeSingle();
			project = event.getProject();
		} else {
			event = new Event();
			Long project_id = intent.getLongExtra("PROJECT_ID", -1);
			if(project_id != -1){
				project = Project.load(Project.class, project_id);
			} else {
				String projectId = intent.getStringExtra("PROJECTID");
				if(projectId != null) {
					project = Project.getModel(Project.class, projectId);
				}
			}
			if (project != null) {
				event.setProjectId(project.getId());
			}
		}
		mEventEditor = event.newModelEditor();
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.projectEventFormFragment_hyjDateTimeField_date);
		mDateTimeFieldDate.setEnabled(false);
		
		mTextFieldName = (HyjTextField) getView().findViewById(R.id.projectEventFormFragment_textField_name);
		mTextFieldName.setText(event.getName());
		
//		mProjectName = (HyjTextField) getView().findViewById(R.id.projectEventFormFragment_textField_projectName);
//		if (project != null) {
//			mProjectName.setText(project.getDisplayName());
//		}
//		mProjectName.setEnabled(false);
		
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.projectEventFormFragment_hyjSelectorField_projectName);

		if (project != null) {
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "("+ project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventFormFragment.this.openActivityWithFragmentForResult(
								ProjectListFragment.class,
								R.string.projectListFragment_title_select_project,
								null, GET_PROJECT_ID);
			}
		});
		
		mDateTimeFieldStartDate = (HyjDateTimeField) getView().findViewById(R.id.projectEventFormFragment_hyjDateTimeField_startDate);
		mDateTimeFieldStartDate.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if(mDateTimeFieldStartDate.getTime() >= mDateTimeFieldEndDate.getTime()){
					mDateTimeFieldEndDate.setTime(mDateTimeFieldStartDate.getTime() + 2*60*60*1000);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		mDateTimeFieldEndDate = (HyjDateTimeField) getView().findViewById(R.id.projectEventFormFragment_hyjDateTimeField_endDate);
		
		mSelectorFieldFinancialOwner = (HyjSelectorField) getView().findViewById(R.id.projectFormFragment_selectorField_financialOwner);
//		mSelectorFieldFinancialOwner.setEnabled(editPermission);
		if(event.getFinancialOwnerUserId() != null){
			mSelectorFieldFinancialOwner.setModelId(event.getFinancialOwnerUserId());
			mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(event.getFinancialOwnerUserId()));
		}
		
		mSelectorFieldFinancialOwner.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mEventEditor.getModel().get_mId() == null){
					if(mSelectorFieldFinancialOwner.getModelId() != null){
						mSelectorFieldFinancialOwner.setModelId(null);
						mSelectorFieldFinancialOwner.setText(null);
					} else {
						mSelectorFieldFinancialOwner.setModelId(HyjApplication.getInstance().getCurrentUser().getId());
						mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(HyjApplication.getInstance().getCurrentUser().getId()));
					}
				} else {
					Bundle bundle = new Bundle();
					Event event = HyjModel.getModel(Event.class,mEventEditor.getModelCopy().getId());
					bundle.putLong("MODEL_ID", event.getProject().get_mId());
					bundle.putString("NULL_ITEM", (String)mSelectorFieldFinancialOwner.getHint());
					bundle.putBoolean("disableMultiChoiceMode", true);
					openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.projectFormFragment_textView_financialOwner, bundle, GET_FINANCIALOWNER_ID);
				}
			}
		}); 
		
		mTextViewFinancialOwner = (TextView) getView().findViewById(R.id.projectFormFragment_textView_hint_financialOwner);
		mTextViewFinancialOwner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("FINANCIAL_TYPE", "Project");
				
				EventFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		mLatitude = mEventEditor.getModel().getLatitude();
		mLongitude = mEventEditor.getModel().getLongitude();
		mHyjRemarkFieldAddress = (HyjRemarkField) getView().findViewById(R.id.projectEventFormFragment_textView_address);
		if(mEventEditor.getModel().getAddress() != null && !"".equals(mEventEditor.getModel().getAddress())) {
			mHyjRemarkFieldAddress.setText(mEventEditor.getModel().getAddress());
		}
		mHyjRemarkFieldAddress.setEditable(false);
		mHyjRemarkFieldAddress.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mHyjRemarkFieldAddress.getText());
				bundle.putString("HINT", "请输入" + mHyjRemarkFieldAddress.getLabelText());
				EventFormFragment.this
						.openActivityWithFragmentForResult(
								HyjTextInputFormFragment.class,
								R.string.projectEventFormFragment_hyjRemarkField_address,
								bundle, GET_ADDRESS);
			}
		});
//		
//		final boolean isOwnerProject;
//		if(project != null && project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//			isOwnerProject = true;
//		} else {
//			isOwnerProject = false;
//		}
		mButtonAddress = (Button) getView().findViewById(R.id.projectEventFormFragment_button_address);
		mButtonAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(),PoiSearchDemo.class);
//				startActivity(intent);
				Bundle bundle = new Bundle();
				bundle.putString("ADDRESS", mHyjRemarkFieldAddress.getText().toString().trim());
				double getLatitude = mLatitude;
				double getLongitude = mLongitude;
				if (getLatitude != 0) {
					bundle.putDouble("LATITUDE", getLatitude);
				}
				if (getLongitude != 0) {
					bundle.putDouble("LONGITUDE", getLongitude);
				}
				String projectId = mSelectorFieldProject.getModelId();
				if(projectId == null){
					bundle.putBoolean("ISOWNERPROJECT", true);
				} else {
					Project project = Project.getModel(Project.class, projectId);
					if(project != null && project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
						bundle.putBoolean("ISOWNERPROJECT", true);
					} else {
						bundle.putBoolean("ISOWNERPROJECT", false);
					}
				}
					
				
				openActivityWithFragmentForResult(PoiSearchDemo.class, R.string.demo_name_basemap, bundle, GET_ADDRESS_MAP);
			}
		});
		
		button_cancel_signUp = (Button) getView().findViewById(R.id.button_cancel_signUp);
		button_cancel_signUp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				cancelSignUp();
			}
		});	
		
		if (modelId != -1) {
			mDateTimeFieldDate.setTime(event.getDate());
			mDateTimeFieldStartDate.setTime(event.getStartDate());
			mDateTimeFieldEndDate.setTime(event.getEndDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
				mDateTimeFieldStartDate.setDate(date);
				mDateTimeFieldStartDate.setTextColor(Color.RED);
				mDateTimeFieldEndDate.setTime(date.getTime()+2*60*60*1000);
				mDateTimeFieldEndDate.setTextColor(Color.RED);
			} else {
//				Date date = new Date();
//				mDateTimeFieldDate.setDate(date);
//				mDateTimeFieldEndDate.setTime(date.getTime()+2*60*60*1000);
				
				Calendar now = Calendar.getInstance();
				now.setTime(new Date());
//				now.set(Calendar.HOUR_OF_DAY, 0);
				now.set(Calendar.MILLISECOND, 0);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
//				now.getTimeInMillis();
				
				mDateTimeFieldStartDate.setTime(now.getTimeInMillis());
				mDateTimeFieldEndDate.setTime(now.getTimeInMillis()+2*60*60*1000);

			}
//			mDateTimeFieldEndDate.setDate(null);
		}

		mRemarkFieldDescription = (HyjRemarkField) getView().findViewById(R.id.projectEventFormFragment_HyjRemarkField_description);
		
		mRemarkFieldDescription.setEditable(false);
		mRemarkFieldDescription.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldDescription.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldDescription.getLabelText());
				EventFormFragment.this.openActivityWithFragmentForResult(
								HyjTextInputFormFragment.class,
								R.string.projectEventFormFragment_hyjRemarkField_hint_description,
								bundle, GET_REMARK);
			}
		});
		
//		mLinearLayoutExpandMore = (LinearLayout)getView().findViewById(R.id.moneyExpenseContainerFormFragment_expandMore);
//		mButtonExpandMore = (ImageButton)getView().findViewById(R.id.expand_more);
//		mButtonExpandMore.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(mLinearLayoutExpandMore.getVisibility() == View.GONE){
//					mButtonExpandMore.setImageResource(R.drawable.ic_action_collapse);
//					mLinearLayoutExpandMore.setVisibility(View.VISIBLE);
//				} else {
//					mButtonExpandMore.setImageResource(R.drawable.ic_action_expand);
//					mLinearLayoutExpandMore.setVisibility(View.GONE);
//				}
//			}
//		});
		
		
		if(modelId != -1){
			EventMember eventMember = new Select().from(EventMember.class).where("eventId = ? AND friendUserId = ?", event.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
			if(eventMember != null && (!"UnSignUp".equals(eventMember.getState()) || !"CancelSignUp".equals(eventMember.getState()))){
				button_cancel_signUp.setVisibility(View.VISIBLE);
			}
			if("Cancel".equals(event.getState())){
				cancel = true;
			}
			if(project != null && project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId()) && !"Cancel".equals(event.getState())) {
				cancelBtn = (Button) getView().findViewById(R.id.button_event_cancel);
				cancelBtn.setVisibility(View.VISIBLE);
				cancelBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						cancelEvent();
					}
				});
			}
			
			mRemarkFieldDescription.setText(event.getDescription());
			mSelectorFieldProject.setEnabled(false);
			if(!mEventEditor.getModel().getProject().getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				mDateTimeFieldEndDate.setEnabled(false);
				mDateTimeFieldStartDate.setEnabled(false);
				mTextFieldName.setEnabled(false);
				mSelectorFieldFinancialOwner.setEnabled(false);
				getView().findViewById(R.id.button_save).setVisibility(View.GONE);
				if(this.mOptionsMenu != null){
					hideSaveAction();
				}
			}
		} else {
			mRemarkFieldDescription.setText("小伙伴们，好久不见了，一起聚聚吧！\n\n地点：老地方\n费用：AA\n其他：可以带家属\n\n温馨提示：喝酒的就别开车了");
//			mButtonExpandMore.setImageResource(R.drawable.ic_action_collapse);
//			mLinearLayoutExpandMore.setVisibility(View.VISIBLE);
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    
	}
	
	private void fillData() {
		Event modelCopy = (Event) mEventEditor.getModelCopy();
		modelCopy.setProjectId(mSelectorFieldProject.getModelId());
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setStartDate(mDateTimeFieldStartDate.getTime());
		modelCopy.setEndDate(mDateTimeFieldEndDate.getTime());
		modelCopy.setName(mTextFieldName.getText().toString().trim());
		modelCopy.setDescription(mRemarkFieldDescription.getText().toString().trim());
		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
		modelCopy.setAddress(mHyjRemarkFieldAddress.getText().toString().trim());
		modelCopy.setLatitude(mLatitude);
		modelCopy.setLongitude(mLongitude);
		if(cancel == true) {
			modelCopy.setState("Cancel");
		} else {
			modelCopy.setState("Normal");
		}
	}

	private void showValidatioErrors() {
		HyjUtil.displayToast(R.string.app_validation_error);

		mSelectorFieldProject.setError(mEventEditor.getValidationError("project"));
		mTextFieldName.setError(mEventEditor.getValidationError("name"));
		mDateTimeFieldStartDate.setError(mEventEditor.getValidationError("startDate"));
		mDateTimeFieldEndDate.setError(mEventEditor.getValidationError("endDate"));
		mRemarkFieldDescription.setError(mEventEditor.getValidationError("description"));
	}

	@Override
	public void onSave(View v) {
		super.onSave(v);

		fillData();

		mEventEditor.validate();
		if (mEventEditor.hasValidationErrors()) {
			showValidatioErrors();
		} else {
//			if(cancel == true) {
//				cancelEvent();
//			} else {
			if(mEventEditor.getModelCopy().getProjectId() == null){
				EventFormCreateProjectDialogFragment createProjectDialog = EventFormCreateProjectDialogFragment.newInstance(null); 
				((HyjActivity)getActivity()).displayDialog(createProjectDialog,
						new DialogCallbackListener() {
							@Override
							public void doPositiveClick(Object object) {
								Bundle bundle = new Bundle();
								bundle.putString("PROJECT_NAME", mEventEditor.getModelCopy().getName());
								openActivityWithFragmentForResult(ProjectFormFragment.class, R.string.projectFormFragment_title_addnew, bundle, CREATE_NEW_PROJECT_AND_SAVE);
							}
							@Override
							public void doNeutralClick() {
								EventFormFragment.this.openActivityWithFragmentForResult(
										ProjectListFragment.class,
										R.string.projectListFragment_title_select_project,
										null, GET_PROJECT_ID);
							}
						});
				return;
			}
			Intent intent = getActivity().getIntent();
			Long modelId = intent.getLongExtra("MODEL_ID", -1);
			if (modelId == -1) {
				Friend toBeDeterminedFriend = new Select().from(Friend.class).where("toBeDetermined = 1 AND ownerUserId = ?", HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
				if(toBeDeterminedFriend != null){
					EventMember toBeDeterminedFriendEM = new EventMember();
					toBeDeterminedFriendEM.setEventId(mEventEditor.getModelCopy().getId());
					toBeDeterminedFriendEM.setState("SignUp");
					toBeDeterminedFriendEM.setFriendUserId(null);
					toBeDeterminedFriendEM.setLocalFriendId(toBeDeterminedFriend.getId());
					toBeDeterminedFriendEM.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
					toBeDeterminedFriendEM.setFriendUserName("待定成员");
					toBeDeterminedFriendEM.setToBeDetermined(true);
					toBeDeterminedFriendEM.setEventShareOwnerDataOnly(false);
					toBeDeterminedFriendEM.save();
				}
				
				EventMember currentUserEM= new EventMember();
				currentUserEM.setEventId(mEventEditor.getModelCopy().getId());
				currentUserEM.setState("UnSignUp");
				currentUserEM.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
				currentUserEM.setLocalFriendId(null);
				currentUserEM.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
				currentUserEM.setFriendUserName(HyjApplication.getInstance().getCurrentUser().getDisplayName());
				currentUserEM.setToBeDetermined(false);
				currentUserEM.setEventShareOwnerDataOnly(false);
				currentUserEM.save();
			}
			doSave();
//			}
		}
//		if(mMoneyAccountEditor.getModelCopy().getAccountType().equalsIgnoreCase("Topup")){
//			if(mMoneyAccountEditor.getModelCopy().getFriendId() == null){
//				mMoneyAccountEditor.setValidationError("friend", R.string.moneyAccountFormFragment_editText_hint_friend);
//			} else {
//				mMoneyAccountEditor.removeValidationError("friend");
//			}
//		} else {
//			mMoneyAccountEditor.removeValidationError("friend");
//		}
		
	}

	protected void doSave() {
		mEventEditor.save();
		HyjUtil.displayToast(R.string.app_save_success);
		getActivity().finish();
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId == -1) {
			Bundle bundle = new Bundle();
			bundle.putLong("MODEL_ID", mEventEditor.getModel().get_mId());
				openActivityWithFragment(EventViewPagerFragment.class,
						R.string.projectEventMemberViewPagerFragment_title,
						bundle);
		}
	}
//	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case GET_REMARK:
				if (resultCode == Activity.RESULT_OK) {
					String text = data.getStringExtra("TEXT");
					mRemarkFieldDescription.setText(text);
				}
				break;
			case GET_ADDRESS:
				if (resultCode == Activity.RESULT_OK) {
					String text = data.getStringExtra("TEXT");
					mHyjRemarkFieldAddress.setText(text);
					mLatitude = 0.0;
					mLongitude = 0.0;
				}
				break;
			case GET_PROJECT_ID:
				if (resultCode == Activity.RESULT_OK) {
					long _id = data.getLongExtra("MODEL_ID", -1);
					Project project = Project.load(Project.class, _id);
					if(project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
						mSelectorFieldProject.setModelId(project.getId());
					} else {
						HyjUtil.displayToast(R.string.projectEventFormFragment_validate_project);
					}
				}
				break;
			case GET_FINANCIALOWNER_ID:
		       	 if(resultCode == Activity.RESULT_OK){
		       		long _id = data.getLongExtra("MODEL_ID", -1);
		       		if(_id == -1){
			       		mSelectorFieldFinancialOwner.setText(null);
			       		mSelectorFieldFinancialOwner.setModelId(null);
		       		} else {
			       		ProjectShareAuthorization psa = HyjModel.load(ProjectShareAuthorization.class, _id);
		
			       		if(psa == null){
							HyjUtil.displayToast(R.string.projectFormFragment_editText_error_financialOwner_must_be_member);
							return;
			       		} else if(psa.getFriendUserId() == null){
							HyjUtil.displayToast(R.string.projectFormFragment_editText_error_financialOwner_cannot_local);
							return;
			       		} else if(!psa.getState().equalsIgnoreCase("Accept")){
							HyjUtil.displayToast(R.string.projectFormFragment_editText_error_financialOwner_must_be_accepted_member);
							return;
			       		} else if(psa.getProjectShareMoneyExpenseOwnerDataOnly() == true){
							HyjUtil.displayToast(R.string.projectFormFragment_editText_error_financialOwner_must_has_all_auth);
							return;
			       		}
			       		
			       		mSelectorFieldFinancialOwner.setText(psa.getFriendDisplayName());
			       		mSelectorFieldFinancialOwner.setModelId(psa.getFriendUserId());
		       		}
		       	 }
		       	 break;
			case CREATE_NEW_PROJECT_AND_SAVE:
				if (resultCode == Activity.RESULT_OK) {
					long _id = data.getLongExtra("MODEL_ID", -1);
					Project project = Project.load(Project.class, _id);
					if(project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
						mSelectorFieldProject.setModelId(project.getId());
						onSave();
					} else {
						HyjUtil.displayToast(R.string.projectEventFormFragment_validate_project);
					}
				}
				break;
			case GET_ADDRESS_MAP:
				if (resultCode == Activity.RESULT_OK) {
					double latitude = data.getDoubleExtra("LATITUDE", -1);
					double longitude = data.getDoubleExtra("LONGITUDE", -1);
					String address = data.getStringExtra("ADDRESS");
					if(latitude != -1){
						mLatitude = latitude;
					}
					if(longitude != -1){
						mLongitude = longitude;
					}
					if(address != null && !"".equals(address)) {
						mHyjRemarkFieldAddress.setText(address);
					}
					
				}
				break;
		}
	}
	
	protected void cancelSignUp() {
		try {
			HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
				@Override
				public void finishCallback(Object object) {
	//				loadProjectProjectShareAuthorizations(object);
					HyjUtil.displayToast(R.string.projectEventMemberFormFragment_eventMember_cancel_success);
					loadEventAndMembers(object);
	//				doSave();
				}
	
				@Override
				public void errorCallback(Object object) {
					JSONObject json = (JSONObject) object;
					HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
					((HyjActivity) EventFormFragment.this.getActivity()).dismissProgressDialog();
				}
			};
			JSONObject evt = new JSONObject();
			evt.put("eventId", mEventEditor.getModelCopy().getId());
			
			HyjHttpPostAsyncTask.newInstance(serverCallbacks, "["+ evt.toString() +"]", "eventMemberUnSignUp");
			((HyjActivity) EventFormFragment.this.getActivity()).displayProgressDialog(R.string.projectEventMemberFormFragment_eventMember_cancel,R.string.projectEventMemberFormFragment_eventMember_canceling);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
	
	protected void loadEventAndMembers(Object object) {
		try {
			JSONArray jsonObjects = (JSONArray) object;
			ActiveAndroid.beginTransaction();
				for (int j = 0; j < jsonObjects.length(); j++) {
					if (jsonObjects.optJSONObject(j).optString("__dataType").equals("EventMember")) {
						String id = jsonObjects.optJSONObject(j).optString("id");
						EventMember newEventMember = HyjModel.getModel(EventMember.class, id);
						if(newEventMember == null){
							newEventMember = new EventMember();
						}
						newEventMember.loadFromJSON(jsonObjects.optJSONObject(j), true);
						newEventMember.save();
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
		} finally {
			ActiveAndroid.endTransaction();
		}
		getActivity().finish();
		((HyjActivity) EventFormFragment.this.getActivity()).dismissProgressDialog();
	}
	
	protected void cancelEvent() {
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				loadEventMembers(object);
			}

			@Override
			public void errorCallback(Object object) {
				JSONObject json = (JSONObject) object;
				HyjUtil.displayToast(json.optJSONObject("__summary").optString("msg"));
				((HyjActivity) EventFormFragment.this.getActivity()).dismissProgressDialog();
			}
		};
		HyjHttpPostAsyncTask.newInstance(serverCallbacks, "["+ mEventEditor.getModelCopy().toJSON() +"]", "eventCancel");
		((HyjActivity) EventFormFragment.this.getActivity()).displayProgressDialog(R.string.app_action_event_cancel,R.string.app_action_event_canceling);
	}
	
	protected void loadEventMembers(Object object) {
		try {
			JSONArray jsonObjects = (JSONArray) object;
			ActiveAndroid.beginTransaction();
				for (int j = 0; j < jsonObjects.length(); j++) {
					if (jsonObjects.optJSONObject(j).optString("__dataType").equals("EventMember")) {
						String id = jsonObjects.optJSONObject(j).optString("id");
						EventMember newEventMember = HyjModel.getModel(EventMember.class, id);
						if(newEventMember == null){
							newEventMember = new EventMember();
						}
						newEventMember.loadFromJSON(jsonObjects.optJSONObject(j), true);
						newEventMember.save();
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
		} finally {
			ActiveAndroid.endTransaction();
		}
		((HyjActivity) EventFormFragment.this.getActivity()).dismissProgressDialog();
		getActivity().finish();
	}
}
