package com.xabaohui.modules.distr.bean;

import java.sql.Timestamp;

/**
 * DistrChange entity. @author MyEclipse Persistence Tools
 */

public class DistrChange implements java.io.Serializable {

	// Fields

	private Integer distrChangeId;
	private Integer distrOrderId;
	private String type;
	private String detail;
	private Timestamp gmtCreate;
	private Timestamp gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public DistrChange() {
	}

	/** full constructor */
	public DistrChange(Integer distrOrderId, String type, String detail,
			Timestamp gmtCreate, Timestamp gmtModify, Integer version) {
		this.distrOrderId = distrOrderId;
		this.type = type;
		this.detail = detail;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getDistrChangeId() {
		return this.distrChangeId;
	}

	public void setDistrChangeId(Integer distrChangeId) {
		this.distrChangeId = distrChangeId;
	}

	public Integer getDistrOrderId() {
		return this.distrOrderId;
	}

	public void setDistrOrderId(Integer distrOrderId) {
		this.distrOrderId = distrOrderId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetail() {
		return this.detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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