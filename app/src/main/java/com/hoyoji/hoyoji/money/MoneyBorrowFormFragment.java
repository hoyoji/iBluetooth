package com.hoyoji.hoyoji.money;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjModelEditor;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjActivity.DialogCallbackListener;
import com.hoyoji.android.hyjframework.fragment.HyjCalculatorFormFragment;
import com.hoyoji.android.hyjframework.fragment.HyjTextInputFormFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjImageField;
import com.hoyoji.android.hyjframework.view.HyjImageField.PictureItem;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.project.ProjectMemberFormFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.event.EventListFragment;

public class MoneyBorrowFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_FRIEND_ID = 3;
	private static final int GET_REMARK = 4;
	private final static int GET_AMOUNT = 8;
	private final static int GET_EVENT_ID = 9;
	private static final int TAG_IS_LOCAL_FRIEND = R.id.moneyBorrowFormFragment_selectorField_friend;
	private static final int ADD_AS_PROJECT_MEMBER = 0;
	protected static final int GET_FINANCIALOWNER_ID = 5;
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;

	private HyjModelEditor<MoneyBorrow> mMoneyBorrowEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericAmount = null;
	private HyjDateTimeField mDateTimeFieldReturnDate = null;
	private HyjNumericField mNumericFieldReturnedAmount = null;
	private View mSeparatorFieldReturnedAmount = null;
	private HyjSelectorField mSelectorFieldMoneyAccount = null;
	private HyjSelectorField mSelectorFieldProject = null;
	private HyjSelectorField mSelectorFieldEvent = null;
	private View mViewSeparatorEvent = null;
	private HyjNumericField mNumericExchangeRate = null;
	private HyjSelectorField mSelectorFieldFriend = null;
//	private ImageView mImageViewClearFriend = null;
	private HyjRemarkField mRemarkFieldRemark = null;
	private ImageView mImageViewRefreshRate = null;
	private View mViewSeparatorExchange = null;
	private LinearLayout mLinearLayoutExchangeRate = null;

	private boolean hasEditPermission = true;
//	private HyjSelectorField mSelectorFieldFinancialOwner;
	private ImageButton mButtonBorrowMore;
	private LinearLayout mLinearLayoutExpandMore;
	
	private ImageButton calculatorTextView = null;

	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneyborrow;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		MoneyBorrow moneyBorrow;

		Intent intent = getActivity().getIntent();
		final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			moneyBorrow = HyjModel.load(MoneyBorrow.class, modelId); // new
																		// Select().from(MoneyBorrow.class).where("_id=?",
																		// modelId).executeSingle();
			hasEditPermission = moneyBorrow.hasEditPermission();
		} else {
			moneyBorrow = new MoneyBorrow();
			final String moneyAccountId = intent
					.getStringExtra("moneyAccountId");
			if (moneyAccountId != null) {
				MoneyAccount moneyAccount = HyjModel.getModel(
						MoneyAccount.class, moneyAccountId);
				moneyBorrow.setMoneyAccountId(moneyAccountId,
						moneyAccount.getCurrencyId());
			}
			if (intent.getStringExtra("counterpartId") != null) {
				moneyBorrow.setIsImported(true);
			}
		}
		mMoneyBorrowEditor = moneyBorrow.newModelEditor();

		setupDeleteButton(mMoneyBorrowEditor);

		mImageFieldPicture = (HyjImageField) getView().findViewById(
				R.id.moneyBorrowFormFragment_imageField_picture);
		mImageFieldPicture.setImages(moneyBorrow.getPictures());

		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(
				R.id.moneyBorrowFormFragment_textField_date);
		if (modelId != -1) {
			mDateTimeFieldDate.setTime(moneyBorrow.getDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
			}
		}

		mNumericAmount = (HyjNumericField) getView().findViewById(
				R.id.moneyBorrowFormFragment_textField_amount);
		if (modelId == -1) {
			double amount = 0.0;
			double exchangeRate = 1.0;
//			if(temPlateJso != null){
//				amount = temPlateJso.optDouble("amount", 0.0);
//				exchangeRate = temPlateJso.optDouble("exchangeRate", 1.0);
//			} else {
				amount = intent.getDoubleExtra("amount", 0.0);//从分享消息导入的金额
				exchangeRate = intent.getDoubleExtra("exchangeRate", 1.0);
//			}
			mNumericAmount.setNumber(amount*exchangeRate);
		} else {
			mNumericAmount.setNumber(moneyBorrow.getAmount());
		}

		mDateTimeFieldReturnDate = (HyjDateTimeField) getView().findViewById(
				R.id.moneyBorrowFormFragment_textField_returnDate);
		mDateTimeFieldReturnDate.setTime(moneyBorrow.getReturnDate());

		mNumericFieldReturnedAmount = (HyjNumericField) getView().findViewById(
				R.id.moneyBorrowFormFragment_textField_returnedAmount);
		mNumericFieldReturnedAmount.setNumber(moneyBorrow.getReturnedAmount());
		mNumericFieldReturnedAmount.setEnabled(false);
		mSeparatorFieldReturnedAmount = (View) getView().findViewById(
				R.id.moneyBorrowFormFragment_separatorField_returnedAmount);
		mNumericFieldReturnedAmount.setVisibility(View.GONE);
		mSeparatorFieldReturnedAmount.setVisibility(View.GONE);

		MoneyAccount moneyAccount = moneyBorrow.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(
				R.id.moneyBorrowFormFragment_selectorField_moneyAccount);

		if (moneyAccount != null) {
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "("
					+ moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				MoneyBorrowFormFragment.this
						.openActivityWithFragmentForResult(
								MoneyAccountListFragment.class,
								R.string.moneyAccountListFragment_title_select_moneyAccount,
								bundle, GET_MONEYACCOUNT_ID);
			}
		});
		
		Long dateImport = intent.getLongExtra("date", -1);
		if(dateImport != -1){
			Date date= new Date(dateImport);
			mDateTimeFieldDate.setDate(date);
			mDateTimeFieldDate.setTextColor(Color.RED);
		}

		Project project;
		String projectId = intent.getStringExtra("projectId");// 从消息导入
		if (moneyBorrow.get_mId() == null && projectId != null) {
			project = HyjModel.getModel(Project.class, projectId);
		} else {
			project = moneyBorrow.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(
				R.id.moneyBorrowFormFragment_selectorField_project);

		if (project != null) {
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "("
					+ project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyBorrowFormFragment.this.openActivityWithFragmentForResult(
						ProjectListFragment.class,
						R.string.projectListFragment_title_select_project,
						null, GET_PROJECT_ID);
			}
		});
		

		mSelectorFieldEvent = (HyjSelectorField) getView().findViewById(R.id.moneyExpenseContainerFormFragment_selectorField_event);
		mViewSeparatorEvent = (View) getView().findViewById(R.id.field_separator_event);

		if (project != null) {
			List<Event> events = new Select().from(Event.class).where("projectId = ?", project.getId()).execute();
			if(events.size() > 0) {
				mSelectorFieldEvent.setVisibility(View.VISIBLE);
				mViewSeparatorEvent.setVisibility(View.VISIBLE);
			} else {
				mSelectorFieldEvent.setVisibility(View.GONE);
				mViewSeparatorEvent.setVisibility(View.GONE);
			}
			
			Event event = null;
			String eventId = intent.getStringExtra("eventId");//从消息导入
			if(moneyBorrow.get_mId() == null){
				if(eventId != null) {
					moneyBorrow.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyBorrow.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyBorrow.getEvent();
			}
			
	
			if (event != null) {
				mSelectorFieldEvent.setModelId(event.getId());
				mSelectorFieldEvent.setText(event.getName());
			}
		} else {
			mSelectorFieldEvent.setVisibility(View.GONE);
			mViewSeparatorEvent.setVisibility(View.GONE);
		}

        mSelectorFieldEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectorFieldProject.getModelId() != null) {
                    Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());

                    Bundle bundle = new Bundle();
                    bundle.putLong("MODEL_ID", project.get_mId());
                    bundle.putString("NULL_ITEM", (String) mSelectorFieldEvent.getHint());

                    MoneyBorrowFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(
				R.id.moneyBorrowFormFragment_textField_exchangeRate);
		mNumericExchangeRate.setNumber(moneyBorrow.getExchangeRate());

		mViewSeparatorExchange = (View) getView().findViewById(
				R.id.moneyBorrowFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(
				R.id.moneyBorrowFormFragment_linearLayout_exchangeRate);

		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(
				R.id.moneyBorrowFormFragment_selectorField_friend);
		mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
		if (moneyBorrow.get_mId() == null) {
			String friendUserId = intent.getStringExtra("friendUserId");// 从消息导入
			if (friendUserId != null) {
					mSelectorFieldFriend.setModelId(friendUserId);
					mSelectorFieldFriend.setText(Friend.getFriendUserDisplayName(null, friendUserId, projectId));
					mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
			} else {
				String localFriendId = intent.getStringExtra("localFriendId");// 从消息导入
				if (localFriendId != null) {
					mSelectorFieldFriend.setModelId(localFriendId);
					mSelectorFieldFriend.setText(Friend.getFriendUserDisplayName(localFriendId, null, projectId));
					mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, true);
				}
			}
		} else {
			if (moneyBorrow.getLocalFriendId() != null) {
				mSelectorFieldFriend.setText(moneyBorrow.getFriendDisplayName());
				mSelectorFieldFriend.setModelId(moneyBorrow.getLocalFriendId());
				mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, true);
			} else if (moneyBorrow.getFriendUserId() != null) {
				mSelectorFieldFriend.setModelId(moneyBorrow.getFriendUserId());
				mSelectorFieldFriend.setText(moneyBorrow.getFriendDisplayName());
				mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
			}
		}

		mSelectorFieldFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(mSelectorFieldProject.getModelId() == null){
                    HyjUtil.displayToast("请选选择一个圈子");
                    return;
                }
				Bundle bundle = new Bundle();
				Project project = HyjModel.getModel(Project.class,
						mSelectorFieldProject.getModelId());
				bundle.putLong("MODEL_ID", project.get_mId());
				bundle.putBoolean("disableMultiChoiceMode", true);
//				bundle.putString("NULL_ITEM", (String) mSelectorFieldFriend.getHint());

				if(!project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.friendListFragment_title_select_friend_creditor, bundle, GET_FRIEND_ID);
				} else {
					openActivityWithFragmentForResult(
						SelectApportionMemberListFragment.class,
						R.string.friendListFragment_title_select_friend_creditor,
						bundle, GET_FRIEND_ID);
				}
			}
		});

//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyBorrowFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});

		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(
				R.id.moneyBorrowFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyBorrow.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT",
						"请输入" + mRemarkFieldRemark.getLabelText());
				MoneyBorrowFormFragment.this.openActivityWithFragmentForResult(
						HyjTextInputFormFragment.class,
						R.string.moneyExpenseFormFragment_textView_remark,
						bundle, GET_REMARK);
			}
		});
		
		calculatorTextView = (ImageButton) getView().findViewById(R.id.calculator);
		calculatorTextView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putDouble("AMOUNT", mNumericAmount.getNumber()!=null?mNumericAmount.getNumber():0.00);
				MoneyBorrowFormFragment.this.openActivityWithFragmentForResult(
								HyjCalculatorFormFragment.class,
								R.string.hyjCalculatorFormFragment_title,
								bundle, GET_AMOUNT);
			}
		});

		ImageView takePictureButton = (ImageView) getView().findViewById(
				R.id.moneyBorrowFormFragment_imageView_camera);
		takePictureButton.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
		takePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(getActivity(), v);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.picture_get_picture, popup.getMenu());
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.picture_take_picture) {
							mImageFieldPicture.takePictureFromCamera();
							return true;
						} else {
							mImageFieldPicture.pickPictureFromGallery();
							return true;
						}
						// return false;
					}
				});

				if (!hasEditPermission) {
					for (int i = 0; i < popup.getMenu().size(); i++) {
						popup.getMenu().setGroupEnabled(i, false);
					}
				}

				popup.show();
			}
		});

//		mSelectorFieldFinancialOwner = (HyjSelectorField) getView().findViewById(R.id.projectFormFragment_selectorField_financialOwner);
//		mSelectorFieldFinancialOwner.setEnabled(hasEditPermission);
//		if(modelId == -1){
//			if(project.getFinancialOwnerUserId() != null){
//				mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
//				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
//			}
//		} else if(moneyBorrow.getFinancialOwnerUserId() != null){
//				mSelectorFieldFinancialOwner.setModelId(moneyBorrow.getFinancialOwnerUserId());
//				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyBorrow.getFinancialOwnerUserId()));
//		}
//		
//		mSelectorFieldFinancialOwner.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				if(mSelectorFieldProject.getModelId() == null){
//					HyjUtil.displayToast("请先选择一个圈子。");
//				} else {
//					Bundle bundle = new Bundle();
//					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
//					bundle.putLong("MODEL_ID", project.get_mId());
//					bundle.putString("NULL_ITEM", (String)mSelectorFieldFinancialOwner.getHint());
//		bundle.putBoolean("disableMultiChoiceMode", true);
//					openActivityWithFragmentForResult(MemberListFragment.class, R.string.friendListFragment_title_select_friend_creditor, bundle, GET_FINANCIALOWNER_ID);
//				}
//			}
//		}); 
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(
				R.id.moneyBorrowFormFragment_imageButton_refresh_exchangeRate);
		mImageViewRefreshRate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectorFieldMoneyAccount.getModelId() != null
						&& mSelectorFieldProject.getModelId() != null) {
					MoneyAccount moneyAccount = HyjModel.getModel(
							MoneyAccount.class,
							mSelectorFieldMoneyAccount.getModelId());
					Project project = HyjModel.getModel(Project.class,
							mSelectorFieldProject.getModelId());

					String fromCurrency = moneyAccount.getCurrencyId();
					String toCurrency = project.getCurrencyId();
					if (fromCurrency != null && toCurrency != null) {
						HyjUtil.startRoateView(mImageViewRefreshRate);
						mImageViewRefreshRate.setEnabled(false);
						HyjUtil.updateExchangeRate(fromCurrency, toCurrency,
								mImageViewRefreshRate, mNumericExchangeRate);
					} else {
						HyjUtil.displayToast(R.string.moneyBorrowFormFragment_toast_select_currency);
					}
				} else {
					HyjUtil.displayToast(R.string.moneyBorrowFormFragment_toast_select_currency);
				}
			}
		});

		// 只在新增时才自动打开软键盘， 修改时不自动打开
		if (modelId == -1) {
			setExchangeRate(false);
			if(this.getUserVisibleHint()){
				this.mNumericAmount.showSoftKeyboard();
			}
		} else {
			setExchangeRate(true);
		}
		setPermission();
		
		mLinearLayoutExpandMore = (LinearLayout)getView().findViewById(R.id.moneyBorrowFormFragment_expandMore);
		mButtonBorrowMore = (ImageButton)getView().findViewById(R.id.expand_more);
		mButtonBorrowMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mLinearLayoutExpandMore.getVisibility() == View.GONE){
					mButtonBorrowMore.setImageResource(R.drawable.ic_action_collapse);
					mLinearLayoutExpandMore.setVisibility(View.VISIBLE);
				} else {
					mButtonBorrowMore.setImageResource(R.drawable.ic_action_expand);
					mLinearLayoutExpandMore.setVisibility(View.GONE);
				}
			}
		});

		// 在修改模式下自动展开
		if(modelId != -1){
			mButtonBorrowMore.setImageResource(R.drawable.ic_action_collapse);
			mLinearLayoutExpandMore.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (mMoneyBorrowEditor != null
				&& mMoneyBorrowEditor.getModelCopy().get_mId() != null
				&& !hasEditPermission) {
			hideSaveAction();
		}
	}

	private void setupDeleteButton(HyjModelEditor<MoneyBorrow> moneyBorrowEditor) {

		Button buttonDelete = (Button) getView().findViewById(
				R.id.button_delete);

		final MoneyBorrow moneyBorrow = moneyBorrowEditor.getModelCopy();

		if (moneyBorrow.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (moneyBorrow.hasDeletePermission()) {
						((HyjActivity) getActivity()).displayDialog(
								R.string.app_action_delete_list_item,
								R.string.app_confirm_delete,
								R.string.alert_dialog_yes,
								R.string.alert_dialog_no, -1,
								new DialogCallbackListener() {
									@Override
									public void doPositiveClick(Object object) {
										try {
											ActiveAndroid.beginTransaction();

											MoneyAccount moneyAccount = moneyBorrow
													.getMoneyAccount();
											HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
											moneyAccountEditor
													.getModelCopy()
													.setCurrentBalance(
															moneyAccount
																	.getCurrentBalance()
																	- moneyBorrow
																			.getAmount());
											moneyAccountEditor.save();

												// 更新圈子余额
												Project newProject = moneyBorrow
														.getProject();
												HyjModelEditor<Project> newProjectEditor = newProject
														.newModelEditor();
												newProjectEditor
														.getModelCopy()
														.setIncomeTotal(
																newProject
																		.getIncomeTotal()
																		- moneyBorrow
																				.getProjectAmount());

												if(moneyBorrow.getLocalFriendId() != null){
													newProjectEditor
													.getModelCopy()
													.setExpenseTotal(
															newProject
																	.getExpenseTotal()
																	- moneyBorrow
																			.getProjectAmount());
												}
												newProjectEditor.save();
											
											MoneyAccount debtAccount = MoneyAccount
													.getDebtAccount(
															moneyBorrow
																	.getProject()
																	.getCurrencyId(),
															moneyBorrow
																	.getLocalFriendId(),
															moneyBorrow
																	.getFriendUserId());
											HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount
													.newModelEditor();
											debtAccountEditor
													.getModelCopy()
													.setCurrentBalance(
															debtAccount
																	.getCurrentBalance()
																	+ moneyBorrow
																			.getProjectAmount());
											debtAccountEditor.save();

											// 更新支出所有者的实际支出
											ProjectShareAuthorization projectAuthorization = ProjectShareAuthorization
													.getSelfProjectShareAuthorization(moneyBorrow
															.getProjectId());
											HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = projectAuthorization.newModelEditor();
											selfProjectAuthorizationEditor
													.getModelCopy()
													.setActualTotalBorrow(
															projectAuthorization
																	.getActualTotalBorrow()
																	- moneyBorrow
																			.getProjectAmount());
											selfProjectAuthorizationEditor
													.save();

											
											if(moneyBorrow.getLocalFriendId() != null){
												MoneyLend moneyLend;
												moneyLend = new Select().from(MoneyLend.class).where("moneyBorrowId=? AND ownerFriendId=?", moneyBorrow.getId(), moneyBorrow.getLocalFriendId()).executeSingle();
												moneyLend.delete();
												
												// 更新旧的ProjectShareAuthorization
												ProjectShareAuthorization oldSelfProjectAuthorization = null;
												oldSelfProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", moneyBorrow.getLocalFriendId(), moneyBorrow.getProjectId()).executeSingle();
												
												HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
												oldSelfProjectAuthorizationEditor.getModelCopy()
														.setActualTotalLend(
																oldSelfProjectAuthorization
																		.getActualTotalLend()
																		- moneyBorrow.getProjectAmount());
												oldSelfProjectAuthorizationEditor.save();
											}
											moneyBorrow.delete();

											HyjUtil.displayToast(R.string.app_delete_success);
											ActiveAndroid
													.setTransactionSuccessful();
											ActiveAndroid.endTransaction();
											getActivity().finish();
										} catch (Exception e) {
											ActiveAndroid.endTransaction();
											HyjUtil.displayToast(R.string.app_delete_failed);
										}
									}
								});
					} else {
						HyjUtil.displayToast(R.string.app_permission_no_delete);
					}
				}
			});
		}
	}

	private void setPermission() {

		if (mMoneyBorrowEditor.getModelCopy().get_mId() != null
				&& !hasEditPermission) {
			mDateTimeFieldDate.setEnabled(false);

//			mNumericAmount.setNumber(mMoneyBorrowEditor.getModel()
//					.getProjectAmount());
			mNumericAmount.setEnabled(false);

			mDateTimeFieldReturnDate.setEnabled(false);

			mSelectorFieldFriend.setEnabled(false);

			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(
					R.id.moneyBorrowFormFragment_separatorField_moneyAccount)
					.setVisibility(View.GONE);

			mSelectorFieldProject.setEnabled(false);
			mSelectorFieldEvent.setEnabled(false);

			mNumericExchangeRate.setEnabled(false);

			mNumericFieldReturnedAmount.setEnabled(false);

			mRemarkFieldRemark.setEnabled(false);

			if (this.mOptionsMenu != null) {
				hideSaveAction();
			}

			// getView().findViewById(R.id.button_save).setEnabled(false);
			getView().findViewById(R.id.button_delete).setEnabled(false);
			getView().findViewById(R.id.button_delete).setVisibility(View.GONE);
		}
	}

	private void setExchangeRate(Boolean editInit) {
		if (mSelectorFieldMoneyAccount.getModelId() != null
				&& mSelectorFieldProject.getModelId() != null) {
			MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class,
					mSelectorFieldMoneyAccount.getModelId());
			Project project = HyjModel.getModel(Project.class,
					mSelectorFieldProject.getModelId());

			String fromCurrency = moneyAccount.getCurrencyId();
			String toCurrency = project.getCurrencyId();

			if (fromCurrency.equals(toCurrency)) {
				if (SET_EXCHANGE_RATE_FLAG != 1) {// 新增或修改打开时不做setNumber
					mNumericExchangeRate.setNumber(1.00);
					CREATE_EXCHANGE = 0;
				}
				mViewSeparatorExchange.setVisibility(View.GONE);
				mLinearLayoutExchangeRate.setVisibility(View.GONE);
			} else {
				mViewSeparatorExchange.setVisibility(View.VISIBLE);
				mLinearLayoutExchangeRate.setVisibility(View.VISIBLE);

				if (!editInit) {// 修改时init不需要set Rate
					Double rate = Exchange.getExchangeRate(fromCurrency,
							toCurrency);
					if (rate != null) {
						mNumericExchangeRate.setNumber(rate);
						CREATE_EXCHANGE = 0;
					} else {
						mNumericExchangeRate.setNumber(null);
						CREATE_EXCHANGE = 1;
					}
				}
			}

		} else {
			mViewSeparatorExchange.setVisibility(View.GONE);
			mLinearLayoutExchangeRate.setVisibility(View.GONE);
		}
		SET_EXCHANGE_RATE_FLAG = 0;
	}

	private void fillData() {
		MoneyBorrow modelCopy = (MoneyBorrow) mMoneyBorrowEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericAmount.getNumber());
		modelCopy.setReturnDate(mDateTimeFieldReturnDate.getTime());
//		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
		if (mSelectorFieldMoneyAccount.getModelId() != null) {
			MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class,
					mSelectorFieldMoneyAccount.getModelId());
			modelCopy.setMoneyAccountId(
					mSelectorFieldMoneyAccount.getModelId(),
					moneyAccount.getCurrencyId());
		} else {
			modelCopy.setMoneyAccountId(null, null);
		}
        if(mSelectorFieldProject.getModelId() != null) {
            modelCopy.setProject(HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId()));
        }
		modelCopy.setEventId(mSelectorFieldEvent.getModelId());
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());

		if (mSelectorFieldFriend.getModelId() != null) {
			if ((Boolean) mSelectorFieldFriend.getTag(TAG_IS_LOCAL_FRIEND)) {
				modelCopy.setLocalFriendId(mSelectorFieldFriend.getModelId());
				modelCopy.setFriendUserId(null);
			} else {
				modelCopy.setFriendUserId(mSelectorFieldFriend.getModelId());
				modelCopy.setLocalFriendId(null);
			}
		} else {
			modelCopy.setLocalFriendId(null);
			modelCopy.setFriendUserId(null);
		}

		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
	}

	private void showValidatioErrors() {
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyBorrowEditor
				.getValidationError("datetime"));

		mNumericAmount
				.setError(mMoneyBorrowEditor.getValidationError("amount"));
		if (mMoneyBorrowEditor.getValidationError("amount") != null) {
			mNumericAmount.showSoftKeyboard();
		}
		mDateTimeFieldReturnDate.setError(mMoneyBorrowEditor
				.getValidationError("returnDate"));
		if (mMoneyBorrowEditor.getValidationError("returnDate") != null) {
			HyjUtil.displayToast(mMoneyBorrowEditor
					.getValidationError("returnDate"));
		}

		mSelectorFieldMoneyAccount.setError(mMoneyBorrowEditor
				.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyBorrowEditor
				.getValidationError("project"));
		mSelectorFieldEvent.setError(mMoneyBorrowEditor.getValidationError("Event"));
		mNumericExchangeRate.setError(mMoneyBorrowEditor
				.getValidationError("exchangeRate"));
		mSelectorFieldFriend.setError(mMoneyBorrowEditor
				.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyBorrowEditor
				.getValidationError("remark"));
	}

	@Override
	public void onSave(View v) {
		super.onSave(v);

		fillData();

		if (mMoneyBorrowEditor.getModelCopy().get_mId() == null
				&& !mMoneyBorrowEditor.getModelCopy().hasAddNewPermission(
						mMoneyBorrowEditor.getModelCopy().getProjectId())) {
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		} else if (mMoneyBorrowEditor.getModelCopy().get_mId() != null
				&& !hasEditPermission) {
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		} else {

			if (mMoneyBorrowEditor.getModelCopy().getFriendUserId() == null
					&& mMoneyBorrowEditor.getModelCopy().getLocalFriendId() == null) {
				mMoneyBorrowEditor.setValidationError("friend",
						R.string.moneyBorrowFormFragment_editText_hint_friend);
			} else {
				mMoneyBorrowEditor.removeValidationError("friend");
			}

			mMoneyBorrowEditor.validate();

			if (mMoneyBorrowEditor.hasValidationErrors()) {
				showValidatioErrors();
			} else {
				try {
					ActiveAndroid.beginTransaction();
					HyjImageField.ImageGridAdapter adapter = mImageFieldPicture
							.getAdapter();
					int count = adapter.getCount();
					boolean mainPicSet = false;
					for (int i = 0; i < count; i++) {
						PictureItem pi = adapter.getItem(i);
						if (pi.getState() == PictureItem.NEW) {
							Picture newPic = pi.getPicture();
							newPic.setRecordId(mMoneyBorrowEditor.getModel()
									.getId());
							newPic.setRecordType("MoneyBorrow");
							newPic.setProjectId(mMoneyBorrowEditor.getModelCopy().getProjectId());
							newPic.setDisplayOrder(i);
							newPic.save();
						} else if (pi.getState() == PictureItem.DELETED) {
							pi.getPicture().delete();
						} else if (pi.getState() == PictureItem.CHANGED) {

						}
						if (!mainPicSet && pi.getPicture() != null
								&& pi.getState() != PictureItem.DELETED) {
							mainPicSet = true;
							mMoneyBorrowEditor.getModelCopy().setPicture(
									pi.getPicture());
						}
					}

					MoneyBorrow oldMoneyBorrowModel = mMoneyBorrowEditor
							.getModel();
					MoneyBorrow moneyBorrowModel = mMoneyBorrowEditor
							.getModelCopy();

					UserData userData = HyjApplication.getInstance()
							.getCurrentUser().getUserData();
					if (moneyBorrowModel.get_mId() == null
							&& !userData.getActiveMoneyAccountId().equals(
									moneyBorrowModel.getMoneyAccountId())) {
						HyjModelEditor<UserData> userDataEditor = userData
								.newModelEditor();
						userDataEditor.getModelCopy().setActiveMoneyAccountId(
								moneyBorrowModel.getMoneyAccountId());
						userDataEditor.getModelCopy().setActiveProjectId(
								moneyBorrowModel.getProjectId());
						userDataEditor.save();
					}
					
					//设置默认活动
					Project project = moneyBorrowModel.getProject();
					if(moneyBorrowModel.get_mId() == null){
						if((moneyBorrowModel.getEventId() != null && !moneyBorrowModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(moneyBorrowModel.getEventId()))){
							HyjModelEditor<Project> projectEditor = project.newModelEditor();
							projectEditor.getModelCopy().setActiveEventId(moneyBorrowModel.getEventId());
							projectEditor.save();
						}
					}

					String localCurrencyId = moneyBorrowModel.getMoneyAccount()
							.getCurrencyId();
					String foreignCurrencyId = moneyBorrowModel.getProject()
							.getCurrencyId();
					if (CREATE_EXCHANGE == 1) {
						Exchange newExchange = new Exchange();
						newExchange.setLocalCurrencyId(localCurrencyId);
						newExchange.setForeignCurrencyId(foreignCurrencyId);
						newExchange.setRate(moneyBorrowModel.getExchangeRate());
						// newExchange.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
						newExchange.save();
					} else {
						if (!localCurrencyId
								.equalsIgnoreCase(foreignCurrencyId)) {
							Exchange exchange = null;
							Double exRate = null;
							Double rate = HyjUtil.toFixed2(moneyBorrowModel
									.getExchangeRate());
							exchange = Exchange.getExchange(localCurrencyId,
									foreignCurrencyId);
							if (exchange != null) {
								exRate = exchange.getRate();
								if (!rate.equals(exRate)) {
									HyjModelEditor<Exchange> exchangModelEditor = exchange
											.newModelEditor();
									exchangModelEditor.getModelCopy().setRate(
											rate);
									exchangModelEditor.save();
								}
							} else {
								exchange = Exchange.getExchange(
										foreignCurrencyId, localCurrencyId);
								if (exchange != null) {
									exRate = HyjUtil.toFixed2(1 / exchange
											.getRate());
									if (!rate.equals(exRate)) {
										HyjModelEditor<Exchange> exchangModelEditor = exchange
												.newModelEditor();
										exchangModelEditor.getModelCopy()
												.setRate(1 / rate);
										exchangModelEditor.save();
									}
								}
							}
						}
					}

					Double oldAmount = oldMoneyBorrowModel.getAmount0();
					MoneyAccount oldMoneyAccount = oldMoneyBorrowModel
							.getMoneyAccount();
					MoneyAccount newMoneyAccount = moneyBorrowModel
							.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount
							.newModelEditor();

					if (moneyBorrowModel.get_mId() == null
							|| oldMoneyAccount.getId().equals(
									newMoneyAccount.getId())) {
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(
								newMoneyAccount.getCurrentBalance() - oldAmount
										+ moneyBorrowModel.getAmount0());

					} else {
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount
								.newModelEditor();
						oldMoneyAccountEditor.getModelCopy()
								.setCurrentBalance(
										oldMoneyAccount.getCurrentBalance()
												- oldAmount);
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(
								newMoneyAccount.getCurrentBalance()
										+ moneyBorrowModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();

					
					Project oldProject = oldMoneyBorrowModel.getProject();
					Project newProject = moneyBorrowModel.getProject();
					HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();

					//更新圈子余额
					if(moneyBorrowModel.get_mId() == null){
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() + moneyBorrowModel.getProjectAmount());
						if(moneyBorrowModel.getLocalFriendId() != null){ // 现在是本地好友
							newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() + moneyBorrowModel.getProjectAmount());
						}
					} else if(oldProject.getId().equals(newProject.getId())){
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - oldMoneyBorrowModel.getProjectAmount() + moneyBorrowModel.getProjectAmount());
						if(moneyBorrowModel.getLocalFriendId() == null) { // 现在是网络好友
							if(oldMoneyBorrowModel.getLocalFriendId() != null){ // 之前是本地好友
								newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() - moneyBorrowModel.getProjectAmount());
							} 
						} else { // 现在是本地好友
							if(oldMoneyBorrowModel.getLocalFriendId() != null){ // 之前也是本地好友
								newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() - oldMoneyBorrowModel.getProjectAmount() + moneyBorrowModel.getProjectAmount());
							} else { // 之前是网络好友
								newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() + oldMoneyBorrowModel.getProjectAmount());
							}
						}
					} else {
						HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
						oldProjectEditor.getModelCopy().setIncomeTotal(oldProject.getIncomeTotal() - oldMoneyBorrowModel.getProjectAmount());
						if(oldMoneyBorrowModel.getLocalFriendId() != null){ // 之前是本地好友
							oldProjectEditor.getModelCopy().setExpenseTotal(oldProject.getExpenseTotal() - oldMoneyBorrowModel.getProjectAmount());
						}
						oldProjectEditor.save();
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() + moneyBorrowModel.getProjectAmount());
						if(moneyBorrowModel.getLocalFriendId() != null){ // 现在是本地好友
							newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() + moneyBorrowModel.getProjectAmount());
						}
					}
					newProjectEditor.save();

					MoneyAccount newDebtAccount = null;
					// 如果不是圈子成员，更新借贷账户
					newDebtAccount = MoneyAccount.getDebtAccount(
							moneyBorrowModel.getProject().getCurrencyId(),
							moneyBorrowModel.getLocalFriendId(),
							moneyBorrowModel.getFriendUserId());
					if (moneyBorrowModel.get_mId() == null) {
						if (newDebtAccount != null) {
							HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
							newDebtAccountEditor
									.getModelCopy()
									.setCurrentBalance(
											newDebtAccount.getCurrentBalance()
													- moneyBorrowModel
															.getProjectAmount());
							newDebtAccountEditor.save();
						} else {
							MoneyAccount
									.createDebtAccount(
											moneyBorrowModel.getFriendDisplayName(),
											moneyBorrowModel.getLocalFriendId(),
											moneyBorrowModel.getFriendUserId(),
											moneyBorrowModel.getProject()
													.getCurrencyId(),
											moneyBorrowModel.getProject().getOwnerUserId(),
											-moneyBorrowModel
													.getProjectAmount());
						}
					} else {
						MoneyAccount oldDebtAccount = null;
						oldDebtAccount = MoneyAccount.getDebtAccount(
								oldMoneyBorrowModel.getProject()
										.getCurrencyId(), oldMoneyBorrowModel
										.getLocalFriendId(),
								oldMoneyBorrowModel.getFriendUserId());
						if (newDebtAccount != null) {
							HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount
									.newModelEditor();
							if (oldDebtAccount != null
									&& oldDebtAccount.getId().equals(
											newDebtAccount.getId())) {
								newDebtAccountEditor
										.getModelCopy()
										.setCurrentBalance(
												newDebtAccount
														.getCurrentBalance()
														+ oldMoneyBorrowModel
																.getProjectAmount()
														- moneyBorrowModel
																.getProjectAmount());
							} else {
								newDebtAccountEditor
										.getModelCopy()
										.setCurrentBalance(
												newDebtAccount
														.getCurrentBalance()
														- moneyBorrowModel
																.getProjectAmount());
								if (oldDebtAccount != null) {
									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount
											.newModelEditor();
									oldDebtAccountEditor
											.getModelCopy()
											.setCurrentBalance(
													oldDebtAccount
															.getCurrentBalance()
															+ oldMoneyBorrowModel
																	.getProjectAmount());
									oldDebtAccountEditor.save();
								}
							}
							newDebtAccountEditor.save();
						} else {
							if (oldDebtAccount != null) {
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount
										.newModelEditor();
								oldDebtAccountEditor
										.getModelCopy()
										.setCurrentBalance(
												oldDebtAccount
														.getCurrentBalance()
														+ oldMoneyBorrowModel
																.getProjectAmount());
								oldDebtAccountEditor.save();
							}

							MoneyAccount
									.createDebtAccount(
											moneyBorrowModel.getFriendDisplayName(),
											moneyBorrowModel.getLocalFriendId(),
											moneyBorrowModel.getFriendUserId(),
											moneyBorrowModel.getProject()
													.getCurrencyId(),
													moneyBorrowModel.getProject().getOwnerUserId(),
											-moneyBorrowModel
													.getProjectAmount());
						}
					}

					// 更新自己（借入人）的实际借入
					ProjectShareAuthorization selfProjectAuthorization = ProjectShareAuthorization
							.getSelfProjectShareAuthorization(moneyBorrowModel
									.getProjectId());
					HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization
							.newModelEditor();
					if (moneyBorrowModel.get_mId() == null
							|| oldMoneyBorrowModel.getProjectId().equals(
									moneyBorrowModel.getProjectId())) {
						selfProjectAuthorizationEditor.getModelCopy()
								.setActualTotalBorrow(
										selfProjectAuthorization
												.getActualTotalBorrow()
												- oldMoneyBorrowModel
														.getProjectAmount()
												+ moneyBorrowModel
														.getProjectAmount());
					} else {
						ProjectShareAuthorization oldSelfProjectAuthorization = ProjectShareAuthorization
								.getSelfProjectShareAuthorization(oldMoneyBorrowModel
										.getProjectId());
						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization
								.newModelEditor();
						oldSelfProjectAuthorizationEditor.getModelCopy()
								.setActualTotalBorrow(
										oldSelfProjectAuthorization
												.getActualTotalBorrow()
												- oldMoneyBorrowModel
														.getProjectAmount());
						selfProjectAuthorizationEditor.getModelCopy()
								.setActualTotalBorrow(
										selfProjectAuthorization
												.getActualTotalBorrow()
												+ moneyBorrowModel
														.getProjectAmount());
						oldSelfProjectAuthorizationEditor.save();
					}
					selfProjectAuthorizationEditor.save();

					MoneyLend moneyLend = null;
					if(oldMoneyBorrowModel.getLocalFriendId() != null){
						moneyLend = new Select().from(MoneyLend.class).where("moneyBorrowId=? AND ownerFriendId=?", oldMoneyBorrowModel.getId(), oldMoneyBorrowModel.getLocalFriendId()).executeSingle();
					}
					if (moneyLend == null){
						moneyLend = new MoneyLend();
					}
					
					// 更新对方（借出人）的实际借出
					if (moneyBorrowModel.getLocalFriendId() != null) { // 现在是本地好友
						ProjectShareAuthorization lendProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", moneyBorrowModel.getLocalFriendId(), moneyBorrowModel.getProjectId()).executeSingle();
						HyjModelEditor<ProjectShareAuthorization> lendProjectAuthorizationEditor = lendProjectAuthorization.newModelEditor();

						if (moneyBorrowModel.get_mId() == null 
								|| oldMoneyBorrowModel.getLocalFriendId() == null) { // 之前是网络好友，不用维护
							lendProjectAuthorizationEditor.getModelCopy()
							.setActualTotalLend(
									lendProjectAuthorization
											.getActualTotalLend()
											+ moneyBorrowModel
													.getProjectAmount());
						} else if(oldMoneyBorrowModel.getProjectId().equals(
										moneyBorrowModel.getProjectId()) && oldMoneyBorrowModel.getLocalFriendId().equals(
												moneyBorrowModel.getLocalFriendId())) {
							// 新旧ProjectShareAuthorization是一样的
							lendProjectAuthorizationEditor.getModelCopy()
							.setActualTotalLend(
									lendProjectAuthorization
											.getActualTotalLend()
											- oldMoneyBorrowModel
													.getProjectAmount()
											+ moneyBorrowModel
													.getProjectAmount());
							
						} else {
							// 更新旧的ProjectShareAuthorization
							ProjectShareAuthorization oldSelfProjectAuthorization = null;
							oldSelfProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", oldMoneyBorrowModel.getLocalFriendId(), oldMoneyBorrowModel.getProjectId()).executeSingle();
							
							HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
							oldSelfProjectAuthorizationEditor.getModelCopy()
									.setActualTotalLend(
											oldSelfProjectAuthorization
													.getActualTotalLend()
													- oldMoneyBorrowModel
															.getProjectAmount());
							lendProjectAuthorizationEditor.getModelCopy()
									.setActualTotalLend(
											lendProjectAuthorization
													.getActualTotalLend()
													+ moneyBorrowModel
															.getProjectAmount());
							oldSelfProjectAuthorizationEditor.save();
						}

						moneyLend.setMoneyBorrowId(moneyBorrowModel.getId());
						moneyLend.setAmount(moneyBorrowModel.getAmount0());
						moneyLend.setDate(moneyBorrowModel.getDate());
						moneyLend.setRemark(moneyBorrowModel.getRemark());
						moneyLend.setProject(moneyBorrowModel.getProject());
						moneyLend.setFriendAccountId(moneyBorrowModel.getFriendAccountId());
						moneyLend.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
						moneyLend.setLocalFriendId(null);
						moneyLend.setExchangeRate(moneyBorrowModel.getExchangeRate());
						moneyLend.setMoneyAccountId(null, moneyBorrowModel.getCurrencyId1());
						moneyLend.setLocation(moneyBorrowModel.getLocation());
						moneyLend.setGeoLat(moneyBorrowModel.getGeoLat());
						moneyLend.setGeoLon(moneyBorrowModel.getGeoLon());
						moneyLend.setAddress(moneyBorrowModel.getAddress());
						moneyLend.setPictureId(moneyBorrowModel.getPictureId());
						moneyLend.setOwnerFriendId(moneyBorrowModel.getLocalFriendId());
						moneyLend.setOwnerUserId("");
						moneyLend.save();
						lendProjectAuthorizationEditor.save();
					} else if(oldMoneyBorrowModel.getLocalFriendId() != null){ 
						moneyLend.delete();
							
						// 更新旧的ProjectShareAuthorization
						ProjectShareAuthorization oldSelfProjectAuthorization = null;
						oldSelfProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", oldMoneyBorrowModel.getLocalFriendId(), oldMoneyBorrowModel.getProjectId()).executeSingle();
						
						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
						oldSelfProjectAuthorizationEditor.getModelCopy()
								.setActualTotalLend(
										oldSelfProjectAuthorization
												.getActualTotalLend()
												- oldMoneyBorrowModel
														.getProjectAmount());
						oldSelfProjectAuthorizationEditor.save();
					}
					
					mMoneyBorrowEditor.save();
					ActiveAndroid.setTransactionSuccessful();
					if (getActivity().getCallingActivity() != null) {
						getActivity().setResult(Activity.RESULT_OK);
					} else {
						HyjUtil.displayToast(R.string.app_save_success);
					}
					getActivity().finish();
				} finally {
					ActiveAndroid.endTransaction();
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GET_MONEYACCOUNT_ID:
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				MoneyAccount moneyAccount = MoneyAccount.load(
						MoneyAccount.class, _id);
				mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "("
						+ moneyAccount.getCurrencyId() + ")");
				mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
				setExchangeRate(false);
			}
			break;
		case GET_PROJECT_ID:
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				Project project = Project.load(Project.class, _id);

				// 检查有没有新增借入的权限
				ProjectShareAuthorization psa = ProjectShareAuthorization.getSelfProjectShareAuthorization(project.getId());
				if (mMoneyBorrowEditor.getModelCopy().get_mId() == null
						&& !psa.getProjectShareMoneyExpenseAddNew()) {
					HyjUtil.displayToast(R.string.app_permission_no_addnew);
					return;
				} else if (mMoneyBorrowEditor.getModelCopy().get_mId() != null
						&& !psa.getProjectShareMoneyExpenseEdit()) {
					HyjUtil.displayToast(R.string.app_permission_no_edit);
					return;
				}

//				if(project.getFinancialOwnerUserId() != null){
//					mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
//					mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
//				} else {
//					mSelectorFieldFinancialOwner.setModelId(null);
//					mSelectorFieldFinancialOwner.setText(null);
//				}
					
				mSelectorFieldProject.setText(project.getDisplayName() + "("
						+ project.getCurrencyId() + ")");
				mSelectorFieldProject.setModelId(project.getId());
				setExchangeRate(false);

				// 看一下好友是不是新圈子的成员
				if (mSelectorFieldFriend.getModelId() != null) {
					ProjectShareAuthorization psaMember = null;
					if ((Boolean) mSelectorFieldFriend
							.getTag(TAG_IS_LOCAL_FRIEND)) {
						String localFriendId = mSelectorFieldFriend
								.getModelId();
						psaMember = new Select()
								.from(ProjectShareAuthorization.class)
								.where("projectId = ? AND localFriendId=? AND state <> 'Delete'",
										project.getId(), localFriendId)
								.executeSingle();
					
						if (psaMember == null) {
							mSelectorFieldFriend.setText(null);
							mSelectorFieldFriend.setModelId(null);
							mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
							HyjUtil.displayToast(R.string.moneyBorrowFormFragment_editText_error_friend_not_member);
						}
					}
				}
				List<Event> events = new Select().from(Event.class).where("projectId = ?", project.getId()).execute();
				if(events.size() > 0) {
					mSelectorFieldEvent.setVisibility(View.VISIBLE);
					mViewSeparatorEvent.setVisibility(View.VISIBLE);
				} else {
					mSelectorFieldEvent.setVisibility(View.GONE);
					mViewSeparatorEvent.setVisibility(View.GONE);
				}
				mSelectorFieldEvent.setText(null);
				mSelectorFieldEvent.setModelId(null);
        	 }
        	 break;
         case GET_EVENT_ID:
 			if (resultCode == Activity.RESULT_OK) {
 				long _id = data.getLongExtra("MODEL_ID", -1);
 				if(_id == -1){
 					mSelectorFieldEvent.setText(null);
 					mSelectorFieldEvent.setModelId(null);
 				} else {
 					Event event = Event.load(Event.class, _id);
 					mSelectorFieldEvent.setText(event.getName());
 					mSelectorFieldEvent.setModelId(event.getId());
 				}
 			}
 			break;
		case GET_REMARK:
			if (resultCode == Activity.RESULT_OK) {
				String text = data.getStringExtra("TEXT");
				mRemarkFieldRemark.setText(text);
			}
			break;
		case GET_AMOUNT:
			if (resultCode == Activity.RESULT_OK) {
				String calculatorAmount = data.getStringExtra("calculatorAmount");
				if (calculatorAmount != null){
					mNumericAmount.setNumber(Double.parseDouble(calculatorAmount));
				}
			}
			break;
		case GET_FRIEND_ID:
         	 if (resultCode == Activity.RESULT_OK) {
        		 long _id = data.getLongExtra("MODEL_ID", -1);

  	   	       	if(_id == -1){
 	   	       		mSelectorFieldFriend.setText(null);
 	   	       		mSelectorFieldFriend.setModelId(null);
  	       		} else {
	 				String type = data.getStringExtra("MODEL_TYPE");
	
	 				ProjectShareAuthorization psa = null;
	 				if ("ProjectShareAuthorization".equalsIgnoreCase(type)) {
	 					psa = ProjectShareAuthorization.load(
	 							ProjectShareAuthorization.class, _id);
	 					if (psa.getFriendUserId() != null) {
	 						// 不能选择自己作为债务人
	 						if (psa.getFriendUserId().equals(
	 								HyjApplication.getInstance().getCurrentUser()
	 										.getId())) {
	 							HyjUtil.displayToast(R.string.moneyBorrowFormFragment_editText_error_friend);
	 							break;
	 						}
	 						mSelectorFieldFriend.setText(psa.getFriendDisplayName());
	 						mSelectorFieldFriend.setModelId(psa.getFriendUserId());
	 						mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
	 					} else {
		     				mSelectorFieldFriend.setText(psa.getFriendDisplayName());
		     				mSelectorFieldFriend.setModelId(psa.getLocalFriendId());
		     				mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, true);
	 					}
	 				} else {
	 					final Friend friend = Friend.load(Friend.class, _id);
	 					if (friend.getFriendUserId() != null) {
	 						// 不能选择自己作为债务人
	 						if (friend.getFriendUserId().equals(
	 								HyjApplication.getInstance().getCurrentUser()
	 										.getId())) {
	 							HyjUtil.displayToast(R.string.moneyBorrowFormFragment_editText_error_friend);
	 							break;
	 						}
								psa = new Select()
									.from(ProjectShareAuthorization.class)
									.where("friendUserId=? AND projectId=? AND state <> 'Delete'",
											friend.getFriendUserId(),
											mSelectorFieldProject.getModelId())
									.executeSingle();
	 					} else {
								psa = new Select()
									.from(ProjectShareAuthorization.class)
									.where("localFriendId=? AND projectId=? AND state <> 'Delete'",
											friend.getId(),
											mSelectorFieldProject.getModelId())
									.executeSingle();
	 					}
	
						if(psa == null){
							((HyjActivity)getActivity()).displayDialog(R.string.moneyBorrowFormFragment_editText_error_friend_not_member, R.string.moneyApportionField_select_confirm_apportion_add_as_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
									new DialogCallbackListener() {
										@Override
										public void doPositiveClick(Object object) {
											Bundle bundle = new Bundle();
											bundle.putString("PROJECTID", mSelectorFieldProject.getModelId());
											if(friend.getFriendUserId() != null){
												bundle.putString("FRIEND_USERID", friend.getFriendUserId());
											} else {
												bundle.putString("LOCAL_FRIENDID", friend.getId());
											}
											openActivityWithFragmentForResult(ProjectMemberFormFragment.class, R.string.memberFormFragment_title_addnew, bundle, ADD_AS_PROJECT_MEMBER);
										}
				
										@Override
										public void doNegativeClick() {
											HyjUtil.displayToast(R.string.moneyBorrowFormFragment_editText_error_friend_not_member);
										}
									});
							
		//					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
							break;
						}
	     				mSelectorFieldFriend.setText(psa.getFriendDisplayName());
	     				mSelectorFieldFriend.setModelId(psa.getLocalFriendId());
	     				mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, true);
	 				}
  	       		}
  			}
  			break;
 		case ADD_AS_PROJECT_MEMBER:
 			if (resultCode == Activity.RESULT_OK) {
 				String id = data.getStringExtra("MODELID");
 				ProjectShareAuthorization psa = HyjModel.getModel(ProjectShareAuthorization.class, id);
 				if(psa != null){
						mSelectorFieldFriend.setText(psa.getFriendDisplayName());
						if(psa.getLocalFriendId() != null){
							mSelectorFieldFriend.setModelId(psa.getLocalFriendId());
							mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, true);
						} else {
							mSelectorFieldFriend.setModelId(psa.getFriendUserId());
							mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
						}
 				} else {
 					HyjUtil.displayToast(R.string.moneyBorrowFormFragment_editText_error_friend_not_member);
 				}
 			} else {
 				HyjUtil.displayToast(R.string.moneyBorrowFormFragment_editText_error_friend_not_member);
 				
 			}
 			break;
        case GET_FINANCIALOWNER_ID:
	       	 if(resultCode == Activity.RESULT_OK){
	       		long _id = data.getLongExtra("MODEL_ID", -1);
	       		if(_id == -1){
//		       		mSelectorFieldFinancialOwner.setText(null);
//		       		mSelectorFieldFinancialOwner.setModelId(null);
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
		       		
//		       		mSelectorFieldFinancialOwner.setText(psa.getFriendDisplayName());
//		       		mSelectorFieldFinancialOwner.setModelId(psa.getFriendUserId());
	       		}
	       	 }
	       	 break;
		}
	}
}
