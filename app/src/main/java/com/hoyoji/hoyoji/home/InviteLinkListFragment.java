package com.hoyoji.hoyoji.home;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostJSONLoader;
import com.hoyoji.android.hyjframework.server.HyjJSONListAdapter;
import com.hoyoji.android.hyjframework.view.HyjDateTimeView;
import com.hoyoji.btcontrol.R;

public class InviteLinkListFragment extends HyjUserListFragment implements OnQueryTextListener{
	private final static int INVITELINK_CHANGESTATE = 1;
	protected SearchView mSearchView;
	protected String mSearchText = "";
	
	@Override
	public Integer useContentView() {
		return R.layout.link_listfragment_invite;
	}

	@Override
	public Integer useToolbarView() {
		return super.useToolbarView();
	}

	@Override
	public void onInitViewData() {
		mSearchView = (SearchView) getView().findViewById(R.id.linkListFragment_inviteLink_searchView);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setSubmitButtonEnabled(true);
		
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
	}
	

	@Override
	public void initLoader(int loaderId) {
		// do not init loader... wait for the user to start search
	}

	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		super.onCreateLoader(arg0, arg1);
		Object loader = new HyjHttpPostJSONLoader(getActivity(), arg1);
		return (Loader<Object>) loader;
	}

	@Override
	public void onLoadFinished(Loader<Object> loader, Object data) {
		super.onLoadFinished(loader, data);
		// Set the new data in the adapter.
		((HyjJSONListAdapter) this.getListAdapter()).addData((List<JSONObject>) data);
	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {
		super.onLoaderReset(loader);
		// Clear the data in the adapter.
		((HyjJSONListAdapter) this.getListAdapter()).clear();
	}

	@Override
	public ListAdapter useListViewAdapter() {
		return new HyjJSONListAdapter(getActivity(),
				R.layout.link_listitem_invite, 
						new String[] { "state", "date", "type", "description" }, 
						new int[] { R.id.inviteFriendLinkListItem_state, R.id.inviteFriendLinkListItem_date, R.id.inviteFriendLinkListItem_type, R.id.inviteFriendLinkListItem_description });
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			return;
		}
		if(id == -1) {
			 return;
		}
		if (id >= 0) {
			final JSONObject jsonInviteLink = (JSONObject) l.getAdapter().getItem(position);
			
			Bundle bundle = new Bundle();
			bundle.putString("INVITELINK_JSON_OBJECT", jsonInviteLink.toString());
			bundle.putInt("position", position);
			openActivityWithFragmentForResult(InviteLinkFormFragment.class, R.string.inviteLinkFormFragment_title, bundle, INVITELINK_CHANGESTATE);
		}
	}
	
	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String searchText) {
		mSearchText = searchText.trim();
		if (searchText.length() == 0) {
			HyjUtil.displayToast("请输入查询条件");
			return true;
		}

	    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
   
		JSONObject data = new JSONObject();
		try {
			data.put("title", mSearchText);
			data.put("description", mSearchText);
			data.put("__dataType", "InviteLink");
			data.put("__limit", getListPageSize());
			data.put("ownerUserId", HyjApplication.getInstance().getCurrentUser().getId());
			data.put("__offset", 0);
			data.put("__orderBy", "date DESC");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Bundle bundle = new Bundle();
		bundle.putString("target", "findData");
		bundle.putString("postData", (new JSONArray()).put(data).toString());
		if (getLoaderManager().getLoader(0) != null) {
			getLoaderManager().destroyLoader(0);
		}
		getLoaderManager().restartLoader(0, bundle, this);
		return true;
	}

//	public void onQueryLinkList() {
//		JSONObject data = new JSONObject();
//		try {
//			if (mSearchText.length() == 0) {
//				data.put("__dataType", "InviteLink");
//				data.put("ownerUserId", HyjApplication.getInstance().getCurrentUser().getId());
//				data.put("__limit", getListPageSize());
//				data.put("__offset", 0);
//				data.put("__orderBy", "date DESC");
//			}else{
//				data.put("title", mSearchText);
//				data.put("description", mSearchText);
//				data.put("__dataType", "InviteLink");
//				data.put("ownerUserId", HyjApplication.getInstance().getCurrentUser().getId());
//				data.put("__limit", getListPageSize());
//				data.put("__offset", 0);
//				data.put("__orderBy", "date ASC");
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//
//		Bundle bundle = new Bundle();
//		bundle.putString("target", "findData");
//		bundle.putString("postData", (new JSONArray()).put(data).toString());
//		if (getLoaderManager().getLoader(0) != null) {
//			getLoaderManager().destroyLoader(0);
//		}
//		getLoaderManager().restartLoader(0, bundle, this);
//	}

	@Override
	public void doFetchMore(ListView l, int offset, int pageSize) {
		Loader loader = getLoaderManager().getLoader(0);
		if(loader != null && ((HyjHttpPostJSONLoader)loader).isLoading()){
			return;
		}
		this.setFooterLoadStart(l);
		JSONObject data = new JSONObject();
		try {
			data.put("title", mSearchText);
			data.put("description", mSearchText);
			data.put("__dataType", "InviteLink");
			data.put("__limit", pageSize);
			data.put("ownerUserId", HyjApplication.getInstance().getCurrentUser().getId());
			data.put("__offset", offset);
			data.put("__orderBy", "date DESC");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Bundle bundle = new Bundle();
		bundle.putString("target", "findData");
		bundle.putString("postData", (new JSONArray()).put(data).toString());
		if(loader == null){
			getLoaderManager().restartLoader(0, bundle, this);
			loader = getLoaderManager().getLoader(0);
		}
		((HyjHttpPostJSONLoader) loader).changePostQuery(bundle);
	}

	@Override
	public boolean setViewValue(View view, Object json, String name) {
		JSONObject jsonObject = (JSONObject)json;
		if (view.getId() == R.id.inviteFriendLinkListItem_state) {
			if ("Open".equals(jsonObject.optString(name))) {
				((TextView) view).setText("打开");
			} else if ("Close".equals(jsonObject.optString(name))) {
				((TextView) view).setText("关闭");
			} else {
				((TextView) view).setText("");
			}
			return true;
		} else if (view.getId() == R.id.inviteFriendLinkListItem_date) {
//			((HyjDateTimeView) view).setTime(jsonObject.optLong(name));
			HyjDateTimeView dateTimeView = (HyjDateTimeView)view;
			dateTimeView.setDateFormat("yyyy-MM-dd HH:mm");
			dateTimeView.setTime(jsonObject.optLong(name));
			return true;
		} else if (view.getId() == R.id.inviteFriendLinkListItem_type) {
			if("EventMember".equals(jsonObject.optString(name))) {
				((TextView) view).setText("邀请活动成员");
			} else if("ProjectShare".equals(jsonObject.optString(name))) {
				((TextView) view).setText("邀请共享好友");
			} else if("ProjectShare".equals(jsonObject.optString(name))) {
				((TextView) view).setText("邀请好友");
			}
			return true;
		} else if (view.getId() == R.id.inviteFriendLinkListItem_description) {
			((TextView) view).setText(jsonObject.optString(name));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void setFooterLoadFinished(ListView l, int count){
		int offset = l.getFooterViewsCount() + l.getHeaderViewsCount();
        super.setFooterLoadFinished(l, l.getAdapter().getCount() + count - offset);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case INVITELINK_CHANGESTATE:
			if (resultCode == Activity.RESULT_OK) {
				String state = data.getStringExtra("state");
				String description = data.getStringExtra("description");
				String verificationCode = data.getStringExtra("verificationCode");
				
				int position = data.getIntExtra("position", -1);
				JSONObject object = ((HyjJSONListAdapter) this.getListAdapter()).getItem(position);
				try {
					object.put("state", state);
					object.put("description", description);
					object.put("verificationCode", verificationCode);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((HyjJSONListAdapter) this.getListAdapter()).notifyDataSetChanged();
			}
			break;
		}
	}
}
