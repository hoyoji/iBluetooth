package com.hoyoji.hoyoji.friend;

import java.io.InputStream;
import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.hoyoji.models.Friend;
import com.hoyoji.btcontrol.R;

public class ImportFriendListFragment extends HyjUserListFragment implements OnQueryTextListener {
	private final static int INVITELINK_CHANGESTATE = 1;
	protected SearchView mSearchView;
	protected String mSearchText = "";
	
	Context mContext = null;  
	 
    /**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    /**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0; 
    /**电话号码**/  
    private static final int PHONES_NUMBER_INDEX = 1;
    /**头像ID**/  
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    /**联系人的ID**/  
    private static final int PHONES_CONTACT_ID_INDEX = 3; 
    /**联系人名称**/  
    private ArrayList<String> mContactsName = new ArrayList<String>();
    /**联系人电话号码**/  
    private ArrayList<String> mContactsNumber = new ArrayList<String>();
    /**联系人头像**/  
    private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
    ListView mListView = null;  
//    MyListAdapter myAdapter = null; 
	
	@Override
	public Integer useContentView() {
		return R.layout.friend_listfragment_import_friend;
	}

	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		mSearchView = (SearchView) getView().findViewById(R.id.linkListFragment_inviteLink_searchView);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.requestFocus();
		ImageView searchImage = (ImageView) mSearchView.findViewById(R.id.search_go_btn);
		if(searchImage != null){
			searchImage.setImageResource(R.drawable.ic_action_search);
		}
		ImageView magImage = (ImageView) mSearchView.findViewById(R.id.search_mag_icon);
		if(magImage != null){
			magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
		}

//		this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
//		onQueryLinkList();
//		importCommunicationList();
	}
	
	public void importCommunicationList(){
		mContext = getActivity();  
	    mListView = this.getListView();  
	    /**得到手机通讯录联系人信息**/  
	    getPhoneContacts();
//	    myAdapter = new MyListAdapter(getActivity());  
//	    setListAdapter(myAdapter);  
	 
//	    mListView.setOnItemClickListener(new OnItemClickListener() {  
//	 
//	        @Override  
//	        public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) {  
//	        //调用系统方法拨打电话  
//		        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mContactsNumber.get(position)));  
//		        startActivity(dialIntent);  
//	        }  
//	    }); 
	}
	
	/**得到手机通讯录联系人信息**/  
    private void getPhoneContacts() {  
	    ContentResolver resolver = mContext.getContentResolver();
	    // 获取手机联系人  
	    Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
	    if (phoneCursor != null) {  
	        while (phoneCursor.moveToNext()) {
		        //得到手机号码  
		        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
		        //当手机号码为空的或者为空字段 跳过当前循环  
		        if (TextUtils.isEmpty(phoneNumber))  
		            continue;  
		        //得到联系人名称  
		        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);  
		        //得到联系人ID  
		        Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);  
		        //得到联系人头像ID  
		        Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);  
		        //得到联系人头像Bitamp  
		        Bitmap contactPhoto = null; 
		        //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的  
		        if(photoid > 0 ) {  
		            Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);  
		            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);  
		            contactPhoto = BitmapFactory.decodeStream(input);  
		        }else {  
		            contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_person);  
		        }
		        mContactsName.add(contactName);  
		        mContactsNumber.add(phoneNumber);  
		        mContactsPhonto.add(contactPhoto);  
	        } 
	        phoneCursor.close();  
	    }  
    }  
      
    /**得到手机SIM卡联系人人信息**/  
    private void getSIMContacts() {  
	    ContentResolver resolver = mContext.getContentResolver();  
	    // 获取Sims卡联系人  
	    Uri uri = Uri.parse("content://icc/adn");  
	    Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null); 
	    if (phoneCursor != null) {  
	        while (phoneCursor.moveToNext()) {
	        // 得到手机号码  
	        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  
	        // 当手机号码为空的或者为空字段 跳过当前循环  
	        if (TextUtils.isEmpty(phoneNumber))  
	            continue;  
	        // 得到联系人名称  
	        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX); 
	        //Sim卡中没有联系人头像  
	        mContactsName.add(contactName);  
	        mContactsNumber.add(phoneNumber);  
	        }
	        phoneCursor.close();  
	    }  
    }  

    @Override
	public ListAdapter useListViewAdapter() {
		return new SimpleCursorAdapter(getActivity(),
				R.layout.friend_listitem_import,
				null,
				new String[] { Phone.DISPLAY_NAME, Phone.NUMBER },
				new int[] {R.id.importFriendListFragment_name ,R.id.importFriendListFragment_phoneNumber},
				0); 
	}	

	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		super.onCreateLoader(arg0, arg1);

		int offset = arg1.getInt("OFFSET");
		int limit = arg1.getInt("LIMIT");
		if(limit == 0){
			limit = getListPageSize();
		}
		super.onCreateLoader(arg0, arg1);
		String searchText = arg1.getString("searchText");
		String selection = null;
		String[] selectionArgs = null;
		
		if(searchText != null && searchText.length() > 0){
			selection = "display_name LIKE ? OR " + Phone.NUMBER + " LIKE ?";
			selectionArgs = new String[]{"%"+searchText+"%", "%"+searchText+"%"};
		}
		Object loader = new CursorLoader(getActivity(),
				Phone.CONTENT_URI, null,
				selection, selectionArgs, "DISPLAY_NAME LIMIT " + (limit + offset)
			);
		return (Loader<Object>)loader;
	}

//	@Override
//	public void onLoadFinished(Loader<Object> loader, Object data) {
//		super.onLoadFinished(loader, data);
//		// Set the new data in the adapter.
//		((HyjJSONListAdapter) this.getListAdapter()).addData((List<JSONObject>) data);
//	}

//	@Override
//	public void onLoaderReset(Loader<Object> loader) {
//		super.onLoaderReset(loader);
//		// Clear the data in the adapter.
//		((HyjJSONListAdapter) this.getListAdapter()).clear();
//	}

//	@Override
//	public ListAdapter useListViewAdapter() {
//		return new HyjJSONListAdapter(getActivity(),
//				R.layout.friend_listitem_import, 
//						new String[] { "pictureId", "name", "phoneNumber"}, 
//						new int[] { R.id.inviteFriendLinkListItem_state, R.id.inviteFriendLinkListItem_date, R.id.inviteFriendLinkListItem_type, R.id.inviteFriendLinkListItem_description });
//	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(id == -1) {
			 return;
		}
		final Cursor jsonPhone = (Cursor) l.getAdapter().getItem(position);
        String display_name = jsonPhone.getString(jsonPhone.getColumnIndex(Phone.DISPLAY_NAME)).toString().trim();
    	String phoneNumber = jsonPhone.getString(jsonPhone.getColumnIndex(Phone.NUMBER)).toString().trim();
		Friend importFiend = new Select().from(Friend.class).where("phoneNumber=?",phoneNumber).executeSingle();
		if(importFiend == null){
            importFiend = new Friend();
            HyjUtil.displayToast("导入好友   " + display_name + " 成功");
        } else {
        	HyjUtil.displayToast(display_name + "已经导入成功,不能重复导入");
        }
		importFiend.setNickName(display_name);
	    importFiend.setPhoneNumber(phoneNumber);
	    importFiend.save();
		
//			Bundle bundle = new Bundle();
//			bundle.putString("INVITELINK_JSON_OBJECT", jsonInviteLink.toString());
//			bundle.putInt("position", position);
//			openActivityWithFragmentForResult(ImportFriendListFragment.class, R.string.inviteLinkFormFragment_title, bundle, INVITELINK_CHANGESTATE);
	}

	@Override
	public boolean onQueryTextSubmit(String searchText) {
		Loader loader = getLoaderManager().getLoader(0);
		mSearchText = searchText.trim();
		if (searchText.length() == 0) {
			HyjUtil.displayToast("请输入查询条件");
			return true;
		}

	    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
   
        Bundle bundle = new Bundle();
		bundle.putString("searchText", mSearchText);
//		if(loader == null){
			getLoaderManager().restartLoader(0, bundle, this);
			loader = getLoaderManager().getLoader(0);
//		}
		return true;
	}

//	@Override
//	public void doFetchMore(ListView l, int offset, int pageSize) {
//		Loader loader = getLoaderManager().getLoader(0);
//		if(loader != null){
//			return;
//		}
//		this.setFooterLoadStart(l);
//		Bundle bundle = new Bundle();
//		bundle.putString("searchText", mSearchText);
////			if(loader == null){
//		getLoaderManager().restartLoader(0, bundle, this);
//		loader = getLoaderManager().getLoader(0);
//		((HyjHttpPostJSONLoader) loader).changePostQuery(bundle);
//	}
//	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if(view.getId() == R.id.importFriendListFragment_name){
			((TextView)view).setText(cursor.getString(columnIndex).trim());
			return true;
		} else if(view.getId() == R.id.importFriendListFragment_phoneNumber){
			((TextView)view).setText(cursor.getString(columnIndex).trim());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Integer useMultiSelectMenuView() {
		return R.menu.friend_listfragment_import_multi_select;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.importFriendListFragment_action_add){
			mSearchView.clearFocus();
			importFriend();
			this.exitMultiChoiceMode(getListView());
			
	        
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void importFriend() {
		SparseBooleanArray sparseBooleanArray = this.getListView().getCheckedItemPositions();
		if(sparseBooleanArray.size() == 0){
			HyjUtil.displayToast("请选择至少一条消息");
			return;
		}
		for (int i = 0; i < sparseBooleanArray.size(); ++i){
			if(sparseBooleanArray.valueAt(i)==true){
				Cursor jsonPhone = (Cursor) this.getListView().getAdapter().getItem(sparseBooleanArray.keyAt(i));
				
				String display_name = jsonPhone.getString(jsonPhone.getColumnIndex(Phone.DISPLAY_NAME)).toString().trim();
		    	String phoneNumber = jsonPhone.getString(jsonPhone.getColumnIndex(Phone.NUMBER)).toString().trim();
				Friend importFiend = new Select().from(Friend.class).where("phoneNumber=?",phoneNumber).executeSingle();
		        if(importFiend == null){
		            importFiend = new Friend();
		            
		            HyjUtil.displayToast("导入好友   " + display_name + " 成功");
		        } else {
		        	HyjUtil.displayToast(display_name + "已经导入成功,不能重复导入");
		        }
				importFiend.setNickName(display_name);
			    importFiend.setPhoneNumber(phoneNumber);
			    importFiend.save();
			}
		}
	}
}
