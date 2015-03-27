package com.hoyoji.android.hyjframework.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.hoyoji.android.hyjframework.HyjUtil;
import com.hoyoji.android.hyjframework.activity.HyjActivity;
import com.hoyoji.android.hyjframework.fragment.HyjImagePreviewFragment;
import com.hoyoji.btcontrol.R;
import com.hoyoji.hoyoji.models.Picture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class HyjImageField extends GridView {
	private ImageGridAdapter mImageGridAdapter;
	private Resources r = getResources();

	public HyjImageField(Context context, AttributeSet attrs) {
		super(context, attrs);
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				56, r.getDisplayMetrics());
		this.setColumnWidth(px);
		this.setNumColumns(AUTO_FIT);
		this.setGravity(Gravity.CENTER);
		px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
				r.getDisplayMetrics());
		this.setVerticalSpacing(px);
		this.setHorizontalSpacing(px);
		// this.setStretchMode(STRETCH_COLUMN_WIDTH);
		this.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		mImageGridAdapter = new ImageGridAdapter(context, 0);
		this.setAdapter(mImageGridAdapter);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = getMeasuredHeight();
	}

	public void setImages(List<Picture> pictures) {
		// List<PictureItem> pis = new ArrayList<PictureItem>();
		for (int i = 0; i < pictures.size(); i++) {
			PictureItem pi = new PictureItem(pictures.get(i));
			mImageGridAdapter.add(pi);
			// pis.add(pi);
		}
		// mImageGridAdapter.addAll(pis);
	}

	public ImageGridAdapter getAdapter() {
		return mImageGridAdapter;
	}

	public void takePictureFromCamera() {
		Picture newPicture = new Picture();
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(this.getContext()
				.getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = HyjUtil.createImageFile(newPicture.getId());
				// Continue only if the File was successfully created
				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					((HyjActivity) getContext()).startActivityForResult(
							takePictureIntent, HyjActivity.REQUEST_TAKE_PHOTO);

					IntentFilter intentFilter = new IntentFilter(
							"REQUEST_TAKE_PHOTO");
					BroadcastReceiver receiver = new TakePhotoBroadcastReceiver(
							photoFile, newPicture);
					getContext().registerReceiver(receiver, intentFilter);
				} else {
					HyjUtil.displayToast(R.string.imageField_cannot_save_picture);
				}
			} catch (IOException ex) {
				// Error occurred while creating the File
				HyjUtil.displayToast(R.string.imageField_cannot_save_picture);
			}
		}
	}

	public void pickPictureFromGallery() {
		Picture newPicture = new Picture();
		Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(this.getContext()
				.getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = HyjUtil.createImageFile(newPicture.getId());
				// Continue only if the File was successfully created
				if (photoFile != null) {
					((HyjActivity) getContext()).startActivityForResult(
							takePictureIntent, HyjActivity.REQUEST_TAKE_PHOTO);

					IntentFilter intentFilter = new IntentFilter(
							"REQUEST_TAKE_PHOTO");
					BroadcastReceiver receiver = new TakePhotoBroadcastReceiver(
							photoFile, newPicture);
					getContext().registerReceiver(receiver, intentFilter);
				} else {
					HyjUtil.displayToast(R.string.imageField_cannot_save_picture);
				}
			} catch (IOException ex) {
				// Error occurred while creating the File
				HyjUtil.displayToast(R.string.imageField_cannot_save_picture);
			}
		}
	}

	public static class ImageGridAdapter extends ArrayAdapter<PictureItem> {
		public ImageGridAdapter(Context context, int resource) {
			super(context, resource);
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HyjImageView iv;
			final HyjImageField self = (HyjImageField) parent;
			if (convertView != null) {
				iv = (HyjImageView) convertView;
			} else {
				iv = new HyjImageView(this.getContext());
				float px = TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 56,
						self.r.getDisplayMetrics());
				iv.setLayoutParams(new LayoutParams((int) px, (int) px));
				// iv.setPadding(0, 0, 0, 0);
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PictureItem pic = (PictureItem) v.getTag();
						if(pic.getState() == PictureItem.DELETED){
							pic.setState(PictureItem.UNCHANGED);
							ImageView iv = (ImageView)v;
							iv.setImageDrawable(iv.getBackground());
							if(android.os.Build.VERSION.SDK_INT < 16){
								iv.setBackgroundDrawable(null);
							} else {
								v.setBackground(null);
							}
						} else {
							Bundle bundle = new Bundle();
							bundle.putString("pictureName", pic.getPicture().getId());
							bundle.putString("pictureType", pic.getPicture().getPictureType());
							((HyjActivity) getContext()).openActivityWithFragment(HyjImagePreviewFragment.class, R.string.app_preview_picture, bundle);
						}
					}
				});
				iv.setOnLongClickListener(new OnLongClickListener(){
					@Override
					public boolean onLongClick(final View v) {
						PopupMenu popup = new PopupMenu(((HyjActivity) getContext()), v);
						MenuInflater inflater = popup.getMenuInflater();
						inflater.inflate(R.menu.imagefield_image, popup.getMenu());
						popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
//								if (item.getItemId() == R.id.imagefield_image_delete) {
////									mImageFieldPicture.takePictureFromCamera();
//									return true;
//								} else {
////									mImageFieldPicture.pickPictureFromGallery();
//									return true;
//								}

								PictureItem pic = (PictureItem) v.getTag();
								if(pic.getState() == PictureItem.NEW){
									ImageGridAdapter.this.remove(pic);
									ImageGridAdapter.this.notifyDataSetChanged();
								} else {
									pic.setState(PictureItem.DELETED);
									ImageView iv = (ImageView)v;
									if(android.os.Build.VERSION.SDK_INT < 16){
										iv.setBackgroundDrawable(iv.getDrawable());
									} else {
										v.setBackground(iv.getDrawable());
									}
									iv.setImageResource(R.drawable.ic_action_discard_red);
								}
								return true;
							}
						});
						popup.show();
						return true;
					}

				});
			}

			PictureItem pic = getItem(position);
			iv.setTag(pic);
			// File imageFile;
			// try {
			// imageFile =
			// HyjUtil.createImageFile(pic.getPicture().getId()+"_icon",
			// pic.getPicture().getPictureType());
			// iv.setImageURI(Uri.fromFile(imageFile));
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			if(pic.getState() == PictureItem.DELETED){
//				iv.setImage(pic.getPicture());
//				iv.setBackground(iv.getDrawable());

				 File imageFile;
				 try {
					 imageFile = HyjUtil.createImageFile(pic.getPicture().getId()+"_icon", pic.getPicture().getPictureType());
					 iv.setImageURI(Uri.fromFile(imageFile));
					 if(android.os.Build.VERSION.SDK_INT < 16){
						iv.setBackgroundDrawable(null);
					 } else {
						iv.setBackground(iv.getDrawable());
					 }
					 iv.setImageResource(R.drawable.ic_action_discard_red);
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			} else {
				iv.setImage(pic.getPicture());
				if(android.os.Build.VERSION.SDK_INT < 16){
					iv.setBackgroundDrawable(null);
				} else {
					iv.setBackground(null);
				}
			}
			return iv;
		}

	}

	public static class PictureItem {
		public static final int UNCHANGED = 0;
		public static final int NEW = 1;
		public static final int DELETED = 2;
		public static final int CHANGED = 3;

		private int mState = UNCHANGED;
		private Picture mPicture;

		PictureItem(Picture picture) {
			mPicture = picture;
		}

		PictureItem(Picture picture, int state) {
			mPicture = picture;
			mState = state;
		}

		public void setState(int state) {
			mState = state;
		}

		public int getState() {
			return mState;
		}

		public Picture getPicture() {
			return mPicture;
		}
	}

	// private void galleryAddPic(String picturePath) {
	// Intent mediaScanIntent = new
	// Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	// File f = new File(picturePath);
	// Uri contentUri = Uri.fromFile(f);
	// mediaScanIntent.setData(contentUri);
	// this.getContext().sendBroadcast(mediaScanIntent);
	// }

	private class TakePhotoBroadcastReceiver extends BroadcastReceiver {
		File mPhotoFile;
		Picture mPicture;

		TakePhotoBroadcastReceiver(File photoFile, Picture picture) {
			mPhotoFile = photoFile;
			mPicture = picture;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				getContext().unregisterReceiver(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (intent.getAction().equals("REQUEST_TAKE_PHOTO")) {
				int result = intent.getIntExtra("resultCode",
						Activity.RESULT_CANCELED);
				if (result == Activity.RESULT_OK) {
					float pxW = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 400,
							r.getDisplayMetrics());
					float pxH = TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 600,
							r.getDisplayMetrics());
					FileOutputStream out = null;
					String picturePath;
					
					if(intent.getStringExtra("selectedImage") != null){
						Uri selectedImage = Uri.parse(intent.getStringExtra("selectedImage"));
						String[] filePathColumn = { MediaStore.Images.Media.DATA };
						Cursor cursor = getContext().getContentResolver()
								.query(selectedImage, filePathColumn, null,
										null, null);
						cursor.moveToFirst();

						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						picturePath = cursor.getString(columnIndex);
						cursor.close();
					} else {
						picturePath = mPhotoFile.getAbsolutePath();
					}

					Bitmap scaled = HyjUtil.decodeSampledBitmapFromFile(
							picturePath, (int) pxW, (int) pxH);

					int px = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 56,
							r.getDisplayMetrics());
					Bitmap thumbnail = ThumbnailUtils.extractThumbnail(
							scaled, px, px);
					
					try {
						out = new FileOutputStream(mPhotoFile);
						scaled.compress(Bitmap.CompressFormat.JPEG, 60, out);
						out.close();
						out = null;
						File imageFile = HyjUtil.createImageFile(mPicture.getId() + "_icon");
					    if(imageFile != null){
							out = new FileOutputStream(imageFile);
							thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, out);
							out.close();
							out = null;
					    }
						thumbnail.recycle();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						out = null;
					}

					scaled.recycle();

					mPicture.setPictureType("JPEG");
					PictureItem pi = new PictureItem(mPicture, PictureItem.NEW);
					mImageGridAdapter.add(pi);
				} else {
					if (!mPhotoFile.exists()) {
						//HyjUtil.displayToast(R.string.imageField_cannot_save_picture);
					} else {
						mPhotoFile.delete();
					}
				}
			}
		}
	}
}
