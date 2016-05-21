package com.xabaohui.modules.distr.dto;

import java.util.List;


public class DistrOrderDTO {
	private String expressCompany;//��ݹ�˾
	private Integer tradeOrderId;//����ϵͳ����id
	private String receiveName;//�ռ�������
	private String receiveCode;//�ռ��˵�ַ�ʱ�
	private Integer receiverId;//���id
	private String receivePhoneNo;//�ռ����ֻ���
	private Integer receiveCityId;
	private String receiveAddr;//�ռ��˵�ַ
	private String status;
	private List<DistrOrderDetailDTO> list;
	
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
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public String getReceivePhoneNo() {
		return receivePhoneNo;
	}
	public void setReceivePhoneNo(String receivePhoneNo) {
		this.receivePhoneNo = receivePhoneNo;
	}
	public String getReceiveAddr() {
		return receiveAddr;
	}
	public void setReceiveAddr(String receiveAddr) {
		this.receiveAddr = receiveAddr;
	}
	public Integer getTradeOrderId() {
		return tradeOrderId;
	}
	public void setTradeOrderId(Integer tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}
	public String getExpressCompany() {
		return expressCompany;
	}
	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}
	
	public List<DistrOrderDetailDTO> getList() {
		return list;
	}
	public void setList(List<DistrOrderDetailDTO> list) {
		this.list = list;
	}
	public Integer getReceiveCityId() {
		return receiveCityId;
	}
	public void setReceiveCityId(Integer receiveCityId) {
		this.receiveCityId = receiveCityId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
