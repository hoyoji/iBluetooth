package com.hoyoji.hoyoji.money;

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
import com.hoyoji.android.hyjframework.fragment.HyjTextInputFormFragment;
import com.hoyoji.android.hyjframework.fragment.HyjUserFormFragment;
import com.hoyoji.android.hyjframework.view.HyjDateTimeField;
import com.hoyoji.android.hyjframework.view.HyjImageField;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.android.hyjframework.view.HyjImageField.PictureItem;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeCategory;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.money.moneycategory.MoneyIncomeCategoryListFragment;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.friend.FriendListFragment;


public class MoneyIncomeFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_FRIEND_ID = 3;
	private final static int GET_CATEGORY_ID = 5;
	private static final int GET_REMARK = 6;
	protected static final int GET_FINANCIALOWNER_ID = 4;
	
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private HyjModelEditor<MoneyIncome> mMoneyIncomeEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericAmount = null;
	private HyjSelectorField mSelectorFieldMoneyAccount = null;
	private HyjSelectorField mSelectorFieldProject = null;
	private HyjNumericField mNumericExchangeRate = null;
	private HyjSelectorField mSelectorFieldMoneyIncomeCategory = null;
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
	
	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneyincome;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		final MoneyIncome moneyIncome;
		
		Intent intent = getActivity().getIntent();
		final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			moneyIncome =  new Select().from(MoneyIncome.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyIncome.hasEditPermission();
		} else {
			moneyIncome = new MoneyIncome();
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyIncome.get_mId() == null && moneyAccountId != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, moneyAccountId);
				moneyIncome.setMoneyAccount(moneyAccount);
			}
			if(intent.getStringExtra("counterpartId") != null){
				moneyIncome.setIsImported(true);
			}
		}
		
		mMoneyIncomeEditor = new HyjModelEditor(moneyIncome);
		
//		setupDeleteButton(mMoneyIncomeEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyIncomeFormFragment_imageField_picture);
		mImageFieldPicture.setImages(moneyIncome.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyIncomeFormFragment_textField_date);	
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyIncome.getDate());
		}
		
		Project project;
		String projectId = intent.getStringExtra("projectId");//从消息导入
		if(moneyIncome.get_mId() == null && projectId != null){
			project = HyjModel.getModel(Project.class, projectId);
			moneyIncome.setProject(project);
		}else{
			project = moneyIncome.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
		} else {
			mSelectorFieldProject.setText("共享来的收支");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyIncomeFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.projectListFragment_title_select_project, null, GET_PROJECT_ID);
			}
		});	
		
		
		mNumericAmount = (HyjNumericField) getView().findViewById(R.id.moneyIncomeFormFragment_textField_amount);
		int incomeColor = Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor());
		mNumericAmount.getEditText().setTextColor(incomeColor);
		mNumericAmount.getEditText().setHintTextColor(incomeColor);
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
			mNumericAmount.setNumber(moneyIncome.getAmount());
		}
		
		
		MoneyAccount moneyAccount = moneyIncome.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeFormFragment_selectorField_moneyAccount);

		if(moneyAccount != null){
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyIncomeFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_MONEYACCOUNT_ID);
			}
		});	
		
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyIncomeFormFragment_textField_exchangeRate);		
		mNumericExchangeRate.setNumber(moneyIncome.getExchangeRate());
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyIncomeFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyIncomeFormFragment_linearLayout_exchangeRate);
		
		mSelectorFieldMoneyIncomeCategory = (HyjSelectorField) getView().findViewById(
				R.id.moneyIncomeFormFragment_textField_moneyIncomeCategory);
		mSelectorFieldMoneyIncomeCategory.setText(moneyIncome
				.getMoneyIncomeCategory());
		if(moneyIncome.getMoneyIncomeCategoryMain() != null && moneyIncome.getMoneyIncomeCategoryMain().length() > 0){
			mSelectorFieldMoneyIncomeCategory.setLabel(moneyIncome.getMoneyIncomeCategoryMain());
		}
		mSelectorFieldMoneyIncomeCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyIncomeFormFragment.this
						.openActivityWithFragmentForResult(
								MoneyIncomeCategoryListFragment.class,
								R.string.moneyIncomeFormFragment_editText_hint_moneyIncomeCategory,
								null, GET_CATEGORY_ID);
			}
		});
		
		String friendUserId, localFriendId;
		if(moneyIncome.get_mId() == null){
			 friendUserId = intent.getStringExtra("friendUserId");//从消息导入
			 localFriendId = intent.getStringExtra("localFriendId");//从消息导入
		} else {
			friendUserId = moneyIncome.getFriendUserId();
			localFriendId = moneyIncome.getLocalFriendId();
		}
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeFormFragment_selectorField_friend);

		mSelectorFieldFriend.setText(Friend.getFriendUserDisplayName(localFriendId, friendUserId, projectId));
		if(friendUserId != null){
			Friend friend =  new Select().from(Friend.class).where("friendUserId=?", friendUserId).executeSingle();
			if(friend != null) {
				mSelectorFieldFriend.setModelId(friend.getId());
			}
		} else {
			mSelectorFieldFriend.setModelId(localFriendId);
		}
		mSelectorFieldFriend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("NULL_ITEM", (String) mSelectorFieldFriend.getHint());
				MoneyIncomeFormFragment.this
				.openActivityWithFragmentForResult(FriendListFragment.class, R.string.friendListFragment_title_select_friend_payer, null, GET_FRIEND_ID);
			}
		}); 
		
//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyIncomeFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});
		
		
		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyIncomeFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyIncome.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyIncomeFormFragment.this
						.openActivityWithFragmentForResult(
								HyjTextInputFormFragment.class,
								R.string.moneyExpenseFormFragment_textView_remark,
								bundle, GET_REMARK);
			}
		});

		mSelectorFieldFinancialOwner = (HyjSelectorField) getView().findViewById(R.id.projectFormFragment_selectorField_financialOwner);
		mSelectorFieldFinancialOwner.setEnabled(hasEditPermission);
		if(modelId == -1){
			if(project != null && project.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
			}
		} else if(moneyIncome.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(moneyIncome.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyIncome.getFinancialOwnerUserId()));
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
				bundle.putString("FINANCIAL_TYPE", "MoneyIncome");
				
				MoneyIncomeFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyIncomeFormFragment_imageView_camera);	
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
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyIncomeFormFragment_imageButton_refresh_exchangeRate);	
		mImageViewRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
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
						HyjUtil.displayToast(R.string.moneyIncomeFormFragment_toast_select_currency);
					}
				}else{
					HyjUtil.displayToast(R.string.moneyIncomeFormFragment_toast_select_currency);
				}
			}
		});

		mLinearLayoutExpandMore = (LinearLayout)getView().findViewById(R.id.moneyIncomeFormFragment_expandMore);
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
			
			// 只在新增时才自动打开软键盘， 修改时不自动打开
			if (modelId == -1) {
				setExchangeRate(false);
				this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			}else{
				setExchangeRate(true);
			}
			setPermission();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    if(mMoneyIncomeEditor!= null && mMoneyIncomeEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
	
	
//	private void setupDeleteButton(HyjModelEditor<MoneyIncome> moneyIncomeEditor) {
//
//		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
//		
//		final MoneyIncome moneyIncome = moneyIncomeEditor.getModelCopy();
//		
//		if (moneyIncome.get_mId() == null) {
//			buttonDelete.setVisibility(View.GONE);
//		} else {
//			buttonDelete.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(moneyIncome.hasDeletePermission()){
//					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
//							new DialogCallbackListener() {
//								@Override
//								public void doPositiveClick(Object object) {
//									try {
//										ActiveAndroid.beginTransaction();
//
//										MoneyAccount moneyAccount = moneyIncome.getMoneyAccount();
//										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
//										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() - moneyIncome.getAmount());
//										MoneyIncomeEditor moneyIncomeEditor = new MoneyIncomeEditor(moneyIncome);
//										
//										//更新圈子余额
//										Project newProject = moneyIncome.getProject();
//										HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
//										newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - moneyIncome.getAmount0()*moneyIncome.getExchangeRate());
//										newProjectEditor.save();
//										
//										//删除收入的同时删除分摊
//										Iterator<MoneyIncomeApportion> moneyIncomeApportions = moneyIncome.getApportions().iterator();
//										while (moneyIncomeApportions.hasNext()) {
//											MoneyIncomeApportion moneyIncomeAportion = moneyIncomeApportions.next();
//											ProjectShareAuthorization oldProjectShareAuthorization;
//											
//											// 非圈子好友不用更新圈子分摊
//											if(moneyIncomeAportion.getFriendUserId() == null){
//												MoneyBorrow moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//												if(moneyBorrow != null){
//													moneyBorrow.delete();
//												} 
//												// 更新旧圈子分摊支出
//												oldProjectShareAuthorization = moneyIncomeEditor.getOldSelfProjectShareAuthorization();
//												HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//												oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//												oldProjectShareAuthorizationEditor.save();
//												
//												MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//												if(moneyIncome != null){
//													moneyIncome.delete();
//												}
//												
//												MoneyLend moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//												if(moneyLend != null){
//													moneyLend.delete();
//												} 
//											} else {
//												if(moneyIncomeAportion.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//													// 更新旧圈子的分摊支出
//													oldProjectShareAuthorization = moneyIncomeEditor.getOldSelfProjectShareAuthorization();
//													HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//													oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.save();
//													
//													MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//													if(moneyIncome != null){
//														moneyIncome.delete();
//													}
//												} else {
//													// 更新旧圈子分摊收入// 更新旧圈子分摊支出
//													oldProjectShareAuthorization = moneyIncomeAportion.getProjectShareAuthorization();
//													HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//													
//													oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//													MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//													if(moneyIncome != null){
//														moneyIncome.delete();
//													} 
//												
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalLend() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//													MoneyLend moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//													if(moneyLend != null){
//														moneyLend.delete();
//													} 
//													oldProjectShareAuthorizationEditor.save();
//													
//													oldProjectShareAuthorization = moneyIncomeEditor.getOldSelfProjectShareAuthorization();
//													oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (moneyIncomeAportion.getAmount0() * moneyIncomeAportion.getMoneyIncome().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.save();
//													MoneyBorrow moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=?", moneyIncomeAportion.getId()).executeSingle();
//													if(moneyBorrow != null){
//														moneyBorrow.delete();
//													} 
//												}
//											}
//											
//											moneyIncomeAportion.delete();
//										}
//										/*
//										//更新支出所有者的实际支出
//										MoneyIncome oldMoneyIncomeModel = moneyIncomeEditor.getModelCopy();
//										ProjectShareAuthorization oldSelfProjectAuthorization = moneyIncomeEditor.getOldSelfProjectShareAuthorization();
//										HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
//										oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(oldSelfProjectAuthorization.getActualTotalIncome() - oldMoneyIncomeModel.getAmount0()*oldMoneyIncomeModel.getExchangeRate());
//										oldSelfProjectAuthorizationEditor.save();
//										*/
//										moneyIncome.delete();
//										moneyAccountEditor.save();
//
//										HyjUtil.displayToast(R.string.app_delete_success);
//										ActiveAndroid.setTransactionSuccessful();
//										ActiveAndroid.endTransaction();
//										getActivity().finish();
//									} catch (Exception e) {
//										ActiveAndroid.endTransaction();
//										HyjUtil.displayToast(R.string.app_delete_failed);
//									} 
//								}
//							});
//					}else{
//						HyjUtil.displayToast(R.string.app_permission_no_delete);
//					}
//				}
//			});
//		}
//		
//	}
	
	private void setPermission(){

		if(mMoneyIncomeEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
//			mNumericAmount.setNumber(mMoneyIncomeEditor.getModel().getProjectAmount());
			mNumericAmount.setEnabled(false);
			
			mSelectorFieldMoneyIncomeCategory.setEnabled(false);

			mSelectorFieldFriend.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyIncomeFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

			mSelectorFieldProject.setEnabled(false);
			
			mNumericExchangeRate.setEnabled(false);

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
		MoneyIncome modelCopy =  mMoneyIncomeEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericAmount.getNumber());
		MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mSelectorFieldMoneyAccount.getModelId());
		modelCopy.setMoneyAccount(moneyAccount);
		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
        if(mSelectorFieldProject.getModelId() != null) {
            modelCopy.setProject(HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId()));
        }
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());
		modelCopy.setMoneyIncomeCategory(mSelectorFieldMoneyIncomeCategory.getText());
		modelCopy.setMoneyIncomeCategoryMain(mSelectorFieldMoneyIncomeCategory.getLabel());
		
		if(mSelectorFieldFriend.getModelId() != null){
			Friend friend = HyjModel.getModel(Friend.class, mSelectorFieldFriend.getModelId());
			modelCopy.setFriend(friend);
		}else{
			modelCopy.setFriend(null);
		}
		
		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
		
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyIncomeEditor.getValidationError("datetime"));
		mNumericAmount.setError(mMoneyIncomeEditor.getValidationError("amount"));
		if(mMoneyIncomeEditor.getValidationError("amount") != null){
					mNumericAmount.showSoftKeyboard();
				}
		mSelectorFieldMoneyAccount.setError(mMoneyIncomeEditor.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyIncomeEditor.getValidationError("project"));
		mNumericExchangeRate.setError(mMoneyIncomeEditor.getValidationError("exchangeRate"));
		mSelectorFieldMoneyIncomeCategory.setError(mMoneyIncomeEditor.getValidationError("moneyIncomeCategory"));
		mSelectorFieldFriend.setError(mMoneyIncomeEditor.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyIncomeEditor.getValidationError("remark"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
		if(mMoneyIncomeEditor.getModelCopy().get_mId() == null && !mMoneyIncomeEditor.getModelCopy().hasAddNewPermission(mMoneyIncomeEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyIncomeEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else{
		
		mMoneyIncomeEditor.validate();
		
		
		if(mMoneyIncomeEditor.hasValidationErrors()){
			showValidatioErrors();
		} else {
			try {
				ActiveAndroid.beginTransaction();
				
				savePictures();
				
				MoneyIncome oldMoneyIncomeModel = mMoneyIncomeEditor.getModel();
				MoneyIncome moneyIncomeModel = mMoneyIncomeEditor.getModelCopy();
				
				//设置默认圈子和账户
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				if(moneyIncomeModel.get_mId() == null && !userData.getActiveMoneyAccountId().equals(moneyIncomeModel.getMoneyAccountId())){
					HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
					userDataEditor.getModelCopy().setActiveMoneyAccountId(moneyIncomeModel.getMoneyAccountId());
					userDataEditor.getModelCopy().setActiveProjectId(moneyIncomeModel.getProjectId());
					userDataEditor.save();
				}
				
				//设置默认活动
				Project project = moneyIncomeModel.getProject();
				if(moneyIncomeModel.get_mId() == null){
					if((moneyIncomeModel.getEventId() != null && !moneyIncomeModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(moneyIncomeModel.getEventId()))){
						HyjModelEditor<Project> projectEditor = project.newModelEditor();
						projectEditor.getModelCopy().setActiveEventId(moneyIncomeModel.getEventId());
						projectEditor.save();
					}
				}
				
				// 更新圈子的默认分类
				if(moneyIncomeModel.get_mId() == null){
					HyjModelEditor<Project> projectEditor = moneyIncomeModel.getProject().newModelEditor();
					projectEditor.getModelCopy().setDefaultIncomeCategory(moneyIncomeModel.getMoneyIncomeCategory());
					projectEditor.getModelCopy().setDefaultIncomeCategoryMain(moneyIncomeModel.getMoneyIncomeCategoryMain());
					projectEditor.save();
				}
				
				//当前汇率不存在时，创建汇率
				String localCurrencyId = moneyIncomeModel.getMoneyAccount().getCurrencyId();
				String foreignCurrencyId = moneyIncomeModel.getProject().getCurrencyId();
				if(CREATE_EXCHANGE == 1){
					MoneyAccount moneyAccount = moneyIncomeModel.getMoneyAccount();
//					Project project = moneyIncomeModel.getProject();
					
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(moneyAccount.getCurrencyId());
					newExchange.setForeignCurrencyId(project.getCurrencyId());
					newExchange.setRate(moneyIncomeModel.getExchangeRate());
					newExchange.save();
				}else {
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = null;
						Double exRate = null;
						Double rate = HyjUtil.toFixed2(moneyIncomeModel.getExchangeRate());
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
				
				    MoneyAccount oldMoneyAccount = oldMoneyIncomeModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = moneyIncomeModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					//更新账户余额
					if(moneyIncomeModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - oldMoneyIncomeModel.getAmount0() + moneyIncomeModel.getAmount0());
					}else{
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() - oldMoneyIncomeModel.getAmount0());
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + moneyIncomeModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();

					Project oldProject = oldMoneyIncomeModel.getProject();
					Project newProject = moneyIncomeModel.getProject();
					HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
					
					//更新圈子余额
					if(moneyIncomeModel.get_mId() == null || oldProject.getId().equals(newProject.getId())){
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - oldMoneyIncomeModel.getAmount0()*oldMoneyIncomeModel.getExchangeRate() + moneyIncomeModel.getAmount0()*moneyIncomeModel.getExchangeRate());
					} else {
						HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
						oldProjectEditor.getModelCopy().setIncomeTotal(oldProject.getIncomeTotal() - oldMoneyIncomeModel.getAmount0()*oldMoneyIncomeModel.getExchangeRate());
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() + moneyIncomeModel.getAmount0()*moneyIncomeModel.getExchangeRate());
						oldProjectEditor.save();
					}
					newProjectEditor.save();
					
					/*
					//更新支出所有者的实际收入
					ProjectShareAuthorization selfProjectAuthorization = mMoneyIncomeEditor.getNewSelfProjectShareAuthorization();
					HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
				    
					if(moneyIncomeModel.get_mId() == null || oldMoneyIncomeModel.getProjectId().equals(moneyIncomeModel.getProjectId())){
				    	selfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(selfProjectAuthorization.getActualTotalIncome() - oldMoneyIncomeModel.getAmount0()*oldMoneyIncomeModel.getExchangeRate() + moneyIncomeModel.getAmount0()*moneyIncomeModel.getExchangeRate());
					}else{
						selfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(selfProjectAuthorization.getActualTotalIncome() + moneyIncomeModel.getAmount0()*moneyIncomeModel.getExchangeRate());
						
						ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyIncomeEditor.getOldSelfProjectShareAuthorization();
						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
						oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(oldSelfProjectAuthorization.getActualTotalIncome() - oldMoneyIncomeModel.getAmount0()*oldMoneyIncomeModel.getExchangeRate());
						oldSelfProjectAuthorizationEditor.save();
					}
					selfProjectAuthorizationEditor.save();
					 */
					
					//更新分类，使之成为最近使用过的
					if(this.mSelectorFieldMoneyIncomeCategory.getModelId() != null){
						MoneyIncomeCategory category = HyjModel.getModel(MoneyIncomeCategory.class, this.mSelectorFieldMoneyIncomeCategory.getModelId());
						if(category != null){
							category.newModelEditor().save();
						}
					}
					
				mMoneyIncomeEditor.save();
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
					newPic.setRecordId(mMoneyIncomeEditor.getModel().getId());
					newPic.setRecordType("MoneyIncome");
					newPic.setProjectId(mMoneyIncomeEditor.getModelCopy().getProjectId());
					newPic.setDisplayOrder(i);
					newPic.save();
				} else if(pi.getState() == PictureItem.DELETED){
					pi.getPicture().delete();
				} else if(pi.getState() == PictureItem.CHANGED){

				}
				if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
					mainPicSet = true;
					mMoneyIncomeEditor.getModelCopy().setPicture(pi.getPicture());
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
					
					if(mMoneyIncomeEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
						HyjUtil.displayToast(R.string.app_permission_no_addnew);
						return;
					}else if(mMoneyIncomeEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
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
	         	 }
	        	 break;
	        	 
             case GET_FRIEND_ID:
            	 if(resultCode == Activity.RESULT_OK){
            		long _id = data.getLongExtra("MODEL_ID", -1);
            		if(_id == -1){
     	   	       		mSelectorFieldFriend.setText(null);
     	   	       		mSelectorFieldFriend.setModelId(null);
    				} else {
	            		Friend friend = Friend.load(Friend.class, _id);
	            		
	            		if(friend.getFriendUserId() != null && friend.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
	    					HyjUtil.displayToast(R.string.moneyIncomeFormFragment_editText_error_friend);
	    					return;
	    				}
	            		
	            		mSelectorFieldFriend.setText(friend.getDisplayName());
	            		mSelectorFieldFriend.setModelId(friend.getId());
    				}
            	 }
            	 break;

     		case GET_REMARK:
     			if (resultCode == Activity.RESULT_OK) {
     				String text = data.getStringExtra("TEXT");
     				mRemarkFieldRemark.setText(text);
     			}
     			break;
             case GET_CATEGORY_ID:
     			if (resultCode == Activity.RESULT_OK) {
     				long _id = data.getLongExtra("MODEL_ID", -1);
     				MoneyIncomeCategory category = MoneyIncomeCategory.load(MoneyIncomeCategory.class, _id);
     				mSelectorFieldMoneyIncomeCategory.setText(category.getName());
     				mSelectorFieldMoneyIncomeCategory.setModelId(category.getId());
     				if(category.getParentIncomeCategory() != null){
     					mSelectorFieldMoneyIncomeCategory.setLabel(category.getParentIncomeCategory().getName());
     				} else {
     					mSelectorFieldMoneyIncomeCategory.setLabel(null);
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
