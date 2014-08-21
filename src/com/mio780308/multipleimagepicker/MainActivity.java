package com.mio780308.multipleimagepicker;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;


public class MainActivity extends Activity implements
	ImageFolderFragment.ImageFolderFragmentListener,
	ImageFragment.ImageFragmentListener{
	
	//Constants
	public final static String RESULT_ALL_PATHS="result_all_paths"; 
	
	private boolean justOne;//true if only one image needs to be chosen
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_picker);
		
		initializeImageLoader();
		
		if(getIntent().getAction().equals(Intent.ACTION_PICK)){
			justOne=true;
		}
		
		if(savedInstanceState==null){
			ImageFolderFragment f=new ImageFolderFragment();
			getFragmentManager().beginTransaction().add(R.id.relativeLayout1, f).commit();
		}
		
	}
	
	private void initializeImageLoader(){
		ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void OnFolderClicked(final ImageFolder folder) {
		
		Bundle arg=new Bundle();
		arg.putBoolean(ImageFragment.ARG_JUST_ONE, justOne);
		arg.putStringArray(ImageFragment.ARG_IMG_PATHS, folder.getImagePathArray());
		ImageFragment f=new ImageFragment();
		f.setArguments(arg);
		getFragmentManager().beginTransaction().replace(R.id.relativeLayout1, f).addToBackStack(null).commit();
		
	}

	@Override
	public void onSelectionComplete(String[] uris) {
		if(justOne){
			Cursor cursor = getContentResolver().query(
	                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	                new String[] { MediaStore.Images.Media._ID },
	                MediaStore.Images.Media.DATA + "=? ",
	                new String[] { uris[0] }, null);
			cursor.moveToFirst();
			int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			
			Intent result=new Intent();
			result.setData(Uri.withAppendedPath(baseUri, "" + id));
			setResult(RESULT_OK, result);
			finish();
		}else{
			Intent result=new Intent();
			result.putExtra(RESULT_ALL_PATHS, uris);
			setResult(RESULT_OK, result);
			finish();
		}
	}
	
}
