package com.xabaohui.modules.distr.dto;

public class DistrBatchOutOfStockRequestDetail {
	
	private String posLabel;//ȱ����λ��ǩ;
	private Integer skuCount;//����
	
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
