package com.xabaohui.modules.distr.daoImpl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.distr.bean.DistrOrderRelation;
import com.xabaohui.modules.distr.dao.DistrOrderRelationDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * DistrOrderRelation entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.distr.bean.DistrOrderRelation
 * @author MyEclipse Persistence Tools
 */

public class DistrOrderRelationDaoImpl extends HibernateDaoSupport implements DistrOrderRelationDao {
	private static final Logger log = LoggerFactory
			.getLogger(DistrOrderRelationDaoImpl.class);
	protected void initDao() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#save(com.zis.bean.DistrOrderRelation)
	 */
	@Override
	public void save(DistrOrderRelation transientInstance) {
		log.debug("saving DistrOrderRelation instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#findById(java.lang.Integer)
	 */
	@Override
	public DistrOrderRelation findById(java.lang.Integer id) {
		log.debug("getting DistrOrderRelation instance with id: " + id);
		try {
			DistrOrderRelation instance = (DistrOrderRelation) getHibernateTemplate()
					.get("com.xabaohui.modules.distr.bean.DistrOrderRelation", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#findByExample(com.zis.bean.DistrOrderRelation)
	 */
	@Override
	public List findByExample(DistrOrderRelation instance) {
		log.debug("finding DistrOrderRelation instance by example");
		try {
			List results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding DistrOrderRelation instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from DistrOrderRelation as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#findByTradeOrderId(java.lang.Object)
	 */
	public List findByTradeOrderId(Integer tradeOrderId) {
		return findByProperty(TRADE_ORDER_ID, tradeOrderId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#findByDistrOrderId(java.lang.Object)
	 */
	public List findByDistrOrderId(Object distrOrderId) {
		return findByProperty(DISTR_ORDER_ID, distrOrderId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderRelationDao#findByVersion(java.lang.Object)
	 */
	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	public List findByCriteria(DetachedCriteria dc){
		log.debug("updating Departmentinfo departmentinfo");
		try {
			return getHibernateTemplate().findByCriteria(dc);
		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		}
	}

	public static DistrOrderRelationDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (DistrOrderRelationDao) ctx.getBean("DistrOrderRelationDAO");
	}
}