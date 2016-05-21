package com.xabaohui.modules.distr.dto;

import java.util.List;
import java.util.Map;

public class DistrMapDTO {
	private Map<Integer, DistrGoodsDTO> comparedMap;
	private Map<Integer, DistrGoodsDTO> scannedMap;
	private Integer distrOrderId;
	
	public Map<Integer, DistrGoodsDTO> getComparedMap() {
		return comparedMap;
	}
	public void setComparedMap(Map<Integer, DistrGoodsDTO> comparedMap) {
		this.comparedMap = comparedMap;
	}
	public Map<Integer, DistrGoodsDTO> getScannedMap() {
		return scannedMap;
	}
	public void setScannedMap(Map<Integer, DistrGoodsDTO> scannedMap) {
		this.scannedMap = scannedMap;
	}
	public Integer getDistrOrderId() {
		return distrOrderId;
	}
	public void setDistrOrderId(Integer distrOrderId) {
		this.distrOrderId = distrOrderId;
	}
}
