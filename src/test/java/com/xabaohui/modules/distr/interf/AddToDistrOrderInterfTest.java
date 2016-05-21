package com.xabaohui.modules.distr.interf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.xabaohui.modules.distr.bean.DistrChange;
import com.xabaohui.modules.distr.bean.DistrChangeType;
import com.xabaohui.modules.distr.bean.DistrOrder;
import com.xabaohui.modules.distr.bean.DistrOrderDetail;
import com.xabaohui.modules.distr.bean.DistrOrderRelation;
import com.xabaohui.modules.distr.bean.DistrOrderStatus;
import com.xabaohui.modules.distr.dao.DistrChangeDao;
import com.xabaohui.modules.distr.dao.DistrOrderDao;
import com.xabaohui.modules.distr.dao.DistrOrderDetailDao;
import com.xabaohui.modules.distr.dao.DistrOrderRelationDao;
import com.xabaohui.modules.distr.dto.DistrExpressPreDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDTO;
import com.xabaohui.modules.distr.dto.DistrOrderDetailDTO;
import com.xabaohui.modules.distr.service.DistrService;

@TransactionConfiguration(transactionManager="transactionManager",defaultRollback=false)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:applicationContext.xml")
@Component
public class AddToDistrOrderInterfTest extends AbstractJUnit4SpringContextTests{
	@Resource
	private DistrOrderDao distrOrderDao;
	@Resource
	private DistrOrderRelationDao distrOrderRelationDao;
	@Resource
	private DistrOrderDetailDao distrOrderDetailDao;
	@Resource
	private DistrService distrService;
	@Resource
	private DistrChangeDao changeDao;
	
	@Test
	public void AddToDistrOrderInterf(){
		DistrOrderDTO distrOrderDTO = buildDistrOrderDTO();
		int distrOrderId = distrService.saveDistrOrder(distrOrderDTO);
		//�Ƿ�ɹ���ӵ�����������
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(distrOrderDTO.getTradeOrderId());
		List<DistrOrderRelation> list = distrOrderRelationDao.findByExample(relation);
		if (list == null || list.isEmpty()){
			throw new RuntimeException("û�гɹ����ϵͳ����Ϊ"+distrOrderDTO.getTradeOrderId()+"�����ݵ�������");
		}
		if (list.size()>1){
			throw new RuntimeException("�����������ж���ϵͳ����Ϊ"+distrOrderDTO.getTradeOrderId()+"������");
		}
		//�Ƿ���ӵ�ϵͳ�����䷢��
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId);
		Assert.assertNotNull(distrOrder);
		DistrOrderDetail relation2 = new DistrOrderDetail();
		relation2.setDistrOrderId(distrOrderId);
		List<DistrOrderDetail> detailList = distrOrderDetailDao.findByExample(relation2);
		//�Ƿ���ӵ�ϵͳ�����䷢��ϸ��
		if (detailList == null|| detailList.isEmpty()){
			throw new RuntimeException("û����ӵ�ϵͳ�����䷢��ϸ����");
		}
		Assert.assertEquals(DistrOrderStatus.WAIT_DISTRIBUTION, distrOrder.getDistrStatus());
	}
	
	/**
	 * ���id�͵�ַ��ͬ
	 */
	@Test
	public void mergeOrder(){
		// ��һ�ε��ã������䷢ϵͳ�����Ͷ�����ϸ
		Integer tradeOrderId = getRandomOrderId();
		DistrOrderDTO distrOrderDTO = buildDistrOrderDTO();
		distrOrderDTO.setTradeOrderId(tradeOrderId);
		distrOrderDTO.setReceiveAddr("���ƺ�" + tradeOrderId);
		int distrOrderId1 = distrService.saveDistrOrder(distrOrderDTO);
		// �ڶ��ε��ã���ַ��ͬ���������䷢ϵͳ������ֻ���¶�����ϸ
		Integer tradeOrderId2 = getRandomOrderId();
		DistrOrderDTO distrOrderDTO2 = buildDistrOrderDTO();
		distrOrderDTO2.setTradeOrderId(tradeOrderId2);
		distrOrderDTO2.setReceiveAddr("���ƺ�" + tradeOrderId);
		distrOrderDTO2.setReceivePhoneNo(distrOrderDTO.getReceivePhoneNo());
		int distrOrderId2 = distrService.saveDistrOrder(distrOrderDTO2);
		
		// �����֤
		// 1. �䷢ϵͳ����ֻ��һ����¼
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationList1 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList1);
		Assert.assertEquals(1, relationList1.size());
		relation.setTradeOrderId(tradeOrderId2);
		List<DistrOrderRelation> relationList2 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList2);
		Assert.assertEquals(1, relationList2.size());
		Assert.assertEquals(distrOrderId1, distrOrderId2);
		Assert.assertNotNull(distrOrderDao.findById(distrOrderId1));
		// 2. ��ϸ�����ж�����¼
		DistrOrderDetail detail = new DistrOrderDetail();
		detail.setDistrOrderId(distrOrderId1);
		List<DistrOrderDetail> detailList = distrOrderDetailDao.findByExample(detail);
		Assert.assertEquals(distrOrderDTO.getList().size()+distrOrderDTO2.getList().size(), detailList.size());
	}
	/**
	 * ���id�͵�ַ��ͬ,״̬��Ϊ�ȴ�״̬
	 */
	@Test
	public void mergeOrder2(){
		// ��һ�ε��ã������䷢ϵͳ�����Ͷ�����ϸ
		Integer tradeOrderId = getRandomOrderId();
		DistrOrderDTO distrOrderDTO = buildDistrOrderDTO();
		distrOrderDTO.setTradeOrderId(tradeOrderId);
		distrOrderDTO.setReceiveAddr("���ƺ�" + tradeOrderId);
		int distrOrderId1 = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId1);
		distrOrder.setDistrStatus(DistrOrderStatus.PICKING_DISTRIBUTION);
		distrOrderDao.update(distrOrder);
		// �ڶ��ε��ã���ַ��ͬ�������䷢ϵͳ�����͸��¶�����ϸ
		Integer tradeOrderId2 = getRandomOrderId();
		DistrOrderDTO distrOrderDTO2 = buildDistrOrderDTO();
		distrOrderDTO2.setTradeOrderId(tradeOrderId2);
		distrOrderDTO2.setReceiveAddr("���ƺ�" + tradeOrderId);
		distrOrderDTO2.setReceivePhoneNo(distrOrderDTO.getReceivePhoneNo());
		int distrOrderId2 = distrService.saveDistrOrder(distrOrderDTO2);
		
		// �����֤
		// 1. �䷢ϵͳ������������¼
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationList1 = distrOrderRelationDao.findByExample(relation);
		Assert.assertEquals(true, distrOrderDao.findById(distrOrderId1).getChangeOrderFlag());
		Assert.assertEquals(true, distrOrderDao.findById(distrOrderId2).getChangeOrderFlag());
		Assert.assertNotNull(relationList1);
		Assert.assertEquals(1, relationList1.size());
		relation.setTradeOrderId(tradeOrderId2);
		List<DistrOrderRelation> relationList2 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList2);
		Assert.assertEquals(1, relationList2.size());
		Assert.assertNotEquals(distrOrderId1, distrOrderId2);
		Assert.assertNotNull(distrOrderDao.findById(distrOrderId1));
		// 2. ������м�¼
		DistrChange distrChange = new DistrChange();
		distrChange.setDistrOrderId(distrOrderId1);
		List<DistrChange> list = changeDao.findByExample(distrChange);
		if (list == null|| list.isEmpty()){
			throw new RuntimeException("û�б����¼");
		}
		String changeType = ""; 
		for (DistrChange distrChange2 : list) {
			changeType = distrChange2.getType();
		}
		Assert.assertEquals(DistrChangeType.ORDER_DUPLICATE_ADDR, changeType);
		distrChange.setDistrOrderId(distrOrderId2);
		List<DistrChange> list2 = changeDao.findByExample(distrChange);
		if (list2 == null|| list2.isEmpty()){
			throw new RuntimeException("û�б����¼");
		}
		for (DistrChange distrChange2 : list2) {
			changeType = distrChange2.getType();
		}
		Assert.assertEquals(DistrChangeType.ORDER_DUPLICATE_ADDR, changeType);
	}
	
	
	/**
	 * ���id�͵�ַ����ͬ
	 */
	@Test
	public void mergeOrderFail(){
		// ��һ�ε��ã������䷢ϵͳ�����Ͷ�����ϸ
		Integer tradeOrderId = getRandomOrderId();
		System.out.println("id1=" + tradeOrderId);
		DistrOrderDTO distrOrderDTO = buildDistrOrderDTO();
		distrOrderDTO.setTradeOrderId(tradeOrderId);
		distrOrderDTO.setReceiveAddr("���ƺ�" + tradeOrderId);
		distrService.saveDistrOrder(distrOrderDTO);
		// �ڶ��ε��ã���ַ��ͬ���������䷢ϵͳ������ֻ���¶�����ϸ
		Integer tradeOrderId2 = getRandomOrderId();
		System.out.println("id2=" + tradeOrderId2);
		DistrOrderDTO distrOrderDTO2 = buildDistrOrderDTO();
		distrOrderDTO2.setTradeOrderId(tradeOrderId2);
		distrOrderDTO2.setReceiveAddr("���ƺ�" + tradeOrderId2);
		distrService.saveDistrOrder(distrOrderDTO2);
		
		// �����֤
		// �䷢ϵͳ����ֻ��һ����¼
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationList1 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList1);
		Assert.assertEquals(1, relationList1.size());
		relation.setTradeOrderId(tradeOrderId2);
		List<DistrOrderRelation> relationList2 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList2);
		Assert.assertEquals(1, relationList2.size());
		int distrOrderId1 = relationList1.get(0).getDistrOrderId();
		int distrOrderId2 = relationList2.get(0).getDistrOrderId();
		Assert.assertNotEquals(distrOrderId1, distrOrderId2);
		Assert.assertNotNull(distrOrderDao.findById(distrOrderId1));
		Assert.assertNotNull(distrOrderDao.findById(distrOrderId2));
	}
	
	/**
	 * �ǵȴ�״̬�Ķ��������ϲ�
	 */
	@Test
	public void mergeOrderFailForNotWait(){
		// ��һ�ε��ã������䷢ϵͳ�����Ͷ�����ϸ
		Integer tradeOrderId = getRandomOrderId();
		DistrOrderDTO distrOrderDTO = buildDistrOrderDTO();
		distrOrderDTO.setTradeOrderId(tradeOrderId);
		distrOrderDTO.setReceiveAddr("���ƺ�" + tradeOrderId);
		Integer distrOrderId1 = distrService.saveDistrOrder(distrOrderDTO);
		DistrOrder distrOrder = distrOrderDao.findById(distrOrderId1);
		distrOrder.setDistrStatus(DistrOrderStatus.PICKING_DISTRIBUTION);
		distrOrderDao.update(distrOrder);
		DistrExpressPreDTO preDTO = new DistrExpressPreDTO();
		preDTO.setDistrOrderId(distrOrderId1);
		preDTO.setExpressNo("123123123");
		distrService.saveExpressNo(preDTO);
		// �ڶ��ε��ã���ַ��ͬ���������䷢ϵͳ������ֻ���¶�����ϸ
		Integer tradeOrderId2 = getRandomOrderId();
		DistrOrderDTO distrOrderDTO2 = buildDistrOrderDTO();
		distrOrderDTO2.setTradeOrderId(tradeOrderId2);
		distrOrderDTO2.setReceiveAddr("���ƺ�" + tradeOrderId);
		distrOrderDTO2.setReceivePhoneNo(distrOrderDTO.getReceivePhoneNo());
		Integer distrOrderId2 = distrService.saveDistrOrder(distrOrderDTO2);
		
		// �����֤
		// �䷢ϵͳ������2����¼
		DistrOrderRelation relation = new DistrOrderRelation();
		relation.setTradeOrderId(tradeOrderId);
		List<DistrOrderRelation> relationList1 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList1);
		Assert.assertEquals(1, relationList1.size());
		relation.setTradeOrderId(tradeOrderId2);
		List<DistrOrderRelation> relationList2 = distrOrderRelationDao.findByExample(relation);
		Assert.assertNotNull(relationList2);
		Assert.assertEquals(1, relationList2.size());
		Assert.assertNotEquals(distrOrderId1, distrOrderId2);
		Assert.assertNotNull(distrOrderDao.findById(distrOrderId1));
		Assert.assertNotNull(distrOrderDao.findById(distrOrderId2));
	}
	
	public Integer getRandomOrderId() {
		Integer id = null;
		do {
			int randomId = (int) (Math.random() * 100000);
			DistrOrderRelation relation = new DistrOrderRelation();
			relation.setTradeOrderId(randomId);
			List<DistrOrderRelation> list = this.distrOrderRelationDao .findByExample(relation);
			if (list == null || list.isEmpty()) {
				id = randomId;
			}
		} while (id == null);
		return id;
	}

	public DistrOrderDTO buildDistrOrderDTO() {
		DistrOrderDTO distrOrderDTO = new DistrOrderDTO();
		distrOrderDTO.setReceiveCityId(3);
		distrOrderDTO.setExpressCompany("expressCompany");
		distrOrderDTO.setTradeOrderId(getRandomOrderId());
		distrOrderDTO.setReceiveAddr("�����人4");
		distrOrderDTO.setReceiveCode("7411233");
		distrOrderDTO.setReceiveName("����");
		distrOrderDTO.setReceivePhoneNo("1"+getRandomOrderId());
		distrOrderDTO.setReceiverId(5);
		distrOrderDTO.setStatus(DistrOrderStatus.WAIT_DISTRIBUTION);
		List<DistrOrderDetailDTO> detailList = new ArrayList<DistrOrderDetailDTO>();
		DistrOrderDetailDTO detail = new DistrOrderDetailDTO();
		detail.setSkuId(3);
		detail.setSkuCount(30);
		detail.setSkuDesc("��ɫ42��");
		detail.setSubjectId(5);
		detail.setSubjectName("ƥ����Ь");
		detailList.add(detail);
		distrOrderDTO.setList(detailList);
		return distrOrderDTO;
	}
}
