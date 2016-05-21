package com.xabaohui.modules.distr.daoImpl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.dao.DistrChangeDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * DistrChange entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.distr.bean.DistrChange
 * @author MyEclipse Persistence Tools
 */

public class DistrChangeDaoImpl extends HibernateDaoSupport implements DistrChangeDao {
	private static final Logger log = LoggerFactory
			.getLogger(DistrChangeDaoImpl.class);
	protected void initDao() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#save(com.zis.bean.DistrChange)
	 */
	@Override
	public void save(DistrChange transientInstance) {
		log.debug("saving DistrChange instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}


	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#findById(java.lang.Integer)
	 */
	@Override
	public DistrChange findById(java.lang.Integer id) {
		log.debug("getting DistrChange instance with id: " + id);
		try {
			DistrChange instance = (DistrChange) getHibernateTemplate().get(
					"com.xabaohui.modules.distr.bean.DistrChange", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#findByExample(com.zis.bean.DistrChange)
	 */
	@Override
	public List findByExample(DistrChange instance) {
		log.debug("finding DistrChange instance by example");
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
	 * @see com.zis.dao.DistrChangeDao#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding DistrChange instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from DistrChange as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#findByTradeOrderId(java.lang.Object)
	 */
	public List findByTradeOrderId(Object tradeOrderId) {
		return findByProperty(TRADE_ORDER_ID, tradeOrderId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#findByDistrOrderId(java.lang.Object)
	 */
	public List findByDistrOrderId(Object distrOrderId) {
		return findByProperty(DISTR_ORDER_ID, distrOrderId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#findByType(java.lang.Object)
	 */
	public List findByType(Object type) {
		return findByProperty(TYPE, type);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrChangeDao#findByDetail(java.lang.Object)
	 */
	public List findByDetail(Object detail) {
		return findByProperty(DETAIL, detail);
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

	public static DistrChangeDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (DistrChangeDao) ctx.getBean("DistrChangeDAO");
	}
}