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
import com.hoyoji.android.hyjframework.view.HyjImageField.PictureItem;
import com.hoyoji.android.hyjframework.view.HyjNumericField;
import com.hoyoji.android.hyjframework.view.HyjRemarkField;
import com.hoyoji.android.hyjframework.view.HyjSelectorField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyExpense;
import com.hoyoji.hoyoji.models.MoneyExpenseCategory;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.money.moneycategory.MoneyExpenseCategoryListFragment;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.friend.FriendListFragment;

public class MoneyExpenseFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_FRIEND_ID = 3;
	private final static int GET_CATEGORY_ID = 5;
	private final static int GET_REMARK = 6;
	protected static final int GET_FINANCIALOWNER_ID = 0;
	
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;

	private HyjModelEditor<MoneyExpense> mMoneyExpenseEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericAmount = null;
	private HyjSelectorField mSelectorFieldMoneyAccount = null;
	private HyjSelectorField mSelectorFieldProject = null;
	private HyjNumericField mNumericExchangeRate = null;
	private HyjSelectorField mSelectorFieldMoneyExpenseCategory = null;
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
		return R.layout.money_formfragment_moneyexpense;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		final MoneyExpense moneyExpense;

		Intent intent = getActivity().getIntent();
		final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if (modelId != -1) {
			moneyExpense = new Select().from(MoneyExpense.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyExpense.hasEditPermission();
		} else {
			moneyExpense = new MoneyExpense();
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyAccountId != null){
				MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, moneyAccountId);
				moneyExpense.setMoneyAccount(moneyAccount);
			}
			if(intent.getStringExtra("counterpartId") != null){
				moneyExpense.setIsImported(true);
			}
		}
				
		mMoneyExpenseEditor = moneyExpense.newModelEditor();

//		setupDeleteButton(mMoneyExpenseEditor);

		mImageFieldPicture = (HyjImageField) getView().findViewById(
				R.id.moneyExpenseFormFragment_imageField_picture);
		mImageFieldPicture.setImages(moneyExpense.getPictures());
				
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyExpenseFormFragment_textField_date);
		if (modelId != -1) {
			mDateTimeFieldDate.setTime(moneyExpense.getDate());
		}
		Project project;
		String projectId = intent.getStringExtra("projectId");//从消息导入
		if(moneyExpense.get_mId() == null && projectId != null){
			project = HyjModel.getModel(Project.class, projectId);
			moneyExpense.setProject(project);
		}else{
			project = moneyExpense.getProject();
		}
		
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(
				R.id.moneyExpenseFormFragment_selectorField_project);

		if (project != null) {
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "("
					+ project.getCurrencyId() + ")");
		}else {
			mSelectorFieldProject.setText("共享来的收支");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyExpenseFormFragment.this
						.openActivityWithFragmentForResult(
								ProjectListFragment.class,
								R.string.projectListFragment_title_select_project,
								null, GET_PROJECT_ID);
			}
		});
		
		mNumericAmount = (HyjNumericField) getView().findViewById(R.id.moneyExpenseFormFragment_textField_amount);
		mNumericAmount.getEditText().setTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
		mNumericAmount.getEditText().setHintTextColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
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
			mNumericAmount.setNumber(moneyExpense.getAmount());
		}
		
		MoneyAccount moneyAccount = moneyExpense.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(
				R.id.moneyExpenseFormFragment_selectorField_moneyAccount);

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
				
				MoneyExpenseFormFragment.this
						.openActivityWithFragmentForResult(
								MoneyAccountListFragment.class,
								R.string.moneyAccountListFragment_title_select_moneyAccount,
								bundle, GET_MONEYACCOUNT_ID);
			}
		});


		mNumericExchangeRate = (HyjNumericField) getView().findViewById(
				R.id.moneyExpenseFormFragment_textField_exchangeRate);
		mNumericExchangeRate.setNumber(moneyExpense.getExchangeRate());

		mViewSeparatorExchange = (View) getView().findViewById(
				R.id.moneyExpenseFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(
				R.id.moneyExpenseFormFragment_linearLayout_exchangeRate);

		mSelectorFieldMoneyExpenseCategory = (HyjSelectorField) getView().findViewById(
				R.id.moneyExpenseFormFragment_textField_moneyExpenseCategory);
		mSelectorFieldMoneyExpenseCategory.setText(moneyExpense
				.getMoneyExpenseCategory());
		if(moneyExpense.getMoneyExpenseCategoryMain() != null && moneyExpense.getMoneyExpenseCategoryMain().length() > 0){
			mSelectorFieldMoneyExpenseCategory.setLabel(moneyExpense.getMoneyExpenseCategoryMain());
		}
		mSelectorFieldMoneyExpenseCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyExpenseFormFragment.this
						.openActivityWithFragmentForResult(
								MoneyExpenseCategoryListFragment.class,
								R.string.moneyExpenseFormFragment_editText_hint_moneyExpenseCategory,
								null, GET_CATEGORY_ID);
			}
		});

		String friendUserId, localFriendId;
		if(moneyExpense.get_mId() == null){
			friendUserId = intent.getStringExtra("friendUserId");//从消息导入
			localFriendId = intent.getStringExtra("localFriendId");//从消息导入
		}else{
			friendUserId = moneyExpense.getFriendUserId();
			localFriendId = moneyExpense.getLocalFriendId();
		}
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyExpenseFormFragment_selectorField_friend);

		mSelectorFieldFriend.setText(Friend.getFriendUserDisplayName(localFriendId, friendUserId, projectId));
		if (friendUserId != null) {
			Friend friend =  new Select().from(Friend.class).where("friendUserId=?", friendUserId).executeSingle();
			if(friend != null) {
				mSelectorFieldFriend.setModelId(friend.getId());
			}
		} else {
			mSelectorFieldFriend.setModelId(localFriendId);
		}
		mSelectorFieldFriend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("NULL_ITEM", (String) mSelectorFieldFriend.getHint());
				MoneyExpenseFormFragment.this
						.openActivityWithFragmentForResult(
								FriendListFragment.class,
								R.string.friendListFragment_title_select_friend_payee,
								null, GET_FRIEND_ID);
			}
		});
		
//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyExpenseFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});
		
		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(
				R.id.moneyExpenseFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyExpense.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyExpenseFormFragment.this
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
		} else if(moneyExpense.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(moneyExpense.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyExpense.getFinancialOwnerUserId()));
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
				bundle.putString("FINANCIAL_TYPE", "MoneyExpense");
				
				MoneyExpenseFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyExpenseFormFragment_imageView_camera);
		takePictureButton.setBackgroundColor(Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getExpenseColor()));
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
				
				if(!hasEditPermission){
					for(int i = 0; i<popup.getMenu().size();i++){
						popup.getMenu().setGroupEnabled(i, false);
					}
				}
				
				popup.show();
			}
		});

		mImageViewRefreshRate = (ImageView) getView().findViewById(
				R.id.moneyExpenseFormFragment_imageButton_refresh_exchangeRate);
		mImageViewRefreshRate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectorFieldMoneyAccount.getModelId() != null && mSelectorFieldProject.getModelId() != null) {
					MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mSelectorFieldMoneyAccount.getModelId());
					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());

					String fromCurrency = moneyAccount.getCurrencyId();
					String toCurrency = project.getCurrencyId();
					if (fromCurrency != null && toCurrency != null) {
						HyjUtil.startRoateView(mImageViewRefreshRate);
						mImageViewRefreshRate.setEnabled(false);
						HyjUtil.updateExchangeRate(fromCurrency, toCurrency, mImageViewRefreshRate, mNumericExchangeRate);
					} else {
						HyjUtil.displayToast(R.string.moneyExpenseFormFragment_toast_select_currency);
					}
				} else {
					HyjUtil.displayToast(R.string.moneyExpenseFormFragment_toast_select_currency);
				}
			}
		});

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
	    if(mMoneyExpenseEditor!= null && mMoneyExpenseEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
//	
//	private void setupDeleteButton(MoneyExpenseEditor moneyExpenseEditor) {
//
//		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
//		
//		final MoneyExpense moneyExpense = moneyExpenseEditor.getModelCopy();
//		
//		if (moneyExpense.get_mId() == null) {
//			buttonDelete.setVisibility(View.GONE);
//		} else {
//			buttonDelete.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(moneyExpense.hasDeletePermission()){
//					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
//							new DialogCallbackListener() {
//								@Override
//								public void doPositiveClick(Object object) {
//									try {
//										ActiveAndroid.beginTransaction();
//
//										MoneyAccount moneyAccount = moneyExpense.getMoneyAccount();
//										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
//										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() + moneyExpense.getAmount());
//										MoneyExpenseEditor moneyExpenseEditor = new MoneyExpenseEditor(moneyExpense);
//										
//										//更新圈子余额
//										Project newProject = moneyExpense.getProject();
//										HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
//										newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() - moneyExpense.getAmount0()*moneyExpense.getExchangeRate());
//										newProjectEditor.save();
//										
//										//删除支出的同时删除分摊
//										Iterator<MoneyExpenseApportion> moneyExpenseApportions = moneyExpense.getApportions().iterator();
//										while (moneyExpenseApportions.hasNext()) {
//											MoneyExpenseApportion moneyExpenseAportion = moneyExpenseApportions.next();
//											ProjectShareAuthorization oldProjectShareAuthorization;
//
//											// 非圈子好友不用更新圈子分摊
//											if(moneyExpenseAportion.getFriendUserId() == null){
//												MoneyLend moneyLend = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//												if(moneyLend != null){
//													moneyLend.delete();
//													//这里不用维护账户余额，因为该笔借出包括在实际支出了
//												}
//												// 更新旧圈子分摊支出
//												oldProjectShareAuthorization = moneyExpenseEditor.getOldSelfProjectShareAuthorization();
//												HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//												oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalLend() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//												oldProjectShareAuthorizationEditor.save();
//												
//												MoneyExpense moneyExpense = new Select().from(MoneyExpense.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//												if(moneyExpense != null){
//													moneyExpense.delete();
//												}
//												MoneyBorrow moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//												if(moneyBorrow != null){
//													moneyBorrow.delete();
//												} 
//											} else {
//												if(moneyExpenseAportion.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//													// 更新旧圈子的分摊支出
//													oldProjectShareAuthorization = moneyExpenseEditor.getOldSelfProjectShareAuthorization();
//													HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//													oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalExpense(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalExpense() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalExpense(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalExpense() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.save();
//													
//													MoneyExpense moneyExpense = new Select().from(MoneyExpense.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//													if(moneyExpense != null){
//														moneyExpense.delete();
//													}
//												} else {
//													// 更新旧圈子分摊支出
//													oldProjectShareAuthorization = moneyExpenseAportion.getProjectShareAuthorization();
//													HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//													
//													oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalExpense(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalExpense() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalExpense(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalExpense() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//													MoneyExpense moneyExpense = new Select().from(MoneyExpense.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//													if(moneyExpense != null){
//														moneyExpense.delete();
//													} 
//												
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//													MoneyBorrow moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//													if(moneyBorrow != null){
//														moneyBorrow.delete();
//													} 
//													oldProjectShareAuthorizationEditor.save();
//													
//													oldProjectShareAuthorization = moneyExpenseEditor.getOldSelfProjectShareAuthorization();
//													oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//													oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalLend() - (moneyExpenseAportion.getAmount0() * moneyExpenseAportion.getMoneyExpense().getExchangeRate()));
//													oldProjectShareAuthorizationEditor.save();
//													MoneyLend moneyLend = new Select().from(MoneyLend.class).where("moneyExpenseApportionId=?", moneyExpenseAportion.getId()).executeSingle();
//													if(moneyLend != null){
//														moneyLend.delete();
//													} 
//												}
//											}
//																					
//											moneyExpenseAportion.delete();
//										}
//										/*
//										//更新支出所有者的实际支出
//										MoneyExpense oldMoneyExpenseModel = moneyExpenseEditor.getModelCopy();
//										ProjectShareAuthorization oldSelfProjectAuthorization = moneyExpenseEditor.getOldSelfProjectShareAuthorization();
//										HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
//										oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalExpense(oldSelfProjectAuthorization.getActualTotalExpense() - oldMoneyExpenseModel.getAmount0()*oldMoneyExpenseModel.getExchangeRate());
//										oldSelfProjectAuthorizationEditor.save();
//										*/
//										moneyExpense.delete();
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
//	
	private void setPermission(){

		if(mMoneyExpenseEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
//			mNumericAmount.setNumber(mMoneyExpenseEditor.getModel().getProjectAmount());
			mNumericAmount.setEnabled(false);
			
			mSelectorFieldMoneyExpenseCategory.setEnabled(false);

			mSelectorFieldFriend.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyExpenseFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

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

				if(!editInit){//修改时init不需要set Rate
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
		MoneyExpense modelCopy = (MoneyExpense) mMoneyExpenseEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericAmount.getNumber());
		MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mSelectorFieldMoneyAccount.getModelId());
		modelCopy.setMoneyAccount(moneyAccount);
		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());
        if(mSelectorFieldProject.getModelId() != null) {
            modelCopy.setProject(HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId()));
        }
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());
		modelCopy.setMoneyExpenseCategory(mSelectorFieldMoneyExpenseCategory.getText());
		modelCopy.setMoneyExpenseCategoryMain(mSelectorFieldMoneyExpenseCategory.getLabel());
		
		if(mSelectorFieldFriend.getModelId() != null){
			Friend friend = HyjModel.getModel(Friend.class, mSelectorFieldFriend.getModelId());
			modelCopy.setFriend(friend);
		}else{
			modelCopy.setFriend(null);
		}

		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
	}

	private void showValidatioErrors() {
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyExpenseEditor
				.getValidationError("datetime"));
		mNumericAmount.setError(mMoneyExpenseEditor
				.getValidationError("amount"));
		if(mMoneyExpenseEditor
		.getValidationError("amount") != null){
			mNumericAmount.showSoftKeyboard();
		}
		mSelectorFieldMoneyAccount.setError(mMoneyExpenseEditor
				.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyExpenseEditor
				.getValidationError("project"));
		mNumericExchangeRate.setError(mMoneyExpenseEditor
				.getValidationError("exchangeRate"));
		mSelectorFieldMoneyExpenseCategory.setError(mMoneyExpenseEditor
				.getValidationError("moneyExpenseCategory"));
		mSelectorFieldFriend.setError(mMoneyExpenseEditor
				.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyExpenseEditor
				.getValidationError("remark"));
	}

	@Override
	public void onSave(View v) {
		super.onSave(v);

		fillData();

		if(mMoneyExpenseEditor.getModelCopy().get_mId() == null 
				&& !mMoneyExpenseEditor.getModelCopy().hasAddNewPermission(mMoneyExpenseEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyExpenseEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else{
			mMoneyExpenseEditor.validate();

			if (mMoneyExpenseEditor.hasValidationErrors()) {
				showValidatioErrors();
			} else {
				try {
					ActiveAndroid.beginTransaction();

					savePictures();

					MoneyExpense oldMoneyExpenseModel = mMoneyExpenseEditor.getModel();
					MoneyExpense moneyExpenseModel = mMoneyExpenseEditor.getModelCopy();
					
					//设置默认圈子和账户
					UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
					if(moneyExpenseModel.get_mId() == null 
							&& !userData.getActiveMoneyAccountId().equals(moneyExpenseModel.getMoneyAccountId())){
						HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
						userDataEditor.getModelCopy().setActiveMoneyAccountId(moneyExpenseModel.getMoneyAccountId());
						userDataEditor.getModelCopy().setActiveProjectId(moneyExpenseModel.getProjectId());
						userDataEditor.save();
					}
					
					//设置默认活动
					Project project = moneyExpenseModel.getProject();
					if(moneyExpenseModel.get_mId() == null){
						if((moneyExpenseModel.getEventId() != null && !moneyExpenseModel.getEventId().equals(project.getActiveEventId())) 
								|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(moneyExpenseModel.getEventId()))){
							HyjModelEditor<Project> projectEditor = project.newModelEditor();
							projectEditor.getModelCopy().setActiveEventId(moneyExpenseModel.getEventId());
							projectEditor.save();
						}
					}
					
					// 更新圈子的默认分类
					if(moneyExpenseModel.get_mId() == null){
						HyjModelEditor<Project> projectEditor = moneyExpenseModel.getProject().newModelEditor();
						projectEditor.getModelCopy().setDefaultExpenseCategory(moneyExpenseModel.getMoneyExpenseCategory());
						projectEditor.getModelCopy().setDefaultExpenseCategoryMain(moneyExpenseModel.getMoneyExpenseCategoryMain());
						projectEditor.save();
					}
					
					//当前汇率不存在时，创建汇率
					String localCurrencyId = moneyExpenseModel.getMoneyAccount().getCurrencyId();
					String foreignCurrencyId = moneyExpenseModel.getProject().getCurrencyId();
					if(CREATE_EXCHANGE == 1){
						MoneyAccount moneyAccount = moneyExpenseModel.getMoneyAccount();
//						Project project = moneyExpenseModel.getProject();
						
						Exchange newExchange = new Exchange();
						newExchange.setLocalCurrencyId(moneyAccount.getCurrencyId());
						newExchange.setForeignCurrencyId(project.getCurrencyId());
						newExchange.setRate(moneyExpenseModel.getExchangeRate());
						newExchange.save();
					}else {
						if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
							Exchange exchange = null;
							Double exRate = null;
							Double rate = HyjUtil.toFixed2(moneyExpenseModel.getExchangeRate());
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
					
				    MoneyAccount oldMoneyAccount = oldMoneyExpenseModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = moneyExpenseModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					//更新账户余额
					if(moneyExpenseModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + oldMoneyExpenseModel.getAmount0() - moneyExpenseModel.getAmount0());
					} else {
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() + oldMoneyExpenseModel.getAmount0());
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - moneyExpenseModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();
				
				    Project oldProject = oldMoneyExpenseModel.getProject();
					Project newProject = moneyExpenseModel.getProject();
					HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
					
					//更新圈子余额
					if(moneyExpenseModel.get_mId() == null || oldProject.getId().equals(newProject.getId())){
						newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() - oldMoneyExpenseModel.getAmount0()*oldMoneyExpenseModel.getExchangeRate() + moneyExpenseModel.getAmount0()*moneyExpenseModel.getExchangeRate());
					} else {
						HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
						oldProjectEditor.getModelCopy().setExpenseTotal(oldProject.getExpenseTotal() - oldMoneyExpenseModel.getAmount0()*oldMoneyExpenseModel.getExchangeRate());
						newProjectEditor.getModelCopy().setExpenseTotal(newProject.getExpenseTotal() + moneyExpenseModel.getAmount0()*moneyExpenseModel.getExchangeRate());
						oldProjectEditor.save();
					}
					newProjectEditor.save();
					
					/*
					//更新支出所有者的实际支出
					ProjectShareAuthorization selfProjectAuthorization = mMoneyExpenseEditor.getNewSelfProjectShareAuthorization();
					HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
				    
					if(moneyExpenseModel.get_mId() == null || oldMoneyExpenseModel.getProjectId().equals(moneyExpenseModel.getProjectId())){
					    // 无旧圈子可更新
						selfProjectAuthorizationEditor.getModelCopy().setActualTotalExpense(selfProjectAuthorization.getActualTotalExpense() - oldMoneyExpenseModel.getAmount0()*oldMoneyExpenseModel.getExchangeRate() + moneyExpenseModel.getAmount0()*moneyExpenseModel.getExchangeRate());
						
					} else {
						selfProjectAuthorizationEditor.getModelCopy().setActualTotalExpense(selfProjectAuthorization.getActualTotalExpense() + moneyExpenseModel.getAmount0()*moneyExpenseModel.getExchangeRate());
							
						ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyExpenseEditor.getOldSelfProjectShareAuthorization();
						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
						oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalExpense(oldSelfProjectAuthorization.getActualTotalExpense() - oldMoneyExpenseModel.getAmount0()*oldMoneyExpenseModel.getExchangeRate());
						oldSelfProjectAuthorizationEditor.save();
					
					}
					selfProjectAuthorizationEditor.save();
					*/
					
					//更新分类，使之成为最近使用过的
					if(this.mSelectorFieldMoneyExpenseCategory.getModelId() != null){
						MoneyExpenseCategory category = HyjModel.getModel(MoneyExpenseCategory.class, this.mSelectorFieldMoneyExpenseCategory.getModelId());
						if(category != null){
							category.newModelEditor().save();
						}
					}
					mMoneyExpenseEditor.save();
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

	private void savePictures() {
		HyjImageField.ImageGridAdapter adapter = mImageFieldPicture.getAdapter();
		int count = adapter.getCount();
		boolean mainPicSet = false;
		for (int i = 0; i < count; i++) {
			PictureItem pi = adapter.getItem(i);
			if (pi.getState() == PictureItem.NEW) {
				Picture newPic = pi.getPicture();
				newPic.setRecordId(mMoneyExpenseEditor.getModel().getId());
				newPic.setRecordType("MoneyExpense");
				newPic.setProjectId(mMoneyExpenseEditor.getModelCopy().getProjectId());
				newPic.setDisplayOrder(i);
				newPic.save();
			} else if (pi.getState() == PictureItem.DELETED) {
				pi.getPicture().delete();
			} else if (pi.getState() == PictureItem.CHANGED) {

			}
			if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
				mainPicSet = true;
				mMoneyExpenseEditor.getModelCopy().setPicture(pi.getPicture());
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
				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", project.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
				
				if(mMoneyExpenseEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
					HyjUtil.displayToast(R.string.app_permission_no_addnew);
					return;
				}else if(mMoneyExpenseEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
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
			if (resultCode == Activity.RESULT_OK) {
				long _id = data.getLongExtra("MODEL_ID", -1);
				if(_id == -1){
 	   	       		mSelectorFieldFriend.setText(null);
 	   	       		mSelectorFieldFriend.setModelId(null);
				} else {
					Friend friend = Friend.load(Friend.class, _id);
					
					if(friend.getFriendUserId() != null && friend.getFriendUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						HyjUtil.displayToast(R.string.moneyExpenseFormFragment_editText_error_friend);
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
				MoneyExpenseCategory category = MoneyExpenseCategory.load(MoneyExpenseCategory.class, _id);
				mSelectorFieldMoneyExpenseCategory.setText(category.getName());
				mSelectorFieldMoneyExpenseCategory.setModelId(category.getId());
				if(category.getParentExpenseCategory() != null){
					mSelectorFieldMoneyExpenseCategory.setLabel(category.getParentExpenseCategory().getName());
				} else {
					mSelectorFieldMoneyExpenseCategory.setLabel(null);
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
