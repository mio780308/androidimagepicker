package com.mio780308.multipleimagepicker;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFolderFragment extends Fragment implements AdapterView.OnItemClickListener {
	
	public interface ImageFolderFragmentListener{
		public void OnFolderClicked(ImageFolder folder);
	}
	
	private class ImageFolderAdapter extends BaseAdapter{
		
		private ArrayList<ImageFolder> mImageFolders;
		private Context mContext;
		private ImageLoader mImageLoader;
		private int itemPixelSize;
		private GridView.LayoutParams mLayoutParams;
		
		public ImageFolderAdapter(Context context,ArrayList<ImageFolder> folderList,ImageLoader imgLoader,int itemSize){
			mContext=context;
			mImageFolders=folderList;
			mImageLoader=imgLoader;
			itemPixelSize=itemSize;
			mLayoutParams= new GridView.LayoutParams(itemPixelSize,itemPixelSize);
		}
		
		@Override
		public int getCount() {
			return mImageFolders.size();
		}
		
		@Override
		public Object getItem(int position) {
			return mImageFolders.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageFolder folder=getImageFolderAt(position);
			View v=convertView;
			if(v==null){
				LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v=inflater.inflate(R.layout.row_image_picker_item, parent, false);
				v.setLayoutParams(mLayoutParams);
			}
			
			ImageView image=(ImageView) v.findViewById(R.id.imageView1);
			
			mImageLoader.displayImage("file:/"+folder.getRandomImages(1).get(0).imagePath, image);
			
			TextView folderName=(TextView) v.findViewById(R.id.textView1);
			TextView folderSize=(TextView) v.findViewById(R.id.textView2);
			folderName.setText(folder.getName());
			folderSize.setText(folder.getCount()+"");
			
			return v;
		}
		
		public ImageFolder getImageFolderAt(int position){
			return (ImageFolder)getItem(position);
		}
		
	}
	
	private ArrayList<ImageFolder> folderList;
	private ImageLoader imageLoader;
	private GridView mGridView;
	private ImageFolderAdapter mGridViewAdapter;
	private ImageFolderFragmentListener mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback=(ImageFolderFragmentListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		imageLoader = ImageLoader.getInstance();
		queryImages();
		
		View v=inflater.inflate(R.layout.fragment_image_picker, container, false);
		
		mGridView=(GridView)v.findViewById(R.id.gridview);
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int columnWidth=
			(Math.min(localDisplayMetrics.heightPixels, localDisplayMetrics.widthPixels)-
					4*this.getResources().getDimensionPixelSize(R.dimen.block_image_padding))/3;
		mGridView.setColumnWidth(columnWidth);
		//mGridView.setNumColumns(3);
		mGridViewAdapter=new ImageFolderAdapter(getActivity(),folderList,imageLoader,columnWidth);
		mGridView.setAdapter(mGridViewAdapter);
		mGridView.setOnItemClickListener(this);
		
		return v;
	}
	
	private void queryImages(){
		final String[] columns={MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
		final String orderBy=MediaStore.Images.Media.DATE_MODIFIED+" desc";
		
		Cursor imageCursor=getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,null,null,orderBy);
		
		folderList=new ArrayList<ImageFolder>();
		//imageURIs=new ArrayList<String>();
		
		for(int i=0;i<imageCursor.getCount();i++){	
			imageCursor.moveToPosition(i);
			//Log.i(Constants.LOG_TAG, imageCursor.getString(1)+":"+imageCursor.getString(0));
			//Log.v(Constants.LOG_TAG, MediaStore.Images.Media.getContentUri(imageCursor.getString(0)).toString());
			//imageURIs.add(imageCursor.getString(0));
			//Log.d(Constants.LOG_TAG, getDirectory(imageCursor.getString(0)));
			
			/*
			Cursor thumbnailCursor=getActivity().getContentResolver().query(
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
					new String[]{
						MediaStore.Images.Thumbnails.DATA,
						MediaStore.Images.Thumbnails.IMAGE_ID
					},MediaStore.Images.Thumbnails.IMAGE_ID+" =?",new String[]{imageCursor.getString(1)},null);
			for(int j=0;j<thumbnailCursor.getCount();j++){	
				thumbnailCursor.moveToPosition(j);
				//Log.d(Constants.LOG_TAG, thumbnailCursor.getString(1)+":"+thumbnailCursor.getString(0));
				//Log.v(Constants.LOG_TAG, MediaStore.Images.Thumbnails.getContentUri(thumbnailCursor.getString(0)).toString());
				//imageURIs.add(thumbnailCursor.getString(0));
				//Log.d(Constants.LOG_TAG, getDirectory(thumbnailCursor.getString(0)));
				addImageToFolders(new MyImage(imageCursor.getString(0),thumbnailCursor.getString(0)));
			}
			
			thumbnailCursor.close();
			*/
			addImageToFolders(new MyImage(imageCursor.getString(0)));
		}
		imageCursor.close();
		//Log.d(Constants.LOG_TAG,imageCursor.getCount()+":"+thumbnailCursor.getCount());
		
	}
	
	private String getFolderName(String uri){
		String[] tmp=uri.split("/");
		return tmp[tmp.length-2];
	}
	
	private void addImageToFolders(MyImage image){
		boolean folderExists=false;
		
		for(int i=0;i<folderList.size();i++){
			if(folderList.get(i).getName().equals(getFolderName(image.imagePath))){
				folderList.get(i).addImage(image);
				folderExists=true;
			}
		}
		
		if(!folderExists){
			ImageFolder tmp=new ImageFolder(getFolderName(image.imagePath));
			tmp.addImage(image);
			folderList.add(tmp);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
		ImageFolder resultData=this.mGridViewAdapter.getImageFolderAt(position);
		mCallback.OnFolderClicked(resultData);
	}
	
}
