package com.hoyoji.hoyoji.money.moneyaccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjModel;
import com.hoyoji.android.hyjframework.HyjSimpleExpandableListAdapter;
import com.hoyoji.android.hyjframework.fragment.HyjUserExpandableListFragment;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.MoneyAccount;
import com.hoyoji.hoyoji.models.User;
import com.hoyoji.hoyoji.models.UserData;

public class MoneyAccountTopupListFragment extends HyjUserExpandableListFragment {
	private static final int EDIT_MONEYACCOUNT_DETAILS = 0;
	private List<Map<String, Object>> mListGroupData = new ArrayList<Map<String, Object>>();
	private ArrayList<List<HyjModel>> mListChildData = new ArrayList<List<HyjModel>>();
	private ContentObserver mChangeObserver = null;

	@Override
	public Integer useContentView() {
		return R.layout.moneyaccount_listfragment_moneyaccount;
	}

	@Override
	public Integer useOptionsMenuView() {
		return R.menu.moneyaccount_listfragment_moneyaccount;
	}
	
	@Override
	public void onInitViewData() {
		super.onInitViewData();
		getListView().setGroupIndicator(null);
		

		Intent intent = getActivity().getIntent();
		String friendDisplayName = intent.getStringExtra("friendDisplayName");
		if(friendDisplayName != null){
//			this.getActivity().getActionBar().setSubtitle(friendDisplayName);
			((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(friendDisplayName);
		}
		
		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
					mChangeObserver);
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(User.class, null), true,
					mChangeObserver);
		}
	}
	
	@Override
	public ExpandableListAdapter useListViewAdapter() {
		MoneyAccountGroupListAdapter adapter = new MoneyAccountGroupListAdapter(
				getActivity(), mListGroupData, R.layout.moneyaccount_listitem_group,
				new String[] { "name", "balanceTotal" },
				new int[] { R.id.moneyAccountListItem_group_name, R.id.moneyAccountListItem_group_balanceTotal }, 
				mListChildData,
				R.layout.moneyaccount_listitem_moneyaccount, 
				new String[] {"id", "currentBalance"}, 
				new int[] {R.id.moneyAccountListItem_name, R.id.moneyAccountListItem_currentBalance});
		return adapter;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.moneyAccountListFragment_action_moneyAccount_addnew){
			Intent intent = getActivity().getIntent();
			Bundle bundle = new Bundle();
			bundle.putString("friendId", intent.getStringExtra("friendId"));
			bundle.putString("accountType", "Topup");
			openActivityWithFragment(MoneyAccountFormFragment.class, R.string.moneyAccountFormFragment_title_addnew, bundle);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(!getUserVisibleHint()){
			return super.onContextItemSelected(item);
		}
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
		Bundle bundle = new Bundle();
		bundle.putLong("MODEL_ID", info.id);
		bundle.putString("accountType", "Topup");
		switch (item.getItemId()) {
			case EDIT_MONEYACCOUNT_DETAILS:
				openActivityWithFragment(MoneyAccountFormFragment.class, R.string.moneyAccountFormFragment_title, bundle);
				break;
		}
		return super.onContextItemSelected(item);
	}

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		ExpandableListContextMenuInfo adapterContextMenuInfo = (ExpandableListContextMenuInfo) menuInfo;
//		if(ExpandableListView.getPackedPositionType(adapterContextMenuInfo.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
//			if(adapterContextMenuInfo.id != -1){
//				menu.add(0, EDIT_MONEYACCOUNT_DETAILS, 0, "充值卡账户资料");
//				menu.add(CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, R.string.app_action_cancel_list_item);
//			}
//		}
//	}
	@Override
	public Loader<Object> onCreateLoader(int groupPos, Bundle arg1) {
//		super.onCreateLoader(groupPos, arg1);
		Object loader;

		Intent intent = getActivity().getIntent();
		String excludeType = intent.getStringExtra("excludeType");

		if(arg1 == null){
			arg1 = new Bundle();
		}
		arg1.putString("excludeType", excludeType);
		arg1.putString("accountType", "Topup");
		arg1.putString("friendId", intent.getStringExtra("friendId"));
		
		if (groupPos < 0) { // 这个是分类
			loader = new MoneyAccountGroupListLoader(getActivity(), arg1);
		} else {
			loader = new MoneyAccountChildListLoader(getActivity(), arg1);
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
					getListView().expandGroup(i);
				}
			}
			adapter.notifyDataSetChanged();
			this.setFooterLoadFinished(((MoneyAccountGroupListLoader)loader).hasMoreData());
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
		String accountType = mListGroupData.get(groupPosition).get("accountType").toString();
		Bundle bundle = new Bundle();
		bundle.putString("accountType", accountType);
		Intent intent = getActivity().getIntent();
		bundle.putString("friendId", intent.getStringExtra("friendId"));
		getLoaderManager().restartLoader(groupPosition, bundle, this);
	}
	
	@Override
	public boolean setViewValue(View view, Object object, String name) {
		MoneyAccount moneyAccount = (MoneyAccount)object;
		if(view.getId() == R.id.moneyAccountListItem_name){
			TextView nameView = (TextView)view;
			nameView.setText(moneyAccount.getDisplayName());
			return true;
		} else if(view.getId() == R.id.moneyAccountListItem_currentBalance){
			HyjNumericView numericView = (HyjNumericView)view;
			Double balance = moneyAccount.getCurrentBalance();
			if(moneyAccount.getAccountType().equalsIgnoreCase("Debt")){
				if(balance > 0){
					numericView.setPrefix("借出" + moneyAccount.getCurrencySymbol());
					numericView.setNumber(balance);	
				}else if(balance == 0){
					numericView.setPrefix(moneyAccount.getCurrencySymbol());
					numericView.setNumber(balance);	
				}else{
					numericView.setPrefix("借入" + moneyAccount.getCurrencySymbol());
					numericView.setNumber(-balance);	
				}
			}else{
				numericView.setPrefix(moneyAccount.getCurrencySymbol());
				numericView.setNumber(balance);	
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override  
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if(id == -1) {
			 return false;
		}
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong("moneyAccount_id", id);
			MoneyAccount moneyAccount = HyjModel.load(MoneyAccount.class, id);
			if(moneyAccount.getAccountType().equalsIgnoreCase("Debt")){
				openActivityWithFragment(MoneyAccountDebtDetailsListFragment.class, R.string.moneyAccountListFragment_title_moneyAccountDebt_transactions, bundle);
			} else {
				openActivityWithFragment(MoneyAccountSearchListFragment.class, R.string.moneyAccountListFragment_title_moneyAccount_transactions, bundle);
			}
		}
		return true;
    } 
	

//	@Override 
//	public void onDeleteListItem(Long id){
//		MoneyAccount moneyAccount = MoneyAccount.load(MoneyAccount.class, id);
//		UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
//		if(userData.getActiveMoneyAccountId().equals(moneyAccount.getId())){
//			HyjUtil.displayToast("默认账户不能删除");
//			return;
//		}
//		moneyAccount.delete();
//	    HyjUtil.displayToast("充值卡账户删除成功");
//	}
	
	private static class MoneyAccountGroupListAdapter extends HyjSimpleExpandableListAdapter{

		public MoneyAccountGroupListAdapter(Context context,
	            List<Map<String, Object>> groupData, int expandedGroupLayout,
	                    String[] groupFrom, int[] groupTo,
	                    List<? extends List<? extends HyjModel>> childData,
	                    int childLayout, String[] childFrom,
	                    int[] childTo) {
			super( context, groupData, expandedGroupLayout, groupFrom, groupTo,childData, childLayout, 
					childFrom, childTo) ;
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
		            	if(v instanceof HyjNumericView){
		            		HyjNumericView balanceTotalView = (HyjNumericView)v;
		            		balanceTotalView.setPrefix(HyjApplication.getInstance().getCurrentUser().getUserData().getActiveCurrencySymbol());
		            		balanceTotalView.setNumber(Double.valueOf(data.get(from[i]).toString()));
		            	} else if(v instanceof TextView){
		            		((TextView)v).setText((String)data.get(from[i]));
		            	}
		            }
		        }
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
						((HyjSimpleExpandableListAdapter) getListView().getExpandableListAdapter()).notifyDataSetChanged();
						mTask = null;
			        }
			    };
			    mTask.execute();
			}
		}
	}
	@Override
	public void onDestroy() {
		if (mChangeObserver != null) {
			this.getActivity().getContentResolver().unregisterContentObserver(mChangeObserver);
		}
		super.onDestroy();
	}
}
