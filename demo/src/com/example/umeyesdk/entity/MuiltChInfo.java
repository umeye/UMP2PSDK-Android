package com.example.umeyesdk.entity;

import java.util.Arrays;

public class MuiltChInfo {
	public int SyntheticNum;
	public int[] ShowChannel;

	public MuiltChInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "MuiltChInfo [SyntheticNum=" + SyntheticNum + ", ShowChannel="
				+ Arrays.toString(ShowChannel) + "]";
	}

}
