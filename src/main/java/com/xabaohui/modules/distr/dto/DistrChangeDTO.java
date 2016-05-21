package com.xabaohui.modules.distr.dto;

public class DistrChangeDTO {
	private Integer tradeOrderId;
	private Integer type;// 变更类型
	private String detail;// 变更内容

	/* 更改地址 */
	public static final int CHANGE_ADDR = 1;
	public static final int ORDER_CANCELED = 2;
	public static final int ORDER_BLOCKED = 3;
	public static final int ORDER_UNBLOCKED = 4;

	public Integer getTradeOrderId() {
		return tradeOrderId;
	}

	public void setTradeOrderId(Integer tradeOrderId) {
		this.tradeOrderId = tradeOrderId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
