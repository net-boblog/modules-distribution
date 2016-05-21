package com.xabaohui.modules.distr.bean;

import java.sql.Timestamp;

/**
 * DistrOrder entity. @author MyEclipse Persistence Tools
 */

public class DistrOrder implements java.io.Serializable {

	// Fields

	private Integer distrOrderId;
	private Integer distrBatchId;
	private Integer receiverId;
	private String expressNo;
	private String expressCompany;
	private String receiveName;
	private String receiveCode;
	private String receivePhoneNo;
	private Integer receiveCityId;
	private String receiveAddr;
	private String distrStatus;
	private String peblockStatus;
	private String expressStatus;
	private Boolean changeOrderFlag;
	private Timestamp gmtCreate;
	private Timestamp gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public DistrOrder() {
	}

	/** full constructor */
	public DistrOrder(Integer distrBatchId, Integer receiverId,
			String expressNo, String expressCompany, String receiveName,
			String receiveCode, String receivePhoneNo, Integer receiveCityId,
			String receiveAddr, String distrStatus, String peblockStatus,
			String expressStatus, Boolean changeOrderFlag, Timestamp gmtCreate,
			Timestamp gmtModify, Integer version) {
		this.distrBatchId = distrBatchId;
		this.receiverId = receiverId;
		this.expressNo = expressNo;
		this.expressCompany = expressCompany;
		this.receiveName = receiveName;
		this.receiveCode = receiveCode;
		this.receivePhoneNo = receivePhoneNo;
		this.receiveCityId = receiveCityId;
		this.receiveAddr = receiveAddr;
		this.distrStatus = distrStatus;
		this.peblockStatus = peblockStatus;
		this.expressStatus = expressStatus;
		this.changeOrderFlag = changeOrderFlag;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getDistrOrderId() {
		return this.distrOrderId;
	}

	public void setDistrOrderId(Integer distrOrderId) {
		this.distrOrderId = distrOrderId;
	}

	public Integer getDistrBatchId() {
		return this.distrBatchId;
	}

	public void setDistrBatchId(Integer distrBatchId) {
		this.distrBatchId = distrBatchId;
	}

	public Integer getReceiverId() {
		return this.receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}

	public String getExpressNo() {
		return this.expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public String getExpressCompany() {
		return this.expressCompany;
	}

	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}

	public String getReceiveName() {
		return this.receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceiveCode() {
		return this.receiveCode;
	}

	public void setReceiveCode(String receiveCode) {
		this.receiveCode = receiveCode;
	}

	public String getReceivePhoneNo() {
		return this.receivePhoneNo;
	}

	public void setReceivePhoneNo(String receivePhoneNo) {
		this.receivePhoneNo = receivePhoneNo;
	}

	public Integer getReceiveCityId() {
		return this.receiveCityId;
	}

	public void setReceiveCityId(Integer receiveCityId) {
		this.receiveCityId = receiveCityId;
	}

	public String getReceiveAddr() {
		return this.receiveAddr;
	}

	public void setReceiveAddr(String receiveAddr) {
		this.receiveAddr = receiveAddr;
	}

	public String getDistrStatus() {
		return this.distrStatus;
	}

	public void setDistrStatus(String distrStatus) {
		this.distrStatus = distrStatus;
	}

	public String getPeblockStatus() {
		return this.peblockStatus;
	}

	public void setPeblockStatus(String peblockStatus) {
		this.peblockStatus = peblockStatus;
	}

	public String getExpressStatus() {
		return this.expressStatus;
	}

	public void setExpressStatus(String expressStatus) {
		this.expressStatus = expressStatus;
	}

	public Boolean getChangeOrderFlag() {
		return this.changeOrderFlag;
	}

	public void setChangeOrderFlag(Boolean changeOrderFlag) {
		this.changeOrderFlag = changeOrderFlag;
	}

	public Timestamp getGmtCreate() {
		return this.gmtCreate;
	}

	public void setGmtCreate(Timestamp gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Timestamp getGmtModify() {
		return this.gmtModify;
	}

	public void setGmtModify(Timestamp gmtModify) {
		this.gmtModify = gmtModify;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}