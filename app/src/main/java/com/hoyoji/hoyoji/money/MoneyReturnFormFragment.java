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
import android.view.WindowManager;
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
import com.hoyoji.hoyoji.event.EventListFragment;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.MoneyReturn;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.project.ProjectMemberFormFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;


public class MoneyReturnFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_FRIEND_ID = 3;
	private static final int GET_REMARK = 4;
	private final static int GET_AMOUNT = 8;
	private final static int GET_EVENT_ID = 9;
	private static final int TAG_IS_LOCAL_FRIEND = R.id.moneyLendFormFragment_selectorField_friend;
	private static final int ADD_AS_PROJECT_MEMBER = 0;
	protected static final int GET_FINANCIALOWNER_ID = 5;
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private HyjModelEditor<MoneyReturn> mMoneyReturnEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericFieldAmount = null;
	private HyjNumericField mNumericFieldInterest = null;
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
	
	private ImageButton mButtonExpandMore;
	private LinearLayout mLinearLayoutExpandMore;
	
	private ImageButton calculatorTextView = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneyreturn;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		MoneyReturn moneyReturn;
		
		Intent intent = getActivity().getIntent();
		final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			moneyReturn =  HyjModel.load(MoneyReturn.class, modelId); //new Select().from(MoneyReturn.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyReturn.hasEditPermission();
		} else {
			moneyReturn = new MoneyReturn();
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyAccountId != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, moneyAccountId);
				moneyReturn.setMoneyAccountId(moneyAccountId, moneyAccount.getCurrencyId());
			}
			if(intent.getStringExtra("counterpartId") != null){
				moneyReturn.setIsImported(true);
			}
		}
		mMoneyReturnEditor = moneyReturn.newModelEditor();
		
		setupDeleteButton(mMoneyReturnEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyReturnFormFragment_imageField_picture);		
		mImageFieldPicture.setImages(moneyReturn.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyReturnFormFragment_textField_date);		
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyReturn.getDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
			}
		}
		
		mNumericFieldAmount = (HyjNumericField) getView().findViewById(R.id.moneyReturnFormFragment_textField_amount);		
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
			mNumericFieldAmount.setNumber(amount*exchangeRate);
		} else {
			mNumericFieldAmount.setNumber(moneyReturn.getAmount());
		}
		
		mNumericFieldInterest = (HyjNumericField) getView().findViewById(R.id.moneyReturnFormFragment_textField_interest);		
		mNumericFieldInterest.setNumber(moneyReturn.getInterest());
		
		MoneyAccount moneyAccount = moneyReturn.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(R.id.moneyReturnFormFragment_selectorField_moneyAccount);

		if(moneyAccount != null){
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyReturnFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_MONEYACCOUNT_ID);
			}
		});	
		
		Long dateImport = intent.getLongExtra("date", -1);
		if(dateImport != -1){
			Date date= new Date(dateImport);
			mDateTimeFieldDate.setDate(date);
			mDateTimeFieldDate.setTextColor(Color.RED);
		}
		
		Project project;
		String projectId = intent.getStringExtra("projectId"); // 从消息导入
		if(moneyReturn.get_mId() == null && projectId != null) {
			project = HyjModel.getModel(Project.class, projectId);
		} else {
			project = moneyReturn.getProject(); // 获取当前圈子
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyReturnFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyReturnFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.projectListFragment_title_select_project, null, GET_PROJECT_ID);
			}
		});	
		
		mSelectorFieldEvent = (HyjSelectorField) getView().findViewById(R.id.moneyExpenseContainerFormFragment_selectorField_event);
		mViewSeparatorEvent = (View) getView().findViewById(R.id.field_separator_event);

		if(project != null){
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
			if(moneyReturn.get_mId() == null){
				if(eventId != null) {
					moneyReturn.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyReturn.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyReturn.getEvent();
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

                    MoneyReturnFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyReturnFormFragment_textField_exchangeRate);		
		mNumericExchangeRate.setNumber(moneyReturn.getExchangeRate());
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyReturnFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyReturnFormFragment_linearLayout_exchangeRate);

		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyReturnFormFragment_selectorField_friend);
		mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
		if(moneyReturn.get_mId() == null){
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
			if (moneyReturn.getLocalFriendId() != null) {
				mSelectorFieldFriend.setText(moneyReturn.getFriendDisplayName());
				mSelectorFieldFriend.setModelId(moneyReturn.getLocalFriendId());
				mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, true);
			} else if (moneyReturn.getFriendUserId() != null) {
				mSelectorFieldFriend.setModelId(moneyReturn.getFriendUserId());
				mSelectorFieldFriend.setText(moneyReturn.getFriendDisplayName());
				mSelectorFieldFriend.setTag(TAG_IS_LOCAL_FRIEND, false);
			}
		}
		
		mSelectorFieldFriend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
                if(mSelectorFieldProject.getModelId() == null){
                    HyjUtil.displayToast("请选选择一个圈子");
                    return;
                }
				Bundle bundle = new Bundle();
				Project project = HyjModel.getModel(Project.class,mSelectorFieldProject.getModelId());
				bundle.putLong("MODEL_ID", project.get_mId());
//				bundle.putString("NULL_ITEM", (String) mSelectorFieldFriend.getHint());
				bundle.putBoolean("disableMultiChoiceMode", true);
				if(!project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.friendListFragment_title_select_friend_creditor, bundle, GET_FRIEND_ID);
				} else {
					openActivityWithFragmentForResult(SelectApportionMemberListFragment.class, R.string.friendListFragment_title_select_friend_creditor, bundle, GET_FRIEND_ID);
				}
			}
		}); 
		
		
//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyReturnFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});
		
		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyReturnFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyReturn.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyReturnFormFragment.this
						.openActivityWithFragmentForResult(
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
				bundle.putDouble("AMOUNT", mNumericFieldAmount.getNumber()!=null?mNumericFieldAmount.getNumber():0.00);
				MoneyReturnFormFragment.this.openActivityWithFragmentForResult(
								HyjCalculatorFormFragment.class,
								R.string.hyjCalculatorFormFragment_title,
								bundle, GET_AMOUNT);
			}
		});

//		mSelectorFieldFinancialOwner = (HyjSelectorField) getView().findViewById(R.id.projectFormFragment_selectorField_financialOwner);
//		mSelectorFieldFinancialOwner.setEnabled(hasEditPermission);
//		if(modelId == -1){
//			if(project.getFinancialOwnerUserId() != null){
//				mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
//				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
//			}
//		} else if(moneyReturn.getFinancialOwnerUserId() != null){
//				mSelectorFieldFinancialOwner.setModelId(moneyReturn.getFinancialOwnerUserId());
//				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyReturn.getFinancialOwnerUserId()));
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
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyReturnFormFragment_imageView_camera);	
		takePictureButton.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
		takePictureButton.setOnClickListener(new OnClickListener(){
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
				
				if(!hasEditPermission){
					for(int i = 0; i<popup.getMenu().size();i++){
						popup.getMenu().setGroupEnabled(i, false);
					}
				}
				
				popup.show();	
			}
		});
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyReturnFormFragment_imageButton_refresh_exchangeRate);	
		mImageViewRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectorFieldMoneyAccount.getModelId() != null && mSelectorFieldProject.getModelId() != null){
					MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mSelectorFieldMoneyAccount.getModelId());
					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
					
					String fromCurrency = moneyAccount.getCurrencyId();
					String toCurrency = project.getCurrencyId();
					if(fromCurrency != null && toCurrency != null){
						HyjUtil.startRoateView(mImageViewRefreshRate);
						mImageViewRefreshRate.setEnabled(false);
						HyjUtil.updateExchangeRate(fromCurrency, toCurrency, mImageViewRefreshRate, mNumericExchangeRate);
					} else {
						HyjUtil.displayToast(R.string.moneyReturnFormFragment_toast_select_currency);
					}
				}else{
					HyjUtil.displayToast(R.string.moneyReturnFormFragment_toast_select_currency);
				}
			}
		});
		
	
		// 只在新增时才自动打开软键盘， 修改时不自动打开
		if (modelId == -1) {
			setExchangeRate(false);
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}else{
			setExchangeRate(true);
		}
		setPermission();
		
		mLinearLayoutExpandMore = (LinearLayout)getView().findViewById(R.id.moneyExpenseFormFragment_expandMore);
		mButtonExpandMore = (ImageButton)getView().findViewById(R.id.expand_more);
		mButtonExpandMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mLinearLayoutExpandMore.getVisibility() == View.GONE){
					mButtonExpandMore.setImageResource(R.drawable.ic_action_collapse);
					mLinearLayoutExpandMore.setVisibility(View.VISIBLE);
				} else {
					mButtonExpandMore.setImageResource(R.drawable.ic_action_expand);
					mLinearLayoutExpandMore.setVisibility(View.GONE);
				}
			}
		});
		// 在修改模式下自动展开
		if(modelId != -1){
			mButtonExpandMore.setImageResource(R.drawable.ic_action_collapse);
			mLinearLayoutExpandMore.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    if(mMoneyReturnEditor!= null && mMoneyReturnEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
	
	private void setupDeleteButton(HyjModelEditor<MoneyReturn> moneyReturnEditor) {

		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
		
		final MoneyReturn moneyReturn = moneyReturnEditor.getModelCopy();
		
		if (moneyReturn.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(moneyReturn.hasDeletePermission()){
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									try {
										ActiveAndroid.beginTransaction();

										MoneyAccount moneyAccount = moneyReturn.getMoneyAccount();
										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() + moneyReturn.getAmount() + moneyReturn.getInterest0());
										moneyAccountEditor.save();
										
										//更新圈子余额
										Project newProject = moneyReturn.getProject();
										HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
										newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() - moneyReturn.getProjectAmount());
										if(moneyReturn.getLocalFriendId() != null){
											newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - moneyReturn.getProjectAmount());
										}
										newProjectEditor.save();

										MoneyAccount debtAccount = MoneyAccount.getDebtAccount(moneyReturn.getProject().getCurrencyId(), moneyReturn.getLocalFriendId(), moneyReturn.getFriendUserId());
										HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
										debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - moneyReturn.getProjectAmount());
										debtAccountEditor.save();
										
										ProjectShareAuthorization projectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(moneyReturn.getProjectId());
										HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = projectAuthorization.newModelEditor();
									    selfProjectAuthorizationEditor.getModelCopy().setActualTotalReturn(projectAuthorization.getActualTotalReturn() - moneyReturn.getProjectAmount());
										
									    selfProjectAuthorizationEditor.save();
									    if(moneyReturn.getLocalFriendId() != null){
											MoneyPayback moneyPayback;
											moneyPayback = new Select().from(MoneyPayback.class).where("moneyReturnId=? AND ownerFriendId=?", moneyReturn.getId(), moneyReturn.getLocalFriendId()).executeSingle();
											moneyPayback.delete();
											
											// 更新旧的ProjectShareAuthorization
											ProjectShareAuthorization oldSelfProjectAuthorization = null;
											oldSelfProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", moneyReturn.getLocalFriendId(), moneyReturn.getProjectId()).executeSingle();
											
											HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
											oldSelfProjectAuthorizationEditor.getModelCopy()
													.setActualTotalPayback(
															oldSelfProjectAuthorization
																	.getActualTotalPayback()
																	- moneyReturn.getProjectAmount());
											oldSelfProjectAuthorizationEditor.save();
										}
										moneyReturn.delete();
										
										HyjUtil.displayToast(R.string.app_delete_success);
										ActiveAndroid.setTransactionSuccessful();
										ActiveAndroid.endTransaction();
										getActivity().finish();
									} catch (Exception e) {
										ActiveAndroid.endTransaction();
										HyjUtil.displayToast(R.string.app_delete_failed);
									} 
								}
							});
					}else{
						HyjUtil.displayToast(R.string.app_permission_no_delete);
					}
				}
			});
		}
		
	}
	
	private void setPermission(){

		if(mMoneyReturnEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
//			mNumericFieldAmount.setNumber(mMoneyReturnEditor.getModel().getProjectAmount());
			mNumericFieldAmount.setEnabled(false);
			
			mSelectorFieldFriend.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyReturnFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

			mSelectorFieldProject.setEnabled(false);
			mSelectorFieldEvent.setEnabled(false);
			mNumericExchangeRate.setEnabled(false);

			mNumericFieldInterest.setEnabled(false);
			
			mRemarkFieldRemark.setEnabled(false);
			
			if(this.mOptionsMenu != null){
		    	hideSaveAction();
			}

//			getView().findViewById(R.id.button_save).setEnabled(false);	
			getView().findViewById(R.id.button_delete).setEnabled(false);
			getView().findViewById(R.id.button_delete).setVisibility(View.GONE);
		}
	}
	
	private void setExchangeRate(Boolean editInit){
		if(mSelectorFieldMoneyAccount.getModelId() != null && mSelectorFieldProject.getModelId()!= null){
			MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class,mSelectorFieldMoneyAccount.getModelId());
			Project project = HyjModel.getModel(Project.class,mSelectorFieldProject.getModelId());
			
			String fromCurrency = moneyAccount.getCurrencyId();
			String toCurrency = project.getCurrencyId();
			
			if(fromCurrency.equals(toCurrency)){
				if(SET_EXCHANGE_RATE_FLAG != 1){//新增或修改打开时不做setNumber
					mNumericExchangeRate.setNumber(1.00);
					CREATE_EXCHANGE = 0;
				}
				mViewSeparatorExchange.setVisibility(View.GONE);
				mLinearLayoutExchangeRate.setVisibility(View.GONE);
			}else{
				mViewSeparatorExchange.setVisibility(View.VISIBLE);
				mLinearLayoutExchangeRate.setVisibility(View.VISIBLE);
				
				if(!editInit){//修改时init不需要set Rate
					Double rate = Exchange.getExchangeRate(fromCurrency, toCurrency);
					if(rate != null){
						mNumericExchangeRate.setNumber(rate);
						CREATE_EXCHANGE = 0;
					}else{
						mNumericExchangeRate.setNumber(null);
						CREATE_EXCHANGE = 1;
					}
				}
			}
			
		}else{
			mViewSeparatorExchange.setVisibility(View.GONE);
			mLinearLayoutExchangeRate.setVisibility(View.GONE);
		}
			SET_EXCHANGE_RATE_FLAG = 0;
	}
	
	private void fillData(){
		MoneyReturn modelCopy = (MoneyReturn) mMoneyReturnEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericFieldAmount.getNumber());
		modelCopy.setInterest(mNumericFieldInterest.getNumber());
//		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
		if(mSelectorFieldMoneyAccount.getModelId() != null){
			MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mSelectorFieldMoneyAccount.getModelId());
			modelCopy.setMoneyAccountId(mSelectorFieldMoneyAccount.getModelId(), moneyAccount.getCurrencyId());
		} else {
			modelCopy.setMoneyAccountId(null, null);
		}
        if(mSelectorFieldProject.getModelId() != null) {
            modelCopy.setProject(HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId()));
        }
		modelCopy.setEventId(mSelectorFieldEvent.getModelId());
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());
		
//		if(mSelectorFieldFriend.getModelId() != null){
//			Friend friend = HyjModel.getModel(Friend.class, mSelectorFieldFriend.getModelId());
//			modelCopy.setFriend(friend);
//		}else{
//			modelCopy.setFriend(null);
//		}
		if(mSelectorFieldFriend.getModelId() != null){
			if((Boolean) mSelectorFieldFriend.getTag(TAG_IS_LOCAL_FRIEND)){
				modelCopy.setLocalFriendId(mSelectorFieldFriend.getModelId());
				modelCopy.setFriendUserId(null);
			} else {
				modelCopy.setFriendUserId(mSelectorFieldFriend.getModelId());
				modelCopy.setLocalFriendId(null);
			}
		}else{
			modelCopy.setLocalFriendId(null);
			modelCopy.setFriendUserId(null);
		}
		
		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyReturnEditor.getValidationError("datetime"));
		mNumericFieldAmount.setError(mMoneyReturnEditor.getValidationError("amount"));
		if(mMoneyReturnEditor.getValidationError("amount") != null){
			mNumericFieldAmount.showSoftKeyboard();
		}
		mNumericFieldInterest.setError(mMoneyReturnEditor.getValidationError("interest"));
		mSelectorFieldMoneyAccount.setError(mMoneyReturnEditor.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyReturnEditor.getValidationError("project"));
		mSelectorFieldEvent.setError(mMoneyReturnEditor.getValidationError("Event"));
		mNumericExchangeRate.setError(mMoneyReturnEditor.getValidationError("exchangeRate"));
		mSelectorFieldFriend.setError(mMoneyReturnEditor.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyReturnEditor.getValidationError("remark"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		if(mMoneyReturnEditor.getModelCopy().get_mId() == null && !mMoneyReturnEditor.getModelCopy().hasAddNewPermission(mMoneyReturnEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyReturnEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else{
			
			if(mMoneyReturnEditor.getModelCopy().getFriendUserId() == null && mMoneyReturnEditor.getModelCopy().getLocalFriendId() == null){
				mMoneyReturnEditor.setValidationError("friend",R.string.moneyReturnFormFragment_editText_hint_friend);
			}else{
				mMoneyReturnEditor.removeValidationError("friend");
			}
			
		mMoneyReturnEditor.validate();
		
		if(mMoneyReturnEditor.hasValidationErrors()){
			showValidatioErrors();
		} else {
			try {
				ActiveAndroid.beginTransaction();
				HyjImageField.ImageGridAdapter adapter = mImageFieldPicture.getAdapter();
				int count = adapter.getCount();
				boolean mainPicSet = false;
				for(int i = 0; i < count; i++){
					PictureItem pi = adapter.getItem(i);
					if(pi.getState() == PictureItem.NEW){
						Picture newPic = pi.getPicture();
						newPic.setRecordId(mMoneyReturnEditor.getModel().getId());
						newPic.setRecordType("MoneyReturn");
						newPic.setProjectId(mMoneyReturnEditor.getModelCopy().getProjectId());
						newPic.setDisplayOrder(i);
						newPic.save();
					} else if(pi.getState() == PictureItem.DELETED){
						pi.getPicture().delete();
					} else if(pi.getState() == PictureItem.CHANGED){

					}
					if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
						mainPicSet = true;
						mMoneyReturnEditor.getModelCopy().setPicture(pi.getPicture());
					}
				}
				
				MoneyReturn oldMoneyReturnModel = mMoneyReturnEditor.getModel();
				MoneyReturn moneyReturnModel = mMoneyReturnEditor.getModelCopy();
				
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				if(moneyReturnModel.get_mId() == null && !userData.getActiveMoneyAccountId().equals(moneyReturnModel.getMoneyAccountId())){
					HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
					userDataEditor.getModelCopy().setActiveMoneyAccountId(moneyReturnModel.getMoneyAccountId());
					userDataEditor.getModelCopy().setActiveProjectId(moneyReturnModel.getProjectId());
					userDataEditor.save();
				}
				
				//设置默认活动
				Project project = moneyReturnModel.getProject();
				if(moneyReturnModel.get_mId() == null){
					if((moneyReturnModel.getEventId() != null && !moneyReturnModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(moneyReturnModel.getEventId()))){
						HyjModelEditor<Project> projectEditor = project.newModelEditor();
						projectEditor.getModelCopy().setActiveEventId(moneyReturnModel.getEventId());
						projectEditor.save();
					}
				}
				
				String localCurrencyId = moneyReturnModel.getMoneyAccount().getCurrencyId();
				String foreignCurrencyId = moneyReturnModel.getProject().getCurrencyId();
				if(CREATE_EXCHANGE == 1){
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(moneyReturnModel.getMoneyAccount().getCurrencyId());
					newExchange.setForeignCurrencyId(moneyReturnModel.getProject().getCurrencyId());
					newExchange.setRate(moneyReturnModel.getExchangeRate());
					newExchange.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
					newExchange.save();
				}else {
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = null;
						Double exRate = null;
						Double rate = HyjUtil.toFixed2(moneyReturnModel.getExchangeRate());
						exchange = Exchange.getExchange(localCurrencyId, foreignCurrencyId);
						if(exchange != null){
							exRate = exchange.getRate();
							if(!rate.equals(exRate)){
								HyjModelEditor<Exchange> exchangModelEditor = exchange.newModelEditor();
								exchangModelEditor.getModelCopy().setRate(rate);
								exchangModelEditor.save();
							}
						} else {
							exchange = Exchange.getExchange(foreignCurrencyId, localCurrencyId);
							if(exchange != null){
								exRate = HyjUtil.toFixed2(1 / exchange.getRate());
								if(!rate.equals(exRate)){
									HyjModelEditor<Exchange> exchangModelEditor = exchange.newModelEditor();
									exchangModelEditor.getModelCopy().setRate(1/rate);
									exchangModelEditor.save();
								}
							}
						}
					}
			}
				
//				if(mSelectorFieldMoneyAccount.getModelId() != null){
					Double oldAmount = oldMoneyReturnModel.getAmount0();
//					Double oldInterest = oldMoneyReturnModel.getInterest0();
					MoneyAccount oldMoneyAccount = oldMoneyReturnModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = moneyReturnModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					if(moneyReturnModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + oldAmount - moneyReturnModel.getAmount0());
							
					}else{
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() + oldAmount);
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - moneyReturnModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();
					
					Project oldProject = oldMoneyReturnModel.getProject();
					Project newProject = moneyReturnModel.getProject();
					HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
					
					//更新圈子余额
					if(moneyReturnModel.get_mId() == null){
						newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() + moneyReturnModel.getProjectAmount());
						if(moneyReturnModel.getLocalFriendId() != null){
							newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() + moneyReturnModel.getProjectAmount());
						}
					} else if(oldProject.getId().equals(newProject.getId())){
						newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() - oldMoneyReturnModel.getProjectAmount() + moneyReturnModel.getProjectAmount());
						if(moneyReturnModel.getLocalFriendId() == null) { // 现在是网络好友
							if(oldMoneyReturnModel.getLocalFriendId() != null){ // 之前是本地好友
								newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - moneyReturnModel.getProjectAmount());
							}
						} else { // 现在是本地好友
							if(oldMoneyReturnModel.getLocalFriendId() != null){ // 之前也是本地好友
								newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - oldMoneyReturnModel.getProjectAmount() + moneyReturnModel.getProjectAmount());
							} else { // 之前是网络好友
								newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - oldMoneyReturnModel.getProjectAmount());
							}
						}
					} else {
						
						HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
						oldProjectEditor.getModelCopy().setExpenseTotal(oldProject.getExpenseTotal() - oldMoneyReturnModel.getProjectAmount());
						if(oldMoneyReturnModel.getLocalFriendId() != null){ // 之前是网络好友
							oldProjectEditor.getModelCopy().setIncomeTotal(oldProject.getIncomeTotal() - oldMoneyReturnModel.getProjectAmount());
						}
						oldProjectEditor.save();
						newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() + moneyReturnModel.getProjectAmount());
						if(moneyReturnModel.getLocalFriendId() != null){ // 现在是是网络好友
							newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() + moneyReturnModel.getProjectAmount());
						}
					}
					newProjectEditor.save();
//				}	

					MoneyAccount newDebtAccount = null;
					newDebtAccount = MoneyAccount.getDebtAccount(moneyReturnModel.getProject().getCurrencyId(), moneyReturnModel.getLocalFriendId(), moneyReturnModel.getFriendUserId());
					if(moneyReturnModel.get_mId() == null){
				    	if(newDebtAccount != null) {
				    		HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
				    		newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() + moneyReturnModel.getProjectAmount());
				    		newDebtAccountEditor.save();
				    	}else{
				    		MoneyAccount.createDebtAccount(moneyReturnModel.getFriendDisplayName(), moneyReturnModel.getLocalFriendId(), moneyReturnModel.getFriendUserId(), moneyReturnModel.getProject().getCurrencyId(), moneyReturnModel.getProject().getOwnerUserId(), moneyReturnModel.getProjectAmount());
				    	}
					}else{
						MoneyAccount oldDebtAccount = null;
						oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyReturnModel.getProject().getCurrencyId(), oldMoneyReturnModel.getLocalFriendId(), oldMoneyReturnModel.getFriendUserId());
						if(newDebtAccount != null){
							HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
							if(oldDebtAccount != null && oldDebtAccount.getId().equals(newDebtAccount.getId())){
								newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() - oldMoneyReturnModel.getProjectAmount() + moneyReturnModel.getProjectAmount());
							}else{
								newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() + moneyReturnModel.getProjectAmount());
								if(oldDebtAccount != null){
									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldMoneyReturnModel.getProjectAmount());
									oldDebtAccountEditor.save();
								}
							}
							newDebtAccountEditor.save();
						}else{
							if(oldDebtAccount != null){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldMoneyReturnModel.getProjectAmount());
								oldDebtAccountEditor.save();
							}
							MoneyAccount.createDebtAccount(moneyReturnModel.getFriendDisplayName(), moneyReturnModel.getLocalFriendId(), moneyReturnModel.getFriendUserId(), moneyReturnModel.getProject().getCurrencyId(), moneyReturnModel.getProject().getOwnerUserId(), moneyReturnModel.getProjectAmount());
						}
					}

					//更新支出所有者的实际还款
						ProjectShareAuthorization selfProjectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(moneyReturnModel.getProjectId());
						HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
					    if(moneyReturnModel.get_mId() == null || oldMoneyReturnModel.getProjectId().equals(moneyReturnModel.getProjectId())){
					    	selfProjectAuthorizationEditor.getModelCopy().setActualTotalReturn(selfProjectAuthorization.getActualTotalReturn() - oldMoneyReturnModel.getProjectAmount() + moneyReturnModel.getProjectAmount());
						}else{
							ProjectShareAuthorization oldSelfProjectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(oldMoneyReturnModel.getProjectId());
							HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
							oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalReturn(oldSelfProjectAuthorization.getActualTotalReturn() - oldMoneyReturnModel.getProjectAmount());
							selfProjectAuthorizationEditor.getModelCopy().setActualTotalReturn(selfProjectAuthorization.getActualTotalReturn() + moneyReturnModel.getProjectAmount());
							oldSelfProjectAuthorizationEditor.save();
						}
						 selfProjectAuthorizationEditor.save();
					
						 MoneyPayback moneyPayback = null;
							if(oldMoneyReturnModel.getLocalFriendId() != null){
								moneyPayback = new Select().from(MoneyPayback.class).where("moneyReturnId=? AND ownerFriendId=?", oldMoneyReturnModel.getId(), oldMoneyReturnModel.getLocalFriendId()).executeSingle();
							}
							if (moneyPayback == null){
								moneyPayback = new MoneyPayback();
							}
							
							// 更新对方（借出人）的实际借出
							if (moneyReturnModel.getLocalFriendId() != null) {
								ProjectShareAuthorization paybackProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", moneyReturnModel.getLocalFriendId(), moneyReturnModel.getProjectId()).executeSingle();
								HyjModelEditor<ProjectShareAuthorization> paybackProjectAuthorizationEditor = paybackProjectAuthorization.newModelEditor();

								if (oldMoneyReturnModel.get_mId() == null 
										|| oldMoneyReturnModel.getLocalFriendId() == null) {
									paybackProjectAuthorizationEditor.getModelCopy()
									.setActualTotalPayback(
											paybackProjectAuthorization
													.getActualTotalPayback()
													+ moneyReturnModel
															.getProjectAmount());
									
								} else if(oldMoneyReturnModel.getProjectId().equals(
												moneyReturnModel.getProjectId()) && oldMoneyReturnModel.getLocalFriendId().equals(
														moneyReturnModel.getLocalFriendId())) {
									// 新旧ProjectShareAuthorization是一样的
									paybackProjectAuthorizationEditor.getModelCopy()
									.setActualTotalPayback(
											paybackProjectAuthorization
													.getActualTotalPayback()
													- oldMoneyReturnModel
															.getProjectAmount()
													+ moneyReturnModel
															.getProjectAmount());
									
								} else {
									// 更新旧的ProjectShareAuthorization
									ProjectShareAuthorization oldSelfProjectAuthorization = null;
									oldSelfProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", oldMoneyReturnModel.getLocalFriendId(), oldMoneyReturnModel.getProjectId()).executeSingle();
									
									HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
									oldSelfProjectAuthorizationEditor.getModelCopy()
											.setActualTotalReturn(
													oldSelfProjectAuthorization
															.getActualTotalReturn()
															- oldMoneyReturnModel
																	.getProjectAmount());
									paybackProjectAuthorizationEditor.getModelCopy()
											.setActualTotalPayback(
													paybackProjectAuthorization
															.getActualTotalPayback()
															+ moneyReturnModel
																	.getProjectAmount());
									oldSelfProjectAuthorizationEditor.save();
								}

								moneyPayback.setMoneyReturnId(moneyReturnModel.getId());
								moneyPayback.setAmount(moneyReturnModel.getAmount0());
								moneyPayback.setDate(moneyReturnModel.getDate());
								moneyPayback.setRemark(moneyReturnModel.getRemark());
								moneyPayback.setProject(moneyReturnModel.getProject());
								moneyPayback.setFriendAccountId(moneyReturnModel.getFriendAccountId());
								moneyPayback.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
								moneyPayback.setLocalFriendId(null);
								moneyPayback.setExchangeRate(moneyReturnModel.getExchangeRate());
								moneyPayback.setMoneyAccountId(null, moneyReturnModel.getCurrencyId1());
								moneyPayback.setLocation(moneyReturnModel.getLocation());
								moneyPayback.setGeoLat(moneyReturnModel.getGeoLat());
								moneyPayback.setGeoLon(moneyReturnModel.getGeoLon());
								moneyPayback.setAddress(moneyReturnModel.getAddress());
								moneyPayback.setPictureId(moneyReturnModel.getPictureId());
								moneyPayback.setOwnerUserId("");
								moneyPayback.setOwnerFriendId(moneyReturnModel.getLocalFriendId());
								moneyPayback.save();
								paybackProjectAuthorizationEditor.save();
							} else if(oldMoneyReturnModel.getLocalFriendId() != null){
								moneyPayback.delete();
									
								// 更新旧的ProjectShareAuthorization
								ProjectShareAuthorization oldSelfProjectAuthorization = null;
								oldSelfProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", oldMoneyReturnModel.getLocalFriendId(), oldMoneyReturnModel.getProjectId()).executeSingle();
								
								HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
								oldSelfProjectAuthorizationEditor.getModelCopy()
										.setActualTotalReturn(
												oldSelfProjectAuthorization
														.getActualTotalReturn()
														- oldMoneyReturnModel
																.getProjectAmount());
								oldSelfProjectAuthorizationEditor.save();
							}
					
				mMoneyReturnEditor.save();
				ActiveAndroid.setTransactionSuccessful();
				if(getActivity().getCallingActivity() != null){
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
         switch(requestCode){
             case GET_MONEYACCOUNT_ID:
	        	 if(resultCode == Activity.RESULT_OK){
	         		long _id = data.getLongExtra("MODEL_ID", -1);
	         		MoneyAccount moneyAccount = MoneyAccount.load(MoneyAccount.class, _id);
	         		mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
	         		mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
	         		setExchangeRate(false);
	        	 }
	        	 break;
             case GET_PROJECT_ID:
	        	 if(resultCode == Activity.RESULT_OK){
	         		long _id = data.getLongExtra("MODEL_ID", -1);
	         		Project project = Project.load(Project.class, _id);
	         		
	         		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", project.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
					
					if(mMoneyReturnEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
						HyjUtil.displayToast(R.string.app_permission_no_addnew);
						return;
					}else if(mMoneyReturnEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
						HyjUtil.displayToast(R.string.app_permission_no_edit);
						return;
					}

//					if(project.getFinancialOwnerUserId() != null){
//						mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
//						mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
//					} else {
//						mSelectorFieldFinancialOwner.setModelId(null);
//						mSelectorFieldFinancialOwner.setText(null);
//					}
//						
//					if(project.getFinancialOwnerUserId() != null){
//						mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
//						mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
//					} else {
//						mSelectorFieldFinancialOwner.setModelId(null);
//						mSelectorFieldFinancialOwner.setText(null);
//					}
						
	         		mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
	         		mSelectorFieldProject.setModelId(project.getId());
	         		setExchangeRate(false);
	         		
	         	// 看一下好友是不是新圈子的成员
	         		if(mSelectorFieldFriend.getModelId() != null) {
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
    					mNumericFieldAmount.setNumber(Double.parseDouble(calculatorAmount));
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
	   	       		
//	   	       		mSelectorFieldFinancialOwner.setText(psa.getFriendDisplayName());
//	   	       		mSelectorFieldFinancialOwner.setModelId(psa.getFriendUserId());
	       		}
   	       	 }
   	       	 break;
          }
    }
}
