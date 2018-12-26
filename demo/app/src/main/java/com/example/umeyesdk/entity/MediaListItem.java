package com.example.umeyesdk.entity;

import android.graphics.Bitmap;

public class MediaListItem 
{
	public Bitmap bmp=null;
	public String fileName;//完整文件名，包括文件名字及路径
	public String description;//文件的描述，一般是文件短名
	public boolean isShowDelete=false;
	public boolean isSelected=false;//checkBox 是否选中
}
