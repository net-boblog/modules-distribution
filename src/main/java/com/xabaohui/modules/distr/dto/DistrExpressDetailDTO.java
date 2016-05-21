package com.xabaohui.modules.distr.dto;

public class DistrExpressDetailDTO {
	private String skuDesc;//商品描述
	private String subjectName;//商品名称
	private Integer skuCount;//商品数量
	public String getSkuDesc() {
		return skuDesc;
	}
	public void setSkuDesc(String skuDesc) {
		this.skuDesc = skuDesc;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public Integer getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(Integer skuCount) {
		this.skuCount = skuCount;
	}
}
