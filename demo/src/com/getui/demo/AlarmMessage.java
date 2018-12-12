package com.getui.demo;

import java.io.Serializable;

public class AlarmMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7911065643097681072L;

	public String AlarmSmallImg;
	public int AlarmEvent;
	public String AlarmBigImg;
	public String DevUmid;
	public int DevChNo;
	public String AlarmTime;
	public String CameraName;
	public String CameraId;
	public String AlarmId;
	public AlarmMessageBody aps;

	public AlarmMessage() {

	}

}
