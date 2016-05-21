package com.xabaohui.modules.distr.daoImpl;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.criterion.DetachedCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.xabaohui.modules.distr.bean.DistrOrder;
import com.xabaohui.modules.distr.dao.DistrOrderDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * DistrOrder entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see com.xabaohui.modules.distr.bean.DistrOrder
 * @author MyEclipse Persistence Tools
 */

public class DistrOrderDaoImpl extends HibernateDaoSupport implements DistrOrderDao {
	private static final Logger log = LoggerFactory
			.getLogger(DistrOrderDaoImpl.class);
	protected void initDao() {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#save(com.zis.bean.DistrOrder)
	 */
	@Override
	public void save(DistrOrder transientInstance) {
		log.debug("saving DistrOrder instance");
		try {
			getHibernateTemplate().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findById(java.lang.Integer)
	 */
	@Override
	public DistrOrder findById(java.lang.Integer id) {
		log.debug("getting DistrOrder instance with id: " + id);
		try {
			DistrOrder instance = (DistrOrder) getHibernateTemplate().get(
					"com.xabaohui.modules.distr.bean.DistrOrder", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByExample(com.zis.bean.DistrOrder)
	 */
	@Override
	public List findByExample(DistrOrder instance) {
		log.debug("finding DistrOrder instance by example");
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
	 * @see com.zis.daoImpl.DistrOrderDao#findByProperty(java.lang.String, java.lang.Object)
	 */
	public List findByProperty(String propertyName, Object value) {
		log.debug("finding DistrOrder instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from DistrOrder as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByDistrBatchId(java.lang.Object)
	 */
	public List findByDistrBatchId(Object distrBatchId) {
		return findByProperty(DISTR_BATCH_ID, distrBatchId);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByReceiverId(java.lang.Object)
	 */
	public List findByReceiverId(Object receiverId) {
		return findByProperty(RECEIVER_ID, receiverId);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByExpressNo(java.lang.Object)
	 */
	public List findByExpressNo(Object expressNo) {
		return findByProperty(EXPRESS_NO, expressNo);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByReceiveName(java.lang.Object)
	 */
	public List findByReceiveName(Object receiveName) {
		return findByProperty(RECEIVE_NAME, receiveName);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByReceiveCode(java.lang.Object)
	 */
	public List findByReceiveCode(Object receiveCode) {
		return findByProperty(RECEIVE_CODE, receiveCode);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByReceivePhoneNo(java.lang.Object)
	 */
	public List findByReceivePhoneNo(Object receivePhoneNo) {
		return findByProperty(RECEIVE_PHONE_NO, receivePhoneNo);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByReceiveAddr(java.lang.Object)
	 */
	public List findByReceiveAddr(Object receiveAddr) {
		return findByProperty(RECEIVE_ADDR, receiveAddr);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByDistrStatus(java.lang.Object)
	 */
	public List findByDistrStatus(Object distrStatus) {
		return findByProperty(DISTR_STATUS, distrStatus);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByExpressStatus(java.lang.Object)
	 */
	public List findByExpressStatus(Object expressStatus) {
		return findByProperty(EXPRESS_STATUS, expressStatus);
	}

	/* (non-Javadoc)
	 * @see com.zis.daoImpl.DistrOrderDao#findByChangeOrderFlag(java.lang.Object)
	 */
	public List findByChangeOrderFlag(Object changeOrderFlag) {
		return findByProperty(CHANGE_ORDER_FLAG, changeOrderFlag);
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
	
	public void update(DistrOrder distrOrder){
		getHibernateTemplate().update(distrOrder);
	}

	public static DistrOrderDao getFromApplicationContext(ApplicationContext ctx) {
		return (DistrOrderDao) ctx.getBean("DistrOrderDAO");
	}
}