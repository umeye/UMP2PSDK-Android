package com.example.umeyesdk.adpter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umeyeNewSdk.AcMediaList;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.MediaListItem;

public class MediaAdapter extends BaseAdapter
{
	private List<MediaListItem> list;
	private Context context;
	private String prefix;
	private LayoutInflater inflater;
	public String[] strFileName=null;
	public  int[] tempInt;
	public  List<MediaListItem> listNew;
	/**
	 * 缩小 变透明
	 */
	private AnimationSet animationSet=new AnimationSet(true);
	private AlphaAnimation alphaAnimation=null;
	//private ScaleAnimation scaleAnimation=null;
	public MediaAdapter(List<MediaListItem> list,Context context,String prefix)
	{
		strFileName=new String[list.size()];
		this.list=list;
		tempInt=new int[list.size()];
		this.context=context;
		this.prefix=prefix;
		inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder=new ViewHolder();
		MediaListItem item=list.get(position);
		//tempInt=new int[list.size()];
		Log.d("list.size()","list.size() is:"+list.size());
		if(convertView==null)
		{
 			convertView=inflater.inflate(R.layout.media_list_item, null);
 			holder.relative=(RelativeLayout) convertView.findViewById(R.id.relative);
 			holder.btnDelete=(Button)convertView.findViewById(R.id.btnDelete);
 			holder.imgThumb=(ImageView)convertView.findViewById(R.id.img);
 			holder.tvCaption=(TextView)convertView.findViewById(R.id.tvCaption);
 			holder.tvInfo=(TextView)convertView.findViewById(R.id.tvInfo);
 			holder.cbkDelete=(CheckBox) convertView.findViewById(R.id.cbkDelete);
 			
 			convertView.setTag(holder);
		}
		else
		{
			holder=(ViewHolder)convertView.getTag();
		}
		int m=item.fileName.lastIndexOf('/');
		holder.tvCaption.setText(item.fileName.substring(m+1));//得到文件短名
	/*	if(prefix!=null)//除去前缀
		{
			int k=item.fileName.indexOf(prefix);
			holder.tvInfo.setText(item.fileName.substring(k+prefix.length()));
		}*/
		holder.tvInfo.setText(item.fileName.substring(0,m));
		//holder.btnDelete.setOnClickListener(new OnClick(position,holder.relative));
		holder.cbkDelete.setOnCheckedChangeListener(new OclickCBX(position,item,holder.cbkDelete));

		if(item.isShowDelete)
		{
			//AcMediaList.isSelected[position]=position;
			holder.cbkDelete.setVisibility(View.VISIBLE);
			//holder.btnDelete.setVisibility(View.VISIBLE);
		}
		
		else
		{
			holder.cbkDelete.setVisibility(View.GONE);
			//holder.btnDelete.setVisibility(View.GONE);
		}
		if(item.bmp!=null)
		{
			holder.imgThumb.setImageBitmap(item.bmp);
			//holder.imgThumb.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
		}
		
		if(item.isSelected){
			holder.cbkDelete.setChecked(true);
		}
		else{
			holder.cbkDelete.setChecked(false);
		}
		return convertView;
	}
	private class OclickCBX implements OnCheckedChangeListener{
		private int postion;
		private MediaListItem item;
		private CheckBox cb;
		public OclickCBX(int postion,MediaListItem item,CheckBox cb) {
			this.postion=postion;
			this.item=item;
			this.cb=cb;
			System.out.println("OclickCBX-------->postion is:"+postion);
		}
		//滑动下拉条  OclickCBX和onCheckedChanged函数也会被调用
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {						
			if (isChecked&&(!item.isSelected)){ //点了选中
				item.isSelected=isChecked;
				tempInt[postion]=1;
			}else{//点了取消选中
				if(item.isSelected && !isChecked){
					
					item.isSelected=isChecked;
					tempInt[postion]=0;
				}
			}			
			for (int i = 0; i < tempInt.length; i++) {
				System.out.println("tempInt====> temp  i=="+i+"tempInt[i]"+tempInt[i]);
			}
		}
	}
	
	public void delete(){
		listNew=new ArrayList<MediaListItem>();		
		boolean flage=true;
		if(tempInt==null)
		{
			return;
		}
		for (int i = 0; i < tempInt.length; i++) {
			System.out.println("tempInt====> temp   i=="+i+"tempInt[i]"+tempInt[i]);
			if(tempInt[i]==1){		
				MediaListItem itme=list.get(i);
				File file=new File(itme.fileName);
				file.delete();				
				flage=false;				
			}else{
				listNew.add(list.get(i));
			}
		}
		
		if(flage){
			Toast.makeText(context,context.getString(R.string.pleaseCheck) , Toast.LENGTH_SHORT).show();
			return;
		}		
		list.clear();
		list=listNew;
		tempInt=new int[list.size()];
		for (int i = 0; i < tempInt.length; i++) {
			tempInt[i]=0;
		}
		Toast.makeText(context,context.getString(R.string.delete_success) , Toast.LENGTH_SHORT).show();
		AcMediaList.data=list;
		notifyDataSetChanged();
		//listNew.clear();
	}
	
	
	private class ViewHolder
	{
		public RelativeLayout relative;
		public Button btnDelete;
		public ImageView imgThumb;
		public TextView tvCaption;//显示标题，单纯的文件名字，不包括路径
		public TextView tvInfo;//关于文件的信息
		public CheckBox cbkDelete;
	}
	
	private class onCBKDelete implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			
		}
		
	}
	
	
	private int remPosition;
	private class OnClick implements OnClickListener
	{
		private int position;
		private RelativeLayout relative;
		
		public OnClick(int position,RelativeLayout relative)
		{
			this.position=position;
			this.relative=relative;
		}
		@Override
		public void onClick(View v)
		{
			remPosition=position;
			animation(relative,position);
		}
	}
	public void animation(RelativeLayout re,int index){
		alphaAnimation=new AlphaAnimation(1, 0);
		//scaleAnimation=new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,   
	    //Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);  
		animationSet.addAnimation(alphaAnimation);
		//animationSet.addAnimation(scaleAnimation);
		//设置动画执行时间
		animationSet.setDuration(1000);
		animationSet.setAnimationListener(new OnAnimation());
		re.startAnimation(animationSet);
	}
	public void delete(int position){
		System.out.println("调用了删除方法没有方法~~~~~~~~~~~~~");
		File file=new File(list.get(position).fileName);
		file.delete();
		System.out.println("是否已经删除"+file.delete());
		Toast.makeText(context,list.get(position).description+context.getString(R.string.delete_success) , Toast.LENGTH_SHORT).show();
		list.remove(position);
		notifyDataSetChanged();
	}
	//全选
	public void SelectedAll(){
		
		try
		{
			for (int i = 0; i < list.size(); i++) {
				MediaListItem item=list.get(i);
				item.isSelected=true;
			}
			
			for (int i = 0; i < tempInt.length; i++) {
				
				tempInt[i]=1;
				System.out.println("tempInt====> temp  i=="+i+"tempInt[i]"+tempInt[i]);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	//全不选
	public void notSelectedAll(){
		for (int i = 0; i < list.size(); i++) {
			MediaListItem item=list.get(i);
			item.isSelected=false;
		}
		for (int i = 0; i < tempInt.length; i++) {
			tempInt[i]=0;
			System.out.println("tempInt====> temp  i=="+i+"tempInt[i]"+tempInt[i]);
		}
	}
	
	public void ShowEditState()
	{
		SetDeleteVisible(true);
	}
	public void ShowFinishState()
	{
		SetDeleteVisible(false);
	}
	private void SetDeleteVisible(boolean b)
	{
		for(int i=0;i<list.size();i++)
			list.get(i).isShowDelete=b;
		notifyDataSetChanged();
	}
	public String GetPath(String fileName)
	{
		File file=new File(fileName);
		return file.getParent();
	}
	public class OnAnimation implements AnimationListener
	{
		@Override
		public void onAnimationEnd(Animation animation)
		{
			delete(remPosition);
		}
		@Override
		public void onAnimationRepeat(Animation animation)
		{
			
		}
		@Override
		public void onAnimationStart(Animation animation)
		{
			
		}
	}
}
