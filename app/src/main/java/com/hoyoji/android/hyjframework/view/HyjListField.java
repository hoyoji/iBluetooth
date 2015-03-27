package com.hoyoji.android.hyjframework.view;

import com.hoyoji.btcontrol.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HyjListField extends LinearLayout {
	private String mLabelText;
	private TextView mTextViewLabel;
	private ListView mListView;
	private Button mAddButton;

	public HyjListField(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.HyjTextField, 0, 0);

		try {
			mLabelText = a.getString(R.styleable.HyjTextField_labelText);
		} finally {
			a.recycle();
		}
		
		final LayoutInflater inflater = (LayoutInflater)
			       context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.list_field, this);
		mTextViewLabel = (TextView)findViewById(R.id.text_field_label);
		mListView = (ListView)findViewById(R.id.list_field_listView);
		mAddButton = (Button)findViewById(R.id.list_field_listViewHeader);
		mTextViewLabel.setText(mLabelText);

//		View emptyView = inflater.inflate(R.layout.list_field_emptyview, null);
//		View headerView = inflater.inflate(R.layout.list_field_headerview, null);	
//		mListView.setEmptyView(emptyView);
//		mListView.addHeaderView(headerView, null, false);
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
	
	public void setError(String error){
	}
	
	public void setOnAddItemListener(OnClickListener onClickListener){
		mAddButton.setOnClickListener(onClickListener);
	}
	
	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener){
		mListView.setOnItemClickListener(onItemClickListener);
	}
	
	public void setListAdapter(ListAdapter adapter){
		mListView.setAdapter(adapter);
	}

	public void setEnabled(boolean enabled){
		if(!enabled){
			mAddButton.setVisibility(View.GONE);
		} else {
			mAddButton.setVisibility(View.VISIBLE);
		}
	}
}
