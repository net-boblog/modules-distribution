package com.xabaohui.modules.distr.dto;

public class DistrGoodsDTO {
	private Integer distrOrderId;
	private String subjectName;
	private String skuDesc;
	private Integer skuId;
	private Integer skuCount;
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getSkuDesc() {
		return skuDesc;
	}
	public void setSkuDesc(String skuDesc) {
		this.skuDesc = skuDesc;
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
	public Integer getDistrOrderId() {
		return distrOrderId;
	}
	public void setDistrOrderId(Integer distrOrderId) {
		this.distrOrderId = distrOrderId;
	}
	
}
