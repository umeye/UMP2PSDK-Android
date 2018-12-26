package com.example.umeyesdk.entity;

public class ChannelInfo {
	public int index;
	public String name;

	public ChannelInfo(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ChannelInfo [index=" + index + ", name=" + name + "]";
	}


	

}
