package com.hoyoji.hoyoji.event;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.hoyoji.android.hyjframework.HyjApplication;
import com.hoyoji.android.hyjframework.HyjAsyncTaskCallbacks;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.server.HyjHttpPostAsyncTask;
import com.hoyoji.hoyoji.AppConstants;
import com.hoyoji.hoyoji.models.Event;
import com.hoyoji.btcontrol.R;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tencent.sample.BaseUIListener;
import com.tencent.tauth.UiError;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class EventMemberDialogFragment extends DialogFragment {
	private IWXAPI api;
	private QQShare mQQShare = null;
	public static QQAuth mQQAuth;
	
	public static EventMemberDialogFragment newInstance(Bundle args) {
    	EventMemberDialogFragment f = new EventMemberDialogFragment();
        f.setArguments(args);
        return f;
    }

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
        // Inflate layout for the view
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        mQQAuth = QQAuth.createInstance(AppConstants.TENTCENT_CONNECT_APP_ID, getActivity());
		mQQShare = new QQShare(getActivity(), mQQAuth.getQQToken());
        
        Intent intent = getActivity().getIntent();
        final Long modelId = intent.getLongExtra("MODEL_ID", -1);
//		final Event event = Event.load(Event.class, modelId);

		final Bundle bundle = getArguments();
    	View v = inflater.inflate(R.layout.event_dialogfragment_member, null);
    	String dialog_type = bundle.getString("DIALOG_TYPE");
    	final Event event = Event.load(Event.class, bundle.getLong("EVENTID"));
    	
    	LinearLayout mInviteLinearLayout = (LinearLayout) v.findViewById(R.id.EventMemberDialogFragment_linearLayout_invite);
    	LinearLayout mSignInLinearLayout = (LinearLayout) v.findViewById(R.id.EventMemberDialogFragment_linearLayout_signIn);
//    	View mView = (View) v.findViewById(R.id.EventMemberDialogFragment_view);
    	if("invite".equals(dialog_type)) {
    		mInviteLinearLayout.setVisibility(View.VISIBLE);
//    		mSignInLinearLayout.setVisibility(View.VISIBLE);
//    		mView.setVisibility(View.VISIBLE);
    	} else if("signIn".equals(dialog_type)){
    		mSignInLinearLayout.setVisibility(View.VISIBLE);
    	}
    	
//    	v.findViewById(R.id.EventMemberDialogFragment_invite_friend).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				Bundle newBundle = new Bundle();
//				newBundle.putLong("EVENT_ID", modelId);
//				((HyjActivity)getActivity()).openActivityWithFragment(EventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, newBundle);
////				((HyjActivity)getActivity()).openActivityWithFragment(MoneyExpenseContainerFormFragment.class, R.string.moneyExpenseFormFragment_title_addnew, bundle);
//				dismiss();
//			}
//    	});
    	v.findViewById(R.id.EventMemberDialogFragment_invite_qq).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "QQ");
				bundle.putString("INVITE_TYPE", "invite");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("QQ", event, event.getName(), "invite");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyIncomeContainerFormFragment.class, R.string.moneyIncomeFormFragment_title_addnew, bundle);
			}
    	});    	
    	v.findViewById(R.id.EventMemberDialogFragment_invite_wx).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "WX");
				bundle.putString("INVITE_TYPE", "invite");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("WX", event, event.getName(), "invite");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, bundle);
//				dismiss();
			}
    	});
    	v.findViewById(R.id.EventMemberDialogFragment_invite_wxCircle).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "WXCIRCLE");
				bundle.putString("INVITE_TYPE", "invite");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("WX", event, event.getName(), "invite");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, bundle);
//				dismiss();
			}
    	});
    	v.findViewById(R.id.EventMemberDialogFragment_invite_other).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "Other");
				bundle.putString("INVITE_TYPE", "invite");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("Other", event, event.getName(), "invite");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositIncomeContainerFormFragment.class, R.string.moneyDepositIncomeContainerFormFragment_title_addnew, bundle);
//				dismiss();
			}
    	});
    	v.findViewById(R.id.EventMemberDialogFragment_signIn_qq).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "QQ");
				bundle.putString("INVITE_TYPE", "signIn");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("QQ", event, event.getName(), "signIn");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositReturnContainerFormFragment.class, R.string.moneyDepositReturnContainerFormFragment_title_addnew, bundle);
//				dismiss();
			}
    	});
    	v.findViewById(R.id.EventMemberDialogFragment_signIn_wx).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "WX");
				bundle.putString("INVITE_TYPE", "signIn");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("WX", event, event.getName(), "signIn");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositPaybackContainerFormFragment.class, R.string.moneyDepositPaybackFormFragment_title_addnew, bundle);
//				dismiss();
			}
    	});
    	v.findViewById(R.id.EventMemberDialogFragment_signIn_wxCircle).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "WXCIRCLE");
				bundle.putString("INVITE_TYPE", "signIn");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("WX", event, event.getName(), "invite");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyDepositExpenseContainerFormFragment.class, R.string.moneyDepositExpenseFormFragment_title_addnew, bundle);
//				dismiss();
			}
    	});
    	v.findViewById(R.id.EventMemberDialogFragment_signIn_other).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putLong("EVENT_ID", modelId);
				bundle.putString("INVITE_WAY", "Other");
				bundle.putString("INVITE_TYPE", "signIn");
				((HyjActivity)getActivity()).openActivityWithFragment(InviteEventMemberFormFragment.class, R.string.projectEventMemberFormFragment_action_addnew, bundle);
				dismiss();
//				inviteFriend("Other", event, event.getName(), "signIn");
//				((HyjActivity)getActivity()).openActivityWithFragment(MoneyTemplateListFragment.class, R.string.moneyTemplateListFragment_title, null);
//				dismiss();
			}
    	});

        // Use the Builder class for convenient dialog construction
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the layout for the dialog
        builder.setView(v);

        // Set title of dialog
        if("invite".equals(dialog_type)) {
        	builder.setTitle("发送邀请链接")
//          // Set Ok button
//          .setPositiveButton(R.string.alert_dialog_ok,
//                  new DialogInterface.OnClickListener() {
//                      public void onClick(DialogInterface dialog, int id) {
//      			        
//                      }
//                  })
          // Set Cancel button
          .setNegativeButton(R.string.alert_dialog_cancel, null); 
    	} else if("signIn".equals(dialog_type)){
    		builder.setTitle("发送签到链接")
//          // Set Ok button
//          .setPositiveButton(R.string.alert_dialog_ok,
//                  new DialogInterface.OnClickListener() {
//                      public void onClick(DialogInterface dialog, int id) {
//      			        
//                      }
//                  })
          // Set Cancel button
          .setNegativeButton(R.string.alert_dialog_cancel, null); 
    	}
        

        // Create the AlertDialog object and return it
        return builder.create();
    }
    
    protected void openActivityWithFragment(
			Class<EventMemberFormFragment> class1,
			int projecteventmemberformfragmentActionAddnew, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}

	public void inviteFriend(final String way, Event event,final String event_name, final String type) {
   	 	final HyjActivity activity = (HyjActivity) getActivity();
		activity.displayProgressDialog(R.string.friendListFragment__action_invite_title,R.string.friendListFragment__action_invite_content);
		String emTitle = null;
		String emDescription = null;
		
		if(type.equals("invite")){
			emTitle = "邀请参加活动";
			emDescription = HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您参加活动    " +event_name;
		} else if(type.equals("signIn")){
			emTitle = "活动签到";
			emDescription = HyjApplication.getInstance().getCurrentUser().getDisplayName() + " 邀请您进行活动    " +event_name + "签到";
		}
		final String emTitleSent = emTitle;
		final String emDescriptionSent = emDescription;
		
		JSONObject inviteFriendObject = new JSONObject();
		final String id = UUID.randomUUID().toString();
		try {
			inviteFriendObject.put("id", id);
			inviteFriendObject.put("data", event.toJSON().toString());
			inviteFriendObject.put("__dataType", "InviteLink");
			inviteFriendObject.put("title", emTitle);
			inviteFriendObject.put("type", "EventMember");
			inviteFriendObject.put("date", (new Date()).getTime());
			inviteFriendObject.put("description", emDescription);
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
            	dismiss();
//            		Util.toastMessage(getActivity(), "onCancel: ");
            }

            @Override
            public void onComplete(Object response) {
            	dismiss();
//                Util.toastMessage(getActivity(), "onComplete: " + response.toString());
            }

            @Override
            public void onError(UiError e) {
            	dismiss();
//                Util.toastMessage(getActivity(), "onError: " + e.errorMessage, "e");
            }

        });
	}
	
}