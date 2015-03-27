package com.hoyoji.hoyoji.event;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjUserFragment;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.android.hyjframework.view.HyjTextField;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.AppConstants;
import com.hoyoji.hoyoji.models.Event;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.sample.BaseUIListener;
import com.tencent.sample.Util;
import com.tencent.tauth.UiError;


public class InviteEventMemberFormFragment extends HyjUserFragment {
	private HyjTextField sendInviteTitle =null;
	private EditText sendInviteDetail =null;
	private HyjTextField mVerificationCode =null;
	private CheckBox mCheckBoxEventShareOwnerDataOnly = null;

	private Button mButtonSendInvite = null;
	
	private IWXAPI api;
	private QQShare mQQShare = null;
	public static QQAuth mQQAuth;
	
	private Intent intent = null;
	private Event event = null;
	
	@Override
	public Integer useContentView() {
		return R.layout.event_formfragment_invitemember;
	}
	
	@Override
	public void onInitViewData(){
		super.onInitViewData();
		intent = getActivity().getIntent();
		Long event_id = intent.getLongExtra("EVENT_ID", -1);
		event = Event.load(Event.class, event_id);
		
		final String way = intent.getStringExtra("INVITE_WAY");
		final String type = intent.getStringExtra("INVITE_TYPE");
		
		
		sendInviteTitle = (HyjTextField) getView().findViewById(R.id.inviteMemberMessageFormFragment_editText_title);
		sendInviteDetail = (EditText) getView().findViewById(R.id.inviteMemberMessageFormFragment_editText_detail);
		if(type.equals("invite")){
			sendInviteTitle.setText("邀请参加活动");
			sendInviteDetail.setText(HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您参加活动    " +event.getName());
		} else if(type.equals("signIn")){
			sendInviteTitle.setText("活动签到");
			sendInviteDetail.setText(HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您进行活动    " +event.getName() + "签到");
		}
		mVerificationCode = (HyjTextField) getView().findViewById(R.id.inviteEventMemberFormFragment_hyjTextField_verificationCode);
		
		mCheckBoxEventShareOwnerDataOnly = (CheckBox)getView().findViewById(R.id.projectEventMemberFormFragment_checkBox_shareAuthorization_self);

		mButtonSendInvite = (Button)getView().findViewById(R.id.button_send_invite);
		mButtonSendInvite.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				inviteFriend(way, event, event.getName(), type);
			}
		});
		if(way.equals("Other")) {
			mButtonSendInvite.setText("发送到其他好友");
		} else if(way.equals("QQ")) {
			mButtonSendInvite.setText("发送到QQ好友");
		} else if(way.equals("WX")) {
			mButtonSendInvite.setText("发送到微信好友");
		} else if(way.equals("WXCIRCLE")) {
			mButtonSendInvite.setText("发送到微信朋友圈");
		}
		mQQAuth = QQAuth.createInstance(AppConstants.TENTCENT_CONNECT_APP_ID, getActivity());
		mQQShare = new QQShare(getActivity(), mQQAuth.getQQToken());
	}

	public void inviteFriend(final String way, Event event,final String event_name, final String type) {
   	 	final HyjActivity activity = (HyjActivity) getActivity();
		activity.displayProgressDialog(R.string.friendListFragment__action_invite_title,R.string.friendListFragment__action_invite_content);
//		String emTitle = null;
//		String emDescription = null;
//		
//		if(type.equals("invite")){
//			emTitle = "邀请参加活动";
//			emDescription = HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您参加活动    " +event_name;
//		} else if(type.equals("signIn")){
//			emTitle = "活动签到";
//			emDescription = HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您进行活动    " +event_name + "签到";
//		}
		final String emTitleSent = sendInviteTitle.getText();
		final String emDescriptionSent = sendInviteDetail.getText()+"";
		
		JSONObject inviteFriendObject = new JSONObject();
		final String id = UUID.randomUUID().toString();
		try {
			inviteFriendObject.put("id", id);
			inviteFriendObject.put("data", event.toJSON().toString());
			inviteFriendObject.put("__dataType", "InviteLink");
			inviteFriendObject.put("title", emTitleSent);
			inviteFriendObject.put("type", "EventMember");
			inviteFriendObject.put("shareOwnerDataOnly", mCheckBoxEventShareOwnerDataOnly.isChecked());
			inviteFriendObject.put("verificationCode", mVerificationCode.getText().toString().trim());
			inviteFriendObject.put("date", (new Date()).getTime());
			inviteFriendObject.put("description", emDescriptionSent);
			inviteFriendObject.put("state", "Open");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
   	// 从服务器上下载用户数据
		HyjAsyncTaskCallbacks serverCallbacks = new HyjAsyncTaskCallbacks() {
			@Override
			public void finishCallback(Object object) {
				activity.dismissProgressDialog();
				String linkUrl = null;
				if(type.equals("invite")){
					linkUrl = HyjApplication.getInstance().getServerUrl()+"m/invite.html?id=" + id;
				} else if(type.equals("signIn")){
					linkUrl = HyjApplication.getInstance().getServerUrl()+"m/eventSignIn.html?id=" + id;
				}
					if(way.equals("Other")){
						inviteOtherFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
					} else if(way.equals("WX")){
						inviteWXFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
					} else if(way.equals("WXCIRCLE")){
						inviteWXCircleFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
					} else if(way.equals("QQ")){
						inviteQQFriend(linkUrl, event_name, emTitleSent, emDescriptionSent);
					}
			}

			@Override
			public void errorCallback(Object object) {
				activity.dismissProgressDialog();
				try {
					JSONObject json = (JSONObject) object;
					activity.displayDialog(null,
							json.getJSONObject("__summary")
									.getString("msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
   	 
   	 	HyjHttpPostAsyncTask.newInstance(serverCallbacks, "[" + inviteFriendObject.toString() + "]", "postData");
	 }

	public void inviteOtherFriend(String linkUrl, String event_name, String emTitleSent, String emDescriptionSent) {
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
	        intent.putExtra(Intent.EXTRA_TITLE, emTitleSent);  
	        intent.putExtra(Intent.EXTRA_SUBJECT, emTitleSent);   
	        intent.putExtra(Intent.EXTRA_TEXT, HyjApplication.getInstance().getCurrentUser().getDisplayName() + emTitleSent +event_name+"。\n\n" + linkUrl);  
	        
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
	        startActivity(Intent.createChooser(intent, emTitleSent)); 
	}
	
	public void inviteWXFriend(String linkUrl,String event_name, String emTitleSent, String emDescriptionSent) {
		HyjActivity activity = (HyjActivity) getActivity();
		api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = linkUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = emTitleSent;
		msg.description = emDescriptionSent;
		Bitmap thumb = BitmapFactory.decodeResource(((HyjActivity) getActivity()).getBaseContext().getResources(), R.drawable.ic_launcher);
		msg.thumbData = Util.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
//		req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		
	}
	
	public void inviteWXCircleFriend(String linkUrl,String event_name, String emTitleSent, String emDescriptionSent) {
		api = WXAPIFactory.createWXAPI(getActivity(), AppConstants.WX_APP_ID, false);
		api.registerApp(AppConstants.WX_APP_ID);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = linkUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = emDescriptionSent;
		msg.description = emDescriptionSent;
		try{
			Bitmap bmp = BitmapFactory.decodeResource(((HyjActivity) getActivity()).getBaseContext().getResources(), R.drawable.ic_launcher);
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
			bmp.recycle();
			msg.setThumbImage(thumbBmp);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
		
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	public void inviteQQFriend(String linkUrl,String event_name, String emTitleSent, String emDescriptionSent) {
		final HyjActivity activity = (HyjActivity) getActivity();
		final Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, emTitleSent);
	    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  emDescriptionSent);
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  linkUrl);
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, HyjApplication.getInstance().getServerUrl() + "imgs/invite_friend.png");
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "好友AA记账");
//	    params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");		
	    mQQShare.shareToQQ(activity, params, new BaseUIListener(activity) {

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
	 
}
