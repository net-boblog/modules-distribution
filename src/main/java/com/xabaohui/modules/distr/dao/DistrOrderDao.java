package com.xabaohui.modules.distr.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.distr.bean.DistrOrder;

public interface DistrOrderDao {

	// property constants
	public static final String DISTR_ORDER_ID = "distrOrderId";
	public static final String DISTR_BATCH_ID = "distrBatchId";
	public static final String RECEIVER_ID = "receiverId";
	public static final String EXPRESS_NO = "expressNo";
	public static final String RECEIVE_NAME = "receiveName";
	public static final String RECEIVE_CODE = "receiveCode";
	public static final String RECEIVE_PHONE_NO = "receivePhoneNo";
	public static final String RECEIVE_ADDR = "receiveAddr";
	public static final String DISTR_STATUS = "distrStatus";
	public static final String EXPRESS_STATUS = "expressStatus";
	public static final String CHANGE_ORDER_FLAG = "changeOrderFlag";
	public static final String VERSION = "version";

	public abstract void save(DistrOrder transientInstance);

	public abstract DistrOrder findById(java.lang.Integer id);

	public abstract List<DistrOrder> findByExample(DistrOrder instance);

	public abstract List<DistrOrder> findByCriteria(DetachedCriteria dc);
	
	public abstract void update(DistrOrder distrOrder);
	
}