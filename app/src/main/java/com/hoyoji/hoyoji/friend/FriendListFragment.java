package com.hoyoji.hoyoji.friend;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjSimpleExpandableListAdapter;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserExpandableListFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.AppConstants;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.hoyoji.models.Picture;
import com.hoyoji.hoyoji.models.User;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.sample.Util;
import com.tencent.sample.BaseUIListener;
import com.tencent.tauth.UiError;

public class FriendListFragment extends HyjUserExpandableListFragment {
	public final static int EDIT_CATEGORY_ITEM = 1;
	private List<Map<String, Object>> mListGroupData = new ArrayList<Map<String, Object>>();
	private ArrayList<List<HyjModel>> mListChildData = new ArrayList<List<HyjModel>>();
	private ContentObserver mUserChangeObserver = null;
	private IWXAPI api;
	private QQShare mQQShare = null;
	public static QQAuth mQQAuth;

//	private int mImageBackgroundColor = R.color.hoyoji_yellow;
	
	@Override
	public Integer useContentView() {
		return R.layout.friend_listfragment_friend;
	}
	
	
	
	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}



	@Override
	public Integer useOptionsMenuView() {
		return R.menu.friend_listfragment_friend;
	}

	public Integer useMultiSelectMenuPickerView() {
		return R.menu.multi_select_menu_picker;
//		return null;
	}

	@Override
	protected View useHeaderView(Bundle savedInstanceState){
		Intent intent = getActivity().getIntent();
		String nullItemName = intent.getStringExtra("NULL_ITEM");
		if(nullItemName == null){
			return null;
		}
		LinearLayout view =  (LinearLayout) getLayoutInflater(savedInstanceState).inflate(R.layout.friend_listitem_friend, null);
		TextView nameView = (TextView)view.findViewById(R.id.friendListItem_nickName);
		nameView.setText(nullItemName);
		HyjImageView imageView = (HyjImageView)view.findViewById(R.id.friendListItem_picture);
		imageView.setBackgroundColor(getResources().getColor(R.color.lightgray));
		imageView.setImageBitmap(HyjUtil.getCommonBitmap(R.drawable.ic_action_person_white));
		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(getActivity().getCallingActivity() != null){
					Intent intent = new Intent();
					intent.putExtra("MODEL_ID", -1);
					intent.putExtra("MODEL_TYPE", "Friend");
					getActivity().setResult(Activity.RESULT_OK, intent);
					getActivity().finish();
				}
			}
			
		});
		return view;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		if (mUserChangeObserver == null) {
			mUserChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(User.class, null), true,
					mUserChangeObserver);
		}
		mQQAuth = QQAuth.createInstance(AppConstants.TENTCENT_CONNECT_APP_ID, getActivity());
		mQQShare = new QQShare(getActivity(), mQQAuth.getQQToken());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		if(item.getItemId() == R.id.searchListFragment_action_addfriend){
//			Bundle bundle = new Bundle();
//			bundle.putLong("eventId", modelId);
//			FriendDialogFragment.newInstance(bundle).show(getActivity().getSupportFragmentManager(), "FriendDialogFragment");
//			return true;
//		} else 
		if(item.getItemId() == R.id.friendListFragment_action_friend_add){
			openActivityWithFragment(AddFriendListFragment.class, R.string.addFriendListFragment_title_add, null);
			return true;
		} else if(item.getItemId() == R.id.friendListFragment_action_friend_create){
			Bundle bundle = new Bundle();
			bundle.putString("DIALOG_TYPE", "create");
			FriendDialogFragment.newInstance(bundle).show(getActivity().getSupportFragmentManager(), "FriendDialogFragment");
			return true;
		} else if(item.getItemId() == R.id.friendListFragment_action_friend_invite){
			Bundle bundle = new Bundle();
			bundle.putString("DIALOG_TYPE", "invite");
			FriendDialogFragment.newInstance(bundle).show(getActivity().getSupportFragmentManager(), "FriendDialogFragment");
			return true;
		} else if(item.getItemId() == R.id.friendListFragment_action_friendCategory_create){
			openActivityWithFragment(FriendCategoryFormFragment.class, R.string.friendCategoryFormFragment_title_create, null);
			return true;
		}
//		  else if(item.getItemId() == R.id.friendListFragment_action_friend_create){
//			openActivityWithFragment(FriendFormFragment.class, R.string.friendFormFragment_title_create, null);
//			return true;
//		} else if(item.getItemId() == R.id.friendListFragment_action_friend_add){
//			openActivityWithFragment(AddFriendListFragment.class, R.string.addFriendListFragment_title_add, null);
//			return true;
//		} else if(item.getItemId() == R.id.friendListFragment_action_friend_import){
//			openActivityWithFragment(ImportFriendListFragment.class, R.string.friendFormFragment_title_import, null);
////			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);  
////            this.startActivityForResult(intent, 1);  
//			return true;
//		} else if(item.getItemId() == R.id.friendListFragment_action_friendCategory_create){
//			openActivityWithFragment(FriendCategoryFormFragment.class, R.string.friendCategoryFormFragment_title_create, null);
//			return true;
//		} else if(item.getItemId() == R.id.friendListFragment_action_friend_invite){
//			inviteFriend("Other");
//			return true;
//		} else if(item.getItemId() == R.id.friendListFragment_action_friend_invite_wxFriend){
//			inviteFriend("WX");
//			return true;
//		} else if(item.getItemId() == R.id.friendListFragment_action_friend_invite_qqFriend){
//			inviteFriend("QQ");
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        switch (requestCode) {  
        case 1:  
            if (resultCode == Activity.RESULT_OK) {  
                Uri contactData = data.getData();  
                Cursor cursor = getActivity().managedQuery(contactData, null, null, null, null);  
                cursor.moveToFirst();  
                HashMap<String , String> phoneMap = this.getContactPhone(cursor);  
                Friend importFiend = new Select().from(Friend.class).where("phoneNumber=?",phoneMap.get("phoneNumber").trim()).executeSingle();
                if(importFiend == null){
	                importFiend = new Friend();
                }
                importFiend.setNickName(phoneMap.get("phoneName").trim());
                importFiend.setPhoneNumber(phoneMap.get("phoneNumber").trim());
                importFiend.save();
            }  
            break;  
  
        default:  
            break;  
        }  
    }  
	
	private HashMap<String , String> getContactPhone(Cursor cursor) {  
        // TODO Auto-generated method stub  
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);  
        int phoneNum = cursor.getInt(phoneColumn);  
//        String result = "";  
        HashMap<String , String> resultMap = new HashMap<String , String>();
        if (phoneNum > 0) {  
            // 获得联系人的ID号  
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);  
            String contactId = cursor.getString(idColumn);  
            // 获得联系人电话的cursor  
            Cursor phone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+ contactId, null, null);  
            if (phone.moveToFirst()) {  
                for (; !phone.isAfterLast(); phone.moveToNext()) {  
                    int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);  
                    int typeindex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);  
                    int phone_type = phone.getInt(typeindex);  
                    String phoneNumber = phone.getString(index);  
                    
                    int indexName = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String phoneName = phone.getString(indexName);  
//                    result = phoneNumber;  
                    resultMap.put("phoneNumber", phoneNumber);
                    resultMap.put("phoneName", phoneName);
                    switch (phone_type) {//此处请看下方注释  
	                  case 2:  
	                	  resultMap.put("phoneNumber", phoneNumber);
	                	  resultMap.put("phoneName", phoneName);
	                      break;  
	  
	                  default:  
	                      break;  
	                  } 
                }  
                if (!phone.isClosed()) {  
                    phone.close();  
                }  
            }  
        }  
        return resultMap;  
    }
	
	public void inviteFriend(final String way) {
		((HyjActivity) getActivity()).displayProgressDialog(R.string.friendListFragment__action_invite_title,R.string.friendListFragment__action_invite_content);
		
		JSONObject inviteFriendObject = new JSONObject();
		final String id = UUID.randomUUID().toString();
		try {
			inviteFriendObject.put("id", id);
			inviteFriendObject.put("__dataType", "InviteLink");
			inviteFriendObject.put("title", "邀请成为好友");
			inviteFriendObject.put("type", "Friend");
			inviteFriendObject.put("date", (new Date()).getTime());
			inviteFriendObject.put("description", HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您成为好友，一起参与记账。");
			inviteFriendObject.put("state", "Open");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
   	 
   	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				((HyjActivity) getActivity()).dismissProgressDialog();
					if(way.equals("Other")){
						inviteOtherFriend(id);
					} else if(way.equals("WX")){
						inviteWXFriend(id);
					} else if(way.equals("QQ")){
						inviteQQFriend(id);
					}
			}

			@Override
			public void errorCallback(Object object) {
				((HyjActivity) getActivity()).dismissProgressDialog();
				try {
					JSONObject json = (JSONObject) object;
					((HyjActivity) getActivity()).displayDialog(null,
							json.getJSONObject("__summary")
									.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
   	 
   	 	HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + inviteFriendObject.toString() + "]", "postData");
	 }

	public void inviteOtherFriend(String id) {
		Intent intent=new Intent(Intent.ACTION_SEND);   
        intent.setType("text/plain");   
        
//      File f;
//		try {
//			f = HyjUtil.createImageFile("invite_friend", "PNG");
//			if(!f.exists()){
//		        Bitmap bmp = HyjUtil.getCommonBitmap(R.drawable.invite_friend);
//			    FileOutputStream out;
//				out = new FileOutputStream(f);
//				bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
//				out.close();
//			}
//	        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	        intent.putExtra(Intent.EXTRA_TITLE, "邀请成为好友");  
	        intent.putExtra(Intent.EXTRA_SUBJECT, "邀请成为好友");   
	        intent.putExtra(Intent.EXTRA_TEXT, HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您成为好友，一起参与记账。\n\n" + HyjApplication.getServerUrl()+"m/invite.html?id=" + id);  
	        
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
	        startActivity(Intent.createChooser(intent, "邀请成为好友")); 
	}
	
	public void inviteWXFriend(String id) {
		api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "邀请成为好友";
		msg.description = HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您成为好友，一起参与记账。";
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		msg.thumbData = Util.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		
	}
	
	public void inviteQQFriend(String id) {
		final Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, "邀请成为好友");
	    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您成为好友，一起参与记账。");
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id);
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, HyjApplication.getInstance().getServerUrl() + "imgs/invite_friend.png");
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "好友AA记账");
//	    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");		
	    mQQShare.shareToQQ(getActivity(), params, new BaseUIListener(getActivity()) {

            @Override
            public void onCancel() {
//            		Util.toastMessage(getActivity(), "onCancel: ");
            }

            @Override
            public void onComplete(Object response) {
//                Util.toastMessage(getActivity(), "onComplete: " + response.toString());
            }

            @Override
            public void onError(UiError e) {
//                Util.toastMessage(getActivity(), "onError: " + e.errorMessage, "e");
            }

        });
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}


//	@Override
//	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
//		super.onCreateLoader(groupPos, arg1);
//		Object loader;
//		if(groupPos < 0){ // 这个是分类
//			int offset = arg1.getInt("OFFSET");
//			int limit = arg1.getInt("LIMIT");
//			if(limit == 0){
//				limit = getListPageSize();
//			}
//			loader = new CursorLoader(getActivity(),
//					ContentProvider.createUri(FriendCategory.class, null),
//					null, null, null, "name_pinYin ASC LIMIT " + (limit + offset)
//				);
//		} else {
//			loader = new CursorLoader(getActivity(),
//					ContentProvider.createUri(Friend.class, null),
//					null, "friendCategoryId=?", new String[]{arg1.getString("friendCategoryId")}, "nickName_pinYin"
//				);
//		}
//		return (Loader<Object>)loader;
//	}



	@Override
	public ExpandableListAdapter useListViewAdapter() {
//		HyjSimpleExpandableListAdapter adapter = new FriendGroupListAdapter(
//				getActivity(), mListGroupData, R.layout.moneyaccount_listitem_group,
//				new String[] { "name", "balanceTotal" },
//				new int[] { R.id.moneyAccountListItem_group_name, R.id.moneyAccountListItem_group_balanceTotal }, 
//				mListChildData,
//				R.layout.home_listitem_row, 
//				new String[] {"picture", "subTitle", "title", "remark", "date", "currentBalance", "owner"}, 
//				new int[] {R.id.homeListItem_picture, R.id.homeListItem_subTitle, R.id.homeListItem_title, 
//							R.id.homeListItem_remark, R.id.homeListItem_date,
//							R.id.homeListItem_amount, R.id.homeListItem_owner});
		
		HyjSimpleExpandableListAdapter adapter =  new FriendGroupListAdapter(getActivity(),
				mListGroupData, 
				R.layout.friend_listitem_friend_group, 
				new String[] { "friendCategoryName"},
				new int[] { R.id.friendListItem_category_name }, 
				mListChildData, 
				R.layout.friend_listitem_friend,
				new String[] { "friendUserId", "_id" },
				new int[] { R.id.friendListItem_picture, R.id.friendListItem_nickName });
//		
		return adapter;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(!getUserVisibleHint()){
			return super.onContextItemSelected(item);
		}
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		int type = ExpandableListView
	            .getPackedPositionType(info.packedPosition);

	    switch (item.getItemId()) {
			case EDIT_CATEGORY_ITEM:
				if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			        int groupPos = ExpandableListView
			                .getPackedPositionGroup(info.packedPosition);
				    Long itemId = getListView().getExpandableListAdapter().getGroupId(groupPos);
				    Bundle bundle = new Bundle();
					bundle.putLong("MODEL_ID", itemId);
					openActivityWithFragment(FriendCategoryFormFragment.class, R.string.friendCategoryFormFragment_title_edit, bundle);
				} 
				break;
//			case EDIT_FRIEND_DETAILS:
//				if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
//			        int groupPos = ExpandableListView
//			                .getPackedPositionGroup(info.packedPosition);
//			        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
//				    Long itemId = getListView().getExpandableListAdapter().getChildId(groupPos, childPos);
//					Bundle bundle = new Bundle();
//					bundle.putLong("MODEL_ID", itemId);
//					openActivityWithFragment(FriendFormFragment.class, R.string.friendFormFragment_title, bundle);
//				} 
//				break;
				
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		ExpandableListContextMenuInfo adapterContextMenuInfo = (ExpandableListContextMenuInfo) menuInfo;
		if(ExpandableListView.getPackedPositionType(adapterContextMenuInfo.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
			menu.add(EDIT_CATEGORY_ITEM, EDIT_CATEGORY_ITEM, EDIT_CATEGORY_ITEM, R.string.friendCategoryFormFragment_title_edit);
//			super.onCreateContextMenu(menu, v, menuInfo);
		} else {
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}		
	
//	@Override  
//	public boolean onGroupClick(ExpandableListView parent, View v,
//			int groupPosition, long id) {
////		if(getActivity().getCallingActivity() != null){
////			Intent intent = new Intent();
////			intent.putExtra("MODEL_ID", id);
////			getActivity().setResult(Activity.RESULT_OK, intent);
////			getActivity().finish();
////			return true;
////		} else {
////			Bundle bundle = new Bundle();
////			bundle.putLong("MODEL_ID", id);
////			openActivityWithFragment(FriendCategoryFormFragment.class, R.string.friendCategoryFormFragment_title_edit, bundle);
////			return true;
////		}
//    } 

	
	protected int getListPageSize(){
		return (int) (displayMetrics.heightPixels / displayMetrics.density / 40);
	}
	
	
	@Override  
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			intent.putExtra("MODEL_TYPE", "Friend");
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
			return true;
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong("friend_id", id);
			bundle.putLong("MODEL_ID", id);
			openActivityWithFragment(FriendViewPagerFragment.class, R.string.friendListFragment_title_friend_transactions, bundle);
			return true;
		}
    }  

//	@Override 
//	public void onDeleteListItem(Long id){
//		Friend friend = Friend.load(Friend.class, id);
//		friend.delete();
//	    HyjUtil.displayToast("好友删除成功");
//	}

//	@Override 
//	public void onDeleteListGroup(int groupPos, Long id){
//		List<Friend> friends = HyjModel.load(FriendCategory.class, id).getFriends();
//		if(getListView().getExpandableListAdapter().getChildrenCount(groupPos) > 0 || !friends.isEmpty()){
//			HyjUtil.displayToast("该好友分类下包含好友，不能被删除");
//		} else {
//			FriendCategory friendCategory = FriendCategory.load(FriendCategory.class, id);
//			friendCategory.delete();
//		    HyjUtil.displayToast("好友分类删除成功");
//		}
//	}


//	@Override
//	public void onGetChildrenCursor(Cursor groupCursor) {
//		Bundle bundle = new Bundle();
//		bundle.putString("friendCategoryId", groupCursor.getString(groupCursor.getColumnIndex("id")));
//		int groupPos = groupCursor.getPosition();
//		Loader<Object> loader = getLoaderManager().getLoader(groupPos);
//	    if (loader != null && !loader.isReset() ) { 
//	    	getLoaderManager().restartLoader(groupPos, bundle, this);
//	    } else {
//	    	getLoaderManager().initLoader(groupPos, bundle, this);
//	    }
//	}
	
	@Override
	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
		super.onCreateLoader(groupPos, arg1);
		Object loader;

		if (groupPos < 0) { // 这个是分类
			loader = new FriendCategoryGroupListLoader(getActivity(), arg1);
		} else {
			loader = new FriendChildListLoader(getActivity(), arg1);
		}
		return (Loader<Object>) loader;
	}

	@Override
	public void onLoadFinished(Loader loader, Object list) {
		HyjSimpleExpandableListAdapter adapter = (HyjSimpleExpandableListAdapter) getListView()
				.getExpandableListAdapter();
		if (loader.getId() < 0) {
			ArrayList<Map<String, Object>> groupList = (ArrayList<Map<String, Object>>) list;
			mListGroupData.clear();
			mListGroupData.addAll(groupList);
			for(int i = 0; i < groupList.size(); i++){
				if(mListChildData.size() <= i){
					mListChildData.add(null);
					getListView().expandGroup(i);
				} else if(getListView().collapseGroup(i)){
//					if(!groupList.get(i).get("accountType").toString().equals("AutoHide")){
						getListView().expandGroup(i);
//					}
				}
			}
			adapter.notifyDataSetChanged();
			this.setFooterLoadFinished(((FriendCategoryGroupListLoader)loader).hasMoreData());
		} else {
				ArrayList<HyjModel> childList = (ArrayList<HyjModel>) list;
				mListChildData.set(loader.getId(), childList);
				adapter.notifyDataSetChanged();
		}
		// The list should now be shown.
		if (isResumed()) {
			// setListShown(true);
		} else {
			// setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		HyjSimpleExpandableListAdapter adapter = (HyjSimpleExpandableListAdapter)
		 getListView().getExpandableListAdapter();
		 if(loader.getId() < 0){
				this.mListGroupData.clear();
		 } else {
			 if(adapter.getGroupCount() > loader.getId()){
					this.mListChildData.set(loader.getId(), null);
			 } else {
				 getLoaderManager().destroyLoader(loader.getId());
			 }
		 }
		
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		String friendCategoryId = mListGroupData.get(groupPosition).get("friendCategoryId").toString();
		Bundle bundle = new Bundle();
		bundle.putString("friendCategoryId", friendCategoryId);
		getLoaderManager().restartLoader(groupPosition, bundle, this);
	}
	
	@Override
	public boolean setViewValue(View view, Object model, String field) {
		Friend friend = (Friend)model;
		if(view.getId() == R.id.friendListItem_nickName){
			String friendUserId = friend.getFriendUserId();
//			if(cursor.getString(columnIndex) != null && cursor.getString(columnIndex).length() > 0){
				((TextView)view).setText(friend.getDisplayName());
//			} else if(friendUserId != null){
//				User user = HyjModel.getModel(User.class, friendUserId);
//				if(user != null){
//					((TextView)view).setText(user.getDisplayName());
//				} else {
//					((TextView)view).setText(cursor.getString(cursor.getColumnIndex("friendUserName")));
//				}
//			} else {
//				((TextView)view).setText(cursor.getString(cursor.getColumnIndex("friendUserName")));
//			}

			if(HyjApplication.getInstance().getCurrentUser().getId().equals(friendUserId)){
				((TextView)view).setTextColor(getResources().getColor(R.color.hoyoji_red));
			} else {
				((TextView)view).setTextColor(Color.BLACK);
			}
			return true;
		} else if(view.getId() == R.id.friendListItem_picture){
			String userId = friend.getFriendUserId();
			HyjImageView imageView = (HyjImageView)view;
			imageView.setDefaultImage(R.drawable.ic_action_person_white);
			if(userId != null){
				User user = HyjModel.getModel(User.class, userId);
				if(user != null){
					imageView.setImage(user.getPictureId());
				} else {
					imageView.setImage((Picture)null);
				}
				if(HyjApplication.getInstance().getCurrentUser().getId().equals(userId)){
					imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_red));
				} else {
					imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_green));
				}
			} else {
				imageView.setImage((Picture)null);
				imageView.setBackgroundColor(getResources().getColor(R.color.hoyoji_yellow));
			}

			
	 		if(view.getTag() == null){
				view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						Bundle bundle = new Bundle();
						bundle.putLong("MODEL_ID", (Long) v.getTag());
						openActivityWithFragment(FriendFormFragment.class, R.string.friendFormFragment_title, bundle);
					}
				});
			}
			view.setTag(friend.get_mId());
			return true;
		} else {
			return false;
		}
	}
	
	private class ChangeObserver extends ContentObserver {
		AsyncTask<String, Void, String> mTask = null;
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

//		@Override
//		public void onChange(boolean selfChange, Uri uri) {
//			super.onChange(selfChange, uri);
//		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if(mTask == null){
				mTask = new AsyncTask<String, Void, String>() {
			        @Override
			        protected String doInBackground(String... params) {
						try {
							//等待其他的更新都到齐后再更新界面
							Thread.sleep(0);
						} catch (InterruptedException e) {}
						return null;
			        }
			        @Override
			        protected void onPostExecute(String result) {
						((FriendGroupListAdapter) getListView().getExpandableListAdapter()).notifyDataSetChanged();
						mTask = null;
			        }
			    };
			    mTask.execute();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		if (mUserChangeObserver != null) {
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mUserChangeObserver);
		}
		super.onDestroy();
	}

	@Override
	protected void returnSelectedItems() {
		long[] ids = getListView().getCheckedItemIds();
		if(ids.length == 0){
			HyjUtil.displayToast("请选择至少一个好友");
			return;
		}
//		if(getActivity().getCallingActivity() != null){
////			HyjUtil.displayToast("暂不支持多选");
//			return;
//		} else {
			Intent intent = new Intent();
			intent.putExtra("MODEL_IDS", ids);
			intent.putExtra("MODEL_TYPE", "Friend");
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
//		}
	}


	private static class FriendGroupListAdapter extends HyjSimpleExpandableListAdapter{

		public FriendGroupListAdapter(Context context,
	            List<Map<String, Object>> groupData, int expandedGroupLayout,
	                    String[] groupFrom, int[] groupTo,
	                    List<? extends List<? extends HyjModel>> childData,
	                    int childLayout, String[] childFrom,
	                    int[] childTo) {
			super( context, groupData, expandedGroupLayout, groupFrom, groupTo,childData, childLayout, 
					childFrom, childTo) ;
		}
		
		@Override
		public long getGroupId(int pos) {
			Map<String, Object> data = (Map<String, Object>) this.getGroup(pos);
			return (Long)data.get("_id");
		}
		
		@Override
		 public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
		            ViewGroup parent) {
		        View v;
		        if (convertView == null) {
		            v = newGroupView(isExpanded, parent);
		        } else {
		            v = convertView;
		        }
		        bindGroupView(v, (Map<String, ?>) this.getGroup(groupPosition), mGroupFrom, mGroupTo);
		        
		        return v;
		    }
		 
		 private void bindGroupView(View view, Map<String, ?> data, String[] from, int[] to) {
		        int len = to.length;

		        for (int i = 0; i < len; i++) {
		            View v = view.findViewById(to[i]);
		            if (v != null) {
		            	if(v instanceof TextView){
		            		((TextView)v).setText(data.get(from[i]).toString()+" ("+data.get("count").toString()+")");
		            	}
		            }
		        }
		    }
	}
}
