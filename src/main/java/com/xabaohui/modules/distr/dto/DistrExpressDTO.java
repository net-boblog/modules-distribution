package com.xabaohui.modules.distr.dto;

import java.util.List;

public class DistrExpressDTO {
	private String expressNo;//快递单号
	private String receiveName;//收件人姓名
	private String receiveAddr;//收件人地址
	private String receiveCode;//收件人邮编
	private String receivePhoneNo;//收件人手机号
	private String senderName;//寄件人姓名
	private String senderAddr;//寄件地址
	private String senderCode;//寄件邮编
	private String senderPhoneNo;//寄件人手机号
	private List<DistrExpressDetailDTO> list;
	
	public String getExpressNo() {
		return expressNo;
	}
	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}
	public String getReceiveName() {
		return receiveName;
	}
	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}
	public String getReceiveAddr() {
		return receiveAddr;
	}
	public void setReceiveAddr(String receiveAddr) {
		this.receiveAddr = receiveAddr;
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
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getSenderAddr() {
		return senderAddr;
	}
	public void setSenderAddr(String senderAddr) {
		this.senderAddr = senderAddr;
	}
	public String getSenderCode() {
		return senderCode;
	}
	public void setSenderCode(String senderCode) {
		this.senderCode = senderCode;
	}
	public String getSenderPhoneNo() {
		return senderPhoneNo;
	}
	public void setSenderPhoneNo(String senderPhoneNo) {
		this.senderPhoneNo = senderPhoneNo;
	}
	public List<DistrExpressDetailDTO> getList() {
		return list;
	}
	public void setList(List<DistrExpressDetailDTO> list) {
		this.list = list;
	}
}
