package com.xabaohui.modules.distr.dto;

public class CheckResultDTO {
	private boolean isSuccess;// 检查是否通过
	private String result;// 未成功原因

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
