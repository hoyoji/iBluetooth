package com.hoyoji.android.hyjframework.fragment;

import java.util.List;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.server.HyjJSONListAdapter;
import com.hoyoji.android.hyjframework.view.HyjListView;
import com.hoyoji.android.hyjframework.view.HyjListView.OnOverScrollByListener;
import com.hoyoji.android.hyjframework.activity.HyjBlankActivity;
import com.hoyoji.android.hyjframework.activity.HyjBlankUserActivity;
import com.hoyoji.btcontrol.R;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public abstract class HyjListFragment extends ListFragment implements 
	LoaderManager.LoaderCallbacks<Object>, 
	SimpleCursorAdapter.ViewBinder, 
	SimpleAdapter.ViewBinder{
	
	public final static int DELETE_LIST_ITEM = 1024;
	public final static int CANCEL_LIST_ITEM = 1025;
	private boolean mIsViewInited = false;
	private View mFooterView;
//	protected int mListPageSize = 10;
//	private TextView mEmptyView = null;
	private Menu mOptionsMenu;
	private DisplayMetrics displayMetrics;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		displayMetrics = getResources().getDisplayMetrics();
			//View v = super.onCreateView(inflater, container, savedInstanceState);
			ViewGroup rootView = (ViewGroup) inflater.inflate(useContentView(), container, false);
			//rootView.addView(v, 0);
//			mEmptyView  = (TextView)rootView.findViewById(android.R.id.empty);
			if(useToolbarView() != null){
				// populate bottom toolbar
			}
			return rootView;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		getListView().setFooterDividersEnabled(false);

		View footerLayout = getLayoutInflater(savedInstanceState).inflate(R.layout.list_view_footer_fetch_more, null);
		mFooterView = footerLayout.findViewById(android.R.id.text1);
	    mFooterView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				doFetchMore(getListView(), getListView().getAdapter().getCount() - getListView().getHeaderViewsCount() - getListView().getFooterViewsCount(), getListPageSize());
			}
	    });

//	    getListView().setOverscrollFooter(getResources().getDrawable(R.drawable.ic_action_refresh));
	    if(getListView() instanceof HyjListView){
		    ((HyjListView)getListView()).setOnOverScrollByListener(new OnOverScrollByListener(){
				private boolean mIsFetchingMore = false;

				@Override
				public void onOverScrollBy(int deltaX, int deltaY, int scrollX,
						int scrollY, int scrollRangeX, int scrollRangeY,
						int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
	
					final float density = displayMetrics.density;
					if(!mIsFetchingMore && scrollY / density > 50.0){
						mIsFetchingMore  = true;
						doFetchMore(getListView(), getListView().getAdapter().getCount() - getListView().getHeaderViewsCount() - getListView().getFooterViewsCount(), getListPageSize());
					} else if(scrollY == 0){
						mIsFetchingMore  = false;
					}
				}
		    });
	    }
		getListView().addFooterView(footerLayout, null, false);
//		getListView().setEmptyView(getView().findViewById(android.R.id.empty));
		this.registerForContextMenu(getListView());
		if(this.getListAdapter() == null){
			ListAdapter adapter = useListViewAdapter();
			if(adapter instanceof SimpleCursorAdapter){
				((SimpleCursorAdapter)adapter).setViewBinder(this);
			} else if(adapter instanceof SimpleAdapter){
				((SimpleAdapter) adapter).setViewBinder(this);
			} else if(adapter instanceof HyjJSONListAdapter){
				((HyjJSONListAdapter) adapter).setViewBinder(this);
			}
			setListAdapter(adapter); 
		}
	}
	

	@Override
	public void onStart(){
		super.onStart();
		if(!mIsViewInited){
			onInitViewData();
			initLoader(0);
			mIsViewInited = true;
		}
	}
	
	public void initLoader(int loaderId){
		getLoaderManager().initLoader(loaderId, null,this);
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(!disableOptionsMenuView()){
			setHasOptionsMenu (true);
		}
	}
	
	protected boolean disableOptionsMenuView() {
		return false;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mOptionsMenu = menu;
	    // Inflate the menu items for use in the action bar
		if(useOptionsMenuView() != null){
			inflater.inflate(useOptionsMenuView(), menu);
		}
	    super.onCreateOptionsMenu(menu, inflater);
	}

	public Integer useToolbarView(){
		return null;
	}
	

	public Integer useOptionsMenuView(){
		return null;
	}
	
	public abstract Integer useContentView();

	public abstract ListAdapter useListViewAdapter();
	
	public void onInitViewData(){
		
	}

	public Menu getOptionsMenu(){
		return mOptionsMenu;
	}

	public void setFooterLoadStart(ListView l){
//		if(l.getAdapter().getCount() == 1){
//			if(mEmptyView != null){
//				mEmptyView.setText(R.string.app_listview_footer_fetching_more);			
//			}
//		} else {
            ((TextView)mFooterView).setText(R.string.app_listview_footer_fetching_more);
            ((TextView)mFooterView).setEnabled(false);
//		}
	}
	
	public void setFooterLoadFinished(ListView l, int count){
		((TextView)mFooterView).setEnabled(true);
		int offset = l.getFooterViewsCount() + l.getHeaderViewsCount();
		if(count >= l.getAdapter().getCount() + getListPageSize() - offset){
	        ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		} else if(count == 0 && l.getAdapter().getCount() == offset){
	        ((TextView)mFooterView).setText(R.string.app_listview_no_content);
	        ((TextView)mFooterView).setBackgroundColor(Color.TRANSPARENT);
	        ((TextView)mFooterView).setHeight(getListView().getHeight());
//	        if(mEmptyView != null){
//				mEmptyView.setText(R.string.app_listview_no_content);
//	        }
		} else {
		    ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_no_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		}
	}

	protected int getListPageSize() {
		return (int) (displayMetrics.heightPixels / displayMetrics.density / 40);
	}



	@Override
	public void onLoadFinished(Loader<Object> arg0, Object cursor) {
		int count = 0;
        if(cursor != null){
	        if(cursor instanceof Cursor){
	        	count = ((Cursor) cursor).getCount();
	        } else if(cursor instanceof List){
	        	count = ((List)cursor).size();
	        }
        }
        setFooterLoadFinished(getListView(), count);
        
		if(this.getListAdapter() instanceof CursorAdapter){
			((SimpleCursorAdapter) this.getListAdapter()).swapCursor((Cursor)cursor);
		}
		// The list should now be shown. 
        if (isResumed()) {
          //  setListShown(true);  
        } else {  
          //  setListShownNoAnimation(true);  
        } 
	}
	
	
	@Override
	public void onLoaderReset(Loader<Object> arg0) {
		if(this.getListAdapter() instanceof CursorAdapter){
			((SimpleCursorAdapter) this.getListAdapter()).swapCursor(null);
		}
	}	

	@Override
	public Loader<Object> onCreateLoader(int arg0, Bundle arg1) {
		setFooterLoadStart(getListView());
		return null;
	}
	
//	@Override  
//    public void onListItemClick(ListView l, View v, int position, long id) { 
//		super.onListItemClick(l, v, position, id);
//    }  
	
	public void doFetchMore(ListView l, int offset, int pageSize){
		Bundle bundle = new Bundle();
		bundle.putInt("OFFSET", offset);
		bundle.putInt("LIMIT", offset + pageSize);
		getLoaderManager().restartLoader(-1, bundle,this);
	}
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
//		AdapterContextMenuInfo mi =(AdapterContextMenuInfo) menuInfo;
//		if(mi.id == -1){
//			return;
//		}
//		menu.add(DELETE_LIST_ITEM, DELETE_LIST_ITEM, DELETE_LIST_ITEM, R.string.app_action_delete_list_item);
//		menu.add(CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, R.string.app_action_cancel_list_item);
//	}	
	
	public void onDeleteListItem(Long id){
	}
	

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		return false;
	}  	
	

	@Override
	public boolean setViewValue(View arg0, Object arg1, String arg2) {
		return false;
	}




	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, false, null);
	}
	
	public void openActivityWithFragmentForResult(Class<? extends Fragment> fragmentClass, int titleRes, Bundle bundle, int requestCode){
		openActivityWithFragment(fragmentClass, getString(titleRes), bundle, true, requestCode);
	}
	
	public void openActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this.getActivity(), HyjBlankUserActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			startActivityForResult(intent, requestCode);
		} else {
			startActivity(intent);
		}
	}

	public void openBlankActivityWithFragment(Class<? extends Fragment> fragmentClass, String title, Bundle bundle, boolean forResult, Integer requestCode){
		Intent intent = new Intent(this.getActivity(), HyjBlankActivity.class);
		HyjApplication.getInstance().addFragmentClassMap(fragmentClass.toString(), fragmentClass);
		intent.putExtra("FRAGMENT_NAME", fragmentClass.toString());
		intent.putExtra("TITLE", title);
		if(bundle != null){
			intent.putExtras(bundle);
		}
		if(forResult){
			startActivityForResult(intent, requestCode);
		} else {
			startActivity(intent);
		}
	}

	public boolean handleBackPressed() {
		boolean backPressedHandled = false;
		if(getChildFragmentManager().getFragments() != null){
			for(Fragment f : getChildFragmentManager().getFragments()){
				if(f instanceof HyjFragment){
					backPressedHandled = backPressedHandled || ((HyjFragment)f).handleBackPressed();
				} else if(f instanceof HyjListFragment){
					backPressedHandled = backPressedHandled || ((HyjListFragment)f).handleBackPressed();
				}  else if(f instanceof HyjUserExpandableListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserExpandableListFragment)f).handleBackPressed();
				} 
			}
		}
		return backPressedHandled;
	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		if(!getUserVisibleHint()){
//			return super.onContextItemSelected(item);
//		}
//		switch (item.getItemId()) {
//			case DELETE_LIST_ITEM:
//			    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//			    Long itemId = getListAdapter().getItemId(info.position);
//				onDeleteListItem(itemId);
//				break;
//			case CANCEL_LIST_ITEM:
//				break;
//		}
//		return super.onContextItemSelected(item);
//	}

}


