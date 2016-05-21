package com.xabaohui.modules.distr.daoImpl;

import java.util.Date;
import java.util.List;
import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.distr.bean.DistrBatch;
import com.xabaohui.modules.distr.dao.DistrBatchDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * DistrBatch entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.zis.bean.DistrBatch
 * @author MyEclipse Persistence Tools
 */

public class DistrBatchDaoImpl extends HibernateDaoSupport implements DistrBatchDao {
	private static final Logger log = LoggerFactory
			.getLogger(DistrBatchDaoImpl.class);
	protected void initDao() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#save(com.xabaohui.modules.distr.bean.DistrBatch)
	 */
	@Override
	public void save(DistrBatch transientInstance) {
		log.debug("saving DistrBatch instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#delete(com.xabaohui.modules.distr.bean.DistrBatch)
	 */
	public void delete(DistrBatch persistentInstance) {
		log.debug("deleting DistrBatch instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findById(java.lang.Integer)
	 */
	@Override
	public DistrBatch findById(java.lang.Integer id) {
		log.debug("getting DistrBatch instance with id: " + id);
		try {
			DistrBatch instance = (DistrBatch) getHibernateTemplate().get(
					"com.xabaohui.modules.distr.bean.DistrBatch", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findByExample(com.xabaohui.modules.distr.bean.DistrBatch)
	 */
	@Override
	public List findByExample(DistrBatch instance) {
		log.debug("finding DistrBatch instance by example");
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
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding DistrBatch instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from DistrBatch as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findByOperator(java.lang.Object)
	 */
	public List findByOperator(Object operator) {
		return findByProperty(OPERATOR, operator);
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findByStorageRef(java.lang.Object)
	 */
	public List findByStorageRef(Object storageRef) {
		return findByProperty(STORAGE_REF, storageRef);
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findByVersion(java.lang.Object)
	 */
	public List findByVersion(Object version) {
		return findByProperty(VERSION, version);
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findAll()
	 */
	public List findAll() {
		log.debug("finding all DistrBatch instances");
		try {
			String queryString = "from DistrBatch";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#findByCriteria(org.hibernate.criterion.DetachedCriteria)
	 */
	@Override
	public List findByCriteria(DetachedCriteria dc){
		log.debug("updating Departmentinfo departmentinfo");
		try {
			return getHibernateTemplate().findByCriteria(dc);
		} catch (RuntimeException re) {
			log.error("update failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#merge(com.xabaohui.modules.distr.bean.DistrBatch)
	 */
	public DistrBatch merge(DistrBatch detachedInstance) {
		log.debug("merging DistrBatch instance");
		try {
			DistrBatch result = (DistrBatch) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#attachDirty(com.xabaohui.modules.distr.bean.DistrBatch)
	 */
	public void attachDirty(DistrBatch instance) {
		log.debug("attaching dirty DistrBatch instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.xabaohui.modules.distr.daoImpl.DistrBatchDao#attachClean(com.xabaohui.modules.distr.bean.DistrBatch)
	 */
	public void attachClean(DistrBatch instance) {
		log.debug("attaching clean DistrBatch instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	

	public static DistrBatchDao getFromApplicationContext(ApplicationContext ctx) {
		return (DistrBatchDao) ctx.getBean("DistrBatchDAO");
	}
}