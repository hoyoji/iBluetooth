package com.hoyoji.hoyoji.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.User;

public class EventMemberListFragment extends HyjUserListFragment {
	// private IWXAPI api;
	// private QQShare mQQShare = null;
	// public static QQAuth mQQAuth;
	private Button mAllEventMember;
	private Button mCancelSignUpEventMember;
	private Button mSignUpEventMember;
	private Button mUnSignInEventMember;
	private Button mSignInEventMember;
	private boolean mIsSelectCancelSignUpEventMembers = false;
	private boolean mIsSelectSignUpEventMembers = false;
	private boolean mIsSelectUnSignInEventMembers = false;
	private boolean mIsSelectSignInEventMembers = false;

	private List<EventMember> mMemberList = new ArrayList<EventMember>();

	@Override
	public Integer useContentView() {
		return R.layout.project_listfragment_event_member;
	}

	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}

	@Override
	public Integer useOptionsMenuView() {
		return R.menu.project_listfragment_event_member;
	}

	public Integer useMultiSelectMenuPickerView() {
		return R.menu.multi_select_menu_picker;
		// return null;
	}

//	@Override
//	protected View useHeaderView(Bundle savedInstanceState) {
//		
//
//
//		return null;
//	}

	@Override
	public ListAdapter useListViewAdapter() {
		// return new SimpleCursorAdapter(getActivity(),
		// R.layout.home_listitem_row,
		// null,
		// new String[] {"friendUserId", "id", "id", "id", "id"},
		// new int[] {R.id.homeListItem_picture, R.id.homeListItem_title,
		// R.id.homeListItem_subTitle, R.id.homeListItem_amount,
		// R.id.homeListItem_remark},
		// 0);

		MemberListAdapter adapter = new MemberListAdapter(getActivity(),
				mMemberList, R.layout.home_listitem_row, new String[] {
						"friendUserId", "_id", "id", "id", "id", "id" },
				new int[] { R.id.homeListItem_picture, R.id.homeListItem_title,
						R.id.homeListItem_subTitle, R.id.homeListItem_amount,
						R.id.homeListItem_remark, R.id.homeListItem_owner });
		return adapter;
	}

	@Override
	public Integer useMultiSelectMenuView() {
		return R.menu.project_listfragment_eventmember_multi_select;
	}

	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		super.onCreateLoader(arg0, arg1);
		int offset = arg1.getInt("OFFSET");
		int limit = arg1.getInt("LIMIT");
		if (limit == 0) {
			limit = getListPageSize();
		}
		// Intent intent = getActivity().getIntent();
		// Long modelId = intent.getLongExtra("MODEL_ID", -1);
		// Event event;
		// String eventId = null;
		// if(getActivity().getCallingActivity() != null){
		// // Project project = Model.load(Project.class, modelId);
		// // event = new Select().from(Event.class).where("projectId=?",
		// project.getId()).executeSingle();
		// eventId = intent.getStringExtra("EVENTID");
		// } else {
		// event = Model.load(Event.class, modelId);
		// eventId = event.getId();
		// }
		// String selection = "eventId = ?";
		// String[] selectionArgs = new String[]{eventId};
		//
		// if(mIsSelectSignUpEventMembers == true){
		// selection = selection + " and state = ?";
		// selectionArgs = new String[]{eventId,"SignUp"};
		// }
		//
		// if(mIsSelectUnSignInEventMembers == true){
		// selection = selection + " and state = ?";
		// selectionArgs = new String[]{eventId,"UnSignIn"};
		// }
		//
		// if(mIsSelectSignInEventMembers == true){
		// selection = selection + " and state = ?";
		// selectionArgs = new String[]{eventId,"SignIn"};
		// }
		// Object loader = new CursorLoader(getActivity(),
		// ContentProvider.createUri(EventMember.class, null),
		// null,
		// selection,
		// selectionArgs,
		// "friendUserName LIMIT " + (limit + offset)
		// );
		//
		arg1.putInt("LIMIT", limit + offset);

		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		Event event;
		String eventId = null;
		if (getActivity().getCallingActivity() != null) {
			// Project project = Model.load(Project.class, modelId);
			// event = new Select().from(Event.class).where("projectId=?",
			// project.getId()).executeSingle();
			eventId = intent.getStringExtra("EVENTID");
		} else {
			event = Model.load(Event.class, modelId);
			eventId = event.getId();
		}
		// Project project = Model.load(Project.class, modelId);
		arg1.putString("EVENTID", eventId);

		if (mIsSelectCancelSignUpEventMembers == true) {
			arg1.putString("STATE", "CancelSignUp");
		} else if (mIsSelectSignUpEventMembers == true) {
			arg1.putString("STATE", "SignUp");
		} else if (mIsSelectUnSignInEventMembers == true) {
			arg1.putString("STATE", "UnSignIn");
		} else if (mIsSelectSignInEventMembers == true) {
			arg1.putString("STATE", "SignIn");
		}

		Object loader = new EventMemberListLoader(getActivity(), arg1);
		return (Loader<Object>) loader;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		// mQQAuth = QQAuth.createInstance(AppConstants.TENTCENT_CONNECT_APP_ID,
		// getActivity());
		// mQQShare = new QQShare(getActivity(), mQQAuth.getQQToken());

//		mAllEventMember = (Button) getView().findViewById(
//				R.id.eventMemberListFragment_action_all_event_member);
//		mCancelSignUpEventMember = (Button) getView().findViewById(
//				R.id.eventMemberListFragment_action_cancel_sign_up_member);
//		mSignUpEventMember = (Button) getView().findViewById(
//				R.id.eventMemberListFragment_action_sign_up_member);
//		mUnSignInEventMember = (Button) getView().findViewById(
//				R.id.eventMemberListFragment_action_un_sign_in_member);
//		mSignInEventMember = (Button) getView().findViewById(
//				R.id.eventMemberListFragment_action_sign_in_member);
//
//		mAllEventMember.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mIsSelectCancelSignUpEventMembers = false;
//				mIsSelectSignUpEventMembers = false;
//				mIsSelectSignInEventMembers = false;
//				mIsSelectUnSignInEventMembers = false;
//				getLoaderManager().restartLoader(0, new Bundle(),
//						EventMemberListFragment.this);
//				mAllEventMember.setBackgroundColor(getResources().getColor(
//						R.color.hoyoji_red));
//				mAllEventMember.setTextColor(Color.WHITE);
//				mCancelSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mCancelSignUpEventMember.setTextColor(Color.BLACK);
//				mSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignUpEventMember.setTextColor(Color.BLACK);
//				mUnSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mUnSignInEventMember.setTextColor(Color.BLACK);
//				mSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignInEventMember.setTextColor(Color.BLACK);
//			}
//		});
//		mAllEventMember.setBackgroundColor(getResources().getColor(
//				R.color.hoyoji_red));
//		mAllEventMember.setTextColor(Color.WHITE);
//
//		mSignUpEventMember.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mIsSelectSignUpEventMembers = true;
//				mIsSelectCancelSignUpEventMembers = false;
//				mIsSelectUnSignInEventMembers = false;
//				mIsSelectSignInEventMembers = false;
//				getLoaderManager().restartLoader(0, new Bundle(),
//						EventMemberListFragment.this);
//				mSignUpEventMember.setBackgroundColor(getResources().getColor(
//						R.color.hoyoji_red));
//				mSignUpEventMember.setTextColor(Color.WHITE);
//				mCancelSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mCancelSignUpEventMember.setTextColor(Color.BLACK);
//				mAllEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mAllEventMember.setTextColor(Color.BLACK);
//				mUnSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mUnSignInEventMember.setTextColor(Color.BLACK);
//				mSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignInEventMember.setTextColor(Color.BLACK);
//			}
//		});
//
//		mUnSignInEventMember.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mIsSelectSignUpEventMembers = false;
//				mIsSelectSignInEventMembers = false;
//				mIsSelectCancelSignUpEventMembers = false;
//				mIsSelectUnSignInEventMembers = true;
//				getLoaderManager().restartLoader(0, new Bundle(),
//						EventMemberListFragment.this);
//				mUnSignInEventMember.setBackgroundColor(getResources()
//						.getColor(R.color.hoyoji_red));
//				mUnSignInEventMember.setTextColor(Color.WHITE);
//				mCancelSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mCancelSignUpEventMember.setTextColor(Color.BLACK);
//				mSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignUpEventMember.setTextColor(Color.BLACK);
//				mAllEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mAllEventMember.setTextColor(Color.BLACK);
//				mSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignInEventMember.setTextColor(Color.BLACK);
//			}
//		});
//
//		mSignInEventMember.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mIsSelectSignUpEventMembers = false;
//				mIsSelectCancelSignUpEventMembers = false;
//				mIsSelectUnSignInEventMembers = false;
//				mIsSelectSignInEventMembers = true;
//				getLoaderManager().restartLoader(0, new Bundle(),
//						EventMemberListFragment.this);
//				mSignInEventMember.setBackgroundColor(getResources().getColor(
//						R.color.hoyoji_red));
//				mSignInEventMember.setTextColor(Color.WHITE);
//				mCancelSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mCancelSignUpEventMember.setTextColor(Color.BLACK);
//				mSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignUpEventMember.setTextColor(Color.BLACK);
//				mAllEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mAllEventMember.setTextColor(Color.BLACK);
//				mUnSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mUnSignInEventMember.setTextColor(Color.BLACK);
//			}
//		});
//
//		mCancelSignUpEventMember.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mIsSelectSignUpEventMembers = false;
//				mIsSelectCancelSignUpEventMembers = true;
//				mIsSelectUnSignInEventMembers = false;
//				mIsSelectSignInEventMembers = false;
//				getLoaderManager().restartLoader(0, new Bundle(),
//						EventMemberListFragment.this);
//				mCancelSignUpEventMember.setBackgroundColor(getResources()
//						.getColor(R.color.hoyoji_red));
//				mCancelSignUpEventMember.setTextColor(Color.WHITE);
//				mSignUpEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignUpEventMember.setTextColor(Color.BLACK);
//				mAllEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mAllEventMember.setTextColor(Color.BLACK);
//				mUnSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mUnSignInEventMember.setTextColor(Color.BLACK);
//				mSignInEventMember.setBackgroundColor(Color.TRANSPARENT);
//				mSignInEventMember.setTextColor(Color.BLACK);
//			}
//		});
		
		Spinner spinner = (Spinner) getView().findViewById(R.id.spinner1);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, 
				new ArrayList<String>(){{
						add("全部");
						add("已报名");
						add("未签到");
						add("已签到");
						add("已取消");
				}});
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		Spinner btnInviteFriend = (Spinner)getView().findViewById(R.id.spinner1);
		//		new Button(this.getActivity());
		//btnInviteFriend
		//		.setBackgroundResource(R.drawable.button_rectangle_round_5);
		//btnInviteFriend.setText("邀请好友参加活动");
		//btnInviteFriend.setTextSize(14.0f);
		// btnInviteFriend.setLayoutParams(new
		// ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT));
		btnInviteFriend.setOnItemSelectedListener(new OnItemSelectedListener() {
		
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				mIsSelectSignUpEventMembers = false;
				mIsSelectCancelSignUpEventMembers = false;
				mIsSelectUnSignInEventMembers = false;
				mIsSelectSignInEventMembers = false;
				switch(position){
					case 1:
						mIsSelectSignUpEventMembers = true;
						break;
					case 2:
						mIsSelectUnSignInEventMembers = true;
						break;
					case 3:
						mIsSelectSignInEventMembers = true;
						break;
					case 4:
						mIsSelectCancelSignUpEventMembers = true;
						break;
					
				}
				getLoaderManager().restartLoader(0, new Bundle(),
						EventMemberListFragment.this);
			}
		
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		String eventId = intent.getStringExtra("EVENTID");
		Event event = null;
		if (eventId != null) {
			event = Event.getModel(Event.class, eventId);
		} else {
			event = Event.load(Event.class, modelId);
		}
		View addNewButton = getView().findViewById(R.id.eventmember_listfragment_addnew);
		if (!event.getOwnerUserId().equals(
				HyjApplication.getInstance().getCurrentUser().getId())) {
			addNewButton.setVisibility(View.GONE);
		} else {
			addNewButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = getActivity().getIntent();
					Long modelId = intent.getLongExtra("MODEL_ID", -1);
						Bundle bundle = new Bundle();
						bundle.putLong("EVENTID", modelId);
						bundle.putString("DIALOG_TYPE", "invite");
						EventMemberDialogFragment.newInstance(bundle).show(
								getActivity().getSupportFragmentManager(),
								"EventMemberDialogFragment");
				}
			});
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
			return;
		}
		if (id == -1) {
			return;
		}
		if (getActivity().getCallingActivity() != null) {
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			intent.putExtra("MODEL_TYPE", "EventMember");

			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong("MODEL_ID", id);
			EventMember memberToBeDetermined = EventMember.load(
					EventMember.class, id);

			bundle.putLong("project_id", memberToBeDetermined.getProject()
					.get_mId());
			if (memberToBeDetermined.getFriend() != null) {
				bundle.putLong("friend_id", memberToBeDetermined.getFriend()
						.get_mId());
			} else if (memberToBeDetermined.getFriendUserId() != null) {
				bundle.putString("friendUserId",
						memberToBeDetermined.getFriendUserId());
			} else if (memberToBeDetermined.getLocalFriendId() != null) {
				bundle.putString("localFriendId",
						memberToBeDetermined.getLocalFriendId());
			}
			Long modelId = getActivity().getIntent().getLongExtra("MODEL_ID",
					-1);
			if (modelId != -1) {
				bundle.putLong("event_id", modelId);
			}
			if (memberToBeDetermined.getOwnerUserId().equalsIgnoreCase(
					HyjApplication.getInstance().getCurrentUser().getId())
					&& memberToBeDetermined.getToBeDetermined()) {
				openActivityWithFragment(EventMemberTBDViewPagerFragment.class,
						R.string.memberTBDFormFragment_title_split, bundle);
			} else {
				openActivityWithFragment(EventMemberViewPagerFragment.class,
						R.string.memberFormFragment_textView_projectMoney,
						bundle);
			}
		}
	}

	// @Override
	// public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
	// if(view.getId() == R.id.homeListItem_picture){
	// String userId = cursor.getString(columnIndex);
	// HyjImageView imageView = (HyjImageView)view;
	// imageView.setDefaultImage(R.drawable.ic_action_person_white);
	// if(cursor.getString(columnIndex) != null){
	// User user = HyjModel.getModel(User.class, userId);
	// if(user != null){
	// imageView.setImage(user.getPictureId());
	// } else {
	// imageView.setImage((Picture)null);
	// }
	// if(HyjApplication.getInstance().getCurrentUser().getId().equals(userId)){
	// imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
	// } else {
	// imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_green));
	// }
	// } else {
	// imageView.setImage((Picture)null);
	// imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
	// }
	// return true;
	// }else if(view.getId() == R.id.homeListItem_title){
	// EventMember em = HyjModel.getModel(EventMember.class,
	// cursor.getString(columnIndex));
	// if("".equals(em.getNickName()) || em.getNickName() == null){
	// ((TextView)view).setText(em.getFriendDisplayName());
	// } else {
	// ((TextView)view).setText(em.getNickName());
	// }
	// return true;
	// }else if(view.getId() == R.id.homeListItem_subTitle){
	// EventMember evtMember = HyjModel.getModel(EventMember.class,
	// cursor.getString(columnIndex));
	// if(!evtMember.getToBeDetermined()){
	// if("SignUp".equals(evtMember.getState())){
	// ((TextView)view).setText("已报名");
	// } else if("SignIn".equals(evtMember.getState())){
	// ((TextView)view).setText("已签到");
	// } else if("UnSignIn".equals(evtMember.getState())){
	// ((TextView)view).setText("未签到");
	// } else if("UnSignUp".equals(evtMember.getState())){
	// ((TextView)view).setText("未报名");
	// }
	// }
	// return true;
	// } else if(view.getId() == R.id.homeListItem_amount) {
	// EventMember evtMember = HyjModel.getModel(EventMember.class,
	// cursor.getString(columnIndex));
	// HyjNumericView numericView = (HyjNumericView)view;
	// Double apportionTotal = evtMember.getApportionTotal();
	// String currencySymbol =
	// evtMember.getEvent().getProject().getCurrencySymbol();
	// if(apportionTotal < 0){
	// apportionTotal = -apportionTotal;
	// numericView.setPrefix("活动收入:" + currencySymbol);
	// // numericView.setTextColor(Color.parseColor("#339900"));
	// }else{
	// if(apportionTotal.equals(0.0)){
	// // numericView.setTextColor(Color.BLACK);
	// numericView.setPrefix(currencySymbol);
	// }else{
	// // numericView.setTextColor(Color.parseColor(R.color.));
	// numericView.setPrefix("活动支出:" + currencySymbol);
	// }
	// }
	// numericView.setSuffix(null);
	// numericView.setNumber(apportionTotal);
	// return true;
	// } else if(view.getId() == R.id.homeListItem_remark){
	// EventMember evtMember = HyjModel.getModel(EventMember.class,
	// cursor.getString(columnIndex));
	// if(evtMember.getToBeDetermined()){
	// ((TextView)view).setText("可进行账务拆分");
	// } else {
	// ((TextView)view).setText("");
	// }
	// }
	// return true;
	// }

	@Override
	public boolean setViewValue(View view, Object model, String field) {
		EventMember em = (EventMember) model;
		if (view.getId() == R.id.homeListItem_picture) {
			String userId = em.getFriendUserId();
			HyjImageView imageView = (HyjImageView) view;
			imageView.setDefaultImage(R.drawable.ic_action_person_white);
			if (em.getFriendUserId() != null) {
				User user = HyjModel.getModel(User.class, userId);
				if (user != null) {
					imageView.setImage(user.getPictureId());
				} else {
					imageView.setImage((Picture) null);
				}
				if (HyjApplication.getInstance().getCurrentUser().getId()
						.equals(userId)) {
					imageView.setBackgroundColor(getResources().getColor(
							R.color.hoyoji_red));
				} else {
					imageView.setBackgroundColor(getResources().getColor(
							R.color.hoyoji_green));
				}
			} else {
				imageView.setImage((Picture) null);
				imageView.setBackgroundColor(getResources().getColor(
						R.color.hoyoji_yellow));
			}
			return true;
		} else if (view.getId() == R.id.homeListItem_title) {
			if ("".equals(em.getNickName()) || em.getNickName() == null) {
				((TextView) view).setText(em.getFriendDisplayName());
			} else {
				((TextView) view).setText(em.getNickName());
			}
			return true;
		} else if (view.getId() == R.id.homeListItem_subTitle) {
			if (!em.getToBeDetermined().booleanValue()) {
				if ("SignUp".equals(em.getState())) {
					((TextView) view).setText("已报名");
				} else if ("SignIn".equals(em.getState())) {
					((TextView) view).setText("已签到");
				} else if ("UnSignIn".equals(em.getState())) {
					((TextView) view).setText("未签到");
				} else if ("UnSignUp".equals(em.getState())) {
					((TextView) view).setText("未报名");
				} else if ("CancelSignUp".equals(em.getState())) {
					((TextView) view).setText("取消报名");
				}
			} else {
				((TextView) view).setText("");
			}
			return true;
		} else if (view.getId() == R.id.homeListItem_amount) {
			HyjNumericView numericView = (HyjNumericView) view;
			EventMember me = new Select()
					.from(EventMember.class)
					.where("eventId = ? AND friendUserId = ?",
							em.getEventId(),
							HyjApplication.getInstance().getCurrentUser()
									.getId()).executeSingle();
			if (me != null
					&& me.getEventShareOwnerDataOnly()
					&& !HyjApplication.getInstance().getCurrentUser().getId()
							.equals(em.getFriendUserId())) {
				numericView.setPrefix("");
				numericView.setText(null);
			} else {
				Double apportionTotal = em.getApportionTotal();
				String currencySymbol = em.getEvent().getProject()
						.getCurrencySymbol();
				if (apportionTotal < 0) {
					apportionTotal = -apportionTotal;
					numericView.setPrefix("活动收入:" + currencySymbol);
					// numericView.setTextColor(Color.parseColor("#339900"));
				} else {
					if (apportionTotal.equals(0.0)) {
						// numericView.setTextColor(Color.BLACK);
						numericView.setPrefix(currencySymbol);
					} else {
						// numericView.setTextColor(Color.parseColor(R.color.));
						numericView.setPrefix("活动支出:" + currencySymbol);
					}
				}
				numericView.setNumber(apportionTotal);
				numericView.setSuffix(null);
			}
			return true;
		} else if (view.getId() == R.id.homeListItem_remark) {
			if (em.getToBeDetermined()) {
				((TextView) view).setText("可进行账务拆分");
			} else {
				((TextView) view).setText("");
			}
			return true;
		} else if (view.getId() == R.id.homeListItem_owner) {
			TextView textView = (TextView) view;
			ProjectShareAuthorization psa = em.getProjectShareAuthorization();
			if (psa == null) {
				textView.setText("");
			} else {
				if (!HyjApplication.getInstance().getCurrentUser().getId()
						.equals(psa.getFriendUserId())) {
					ProjectShareAuthorization psa1 = new Select()
							.from(ProjectShareAuthorization.class)
							.where("projectId=? AND friendUserId=?",
									psa.getProjectId(),
									HyjApplication.getInstance()
											.getCurrentUser().getId())
							.executeSingle();
					if (psa1 != null
							&& psa1.getProjectShareMoneyExpenseOwnerDataOnly() == true) {
						textView.setText(null);
						return true;
					}
				}
				Double settlement = psa.getSettlement();
				String currencySymbol = psa.getProject().getCurrencySymbol();
				textView.setText("会费结余:" + currencySymbol
						+ HyjUtil.toFixed2(settlement));
			}
			return true;
		}
		return true;
	}

	protected void returnSelectedItems() {
		long[] ids = getListView().getCheckedItemIds();
		if(ids.length == 0){
			HyjUtil.displayToast("请选择至少一条记录");
			return;
		}

		Intent intent = new Intent();
		intent.putExtra("MODEL_IDS", ids);
		intent.putExtra("MODEL_TYPE", "EventMember");
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.multi_select_menu_ok){
			return super.onOptionsItemSelected(item);
		}
		
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		String eventId = intent.getStringExtra("EVENTID");
		Event event = null;
		if (eventId != null) {
			event = Event.getModel(Event.class, eventId);
		} else {
			event = Event.load(Event.class, modelId);
		}
		if (!event.getProject().getOwnerUserId()
				.equals(HyjApplication.getInstance().getCurrentUser().getId())) {
			HyjUtil.displayToast("您不能在共享来的圈子添加活动成员");
			return true;
		}
		if (item.getItemId() == R.id.projectEventMemberListFragment_invite_friend) {
			Bundle bundle = new Bundle();
			bundle.putLong("EVENTID", modelId);
			bundle.putString("DIALOG_TYPE", "invite");
			EventMemberDialogFragment.newInstance(bundle).show(
					getActivity().getSupportFragmentManager(),
					"EventMemberDialogFragment");
			return true;
		} else if (item.getItemId() == R.id.projectEventMemberListFragment_signIn_friend) {
			Bundle bundle = new Bundle();
			bundle.putLong("EVENTID", modelId);
			bundle.putString("DIALOG_TYPE", "signIn");
			EventMemberDialogFragment.newInstance(bundle).show(
					getActivity().getSupportFragmentManager(),
					"EventMemberDialogFragment");
			return true;
		} else if (item.getItemId() == R.id.projectEventMemberListFragment_action_add) {
			Bundle bundle = new Bundle();
			bundle.putLong("EVENT_ID", modelId);
			openActivityWithFragment(EventMemberFormFragment.class,
					R.string.projectEventMemberFormFragment_action_addnew,
					bundle);
			return true;
		}
		// else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_invite){
		// inviteFriend("Other", event, event.getName(), "invite");
		// return true;
		// } else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_invite_wxFriend){
		// inviteFriend("WX", event, event.getName(), "invite");
		// return true;
		// } else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_invite_qqFriend){
		// inviteFriend("QQ", event, event.getName(), "invite");
		// return true;
		// } else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_signIn){
		// inviteFriend("Other", event, event.getName(), "signIn");
		// return true;
		// } else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_signIn_wxFriend){
		// inviteFriend("WX", event, event.getName(), "signIn");
		// return true;
		// } else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_signIn_qqFriend){
		// inviteFriend("QQ", event, event.getName(), "signIn");
		// return true;
		// }
		// else if(item.getItemId() ==
		// R.id.projectEventMemberListFragment_action_member_edit){
		// Bundle bundle = new Bundle();
		// bundle.putLong("MODEL_ID", modelId);
		// openActivityWithFragment(ProjectEventFormFragment.class,
		// R.string.projectEventFormFragment_title_edit, bundle);
		// return true;
		// }
		else if (item.getItemId() == R.id.projectEventMemberListFragment_action_setUnSignUp) {
			setUnSignUpEventMembers(event);
			this.exitMultiChoiceMode(getListView());
			return true;
		} else if (item.getItemId() == R.id.projectEventMemberListFragment_action_setSignUp) {
			setSignUpEventMembers(event);
			this.exitMultiChoiceMode(getListView());
			return true;
		} else if (item.getItemId() == R.id.projectEventMemberListFragment_action_setUnSignIn) {
			setUnSignInEventMembers(event);
			this.exitMultiChoiceMode(getListView());
			return true;
		} else if (item.getItemId() == R.id.projectEventMemberListFragment_action_setSignIn) {
			setSignInEventMembers(event);
			this.exitMultiChoiceMode(getListView());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// public void inviteFriend(final String way, Event event,final String
	// event_name, final String type) {
	// ((HyjActivity)
	// getActivity()).displayProgressDialog(R.string.friendListFragment__action_invite_title,R.string.friendListFragment__action_invite_content);
	// String emTitle = null;
	// String emDescription = null;
	// if(type.equals("invite")){
	// emTitle = "邀请参加活动";
	// emDescription =
	// HyjApplication.getInstance().getCurrentUser().getDisplayName() +
	// " 邀请您参加活动    " +event_name;
	// } else if(type.equals("signIn")){
	// emTitle = "活动签到";
	// emDescription =
	// HyjApplication.getInstance().getCurrentUser().getDisplayName() +
	// " 邀请您进行活动    " +event_name +"签到";
	// }
	// final String emTitleSent = emTitle;
	// final String emDescriptionSent = emDescription;
	//
	// JSONObject inviteFriendObject = new JSONObject();
	// final String id = UUID.randomUUID().toString();
	// try {
	// inviteFriendObject.put("id", id);
	// inviteFriendObject.put("data", event.toJSON().toString());
	// inviteFriendObject.put("__dataType", "InviteLink");
	// inviteFriendObject.put("title", emTitle);
	// inviteFriendObject.put("type", "EventMember");
	// inviteFriendObject.put("date", (new Date()).getTime());
	// inviteFriendObject.put("description", emDescription);
	// inviteFriendObject.put("state", "Open");
	// } catch (JSONException e1) {
	// e1.printStackTrace();
	// }
	//
	// // 从服务器上下载用户数据
	// HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
	// @Override
	// public void finishCallback(Object object) {
	// ((HyjActivity) getActivity()).dismissProgressDialog();
	// String linkUrl = null;
	// if(type.equals("invite")){
	// linkUrl = HyjApplication.getInstance().getServerUrl()+"m/invite.html?id="
	// + id;
	// } else if(type.equals("signIn")){
	// linkUrl =
	// HyjApplication.getInstance().getServerUrl()+"m/eventSignIn.html?id=" +
	// id;
	// }
	// if(way.equals("Other")){
	// inviteOtherFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
	// } else if(way.equals("WX")){
	// inviteWXFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
	// } else if(way.equals("QQ")){
	// inviteQQFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
	// }
	// }
	//
	// @Override
	// public void errorCallback(Object object) {
	// ((HyjActivity) getActivity()).dismissProgressDialog();
	// try {
	// JSONObject json = (JSONObject) object;
	// ((HyjActivity) getActivity()).displayDialog(null,
	// json.getJSONObject("__summary")
	// .getString("msg"));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// };
	//
	// HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" +
	// inviteFriendObject.toString() + "]", "postData");
	// }
	//
	// public void inviteOtherFriend(String linkUrl, String event_name, String
	// emTitleSent, String emDescriptionSent) {
	// Intent intent=new Intent(Intent.ACTION_SEND);
	// intent.setType("text/plain");
	//
	// // File f;
	// // try {
	// // f = HyjUtil.createImageFile("invite_friend", "PNG");
	// // if(!f.exists()){
	// // Bitmap bmp = HyjUtil.getCommonBitmap(R.drawable.invite_friend);
	// // FileOutputStream out;
	// // out = new FileOutputStream(f);
	// // bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
	// // out.close();
	// // }
	// // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
	//
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // }
	// intent.putExtra(Intent.EXTRA_TITLE, emTitleSent);
	// intent.putExtra(Intent.EXTRA_SUBJECT, emTitleSent);
	// intent.putExtra(Intent.EXTRA_TEXT,
	// HyjApplication.getInstance().getCurrentUser().getDisplayName() +
	// emTitleSent +event_name+"。\n\n" + linkUrl);
	//
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// startActivity(Intent.createChooser(intent, emTitleSent));
	// }
	//
	// public void inviteWXFriend(String linkUrl,String event_name, String
	// emTitleSent, String emDescriptionSent) {
	// api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID);
	// WXWebpageObject webpage = new WXWebpageObject();
	// webpage.webpageUrl = linkUrl;
	// WXMediaMessage msg = new WXMediaMessage(webpage);
	// msg.title = emTitleSent;
	// msg.description = emDescriptionSent;
	// Bitmap thumb = BitmapFactory.decodeResource(getResources(),
	// R.drawable.ic_launcher);
	// msg.thumbData = Util.bmpToByteArray(thumb, true);
	//
	// SendMessageToWX.Req req = new SendMessageToWX.Req();
	// req.transaction = buildTransaction("webpage");
	// req.message = msg;
	// // req.scene = isTimelineCb.isChecked() ?
	// SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
	// api.sendReq(req);
	//
	// }
	//
	// private String buildTransaction(final String type) {
	// return (type == null) ? String.valueOf(System.currentTimeMillis()) : type
	// + System.currentTimeMillis();
	// }
	//
	// public void inviteQQFriend(String linkUrl,String event_name, String
	// emTitleSent, String emDescriptionSent) {
	// final Bundle params = new Bundle();
	// params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
	// QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	// params.putString(QQShare.SHARE_TO_QQ_TITLE, emTitleSent);
	// params.putString(QQShare.SHARE_TO_QQ_SUMMARY, emDescriptionSent);
	// params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, linkUrl);
	// params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
	// HyjApplication.getInstance().getServerUrl() + "imgs/invite_friend.png");
	// params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "好友AA记账");
	// // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");
	// mQQShare.shareToQQ(getActivity(), params, new
	// BaseUIListener(getActivity()) {
	//
	// @Override
	// public void onCancel() {
	// // Util.toastMessage(getActivity(), "onCancel: ");
	// }
	//
	// @Override
	// public void onComplete(Object response) {
	// // Util.toastMessage(getActivity(), "onComplete: " +
	// response.toString());
	// }
	//
	// @Override
	// public void onError(UiError e) {
	// // Util.toastMessage(getActivity(), "onError: " + e.errorMessage, "e");
	// }
	//
	// });
	// }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Intent intent = getActivity().getIntent();
		Long modelId = intent.getLongExtra("MODEL_ID", -1);
		String eventId = intent.getStringExtra("EVENTID");
		Event event = null;
		if (eventId != null) {
			event = Event.getModel(Event.class, eventId);
		} else {
			event = Event.load(Event.class, modelId);
		}
		if (!event.getOwnerUserId().equals(
				HyjApplication.getInstance().getCurrentUser().getId())
				&& getOptionsMenu().findItem(
						R.id.projectEventMemberListFragment_action_member_add) != null) {
			getOptionsMenu().findItem(
					R.id.projectEventMemberListFragment_action_member_add)
					.setVisible(false);
		}
	}

	private void setUnSignUpEventMembers(Event event) {
		long[] ids = this.getListView().getCheckedItemIds();
		if (ids.length == 0) {
			HyjUtil.displayToast("请选择至少一个活动成员");
			return;
		}
		int updateToUnSignUpCount = 0;
		for (int i = 0; i < ids.length; i++) {
			EventMember em = Model.load(EventMember.class, ids[i]);
			if(em != null){
				if (!"UnSignUp".equals(em.getState()) && !"CancelSignUp".equals(em.getState())) {
					updateToUnSignUpCount ++;
				}
				em.setState("UnSignUp");
				em.save();
			}
		}
		if (updateToUnSignUpCount > 0) {
			event.setSignUpCount(event.getSignUpCount() - updateToUnSignUpCount);
			event.setSyncFromServer(true);
			event.save();
		}

	}

	private void setSignUpEventMembers(Event event) {
		long[] ids = this.getListView().getCheckedItemIds();
		if (ids.length == 0) {
			HyjUtil.displayToast("请选择至少一个活动成员");
			return;
		}
		int updateToSignUpCount = 0;
		for (int i = 0; i < ids.length; i++) {
			EventMember em = Model.load(EventMember.class, ids[i]);
			if(em != null){
				if ("UnSignUp".equals(em.getState()) || "CancelSignUp".equals(em.getState())) {
					updateToSignUpCount ++;
				}
				em.setState("SignUp");
				em.save();
			}
		}
		if (updateToSignUpCount > 0) {
			event.setSignUpCount(event.getSignUpCount() + updateToSignUpCount);
			event.setSyncFromServer(true);
			event.save();
		}

	}

	private void setSignInEventMembers(Event event) {
		long[] ids = this.getListView().getCheckedItemIds();
		if (ids.length == 0) {
			HyjUtil.displayToast("请选择至少一个活动成员");
			return;
		}
		int updateToSignInCount = 0;
		for (int i = 0; i < ids.length; i++) {
			EventMember em = Model.load(EventMember.class, ids[i]);
			if(em != null){
				if ("UnSignUp".equals(em.getState()) || "CancelSignUp".equals(em.getState())) {
					updateToSignInCount ++;
				}
				em.setState("SignIn");
				em.save();
			}
		}
		if (updateToSignInCount > 0) {
			event.setSignUpCount(event.getSignUpCount() + updateToSignInCount);
			event.setSyncFromServer(true);
			event.save();
		}

	}

	private void setUnSignInEventMembers(Event event) {
		long[] ids = this.getListView().getCheckedItemIds();
		if (ids.length == 0) {
			HyjUtil.displayToast("请选择至少一个活动成员");
			return;
		}
		int updateToUnSignInCount = 0;
		for (int i = 0; i < ids.length; i++) {
			EventMember em = Model.load(EventMember.class, ids[i]);
			if(em != null){
				if ("UnSignUp".equals(em.getState()) || "CancelSignUp".equals(em.getState())) {
					updateToUnSignInCount ++;
				}
				em.setState("UnSignIn");
				em.save();
			}
		}
		if (updateToUnSignInCount > 0) {
			event.setSignUpCount(event.getSignUpCount() + updateToUnSignInCount);
			event.setSyncFromServer(true);
			event.save();
		}

	}

	@Override
	public void onLoadFinished(Loader loader, Object list) {
		Collection<EventMember> childList = (ArrayList<EventMember>) list;
		mMemberList.clear();
		mMemberList.addAll(childList);

		((SimpleAdapter) getListAdapter()).notifyDataSetChanged();
		setFooterLoadFinished(getListView(), childList.size());

		// The list should now be shown.
		if (isResumed()) {
			// setListShown(true);
		} else {
			// setListShownNoAnimation(true);
		}
	}

	private static class MemberListAdapter extends SimpleAdapter {
		private Context mContext;
		private int[] mViewIds;
		private String[] mFields;
		private int mLayoutResource;

		// private ViewBinder mViewBinder;

		public MemberListAdapter(Context context, List<EventMember> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, (List<? extends Map<String, ?>>) ((Object)childData),
					childLayout, childFrom, childTo);

			mContext = context;
			mLayoutResource = childLayout;
			mViewIds = childTo;
			mFields = childFrom;
		}

		@Override
		public long getItemId(int position) {
			return ((HyjModel) getItem(position)).get_mId();
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		/**
		 * Populate new items in the list.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			View[] viewHolder;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(mLayoutResource, null);
				viewHolder = new View[mViewIds.length];
				for (int i = 0; i < mViewIds.length; i++) {
					View v = view.findViewById(mViewIds[i]);
					viewHolder[i] = v;
				}
				view.setTag(viewHolder);
			} else {
				viewHolder = (View[]) view.getTag();
			}

			Object item = getItem(position);
			for (int i = 0; i < mViewIds.length; i++) {
				View v = viewHolder[i];
				getViewBinder().setViewValue(v, item, mFields[i]);
			}

			return view;
		}
	}

}
