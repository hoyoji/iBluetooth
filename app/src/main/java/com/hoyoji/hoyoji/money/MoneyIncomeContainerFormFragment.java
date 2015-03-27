package com.hoyoji.hoyoji.money;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.EventMember;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyApportion;
import com.hoyoji.hoyoji.models.MoneyIncome;
import com.hoyoji.hoyoji.models.MoneyIncomeApportion;
import com.hoyoji.hoyoji.models.MoneyIncomeCategory;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer;
import com.hoyoji.hoyoji.models.MoneyTemplate;
import com.hoyoji.hoyoji.models.MoneyIncomeContainer.MoneyIncomeContainerEditor;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.models.ProjectShareAuthorization;
import com.hoyoji.hoyoji.models.UserData;
import com.hoyoji.hoyoji.money.MoneyApportionField.ApportionItem;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.money.moneycategory.MoneyIncomeCategoryListFragment;
import com.hoyoji.hoyoji.project.ExplainFinancialOwnerFragment;
import com.hoyoji.hoyoji.project.ProjectMemberFormFragment;
import com.hoyoji.hoyoji.project.ProjectMemberListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.event.EventListFragment;
import com.hoyoji.hoyoji.event.EventMemberFormFragment;
import com.hoyoji.hoyoji.event.EventMemberListFragment;
import com.hoyoji.hoyoji.friend.FriendListFragment;

public class MoneyIncomeContainerFormFragment extends HyjUserFormFragment {
	private final static int GET_MONEYACCOUNT_ID = 1;
	private final static int GET_PROJECT_ID = 2;
	private final static int GET_EVENT_ID = 9;
	private final static int GET_FRIEND_ID = 3;
	private final static int GET_APPORTION_MEMBER_ID = 4;
	private final static int GET_CATEGORY_ID = 5;
	private static final int GET_REMARK = 6;
	private final static int GET_AMOUNT = 8;
	private static final int ADD_AS_PROJECT_MEMBER = 0;
	protected static final int GET_FINANCIALOWNER_ID = 7;
	
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private MoneyIncomeContainerEditor mMoneyIncomeContainerEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private MoneyApportionField mApportionFieldApportions = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericAmount = null;
	private HyjSelectorField mSelectorFieldMoneyAccount = null;
	private HyjSelectorField mSelectorFieldProject = null;
	private HyjSelectorField mSelectorFieldEvent = null;
	private View mViewSeparatorEvent = null;
	private HyjNumericField mNumericExchangeRate = null;
	private HyjSelectorField mSelectorFieldMoneyIncomeCategory = null;
	private HyjSelectorField mSelectorFieldFriend = null;
//	private ImageView mImageViewClearFriend = null;
	private HyjRemarkField mRemarkFieldRemark = null;
	private ImageView mImageViewRefreshRate = null;
	private View mViewSeparatorExchange = null;
	private LinearLayout mLinearLayoutExchangeRate = null;
	
	private boolean hasEditPermission = true;
	private TextView mTextViewApportionFieldTitle;
	private DataSetObserver mApportionCountObserver;
	private HyjSelectorField mSelectorFieldFinancialOwner;
	private TextView mTextViewFinancialOwner;
	private ImageButton mButtonExpandMore;
	private LinearLayout mLinearLayoutExpandMore;
	
	private ImageButton calculatorTextView = null;
	
	private Button app_action_save_template = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneyincomecontainer;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		final MoneyIncomeContainer moneyIncomeContainer;
		Double presetAmount = null;
		MoneyTemplate template =null;
		
		Intent intent = getActivity().getIntent();
		final long modelId = intent.getLongExtra("MODEL_ID", -1);
		if(modelId != -1){
			moneyIncomeContainer =  new Select().from(MoneyIncomeContainer.class).where("_id=?", modelId).executeSingle();
			hasEditPermission = moneyIncomeContainer.hasEditPermission();
		} else {
			moneyIncomeContainer = new MoneyIncomeContainer();
			long templateId = intent.getLongExtra("MONEYTEMPLATE_ID", -1);
			if(templateId != -1){
				template = new Select().from(MoneyTemplate.class).where("_id=?", templateId).executeSingle();
				if (template != null) {
					try {
						JSONObject temPlateJso = new JSONObject(template.getData());
						temPlateJso.remove("id");
						temPlateJso.remove("date");
						presetAmount = temPlateJso.optDouble("amount", 0.0);
						temPlateJso.remove("amount");
						moneyIncomeContainer.loadFromJSON(temPlateJso,false);
						// 从模版中获取预设金额
						presetAmount = presetAmount * temPlateJso.optDouble("exchangeRate", 1.0);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} 
			} else {
				final String moneyAccountId = intent.getStringExtra("moneyAccountId");
				if(moneyIncomeContainer.get_mId() == null && moneyAccountId != null){
					moneyIncomeContainer.setMoneyAccountId(moneyAccountId);
				}
				if(intent.getStringExtra("counterpartId") != null){
					moneyIncomeContainer.setIsImported(true);
				}
				presetAmount = intent.getDoubleExtra("amount", 0.0) * intent.getDoubleExtra("exchangeRate", 1.0);//从分享消息导入的金额
			}
		}
		
		mMoneyIncomeContainerEditor = new MoneyIncomeContainerEditor(moneyIncomeContainer);
		
		setupDeleteButton(mMoneyIncomeContainerEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageField_picture);
		mImageFieldPicture.setImages(moneyIncomeContainer.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_textField_date);	
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyIncomeContainer.getDate());
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
		if(moneyIncomeContainer.get_mId() == null && projectId != null){
			moneyIncomeContainer.setProjectId(projectId);
			project = HyjModel.getModel(Project.class, projectId);
		}else{
			project = moneyIncomeContainer.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName() + "(" + project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyIncomeContainerFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.projectListFragment_title_select_project, null, GET_PROJECT_ID);
			}
		});	
		
		mSelectorFieldEvent = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_selectorField_event);
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
			if(moneyIncomeContainer.get_mId() == null){
				if(eventId != null) {
					moneyIncomeContainer.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyIncomeContainer.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyIncomeContainer.getEvent();
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

                    MoneyIncomeContainerFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });
		if(template == null) {
			setupApportionField(moneyIncomeContainer);
		} else {
			setTemplateApportion(moneyIncomeContainer,template.getApportionString());
		}
		
		
		mNumericAmount = (HyjNumericField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_textField_amount);
		int incomeColor = Color.parseColor(HyjApplication.getInstance().getCurrentUser().getUserData().getIncomeColor());
		mNumericAmount.getEditText().setTextColor(incomeColor);
		mNumericAmount.getEditText().setHintTextColor(incomeColor);
		if (presetAmount != null) {
			mNumericAmount.setNumber(presetAmount);
			mApportionFieldApportions.setTotalAmount(presetAmount);
		} else {
			mNumericAmount.setNumber(moneyIncomeContainer.getAmount());
		}
		
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
		
		MoneyAccount moneyAccount = moneyIncomeContainer.getMoneyAccount();
		mSelectorFieldMoneyAccount = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_selectorField_moneyAccount);

		if(moneyAccount != null){
			mSelectorFieldMoneyAccount.setModelId(moneyAccount.getId());
			mSelectorFieldMoneyAccount.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
		}
		mSelectorFieldMoneyAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyIncomeContainerFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_MONEYACCOUNT_ID);
			}
		});	
		
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_textField_exchangeRate);		
		mNumericExchangeRate.setNumber(moneyIncomeContainer.getExchangeRate());
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyIncomeContainerFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyIncomeContainerFormFragment_linearLayout_exchangeRate);
		
		mSelectorFieldMoneyIncomeCategory = (HyjSelectorField) getView().findViewById(
				R.id.moneyIncomeContainerFormFragment_textField_moneyIncomeCategory);
		mSelectorFieldMoneyIncomeCategory.setText(moneyIncomeContainer
				.getMoneyIncomeCategory());
		if(moneyIncomeContainer.getMoneyIncomeCategoryMain() != null && moneyIncomeContainer.getMoneyIncomeCategoryMain().length() > 0){
			mSelectorFieldMoneyIncomeCategory.setLabel(moneyIncomeContainer.getMoneyIncomeCategoryMain());
		}
		mSelectorFieldMoneyIncomeCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MoneyIncomeContainerFormFragment.this
						.openActivityWithFragmentForResult(
								MoneyIncomeCategoryListFragment.class,
								R.string.moneyIncomeFormFragment_editText_hint_moneyIncomeCategory,
								null, GET_CATEGORY_ID);
			}
		});
		
		String friendUserId, localFriendId;
		if(moneyIncomeContainer.get_mId() == null){
			long moneyTemplateId = intent.getLongExtra("MONEYTEMPLATE_ID", -1);
			if(moneyTemplateId == -1){
				 friendUserId = intent.getStringExtra("friendUserId");//从消息导入
				 localFriendId = intent.getStringExtra("localFriendId");//从消息导入
			} else {
				friendUserId = moneyIncomeContainer.getFriendUserId();
				localFriendId = moneyIncomeContainer.getLocalFriendId();
			}
		} else {
			friendUserId = moneyIncomeContainer.getFriendUserId();
			localFriendId = moneyIncomeContainer.getLocalFriendId();
		}
		mSelectorFieldFriend = (HyjSelectorField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_selectorField_friend);

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
				bundle.putBoolean("disableMultiChoiceMode", true);
				MoneyIncomeContainerFormFragment.this
				.openActivityWithFragmentForResult(FriendListFragment.class, R.string.friendListFragment_title_select_friend_payer, bundle, GET_FRIEND_ID);
			}
		}); 
		
//		mImageViewClearFriend = (ImageView) getView().findViewById(
//				R.id.moneyIncomeContainerFormFragment_imageView_clear_friend);
//		mImageViewClearFriend.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mSelectorFieldFriend.setModelId(null);
//				mSelectorFieldFriend.setText("");
//			}
//		});
		
		
		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyIncomeContainer.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyIncomeContainerFormFragment.this
						.openActivityWithFragmentForResult(
								HyjTextInputFormFragment.class,
								R.string.moneyIncomeFormFragment_textView_remark,
								bundle, GET_REMARK);
			}
		});
		
		calculatorTextView = (ImageButton) getView().findViewById(R.id.calculator);
		calculatorTextView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putDouble("AMOUNT", mNumericAmount.getNumber()!=null?mNumericAmount.getNumber():0.00);
				MoneyIncomeContainerFormFragment.this.openActivityWithFragmentForResult(
								HyjCalculatorFormFragment.class,
								R.string.hyjCalculatorFormFragment_title,
								bundle, GET_AMOUNT);
			}
		});
		
		app_action_save_template = (Button) getView().findViewById(R.id.button_save_template);
		app_action_save_template.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				fillData();
				if(validate()){
					MoneyTemplate moneyTemplate = new MoneyTemplate();
					moneyTemplate.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
					moneyTemplate.setType("MoneyIncomeTemplate");
					moneyTemplate.setData(mMoneyIncomeContainerEditor.getModelCopy().toJSON().toString());
					
					MoneyApportionField.ImageGridAdapter adapter = mApportionFieldApportions.getAdapter();
					int count = adapter.getCount();
					JSONArray array = new JSONArray();
					for (int i = 0; i < count; i++) {
						try {
							JSONObject evt = new JSONObject();
							evt.put("amount", adapter.getItem(i).getApportion().getAmount());
							evt.put("friendUserId", adapter.getItem(i).getApportion().getFriendUserId());
							evt.put("localFriendId", adapter.getItem(i).getApportion().getLocalFriendId());
							evt.put("apportionType", adapter.getItem(i).getApportion().getApportionType());
							array.put(evt);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					moneyTemplate.setApportionString(array.toString());
					
					moneyTemplate.save();
					HyjUtil.displayToast(R.string.app_save_template_success);
				}
			}
		});

		mSelectorFieldFinancialOwner = (HyjSelectorField) getView().findViewById(R.id.projectFormFragment_selectorField_financialOwner);
		mSelectorFieldFinancialOwner.setEnabled(hasEditPermission);
		if(modelId == -1){
			long moneyTemplateId = intent.getLongExtra("MONEYTEMPLATE_ID", -1);
			if(moneyTemplateId != -1){
				if(moneyIncomeContainer.getFinancialOwnerUserId() != null) {
					mSelectorFieldFinancialOwner.setModelId(moneyIncomeContainer.getFinancialOwnerUserId());
					mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyIncomeContainer.getFinancialOwnerUserId()));
				}
			} else if (event != null) {
				if(event.getFinancialOwnerUserId() != null) {
					mSelectorFieldFinancialOwner.setModelId(event.getFinancialOwnerUserId());
					mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(event.getFinancialOwnerUserId()));
				}
			} else if(project != null && project.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(project.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(project.getFinancialOwnerUserId()));
			}
		} else if(moneyIncomeContainer.getFinancialOwnerUserId() != null){
				mSelectorFieldFinancialOwner.setModelId(moneyIncomeContainer.getFinancialOwnerUserId());
				mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(moneyIncomeContainer.getFinancialOwnerUserId()));
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
				
				MoneyIncomeContainerFormFragment.this.openActivityWithFragment(ExplainFinancialOwnerFragment.class, R.string.explainFinancialOwnerFragment_title, bundle);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageView_camera);	
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
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageButton_refresh_exchangeRate);	
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
		
		mLinearLayoutExpandMore = (LinearLayout)getView().findViewById(R.id.moneyIncomeContainerFormFragment_expandMore);
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
			
			getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageButton_apportion_add).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    if(mSelectorFieldProject.getModelId() == null){
                        HyjUtil.displayToast("请选择一个圈子");
                        return;
                    }
					Bundle bundle = new Bundle();
					Project project = HyjModel.getModel(Project.class,mSelectorFieldProject.getModelId());
					bundle.putLong("MODEL_ID", project.get_mId());
					if(!project.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
						if(mSelectorFieldEvent.getModelId() != null){
							bundle.putString("EVENTID", mSelectorFieldEvent.getModelId());
							openActivityWithFragmentForResult(EventMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						} else {
							openActivityWithFragmentForResult(ProjectMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						}
					} else {
						if(mSelectorFieldEvent.getModelId() != null){
							bundle.putString("EVENTID", mSelectorFieldEvent.getModelId());
							openActivityWithFragmentForResult(SelectApportionEventMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						} else {
							openActivityWithFragmentForResult(SelectApportionMemberListFragment.class, R.string.moneyApportionField_select_apportion_member, bundle, GET_APPORTION_MEMBER_ID);
						}
					}
				}
			});
			
			getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageButton_apportion_add_all).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addAllProjectMemberIntoApportionsField(moneyIncomeContainer);
				}
			});
			
			getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageButton_apportion_more_actions).setOnClickListener(new OnClickListener() {
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
					mNumericAmount.showSoftKeyboard();
				}
				app_action_save_template.setVisibility(View.VISIBLE);
//				this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			}else{
				if(!moneyIncomeContainer.getOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
//					app_action_save_template.setVisibility(View.GONE);
				} else {
					if(hasEditPermission){
						app_action_save_template.setVisibility(View.VISIBLE);
					}
				}
				setExchangeRate(true);
			}
			setPermission();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    if(mMoneyIncomeContainerEditor!= null && mMoneyIncomeContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
	    	hideSaveAction();
	    }
	}
	
	private void addAllProjectMemberIntoApportionsField(MoneyIncomeContainer moneyIncomeContainer) {
        if(mSelectorFieldProject.getModelId() == null){
            HyjUtil.displayToast("请选择一个圈子");
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
				MoneyIncomeApportion apportion = new MoneyIncomeApportion();
				apportion.setAmount(0.0);
				apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
				apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
				apportion.setMoneyIncomeContainerId(moneyIncomeContainer.getId());
				if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
					apportion.setApportionType("Average");
				} else {
					apportion.setApportionType("Share");
				}
				mApportionFieldApportions.addApportion(apportion, project.getId(),mSelectorFieldEvent.getModelId(), ApportionItem.NEW);
			}
			mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
		} else {
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
					MoneyIncomeApportion apportion = new MoneyIncomeApportion();
					apportion.setAmount(0.0);
					apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
					apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
					apportion.setMoneyIncomeContainerId(moneyIncomeContainer.getId());
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
	
	private void setupApportionField(MoneyIncomeContainer moneyIncomeContainer) {

		mApportionFieldApportions = (MoneyApportionField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_apportionField);
		mTextViewApportionFieldTitle = (TextView) getView().findViewById(R.id.moneyIncomeContainerFormFragment_apportionField_title);
		mApportionCountObserver = new DataSetObserver(){
	        @Override
	        public void onChanged() {
	    		mTextViewApportionFieldTitle.setText(getString(R.string.moneyApportionField_title)+"("+mApportionFieldApportions.getApportionCount()+")");
	        }
		};
		mApportionFieldApportions.getAdapter().registerDataSetObserver(mApportionCountObserver);
		
		List<MoneyIncomeApportion> moneyApportions = null;
		
		if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null) {
			
			moneyApportions = new ArrayList<MoneyIncomeApportion>();
			if(moneyIncomeContainer.getEvent() != null &&  !moneyIncomeContainer.getIsImported()){
				List<ProjectShareAuthorization> projectShareAuthorizations = moneyIncomeContainer.getProject().getShareAuthorizations();
				for(int i=0; i < projectShareAuthorizations.size(); i++){
					if(projectShareAuthorizations.get(i).getState().equalsIgnoreCase("Delete") || !projectShareAuthorizations.get(i).getToBeDetermined()){
						continue;
					} else if(projectShareAuthorizations.get(i).getToBeDetermined()) {
						MoneyIncomeApportion apportion = new MoneyIncomeApportion();
						apportion.setAmount(moneyIncomeContainer.getAmount0());
						apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
						apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
						apportion.setMoneyIncomeContainerId(moneyIncomeContainer.getId());
						if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
							apportion.setApportionType("Average");
						} else {
							apportion.setApportionType("Share");
						}
						moneyApportions.add(apportion);
					}
				}
			} else if(moneyIncomeContainer.getProject() != null && moneyIncomeContainer.getProject().getAutoApportion() && !moneyIncomeContainer.getIsImported()){
				List<ProjectShareAuthorization> projectShareAuthorizations = moneyIncomeContainer.getProject().getShareAuthorizations();
				for(int i=0; i < projectShareAuthorizations.size(); i++){
					if(projectShareAuthorizations.get(i).getState().equalsIgnoreCase("Delete") ||
							projectShareAuthorizations.get(i).getToBeDetermined()){
						continue;
					}
					
					MoneyIncomeApportion apportion = new MoneyIncomeApportion();
					apportion.setAmount(0.0);
					apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
					apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
					apportion.setMoneyIncomeContainerId(moneyIncomeContainer.getId());
					if(projectShareAuthorizations.get(i).getSharePercentageType() != null && projectShareAuthorizations.get(i).getSharePercentageType().equals("Average")){
						apportion.setApportionType("Average");
					} else {
						apportion.setApportionType("Share");
					}
					
					moneyApportions.add(apportion);
				}
			} else if(moneyIncomeContainer.getProject() != null) {
				MoneyIncomeApportion apportion = new MoneyIncomeApportion();
				apportion.setAmount(moneyIncomeContainer.getAmount0());
				apportion.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
				apportion.setMoneyIncomeContainerId(moneyIncomeContainer.getId());
				ProjectShareAuthorization projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", moneyIncomeContainer.getProjectId(), apportion.getFriendUserId()).executeSingle();
				if(projectShareAuthorization.getSharePercentageType() != null && projectShareAuthorization.getSharePercentageType().equals("Average")){
					apportion.setApportionType("Average");
				} else {
					apportion.setApportionType("Share");
				}
				moneyApportions.add(apportion);
			}
			
		} else {
			moneyApportions = moneyIncomeContainer.getApportions();
		}
		
		mApportionFieldApportions.init(moneyIncomeContainer.getAmount0(), moneyApportions, moneyIncomeContainer.getProjectId(), moneyIncomeContainer.getEventId(), moneyIncomeContainer.getId());
	}
	
	private void setTemplateApportion(MoneyIncomeContainer moneyIncomeContainer, String apportionString) {

		mApportionFieldApportions = (MoneyApportionField) getView().findViewById(R.id.moneyIncomeContainerFormFragment_apportionField);
		mTextViewApportionFieldTitle = (TextView) getView().findViewById(R.id.moneyIncomeContainerFormFragment_apportionField_title);
		mApportionCountObserver = new DataSetObserver(){
	        @Override
	        public void onChanged() {
	    		mTextViewApportionFieldTitle.setText(getString(R.string.moneyApportionField_title)+"("+mApportionFieldApportions.getApportionCount()+")");
	        }
		};
		mApportionFieldApportions.getAdapter().registerDataSetObserver(mApportionCountObserver);
		
		List<MoneyIncomeApportion> moneyApportions = new ArrayList<MoneyIncomeApportion>();
		
		if (apportionString != null) {
			try {
				JSONArray templateApportions = new JSONArray(apportionString);
//				JSONObject temPlateApportionJso = null;
				List<ProjectShareAuthorization> projectShareAuthorizations = moneyIncomeContainer.getProject().getShareAuthorizations();
		        for (int j = 0; j < templateApportions.length(); j++) {
		        	JSONObject templateApportion = templateApportions.getJSONObject(j);
//					temPlateApportionJso = new JSONObject(templateApportions[j]);
					if(moneyIncomeContainer.getProject() != null && !moneyIncomeContainer.getIsImported()){
						for(int i=0; i < projectShareAuthorizations.size(); i++){
							if(projectShareAuthorizations.get(i).getState().equalsIgnoreCase("Delete")){
								continue;
							}
							if ((projectShareAuthorizations.get(i).getFriendUserId()!=null && projectShareAuthorizations.get(i).getFriendUserId().equals(templateApportion.optString("friendUserId")))
									|| (projectShareAuthorizations.get(i).getLocalFriendId()!=null && projectShareAuthorizations.get(i).getLocalFriendId().equals(templateApportion.optString("localFriendId")))){
								MoneyIncomeApportion apportion = new MoneyIncomeApportion();
								apportion.setAmount(templateApportion.optDouble("amount"));
								apportion.setFriendUserId(projectShareAuthorizations.get(i).getFriendUserId());
								apportion.setLocalFriendId(projectShareAuthorizations.get(i).getLocalFriendId());
								apportion.setMoneyIncomeContainerId(moneyIncomeContainer.getId());
								apportion.setApportionType(templateApportion.optString("apportionType"));
								moneyApportions.add(apportion);
							}
							
						}
					} 
		        }
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mApportionFieldApportions.init(moneyIncomeContainer.getAmount0(), moneyApportions, moneyIncomeContainer.getProjectId(), moneyIncomeContainer.getEventId(), moneyIncomeContainer.getId());
			
	}
	
	private void setupDeleteButton(HyjModelEditor<MoneyIncomeContainer> moneyIncomeContainerEditor) {

		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
		
		final MoneyIncomeContainer moneyIncomeContainer = moneyIncomeContainerEditor.getModelCopy();
		
		if (moneyIncomeContainer.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(moneyIncomeContainer.hasDeletePermission()){
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									try {
										ActiveAndroid.beginTransaction();

										MoneyAccount moneyAccount = moneyIncomeContainer.getMoneyAccount();
										HyjModelEditor<MoneyAccount> moneyAccountEditor = moneyAccount.newModelEditor();
										moneyAccountEditor.getModelCopy().setCurrentBalance(moneyAccount.getCurrentBalance() - moneyIncomeContainer.getAmount());
										MoneyIncomeContainerEditor moneyIncomeContainerEditor = new MoneyIncomeContainerEditor(moneyIncomeContainer);
										
										//更新圈子余额
										Project newProject = moneyIncomeContainer.getProject();
										HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
										newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - moneyIncomeContainer.getAmount0()*moneyIncomeContainer.getExchangeRate());
										newProjectEditor.save();
										
										//更新活动余额
										Event newEvent = moneyIncomeContainer.getEvent();
										if(newEvent != null) {
											HyjModelEditor<Event> newEventEditor = newEvent.newModelEditor();
											newEventEditor.getModelCopy().setIncomeTotal(newEvent.getIncomeTotal() - moneyIncomeContainer.getAmount0()*moneyIncomeContainer.getExchangeRate());
											newEventEditor.save();
										}
										
										//删除收入的同时删除分摊
										Iterator<MoneyIncomeApportion> moneyIncomeApportions = moneyIncomeContainer.getApportions().iterator();
										while (moneyIncomeApportions.hasNext()) {
											MoneyIncomeApportion moneyIncomeApportion = moneyIncomeApportions.next();
											MoneyIncomeContainer.deleteApportion(moneyIncomeApportion, moneyIncomeContainerEditor);
											
//											ProjectShareAuthorization oldProjectShareAuthorization;
//											
//											if(HyjApplication.getInstance().getCurrentUser().getId().equals(moneyIncomeApportion.getFriendUserId())){
//												// 更新旧圈子的分摊支出
//												oldProjectShareAuthorization = moneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//												HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//												oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (moneyIncomeApportion.getAmount0() * moneyIncomeApportion.getMoneyIncomeContainer().getExchangeRate()));
//												oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (moneyIncomeApportion.getAmount0() * moneyIncomeApportion.getMoneyIncomeContainer().getExchangeRate()));
//												oldProjectShareAuthorizationEditor.save();
//												
//												MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", moneyIncomeApportion.getId()).executeSingle();
//												if(moneyIncome != null){
//													moneyIncome.delete();
//												}
//											} else {
//												// 更新旧圈子分摊收入// 更新旧圈子分摊支出
//												oldProjectShareAuthorization = moneyIncomeApportion.getProjectShareAuthorization();
//												HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//												
//												oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (moneyIncomeApportion.getAmount0() * moneyIncomeApportion.getMoneyIncomeContainer().getExchangeRate()));
//												oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (moneyIncomeApportion.getAmount0() * moneyIncomeApportion.getMoneyIncomeContainer().getExchangeRate()));
//												MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", moneyIncomeApportion.getId()).executeSingle();
//												if(moneyIncome != null){
//													moneyIncome.delete();
//												} 
//											
//												oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalLend() - (moneyIncomeApportion.getAmount0() * moneyIncomeApportion.getMoneyIncomeContainer().getExchangeRate()));
//												List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=?", moneyIncomeApportion.getId()).execute();
//												for(MoneyLend moneyLend : moneyLends){
//													moneyLend.delete();
//												} 
//												oldProjectShareAuthorizationEditor.save();
//												
//												oldProjectShareAuthorization = moneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//												oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//												oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (moneyIncomeApportion.getAmount0() * moneyIncomeApportion.getMoneyIncomeContainer().getExchangeRate()));
//												oldProjectShareAuthorizationEditor.save();
//												List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=?", moneyIncomeApportion.getId()).execute();
//												for(MoneyBorrow moneyBorrow : moneyBorrows){
//													moneyBorrow.delete();
//												} 
//											}
//											
//											moneyIncomeApportion.delete();
										}
	
										moneyIncomeContainer.delete();
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

		if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			mDateTimeFieldDate.setEnabled(false);
			
//			mNumericAmount.setNumber(mMoneyIncomeContainerEditor.getModel().getProjectAmount());
			mNumericAmount.setEnabled(false);
			
			mSelectorFieldMoneyIncomeCategory.setEnabled(false);
			mSelectorFieldFriend.setEnabled(false);
			
			mSelectorFieldMoneyAccount.setEnabled(false);
			mSelectorFieldMoneyAccount.setVisibility(View.GONE);
			getView().findViewById(R.id.moneyIncomeContainerFormFragment_separatorField_moneyAccount).setVisibility(View.GONE);

			mSelectorFieldProject.setEnabled(false);
			
			mNumericExchangeRate.setEnabled(false);
			
			mApportionFieldApportions.setEnabled(false);

			mRemarkFieldRemark.setEnabled(false);
			
			if(this.mOptionsMenu != null){
		    	hideSaveAction();
			}
			
			getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageButton_apportion_add).setEnabled(false);
			getView().findViewById(R.id.moneyIncomeContainerFormFragment_imageButton_apportion_add_all).setEnabled(false);
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
		MoneyIncomeContainer modelCopy =  mMoneyIncomeContainerEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setAmount(mNumericAmount.getNumber());
		modelCopy.setMoneyAccountId(mSelectorFieldMoneyAccount.getModelId());
        if(mSelectorFieldProject.getModelId() != null) {
            modelCopy.setProject(HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId()));
        }
		modelCopy.setEventId(mSelectorFieldEvent.getModelId());
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());
		modelCopy.setMoneyIncomeCategory(mSelectorFieldMoneyIncomeCategory.getText());
		modelCopy.setMoneyIncomeCategoryMain(mSelectorFieldMoneyIncomeCategory.getLabel());
		modelCopy.setFinancialOwnerUserId(mSelectorFieldFinancialOwner.getModelId());

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
		mDateTimeFieldDate.setError(mMoneyIncomeContainerEditor.getValidationError("datetime"));
		mNumericAmount.setError(mMoneyIncomeContainerEditor.getValidationError("amount"));
		if(mMoneyIncomeContainerEditor.getValidationError("amount") != null){
					mNumericAmount.showSoftKeyboard();
				}
		mSelectorFieldMoneyAccount.setError(mMoneyIncomeContainerEditor.getValidationError("moneyAccount"));
		mSelectorFieldProject.setError(mMoneyIncomeContainerEditor.getValidationError("project"));
		mNumericExchangeRate.setError(mMoneyIncomeContainerEditor.getValidationError("exchangeRate"));
		mSelectorFieldMoneyIncomeCategory.setError(mMoneyIncomeContainerEditor.getValidationError("moneyIncomeCategory"));
		mSelectorFieldFriend.setError(mMoneyIncomeContainerEditor.getValidationError("friend"));
		mRemarkFieldRemark.setError(mMoneyIncomeContainerEditor.getValidationError("remark"));
		mApportionFieldApportions.setError(mMoneyIncomeContainerEditor.getValidationError("apportionTotalAmount"));
	}
	
	private boolean validate(){
		if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null && !mMoneyIncomeContainerEditor.getModelCopy().hasAddNewPermission(mMoneyIncomeContainerEditor.getModelCopy().getProjectId())){
			HyjUtil.displayToast(R.string.app_permission_no_addnew);
		}else if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() != null && !hasEditPermission){
			HyjUtil.displayToast(R.string.app_permission_no_edit);
		}else{
		
			mMoneyIncomeContainerEditor.validate();
			
			if (mApportionFieldApportions.getCount() > 0) {
				if (mNumericAmount.getNumber() != null && !mNumericAmount.getNumber().equals(mApportionFieldApportions.getTotalAmount())) {
					mMoneyIncomeContainerEditor.setValidationError("apportionTotalAmount",R.string.moneyApportionField_select_toast_apportion_totalAmount_not_equal);
				} else {
					mMoneyIncomeContainerEditor.removeValidationError("apportionTotalAmount");
				}
			} else {
				mMoneyIncomeContainerEditor.removeValidationError("apportionTotalAmount");
			}
			
			if(mMoneyIncomeContainerEditor.hasValidationErrors()){
				showValidatioErrors();
			} else {
				return true;
			}
		}
		return false;
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		if(!validate()){
			return;
		}
		
			try {
				ActiveAndroid.beginTransaction();
				
				savePictures();

				saveApportions();
				
				MoneyIncomeContainer oldMoneyIncomeContainerModel = mMoneyIncomeContainerEditor.getModel();
				MoneyIncomeContainer moneyIncomeContainerModel = mMoneyIncomeContainerEditor.getModelCopy();
				
				//设置默认圈子和账户
				UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
				if(moneyIncomeContainerModel.get_mId() == null && !userData.getActiveMoneyAccountId().equals(moneyIncomeContainerModel.getMoneyAccountId())){
					HyjModelEditor<UserData> userDataEditor = userData.newModelEditor();
					userDataEditor.getModelCopy().setActiveMoneyAccountId(moneyIncomeContainerModel.getMoneyAccountId());
					userDataEditor.getModelCopy().setActiveProjectId(moneyIncomeContainerModel.getProjectId());
					userDataEditor.save();
				}
				
				//设置默认活动
				Project project = moneyIncomeContainerModel.getProject();
				if(moneyIncomeContainerModel.get_mId() == null){
					if((moneyIncomeContainerModel.getEventId() != null && !moneyIncomeContainerModel.getEventId().equals(project.getActiveEventId())) 
							|| (project.getActiveEventId() != null && !project.getActiveEventId().equals(moneyIncomeContainerModel.getEventId()))){
						HyjModelEditor<Project> projectEditor = project.newModelEditor();
						projectEditor.getModelCopy().setActiveEventId(moneyIncomeContainerModel.getEventId());
						projectEditor.save();
					}
				}
				
				// 更新圈子的默认分类
				if(moneyIncomeContainerModel.get_mId() == null){
					HyjModelEditor<Project> projectEditor = moneyIncomeContainerModel.getProject().newModelEditor();
					projectEditor.getModelCopy().setDefaultIncomeCategory(moneyIncomeContainerModel.getMoneyIncomeCategory());
					projectEditor.getModelCopy().setDefaultIncomeCategoryMain(moneyIncomeContainerModel.getMoneyIncomeCategoryMain());
					projectEditor.save();
				}
				
				//当前汇率不存在时，创建汇率
				String localCurrencyId = moneyIncomeContainerModel.getMoneyAccount().getCurrencyId();
				String foreignCurrencyId = moneyIncomeContainerModel.getProject().getCurrencyId();
				if(CREATE_EXCHANGE == 1){
					MoneyAccount moneyAccount = moneyIncomeContainerModel.getMoneyAccount();
//					Project project = moneyIncomeContainerModel.getProject();
					
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(moneyAccount.getCurrencyId());
					newExchange.setForeignCurrencyId(project.getCurrencyId());
					newExchange.setRate(moneyIncomeContainerModel.getExchangeRate());
					newExchange.save();
				}else {
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = null;
						Double exRate = null;
						Double rate = HyjUtil.toFixed2(moneyIncomeContainerModel.getExchangeRate());
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
				
				    MoneyAccount oldMoneyAccount = oldMoneyIncomeContainerModel.getMoneyAccount();
					MoneyAccount newMoneyAccount = moneyIncomeContainerModel.getMoneyAccount();
					HyjModelEditor<MoneyAccount> newMoneyAccountEditor = newMoneyAccount.newModelEditor();
					
					//更新账户余额
					if(moneyIncomeContainerModel.get_mId() == null || oldMoneyAccount.getId().equals(newMoneyAccount.getId())){
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() - oldMoneyIncomeContainerModel.getAmount0() + moneyIncomeContainerModel.getAmount0());
					}else{
						HyjModelEditor<MoneyAccount> oldMoneyAccountEditor = oldMoneyAccount.newModelEditor();
						oldMoneyAccountEditor.getModelCopy().setCurrentBalance(oldMoneyAccount.getCurrentBalance() - oldMoneyIncomeContainerModel.getAmount0());
						newMoneyAccountEditor.getModelCopy().setCurrentBalance(newMoneyAccount.getCurrentBalance() + moneyIncomeContainerModel.getAmount0());
						oldMoneyAccountEditor.save();
					}
					newMoneyAccountEditor.save();

					Project oldProject = oldMoneyIncomeContainerModel.getProject();
					Project newProject = moneyIncomeContainerModel.getProject();
					HyjModelEditor<Project> newProjectEditor = newProject.newModelEditor();
					
					//更新圈子余额
					if(moneyIncomeContainerModel.get_mId() == null || oldProject.getId().equals(newProject.getId())){
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
					} else {
						HyjModelEditor<Project> oldProjectEditor = oldProject.newModelEditor();
						oldProjectEditor.getModelCopy().setIncomeTotal(oldProject.getIncomeTotal() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate());
						newProjectEditor.getModelCopy().setIncomeTotal(newProject.getIncomeTotal() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
						oldProjectEditor.save();
					}
					newProjectEditor.save();
					
					Event oldEvent = oldMoneyIncomeContainerModel.getEvent();
					Event newEvent = moneyIncomeContainerModel.getEvent();
					if(oldEvent != null && newEvent != null) {
						HyjModelEditor<Event> newEventEditor = newEvent.newModelEditor();
						
						//更新活动余额
						if(oldEvent.getId().equals(newEvent.getId())){
							newEventEditor.getModelCopy().setIncomeTotal(newEvent.getIncomeTotal() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
						} else {
							HyjModelEditor<Event> oldEventEditor = oldEvent.newModelEditor();
							oldEventEditor.getModelCopy().setIncomeTotal(oldEvent.getIncomeTotal() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate());
							newEventEditor.getModelCopy().setIncomeTotal(newEvent.getIncomeTotal() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
							oldEventEditor.save();
						}
						newEventEditor.save();
					} else if(oldEvent != null) {
						HyjModelEditor<Event> oldEventEditor = oldEvent.newModelEditor();
						oldEventEditor.getModelCopy().setIncomeTotal(oldEvent.getIncomeTotal() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate());
						oldEventEditor.save();
					} else if(newEvent != null) {
						HyjModelEditor<Event> newEventEditor = newEvent.newModelEditor();
						newEventEditor.getModelCopy().setIncomeTotal(newEvent.getIncomeTotal() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
						newEventEditor.save();
					}
					
					/*
					//更新支出所有者的实际收入
					ProjectShareAuthorization selfProjectAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
					HyjModelEditor<ProjectShareAuthorization> selfProjectAuthorizationEditor = selfProjectAuthorization.newModelEditor();
				    
					if(moneyIncomeContainerModel.get_mId() == null || oldMoneyIncomeContainerModel.getProjectId().equals(moneyIncomeContainerModel.getProjectId())){
				    	selfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(selfProjectAuthorization.getActualTotalIncome() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
					}else{
						selfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(selfProjectAuthorization.getActualTotalIncome() + moneyIncomeContainerModel.getAmount0()*moneyIncomeContainerModel.getExchangeRate());
						
						ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
						HyjModelEditor<ProjectShareAuthorization> oldSelfProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
						oldSelfProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(oldSelfProjectAuthorization.getActualTotalIncome() - oldMoneyIncomeContainerModel.getAmount0()*oldMoneyIncomeContainerModel.getExchangeRate());
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
					
				mMoneyIncomeContainerEditor.save();
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

	private void savePictures(){
		 HyjImageField.ImageGridAdapter adapter = mImageFieldPicture.getAdapter();
			int count = adapter.getCount();
			boolean mainPicSet = false;
			for(int i = 0; i < count; i++){
				PictureItem pi = adapter.getItem(i);
				if(pi.getState() == PictureItem.NEW){
					Picture newPic = pi.getPicture();
					newPic.setRecordId(mMoneyIncomeContainerEditor.getModel().getId());
					newPic.setRecordType("MoneyIncomeContainer");
					newPic.setProjectId(mMoneyIncomeContainerEditor.getModelCopy().getProjectId());
					newPic.setDisplayOrder(i);
					newPic.save();
				} else if(pi.getState() == PictureItem.DELETED){
					pi.getPicture().delete();
				} else if(pi.getState() == PictureItem.CHANGED){

				}
				if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
					mainPicSet = true;
					mMoneyIncomeContainerEditor.getModelCopy().setPicture(pi.getPicture());
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
			savedCount = MoneyIncomeContainer.saveApportions(apportionItems, mMoneyIncomeContainerEditor);
			
//			for (int i = 0; i < count; i++) {
//				ApportionItem<MoneyApportion> api = adapter.getItem(i);
//				MoneyIncomeApportion apportion = (MoneyIncomeApportion) api.getApportion();
//				HyjModelEditor<MoneyIncomeApportion> apportionEditor = apportion.newModelEditor();
//	            
//				
//					if(api.getState() == ApportionItem.DELETED ){
//						ProjectShareAuthorization oldProjectShareAuthorization;
//						if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//							// 更新旧圈子的分摊支出
//							oldProjectShareAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//							HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//							oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							oldProjectShareAuthorizationEditor.save();
//							
//							MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
//							if(moneyIncome != null){
//								moneyIncome.delete();
//							}
//						} else {
//							// 更新旧圈子分摊支出
//							oldProjectShareAuthorization = apportion.getProjectShareAuthorization();
//							HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//							
//							oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
//							if(moneyIncome != null){
//								moneyIncome.delete();
//							}
//						
//							oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalLend() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=?", apportion.getId()).execute();
//							for(MoneyLend moneyLend : moneyLends){
//								moneyLend.delete();
//							} 
//							oldProjectShareAuthorizationEditor.save();
//							
//							oldProjectShareAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//							oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//							oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							oldProjectShareAuthorizationEditor.save();
//							List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=?", apportion.getId()).execute();
//							for(MoneyBorrow moneyBorrow : moneyBorrows){
//								moneyBorrow.delete();
//							}
//						}
//						
//						apportion.delete();
//						
//					} else {
//						if(api.getState() != ApportionItem.UNCHANGED
//							|| !mMoneyIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyIncomeContainerEditor.getModel().getProjectId())
//							|| !mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyIncomeContainerEditor.getModel().getMoneyAccountId())) {
//							api.saveToCopy(apportionEditor.getModelCopy());
//						}
//						Double oldRate = mMoneyIncomeContainerEditor.getModel().getExchangeRate(); 
//						Double rate = mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate();
//						Double oldApportionAmount = apportionEditor.getModel().getAmount0();
//						
//						ProjectShareAuthorization projectShareAuthorization;
//							//维护圈子成员金额
//						if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//							projectShareAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
//						} else if(apportion.getLocalFriendId() != null){
//							projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
//									mMoneyIncomeContainerEditor.getModelCopy().getProjectId(), apportion.getLocalFriendId()).executeSingle();
//						} else {
//							projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
//									mMoneyIncomeContainerEditor.getModelCopy().getProjectId(), apportion.getFriendUserId()).executeSingle();
//						}
//							HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
//							
//							
//							if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null || 
//									mMoneyIncomeContainerEditor.getModel().getProjectId().equals(mMoneyIncomeContainerEditor.getModelCopy().getProjectId())){
//								 // 无旧圈子可更新
//								projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorization.getApportionedTotalIncome() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
//								projectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(projectShareAuthorization.getActualTotalIncome() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
//								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//									projectShareAuthorizationEditor.getModelCopy().setActualTotalLend(projectShareAuthorization.getActualTotalLend() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
//									projectShareAuthorizationEditor.save();
//									
//									ProjectShareAuthorization selfProjectShareAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
//									projectShareAuthorizationEditor = selfProjectShareAuthorization.newModelEditor();
//									projectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(selfProjectShareAuthorization.getActualTotalBorrow() - (oldApportionAmount * oldRate) + (apportionEditor.getModelCopy().getAmount0() * rate));
//									projectShareAuthorizationEditor.save();
//								} else {
//									projectShareAuthorizationEditor.save();
//								}
//							
//							}else{
//								//更新新圈子分摊支出
//								projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorization.getApportionedTotalIncome() + (apportionEditor.getModelCopy().getAmount0() * rate));
//								projectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(projectShareAuthorization.getActualTotalIncome() + (apportionEditor.getModelCopy().getAmount0() * rate));
//								if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//									projectShareAuthorizationEditor.getModelCopy().setActualTotalLend(projectShareAuthorization.getActualTotalLend() + (apportionEditor.getModelCopy().getAmount0() * rate));
//									projectShareAuthorizationEditor.save();
//										
//									ProjectShareAuthorization selfProjectShareAuthorization = mMoneyIncomeContainerEditor.getNewSelfProjectShareAuthorization();
//									projectShareAuthorizationEditor = selfProjectShareAuthorization.newModelEditor();
//									projectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(selfProjectShareAuthorization.getActualTotalBorrow() + (apportionEditor.getModelCopy().getAmount0() * rate));
//									projectShareAuthorizationEditor.save();
//								} else {
//									projectShareAuthorizationEditor.save();
//								}
//								
//								//更新老圈子分摊支出
//								ProjectShareAuthorization oldProjectAuthorization;
//
//								if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//									oldProjectAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//								} else if(apportion.getLocalFriendId() != null){
//									oldProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND localFriendId=?", 
//											mMoneyIncomeContainerEditor.getModel().getProjectId(), apportion.getLocalFriendId()).executeSingle();
//								} else {
//									oldProjectAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
//											mMoneyIncomeContainerEditor.getModel().getProjectId(), apportion.getFriendUserId()).executeSingle();
//									
//								}
//								if(oldProjectAuthorization != null){
//									HyjModelEditor<ProjectShareAuthorization> oldProjectAuthorizationEditor = oldProjectAuthorization.newModelEditor();
//									oldProjectAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectAuthorization.getApportionedTotalIncome() - (oldApportionAmount * oldRate));
//									oldProjectAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectAuthorization.getActualTotalIncome() - (oldApportionAmount * oldRate));
//									if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//										oldProjectAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectAuthorization.getActualTotalLend() - (oldApportionAmount * oldRate));
//										oldProjectAuthorizationEditor.save();
//
//										ProjectShareAuthorization oldSelfProjectAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//										oldProjectAuthorizationEditor = oldSelfProjectAuthorization.newModelEditor();
//										oldProjectAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldSelfProjectAuthorization.getActualTotalBorrow() - (oldApportionAmount * oldRate));
//										oldProjectAuthorizationEditor.save();
//									} else {
//										oldProjectAuthorizationEditor.save();
//									}
//								}
//							}
//							
//							//更新相关好友的借贷账户
//							if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//								MoneyAccount debtAccount = null;
//								if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
//									if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//										debtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), null, mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//									} else {
//										debtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId());
//									}
//								} else {
//									debtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId());
//								}
//								if(api.getState() == ApportionItem.NEW){
//					                if(debtAccount != null){
//					                	HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//					                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//					                	debtAccountEditor.save();
//					                }else{
//					                	// 创建新的借贷账户
//					                	if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
//					                		if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//												MoneyAccount.createDebtAccount(null, null, mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//					                		} else {
//						                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//							                }
//					                	} else {
//					                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//						                }
//					                }
//								} else{
//									MoneyAccount oldDebtAccount = null;
//									oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
//									if(mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() != null) {
//										if(!mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//											oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), null, mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId());
//										} else {
//											oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
//										}
//									} else {
//										oldDebtAccount = MoneyAccount.getDebtAccount(mMoneyIncomeContainerEditor.getModel().getProject().getCurrencyId(), apportionEditor.getModel().getLocalFriendId(), apportionEditor.getModel().getFriendUserId());
//									}
//									if(debtAccount == null){
//					                	if(oldDebtAccount != null){
//											HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
//											oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + apportionEditor.getModel().getAmount0()*apportionEditor.getModel().getExchangeRate());
//											oldDebtAccountEditor.save();
//					                	}// 创建新的借贷账户
//					                	if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() != null){
//					                		if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//												MoneyAccount.createDebtAccount(null, null, mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//					                		} else {
//						                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//						                	}
//					                	} else {
//					                		MoneyAccount.createDebtAccount(projectShareAuthorization.getFriendUserName(), apportionEditor.getModelCopy().getLocalFriendId(), apportionEditor.getModelCopy().getFriendUserId(), mMoneyIncomeContainerEditor.getModelCopy().getProject().getCurrencyId(), -apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//					                	}
//									} else if(oldDebtAccount != null && debtAccount.getId().equals(oldDebtAccount.getId())){
//					                	HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
//										oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + apportionEditor.getModel().getAmount0()*apportionEditor.getModel().getExchangeRate() - apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//					                	oldDebtAccountEditor.save();
//					                } else {
//					                	if(oldDebtAccount != null){
//						                	HyjModelEditor<MoneyAccount> oldDebtAccountEditor = oldDebtAccount.newModelEditor();
//											oldDebtAccountEditor.getModelCopy().setCurrentBalance(oldDebtAccount.getCurrentBalance() + apportionEditor.getModel().getAmount0()*apportionEditor.getModel().getExchangeRate());
//						                	oldDebtAccountEditor.save();
//					                	}
//							    		HyjModelEditor<MoneyAccount> debtAccountEditor = debtAccount.newModelEditor();
//					                	debtAccountEditor.getModelCopy().setCurrentBalance(debtAccount.getCurrentBalance() - apportionEditor.getModelCopy().getAmount0()*mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//				                		debtAccountEditor.save();
//							        }
//								}
//						    }
//							
//							if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//								MoneyIncome moneyIncome = null;
//								if(apportion.get_mId() == null) {
//									moneyIncome = new MoneyIncome();
//								} else {
//									moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
//								}
//								moneyIncome.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//								moneyIncome.setAmount(apportionEditor.getModelCopy().getAmount0());
//								moneyIncome.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//								moneyIncome.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//								moneyIncome.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//								moneyIncome.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFriendUserId());
//								moneyIncome.setLocalFriendId(mMoneyIncomeContainerEditor.getModelCopy().getLocalFriendId());
//								moneyIncome.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//								moneyIncome.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//								moneyIncome.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//
//								if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null) {
//									MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//									moneyIncome.setMoneyAccountId(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
//								} else {
//									moneyIncome.setMoneyAccountId(null, null);
//								}
//
//								moneyIncome.setMoneyIncomeCategory(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategory());
//								moneyIncome.setMoneyIncomeCategoryMain(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategoryMain());
//								moneyIncome.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//								moneyIncome.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//								moneyIncome.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//								moneyIncome.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//								moneyIncome.save();
//							} else {
//								MoneyLend moneyLendOfFinancialOwner = null; // 财务负责人向记账人借出
//								MoneyBorrow moneyBorrow = null; // 记账人向财务负责人借入
//								
//								MoneyLend moneyLend = null;		// 分摊人向财务负责人借出
//								MoneyBorrow moneyBorrowOfFinancialOwner = null; // 财务负责人向分摊人借入
//								
//								if(apportion.get_mId() == null){
//									moneyBorrow = new MoneyBorrow();
//									moneyLendOfFinancialOwner = new MoneyLend();
//									
//									moneyLend = new MoneyLend();
//									moneyBorrowOfFinancialOwner = new MoneyBorrow();
//								} else {
//									if(mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() == null
//											|| mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//										if(apportionEditor.getModel().getFriendUserId() != null){
//											// 记账人向财务负责人借入
//											moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
//											// 分摊人向财务负责人借出
//											moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerUserId=?", apportion.getId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
//										} else {
//											// 记账人向财务负责人借入
//											moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND localFriendId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
//											// 分摊人向财务负责人借出
//											moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerFriendId=?", apportion.getId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
//										}
//										moneyBorrowOfFinancialOwner = new MoneyBorrow();
//										moneyLendOfFinancialOwner = new MoneyLend();
//									} else {
//										// 记账人向财务负责人借入
//										moneyBorrow = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), HyjApplication.getInstance().getCurrentUser().getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() ).executeSingle();
//										// 分摊人向财务负责人借出
//										moneyLend = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerUserId=?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() ).executeSingle();
//										// 财务负责人向记账人借出
//										moneyLendOfFinancialOwner = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=? AND ownerUserId=?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId()).executeSingle();
//										if(apportionEditor.getModel().getFriendUserId() != null){
//											// 财务负责人向分摊人借入
//											moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND friendUserId = ?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId(), apportionEditor.getModel().getFriendUserId()).executeSingle();
//										} else {
//											// 财务负责人向分摊人借入
//											moneyBorrowOfFinancialOwner = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=? AND ownerUserId=? AND localFriendId = ?", apportion.getId(), mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId(), apportionEditor.getModel().getLocalFriendId()).executeSingle();
//										}
//									}
//
//									String previousFinancialOwnerUserId = HyjUtil.ifNull(mMoneyIncomeContainerEditor.getModel().getFinancialOwnerUserId() , "");
//									String currentFinancialOwnerUserId = HyjUtil.ifNull(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() , "");
//									if(moneyBorrowOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//										// 删除老的 财务负责人向分摊人借入 
//										moneyBorrowOfFinancialOwner.delete();
//										// 生成新的 财务负责人向分摊人借入
//										moneyBorrowOfFinancialOwner = new MoneyBorrow();
//									}
//									if(moneyBorrowOfFinancialOwner == null) {
//										moneyBorrowOfFinancialOwner = new MoneyBorrow();
//									}
//									
//									if(moneyLend != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//										moneyLend.delete();
//										moneyLend = new MoneyLend();
//									}
//									if(moneyLend == null){
//										moneyLend = new MoneyLend();
//									}
//									
//									if(moneyBorrow != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//										moneyBorrow.delete();
//										moneyBorrow = new MoneyBorrow();
//									}
//									if(moneyBorrow == null){
//										moneyBorrow = new MoneyBorrow();
//									}
//									
//									if(moneyLendOfFinancialOwner != null && !previousFinancialOwnerUserId.equals(currentFinancialOwnerUserId)){
//										moneyLendOfFinancialOwner.delete();
//										moneyLendOfFinancialOwner = new MoneyLend();
//									}
//									if(moneyLendOfFinancialOwner == null) {
//										moneyLendOfFinancialOwner = new MoneyLend();
//									}
//								}
//								if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
//										|| mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//									moneyBorrow.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//									moneyBorrow.setAmount(apportionEditor.getModelCopy().getAmount0());
//									moneyBorrow.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//									moneyBorrow.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//									moneyBorrow.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//									moneyBorrow.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
//									moneyBorrow.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//									moneyBorrow.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//									moneyBorrow.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//									moneyBorrow.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//									
//									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//										moneyBorrow.setMoneyAccountId(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
//									} else {
//										moneyBorrow.setMoneyAccountId(null, null);
//									}
//
//									moneyBorrow.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//									moneyBorrow.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//									moneyBorrow.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//									moneyBorrow.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//									moneyBorrow.save();
//								} else  if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())) {
//									moneyBorrow.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//									moneyBorrow.setAmount(apportionEditor.getModelCopy().getAmount0());
//									moneyBorrow.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//									moneyBorrow.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//									moneyBorrow.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//									moneyBorrow.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//									moneyBorrow.setLocalFriendId(null);
//									moneyBorrow.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//									moneyBorrow.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//									moneyBorrow.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//									
//									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//										moneyBorrow.setMoneyAccountId(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
//									} else {
//										moneyBorrow.setMoneyAccountId(null, null);
//									}
//
//									moneyBorrow.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//									moneyBorrow.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//									moneyBorrow.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//									moneyBorrow.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//									moneyBorrow.save();
//
//									//===================================================================================================
//									
//									moneyLendOfFinancialOwner.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//									moneyLendOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
//									moneyLendOfFinancialOwner.setOwnerUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//									moneyLendOfFinancialOwner.setOwnerFriendId(null);
//									moneyLendOfFinancialOwner.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//									moneyLendOfFinancialOwner.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//									moneyLendOfFinancialOwner.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//									moneyLendOfFinancialOwner.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
//									moneyLendOfFinancialOwner.setLocalFriendId(null);
//									moneyLendOfFinancialOwner.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//									moneyLendOfFinancialOwner.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//									moneyLendOfFinancialOwner.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//
//									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//										moneyLendOfFinancialOwner.setMoneyAccountId(null, moneyAccount.getCurrencyId());
//									} else {
//										moneyLendOfFinancialOwner.setMoneyAccountId(null, null);
//									}
//									
//									moneyLendOfFinancialOwner.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//									moneyLendOfFinancialOwner.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//									moneyLendOfFinancialOwner.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//									moneyLendOfFinancialOwner.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//									moneyLendOfFinancialOwner.save();
//								}
//								
//
//								
//								if(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId() == null
//										|| mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(HyjApplication.getInstance().getCurrentUser().getId())){
//									moneyLend.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//									moneyLend.setAmount(apportionEditor.getModelCopy().getAmount0());
//									if(apportionEditor.getModelCopy().getFriendUserId() != null){
//										moneyLend.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
//										moneyLend.setOwnerFriendId(null);
//									} else {
//										moneyLend.setOwnerUserId(""); // 设为"",使他不会自动使用当前的用户id
//										moneyLend.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//									}
//									moneyLend.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//									moneyLend.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//									moneyLend.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//									moneyLend.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
//									moneyLend.setLocalFriendId(null);
//									moneyLend.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//									moneyLend.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//									moneyLend.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//	
//									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//										moneyLend.setMoneyAccountId(null, moneyAccount.getCurrencyId());
//									} else {
//										moneyLend.setMoneyAccountId(null, null);
//									}
//									
//									moneyLend.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//									moneyLend.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//									moneyLend.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//									moneyLend.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//									moneyLend.save();
//								} else if(!mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId().equals(apportionEditor.getModelCopy().getFriendUserId())){
//									moneyLend.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//									moneyLend.setAmount(apportionEditor.getModelCopy().getAmount0());
//									if(apportionEditor.getModelCopy().getFriendUserId() != null){
//										moneyLend.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
//										moneyLend.setOwnerFriendId(null);
//									} else {
//										moneyLend.setOwnerUserId("");  // 设为"",使他不会自动使用当前的用户id
//										moneyLend.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//									}
//									moneyLend.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//									moneyLend.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//									moneyLend.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//									moneyLend.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//									moneyLend.setLocalFriendId(null);
//									moneyLend.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//									moneyLend.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//									moneyLend.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//	
//									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//										moneyLend.setMoneyAccountId(null, moneyAccount.getCurrencyId());
//									} else {
//										moneyLend.setMoneyAccountId(null, null);
//									}
//									
//									moneyLend.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//									moneyLend.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//									moneyLend.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//									moneyLend.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//									moneyLend.save();
//									
//									//===================================================================================================
//									
//									moneyBorrowOfFinancialOwner.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//									moneyBorrowOfFinancialOwner.setAmount(apportionEditor.getModelCopy().getAmount0());
//									moneyBorrowOfFinancialOwner.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//									moneyBorrowOfFinancialOwner.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//									moneyBorrowOfFinancialOwner.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//									moneyBorrowOfFinancialOwner.setOwnerUserId(mMoneyIncomeContainerEditor.getModelCopy().getFinancialOwnerUserId());
//									moneyBorrowOfFinancialOwner.setOwnerFriendId(null);
//									if(apportionEditor.getModelCopy().getFriendUserId() != null){
//										moneyBorrowOfFinancialOwner.setFriendUserId(apportionEditor.getModelCopy().getFriendUserId());
//										moneyBorrowOfFinancialOwner.setLocalFriendId(null);
//									} else {
//										moneyBorrowOfFinancialOwner.setFriendUserId(null);
//										moneyBorrowOfFinancialOwner.setLocalFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//									}
//									moneyBorrowOfFinancialOwner.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//									moneyBorrowOfFinancialOwner.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//									moneyBorrowOfFinancialOwner.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//									
//									if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//										MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//										moneyBorrowOfFinancialOwner.setMoneyAccountId(null, moneyAccount.getCurrencyId());
//									} else {
//										moneyBorrowOfFinancialOwner.setMoneyAccountId(null, null);
//									}
//
//									moneyBorrowOfFinancialOwner.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//									moneyBorrowOfFinancialOwner.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//									moneyBorrowOfFinancialOwner.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//									moneyBorrowOfFinancialOwner.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//									moneyBorrowOfFinancialOwner.save();
//
//								}
//								
//								
//								MoneyIncome moneyIncome = null;
//								if(apportion.get_mId() == null){
//									moneyIncome = new MoneyIncome();
//								} else {
//									moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
//								}
//								moneyIncome.setMoneyIncomeApportionId(apportionEditor.getModelCopy().getId());
//								moneyIncome.setAmount(apportionEditor.getModelCopy().getAmount0());
//								if(apportionEditor.getModelCopy().getFriendUserId() != null){
//									moneyIncome.setOwnerUserId(apportionEditor.getModelCopy().getFriendUserId());
//									moneyIncome.setOwnerFriendId(null);
//								} else {
//									moneyIncome.setOwnerUserId("");  // 设为"",使他不会自动使用当前的用户id
//									moneyIncome.setOwnerFriendId(apportionEditor.getModelCopy().getLocalFriendId());
//								}
//								moneyIncome.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
//								moneyIncome.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
//								moneyIncome.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
//								moneyIncome.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFriendUserId());
//								moneyIncome.setLocalFriendId(null);
//								moneyIncome.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
//								moneyIncome.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
//								moneyIncome.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());
//
//								if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
//									MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
//									moneyIncome.setMoneyAccountId(null, moneyAccount.getCurrencyId());
//								} else {
//									moneyIncome.setMoneyAccountId(null, null);
//								}
//								moneyIncome.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
//								moneyIncome.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
//								moneyIncome.setMoneyIncomeCategory(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategory());
//								moneyIncome.setMoneyIncomeCategoryMain(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategoryMain());
//								moneyIncome.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
//								moneyIncome.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
//								moneyIncome.save();
//							}		
//							
////							 if(api.getState() != ApportionItem.UNCHANGED
////										|| !mMoneyIncomeContainerEditor.getModelCopy().getProjectId().equals(mMoneyIncomeContainerEditor.getModel().getProjectId())
////										|| !mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId().equals(mMoneyIncomeContainerEditor.getModel().getMoneyAccountId())) {
//									apportionEditor.save();
////								 }
//								savedCount++;
//					}
//			}
			
			// 从隐藏掉的分摊里面删除原来的分摊
			Iterator<ApportionItem<MoneyApportion>> it = mApportionFieldApportions.getHiddenApportions().iterator();
			while (it.hasNext()) {
				// Get element
				ApportionItem<MoneyApportion> item = it.next();
				if (item.getState() != ApportionItem.NEW) {
					MoneyIncomeApportion apportion = ((MoneyIncomeApportion) item.getApportion());
					MoneyIncomeContainer.deleteApportion(apportion, mMoneyIncomeContainerEditor);
					
//					ProjectShareAuthorization oldProjectShareAuthorization;
////					if(apportion.getFriendUserId() != null){
//						if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//							// 更新旧圈子的分摊支出
//							oldProjectShareAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//						} else {
//							// 更新旧圈子分摊支出
//							oldProjectShareAuthorization = apportion.getProjectShareAuthorization();
//						}
//						
//						HyjModelEditor<ProjectShareAuthorization> oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//						oldProjectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//						oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//						if(!HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//							oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalLend(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalLend() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							oldProjectShareAuthorizationEditor.save();
//					
//							oldProjectShareAuthorization = mMoneyIncomeContainerEditor.getOldSelfProjectShareAuthorization();
//							oldProjectShareAuthorizationEditor = oldProjectShareAuthorization.newModelEditor();
//							oldProjectShareAuthorizationEditor.getModelCopy().setActualTotalBorrow(oldProjectShareAuthorizationEditor.getModelCopy().getActualTotalBorrow() - (apportion.getAmount0() * apportion.getMoneyIncomeContainer().getExchangeRate()));
//							oldProjectShareAuthorizationEditor.save();
//						} else {
//							oldProjectShareAuthorizationEditor.save();
//						}
////					}
//					
//					if(HyjApplication.getInstance().getCurrentUser().getId().equals(apportion.getFriendUserId())){
//						MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
//						if(moneyIncome != null){
//							moneyIncome.delete();
//						}
//					} else {
//						List<MoneyBorrow> moneyBorrows = new Select().from(MoneyBorrow.class).where("moneyIncomeApportionId=?", apportion.getId()).execute();
//						for(MoneyBorrow moneyBorrow : moneyBorrows){
//							moneyBorrow.delete();
//						} 
////						if(apportion.getFriendUserId() != null){
//							List<MoneyLend> moneyLends = new Select().from(MoneyLend.class).where("moneyIncomeApportionId=?", apportion.getId()).execute();
//							for(MoneyLend moneyLend : moneyLends){
//								moneyLend.delete();
//							} 
//							MoneyIncome moneyIncome = new Select().from(MoneyIncome.class).where("moneyIncomeApportionId=?", apportion.getId()).executeSingle();
//							if(moneyIncome != null){
//								moneyIncome.delete();
//							} 
////						}
//					}
//				apportion.delete();
				}
			}
			
			// 如果列表里一个都没有被保存，我们生成一个默认的分摊
			if (savedCount == 0) {
				MoneyIncomeApportion apportion = new MoneyIncomeApportion();
				apportion.setAmount(mMoneyIncomeContainerEditor.getModelCopy().getAmount0());
				apportion.setFriendUserId(HyjApplication.getInstance().getCurrentUser().getId());
				apportion.setMoneyIncomeContainerId(mMoneyIncomeContainerEditor.getModelCopy().getId());
				apportion.setApportionType("Average");
				
				//更新圈子成员的分摊金额
				ProjectShareAuthorization projectShareAuthorization = new Select().from(ProjectShareAuthorization.class).where("projectId=? AND friendUserId=?", 
						mMoneyIncomeContainerEditor.getModelCopy().getProjectId(), apportion.getFriendUserId()).executeSingle();
				HyjModelEditor<ProjectShareAuthorization> projectShareAuthorizationEditor = projectShareAuthorization.newModelEditor();
				projectShareAuthorizationEditor.getModelCopy().setApportionedTotalIncome(projectShareAuthorizationEditor.getModelCopy().getApportionedTotalIncome() + (apportion.getAmount0() * mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate()));
				projectShareAuthorizationEditor.getModelCopy().setActualTotalIncome(projectShareAuthorizationEditor.getModelCopy().getActualTotalIncome() + (apportion.getAmount0() * mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate()));
				projectShareAuthorizationEditor.save();
				
				MoneyIncome moneyIncome = null;
				moneyIncome = new MoneyIncome();
				
				moneyIncome.setMoneyIncomeApportionId(apportion.getId());
				moneyIncome.setAmount(apportion.getAmount0());
				moneyIncome.setDate(mMoneyIncomeContainerEditor.getModelCopy().getDate());
				moneyIncome.setRemark(mMoneyIncomeContainerEditor.getModelCopy().getRemark());
				moneyIncome.setFriendAccountId(mMoneyIncomeContainerEditor.getModelCopy().getFriendAccountId());
				moneyIncome.setFriendUserId(mMoneyIncomeContainerEditor.getModelCopy().getFriendUserId());
				moneyIncome.setLocalFriendId(mMoneyIncomeContainerEditor.getModelCopy().getLocalFriendId());
				moneyIncome.setExchangeRate(mMoneyIncomeContainerEditor.getModelCopy().getExchangeRate());
				moneyIncome.setGeoLat(mMoneyIncomeContainerEditor.getModelCopy().getGeoLat());
				moneyIncome.setGeoLon(mMoneyIncomeContainerEditor.getModelCopy().getGeoLon());

				if(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId() != null){
					MoneyAccount moneyAccount = HyjModel.getModel(MoneyAccount.class, mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId());
					moneyIncome.setMoneyAccountId(mMoneyIncomeContainerEditor.getModelCopy().getMoneyAccountId(), moneyAccount.getCurrencyId());
				} else {
					moneyIncome.setMoneyAccountId(null, null);
				}
				moneyIncome.setLocation(mMoneyIncomeContainerEditor.getModelCopy().getLocation());
				moneyIncome.setAddress(mMoneyIncomeContainerEditor.getModelCopy().getAddress());
				moneyIncome.setMoneyIncomeCategory(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategory());
				moneyIncome.setMoneyIncomeCategoryMain(mMoneyIncomeContainerEditor.getModelCopy().getMoneyIncomeCategoryMain());
				moneyIncome.setPictureId(mMoneyIncomeContainerEditor.getModelCopy().getPictureId());
				moneyIncome.setProject(mMoneyIncomeContainerEditor.getModelCopy().getProject());
				moneyIncome.setEventId(mMoneyIncomeContainerEditor.getModelCopy().getEventId());
				moneyIncome.save();
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
	         		
	         		ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", project.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
					
					if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
						HyjUtil.displayToast(R.string.app_permission_no_addnew);
						return;
					}else if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
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
	         		mApportionFieldApportions.changeProject(project, mSelectorFieldEvent.getModelId(),MoneyIncomeApportion.class);
					mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
					
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
    					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
    					mSelectorFieldEvent.setText(null);
    					mSelectorFieldEvent.setModelId(null);
    					mApportionFieldApportions.changeProject(project,mSelectorFieldEvent.getModelId(), MoneyIncomeApportion.class);
    					mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
    				} else {
	     				Event event = Event.load(Event.class, _id);
	     				ProjectShareAuthorization psa = new Select().from(ProjectShareAuthorization.class).where("projectId = ? AND friendUserId=?", event.getProject().getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
	     				
	     				if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() == null && !psa.getProjectShareMoneyExpenseAddNew()){
	     					HyjUtil.displayToast(R.string.app_permission_no_addnew);
	     					return;
	     				}else if(mMoneyIncomeContainerEditor.getModelCopy().get_mId() != null && !psa.getProjectShareMoneyExpenseEdit()){
	     					HyjUtil.displayToast(R.string.app_permission_no_edit);
	     					return;
	     				}
	     				
	     				mApportionFieldApportions.changeEvent(event.getProject(), event, MoneyIncomeApportion.class);
	    				mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
	
	    				if( event.getFinancialOwnerUserId() != null){
	     					mSelectorFieldFinancialOwner.setModelId(event.getFinancialOwnerUserId());
	     					mSelectorFieldFinancialOwner.setText(Friend.getFriendUserDisplayName(event.getFinancialOwnerUserId()));
	     				} else if( event.getProject().getFinancialOwnerUserId() != null){
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
             case GET_FRIEND_ID:
            	 if(resultCode == Activity.RESULT_OK){
            		long _id = data.getLongExtra("MODEL_ID", -1);

            		if(_id == -1){
     	   	       		mSelectorFieldFriend.setText(null);
     	   	       		mSelectorFieldFriend.setModelId(null);
    				} else {
	            		Friend friend = Friend.load(Friend.class, _id);
	            		
	            		if(HyjApplication.getInstance().getCurrentUser().getId().equals(friend.getFriendUserId())){
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
     		case GET_AMOUNT:
    			if (resultCode == Activity.RESULT_OK) {
    				String calculatorAmount = data.getStringExtra("calculatorAmount");
    				if (calculatorAmount != null){
    					mNumericAmount.setNumber(Double.parseDouble(calculatorAmount));
    				}
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
            	 
             case GET_APPORTION_MEMBER_ID:
     			if (resultCode == Activity.RESULT_OK) {
     				String type = data.getStringExtra("MODEL_TYPE");
     				long _id = data.getLongExtra("MODEL_ID", -1);
     				if(_id != -1){
     					AddApportionMember(type, _id, new long[0]);
     				} else {
    					long[] _ids = data.getLongArrayExtra("MODEL_IDS");
    					if(_ids != null && _ids.length > 0) {
//    						for(int i=0; i<_ids.length; i++){
    							AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
//    						}
    					}
     				}
    			}
    			break;
    		case ADD_AS_PROJECT_MEMBER:
    			if (resultCode == Activity.RESULT_OK) {
    				String id = data.getStringExtra("MODELID");
    				ProjectShareAuthorization psa = HyjModel.getModel(ProjectShareAuthorization.class, id);
    				if(psa != null){
    					addAsProjectMember(psa, "ProjectShareAuthorization", new long[]{});
    				} else {
    					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
    				}
    			} else {
    				HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
    				
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

//		private void AddApportionMember(String type, long _id) {
//			ProjectShareAuthorization psa = null;
//			if("ProjectShareAuthorization".equalsIgnoreCase(type)){
//				psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
//			} else if("EventMember".equalsIgnoreCase(type)){
//				EventMember em = EventMember.load(EventMember.class, _id);
////				if(em.getFriendUserId() != null){
////					psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", em.getFriendUserId(), mSelectorFieldProject.getModelId()).executeSingle();
////				} else {
////					psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", em.getLocalFriendId(), mSelectorFieldProject.getModelId()).executeSingle();
////				}
//				psa = em.getProjectShareAuthorization();
////				psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
//			} else {
//				final Friend friend = Friend.load(Friend.class, _id);
//				if(friend.getFriendUserId() != null){
//					//看一下该好友是不是圈子成员, 如果是，作为圈子成员添加
//					psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=?", friend.getFriendUserId(), mSelectorFieldProject.getModelId()).executeSingle();
//				} else {
//					psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=?", friend.getId(), mSelectorFieldProject.getModelId()).executeSingle();
//				}
//				if(psa == null){
//					((HyjActivity)getActivity()).displayDialog(R.string.moneyApportionField_select_toast_apportion_user_not_member, R.string.moneyApportionField_select_confirm_apportion_add_as_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
//							new DialogCallbackListener() {
//								@Override
//								public void doPositiveClick(Object object) {
//									Bundle bundle = new Bundle();
//									bundle.putString("PROJECTID", mSelectorFieldProject.getModelId());
//									if(friend.getFriendUserId() != null){
//										bundle.putString("FRIEND_USERID", friend.getFriendUserId());
//									} else {
//										bundle.putString("LOCAL_FRIENDID", friend.getId());
//									}
//									openActivityWithFragmentForResult(ProjectMemberFormFragment.class, R.string.memberFormFragment_title_addnew, bundle, ADD_AS_PROJECT_MEMBER);
//								}
//		
//								@Override
//								public void doNegativeClick() {
//									HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
//								}
//							});
//					
////					HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_not_member);
//					return;
//				}
//			}
//			addAsProjectMember(psa);
//		
//	}
	 
	 private void AddApportionMember(final String type, final long _id, final long[] _ids) {
			ProjectShareAuthorization psa = null;
			if("ProjectShareAuthorization".equalsIgnoreCase(type)){
				psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
				if(mSelectorFieldEvent.getModelId() != null){
					final Event event = new Select().from(Event.class).where("id=?", mSelectorFieldEvent.getModelId()).executeSingle();
					EventMember em = null;
					if (psa.getFriendUserId() != null) {
						em = new Select().from(EventMember.class).where("friendUserId=? AND eventId=?", psa.getFriendUserId(), mSelectorFieldEvent.getModelId()).executeSingle();
					} else {
						em = new Select().from(EventMember.class).where("localFriendId=? AND eventId=?", psa.getLocalFriendId(), mSelectorFieldEvent.getModelId()).executeSingle();
					}
					if(em == null){
						final ProjectShareAuthorization psaCopy = psa;
						((HyjActivity)getActivity()).displayDialog(R.string.moneyApportionField_select_toast_apportion_user_not_event_member, R.string.moneyApportionField_select_confirm_apportion_add_as_event_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									Bundle bundle = new Bundle();
									bundle.putLong("EVENT_ID", event.get_mId());
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
				}
			} else if("EventMember".equalsIgnoreCase(type)){
				EventMember em = EventMember.load(EventMember.class, _id);
//				if(em.getFriendUserId() != null){
//					psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", em.getFriendUserId(), mSelectorFieldProject.getModelId()).executeSingle();
//				} else {
//					psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", em.getLocalFriendId(), mSelectorFieldProject.getModelId()).executeSingle();
//				}
				psa = em.getProjectShareAuthorization();
//				psa = ProjectShareAuthorization.load(ProjectShareAuthorization.class, _id);
			} else {
				final Friend friend = Friend.load(Friend.class, _id);
				
				if(mSelectorFieldEvent.getModelId() != null){
					final Event event = new Select().from(Event.class).where("id=?", mSelectorFieldEvent.getModelId()).executeSingle();
					EventMember em = null;
					if (friend.getFriendUserId() != null) {
						em = new Select().from(EventMember.class).where("friendUserId=? AND eventId=?", friend.getFriendUserId(), mSelectorFieldEvent.getModelId()).executeSingle();
					} else {
						em = new Select().from(EventMember.class).where("localFriendId=? AND eventId=?", friend.getId(), mSelectorFieldEvent.getModelId()).executeSingle();
					}
					if(em == null){
						((HyjActivity)getActivity()).displayDialog(R.string.moneyApportionField_select_toast_apportion_user_not_event_member, R.string.moneyApportionField_select_confirm_apportion_add_as_event_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									Bundle bundle = new Bundle();
									bundle.putLong("EVENT_ID", event.get_mId());
									if(friend.getFriendUserId() != null){
										bundle.putString("FRIEND_USERID", friend.getFriendUserId());
									} else {
										bundle.putString("LOCAL_FRIENDID", friend.getId());
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
					} else {
						psa = em.getProjectShareAuthorization();
					}
				} else {
					//看一下该好友是不是圈子成员
					if(friend.getFriendUserId() != null){
						psa = new Select().from(ProjectShareAuthorization.class).where("friendUserId=? AND projectId=? AND state <> 'Delete'", friend.getFriendUserId(), mSelectorFieldProject.getModelId()).executeSingle();
					} else {
						psa = new Select().from(ProjectShareAuthorization.class).where("localFriendId=? AND projectId=? AND state <> 'Delete'", friend.getId(), mSelectorFieldProject.getModelId()).executeSingle();
					}
					
					if(psa == null){
						((HyjActivity)getActivity()).displayDialog(R.string.moneyApportionField_select_toast_apportion_user_not_member, R.string.moneyApportionField_select_confirm_apportion_add_as_member, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
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
						
						return;
					}
				}
			}
			addAsProjectMember(psa, type, _ids);
		}

		private void addAsProjectMember(ProjectShareAuthorization psa, final String type, final long _ids[]){
			MoneyIncomeApportion apportion = new MoneyIncomeApportion();
			apportion.setFriendUserId(psa.getFriendUserId());
			apportion.setLocalFriendId(psa.getLocalFriendId());
			apportion.setAmount(0.0);
			if(psa.getSharePercentageType() != null && psa.getSharePercentageType().equals("Average")){
				apportion.setApportionType("Average");
			} else {
				apportion.setApportionType("Share");
			}
			apportion.setMoneyIncomeContainerId(mMoneyIncomeContainerEditor.getModel().getId());
			if (mApportionFieldApportions.addApportion(apportion,mSelectorFieldProject.getModelId(),mSelectorFieldEvent.getModelId(), ApportionItem.NEW)) {
				mApportionFieldApportions.setTotalAmount(mNumericAmount.getNumber());
			} else {
				HyjUtil.displayToast(R.string.moneyApportionField_select_toast_apportion_user_already_exists);
			}
			if(_ids.length > 0){
				AddApportionMember(type, _ids[0], HyjUtil.arrayTail(_ids));
			}
		}
		
		
		@Override
		public void onDestroy(){
			super.onDestroy();
			mApportionFieldApportions.getAdapter().unregisterDataSetObserver(mApportionCountObserver);
		}
	}
