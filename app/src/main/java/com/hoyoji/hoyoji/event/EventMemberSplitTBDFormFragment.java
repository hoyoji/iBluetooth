package com.hoyoji.hoyoji.event;

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
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyApportion;
import com.hoyoji.hoyoji.models.MoneyExpenseApportion;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.money.MoneyApportionField;
import com.hoyoji.hoyoji.money.SelectApportionEventMemberListFragment;
import com.hoyoji.hoyoji.money.MoneyApportionField.ApportionItem;
import com.hoyoji.hoyoji.project.ProjectMemberFormFragment;
import com.hoyoji.hoyoji.project.ProjectMoneyTBDListFragment;


public class EventMemberSplitTBDFormFragment extends HyjUserFormFragment {

	protected static final int GET_APPORTION_MEMBER_ID = 0;
	private static final int ADD_AS_PROJECT_MEMBER = 1;
	protected static final int ADD_AS_EVENT_MEMBER = 0;

	EventMember mEventMember;
	Event mEvent;
	private HyjTextField mTextFieldProjectName = null;

	private MoneyApportionField mApportionFieldApportions;
	
	@Override
	public Integer useContentView() {
		return R.layout.event_formfragment_member_tbd;
	}
	
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		mEventMember =  EventMember.load(EventMember.class, modelId);
		mEvent = mEventMember.getEvent();
		
		boolean _canNotEdit = false;
		if(!mEventMember.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
			_canNotEdit = true;
		}
		final boolean canNotEdit = _canNotEdit;
		
//		mProjectShareAuthorizations = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND state <> ? AND id <> ?", project.getId(), "Delete", projectShareAuthorization.getId()).execute();
//		mProjectShareAuthorizations.add(projectShareAuthorization);
		
		mTextFieldProjectName = (HyjTextField) getView().findViewById(R.id.eventMemberTBDFormFragment_textField_eventName);
		mTextFieldProjectName.setText(mEvent.getName());
		mTextFieldProjectName.setEnabled(false);
		
		mApportionFieldApportions = (MoneyApportionField) getView().findViewById(R.id.eventMemberTBDFormFragment_apportionField);
		mApportionFieldApportions.setHideMoney(true);
		getView().findViewById(R.id.eventMemberTBDFormFragment_imageButton_apportion_add).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				Event event = mEventMember.getEvent();
				bundle.putLong("MODEL_ID", event.getProject().get_mId());
				if(!event.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					openActivityWithFragmentForResult(EventMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
				} else {
					bundle.putString("EVENTID", event.getId());
					openActivityWithFragmentForResult(SelectApportionEventMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
				}
			}
		});

		getView().findViewById(R.id.eventMemberTBDFormFragment_imageButton_apportion_add_all).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addAllEventMemberIntoApportionsField(mEventMember.getEvent());
			}
		});
		
		getView().findViewById(R.id.eventMemberTBDFormFragment_imageButton_apportion_more_actions).setOnClickListener(new OnClickListener() {
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

		
		getView().findViewById(R.id.eventMemberTBDFormFragment_button_transactions).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", mEvent.get_mId());
				bundle.putString("LOCAL_FRIENDID", mEventMember.getLocalFriendId());
				openActivityWithFragment(ProjectMoneyTBDListFragment.class, R.string.eventMemberTBDFormFragment_title_transactions, bundle);
			}
		});
		
		if(canNotEdit){
			getView().findViewById(R.id.button_save).setVisibility(View.GONE);
			if(this.mOptionsMenu != null){
				hideSaveAction();
			}
		}
	}
	
	private void addAllEventMemberIntoApportionsField(Event event) {
		List<EventMember> eventMembers = event.getEventMembers();
		for (int i = 0; i < eventMembers.size(); i++) {
			if(eventMembers.get(i).getToBeDetermined()){
				continue;
			}
			MoneyExpenseApportion apportion = new MoneyExpenseApportion();
			apportion.setAmount(0.0);
			apportion.setFriendUserId(eventMembers.get(i).getFriendUserId());
			apportion.setLocalFriendId(eventMembers.get(i).getLocalFriendId());
			ProjectShareAuthorization projectShareAuthorization = eventMembers.get(i).getProjectShareAuthorization();
			if(projectShareAuthorization.getSharePercentageType() != null && projectShareAuthorization.getSharePercentageType().equals("Average")){
				apportion.setApportionType("Average");
			} else {
				apportion.setApportionType("Share");
			}
			mApportionFieldApportions.addApportion(apportion, projectShareAuthorization.getProjectId(), event.getId(), ApportionItem.NEW);
//			mApportionFieldApportions.setTotalAmount(0.0);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(mEventMember != null && mEventMember.get_mId() != null){
				boolean canNotEdit = false;
				if(!mEventMember.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
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
		
		if(!mEventMember.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
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
					R.string.eventMemberTBDFormFragment_title_split,
					R.string.eventMemberTBDFormFragment_progress_uploading_data);
			MainActivity.uploadData(false, getActivity(), null, new HyjAsyncTaskCallbacks(){
				@Override
				public void finishCallback(Object object) {
					((HyjActivity) getActivity())
					.displayProgressDialog(
							R.string.eventMemberTBDFormFragment_title_split,
							R.string.eventMemberTBDFormFragment_progress_splitting);
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
					R.string.eventMemberTBDFormFragment_title_split,
					R.string.eventMemberTBDFormFragment_progress_splitting);
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
					HyjUtil.displayToast(R.string.eventMemberTBDFormFragment_toast_split_success);
					MainActivity.uploadData(true, getActivity(), null, null);
				}

				@Override
				public void errorCallback(Object object) {
					displayError(object);
				}
			};

			
			JSONObject data = new JSONObject();
			try {
				data.put("eventId", mEventMember.getEventId());
				data.put("tbdFriendId", mEventMember.getLocalFriendId());
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
// 					addAsProjectMember(psa, mEventMember.getEventId());
 					AddApportionMember("ProjectShareAuthorization", psa.get_mId().longValue(), new long[]{});
 				} else {
 					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
 				}
 			} else {
 				HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
 				
 			}
 			break;
          }
    }

	private void AddApportionMember(final String type, final long _id, final long _ids[]) {
			ProjectShareAuthorization psa = null;
			if("EventMember".equalsIgnoreCase(type)){
				EventMember eventMember = HyjModel.load(EventMember.class, _id);
				psa = eventMember.getProjectShareAuthorization();
			} else if("ProjectShareAuthorization".equalsIgnoreCase(type)){
				psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
			} else {
				final Friend friend = Friend.load(Friend.class, _id);
				//看一下该好友是不是圈子成员
				if(friend.getFriendUserId() != null){
					psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", friend.getFriendUserId(), mEventMember.getProjectId()).executeSingle();
				} else {
					psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", friend.getId(), mEventMember.getProjectId()).executeSingle();
				}
				
				if(psa == null){
					((HyjActivity)getActivity()).displayDialog(R.string.moneyApportionField_select_toast_apportion_user_not_member, R.string.moneyApportionField_select_confirm_apportion_add_as_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									Bundle bundle = new Bundle();
									bundle.putString("PROJECTID", mEventMember.getProjectId());
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
				EventMember em = null;
				if (psa.getFriendUserId() != null) {
					em = new Select().from(EventMember.class).where("friendUserId=? AND eventId=?", psa.getFriendUserId(), mEvent.getId()).executeSingle();
				} else {
					em = new Select().from(EventMember.class).where("localFriendId=? AND eventId=?", psa.getLocalFriendId(), mEvent.getId()).executeSingle();
				}
				if(em == null){
					final ProjectShareAuthorization psaCopy = psa;
					
					((HyjActivity)getActivity()).displayDialog(
							psa.getFriendDisplayName() + " " +  getString(R.string.moneyApportionField_select_toast_apportion_user_not_event_member), 
							getString(R.string.moneyApportionField_select_confirm_apportion_add_as_event_member), 
							R.string.alert_dialog_yes, R.string.alert_dialog_no, -1, 
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									Bundle bundle = new Bundle();
									bundle.putLong("EVENT_ID", mEvent.get_mId());
									if(psaCopy.getFriendUserId() != null){
										bundle.putString("FRIEND_USERID", psaCopy.getFriendUserId());
									} else {
										bundle.putString("LOCAL_FRIENDID", psaCopy.getLocalFriendId());
									}
									
									openActivityWithFragmentForResult(EventMemberFormFragment.class, R.string.moneyApportionField_moreActions_event_member_add, bundle, ADD_AS_PROJECT_MEMBER);
									if(_ids.length > 0){
										AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
									}
								}
		
								@Override
								public void doNegativeClick() {
									if(_ids.length > 0){
										AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
									} else {
										HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_event_member);
									}
								}
							});
					return;
				}
			addAsProjectMember(psa, mEventMember.getEventId(), type, _ids);
		
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
		if (mApportionFieldApportions.addApportion(apportion,mEventMember.getProjectId(), eventId, ApportionItem.NEW)) {
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
