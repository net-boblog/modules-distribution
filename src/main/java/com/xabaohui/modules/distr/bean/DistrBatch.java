package com.xabaohui.modules.distr.bean;

import java.sql.Timestamp;

/**
 * DistrBatch entity. @author MyEclipse Persistence Tools
 */

public class DistrBatch implements java.io.Serializable {

	// Fields

	private Integer distrBatchId;
	private Integer operator;
	private String storageRef;
	private Timestamp gmtCreate;
	private Timestamp gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public DistrBatch() {
	}

	/** full constructor */
	public DistrBatch(Integer operator, String storageRef, Timestamp gmtCreate,
			Timestamp gmtModify, Integer version) {
		this.operator = operator;
		this.storageRef = storageRef;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getDistrBatchId() {
		return this.distrBatchId;
	}

	public void setDistrBatchId(Integer distrBatchId) {
		this.distrBatchId = distrBatchId;
	}

	public Integer getOperator() {
		return this.operator;
	}

	public void setOperator(Integer operator) {
		this.operator = operator;
	}

	public String getStorageRef() {
		return this.storageRef;
	}

	public void setStorageRef(String storageRef) {
		this.storageRef = storageRef;
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