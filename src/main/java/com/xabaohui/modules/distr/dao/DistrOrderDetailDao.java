package com.xabaohui.modules.distr.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.bean.DistrOrderDetail;

public interface DistrOrderDetailDao {

	// property constants
	public static final String DISTR_ORDER_ID = "distrOrderId";
	public static final String SUBJECT_ID = "subjectId";
	public static final String SKU_ID = "skuId";
	public static final String SKU_DESC = "skuDesc";
	public static final String SUBJECT_NAME = "subjectName";
	public static final String SKU_COUNT = "skuCount";
	public static final String VERSION = "version";

	public abstract void save(DistrOrderDetail transientInstance);

	public abstract DistrOrderDetail findById(java.lang.Integer id);

	public abstract List<DistrOrderDetail> findByExample(DistrOrderDetail instance);
	
	public abstract List<DistrOrderDetail> findByCriteria(DetachedCriteria dc);

}