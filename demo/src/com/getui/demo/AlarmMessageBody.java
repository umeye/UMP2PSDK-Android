package com.getui.demo;

import java.io.Serializable;

public class AlarmMessageBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7911065643097681072L;

	public String sound;
	public int badge;
	public AlarmMessageBodyAlert alert;

	public AlarmMessageBody() {
		// TODO Auto-generated constructor stub
	}

}
