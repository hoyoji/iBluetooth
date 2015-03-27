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
import android.widget.TextView;

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
import com.hoyoji.hoyoji.models.MoneyDepositExpenseContainer;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.event.EventListFragment;


public class MoneyDepositExpenseContainerFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_FRIEND_ID = 3;
	private static final int GET_REMARK = 4;
	protected static final int GET_FINANCIALOWNER_ID = 0;
	private int CREATE_EXCHANGE = 0;
	private final static int GET_AMOUNT = 8;
	private final static int GET_EVENT_ID = 9;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private HyjModelEditor<MoneyDepositExpenseContainer> mMoneyDepositExpenseContainerEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericAmount = null;
//	private HyjDateTimeField mDateTimeFieldPaybackDate = null;
//	private HyjNumericField mNumericFieldPaybackedAmount = null;
//	private View mSeparatorFieldPaybackedAmount = null;
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
	private HyjSelectorField mSelectorFieldFinancialOwner;
	private TextView mTextViewFinancialOwner;
	private ImageButton mButtonExpandMore;
	private LinearLayout mLinearLayoutExpandMore;
	
	private ImageButton calculatorTextView = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneydepositexpense;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		MoneyDepositExpenseContainer moneyLend;
		
		Intent intent = getActivity().getIntent();
	    final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			moneyLend =  new Select().from(MoneyDepositExpenseContainer.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyLend.hasEditPermission();
		} else {
			moneyLend = new MoneyDepositExpenseContainer();
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyAccountId != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, moneyAccountId);
				moneyLend.setMoneyAccountId(moneyAccountId, moneyAccount.getCurrencyId());
			}
			if(intent.getStringExtra("counterpartId") != null){
				moneyLend.setIsImported(true);
			}
		}
		mMoneyDepositExpenseContainerEditor = moneyLend.newModelEditor();
		
		setupDeleteButton(mMoneyDepositExpenseContainerEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_imageField_picture);		
		mImageFieldPicture.setImages(moneyLend.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_textField_date);		
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyLend.getDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
			}
		}
		
		mNumericAmount = (HyjNumericField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_textField_amount);		
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
			mNumericAmount.setNumber(moneyLend.getAmount());
		}
		
//		mDateTimeFieldPaybackDate = (HyjDateTimeField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_textField_paybackDate);
//		mDateTimeFieldPaybackDate.setText(moneyLend.getPaybackDate());
//	
//		mNumericFieldPaybackedAmount = (HyjNumericField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_textField_paybackedAmount);	
//		mNumericFieldPaybackedAmount.setNumber(moneyLend.getPaybackedAmount());
//		mNumericFieldPaybackedAmount.setEnabled(false);
//		mSeparatorFieldPaybackedAmount = (View) getView().findViewById(R.id.moneyDepositExpenseFormFragment_separatorField_paybackedAmount);
//		mNumericFieldPaybackedAmount.setVisibility(View.GONE);
//		mSeparatorFieldPaybackedAmount.setVisibility(View.GONE);
			
		MoneyAccount moneyAccount = moneyLend.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_selectorField_moneyAccount);

		if(moneyAccount != null){
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyDepositExpenseContainerFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_MONEYACCOUNT_ID);
			}
		});	
		
		Long dateImport = intent.getLongExtra("date", -1);
		if(dateImport != -1){
			Date date= new Date(dateImport);
			mDateTimeFieldDate.setDate(date);
			mDateTimeFieldDate.setTextColor(Color.RED);
		}
		
		Project project;
		String projectId = intent.getStringExtra("projectId");//从消息导入
		if(moneyLend.get_mId() == null && projectId != null){
			project = HyjModel.getModel(Project.class, projectId);
		}else{
			project = moneyLend.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyDepositExpenseContainerFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.projectListFragment_title_select_project, null, GET_PROJECT_ID);
			}
		});	

		mSelectorFieldEvent = (HyjSelectorField) getView().findViewById(R.id.moneyExpenseContainerFormFragment_selectorField_event);
		mViewSeparatorEvent = (View) getView().findViewById(R.id.field_separator_event);

		Event event = null;
		if (project != null) {
			List<Event> events = new Select().from(Event.class).where("projectId = ?", project.getId()).execute();
			if(events.size() > 0) {
				mSelectorFieldEvent.setVisibility(View.VISIBLE);
				mViewSeparatorEvent.setVisibility(View.VISIBLE);
			} else {
				mSelectorFieldEvent.setVisibility(View.GONE);
				mViewSeparatorEvent.setVisibility(View.GONE);
			}
			
			String eventId = intent.getStringExtra("eventId");//从消息导入
			if(moneyLend.get_mId() == null){
				if(eventId != null) {
					moneyLend.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyLend.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyLend.getEvent();
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

                    MoneyDepositExpenseContainerFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_textField_exchangeRate);		
		mNumericExchangeRate.setNumber(moneyLend.getExchangeRate());
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyDepositExpenseFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyDepositExpenseFormFragment_linearLayout_exchangeRate);
		
//		Friend friend;
//		if(moneyLend.get_mId() == null){
//			String friendUserId = intent.getStringExtra("friendUserId");//从消息导入
//			if(friendUserId != null){
//				friend = new Select().from(Friend.class).where("friendUserId=?",friendUserId).executeSingle();
//			} else {
//				String localFriendId = intent.getStringExtra("localFriendId");//从消息导入
//				if(localFriendId != null){
//					friend = HyjModel.getModel(Friend.class, localFriendId);
//				} else {
//					friend = moneyLend.getFriend();
//				}
//			}
//		} else {
//			friend = moneyLend.getFriend();
//		}
//		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_selectorField_friend);
//		
//		if(friend != null){
//			mSelectorFieldFriend.setModelId(friend.getId());
//			mSelectorFieldFriend.setText(friend.getDisplayName());
//		}
//		mSelectorFieldFriend.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				MoneyDepositExpenseFormFragment.this
//				.openActivityWithFragmentForResult(FriendListFragment.class, R.string.friendListFragment_title_select_friend_debtor, null, GET_FRIEND_ID);
//			}
//		}); 
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_selectorField_friend);
		String friendUserId = null;
		if(moneyLend.get_mId() == null){
			friendUserId = intent.getStringExtra("friendUserId");//从消息导入
		} else {
			friendUserId = moneyLend.getFriendUserId();
		}
		if(friendUserId != null){
			mSelectorFieldFriend.setModelId(friendUserId);
			mSelectorFieldFriend.setText(Friend.getFriendUserDisplayName(null, friendUserId, projectId));
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
				bundle.putBoolean("disableMultiChoiceMode", true);
				openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyDepositExpenseFormFragment_select_friend, bundle, GET_FRIEND_ID);
			}
		}); 
		
//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyDepositExpenseFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});

		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyDepositExpenseFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyLend.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyDepositExpenseContainerFormFragment.this
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
				bundle.putDouble("AMOUNT", mNumericAmount.getNumber()!=null?mNumericAmount.getNumber():0.00);
				MoneyDepositExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
								HyjCalculatorFormFragment.class,
								R.string.hyjCalculatorFormFragment_title,
								bundle, GET_AMOUNT);
			}
		});

		mSelectorFieldFinancialOwner = (HyjSelectorField) getView().findViewById(R.id.projectFormFragment_selectorField_financialOwner);
		mSelectorFieldFinancialOwner.setEnabled(hasEditPermission);
		if(modelId == -1){
			if (event != null) {
				if(event.getFinancialOwnerUserId() != null) {
					mSelectorFieldFinancialOwner.setModelId(event.getFinancialOwnerUserId());
					mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(event.getFinancialOwnerUserId()));
				}
			} else if(project != null && project.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
			}
		} else if(moneyLend.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(moneyLend.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyLend.getFinancialOwnerUserId()));
		}
		
		mSelectorFieldFinancialOwner.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectorFieldProject.getModelId() == null){
					HyjUtil.displayToast("请先选择一个圈子。");
				} else {
					Bundle bundle = new Bundle();
					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
					bundle.putLong("MODEL_ID", project.get_mId());
					bundle.putString("NULL_ITEM", (String)mSelectorFieldFinancialOwner.getHint());
					bundle.putBoolean("disableMultiChoiceMode", true);
					openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.explainFinancialOwnerFragment_title, bundle, GET_FINANCIALOWNER_ID);
				}
			}
		});
		
		mTextViewFinancialOwner = (TextView) getView().findViewById(R.id.projectFormFragment_textView_hint_financialOwner);
		mTextViewFinancialOwner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("FINANCIAL_TYPE", "MoneyDepositExpense");
				
				MoneyDepositExpenseContainerFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyDepositExpenseFormFragment_imageView_camera);	
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
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyDepositExpenseFormFragment_imageButton_refresh_exchangeRate);	
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
						HyjUtil.displayToast(R.string.moneyDepositExpenseFormFragment_toast_select_currency);
					}
				}else{
					HyjUtil.displayToast(R.string.moneyDepositExpenseFormFragment_toast_select_currency);
				}
			}
		});
		
	
		// 只在新增时才自动打开软键盘， 修改时不自动打开
		if (modelId == -1) {
			setExchangeRate(false);
			if(this.getUserVisibleHint()){
				this.mNumericAmount.showSoftKeyboard();
			}
//			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}else{
			setExchangeRate(true);
		}
		
		
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
		
		setPermission();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    if(mMoneyDepositExpenseContainerEditor!= null && mMoneyDepositExpenseContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
	
	private void setupDeleteButton(HyjModelEditor<MoneyDepositExpenseContainer> moneyDepositExpenseContainerEditor) {

		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
		
		final MoneyDepositExpenseContainer moneyDepositExpenseContainer = moneyDepositExpenseContainerEditor.getModelCopy();
		
		if (moneyDepositExpenseContainer.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(moneyDepositExpenseContainer.hasDeletePermission()){
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									try {
										ActiveAndroid.beginTransaction();

										MoneyAccount moneyAccount = moneyDepositExpenseContainer.getMoneyAccount();
										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() + moneyDepositExpenseContainer.getAmount());
										moneyAccountEditor.save();

										MoneyAccount debtAccount;
										if(moneyDepositExpenseContainer.getFinancialOwnerUserId() != null){
											if(moneyDepositExpenseContainer.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
												debtAccount = MoneyAccount.getDebtAccount(moneyDepositExpenseContainer.getProject().getCurrencyId(), moneyDepositExpenseContainer.getLocalFriendId(), moneyDepositExpenseContainer.getFriendUserId());
											} else {
												debtAccount = MoneyAccount.getDebtAccount(moneyDepositExpenseContainer.getProject().getCurrencyId(), null, moneyDepositExpenseContainer.getFinancialOwnerUserId());
											}
										} else {
											debtAccount = MoneyAccount.getDebtAccount(moneyDepositExpenseContainer.getProject().getCurrencyId(), moneyDepositExpenseContainer.getLocalFriendId(), moneyDepositExpenseContainer.getFriendUserId());
										}

										HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
										debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - moneyDepositExpenseContainer.getProjectAmount());
										debtAccountEditor.save();
//										ProjectShareAuthorization projectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(moneyLend.getProjectId());
//										HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = projectAuthorization.newModelEditor();
//									    selfProjectAuthorizationEditor.getModelCopy().setActualTotalLend(projectAuthorization.getActualTotalLend() - moneyLend.getProjectAmount());
//										
//									    selfProjectAuthorizationEditor.save();
										
										List<MoneyLend> financialOwnerMoneyLends = new Select().from(MoneyLend.class).where("moneyDepositExpenseContainerId = ?", moneyDepositExpenseContainer.getId()).execute();
										for(MoneyLend m : financialOwnerMoneyLends)	{
											m.delete();
										}
										
										moneyDepositExpenseContainer.delete();
										
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

		if(mMoneyDepositExpenseContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
			mNumericAmount.setNumber(mMoneyDepositExpenseContainerEditor.getModel().getProjectAmount());
			mNumericAmount.setEnabled(false);
			
//			mDateTimeFieldPaybackDate.setEnabled(false);
			
			mSelectorFieldFriend.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyDepositExpenseFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

			mSelectorFieldProject.setEnabled(false);
			mSelectorFieldEvent.setEnabled(false);
			mNumericExchangeRate.setEnabled(false);

//			mNumericFieldPaybackedAmount.setEnabled(false);
			
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
		MoneyDepositExpenseContainer modelCopy = (MoneyDepositExpenseContainer) mMoneyDepositExpenseContainerEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericAmount.getNumber());
//		modelCopy.setPaybackDate(mDateTimeFieldPaybackDate.getText());
		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
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
		
		modelCopy.setFriendUserId(mSelectorFieldFriend.getModelId());
			
		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyDepositExpenseContainerEditor.getValidationError("datetime"));
		mNumericAmount.setError(mMoneyDepositExpenseContainerEditor.getValidationError("amount"));
		if(mMoneyDepositExpenseContainerEditor.getValidationError("amount") != null){
			mNumericAmount.showSoftKeyboard();
		}
//		mDateTimeFieldPaybackDate.setError(mMoneyLendEditor.getValidationError("paybackDate"));
//		if(mMoneyLendEditor.getValidationError("paybackDate") != null){
//			HyjUtil.displayToast(mMoneyLendEditor.getValidationError("paybackDate"));
//		}
		mSelectorFieldMoneyAccount.setError(mMoneyDepositExpenseContainerEditor.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyDepositExpenseContainerEditor.getValidationError("project"));
		mSelectorFieldEvent.setError(mMoneyDepositExpenseContainerEditor.getValidationError("Event"));
		mNumericExchangeRate.setError(mMoneyDepositExpenseContainerEditor.getValidationError("exchangeRate"));
		mSelectorFieldFriend.setError(mMoneyDepositExpenseContainerEditor.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyDepositExpenseContainerEditor.getValidationError("remark"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		if(mMoneyDepositExpenseContainerEditor.getModelCopy().get_mId() == null && !mMoneyDepositExpenseContainerEditor.getModelCopy().hasAddNewPermission(mMoneyDepositExpenseContainerEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyDepositExpenseContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else{
		mMoneyDepositExpenseContainerEditor.validate();

		if(mMoneyDepositExpenseContainerEditor.getModelCopy().getFriendUserId() == null){
			mMoneyDepositExpenseContainerEditor.setValidationError("friend",R.string.moneyDepositExpenseFormFragment_editText_hint_friend);
		}else{
			mMoneyDepositExpenseContainerEditor.removeValidationError("friend");
		}
		
		if(mMoneyDepositExpenseContainerEditor.hasValidationErrors()){
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
						newPic.setRecordId(mMoneyDepositExpenseContainerEditor.getModel().getId());
						newPic.setRecordType("MoneyLend");
						newPic.setProjectId(mMoneyDepositExpenseContainerEditor.getModelCopy().getProjectId());
						newPic.setDisplayOrder(i);
						newPic.save();
					} else if(pi.getState() == PictureItem.DELETED){
						pi.getPicture().delete();
					} else if(pi.getState() == PictureItem.CHANGED){

					}
					if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
						mainPicSet = true;
						mMoneyDepositExpenseContainerEditor.getModelCopy().setPicture(pi.getPicture());
					}
				}
				
				MoneyDepositExpenseContainer oldMoneyDepositExpenseContainerModel = mMoneyDepositExpenseContainerEditor.getModel();
				MoneyDepositExpenseContainer newMoneyDepositExpenseContainerModel = mMoneyDepositExpenseContainerEditor.getModelCopy();
				
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				if(newMoneyDepositExpenseContainerModel.get_mId() == null && !userData.getActiveMoneyAccountId().equals(newMoneyDepositExpenseContainerModel.getMoneyAccountId())){
					HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
					userDataEditor.getModelCopy().setActiveMoneyAccountId(newMoneyDepositExpenseContainerModel.getMoneyAccountId());
					userDataEditor.getModelCopy().setActiveProjectId(newMoneyDepositExpenseContainerModel.getProjectId());
					userDataEditor.save();
				}
				
				//设置默认活动
				Project project = newMoneyDepositExpenseContainerModel.getProject();
				if(newMoneyDepositExpenseContainerModel.get_mId() == null){
					if((newMoneyDepositExpenseContainerModel.getEventId() != null && !newMoneyDepositExpenseContainerModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(newMoneyDepositExpenseContainerModel.getEventId()))){
						HyjModelEditor<Project> projectEditor = project.newModelEditor();
						projectEditor.getModelCopy().setActiveEventId(newMoneyDepositExpenseContainerModel.getEventId());
						projectEditor.save();
					}
				}
				
				String localCurrencyId = newMoneyDepositExpenseContainerModel.getMoneyAccount().getCurrencyId();
				String foreignCurrencyId = newMoneyDepositExpenseContainerModel.getProject().getCurrencyId();
				if(CREATE_EXCHANGE == 1){
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(newMoneyDepositExpenseContainerModel.getMoneyAccount().getCurrencyId());
					newExchange.setForeignCurrencyId(newMoneyDepositExpenseContainerModel.getProject().getCurrencyId());
					newExchange.setRate(newMoneyDepositExpenseContainerModel.getExchangeRate());
//					newExchange.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
					newExchange.save();
				}else {
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = null;
						Double exRate = null;
						Double rate = HyjUtil.toFixed2(newMoneyDepositExpenseContainerModel.getExchangeRate());
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
				    Double oldAmount = oldMoneyDepositExpenseContainerModel.getAmount0();
					MoneyAccount oldMoneyAccount = oldMoneyDepositExpenseContainerModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = newMoneyDepositExpenseContainerModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					if(newMoneyDepositExpenseContainerModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + oldAmount - newMoneyDepositExpenseContainerModel.getAmount0());
					}else{
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() + oldAmount);
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - newMoneyDepositExpenseContainerModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();
//				}	
					
					
					MoneyAccount newDebtAccount = null;
					if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() == null){
						newDebtAccount = MoneyAccount.getDebtAccount(newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getLocalFriendId(), newMoneyDepositExpenseContainerModel.getFriendUserId());
					} else {
						if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							newDebtAccount = MoneyAccount.getDebtAccount(newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getLocalFriendId(), newMoneyDepositExpenseContainerModel.getFriendUserId());
						} else {
							newDebtAccount = MoneyAccount.getDebtAccount(newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), null, newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId());
						}
					}
					if(newMoneyDepositExpenseContainerModel.get_mId() == null){
				    	if(newDebtAccount != null) {
				    		HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
				    		newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() + newMoneyDepositExpenseContainerModel.getProjectAmount());
				    		newDebtAccountEditor.save();
				    	} else {
				    		if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() == null){
								MoneyAccount.createDebtAccount(newMoneyDepositExpenseContainerModel.getFriendDisplayName(), newMoneyDepositExpenseContainerModel.getLocalFriendId(), newMoneyDepositExpenseContainerModel.getFriendUserId(), newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getProject().getOwnerUserId(), newMoneyDepositExpenseContainerModel.getProjectAmount());
				    		} else {
				    			if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
				    				MoneyAccount.createDebtAccount(newMoneyDepositExpenseContainerModel.getFriendDisplayName(), newMoneyDepositExpenseContainerModel.getLocalFriendId(), newMoneyDepositExpenseContainerModel.getFriendUserId(), newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getProject().getOwnerUserId(), newMoneyDepositExpenseContainerModel.getProjectAmount());
						    	} else {
									MoneyAccount.createDebtAccount(newMoneyDepositExpenseContainerModel.getFriendDisplayName(), null, newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId(), newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getProject().getOwnerUserId(), newMoneyDepositExpenseContainerModel.getProjectAmount());
					    		}
					    	}
				    	}
					} else {
						MoneyAccount oldDebtAccount = null;
						if(oldMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() == null){
							oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyAccount.getCurrencyId(), oldMoneyDepositExpenseContainerModel.getLocalFriendId(), oldMoneyDepositExpenseContainerModel.getFriendUserId());
						} else {
							if(oldMoneyDepositExpenseContainerModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
								oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyAccount.getCurrencyId(), oldMoneyDepositExpenseContainerModel.getLocalFriendId(), oldMoneyDepositExpenseContainerModel.getFriendUserId());
							} else {
								oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyAccount.getCurrencyId(), null, oldMoneyDepositExpenseContainerModel.getFinancialOwnerUserId());
							}
						}
						if(newDebtAccount != null){
							HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
							if(oldDebtAccount != null && oldDebtAccount.getId().equals(newDebtAccount.getId())){
								newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() - oldMoneyDepositExpenseContainerModel.getProjectAmount() + newMoneyDepositExpenseContainerModel.getProjectAmount());
							} else {
								newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() + newMoneyDepositExpenseContainerModel.getProjectAmount());
								if(oldDebtAccount != null){
									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldMoneyDepositExpenseContainerModel.getProjectAmount());
									oldDebtAccountEditor.save();
								}
							}
							newDebtAccountEditor.save();
						} else {
							if(oldDebtAccount != null) {
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() - oldMoneyDepositExpenseContainerModel.getProjectAmount());
								oldDebtAccountEditor.save();
							}
							if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() == null){
								MoneyAccount.createDebtAccount(newMoneyDepositExpenseContainerModel.getFriendDisplayName(), newMoneyDepositExpenseContainerModel.getLocalFriendId(), newMoneyDepositExpenseContainerModel.getFriendUserId(), newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getProject().getOwnerUserId(), newMoneyDepositExpenseContainerModel.getProjectAmount());
							} else {
								if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
									MoneyAccount.createDebtAccount(newMoneyDepositExpenseContainerModel.getFriendDisplayName(), newMoneyDepositExpenseContainerModel.getLocalFriendId(), newMoneyDepositExpenseContainerModel.getFriendUserId(), newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getProject().getOwnerUserId(), newMoneyDepositExpenseContainerModel.getProjectAmount());
								} else {
						    		MoneyAccount.createDebtAccount(newMoneyDepositExpenseContainerModel.getFriendDisplayName(), null, newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId(), newMoneyDepositExpenseContainerModel.getProject().getCurrencyId(), newMoneyDepositExpenseContainerModel.getProject().getOwnerUserId(), newMoneyDepositExpenseContainerModel.getProjectAmount());
								}
							}
						}
					}
					
//					//更新支出所有者的实际借出
//						ProjectShareAuthorization selfProjectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(moneyLendModel.getProjectId());
//						HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
//					    if(moneyLendModel.get_mId() == null || oldMoneyLendModel.getProjectId().equals(moneyLendModel.getProjectId())){
//					    	selfProjectAuthorizationEditor.getModelCopy().setActualTotalLend(selfProjectAuthorization.getActualTotalLend() - oldMoneyLendModel.getProjectAmount() + moneyLendModel.getProjectAmount());
//						}else{
//							ProjectShareAuthorization oldSelfProjectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(oldMoneyLendModel.getProjectId());
//							HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
//							oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalLend(oldSelfProjectAuthorization.getActualTotalLend() - oldMoneyLendModel.getProjectAmount());
//							selfProjectAuthorizationEditor.getModelCopy().setActualTotalLend(selfProjectAuthorization.getActualTotalLend() + moneyLendModel.getProjectAmount());
//							oldSelfProjectAuthorizationEditor.save();
//						}
//						 selfProjectAuthorizationEditor.save();
					
				// 如果有财务负责人，生成财务负责人到收款人的借出
//				MoneyLend moneyLendOfFinancialOwner = null;
				MoneyLend moneyLendToFinancialOwner = null;
				if(newMoneyDepositExpenseContainerModel.get_mId() == null){
//					moneyLendOfFinancialOwner = new MoneyLend();
					moneyLendToFinancialOwner = new MoneyLend();
				} else {
					String previousFinancialOwnerUserId = HyjUtil.ifNull(oldMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() , "");
					String currentFinancialOwnerUserId = HyjUtil.ifNull(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() , "");
//					moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("depositExpenseId = ? AND ownerUserId = ?", oldMoneyLendModel.getId(), previousFinancialOwnerUserId).executeSingle();
//					if(moneyLendOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//						moneyLendOfFinancialOwner.delete();
//						moneyLendOfFinancialOwner = new MoneyLend();
//					}

					if(oldMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() != null
							&& !oldMoneyDepositExpenseContainerModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						moneyLendToFinancialOwner = new Select().from(MoneyLend.class).where("moneyDepositExpenseContainerId = ? AND friendUserId = ?", oldMoneyDepositExpenseContainerModel.getId(), previousFinancialOwnerUserId).executeSingle();
					} else {
						moneyLendToFinancialOwner = new Select().from(MoneyLend.class).where("moneyDepositExpenseContainerId = ? AND friendUserId = ?", oldMoneyDepositExpenseContainerModel.getId(), oldMoneyDepositExpenseContainerModel.getFriendUserId()).executeSingle();
					}
					if(moneyLendToFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
						moneyLendToFinancialOwner.delete();
						moneyLendToFinancialOwner = new MoneyLend();
					}
				}
				if(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId() != null
						&& !newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
					moneyLendToFinancialOwner.setFriendUserId(newMoneyDepositExpenseContainerModel.getFinancialOwnerUserId());
				} else {
					moneyLendToFinancialOwner.setFriendUserId(newMoneyDepositExpenseContainerModel.getFriendUserId());
				}
//					moneyLendOfFinancialOwner.setMoneyDepositExpenseContainerId(newMoneyLendModel.getId());
//					moneyLendOfFinancialOwner.setDate(newMoneyLendModel.getDate());
//					moneyLendOfFinancialOwner.setAmount(newMoneyLendModel.getAmount());
//					moneyLendOfFinancialOwner.setAddress(newMoneyLendModel.getAddress());
//					moneyLendOfFinancialOwner.setCurrencyId1(newMoneyLendModel.getCurrencyId());
//					moneyLendOfFinancialOwner.setExchangeRate(newMoneyLendModel.getExchangeRate());
//					moneyLendOfFinancialOwner.setFinancialOwnerUserId(null);
//					moneyLendOfFinancialOwner.setFriendAccountId(newMoneyLendModel.getFriendUserId());
//					moneyLendOfFinancialOwner.setFriendUserId(newMoneyLendModel.getFriendUserId());
//					moneyLendOfFinancialOwner.setGeoLat(newMoneyLendModel.getGeoLat());
//					moneyLendOfFinancialOwner.setGeoLon(newMoneyLendModel.getGeoLon());
//					moneyLendOfFinancialOwner.setLocalFriendId(newMoneyLendModel.getLocalFriendId());
//					moneyLendOfFinancialOwner.setLocation(newMoneyLendModel.getLocation());
//					moneyLendOfFinancialOwner.setMoneyAccountId(newMoneyLendModel.getMoneyAccountId(), newMoneyLendModel.getMoneyAccount().getCurrencyId());
//					moneyLendOfFinancialOwner.setOwnerUserId(newMoneyLendModel.getFinancialOwnerUserId());
//					moneyLendOfFinancialOwner.setOwnerFriendId(null);
//					moneyLendOfFinancialOwner.setPaybackDate(newMoneyLendModel.getPaybackDate());
//					moneyLendOfFinancialOwner.setPaybackedAmount(newMoneyLendModel.getPaybackedAmount());
//					moneyLendOfFinancialOwner.setProjectId(newMoneyLendModel.getProjectId(), newMoneyLendModel.getProjectCurrencyId());
//					moneyLendOfFinancialOwner.setPictureId(newMoneyLendModel.getPictureId());
//					moneyLendOfFinancialOwner.setRemark(newMoneyLendModel.getRemark());
//					
//					moneyLendOfFinancialOwner.save();
					
					moneyLendToFinancialOwner.setMoneyDepositExpenseContainerId(newMoneyDepositExpenseContainerModel.getId());
//					moneyLendToFinancialOwner.setLendType("Deposit");
					moneyLendToFinancialOwner.setDate(newMoneyDepositExpenseContainerModel.getDate());
					moneyLendToFinancialOwner.setAmount(newMoneyDepositExpenseContainerModel.getAmount());
					moneyLendToFinancialOwner.setAddress(newMoneyDepositExpenseContainerModel.getAddress());
					moneyLendToFinancialOwner.setCurrencyId1(newMoneyDepositExpenseContainerModel.getCurrencyId());
					moneyLendToFinancialOwner.setExchangeRate(newMoneyDepositExpenseContainerModel.getExchangeRate());
//					moneyLendToFinancialOwner.setFinancialOwnerUserId(null);
					moneyLendToFinancialOwner.setFriendAccountId(newMoneyDepositExpenseContainerModel.getFriendUserId());
					moneyLendToFinancialOwner.setLocalFriendId(null);
					moneyLendToFinancialOwner.setGeoLat(newMoneyDepositExpenseContainerModel.getGeoLat());
					moneyLendToFinancialOwner.setGeoLon(newMoneyDepositExpenseContainerModel.getGeoLon());
					moneyLendToFinancialOwner.setLocation(newMoneyDepositExpenseContainerModel.getLocation());
					moneyLendToFinancialOwner.setMoneyAccountId(newMoneyDepositExpenseContainerModel.getMoneyAccountId(), newMoneyDepositExpenseContainerModel.getMoneyAccount().getCurrencyId());
//					moneyLendToFinancialOwner.setOwnerUserId(newMoneyLendModel.getFinancialOwnerUserId());
//					moneyLendToFinancialOwner.setOwnerFriendId(null);
					moneyLendToFinancialOwner.setPaybackDate(newMoneyDepositExpenseContainerModel.getPaybackDate());
					moneyLendToFinancialOwner.setPaybackedAmount(newMoneyDepositExpenseContainerModel.getPaybackedAmount());
					moneyLendToFinancialOwner.setProjectId(newMoneyDepositExpenseContainerModel.getProjectId(), newMoneyDepositExpenseContainerModel.getProjectCurrencyId());
					moneyLendToFinancialOwner.setPictureId(newMoneyDepositExpenseContainerModel.getPictureId());
					moneyLendToFinancialOwner.setRemark(newMoneyDepositExpenseContainerModel.getRemark());
					
					moneyLendToFinancialOwner.save();

				mMoneyDepositExpenseContainerEditor.save();
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
					
					if(mMoneyDepositExpenseContainerEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
						HyjUtil.displayToast(R.string.app_permission_no_addnew);
						return;
					}else if(mMoneyDepositExpenseContainerEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
						HyjUtil.displayToast(R.string.app_permission_no_edit);
						return;
					}
					if(!psa.getState().equalsIgnoreCase("Accept")){
 						HyjUtil.displayToast(R.string.moneyDepositExpenseFormFragment_editText_error_not_member);
 						return;
 					} 

					if(project.getFinancialOwnerUserId() != null){
						mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
						mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
					} else {
						mSelectorFieldFinancialOwner.setModelId(null);
						mSelectorFieldFinancialOwner.setText(null);
					}
						
	         		mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
	         		mSelectorFieldProject.setModelId(project.getId());
	         		setExchangeRate(false);
	         		
	         		// 看一下好友是不是新圈子的成员
	         		if(mSelectorFieldFriend.getModelId() != null) {
	        			String friendUserId;
	        			friendUserId = mSelectorFieldFriend.getModelId();
	        			ProjectShareAuthorization psaMember = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=? AND state <> 'Delete'", project.getId(), friendUserId).executeSingle();
	    				if(psaMember != null){
//	    					Friend friend =  new Select().from(Friend.class).where("friendUserId=?", friendUserId).executeSingle();
//	    					if(friend != null) {
	    						mSelectorFieldFriend.setModelId(friendUserId);
//	    					}
	    				} else {
    						mSelectorFieldFriend.setText(null);
    						mSelectorFieldFriend.setModelId(null);
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
     					
     					if(event.getFinancialOwnerUserId() != null){
    						mSelectorFieldFinancialOwner.setModelId(event.getFinancialOwnerUserId());
    						mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(event.getFinancialOwnerUserId()));
    					} else if(event.getProject().getFinancialOwnerUserId() != null){
    						mSelectorFieldFinancialOwner.setModelId(event.getProject().getFinancialOwnerUserId());
    						mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(event.getProject().getFinancialOwnerUserId()));
    					} else {
    						mSelectorFieldFinancialOwner.setModelId(null);
    						mSelectorFieldFinancialOwner.setText(null);
    					}
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
            	 if(resultCode == Activity.RESULT_OK){
             		long _id = data.getLongExtra("MODEL_ID", -1);
             		ProjectShareAuthorization psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
 		       		if(!psa.getState().equalsIgnoreCase("Accept")){
 						HyjUtil.displayToast(R.string.moneyDepositExpenseFormFragment_editText_error_not_member);
 						return;
 					} else {
 			       		mSelectorFieldFriend.setText(psa.getFriendDisplayName());
 			       		mSelectorFieldFriend.setModelId(psa.getFriendUserId());
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

          }
    }
}
