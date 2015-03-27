package com.hoyoji.hoyoji.money;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjImageField.PictureItem;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.event.EventListFragment;
import com.hoyoji.hoyoji.event.EventMemberListFragment;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyApportion;
import com.hoyoji.hoyoji.models.MoneyBorrow;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyDepositIncomeContainer.MoneyDepositIncomeContainerEditor;
import com.hoyoji.hoyoji.models.MoneyLend;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.MoneyApportionField.ApportionItem;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;


public class MoneyDepositIncomeContainerFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_APPORTION_MEMBER_ID = 4;
	private static final int GET_REMARK = 3;
	private final static int GET_AMOUNT = 8;
	private final static int GET_EVENT_ID = 9;
	protected static final int GET_FINANCIALOWNER_ID = 0;
	
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private MoneyDepositIncomeContainerEditor mMoneyDepositIncomeContainerEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private MoneyApportionField mApportionFieldApportions = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericAmount = null;
	private HyjSelectorField mSelectorFieldMoneyAccount = null;
	private HyjSelectorField mSelectorFieldProject = null;
	private HyjSelectorField mSelectorFieldEvent = null;
	private View mViewSeparatorEvent = null;
	private HyjNumericField mNumericExchangeRate = null;
	private HyjRemarkField mRemarkFieldRemark = null;
	private ImageView mImageViewRefreshRate = null;
	private View mViewSeparatorExchange = null;
	private LinearLayout mLinearLayoutExchangeRate = null;
	
	private boolean hasEditPermission = true;
	protected TextView mTextViewApportionFieldTitle;
	private DataSetObserver mApportionCountObserver;
	private HyjSelectorField mSelectorFieldFinancialOwner;
	private TextView mTextViewFinancialOwner;
	private ImageButton mButtonExpandMore;
	private LinearLayout mLinearLayoutExpandMore;
	
	private ImageButton calculatorTextView = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneydepositincomecontainer;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		final MoneyDepositIncomeContainer moneyDepositIncomeContainer;
		
		Intent intent = getActivity().getIntent();
		final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1) {
			moneyDepositIncomeContainer =  new Select().from(MoneyDepositIncomeContainer.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyDepositIncomeContainer.hasEditPermission();
		} else {
			moneyDepositIncomeContainer = new MoneyDepositIncomeContainer();
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyDepositIncomeContainer.get_mId() == null && moneyAccountId != null) {
				moneyDepositIncomeContainer.setMoneyAccountId(moneyAccountId);
			}
			if(intent.getStringExtra("counterpartId") != null){
				moneyDepositIncomeContainer.setIsImported(true);
			}
		}
		
		mMoneyDepositIncomeContainerEditor = new MoneyDepositIncomeContainerEditor(moneyDepositIncomeContainer);
		
		setupDeleteButton(mMoneyDepositIncomeContainerEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageField_picture);
		mImageFieldPicture.setImages(moneyDepositIncomeContainer.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_textField_date);	
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyDepositIncomeContainer.getDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
			}
		}
		
		Long dateImport = intent.getLongExtra("date", -1);
		if(dateImport != -1){
			Date date= new Date(dateImport);
			mDateTimeFieldDate.setDate(date);
			mDateTimeFieldDate.setTextColor(Color.RED);
		}
		
		Project project;
		String projectId = intent.getStringExtra("projectId");//从消息导入
		if(projectId != null){
			moneyDepositIncomeContainer.setProjectId(projectId);
			project = HyjModel.getModel(Project.class, projectId);
		}else{
			project = moneyDepositIncomeContainer.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyDepositIncomeContainerFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.projectListFragment_title_select_project, null, GET_PROJECT_ID);
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
			if(moneyDepositIncomeContainer.get_mId() == null){
				if(eventId != null) {
					moneyDepositIncomeContainer.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyDepositIncomeContainer.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyDepositIncomeContainer.getEvent();
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

                    MoneyDepositIncomeContainerFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });

		setupApportionField(moneyDepositIncomeContainer);
		
		mNumericAmount = (HyjNumericField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_textField_amount);		
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
			mApportionFieldApportions.setTotalAmount(amount*exchangeRate);
		} else {
			mNumericAmount.setNumber(moneyDepositIncomeContainer.getAmount());
		}
//		if(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor() != null){
			mNumericAmount.getEditText().setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
			mNumericAmount.getEditText().setHintTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor()));
//		}
		
		mNumericAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				mApportionFieldApportions.setTotalAmount(mNumericAmount
						.getNumber());
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
		
		MoneyAccount moneyAccount = moneyDepositIncomeContainer.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_selectorField_moneyAccount);

		if(moneyAccount != null){
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyDepositIncomeContainerFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_MONEYACCOUNT_ID);
			}
		});	
		
		
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_textField_exchangeRate);		
		mNumericExchangeRate.setNumber(moneyDepositIncomeContainer.getExchangeRate());
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_linearLayout_exchangeRate);
		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyDepositIncomeContainer.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyDepositIncomeContainerFormFragment.this
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
				MoneyDepositIncomeContainerFormFragment.this.openActivityWithFragmentForResult(
								HyjCalculatorFormFragment.class,
								R.string.hyjCalculatorFormFragment_title,
								bundle, GET_AMOUNT);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageView_camera);	
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
		} else if(moneyDepositIncomeContainer.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(moneyDepositIncomeContainer.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyDepositIncomeContainer.getFinancialOwnerUserId()));
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
				bundle.putString("FINANCIAL_TYPE", "MoneyDepositIncome");
				
				MoneyDepositIncomeContainerFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_refresh_exchangeRate);	
		mImageViewRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
                if(mSelectorFieldProject.getModelId() == null){
                    HyjUtil.displayToast("请先选择一个圈子");
                    return;
                }
				if(mSelectorFieldMoneyAccount.getModelId() != null && mSelectorFieldProject.getModelId() != null){
					MoneyAccount moneyAccount = (MoneyAccount)HyjModel.getModel(MoneyAccount.class, mSelectorFieldMoneyAccount.getModelId());
					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
					
					String fromCurrency = moneyAccount.getCurrencyId();
					String toCurrency = project.getCurrencyId();
					if(fromCurrency != null && toCurrency != null){
						HyjUtil.startRoateView(mImageViewRefreshRate);
						mImageViewRefreshRate.setEnabled(false);
						HyjUtil.updateExchangeRate(fromCurrency, toCurrency, mImageViewRefreshRate, mNumericExchangeRate);
					} else {
						HyjUtil.displayToast(R.string.moneyDepositIncomeContainerFormFragment_toast_select_currency);
					}
				}else{
					HyjUtil.displayToast(R.string.moneyDepositIncomeContainerFormFragment_toast_select_currency);
				}
			}
		});
		
			
//			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_apportion_add).setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Bundle bundle = new Bundle();
//					Project project = HyjModel.getModel(Project.class,mSelectorFieldProject.getModelId());
//					bundle.putLong("MODEL_ID", project.get_mId());
//					openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyDepositIncomeContainerFormFragment_moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
//				}
//			});
			
			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_apportion_add).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    if(mSelectorFieldProject.getModelId() == null){
                        HyjUtil.displayToast("请先选择一个圈子");
                        return;
                    }
					Bundle bundle = new Bundle();
					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
					bundle.putLong("MODEL_ID", project.get_mId());
					if(!project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						if(mSelectorFieldEvent.getModelId() != null){
							bundle.putString("EVENTID", mSelectorFieldEvent.getModelId());
							openActivityWithFragmentForResult(EventMemberListFragment.class, R.string.moneyDepositIncomeContainerFormFragment_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						} else {
							openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyDepositIncomeContainerFormFragment_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						}
					} else {
						if(mSelectorFieldEvent.getModelId() != null){
							bundle.putString("EVENTID", mSelectorFieldEvent.getModelId());
							openActivityWithFragmentForResult(SelectApportionProjectEventMemberListFragment.class, R.string.moneyDepositIncomeContainerFormFragment_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						} else {
							openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyDepositIncomeContainerFormFragment_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						}
					}
				}
			});
			
			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_apportion_add_all).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addAllProjectMemberIntoApportionsField(moneyDepositIncomeContainer);
				}
			});
			
			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_apportion_more_actions).setOnClickListener(new OnClickListener() {
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
								mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
								return true;
							} else if (item.getItemId() == R.id.moneyApportionField_menu_moreActions_all_average) {
								mApportionFieldApportions.setAllApportionAverage();
								mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
								return true;
							} else if (item.getItemId() == R.id.moneyApportionField_menu_moreActions_all_share) {
								mApportionFieldApportions.setAllApportionShare();
								mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
								return true;
							}
							return false;
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
			// 只在新增时才自动打开软键盘， 修改时不自动打开
			if (modelId == -1) {
				setExchangeRate(false);
				if(this.getUserVisibleHint()){
					this.mNumericAmount.showSoftKeyboard();
				}
//				this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
	    if(mMoneyDepositIncomeContainerEditor!= null && mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
	
	private void addAllProjectMemberIntoApportionsField(MoneyDepositIncomeContainer moneyIncomeContainer) {
        if(mSelectorFieldProject.getModelId() == null){
            HyjUtil.displayToast("请先选择一个圈子");
            return;
        }
		if(mSelectorFieldEvent.getModelId() == null){
			Project project = HyjModel.getModel(Project.class,mSelectorFieldProject.getModelId());
			List<ProjectShareAuthorization> projectShareAuthorizations = project.getShareAuthorizations();
			for (int i = 0; i < projectShareAuthorizations.size(); i++) {
				if(projectShareAuthorizations.get(i).getState().equalsIgnoreCase("Delete") ||
						projectShareAuthorizations.get(i).getToBeDetermined()){
					continue;
				}
				MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
				apportion.setAmount(0.0);
				apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
				apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
				apportion.setMoneyDepositIncomeContainerId(moneyIncomeContainer.getId());
				if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
					apportion.setApportionType("Average");
				} else {
					apportion.setApportionType("Share");
				}
				mApportionFieldApportions.addApportion(apportion, project.getId(), null, ApportionItem.NEW);
			}
			mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
		} else {
//			Event event = HyjModel.getModel(Event.class,mSelectorFieldEvent.getModelId());
			Project project = HyjModel.getModel(Project.class,mSelectorFieldProject.getModelId());
			List<ProjectShareAuthorization> projectShareAuthorizations = project.getShareAuthorizations();
			for (int i = 0; i < projectShareAuthorizations.size(); i++) {
				if(projectShareAuthorizations.get(i).getState().equalsIgnoreCase("Delete") ||
						projectShareAuthorizations.get(i).getToBeDetermined()){
					continue;
				}
				EventMember em = null;
				if(projectShareAuthorizations.get(i).getFriendUserId() != null){
					em = new Select().from(EventMember.class).where("eventId=? AND friendUserId=?", mSelectorFieldEvent.getModelId(), projectShareAuthorizations.get(i).getFriendUserId()).executeSingle();
				} else {
					em = new Select().from(EventMember.class).where("eventId=? AND localFriendId=?", mSelectorFieldEvent.getModelId(), projectShareAuthorizations.get(i).getLocalFriendId()).executeSingle();
				}
				if(em != null) {
					MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
					apportion.setAmount(0.0);
					apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
					apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
					apportion.setMoneyDepositIncomeContainerId(moneyIncomeContainer.getId());
					if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
						apportion.setApportionType("Average");
					} else {
						apportion.setApportionType("Share");
					}
					mApportionFieldApportions.addApportion(apportion, project.getId(),mSelectorFieldEvent.getModelId(), ApportionItem.NEW);
				}
			}
			mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
		}
	}
	
	private void setupApportionField(MoneyDepositIncomeContainer moneyDepositIncomeContainer) {

		mApportionFieldApportions = (MoneyApportionField) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_apportionField);
		mTextViewApportionFieldTitle = (TextView) getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_apportionField_title);
		mApportionCountObserver = new DataSetObserver(){
	        @Override
	        public void onChanged() {
	    		mTextViewApportionFieldTitle.setText(getString(R.string.moneyDepositIncomeContainerFormFragment_moneyApportionField_title)+"("+mApportionFieldApportions.getApportionCount()+")");
	        }
		};
		mApportionFieldApportions.getAdapter().registerDataSetObserver(mApportionCountObserver);
		
		List<MoneyDepositIncomeApportion> moneyApportions = null;
		
		if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() == null) {
			
			moneyApportions = new ArrayList<MoneyDepositIncomeApportion>();
//			if(moneyDepositIncomeContainer.getProject() != null && moneyDepositIncomeContainer.getProject().getAutoApportion()){
//				List<ProjectShareAuthorization> projectShareAuthorizations = moneyDepositIncomeContainer.getProject().getShareAuthorizations();
//				for(int i=0; i < projectShareAuthorizations.size(); i++){
//					MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
//					apportion.setAmount(0.0);
//					apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
//					apportion.setMoneyDepositIncomeContainerId(moneyDepositIncomeContainer.getId());
//					apportion.setApportionType("Share");
//					
//					moneyApportions.add(apportion);
//				}
//			} else 
//			if(moneyDepositIncomeContainer.getProject() != null) {
//				MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
//				apportion.setAmount(moneyDepositIncomeContainer.getAmount0());
//				apportion.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
//				apportion.setMoneyDepositIncomeContainerId(moneyDepositIncomeContainer.getId());
//				apportion.setApportionType("Average");
//				moneyApportions.add(apportion);
//			}

			Intent intent = getActivity().getIntent();
			String friendUserId = intent.getStringExtra("friendUserId");
			if(friendUserId != null){
				MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
				apportion.setAmount(moneyDepositIncomeContainer.getAmount0());
				apportion.setFriendUserId(friendUserId);
				apportion.setMoneyDepositIncomeContainerId(moneyDepositIncomeContainer.getId());
				ProjectShareAuthorization projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", moneyDepositIncomeContainer.getProjectId(), friendUserId).executeSingle();
				if(projectShareAuthorization.getSharePercentageType() != null && projectShareAuthorization.getSharePercentageType().equals("Average")){
					apportion.setApportionType("Average");
				} else {
					apportion.setApportionType("Share");
				}
				moneyApportions.add(apportion);
			}
			
		} else {
			moneyApportions = moneyDepositIncomeContainer.getApportions();
		}
		
		mApportionFieldApportions.init(moneyDepositIncomeContainer.getAmount0(), moneyApportions, moneyDepositIncomeContainer.getProjectId(), moneyDepositIncomeContainer.getEventId(), moneyDepositIncomeContainer.getId());
	}
	
	private void setupDeleteButton(HyjModelEditor<MoneyDepositIncomeContainer> moneyDepositIncomeContainerEditor) {

		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
		
		final MoneyDepositIncomeContainer moneyDepositIncomeContainer = moneyDepositIncomeContainerEditor.getModelCopy();
		
		if (moneyDepositIncomeContainer.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(moneyDepositIncomeContainer.hasDeletePermission()){
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									try {
										ActiveAndroid.beginTransaction();

										MoneyAccount moneyAccount = moneyDepositIncomeContainer.getMoneyAccount();
										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() - moneyDepositIncomeContainer.getAmount0());

										//更新圈子余额
										Project oldProject = moneyDepositIncomeContainer.getProject();
										HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
										oldProjectEditor.getModelCopy().setDepositTotal(oldProject.getDepositTotal() - moneyDepositIncomeContainer.getAmount0()*moneyDepositIncomeContainer.getExchangeRate());
										oldProjectEditor.save();
										
										// 更新旧圈子收入所有者的实际借入
										ProjectShareAuthorization oldProjectShareAuthorization = ProjectShareAuthorization.getSelfProjectShareAuthorization(oldProject.getId());
										HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
										oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (moneyDepositIncomeContainer.getAmount0() * moneyDepositIncomeContainer.getExchangeRate()));
										oldProjectShareAuthorizationEditor.save();
										
										//删除收入的同时删除分摊
										Iterator<MoneyDepositIncomeApportion> moneyDepositIncomeApportions = moneyDepositIncomeContainer.getApportions().iterator();
										while (moneyDepositIncomeApportions.hasNext()) {
											MoneyDepositIncomeApportion moneyDepositIncomeApportion = moneyDepositIncomeApportions.next();
											MoneyDepositIncomeContainer.deleteApportion(moneyDepositIncomeApportion, mMoneyDepositIncomeContainerEditor);
										}

										moneyDepositIncomeContainer.delete();
										moneyAccountEditor.save();

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

		if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
//			mNumericAmount.setNumber(mMoneyDepositIncomeContainerEditor.getModel().getProjectAmount());
			mNumericAmount.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

			mSelectorFieldProject.setEnabled(false);
			mSelectorFieldEvent.setEnabled(false);
			
			mNumericExchangeRate.setEnabled(false);
			
			mApportionFieldApportions.setEnabled(false);

			mRemarkFieldRemark.setEnabled(false);
			
			if(this.mOptionsMenu != null){
		    	hideSaveAction();
			}
			
			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_apportion_add).setEnabled(false);
			getView().findViewById(R.id.moneyDepositIncomeContainerFormFragment_imageButton_apportion_add_all).setEnabled(false);
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
				mLinearLayoutExchangeRate.setVisibility(View.GONE);
				mViewSeparatorExchange.setVisibility(View.GONE);
			}else{
				mLinearLayoutExchangeRate.setVisibility(View.VISIBLE);
				mViewSeparatorExchange.setVisibility(View.VISIBLE);
				
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
		MoneyDepositIncomeContainer modelCopy =  mMoneyDepositIncomeContainerEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericAmount.getNumber());
		modelCopy.setMoneyAccountId(mSelectorFieldMoneyAccount.getModelId());

        if(mSelectorFieldProject.getModelId() != null){
            modelCopy.setProject(HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId()));
        }
        modelCopy.setEventId(mSelectorFieldEvent.getModelId());
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());
		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
		
		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
		
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyDepositIncomeContainerEditor.getValidationError("datetime"));
		mNumericAmount.setError(mMoneyDepositIncomeContainerEditor.getValidationError("amount"));
		if(mMoneyDepositIncomeContainerEditor.getValidationError("amount") != null){
					mNumericAmount.showSoftKeyboard();
				}
		mSelectorFieldMoneyAccount.setError(mMoneyDepositIncomeContainerEditor.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyDepositIncomeContainerEditor.getValidationError("project"));
		mSelectorFieldEvent.setError(mMoneyDepositIncomeContainerEditor.getValidationError("Event"));
		mNumericExchangeRate.setError(mMoneyDepositIncomeContainerEditor.getValidationError("exchangeRate"));
		mRemarkFieldRemark.setError(mMoneyDepositIncomeContainerEditor.getValidationError("remark"));
		mApportionFieldApportions.setError(mMoneyDepositIncomeContainerEditor.getValidationError("apportionTotalAmount"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() == null && !mMoneyDepositIncomeContainerEditor.getModelCopy().hasAddNewPermission(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else if(mApportionFieldApportions.getCount() <= 0){
			HyjUtil.displayToast("请选择至少一个缴费成员");
		}else{
		
		mMoneyDepositIncomeContainerEditor.validate();
		
		if (mApportionFieldApportions.getCount() > 0) {
			if (mNumericAmount.getNumber() != null && !mNumericAmount.getNumber().equals(mApportionFieldApportions.getTotalAmount())) {
				mMoneyDepositIncomeContainerEditor.setValidationError("apportionTotalAmount",R.string.moneyApportionField_select_toast_apportion_totalAmount_not_equal);
			} else {
				mMoneyDepositIncomeContainerEditor.removeValidationError("apportionTotalAmount");
			}
		} else {
			mMoneyDepositIncomeContainerEditor.removeValidationError("apportionTotalAmount");
		}
		
		if(mMoneyDepositIncomeContainerEditor.hasValidationErrors()){
			showValidatioErrors();
		} else {
			try {
				ActiveAndroid.beginTransaction();
				
				savePictures();

				saveApportions();
				
				MoneyDepositIncomeContainer oldMoneyDepositIncomeContainerModel = mMoneyDepositIncomeContainerEditor.getModel();
				MoneyDepositIncomeContainer moneyDepositIncomeContainerModel = mMoneyDepositIncomeContainerEditor.getModelCopy();
				
				//设置默认圈子和账户
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				if(moneyDepositIncomeContainerModel.get_mId() == null && !userData.getActiveMoneyAccountId().equals(moneyDepositIncomeContainerModel.getMoneyAccountId()) ){
					HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
					userDataEditor.getModelCopy().setActiveMoneyAccountId(moneyDepositIncomeContainerModel.getMoneyAccountId());
					userDataEditor.getModelCopy().setActiveProjectId(moneyDepositIncomeContainerModel.getProjectId());
					userDataEditor.save();
				}
				
				//设置默认活动
				Project project = moneyDepositIncomeContainerModel.getProject();
				if(moneyDepositIncomeContainerModel.get_mId() == null){
					if((moneyDepositIncomeContainerModel.getEventId() != null && !moneyDepositIncomeContainerModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(moneyDepositIncomeContainerModel.getEventId()))){
						HyjModelEditor<Project> projectEditor = project.newModelEditor();
						projectEditor.getModelCopy().setActiveEventId(moneyDepositIncomeContainerModel.getEventId());
						projectEditor.save();
					}
				}
				
				//当前汇率不存在时，创建汇率
				String localCurrencyId = moneyDepositIncomeContainerModel.getMoneyAccount().getCurrencyId();
				String foreignCurrencyId = moneyDepositIncomeContainerModel.getProject().getCurrencyId();
				if(CREATE_EXCHANGE == 1){
					MoneyAccount moneyAccount = moneyDepositIncomeContainerModel.getMoneyAccount();
//					Project project = moneyDepositIncomeContainerModel.getProject();
					
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(moneyAccount.getCurrencyId());
					newExchange.setForeignCurrencyId(project.getCurrencyId());
					newExchange.setRate(moneyDepositIncomeContainerModel.getExchangeRate());
					newExchange.save();
				}else {
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = null;
						Double exRate = null;
						Double rate = HyjUtil.toFixed2(moneyDepositIncomeContainerModel.getExchangeRate());
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
				
				    MoneyAccount oldMoneyAccount = oldMoneyDepositIncomeContainerModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = moneyDepositIncomeContainerModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					//更新账户余额
					if(moneyDepositIncomeContainerModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - oldMoneyDepositIncomeContainerModel.getAmount0() + moneyDepositIncomeContainerModel.getAmount0());
					}else{
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() - oldMoneyDepositIncomeContainerModel.getAmount0());
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + moneyDepositIncomeContainerModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();

					Project oldProject = oldMoneyDepositIncomeContainerModel.getProject();
					Project newProject = moneyDepositIncomeContainerModel.getProject();
					HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
					
					//更新圈子余额
					if(moneyDepositIncomeContainerModel.get_mId() == null || oldProject.getId().equals(newProject.getId())){
						newProjectEditor.getModelCopy().setDepositTotal(newProject.getDepositTotal() - oldMoneyDepositIncomeContainerModel.getAmount0()*oldMoneyDepositIncomeContainerModel.getExchangeRate() + moneyDepositIncomeContainerModel.getAmount0()*moneyDepositIncomeContainerModel.getExchangeRate());
					} else {
						HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
						oldProjectEditor.getModelCopy().setDepositTotal(oldProject.getDepositTotal() - oldMoneyDepositIncomeContainerModel.getAmount0()*oldMoneyDepositIncomeContainerModel.getExchangeRate());
						newProjectEditor.getModelCopy().setDepositTotal(newProject.getDepositTotal() + moneyDepositIncomeContainerModel.getAmount0()*moneyDepositIncomeContainerModel.getExchangeRate());
						oldProjectEditor.save();
					}
					newProjectEditor.save();
					
					
					//更新收款人（自己）的实际借入
					ProjectShareAuthorization selfProjectAuthorization = mMoneyDepositIncomeContainerEditor.getNewSelfProjectShareAuthorization();
					HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
				    
					if(moneyDepositIncomeContainerModel.get_mId() == null || oldMoneyDepositIncomeContainerModel.getProjectId().equals(moneyDepositIncomeContainerModel.getProjectId())){
				    	selfProjectAuthorizationEditor.getModelCopy().setActualTotalBorrow(selfProjectAuthorization.getActualTotalBorrow() - oldMoneyDepositIncomeContainerModel.getProjectAmount() + moneyDepositIncomeContainerModel.getProjectAmount());
					}else{
						selfProjectAuthorizationEditor.getModelCopy().setActualTotalBorrow(selfProjectAuthorization.getActualTotalBorrow() + moneyDepositIncomeContainerModel.getAmount0()*moneyDepositIncomeContainerModel.getExchangeRate());
						
						ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyDepositIncomeContainerEditor.getOldSelfProjectShareAuthorization();
						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
						oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldSelfProjectAuthorization.getActualTotalBorrow() - oldMoneyDepositIncomeContainerModel.getAmount0()*oldMoneyDepositIncomeContainerModel.getExchangeRate());
						oldSelfProjectAuthorizationEditor.save();
					}
					selfProjectAuthorizationEditor.save();

					
				mMoneyDepositIncomeContainerEditor.save();
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

	private void savePictures(){
		 HyjImageField.ImageGridAdapter adapter = mImageFieldPicture.getAdapter();
			int count = adapter.getCount();
			boolean mainPicSet = false;
			for(int i = 0; i < count; i++){
				PictureItem pi = adapter.getItem(i);
				if(pi.getState() == PictureItem.NEW){
					Picture newPic = pi.getPicture();
					newPic.setRecordId(mMoneyDepositIncomeContainerEditor.getModel().getId());
					newPic.setRecordType("MoneyDepositIncomeContainer");
					newPic.setProjectId(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId());
					newPic.setDisplayOrder(i);
					newPic.save();
				} else if(pi.getState() == PictureItem.DELETED){
					pi.getPicture().delete();
				} else if(pi.getState() == PictureItem.CHANGED){

				}
				if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
					mainPicSet = true;
					mMoneyDepositIncomeContainerEditor.getModelCopy().setPicture(pi.getPicture());
				}
			}
	 }
	
	 private void saveApportions() {
		 MoneyApportionField.ImageGridAdapter adapter = mApportionFieldApportions.getAdapter();
			int count = adapter.getCount();
			int savedCount = 0;
			List<ApportionItem> apportionItems = new ArrayList<ApportionItem>();
			for (int i = 0; i < count; i++) {
				apportionItems.add(adapter.getItem(i));
			}
			savedCount = MoneyDepositIncomeContainer.saveApportions(apportionItems, mMoneyDepositIncomeContainerEditor);
//			for (int i = 0; i < count; i++) {
//				ApportionItem<MoneyApportion> api = adapter.getItem(i);
//				MoneyDepositIncomeApportion apportion = (MoneyDepositIncomeApportion) api.getApportion();
//	            
//					if(api.getState() == ApportionItem.DELETED ){
//						// 维护缴款人的 ProjectShareAuthorization
//						ProjectShareAuthorization psa = api.getProjectShareAuthorization();
//						HyjModelEditor<ProjectShareAuthorization> psaEditor = psa.newModelEditor();
//						psaEditor.getModelCopy().setActualTotalLend(psa.getActualTotalLend() - apportion.getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
//						psaEditor.save();
//						
//						List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=?", apportion.getId()).execute();
//						for(MoneyBorrow moneyBorrow : moneyBorrows){
//							if(HyjApplication.getInstance().getCurrentUser().getId().equals(moneyBorrow.getOwnerUserId())) {
//								MoneyAccount debtAccount = null;
//								if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null){
//									debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId());
//								} else {
//									debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), moneyBorrow.getLocalFriendId(), moneyBorrow.getFriendUserId());
//								}
//							
//								HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//								debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + moneyBorrow.getProjectAmount());
//								debtAccountEditor.save(); 
//							}
//							moneyBorrow.delete();
//						} 
//						List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=?", apportion.getId()).execute();
//						for(MoneyLend moneyLend : moneyLends){
//							moneyLend.delete();
//						}
//						apportion.delete();
//					} else {
//						HyjModelEditor<MoneyDepositIncomeApportion> apportionEditor = apportion.newModelEditor();
//						if(api.getState() != ApportionItem.UNCHANGED		
//								|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositIncomeContainerEditor.getModel().getProjectId())
//								|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyDepositIncomeContainerEditor.getModel().getMoneyAccountId())) {
//							api.saveToCopy(apportionEditor.getModelCopy());
//						}
//						
//						MoneyAccount debtAccount = null;
//						// 该好友是网络好友 或 该好友是本地好友
//						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
//							if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//								debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
//							} else {
//								debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//							}
//						} else {
//							debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
//						}
//						if(api.getState() == ApportionItem.NEW){
//			                if(debtAccount != null){
//			                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//			                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//			                	debtAccountEditor.save();
//			                }else {
//			                	if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
//									if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//										MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//				                	} else {
//				                		MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), null, mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//				                	}
//			                	} else {
//			                		MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//					            }
//				            }
//						} else {
//							MoneyAccount oldDebtAccount = null;
//							if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null){
//								if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//									oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
//								} else {
//									oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModel().getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId());
//								}
//							} else {
//								oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
//							}
//							if(debtAccount == null){
//								if(oldDebtAccount != null){
//									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
//									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + apportionEditor.getModel().getAmount0()*apportionEditor.getModel().getExchangeRate());
//									oldDebtAccountEditor.save();
//								}
//								if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
//									if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//										MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//									} else {
//										MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), null, mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//									}
//								} else {
//									MoneyAccount.createDebtAccount(apportion.getFriendDisplayName(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId()), apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//								}
//							}else if(oldDebtAccount != null && debtAccount.getId().equals(oldDebtAccount.getId())){
//									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
//									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + apportionEditor.getModel().getAmount0()*apportionEditor.getModel().getExchangeRate() - apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//									oldDebtAccountEditor.save();
//							}else {
//								if(oldDebtAccount != null){
//									HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
//									oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + apportionEditor.getModel().getAmount0()*apportionEditor.getModel().getExchangeRate());
//									oldDebtAccountEditor.save();
//								}
//								HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//			                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//			                	debtAccountEditor.save();
//							}
//						}
//						
//						// 维护缴款人的 ProjectShareAuthorization
//						ProjectShareAuthorization newPsa = api.getProjectShareAuthorization();
//						HyjModelEditor<ProjectShareAuthorization> newPsaEditor = newPsa.newModelEditor();
//						if(apportion.get_mId() == null) {
//							newPsaEditor.getModelCopy().setActualTotalLend(newPsa.getActualTotalLend() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//						} else if(mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositIncomeContainerEditor.getModel().getProjectId())){
//							newPsaEditor.getModelCopy().setActualTotalLend(newPsa.getActualTotalLend() - apportionEditor.getModel().getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//						} else {
//							ProjectShareAuthorization oldPsa;
//							if(apportion.getFriendUserId() != null){
//								oldPsa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId AND state <> 'Delete'", mMoneyDepositIncomeContainerEditor.getModel().getProjectId(), apportion.getFriendUserId()).executeSingle();
//							} else {
//								oldPsa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId AND state <> 'Delete'", mMoneyDepositIncomeContainerEditor.getModel().getProjectId(), apportion.getLocalFriendId()).executeSingle();
//							}
//							HyjModelEditor<ProjectShareAuthorization> oldPsaEditor = oldPsa.newModelEditor();
//							oldPsaEditor.getModelCopy().setActualTotalLend(oldPsa.getActualTotalLend() - apportionEditor.getModel().getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
//							newPsaEditor.getModelCopy().setActualTotalLend(newPsa.getActualTotalLend() + apportionEditor.getModelCopy().getAmount0()*mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//						}
//						newPsaEditor.save();
//						
//						MoneyBorrow moneyBorrow = null;
//						MoneyLend moneyLend = null;
//						MoneyBorrow moneyBorrowOfFinancialOwner = null;
//						MoneyLend moneyLendOfFinancialOwner = null;
//						if(apportion.get_mId() != null){
//							moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
//							if(apportionEditor.getModel().getLocalFriendId() != null){
//								moneyLend = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=? AND ownerFriendId=?", apportionEditor.getModel().getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
//							} else {
//								moneyLend = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
//							}
//							if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null &&
//									!mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//								moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
//								moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=? AND ownerUserId=?", apportionEditor.getModel().getId(), mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
//							}
//							String previousFinancialOwnerUserId = HyjUtil.ifNull(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() , "");
//							String currentFinancialOwnerUserId = HyjUtil.ifNull(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() , "");
//							if(!previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//								if(moneyBorrow != null){
//									moneyBorrow.delete();
//									moneyBorrow = new MoneyBorrow();
//								}
//								if(moneyLend != null){
//									moneyLend.delete();
//									moneyLend = new MoneyLend();
//								}
//								if(moneyBorrowOfFinancialOwner != null){
//									moneyBorrowOfFinancialOwner.delete();
//									moneyBorrowOfFinancialOwner = new MoneyBorrow();
//								}
//								if(moneyLendOfFinancialOwner != null){
//									moneyLendOfFinancialOwner.delete();
//									moneyLendOfFinancialOwner = new MoneyLend();
//								}
//							}
//						}
//						if(moneyBorrow == null){
//							moneyBorrow = new MoneyBorrow();
//						}
//						if(moneyLend == null){
//							moneyLend = new MoneyLend();
//						}
//						if(moneyBorrowOfFinancialOwner == null){
//							moneyBorrowOfFinancialOwner = new MoneyBorrow();
//						}
//						if(moneyLendOfFinancialOwner == null){
//							moneyLendOfFinancialOwner = new MoneyLend();
//						}
//						
//						moneyBorrow.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
//						moneyBorrow.setAmount(apportionEditor.getModelCopy().getAmount0());
//						moneyBorrow.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
//						moneyBorrow.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
//						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
//								|| HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId())){
//							moneyBorrow.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
//							moneyBorrow.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//						} else {
//							moneyBorrow.setFriendUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//						}
//						moneyBorrow.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//						moneyBorrow.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
//						moneyBorrow.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
//						
//						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//							MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//							moneyBorrow.setMoneyAccountId(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
//						} else {
//							moneyBorrow.setMoneyAccountId(null, null);
//						}
//
//						moneyBorrow.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
//						moneyBorrow.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
//						moneyBorrow.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
//						moneyBorrow.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
//						moneyBorrow.save();
//						
//						if(apportionEditor.getModelCopy().getLocalFriendId() != null){
//							moneyLend.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
////							moneyLend.setMoneyBorrowId(moneyBorrow.getId());
//							moneyLend.setAmount(apportionEditor.getModelCopy().getAmount0());
//							moneyLend.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
//							moneyLend.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
//							if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
//									|| HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId())){
//								moneyLend.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
//							} else {
//								moneyLend.setFriendUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//							}
//							moneyLend.setLocalFriendId(null);
//							moneyLend.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//							moneyLend.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
//							moneyLend.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
//							moneyLend.setMoneyAccountId(null, moneyBorrow.getCurrencyId1());
//							moneyLend.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
//							moneyLend.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
//							moneyLend.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
//							moneyLend.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
//							moneyLend.setOwnerFriendId(apportionEditor.getModel().getLocalFriendId());
//							moneyLend.setOwnerUserId("");
//							moneyLend.save();
//						}
//						
//						if(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null
//								&& !HyjApplication.getInstance().getCurrentUser().getId().equals(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId())){
//							moneyBorrowOfFinancialOwner.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
//							moneyBorrowOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
//							moneyBorrowOfFinancialOwner.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
//							moneyBorrowOfFinancialOwner.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
//							moneyBorrowOfFinancialOwner.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
//							moneyBorrowOfFinancialOwner.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//							moneyBorrowOfFinancialOwner.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//							moneyBorrowOfFinancialOwner.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
//							moneyBorrowOfFinancialOwner.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
//							moneyBorrowOfFinancialOwner.setOwnerUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//							
//							if(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//								MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//								moneyBorrowOfFinancialOwner.setMoneyAccountId(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
//							} else {
//								moneyBorrowOfFinancialOwner.setMoneyAccountId(null, null);
//							}
//							moneyBorrowOfFinancialOwner.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
//							moneyBorrowOfFinancialOwner.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
//							moneyBorrowOfFinancialOwner.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
//							moneyBorrowOfFinancialOwner.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
//							moneyBorrowOfFinancialOwner.save();
//							
//							moneyLendOfFinancialOwner.setMoneyDepositIncomeApportionId(apportionEditor.getModelCopy().getId());
////							moneyLendOfFinancialOwner.setMoneyBorrowId(moneyBorrow.getId());
//							moneyLendOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
//							moneyLendOfFinancialOwner.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
//							moneyLendOfFinancialOwner.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
//							moneyLendOfFinancialOwner.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
//							moneyLendOfFinancialOwner.setLocalFriendId(null);
//							moneyLendOfFinancialOwner.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
//							moneyLendOfFinancialOwner.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
//							moneyLendOfFinancialOwner.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
//							moneyLendOfFinancialOwner.setMoneyAccountId(null, moneyBorrow.getCurrencyId1());
//							moneyLendOfFinancialOwner.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
//							moneyLendOfFinancialOwner.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
//							moneyLendOfFinancialOwner.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
//							moneyLendOfFinancialOwner.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
//							moneyLendOfFinancialOwner.setOwnerUserId(mMoneyDepositIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//							moneyLendOfFinancialOwner.save();
//						}
////						if(api.getState() != ApportionItem.UNCHANGED
////								|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyDepositIncomeContainerEditor.getModel().getProjectId())
////								|| !mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyDepositIncomeContainerEditor.getModel().getMoneyAccountId())) {
//							apportionEditor.save();
////						}
//						savedCount++;
//					}
//			}
			
			// 从隐藏掉的分摊里面删除原来的分摊
			Iterator<ApportionItem<MoneyApportion>> it = mApportionFieldApportions.getHiddenApportions().iterator();
			while (it.hasNext()) {
				// Get element
				ApportionItem<MoneyApportion> item = it.next();
				if (item.getState() != ApportionItem.NEW) {
						MoneyDepositIncomeApportion apportion = ((MoneyDepositIncomeApportion) item.getApportion());
						MoneyDepositIncomeContainer.deleteApportion(apportion, mMoneyDepositIncomeContainerEditor);
//						// 维护缴款人的 ProjectShareAuthorization
//						ProjectShareAuthorization psa = item.getProjectShareAuthorization();
//						HyjModelEditor<ProjectShareAuthorization> psaEditor = psa.newModelEditor();
//						psaEditor.getModelCopy().setActualTotalLend(psa.getActualTotalLend() - apportion.getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
//						psaEditor.save();
//					
//						List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyDepositIncomeApportionId=?", apportion.getId()).execute();
//						for(MoneyBorrow moneyBorrow : moneyBorrows){
//							MoneyAccount debtAccount = null;
//							if(HyjApplication.getInstance().getCurrentUser().getId().equals(moneyBorrow.getOwnerUserId())){
//								if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null){
//									if(mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//										debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), moneyBorrow.getLocalFriendId(), moneyBorrow.getFriendUserId());
//									} else {
//										debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), null, mMoneyDepositIncomeContainerEditor.getModel().getFinancialOwnerUserId());
//									}
//								} else {
//									debtAccount = MoneyAccount.getDebtAccount(moneyBorrow.getProject().getCurrencyId(), moneyBorrow.getLocalFriendId(), moneyBorrow.getFriendUserId());
//								}
//								HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//								debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() + moneyBorrow.getProjectAmount());
//								debtAccountEditor.save();
//							}
//							moneyBorrow.delete();
//						}
//						List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyDepositIncomeApportionId=?", apportion.getId()).execute();
//						for(MoneyLend moneyLend : moneyLends){
//							moneyLend.delete();
//						}
//						apportion.delete();
				}
			}
			
			// 如果列表里一个都没有被保存，我们生成一个默认的分摊
			if (savedCount == 0) {
				MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
				apportion.setAmount(mMoneyDepositIncomeContainerEditor.getModelCopy().getAmount0());
				apportion.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
				apportion.setMoneyDepositIncomeContainerId(mMoneyDepositIncomeContainerEditor.getModelCopy().getId());
				apportion.setApportionType("Average");

				// 维护缴款人的 ProjectShareAuthorization
				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=? AND state <> 'Delete'", mMoneyDepositIncomeContainerEditor.getModelCopy().getProjectId(), apportion.getFriendUserId()).executeSingle();
				HyjModelEditor<ProjectShareAuthorization> psaEditor = psa.newModelEditor();
				psaEditor.getModelCopy().setActualTotalLend(psa.getActualTotalLend() + apportion.getAmount0()*mMoneyDepositIncomeContainerEditor.getModel().getExchangeRate());
				psaEditor.save();
				
				MoneyBorrow moneyBorrow = null;
				moneyBorrow = new MoneyBorrow();
//				moneyBorrow.setBorrowType("Deposit");
				
				//自己一定是圈子成员，所以不用更新借贷账户
//				Friend friend = new Select().from(Friend.class).where("friendUserId = ?", apportion.getFriendUserId()).executeSingle();
//				MoneyAccount debtAccount = MoneyAccount.getDebtAccount(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportion.getLocalFriendId(), apportion.getFriendUserId());
//			    
//				if(debtAccount == null){
//                	MoneyAccount.createDebtAccount(apportion.getLocalFriendId(), apportion.getFriendUserId(), mMoneyDepositIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportion.getAmount0()*apportion.getExchangeRate());
//              } else {
//                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportion.getAmount0()*apportion.getExchangeRate());
//                	debtAccountEditor.save();
//              }
				
				moneyBorrow.setMoneyDepositIncomeApportionId(apportion.getId());
				moneyBorrow.setAmount(apportion.getAmount0());
				moneyBorrow.setFriendUserId(apportion.getFriendUserId());
				moneyBorrow.setLocalFriendId(apportion.getLocalFriendId());
				moneyBorrow.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
				moneyBorrow.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
				moneyBorrow.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
				moneyBorrow.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
				moneyBorrow.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());

				if(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
					MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId());
					moneyBorrow.setMoneyAccountId(mMoneyDepositIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
				} else {
					moneyBorrow.setMoneyAccountId(null, null);
				}
				moneyBorrow.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
				moneyBorrow.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
				moneyBorrow.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
				moneyBorrow.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
				moneyBorrow.save();
				
				if(moneyBorrow.getLocalFriendId() != null){
					MoneyLend moneyLend = null;
					if(apportion.get_mId() == null){
						moneyLend = new MoneyLend();
//						moneyLend.setLendType("Deposit");
						moneyLend.setMoneyDepositIncomeApportionId(apportion.getId());
					} else {
						moneyLend = new Select().from(MoneyLend.class).where("moneyBorrowId=? AND ownerFriendId=?", moneyBorrow.getId(), moneyBorrow.getLocalFriendId()).executeSingle();
					}
//					moneyLend.setMoneyBorrowId(moneyBorrow.getId());
					moneyLend.setAmount(moneyBorrow.getAmount0());
					moneyLend.setDate(mMoneyDepositIncomeContainerEditor.getModelCopy().getDate());
					moneyLend.setRemark(mMoneyDepositIncomeContainerEditor.getModelCopy().getRemark());
					moneyLend.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
					moneyLend.setLocalFriendId(null);
					moneyLend.setExchangeRate(mMoneyDepositIncomeContainerEditor.getModelCopy().getExchangeRate());
					moneyLend.setGeoLat(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLat());
					moneyLend.setGeoLon(mMoneyDepositIncomeContainerEditor.getModelCopy().getGeoLon());
					moneyLend.setMoneyAccountId(null, moneyBorrow.getCurrencyId1());
					moneyLend.setLocation(mMoneyDepositIncomeContainerEditor.getModelCopy().getLocation());
					moneyLend.setAddress(mMoneyDepositIncomeContainerEditor.getModelCopy().getAddress());
					moneyLend.setPictureId(mMoneyDepositIncomeContainerEditor.getModelCopy().getPictureId());
					moneyLend.setProject(mMoneyDepositIncomeContainerEditor.getModelCopy().getProject());
					moneyLend.setOwnerFriendId(moneyBorrow.getLocalFriendId());
					moneyLend.setOwnerUserId("");
					moneyLend.save();
				}
				apportion.save();
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
					
					if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
						HyjUtil.displayToast(R.string.app_permission_no_addnew);
						return;
					}else if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
						HyjUtil.displayToast(R.string.app_permission_no_edit);
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
//	         		mApportionFieldApportions.changeProject(project, MoneyDepositIncomeApportion.class);
//					mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
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
					
					mApportionFieldApportions.removeAll();
				}
				break;
			case GET_EVENT_ID:
				if (resultCode == Activity.RESULT_OK) {
					long _id = data.getLongExtra("MODEL_ID", -1);
					if(_id == -1){
						Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
						mSelectorFieldEvent.setText(null);
						mSelectorFieldEvent.setModelId(null);
						mApportionFieldApportions.changeProject(project, mSelectorFieldEvent.getModelId(), MoneyDepositIncomeApportion.class);
						mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
					} else {
						Event event = Event.load(Event.class, _id);
						ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", event.getProject().getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
						
						if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
							HyjUtil.displayToast(R.string.app_permission_no_addnew);
							return;
						}else if(mMoneyDepositIncomeContainerEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
							HyjUtil.displayToast(R.string.app_permission_no_edit);
							return;
						}
						
//						mApportionFieldApportions.changeEvent(event.getProject(), event, MoneyDepositIncomeApportion.class);
//						mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
		
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
					mApportionFieldApportions.removeAll();
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
            	 
             case GET_APPORTION_MEMBER_ID:
            	 if (resultCode == Activity.RESULT_OK) {
     				String type = data.getStringExtra("MODEL_TYPE");
     				long _id = data.getLongExtra("MODEL_ID", -1);
     				if(_id != -1){
     					AddApportionMember(type, _id);
     				} else {
     					long[] _ids = data.getLongArrayExtra("MODEL_IDS");
     					if(_ids != null){
     						for(int i=0; i<_ids.length; i++){
     							AddApportionMember(type, _ids[i]);
     						}
     					}
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
	 
		
		
		

		private void AddApportionMember(String type, long _id) {
				MoneyDepositIncomeApportion apportion = new MoneyDepositIncomeApportion();
				ProjectShareAuthorization psa;
				if("ProjectShareAuthorization".equalsIgnoreCase(type)){
					psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
					if(psa.getState().equalsIgnoreCase("Delete")){
						HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
						return;
					} if(psa.getToBeDetermined()){
						HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_getToBeDetermined);
						return;
					}
				}  else if("EventMember".equalsIgnoreCase(type)){
					EventMember em = EventMember.load(EventMember.class, _id);
//					if(em.getFriendUserId() != null){
//						psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", em.getFriendUserId(), mSelectorFieldProject.getModelId()).executeSingle();
//					} else {
//						psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", em.getLocalFriendId(), mSelectorFieldProject.getModelId()).executeSingle();
//					}
					psa = em.getProjectShareAuthorization();
//					psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
					if(em.getToBeDetermined()){
						HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_getToBeDetermined);
						return;
					} else {
						if(psa == null){
							HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
							return;
						}
					}
				} else {
					Friend friend = Friend.load(Friend.class, _id);
					if(friend.getFriendUserId() != null){
						//看一下该好友是不是圈子成员, 如果是，作为圈子成员添加
						psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", friend.getFriendUserId(), mSelectorFieldProject.getModelId()).executeSingle();
					} else {
						psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", friend.getId(), mSelectorFieldProject.getModelId()).executeSingle();
					}
					if(psa == null){
						HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
						return;
					}
				}
				if(psa.getFriendUserId() != null){
					apportion.setFriendUserId(psa.getFriendUserId());
				} else {
					apportion.setLocalFriendId(psa.getLocalFriendId());
				}
				apportion.setAmount(0.0);
				if(psa.getSharePercentageType() != null && psa.getSharePercentageType().equals("Average")){
					apportion.setApportionType("Average");
				} else {
					apportion.setApportionType("Share");
				}
				apportion.setMoneyDepositIncomeContainerId(mMoneyDepositIncomeContainerEditor.getModel().getId());
				if (mApportionFieldApportions.addApportion(apportion,mSelectorFieldProject.getModelId(),mSelectorFieldEvent.getModelId(), ApportionItem.NEW)) {
					mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
				} else {
					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_already_exists);
				}
		
	}

		@Override
		public void onDestroy(){
			super.onDestroy();
			mApportionFieldApportions.getAdapter().unregisterDataSetObserver(mApportionCountObserver);
		}
	}
