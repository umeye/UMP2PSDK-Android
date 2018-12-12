package com.example.umeyesdk.adpter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.umeyesdk.AcSearchRecord;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.PlayNode;

public class DeviceManagerAdapter extends BaseAdapter implements
		OnClickListener {
	private List<PlayNode> nodeList;
	private LayoutInflater inflater;
	public TextView txtParameters, txtName, txtDelete;
	int currentPosition;
	public ProgressDialog progressDialog;
	Context con;

	public DeviceManagerAdapter(Context con, List<PlayNode> nodeList) {
		this.nodeList = nodeList;
		inflater = LayoutInflater.from(con);
		this.con = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nodeList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public List<PlayNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<PlayNode> nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;
		PlayNode node = nodeList.get(position);
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = inflater.inflate(R.layout.device_manage_item, null);
			vh.tv = (TextView) convertView.findViewById(R.id.tvCaption);
			vh.img = (ImageView) convertView.findViewById(R.id.imgCamera);
			vh.btnRemote = (Button) convertView.findViewById(R.id.btn_remote);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		if (node.IsDirectory())// 如果是目录的话,一律处理为彩色
		{
			vh.tv.setText(node.getName() + "(" + node.getChildrenCount() + ")");
			vh.img.setBackgroundResource(R.drawable.camera_group);
			vh.btnRemote.setVisibility(View.GONE);
		} else// 如果是叶子节点，则根据是否在线处理颜色
		{
			vh.tv.setText(node.getName());
			if (node.IsDvr()) {
				vh.btnRemote.setVisibility(View.GONE);
				if (!node.isOnline())// 表明不可用，要使用灰色显示
				{
					vh.img.setBackgroundResource(R.drawable.dvr_offline);
				} else {
					vh.img.setBackgroundResource(R.drawable.dvr_online);
				}
			} else {// camera
				if (!node.isOnline())// 表明不可用，要使用灰色显示
				{
					vh.btnRemote.setVisibility(View.GONE);
					vh.img.setBackgroundResource(R.drawable.camera_offline);
				} else {
					vh.btnRemote.setVisibility(View.VISIBLE);
					vh.img.setBackgroundResource(R.drawable.camera_online);
					vh.btnRemote
							.setOnClickListener(new OnClickListstener(node));
				}
			}

		}
		return convertView;

	}

	class ViewHolder {
		TextView tv;
		ImageView img;
		Button btnRemote;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public class OnClickListstener implements OnClickListener

	{
		PlayNode node;

		public OnClickListstener(PlayNode node) {
			this.node = node;

		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(con, AcSearchRecord.class);
			intent.putExtra("deviceId", node.connecParams);
			intent.putExtra("title", node.getName());
			con.startActivity(intent);
		}
	}
}
