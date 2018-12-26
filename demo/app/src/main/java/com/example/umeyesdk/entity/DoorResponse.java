

package com.example.umeyesdk.entity;


import java.io.Serializable;


public class DoorResponse implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8141786790699201977L;

	public DoorResponse()
	{
		// TODO Auto-generated constructor stub
	}

	int Result;

	int Request_Type;

	int Operation;

	DoorBean Value;

	public int getResult()
	{
		return Result;
	}

	public void setResult(int result)
	{
		Result = result;
	}

	public int getRequest_Type()
	{
		return Request_Type;
	}

	public void setRequest_Type(int request_Type)
	{
		Request_Type = request_Type;
	}

	public int getOperation()
	{
		return Operation;
	}

	public void setOperation(int operation)
	{
		Operation = operation;
	}

	public DoorBean getValue()
	{
		return Value;
	}

	public void setValue(DoorBean value)
	{
		Value = value;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	@Override
	public String toString()
	{
		return "Result:" + Result + " Request_Type:" + Request_Type + "Operation:" + Operation + " Value:" + Value.toString();
	}
}
