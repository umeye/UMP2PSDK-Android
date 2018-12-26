package com.example.umeyesdk.entity;

import java.io.Serializable;

public class DoorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6185767482633921132L;

	public DoorBean() {
		// TODO Auto-generated constructor stub
	}

	String Dev_Type;

	String Dev_Serial;

	String Dev_Psw;

	String Lock_Psw;

	int Channel_Num;

	int Pir_State;

	int Tamper_State;

	String Power_Mode;

	int Door_Audio;
	public String Pir_Time;

	public String getDev_Type() {
		return Dev_Type;
	}

	public void setDev_Type(String dev_Type) {
		Dev_Type = dev_Type;
	}

	public String getDev_Serial() {
		return Dev_Serial;
	}

	public void setDev_Serial(String dev_Serial) {
		Dev_Serial = dev_Serial;
	}

	public String getDev_Psw() {
		return Dev_Psw;
	}

	public void setDev_Psw(String dev_Psw) {
		Dev_Psw = dev_Psw;
	}

	public String getLock_Psw() {
		return Lock_Psw;
	}

	public void setLock_Psw(String lock_Psw) {
		Lock_Psw = lock_Psw;
	}

	public int getChannel_Num() {
		return Channel_Num;
	}

	public void setChannel_Num(int channel_Num) {
		Channel_Num = channel_Num;
	}

	public int getPir_State() {
		return Pir_State;
	}

	public void setPir_State(int pir_State) {
		Pir_State = pir_State;
	}

	public int getTamper_State() {
		return Tamper_State;
	}

	public void setTamper_State(int tamper_State) {
		Tamper_State = tamper_State;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getDoor_Audio() {
		return Door_Audio;
	}

	public void setDoor_Audio(int door_Audio) {
		Door_Audio = door_Audio;
	}

	public String getPir_time() {
		return Pir_Time;
	}

	public void setPir_time(String pir_time) {
		this.Pir_Time = pir_time;
	}

	@Override
	public String toString() {
		return "DoorBean [Dev_Type=" + Dev_Type + ", Dev_Serial=" + Dev_Serial
				+ ", Dev_Psw=" + Dev_Psw + ", Lock_Psw=" + Lock_Psw
				+ ", Channel_Num=" + Channel_Num + ", Pir_State=" + Pir_State
				+ ", Tamper_State=" + Tamper_State + ", Door_Audio="
				+ Door_Audio + ", Pir_Time=" + Pir_Time + "]";
	}

	public String getPower_Mode() {
		return Power_Mode;
	}

	public void setPower_Mode(String power_Mode) {
		Power_Mode = power_Mode;
	}

}
