package com.example.umeyesdk.adpter;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Player.Source.TVideoFile;
import com.example.umeyesdk.R;

public class SearchDownloadAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<TVideoFile> list;

	public SearchDownloadAdapter(Context c, List<TVideoFile> list) {
		inflater = LayoutInflater.from(c);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	ViewHold vh;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			vh = new ViewHold();
			convertView = inflater.inflate(R.layout.search_download_item, null);
			vh.tvStart = (TextView) convertView.findViewById(R.id.tvStart);
			vh.tvSize = (TextView) convertView.findViewById(R.id.tvsize);
			vh.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(vh);
		} else {
			vh = (ViewHold) convertView.getTag();
		}

		TVideoFile info = list.get(position);

		vh.tvName.setText(info.FileName);

		vh.tvStart.setText(getTimeDes(info.syear, info.smonth, info.sday,
				info.shour, info.sminute, info.ssecond));

		vh.tvSize.setText(getSizeDes(info.nFileSize));

		// String
		// range=info.syear+"-"+info.smonth+"-"+info.sday+" "+info.shour+":"+info.sminute+":"+info.ssecond;
		// range=range+"——"+info.eyear+"-"+info.emonth+"-"+info.eday+" "+info.ehour+":"+info.eminute+":"+info.esecond;

		// String range=info.shour+":"+info.sminute+":"+info.ssecond;
		// range=range+"—"+info.ehour+":"+info.eminute+":"+info.esecond;

		// tvRange.setText(range);
		//
		// TextView tvSize=(TextView)v.findViewById(R.id.tvSize);
		//
		// tvSize.setText(getSizeDes(info.nFileSize));

		return convertView;
	}

	class ViewHold {
		public TextView tvSize;
		TextView tvStart;
		TextView tvName;
	}

	public String getTimeDes(int year, int month, int day, int hour,
							 int minute, int second) {
		String s = "";
		String month1 = "", day1 = "", hour1 = "", minute1 = "", second1 = "";
		if (month < 10)
			month1 = "0" + month;
		else
			month1 = "" + month;
		if (day < 10)
			day1 = "0" + day;
		else
			day1 = "" + day;
		if (hour < 10)
			hour1 = "0" + hour;
		else
			hour1 = "" + hour;
		if (minute < 10)
			minute1 = "0" + minute;
		else
			minute1 = "" + minute;
		if (second < 10)
			second1 = "0" + second;
		else
			second1 = "" + second;
		s = year + "-" + month1 + "-" + day1 + " " + hour1 + ":" + minute1
				+ ":" + second1;

		return s;
	}

	public String getSizeDes(double len) {

		String des = "";
		if (len < 1024) {
			des = len + "B";
		} else if (len < 1024 * 1024) {
			des = formate(len / 1024) + "KB";
		} else if (len < 1024 * 1024 * 1024) {
			des = formate(len / 1024 / 1024) + "MB";
		} else if (len < 1024 * 1024 * 1024 * 1024) {
			des = formate(len / 1024 / 1024 / 1024) + "GB";
		}
		return des;
	}

	public double formate(double d) {
		BigDecimal b = new BigDecimal(d);
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

}
