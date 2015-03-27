package com.hoyoji.hoyoji.setting;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.fragment.HyjFragment;
import com.hoyoji.btcontrol.R;

public class AboutFragment extends HyjFragment {

	@Override
	public Integer useContentView() {
		return R.layout.setting_fragment_about;
	}

	@Override
	public void onInitViewData() {
		getView().findViewById(R.id.aboutFragment_button_checkUpdate)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						HyjUtil.displayToast("该功能尚未完善，请关注后续版本");
					}
				});

		getView().findViewById(R.id.aboutFragment_textView_website)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						Uri uri = Uri.parse("http://www.hoyoji.com");
//						Intent it = new Intent(Intent.ACTION_VIEW,uri);
//						startActivity(it);
					}
				});

		getView().findViewById(R.id.aboutFragment_textView_email)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String[] receiver = new String[]{"help@hoyoji.com"};
						String subject = "提交bug";
						String content = "test";
						
						Intent email = new Intent(Intent.ACTION_SEND);  
		                email.setType("message/rfc822");  
		                // 设置邮件发收人  
		                email.putExtra(Intent.EXTRA_EMAIL, receiver);  
		                // 设置邮件标题  
		                email.putExtra(Intent.EXTRA_SUBJECT, subject);  
		                // 设置邮件内容  
		                email.putExtra(Intent.EXTRA_TEXT, content);               
		                  
		                // 调用系统的邮件系统  
		                startActivity(Intent.createChooser(email, "请选择邮件发送软件"));  
					}
				});

		getView().findViewById(R.id.aboutFragment_textView_sinaBlog)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						Uri uri = Uri.parse("http://weibo.com/hoyoji");
//						Intent it = new Intent(Intent.ACTION_VIEW,uri);
//						startActivity(it);
					}
				});

		getView().findViewById(R.id.aboutFragment_textView_tencentBlog)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
//						Uri uri = Uri.parse("http://weibo.com/hoyoji");
//						Intent it = new Intent(Intent.ACTION_VIEW,uri);
//						startActivity(it);
					}
				});
	}
}
