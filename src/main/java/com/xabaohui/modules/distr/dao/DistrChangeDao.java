package com.xabaohui.modules.distr.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.bean.DistrOrder;

public interface DistrChangeDao {

	// property constants
	public static final String TRADE_ORDER_ID = "tradeOrderId";
	public static final String DISTR_ORDER_ID = "distrOrderId";
	public static final String TYPE = "type";
	public static final String DETAIL = "detail";
	public static final String VERSION = "version";

	public abstract void save(DistrChange transientInstance);

	public abstract DistrChange findById(java.lang.Integer id);

	public abstract List<DistrChange> findByExample(DistrChange instance);
	
	public abstract List<DistrChange> findByCriteria(DetachedCriteria dc);
}