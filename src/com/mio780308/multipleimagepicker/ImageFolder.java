package com.mio780308.multipleimagepicker;

import java.util.ArrayList;

public class ImageFolder{
	private ArrayList<MyImage> images;
	private String name;
	
	public ImageFolder(String _name){
		images=new ArrayList<MyImage>();
		name=_name;
	}
	
	public String getName(){return name;}
	public int getCount(){return images.size();}
	
	public ArrayList<MyImage> getRandomImages(int n){
		ArrayList<MyImage> result=new ArrayList<MyImage>();
		int count=0;
		while(count<n){
			boolean repeat=false;
			int randomIndex=(int)(Math.random()*getCount());
			
			for(int i=0;i<result.size();i++){
				if(result.get(i).equals(images.get(randomIndex))){
					repeat=true;
				}
			}
			
			if(!repeat){
				result.add(images.get(randomIndex));
				count++;
			}
		}
		
		return result;
	}
	
	public ArrayList<MyImage> getImages(){
		return images;
	}
	
	public void addImage(MyImage image){
		images.add(image);
	}
	
	public String[] getImagePathArray(){
		String tmp[]=new String[images.size()];
		for(int i=0;i<images.size();i++	){
			tmp[i]=images.get(i).imagePath;
		}
		return tmp;
	}
}