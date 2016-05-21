package com.xabaohui.modules.distr.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.xabaohui.modules.distr.bean.DistrBatch;

public interface DistrBatchDao {

	// property constants
	public static final String OPERATOR = "operator";
	public static final String STORAGE_REF = "storageRef";
	public static final String VERSION = "version";

	public abstract void save(DistrBatch transientInstance);

	public abstract DistrBatch findById(java.lang.Integer id);

	public abstract List<DistrBatch> findByExample(DistrBatch instance);

	public abstract List<DistrBatch> findByCriteria(DetachedCriteria dc);

}