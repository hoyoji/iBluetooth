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
import com.hoyoji.hoyoji.models.MoneyDepositPaybackContainer;
import com.hoyoji.hoyoji.models.MoneyPayback;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.event.EventListFragment;


public class MoneyDepositPaybackContainerFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_FRIEND_ID = 3;
	protected static final int GET_REMARK = 4;
	private final static int GET_AMOUNT = 8;
	private final static int GET_EVENT_ID = 9;
	protected static final int GET_FINANCIALOWNER_ID = 0;
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private HyjModelEditor<MoneyDepositPaybackContainer> mMoneyDepositPaybackContainerEditor = null;
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
		return R.layout.money_formfragment_moneydepositpayback;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		MoneyDepositPaybackContainer moneyDepositPaybackContainer;
		
		Intent intent = getActivity().getIntent();
	    final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			moneyDepositPaybackContainer =  new Select().from(MoneyDepositPaybackContainer.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyDepositPaybackContainer.hasEditPermission();
		} else {
			moneyDepositPaybackContainer = new MoneyDepositPaybackContainer();
//			moneyPayback.setPaybackType("Deposit"); 
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyAccountId != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, moneyAccountId);
				moneyDepositPaybackContainer.setMoneyAccountId(moneyAccountId, moneyAccount.getCurrencyId());
			}
			if(intent.getStringExtra("counterpartId") != null){
				moneyDepositPaybackContainer.setIsImported(true);
			}
		}
		mMoneyDepositPaybackContainerEditor = moneyDepositPaybackContainer.newModelEditor();
		
		setupDeleteButton(mMoneyDepositPaybackContainerEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_imageField_picture);		
		mImageFieldPicture.setImages(moneyDepositPaybackContainer.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textField_date);		
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyDepositPaybackContainer.getDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
			}
		}
		
		mNumericAmount = (HyjNumericField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textField_amount);		
		double amount = intent.getDoubleExtra("amount", -1.0);//从分享消息导入的金额
		if(amount >= 0.0){
			double exchangeRate = intent.getDoubleExtra("exchangeRate", 1.0);
			mNumericAmount.setNumber(amount*exchangeRate);
		}else{
			mNumericAmount.setNumber(moneyDepositPaybackContainer.getAmount());
		}
		
//		mDateTimeFieldPaybackDate = (HyjDateTimeField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textField_paybackDate);
//		mDateTimeFieldPaybackDate.setText(moneyPayback.getPaybackDate());
//	
//		mNumericFieldPaybackedAmount = (HyjNumericField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textField_paybackedAmount);	
//		mNumericFieldPaybackedAmount.setNumber(moneyPayback.getPaybackedAmount());
//		mNumericFieldPaybackedAmount.setEnabled(false);
//		mSeparatorFieldPaybackedAmount = (View) getView().findViewById(R.id.moneyDepositPaybackFormFragment_separatorField_paybackedAmount);
//		mNumericFieldPaybackedAmount.setVisibility(View.GONE);
//		mSeparatorFieldPaybackedAmount.setVisibility(View.GONE);
			
		MoneyAccount moneyAccount = moneyDepositPaybackContainer.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_selectorField_moneyAccount);

		if(moneyAccount != null){
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyDepositPaybackContainerFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_MONEYACCOUNT_ID);
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
		if(moneyDepositPaybackContainer.get_mId() == null && projectId != null){
			project = HyjModel.getModel(Project.class, projectId);
		}else{
			project = moneyDepositPaybackContainer.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyDepositPaybackContainerFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.projectListFragment_title_select_project, null, GET_PROJECT_ID);
			}
		});	
		
		mSelectorFieldEvent = (HyjSelectorField) getView().findViewById(R.id.moneyExpenseContainerFormFragment_selectorField_event);
		mViewSeparatorEvent = (View) getView().findViewById(R.id.field_separator_event);

		Event event = null;
		if(project != null){
			List<Event> events = new Select().from(Event.class).where("projectId = ?", project.getId()).execute();
			if(events.size() > 0) {
				mSelectorFieldEvent.setVisibility(View.VISIBLE);
				mViewSeparatorEvent.setVisibility(View.VISIBLE);
			} else {
				mSelectorFieldEvent.setVisibility(View.GONE);
				mViewSeparatorEvent.setVisibility(View.GONE);
			}
			String eventId = intent.getStringExtra("eventId");//从消息导入
			if(moneyDepositPaybackContainer.get_mId() == null){
				if(eventId != null) {
					moneyDepositPaybackContainer.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyDepositPaybackContainer.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyDepositPaybackContainer.getEvent();
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

                    MoneyDepositPaybackContainerFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textField_exchangeRate);		
		mNumericExchangeRate.setNumber(moneyDepositPaybackContainer.getExchangeRate());
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyDepositPaybackFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyDepositPaybackFormFragment_linearLayout_exchangeRate);
		
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_selectorField_friend);
		String friendUserId = null;
		if(moneyDepositPaybackContainer.get_mId() == null){
			friendUserId = intent.getStringExtra("friendUserId");//从消息导入
		} else {
			friendUserId = moneyDepositPaybackContainer.getFriendUserId();
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
				MoneyDepositPaybackContainerFormFragment.this
				.openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyDepositPaybackFormFragment_textView_friend, bundle, GET_FRIEND_ID);
			}
		}); 
		
//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyDepositPaybackFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});

		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyDepositPaybackContainer.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyDepositPaybackContainerFormFragment.this
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
				MoneyDepositPaybackContainerFormFragment.this.openActivityWithFragmentForResult(
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
		} else if(moneyDepositPaybackContainer.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(moneyDepositPaybackContainer.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyDepositPaybackContainer.getFinancialOwnerUserId()));
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
		
		mTextViewFinancialOwner = (TextView) getView().findViewById(R.id.moneyDepositPaybackFormFragment_textView_hint_financialOwner);
		mTextViewFinancialOwner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("FINANCIAL_TYPE", "MoneyDepositPayback");
				
				MoneyDepositPaybackContainerFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyDepositPaybackFormFragment_imageView_camera);	
		takePictureButton.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
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
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyDepositPaybackFormFragment_imageButton_refresh_exchangeRate);	
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
						HyjUtil.displayToast(R.string.moneyDepositPaybackFormFragment_toast_select_currency);
					}
				}else{
					HyjUtil.displayToast(R.string.moneyDepositPaybackFormFragment_toast_select_currency);
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
	    if(mMoneyDepositPaybackContainerEditor!= null && mMoneyDepositPaybackContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
	
	private void setupDeleteButton(HyjModelEditor<MoneyDepositPaybackContainer> moneyPaybackEditor) {

		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
		
		final MoneyDepositPaybackContainer moneyDepositPaybackContainer = moneyPaybackEditor.getModelCopy();
		
		if (moneyDepositPaybackContainer.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(moneyDepositPaybackContainer.hasDeletePermission()){
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									try {
										ActiveAndroid.beginTransaction();

										MoneyAccount moneyAccount = moneyDepositPaybackContainer.getMoneyAccount();
										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() - moneyDepositPaybackContainer.getAmount());
										moneyAccountEditor.save();

										MoneyAccount debtAccount;
										if(moneyDepositPaybackContainer.getFinancialOwnerUserId() != null){
											if(moneyDepositPaybackContainer.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
												debtAccount = MoneyAccount.getDebtAccount(moneyDepositPaybackContainer.getProject().getCurrencyId(), moneyDepositPaybackContainer.getLocalFriendId(), moneyDepositPaybackContainer.getFriendUserId());
											} else {
												debtAccount = MoneyAccount.getDebtAccount(moneyDepositPaybackContainer.getProject().getCurrencyId(), null, moneyDepositPaybackContainer.getFinancialOwnerUserId());
											}
										} else {
											debtAccount = MoneyAccount.getDebtAccount(moneyDepositPaybackContainer.getProject().getCurrencyId(), moneyDepositPaybackContainer.getLocalFriendId(), moneyDepositPaybackContainer.getFriendUserId());
										}
										HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
										debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + moneyDepositPaybackContainer.getProjectAmount());
										debtAccountEditor.save();
										
//										ProjectShareAuthorization projectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(moneyPayback.getProjectId());
//										HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = projectAuthorization.newModelEditor();
//									    selfProjectAuthorizationEditor.getModelCopy().setActualTotalPayback(projectAuthorization.getActualTotalPayback() - moneyPayback.getProjectAmount());
//										selfProjectAuthorizationEditor.save();
										List<MoneyPayback> financialOwnerMoneyPaybacks = new Select().from(MoneyPayback.class).where("moneyDepositPaybackContainerId = ?", moneyDepositPaybackContainer.getId()).execute();
										for(MoneyPayback m : financialOwnerMoneyPaybacks)	{
											m.delete();
										}
										moneyDepositPaybackContainer.delete();
										
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

		if(mMoneyDepositPaybackContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
//			mNumericAmount.setNumber(mMoneyDepositPaybackContainerEditor.getModel().getProjectAmount());
			mNumericAmount.setEnabled(false);
			
//			mDateTimeFieldPaybackDate.setEnabled(false);
			
			mSelectorFieldFriend.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyDepositPaybackFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

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
		MoneyDepositPaybackContainer modelCopy = (MoneyDepositPaybackContainer) mMoneyDepositPaybackContainerEditor.getModelCopy();
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
		mDateTimeFieldDate.setError(mMoneyDepositPaybackContainerEditor.getValidationError("datetime"));
		mNumericAmount.setError(mMoneyDepositPaybackContainerEditor.getValidationError("amount"));
		if(mMoneyDepositPaybackContainerEditor.getValidationError("amount") != null){
			mNumericAmount.showSoftKeyboard();
		}
//		mDateTimeFieldPaybackDate.setError(mMoneyPaybackEditor.getValidationError("paybackDate"));
//		if(mMoneyPaybackEditor.getValidationError("paybackDate") != null){
//			HyjUtil.displayToast(mMoneyPaybackEditor.getValidationError("paybackDate"));
//		}
		mSelectorFieldMoneyAccount.setError(mMoneyDepositPaybackContainerEditor.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyDepositPaybackContainerEditor.getValidationError("project"));
		mSelectorFieldEvent.setError(mMoneyDepositPaybackContainerEditor.getValidationError("Event"));
		mNumericExchangeRate.setError(mMoneyDepositPaybackContainerEditor.getValidationError("exchangeRate"));
		mSelectorFieldFriend.setError(mMoneyDepositPaybackContainerEditor.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyDepositPaybackContainerEditor.getValidationError("remark"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		if(mMoneyDepositPaybackContainerEditor.getModelCopy().get_mId() == null && !mMoneyDepositPaybackContainerEditor.getModelCopy().hasAddNewPermission(mMoneyDepositPaybackContainerEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyDepositPaybackContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else{
			mMoneyDepositPaybackContainerEditor.validate();
		
			if(mMoneyDepositPaybackContainerEditor.getModelCopy().getFriendUserId() == null){
				mMoneyDepositPaybackContainerEditor.setValidationError("friend",R.string.moneyDepositPaybackFormFragment_editText_hint_friend);
			}else{
				mMoneyDepositPaybackContainerEditor.removeValidationError("friend");
			}
			
		if(mMoneyDepositPaybackContainerEditor.hasValidationErrors()){
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
						newPic.setRecordId(mMoneyDepositPaybackContainerEditor.getModel().getId());
						newPic.setRecordType("MoneyPayback");
						newPic.setProjectId(mMoneyDepositPaybackContainerEditor.getModelCopy().getProjectId());
						newPic.setDisplayOrder(i);
						newPic.save();
					} else if(pi.getState() == PictureItem.DELETED){
						pi.getPicture().delete();
					} else if(pi.getState() == PictureItem.CHANGED){

					}
					if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
						mainPicSet = true;
						mMoneyDepositPaybackContainerEditor.getModelCopy().setPicture(pi.getPicture());
					}
				}
				
				MoneyDepositPaybackContainer oldMoneyPaybackModel = mMoneyDepositPaybackContainerEditor.getModel();
				MoneyDepositPaybackContainer newMoneyPaybackModel = mMoneyDepositPaybackContainerEditor.getModelCopy();
				
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				if(newMoneyPaybackModel.get_mId() == null && !userData.getActiveMoneyAccountId().equals(newMoneyPaybackModel.getMoneyAccountId())){
					HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
					userDataEditor.getModelCopy().setActiveMoneyAccountId(newMoneyPaybackModel.getMoneyAccountId());
					userDataEditor.getModelCopy().setActiveProjectId(newMoneyPaybackModel.getProjectId());
					userDataEditor.save();
				}
				
				//设置默认活动
				Project project = newMoneyPaybackModel.getProject();
				if(newMoneyPaybackModel.get_mId() == null){
					if((newMoneyPaybackModel.getEventId() != null && !newMoneyPaybackModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(newMoneyPaybackModel.getEventId()))){
						HyjModelEditor<Project> projectEditor = project.newModelEditor();
						projectEditor.getModelCopy().setActiveEventId(newMoneyPaybackModel.getEventId());
						projectEditor.save();
					}
				}
				
				String localCurrencyId = newMoneyPaybackModel.getMoneyAccount().getCurrencyId();
				String foreignCurrencyId = newMoneyPaybackModel.getProject().getCurrencyId();
				if(CREATE_EXCHANGE == 1){
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(newMoneyPaybackModel.getMoneyAccount().getCurrencyId());
					newExchange.setForeignCurrencyId(newMoneyPaybackModel.getProject().getCurrencyId());
					newExchange.setRate(newMoneyPaybackModel.getExchangeRate());
//					newExchange.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
					newExchange.save();
				}else {
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = null;
						Double exRate = null;
						Double rate = HyjUtil.toFixed2(newMoneyPaybackModel.getExchangeRate());
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
				    Double oldAmount = oldMoneyPaybackModel.getAmount0();
					MoneyAccount oldMoneyAccount = oldMoneyPaybackModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = newMoneyPaybackModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					if(newMoneyPaybackModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - oldAmount + newMoneyPaybackModel.getAmount0());
					}else{
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() - oldAmount);
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + newMoneyPaybackModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();
//				}	
					MoneyAccount newDebtAccount = null;
					if(newMoneyPaybackModel.getFinancialOwnerUserId() == null){
						newDebtAccount = MoneyAccount.getDebtAccount(newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getLocalFriendId(), newMoneyPaybackModel.getFriendUserId());
					} else {
						if(newMoneyPaybackModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							newDebtAccount = MoneyAccount.getDebtAccount(newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getLocalFriendId(), newMoneyPaybackModel.getFriendUserId());
						} else {
							newDebtAccount = MoneyAccount.getDebtAccount(newMoneyPaybackModel.getProject().getCurrencyId(), null, newMoneyPaybackModel.getFinancialOwnerUserId());
						}
					}
					if(newMoneyPaybackModel.get_mId() == null){
				    	if(newDebtAccount != null) {
				    		HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
				    		newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() - newMoneyPaybackModel.getProjectAmount());
				    		newDebtAccountEditor.save();
				    	}else{
				    		if(newMoneyPaybackModel.getFinancialOwnerUserId() == null){
				    			MoneyAccount.createDebtAccount(newMoneyPaybackModel.getFriendDisplayName(), newMoneyPaybackModel.getLocalFriendId(), newMoneyPaybackModel.getFriendUserId(), newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getProject().getOwnerUserId(), newMoneyPaybackModel.getProjectAmount());
						   	} else {
						   		if(newMoneyPaybackModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						   			MoneyAccount.createDebtAccount(newMoneyPaybackModel.getFriendDisplayName(), newMoneyPaybackModel.getLocalFriendId(), newMoneyPaybackModel.getFriendUserId(), newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getProject().getOwnerUserId(), newMoneyPaybackModel.getProjectAmount());
								} else {	
						   			MoneyAccount.createDebtAccount(newMoneyPaybackModel.getFriendDisplayName(), null, newMoneyPaybackModel.getFinancialOwnerUserId(), newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getProject().getOwnerUserId(), newMoneyPaybackModel.getProjectAmount());
						   		}
					    	}
				    	}
					}else{
						MoneyAccount oldDebtAccount = null;
						if(oldMoneyPaybackModel.getFinancialOwnerUserId() == null){
							oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyAccount.getCurrencyId(), oldMoneyPaybackModel.getLocalFriendId(), oldMoneyPaybackModel.getFriendUserId());
						} else {
							if(oldMoneyPaybackModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
								oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyAccount.getCurrencyId(), oldMoneyPaybackModel.getLocalFriendId(), oldMoneyPaybackModel.getFriendUserId());
							} else {
								oldDebtAccount = MoneyAccount.getDebtAccount(oldMoneyAccount.getCurrencyId(), null, oldMoneyPaybackModel.getFinancialOwnerUserId());
							}
						}
						if(newDebtAccount != null){
							HyjModelEditor<MoneyAccount> newDebtAccountEditor = newDebtAccount.newModelEditor();
							if(oldDebtAccount != null && oldDebtAccount.getId().equals(newDebtAccount.getId())){
								newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() +  oldMoneyPaybackModel.getProjectAmount() - newMoneyPaybackModel.getProjectAmount());
							}else{
								newDebtAccountEditor.getModelCopy().setCurrentBalance(newDebtAccount.getCurrentBalance() - newMoneyPaybackModel.getProjectAmount());
								if(oldDebtAccount != null){
									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldMoneyPaybackModel.getProjectAmount());
									oldDebtAccountEditor.save();
								}
							}
							newDebtAccountEditor.save();
						}else{
							if(oldDebtAccount != null){
								HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
								oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + oldMoneyPaybackModel.getProjectAmount());
								oldDebtAccountEditor.save();
							}
							if(newMoneyPaybackModel.getFinancialOwnerUserId() == null){
				    			MoneyAccount.createDebtAccount(newMoneyPaybackModel.getFriendDisplayName(), newMoneyPaybackModel.getLocalFriendId(), newMoneyPaybackModel.getFriendUserId(), newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getProject().getOwnerUserId(), newMoneyPaybackModel.getProjectAmount());
						   	} else {
						   		if(newMoneyPaybackModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						   			MoneyAccount.createDebtAccount(newMoneyPaybackModel.getFriendDisplayName(), newMoneyPaybackModel.getLocalFriendId(), newMoneyPaybackModel.getFriendUserId(), newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getProject().getOwnerUserId(), newMoneyPaybackModel.getProjectAmount());
								} else {	
						   			MoneyAccount.createDebtAccount(newMoneyPaybackModel.getFriendDisplayName(), null, newMoneyPaybackModel.getFinancialOwnerUserId(), newMoneyPaybackModel.getProject().getCurrencyId(), newMoneyPaybackModel.getProject().getOwnerUserId(), newMoneyPaybackModel.getProjectAmount());
						   		}
					    	}
						}
					}
					
//					//更新支出所有者的实际借出
//					ProjectShareAuthorization selfProjectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(moneyPaybackModel.getProjectId());
//					HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
//				    if(moneyPaybackModel.get_mId() == null || oldMoneyPaybackModel.getProjectId().equals(moneyPaybackModel.getProjectId())){
//				    	selfProjectAuthorizationEditor.getModelCopy().setActualTotalPayback(selfProjectAuthorization.getActualTotalPayback() - oldMoneyPaybackModel.getAmount0()*oldMoneyPaybackModel.getExchangeRate() + moneyPaybackModel.getAmount0()*moneyPaybackModel.getExchangeRate());
//					}else{
//						ProjectShareAuthorization oldSelfProjectAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(oldMoneyPaybackModel.getProjectId());
//						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
//						oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalPayback(oldSelfProjectAuthorization.getActualTotalPayback() - oldMoneyPaybackModel.getAmount0()*oldMoneyPaybackModel.getExchangeRate());
//						selfProjectAuthorizationEditor.getModelCopy().setActualTotalPayback(selfProjectAuthorization.getActualTotalPayback() + moneyPaybackModel.getAmount0()*moneyPaybackModel.getExchangeRate());
//						oldSelfProjectAuthorizationEditor.save();
//					}
//					selfProjectAuthorizationEditor.save();
					
					// 如果有财务负责人，生成财务负责人到收款人的借出
					MoneyPayback moneyPaybackToFinancialOwner = null;
					if(newMoneyPaybackModel.get_mId() == null){
//						MoneyPaybackOfFinancialOwner = new MoneyPayback();
						moneyPaybackToFinancialOwner = new MoneyPayback();
					} else {
						String previousFinancialOwnerUserId = HyjUtil.ifNull(oldMoneyPaybackModel.getFinancialOwnerUserId() , "");
						String currentFinancialOwnerUserId = HyjUtil.ifNull(newMoneyPaybackModel.getFinancialOwnerUserId() , "");
//						MoneyPaybackOfFinancialOwner = new Select().from(MoneyPayback.class).where("depositExpenseId = ? AND ownerUserId = ?", oldMoneyPaybackModel.getId(), previousFinancialOwnerUserId).executeSingle();
//						if(MoneyPaybackOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//							MoneyPaybackOfFinancialOwner.delete();
//							MoneyPaybackOfFinancialOwner = new MoneyPayback();
//						}

						if(oldMoneyPaybackModel.getFinancialOwnerUserId() != null
								&& !oldMoneyPaybackModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
							moneyPaybackToFinancialOwner = new Select().from(MoneyPayback.class).where("moneyDepositPaybackContainerId = ? AND friendUserId = ?", oldMoneyPaybackModel.getId(), previousFinancialOwnerUserId).executeSingle();
						} else {
							moneyPaybackToFinancialOwner = new Select().from(MoneyPayback.class).where("moneyDepositPaybackContainerId = ? AND friendUserId = ?", oldMoneyPaybackModel.getId(), oldMoneyPaybackModel.getFriendUserId()).executeSingle();
						}
						if(moneyPaybackToFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
							moneyPaybackToFinancialOwner.delete();
							moneyPaybackToFinancialOwner = new MoneyPayback();
						}
					}
					if(newMoneyPaybackModel.getFinancialOwnerUserId() != null
							&& !newMoneyPaybackModel.getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						moneyPaybackToFinancialOwner.setFriendUserId(newMoneyPaybackModel.getFinancialOwnerUserId());
					} else {
						moneyPaybackToFinancialOwner.setFriendUserId(newMoneyPaybackModel.getFriendUserId());
					}
//						MoneyPaybackOfFinancialOwner.setMoneyDepositExpenseContainerId(newMoneyPaybackModel.getId());
//						MoneyPaybackOfFinancialOwner.setDate(newMoneyPaybackModel.getDate());
//						MoneyPaybackOfFinancialOwner.setAmount(newMoneyPaybackModel.getAmount());
//						MoneyPaybackOfFinancialOwner.setAddress(newMoneyPaybackModel.getAddress());
//						MoneyPaybackOfFinancialOwner.setCurrencyId1(newMoneyPaybackModel.getCurrencyId());
//						MoneyPaybackOfFinancialOwner.setExchangeRate(newMoneyPaybackModel.getExchangeRate());
//						MoneyPaybackOfFinancialOwner.setFinancialOwnerUserId(null);
//						MoneyPaybackOfFinancialOwner.setFriendAccountId(newMoneyPaybackModel.getFriendUserId());
//						MoneyPaybackOfFinancialOwner.setFriendUserId(newMoneyPaybackModel.getFriendUserId());
//						MoneyPaybackOfFinancialOwner.setGeoLat(newMoneyPaybackModel.getGeoLat());
//						MoneyPaybackOfFinancialOwner.setGeoLon(newMoneyPaybackModel.getGeoLon());
//						MoneyPaybackOfFinancialOwner.setLocalFriendId(newMoneyPaybackModel.getLocalFriendId());
//						MoneyPaybackOfFinancialOwner.setLocation(newMoneyPaybackModel.getLocation());
//						MoneyPaybackOfFinancialOwner.setMoneyAccountId(newMoneyPaybackModel.getMoneyAccountId(), newMoneyPaybackModel.getMoneyAccount().getCurrencyId());
//						MoneyPaybackOfFinancialOwner.setOwnerUserId(newMoneyPaybackModel.getFinancialOwnerUserId());
//						MoneyPaybackOfFinancialOwner.setOwnerFriendId(null);
//						MoneyPaybackOfFinancialOwner.setPaybackDate(newMoneyPaybackModel.getPaybackDate());
//						MoneyPaybackOfFinancialOwner.setPaybackedAmount(newMoneyPaybackModel.getPaybackedAmount());
//						MoneyPaybackOfFinancialOwner.setProjectId(newMoneyPaybackModel.getProjectId(), newMoneyPaybackModel.getProjectCurrencyId());
//						MoneyPaybackOfFinancialOwner.setPictureId(newMoneyPaybackModel.getPictureId());
//						MoneyPaybackOfFinancialOwner.setRemark(newMoneyPaybackModel.getRemark());
//						
//						MoneyPaybackOfFinancialOwner.save();
						
						moneyPaybackToFinancialOwner.setMoneyDepositPaybackContainerId(newMoneyPaybackModel.getId());
//						moneyPaybackToFinancialOwner.setLendType("Deposit");
						moneyPaybackToFinancialOwner.setDate(newMoneyPaybackModel.getDate());
						moneyPaybackToFinancialOwner.setAmount(newMoneyPaybackModel.getAmount());
						moneyPaybackToFinancialOwner.setAddress(newMoneyPaybackModel.getAddress());
						moneyPaybackToFinancialOwner.setCurrencyId1(newMoneyPaybackModel.getCurrencyId1());
						moneyPaybackToFinancialOwner.setExchangeRate(newMoneyPaybackModel.getExchangeRate());
//						moneyPaybackToFinancialOwner.setFinancialOwnerUserId(null);
						moneyPaybackToFinancialOwner.setFriendAccountId(newMoneyPaybackModel.getFriendUserId());
						moneyPaybackToFinancialOwner.setLocalFriendId(null);
						moneyPaybackToFinancialOwner.setGeoLat(newMoneyPaybackModel.getGeoLat());
						moneyPaybackToFinancialOwner.setGeoLon(newMoneyPaybackModel.getGeoLon());
						moneyPaybackToFinancialOwner.setLocation(newMoneyPaybackModel.getLocation());
						moneyPaybackToFinancialOwner.setMoneyAccountId(newMoneyPaybackModel.getMoneyAccountId(), newMoneyPaybackModel.getMoneyAccount().getCurrencyId());
//						moneyPaybackToFinancialOwner.setOwnerUserId(newMoneyPaybackModel.getFinancialOwnerUserId());
//						moneyPaybackToFinancialOwner.setOwnerFriendId(null);
						moneyPaybackToFinancialOwner.setProjectId(newMoneyPaybackModel.getProjectId(), newMoneyPaybackModel.getProject().getCurrencyId());
						moneyPaybackToFinancialOwner.setPictureId(newMoneyPaybackModel.getPictureId());
						moneyPaybackToFinancialOwner.setRemark(newMoneyPaybackModel.getRemark());
						
						moneyPaybackToFinancialOwner.save();

					
					
				mMoneyDepositPaybackContainerEditor.save();
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
	         		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=? AND state <> 'Delete'", project.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
					
					if(mMoneyDepositPaybackContainerEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
						HyjUtil.displayToast(R.string.app_permission_no_addnew);
						return;
					} else if(mMoneyDepositPaybackContainerEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
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
