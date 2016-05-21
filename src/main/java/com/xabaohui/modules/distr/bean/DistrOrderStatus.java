package com.xabaohui.modules.distr.bean;

public class DistrOrderStatus {
	public static final String ORDER_STATUS_CANCEL="order_status_cancel";//取消配货
	public static final String BLOCK_DISTRIBUTION="block_distribution";//拦截订单
	public static final String WAIT_DISTRIBUTION="wait_distribution";//等待配货
	public static final String PICKING_DISTRIBUTION="picking_distribution";//正在配货
	public static final String CHECKOK_DISTRIBUTION="checkok_distribution";//检验成功
	public static final String EXPORTING_DISTRIBUTION="exporting_distribution";//出库
}
