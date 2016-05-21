package com.xabaohui.modules.distr.dto;

public class DistrBatchOutOfStockResponseDetail {
	private String posLabel;//库位标签
	private Integer skuId;//最小库存单元id
	private Integer skuCount;//数量
	public String getPosLabel() {
		return posLabel;
	}
	public void setPosLabel(String posLabel) {
		this.posLabel = posLabel;
	}
	public Integer getSkuId() {
		return skuId;
	}
	public void setSkuId(Integer skuId) {
		this.skuId = skuId;
	}
	public Integer getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(Integer skuCount) {
		this.skuCount = skuCount;
	}
	
}
