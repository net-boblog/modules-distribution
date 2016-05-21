package com.xabaohui.modules.distr.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.distr.bean.DistrOrderDetail;
import com.xabaohui.modules.distr.bean.DistrOrderRelation;

public interface DistrOrderRelationDao {

	// property constants
	public static final String TRADE_ORDER_ID = "tradeOrderId";
	public static final String DISTR_ORDER_ID = "distrOrderId";
	public static final String VERSION = "version";

	public abstract void save(DistrOrderRelation transientInstance);

	public abstract DistrOrderRelation findById(java.lang.Integer id);

	public abstract List<DistrOrderRelation> findByExample(DistrOrderRelation instance);
	
	public abstract List<DistrOrderRelation> findByCriteria(DetachedCriteria dc);
	
}