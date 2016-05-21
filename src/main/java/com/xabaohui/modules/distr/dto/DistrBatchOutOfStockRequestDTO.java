package com.xabaohui.modules.distr.dto;

import java.util.List;

public class DistrBatchOutOfStockRequestDTO {
	private String storageRef;//³ö¿âÁ÷Ë®ºÅ
	private Integer skuId;
	private List<DistrBatchOutOfStockRequestDetail> list;
	
	public String getStorageRef() {
		return storageRef;
	}
	public void setStorageRef(String storageRef) {
		this.storageRef = storageRef;
	}
	public List<DistrBatchOutOfStockRequestDetail> getList() {
		return list;
	}
	public void setList(List<DistrBatchOutOfStockRequestDetail> list) {
		this.list = list;
	}
	public Integer getSkuId() {
		return skuId;
	}
	public void setSkuId(Integer skuId) {
		this.skuId = skuId;
	}
}
