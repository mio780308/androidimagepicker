package com.mio780308.multipleimagepicker;

public class MyImage{
	public String imagePath;
	public String thumbnailPath;
	
	public MyImage(String imgPath){
		imagePath=imgPath;
	}
	
	public MyImage(String imgPath,String thumbPath){
		imagePath=imgPath;
		thumbnailPath=thumbPath;
	}
	
	public boolean equals(MyImage another){
		return another.imagePath.equals(this.imagePath);
	}
	
	public String getName(){
		String[] tmp=imagePath.split("/");
		return tmp[tmp.length-1];
	}
}