package com.xabaohui.modules.distr.dto;
/**
 * 打印配货单界面显示需要的值
 * @author Administrator
 *
 */
public class DistrBatchPrintDTO {
	private String subjectName;//商品名称
	private String skuDesc;//商品描述，如红色42码
	private Integer skuCount;//商品数量
	private String posLabel;//库位号
	
	private String storageRef;//流水号，不用于展示，只用于缺货补配
	private Integer skuId;//用于缺货补配
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
	public Integer getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(Integer skuCount) {
		this.skuCount = skuCount;
	}
	public String getPosLabel() {
		return posLabel;
	}
	public void setPosLabel(String posLabel) {
		this.posLabel = posLabel;
	}
	public String getStorageRef() {
		return storageRef;
	}
	public void setStorageRef(String storageRef) {
		this.storageRef = storageRef;
	}
	public Integer getSkuId() {
		return skuId;
	}
	public void setSkuId(Integer skuId) {
		this.skuId = skuId;
	}
	
}
