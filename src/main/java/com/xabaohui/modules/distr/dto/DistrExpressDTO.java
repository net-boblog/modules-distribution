package com.xabaohui.modules.distr.dto;

import java.util.List;

public class DistrExpressDTO {
	private String expressNo;//��ݵ���
	private String receiveName;//�ռ�������
	private String receiveAddr;//�ռ��˵�ַ
	private String receiveCode;//�ռ����ʱ�
	private String receivePhoneNo;//�ռ����ֻ���
	private String senderName;//�ļ�������
	private String senderAddr;//�ļ���ַ
	private String senderCode;//�ļ��ʱ�
	private String senderPhoneNo;//�ļ����ֻ���
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
