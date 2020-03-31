package com.example.umeyesdk.adpter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

import com.example.umeyeNewSdk.AcModifyDevice;
import com.example.umeyeNewSdk.PlayActivity;
import com.example.umeyesdk.PlayActivity2;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.SearchDeviceInfo;
import com.example.umeyesdk.utils.Constants;

public class SearchDeviceAdapter extends BaseAdapter {
	public static final int MODIFY_DIR_SUCCESS = 4;
	public static final int MODIFY_DIR_FIALED = 5;
	private List<SearchDeviceInfo> nodeList;
	private Context con;
	private LayoutInflater inflater;
	// View view;
	public TextView txtParameters, txtName, txtDelete;
	int currentPosition;
	public ProgressDialog progressDialog;
	public boolean parentIsDvr = false;

	public SearchDeviceAdapter(Context con) {
		this.con = con;
		inflater = LayoutInflater.from(con);
		nodeList = new ArrayList<SearchDeviceInfo>();
		// editor = con.getSharedPreferences(FgFavorite.fileName,
		// Context.MODE_PRIVATE).edit();
	}

	public List<SearchDeviceInfo> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<SearchDeviceInfo> nodeList) {
		this.nodeList = nodeList;
		notifyDataSetChanged();
	}

	public boolean isParentIsDvr() {
		return parentIsDvr;
	}

	public void setParentIsDvr(boolean parentIsDvr) {
		this.parentIsDvr = parentIsDvr;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SearchDeviceInfo node = nodeList.get(position);
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = inflater.inflate(R.layout.search_device_item, null);
			vh.tv = (TextView) convertView.findViewById(R.id.tvCaption);
			vh.info = (TextView) convertView.findViewById(R.id.tvInfo);

			vh.imgaArrow = (ImageView) convertView.findViewById(R.id.imgArrow);
			vh.add = (Button) convertView.findViewById(R.id.btn_add);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tv.setText(node.getsDevName());
		vh.info.setText(node.getsIpaddr_1() + "  " + node.getsDevId() + "  "
				+ node.usChNum);
		OnClickListstener clickListener = new OnClickListstener(node, position);
		vh.imgaArrow.setOnClickListener(clickListener);
		vh.add.setOnClickListener(clickListener);
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		TextView info;
		ImageView imgaArrow;
		Button add;
	}

	public class OnClickListstener implements OnClickListener

	{
		SearchDeviceInfo node;
		int position;

		public OnClickListstener(SearchDeviceInfo node, int position) {
			this.node = node;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.imgArrow) {
				con.startActivity(new Intent(con, AcModifyDevice.class)
						.putExtra("node", node).putExtra("position", position));
			} else if (v.getId() == R.id.btn_add) {

				// con.startActivity();
				Constants.UMID = node.getsDevId();
				Intent Intent = new Intent(con, PlayActivity2.class)
						.putExtra("deviceName", node.getsDevName())
						.putExtra("umid", node.getsDevId())
						.putExtra("channels", node.usChNum);
				Activity activity = (Activity) con;
				activity.setResult(Activity.RESULT_OK, Intent);
				activity.finish();
			}
		}
	}

}
