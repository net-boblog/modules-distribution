package com.xabaohui.modules.distr.dto;


public class DistrBatchRequestDTO {
	
	private Integer skuId;// ��С��浥Ԫid
	private Integer skuCount;// ����
	
	public DistrBatchRequestDTO() {
	}
	
	public DistrBatchRequestDTO(Integer skuId, Integer skuCount) {
		this.skuId = skuId;
		this.skuCount = skuCount;
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
