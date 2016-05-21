package com.xabaohui.modules.distr.bean;

import java.sql.Timestamp;

/**
 * DistrOrderDetail entity. @author MyEclipse Persistence Tools
 */

public class DistrOrderDetail implements java.io.Serializable {

	// Fields

	private Integer distrOrderDetailId;
	private Integer distrOrderId;
	private Integer subjectId;
	private Integer skuId;
	private String skuDesc;
	private String subjectName;
	private Integer skuCount;
	private Timestamp gmtCreate;
	private Timestamp gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public DistrOrderDetail() {
	}

	/** full constructor */
	public DistrOrderDetail(Integer distrOrderId, Integer subjectId,
			Integer skuId, String skuDesc, String subjectName,
			Integer skuCount, Timestamp gmtCreate, Timestamp gmtModify,
			Integer version) {
		this.distrOrderId = distrOrderId;
		this.subjectId = subjectId;
		this.skuId = skuId;
		this.skuDesc = skuDesc;
		this.subjectName = subjectName;
		this.skuCount = skuCount;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getDistrOrderDetailId() {
		return this.distrOrderDetailId;
	}

	public void setDistrOrderDetailId(Integer distrOrderDetailId) {
		this.distrOrderDetailId = distrOrderDetailId;
	}

	public Integer getDistrOrderId() {
		return this.distrOrderId;
	}

	public void setDistrOrderId(Integer distrOrderId) {
		this.distrOrderId = distrOrderId;
	}

	public Integer getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}

	public Integer getSkuId() {
		return this.skuId;
	}

	public void setSkuId(Integer skuId) {
		this.skuId = skuId;
	}

	public String getSkuDesc() {
		return this.skuDesc;
	}

	public void setSkuDesc(String skuDesc) {
		this.skuDesc = skuDesc;
	}

	public String getSubjectName() {
		return this.subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getSkuCount() {
		return this.skuCount;
	}

	public void setSkuCount(Integer skuCount) {
		this.skuCount = skuCount;
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