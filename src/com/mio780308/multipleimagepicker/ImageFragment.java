package com.mio780308.multipleimagepicker;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageFragment extends Fragment{
	
	public interface ImageFragmentListener{
		public void onSelectionComplete(String[] uris);
	}
	
	private class RowHandle{
		public String name;
		public String path;
		public boolean isSelected;
		
		public RowHandle(String uri){
			path=uri;
			String[] tmp=uri.split("/");
			name=tmp[tmp.length-1];
			isSelected=false;
		}
	}
	
	private class ImageAdapter extends BaseAdapter{
		private String[] imageList;
		private Context mContext;
		private ImageLoader imageLoader;
		private int columnWidth;
		private RowHandle[] rowHandles;
		private LayoutInflater mInflater;
		private GridView.LayoutParams mLayoutParams;
		private boolean justOne;
		public ImageAdapter(Context _context,String[] _imageList,ImageLoader _imageLoader,int _columnWidth,boolean _justOne){
			justOne=_justOne;
			mContext=_context;
			mInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageList=_imageList;
			imageLoader=_imageLoader;
			columnWidth=_columnWidth;
			mLayoutParams= new GridView.LayoutParams(columnWidth,columnWidth);
			setHandleData(_imageList);
		}
		
		public void setHandleData(String[] imageList){
			rowHandles=new RowHandle[imageList.length];
			for(int i=0;i<imageList.length;i++){
				rowHandles[i]=new RowHandle(imageList[i]);
			}
		}
		
		@Override
		public int getCount() {
			return imageList.length;
		}

		@Override
		public Object getItem(int position) {
			return rowHandles[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int rowIndex=position;
			View v=convertView;
			RowHandle rh=rowHandles[position];
		
			if(v==null){
				v=mInflater.inflate(R.layout.row_image_picker_item_2, parent, false);
				v.setLayoutParams(mLayoutParams);
				//Log.v(Constants.LOG_TAG, "inflating "+position);
			}
			
			ImageView image=(ImageView) v.findViewById(R.id.imageView1);
			imageLoader.displayImage("file://"+rh.path, image);
			
			TextView name=(TextView)v.findViewById(R.id.textView1);
			name.setText(rh.name);
			
			CheckBox checker=(CheckBox)v.findViewById(R.id.checkBox1);
			
			checker.setOnCheckedChangeListener(null);
			
			checker.setChecked(rh.isSelected);
			if(this.justOne){
				checker.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
						rowHandles[rowIndex].isSelected=isChecked;
						mCallback.onSelectionComplete(getSelectedItems());
					}
				});
			}else{
				checker.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
						rowHandles[rowIndex].isSelected=isChecked;
						updateActionBarState();
					}
				});
			}
			
			return v;
		}
		
		public String[] getSelectedItems(){
			ArrayList<String> tmp=new ArrayList<String>();
			for(int i=0;i<rowHandles.length;i++){
				if(rowHandles[i].isSelected){
					tmp.add(rowHandles[i].path);
				}
			}
			
			String[] tmp2=new String[tmp.size()];
			for(int i=0;i<tmp.size();i++){
				tmp2[i]=tmp.get(i);
			}
			
			return tmp2;
		}
		
		public void setAllCheckedState(boolean state){
			for(int i=0;i<rowHandles.length;i++){
				rowHandles[i].isSelected=state;
			}
			this.notifyDataSetChanged();
		}
		
	}
	
	private View actionBar;
	private Button button1,button2,button3;
	private GridView mGridView;
	private ImageAdapter mGridViewAdapter;
	private ImageLoader imageLoader;
	private boolean justOne;
	public final static String ARG_IMG_PATHS="image_paths";
	public final static String ARG_JUST_ONE="justone";
	
	private ImageFragmentListener mCallback;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback=(ImageFragmentListener) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		justOne=getArguments().getBoolean(ARG_JUST_ONE);
		
		imageLoader=ImageLoader.getInstance();
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int columnWidth=
			(Math.min(localDisplayMetrics.heightPixels, localDisplayMetrics.widthPixels)-
					4*this.getResources().getDimensionPixelSize(R.dimen.block_image_padding))/3;
		
		View v=inflater.inflate(R.layout.fragment_image_picker, container, false);
		
		mGridView=(GridView) v.findViewById(R.id.gridview);
		
		mGridView.setColumnWidth(columnWidth);
		mGridView.setNumColumns(-1);
		
		mGridViewAdapter=new ImageAdapter(getActivity(),getArguments().getStringArray(ARG_IMG_PATHS),imageLoader,columnWidth,justOne);
		
		mGridView.setAdapter(mGridViewAdapter);
		
		button1=(Button) v.findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mGridViewAdapter.setAllCheckedState(true);
			}
		});
		
		button2=(Button) v.findViewById(R.id.button2);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mGridViewAdapter.setAllCheckedState(false);
				updateActionBarState();
			}
		});
		
		button3=(Button) v.findViewById(R.id.button3);
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallback.onSelectionComplete(mGridViewAdapter.getSelectedItems());
			}
		});
		
		actionBar=v.findViewById(R.id.relativeLayout1);
		
		return v;
	}
	
	private void updateActionBarState(){
		TranslateAnimation a1=new TranslateAnimation(0, 0, -getResources().getDimension(R.dimen.abc_action_bar_default_height), 0);
		a1.setDuration(300);
		
		TranslateAnimation a2=new TranslateAnimation(0, 0, 0, -getResources().getDimension(R.dimen.abc_action_bar_default_height));
		a2.setDuration(300);
		
		if(mGridViewAdapter.getSelectedItems().length==0){
			if(actionBar.getVisibility()==View.VISIBLE){
				actionBar.setAnimation(a2);
				//actionBar.startAnimation(a2);
				actionBar.setVisibility(View.GONE);
			}
		}else{
			if(actionBar.getVisibility()==View.GONE){
				actionBar.setAnimation(a1);
				actionBar.setVisibility(View.VISIBLE);
				//actionBar.startAnimation(a1);
			}
		}
	}

}
