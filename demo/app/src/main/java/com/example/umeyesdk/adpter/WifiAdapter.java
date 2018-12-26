package com.example.umeyesdk.adpter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.umeyesdk.R;

public class WifiAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	Context context;
	private List<ScanResult> nodeList;

	public WifiAdapter(Context context) {

		inflater = LayoutInflater.from(context);
		this.context = context;
		this.nodeList = new ArrayList<ScanResult>();
	}

	public List<ScanResult> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<ScanResult> nodeList) {
		this.nodeList = nodeList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nodeList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_wifi_item, null);
			vh.tv = (TextView) convertView.findViewById(R.id.show_name);
			vh.img = (ImageView) convertView.findViewById(R.id.wifi_rssi);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		ScanResult node = nodeList.get(position);
		vh.tv.setText(node.SSID + "");
//		int resId = 0;
//		if (node.iRSSI <= 33) {
//			resId = R.drawable.wifi_rssi_2;
//		} else if (node.iRSSI > 33 && node.iRSSI <= 66) {
//			resId = R.drawable.wifi_rssi_1;
//		} else {
//			resId = R.drawable.wifi_rssi_0;
//		}
//		vh.img.setImageResource(resId);
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		ImageView img;
	}
}
