package com.hoyoji.android.hyjframework.fragment;

import java.util.List;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.server.HyjJSONListAdapter;
import com.hoyoji.android.hyjframework.view.HyjListView;
import com.hoyoji.android.hyjframework.view.HyjListView.OnOverScrollByListener;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.activity.HyjBlankUserActivity;
import com.hoyoji.btcontrol.R;
import android.app.Activity;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public abstract class HyjUserListFragment extends ListFragment implements 
	LoaderManager.LoaderCallbacks<Object>, 
	SimpleCursorAdapter.ViewBinder, 
	SimpleAdapter.ViewBinder, OnItemLongClickListener{
	
//	public final static int DELETE_LIST_ITEM = 1024;
//	public final static int CANCEL_LIST_ITEM = 1025;
	private boolean mIsViewInited = false;
	protected View mFooterView;
//	protected TextView mEmptyView;
//	protected int mListPageSize = 10;
	protected Menu mOptionsMenu;
	protected static DisplayMetrics displayMetrics;
	protected View mHeaderView;
	private View mMultiSelectActionBarView;
	private TextView mSelectedCount;
	private boolean mRestoreHomeAsUp = false;
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			displayMetrics = getResources().getDisplayMetrics();
		
			//View v = super.onCreateView(inflater, container, savedInstanceState);
			ViewGroup rootView = (ViewGroup) inflater.inflate(useContentView(), container, false);
			//rootView.addView(v, 0);
			if(useToolbarView() != null){
				// populate bottom toolbar
			}
//			mEmptyView = (TextView)rootView.findViewById(android.R.id.empty);
			return rootView;
	}
	
	protected int getListPageSize() {
		return (int) (displayMetrics.heightPixels / displayMetrics.density / 40);
	}
	
	protected View useHeaderView(Bundle savedInstanceState){
		return null;
	}

	public int getHeaderHeight(){
		if(mHeaderView != null){
			return mHeaderView.getHeight();
		} 
		return 0;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		mHeaderView = useHeaderView(savedInstanceState); 
		if(mHeaderView != null){
			getListView().addHeaderView(mHeaderView);
		}
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
					if(!mIsFetchingMore  && scrollY / density > 50.0){
						mIsFetchingMore   = true;
						doFetchMore(getListView(), getListView().getAdapter().getCount() - getListView().getHeaderViewsCount() - getListView().getFooterViewsCount(), getListPageSize());
					} else if(scrollY == 0){
						mIsFetchingMore  = false;
					}
				}
		    });
	    }
		getListView().addFooterView(footerLayout, null, false);
//		getListView().setEmptyView(getView().findViewById(android.R.id.empty));
//		this.registerForContextMenu(getListView());
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
		if(HyjApplication.getInstance().isLoggedIn()) {
	        //setListShown(false);  
			if(!mIsViewInited){
				onInitViewData();
				initLoader(0);
				mIsViewInited = true;
			}
		}
	}
	
	public void initLoader(int loaderId){
		Bundle bundle = new Bundle();
		bundle.putInt("OFFSET", 0);
		bundle.putInt("LIMIT", getListPageSize());
		getLoaderManager().initLoader(loaderId, bundle,this);
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
		
		// will throw ensureList: content view not yet created. so we check if getView() != null
		if(getView() != null && getListView().getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			menu.clear();
			if(getActivity().getCallingActivity() == null){
				if(useMultiSelectMenuView() != null){
					inflater.inflate(useMultiSelectMenuView(), menu);
				}
			} else {
				if(useMultiSelectMenuPickerView() != null){
					inflater.inflate(useMultiSelectMenuPickerView(), menu);
				}
			}
		} else {
			if(useOptionsMenuView() != null){
				inflater.inflate(useOptionsMenuView(), menu);
			} 
		}
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.multi_select_menu_ok){
			returnSelectedItems();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void returnSelectedItems() {
		long[] ids = getListView().getCheckedItemIds();
		if(ids.length == 0){
			HyjUtil.displayToast("请选择至少一条记录");
			return;
		}

		Intent intent = new Intent();
		intent.putExtra("MODEL_IDS", ids);
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
		
	}

	public Integer useToolbarView(){
		return null;
	}
	

	public Integer useOptionsMenuView(){
		return null;
	}
	
	public Integer useMultiSelectMenuView(){
//		return R.menu.multi_select_menu;
		return null;
	}

	public Integer useMultiSelectMenuPickerView() {
//		return R.menu.multi_select_menu_picker;
		return null;
	}
	
	public abstract Integer useContentView();

	public abstract ListAdapter useListViewAdapter();
	
	public void onInitViewData(){
		getListView().setOnItemLongClickListener(this);
	}

	public Menu getOptionsMenu(){
		return mOptionsMenu;
	}

	public void enterMultiChoiceMode(final ListView listView, int position){
		if(getActivity().getIntent().getBooleanExtra("disableMultiChoiceMode", false)){
			return;
		}
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setItemChecked(position, true);
		((HyjActivity)getActivity()).setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
		mRestoreHomeAsUp = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) == ActionBar.DISPLAY_HOME_AS_UP;
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
		if (mMultiSelectActionBarView == null) {
		      mMultiSelectActionBarView = LayoutInflater.from(getActivity()).inflate(R.layout.multi_select_actionbar, null);
		      mSelectedCount = (TextView)mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_count);
		      mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_close).setOnClickListener(new OnClickListener(){
			    	@Override
					public void onClick(View v) {
			    		exitMultiChoiceMode(listView);
					}
		      });
		      mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_select_all).setOnClickListener(new OnClickListener(){
			    	@Override
					public void onClick(View v) {
						for(int g = listView.getHeaderViewsCount(); g < listView.getAdapter().getCount() - listView.getFooterViewsCount(); g++){
								listView.setItemChecked(g, true);
						}
						mSelectedCount.setText(listView.getCheckedItemIds().length + "");
					}
		      });
		      mMultiSelectActionBarView.findViewById(R.id.multi_select_menu_select_clear).setOnClickListener(new OnClickListener(){
			    	@Override
					public void onClick(View v) {
			    		listView.clearChoices();
			    		if(listView.getAdapter().getCount() > 0){
			    			getListView().setItemChecked(0, false);
			    		}
						mSelectedCount.setText(listView.getCheckedItemIds().length + "");
					}
		      });
			  actionBar.setCustomView(mMultiSelectActionBarView);
			  listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if(listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
						mSelectedCount.setText(listView.getCheckedItemIds().length + "");
					} else {
						onListItemClick(listView, arg1, arg2, arg3);
					}
				}
			  });
		}
		mSelectedCount.setText(listView.getCheckedItemIds().length + "");
		getActivity().supportInvalidateOptionsMenu();
	}
	
	public void exitMultiChoiceMode(final ListView listView){
		listView.clearChoices();
		if(listView.getAdapter().getCount() > 0){
			listView.setItemChecked(0, false);
		}
		listView.post(new Runnable(){
			@Override
			public void run() {
				listView.setChoiceMode(ListView.CHOICE_MODE_NONE);	
				((HyjActivity)getActivity()).setChoiceMode(ListView.CHOICE_MODE_NONE);
				getActivity().supportInvalidateOptionsMenu();
			}
		});

		ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();

//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_CUSTOM);

		if(mRestoreHomeAsUp ){
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowCustomEnabled(false);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> l, View view,
			int position, long id) {
		final ListView listView = (ListView)l;
		if(listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
			exitMultiChoiceMode(listView);
			return true;
		} else {	
			if(getActivity().getCallingActivity() != null){
				if(this.useMultiSelectMenuPickerView() != null){
					enterMultiChoiceMode(listView, position);
					return true;
				}
			} else {
				if(this.useMultiSelectMenuView() != null){
					enterMultiChoiceMode(listView, position);
					return true;
				}
			}
		}
		return false;
	}
		
	
	
	public void setFooterLoadStart(ListView l){
//        if(l.getItemAtPosition(0) == null){
//        	((TextView)mFooterView).setText(R.string.app_listview_footer_fetching_more);
//        } else {
//        }

//		if(l.getAdapter().getCount() == l.getHeaderViewsCount() + l.getFooterViewsCount()){
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
        int offset = l.getHeaderViewsCount() + l.getFooterViewsCount();
		if(count >= l.getAdapter().getCount() + getListPageSize() - offset){
	        ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		} else if(count == 0 && l.getAdapter().getCount() == offset){
	        ((TextView)mFooterView).setText(getNoContentText());
	        ((TextView)mFooterView).setBackgroundColor(Color.TRANSPARENT);
	        ((TextView)mFooterView).setHeight(getListView().getHeight()-getHeaderHeight());
//	        if(mEmptyView != null){
//				mEmptyView.setText(R.string.app_listview_no_content);
//	        }
		} else {
		    ((TextView)mFooterView).setText(R.string.app_listview_footer_fetch_no_more);
	        ((TextView)mFooterView).setBackgroundColor(getResources().getColor(R.color.hoyoji_lightgray));
	        ((TextView)mFooterView).setHeight(48);
		}
	}

	protected CharSequence getNoContentText() {
		return getResources().getString(R.string.app_listview_no_content);
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
//		if(l.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE){
//			return;
//		}
//    }  
	
	
	
	public void doFetchMore(ListView l, int offset, int pageSize){
//		if(getLoaderManager().getLoader(0) != null && getLoaderManager().getLoader(0).isStarted()){
//			return;
//		}
		Bundle bundle = new Bundle();
		bundle.putInt("OFFSET", offset);
		bundle.putInt("LIMIT", offset + pageSize);
		getLoaderManager().restartLoader(0, bundle, this);
	}
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
////		AdapterContextMenuInfo mi =(AdapterContextMenuInfo) menuInfo;
////		if(mi.id == -1){
////			return;
////		}
////		menu.add(DELETE_LIST_ITEM, DELETE_LIST_ITEM, DELETE_LIST_ITEM, R.string.app_action_delete_list_item);
////		menu.add(CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, CANCEL_LIST_ITEM, R.string.app_action_cancel_list_item);
//	}	
	
//	public void onDeleteListItem(Long id){
//	}
	

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

	public boolean handleBackPressed() {
		boolean backPressedHandled = false;
		if(getChildFragmentManager().getFragments() != null){
			for(Fragment f : getChildFragmentManager().getFragments()){
				if(f instanceof HyjFragment){
					backPressedHandled = backPressedHandled || ((HyjFragment)f).handleBackPressed();
				} else if(f instanceof HyjUserListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserListFragment)f).handleBackPressed();
				}  else if(f instanceof HyjUserExpandableListFragment){
					backPressedHandled = backPressedHandled || ((HyjUserExpandableListFragment)f).handleBackPressed();
				} 
			}
		} else if(getListView().getChoiceMode() != ListView.CHOICE_MODE_NONE){
			backPressedHandled = true;
			this.exitMultiChoiceMode(getListView());
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


