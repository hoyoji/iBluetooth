package com.hoyoji.hoyoji.money;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.hoyoji.models.Exchange;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.MoneyTransfer;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.Project;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountListFragment;
import com.hoyoji.hoyoji.money.moneyaccount.MoneyAccountTopupListFragment;
import com.hoyoji.hoyoji.project.ProjectListFragment;
import com.hoyoji.hoyoji.event.EventListFragment;
import com.hoyoji.hoyoji.friend.FriendListFragment;


public class MoneyTopupFormFragment extends HyjUserFormFragment {
	private final static int GET_TRANSFEROUT_ID = 2;
	private final static int GET_TRANSFERIN_FRIEND_ID = 3;
	private final static int GET_TRANSFERIN_ID = 4;
	private final static int GET_PROJECT_ID = 5;
	private static final int GET_REMARK = 6;
	private final static int GET_AMOUNT = 8;
	private final static int GET_EVENT_ID = 9;
	private int CREATE_EXCHANGE = 0;
	private int SET_EXCHANGE_RATE_FLAG = 1;
	
	private HyjModelEditor<MoneyTransfer> mMoneyTransferEditor = null;
	private HyjImageField mImageFieldPicture = null;
	private HyjDateTimeField mDateTimeFieldDate = null;
	private HyjNumericField mNumericTransferOutAmount = null;
	private HyjSelectorField mSelectorFieldTransferOut = null;
	private HyjSelectorField mSelectorFieldTransferInFriend = null;
	private HyjSelectorField mSelectorFieldTransferIn = null;
	private HyjNumericField mNumericTransferInAmount = null;
	private View mViewSeparatorTransferInAmount = null;
	private HyjSelectorField mSelectorFieldProject = null;
	private HyjSelectorField mSelectorFieldEvent = null;
	private View mViewSeparatorEvent = null;
	private HyjNumericField mNumericExchangeRate = null;
	private HyjRemarkField mRemarkFieldRemark = null;
	private ImageView mImageViewRefreshRate = null;
	private View mViewSeparatorExchange = null;
	private LinearLayout mLinearLayoutExchangeRate = null;
	
	private LinearLayout mLinearLayoutTransferOutProject = null;
	private LinearLayout mLinearLayoutProjectTransferIn = null;
	
	private Project project = null;
//	private View mViewSeparatorTransferOutProject = null;
	private View mViewSeparatorProjectTransferIn = null;
//	private View mViewSeparatorProjectTransferIn1 = null;
	
	private HyjTextField transferOutCurrency = null;
	private HyjTextField transferProjectCurrency = null;
	
	private HyjTextField projectTransferInCurrency = null;
	private HyjTextField transferInCurrency = null;
	
	private HyjNumericField transferOutProjectExchangeRate = null;
	private HyjNumericField projectTransferInExchangeRate = null;
	
	private ImageView mImageViewTransferOutProjectRefreshRate = null;
	private ImageView mImageViewProjectTransferInRefreshRate = null;
	
	private MoneyTransfer moneyTopup;
	
	private ImageButton calculatorTextView = null;
	
	private ImageButton mButtonExpandMore;
	private LinearLayout mLinearLayoutExpandMore;
	@Override
	public Integer useContentView() {
		return R.layout.money_formfragment_moneytopup;
	}
	 
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		
		
		Intent intent = getActivity().getIntent();
		long modelId = intent.getLongExtra("MODEL_ID", -1);
		
		transferOutCurrency = (HyjTextField) getView().findViewById(R.id.moneyTopupFormFragment_textField_transferOutCurrency);
		transferProjectCurrency = (HyjTextField) getView().findViewById(R.id.moneyTopupFormFragment_textField_transferProjectCurrency);
		projectTransferInCurrency = (HyjTextField) getView().findViewById(R.id.moneyTopupFormFragment_textField_projectTransferInCurrency);
		transferInCurrency = (HyjTextField) getView().findViewById(R.id.moneyTopupFormFragment_textField_transferInCurrency);
		
		
		
//		mViewSeparatorTransferOutProject = (View) getView().findViewById(R.id.field_separator_transferOutProject);
		mLinearLayoutTransferOutProject = (LinearLayout) getView().findViewById(R.id.moneyTopupFormFragment_LinerLayout_transferOutProject);
		
		mViewSeparatorProjectTransferIn = (View) getView().findViewById(R.id.field_separator_projectTransferIn);
//		mViewSeparatorProjectTransferIn1 = (View) getView().findViewById(R.id.field_separator_projectTransferIn1);
		mLinearLayoutProjectTransferIn = (LinearLayout) getView().findViewById(R.id.moneyTopupFormFragment_LinerLayout_projectTransferIn);
		
		if(modelId != -1){
			moneyTopup =  new Select().from(MoneyTransfer.class).where("_id=?", modelId).executeSingle();
			
//			mViewSeparatorTransferOutProject.setVisibility(View.VISIBLE);
			
		} else {
			moneyTopup = new MoneyTransfer();
			moneyTopup.setTransferInId(null);
			moneyTopup.setTransferType("Topup");
			
			final String moneyAccountId = intent.getStringExtra("moneyAccountId");
			if(moneyAccountId != null){
				moneyTopup.setTransferOutId(moneyAccountId);
			}
			
//			String friendUserId = intent.getStringExtra("friendUserId");//从消息导入
//			if(friendUserId != null){
//				moneyTopup.setTransferInFriendUserId(friendUserId);
//				moneyTopup.setTransferOutFriendUserId(friendUserId);
//			} else {
//				String localFriendId = intent.getStringExtra("localFriendId");//从消息导入
//				if(localFriendId != null){
//					moneyTopup.setTransferInLocalFriendId(localFriendId);
//					moneyTopup.setTransferOutLocalFriendId(localFriendId);
//				}
//			}
		}
		mMoneyTransferEditor = moneyTopup.newModelEditor();
		
		setupDeleteButton(mMoneyTransferEditor);
		
		mImageFieldPicture = (HyjImageField) getView().findViewById(R.id.moneyTopupFormFragment_imageField_picture);		
		mImageFieldPicture.setImages(moneyTopup.getPictures());
		
		mDateTimeFieldDate = (HyjDateTimeField) getView().findViewById(R.id.moneyTopupFormFragment_textField_date);	
		if(modelId != -1){
			mDateTimeFieldDate.setTime(moneyTopup.getDate());
		} else {
			long dateInMillisec = intent.getLongExtra("DATE_IN_MILLISEC", -1);
			if(dateInMillisec != -1){
				Date date = new Date(dateInMillisec);
				mDateTimeFieldDate.setDate(date);
				mDateTimeFieldDate.setTextColor(Color.RED);
			}
		}
		
		mNumericTransferOutAmount = (HyjNumericField) getView().findViewById(R.id.moneyTopupFormFragment_textField_transferOutAmount);		
		mNumericTransferOutAmount.setNumber(moneyTopup.getTransferOutAmount());
		mNumericTransferOutAmount.getEditText().addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if(s!= null && s.length()>0 && mNumericExchangeRate.getNumber() != null){
					mNumericTransferInAmount.setNumber(Double.valueOf(s.toString()) * mNumericExchangeRate.getNumber());
				}else{
					mNumericTransferInAmount.setNumber(null);
				}
			}
			
		});
		
		MoneyAccount transferOut = moneyTopup.getTransferOut();
		mSelectorFieldTransferOut = (HyjSelectorField) getView().findViewById(R.id.moneyTopupFormFragment_selectorField_transferOut);

		if(transferOut != null){
			mSelectorFieldTransferOut.setModelId(transferOut.getId());
			mSelectorFieldTransferOut.setText(transferOut.getName() + "(" + transferOut.getCurrencyId() + ")");
			
			transferOutCurrency.setText(transferOut.getCurrency().getName() + "(" + transferOut.getCurrencyId() + ")");
		}
		mSelectorFieldTransferOut.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				
				MoneyTopupFormFragment.this.openActivityWithFragmentForResult(MoneyAccountListFragment.class, R.string.moneyAccountListFragment_title_select_moneyAccount, bundle, GET_TRANSFEROUT_ID);
			}
		});	
		
		Friend transferInFriend = moneyTopup.getTransferInFriend();
		mSelectorFieldTransferInFriend = (HyjSelectorField) getView().findViewById(R.id.moneyTopupFormFragment_selectorField_transferInFriend);
		if(transferInFriend != null){
			mSelectorFieldTransferInFriend.setModelId(transferInFriend.getId());
			mSelectorFieldTransferInFriend.setText(transferInFriend.getDisplayName());
		}
		mSelectorFieldTransferInFriend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("disableMultiChoiceMode", true);
				MoneyTopupFormFragment.this
				.openActivityWithFragmentForResult(FriendListFragment.class, R.string.moneyTopupFormFragment_editText_hint_transferInFriend, bundle, GET_TRANSFERIN_FRIEND_ID);
			}
		}); 
		
		MoneyAccount transferIn = moneyTopup.getTransferIn();
		mSelectorFieldTransferIn = (HyjSelectorField) getView().findViewById(R.id.moneyTopupFormFragment_selectorField_transferIn);

		if(transferIn != null){
			mSelectorFieldTransferIn.setModelId(transferIn.getId());
			mSelectorFieldTransferIn.setText(transferIn.getName() + "(" + transferIn.getCurrencyId() + ")");
			transferInCurrency.setText(transferIn.getCurrency().getName() + "(" + transferIn.getCurrencyId() + ")");
		}
		mSelectorFieldTransferIn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectorFieldTransferInFriend.getModelId() == null){
					HyjUtil.displayToast(R.string.moneyTopupFormFragment_editText_hint_transferInFriend);
					return;
				} 
				Bundle bundle = new Bundle();
				bundle.putString("excludeType", "Debt");
				bundle.putString("friendId", mSelectorFieldTransferInFriend.getModelId());
				bundle.putString("friendDisplayName", mSelectorFieldTransferInFriend.getText());
				
				MoneyTopupFormFragment.this.openActivityWithFragmentForResult(MoneyAccountTopupListFragment.class, R.string.moneyTopupFormFragment_editText_hint_transferIn, bundle, GET_TRANSFERIN_ID);
			}
		});	
		
		mNumericTransferInAmount = (HyjNumericField) getView().findViewById(R.id.moneyTopupFormFragment_textField_transferInAmount);		
		mNumericTransferInAmount.setNumber(moneyTopup.getTransferInAmount());
		mNumericTransferInAmount.setEnabled(false);
		
		mViewSeparatorTransferInAmount = (View) getView().findViewById(R.id.field_separator_exchangeRate);
		
		String projectId = intent.getStringExtra("projectId");//从消息导入
		if(moneyTopup.get_mId() == null && projectId != null){
			project = HyjModel.getModel(Project.class, projectId);
		}else{
			project = moneyTopup.getProject();
		}
		mSelectorFieldProject = (HyjSelectorField) getView().findViewById(R.id.moneyTopupFormFragment_selectorField_project);
		
		if(project != null){
			mSelectorFieldProject.setModelId(project.getId());
			mSelectorFieldProject.setText(project.getDisplayName());
			transferProjectCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
			projectTransferInCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
		}
		mSelectorFieldProject.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				MoneyTopupFormFragment.this.openActivityWithFragmentForResult(ProjectListFragment.class, R.string.moneyTopupFormFragment_editText_hint_project, null, GET_PROJECT_ID);
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
			if(moneyTopup.get_mId() == null){
				if(eventId != null) {
					moneyTopup.setEventId(eventId);
					event = HyjModel.getModel(Event.class, eventId);
				} else if(project.getActiveEventId() != null){
					moneyTopup.setEventId(project.getActiveEventId());
					event = HyjModel.getModel(Event.class, project.getActiveEventId());
				}
			}else{
				event = moneyTopup.getEvent();
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

                    MoneyTopupFormFragment.this.openActivityWithFragmentForResult(EventListFragment.class, R.string.projectEventFormFragment_action_select, bundle, GET_EVENT_ID);

                    //				MoneyExpenseContainerFormFragment.this.openActivityWithFragmentForResult(
                    //								ProjectEventListFragment.class,
                    //								R.string.projectListFragment_title_select_project,
                    //								null, GET_PROJECT_ID);
                }
            }
        });
		mNumericExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyTopupFormFragment_textField_exchangeRate);	
		transferOutProjectExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyTopupFormFragment_textField_transferOutProjectExchangeRate);
		projectTransferInExchangeRate = (HyjNumericField) getView().findViewById(R.id.moneyTopupFormFragment_textField_projectTransferInExchangeRate);
		mNumericExchangeRate.setNumber(moneyTopup.getExchangeRate());
		mNumericExchangeRate.getEditText().addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(mNumericExchangeRate.getEditText().isFocused()){
					if(s!= null && s.length()>0 && mNumericTransferOutAmount.getNumber() != null){
						mNumericTransferInAmount.setNumber(Double.valueOf(s.toString()) * mNumericTransferOutAmount.getNumber());
						if(transferOutProjectExchangeRate.getNumber()!=null){
							projectTransferInExchangeRate.setNumber(transferOutProjectExchangeRate.getNumber() / Double.valueOf(s.toString()));
						}
					}else{
						mNumericTransferInAmount.setNumber(null);
					}
				}
			}
			
		});
		
		transferOutProjectExchangeRate.getEditText().addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s!= null && s.length()>0 && mNumericTransferOutAmount.getNumber() != null){
					if(transferOutProjectExchangeRate.getEditText().isFocused()){
						if(transferOutProjectExchangeRate.getNumber() != null && mNumericExchangeRate.getNumber() != null) {
							projectTransferInExchangeRate.setNumber(transferOutProjectExchangeRate.getNumber() / mNumericExchangeRate.getNumber());
						}
					}
				}
			}
			
		});
		
		projectTransferInExchangeRate.getEditText().addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(s!= null && s.length()>0 && mNumericTransferOutAmount.getNumber() != null){
					if(projectTransferInExchangeRate.getEditText().isFocused()){
						if(projectTransferInExchangeRate.getNumber() != null && mNumericExchangeRate.getNumber() != null) {
							transferOutProjectExchangeRate.setNumber(mNumericExchangeRate.getNumber() * projectTransferInExchangeRate.getNumber());
						}
					}
				}
			}
			
		});
		
		mViewSeparatorExchange = (View) getView().findViewById(R.id.moneyTopupFormFragment_separatorField_exchange);
		mLinearLayoutExchangeRate = (LinearLayout) getView().findViewById(R.id.moneyTopupFormFragment_linearLayout_exchangeRate);
		
		mRemarkFieldRemark = (HyjRemarkField) getView().findViewById(R.id.moneyTopupFormFragment_textField_remark);
		mRemarkFieldRemark.setText(moneyTopup.getRemark());
		mRemarkFieldRemark.setEditable(false);
		mRemarkFieldRemark.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("TEXT", mRemarkFieldRemark.getText());
				bundle.putString("HINT", "请输入" + mRemarkFieldRemark.getLabelText());
				MoneyTopupFormFragment.this
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
				bundle.putDouble("AMOUNT", mNumericTransferOutAmount.getNumber()!=null?mNumericTransferOutAmount.getNumber():0.00);
				MoneyTopupFormFragment.this.openActivityWithFragmentForResult(
								HyjCalculatorFormFragment.class,
								R.string.hyjCalculatorFormFragment_title,
								bundle, GET_AMOUNT);
			}
		});
		
		ImageView takePictureButton = (ImageView) getView().findViewById(R.id.moneyTopupFormFragment_imageView_camera);	
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
				
				if(mSelectorFieldTransferInFriend.getModelId() == null && (mSelectorFieldTransferOut.getModelId()== null || mSelectorFieldTransferIn.getModelId() == null)){
					for(int i = 0; i<popup.getMenu().size();i++){
						popup.getMenu().setGroupEnabled(i, false);
					}
				}
				
				popup.show();	
			}
		});
		
		mImageViewRefreshRate = (ImageView) getView().findViewById(R.id.moneyTopupFormFragment_imageButton_refresh_exchangeRate);	
		mImageViewRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectorFieldTransferOut.getModelId() != null && mSelectorFieldTransferIn.getModelId() != null){
					MoneyAccount transferOut = HyjModel.getModel(MoneyAccount.class, mSelectorFieldTransferOut.getModelId());
					MoneyAccount transferIn = HyjModel.getModel(MoneyAccount.class, mSelectorFieldTransferIn.getModelId());
					
					String fromCurrency = transferOut.getCurrencyId();
					String toCurrency = transferIn.getCurrencyId();
					if(fromCurrency != null && toCurrency != null){
						HyjUtil.startRoateView(mImageViewRefreshRate);
						mImageViewRefreshRate.setEnabled(false);
						HyjUtil.updateExchangeRate(fromCurrency, toCurrency, mImageViewRefreshRate, mNumericExchangeRate);
					} else {
						HyjUtil.displayToast(R.string.moneyTopupFormFragment_toast_select_currency);
					}
				}else{
					HyjUtil.displayToast(R.string.moneyTopupFormFragment_toast_select_currency);
				}
			}
		});
		
		mImageViewTransferOutProjectRefreshRate = (ImageView) getView().findViewById(R.id.moneyTopupFormFragment_imageButton_refresh_transferOutProjectExchangeRate);	
		mImageViewTransferOutProjectRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(mSelectorFieldTransferOut.getModelId() != null && mSelectorFieldProject.getModelId() != null){
					HyjUtil.startRoateView(mImageViewRefreshRate);
					MoneyAccount transferOut = HyjModel.getModel(MoneyAccount.class, mSelectorFieldTransferOut.getModelId());
					Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
					
					transferOutProjectExchangeRate.setNumber(transferExchangeRate(transferOut.getCurrencyId(),project.getCurrencyId()));
					
					HyjUtil.stopRoateView(mImageViewRefreshRate);
				}else{
					HyjUtil.displayToast(R.string.moneyTopupFormFragment_toast_select_currency);
				}
			}
		});
		
		
		mImageViewProjectTransferInRefreshRate = (ImageView) getView().findViewById(R.id.moneyTopupFormFragment_imageButton_refresh_projectTransferInExchangeRate);	
		mImageViewProjectTransferInRefreshRate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(transferOutProjectExchangeRate.getNumber() != null && mNumericExchangeRate.getNumber() != null) {
					projectTransferInExchangeRate.setNumber(transferOutProjectExchangeRate.getNumber() / mNumericExchangeRate.getNumber());
				} else {
					if(mSelectorFieldProject.getModelId() != null && mSelectorFieldTransferIn.getModelId() != null){
						HyjUtil.startRoateView(mImageViewRefreshRate);
						Project project = HyjModel.getModel(Project.class, mSelectorFieldProject.getModelId());
						MoneyAccount transferIn = HyjModel.getModel(MoneyAccount.class, mSelectorFieldTransferIn.getModelId());
						
						projectTransferInExchangeRate.setNumber(1/transferExchangeRate(project.getCurrencyId(),transferIn.getCurrencyId()));
						HyjUtil.stopRoateView(mImageViewRefreshRate);
					}else{
						HyjUtil.displayToast(R.string.moneyTopupFormFragment_toast_select_currency);
					}
				}
				
			}
		});
		
		mLinearLayoutExpandMore = (LinearLayout)getView().findViewById(R.id.moneyExpenseContainerFormFragment_expandMore);
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
		
		setPermission();
		
		// 只在新增时才自动打开软键盘， 修改时不自动打开
		if (modelId == -1) {
			setExchangeRate(false);
			this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}else{
//			setExchangeRate(true);
			if(moneyTopup.getTransferIn() != null){
				if (!moneyTopup.getTransferIn().getCurrencyId().equals(moneyTopup.getProject().getCurrencyId())) {
					transferInCurrency.setText(moneyTopup.getTransferIn().getCurrency().getName() + "(" + moneyTopup.getTransferIn().getCurrencyId() + ")");
					mViewSeparatorProjectTransferIn.setVisibility(View.VISIBLE);
	//				mViewSeparatorProjectTransferIn1.setVisibility(View.VISIBLE);
					mLinearLayoutProjectTransferIn.setVisibility(View.VISIBLE);
				}
			}
			if(moneyTopup.getTransferOut() != null){
				if (!moneyTopup.getTransferOut().getCurrencyId().equals(moneyTopup.getProject().getCurrencyId())) {
					transferOutCurrency.setText(moneyTopup.getTransferOut().getCurrency().getName() + "(" + moneyTopup.getTransferOut().getCurrencyId() + ")");
					mLinearLayoutTransferOutProject.setVisibility(View.VISIBLE);
				}
			} else if(moneyTopup.getTransferIn() != null){
				mViewSeparatorProjectTransferIn.setVisibility(View.GONE);
			}
			if (moneyTopup.getTransferOut() == null || moneyTopup.getTransferIn() == null) {
				mViewSeparatorTransferInAmount.setVisibility(View.GONE);
				mLinearLayoutExchangeRate.setVisibility(View.GONE);
			} else {
				if (moneyTopup.getTransferOut().getCurrencyId().equals(moneyTopup.getTransferIn().getCurrencyId())) {
					mViewSeparatorProjectTransferIn.setVisibility(View.GONE);
					mLinearLayoutProjectTransferIn.setVisibility(View.GONE);
					mLinearLayoutTransferOutProject.setVisibility(View.GONE);
					mViewSeparatorTransferInAmount.setVisibility(View.GONE);
					mViewSeparatorExchange.setVisibility(View.GONE);
					mLinearLayoutExchangeRate.setVisibility(View.GONE);
				}
			}
			
//			mLinearLayoutTransferOutProject.setVisibility(View.VISIBLE);
//			mViewSeparatorProjectTransferIn.setVisibility(View.VISIBLE);
//			mViewSeparatorProjectTransferIn1.setVisibility(View.VISIBLE);
//			mLinearLayoutProjectTransferIn.setVisibility(View.VISIBLE);
			transferOutProjectExchangeRate.setNumber(moneyTopup.getTransferOutExchangeRate());
			projectTransferInExchangeRate.setNumber(moneyTopup.getTransferInExchangeRate());
			transferOutCurrency.setText(moneyTopup.getTransferOut().getCurrency().getName() + "(" + moneyTopup.getTransferOut().getCurrencyId() + ")");
			transferProjectCurrency.setText(moneyTopup.getProject().getCurrency().getName() + "(" + moneyTopup.getProject().getCurrencyId() + ")");
			projectTransferInCurrency.setText(moneyTopup.getProject().getCurrency().getName() + "(" + moneyTopup.getProject().getCurrencyId() + ")");
			transferInCurrency.setText(moneyTopup.getTransferIn().getCurrency().getName() + "(" + moneyTopup.getTransferIn().getCurrencyId() + ")");
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    if(mMoneyTransferEditor!= null && mMoneyTransferEditor.getModelCopy().get_mId() != null && (mSelectorFieldTransferInFriend.getModelId() == null 
				&& (mSelectorFieldTransferOut.getModelId()== null || mSelectorFieldTransferIn.getModelId() == null))){
	    	hideSaveAction();
	    }
	}
	
	private void setPermission() {
		transferOutCurrency.setEnabled(false);
		transferProjectCurrency.setEnabled(false);
		projectTransferInCurrency.setEnabled(false);
		transferInCurrency.setEnabled(false);
//		mSelectorFieldEvent.setEnabled(false);
//		if(mSelectorFieldTransferInFriend.getModelId() == null 
//				&& (mSelectorFieldTransferOut.getModelId()== null || mSelectorFieldTransferIn.getModelId() == null)){
//			
//			mDateTimeFieldDate.setEnabled(false);
//			mNumericTransferOutAmount.setEnabled(false);
//			mSelectorFieldTransferOut.setEnabled(false);
//			mNumericTransferInAmount.setEnabled(false);
//			mSelectorFieldTransferInFriend.setEnabled(false);
//			mSelectorFieldTransferIn.setEnabled(false);
//			mNumericExchangeRate.setEnabled(false);
//			mSelectorFieldProject.setEnabled(false);
//			mRemarkFieldRemark.setEnabled(false);
//			
//			if(this.mOptionsMenu != null){
//		    	hideSaveAction();
//			}
//			
////			if(mSelectorFieldTransferIn.getModelId() == null){
////				mSelectorFieldTransferInFriend.setText("请选择商家好友");
////			}
//		} 
//		else if(mSelectorFieldTransferInFriend.getModelId() != null){
//     		mViewSeparatorTransferIn.setVisibility(View.GONE);
//     		mSelectorFieldTransferIn.setVisibility(View.GONE);
//		}
	}

	private void setupDeleteButton(HyjModelEditor<MoneyTransfer> moneyTopupEditor) {

		Button buttonDelete = (Button) getView().findViewById(R.id.button_delete);
		
		final MoneyTransfer moneyTopup = moneyTopupEditor.getModelCopy();
		
		if (moneyTopup.get_mId() == null) {
			buttonDelete.setVisibility(View.GONE);
		} else {
			buttonDelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((HyjActivity)getActivity()).displayDialog(R.string.app_action_delete_list_item, R.string.app_confirm_delete, R.string.alert_dialog_yes, R.string.alert_dialog_no, -1,
							new DialogCallbackListener() {
								@Override
								public void doPositiveClick(Object object) {
									try {
										ActiveAndroid.beginTransaction();

										MoneyAccount transferOut = moneyTopup.getTransferOut();
										MoneyAccount transferIn = moneyTopup.getTransferIn();
										
										if(transferOut != null){
											HyjModelEditor<MoneyAccount> transferOutEditor = transferOut.newModelEditor();
											transferOutEditor.getModelCopy().setCurrentBalance(transferOut.getCurrentBalance() + moneyTopup.getTransferOutAmount());
											transferOutEditor.save();
										}
										if(transferIn != null){
											HyjModelEditor<MoneyAccount> transferInEditor = transferIn.newModelEditor();
											transferInEditor.getModelCopy().setCurrentBalance(transferIn.getCurrentBalance() - moneyTopup.getTransferInAmount());
											transferInEditor.save();
										}
										
										moneyTopup.delete();

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
				}
			});
		}
		
	}
	
	private void setExchangeRate(Boolean editInit){
        if(project == null){
            return;
        }
		if(mSelectorFieldTransferOut.getModelId() != null && mSelectorFieldTransferIn.getModelId()!= null){
			MoneyAccount transferOut = HyjModel.getModel(MoneyAccount.class,mSelectorFieldTransferOut.getModelId());
			MoneyAccount transferIn = HyjModel.getModel(MoneyAccount.class,mSelectorFieldTransferIn.getModelId());
			
			String fromCurrency = transferOut.getCurrencyId();
			String toCurrency = transferIn.getCurrencyId();
			
			if(fromCurrency.equals(toCurrency)){
				if(SET_EXCHANGE_RATE_FLAG != 1){//新增或修改打开时不做setNumber
					mNumericExchangeRate.setNumber(1.00);
					CREATE_EXCHANGE = 0;
				}
				mViewSeparatorExchange.setVisibility(View.GONE);
				mLinearLayoutExchangeRate.setVisibility(View.GONE);
				mViewSeparatorTransferInAmount.setVisibility(View.GONE);
				mNumericTransferInAmount.setVisibility(View.GONE);
			}else{
				mViewSeparatorExchange.setVisibility(View.VISIBLE);
				mLinearLayoutExchangeRate.setVisibility(View.VISIBLE);
				mViewSeparatorTransferInAmount.setVisibility(View.VISIBLE);
				mNumericTransferInAmount.setVisibility(View.VISIBLE);
				
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
			
			transferOutProjectExchangeRate.setNumber(transferExchangeRate(transferOut.getCurrencyId(),project.getCurrencyId()));
			
			projectTransferInExchangeRate.setNumber(1/transferExchangeRate(project.getCurrencyId(),transferIn.getCurrencyId()));

			mNumericExchangeRate.setNumber(transferOutProjectExchangeRate.getNumber() / projectTransferInExchangeRate.getNumber());
			
			transferOutCurrency.setText(transferOut.getCurrency().getName() + "(" + transferOut.getCurrencyId() + ")");
			transferProjectCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
			projectTransferInCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
			transferInCurrency.setText(transferIn.getCurrency().getName() + "(" + transferIn.getCurrencyId() + ")");
			
		}else{
			mViewSeparatorExchange.setVisibility(View.GONE);
			mLinearLayoutExchangeRate.setVisibility(View.GONE);
			mViewSeparatorTransferInAmount.setVisibility(View.GONE);
			mNumericTransferInAmount.setVisibility(View.GONE);
			
			if (mSelectorFieldTransferOut.getModelId() != null){
				MoneyAccount transferOut = HyjModel.getModel(MoneyAccount.class,mSelectorFieldTransferOut.getModelId());
				transferOutProjectExchangeRate.setNumber(transferExchangeRate(transferOut.getCurrencyId(),project.getCurrencyId()));
				
				transferOutCurrency.setText(transferOut.getCurrency().getName() + "(" + transferOut.getCurrencyId() + ")");
				transferProjectCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
			} else {
				transferOutProjectExchangeRate.setNumber(null);
				transferOutCurrency.setText(null);
				transferProjectCurrency.setText(null);
//				mViewSeparatorTransferOutProject.setVisibility(View.GONE);
				mLinearLayoutTransferOutProject.setVisibility(View.GONE);
				mViewSeparatorProjectTransferIn.setVisibility(View.GONE);
			}
			
			if(mSelectorFieldTransferIn.getModelId() != null){
				MoneyAccount transferIn = HyjModel.getModel(MoneyAccount.class,mSelectorFieldTransferIn.getModelId());
				projectTransferInExchangeRate.setNumber(1/transferExchangeRate(project.getCurrencyId(),transferIn.getCurrencyId()));
				
				projectTransferInCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
				transferInCurrency.setText(transferIn.getCurrency().getName() + "(" + transferIn.getCurrencyId() + ")");
			} else {
				projectTransferInExchangeRate.setNumber(null);
				projectTransferInCurrency.setText(null);
				transferInCurrency.setText(null);
				mViewSeparatorProjectTransferIn.setVisibility(View.GONE);
//				mViewSeparatorProjectTransferIn1.setVisibility(View.GONE);
				mLinearLayoutProjectTransferIn.setVisibility(View.GONE);
			}
			mNumericExchangeRate.setNumber(null);
		}
			SET_EXCHANGE_RATE_FLAG = 0;
			setTransferInAmount();
	}
	
	private void setTransferInAmount(){
		if(mNumericTransferOutAmount.getNumber() != null && mNumericExchangeRate.getNumber() != null){
			mNumericTransferInAmount.setNumber(mNumericTransferOutAmount.getNumber() * mNumericExchangeRate.getNumber());
		}else{
			mNumericTransferInAmount.setNumber(mNumericTransferOutAmount.getNumber());
		}
	}
	
	private void fillData(){
		MoneyTransfer modelCopy = (MoneyTransfer) mMoneyTransferEditor.getModelCopy();
		modelCopy.setDate(mDateTimeFieldDate.getTime());
		modelCopy.setTransferOutAmount(mNumericTransferOutAmount.getNumber());
		
		modelCopy.setTransferOutId(mSelectorFieldTransferOut.getModelId());
		modelCopy.setTransferOutFriend(null);
		
		modelCopy.setTransferInId(mSelectorFieldTransferIn.getModelId());
		
		Friend transferInFriend = null;
		if(mSelectorFieldTransferInFriend.getModelId() != null){
			transferInFriend = HyjModel.getModel(Friend.class, mSelectorFieldTransferInFriend.getModelId());
		}
		modelCopy.setTransferInFriend(transferInFriend);
		
		modelCopy.setTransferInAmount(mNumericTransferInAmount.getNumber());
		modelCopy.setProjectId(mSelectorFieldProject.getModelId());
		modelCopy.setProjectCurrencyId(project.getCurrencyId());
		modelCopy.setEventId(mSelectorFieldEvent.getModelId());
		modelCopy.setExchangeRate(mNumericExchangeRate.getNumber());
		modelCopy.setTransferOutExchangeRate(transferOutProjectExchangeRate.getNumber());
		modelCopy.setTransferInExchangeRate(projectTransferInExchangeRate.getNumber());
		modelCopy.setRemark(mRemarkFieldRemark.getText().toString().trim());
	}
	
	private void showValidatioErrors(){
		HyjUtil.displayToast(R.string.app_validation_error);
		mDateTimeFieldDate.setError(mMoneyTransferEditor.getValidationError("datetime"));
		mNumericTransferOutAmount.setError(mMoneyTransferEditor.getValidationError("transferOutAmount"));
		if(mMoneyTransferEditor.getValidationError("transferOutAmount") != null){
			mNumericTransferOutAmount.showSoftKeyboard();
		}
		mSelectorFieldTransferOut.setError(mMoneyTransferEditor.getValidationError("transferOut"));
		mSelectorFieldTransferInFriend.setError(mMoneyTransferEditor.getValidationError("transferInFriend"));
		mSelectorFieldTransferIn.setError(mMoneyTransferEditor.getValidationError("transferIn"));
		mNumericTransferInAmount.setError(mMoneyTransferEditor.getValidationError("transferInAmount"));
		mSelectorFieldProject.setError(mMoneyTransferEditor.getValidationError("project"));
		mSelectorFieldEvent.setError(mMoneyTransferEditor.getValidationError("Event"));
		mNumericExchangeRate.setError(mMoneyTransferEditor.getValidationError("exchangeRate"));
		mRemarkFieldRemark.setError(mMoneyTransferEditor.getValidationError("remark"));
	}

	 @Override
	public void onSave(View v){
		super.onSave(v);
		
		fillData();
		
//		mMoneyTransferEditor.validate();
		if(mMoneyTransferEditor.getModelCopy().getDate() == null){
			mMoneyTransferEditor.setValidationError("date",R.string.moneyTopupFormFragment_editText_hint_date);
		}else{
			mMoneyTransferEditor.removeValidationError("date");
		}
		
		if(mMoneyTransferEditor.getModelCopy().getTransferOutAmount() == null){
			mMoneyTransferEditor.setValidationError("transferOutAmount",R.string.moneyTopupFormFragment_editText_hint_transferOutAmount);
		}else if(mMoneyTransferEditor.getModelCopy().getTransferOutAmount() < 0){
			mMoneyTransferEditor.setValidationError("transferOutAmount",R.string.moneyTopupFormFragment_editText_validationError_negative_transferOutAmount);
		}else if(mMoneyTransferEditor.getModelCopy().getTransferOutAmount() > 99999999){
			mMoneyTransferEditor.setValidationError("transferOutAmount",R.string.moneyTopupFormFragment_editText_validationError_beyondMAX_transferOutAmount);
		}else{
			mMoneyTransferEditor.removeValidationError("transferOutAmount");
		}
		
		if(mMoneyTransferEditor.getModelCopy().getTransferInFriendUserId() == null
			&& mMoneyTransferEditor.getModelCopy().getTransferInLocalFriendId() == null){
			mMoneyTransferEditor.setValidationError("transferInFriend", R.string.moneyTopupFormFragment_editText_hint_transferInFriend);
		} else {
			mMoneyTransferEditor.removeValidationError("transferInFriend");
		}
		
		if(mMoneyTransferEditor.getModelCopy().getTransferOutId() != null &&
				mMoneyTransferEditor.getModelCopy().getTransferOutId().equalsIgnoreCase(mMoneyTransferEditor.getModelCopy().getTransferInId())){
			mMoneyTransferEditor.setValidationError("transferIn", R.string.moneyTopupFormFragment_editText_validationError_same_account);
			mMoneyTransferEditor.setValidationError("transferOut", R.string.moneyTopupFormFragment_editText_validationError_same_account);
		} else {
//			if(mMoneyTransferEditor.getModelCopy().getTransferInId() == null){
//				mMoneyTransferEditor.setValidationError("transferIn", R.string.moneyTopupFormFragment_editText_hint_transferIn);
//			}else{
//				mMoneyTransferEditor.removeValidationError("transferIn");
//			}
			if(mMoneyTransferEditor.getModelCopy().getTransferOutId() == null){
				mMoneyTransferEditor.setValidationError("transferOut", R.string.moneyTopupFormFragment_editText_hint_transferOut);
			}else{
				mMoneyTransferEditor.removeValidationError("transferOut");
			}
		}
		
		if(mMoneyTransferEditor.getModelCopy().getProjectId() == null){
			mMoneyTransferEditor.setValidationError("project", R.string.moneyTopupFormFragment_editText_hint_project);
		}else{
			mMoneyTransferEditor.removeValidationError("project");
		}
				
//		if(mMoneyTransferEditor.getModelCopy().getExchangeRate() == null){
//			mMoneyTransferEditor.setValidationError("exchangeRate",R.string.moneyTopupFormFragment_editText_hint_exchangeRate);
//		}else if(mMoneyTransferEditor.getModelCopy().getExchangeRate() == 0){
//			mMoneyTransferEditor.setValidationError("exchangeRate",R.string.moneyTopupFormFragment_editText_validationError_zero_exchangeRate);
//		}else if(mMoneyTransferEditor.getModelCopy().getExchangeRate() < 0){
//			mMoneyTransferEditor.setValidationError("exchangeRate",R.string.moneyTopupFormFragment_editText_validationError_negative_exchangeRate);
//		}else if(mMoneyTransferEditor.getModelCopy().getExchangeRate() > 99999999){
//			mMoneyTransferEditor.setValidationError("exchangeRate",R.string.moneyTopupFormFragment_editText_validationError_beyondMAX_exchangeRate);
//		}
//		else{
//			mMoneyTransferEditor.removeValidationError("exchangeRate");
//		}
		
		
		if(mMoneyTransferEditor.hasValidationErrors()){
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
						newPic.setRecordId(mMoneyTransferEditor.getModel().getId());
						newPic.setRecordType("MoneyTransfer");
						newPic.setProjectId(mMoneyTransferEditor.getModelCopy().getProjectId());
						newPic.setDisplayOrder(i);
						newPic.save();
					} else if(pi.getState() == PictureItem.DELETED){
						pi.getPicture().delete();
					} else if(pi.getState() == PictureItem.CHANGED){

					}
					if (!mainPicSet && pi.getPicture() != null && pi.getState() != PictureItem.DELETED) {
						mainPicSet = true;
						mMoneyTransferEditor.getModelCopy().setPicture(pi.getPicture());
					}
				}
				
				MoneyTransfer oldMoneyTransferModel = mMoneyTransferEditor.getModel();
				MoneyTransfer moneyTopupModel = mMoneyTransferEditor.getModelCopy();

				if(CREATE_EXCHANGE == 1){
					MoneyAccount transferOut = moneyTopupModel.getTransferOut();
					MoneyAccount transferIn = moneyTopupModel.getTransferIn();
					
					Exchange newExchange = new Exchange();
					newExchange.setLocalCurrencyId(transferOut.getCurrencyId());
					newExchange.setForeignCurrencyId(transferIn.getCurrencyId());
					newExchange.setRate(moneyTopupModel.getExchangeRate());
					newExchange.setOwnerUserId(HyjApplication.getInstance().getCurrentUser().getId());
					newExchange.save();
				}else if(moneyTopupModel.getTransferOut() != null && moneyTopupModel.getTransferIn() != null){
					String localCurrencyId = moneyTopupModel.getTransferOut().getCurrencyId();
					String foreignCurrencyId = moneyTopupModel.getTransferIn().getCurrencyId();
					if(!localCurrencyId.equalsIgnoreCase(foreignCurrencyId)){
						Exchange exchange = Exchange.getExchange(localCurrencyId, foreignCurrencyId);
						Double rate = HyjUtil.toFixed2(moneyTopupModel.getExchangeRate());
						if(exchange != null){
							if(exchange.getRate() != rate){
								HyjModelEditor<Exchange> exchangModelEditor = exchange.newModelEditor();
								exchangModelEditor.getModelCopy().setRate(rate);
								exchangModelEditor.save();
							}
						}
					}
				}
				
				MoneyAccount newTransferOut = moneyTopupModel.getTransferOut();
				MoneyAccount newTransferIn = moneyTopupModel.getTransferIn();
				
				if(moneyTopupModel.get_mId() == null){
				    if(newTransferOut != null){
				    	HyjModelEditor<MoneyAccount> newTransferOutEditor = newTransferOut.newModelEditor();
				    	newTransferOutEditor.getModelCopy().setCurrentBalance(newTransferOut.getCurrentBalance() - moneyTopupModel.getTransferOutAmount0());
				    	newTransferOutEditor.save();
				    }
				    if(newTransferIn != null){
						HyjModelEditor<MoneyAccount> newTransferInEditor = newTransferIn.newModelEditor();
				    	newTransferInEditor.getModelCopy().setCurrentBalance(newTransferIn.getCurrentBalance() + moneyTopupModel.getTransferInAmount0());
				    	newTransferInEditor.save();
				    }
				}else{
					MoneyAccount oldTransferOut = oldMoneyTransferModel.getTransferOut();
					MoneyAccount oldTransferIn = oldMoneyTransferModel.getTransferIn();
					
					if(oldTransferOut != null && newTransferOut != null && oldTransferOut.getId().equals(newTransferOut.getId())){
						HyjModelEditor<MoneyAccount> newTransferOutEditor = newTransferOut.newModelEditor();
						newTransferOutEditor.getModelCopy().setCurrentBalance(newTransferOut.getCurrentBalance() + oldMoneyTransferModel.getTransferOutAmount0() - moneyTopupModel.getTransferOutAmount0());
						newTransferOutEditor.save();
					}else{
						if(oldTransferOut != null){
							HyjModelEditor<MoneyAccount> oldTransferOutEditor = oldTransferOut.newModelEditor();
							oldTransferOutEditor.getModelCopy().setCurrentBalance(oldTransferOut.getCurrentBalance() + oldMoneyTransferModel.getTransferOutAmount0());
							oldTransferOutEditor.save();
						}
						if(newTransferOut != null){
							HyjModelEditor<MoneyAccount> newTransferOutEditor = newTransferOut.newModelEditor();
							newTransferOutEditor.getModelCopy().setCurrentBalance(newTransferOut.getCurrentBalance() - moneyTopupModel.getTransferOutAmount0());
							newTransferOutEditor.save();
						}
					}
					
					if(oldTransferIn != null && newTransferIn != null && oldTransferIn.getId().equals(newTransferIn.getId())){
						HyjModelEditor<MoneyAccount> newTransferInEditor = newTransferIn.newModelEditor();
						newTransferInEditor.getModelCopy().setCurrentBalance(newTransferIn.getCurrentBalance() - oldMoneyTransferModel.getTransferInAmount0() + moneyTopupModel.getTransferInAmount0());
						newTransferInEditor.save();
					}else{
						if(oldTransferIn != null){
							HyjModelEditor<MoneyAccount> oldTransferInEditor = oldTransferIn.newModelEditor();
							oldTransferInEditor.getModelCopy().setCurrentBalance(oldTransferIn.getCurrentBalance() - oldMoneyTransferModel.getTransferInAmount0());
							oldTransferInEditor.save();
						}
						if(newTransferIn != null){
							HyjModelEditor<MoneyAccount> newTransferInEditor = newTransferIn.newModelEditor();
							newTransferInEditor.getModelCopy().setCurrentBalance(newTransferIn.getCurrentBalance() + moneyTopupModel.getTransferInAmount0());
							newTransferInEditor.save();
						}
					}
				}
				if(newTransferIn == null && mSelectorFieldTransferIn.getText() != null){
					newTransferIn = new MoneyAccount();
					newTransferIn.setName(mSelectorFieldTransferIn.getText());
					newTransferIn.setLocalFriendId(mMoneyTransferEditor.getModelCopy().getTransferInFriend().getId());
					newTransferIn.setAccountType("Topup");
					newTransferIn.setCurrencyId(mMoneyTransferEditor.getModelCopy().getTransferOut().getCurrencyId());
					newTransferIn.setCurrentBalance(mMoneyTransferEditor.getModelCopy().getTransferInAmount0());
					newTransferIn.save();
					mMoneyTransferEditor.getModelCopy().setTransferInId(newTransferIn.getId());
				}
				mMoneyTransferEditor.save();
				ActiveAndroid.setTransactionSuccessful();
				HyjUtil.displayToast(R.string.app_save_success);
				getActivity().finish();
			} finally {
			    ActiveAndroid.endTransaction();
			}
		}
	}	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
         switch(requestCode){
             case GET_TRANSFEROUT_ID:
	        	 if(resultCode == Activity.RESULT_OK){
	         		long _id = data.getLongExtra("MODEL_ID", -1);
	         		MoneyAccount moneyAccount = MoneyAccount.load(MoneyAccount.class, _id);
	         		mSelectorFieldTransferOut.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
	         		mSelectorFieldTransferOut.setModelId(moneyAccount.getId());
	         		
	         		transferOutCurrency.setText(moneyAccount.getCurrency().getName() + "(" + moneyAccount.getCurrencyId() + ")");
	         		setExchangeRate(false);
	        	 }
	        	 break;
             case GET_TRANSFERIN_FRIEND_ID :
            	 if(resultCode == Activity.RESULT_OK){
             		long _id = data.getLongExtra("MODEL_ID", -1);
             		Friend friend = Friend.load(Friend.class, _id);
         			mSelectorFieldTransferInFriend.setText(friend.getDisplayName());
             		mSelectorFieldTransferInFriend.setModelId(friend.getId());
        
             		MoneyAccount moneyAccount = new Select().from(MoneyAccount.class).where("accountType = ? AND localFriendId = ? AND ownerUserId=?", "Topup", friend.getId(), HyjApplication.getInstance().getCurrentUser().getId()).executeSingle();
             		if(moneyAccount != null){
                 		mSelectorFieldTransferIn.setText(moneyAccount.getDisplayName());
                 		mSelectorFieldTransferIn.setModelId(moneyAccount.getId());
                 		transferInCurrency.setText(moneyAccount.getCurrency().getName() + "(" + moneyAccount.getCurrencyId() + ")");
             		} else {
             			mSelectorFieldTransferIn.setText(friend.getDisplayName() + "充值卡1");
                 		mSelectorFieldTransferIn.setModelId(null);
             			transferInCurrency.setText(moneyTopup.getTransferOut().getCurrency().getName() + "(" + moneyTopup.getTransferOut().getCurrencyId() + ")");
             		}
             		
             		Intent intent = getActivity().getIntent();
	         		long modelId = intent.getLongExtra("MODEL_ID", -1);
	         		
	         		if (modelId == -1) {
        				setExchangeRate(false);
        			} else {
        				mViewSeparatorProjectTransferIn.setVisibility(View.VISIBLE);
//        				mViewSeparatorProjectTransferIn1.setVisibility(View.VISIBLE);
        				mLinearLayoutProjectTransferIn.setVisibility(View.VISIBLE);
        				setExchangeRate(true);
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
    					mNumericTransferOutAmount.setNumber(Double.parseDouble(calculatorAmount));
    				}
    			}
    			break;
             case GET_TRANSFERIN_ID:
	        	 if(resultCode == Activity.RESULT_OK){
	         		long _id = data.getLongExtra("MODEL_ID", -1);
	         		MoneyAccount moneyAccount = MoneyAccount.load(MoneyAccount.class, _id);
	         		mSelectorFieldTransferIn.setText(moneyAccount.getName() + "(" + moneyAccount.getCurrencyId() + ")");
	         		mSelectorFieldTransferIn.setModelId(moneyAccount.getId());
	         		transferInCurrency.setText(moneyAccount.getCurrency().getName() + "(" + moneyAccount.getCurrencyId() + ")");
	         		setExchangeRate(false);
	        	 }
	        	 break;
             case GET_PROJECT_ID:
	        	 if(resultCode == Activity.RESULT_OK){
	         		long _id = data.getLongExtra("MODEL_ID", -1);
	         		project = Project.load(Project.class, _id);
	         		mSelectorFieldProject.setText(project.getDisplayName());
	         		mSelectorFieldProject.setModelId(project.getId());
	         		transferProjectCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
	    			projectTransferInCurrency.setText(project.getCurrency().getName() + "(" + project.getCurrencyId() + ")");
	    			
	    			Intent intent = getActivity().getIntent();
	         		long modelId = intent.getLongExtra("MODEL_ID", -1);
	         		
	         		if (modelId == -1) {
        				setExchangeRate(false);
        			} else {
        				setExchangeRate(true);
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
          }
    }
	
	public Double transferExchangeRate (String transferInCurrencyId , String transferOutCurrencyId) {
		String activityCurrencyId = HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencyId();
		Double transferInActivityRate; 
		if (transferInCurrencyId.equals(activityCurrencyId)) {
			transferInActivityRate = 1.00;
		} else {
			transferInActivityRate = Exchange.getExchangeRate(transferInCurrencyId, activityCurrencyId);
		}
		Double activityTransferOutRate;
		
		if (activityCurrencyId.equals(transferOutCurrencyId)) {
			activityTransferOutRate = 1.00;
		} else {
			activityTransferOutRate = Exchange.getExchangeRate(activityCurrencyId, transferOutCurrencyId);
		}
		return transferInActivityRate * activityTransferOutRate;
	}
}
