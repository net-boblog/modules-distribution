package com.xabaohui.modules.distr.dto;

import java.util.List;

public class DistrBatchOutOfStockResponseDTO {
	private String storageRef;//流水号
	private boolean isSuccess;//是否成功
	private String failReason;//失败原因
	private List<DistrBatchOutOfStockResponseDetail> list;
	public String getStorageRef() {
		return storageRef;
	}
	public void setStorageRef(String storageRef) {
		this.storageRef = storageRef;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public List<DistrBatchOutOfStockResponseDetail> getList() {
		return list;
	}
	public void setList(List<DistrBatchOutOfStockResponseDetail> list) {
		this.list = list;
	}
	
}
