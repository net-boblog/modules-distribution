package com.xabaohui.modules.distr.bean;

import java.sql.Timestamp;

/**
 * DistrOrderRelation entity. @author MyEclipse Persistence Tools
 */

public class DistrOrderRelation implements java.io.Serializable {

	// Fields

	private Integer relationId;
	private Integer tradeOrderId;
	private Integer distrOrderId;
	private Timestamp gmtCreate;
	private Timestamp gmtModify;
	private Integer version;

	// Constructors

	/** default constructor */
	public DistrOrderRelation() {
	}

	/** full constructor */
	public DistrOrderRelation(Integer tradeOrderId, Integer distrOrderId,
			Timestamp gmtCreate, Timestamp gmtModify, Integer version) {
		this.tradeOrderId = tradeOrderId;
		this.distrOrderId = distrOrderId;
		this.gmtCreate = gmtCreate;
		this.gmtModify = gmtModify;
		this.version = version;
	}

	// Property accessors

	public Integer getRelationId() {
		return this.relationId;
	}

	public void setRelationId(Integer relationId) {
		this.relationId = relationId;
	}

	public Integer getTradeOrderId() {
		return this.tradeOrderId;
	}

	public void setTradeOrderId(Integer tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	public Integer getDistrOrderId() {
		return this.distrOrderId;
	}

	public void setDistrOrderId(Integer distrOrderId) {
		this.distrOrderId = distrOrderId;
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