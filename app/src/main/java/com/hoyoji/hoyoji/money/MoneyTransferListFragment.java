package com.hoyoji.hoyoji.money;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.hoyoji.android.hyjframework.fragment.HyjUserListFragment;
import com.hoyoji.android.hyjframework.view.HyjDateTimeView;
import com.hoyoji.android.hyjframework.view.HyjImageView;
import com.hoyoji.android.hyjframework.view.HyjNumericView;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.MoneyTransfer;

public class MoneyTransferListFragment extends HyjUserListFragment {

	@Override
	public Integer useContentView() {
		return R.layout.money_listfragment_moneytransfer;
	}

	@Override
	public Integer useOptionsMenuView() {
		return R.menu.money_listfragment_moneytransfer;
	}

	@Override
	public ListAdapter useListViewAdapter() {
		return new SimpleCursorAdapter(getActivity(),
				R.layout.home_listitem_row,
				null,
				new String[] {"pictureId", "date", "transferOutAmount" },
				new int[] { R.id.homeListItem_picture, R.id.homeListItem_date, R.id.homeListItem_amount },
				0); 
	}	

	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		super.onCreateLoader(arg0, arg1);
		Object loader = new CursorLoader(getActivity(),
				ContentProvider.createUri(MoneyTransfer.class, null),
				null, null, null, "date DESC"
			);
		return (Loader<Object>)loader;
	}


	@Override
	public void onInitViewData() {
		super.onInitViewData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.moneyTransferListFragment_action_moneyTransfer_addnew){
			openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_addnew, null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override  
    public void onListItemClick(ListView l, View v, int position, long id) { 
		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			return;
		}
		if(id == -1) {
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
			openActivityWithFragment(MoneyTransferFormFragment.class, R.string.moneyTransferFormFragment_title_edit, bundle);
		}
    }  

//	@Override 
//	public void onDeleteListItem(Long id){
//		try {
//				ActiveAndroid.beginTransaction();
//				
//				MoneyTransfer moneyTransfer = MoneyTransfer.load(MoneyTransfer.class, id);
//				MoneyAccount transferOut = moneyTransfer.getTransferOut();
//				MoneyAccount transferIn = moneyTransfer.getTransferIn();
//				
//				HyjModelEditor<MoneyAccount> transferOutEditor = transferOut.newModelEditor();
//				HyjModelEditor<MoneyAccount> transferInEditor = transferIn.newModelEditor();
//				
//				moneyTransfer.delete();
//				
//				if(transferOut != null){
//					transferOutEditor.getModelCopy().setCurrentBalance(transferOut.getCurrentBalance() + moneyTransfer.getTransferOutAmount());
//					transferOutEditor.save();
//				}
//				if(transferIn != null){
//					transferInEditor.getModelCopy().setCurrentBalance(transferIn.getCurrentBalance() - moneyTransfer.getTransferInAmount());
//					transferInEditor.save();
//				}
//				
//			    HyjUtil.displayToast("转账删除成功");
//			    ActiveAndroid.setTransactionSuccessful();
//		} finally {
//		    ActiveAndroid.endTransaction();
//		}
//	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		if(view.getId() == R.id.homeListItem_date){
			((HyjDateTimeView)view).setText(cursor.getString(columnIndex));
			return true;
		} else if(view.getId() == R.id.homeListItem_amount){
			HyjNumericView numericView = (HyjNumericView)view;
			numericView.setPrefix("¥");
			numericView.setNumber(cursor.getDouble(columnIndex));
			return true;
		} else if(view.getId() == R.id.homeListItem_picture){
			HyjImageView imageView = (HyjImageView)view;
			imageView.setImage(cursor.getString(columnIndex));
			return true;
		} else {
			return false;
		}
	}
}
