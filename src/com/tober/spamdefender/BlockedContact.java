package com.tober.spamdefender;

public class BlockedContact {
	private String name;
	private String number;
	private boolean enable;

	public BlockedContact(String name, String number, boolean enable) {
		super();
		this.name = name;
		this.number = number;
		this.enable = enable;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	
}
