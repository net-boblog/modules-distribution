package com.xabaohui.modules.distr.dto;
/**
 * ��ӡ�����������ʾ��Ҫ��ֵ
 * @author Administrator
 *
 */
public class DistrBatchPrintDTO {
	private String subjectName;//��Ʒ����
	private String skuDesc;//��Ʒ���������ɫ42��
	private Integer skuCount;//��Ʒ����
	private String posLabel;//��λ��
	
	private String storageRef;//��ˮ�ţ�������չʾ��ֻ����ȱ������
	private Integer skuId;//����ȱ������
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
