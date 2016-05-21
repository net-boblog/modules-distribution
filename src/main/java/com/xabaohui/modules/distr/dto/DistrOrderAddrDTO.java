package com.xabaohui.modules.distr.dto;

public class DistrOrderAddrDTO {
	private String receiveName;
	private String receiveCode;
	private String receivePhoneNo;
	private Integer receiveCityId;
	private String receiveAddr;
	
	public String getReceiveName() {
		return receiveName;
	}
	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}
	public String getReceiveCode() {
		return receiveCode;
	}
	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}
	public String getReceivePhoneNo() {
		return receivePhoneNo;
	}
	public void setReceivePhoneNo(String receivePhoneNo) {
		this.receivePhoneNo = receivePhoneNo;
	}
	public Integer getReceiveCityId() {
		return receiveCityId;
	}
	public void setReceiveCityId(Integer receiveCityId) {
		this.receiveCityId = receiveCityId;
	}
	public String getReceiveAddr() {
		return receiveAddr;
	}
	public void setReceiveAddr(String receiveAddr) {
		this.receiveAddr = receiveAddr;
	}
	
}
