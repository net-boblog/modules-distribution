package com.xabaohui.modules.distr.dto;

public class DistrBatchOutOfStockResponseDetail {
	private String posLabel;//��λ��ǩ
	private Integer skuId;//��С��浥Ԫid
	private Integer skuCount;//����
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
