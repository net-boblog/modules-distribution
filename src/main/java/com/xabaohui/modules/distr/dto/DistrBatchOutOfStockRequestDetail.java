package com.xabaohui.modules.distr.dto;

public class DistrBatchOutOfStockRequestDetail {
	
	private String posLabel;//缺货库位标签;
	private Integer skuCount;//数量
	
	public String getPosLabel() {
		return posLabel;
	}
	public void setPosLabel(String posLabel) {
		this.posLabel = posLabel;
	}
	public Integer getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(Integer skuCount) {
		this.skuCount = skuCount;
	}
}
