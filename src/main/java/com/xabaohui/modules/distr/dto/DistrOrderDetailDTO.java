package com.xabaohui.modules.distr.dto;

public class DistrOrderDetailDTO {
	private Integer subjectId;//��Ʒid
	private Integer skuId;//��Ʒ��С��浥Ԫid
	private String skuDesc;//��Ʒ��С��浥Ԫ�������磺��ɫ42��
	private String subjectName;//��Ʒ����
	private Integer skuCount;//��Ʒ����
	public Integer getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	public Integer getSkuId() {
		return skuId;
	}
	public void setSkuId(Integer skuId) {
		this.skuId = skuId;
	}
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
