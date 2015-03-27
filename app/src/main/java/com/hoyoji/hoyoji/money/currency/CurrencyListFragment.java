package com.hoyoji.hoyoji.money.currency;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Currency;
import com.hoyoji.hoyoji.models.UserData;

public class CurrencyListFragment extends HyjUserListFragment{
	private ContentObserver mChangeObserver = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.currency_listfragment_currency;
	}

	@Override
	public Integer useOptionsMenuView() {
		return R.menu.currency_listfragment_currency;
	}

	@Override
	public ListAdapter useListViewAdapter() {
		return new SimpleCursorAdapter(getActivity(),
				R.layout.currency_listitem_currency,
				null,
				new String[] { "name","id" },
				new int[] { R.id.currencyListItem_name,R.id.currencyListItem_imageView_localCurrency },
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
		Object loader = new CursorLoader(getActivity(),
				ContentProvider.createUri(Currency.class, null),
				null, null, null, "name_pinYin ASC LIMIT " + (limit + offset)
			);
		return (Loader<Object>)loader;
	}

	@Override
	public void onInitViewData() {
		super.onInitViewData();
		if (mChangeObserver == null) {
			mChangeObserver = new ChangeObserver();
			this.getActivity().getContentResolver().registerContentObserver(ContentProvider.createUri(UserData.class, null), true,
							mChangeObserver);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.currencyListFragment_action_currency_addnew){
			openActivityWithFragment(AddCurrencyListFragment.class, R.string.addCurrencyListFragment_title_add, null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) { 
		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			return;
		}
		if(id < 0){
			super.onListItemClick(l, v, position, id);
			return;
		}
		if(getActivity().getCallingActivity() != null){
			Intent intent = new Intent();
			intent.putExtra("MODEL_ID", id);
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		} else {
			Bundle bundle = new Bundle();
			bundle.putLong("MODEL_ID", id);
			openActivityWithFragment(CurrencyFormFragment.class, R.string.currencyFormFragment_title_edit, bundle);
		}
    }  

//	@Override 
//	public void onDeleteListItem(Long id){
//		Currency currency= Currency.load(Currency.class, id);
//		currency.delete();
//	    HyjUtil.displayToast(R.string.currencyFormFragment_toast_delete_success);
//	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if(view.getId() == R.id.currencyListItem_name){
			((TextView)view).setText(cursor.getString(columnIndex));
			return true;
		}else if(view.getId() == R.id.currencyListItem_imageView_localCurrency){
			UserData userData = HyjApplication.getInstance().getCurrentUser().getUserData();
		    if(userData.getActiveCurrencyId().equalsIgnoreCase(cursor.getString(columnIndex))){
		    	((ImageView)view).setVisibility(View.VISIBLE);
		    }else{
		    	((ImageView)view).setVisibility(View.GONE);
		    }
		    return true;
		}
		
		return false;
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
						((SimpleCursorAdapter) getListAdapter()).notifyDataSetChanged();
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
			this.getActivity().getContentResolver()
					.unregisterContentObserver(mChangeObserver);
		}
		super.onDestroy();
	}

}
