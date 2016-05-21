package com.xabaohui.modules.distr.daoImpl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.distr.bean.DistrOrderDetail;
import com.xabaohui.modules.distr.dao.DistrOrderDetailDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * DistrOrderDetail entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see com.xabaohui.modules.distr.bean.DistrOrderDetail
 * @author MyEclipse Persistence Tools
 */

public class DistrOrderDetailDaoImpl extends HibernateDaoSupport implements DistrOrderDetailDao {
	private static final Logger log = LoggerFactory
			.getLogger(DistrOrderDetailDaoImpl.class);
	protected void initDao() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#save(com.zis.bean.DistrOrderDetail)
	 */
	@Override
	public void save(DistrOrderDetail transientInstance) {
		log.debug("saving DistrOrderDetail instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findById(java.lang.Integer)
	 */
	@Override
	public DistrOrderDetail findById(java.lang.Integer id) {
		log.debug("getting DistrOrderDetail instance with id: " + id);
		try {
			DistrOrderDetail instance = (DistrOrderDetail) getHibernateTemplate()
					.get("com.xabaohui.modules.distr.bean.DistrOrderDetail", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findByExample(com.zis.bean.DistrOrderDetail)
	 */
	@Override
	public List findByExample(DistrOrderDetail instance) {
		log.debug("finding DistrOrderDetail instance by example");
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
	 * @see com.zis.dao.DistrOrderDetailDao#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding DistrOrderDetail instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from DistrOrderDetail as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findByDistrOrderId(java.lang.Object)
	 */
	public List findByDistrOrderId(Object distrOrderId) {
		return findByProperty(DISTR_ORDER_ID, distrOrderId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findBySubjectId(java.lang.Object)
	 */
	public List findBySubjectId(Object subjectId) {
		return findByProperty(SUBJECT_ID, subjectId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findBySkuId(java.lang.Object)
	 */
	public List findBySkuId(Integer skuId) {
		return findByProperty(SKU_ID, skuId);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findBySkuDesc(java.lang.Object)
	 */
	public List findBySkuDesc(Object skuDesc) {
		return findByProperty(SKU_DESC, skuDesc);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findBySubjectName(java.lang.Object)
	 */
	public List findBySubjectName(Object subjectName) {
		return findByProperty(SUBJECT_NAME, subjectName);
	}

	/* (non-Javadoc)
	 * @see com.zis.dao.DistrOrderDetailDao#findBySkuCount(java.lang.Object)
	 */
	public List findBySkuCount(Object skuCount) {
		return findByProperty(SKU_COUNT, skuCount);
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

	public static DistrOrderDetailDao getFromApplicationContext(
			ApplicationContext ctx) {
		return (DistrOrderDetailDao) ctx.getBean("DistrOrderDetailDAO");
	}
}