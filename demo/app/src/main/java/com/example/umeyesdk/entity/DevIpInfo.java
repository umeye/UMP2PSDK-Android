package com.example.umeyesdk.entity;

public class DevIpInfo {
	public String Net_Mac;
	public int Net_DHCP;
	public String Net_IPAddr;
	public String Net_Netmask;
	public String Net_Gateway;

	public DevIpInfo() {
		super();
	}

	@Override
	public String toString() {
		return "DevIpInfo [Net_Mac=" + Net_Mac + ", Net_DHCP=" + Net_DHCP
				+ ", Net_IPAddr=" + Net_IPAddr + ", Net_Netmask=" + Net_Netmask
				+ ", Net_Gateway=" + Net_Gateway + "]";
	}

}
